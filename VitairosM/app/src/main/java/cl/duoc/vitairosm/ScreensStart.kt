package cl.duoc.vitairosm

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate

@Composable
fun OnboardingScreen(vm: AppViewModel, nav: NavController) {
    var page by remember { mutableIntStateOf(0) }
    var accepted by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LogoImage(Modifier.fillMaxWidth().height(110.dp))
        when (page) {
            0 -> {
                H("Que pedir ayuda no signifique empezar de cero.")
                Body("Registra cómo te sientes en pocos pasos, recibe orientación general y continúa desde donde quedaste. Tú tienes el control.")
            }
            1 -> {
                H("Qué es y qué no es VITAIROS-M")
                SoftCard { Body(DISCLAIMER, Navy) }
                Body("• No diagnostica.\n• No prescribe tratamientos.\n• No entrega terapia automatizada.\n• No reemplaza a un profesional.")
            }
            else -> {
                H("Tus datos y tu control")
                Body("• Tus registros se guardan en este teléfono.\n• Solo se comparten con un profesional si tú lo autorizas expresamente.\n• Tendrás siempre a la vista el acceso a «Ayuda urgente».\n• Esta es una versión académica: todos los datos son ficticios.")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(accepted, { accepted = it })
                    Text("Entiendo los límites de la app y acepto continuar.", fontSize = 14.sp)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { i -> Text(if (i == page) "●" else "○", color = CyanText) }
        }
        GradientButton(if (page < 2) "Continuar" else "Comenzar", Modifier.fillMaxWidth(), enabled = page < 2 || accepted) {
            if (page < 2) page++ else {
                vm.acceptConsent()
                nav.navigate("gate") { popUpTo("onboarding") { inclusive = true } }
            }
        }
        if (page > 0) TextButton({ page-- }) { Text("Volver") }
        TextButton({ nav.navigate("crisis") }) { Text("Ayuda urgente") }
    }
}

