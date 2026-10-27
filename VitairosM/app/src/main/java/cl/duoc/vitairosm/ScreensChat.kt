package cl.duoc.vitairosm

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun ChatScreen(vm: AppViewModel, nav: NavController) {
    val ctx = LocalContext.current
    val msgs = remember { mutableStateListOf<Pair<Boolean, String>>() }
    var node by remember { mutableStateOf("start") }
    var step by remember { mutableStateOf<Step?>(null) }
    var input by remember { mutableStateOf("") }
    var showIntro by remember { mutableStateOf(!vm.chatIntroSeen) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { if (msgs.isEmpty()) msgs.add(false to CHAT.getValue("start").text) }
    LaunchedEffect(msgs.size, step) { if (msgs.isNotEmpty()) listState.animateScrollToItem(msgs.size) }

    fun pick(o: Opt) {
        msgs.add(true to o.label)
        if (o.step != null) { step = o.step; msgs.add(false to "Gracias por contarlo. Te dejo un paso concreto:") }
        else { node = o.next ?: "start"; msgs.add(false to CHAT.getValue(node).text) }
    }
    fun send() {
        val t = input.trim()
        if (t.isEmpty()) return
        input = ""
        if (isCrisis(t)) { nav.navigate("crisis"); return }
        msgs.add(true to t)
        msgs.add(false to "Gracias por compartirlo. Si te sirve, puedes elegir una de estas opciones:")
        node = "start"; step = null
    }
    fun runStep(s: Step) {
        when (s.route) {
            "save" -> { vm.savePending(s.title); Toast.makeText(ctx, "Guardado en tu Inicio", Toast.LENGTH_SHORT).show(); nav.navigate("home") }
            "checkin" -> nav.navigate("checkin/0")
            else -> nav.navigate(s.route)
        }
    }

    AppScaffold(nav, "Hablar con VITAIROS-M", tab = "chat") { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp), state = listState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(msgs.toList()) { (mine, t) ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
                        Box(Modifier.widthIn(max = 300.dp).background(if (mine) SoftBlue else Soft, RoundedCornerShape(18.dp)).padding(12.dp)) {
                            Text(t, color = Navy, fontSize = 15.sp)
                        }
                    }
                }
                item {
                    val s = step
                    if (s == null) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            CHAT.getValue(node).options.forEach { o -> OutlinedButton({ pick(o) }, Modifier.fillMaxWidth()) { Text(o.label) } }
                        }
                    } else {
                        SoftCard(container = Mint) {
                            Kicker("Tu próximo paso")
                            Text(s.title, fontSize = 18.sp)
                            Body(s.why, Navy)
                            GradientButton(s.button, Modifier.fillMaxWidth()) { runStep(s) }
                            TextButton({ step = null; node = "start" }) { Text("Seguir conversando") }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(input, { input = it }, Modifier.weight(1f), placeholder = { Text("Escribe lo que quieras…") }, maxLines = 3)
                IconButton({ send() }) { Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = CyanText) }
            }
        }
    }
    if (showIntro) AlertDialog(
        onDismissRequest = {}, title = { Text("Antes de comenzar") }, text = { Text(DISCLAIMER) },
        confirmButton = { TextButton({ vm.markChatIntro(); showIntro = false }) { Text("Entiendo") } }
    )
}

@Composable
fun BreathingScreen(nav: NavController) {
    var running by remember { mutableStateOf(false) }
    var phase by remember { mutableStateOf("Cuando estés listo/a, presiona comenzar") }
    var secs by remember { mutableIntStateOf(0) }
    var cycle by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        if (running) {
            finished = false
            for (c in 1..4) {
                cycle = c
                for ((name, s) in listOf("Inhala por la nariz" to 4, "Sostén el aire" to 7, "Exhala lentamente" to 8)) {
                    phase = name
                    for (t in s downTo 1) { secs = t; delay(1000) }
                }
            }
            phase = "Gracias por darte esta pausa"; secs = 0; running = false; finished = true
        }
    }
    AppScaffold(nav, "Respiración guiada") { pad ->
        Column(Modifier.padding(pad).padding(24.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Body("Técnica 4-7-8 · 4 ciclos. Si te mareas, detente y respira con normalidad.")
            Box(Modifier.size(200.dp).background(Mint, RoundedCornerShape(100.dp)), contentAlignment = Alignment.Center) {
                Text(if (running) "$secs" else "🌿", fontSize = 56.sp, color = Navy)
            }
            Text(phase, fontSize = 20.sp, color = Navy)
            if (running) Body("Ciclo $cycle de 4")
            GradientButton(if (running) "Detener" else "Comenzar", Modifier.fillMaxWidth()) {
                if (running) { running = false; phase = "Pausa detenida. Puedes retomar cuando quieras." } else running = true
            }
            if (finished) {
                SoftCard(container = SoftBlue) {
                    Kicker("Tu próximo paso")
                    Body("Después de una pausa puede ser útil registrar cómo te sientes.", Navy)
                    GradientButton("Hacer check-in", Modifier.fillMaxWidth()) { nav.navigate("checkin/0") }
                }
            }
        }
    }
}

/** Pantalla de alta prioridad. Números de Chile: verificar antes de cualquier despliegue real. */
@Composable
fun CrisisScreen(vm: AppViewModel, nav: NavController) {
    val ctx = LocalContext.current
    val user by vm.user.collectAsState()
    fun dial(number: String) {
        runCatching { ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)))) }
    }
    AppScaffold(nav, "Ayuda urgente") { pad ->
        Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            H("No tienes que pasar por esto solo/a")
            Body("Si estás en riesgo inmediato o sientes que podrías hacerte daño, busca ayuda ahora. Estos canales están a un toque:", Navy)
            GradientButton("Fono Prevención del Suicidio *4141", Modifier.fillMaxWidth()) { dial("*4141") }
            OutlinedButton({ dial("6003607777") }, Modifier.fillMaxWidth()) { Text("Salud Responde 600 360 7777") }
            OutlinedButton({ dial("131") }, Modifier.fillMaxWidth()) { Text("Emergencias médicas 131") }
            OutlinedButton({ dial("133") }, Modifier.fillMaxWidth()) { Text("Carabineros 133") }
            val u = user
            if (u != null && u.trustedPhone.isNotBlank()) {
                SoftCard(container = Mint) {
                    Kicker("Persona de confianza")
                    Text("${u.trustedName.ifBlank { "Contacto" }} · ${u.trustedPhone}")
                    OutlinedButton({ dial(u.trustedPhone) }, Modifier.fillMaxWidth()) { Text("Llamar") }
                    OutlinedButton({
                        val i = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${u.trustedPhone}"))
                            .putExtra("sms_body", "Hola, estoy pasando un momento difícil y me gustaría que me llamaras o estuvieras conmigo. (Mensaje desde VITAIROS-M)")
                        runCatching { ctx.startActivity(i) }
                    }, Modifier.fillMaxWidth()) { Text("Enviar mensaje preparado") }
                }
            }
            if (u != null) OutlinedButton({ nav.navigate("pros") }, Modifier.fillMaxWidth()) { Text("Ver profesionales disponibles en la app") }
            Body("VITAIROS-M no entrega diagnósticos ni atiende urgencias. Canales formales: MINSAL.")
        }
    }
}
