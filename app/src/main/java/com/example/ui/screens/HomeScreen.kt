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
import androidx.compose.ui.graphics.Brush
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("home_screen")
    ) {
        // Hero / Welcome Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "HRGAF Dashboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Hybrid RL & Genetic Algorithm GUI Tester",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Upload Status Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.setShowUploadApkScreen(true) }
                .testTag("upload_status_banner"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedApk != null) {
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                } else {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (selectedApk != null) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (selectedApk != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selectedApk != null) "Target Loaded" else "System Awaiting APK",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = selectedApk?.name ?: "Click here to upload your target Android package (.apk) to initiate hybrid testing.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Testing Modules",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 6 Cards Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                MenuGridCard(
                    title = "Upload APK",
                    description = "Choose APK, configure permissions & SDKs.",
                    icon = Icons.Default.CloudUpload,
                    accentColor = MaterialTheme.colorScheme.primary,
                    testTag = "card_upload_apk",
                    onClick = { viewModel.setShowUploadApkScreen(true) }
                )
            }
            item {
                MenuGridCard(
                    title = "Static Analysis",
                    description = "Extract Activities, services & structures.",
                    icon = Icons.Default.Assessment,
                    accentColor = MaterialTheme.colorScheme.primary,
                    testTag = "card_static_analysis",
                    onClick = { viewModel.navigateToAnalysisSubTab(0) }
                )
            }
            item {
                MenuGridCard(
                    title = "Dynamic Analysis",
                    description = "Interactive scanner runtime GUI states.",
                    icon = Icons.Default.PlayCircle,
                    accentColor = MaterialTheme.colorScheme.secondary,
                    testTag = "card_dynamic_analysis",
                    onClick = { viewModel.navigateToAnalysisSubTab(1) }
                )
            }
            item {
                MenuGridCard(
                    title = "Reinforcement Learning",
                    description = "Agent rewards-based GUI explorer.",
                    icon = Icons.Default.Hub,
                    accentColor = MaterialTheme.colorScheme.secondary,
                    testTag = "card_rl_simulation",
                    onClick = { viewModel.navigateToAnalysisSubTab(2) }
                )
            }
            item {
                MenuGridCard(
                    title = "Genetic Algorithm",
                    description = "Fitness-optimized test cases generator.",
                    icon = Icons.Default.Science,
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    testTag = "card_genetic_algorithm",
                    onClick = { viewModel.navigateToAnalysisSubTab(3) }
                )
            }
            item {
                MenuGridCard(
                    title = "Reports Hub",
                    description = "Export generated coverage logs.",
                    icon = Icons.Default.Folder,
                    accentColor = MaterialTheme.colorScheme.primary,
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
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(155.dp)
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
