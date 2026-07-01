package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: MainViewModel
) {
    val selectedApk by viewModel.selectedApk.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val exportMessage by viewModel.exportMessage.collectAsState()
    
    var showReportDialog by remember { mutableStateOf(false) }

    // Hardcoded realistic analytics matching the selected APK
    val reportData = remember(selectedApk) {
        selectedApk?.let { apk ->
            when (apk.name) {
                "hrgaf_target_v1.apk" -> SimulatedReport(
                    coverage = "88.4%",
                    crashCount = "3 Crashes",
                    riskLevel = "Medium Risk",
                    riskColor = Color(0xFFF59E0B), // Orange
                    rlScore = "9,420 pts",
                    gaScore = "0.941 fitness"
                )
                "ecommerce_shopping_prod.apk" -> SimulatedReport(
                    coverage = "91.2%",
                    crashCount = "0 Crashes",
                    riskLevel = "Low Risk",
                    riskColor = Color(0xFF10B981), // Green
                    rlScore = "12,110 pts",
                    gaScore = "0.965 fitness"
                )
                "fitness_tracker_debug.apk" -> SimulatedReport(
                    coverage = "76.5%",
                    crashCount = "12 Crashes",
                    riskLevel = "High Risk",
                    riskColor = Color(0xFFEF4444), // Red
                    rlScore = "4,220 pts",
                    gaScore = "0.812 fitness"
                )
                else -> SimulatedReport(
                    coverage = "84.1%",
                    crashCount = "2 Crashes",
                    riskLevel = "Medium Risk",
                    riskColor = Color(0xFFF59E0B),
                    rlScore = "8,140 pts",
                    gaScore = "0.890 fitness"
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("reports_screen")
    ) {
        // App Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reports Hub",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }

        if (selectedApk == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Reports Available",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Analyze a target APK package to generate and view comparative hybrid coverage and crash telemetry reports here.",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.setShowUploadApkScreen(true) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Upload APK to Begin")
                        }
                    }
                }
            }
        } else {
            val apk = selectedApk!!
            val report = reportData!!

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Testing Performance Telemetry",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // APK Overview Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Android,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(apk.name, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(apk.packageName, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                    }
                }

                // Grid of core report metrics
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ReportIndicatorCard("GUI Code Coverage", report.coverage, Icons.Default.Percent, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                            ReportIndicatorCard("Crashes Found", report.crashCount, Icons.Default.BugReport, if (report.crashCount.startsWith("0")) MaterialTheme.colorScheme.secondary else Color(0xFFEF4444), Modifier.weight(1f))
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            ReportIndicatorCard("RL Exploratory Score", report.rlScore, Icons.Default.Stars, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                            ReportIndicatorCard("GA Optimization Fitness", report.gaScore, Icons.Default.TrendingUp, MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                        }

                        // Risk level wide card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = report.riskColor, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Application Risk Level", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(report.riskColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(report.riskLevel, fontWeight = FontWeight.ExtraBold, color = report.riskColor, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                // Action area
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Simulated HRGAF Run Complete", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(
                                "The combined Reinforcement Learning exploration agent and Genetic sequence optimizer has finished the assessment of all active Activities.",
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            if (isExporting) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Compiling test coverage reports...", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            } else {
                                Button(
                                    onClick = { 
                                        viewModel.triggerExport()
                                        showReportDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("btn_export_report")
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Export Report")
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Export Report Viewer Dialog
        if (showReportDialog && selectedApk != null) {
            val apk = selectedApk!!
            val report = reportData!!
            
            AlertDialog(
                onDismissRequest = { showReportDialog = false },
                title = {
                    Text(
                        text = "Exported Text Report",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Below is the generated plain text report. In a real environment, this is saved to device downloads.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        // Scrollable Report Area
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp)
                            ) {
                                item {
                                    Text(
                                        text = generateReportText(apk.name, apk.packageName, report),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showReportDialog = false },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
fun ReportIndicatorCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            
            Column {
                Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), lineHeight = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, lineHeight = 22.sp)
            }
        }
    }
}

data class SimulatedReport(
    val coverage: String,
    val crashCount: String,
    val riskLevel: String,
    val riskColor: Color,
    val rlScore: String,
    val gaScore: String
)

fun generateReportText(apkName: String, packageName: String, report: SimulatedReport): String {
    return """
===============================================
    HRGAF HYBRID GUI TESTING SYSTEM REPORT
===============================================
Generated At: 2026-07-01 12:49:31
Application Target: $apkName
Package Name: $packageName
-----------------------------------------------
TEST RESULTS SUMMARY:
- Target Code Coverage:   ${report.coverage}
- Total Crashes Detected: ${report.crashCount}
- Computed Risk Level:    ${report.riskLevel}
-----------------------------------------------
HYBRID ENGINE SCORES:
- Reinforcement Learning: ${report.rlScore}
- Genetic Optimization:  ${report.gaScore}
-----------------------------------------------
DETAILED ENGINE METRICS:
1. Reinforcement Learning:
   - Exploration Strategy: Deep Q-Network (DQN)
   - Reward Signal: Cumulative widget node visit frequency
   - Max Episodes: 1000
   - Terminal State: Static discovery convergence
   
2. Genetic Algorithm:
   - Population Size: 100 test chromosomes
   - Selection Mode: Tournament Selection (size=5)
   - Crossover operator: 1-point recombination
   - Mutation rate: 5% random action jitter
   - Optimizing Parameter: Sequence length minimizer

VERDICT:
The application target has been fully verified. 
No severe logic deadlocks were observed. Export 
completed successfully.
===============================================
""".trimIndent()
}
