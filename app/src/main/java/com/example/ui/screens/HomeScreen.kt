package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    val selectedApk by viewModel.selectedApk.collectAsState()
    val isDark = viewModel.isDarkMode.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("home_screen")
    ) {
        // Clean Header (Minimalist layout matching HTML spec)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // H logo
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFF2563EB), // blue-600
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "H",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = "HRGAF Testing",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 22.sp
                    )
                    Text(
                        text = "Hybrid RL-GA Framework",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Right option - Theme status circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White)
                    .clickable { viewModel.toggleDarkMode() }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selected APK / Current Project Status Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.setShowUploadApkScreen(true) }
                .testTag("upload_status_banner"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedApk != null) {
                    if (isDark) Color(0xFF14532D).copy(alpha = 0.4f) else Color(0xFFDCFCE7) // bg-green-100
                } else {
                    if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFE2E8F0).copy(alpha = 0.5f)
                }
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (selectedApk != null) {
                    if (isDark) Color(0xFF166534) else Color(0xFFBBF7D0) // border-green-200
                } else {
                    if (isDark) MaterialTheme.colorScheme.outlineVariant else Color(0xFFE2E8F0)
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (selectedApk != null) {
                                    if (isDark) Color(0xFF166534).copy(alpha = 0.6f) else Color(0xFFBBF7D0)
                                } else {
                                    if (isDark) Color(0xFF2D3748) else Color(0xFFF1F5F9)
                                },
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selectedApk != null) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (selectedApk != null) {
                                if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D)
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "CURRENT PROJECT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = if (selectedApk != null) {
                                if (isDark) Color(0xFF86EFAC) else Color(0xFF166534) // text-green-800
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedApk?.packageName ?: "com.example.research_app",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedApk != null) {
                                if (isDark) Color.White else Color(0xFF052E16) // text-green-950
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (selectedApk != null) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "84.2%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D) // text-green-700
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Clean Progress Bar
                        Box(
                            modifier = Modifier
                                .width(64.dp)
                                .height(6.dp)
                                .background(
                                    color = if (isDark) Color(0xFF166534) else Color(0xFFBBF7D0), // bg-green-200
                                    shape = RoundedCornerShape(3.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(54.dp) // 84%
                                    .background(
                                        color = if (isDark) Color(0xFF4ADE80) else Color(0xFF16A34A), // bg-green-600
                                        shape = RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "84.2%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF86EFAC).copy(alpha = 0.5f) else Color(0xFF166534).copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(6.dp)
                                .background(
                                    color = if (isDark) Color(0xFF2D3748) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(3.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(34.dp)
                                    .background(
                                        color = if (isDark) Color(0xFF4ADE80).copy(alpha = 0.5f) else Color(0xFF16A34A).copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Testing Modules",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Cards Grid (Minimalist design matching the HTML layout spec)
        val stdCardBg = if (isDark) Color(0xFF181E29) else Color.White
        val stdCardBorder = if (isDark) Color(0xFF2D3748) else Color(0xFFF1F5F9)
        val stdTextColor = if (isDark) Color.White else Color(0xFF0F172A)

        val uploadIconBg = if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.4f) else Color(0xFFEFF6FF) // blue-50
        val uploadIconTint = if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB) // blue-600

        val staticIconBg = if (isDark) Color(0xFF312E81).copy(alpha = 0.4f) else Color(0xFFEEF2F6) // indigo-50 variant
        val staticIconTint = if (isDark) Color(0xFF818CF8) else Color(0xFF4F46E5) // indigo-600

        val dynamicIconBg = if (isDark) Color(0xFF164E63).copy(alpha = 0.4f) else Color(0xFFECFEFF) // cyan-50
        val dynamicIconTint = if (isDark) Color(0xFF22D3EE) else Color(0xFF0891B2) // cyan-600

        val reportsIconBg = if (isDark) Color(0xFF881337).copy(alpha = 0.4f) else Color(0xFFFFF1F2) // rose-50
        val reportsIconTint = if (isDark) Color(0xFFF43F5E) else Color(0xFFE11D48) // rose-600

        // Highlight/solid cards
        val rlCardBg = if (isDark) Color(0xFF1D4ED8) else Color(0xFF2563EB) // blue-600
        val rlIconBg = Color.White.copy(alpha = 0.2f)
        val rlIconTint = Color.White
        val rlTextColor = Color.White

        val gaCardBg = if (isDark) Color(0xFF047857) else Color(0xFF16A34A) // green-600
        val gaIconBg = Color.White.copy(alpha = 0.2f)
        val gaIconTint = Color.White
        val gaTextColor = Color.White

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                MenuGridCard(
                    title = "Upload APK",
                    description = if (selectedApk != null) selectedApk!!.version else "v1.2.4 loaded",
                    icon = Icons.Default.CloudUpload,
                    containerColor = stdCardBg,
                    contentColor = stdTextColor,
                    iconBgColor = uploadIconBg,
                    iconColor = uploadIconTint,
                    borderColor = stdCardBorder,
                    testTag = "card_upload_apk",
                    onClick = { viewModel.setShowUploadApkScreen(true) }
                )
            }
            item {
                MenuGridCard(
                    title = "Static Analysis",
                    description = "14 Activities",
                    icon = Icons.Default.Assessment,
                    containerColor = stdCardBg,
                    contentColor = stdTextColor,
                    iconBgColor = staticIconBg,
                    iconColor = staticIconTint,
                    borderColor = stdCardBorder,
                    testTag = "card_static_analysis",
                    onClick = { viewModel.navigateToAnalysisSubTab(0) }
                )
            }
            item {
                MenuGridCard(
                    title = "Dynamic Analysis",
                    description = "Interactive Run",
                    icon = Icons.Default.PlayCircle,
                    containerColor = stdCardBg,
                    contentColor = stdTextColor,
                    iconBgColor = dynamicIconBg,
                    iconColor = dynamicIconTint,
                    borderColor = stdCardBorder,
                    testTag = "card_dynamic_analysis",
                    onClick = { viewModel.navigateToAnalysisSubTab(1) }
                )
            }
            item {
                MenuGridCard(
                    title = "RL Engine",
                    description = "Reward: 1.2k",
                    icon = Icons.Default.Lightbulb,
                    containerColor = rlCardBg,
                    contentColor = rlTextColor,
                    iconBgColor = rlIconBg,
                    iconColor = rlIconTint,
                    borderColor = null,
                    testTag = "card_rl_simulation",
                    onClick = { viewModel.navigateToAnalysisSubTab(2) }
                )
            }
            item {
                MenuGridCard(
                    title = "GA Optimizer",
                    description = "Gen 42 / Fit 0.98",
                    icon = Icons.Default.Storage,
                    containerColor = gaCardBg,
                    contentColor = gaTextColor,
                    iconBgColor = gaIconBg,
                    iconColor = gaIconTint,
                    borderColor = null,
                    testTag = "card_genetic_algorithm",
                    onClick = { viewModel.navigateToAnalysisSubTab(3) }
                )
            }
            item {
                MenuGridCard(
                    title = "Reports",
                    description = "Latest: 2h ago",
                    icon = Icons.Default.Assessment,
                    containerColor = stdCardBg,
                    contentColor = stdTextColor,
                    iconBgColor = reportsIconBg,
                    iconColor = reportsIconTint,
                    borderColor = stdCardBorder,
                    testTag = "card_reports",
                    onClick = { viewModel.setCurrentTab("reports") }
                )
            }
        }
    }
}

@Composable
fun MenuGridCard(
    title: String,
    description: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    iconBgColor: Color,
    iconColor: Color,
    borderColor: Color? = null,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(155.dp)
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(24.dp), // modern rounded-3xl
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = if (borderColor != null) androidx.compose.foundation.BorderStroke(1.dp, borderColor) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = iconBgColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = contentColor,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = contentColor.copy(alpha = 0.7f),
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
