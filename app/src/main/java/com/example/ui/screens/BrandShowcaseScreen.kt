package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandShowcaseScreen(
    isArabic: Boolean,
    onClose: () -> Unit
) {
    var selectedVersion by remember { mutableStateOf("MAIN") } // MAIN, APP_ICON, SPLASH, DARK, LIGHT, HORIZONTAL, VERTICAL
    var showGridLines by remember { mutableStateOf(true) }
    var glowIntensity by remember { mutableStateOf(0.7f) }
    var zoomFactor by remember { mutableStateOf(1f) }
    var activeMockupType by remember { mutableStateOf("NONE") } // NONE, SHOP_BOARD, GLASS_WALL, APP_PREVIEW, CNC_BRASS
    
    val versionsList = listOf(
        Triple("MAIN", if (isArabic) "الشعار الرئيسي" else "Main Logo", Icons.Default.WorkspacePremium),
        Triple("APP_ICON", if (isArabic) "أيقونة التطبيق" else "App Icon", Icons.Default.SmartButton),
        Triple("SPLASH", if (isArabic) "شاشة البدء" else "Splash Screen", Icons.Default.ImportantDevices),
        Triple("DARK", if (isArabic) "الوضع الداكن" else "Dark Mode", Icons.Default.DarkMode),
        Triple("LIGHT", if (isArabic) "الوضع الفاتح" else "Light Mode", Icons.Default.LightMode),
        Triple("HORIZONTAL", if (isArabic) "النسخة الأفقية" else "Horizontal Layout", Icons.Default.GridGoldenratio),
        Triple("VERTICAL", if (isArabic) "النسخة الرأسية" else "Vertical Layout", Icons.Default.ViewStream)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isArabic) "الهوية البصرية الفاخرة • يافطة للتصميم" else "Luxury Brand Room • YAFTA Design",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFC107)
                        )
                        Text(
                            text = if (isArabic) "مواصفات الشعار الهندسي ثلاثي الأبعاد 3D" else "CNC Acrylic & Gold Layered 3D Emblem",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showGridLines = !showGridLines }) {
                        Icon(
                            imageVector = Icons.Default.Grid4x4,
                            contentDescription = "Toggle Grid Guides",
                            tint = if (showGridLines) Color(0xFFFF9800) else Color.Gray
                        )
                    }
                    IconButton(onClick = { 
                        zoomFactor = if (zoomFactor == 1f) 1.25f else if (zoomFactor == 1.25f) 0.85f else 1f
                    }) {
                        Icon(Icons.Default.ZoomIn, "Zoom Toggle", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F0F14))
            )
        },
        containerColor = Color(0xFF0B0B0F)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. LIVE VECTOR INTERACTIVE RENDERING BOX ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(
                        if (selectedVersion == "LIGHT" && activeMockupType == "NONE") Color(0xFFFDFBF7) else Color(0xFF0D0D11)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // If a mockup is selected, we inject it inside a gorgeous surrounding context
                if (activeMockupType != "NONE") {
                    MockupView(
                        type = activeMockupType,
                        selectedVersion = selectedVersion,
                        isArabic = isArabic,
                        glowIntensity = glowIntensity,
                        zoomFactor = zoomFactor,
                        showGridLines = showGridLines
                    )
                } else {
                    // Standard isolated technical model
                    LogoModelRenderer(
                        version = selectedVersion,
                        glowIntensity = glowIntensity,
                        zoom = zoomFactor,
                        showGrid = showGridLines,
                        isArabic = isArabic
                    )
                }

                // Fine Tech Annotations
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "RENDER STATS: VCTOR_GEOM_OK",
                        color = Color(0xFF10B981),
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "SCALE: " + String.format("%.2fx", zoomFactor) + " | SHAD_BLUR: 40dp",
                        color = Color.LightGray,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    if (activeMockupType != "NONE") {
                        Text(
                            text = "MOCKUP: $activeMockupType",
                            color = Color(0xFFFF9800),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Ruler grid indicator
                if (showGridLines) {
                    Text(
                        text = "YAFTA DESIGN CNC SYSTEM • ACCURACY 0.05mm",
                        color = Color(0xFFFFC107).copy(alpha = 0.4f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                    )
                }
            }

            // --- 2. VERSION TABS ROW (HORIZONTAL LIST) ---
            Text(
                text = if (isArabic) "📁 تصفح نسخ وحالات الشعار المتكاملة" else "📁 Discover Delivered Branding Layouts",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(versionsList) { (id, label, icon) ->
                    val isSelected = selectedVersion == id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFFFFC107).copy(alpha = 0.15f) else Color(0xFF16181D))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFFFC107) else Color(0xFF2E313D),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { 
                                selectedVersion = id 
                                // Clean light mode doesn't pair nicely with non-light mockup initially
                                if (id == "LIGHT") {
                                    activeMockupType = "NONE"
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = label,
                                color = if (isSelected) Color(0xFFFFC107) else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // --- 3. PARAMETRIC ENGINE CONTROLLER SECTION ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(0.5.dp, Color(0xFF2E313D), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Tune, "Control Tuning", tint = Color(0xFFFF9800), modifier = Modifier.size(18.dp))
                            Text(
                                text = if (isArabic) "متحكمات الإضاءة والتجسيم" else "Parametric Lighting & Materials",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        
                        Button(
                            onClick = {
                                glowIntensity = 0.7f
                                zoomFactor = 1f
                                activeMockupType = "NONE"
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E313D)),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(if (isArabic) "إعادة ضبط" else "Reset Engine", fontSize = 10.sp, color = Color.White)
                        }
                    }

                    // Intensity Selector
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) "شدة وهج النيون والأكريليك" else "Acrylic Neon Glow Intensity",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                            Text(
                                text = String.format("%.0f%%", glowIntensity * 100),
                                fontSize = 11.sp,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = glowIntensity,
                            onValueChange = { glowIntensity = it },
                            valueRange = 0.1f..1.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFF9800),
                                activeTrackColor = Color(0xFFFF9800)
                            )
                        )
                    }
                }
            }

            // --- 4. REAL PHYSICAL MOCKUP SIMULATIONS ---
            Text(
                text = if (isArabic) "🏢 محاكاة الشعار على أسطح ومواد حقيقية" else "🏢 Real Surface & Material Mockup Tests",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val mockups = listOf(
                    Triple("NONE", if (isArabic) "منظور معزول\n(Isolated)" else "Isolated\nPerspective", Icons.Default.AspectRatio),
                    Triple("SHOP_BOARD", if (isArabic) "واجهة لافتة مضيئة\n(Outdoor Sign)" else "Outdoor lit\nSignboard", Icons.Default.Launch),
                    Triple("GLASS_WALL", if (isArabic) "لوح زجاجي مكتبي\n(Glass Plaque)" else "Corporate\nGlass Board", Icons.Default.FilterFrames),
                    Triple("CNC_BRASS", if (isArabic) "نحت ومعدن CNC\n(CNC Engraving)" else "CNC Gold\nBrass Plaque", Icons.Default.Copyright)
                )
                
                mockups.forEach { (id, label, icon) ->
                    val isAct = activeMockupType == id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isAct) Color(0xFFFF9800).copy(alpha = 0.15f) else Color(0xFF16181D))
                            .border(1.dp, if (isAct) Color(0xFFFF9800) else Color(0xFF2E313D), RoundedCornerShape(8.dp))
                            .clickable { activeMockupType = id }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(icon, null, tint = if (isAct) Color(0xFFFF9800) else Color.LightGray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                label,
                                color = Color.White,
                                fontSize = 8.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 10.sp
                            )
                        }
                    }
                }
            }

            // --- 5. THE DESIGN SPEC SHEET ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .border(0.5.dp, Color(0xFF2E313D), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Task, "Specifications info", tint = Color(0xFFFFC107))
                        Text(
                            text = if (isArabic) "المواصفات الفنية المعتمدة للهوية 8K" else "Technical Brand Specifications Suite",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Divider(color = Color(0xFF2E313D))

                    // Spec 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isArabic) "خطوط الكتابة العربي" else "Arabic Primary Typography", color = Color.LightGray, fontSize = 12.sp)
                        Text(if (isArabic) "خط ديواني هندسي مخصص" else "Geometric Diwani Custom Calligraphy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Spec 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isArabic) "خطوط الكتابة الإنجليزية" else "English Secondary Typography", color = Color.LightGray, fontSize = 12.sp)
                        Text("Futura Black / Space Grotesk Bold", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Spec 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isArabic) "الألوان المعتمدة ومستوى التباين" else "Verified Color Contrast Standard", color = Color.LightGray, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(12.dp).background(Color(0xFFFFC107)).border(0.5.dp, Color.White))
                            Box(modifier = Modifier.size(12.dp).background(Color(0xFFFF9800)).border(0.5.dp, Color.White))
                            Box(modifier = Modifier.size(12.dp).background(Color(0xFF16181D)).border(0.5.dp, Color.White))
                            Box(modifier = Modifier.size(12.dp).background(Color(0xFF0B0B0F)).border(0.5.dp, Color.White))
                        }
                    }

                    // Spec 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isArabic) "طريقة القص والتصنيع" else "Fabrication / Material Inspiration", color = Color.LightGray, fontSize = 12.sp)
                        Text(
                            text = if (isArabic) "قص ليزر CNC أكريليك مطلي بالذهب ومضاء خلفية" else "CNC Gold CNC Brushed Metal Overlay + Neon",
                            color = Color(0xFFFFC107),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // --- 6. EXPORT / SAVE BUTTONS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* Export action simulated */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Download, "Download raw logo", tint = Color(0xFF0F0F14))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "تحميل PNG شفاف (8K)" else "Export Transparent PNG",
                        color = Color(0xFF0F0F14),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { /* Export SVG pattern simulated */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16181D)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0xFF2E313D), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Code, "Get SVG specs", tint = Color.LightGray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "تصدير مسارات ليزر SVG" else "Get CNC Vector SVG",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Utility function to decide state colors
