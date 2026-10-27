package cl.duoc.vitairosm

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResourcesScreen(vm: AppViewModel, nav: NavController) {
    val all by vm.resources.collectAsState()
    var query by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("") }
    var favOnly by remember { mutableStateOf(false) }
    var open by remember { mutableStateOf<Long?>(null) }
    val cats = all.map { it.category }.distinct()
    val list = all.filter {
        (cat.isBlank() || it.category == cat) && (!favOnly || it.favorite) &&
            (query.isBlank() || it.title.contains(query, true) || it.body.contains(query, true))
    }
    AppScaffold(nav, "Recursos", tab = "resources") { pad ->
        LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Buscar") })
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(favOnly, { favOnly = !favOnly }, { Text("⭐ Favoritos") })
                    cats.forEach { c -> FilterChip(cat == c, { cat = if (cat == c) "" else c }, { Text(c) }) }
                }
                Body("Disponibles sin conexión. Contenido breve, educativo y no terapéutico.")
            }
            if (list.isEmpty()) item { Body("No hay recursos con ese filtro.") }
            items(list, key = { it.id }) { r ->
                SoftCard(Modifier.clickable { open = if (open == r.id) null else r.id }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Kicker(r.category)
                            Text(r.title, fontWeight = FontWeight.Bold)
                            Body("${r.minutes} min de lectura")
                        }
                        IconButton({ vm.toggleFav(r) }) {
                            Icon(if (r.favorite) Icons.Filled.Star else Icons.Filled.StarBorder, "Favorito", tint = CyanText)
                        }
                    }
                    if (open == r.id) {
                        Body(r.body, Navy)
                        Text("Fuente: ${r.source}", fontSize = 11.sp, color = GrayBlue)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(vm: AppViewModel, nav: NavController) {
    val ctx = LocalContext.current
    val user by vm.user.collectAsState()
    val u = user ?: return
    var alias by remember(u.id) { mutableStateOf(u.alias) }
    var goal by remember(u.id) { mutableStateOf(u.goal) }
    var tName by remember(u.id) { mutableStateOf(u.trustedName) }
    var tPhone by remember(u.id) { mutableStateOf(u.trustedPhone) }
    var pinDialog by remember { mutableStateOf(false) }
    var newPin by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }
    val isUser = u.role == "USUARIO"

    AppScaffold(nav, "Perfil y ajustes") { pad ->
        Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Avatar", fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("🌿", "🌱", "🌊", "☀️", "🌙", "🦋").forEach { a -> FilterChip(u.avatar == a, { vm.updateUser(u.copy(avatar = a)) }, { Text(a, fontSize = 20.sp) }) }
            }
            OutlinedTextField(alias, { alias = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Alias") })
            Body("Correo (simulado): ${u.email}")
            if (isUser) {
                OutlinedTextField(goal, { goal = it }, Modifier.fillMaxWidth(), label = { Text("Objetivo personal general") })
                OutlinedTextField(tName, { tName = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Persona de confianza") })
                OutlinedTextField(tPhone, { tPhone = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Teléfono de confianza") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            }
            GradientButton("Guardar cambios", Modifier.fillMaxWidth(), enabled = alias.isNotBlank()) {
                vm.updateUser(u.copy(alias = alias.trim(), goal = goal.trim(), trustedName = tName.trim(), trustedPhone = tPhone.trim())); saved = true
            }
            if (saved) Body("Cambios guardados en tu teléfono.")

            if (isUser) {
                HorizontalDivider()
                Kicker("Recordatorios")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(u.reminderOn, { vm.updateUser(u.copy(reminderOn = it)) }); Spacer(Modifier.width(10.dp))
                    Text("Check-in diario a las %02d:%02d".format(u.reminderHour, u.reminderMinute))
                }
                OutlinedButton({
                    TimePickerDialog(ctx, { _, h, m -> vm.updateUser(u.copy(reminderHour = h, reminderMinute = m)) }, u.reminderHour, u.reminderMinute, true).show()
                }) { Text("Cambiar hora") }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(u.apptReminderOn, { vm.updateUser(u.copy(apptReminderOn = it)) }); Spacer(Modifier.width(10.dp))
                    Text("Aviso 2 horas antes de una cita")
                }
            }

            HorizontalDivider()
            Kicker("Privacidad")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(u.pinEnabled, { if (it) { newPin = ""; pinDialog = true } else vm.updateUser(u.copy(pinEnabled = false, pin = "")) })
                Spacer(Modifier.width(10.dp)); Text("Bloqueo con PIN de 4 dígitos")
            }

            HorizontalDivider()
            Kicker("Ayuda / Acerca de")
            Body(DISCLAIMER)
            Body("Versión académica DSY1105. Todos los datos son ficticios o sintéticos.")
            OutlinedButton({ nav.navigate("crisis") }, Modifier.fillMaxWidth()) { Text("Ayuda urgente") }
            OutlinedButton({ vm.logout(); nav.navigate("gate") { popUpTo(0) } }, Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
        }
    }
    if (pinDialog) AlertDialog(
        onDismissRequest = { pinDialog = false }, title = { Text("Crea tu PIN") },
        text = {
            OutlinedTextField(newPin, { if (it.length <= 4 && it.all(Char::isDigit)) newPin = it }, singleLine = true,
                label = { Text("4 dígitos") }, visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword))
        },
        confirmButton = { TextButton({ vm.updateUser(u.copy(pinEnabled = true, pin = newPin)); pinDialog = false }, enabled = newPin.length == 4) { Text("Guardar") } },
        dismissButton = { TextButton({ pinDialog = false }) { Text("Cancelar") } }
    )
}
