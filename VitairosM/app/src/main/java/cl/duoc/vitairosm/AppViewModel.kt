package cl.duoc.vitairosm

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDb.get(app)
    private val prefs = app.getSharedPreferences("vitairos", Context.MODE_PRIVATE)
    private val ctx = app.applicationContext

    val userId = MutableStateFlow(prefs.getLong("uid", 0L))
    var ready by mutableStateOf(false)
        private set
    var consent by mutableStateOf(prefs.getBoolean("consent", false))
        private set
    var chatIntroSeen by mutableStateOf(prefs.getBoolean("chat_intro", false))
        private set
    var pendingStep by mutableStateOf(prefs.getString("pending", "") ?: "")
        private set
    var locked by mutableStateOf(true)
    var syncing by mutableStateOf(false)
        private set

    val user: StateFlow<User?> = userId
        .flatMapLatest { if (it == 0L) flowOf<User?>(null) else db.users().observe(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val checkIns: StateFlow<List<CheckIn>> = userId
        .flatMapLatest { if (it == 0L) flowOf(emptyList<CheckIn>()) else db.checkIns().observe(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val appointments: StateFlow<List<Appointment>> = userId
        .flatMapLatest { if (it == 0L) flowOf(emptyList<Appointment>()) else db.appointments().observe(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val professionals = db.professionals().observe().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val resources = db.resources().observe().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    // Vista del profesional de prueba
    val allUsers = db.users().observeAll().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val allCheckIns = db.checkIns().observeAll().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val allAppointments = db.appointments().observeAll().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init { viewModelScope.launch { seedIfEmpty(db); ready = true } }

    // ---------- Onboarding / sesión ----------
    fun acceptConsent() { consent = true; prefs.edit().putBoolean("consent", true).apply() }
    fun markChatIntro() { chatIntroSeen = true; prefs.edit().putBoolean("chat_intro", true).apply() }

    private fun setSession(u: User) {
        userId.value = u.id
        prefs.edit().putLong("uid", u.id).apply()
        locked = false
        applyReminders(u)
    }
    private fun applyReminders(u: User) {
        if (u.role == "USUARIO" && u.reminderOn) Reminders.scheduleDaily(ctx, u.reminderHour, u.reminderMinute)
        else Reminders.cancelDaily(ctx)
    }

    fun login(email: String, pass: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val u = db.users().byEmail(email.trim().lowercase())
            if (u == null || u.password != pass) onResult("Correo o contraseña incorrectos")
            else { setSession(u); onResult(null) }
        }
    }

    fun register(alias: String, email: String, pass: String, role: String, tName: String, tPhone: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val e = email.trim().lowercase()
            if (db.users().byEmail(e) != null) { onResult("Ese correo ya está registrado (simulado)"); return@launch }
            val id = db.users().insert(User(alias = alias.trim(), email = e, password = pass, role = role,
                avatar = if (role == "PROFESIONAL") "🩺" else "🌿", trustedName = tName.trim(), trustedPhone = tPhone.trim()))
            db.users().byId(id)?.let { setSession(it) }
            onResult(null)
        }
    }

    fun logout() {
        Reminders.cancelDaily(ctx)
        prefs.edit().remove("uid").apply()
        userId.value = 0L
        locked = true
    }

    fun updateUser(u: User) { viewModelScope.launch { db.users().update(u); applyReminders(u) } }

    // ---------- Check-ins ----------
    suspend fun getCheckIn(id: Long): CheckIn? = db.checkIns().byId(id)

    fun saveCheckIn(c: CheckIn, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            if (c.id == 0L) db.checkIns().insert(c.copy(userId = userId.value, synced = false))
            else db.checkIns().update(c.copy(updatedAt = System.currentTimeMillis(), synced = false))
            onDone()
        }
    }
    fun deleteCheckIn(c: CheckIn, onDone: () -> Unit = {}) { viewModelScope.launch { db.checkIns().delete(c); onDone() } }

    // ---------- Citas ----------
    fun book(proId: Long, slot: Long, reason: String, share: Boolean, onDone: () -> Unit) {
        viewModelScope.launch {
            val id = db.appointments().insert(Appointment(userId = userId.value, professionalId = proId,
                slot = slot, reason = reason.trim(), shareContext = share))
            if (user.value?.apptReminderOn == true) Reminders.scheduleAppt(ctx, id, slot)
            onDone()
        }
    }
    fun setStatus(a: Appointment, status: String) {
        viewModelScope.launch {
            db.appointments().update(a.copy(status = status, synced = false))
            if (status == "CANCELADA" || status == "REALIZADA") Reminders.cancelAppt(ctx, a.id)
        }
    }
    fun reschedule(a: Appointment, newSlot: Long) {
        viewModelScope.launch {
            db.appointments().update(a.copy(slot = newSlot, status = "REPROGRAMADA", synced = false))
            if (user.value?.apptReminderOn == true) Reminders.scheduleAppt(ctx, a.id, newSlot)
        }
    }

    // ---------- Recursos ----------
    fun toggleFav(r: Resource) { viewModelScope.launch { db.resources().setFav(r.id, !r.favorite) } }

    // ---------- Próximo paso pendiente ----------
    fun savePending(text: String) { pendingStep = text; prefs.edit().putString("pending", text).apply() }
    fun clearPending() { pendingStep = ""; prefs.edit().remove("pending").apply() }

    // ---------- Sincronización simulada ----------
    fun sync() {
        val uid = userId.value
        if (uid == 0L || syncing) return
        viewModelScope.launch {
            syncing = true
            delay(1500)
            db.checkIns().markSynced(uid)
            db.appointments().markSynced(uid)
            syncing = false
        }
    }
}