fun isSelected(selected: Boolean): Color = if (selected) Color(0xFFFF9800) else Color.LightGray

@Composable
fun LogoModelRenderer(
    version: String,
    glowIntensity: Float,
    zoom: Float,
    showGrid: Boolean,
    isArabic: Boolean
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .shadow(if (version == "LIGHT") 0.dp else 24.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2
        
        val baseRadius = minOf(width, height) * 0.28f * zoom

        // 1. Draw Technical Grid background if requested
        if (showGrid) {
            val gridStep = 40f
            // Grid Lines
            val gridColor = if (version == "LIGHT") Color.LightGray.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.15f)
            var currentX = 0f
            while (currentX < width) {
                drawLine(gridColor, Offset(currentX, 0f), Offset(currentX, height), 1f)
                currentX += gridStep
            }
            var currentY = 0f
            while (currentY < height) {
                drawLine(gridColor, Offset(0f, currentY), Offset(width, currentY), 1f)
                currentY += gridStep
            }
            // Concentric design calibration circles
            drawCircle(gridColor, baseRadius * 1.5f, Offset(centerX, centerY), style = Stroke(1f))
            drawCircle(gridColor, baseRadius * 0.8f, Offset(centerX, centerY), style = Stroke(0.5f))
            
            // X & Y Axes
            drawLine(Color(0xFFFF9800).copy(alpha = 0.3f), Offset(centerX, 0f), Offset(centerX, height), 1.5f)
            drawLine(Color(0xFFFF9800).copy(alpha = 0.3f), Offset(0f, centerY), Offset(width, centerY), 1.5f)
        }

        // Apply state rules based on version:
        when (version) {
            "APP_ICON" -> {
                // Large SQUIRCLE backdrop (adaptive icon container style)
                val appBgBrush = Brush.radialGradient(
                    listOf(Color(0xFF1E2028), Color(0xFF0F1014)),
                    Offset(centerX, centerY - 50f)
                )
                drawRoundRect(
                    brush = appBgBrush,
                    topLeft = Offset(centerX - baseRadius * 1.2f, centerY - baseRadius * 1.2f),
                    size = Size(baseRadius * 2.4f, baseRadius * 2.4f),
                    cornerRadius = CornerRadius(baseRadius * 0.5f, baseRadius * 0.5f),
                    style = Fill
                )
                // App premium borders
                drawRoundRect(
                    brush = Brush.linearGradient(listOf(Color(0xFFFFC107).copy(alpha = 0.8f), Color(0xFFFF9800).copy(alpha = 0.2f))),
                    topLeft = Offset(centerX - baseRadius * 1.2f, centerY - baseRadius * 1.2f),
                    size = Size(baseRadius * 2.4f, baseRadius * 2.4f),
                    cornerRadius = CornerRadius(baseRadius * 0.5f, baseRadius * 0.5f),
                    style = Stroke(3f)
                )
            }
            "LIGHT" -> {
                // Draw luxury shadow under white backdrop plate to make it feel tangible
                drawCircle(
                    Color.Black.copy(alpha = 0.08f),
                    baseRadius * 1.1f,
                    Offset(centerX, centerY + 10f),
                    style = Fill
                )
            }
            else -> {
                // Dark luxurious core behind emblem
                drawCircle(
                    Color.Black.copy(alpha = 0.4f),
                    baseRadius * 1.05f,
                    Offset(centerX, centerY),
                    style = Fill
                )
            }
        }

        // Glow back-radiations
        if (version != "LIGHT") {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFF9800).copy(alpha = 0.2f * glowIntensity), Color.Transparent),
                    Offset(centerX, centerY)
                ),
                radius = baseRadius * 1.8f,
                center = Offset(centerX, centerY)
            )
        }

        // Core 3D Layer 1: Brushed Metal CNC shield/ring base
        val ringGradient = if (version == "LIGHT") {
            listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1), Color(0xFFE2E8F0))
        } else {
            listOf(Color(0xFF1E2028), Color(0xFF14151B), Color(0xFF1E2028))
        }
        
        drawCircle(
            brush = Brush.sweepGradient(ringGradient, Offset(centerX, centerY)),
            radius = baseRadius,
            center = Offset(centerX, centerY),
            style = Fill
        )

        // Golden Bevel Border (CNC milling simulation)
        val goldBevelBrush = Brush.linearGradient(
            listOf(Color(0xFFFFD700), Color(0xFFFF9800), Color(0xFFFFF8DC), Color(0xFFFF9800)),
            Offset(centerX - baseRadius, centerY - baseRadius),
            Offset(centerX + baseRadius, centerY + baseRadius)
        )
        drawCircle(
            brush = goldBevelBrush,
            radius = baseRadius,
            center = Offset(centerX, centerY),
            style = Stroke(width = baseRadius * 0.07f)
        )

        // Inside neon acrylic ring path
        if (version != "LIGHT") {
            drawCircle(
                color = Color(0xFFFFC107).copy(alpha = 0.8f * glowIntensity),
                radius = baseRadius * 0.88f,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )
        }

        // Core 3D Layer 2: Inside calligraphic letterform representing 'Y' + 'ي'
        // Let's draw an elegant hand-milled calligraphy sculpture path manually inside the circle
        val calligBrush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFF099), Color(0xFFFFC107), Color(0xFFFF9800), Color(0xFFB57000)),
            start = Offset(centerX - baseRadius * 0.5f, centerY - baseRadius * 0.5f),
            end = Offset(centerX + baseRadius * 0.5f, centerY + baseRadius * 0.5f)
        )

        val logoPath = Path().apply {
            val s = baseRadius * 1.25f
            // Primary elegant structural curve for Arabic Calligraphy "يـ / يافطة"
            moveTo(centerX - s * 0.15f, centerY - s * 0.35f)
            // Left branch of the creative 'Y'
            cubicTo(
                centerX - s * 0.35f, centerY - s * 0.3f,
                centerX - s * 0.45f, centerY - s * 0.05f,
                centerX - s * 0.25f, centerY + s * 0.18f
            )
            // Bottom loop of the Arabic character 'ي'
            cubicTo(
                centerX - s * 0.05f, centerY + s * 0.35f,
                centerX + s * 0.25f, centerY + s * 0.3f,
                centerX + s * 0.35f, centerY + s * 0.05f
            )
            // Right branch of the creative 'Y' and gold swirl
            cubicTo(
                centerX + s * 0.25f, centerY - s * 0.18f,
                centerX + s * 0.15f, centerY - s * 0.35f,
                centerX + s * 0.0f, centerY - s * 0.1f
            )
            // Middle stem loop
            lineTo(centerX, centerY + s * 0.15f)
        }

        // Draw overlapping dimensional drop shadow for calligraphy element
        drawPath(
            path = logoPath,
            color = Color.Black.copy(alpha = 0.5f),
            style = Stroke(width = baseRadius * 0.18f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Main CNC Calligraphy stroke
        drawPath(
            path = logoPath,
            brush = calligBrush,
            style = Stroke(width = baseRadius * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Highlights overlay (acrylic sheet edge reflection)
        drawPath(
            path = logoPath,
            brush = Brush.linearGradient(
                listOf(Color.White.copy(alpha = 0.9f), Color.Transparent, Color.White.copy(alpha = 0.4f)),
                Offset(centerX - baseRadius * 0.4f, centerY - baseRadius * 0.4f),
                Offset(centerX, centerY)
            ),
            style = Stroke(width = baseRadius * 0.03f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Twin Arabic Calligraphy dots bottom-center (CNC bronze spheres)
        val dot1Center = Offset(centerX - baseRadius * 0.18f, centerY + baseRadius * 0.5f)
        val dot2Center = Offset(centerX + baseRadius * 0.18f, centerY + baseRadius * 0.5f)
        val dotRadius = baseRadius * 0.08f

        // Shadow under dots
        drawCircle(Color.Black.copy(alpha = 0.4f), dotRadius, dot1Center + Offset(0f, 4f))
        drawCircle(Color.Black.copy(alpha = 0.4f), dotRadius, dot2Center + Offset(0f, 4f))

        // Sphere Rendering (CNC cut sphere with highlight)
        drawCircle(calligBrush, dotRadius, dot1Center)
        drawCircle(calligBrush, dotRadius, dot2Center)

        // Bright highlights on spheres
        drawCircle(Color.White.copy(alpha = 0.8f), dotRadius * 0.3f, dot1Center - Offset(dotRadius * 0.3f, dotRadius * 0.3f))
        drawCircle(Color.White.copy(alpha = 0.8f), dotRadius * 0.3f, dot2Center - Offset(dotRadius * 0.3f, dotRadius * 0.3f))

        // Neon Glow behind dots
        if (version != "LIGHT") {
            drawCircle(Color(0xFFFF9800).copy(alpha = 0.6f * glowIntensity), dotRadius * 1.5f, dot1Center, style = Stroke(1.5f))
            drawCircle(Color(0xFFFF9800).copy(alpha = 0.6f * glowIntensity), dotRadius * 1.5f, dot2Center, style = Stroke(1.5f))
        }

        // Under-shine spotlight ray
        if (version != "LIGHT") {
            drawLine(
                brush = Brush.verticalGradient(listOf(Color(0xFFFFC107).copy(alpha = 0.3f * glowIntensity), Color.Transparent)),
                start = Offset(centerX, centerY + baseRadius),
                end = Offset(centerX, centerY + baseRadius * 1.7f),
                strokeWidth = 3f
            )
        }
    }

    // Secondary text overlays depending on the style
    Box(modifier = Modifier.fillMaxSize()) {
        val displayTextColor = if (version == "LIGHT") Color(0xFF16181D) else Color.White
        val subtitleColor = if (version == "LIGHT") Color(0xFF71717A) else Color(0xFFFFC107)

        when (version) {
            "APP_ICON" -> {
                // Minimalist App Icon, no text, pure focus on the glyph
            }
            "HORIZONTAL" -> {
                // Text aligned to the right side
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))
                }
            }
            "SPLASH" -> {
                // Grand majestic setup
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "يافطة للتصميم",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.shadow(8.dp, clip = false)
                    )
                    Text(
                        text = "Y A F T A   D E S I G N",
                        color = Color(0xFFFFC107),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "POWERING CREATIVE SECTOR • PRO SOFTWARE SYSTEM",
                        color = Color.Gray,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                // Symmetrical Standard (MAIN, DARK, LIGHT, VERTICAL)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "يافطة للتصميم",
                        color = displayTextColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "YAFTA Design Pro",
                        color = subtitleColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MockupView(
    type: String,
    selectedVersion: String,
    isArabic: Boolean,
    glowIntensity: Float,
    zoomFactor: Float,
    showGridLines: Boolean
) {
    // Renders the chosen mockup environment frame
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF07070A))
    ) {
        when (type) {
            "SHOP_BOARD" -> {
                // Render outdoor shop board background (dark concrete facade)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Concrete blocks texture
                    drawRect(Color(0xFF1C1D24), style = Fill)
                    // Wall texture mortar joints
                    drawLine(Color(0xFF101014), Offset(0f, size.height * 0.3f), Offset(size.width, size.height * 0.3f), 4f)
                    drawLine(Color(0xFF101014), Offset(0f, size.height * 0.7f), Offset(size.width, size.height * 0.7f), 4f)
                    drawLine(Color(0xFF101014), Offset(size.width * 0.5f, 0f), Offset(size.width * 0.5f, size.height), 4f)
                    
                    // Shopboard heavy plate
                    drawRoundRect(
                        color = Color(0xFF0F0F14),
                        topLeft = Offset(size.width * 0.1f, size.height * 0.15f),
                        size = Size(size.width * 0.8f, size.height * 0.7f),
                        cornerRadius = CornerRadius(16f, 16f),
                        style = Fill
                    )
                    
                    // Metal spacer screws at corners
                    val scrollY = size.height * 0.15f
                    val scrollX = size.width * 0.1f
                    val scrollW = size.width * 0.8f
                    val scrollH = size.height * 0.7f
                    val screwColor = Color(0xFF71717A)
                    drawCircle(screwColor, 8f, Offset(scrollX + 20f, scrollY + 20f))
                    drawCircle(screwColor, 8f, Offset(scrollX + scrollW - 20f, scrollY + 20f))
                    drawCircle(screwColor, 8f, Offset(scrollX + 20f, scrollY + scrollH - 20f))
                    drawCircle(screwColor, 8f, Offset(scrollX + scrollW - 20f, scrollY + scrollH - 20f))

                    // Simulated neon wall splash reflection glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFFFF9800).copy(alpha = 0.25f * glowIntensity), Color.Transparent),
                            Offset(size.width / 2, size.height / 2)
                        ),
                        radius = size.width * 0.4f
                    )
                }
                
                // Embedded Logo model scaled nicely inside the board
                Box(modifier = Modifier.fillMaxSize(0.6f).align(Alignment.Center)) {
                    LogoModelRenderer(
                        version = selectedVersion,
                        glowIntensity = glowIntensity,
                        zoom = 0.75f * zoomFactor,
                        showGrid = false,
                        isArabic = isArabic
                    )
                }
                
                // Fine indicator
                Text(
                    text = "ESTABLISHED 2026 • PREMIUM LIT SIGNBOARD",
                    color = Color.Gray,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                )
            }
            "GLASS_WALL" -> {
                // Corporate Office Wall (Blurred office background layout)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Soft gradient represents luxury executive office backwall
                    drawRect(
                        brush = Brush.linearGradient(listOf(Color(0xFF2E313D), Color(0xFF16181D))),
                        style = Fill
                    )
                    
                    // Glass plate overlay (Acrylic panel)
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.12f),
                        topLeft = Offset(size.width * 0.15f, size.height * 0.2f),
                        size = Size(size.width * 0.7f, size.height * 0.6f),
                        cornerRadius = CornerRadius(24f, 24f),
                        style = Fill
                    )
                    // Glass highlight bevel
                    drawRoundRect(
                        brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.3f), Color.Transparent, Color.White.copy(alpha = 0.1f))),
                        topLeft = Offset(size.width * 0.15f, size.height * 0.2f),
                        size = Size(size.width * 0.7f, size.height * 0.6f),
                        cornerRadius = CornerRadius(24f, 24f),
                        style = Stroke(2f)
                    )
                    
                    // Beveled metal studs spacers
                    val studs = listOf(
                        Offset(size.width * 0.18f, size.height * 0.24f),
                        Offset(size.width * 0.82f, size.height * 0.24f),
                        Offset(size.width * 0.18f, size.height * 0.76f),
                        Offset(size.width * 0.82f, size.height * 0.76f)
                    )
                    studs.forEach { offset ->
                        drawCircle(Color.Black.copy(alpha = 0.4f), 14f, offset + Offset(0f, 4f))
                        drawCircle(Color(0xFF94A3B8), 12f, offset)
                        drawCircle(Color.White, 4f, offset - Offset(3f, 3f))
                    }
                }
                
                Box(modifier = Modifier.fillMaxSize(0.52f).align(Alignment.Center)) {
                    LogoModelRenderer(
                        version = selectedVersion,
                        glowIntensity = glowIntensity,
                        zoom = 0.68f * zoomFactor,
                        showGrid = false,
                        isArabic = isArabic
                    )
                }

                Text(
                    text = "CORPORATE HEADQUARTERS • FROSTED PLATINUM GLASS",
                    color = Color.LightGray.copy(alpha = 0.7f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                )
            }
            "CNC_BRASS" -> {
                // Brass/Gold cnc cut metal plaque
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Dark wooden desk or brushed graphite backdrop
                    drawRect(Color(0xFF0B0C0E), style = Fill)
                    
                    // Heavy brass plate base
                    val plaqueLeft = size.width * 0.12f
                    val plaqueTop = size.height * 0.15f
                    val plaqueWidth = size.width * 0.76f
                    val plaqueHeight = size.height * 0.7f
                    
                    // Drop shadow of plate
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.7f),
                        topLeft = Offset(plaqueLeft, plaqueTop + 10f),
                        size = Size(plaqueWidth, plaqueHeight),
                        cornerRadius = CornerRadius(8f, 8f),
                        style = Fill
                    )
                    
                    // Brushed Brass Gradient
                    val brassBrush = Brush.linearGradient(
                        listOf(Color(0xFF5C3A00), Color(0xFFC79200), Color(0xFF5C3A00), Color(0xFF8A5A00), Color(0xFFC79200)),
                        Offset(plaqueLeft, plaqueTop),
                        Offset(plaqueLeft + plaqueWidth, plaqueTop + plaqueHeight)
                    )
                    drawRoundRect(
                        brush = brassBrush,
                        topLeft = Offset(plaqueLeft, plaqueTop),
                        size = Size(plaqueWidth, plaqueHeight),
                        cornerRadius = CornerRadius(8f, 8f),
                        style = Fill
                    )
                    
                    // Double inner milled CNC lines
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.5f),
                        topLeft = Offset(plaqueLeft + 15f, plaqueTop + 15f),
                        size = Size(plaqueWidth - 30f, plaqueHeight - 30f),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Stroke(1.5f)
                    )
                    drawRoundRect(
                        color = Color(0xFFFFD700).copy(alpha = 0.3f),
                        topLeft = Offset(plaqueLeft + 18f, plaqueTop + 18f),
                        size = Size(plaqueWidth - 36f, plaqueHeight - 36f),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Stroke(1.5f)
                    )
                }

                Box(modifier = Modifier.fillMaxSize(0.55f).align(Alignment.Center)) {
                    LogoModelRenderer(
                        version = "DARK", // always rich premium metal contrast inside the gold plaque
                        glowIntensity = glowIntensity,
                        zoom = 0.7f * zoomFactor,
                        showGrid = false,
                        isArabic = isArabic
                    )
                }

                Text(
                    text = "HEAVY CAST BRASS PLAQUE • DEEP GRAVED CONTRAST",
                    color = Color.Black.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                )
            }
        }
    }
}
