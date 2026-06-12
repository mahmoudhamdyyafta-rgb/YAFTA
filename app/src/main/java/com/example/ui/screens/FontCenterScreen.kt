package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontCenterDialog(
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onFontApplied: (String) -> Unit,
    favoriteFonts: MutableList<String>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") } // ALL, ARABIC, ENGLISH, FAVORITES, RECOMMENDED
    var testPreviewText by remember { mutableStateOf("") }

    val arabicFonts = listOf("Cairo", "Tajawal", "IBM Plex Arabic", "DIN Next Arabic", "GE SS", "29LT Family")
    val englishFonts = listOf("Inter", "Poppins", "Montserrat", "Helvetica", "Avenir", "Roboto")
    val allFonts = arabicFonts + englishFonts

    val filteredFonts = allFonts.filter { fontName ->
        val matchesSearch = fontName.contains(searchQuery, ignoreCase = true)
        val matchesCat = when (selectedCategory) {
            "ARABIC" -> arabicFonts.contains(fontName)
            "ENGLISH" -> englishFonts.contains(fontName)
            "FAVORITES" -> favoriteFonts.contains(fontName)
            "RECOMMENDED" -> fontName == "Cairo" || fontName == "Poppins" || fontName == "DIN Next Arabic" || fontName == "Inter"
            else -> true
        }
        matchesSearch && matchesCat
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, Color(0xFF2E313D), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(0.85f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "مركز الخطوط الاحترافي 🔠" else "Professional Font Center 🔠",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFFFC107)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Dismiss", tint = Color.LightGray)
                    }
                }

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    placeholder = { Text(if (isArabic) "ابحث عن خط مميز..." else "Search premium typography...", fontSize = 12.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFC107),
                        unfocusedBorderColor = Color(0xFF2E313D)
                    )
                )

                // Category Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        Pair("ALL", if (isArabic) "الكل" else "All"),
                        Pair("ARABIC", if (isArabic) "عربي" else "Arabic"),
                        Pair("ENGLISH", if (isArabic) "إنجليزي" else "English"),
                        Pair("FAVORITES", if (isArabic) "المفضلة" else "Favs"),
                        Pair("RECOMMENDED", if (isArabic) "مقترح" else "AI Recs")
                    ).forEach { (id, label) ->
                        val isSel = selectedCategory == id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFFFFC107) else Color(0xFF2E313D))
                                .clickable { selectedCategory = id }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (isSel) Color(0xFF0B0B0F) else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Live Preview Input Box
                OutlinedTextField(
                    value = testPreviewText,
                    onValueChange = { testPreviewText = it },
                    placeholder = { Text(if (isArabic) "جرب الخط: اكتب نصاً للمعاينة الحية..." else "Type here to see live font rendering...", fontSize = 11.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF9800),
                        unfocusedBorderColor = Color(0xFF2E313D)
                    )
                )

                // Font list contents
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (filteredFonts.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text(if (isArabic) "لا توجد نتائج مطابقة لبحثك" else "No matching fonts found", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    } else {
                        items(filteredFonts) { fontName ->
                            val isFavorite = favoriteFonts.contains(fontName)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF22242B))
                                    .border(0.5.dp, Color(0xFF2E313D), RoundedCornerShape(8.dp))
                                    .clickable {
                                        onFontApplied(fontName)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(fontName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (testPreviewText.isNotBlank()) testPreviewText else (if (arabicFonts.contains(fontName)) "يافطة جديدة ممتازة" else "Creative Visual Studio"),
                                        color = Color(0xFFFFC107),
                                        fontSize = 12.sp,
                                        fontFamily = when (fontName) {
                                            "Cairo" -> FontFamily.SansSerif
                                            "Tajawal" -> FontFamily.Serif
                                            "IBM Plex Arabic" -> FontFamily.Default
                                            "DIN Next Arabic" -> FontFamily.SansSerif
                                            "GE SS" -> FontFamily.Serif
                                            "Inter" -> FontFamily.Default
                                            "Poppins" -> FontFamily.Default
                                            "Montserrat" -> FontFamily.Default
                                            "Helvetica" -> FontFamily.Default
                                            "Avenir" -> FontFamily.Serif
                                            "Roboto" -> FontFamily.Monospace
                                            else -> FontFamily.Default
                                        }
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (isFavorite) favoriteFonts.remove(fontName) else favoriteFonts.add(fontName)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite Toggle",
                                            tint = if (isFavorite) Color.Red else Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Text(
                                        text = if (isArabic) "تطبيق ⚡" else "Apply ⚡",
                                        color = Color(0xFFFF9800),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // AI suggestions tagline
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFF9800).copy(alpha = 0.08f))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                    Text(
                        text = if (isArabic) "نصيحة ذكية: استخدم Cairo للعناوين البارزة و Tajawal للنصوص الطويلة." else "AI Tip: Use Montserrat for headers & Inter for body texts.",
                        color = Color.LightGray,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FontRecognitionDialog(
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onFontSelected: (String) -> Unit
) {
    var isScanning by remember { mutableStateOf(false) }
    var stepResult by remember { mutableStateOf(0) } // 0 = upload trigger, 1 = scan in action, 2 = success details
    
    // Laser scanning animation offset
    val infiniteTransition = rememberInfiniteTransition(label = "Laser scanner animation scale")
    val sweepPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweep"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFFFFC107).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "كاشف الخطوط بالذكاء الاصطناعي 🔮" else "AI Font Scanner & Matcher 🔮",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC107),
                        fontSize = 15.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = Color.LightGray)
                    }
                }

                if (stepResult == 0) {
                    // Step 0: Upload prompt placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0B0B0F))
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                            .clickable {
                                isScanning = true
                                stepResult = 1
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, null, tint = Color(0xFFFF9800), modifier = Modifier.size(36.dp))
                            Text(
                                text = if (isArabic) "اضغط لمحاكاة التقاط أو رفع صورة اليافطة المكتوبة" else "Simulate snap or upload written banner text",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isArabic) "يقوم الذكاء الاصطناعي بالتعرف فورياً على فصيلة الخط ونسب التشابه!" else "AI matches glyph contours with integrated print engines instantly!",
                                color = Color.Gray,
                                fontSize = 9.sp
                            )
                        }
                    }
                } else if (stepResult == 1) {
                    // Step 1: Scanning radar animation
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(2600)
                        stepResult = 2
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(135.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black)
                                .border(1.dp, Color(0xFFFF9800), RoundedCornerShape(8.dp))
                        ) {
                            // Mock image outlines represent text font
                            Text(
                                text = "أقوى برجر كلاسيك",
                                color = Color.LightGray.copy(alpha = 0.4f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            
                            // Laser Sweep line drawing
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.04f)
                                    .align(Alignment.TopCenter)
                                    .offset(y = (sweepPosition * 125).dp)
                                    .background(Color(0xFFFFC107))
                            )
                        }
                        Text(if (isArabic) "جاري تحليل زوايا الحروف ومقارنة اللوغاريتمات..." else "Analyzing font contours & comparison grids...", color = Color.LightGray, fontSize = 11.sp)
                        LinearProgressIndicator(color = Color(0xFFFFC107), modifier = Modifier.fillMaxWidth())
                    }
                } else {
                    // Step 2: Recognition outcomes successfully found
                    Text(
                        text = if (isArabic) "تم فك شيفرة حروف الخط بنجاح! 🎉" else "Font Glyphs Decoded Successfully! 🎉",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        fontSize = 13.sp
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0B0F)),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF2E313D), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(if (isArabic) "الخط المكتشف:" else "Detected font:", color = Color.LightGray, fontSize = 11.sp)
                                Text("Cairo Bold (عربي محلي)", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(if (isArabic) "دقة ومعدل المطابقة:" else "Confidence Score:", color = Color.LightGray, fontSize = 11.sp)
                                Text("97.8% Confidence ✅", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Divider(color = Color.DarkGray)
                            Text(if (isArabic) "الخطوط البديلة والأقرب:" else "Suggest alternatives:", color = Color.LightGray, fontSize = 11.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Tajawal Bold", "DIN Next Arabic", "GE SS").forEach { alt ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF16181D), RoundedCornerShape(6.dp))
                                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(alt, color = Color.White, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onFontSelected("Cairo")
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isArabic) "تطبيق خط Cairo المكتشف" else "Apply Cairo", color = Color(0xFF0B0B0F), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = { stepResult = 0 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E313D)),
                            modifier = Modifier.weight(0.8f)
                        ) {
                            Text(if (isArabic) "إعادة مسح 🔄" else "Scan Again 🔄", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
