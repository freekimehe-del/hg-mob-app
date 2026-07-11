package com.example.util

import android.content.Context
import android.os.Environment
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

object ExcelExporter {

    private fun setBorders(style: CellStyle, borderColor: Short) {
        style.borderBottom = BorderStyle.THIN
        style.borderTop = BorderStyle.THIN
        style.borderLeft = BorderStyle.THIN
        style.borderRight = BorderStyle.THIN
        style.bottomBorderColor = borderColor
        style.topBorderColor = borderColor
        style.leftBorderColor = borderColor
        style.rightBorderColor = borderColor
    }

    private fun createCell(row: Row, colIndex: Int, value: String, style: CellStyle, maxChars: IntArray) {
        val cell = row.createCell(colIndex)
        cell.setCellValue(value)
        cell.cellStyle = style
        
        // Calculate max string length to optimize auto-fit column sizes (without reading row-level cells)
        val maxLineLength = value.lines().maxOfOrNull { it.length } ?: value.length
        maxChars[colIndex] = maxOf(maxChars[colIndex], maxLineLength)
    }

    fun generateAutomatedReport(
        context: Context, 
        recordCount: Int, 
        onProgress: (Int, Float) -> Unit
    ): File {
        // Create in-memory workbook (replaces SXSSFWorkbook to avoid AWT dependencies on Android)
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Automated Test Report")
        
        // Requirements: Freeze Header Row
        sheet.createFreezePane(0, 1)
        
        // Fonts
        val headerFont = workbook.createFont().apply {
            bold = true
            color = IndexedColors.WHITE.index
            fontHeightInPoints = 11.toShort()
        }
        val fontNormal = workbook.createFont().apply {
            fontHeightInPoints = 10.toShort()
        }
        
        // Header style
        val headerStyle = workbook.createCellStyle().apply {
            setFont(headerFont)
            fillForegroundColor = IndexedColors.ROYAL_BLUE.index // Professional corporate steel blue
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            wrapText = true
            setBorders(this, IndexedColors.GREY_50_PERCENT.index)
        }

        // Alternating Data Row Styles
        val styleEven = workbook.createCellStyle().apply {
            setFont(fontNormal)
            wrapText = true
            verticalAlignment = VerticalAlignment.TOP
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }
        val styleOdd = workbook.createCellStyle().apply {
            setFont(fontNormal)
            fillForegroundColor = IndexedColors.LIGHT_CORNFLOWER_BLUE.index // Premium subtle alternating light blue
            fillPattern = FillPatternType.SOLID_FOREGROUND
            wrapText = true
            verticalAlignment = VerticalAlignment.TOP
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }

        // Requirements: Conditional Formatting for Statuses
        // PASS = Green, FAIL = Red, WARNING = Orange, BLOCKED = Gray
        
        // PASS Style
        val passFont = workbook.createFont().apply {
            bold = true
            color = IndexedColors.DARK_GREEN.index
            fontHeightInPoints = 10.toShort()
        }
        val passStyle = workbook.createCellStyle().apply {
            setFont(passFont)
            fillForegroundColor = IndexedColors.LIGHT_GREEN.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }

        // FAIL Style
        val failFont = workbook.createFont().apply {
            bold = true
            color = IndexedColors.RED.index
            fontHeightInPoints = 10.toShort()
        }
        val failStyle = workbook.createCellStyle().apply {
            setFont(failFont)
            fillForegroundColor = IndexedColors.ROSE.index // Soft light red
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }

        // WARNING Style
        val warningFont = workbook.createFont().apply {
            bold = true
            color = IndexedColors.DARK_YELLOW.index
            fontHeightInPoints = 10.toShort()
        }
        val warningStyle = workbook.createCellStyle().apply {
            setFont(warningFont)
            fillForegroundColor = IndexedColors.LIGHT_YELLOW.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }

        // BLOCKED Style
        val blockedFont = workbook.createFont().apply {
            bold = true
            color = IndexedColors.GREY_80_PERCENT.index
            fontHeightInPoints = 10.toShort()
        }
        val blockedStyle = workbook.createCellStyle().apply {
            setFont(blockedFont)
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index // Soft light grey
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }

        val columns = listOf(
            "Test Case", "Test Title", "Test Summary", "Testing Steps", "Test Data", "Actual Result", "Status"
        )
        
        // Setup Header row
        val headerRow = sheet.createRow(0)
        headerRow.heightInPoints = 28f
        val maxChars = IntArray(columns.size) { 10 }
        
        for (colIndex in columns.indices) {
            val cell = headerRow.createCell(colIndex)
            cell.setCellValue(columns[colIndex])
            cell.cellStyle = headerStyle
            maxChars[colIndex] = maxOf(maxChars[colIndex], columns[colIndex].length)
        }

        // Dynamic content options
        val components = listOf("LoginActivity", "DashboardScreen", "SettingsPanel", "CheckoutFlow", "DeviceRegistration", "TelemetryService", "UserProfile")
        val triggers = listOf("tapping button", "inputting invalid email", "monitoring API delay", "simulating orientation change", "injecting crash signal")

        // Progress notification step logic
        val progressStep = maxOf(1, recordCount / 100)

        for (i in 1..recordCount) {
            val row = sheet.createRow(i)
            row.heightInPoints = 22f
            
            val isEven = i % 2 == 0
            val rowStyle = if (isEven) styleEven else styleOdd
            
            // 1. Test Case
            val tcCode = "TC-AUT-${String.format("%06d", i)}"
            createCell(row, 0, tcCode, rowStyle, maxChars)
            
            // 2. Test Title
            val component = components[i % components.size]
            val trigger = triggers[i % triggers.size]
            val title = "$component $trigger regression verify"
            createCell(row, 1, title, rowStyle, maxChars)
            
            // 3. Test Summary
            val summary = "Automated execution testing $component. Specifically verifying stability, view bounds, and memory spikes when $trigger in a simulated sandbox environment."
            createCell(row, 2, summary, rowStyle, maxChars)
            
            // 4. Testing Steps
            val steps = "1. Launch emulator context for $component.\n2. Trigger action: $trigger.\n3. Poll for visual refresh and check thread stability.\n4. Capture screenshots and code coverage percentages."
            createCell(row, 3, steps, rowStyle, maxChars)
            
            // 5. Test Data
            val testData = "context=$component, action=$trigger, run_id=${i + 1400}, scale_factor=${Random.nextFloat() * 10f}"
            createCell(row, 4, testData, rowStyle, maxChars)
            
            // 6. Actual Result & Status Setup
            val statusVal = if (i % 22 == 0) "FAIL" else if (i % 37 == 0) "WARNING" else if (i % 49 == 0) "BLOCKED" else "PASS"
            val actualResult = when (statusVal) {
                "PASS" -> "Actions executed without errors. UI state transitioned fully as expected. Code coverage checked."
                "FAIL" -> "Error: Assertion failure! UI element target not found. Emulator thread was blocked or app crashed."
                "WARNING" -> "Execution completed but latency of action execution exceeded threshold limit by ${Random.nextInt(500, 2000)}ms."
                else -> "Prerequisite login flow blocked. Active testing session skipped."
            }
            createCell(row, 5, actualResult, rowStyle, maxChars)
            
            // 7. Status Cell (Colored style)
            val statusCell = row.createCell(6)
            statusCell.setCellValue(statusVal)
            statusCell.cellStyle = when (statusVal) {
                "PASS" -> passStyle
                "FAIL" -> failStyle
                "WARNING" -> warningStyle
                else -> blockedStyle
            }
            maxChars[6] = maxOf(maxChars[6], statusVal.length)
            
            // Dispatch progress updates
            if (i % progressStep == 0 || i == recordCount) {
                onProgress(i, i.toFloat() / recordCount)
            }
        }

        // Requirements: Dynamic Width Setup (Auto-fit Columns)
        for (colIndex in columns.indices) {
            // Apply bounds of 12 (min) and 45 (max) to avoid giant column layouts
            val width = maxOf(12, minOf(maxChars[colIndex], 45)) * 256
            sheet.setColumnWidth(colIndex, width)
        }

        // Requirements: Enable Filters and Sorting
        if (recordCount > 0) {
            sheet.setAutoFilter(CellRangeAddress(0, recordCount, 0, columns.size - 1))
        }

        // Write the Workbook to a professional xlsx file inside app downloads
        val fileName = "Automated_Test_Report_${System.currentTimeMillis()}.xlsx"
        val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir
        val file = File(downloadDir, fileName)
        
        FileOutputStream(file).use { out ->
            workbook.write(out)
        }
        workbook.close() // Close the workbook to release resources
        return file
    }

