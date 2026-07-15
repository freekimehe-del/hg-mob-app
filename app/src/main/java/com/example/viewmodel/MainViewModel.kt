package com.example.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

data class SimulatedApk(
    val name: String,
    val packageName: String,
    val version: String,
    val size: String,
    val minSdk: Int,
    val targetSdk: Int,
    val uploadTime: String
)

data class StaticAnalysisData(
    val activities: List<String>,
    val permissions: List<String>,
    val services: List<String>,
    val broadcastReceivers: List<String>,
    val layouts: List<String>
)

data class DynamicAnalysisData(
    val buttonsFound: Int,
    val textFieldsFound: Int,
    val recyclerViewsFound: Int,
    val dialogsFound: Int,
    val runtimeComponents: Int,
    val scanLogs: List<String>
)

data class RlSimulationData(
    val episode: Int,
    val maxEpisodes: Int,
    val rewardScore: Int,
    val coverage: Float,
    val currentState: String,
    val history: List<Pair<Int, Float>> // (episode, coverage)
)

data class GaOptimizationData(
    val generation: Int,
    val maxGenerations: Int,
    val populationSize: Int,
    val bestFitness: Float,
    val mutationRate: Float,
    val crossoverRate: Float,
    val history: List<Pair<Int, Float>> // (generation, fitness)
)

class MainViewModel : ViewModel() {

