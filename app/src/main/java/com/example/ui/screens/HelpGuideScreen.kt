package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpGuideScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("دليل المصمم الذكي 🔮", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "مرحباً بك في يافطة لتصميم الذكاء الاصطناعي!",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "منصة فنية ذكية تمكنك من توليد لافتات إعلانية وتصاميم واجهات احترافية ومزامنتها بملفات تفاعلية كاملة مع أدوات تعديل تصفية ومحاذاة الطبقات.",
                color = Color.LightGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            // Guide Cards
            GuideItemCard(
                icon = Icons.Default.AutoAwesome,
                title = "١. التوليد بالذكاء الاصطناعي",
                description = "اكتب فكرة اللافتة بشكل مفصل في خانة 'ذكاء يافطة'. يقترح الذكاء الاصطناعي الألوان والتدرجات المتناسقة، وينشئ نصوصاً وشعارات تسويقية قوية باللغتين العربية والإنجليزية، ليرتبها مباشرة كطبقات مستقلة على لوحة الرسم لتقوم بتعديلها."
            )

            GuideItemCard(
                icon = Icons.Default.Layers,
                title = "٢. إدارة وتعديل الطبقات",
                description = "انقر فوق أي عنصر في اللوحة لتحديده. يمكنك تدويره وتغيير درجة شفافيته أو لونه، ومضاعفته أو تحريكه للأعلى وللأسفل في تكديس الطبقات. استخدم شريط الخصائص المتميز لتعديل دقيق بنقرات بسيطة."
            )

            GuideItemCard(
                icon = Icons.Default.FilterDrama,
                title = "٣. مرشحات متقدمة وأبعاد مرنة",
                description = "غير نسبة الارتفاع إلى العرض لتناسب إنستغرام أو لافتات الشوارع العريضة. اختر خلفية صور فوتوغرافية جاهزة للمأكولات أو الحفلات لتوفير الجهد، مع الاستفادة التامة من تدرجات الألوان الفائقة."
            )

            GuideItemCard(
                icon = Icons.Default.SaveAlt,
                title = "٤. قراءة وحفظ وتصدير الملفات",
                description = "احفظ تصميماتك محلياً في معرض مشاريعك بمساحة تخزين SQLite السريعة لتستأنف عملك لاحقاً. عند التصدير، يدعم يافطة تنزيل الملفات بصيغ PNG، وJPEG للأجهزة المحمولة، وصيغ SVG للمتجهات الفنية المتطورة، وقوالب PSD للبرامج الرائدة كـ Photoshop."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("ابدأ التصميم الآن 🎨🚀", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GuideItemCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF0F0F12), RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = description,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
