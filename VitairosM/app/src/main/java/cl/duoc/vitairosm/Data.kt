package cl.duoc.vitairosm

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alias: String,
    val email: String,
    val password: String,            // simulado (MVP académico). En producción: hash + backend.
    val role: String = "USUARIO",    // USUARIO | PROFESIONAL
    val avatar: String = "🌿",
    val goal: String = "",
    val reminderOn: Boolean = true,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val apptReminderOn: Boolean = true,
    val pinEnabled: Boolean = false,
    val pin: String = "",
    val trustedName: String = "",
    val trustedPhone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "checkins")
data class CheckIn(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val mood: Int,                   // 1..5 (obligatorio)
    val category: String = "",
    val emotions: String = "",       // CSV
    val energy: Int = 0,             // 0 = sin registrar, 1..5
    val note: String = "",
    val activity: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null,
    val synced: Boolean = false
)

@Entity(tableName = "professionals")
data class Professional(
    @PrimaryKey val id: Long,
    val name: String,
    val role: String,
    val areas: String,
    val specialty: String,
    val description: String,
    val avatar: String,
    val availability: String,
    val modality: String = "Online (simulada)"
)

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val professionalId: Long,
    val slot: Long,
    val reason: String,
    val shareContext: Boolean,
    val status: String = "SOLICITADA", // SOLICITADA|CONFIRMADA|REPROGRAMADA|CANCELADA|REALIZADA
    val createdAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)

@Entity(tableName = "resources")
data class Resource(
    @PrimaryKey val id: Long,
    val title: String,
    val category: String,
    val minutes: Int,
    val body: String,
    val source: String,
    val favorite: Boolean = false
)

@Dao
interface UserDao {
    @Insert suspend fun insert(u: User): Long
    @Update suspend fun update(u: User)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1") suspend fun byEmail(email: String): User?
    @Query("SELECT * FROM users WHERE id = :id") suspend fun byId(id: Long): User?
    @Query("SELECT * FROM users WHERE id = :id") fun observe(id: Long): Flow<User?>
    @Query("SELECT * FROM users") fun observeAll(): Flow<List<User>>
}

@Dao
interface CheckInDao {
    @Insert suspend fun insert(c: CheckIn): Long
    @Update suspend fun update(c: CheckIn)
    @Delete suspend fun delete(c: CheckIn)
    @Query("SELECT * FROM checkins WHERE id = :id") suspend fun byId(id: Long): CheckIn?
    @Query("SELECT * FROM checkins WHERE userId = :uid ORDER BY createdAt DESC") fun observe(uid: Long): Flow<List<CheckIn>>
    @Query("SELECT * FROM checkins ORDER BY createdAt DESC") fun observeAll(): Flow<List<CheckIn>>
    @Query("UPDATE checkins SET synced = 1 WHERE userId = :uid") suspend fun markSynced(uid: Long)
}

@Dao
interface ProfessionalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(list: List<Professional>)
    @Query("SELECT COUNT(*) FROM professionals") suspend fun count(): Int
    @Query("SELECT * FROM professionals") fun observe(): Flow<List<Professional>>
}

@Dao
interface AppointmentDao {
    @Insert suspend fun insert(a: Appointment): Long
    @Update suspend fun update(a: Appointment)
    @Query("SELECT * FROM appointments WHERE userId = :uid ORDER BY slot ASC") fun observe(uid: Long): Flow<List<Appointment>>
    @Query("SELECT * FROM appointments ORDER BY slot ASC") fun observeAll(): Flow<List<Appointment>>
    @Query("UPDATE appointments SET synced = 1 WHERE userId = :uid") suspend fun markSynced(uid: Long)
}

@Dao
interface ResourceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(list: List<Resource>)
    @Query("SELECT COUNT(*) FROM resources") suspend fun count(): Int
    @Query("SELECT * FROM resources ORDER BY id") fun observe(): Flow<List<Resource>>
    @Query("UPDATE resources SET favorite = :fav WHERE id = :id") suspend fun setFav(id: Long, fav: Boolean)
}

@Database(
    entities = [User::class, CheckIn::class, Professional::class, Appointment::class, Resource::class],
    version = 1, exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun users(): UserDao
    abstract fun checkIns(): CheckInDao
    abstract fun professionals(): ProfessionalDao
    abstract fun appointments(): AppointmentDao
    abstract fun resources(): ResourceDao

    companion object {
        @Volatile private var instance: AppDb? = null
        fun get(ctx: Context): AppDb = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(ctx.applicationContext, AppDb::class.java, "vitairosm.db")
                .build().also { instance = it }
        }
    }
}
