package cl.duoc.vitairosm

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Paleta oficial (Manual de identidad visual v3.0)
val Navy = Color(0xFF000E2E)
val GrayBlue = Color(0xFF536477)
val Green = Color(0xFF00C98E)
val Cyan = Color(0xFF00B9D3)
val Blue = Color(0xFF00A5F4)
val CyanText = Color(0xFF007F9E)
val Bg = Color(0xFFFEFEFE)
val Soft = Color(0xFFF2F8FB)
val Mint = Color(0xFFDDF6F0)
val SoftBlue = Color(0xFFE1F3FE)
val Coral = Color(0xFFFFDAD3)
val MainGradient = Brush.horizontalGradient(listOf(Green, Cyan, Blue))

@Composable
fun VitairosTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = CyanText, onPrimary = Color.White,
            primaryContainer = Mint, onPrimaryContainer = Navy,
            secondary = Cyan, secondaryContainer = SoftBlue, onSecondaryContainer = Navy,
            background = Bg, onBackground = Navy, surface = Bg, onSurface = Navy,
            surfaceVariant = Soft, onSurfaceVariant = GrayBlue,
            outlineVariant = Color(0xFFD5E3EA)
        ),
        shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(28.dp)),
        content = content
    )
}

/** El logo se usa SOLO como imagen oficial (res/drawable/logo_vitairos.png). Nunca se reescribe con tipografía. */
@Composable
fun LogoImage(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val id = ctx.resources.getIdentifier("logo_vitairos", "drawable", ctx.packageName)
    if (id != 0) Image(painterResource(id), "VITAIROS-M Salud mental digital", modifier, contentScale = ContentScale.Fit)
    else Box(modifier, contentAlignment = Alignment.Center) { Text("[ logo oficial pendiente ]", color = GrayBlue) }
}

@Composable
fun GradientButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier.height(52.dp).clip(RoundedCornerShape(50))
            .background(if (enabled) MainGradient else SolidColor(Color(0xFFD5E3EA)))
            .clickable(enabled = enabled, onClick = onClick).padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) { Text(text, color = Navy, fontWeight = FontWeight.Bold) }
}

@Composable
fun SoftCard(modifier: Modifier = Modifier, container: Color = Soft, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = container)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable fun H(text: String) = Text(text, fontSize = 26.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, color = Navy)
@Composable fun Body(text: String, color: Color = GrayBlue) = Text(text, color = color, fontSize = 15.sp, lineHeight = 22.sp)
@Composable fun Kicker(text: String) = Text(text.uppercase(), color = CyanText, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

@Composable
fun HelpButton(nav: NavController) {
    FilledTonalButton(
        onClick = { nav.navigate("crisis") { launchSingleTop = true } },
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Coral, contentColor = Navy),
        contentPadding = PaddingValues(horizontal = 12.dp), modifier = Modifier.height(36.dp)
    ) {
        Icon(Icons.Filled.Favorite, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp))
        Text("Ayuda urgente", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)
private val TABS = listOf(
    Tab("home", "Inicio", Icons.Filled.Home), Tab("history", "Bitácora", Icons.Filled.EditNote),
    Tab("chat", "Hablar", Icons.Filled.Chat), Tab("pros", "Profesionales", Icons.Filled.Event),
    Tab("resources", "Recursos", Icons.Filled.MenuBook)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(nav: NavController, title: String, tab: String? = null, back: Boolean = tab == null, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 1) },
                navigationIcon = { if (back) IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    HelpButton(nav)
                    IconButton({ nav.navigate("profile") }) { Icon(Icons.Filled.AccountCircle, "Perfil") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg, titleContentColor = Navy)
            )
        },
        bottomBar = {
            if (tab != null) NavigationBar(containerColor = Soft) {
                TABS.forEach { t ->
                    NavigationBarItem(
                        selected = tab == t.route,
                        onClick = {
                            if (tab != t.route) nav.navigate(t.route) {
                                popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true
                            }
                        },
                        icon = { Icon(t.icon, null) },
                        label = { Text(t.label, fontSize = 10.sp, maxLines = 1) }
                    )
                }
            }
        },
        content = content
    )
}
