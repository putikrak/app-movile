package cl.duoc.vitairosm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val AREAS = listOf("Estrés", "Ansiedad", "Sueño", "Organización", "Relaciones", "Familia")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfessionalsScreen(vm: AppViewModel, nav: NavController) {
    val pros by vm.professionals.collectAsState()
    var area by remember { mutableStateOf("") }
    val list = pros.filter { area.isBlank() || it.areas.contains(area) }
    AppScaffold(nav, "Profesionales", tab = "pros") { pad ->
        LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Body("Perfiles ficticios de demostración. Filtra por necesidad:")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AREAS.forEach { a -> FilterChip(area == a, { area = if (area == a) "" else a }, { Text(a) }) }
                }
                OutlinedButton({ nav.navigate("appts") }, Modifier.fillMaxWidth()) { Text("Mis citas") }
            }
            items(list, key = { it.id }) { p ->
                SoftCard {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(p.avatar, fontSize = 36.sp)
                        Column {
                            Text(p.name, fontWeight = FontWeight.Bold)
                            Body("${p.role} · ${p.specialty}")
                        }
                    }
                    Body(p.description)
                    Body("🖥 ${p.modality} · ${p.availability}")
                    Text("Perfil verificado – demo (ficticio)", fontSize = 12.sp, color = CyanText)
                    GradientButton("Ver ficha y agendar", Modifier.fillMaxWidth()) { nav.navigate("book/${p.id}") }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookScreen(vm: AppViewModel, nav: NavController, proId: Long) {
    val pros by vm.professionals.collectAsState()
    val pro = pros.firstOrNull { it.id == proId }
    val slots = remember { availableSlots() }
    var step by remember { mutableIntStateOf(0) }
    var slot by remember { mutableStateOf<Long?>(null) }
    var reason by remember { mutableStateOf("") }
    var share by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AppScaffold(nav, "Agendar cita") { pad ->
        Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (pro == null) { Body("Profesional no encontrado."); return@Column }
            Text("${pro.avatar} ${pro.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Body("${pro.role} · ${pro.specialty} · ${pro.modality}")
            when (step) {
                0 -> {
                    Kicker("1 · Elige un bloque horario (simulado)")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        slots.forEach { s -> FilterChip(slot == s, { slot = s }, { Text(fmt(s)) }) }
                    }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    GradientButton("Continuar", Modifier.fillMaxWidth()) { if (slot == null) error = "Elige un horario" else { error = null; step = 1 } }
                }
                1 -> {
                    Kicker("2 · Motivo breve de consulta")
                    OutlinedTextField(reason, { reason = it }, Modifier.fillMaxWidth(), label = { Text("¿Qué te gustaría conversar?") }, minLines = 3)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(share, { share = it })
                        Spacer(Modifier.width(10.dp))
                        Text("Autorizo asociar una guía de contexto (mis últimos registros) a esta cita", fontSize = 14.sp)
                    }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    GradientButton("Revisar resumen", Modifier.fillMaxWidth()) {
                        if (reason.trim().length < 5) error = "Cuéntanos brevemente el motivo (mín. 5 letras)" else { error = null; step = 2 }
                    }
                    TextButton({ step = 0 }) { Text("Volver") }
                }
                2 -> {
                    Kicker("3 · Confirma tu cita")
                    SoftCard(container = Mint) {
                        Text("📅 ${fmt(slot ?: 0)}")
                        Body("Motivo: ${reason.trim()}", Navy)
                        Body(if (share) "Compartirás una guía de contexto, solo con tu autorización." else "No compartirás contexto.", Navy)
                    }
                    GradientButton("Confirmar solicitud", Modifier.fillMaxWidth()) {
                        vm.book(pro.id, slot ?: return@GradientButton, reason, share) { step = 3 }
                    }
                    TextButton({ step = 1 }) { Text("Volver") }
                }
                else -> {
                    SoftCard(container = Mint) {
                        Kicker("Cita solicitada")
                        Text("Quedó registrada en tu teléfono.", fontSize = 18.sp)
                        if (share) Body("Tu guía de contexto quedó asociada y estaría disponible para revisión profesional (simulado).", Navy)
                    }
                    GradientButton("Ver mis citas", Modifier.fillMaxWidth()) { nav.navigate("appts") { popUpTo("pros") } }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppointmentsScreen(vm: AppViewModel, nav: NavController) {
    val appts by vm.appointments.collectAsState()
    val pros by vm.professionals.collectAsState()
    val slots = remember { availableSlots() }
    var reschedule by remember { mutableStateOf<Appointment?>(null) }
    AppScaffold(nav, "Mis citas") { pad ->
        LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (appts.isEmpty()) item { Body("Aún no tienes citas. Puedes explorar profesionales cuando quieras.") }
            items(appts, key = { it.id }) { a ->
                val pro = pros.firstOrNull { it.id == a.professionalId }
                SoftCard {
                    Text(pro?.name ?: "Profesional", fontWeight = FontWeight.Bold)
                    Text("📅 ${fmt(a.slot)}")
                    Text(statusLabel(a.status), color = CyanText, fontWeight = FontWeight.SemiBold)
                    Body("Motivo: ${a.reason}")
                    if (a.shareContext) Body("🔒 Guía de contexto autorizada")
                    if (a.status in listOf("SOLICITADA", "CONFIRMADA", "REPROGRAMADA")) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton({ reschedule = a }) { Text("Reprogramar") }
                        OutlinedButton({ vm.setStatus(a, "CANCELADA") }) { Text("Cancelar") }
                    }
                }
            }
        }
    }
    reschedule?.let { a ->
        AlertDialog(
            onDismissRequest = { reschedule = null }, title = { Text("Elige un nuevo horario") },
            text = { Column(Modifier.verticalScroll(rememberScrollState())) {
                slots.forEach { s -> TextButton({ vm.reschedule(a, s); reschedule = null }) { Text(fmt(s)) } }
            } },
            confirmButton = { TextButton({ reschedule = null }) { Text("Cerrar") } }
        )
    }
}

/** Vista del profesional de prueba: revisa solicitudes y la guía de contexto autorizada. */
@Composable
fun ProHomeScreen(vm: AppViewModel, nav: NavController) {
    val appts by vm.allAppointments.collectAsState()
    val users by vm.allUsers.collectAsState()
    val cis by vm.allCheckIns.collectAsState()
    val pros by vm.professionals.collectAsState()
    AppScaffold(nav, "Vista profesional (demo)", tab = null, back = false) { pad ->
        LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Body("Solicitudes de citas ficticias. Solo ves el contexto que la persona autorizó.") }
            if (appts.isEmpty()) item { Body("Sin solicitudes por ahora.") }
            items(appts, key = { it.id }) { a ->
                val person = users.firstOrNull { it.id == a.userId }
                val pro = pros.firstOrNull { it.id == a.professionalId }
                SoftCard {
                    Text("${person?.avatar ?: ""} ${person?.alias ?: "Persona"}", fontWeight = FontWeight.Bold)
                    Body("Con: ${pro?.name ?: "-"} · ${fmt(a.slot)}")
                    Text(statusLabel(a.status), color = CyanText, fontWeight = FontWeight.SemiBold)
                    Body("Motivo: ${a.reason}")
                    if (a.shareContext) {
                        val last = cis.filter { it.userId == a.userId }.take(3)
                        Body("Guía de contexto autorizada: " + if (last.isEmpty()) "sin registros" else last.joinToString(" ") { moodOf(it.mood).emoji + (if (it.category.isNotBlank()) "(${it.category})" else "") })
                    } else Body("🔒 Sin contexto autorizado")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (a.status == "SOLICITADA" || a.status == "REPROGRAMADA")
                            OutlinedButton({ vm.setStatus(a, "CONFIRMADA") }) { Text("Confirmar") }
                        if (a.status == "CONFIRMADA") OutlinedButton({ vm.setStatus(a, "REALIZADA") }) { Text("Marcar realizada") }
                    }
                }
            }
        }
    }
}