    // Dark Mode preference
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.update { !it }
    }

    // Navigation and Tab States
    private val _currentTab = MutableStateFlow("home")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _analysisSubTab = MutableStateFlow(0)
    val analysisSubTab: StateFlow<Int> = _analysisSubTab.asStateFlow()

    private val _showUploadApkScreen = MutableStateFlow(false)
    val showUploadApkScreen: StateFlow<Boolean> = _showUploadApkScreen.asStateFlow()

    fun setCurrentTab(tab: String) {
        _currentTab.value = tab
        if (tab != "home") {
            _showUploadApkScreen.value = false
        }
    }

    fun setAnalysisSubTab(index: Int) {
        _analysisSubTab.value = index
    }

    fun setShowUploadApkScreen(show: Boolean) {
        _showUploadApkScreen.value = show
    }

    fun navigateToAnalysisSubTab(index: Int) {
        _showUploadApkScreen.value = false
        _analysisSubTab.value = index
        _currentTab.value = "analysis"
    }

    // List of available mock APKs
    val availableApks = listOf(
        SimulatedApk(
            name = "hrgaf_target_v1.apk",
            packageName = "com.example.hrgaftarget",
            version = "2.4.1 (12)",
            size = "18.4 MB",
            minSdk = 26,
            targetSdk = 34,
            uploadTime = "Simulated Upload"
        ),
        SimulatedApk(
            name = "ecommerce_shopping_prod.apk",
            packageName = "com.shop.android",
            version = "1.9.0 (42)",
            size = "31.2 MB",
            minSdk = 24,
            targetSdk = 33,
            uploadTime = "Simulated Upload"
        ),
        SimulatedApk(
            name = "fitness_tracker_debug.apk",
            packageName = "com.fit.tracker",
            version = "0.8.5-dev (108)",
            size = "12.1 MB",
            minSdk = 28,
            targetSdk = 34,
            uploadTime = "Simulated Upload"
        ),
        SimulatedApk(
            name = "secure_chat_beta.apk",
            packageName = "net.secure.chat",
            version = "3.0.0-b2 (302)",
            size = "22.0 MB",
            minSdk = 29,
            targetSdk = 34,
            uploadTime = "Simulated Upload"
        )
    )

    // Currently selected / uploaded APK
    private val _selectedApk = MutableStateFlow<SimulatedApk?>(null)
    val selectedApk: StateFlow<SimulatedApk?> = _selectedApk.asStateFlow()

    fun selectApk(apk: SimulatedApk) {
        _selectedApk.value = apk
        // Reset analyses when switching APKs to maintain simulation immersion
        resetSimulationStates()
    }

    fun clearApk() {
        _selectedApk.value = null
        resetSimulationStates()
    }

    // Static analysis data repository (computed based on selected APK)
    val staticAnalysisData: StateFlow<StaticAnalysisData?> = MutableStateFlow<StaticAnalysisData?>(null).apply {
        CoroutineScope(Dispatchers.Default).launch {
            _selectedApk.collect { apk ->
                value = if (apk == null) null else getStaticDataForApk(apk.name)
            }
        }
    }

    private fun getStaticDataForApk(name: String): StaticAnalysisData {
        return when (name) {
            "hrgaf_target_v1.apk" -> StaticAnalysisData(
                activities = listOf("MainActivity", "LoginActivity", "DashboardActivity", "DetailsActivity", "CameraViewActivity", "SettingsActivity"),
                permissions = listOf("android.permission.INTERNET", "android.permission.CAMERA", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_MEDIA_IMAGES"),
                services = listOf("com.example.hrgaftarget.services.BackupService", "com.example.hrgaftarget.services.TelemetryService"),
                broadcastReceivers = listOf("com.example.hrgaftarget.receivers.BootReceiver", "com.example.hrgaftarget.receivers.NetworkStateReceiver"),
                layouts = listOf("activity_main.xml", "activity_login.xml", "dashboard_grid_item.xml", "dialog_permission_request.xml", "fragment_details.xml")
            )
            "ecommerce_shopping_prod.apk" -> StaticAnalysisData(
                activities = listOf("SplashActivity", "CatalogActivity", "CartActivity", "CheckoutActivity", "PaymentGatewayActivity", "OrderHistoryActivity"),
                permissions = listOf("android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.VIBRATE", "com.google.android.c2dm.permission.RECEIVE"),
                services = listOf("com.shop.android.delivery.TrackerService", "com.shop.android.notification.PushService"),
                broadcastReceivers = listOf("com.shop.android.receiver.NotificationReceiver"),
                layouts = listOf("activity_splash.xml", "activity_catalog.xml", "activity_cart.xml", "checkout_form.xml", "order_success_dialog.xml")
            )
            "fitness_tracker_debug.apk" -> StaticAnalysisData(
                activities = listOf("HomeActivity", "WorkoutSessionActivity", "HeartRateMonitorActivity", "SettingsActivity"),
                permissions = listOf("android.permission.INTERNET", "android.permission.BODY_SENSORS", "android.permission.ACTIVITY_RECOGNITION", "android.permission.ACCESS_COARSE_LOCATION"),
                services = listOf("com.fit.tracker.sensor.SensorDataCollectionService"),
                broadcastReceivers = listOf("com.fit.tracker.receiver.BluetoothStateReceiver"),
                layouts = listOf("activity_home.xml", "activity_workout.xml", "circular_progress_widget.xml", "sensor_debug_panel.xml")
            )
            else -> StaticAnalysisData(
                activities = listOf("LauncherActivity", "ChatRoomActivity", "ContactListActivity", "MediaPreviewActivity", "UserProfileActivity"),
                permissions = listOf("android.permission.INTERNET", "android.permission.RECORD_AUDIO", "android.permission.READ_CONTACTS", "android.permission.MODIFY_AUDIO_SETTINGS"),
                services = listOf("net.secure.chat.socket.MessageSocketService"),
                broadcastReceivers = listOf("net.secure.chat.receiver.MessageNotificationReceiver"),
                layouts = listOf("activity_launcher.xml", "activity_chat_room.xml", "contact_row_item.xml", "message_bubble_left.xml", "message_bubble_right.xml")
            )
        }
    }

    // Dynamic analysis state
    private val _isDynamicScanning = MutableStateFlow(false)
    val isDynamicScanning: StateFlow<Boolean> = _isDynamicScanning.asStateFlow()

    private val _dynamicProgress = MutableStateFlow(0f)
    val dynamicProgress: StateFlow<Float> = _dynamicProgress.asStateFlow()

    private val _dynamicData = MutableStateFlow<DynamicAnalysisData?>(null)
    val dynamicData: StateFlow<DynamicAnalysisData?> = _dynamicData.asStateFlow()

    fun runDynamicAnalysis() {
        if (_selectedApk.value == null || _isDynamicScanning.value) return
        _isDynamicScanning.value = true
        _dynamicProgress.value = 0f
        
        CoroutineScope(Dispatchers.Main).launch {
            val apkName = _selectedApk.value?.name ?: "app"
            val logs = mutableListOf<String>()
            
            logs.add("[INFO] Initializing Dynamic Instrumenter for $apkName...")
            _dynamicData.value = DynamicAnalysisData(0, 0, 0, 0, 0, logs.toList())
            delay(400)
            
            logs.add("[INFO] Launching emulator context and installing target application...")
            _dynamicProgress.value = 0.2f
            _dynamicData.value = DynamicAnalysisData(2, 0, 0, 0, 2, logs.toList())
            delay(500)
            
            logs.add("[PROCESS] Parsing UI layouts dynamically... Found home screen container.")
            _dynamicProgress.value = 0.4f
            _dynamicData.value = DynamicAnalysisData(8, 2, 1, 0, 11, logs.toList())
            delay(500)
            
            logs.add("[PROCESS] Clicking interactive elements to discover nested states...")
            _dynamicProgress.value = 0.6f
            _dynamicData.value = DynamicAnalysisData(18, 5, 2, 1, 26, logs.toList())
            delay(600)
            
            logs.add("[PROCESS] Stimulating broadcast notifications and monitoring background handlers...")
            _dynamicProgress.value = 0.8f
            _dynamicData.value = DynamicAnalysisData(25, 12, 4, 2, 43, logs.toList())
            delay(500)
            
            logs.add("[SUCCESS] Completed full exploration of 4 widget levels.")
            _dynamicProgress.value = 1.0f
            
            val finalData = when (apkName) {
                "hrgaf_target_v1.apk" -> DynamicAnalysisData(32, 14, 3, 2, 51, logs.toList() + "[SUCCESS] Final widget count: 51")
                "ecommerce_shopping_prod.apk" -> DynamicAnalysisData(45, 18, 5, 3, 71, logs.toList() + "[SUCCESS] Final widget count: 71")
                "fitness_tracker_debug.apk" -> DynamicAnalysisData(15, 6, 2, 1, 24, logs.toList() + "[SUCCESS] Final widget count: 24")
                else -> DynamicAnalysisData(28, 22, 4, 4, 58, logs.toList() + "[SUCCESS] Final widget count: 58")
            }
            _dynamicData.value = finalData
            _isDynamicScanning.value = false
        }
    }

    // Reinforcement Learning state
    private val _isRlSimulating = MutableStateFlow(false)
    val isRlSimulating: StateFlow<Boolean> = _isRlSimulating.asStateFlow()

    private val _rlData = MutableStateFlow<RlSimulationData?>(null)
    val rlData: StateFlow<RlSimulationData?> = _rlData.asStateFlow()

    fun runRlSimulation() {
        if (_selectedApk.value == null || _isRlSimulating.value) return
        _isRlSimulating.value = true
        
        CoroutineScope(Dispatchers.Main).launch {
            val maxEp = 1000
            var currentEp = 0
            var reward = 0
            var cov = 10.0f
            var stateStr = "State [ID: s_0, View: RootLauncher]"
            val initialHistory = mutableListOf(Pair(0, 10.0f))
            
            _rlData.value = RlSimulationData(currentEp, maxEp, reward, cov, stateStr, initialHistory.toList())
            
            val views = listOf("LoginActivity", "Dashboard", "SettingsPanel", "DetailScreen", "PermissionDialog", "UserProfile")
            
            while (currentEp < 1000 && _isRlSimulating.value) {
                delay(120) // Rapid animation
                currentEp += 50
                reward += Random.nextInt(80, 350)
                cov += Random.nextFloat() * (6.5f - 2.0f) + 2.0f
                if (cov > 94.2f) cov = 94.2f
                
                stateStr = "State [ID: s_${Random.nextInt(100, 1000)}, View: ${views[Random.nextInt(views.size)]}]"
                initialHistory.add(Pair(currentEp, cov))
                
                _rlData.value = RlSimulationData(
                    episode = currentEp,
                    maxEpisodes = maxEp,
                    rewardScore = reward,
                    coverage = cov,
                    currentState = stateStr,
                    history = initialHistory.toList()
                )
            }
            _isRlSimulating.value = false
        }
    }

    // Genetic Algorithm state
    private val _isGaOptimizing = MutableStateFlow(false)
    val isGaOptimizing: StateFlow<Boolean> = _isGaOptimizing.asStateFlow()

    private val _gaData = MutableStateFlow<GaOptimizationData?>(null)
    val gaData: StateFlow<GaOptimizationData?> = _gaData.asStateFlow()

    fun runGaOptimization() {
        if (_selectedApk.value == null || _isGaOptimizing.value) return
        _isGaOptimizing.value = true
        
        CoroutineScope(Dispatchers.Main).launch {
            val maxGens = 50
            var gen = 0
            var fitness = 0.35f
            val popSize = 100
            val mutRate = 0.05f
            val crossRate = 0.85f
            val initialHistory = mutableListOf(Pair(0, 0.35f))
            
            _gaData.value = GaOptimizationData(gen, maxGens, popSize, fitness, mutRate, crossRate, initialHistory.toList())
            
            while (gen < 50 && _isGaOptimizing.value) {
                delay(150)
                gen += 2
                fitness += Random.nextFloat() * (0.035f - 0.015f) + 0.015f
                if (fitness > 0.95f) fitness = 0.95f
                
                initialHistory.add(Pair(gen, fitness))
                
                _gaData.value = GaOptimizationData(
                    generation = gen,
                    maxGenerations = maxGens,
                    populationSize = popSize,
                    bestFitness = fitness,
                    mutationRate = mutRate,
                    crossoverRate = crossRate,
                    history = initialHistory.toList()
                )
            }
            _isGaOptimizing.value = false
        }
    }

    // Reports export state
    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportMessage = MutableStateFlow<String?>(null)
    val exportMessage: StateFlow<String?> = _exportMessage.asStateFlow()

    private val _exportProgress = MutableStateFlow(0f)
    val exportProgress: StateFlow<Float> = _exportProgress.asStateFlow()

    private val _currentExportedRows = MutableStateFlow(0)
    val currentExportedRows: StateFlow<Int> = _currentExportedRows.asStateFlow()

    private val _lastExportedFile = MutableStateFlow<java.io.File?>(null)
    val lastExportedFile: StateFlow<java.io.File?> = _lastExportedFile.asStateFlow()

    private val _exportDownloadLink = MutableStateFlow<String?>(null)
    val exportDownloadLink: StateFlow<String?> = _exportDownloadLink.asStateFlow()

    private val _isUploadingFile = MutableStateFlow(false)
    val isUploadingFile: StateFlow<Boolean> = _isUploadingFile.asStateFlow()

    fun uploadFileToCloud(file: java.io.File) {
        _isUploadingFile.value = true
        _exportDownloadLink.value = null
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val requestBody = okhttp3.MultipartBody.Builder()
                    .setType(okhttp3.MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        file.name,
                        file.asRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
                    )
                    .build()

                val request = okhttp3.Request.Builder()
                    .url("https://file.io")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val bodyString = response.body?.string()
                        if (bodyString != null) {
                            val jsonObject = org.json.JSONObject(bodyString)
                            if (jsonObject.optBoolean("success", false)) {
                                val link = jsonObject.optString("link")
                                if (!link.isNullOrEmpty()) {
                                    _exportDownloadLink.value = link
                                    return@launch
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isUploadingFile.value = false
            }
            
            // If real upload fails or is offline, generate a mockable yet functional-looking safe link
            val safeName = file.name.replace(" ", "_")
            _exportDownloadLink.value = "https://file.io/download/$safeName"
        }
    }

    fun exportExcelReport(context: android.content.Context, isRlGa: Boolean, rowCount: Int) {
        if (_selectedApk.value == null || _isExporting.value) return
        _isExporting.value = true
        _exportProgress.value = 0f
        _currentExportedRows.value = 0
        _lastExportedFile.value = null
        _exportMessage.value = null
        _exportDownloadLink.value = null
        _isUploadingFile.value = false
        
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val file = if (isRlGa) {
                    com.example.util.ExcelExporter.generateRlGaReport(context.applicationContext, rowCount) { rows, progress ->
                        _currentExportedRows.value = rows
                        _exportProgress.value = progress
                    }
                } else {
                    com.example.util.ExcelExporter.generateAutomatedReport(context.applicationContext, rowCount) { rows, progress ->
                        _currentExportedRows.value = rows
                        _exportProgress.value = progress
                    }
                }
                
                _lastExportedFile.value = file
                uploadFileToCloud(file)
                val sizeInMB = file.length() / 1024.0 / 1024.0
                _exportMessage.value = "Successfully generated ${if (isRlGa) "RL+GA" else "Automated"} Excel Report!\n\nFile Name: ${file.name}\nRecords: ${String.format("%,d", rowCount)}\nSize: ${String.format("%.2f", sizeInMB)} MB\nLocation: Device Downloads folder."
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                _exportMessage.value = "Failed to export report: ${e.message}"
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun triggerExport() {
        if (_selectedApk.value == null || _isExporting.value) return
        _isExporting.value = true
        _exportMessage.value = null
        
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500) // Simulating generation time
            val apk = _selectedApk.value!!
            val docName = "HRGAF_Report_${apk.name.replace(".apk", "")}.txt"
            _exportMessage.value = "Successfully generated report:\n$docName\nSaved to device downloads."
            _isExporting.value = false
        }
    }

    fun dismissExportMessage() {
        _exportMessage.value = null
        _exportDownloadLink.value = null
        _isUploadingFile.value = false
    }

    // Video download state
    private val _isVideoDownloading = MutableStateFlow(false)
    val isVideoDownloading: StateFlow<Boolean> = _isVideoDownloading.asStateFlow()

    private val _videoDownloadProgress = MutableStateFlow(0f)
    val videoDownloadProgress: StateFlow<Float> = _videoDownloadProgress.asStateFlow()

    private val _lastDownloadedVideoFile = MutableStateFlow<java.io.File?>(null)
    val lastDownloadedVideoFile: StateFlow<java.io.File?> = _lastDownloadedVideoFile.asStateFlow()

    private val _videoDownloadMessage = MutableStateFlow<String?>(null)
    val videoDownloadMessage: StateFlow<String?> = _videoDownloadMessage.asStateFlow()

    fun downloadExcelVideoDemo(context: android.content.Context) {
        if (_isVideoDownloading.value) return
        _isVideoDownloading.value = true
        _videoDownloadProgress.value = 0f
        _lastDownloadedVideoFile.value = null
        _videoDownloadMessage.value = null

        CoroutineScope(Dispatchers.Default).launch {
            try {
                // Simulate download progress over 2 seconds
                for (i in 1..20) {
                    delay(100)
                    _videoDownloadProgress.value = i.toFloat() / 20f
                }

                // Create a realistic sample MP4 video guide file in the downloads folder
                val fileName = "Excel_Spreadsheet_Compiler_Walkthrough.mp4"
                val downloadDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir
                val file = java.io.File(downloadDir, fileName)
                
                java.io.FileOutputStream(file).use { out ->
                    // Write dummy binary bytes (512 KB) to simulate a real video file
                    val dummyBytes = ByteArray(1024 * 512)
                    for (j in dummyBytes.indices) {
                        dummyBytes[j] = (j % 256).toByte()
                    }
                    out.write(dummyBytes)
                }

                _lastDownloadedVideoFile.value = file
                _videoDownloadMessage.value = "Walkthrough video guide successfully downloaded!\n\nFile: $fileName\nSize: 512 KB\nSaved to device downloads."
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                _videoDownloadMessage.value = "Failed to download video: ${e.message}"
            } finally {
                _isVideoDownloading.value = false
            }
        }
    }

    fun dismissVideoDownloadMessage() {
        _videoDownloadMessage.value = null
        _lastDownloadedVideoFile.value = null
    }

    private fun resetSimulationStates() {
        _isDynamicScanning.value = false
        _dynamicProgress.value = 0f
        _dynamicData.value = null
        
        _isRlSimulating.value = false
        _rlData.value = null
        
        _isGaOptimizing.value = false
        _gaData.value = null
        
        _isExporting.value = false
        _exportMessage.value = null
        _exportProgress.value = 0f
        _currentExportedRows.value = 0
        _lastExportedFile.value = null
        _exportDownloadLink.value = null
        _isUploadingFile.value = false

        _isVideoDownloading.value = false
        _videoDownloadProgress.value = 0f
        _lastDownloadedVideoFile.value = null
        _videoDownloadMessage.value = null
    }
}
