package cl.duoc.vitairosm

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

val CATEGORIES = listOf("Estudios/Académico", "Trabajo", "Relaciones/Pareja", "Familia", "Sueño/Descanso", "Salud/Bienestar", "Otro")
val EMOTIONS = listOf("Ansiedad", "Frustración", "Cansancio", "Calma", "Gratitud")

data class Mood(val value: Int, val emoji: String, val label: String)
val MOODS = listOf(
    Mood(1, "😞", "Muy bajo"), Mood(2, "😕", "Bajo"), Mood(3, "😐", "Neutro"),
    Mood(4, "🙂", "Bien"), Mood(5, "😄", "Muy bien")
)
fun moodOf(v: Int) = MOODS.firstOrNull { it.value == v } ?: MOODS[2]

const val DISCLAIMER =
    "VITAIROS-M es una herramienta de orientación y acompañamiento general. No realiza diagnósticos, " +
    "no prescribe tratamientos, no entrega terapia y no reemplaza la atención de profesionales de salud. " +
    "Si existe riesgo inmediato o una emergencia, busca ayuda presencial y utiliza los canales oficiales " +
    "de emergencia disponibles en Chile."

// ---------- Detección de señales críticas (lista inicial; el equipo debe afinarla con VITAIROS) ----------
private val CRISIS_WORDS = listOf(
    "suicid", "quitarme la vida", "quiero morir", "quiero morirme", "no quiero vivir", "matarme",
    "acabar con mi vida", "hacerme dano", "no aguanto mas", "desaparecer para siempre", "terminar con todo"
)
private fun normalize(s: String) =
    Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
fun isCrisis(text: String): Boolean = normalize(text).let { t -> CRISIS_WORDS.any { t.contains(it) } }

// ---------- Fechas ----------
private val dateFmt = SimpleDateFormat("EEE d MMM, HH:mm", Locale("es", "CL"))
fun fmt(t: Long): String = dateFmt.format(Date(t))
fun dayOf(t: Long): LocalDate = Instant.ofEpochMilli(t).atZone(ZoneId.systemDefault()).toLocalDate()

fun streak(list: List<CheckIn>): Int {
    val days = list.map { dayOf(it.createdAt) }.toSet()
    var d = LocalDate.now()
    if (d !in days) d = d.minusDays(1)
    var n = 0
    while (d in days) { n++; d = d.minusDays(1) }
    return n
}

/** Bloques horarios simulados para los próximos 4 días. */
fun availableSlots(): List<Long> {
    val out = mutableListOf<Long>()
    for (d in 1..4) for (h in listOf(10, 12, 16)) {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, d)
        c.set(Calendar.HOUR_OF_DAY, h); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
        out += c.timeInMillis
    }
    return out
}

fun statusLabel(s: String) = when (s) {
    "SOLICITADA" -> "⏳ Solicitada"
    "CONFIRMADA" -> "✅ Confirmada"
    "REPROGRAMADA" -> "🔄 Reprogramada"
    "CANCELADA" -> "✖ Cancelada"
    "REALIZADA" -> "✔ Realizada"
    else -> s
}

fun isOnline(ctx: Context): Boolean {
    val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// ---------- Árbol de orientación determinístico (sin LLM) ----------
data class Step(val title: String, val why: String, val button: String, val route: String)
data class Opt(val label: String, val next: String? = null, val step: Step? = null)
data class Node(val text: String, val options: List<Opt>)

private val breathing = Step("Hacer una respiración guiada", "Una pausa corta ayuda a bajar el ritmo y volver al presente.", "Hacer actividad", "breathing")
private val checkin = Step("Registrar cómo te sientes", "Ponerle nombre a lo que sientes ayuda a tener más claridad.", "Hacer check-in", "checkin")
private val resources = Step("Ver un recurso breve", "Un recurso de 1 a 2 minutos puede darte ideas simples para ahora.", "Ver recurso", "resources")
private val pros = Step("Explorar profesionales", "Si lo que sientes te supera o se mantiene, un profesional puede acompañarte mejor.", "Explorar profesionales", "pros")
private val later = Step("Dar un primer paso pequeño", "Empezar con algo muy pequeño reduce la sensación de bloqueo.", "Guardar para después", "save")
private val trusted = Step("Hablar con alguien de confianza", "Contarle a alguien cómo estás puede hacer menos pesado el momento.", "Guardar para después", "save")

val CHAT: Map<String, Node> = mapOf(
    "start" to Node("Hola 👋 Estoy aquí para orientarte de forma general. ¿Qué te gustaría ahora?", listOf(
        Opt("Me siento sobrecargado/a", next = "overload"),
        Opt("Me cuesta dormir", next = "sleep"),
        Opt("Siento ansiedad o tensión", next = "tension"),
        Opt("Quiero desahogarme", next = "vent"),
        Opt("Quiero ver profesionales", step = pros)
    )),
    "overload" to Node("Gracias por contarlo. Cuando todo se junta, puede servir bajar el ritmo. ¿Te gustaría…?", listOf(
        Opt("Hacer una pausa de respiración", step = breathing),
        Opt("Ordenar una tarea pequeña", step = later),
        Opt("Ver recursos sobre sobrecarga", step = resources)
    )),
    "sleep" to Node("Dormir mal desgasta. Podemos probar algo simple. ¿Qué prefieres?", listOf(
        Opt("Ver una rutina de sueño", step = resources),
        Opt("Respiración guiada", step = breathing),
        Opt("Registrar cómo me siento", step = checkin)
    )),
    "tension" to Node("Puedes considerar alguna de estas opciones. ¿Cuál te sirve más?", listOf(
        Opt("Respiración guiada", step = breathing),
        Opt("Grounding 5-4-3-2-1", next = "grounding"),
        Opt("Creo que necesito apoyo profesional", next = "needpro")
    )),
    "grounding" to Node("Mira a tu alrededor: 5 cosas que ves, 4 que puedes tocar, 3 que oyes, 2 que hueles y 1 que saboreas. Tómate tu tiempo.", listOf(
        Opt("Listo, lo hice", step = checkin)
    )),
    "needpro" to Node("Si lo que sientes se mantiene o te supera, hablar con un profesional puede ayudar. ¿Te gustaría…?", listOf(
        Opt("Ver profesionales", step = pros),
        Opt("Pensar en alguien de confianza", step = trusted)
    )),
    "vent" to Node("Puedes escribir lo que quieras en el cuadro de abajo, o elegir una opción:", listOf(
        Opt("Escribirlo en mi bitácora", step = checkin),
        Opt("Prefiero una pausa", step = breathing)
    ))
)