@Composable
fun AuthScreen(vm: AppViewModel, nav: NavController) {
    var register by remember { mutableStateOf(false) }
    var alias by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("USUARIO") }
    var tName by remember { mutableStateOf("") }
    var tPhone by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    fun done(err: String?) { error = err; if (err == null) nav.navigate("gate") { popUpTo("auth") { inclusive = true } } }

    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LogoImage(Modifier.fillMaxWidth().height(100.dp))
        H(if (register) "Crea tu cuenta" else "Ingresa a VITAIROS-M")
        Body("Acceso simulado: no se conecta a ningún servidor real.")
        if (register) OutlinedTextField(alias, { alias = it }, Modifier.fillMaxWidth(), singleLine = true,
            label = { Text("Alias o nombre de preferencia") })
        OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Correo (ficticio)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(pass, { pass = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Contraseña (mín. 6)") },
            visualTransformation = PasswordVisualTransformation())
        if (register) {
            Text("¿Cómo usarás la app?", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(role == "USUARIO", { role = "USUARIO" }, { Text("Persona usuaria") })
                FilterChip(role == "PROFESIONAL", { role = "PROFESIONAL" }, { Text("Profesional de salud") })
            }
            if (role == "USUARIO") {
                Body("Persona de confianza: si activas el protocolo de ayuda urgente, podrás contactarla con un mensaje preparado.")
                OutlinedTextField(tName, { tName = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Nombre de tu persona de confianza") })
                OutlinedTextField(tPhone, { tPhone = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Teléfono (ej. +56912345678)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        GradientButton(if (register) "Crear cuenta" else "Ingresar", Modifier.fillMaxWidth()) {
            val msg = when {
                register && alias.isBlank() -> "Escribe un alias"
                !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Ingresa un correo válido"
                pass.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
                register && role == "USUARIO" && tName.isBlank() -> "Indica el nombre de tu persona de confianza"
                register && role == "USUARIO" && !Regex("^\\+?[0-9 ]{8,15}$").matches(tPhone.trim()) -> "Ingresa un teléfono válido"
                else -> null
            }
            if (msg != null) error = msg
            else if (register) vm.register(alias, email, pass, role, tName, tPhone) { done(it) }
            else vm.login(email, pass) { done(it) }
        }
        TextButton({ register = !register; error = null }) { Text(if (register) "Ya tengo cuenta" else "Crear una cuenta nueva") }
        OutlinedButton({ vm.login("demo@vitairos.cl", "demo1234") { done(it) } }, Modifier.fillMaxWidth()) { Text("Ingresar como Usuario Demo") }
        OutlinedButton({ vm.login("pro@vitairos.cl", "pro1234") { done(it) } }, Modifier.fillMaxWidth()) { Text("Ingresar como Profesional Demo") }
        TextButton({ nav.navigate("crisis") }) { Text("Ayuda urgente") }
    }
}

@Composable
fun LockScreen(vm: AppViewModel, nav: NavController) {
    val user by vm.user.collectAsState()
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().systemBarsPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally) {
        LogoImage(Modifier.fillMaxWidth().height(90.dp))
        H("Ingresa tu PIN")
        Body("Protege tus notas y registros si el teléfono se comparte.")
        OutlinedTextField(pin, { if (it.length <= 4 && it.all(Char::isDigit)) { pin = it; error = false } },
            singleLine = true, label = { Text("PIN de 4 dígitos") }, isError = error,
            visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword))
        if (error) Text("PIN incorrecto", color = MaterialTheme.colorScheme.error)
        GradientButton("Desbloquear", Modifier.fillMaxWidth(), enabled = pin.length == 4) {
            if (pin == user?.pin) { vm.locked = false; nav.navigate("gate") { popUpTo("lock") { inclusive = true } } } else error = true
        }
        TextButton({ nav.navigate("crisis") }) { Text("Ayuda urgente") }
        TextButton({ vm.logout(); nav.navigate("gate") { popUpTo("lock") { inclusive = true } } }) { Text("Cerrar sesión") }
    }
}

@Composable
fun HomeScreen(vm: AppViewModel, nav: NavController) {
    val ctx = LocalContext.current
    val user by vm.user.collectAsState()
    val cis by vm.checkIns.collectAsState()
    val appts by vm.appointments.collectAsState()
    val pros by vm.professionals.collectAsState()
    val pending = cis.any { !it.synced } || appts.any { !it.synced }
    LaunchedEffect(pending) { if (pending && isOnline(ctx)) vm.sync() }
    val today = cis.firstOrNull { dayOf(it.createdAt) == LocalDate.now() }
    val next = appts.filter { it.status != "CANCELADA" && it.status != "REALIZADA" && it.slot > System.currentTimeMillis() }.minByOrNull { it.slot }
    val streakDays = streak(cis)
    val syncText = when { vm.syncing -> "Sincronizando…"; pending -> "Pendiente de sincronizar"; else -> "Datos respaldados" }

    AppScaffold(nav, "VITAIROS-M", tab = "home") { pad ->
        LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                H("Hola, ${user?.alias ?: ""} ${user?.avatar ?: ""}")
                Body("📱 Guardado en tu teléfono · $syncText")
            }
            item {
                SoftCard(container = Mint) {
                    Kicker("Check-in de hoy")
                    if (today != null) {
                        val m = moodOf(today.mood)
                        Text("${m.emoji} Hoy registraste: ${m.label}", fontSize = 18.sp)
                        OutlinedButton({ nav.navigate("checkin/0") }) { Text("Agregar otro registro") }
                    } else {
                        Body("Aún no registras cómo te sientes hoy. Solo toma unos segundos.", Navy)
                        GradientButton("Registrar cómo me siento", Modifier.fillMaxWidth()) { nav.navigate("checkin/0") }
                    }
                }
            }
            item { GradientButton("Hablar con VITAIROS-M", Modifier.fillMaxWidth()) { nav.navigate("chat") } }
            if (vm.pendingStep.isNotBlank()) item {
                SoftCard(container = SoftBlue) {
                    Kicker("Tu próximo paso pendiente")
                    Text(vm.pendingStep, fontSize = 16.sp)
                    TextButton({ vm.clearPending() }) { Text("Ya lo hice") }
                }
            }
            if (next != null) item {
                val pro = pros.firstOrNull { it.id == next.professionalId }
                SoftCard {
                    Kicker("Próxima cita (ficticia)")
                    Text("${pro?.name ?: "Profesional"} · ${fmt(next.slot)}")
                    Body(statusLabel(next.status))
                    TextButton({ nav.navigate("appts") }) { Text("Ver mis citas") }
                }
            }
            item {
                SoftCard {
                    Kicker("Tu continuidad")
                    Body(if (streakDays > 0) "🌱 Llevas $streakDays día(s) con registro. Si un día no puedes, no pasa nada: aquí seguimos." else "Cuando quieras retomar, aquí seguimos. Sin presión.", Navy)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton({ nav.navigate("history") }) { Text("Ver bitácora") }
                        OutlinedButton({ nav.navigate("resources") }) { Text("Recursos") }
                    }
                }
            }
            item { Text(DISCLAIMER, fontSize = 11.sp, color = GrayBlue, lineHeight = 15.sp) }
        }
    }
}
