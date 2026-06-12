package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.DesignViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DesignViewModel,
    isArabic: Boolean,
    onNavigateToProjects: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onStartNew: (String) -> Unit,
    onLoadTemplate: (String) -> Unit,
    onTriggerSettings: () -> Unit
) {
    val aiChatHistory by viewModel.aiChatHistory.collectAsState()
    var quickPromptText by remember { mutableStateOf("") }
    var chatInput by remember { mutableStateOf("") }
    var showBrandShowcase by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0F))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // --- TOP HERO WELCOME BLOCK ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(Color(0xFFFFC107).copy(alpha = 0.3f), Color(0xFFFF9800).copy(alpha = 0.1f))
                        ),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isArabic) "مرحباً إسلام Hamdy 👋" else "Welcome Islam Hamdy 👋",
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = if (isArabic) 
                            "جاهز لابتكار تصميم يافطة إعلانية استثنائي ومثالي للطباعة اليوم؟" 
                            else "Ready to engineer an outstanding print-optimized sign layout today?",
                        color = Color(0xFFF5F5F5),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFFF9800).copy(alpha = 0.15f),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "Pro Workspace Plan ⭐",
                                color = Color(0xFFFF9800),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(
                                text = "Cloud Backup Active ☁️",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // --- BRAND LOGO SYSTEM HERO CARD ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(Color(0xFFFF9800).copy(alpha = 0.4f), Color(0xFFFFC107).copy(alpha = 0.2f))
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { showBrandShowcase = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF0F0F14), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFFFC107).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "💎 يافطة للتصميم • شعار الـ 3D الفاخر" else "💎 YAFTA Design • Premium 3D Logo",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) 
                                "استكشف 7 نسخ مخصصة للشعار الفاخر للهوية البصرية والمطابع والويب!" 
                                else "Inspect the 7 custom premium logo variants for high-end digital & press branding.",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open Brand Room",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // --- 1. NEW DESIGN ASPECT RATIOS ---
            Text(
                text = if (isArabic) "🆕 ابدأ تصميماً جديداً بمقاس مثالي" else "🆕 Start New Layout with Perfect Size",
                color = Color(0xFFF5F5F5),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val ratioOptions = listOf(
                    Triple("16:9", if (isArabic) "يافطة عريضة 📺\n(16:9 Banner)" else "Wide Banner 📺\n(16:9)", "16:9"),
                    Triple("9:16", if (isArabic) "شاشة واقفة 📱\n(9:16 Vertical)" else "Vertical Sign 📱\n(9:16)", "9:16"),
                    Triple("1:1", if (isArabic) "ملصق مربع 🟦\n(1:1 Square)" else "Square Post 🟦\n(1:1)", "1:1"),
                    Triple("4:3", if (isArabic) "يافطة طريق 🖼️\n(4:3 Billboard)" else "Billboard 🖼️\n(4:3)", "4:3")
                )
                ratioOptions.forEach { (id, label, ratioValue) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF16181D))
                            .border(1.dp, Color(0xFF2E313D), RoundedCornerShape(12.dp))
                            .clickable { onStartNew(ratioValue) }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = Color(0xFFF5F5F5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // --- 2. FAST GENERATIVE PROMPT WIDGET ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2E313D), RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, "AI icon", tint = Color(0xFFFFC107))
                        Text(
                            text = if (isArabic) "توليد فوري بالذكاء الاصطناعي 🔮" else "Instant AI Banner Blueprint Generator 🔮",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    OutlinedTextField(
                        value = quickPromptText,
                        onValueChange = { quickPromptText = it },
                        placeholder = {
                            Text(
                                if (isArabic) "اكتب فكرتك مثل: لافتة تخفيضات مطعم مأكولات شعبية..." else "Enter your design goal, e.g., grand opening premium perfume store...",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFC107),
                            unfocusedBorderColor = Color(0xFF2E313D)
                        )
                    )
                    Button(
                        onClick = {
                            if (quickPromptText.isNotBlank()) {
                                viewModel.generateAiDesign(quickPromptText)
                                onStartNew("16:9") // direct transition
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isArabic) "ابتكار التصميم بالكامل والذهاب للستوديو 🚀" else "Construct Design & Enter Studio 🚀",
                            color = Color(0xFF16181D),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- 3. PREMIUM AD TEMPLATES LIBRARY ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "🗂️ قوالب إعلانية جاهزة احترافية" else "🗂️ Ready Ads Design Templates",
                    color = Color(0xFFF5F5F5),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isArabic) "عرض الكل" else "View All",
                    color = Color(0xFFFF9800),
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onNavigateToHelp() }
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val templatesList = listOf(
                    Pair("BURGER", if (isArabic) "برجر كلاسيك الشهير 🍔" else "Tasty Burger Joy 🍔"),
                    Pair("WEDDING", if (isArabic) "حفلات زفاف ملكية 💍" else "Royal Wedding Gala 💍"),
                    Pair("COFFEE", if (isArabic) "قهوة الصباح الفاخرة ☕" else "Morning Coffee Mood ☕"),
                    Pair("MINIMALIST", if (isArabic) "تصميم استوديو عصري ✨" else "Modern Minimal Tech ✨")
                )
                items(templatesList) { (templateId, name) ->
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF16181D))
                            .border(1.dp, Color(0xFF2E313D), RoundedCornerShape(12.dp))
                            .clickable { onLoadTemplate(templateId) }
                            .padding(12.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        // Simulating a rich preview with colored background bars
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (templateId) {
                                        "BURGER" -> Color(0xFFE05224)
                                        "WEDDING" -> Color(0xFFE5C8A6)
                                        "COFFEE" -> Color(0xFF5D4037)
                                        else -> Color(0xFF3F51B5)
                                    }
                                )
                        )
                        Text(
                            text = name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // --- 4. QUICK PORTAL BUTTONS ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Drafts folder
                Button(
                    onClick = onNavigateToProjects,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16181D)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0xFF2E313D), RoundedCornerShape(12.dp))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.FolderSpecial, "Projects Folder", tint = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(if (isArabic) "مسوداتي ومشاريعي 📁" else "My Saved Canvas 📁", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // AI Expert Partner
                Button(
                    onClick = onTriggerSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16181D)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0xFF2E313D), RoundedCornerShape(12.dp))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.Settings, "Help Hub", tint = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(if (isArabic) "إعدادات الأمان ومطابع ⚙️" else "Press Core Settings ⚙️", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // --- 5. PRINT QUALITY HUB SUMMARY ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, Color(0xFF2E313D), RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.AssignmentTurnedIn, "Print verification", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        Text(
                            text = if (isArabic) "مركز مراقبة جودة الطباعة المعتمد DPI / CMYK" else "Verified Printing Standards Monitor",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = if (isArabic) 
                            "كل اللافتات المنتجة تتم مراجعة قنواتها اللونية بدقة CMYK وتفحص حدود الهوامش لمنع الخدش وتلف التصميم للتسليم فوري للمطابع!"
                            else "All exports are fully checked for bleed safety thresholds, resolution DPI validations, and CMYK color mappings for zero-error professional print outputs.",
                        color = Color.LightGray,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        if (showBrandShowcase) {
            BrandShowcaseScreen(
                isArabic = isArabic,
                onClose = { showBrandShowcase = false }
            )
        }
    }
}
