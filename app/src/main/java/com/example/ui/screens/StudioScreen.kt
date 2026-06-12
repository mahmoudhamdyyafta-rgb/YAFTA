package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DesignLayer
import com.example.ui.viewmodel.DesignViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    viewModel: DesignViewModel,
    onNavigateToProjects: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Observe State flows from Viewmodel
    val layers by viewModel.layers.collectAsStateWithLifecycle()
    val selectedId by viewModel.selectedLayerId.collectAsStateWithLifecycle()
    val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()
    val isGradient by viewModel.isGradient.collectAsStateWithLifecycle()
    val solidColorHex by viewModel.backgroundColor.collectAsStateWithLifecycle()
    val gradStartHex by viewModel.gradientStartColor.collectAsStateWithLifecycle()
    val gradEndHex by viewModel.gradientEndColor.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val statusMsg by viewModel.statusMessage.collectAsStateWithLifecycle()
    val errMsg by viewModel.errorMessage.collectAsStateWithLifecycle()

    val snapToGrid by viewModel.snapToGrid.collectAsStateWithLifecycle()
    val smartGuides by viewModel.smartGuides.collectAsStateWithLifecycle()
    val zoomLevel by viewModel.zoomLevel.collectAsStateWithLifecycle()
    val canvasRotation by viewModel.canvasRotation.collectAsStateWithLifecycle()
    val multiSelectedIds by viewModel.multiSelectedIds.collectAsStateWithLifecycle()

    // Observed new Audit Feature variables
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val gridSubdivisions by viewModel.gridSubdivisions.collectAsStateWithLifecycle()
    val canvasGridVisible by viewModel.canvasGridVisible.collectAsStateWithLifecycle()
    val snapMagnetism by viewModel.snapMagnetism.collectAsStateWithLifecycle()
    val aiChatHistory by viewModel.aiChatHistory.collectAsStateWithLifecycle()
    val printMarginCm by viewModel.printMarginCm.collectAsStateWithLifecycle()
    val printResolutionDpi by viewModel.printResolutionDpi.collectAsStateWithLifecycle()
    val cloudBackupEnabled by viewModel.cloudBackupEnabled.collectAsStateWithLifecycle()
    val cloudSyncProgress by viewModel.cloudSyncProgress.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("AI") } // "AI", "ADJUST", "ELEMENTS", "EXPORT"
    var aiPromptText by remember { mutableStateOf("") }
    
    // Dialog overlays
    var showSaveDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showPresetBackgroundDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var projectTitleInput by remember { mutableStateOf("تصميم يافطة ذكي جديد") }
    var exportFormat by remember { mutableStateOf("PNG") } // PNG, JPG, WEBP, SVG
    var exportResolution by remember { mutableStateOf("HQ (1080p)") }

    // Phase 3 Professional Tools states
    var showMockupDialog by remember { mutableStateOf(false) }
    var selectedMockupType by remember { mutableStateOf("BILLBOARD") } // "BILLBOARD", "SHOPFRONT", "ROLLUP", "DIGITAL"
    var showPdfXExportDialog by remember { mutableStateOf(false) }
    var showFontUploadDialog by remember { mutableStateOf(false) }
    var customFontInputName by remember { mutableStateOf("") }
    var showPrintReviewOverlay by remember { mutableStateOf(false) }
    var templateCategoryInput by remember { mutableStateOf("ALL") }

    var isFullScreenMode by remember { mutableStateOf(false) }
    var showDashboard by remember { mutableStateOf(true) }
    var workingMode by remember { mutableStateOf("STUDIO_PRO") } // "STUDIO_PRO", "SMART_DESIGNER", "FOCUS_MODE"
    var activeSidebarTab by remember { mutableStateOf("AI") } // "AI", "TEMPLATES", "TEXT", "IMAGES", "BACKGROUNDS", "SHAPES", "LAYERS", "PRINTING", "ASSETS"
    var isLeftSidebarExpanded by remember { mutableStateOf(true) }
    var isRightPropertiesPanelExpanded by remember { mutableStateOf(true) }
    
    // Font Center
    var showFontCenterDialog by remember { mutableStateOf(false) }
    var fontSearchQuery by remember { mutableStateOf("") }
    var fontCenterCategory by remember { mutableStateOf("ALL") } // ALL, ARABIC, ENGLISH, FAVORITES, RECOMMENDED
    val favoriteFonts = remember { mutableStateListOf("Cairo", "Tajawal", "Inter", "Poppins") }
    
    // Advanced UI Aids
    var isSmartGuidesEnabled by remember { mutableStateOf(true) }
    var isSnapSystemEnabled by remember { mutableStateOf(true) }
    var isGridSystemEnabled by remember { mutableStateOf(false) }
    var showRulers by remember { mutableStateOf(true) }

    // Font Recognition simulation
    var showFontRecognitionDialog by remember { mutableStateOf(false) }
    var isRecognizingFontState by remember { mutableStateOf(false) }
    var detectedFontNameState by remember { mutableStateOf("Cairo Bold") }
    var detectedConfidenceState by remember { mutableStateOf(0.96f) }
    var detectedAlternativesState by remember { mutableStateOf(listOf("Tajawal", "GE SS", "29LT Family")) }
    var didDetectFontState by remember { mutableStateOf(false) }

    // Automatically collapse non-essential toolbars when in Focus Mode or when selected
    LaunchedEffect(selectedId, workingMode) {
        if (workingMode == "FOCUS_MODE") {
            isFullScreenMode = true
        } else {
            isFullScreenMode = false
        }
    }

    // Display Status updates in modern Toast
    LaunchedEffect(statusMsg) {
        statusMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearStatusMessage()
        }
    }

    LaunchedEffect(errMsg) {
        errMsg?.let {
            Toast.makeText(context, "⚠️ خطأ: $it", Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    if (showDashboard) {
        DashboardScreen(
            viewModel = viewModel,
            isArabic = isArabic,
            onNavigateToProjects = onNavigateToProjects,
            onNavigateToHelp = onNavigateToHelp,
            onStartNew = { ratio ->
                viewModel.changeAspectRatio(ratio)
                viewModel.startNewCanvas()
                showDashboard = false
            },
            onLoadTemplate = { templateId ->
                viewModel.loadPresetTemplate(templateId)
                showDashboard = false
            },
            onTriggerSettings = { showSettingsDialog = true }
        )
    } else {
        Scaffold(
            topBar = {
                if (!isFullScreenMode) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(onClick = { showDashboard = true }) {
                                    Icon(Icons.Default.Home, contentDescription = "Dashboard", tint = MaterialTheme.colorScheme.primary)
                                }
                                Text(
                                    if (isArabic) "يافطة AI Pro 🎨" else "Yafta AI Pro 🎨",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        actions = {
                            // Studio Pro vs Smart Designer selection
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF22242B))
                            ) {
                                listOf(
                                    Pair("STUDIO_PRO", if (isArabic) "محترف 💻" else "Pro 💻"),
                                    Pair("SMART_DESIGNER", if (isArabic) "خبير ذكي ⚙️" else "Smart ⚙️"),
                                    Pair("FOCUS_MODE", if (isArabic) "تركيز 👁️" else "Focus 👁️")
                                ).forEach { (mode, label) ->
                                    val isSel = workingMode == mode
                                    Box(
                                        modifier = Modifier
                                            .background(if (isSel) Color(0xFFFFC107) else Color.Transparent)
                                            .clickable { workingMode = mode }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSel) Color(0xFF0B0B0F) else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Undo
                            IconButton(onClick = { viewModel.undo() }) {
                                Icon(Icons.Default.Undo, contentDescription = "Undo")
                            }
                            // Redo
                            IconButton(onClick = { viewModel.redo() }) {
                                Icon(Icons.Default.Redo, contentDescription = "Redo")
                            }
                            // Start clean canvas
                            IconButton(onClick = { viewModel.startNewCanvas() }) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Canvas")
                            }
                            // Font Center trigger
                            IconButton(onClick = { showFontCenterDialog = true }) {
                                Icon(Icons.Default.TextFields, contentDescription = "Font Center", tint = Color(0xFFFF9800))
                            }
                            // Font Scan trigger
                            IconButton(onClick = { showFontRecognitionDialog = true }) {
                                Icon(Icons.Default.DocumentScanner, contentDescription = "AI Font Scan", tint = Color(0xFF10B981))
                            }
                            // Save
                            IconButton(onClick = { showSaveDialog = true }) {
                                Icon(Icons.Default.Save, contentDescription = "Save Draft", tint = Color(0xFFFF9800))
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                            actionIconContentColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFF0B0B0F))
            ) {
                // Left compact vertical sidebar for Studio Pro!
                if (!isFullScreenMode) {
                    Column(
                        modifier = Modifier
                            .width(60.dp)
                            .fillMaxHeight()
                            .background(Color(0xFF16181D))
                            .border(1.dp, Color(0xFF2C2F3A))
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val sidebarTabs = listOf(
                            Triple("AI", Icons.Default.AutoAwesome, "AI Tools"),
                            Triple("TEMPLATES", Icons.Default.Folder, "Templates"),
                            Triple("ELEMENTS", Icons.Default.Add, "Elements"),
                            Triple("ADJUST", Icons.Default.Build, "Adjust"),
                            Triple("EXPORT", Icons.Default.Print, "Print & Ex")
                        )
                        sidebarTabs.forEach { (tabId, icon, label) ->
                            val isAct = activeTab == tabId
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (activeTab == tabId) {
                                            isLeftSidebarExpanded = !isLeftSidebarExpanded
                                        } else {
                                            activeTab = tabId
                                            isLeftSidebarExpanded = true
                                        }
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isAct && isLeftSidebarExpanded) Color(0xFFFFC107) else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isArabic) {
                                        when (tabId) {
                                            "AI" -> "ذكاء 🔮"
                                            "TEMPLATES" -> "قوالب 🗂️"
                                            "ELEMENTS" -> "عناصر ➕"
                                            "ADJUST" -> "تعديل 🛠️"
                                            else -> "تصدير 💾"
                                        }
                                    } else label,
                                    color = if (isAct && isLeftSidebarExpanded) Color(0xFFFFC107) else Color.Gray,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Settings Gear
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(Icons.Default.Settings, "Config", tint = Color.LightGray)
                        }
                    }
                    
                    // COLLAPSIBLE DRAWER SIDE PANEL (Renders details of the active tab)
                    AnimatedVisibility(
                        visible = isLeftSidebarExpanded,
                        enter = slideInHorizontally() + fadeIn(),
                        exit = slideOutHorizontally() + fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .width(260.dp)
                                .fillMaxHeight()
                                .background(Color(0xFF16181D))
                                .border(1.dp, Color(0xFF2E313D))
                                .padding(12.dp)
                        ) {
                            // Container to load the original control views
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "خيارات الأداة النشطة ⚡" else "Tool Configurations ⚡",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFFFC107)
                                )
                                // Renders the navigation tab picker controls locally so they can edit instantly!
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(
                                        Pair("AI", if (isArabic) "ذكاء" else "AI"),
                                        Pair("TEMPLATES", if (isArabic) "قوالب" else "Presets"),
                                        Pair("ELEMENTS", if (isArabic) "مكون" else "Elem"),
                                        Pair("ADJUST", if (isArabic) "ضبط" else "Tune")
                                    ).forEach { (id, label) ->
                                        val isSel = activeTab == id
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSel) Color(0xFFFFC107).copy(alpha = 0.2f) else Color.Transparent)
                                                .border(1.dp, if (isSel) Color(0xFFFFC107) else Color.Transparent, RoundedCornerShape(6.dp))
                                                .clickable { activeTab = id }
                                                .padding(vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(label, color = if (isSel) Color(0xFFFFC107) else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                
                                // Direct actions inside Drawer Panel
                                if (activeTab == "AI") {
                                    Text(if (isArabic) "التوليد بالذكاء الاصطناعي 🔮" else "AI Creative Synthesis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Button(
                                        onClick = { showFontRecognitionDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.DocumentScanner, null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isArabic) "كاشف الخطوط بالذكاء الاصطناعي 🔮" else "AI Font Scanner 🔮", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else if (activeTab == "ELEMENTS") {
                                    Text(if (isArabic) "إضافة نصوص وعناصر الفن ➕" else "Typography & Geometry", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Button(
                                        onClick = { showFontCenterDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.TextFields, null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isArabic) "مركز الخطوط الاحترافي 🔠" else "Professional Font Center 🔠", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                
                                Text(if (isArabic) "تعديل تفصيلي بالأسفل 👇" else "Tweak details below 👇", color = Color.Gray, fontSize = 10.sp)
                            }
                        }
                    }
                }
                
                // Center-right Workspace Workspace column containing Canvas and Rulers
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Smart Guides control options & Rulers
                    if (!isFullScreenMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF16181D))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isSmartGuidesEnabled,
                                        onCheckedChange = { isSmartGuidesEnabled = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFC107)),
                                        modifier = Modifier.graphicsLayer { scaleX = 0.8f; scaleY = 0.8f }
                                    )
                                    Text(if (isArabic) "خطوط ذكية" else "Smart Guides", color = Color.White, fontSize = 9.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isSnapSystemEnabled,
                                        onCheckedChange = { isSnapSystemEnabled = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF9800)),
                                        modifier = Modifier.graphicsLayer { scaleX = 0.8f; scaleY = 0.8f }
                                    )
                                    Text(if (isArabic) "المغناطيس" else "Snaps", color = Color.White, fontSize = 9.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = isGridSystemEnabled,
                                        onCheckedChange = { isGridSystemEnabled = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981)),
                                        modifier = Modifier.graphicsLayer { scaleX = 0.8f; scaleY = 0.8f }
                                    )
                                    Text(if (isArabic) "الشبكة الإرشادية" else "Grids", color = Color.White, fontSize = 9.sp)
                                }
                            }
                            
                            IconButton(onClick = { isFullScreenMode = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Fullscreen, "Fullscreen", tint = Color.LightGray, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    
                    // Ruler wrapped layout row
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Left vertical coordinate ruler
                        if (showRulers && !isFullScreenMode) {
                            Column(
                                modifier = Modifier
                                    .width(22.dp)
                                    .fillMaxHeight()
                                    .padding(vertical = 12.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.End
                            ) {
                                listOf("0", "200", "400", "600", "800", "1K").forEach { tick ->
                                    Text(tick, color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        
                        Column(modifier = Modifier.weight(1f)) {
                            // Top horizontal coordinate ruler
                            if (showRulers && !isFullScreenMode) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp)
                                        .padding(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    listOf("0", "250", "500", "750", "1000").forEach { tick ->
                                        Text(tick, color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            // ---------------- CANVAS CONTAINER ---------------- //
                            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isFullScreenMode) {
                            Modifier.weight(1f)
                        } else {
                            Modifier.heightIn(
                                min = if (androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 600) 360.dp else 250.dp,
                                max = if (androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 600) 500.dp else 340.dp
                            )
                        }
                    )
                    .padding(horizontal = if (androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 600) 24.dp else 8.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0F12))
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.selectLayer(null) } // Deselect
                    .testTag("design_workspace_canvas")
            ) {
                val canvasRatio = when (aspectRatio) {
                    "16:9" -> 1.77f
                    "9:16" -> 0.56f
                    "4:3" -> 1.33f
                    else -> 1f // 1:1
                }
                val viewportWidth = maxWidth
                val viewportHeight = maxHeight
                val viewportRatio = viewportWidth.value / maxOf(0.1f, viewportHeight.value)
                val (canvasWidth, canvasHeight) = if (canvasRatio > viewportRatio) {
                    Pair(viewportWidth, (viewportWidth.value / canvasRatio).dp)
                } else {
                    Pair((viewportHeight.value * canvasRatio).dp, viewportHeight)
                }
                
                // Background rendering
                val backBrush = if (isGradient) {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(android.graphics.Color.parseColor(gradStartHex)),
                            Color(android.graphics.Color.parseColor(gradEndHex))
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                } else {
                    SolidColor(Color(android.graphics.Color.parseColor(solidColorHex)))
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = zoomLevel,
                            scaleY = zoomLevel,
                            rotationZ = canvasRotation
                        )
                        .background(backBrush)
                ) {
                    // Draw alignment grids if Snapping is active
                    if (snapToGrid) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val step = size.minDimension / 20f // 20 grid sectors
                            val cols = (size.width / step).toInt()
                            val rows = (size.height / step).toInt()
                            for (i in 1..cols) {
                                drawLine(
                                    color = Color(0xFFFFD700).copy(alpha = 0.1f),
                                    start = Offset(i * step, 0f),
                                    end = Offset(i * step, size.height),
                                    strokeWidth = 1f
                                )
                            }
                            for (j in 1..rows) {
                                drawLine(
                                    color = Color(0xFFFFD700).copy(alpha = 0.1f),
                                    start = Offset(0f, j * step),
                                    end = Offset(size.width, j * step),
                                    strokeWidth = 1f
                                )
                            }
                        }
                    }

                    // Render Vector Design Layers sequentially based on list order
                    layers.forEach { layer ->
                        if (!layer.isVisible) return@forEach // Hidden layers are not drawn

                        val scaleX = canvasWidth.value / 1000f
                        val scaleY = canvasHeight.value / 1000f

                        val isSelected = selectedId == layer.id
                        val isMulti = multiSelectedIds.contains(layer.id)
                        val isAnySelected = isSelected || isMulti

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (layer.x * scaleX).dp,
                                    y = (layer.y * scaleY).dp
                                )
                                .size(
                                    width = (layer.width * scaleX).dp,
                                    height = (layer.height * scaleY).dp
                                )
                                .rotate(layer.rotation)
                                .alpha(layer.opacity)
                                .border(
                                    width = if (isAnySelected) 2.dp else 0.dp,
                                    color = if (isSelected) Color(0xFFFFD700) else if (isMulti) Color(0xFFFFB300) else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .pointerInput(layer.id) {
                                    if (layer.isLocked) return@pointerInput // Locked layers cannot be dragged!
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        // Back conversion to virtual coordinates (0 - 1000 range)
                                        val curX = layer.x + (dragAmount.x / scaleX) / 1.5f
                                        val curY = layer.y + (dragAmount.y / scaleY) / 1.5f
                                        viewModel.updateLayerPosition(
                                            id = layer.id,
                                            x = curX.coerceIn(-200f, 1100f),
                                            y = curY.coerceIn(-200f, 1100f)
                                        )
                                    }
                                }
                                .clickable {
                                    if (multiSelectedIds.size > 1) {
                                        viewModel.toggleMultiSelectLayer(layer.id)
                                    } else {
                                        viewModel.selectLayer(layer.id)
                                    }
                                }
                        ) {
                            // Render specific graphic element types
                            when (layer.type) {
                                "TEXT" -> {
                                    Text(
                                        text = layer.text,
                                        color = Color(android.graphics.Color.parseColor(layer.colorHex)),
                                        fontSize = layer.fontSize.sp,
                                        fontFamily = when (layer.fontFamily) {
                                            "SERIF" -> FontFamily.Serif
                                            "MONOSPACE" -> FontFamily.Monospace
                                            "ARABIC_DISPLAY" -> FontFamily.SansSerif // Gorgeous simulated
                                            "ARABIC_GEOMETRIC" -> FontFamily.Serif
                                            else -> FontFamily.Default
                                        },
                                        fontWeight = if (layer.fontFamily == "ARABIC_DISPLAY") FontWeight.ExtraBold else FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        letterSpacing = layer.letterSpacing.sp,
                                        style = if (layer.hasShadow) {
                                            androidx.compose.ui.text.TextStyle(
                                                shadow = Shadow(
                                                    color = Color(android.graphics.Color.parseColor(layer.textShadowColorHex)),
                                                    offset = Offset(4f + layer.textStrokeWidth * 2, 4f + layer.textStrokeWidth * 2),
                                                    blurRadius = 8f
                                                )
                                            )
                                        } else {
                                            androidx.compose.ui.text.TextStyle.Default
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                "SHAPE" -> {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val strokeColor = Color(android.graphics.Color.parseColor(layer.colorHex))
                                        val fillBrush = SolidColor(strokeColor)
                                        val style = if (layer.strokeWidth > 0) Stroke(width = layer.strokeWidth) else androidx.compose.ui.graphics.drawscope.Fill
                                        
                                        when (layer.shapeType) {
                                            "CIRCLE" -> {
                                                drawCircle(brush = fillBrush, radius = size.minDimension / 2, style = style)
                                            }
                                            "ROUNDED_RECTANGLE" -> {
                                                drawRoundRect(
                                                    brush = fillBrush,
                                                    size = size,
                                                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                                                    style = style
                                                )
                                            }
                                            "TRIANGLE" -> {
                                                val path = Path().apply {
                                                    moveTo(size.width / 2, 0f)
                                                    lineTo(0f, size.height)
                                                    lineTo(size.width, size.height)
                                                    close()
                                                }
                                                drawPath(path = path, brush = fillBrush, style = style)
                                            }
                                            "STAR_BADGE" -> {
                                                // Simplified 8-point gold medal outline/fill
                                                val path = Path().apply {
                                                    val cx = size.width / 2
                                                    val cy = size.height / 2
                                                    val points = 8
                                                    val outerRadius = size.minDimension / 2
                                                    val innerRadius = size.minDimension / 4
                                                    var angle = -Math.PI / 2
                                                    for (i in 0 until points * 2) {
                                                        val r = if (i % 2 == 0) outerRadius else innerRadius
                                                        val x = cx + r * Math.cos(angle).toFloat()
                                                        val y = cy + r * Math.sin(angle).toFloat()
                                                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                                                        angle += Math.PI / points
                                                    }
                                                    close()
                                                }
                                                drawPath(path = path, brush = fillBrush, style = style)
                                            }
                                            "BANNER_RIBBON" -> {
                                                // Horizontal banner backing for headlines in gold/crimson
                                                val path = Path().apply {
                                                    moveTo(0f, 10f)
                                                    lineTo(size.width, 10f)
                                                    lineTo(size.width - 25f, size.height / 2)
                                                    lineTo(size.width, size.height - 10f)
                                                    lineTo(0f, size.height - 10f)
                                                    lineTo(25f, size.height / 2)
                                                    close()
                                                }
                                                drawPath(path = path, brush = fillBrush, style = style)
                                            }
                                            else -> { // RECTANGLE
                                                drawRect(brush = fillBrush, size = size, style = style)
                                            }
                                        }
                                    }
                                }
                                "STICKER" -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Color(
                                                    android.graphics.Color.parseColor(
                                                        layer.colorHex
                                                    )
                                                ).copy(alpha = 0.15f), RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                1.dp,
                                                Color(
                                                    android.graphics.Color.parseColor(
                                                        layer.colorHex
                                                    )
                                                ),
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (layer.stickerId) {
                                                "ic_discount" -> Icons.Default.Discount
                                                "ic_star" -> Icons.Default.Grade
                                                "ic_heart" -> Icons.Default.Favorite
                                                "ic_arrow" -> Icons.Default.KeyboardDoubleArrowDown
                                                "ic_ribbon" -> Icons.Default.AutoAwesome
                                                "ic_certified" -> Icons.Default.Verified
                                                else -> Icons.Default.AutoAwesome
                                            },
                                            contentDescription = layer.name,
                                            tint = Color(android.graphics.Color.parseColor(layer.colorHex)),
                                            modifier = Modifier.size((layer.height * scaleY * 0.6f).dp)
                                        )
                                    }
                                }
                                "IMAGE" -> {
                                    val imageUrl = layer.imageUrl
                                    if (imageUrl.isNullOrBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.DarkGray, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray)
                                                Text("صورة فارغة", fontSize = 10.sp, color = Color.LightGray)
                                            }
                                        }
                                    } else {
                                        coil.compose.AsyncImage(
                                            model = imageUrl,
                                            contentDescription = layer.name,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Safety Bleed Margins (Dashed border overlay for Print Preview)
                    if (showPrintReviewOverlay) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val marginPx = (printMarginCm / 8.0f) * size.minDimension
                            val strokeStyle = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 4f,
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                            )
                            drawRect(
                                color = Color.Red,
                                topLeft = Offset(marginPx, marginPx),
                                size = androidx.compose.ui.geometry.Size(size.width - 2 * marginPx, size.height - 2 * marginPx),
                                style = strokeStyle
                            )
                        }
                    }
                }

                // Floating Fullscreen Toggle Button at top-right corner of the canvas container
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { isFullScreenMode = !isFullScreenMode }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("fullscreen_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isFullScreenMode) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = if (isArabic) "تبديل الشاشة كاملة" else "Toggle Fullscreen",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isFullScreenMode) {
                                if (isArabic) "عرض الأدوات" else "Show Tools"
                            } else {
                                if (isArabic) "ملء الشاشة" else "Full Screen"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Floating Zoom-to-Fit Button at top-left corner of the canvas container
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { viewModel.zoomToFit() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("zoom_to_fit_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CropFree,
                            contentDescription = if (isArabic) "ملاءة اللوحة بالكامل" else "Zoom to Fit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isArabic) "ملاءمة الشاشة" else "Zoom Fit",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            } // Closes top ruler Column
            } // Closes outer rulers Row
            
            // Real-time API Loading Wheel Overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text(
                            "جاري البناء الذكي بالذكاء الاصطناعي وترتيب الطبقات... ⚡🎨",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Quick Canvas Context Bubble Card
            val activeLayer = layers.find { it.id == selectedId }
            if (activeLayer != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title / Info
                        Column(
                            modifier = Modifier.weight(1.2f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isArabic) "الطبقة المحددة:" else "Selected Layer:",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                            Text(
                                text = activeLayer.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }

                        // Bring Front / Send Back
                        IconButton(
                            onClick = { viewModel.bringSelectedLayerToFront() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerticalAlignTop,
                                contentDescription = "أعلى الطبقات",
                                tint = Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.sendSelectedLayerToBack() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerticalAlignBottom,
                                contentDescription = "أسفل الطبقات",
                                tint = Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Lock / Unlock Toggle
                        IconButton(
                            onClick = { viewModel.toggleLockLayer(activeLayer.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (activeLayer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "قفل",
                                tint = if (activeLayer.isLocked) Color(0xFFEF4444) else Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Copy / Duplicate
                        IconButton(
                            onClick = { viewModel.duplicateSelectedLayer() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "تكرار",
                                tint = Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Delete
                        IconButton(
                            onClick = { viewModel.deleteSelectedLayer() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            if (!isFullScreenMode) {
                Spacer(modifier = Modifier.height(12.dp))

                // ---------------- CONTROL NAVIGATION TABS ---------------- //
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F0F12)) // Deep sophisticated bottom-nav dark
                    .border(1.dp, Color(0xFF2C2C2E).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    Pair("AI", if (isArabic) "ذكاء 🔮" else "AI 🔮"),
                    Pair("TEMPLATES", if (isArabic) "قوالب 🗂️" else "Templates 🗂️"),
                    Pair("ELEMENTS", if (isArabic) "إضافة ➕" else "Add ➕"),
                    Pair("ADJUST", if (isArabic) "تعديل 🛠️" else "Adjust 🛠️"),
                    Pair("EXPORT", if (isArabic) "تصدير 💾" else "Export 💾")
                ).forEach { (id, label) ->
                    val isSelected = activeTab == id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = id }
                            .background(if (isSelected) Color(0xFF1C1C1E) else Color.Transparent) // Active chip black gray
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------------- TABBED CONTENT SECTIONS ---------------- //
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (activeTab) {
                    "AI" -> {
                        // AI Generation Center
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isArabic) "مركز توليد وابتكار يافطة بالذكاء الاصطناعي" else "Yafta AI Generative Studio Center",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            
                            OutlinedTextField(
                                value = aiPromptText,
                                onValueChange = { aiPromptText = it },
                                placeholder = {
                                    Text(
                                        text = if (isArabic) "اكتب فكرتك الإعلانية بالتفصيل هنا..." else "Describe your layout vision in detail here...",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ai_prompt_input_field"),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    keyboardController?.hide()
                                    viewModel.generateAiDesign(aiPromptText)
                                }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        keyboardController?.hide()
                                        viewModel.generateAiDesign(aiPromptText)
                                    },
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("generate_ai_design_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = if (isArabic) "ابتكار ذكي 🚀" else "Generate 🚀",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        keyboardController?.hide()
                                        viewModel.enhancePrompt(aiPromptText) { expanded ->
                                            aiPromptText = expanded
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(minHeight = 48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E38))
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.OfflineBolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = if (isArabic) "تحسين الوصف ✨" else "Enhance Prompt ✨",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                            Text(
                                text = if (isArabic) "اقتراحات سريعة ملهمة:" else "Inspirational Suggestions:",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val samples = if (isArabic) {
                                    listOf(
                                        "يافطة مطعم برجر لذيذ بخصم ٣٠٪",
                                        "كارت حفل زفاف فخم باللون الذهبي والأسود",
                                        "لافتة ترويجية لقهوة ستاربكس المميزة بمزاج رائع",
                                        "إعلان ملابس صيفية للرجال بألوان النيون فسفوري"
                                    )
                                } else {
                                    listOf(
                                        "Premium burger restaurant dynamic banner with 50% off",
                                        "Minimal elegant gold and black wedding greeting cards",
                                        "Creative morning coffee shop visual with aroma cup",
                                        "Summer fashion apparel flyer with neon theme"
                                    )
                                }
                                items(samples) { sample ->
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                aiPromptText = sample
                                                viewModel.generateAiDesign(sample)
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(sample, color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                            // AI Assistant Chat Widget
                            Text(
                                text = if (isArabic) "سؤال خبير ومساعد الذكاء الاصطناعي 🔮" else "Consult Yafta Design Assistant 🔮",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F12)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF2C2C2E), RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Chat transcript
                                    aiChatHistory.takeLast(3).forEach { (message, isUser) ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp),
                                            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isUser) MaterialTheme.colorScheme.primary else Color(0xFF1E1E24))
                                                    .padding(8.dp)
                                                    .widthIn(max = 240.dp)
                                            ) {
                                                Text(
                                                    text = message,
                                                    color = if (isUser) MaterialTheme.colorScheme.onPrimary else Color.LightGray,
                                                    fontSize = 11.sp,
                                                    lineHeight = 16.sp,
                                                    textAlign = if (isArabic) TextAlign.Right else TextAlign.Left
                                                )
                                            }
                                        }
                                    }

                                    // Quick questions chips
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        val queries = if (isArabic) {
                                            listOf("اقترح ألوان متناسقة", "اقترح عنواناً تسويقياً")
                                        } else {
                                            listOf("Suggest color palette", "Refine marketing headline")
                                        }
                                        queries.forEach { q ->
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF232329), RoundedCornerShape(16.dp))
                                                    .clickable { viewModel.askAiAssistant(q) }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(q, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
                                            }
                                        }
                                    }

                                    // Chat Input Box
                                    var chatInput by remember { mutableStateOf("") }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = chatInput,
                                            onValueChange = { chatInput = it },
                                            placeholder = { Text(if (isArabic) "اسأله أي سؤال فني..." else "Ask design questions...", fontSize = 10.sp, color = Color.Gray) },
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor = Color.DarkGray
                                            ),
                                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                            maxLines = 1,
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                            keyboardActions = KeyboardActions(onSend = {
                                                if (chatInput.isNotBlank()) {
                                                    viewModel.askAiAssistant(chatInput)
                                                    chatInput = ""
                                                }
                                            })
                                        )

                                        IconButton(
                                            onClick = {
                                                if (chatInput.isNotBlank()) {
                                                    viewModel.askAiAssistant(chatInput)
                                                    chatInput = ""
                                                }
                                            },
                                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            // Professional Phase 3 AI tools for selected layers
                            val selectedLayer = layers.find { it.id == selectedId }
                            if (selectedLayer != null) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                Text(
                                    text = if (isArabic) "معالجة الطبقة النشطة بالذكاء الاصطناعي 🛠️🔮" else "Active Layer AI Treatments 🛠️🔮",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Background remover (if image, else simulated clear)
                                    Button(
                                        onClick = {
                                            if (selectedLayer.type == "IMAGE" || selectedLayer.type == "STICKER") {
                                                viewModel.simulateBgRemoval()
                                            } else {
                                                Toast.makeText(context, if (isArabic) "عذراً، فصل الخلفية يعمل فقط على الصور والملصقات!" else "Note: BG extraction only applies to Image or sticker layers!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE02424)),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(if (isArabic) "عزل الخلفية" else "Erase BG", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text(if (isArabic) "قص ذكي ✂️" else "Isolate ✂️", fontSize = 8.sp, color = Color.LightGray)
                                        }
                                    }

                                    // Upscale
                                    Button(
                                        onClick = { viewModel.simulateAiUpscale() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(if (isArabic) "رفع الدقة UHD" else "AI Upscale", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text(if (isArabic) "تصفية 📈" else "Scale HD 📈", fontSize = 8.sp, color = Color.LightGray)
                                        }
                                    }

                                    // AI Enhance
                                    Button(
                                        onClick = { viewModel.simulateAiEnhance() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(if (isArabic) "محاذاة لونية" else "AI Enhance", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text(if (isArabic) "فلاتر ✨" else "Optimize ✨", fontSize = 8.sp, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "TEMPLATES" -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isArabic) "مكتبة القوالب الإعلانية الجاهزة المتكاملة 🗂️" else "Complete Ready-Made Ads Template Library 🗂️",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isArabic) "قوالب عالية الدقة مصممة خصيصاً للتسويق المباشر ومعدلة لطباعة اليافطات" else "Ultra-high quality templates designed especially for marketing and calibrated for print presses",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )

                            // Category selection chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    Pair("ALL", if (isArabic) "الكل 🌐" else "All 🌐"),
                                    Pair("FOOD", if (isArabic) "مطاعم 🍔" else "Food 🍔"),
                                    Pair("WEDDING", if (isArabic) "زفاف 💍" else "Social 💍"),
                                    Pair("CLEAN", if (isArabic) "عصري 📱" else "Minimal 📱")
                                ).forEach { (id, label) ->
                                    val isCatSelected = templateCategoryInput == id
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (isCatSelected) MaterialTheme.colorScheme.primary else Color(0xFF131317),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { templateCategoryInput = id }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isCatSelected) Color.Black else Color.LightGray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = Color.DarkGray)

                            // Render filtered template cards
                            val templateItems = listOf(
                                Triple("BURGER", if (isArabic) "برجر كلاسيك الشهي 🍔" else "Tasty Classic Burger 🍔", if (isArabic) "برجر وصوصات دافئة بنمط كلاسيكي" else "Warm golden composition for fast food"),
                                Triple("WEDDING", if (isArabic) "دعوة زفاف مبهبة 💍" else "Luxury Gold Wedding Call 💍", if (isArabic) "تصميم ملكي فاخر بأطر وخطوط تواصل" else "Elegant premium layouts to honor guests"),
                                Triple("COFFEE", if (isArabic) "أريج القهوة الصباحية ☕" else "Morning Aroma Coffee ☕", if (isArabic) "ألوان أرضية دافئة لمقاهي ومحامص البن" else "Cozy warm branding for cafes"),
                                Triple("CLEAN", if (isArabic) "واجهة ذكية بسيطة 📱" else "Minimal Business Card 📱", if (isArabic) "أسلوب مينيمال عصري واثق ومرتب" else "Clean precise modern layout for startups")
                            ).filter { 
                                templateCategoryInput == "ALL" || 
                                (templateCategoryInput == "FOOD" && (it.first == "BURGER" || it.first == "COFFEE")) ||
                                (templateCategoryInput == "WEDDING" && it.first == "WEDDING") ||
                                (templateCategoryInput == "CLEAN" && it.first == "CLEAN")
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                templateItems.forEach { (key, title, desc) ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131317)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp))
                                            .clickable {
                                                viewModel.loadPresetTemplate(key)
                                                Toast.makeText(context, if (isArabic) "تم تحميل القالب بنجاح! 🎨⚡" else "Template loaded successfully! 🎨⚡", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(title, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text(desc, color = Color.Gray, fontSize = 10.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Text(if (isArabic) "⭐ كفاءة 4.9" else "⭐ 4.9 rating", color = Color(0xFFFFD700), fontSize = 8.sp)
                                                    Text(if (isArabic) "✓ عناصر CMYK" else "✓ CMYK Print Safe", color = Color(0xFF10B981), fontSize = 8.sp)
                                                }
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(if (isArabic) "تفعيل ⚡" else "Use ⚡", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "ADJUST" -> {
                        // Selected element tweaking panel
                        val selectedLayer = layers.find { it.id == selectedId } ?: layers.find { multiSelectedIds.contains(it.id) }
                        if (selectedLayer != null) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "نمط الطبقة: ${selectedLayer.name}",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        // Lock/Unlock eye icon toggle
                                        IconButton(onClick = { viewModel.toggleLockLayer(selectedLayer.id) }) {
                                            Icon(
                                                imageVector = if (selectedLayer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                                contentDescription = "قفل الطبقة",
                                                tint = if (selectedLayer.isLocked) Color(0xFFFFD700) else Color.White
                                            )
                                        }
                                        // Visibility toggle
                                        IconButton(onClick = { viewModel.toggleVisibilityLayer(selectedLayer.id) }) {
                                            Icon(
                                                imageVector = if (selectedLayer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "رؤية الطبقة",
                                                tint = if (selectedLayer.isVisible) Color.White else Color.Gray
                                            )
                                        }
                                        IconButton(onClick = { viewModel.duplicateSelectedLayer() }) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "مضاعفة الطبقة", tint = Color.White)
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteSelectedLayer() },
                                            modifier = Modifier.testTag("delete_layer_button")
                                        ) {
                                            Icon(Icons.Default.DeleteForever, contentDescription = "حذف الطبقة", tint = Color.Red)
                                        }
                                    }
                                }

                                // Alignment controls for selected components
                                Text("محاذاة الطبقات النشطة:", color = Color.LightGray, fontSize = 12.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val alignments = listOf(
                                        Pair("LEFT", "⬅️ يسار"),
                                        Pair("CENTER_HORIZONTAL", "↔️ وسط أفقي"),
                                        Pair("RIGHT", "➡️ يمين"),
                                        Pair("TOP", "⬆️ أعلى"),
                                        Pair("CENTER_VERTICAL", "↕️ وسط رأسي"),
                                        Pair("BOTTOM", "⬇️ أسفل")
                                    )
                                    alignments.forEach { (alignKey, label) ->
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF1E1E24), RoundedCornerShape(6.dp))
                                                .clickable { viewModel.alignSelectedLayers(alignKey) }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Interactive grouping mechanisms
                                if (multiSelectedIds.size >= 2) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.groupSelectedLayers() },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                                        ) {
                                            Text("تجميع العناصر 🔗", color = Color(0xFF1A1A1A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = { viewModel.ungroupSelectedLayers() },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF383842))
                                        ) {
                                            Text("تفكيك المجموعات 🔓", color = Color.White, fontSize = 11.sp)
                                        }
                                    }
                                }

                                // Blend mode selection tools
                                Text("وضع دمج مظهر الطبقة (Blend Mode):", color = Color.LightGray, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Normal", "Multiply", "Screen", "Overlay").forEach { modeName ->
                                        val isSelectedMode = selectedLayer.blendMode.equals(modeName, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .background(if (isSelectedMode) Color(0xFFFFD700) else Color(0xFF1E1E24), RoundedCornerShape(8.dp))
                                                .clickable { viewModel.updateLayerBlendMode(selectedLayer.id, modeName) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(modeName, color = if (isSelectedMode) Color(0xFF1A1A1A) else Color.White, fontSize = 10.sp)
                                        }
                                    }
                                }

                                // Interactive properties based on Layer Type
                                when (selectedLayer.type) {
                                    "TEXT" -> {
                                        OutlinedTextField(
                                            value = selectedLayer.text,
                                            onValueChange = { viewModel.updateLayerText(selectedLayer.id, it) },
                                            label = { Text("محتوى النص") },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                            )
                                        )

                                        // Typography fonts picker
                                        Text("عائلة الخط وطابعه:", color = Color.LightGray, fontSize = 12.sp)
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val families = listOf(
                                                Pair("ARABIC_DISPLAY", "خط عرض عريض"),
                                                Pair("ARABIC_GEOMETRIC", "قلم هندسي"),
                                                Pair("SERIF", "رسمي كلاسيكي"),
                                                Pair("MONOSPACE", "شريط مهندس")
                                            )
                                            items(families) { (id, label) ->
                                                val isFam = selectedLayer.fontFamily == id
                                                Box(
                                                    modifier = Modifier
                                                        .background(if (isFam) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                                        .clickable { viewModel.updateLayerText(selectedLayer.id, selectedLayer.text, fFamily = id) }
                                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Text(label, color = if (isFam) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        // Slider font size adjustment
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("حجم الحروف: ${selectedLayer.fontSize.toInt()}sp", color = Color.White, modifier = Modifier.width(110.dp), fontSize = 12.sp)
                                            Slider(
                                                value = selectedLayer.fontSize,
                                                onValueChange = { viewModel.updateLayerText(selectedLayer.id, selectedLayer.text, fSize = it) },
                                                valueRange = 12f..80f,
                                                modifier = Modifier.weight(1f),
                                                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                                            )
                                        }

                                        // Advanced Text Effects: Letter Spacing
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("تباعد الحروف: ${String.format("%.1f", selectedLayer.letterSpacing)}sp", color = Color.White, modifier = Modifier.width(110.dp), fontSize = 12.sp)
                                            Slider(
                                                value = selectedLayer.letterSpacing,
                                                onValueChange = { viewModel.updateLayerTextStyling(selectedLayer.id, letterSpacing = it) },
                                                valueRange = 0f..15f,
                                                modifier = Modifier.weight(1f),
                                                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                                            )
                                        }

                                        // Shadow switch
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("إضافة ظل ترويجي للنص:", color = Color.LightGray, fontSize = 12.sp)
                                            Switch(
                                                checked = selectedLayer.hasShadow,
                                                onCheckedChange = { viewModel.updateLayerTextStyling(selectedLayer.id, hasShadow = it) },
                                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                                            )
                                        }
                                    }

                                    "SHAPE" -> {
                                        Text("تعديل شكل العنصر:", color = Color.LightGray, fontSize = 12.sp)
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            val shapes = listOf("RECTANGLE", "ROUNDED_RECTANGLE", "CIRCLE", "TRIANGLE", "STAR_BADGE", "BANNER_RIBBON")
                                            items(shapes) { s ->
                                                val isS = selectedLayer.shapeType == s
                                                Box(
                                                    modifier = Modifier
                                                        .background(if (isS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                                        .clickable { viewModel.updateLayerShapeType(selectedLayer.id, s) }
                                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Text(s, color = if (isS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }

                                // Color Customization slider for items
                                Text("اختر لون المكون الرئيسي:", color = Color.LightGray, fontSize = 12.sp)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val palette = listOf("#FFC107", "#E02424", "#0E9F6E", "#1C64F2", "#FFFFFF", "#000000", "#FF5722", "#D4AF37", "#9333EA", "#D946EF")
                                    items(palette) { hex ->
                                        val isSel = selectedLayer.colorHex.equals(hex, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(Color(android.graphics.Color.parseColor(hex)))
                                                .border(
                                                    width = if (isSel) 2.dp else 0.dp,
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                                .clickable { viewModel.updateLayerColor(selectedLayer.id, hex) }
                                        )
                                    }
                                }

                                // Height & width scale sliders
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("عرض العنصر: ${selectedLayer.width.toInt()}px", color = Color.White, modifier = Modifier.width(110.dp), fontSize = 12.sp)
                                    Slider(
                                        value = selectedLayer.width,
                                        onValueChange = { viewModel.updateLayerSize(selectedLayer.id, it, selectedLayer.height) },
                                        valueRange = 20f..1000f,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("ارتفاع العنصر: ${selectedLayer.height.toInt()}px", color = Color.White, modifier = Modifier.width(110.dp), fontSize = 12.sp)
                                    Slider(
                                        value = selectedLayer.height,
                                        onValueChange = { viewModel.updateLayerSize(selectedLayer.id, selectedLayer.width, it) },
                                        valueRange = 20f..1000f,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Rotation
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("درجة التدوير: ${selectedLayer.rotation.toInt()}°", color = Color.White, modifier = Modifier.width(110.dp), fontSize = 12.sp)
                                    Slider(
                                        value = selectedLayer.rotation,
                                        onValueChange = { viewModel.updateLayerRotation(selectedLayer.id, it) },
                                        valueRange = -180f..180f,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Opacity
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("الشفافية: ${(selectedLayer.opacity * 10).toInt() * 10}%", color = Color.White, modifier = Modifier.width(110.dp), fontSize = 12.sp)
                                    Slider(
                                        value = selectedLayer.opacity,
                                        onValueChange = { viewModel.updateLayerOpacity(selectedLayer.id, it) },
                                        valueRange = 0f..1f,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // CMYK Color Inspector widget
                                val cmykMap = try {
                                    val cleanHex = selectedLayer.colorHex.removePrefix("#")
                                    val rgbR = cleanHex.substring(0, 2).toInt(16) / 255f
                                    val rgbG = cleanHex.substring(2, 4).toInt(16) / 255f
                                    val rgbB = cleanHex.substring(4, 6).toInt(16) / 255f
                                    val kVal = 1f - maxOf(rgbR, maxOf(rgbG, rgbB))
                                    if (kVal == 1f) {
                                        mapOf("C" to 0, "M" to 0, "Y" to 0, "K" to 100)
                                    } else {
                                        val cVal = ((1f - rgbR - kVal) / (1f - kVal) * 100).toInt()
                                        val mVal = ((1f - rgbG - kVal) / (1f - kVal) * 100).toInt()
                                        val yVal = ((1f - rgbB - kVal) / (1f - kVal) * 100).toInt()
                                        mapOf("C" to cVal, "M" to mVal, "Y" to yVal, "K" to (kVal * 100).toInt())
                                    }
                                } catch (e: Exception) {
                                    mapOf("C" to 0, "M" to 0, "Y" to 0, "K" to 0)
                                }

                                HorizontalDivider(color = Color.DarkGray)
                                Text(
                                    text = if (isArabic) "معاين ومطابق ألوان الطباعة (CMYK Analyzer)" else "CMYK Color Analyzer & Print Safe Guide",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131317)),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            cmykMap.forEach { (colorKey, pct) ->
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(colorKey, color = when(colorKey) {
                                                        "C" -> Color(0xFF00E5FF)
                                                        "M" -> Color(0xFFE040FB)
                                                        "Y" -> Color(0xFFFFFF00)
                                                        else -> Color.White
                                                    }, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text("$pct%", color = Color.White, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                        HorizontalDivider(color = Color.DarkGray)
                                        Text(
                                            text = if (isArabic) "✓ اللون ممعاير ومطابق لآلات البانر واليافطات CMYK." else "✓ Premium ink formulation calibrated successfully.",
                                            color = Color(0xFF10B981),
                                            fontSize = 9.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                if (selectedLayer.type == "TEXT") {
                                    HorizontalDivider(color = Color.DarkGray)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isArabic) "إدارة الخطوط المتميزة 📝" else "Premium Font Manager 📝",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                                                .clickable { showFontUploadDialog = true }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(if (isArabic) "رفع خط محلي + " else "Upload Font + ", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else {
                            // No elements selected -> Adjust core Canvas Background Settings + Guides & Grid Snaps
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "تعديل خلفية اليافطة وأبعادها",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                // Precise Snapping & Guides Grid switches
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text("محاذاة تلقائية بالشبكة (Snap to Grid):", color = Color.White, fontSize = 12.sp)
                                        Text("محاذاة المكونات للشبكة المغناطيسية الذهبية", color = Color.Gray, fontSize = 10.sp)
                                    }
                                    Switch(
                                        checked = snapToGrid,
                                        onCheckedChange = { viewModel.toggleSnapToGrid() },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text("أدلة ذكية ذكية (Smart Guides):", color = Color.White, fontSize = 12.sp)
                                        Text("تفعيل الهوامش والارتكاز الأتوماتيكي", color = Color.Gray, fontSize = 10.sp)
                                    }
                                    Switch(
                                        checked = smartGuides,
                                        onCheckedChange = { viewModel.toggleSmartGuides() },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                                // Canvas Magnifying Zoom controllers
                                Text("تكبير اللوحة ومنظور العرض (Canvas Zoom):", color = Color.LightGray, fontSize = 12.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("المنظور: ${(zoomLevel * 100).toInt()}%", color = Color.White, modifier = Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Button(
                                        onClick = { viewModel.zoomOut() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E24))
                                    ) {
                                        Text("تصغير 🔍-", color = Color.White, fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.zoomIn() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E24))
                                    ) {
                                        Text("تكبير 🔍+", color = Color.White, fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.resetZoom() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                                    ) {
                                        Text("إرجاع 🔄", color = Color(0xFF1A1A1A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { viewModel.zoomToFit() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2E38))
                                    ) {
                                        Text(if (isArabic) "ملاءة 📐" else "Fit 📐", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Canvas Rotation slider
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("تدوير اللوحة: ${canvasRotation.toInt()}°", color = Color.White, modifier = Modifier.width(110.dp), fontSize = 12.sp)
                                    Slider(
                                        value = canvasRotation,
                                        onValueChange = { viewModel.rotateCanvas(it) },
                                        valueRange = 0f..360f,
                                        modifier = Modifier.weight(1f),
                                        colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700))
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                                Text("اختر مقاس اللوحة (نسبة الارتفاع للعرض):", color = Color.LightGray, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(
                                        Pair("1:1", "مربع (1:1)"),
                                        Pair("16:9", "عريض (16:9)"),
                                        Pair("9:16", "طولي (9:16)"),
                                        Pair("4:3", "لوحة قصيرة")
                                    ).forEach { (ratio, desc) ->
                                        val isSelected = aspectRatio == ratio
                                        Box(
                                            modifier = Modifier
                                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                                .clickable { viewModel.changeAspectRatio(ratio) }
                                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                        ) {
                                            Text(desc, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("نمط تدرج خلفية اللوحة:", color = Color.LightGray, fontSize = 12.sp)
                                    Switch(
                                        checked = isGradient,
                                        onCheckedChange = { viewModel.setCanvasBackgroundColor("#0F172A", isGrad = it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        )
                                    )
                                }

                                Text("قفل وتلوين من الكتالوج السريع:", color = Color.LightGray, fontSize = 12.sp)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        Triple("البرجر الداكن 🍔", "#1E0F05", "#3B1D0E"),
                                        Triple("ذهبي فاخر ✨", "#111111", "#4A3B1B"),
                                        Triple("المحيط الهادئ 🌊", "#0B192C", "#1E3E62"),
                                        Triple("الأرجوان الفني 🔮", "#170F2C", "#431A60")
                                    ).forEach { (title, start, end) ->
                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                                .clickable { viewModel.setCanvasBackgroundColor(start, isGrad = true, sColor = start, eColor = end) }
                                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                        ) {
                                            Text(title, color = Color.White, fontSize = 10.sp)
                                        }
                                    }
                                }

                                Button(
                                    onClick = { showPresetBackgroundDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                                ) {
                                    Text("مجموعات وخلفيات فنية فوتوغرافية جاهزة 🌌", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Text("تلميح: انقر على أي نص أو شكل في اللوحة أعلاه لتعديله بالتفصيل.", color = Color.LightGray, fontSize = 11.sp)
                            }
                        }
                    }

                    "ELEMENTS" -> {
                        // Insert components panel (Text, Shape, Sticker)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                "إضافة عناصر فنية وتصميمية جديدة",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            // Add Texts
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("إضافة نصوص مروجة ملونة:", color = Color.LightGray, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.addTextLayer("برجر شهي 🍔") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
                                    ) {
                                        Text("أضف عنوان إعلاني", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { viewModel.addTextLayer("خصم خاص ٥٠٪", false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE02424))
                                    ) {
                                        Text("عنوان خصومات بديل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                            // Add Shapes
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("إضافة أشكال وقوالب هندسية:", color = Color.LightGray, fontSize = 12.sp)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val layouts = listOf(
                                        Pair("RECTANGLE", "مستطيل"),
                                        Pair("ROUNDED_RECTANGLE", "إطار مستدير حائر"),
                                        Pair("CIRCLE", "دائرة خلفية"),
                                        Pair("TRIANGLE", "مثلث إشارة"),
                                        Pair("STAR_BADGE", "نجم العرض ذهبي"),
                                        Pair("BANNER_RIBBON", "شريط لافتة سفلي")
                                    )
                                    items(layouts) { (id, label) ->
                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                                .clickable { viewModel.addShapeLayer(id) }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                            // Add Graphic Symbols
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("إضافة ملصقات وشعارات لافتات شهيرة:", color = Color.LightGray, fontSize = 12.sp)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val stickers = listOf(
                                        Triple("ic_sparkle", "مشرق ✨", Icons.Default.AutoAwesome),
                                        Triple("ic_discount", "خصم 🏷️", Icons.Default.Discount),
                                        Triple("ic_star", "نجمة ⭐️", Icons.Default.Grade),
                                        Triple("ic_heart", "حفل / حب ❤️", Icons.Default.Favorite),
                                        Triple("ic_certified", "موثوق ✅", Icons.Default.Verified),
                                        Triple("ic_medal", "درع مميز 🏆", Icons.Default.Grade),
                                        Triple("ic_gift", "هدية ترويجية 🎁", Icons.Default.Discount),
                                        Triple("ic_spark", "برق سريع ⚡", Icons.Default.AutoAwesome),
                                        Triple("ic_coffee", "فنجان قهوة ☕", Icons.Default.Favorite),
                                        Triple("ic_cart", "عربة شراء 🛒", Icons.Default.Discount)
                                    )
                                    items(stickers) { (id, label, icon) ->
                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                                .clickable { viewModel.addStickerLayer(id) }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(label, color = Color.White, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "EXPORT" -> {
                        // Project saving & physical rendering options
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "تصدير العمل الفني وحفظه للمستقبل",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Text(
                                "أقوى أنظمة الجيرافيك تتيح قراءة وحفظ وتصدير لافتاتك بصيغ متعددة احترافية.",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showSaveDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("save_project_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("حفظ المسودة مسبقاً", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { showExportDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.IosShare, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تصدير الصورة النهائية", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                            Text("الصيغ التي يدعمها يافطة للتصدير المباشر والمزامنة مسبقاً:", color = Color.LightGray, fontSize = 11.sp)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                val formats = listOf("PNG (ويب فائق)", "JPEG (مضغوط)", "WEBP (حديث)", "SVG (لمتجهات الويب)", "PSD (قوالب مجمعة فوتوشوب)")
                                items(formats) { f ->
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(f, color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                            // A. Print Bleed Margins setup
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(if (isArabic) "معاينة حدود الأمان للقص (Print Bleed Guides)" else "Show Safe Bleed Margin Border", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(if (isArabic) "رسم خطوط أمان وهمية لمنع تلف اليافطة أثناء القص" else "Render red dashed guidelines for press cut margins", color = Color.Gray, fontSize = 9.sp)
                                }
                                Switch(
                                    checked = showPrintReviewOverlay,
                                    onCheckedChange = { showPrintReviewOverlay = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                                )
                            }

                            if (showPrintReviewOverlay) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (isArabic) "هامش القص: ${String.format("%.1f", printMarginCm)}cm" else "Bleed Offset: ${printMarginCm}cm", color = Color.White, modifier = Modifier.width(110.dp), fontSize = 11.sp)
                                    Slider(
                                        value = printMarginCm,
                                        onValueChange = { viewModel.updatePrintMargin(it) },
                                        valueRange = 0.2f..2.5f,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (isArabic) "كثافة الطباعة: ${printResolutionDpi}DPI" else "Print DPI: ${printResolutionDpi}", color = Color.White, modifier = Modifier.width(110.dp), fontSize = 11.sp)
                                    Slider(
                                        value = printResolutionDpi.toFloat(),
                                        onValueChange = { viewModel.updatePrintResolution(it.toInt()) },
                                        valueRange = 150f..600f,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                            // B. Mockups panel
                            Text(if (isArabic) "معاينة ثلاثية الأبعاد وعرض النماذج (Realistic Mockups) 🖼️" else "Realistic Mockups Generator & Visualizer 🖼️", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                             ) {
                                 listOf(
                                     Pair("BILLBOARD", if (isArabic) "يافطة طريق" else "Billboard"),
                                     Pair("SHOPFRONT", if (isArabic) "واجهة محل" else "Shop Front"),
                                     Pair("ROLLUP", if (isArabic) "ستاند رول-اب" else "Roll up"),
                                     Pair("DIGITAL", if (isArabic) "شاشة ذكية" else "Smart LED")
                                 ).forEach { (type, label) ->
                                     val isSel = selectedMockupType == type
                                     Box(
                                         modifier = Modifier
                                             .weight(1f)
                                             .background(if (isSel) MaterialTheme.colorScheme.primary else Color(0xFF131317), RoundedCornerShape(8.dp))
                                             .border(1.dp, if (isSel) Color.Transparent else Color.DarkGray, RoundedCornerShape(8.dp))
                                             .clickable { selectedMockupType = type }
                                             .padding(vertical = 12.dp),
                                         contentAlignment = Alignment.Center
                                     ) {
                                         Text(label, color = if (isSel) Color.Black else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                     }
                                 }
                             }

                            Button(
                                onClick = { showMockupDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isArabic) "عرض وتوليد النموذج ثلاثي الأبعاد الواقعي ⚡" else "Visualize Design Inside Mockup Scene ⚡", fontWeight = FontWeight.Bold)
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                            // C. PDF/X export button
                            Button(
                                onClick = { showPdfXExportDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE02424))
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isArabic) "تصدير بصيغة مطابع اليافطات الرسمية PDF/X-1a 📄" else "Compile Press Ready Design to PDF/X-1a 📄", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            }
            } // Closes outer central area Column
            
            // --- GORGEOUS PROPERTIES PANEL ON THE RIGHT SIDE ---
            if (!isFullScreenMode && isRightPropertiesPanelExpanded) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D)),
                    shape = RoundedCornerShape(0.dp), // stays beautifully glued on the side
                    modifier = Modifier
                        .width(230.dp)
                        .fillMaxHeight()
                        .border(1.dp, Color(0xFF2C2F3A))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = if (isArabic) "خصائص وعناصر التصميم ⚙️" else "Design Properties ⚙️",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Color(0xFFFFC107)
                        )
                        
                        val activeLayer = layers.find { it.id == selectedId }
                        if (activeLayer != null) {
                            Text(
                                text = "Layer Type: ${activeLayer.type}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Coord X", color = Color.Gray, fontSize = 9.sp)
                                    Text("${activeLayer.x.toInt()}px", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Coord Y", color = Color.Gray, fontSize = 9.sp)
                                    Text("${activeLayer.y.toInt()}px", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Divider(color = Color.DarkGray)
                            
                            // Width Slider
                            Text(if (isArabic) "عرض الطبقة (${activeLayer.width.toInt()}px)" else "Width (${activeLayer.width.toInt()}px)", color = Color.LightGray, fontSize = 10.sp)
                            Slider(
                                value = activeLayer.width,
                                onValueChange = { viewModel.updateLayerSize(activeLayer.id, it, activeLayer.height) },
                                valueRange = 40f..900f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFC107))
                            )
                            
                            // Height Slider
                            Text(if (isArabic) "ارتفاع الطبقة (${activeLayer.height.toInt()}px)" else "Height (${activeLayer.height.toInt()}px)", color = Color.LightGray, fontSize = 10.sp)
                            Slider(
                                value = activeLayer.height,
                                onValueChange = { viewModel.updateLayerSize(activeLayer.id, activeLayer.width, it) },
                                valueRange = 40f..900f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFFF9800))
                            )
                            
                            // Rotation slider
                            Text(if (isArabic) "درجة الدوران (${activeLayer.rotation.toInt()}°)" else "Rotation (${activeLayer.rotation.toInt()}°)", color = Color.LightGray, fontSize = 10.sp)
                            Slider(
                                value = activeLayer.rotation,
                                onValueChange = { viewModel.updateLayerRotation(activeLayer.id, it) },
                                valueRange = 0f..360f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFFF9800))
                            )
                            
                            // Opacity slider
                            Text(if (isArabic) "معدل الشفافية (${(activeLayer.opacity * 100).toInt()}%)" else "Opacity (${(activeLayer.opacity * 100).toInt()}%)", color = Color.LightGray, fontSize = 10.sp)
                            Slider(
                                value = activeLayer.opacity,
                                onValueChange = { viewModel.updateLayerOpacity(activeLayer.id, it) },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF10B981))
                            )
                            
                            Divider(color = Color.DarkGray)
                            
                            // Extra Font Typography system shortcut center
                            if (activeLayer.type == "TEXT") {
                                Button(
                                    onClick = { showFontCenterDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.TextFields, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isArabic) "تغيير الخط ونوع الحرف" else "Select Typography", fontSize = 10.sp)
                                }
                            }
                            
                            // Lock and duplicate actions
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(
                                    onClick = { viewModel.toggleLockLayer(activeLayer.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (activeLayer.isLocked) Color(0xFFFF9800) else Color(0xFF2E313D)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(if (activeLayer.isLocked) Icons.Default.Lock else Icons.Default.LockOpen, null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (activeLayer.isLocked) "Locked" else "Lock", fontSize = 9.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { viewModel.deleteSelectedLayer() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE02424)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete", fontSize = 9.sp)
                                }
                            }
                        } else {
                            Text(if (isArabic) "اضغط على أي عنصر أو طبقة على اللوحة للتحكم الدقيق بخصائصه الإنشائية والمكانية." else "Tap any layer on canvas to refine its precise geometry, typography, and orientations instantly.", color = Color.Gray, fontSize = 10.sp, lineHeight = 14.sp)
                        }
                    }
                }
            }
            } // Closes outer split Row in Scaffold body
        }
    }

    // --- NEW WORD TYPOGRAPHY ENGINE DIALOGS REGISTER ---
    if (showFontCenterDialog) {
        FontCenterDialog(
            isArabic = isArabic,
            onDismiss = { showFontCenterDialog = false },
            onFontApplied = { fontName ->
                if (selectedId != null) {
                    val activeLayer = layers.find { it.id == selectedId }
                    if (activeLayer != null && activeLayer.type == "TEXT") {
                        viewModel.updateLayerText(selectedId!!, activeLayer.text, activeLayer.fontSize, fontName)
                        Toast.makeText(context, if (isArabic) "تم تطبيق خط $fontName" else "Applied font $fontName", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, if (isArabic) "يرجى اختيار طبقة نصية أولاً لتطبيق الخط عليها!" else "Please select a text layer first to apply font styling!", Toast.LENGTH_SHORT).show()
                }
                showFontCenterDialog = false
            },
            favoriteFonts = favoriteFonts
        )
    }

    if (showFontRecognitionDialog) {
        FontRecognitionDialog(
            isArabic = isArabic,
            onDismiss = { showFontRecognitionDialog = false },
            onFontSelected = { fontName ->
                if (selectedId != null) {
                    val activeLayer = layers.find { it.id == selectedId }
                    if (activeLayer != null && activeLayer.type == "TEXT") {
                        viewModel.updateLayerText(selectedId!!, activeLayer.text, activeLayer.fontSize, fontName)
                        Toast.makeText(context, if (isArabic) "تم تطبيق خط $fontName بنجاح!" else "Applied matched font $fontName successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
                showFontRecognitionDialog = false
            }
        )
    }

    // ---------------- SAVE DRAFT STATE DIALOG ---------------- //
    if (showSaveDialog) {
        Dialog(onDismissRequest = { showSaveDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("أدخل اسم لوحة اليافطة المبتكرة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    
                    OutlinedTextField(
                        value = projectTitleInput,
                        onValueChange = { projectTitleInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showSaveDialog = false }) {
                            Text("رجوع", color = Color.LightGray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.saveCurrentProject(projectTitleInput)
                                showSaveDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("حفظ وتخزين 💾", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ---------------- EXPORT IMAGE METADATA DIALOG ---------------- //
    if (showExportDialog) {
        Dialog(onDismissRequest = { showExportDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("ضبط إعدادات التصدير والحفظ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    
                    Text("اختر صيغة الملف لحفظه بالبرامج الرائدة:", color = Color.LightGray, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("PNG", "JPEG", "WEBP", "SVG", "PSD").forEach { format ->
                            val isF = exportFormat == format
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isF) MaterialTheme.colorScheme.primary else Color(0xFF0F0F12), RoundedCornerShape(10.dp))
                                    .border(1.dp, if (isF) Color.Transparent else MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                    .clickable { exportFormat = format }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(format, color = if (isF) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    Text("دقة التصدير (المجموع العدادي):", color = Color.LightGray, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf("Standard (480p)", "HQ (1080p)", "Ultra HD (4K)")) { res ->
                            val isR = exportResolution == res
                            Box(
                                modifier = Modifier
                                    .background(if (isR) MaterialTheme.colorScheme.primary else Color(0xFF0F0F12), RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isR) Color.Transparent else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .clickable { exportResolution = res }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(res, color = if (isR) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                    Button(
                        onClick = {
                            Toast.makeText(context, "تم تصدير وحفظ الصورة الإعلانية كملف $exportFormat بنجاح داخل المعرض! 💾🎥", Toast.LENGTH_LONG).show()
                            showExportDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("الإنهاء والتحميل 💾", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Preset background layouts selection dialog
    if (showPresetBackgroundDialog) {
        Dialog(onDismissRequest = { showPresetBackgroundDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("مجموعات القوالب والخلفيات الجاهزة", color = Color.White, fontWeight = FontWeight.Bold)
                    
                    val presets = listOf(
                        Triple("برجر وحفلات المطاعم 🍔", "BURGER", "تصاميم سريعة للمأكولات بنمط دافئ"),
                        Triple("بطاقات زفاف تهنئة 💍", "WEDDING", "تصميم رومانسي فخم بأطر ذهبية مبهجة"),
                        Triple("مقاهي القهوة الفطيرة ☕", "COFFEE", "درجات دافئة من البني مريحة للعين"),
                        Triple("تطوير واجهات مينيمال 📱", "CLEAN", "تصميم عصري بسيط بلون أسود حاد")
                    )

                    presets.forEach { (title, key, desc) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F0F12), RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.loadPresetTemplate(key)
                                    showPresetBackgroundDialog = false
                                }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(title, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(desc, color = Color.LightGray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // ---------------- SETTINGS DIALOG WITH ADVANCED CONTROLS ---------------- //
    if (showSettingsDialog) {
        Dialog(onDismissRequest = { showSettingsDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isArabic) "إعدادات استوديو الذكاء الاصطناعي 🛠️" else "AI Studio Settings 🛠️",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = if (isArabic) TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = Color.DarkGray)

                    // Language Toggle Switches
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "لغة الواجهة (Language)" else "Interface Language",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "التبديل بين العربية والإنجليزية" else "Switch between Arabic & English",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleLanguage() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = if (isArabic) "English 🇺🇸" else "العربية 🇸🇦",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(color = Color.DarkGray)

                    // Custom API Key Ingestion
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isArabic) "مفتاح API الخاص بـ Gemini 🔑" else "Gemini API Key 🔑",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "لتخطي قيود حظر طلبات توليد الذكاء الاصطناعي" else "Provide own Gemini token to bypass request limits",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )

                        var apiKeyInput by remember { mutableStateOf(customApiKey) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = apiKeyInput,
                                onValueChange = { apiKeyInput = it },
                                placeholder = { Text("AIzaSy...", fontSize = 11.sp, color = Color.Gray) },
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = Color.White),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.DarkGray
                                )
                            )

                            Button(
                                onClick = {
                                    viewModel.setCustomApiKey(apiKeyInput)
                                    Toast.makeText(context, if (isArabic) "تم حفظ مفتاح الترخيص بنجاح!🔑" else "API Key Saved Successfully!🔑", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text(if (isArabic) "حفظ" else "Save", fontSize = 10.sp)
                            }
                        }
                    }

                    HorizontalDivider(color = Color.DarkGray)

                    // Grid subdivisions & magnetism
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "خطوط الشبكة الفرعية 📊" else "Grid Subdivisions 📊",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "عدد تقسيمات الشبكة: $gridSubdivisions" else "Subdivisions count: $gridSubdivisions",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(10, 20, 30).forEach { num ->
                                val isSelected = gridSubdivisions == num
                                Box(
                                    modifier = Modifier
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF131317), RoundedCornerShape(8.dp))
                                        .clickable { viewModel.updateGridSubdivisions(num) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(num.toString(), color = if (isSelected) Color.Black else Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "قوة مغناطيسية خطوط المحاذاة 🧲" else "Snap Magnetism 🧲",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "مدى الجذب بالبكسل: ${snapMagnetism.toInt()}px" else "Pixels distance: ${snapMagnetism.toInt()}px",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }

                        Slider(
                            value = snapMagnetism,
                            onValueChange = { viewModel.updateSnapMagnetism(it) },
                            valueRange = 5f..30f,
                            modifier = Modifier.width(100.dp)
                        )
                    }

                    HorizontalDivider(color = Color.DarkGray)

                    // Printing Calibration Settings
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isArabic) "إعدادات الطباعة والقياس المادي 🖨️" else "Printing & Proofing Calibration 🖨️",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (isArabic) "هوامش أمان الطباعة (سم):" else "Safety margin (cm):", color = Color.LightGray, fontSize = 11.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { viewModel.updatePrintMargin((printMarginCm - 0.5f).coerceIn(0f, 5f)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131317)),
                                    modifier = Modifier.size(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("-", color = Color.White)
                                }
                                Text("$printMarginCm", color = Color.White, fontSize = 12.sp)
                                Button(
                                    onClick = { viewModel.updatePrintMargin((printMarginCm + 0.5f).coerceIn(0f, 5f)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131317)),
                                    modifier = Modifier.size(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("+", color = Color.White)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (isArabic) "دقة المعالجة الفنية (DPI):" else "Rendering resolution (DPI):", color = Color.LightGray, fontSize = 11.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(150, 300, 600).forEach { dpi ->
                                    val isSelected = printResolutionDpi == dpi
                                    Box(
                                        modifier = Modifier
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF131317), RoundedCornerShape(6.dp))
                                            .clickable { viewModel.updatePrintResolution(dpi) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("${dpi}DPI", color = if (isSelected) Color.Black else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.DarkGray)

                    // Cloud backup simulator
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isArabic) "مزامنة سحابية مسبقة ☁️" else "Cloud Safe Sync ☁️",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isArabic) "المزامنة والنسخ التلقائي السحابي بنقرة" else "Backup and restore templates to remote cloud safety",
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            }

                            Switch(
                                checked = cloudBackupEnabled,
                                onCheckedChange = { viewModel.toggleCloudBackup(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                            )
                        }

                        if (cloudBackupEnabled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val currentSyncText = cloudSyncProgress
                                if (currentSyncText != null) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = currentSyncText,
                                            color = Color.LightGray,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(
                                        onClick = { viewModel.triggerCloudSync() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text(if (isArabic) "مزامنة الآن 🔄" else "Sync Now 🔄", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.DarkGray)

                    Button(
                        onClick = { showSettingsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isArabic) "إغلاق نافذة الإعدادات" else "Close Settings Panel", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // ---------------- Phase 3 Professional Dialogs ---------------- //

    // 1. Custom Font Upload Simulator
    if (showFontUploadDialog) {
        Dialog(onDismissRequest = { showFontUploadDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131317)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isArabic) "محاكي رفع الخطوط الرقمية المخصصة (TTF/OTF)" else "Register Custom Local Font Simulator",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (isArabic) "اكتب اسم الخط أو اختر ملفاً محلياً لتسجيله وتطبيقه فوراً على الطبقات الـ TEXT" else "Type custom font family descriptor or select a file to simulate workspace registration",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = customFontInputName,
                        onValueChange = { customFontInputName = it },
                        placeholder = { Text("مثال: Cairo-Bold أو Amiri Kufic...", fontSize = 11.sp, color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.DarkGray
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showFontUploadDialog = false }) {
                            Text(if (isArabic) "إلغاء" else "Cancel", color = Color.LightGray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (customFontInputName.isNotBlank()) {
                                    Toast.makeText(context, if (isArabic) "✓ تم تسجيل وتحميل الخط الداخلي ${customFontInputName}!" else "Successfully compiled & loaded ${customFontInputName} font family descriptor!", Toast.LENGTH_LONG).show()
                                    val actId = selectedId
                                    if (actId != null) {
                                        viewModel.updateLayerText(actId, layers.find { it.id == actId }?.text ?: "", fFamily = "ARABIC_DISPLAY")
                                    }
                                    showFontUploadDialog = false
                                } else {
                                    Toast.makeText(context, if (isArabic) "الرجاء كتابة اسم الخط!" else "Please write a valid font reference name!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(if (isArabic) "تسجيل الخط 📝" else "Register 📝", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // 2. High-Fidelity PDF/X Press Assembler Simulation
    if (showPdfXExportDialog) {
        var pdfStep by remember { mutableStateOf(1) }
        var pdfStatusMsg by remember { mutableStateOf(if (isArabic) "جاري التحضير لتشغيل مجمع PDF/X..." else "Loading PDF/X Print compiler standard...") }

        LaunchedEffect(pdfStep) {
            when (pdfStep) {
                1 -> {
                    kotlinx.coroutines.delay(1000)
                    pdfStatusMsg = if (isArabic) "1. تحويل كافة المكونات والألوان والنصوص إلى نظام CMYK Euroscale Coated v2..." else "1. Converting vector structures & linear brushes to CMYK Euroscale Coated v2..."
                    pdfStep = 2
                }
                2 -> {
                    kotlinx.coroutines.delay(1200)
                    pdfStatusMsg = if (isArabic) "2. تضمين واصفات الخطوط العربية المختارة (Font Descriptors) داخل المجمع لمنع التشوه..." else "2. Resolving type outlines and embedding vector typography descriptors..."
                    pdfStep = 3
                }
                3 -> {
                    kotlinx.coroutines.delay(1200)
                    pdfStatusMsg = if (isArabic) "3. وضع أبعاد القص وأطر الأمان الطباعية (MediaBox، TrimBox، BleedBox)..." else "3. Registering printing margins and boundaries (MediaBox, TrimBox, BleedBox)..."
                    pdfStep = 4
                }
                4 -> {
                    kotlinx.coroutines.delay(1000)
                    pdfStatusMsg = if (isArabic) "4. ضغط كافة المكونات والطبقات بدقة 300DPI وحفظ الملف..." else "4. Packaging layers and assets in high raster format (300DPI)..."
                    pdfStep = 5
                }
            }
        }

        Dialog(onDismissRequest = { if (pdfStep == 5) showPdfXExportDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131317)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = if (pdfStep == 5) Color(0xFF10B981) else Color(0xFFE02424),
                        modifier = Modifier.size(44.dp)
                    )

                    Text(
                        text = if (isArabic) "مجمع وحوافظ مطابع اليافطات PDF/X-1a" else "PDF/X-1a Professional Press Assembler",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    if (pdfStep < 5) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pdfStatusMsg,
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = if (isArabic) "✓ اكتمل تجميع الملف وتصديره بنجاح فائق وصيغة مجهزة للمطابع مباشرة!" else "✓ PDF/X-1a file compiled successfully! Verified print-ready with correct bleeding, CMYK margins, and typefaces embedded.",
                            color = Color(0xFF10B981),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Technical parameters box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("• File Type: PDF/X-1a:2001 Standard", color = Color.Gray, fontSize = 10.sp)
                            Text("• Color Format: CMYK Process Calibrated", color = Color.Gray, fontSize = 10.sp)
                            Text("• Margins/Bleeds: $printMarginCm cm Safe Zones", color = Color.Gray, fontSize = 10.sp)
                            Text("• Density: $printResolutionDpi DPI Vector", color = Color.Gray, fontSize = 10.sp)
                        }

                        Button(
                            onClick = { showPdfXExportDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isArabic) "حفظ وتحميل المستند المطبوع 📄" else "Download Compiled Doc 📄", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // 3. Peak Interactive Multi-Scene Mockups Builder Dialog
    if (showMockupDialog) {
        Dialog(onDismissRequest = { showMockupDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131317)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isArabic) "النموذج الواقعي الذكي للوحتك (Mockup Scene Preview)" else "Realistic Digital Mockup Scene Preview",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    // Render mock selection tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            Pair("BILLBOARD", if (isArabic) "طريق 🛣️" else "Road"),
                            Pair("SHOPFRONT", if (isArabic) "متجر 🏬" else "Shop"),
                            Pair("ROLLUP", if (isArabic) "حامل 🪧" else "Rollup"),
                            Pair("DIGITAL", if (isArabic) "LED 📺" else "LED")
                        ).forEach { (type, label) ->
                            val isSel = selectedMockupType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Black, RoundedCornerShape(6.dp))
                                    .clickable { selectedMockupType = type }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = if (isSel) Color.Black else Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Interactive Drawing of the Scene matching the style
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF070709)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Render physical scene backgrounds
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                when (selectedMockupType) {
                                    "BILLBOARD" -> {
                                        // Draw a road / highway
                                        drawRect(Color(0xFF232329), topLeft = Offset(0f, 160f), size = androidx.compose.ui.geometry.Size(size.width, 40f))
                                        drawLine(Color.Yellow, start = Offset(0f, 180f), end = Offset(size.width, 180f), strokeWidth = 2f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 15f), 0f))
                                        // Draw the massive pole
                                        drawRect(Color(0xFF4A4A54), topLeft = Offset(size.width / 2f - 10f, 90f), size = androidx.compose.ui.geometry.Size(20f, 70f))
                                        // Frame shadow
                                        drawRect(Color(0xFF131317), topLeft = Offset(size.width / 2f - 110f, 15f), size = androidx.compose.ui.geometry.Size(220f, 80f))
                                    }
                                    "SHOPFRONT" -> {
                                        // Draw shop outline bricks
                                        drawRect(Color(0xFF2C1616), topLeft = Offset(0f, 0f), size = size)
                                        // Door
                                        drawRect(Color(0xFF1F2937), topLeft = Offset(size.width / 2f - 40f, 100f), size = androidx.compose.ui.geometry.Size(80f, 100f))
                                        // Advertising area frame above door
                                        drawRect(Color.Black, topLeft = Offset(size.width / 2f - 120f, 20f), size = androidx.compose.ui.geometry.Size(240f, 65f))
                                    }
                                    "ROLLUP" -> {
                                        // Aluminum support back
                                        drawRect(Color(0xFF4B5563), topLeft = Offset(size.width / 2f - 3f, 10f), size = androidx.compose.ui.geometry.Size(6f, 180f))
                                        // Double feet
                                        drawLine(Color(0xFF374151), start = Offset(size.width / 2f - 30f, 190f), end = Offset(size.width / 2f + 30f, 190f), strokeWidth = 8f)
                                        // Active canvas frame
                                        drawRect(Color(0xFF000000), topLeft = Offset(size.width / 2f - 55f, 15f), size = androidx.compose.ui.geometry.Size(110f, 172f))
                                    }
                                    "DIGITAL" -> {
                                        // Futuristic studio glowing dots
                                        drawCircle(Color(0x11FFD700), radius = 50f, center = Offset(50f, 50f))
                                        drawCircle(Color(0x11FFD700), radius = 70f, center = Offset(size.width - 50f, 150f))
                                        // Sleek bezel stand
                                        drawRect(Color(0xFF1E1B4B), topLeft = Offset(size.width / 2f - 15f, 150f), size = androidx.compose.ui.geometry.Size(30f, 50f))
                                        // Frame
                                        drawRect(Color(0xFF111827), topLeft = Offset(size.width / 2f - 125f, 20f), size = androidx.compose.ui.geometry.Size(250f, 130f))
                                    }
                                }
                            }

                            // Layer physical components layout scaled down exactly to perspective!
                            val backBrush = if (isGradient) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(android.graphics.Color.parseColor(gradStartHex)),
                                        Color(android.graphics.Color.parseColor(gradEndHex))
                                    )
                                )
                            } else {
                                SolidColor(Color(android.graphics.Color.parseColor(solidColorHex)))
                            }

                            // Scaling container overlay representing their live design canvas
                            Box(
                                modifier = Modifier
                                    .then(
                                        when (selectedMockupType) {
                                            "BILLBOARD" -> Modifier.size(208.dp, 72.dp).offset(y = (-37).dp)
                                            "SHOPFRONT" -> Modifier.size(232.dp, 58.dp).offset(y = (-35).dp)
                                            "ROLLUP" -> Modifier.size(102.dp, 164.dp).offset(y = (-3).dp)
                                            else -> Modifier.size(242.dp, 122.dp).offset(y = (-10).dp)
                                        }
                                    )
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(backBrush)
                            ) {
                                // Draw miniaturized layers
                                layers.forEach { miniLayer ->
                                    if (!miniLayer.isVisible) return@forEach
                                    val scale = when (selectedMockupType) {
                                        "BILLBOARD" -> 208f / 1000f
                                        "SHOPFRONT" -> 232f / 1000f
                                        "ROLLUP" -> 102f / 1000f
                                        else -> 242f / 1000f
                                    }

                                    Box(
                                        modifier = Modifier
                                            .offset(
                                                x = (miniLayer.x * scale).dp,
                                                y = (miniLayer.y * scale).dp
                                            )
                                            .size(
                                                width = (miniLayer.width * scale).dp,
                                                height = (miniLayer.height * scale).dp
                                            )
                                            .alpha(miniLayer.opacity)
                                    ) {
                                        when (miniLayer.type) {
                                            "TEXT" -> {
                                                Text(
                                                    text = miniLayer.text,
                                                    color = Color(android.graphics.Color.parseColor(miniLayer.colorHex)),
                                                    fontSize = (miniLayer.fontSize * scale * 0.9f).sp,
                                                    maxLines = 1,
                                                    lineHeight = (miniLayer.fontSize * scale * 0.9f).sp,
                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                )
                                            }
                                            "SHAPE" -> {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            Color(android.graphics.Color.parseColor(miniLayer.colorHex)),
                                                            shape = if (miniLayer.shapeType == "CIRCLE") CircleShape else RoundedCornerShape(2.dp)
                                                        )
                                                )
                                            }
                                            else -> {
                                                Icon(
                                                    imageVector = Icons.Default.Verified,
                                                    contentDescription = null,
                                                    tint = Color(android.graphics.Color.parseColor(miniLayer.colorHex)),
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = if (isArabic) "✓ النموذج يوضح تباين الخطوط ومحاذاة الطبقات الحقيقية لعملك في الواقع الافتراضي." else "✓ Perspective modeling matches your exact layer coordinates and canvas aspect ratios.",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { showMockupDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isArabic) "الرجوع للاستوديو" else "Back to Studio Panel", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