    fun generateRlGaReport(
        context: Context, 
        recordCount: Int, 
        onProgress: (Int, Float) -> Unit
    ): File {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("RL+GA Exploratory Report")
        
        // Requirements: Freeze Header Row
        sheet.createFreezePane(0, 1)
        
        // Fonts
        val headerFont = workbook.createFont().apply {
            bold = true
            color = IndexedColors.WHITE.index
            fontHeightInPoints = 11.toShort()
        }
        val fontNormal = workbook.createFont().apply {
            fontHeightInPoints = 10.toShort()
        }
        
        // Header style
        val headerStyle = workbook.createCellStyle().apply {
            setFont(headerFont)
            fillForegroundColor = IndexedColors.DARK_BLUE.index // Distinct Dark Blue for AI agent reports
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            wrapText = true
            setBorders(this, IndexedColors.GREY_50_PERCENT.index)
        }

        // Alternating Data Row Styles
        val styleEven = workbook.createCellStyle().apply {
            setFont(fontNormal)
            wrapText = true
            verticalAlignment = VerticalAlignment.TOP
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }
        val styleOdd = workbook.createCellStyle().apply {
            setFont(fontNormal)
            fillForegroundColor = IndexedColors.LIGHT_TURQUOISE.index // Teal-blue shading for AI alternating row
            fillPattern = FillPatternType.SOLID_FOREGROUND
            wrapText = true
            verticalAlignment = VerticalAlignment.TOP
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }

        // Requirements: Conditional Formatting for Statuses
        // PASS = Green, FAIL = Red, WARNING = Orange, BLOCKED = Gray
        
        val passFont = workbook.createFont().apply { bold = true; color = IndexedColors.DARK_GREEN.index }
        val passStyle = workbook.createCellStyle().apply {
            setFont(passFont)
            fillForegroundColor = IndexedColors.LIGHT_GREEN.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }

        val failFont = workbook.createFont().apply { bold = true; color = IndexedColors.RED.index }
        val failStyle = workbook.createCellStyle().apply {
            setFont(failFont)
            fillForegroundColor = IndexedColors.ROSE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }

        val warningFont = workbook.createFont().apply { bold = true; color = IndexedColors.DARK_YELLOW.index }
        val warningStyle = workbook.createCellStyle().apply {
            setFont(warningFont)
            fillForegroundColor = IndexedColors.LIGHT_YELLOW.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }

        val blockedFont = workbook.createFont().apply { bold = true; color = IndexedColors.GREY_80_PERCENT.index }
        val blockedStyle = workbook.createCellStyle().apply {
            setFont(blockedFont)
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            setBorders(this, IndexedColors.GREY_25_PERCENT.index)
        }

        val columns = listOf(
            "Test Case", "Test Title", "Test Summary", "Testing Steps (Automated + AI Steps)", "Test Data", "Automated Expected Result", "RL + GA Expected Result", "Status"
        )
        
        // Setup Header row
        val headerRow = sheet.createRow(0)
        headerRow.heightInPoints = 28f
        val maxChars = IntArray(columns.size) { 10 }
        
        for (colIndex in columns.indices) {
            val cell = headerRow.createCell(colIndex)
            cell.setCellValue(columns[colIndex])
            cell.cellStyle = headerStyle
            maxChars[colIndex] = maxOf(maxChars[colIndex], columns[colIndex].length)
        }

        // Dynamic content options
        val exploratoryTargets = listOf("Deep Path Expansion", "State Transition Minimization", "Action Sequence Evolutionary Search", "Edge-case Widget Stimulation", "Concurrency Timing Optimizer")
        val networkContexts = listOf("seed_run=7790, depth=10", "episodes=1000, mutation=0.05", "crossover=0.85, population=100", "epsilon_decay=0.995, alpha=0.1", "fitness_mode=length_minimizer")

        val progressStep = maxOf(1, recordCount / 100)

        for (i in 1..recordCount) {
            val row = sheet.createRow(i)
            row.heightInPoints = 24f
            
            val isEven = i % 2 == 0
            val rowStyle = if (isEven) styleEven else styleOdd
            
            // 1. Test Case
            val tcCode = "TC-RLGA-${String.format("%06d", i)}"
            createCell(row, 0, tcCode, rowStyle, maxChars)
            
            // 2. Test Title
            val target = exploratoryTargets[i % exploratoryTargets.size]
            val title = "$target deep sequence simulation $i"
            createCell(row, 1, title, rowStyle, maxChars)
            
            // 3. Test Summary
            val summary = "Reinforcement learning exploration of the system layout. Genetic sequence mutations applied to discover deep logic states and remove redundant UI traversal loops for $target."
            createCell(row, 2, summary, rowStyle, maxChars)
            
            // 4. Testing Steps (Automated + AI Steps)
            val steps = "[Auto] Pre-populate session cache -> [RL Agent] Explore screen layout recursively -> [RL Agent] Click component matrix index $i -> [GA Engine] Mutate and recombine sequence to minimize path length -> [Auto] Perform validation check."
            createCell(row, 3, steps, rowStyle, maxChars)
            
            // 5. Test Data
            val testData = "${networkContexts[i % networkContexts.size]}, generation=${i / 50}, current_episode=${i * 10}"
            createCell(row, 4, testData, rowStyle, maxChars)
            
            // 6. Automated Expected Result
            val autoExpected = "Standard automation performs linear traversal of 5 default screens. No deep exploration branches visited."
            createCell(row, 5, autoExpected, rowStyle, maxChars)
            
            // 7. RL + GA Expected Result
            val statusVal = if (i % 25 == 0) "FAIL" else if (i % 41 == 0) "WARNING" else if (i % 53 == 0) "BLOCKED" else "PASS"
            val rlgaExpected = when (statusVal) {
                "PASS" -> "AI deep exploration uncovered ${Random.nextInt(10, 35)} additional dynamic nodes. GA minimized verification actions from 45 down to ${Random.nextInt(4, 9)} actions successfully."
                "FAIL" -> "ERROR! RL agent triggered a deep race condition on child window which caused immediate UI freeze and system thread block."
                "WARNING" -> "Exploration complete but memory profile showed slight leak over 1000 continuous action cycles."
                else -> "Simulation environment crashed at episode ${i * 4} due to unhandled container exception. Blocked further evolutionary sweeps."
            }
            createCell(row, 6, rlgaExpected, rowStyle, maxChars)
            
            // 8. Status Cell
            val statusCell = row.createCell(7)
            statusCell.setCellValue(statusVal)
            statusCell.cellStyle = when (statusVal) {
                "PASS" -> passStyle
                "FAIL" -> failStyle
                "WARNING" -> warningStyle
                else -> blockedStyle
            }
            maxChars[7] = maxOf(maxChars[7], statusVal.length)
            
            // Dispatch progress updates
            if (i % progressStep == 0 || i == recordCount) {
                onProgress(i, i.toFloat() / recordCount)
            }
        }

        // Requirements: Dynamic Width Setup (Auto-fit Columns)
        for (colIndex in columns.indices) {
            val width = maxOf(12, minOf(maxChars[colIndex], 45)) * 256
            sheet.setColumnWidth(colIndex, width)
        }

        // Requirements: Enable Filters and Sorting
        if (recordCount > 0) {
            sheet.setAutoFilter(CellRangeAddress(0, recordCount, 0, columns.size - 1))
        }

        // Save Workbook
        val fileName = "RLGA_Optimization_Report_${System.currentTimeMillis()}.xlsx"
        val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir
        val file = File(downloadDir, fileName)
        
        FileOutputStream(file).use { out ->
            workbook.write(out)
        }
        workbook.close()
        return file
    }
}
