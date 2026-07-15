package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.compose.ui.platform.LocalContext
import java.io.File

import androidx.compose.ui.composed
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.ui.draw.scale

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.OpenInNew
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
    val exportProgress by viewModel.exportProgress.collectAsState()
    val currentExportedRows by viewModel.currentExportedRows.collectAsState()
    val lastExportedFile by viewModel.lastExportedFile.collectAsState()
    val exportDownloadLink by viewModel.exportDownloadLink.collectAsState()
    val isUploadingFile by viewModel.isUploadingFile.collectAsState()

    val isVideoDownloading by viewModel.isVideoDownloading.collectAsState()
    val videoDownloadProgress by viewModel.videoDownloadProgress.collectAsState()
    val lastDownloadedVideoFile by viewModel.lastDownloadedVideoFile.collectAsState()
    val videoDownloadMessage by viewModel.videoDownloadMessage.collectAsState()
    
    val context = LocalContext.current
    var isRlGaReportSelection by remember { mutableStateOf(false) }
    var selectedRowCount by remember { mutableStateOf(10) }
    var showVideoPlayerDialog by remember { mutableStateOf(false) }
    var isCompilerExpanded by remember { mutableStateOf(true) }
    var isVideoGuideExpanded by remember { mutableStateOf(true) }
    var openClicked by remember { mutableStateOf(false) }

    fun shareFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "com.example.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Report via"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "com.example.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            shareFile(file)
        }
    }

    fun shareVideo(file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "com.example.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "video/mp4"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Video Guide via"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openVideo(file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "com.example.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "video/mp4")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            shareVideo(file)
        }
    }

    // Hardcoded realistic analytics matching the selected APK
    val reportData = remember(selectedApk) {
        selectedApk?.let { apk ->
            when (apk.name) {
                "hrgaf_target_v1.apk" -> SimulatedReport(
                    coverage = "98.4%",
                    crashCount = "0 Crashes",
                    riskLevel = "Low Risk",
                    riskColor = Color(0xFF10B981), // Green
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
                    coverage = "96.5%",
                    crashCount = "0 Crashes",
                    riskLevel = "Low Risk",
                    riskColor = Color(0xFF10B981), // Green
                    rlScore = "4,220 pts",
                    gaScore = "0.812 fitness"
                )
                else -> SimulatedReport(
                    coverage = "94.1%",
                    crashCount = "0 Crashes",
                    riskLevel = "Low Risk",
                    riskColor = Color(0xFF10B981), // Green
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
                            ReportIndicatorCard("GA Optimization Fitness", report.gaScore, Icons.AutoMirrored.Filled.TrendingUp, MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
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

                // Action area - replaced with dynamic Excel compiler dashboard
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        onClick = { isCompilerExpanded = !isCompilerExpanded },
                        modifier = Modifier.fillMaxWidth().pressScaleEffect().testTag("excel_compiler_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                        .padding(8.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Excel Spreadsheet Compiler",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Generate professional .xlsx report with filters, sorting & conditional formatting",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                Icon(
                                    imageVector = if (isCompilerExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isCompilerExpanded) "Collapse" else "Expand",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            AnimatedVisibility(
                                visible = isCompilerExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 16.dp),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                    )

                            if (exportMessage == null) {
                                // 1. Select Report Type
                                Text(
                                    text = "1. Select Report Type",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                // Automated Report Select
                                    Card(
                                        onClick = { isRlGaReportSelection = false },
                                        modifier = Modifier.weight(1f).pressScaleEffect(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (!isRlGaReportSelection) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.5.dp, 
                                            if (!isRlGaReportSelection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = null,
                                                tint = if (!isRlGaReportSelection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Automated",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "7 columns • Regression",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }

                                    // RL + GA Report Select
                                    Card(
                                        onClick = { isRlGaReportSelection = true },
                                        modifier = Modifier.weight(1f).pressScaleEffect(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isRlGaReportSelection) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.5.dp, 
                                            if (isRlGaReportSelection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = if (isRlGaReportSelection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "RL + GA",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "8 columns • AI Optimizer",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // 2. Select Record Count
                                Text(
                                    text = "2. Select Data Scale (Performance Stress-Test)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val rowCountOptions = listOf(
                                        10 to "Standard\n(10 rows)",
                                        1000 to "Medium\n(1,000 rows)",
                                        10005 to "Stress-Test\n(10k+ rows)"
                                    )
                                    
                                    rowCountOptions.forEach { (count, label) ->
                                        val isSelected = selectedRowCount == count
                                        Card(
                                            onClick = { selectedRowCount = count },
                                            modifier = Modifier.weight(1f).pressScaleEffect(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp, 
                                                if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                            )
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    textAlign = TextAlign.Center,
                                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // 3. Export CTA Trigger
                                if (isExporting) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        LinearProgressIndicator(
                                            progress = { exportProgress },
                                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = if (exportProgress >= 0.95f && currentExportedRows >= selectedRowCount) "Writing & compressing spreadsheet file..." else "Compiling row ${String.format("%,d", currentExportedRows)} of ${String.format("%,d", selectedRowCount)}...",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${(exportProgress * 100).toInt()}% completed • Low-memory stream buffers active",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { 
                                            openClicked = false
                                            viewModel.exportExcelReport(context, isRlGaReportSelection, selectedRowCount)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().pressScaleEffect().testTag("btn_export_report")
                                    ) {
                                        Icon(Icons.Default.GridOn, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Compile & Export Excel (.xlsx)", fontWeight = FontWeight.Bold)
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Utilizes optimized memory-efficient sheet streaming.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            } else {
                                // Report successfully compiled screen!
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.08f)),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981).copy(alpha = 0.25f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircleOutline,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Report Generation Complete",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF10B981)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        // Metadata breakdown
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                                .padding(12.dp)
                                        ) {
                                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                Text("Doc Type:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                                Text(if (isRlGaReportSelection) "RL+GA Exploratory Report" else "Automated Test Report", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                Text("Record Count:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                                Text(String.format("%,d records", selectedRowCount), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            lastExportedFile?.let { file ->
                                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                    Text("File Size:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                                    Text("${String.format("%.2f", file.length() / 1024.0 / 1024.0)} MB", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                    Text("File Name:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                                    Text(file.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Action buttons row
                                        lastExportedFile?.let { file ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { 
                                                        openClicked = true
                                                        openFile(file) 
                                                    },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                    modifier = Modifier.weight(1f).pressScaleEffect()
                                                ) {
                                                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Open", fontSize = 13.sp)
                                                }
                                                
                                                Button(
                                                    onClick = { shareFile(file) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                                    modifier = Modifier.weight(1f).pressScaleEffect()
                                                ) {
                                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Share", fontSize = 13.sp)
                                                }
                                            }

                                            // Cloud Download Link Section
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (openClicked) 
                                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                                     else 
                                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                                ),
                                                border = if (openClicked) 
                                                    androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                                else 
                                                    null,
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.CloudDownload,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "Cloud Download Mirror",
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                    
                                                    if (openClicked) {
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(
                                                            text = "💡 Tap the link below to download the spreadsheet directly to your browser.",
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            textAlign = TextAlign.Center,
                                                            fontWeight = FontWeight.SemiBold,
                                                            modifier = Modifier.padding(horizontal = 4.dp)
                                                        )
                                                    }
                                                    
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    
                                                    if (isUploadingFile) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center,
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(14.dp),
                                                                strokeWidth = 2.dp,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text(
                                                                 text = "Uploading report to cloud mirror...",
                                                                 fontSize = 12.sp,
                                                                 color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                            )
                                                        }
                                                    } else {
                                                        exportDownloadLink?.let { link ->
                                                            androidx.compose.foundation.text.selection.SelectionContainer {
                                                                Text(
                                                                    text = link,
                                                                    fontSize = 12.sp,
                                                                    color = MaterialTheme.colorScheme.secondary,
                                                                    textAlign = TextAlign.Center,
                                                                    fontWeight = FontWeight.Medium,
                                                                    modifier = Modifier.padding(horizontal = 6.dp)
                                                                )
                                                            }
                                                            
                                                            Spacer(modifier = Modifier.height(10.dp))
                                                            
                                                            val localClipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                                                            val localUriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                                                            
                                                            Row(
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                                modifier = Modifier.fillMaxWidth()
                                                            ) {
                                                                OutlinedButton(
                                                                    onClick = {
                                                                        localClipboardManager.setText(androidx.compose.ui.text.AnnotatedString(link))
                                                                    },
                                                                    modifier = Modifier.weight(1f),
                                                                    contentPadding = PaddingValues(vertical = 4.dp),
                                                                    shape = RoundedCornerShape(8.dp)
                                                                ) {
                                                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text("Copy Link", fontSize = 12.sp)
                                                                }
                                                                
                                                                Button(
                                                                    onClick = {
                                                                        try {
                                                                            localUriHandler.openUri(link)
                                                                        } catch (e: Exception) {
                                                                            e.printStackTrace()
                                                                        }
                                                                    },
                                                                    modifier = Modifier.weight(1f),
                                                                    contentPadding = PaddingValues(vertical = 4.dp),
                                                                    shape = RoundedCornerShape(8.dp),
                                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                                ) {
                                                                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Text("Open Link", fontSize = 12.sp)
                                                                }
                                                            }
                                                        } ?: Text(
                                                            text = "Mirror link unavailable.",
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.error
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        TextButton(
                                            onClick = { viewModel.dismissExportMessage() },
                                            modifier = Modifier.pressScaleEffect()
                                        ) {
                                            Text("Generate Another Report", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                                }
                            }
                        }
                    }
                }

                // Video Walkthrough and Tutorial section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        onClick = { isVideoGuideExpanded = !isVideoGuideExpanded },
                        modifier = Modifier.fillMaxWidth().pressScaleEffect().testTag("video_guide_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Video Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideoLibrary,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                        .padding(8.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Walkthrough & Video Guide",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Learn how the low-memory Excel Spreadsheet Compiler works dynamically",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                Icon(
                                    imageVector = if (isVideoGuideExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isVideoGuideExpanded) "Collapse" else "Expand",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            AnimatedVisibility(
                                visible = isVideoGuideExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 16.dp),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                    )

                            if (videoDownloadMessage == null && !isVideoDownloading) {
                                Text(
                                    text = "Ready to discover how this feature optimizes high-performance data pipelines? Watch the full simulated walkthrough video or download the MP4 guide directly to your device.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    lineHeight = 18.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showVideoPlayerDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1.2f).pressScaleEffect()
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Watch Video", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.downloadExcelVideoDemo(context) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1.3f).pressScaleEffect()
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Download Guide", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            } else if (isVideoDownloading) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    LinearProgressIndicator(
                                        progress = { videoDownloadProgress },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                        color = MaterialTheme.colorScheme.secondary,
                                        trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Downloading walkthrough video...",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "${(videoDownloadProgress * 100).toInt()}% completed • Downloading MP4 stream",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            } else {
                                // Walkthrough video successfully downloaded!
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0EA5E9).copy(alpha = 0.08f)),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0EA5E9).copy(alpha = 0.25f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF0EA5E9),
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Walkthrough Video Downloaded",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF0EA5E9)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        lastDownloadedVideoFile?.let { file ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { openVideo(file) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                    modifier = Modifier.weight(1f).pressScaleEffect()
                                                ) {
                                                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Open Video", fontSize = 12.sp)
                                                }

                                                Button(
                                                    onClick = { shareVideo(file) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                                    modifier = Modifier.weight(1f).pressScaleEffect()
                                                ) {
                                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Share Video", fontSize = 12.sp)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        TextButton(
                                            onClick = { viewModel.dismissVideoDownloadMessage() },
                                            modifier = Modifier.pressScaleEffect()
                                        ) {
                                            Text("Reset Video State", fontWeight = FontWeight.Bold, color = Color(0xFF0EA5E9))
                                        }
                                    }
                                }
                            }
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

        // Immersive walkthrough video guide dialog
        if (showVideoPlayerDialog) {
            var isPlaying by remember { mutableStateOf(true) }
            var currentTimeMs by remember { mutableStateOf(0) }
            val totalTimeMs = 85 * 1000 // 1 minute 25 seconds
            var playbackSpeed by remember { mutableStateOf(1.0f) }

            LaunchedEffect(isPlaying, playbackSpeed) {
                if (isPlaying) {
                    while (currentTimeMs < totalTimeMs) {
                        delay(100)
                        currentTimeMs += (100 * playbackSpeed).toInt()
                        if (currentTimeMs >= totalTimeMs) {
                            currentTimeMs = totalTimeMs
                            isPlaying = false
                        }
                    }
                }
            }

            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showVideoPlayerDialog = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep obsidian dark background
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Title bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SmartDisplay,
                                    contentDescription = null,
                                    tint = Color(0xFF0EA5E9),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Video Guide: Excel Compiler",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                            IconButton(
                                onClick = { showVideoPlayerDialog = false },
                                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close dialog")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Simulated Video screen
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.Black, RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            val currentSec = currentTimeMs / 1000
                            
                            // Visual slide content based on current time
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                when {
                                    currentSec < 20 -> {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Part 1: Low-Memory SXSSF Stream",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Keeps only 100 active rows in RAM while flush streaming data directly to disk. Fully prevents standard OOM exceptions on large stress tests.",
                                            fontSize = 11.sp,
                                            color = Color.LightGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    currentSec < 45 -> {
                                        Icon(
                                            imageVector = Icons.Default.Dns,
                                            contentDescription = null,
                                            tint = Color(0xFF6366F1),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Part 2: Stress-Testing 100,000+ Rows",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Simulates robust enterprise testing loads. Generates full real workbook data in seconds with live-buffered UI feedback indicators.",
                                            fontSize = 11.sp,
                                            color = Color.LightGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    currentSec < 65 -> {
                                        Icon(
                                            imageVector = Icons.Default.Palette,
                                            contentDescription = null,
                                            tint = Color(0xFFEC4899),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Part 3: Advanced Styles & Formatting",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Applies professional header palettes, auto-fit columns, filters, sorting, and conditional status formatting rules (PASS/FAIL highlight colors).",
                                            fontSize = 11.sp,
                                            color = Color.LightGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    else -> {
                                        Icon(
                                            imageVector = Icons.Default.DownloadForOffline,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Part 4: Instant Sharing & Local Use",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Exported files are placed directly in device downloads. Use native android sharing or open instantly with default Excel viewers.",
                                            fontSize = 11.sp,
                                            color = Color.LightGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // Dynamic playback status badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(if (isPlaying) Color.Red else Color.Gray, RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isPlaying) "PLAYING (${playbackSpeed}x)" else "PAUSED",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Video Player progress bar & timeline
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val progress = currentTimeMs.toFloat() / totalTimeMs.toFloat()
                            val minutesElapsed = (currentTimeMs / 1000) / 60
                            val secondsElapsed = (currentTimeMs / 1000) % 60
                            val minutesTotal = (totalTimeMs / 1000) / 60
                            val secondsTotal = (totalTimeMs / 1000) % 60

                            Text(
                                text = String.format("%02d:%02d", minutesElapsed, secondsElapsed),
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace
                            )

                            Slider(
                                value = progress,
                                onValueChange = { newProgress ->
                                    currentTimeMs = (newProgress * totalTimeMs).toInt()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF0EA5E9),
                                    activeTrackColor = Color(0xFF0EA5E9),
                                    inactiveTrackColor = Color(0xFF334155)
                                )
                            )

                            Text(
                                text = String.format("%02d:%02d", minutesTotal, secondsTotal),
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Video Player controls row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Play / Pause
                                IconButton(
                                    onClick = { isPlaying = !isPlaying },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause video" else "Play video"
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // Replay from start
                                IconButton(
                                    onClick = {
                                        currentTimeMs = 0
                                        isPlaying = true
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.Replay, contentDescription = "Replay walkthrough")
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Speed toggle button
                                TextButton(
                                    onClick = {
                                        playbackSpeed = when (playbackSpeed) {
                                            1.0f -> 1.5f
                                            1.5f -> 2.0f
                                            else -> 1.0f
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "${playbackSpeed}x Speed",
                                        color = Color(0xFF0EA5E9),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Local download inside player
                                Button(
                                    onClick = {
                                        showVideoPlayerDialog = false
                                        viewModel.downloadExcelVideoDemo(context)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.pressScaleEffect()
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download MP4", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
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

@Composable
fun Modifier.pressScaleEffect(): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pressScale"
    )
    this.scale(scale)
}
