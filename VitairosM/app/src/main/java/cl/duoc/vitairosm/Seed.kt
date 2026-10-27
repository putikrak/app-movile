package cl.duoc.vitairosm

const val SRC_DEMO = "Contenido de demostración (sintético) – VITAIROS-M"

/** Dataset sintético. Reemplazar/ampliar con el JSON que entregue VITAIROS. */
suspend fun seedIfEmpty(db: AppDb) {
    if (db.users().byEmail("demo@vitairos.cl") == null) {
        val id = db.users().insert(
            User(alias = "Alex Demo", email = "demo@vitairos.cl", password = "demo1234", avatar = "🌱",
                goal = "Tomarme pausas durante la semana",
                trustedName = "Persona de confianza (demo)", trustedPhone = "+56911111111")
        )
        val day = 86_400_000L
        val now = System.currentTimeMillis()
        val moods = listOf(3, 4, 2, 3, 5, 4)
        moods.forEachIndexed { i, m ->
            db.checkIns().insert(
                CheckIn(userId = id, mood = m, category = CATEGORIES[i % CATEGORIES.size],
                    energy = (m % 5) + 1, note = "Registro de demostración número ${i + 1}.",
                    createdAt = now - (i + 1) * day, synced = true)
            )
        }
    }
    if (db.users().byEmail("pro@vitairos.cl") == null) {
        db.users().insert(User(alias = "Profesional Demo", email = "pro@vitairos.cl", password = "pro1234",
            role = "PROFESIONAL", avatar = "🩺"))
    }
    if (db.professionals().count() == 0) {
        db.professionals().insertAll(listOf(
            Professional(1, "Camila Rojas (ficticia)", "Psicóloga", "Estrés,Sueño", "Estrés académico y laboral",
                "Acompaña procesos de organización y manejo del estrés. Perfil ficticio.", "👩‍⚕️", "Lun a Vie · tardes"),
            Professional(2, "Mateo Fuentes (ficticio)", "Psicólogo", "Ansiedad,Estrés", "Ansiedad y regulación emocional",
                "Orientación en regulación emocional general. Perfil ficticio.", "🧑‍⚕️", "Mar y Jue · mañanas"),
            Professional(3, "Valentina Soto (ficticia)", "Tutora de bienestar", "Sueño,Organización", "Hábitos de sueño y rutinas",
                "Apoyo en rutinas, descanso y autocuidado. Perfil ficticio.", "🌿", "Lun, Mié y Vie"),
            Professional(4, "Ignacio Paredes (ficticio)", "Psicólogo", "Relaciones,Familia", "Relaciones y familia",
                "Orientación en vínculos y comunicación. Perfil ficticio.", "🧑‍💼", "Mié a Sáb · tardes")
        ))
    }
    if (db.resources().count() == 0) {
        db.resources().insertAll(listOf(
            Resource(1, "Una pausa de respiración en 2 minutos", "Respiración y relajación", 2,
                "Siéntate cómodo/a. Inhala por la nariz contando 4, sostén el aire contando 7 y exhala lento contando 8. Repite 4 veces. Si te mareas, vuelve a respirar normal.", SRC_DEMO),
            Resource(2, "Cuando el estrés académico se acumula", "Estrés", 2,
                "Escribe todo lo pendiente, elige solo una tarea pequeña de 10 minutos y comienza por ella. Avanzar un poco suele bajar la sensación de bloqueo.", SRC_DEMO),
            Resource(3, "Rutina simple para dormir mejor", "Sueño y descanso", 2,
                "Mantén horarios parecidos, baja las pantallas un rato antes de acostarte y evita la cafeína en la tarde. Pequeños cambios constantes ayudan más que grandes cambios aislados.", SRC_DEMO),
            Resource(4, "Grounding 5-4-3-2-1", "Emociones", 1,
                "Nota 5 cosas que ves, 4 que puedes tocar, 3 que oyes, 2 que hueles y 1 que saboreas. Ayuda a volver al presente.", SRC_DEMO),
            Resource(5, "Autocuidado en lo cotidiano", "Autocuidado y rutinas", 2,
                "Agua, comida, movimiento suave y descanso son una base. Elige una acción pequeña para hoy y repítela mañana.", SRC_DEMO),
            Resource(6, "Organización ante la sobrecarga", "Organización", 2,
                "Divide lo grande en partes pequeñas, prioriza 1 a 3 cosas por día y deja espacio para imprevistos.", SRC_DEMO),
            Resource(7, "Apoyarte en otras personas", "Apoyo social", 1,
                "Contarle a alguien de confianza cómo estás puede aliviar. No necesitas tener todo claro para decir 'hoy ando con peso'.", SRC_DEMO),
            Resource(8, "¿Cuándo considerar apoyo profesional?", "Cuándo buscar apoyo", 2,
                "Si lo que sientes se mantiene en el tiempo, afecta tu día a día o te supera, hablar con un profesional puede ayudar. Si hay riesgo inmediato, usa la sección de Ayuda urgente.", SRC_DEMO)
        ))
    }
}
