package cl.duoc.vitairosm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CheckInScreen(vm: AppViewModel, nav: NavController, id: Long) {
    val user by vm.user.collectAsState()
    var existing by remember { mutableStateOf<CheckIn?>(null) }
    var mood by remember { mutableIntStateOf(0) }
    var category by remember { mutableStateOf("") }
    val emotions = remember { mutableStateListOf<String>() }
    var energy by remember { mutableFloatStateOf(0f) }
    var note by remember { mutableStateOf("") }
    var activity by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var saved by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        if (id != 0L) vm.getCheckIn(id)?.let {
            existing = it; mood = it.mood; category = it.category; energy = it.energy.toFloat()
            note = it.note; activity = it.activity
            emotions.clear(); emotions.addAll(it.emotions.split(",").filter(String::isNotBlank))
        }
    }

    AppScaffold(nav, if (id == 0L) "Check-in" else "Editar registro") { pad ->
        if (saved) {
            Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SoftCard(container = Mint) {
                    Kicker("Registro guardado")
                    Text("Gracias por registrarlo. Si te sirve, puedes elegir un pequeño paso para ahora.", fontSize = 18.sp)
                }
                GradientButton("Respiración guiada (2 min)", Modifier.fillMaxWidth()) { nav.navigate("breathing") }
                OutlinedButton({ nav.navigate("chat") }, Modifier.fillMaxWidth()) { Text("Continuar con la orientación") }
                OutlinedButton({ nav.navigate("resources") }, Modifier.fillMaxWidth()) { Text("Ver un recurso breve") }
                TextButton({ nav.popBackStack() }) { Text("Volver") }
            }
        } else Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            H("¿Cómo te sientes ahora?")
            Body("Es un registro personal de cómo dices sentirte en este momento. No es un puntaje clínico.")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MOODS.forEach { m ->
                    val sel = mood == m.value
                    Card(
                        Modifier.weight(1f).clickable { mood = m.value },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (sel) Mint else Soft),
                        border = if (sel) BorderStroke(2.dp, CyanText) else null
                    ) {
                        Column(Modifier.padding(vertical = 10.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(m.emoji, fontSize = 28.sp)
                            Text(m.label, fontSize = 10.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            Text("Opcional: ¿en qué área?", fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CATEGORIES.forEach { c -> FilterChip(category == c, { category = if (category == c) "" else c }, { Text(c) }) }
            }
            Text("Opcional: ¿qué emociones te acompañan?", fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EMOTIONS.forEach { e ->
                    FilterChip(e in emotions, { if (e in emotions) emotions.remove(e) else emotions.add(e) }, { Text(e) })
                }
            }
            Text("Opcional: energía ${if (energy.toInt() == 0) "(sin registrar)" else energy.toInt()}", fontWeight = FontWeight.SemiBold)
            Slider(energy, { energy = it }, valueRange = 0f..5f, steps = 4)
            OutlinedTextField(note, { note = it }, Modifier.fillMaxWidth(), label = { Text("Nota personal (opcional)") }, minLines = 3)
            OutlinedTextField(activity, { activity = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Actividad o situación (opcional)") })
            existing?.updatedAt?.let { Body("Última actualización: ${fmt(it)}") }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            GradientButton("Guardar", Modifier.fillMaxWidth()) {
                if (mood == 0) { error = "Elige cómo te sientes para guardar"; return@GradientButton }
                val base = existing ?: CheckIn(userId = user?.id ?: 0, mood = mood)
                vm.saveCheckIn(base.copy(mood = mood, category = category, emotions = emotions.joinToString(","),
                    energy = energy.toInt(), note = note.trim(), activity = activity.trim())) {
                    if (isCrisis(note + " " + activity)) nav.navigate("crisis") else saved = true
                }
            }
            if (existing != null) OutlinedButton({ confirmDelete = true }, Modifier.fillMaxWidth()) { Text("Eliminar este registro") }
        }
    }
    if (confirmDelete) AlertDialog(
        onDismissRequest = { confirmDelete = false },
        title = { Text("¿Eliminar registro?") }, text = { Text("Se borrará de tu teléfono. Tú decides sobre tus datos.") },
        confirmButton = { TextButton({ existing?.let { vm.deleteCheckIn(it) { nav.popBackStack() } } }) { Text("Eliminar") } },
        dismissButton = { TextButton({ confirmDelete = false }) { Text("Cancelar") } }
    )
}

@Composable
fun HistoryScreen(vm: AppViewModel, nav: NavController) {
    val cis by vm.checkIns.collectAsState()
    AppScaffold(nav, "Bitácora", tab = "history") { pad ->
        LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { GradientButton("Nuevo check-in", Modifier.fillMaxWidth()) { nav.navigate("checkin/0") } }
            item { WeeklySummary(cis) }
            if (cis.isEmpty()) item { Body("Aún no tienes registros. Cuando quieras, puedes comenzar.") }
            items(cis, key = { it.id }) { c ->
                val m = moodOf(c.mood)
                SoftCard(Modifier.clickable { nav.navigate("checkin/${c.id}") }) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(m.emoji, fontSize = 30.sp)
                        Column(Modifier.weight(1f)) {
                            Text("${m.label} · ${fmt(c.createdAt)}", fontWeight = FontWeight.SemiBold)
                            if (c.category.isNotBlank()) Body(c.category)
                        }
                        Text(if (c.synced) "☁" else "📱", fontSize = 14.sp)
                    }
                    if (c.emotions.isNotBlank()) Body(c.emotions.replace(",", " · "))
                    if (c.note.isNotBlank()) Body(c.note.take(90) + if (c.note.length > 90) "…" else "")
                    if (c.activity.isNotBlank()) Body("Actividad: ${c.activity}")
                }
            }
        }
    }
}

@Composable
private fun WeeklySummary(cis: List<CheckIn>) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    SoftCard(container = SoftBlue) {
        Kicker("Resumen semanal")
        Text("Así has registrado tu ánimo durante los últimos 7 días", fontWeight = FontWeight.SemiBold)
        Row(Modifier.fillMaxWidth().height(90.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
            days.forEach { d ->
                val list = cis.filter { dayOf(it.createdAt) == d }
                val avg = if (list.isEmpty()) 0f else list.map { it.mood }.average().toFloat()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.width(18.dp).height(if (avg == 0f) 4.dp else (avg * 12).dp)
                        .background(if (avg == 0f) Color0 else CyanText, RoundedCornerShape(6.dp)))
                    Text(d.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale("es")), fontSize = 11.sp)
                }
            }
        }
        Body("Es una herramienta de reflexión personal; no es una evaluación ni un diagnóstico clínico.")
    }
}
private val Color0 = androidx.compose.ui.graphics.Color(0xFFD5E3EA)
