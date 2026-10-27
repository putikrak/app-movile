package cl.duoc.vitairosm

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    private val notifPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33) notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        setContent { VitairosTheme { AppNav(viewModel()) } }
    }
}

@Composable
fun AppNav(vm: AppViewModel) {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "gate") {
        composable("gate") { GateScreen(vm, nav) }
        composable("onboarding") { OnboardingScreen(vm, nav) }
        composable("auth") { AuthScreen(vm, nav) }
        composable("lock") { LockScreen(vm, nav) }
        composable("home") { HomeScreen(vm, nav) }
        composable("proHome") { ProHomeScreen(vm, nav) }
        composable("history") { HistoryScreen(vm, nav) }
        composable("checkin/{id}", listOf(navArgument("id") { type = NavType.LongType })) {
            CheckInScreen(vm, nav, it.arguments?.getLong("id") ?: 0L)
        }
        composable("chat") { ChatScreen(vm, nav) }
        composable("breathing") { BreathingScreen(nav) }
        composable("crisis") { CrisisScreen(vm, nav) }
        composable("pros") { ProfessionalsScreen(vm, nav) }
        composable("book/{id}", listOf(navArgument("id") { type = NavType.LongType })) {
            BookScreen(vm, nav, it.arguments?.getLong("id") ?: 0L)
        }
        composable("appts") { AppointmentsScreen(vm, nav) }
        composable("resources") { ResourcesScreen(vm, nav) }
        composable("profile") { ProfileScreen(vm, nav) }
    }
}

/** Decide a qué pantalla ir según consentimiento, sesión, PIN y rol. */
@Composable
fun GateScreen(vm: AppViewModel, nav: NavController) {
    val user by vm.user.collectAsState()
    val uid by vm.userId.collectAsState()
    LaunchedEffect(vm.ready, user, uid, vm.consent, vm.locked) {
        if (!vm.ready) return@LaunchedEffect
        val u = user
        val dest = when {
            !vm.consent -> "onboarding"
            uid == 0L -> "auth"
            u == null -> null
            u.pinEnabled && vm.locked -> "lock"
            u.role == "PROFESIONAL" -> "proHome"
            else -> "home"
        }
        if (dest != null) nav.navigate(dest) { popUpTo("gate") { inclusive = true } }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}
