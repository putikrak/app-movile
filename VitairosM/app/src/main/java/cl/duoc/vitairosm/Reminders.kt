package cl.duoc.vitairosm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object Reminders {
    private const val CHANNEL = "vitairos_reminders"

    private fun delayUntil(h: Int, m: Int): Long {
        val c = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, m); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        if (c.timeInMillis <= System.currentTimeMillis()) c.add(Calendar.DAY_OF_YEAR, 1)
        return c.timeInMillis - System.currentTimeMillis()
    }

    fun scheduleDaily(ctx: Context, h: Int, m: Int) {
        val req = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayUntil(h, m), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("kind" to "daily", "h" to h, "m" to m)).build()
        WorkManager.getInstance(ctx).enqueueUniqueWork("daily", ExistingWorkPolicy.REPLACE, req)
    }
    fun cancelDaily(ctx: Context) { WorkManager.getInstance(ctx).cancelUniqueWork("daily") }

    /** Aviso 2 horas antes de la cita ficticia. */
    fun scheduleAppt(ctx: Context, apptId: Long, slot: Long) {
        val delay = slot - 2 * 3_600_000L - System.currentTimeMillis()
        if (delay <= 0) return
        val req = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("kind" to "appt")).build()
        WorkManager.getInstance(ctx).enqueueUniqueWork("appt_$apptId", ExistingWorkPolicy.REPLACE, req)
    }
    fun cancelAppt(ctx: Context, apptId: Long) { WorkManager.getInstance(ctx).cancelUniqueWork("appt_$apptId") }

    fun show(ctx: Context, title: String, text: String, id: Int) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26)
            nm.createNotificationChannel(NotificationChannel(CHANNEL, "Recordatorios", NotificationManager.IMPORTANCE_DEFAULT))
        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title).setContentText(text).setAutoCancel(true).build()
        nm.notify(id, n)
    }
}

class ReminderWorker(ctx: Context, p: WorkerParameters) : CoroutineWorker(ctx, p) {
    override suspend fun doWork(): Result {
        when (inputData.getString("kind")) {
            "daily" -> {
                Reminders.show(applicationContext, "Un momento para ti",
                    "Si te sirve, puedes registrar cómo te sientes hoy.", 1)
                Reminders.scheduleDaily(applicationContext, inputData.getInt("h", 20), inputData.getInt("m", 0))
            }
            "appt" -> Reminders.show(applicationContext, "Tu cita es pronto",
                "Tienes una cita ficticia en unas 2 horas. Puedes revisarla en la app.", 2)
        }
        return Result.success()
    }
}
