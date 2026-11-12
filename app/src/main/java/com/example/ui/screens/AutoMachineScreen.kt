package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.InstagramTask
import com.example.data.model.ServerConfig
import com.example.data.model.TaskStatus
import com.example.ui.theme.InstaBlue
import com.example.ui.theme.InstaOrange
import com.example.ui.theme.InstaPink
import com.example.ui.theme.InstaPurple
import com.example.ui.theme.PrCardDark
import com.example.ui.theme.PrNavyDark
import com.example.ui.theme.PrPrimary
import com.example.ui.theme.PrSuccess
import com.example.ui.theme.PrSurfaceDark
import com.example.ui.theme.PrTextPrimary
import com.example.ui.theme.PrTextSecondary
import com.example.ui.viewmodel.AutomationUiState
import com.example.util.ScreenshotHelper
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AutoMachineScreen(
    uiState: AutomationUiState,
    serverConfig: ServerConfig,
    onFollowDetected: (username: String, bitmapProvider: () -> Bitmap?) -> Unit,
    onManualCapture: (bitmapProvider: () -> Bitmap?) -> Unit,
    onGalleryPicked: (Uri) -> Unit,
    onNextTask: () -> Unit,
    onSubmitProof: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismissBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var webProgress by remember { mutableFloatStateOf(0f) }
    var isWebLoading by remember { mutableStateOf(true) }
    var showPreviewDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onGalleryPicked(uri)
        }
    }

    val instagramGradient = Brush.horizontalGradient(
        colors = listOf(InstaPurple, InstaPink, InstaOrange)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrNavyDark)
    ) {
        // Top Machine Status Bar
        Surface(
            color = PrSurfaceDark,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (uiState.isAutoMachineActive) PrSuccess else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PR. Social অটো মেশিন",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrTextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrCardDark)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (serverConfig.autoSubmitEnabled) "অটো-সাবমিট: অন" else "অটো-সাবমিট: অফ",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (serverConfig.autoSubmitEnabled) PrSuccess else PrTextSecondary
                            )
                        }
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.testTag("settings_icon_button")
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = PrTextSecondary
                            )
                        }
                    }
                }

                // Active Task Badge
                val activeTask = uiState.activeTask
                if (activeTask != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = PrCardDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "@${activeTask.targetUsername}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = InstaPink
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${activeTask.followersCount}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PrTextSecondary
                                    )
                                }
                                Text(
                                    text = "ফলো করুন ➔ সাথে সাথে স্ক্রিনশট হয়ে সার্ভারে আপলোড হবে",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(instagramGradient)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+${activeTask.rewardPoints} Pts",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Web Loading indicator
        if (isWebLoading) {
            LinearProgressIndicator(
                progress = { webProgress },
                modifier = Modifier.fillMaxWidth(),
                color = InstaPink,
                trackColor = PrCardDark,
            )
        }

        // Status Feedback Banner
        AnimatedVisibility(
            visible = uiState.statusBannerMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val bannerBg = if (uiState.statusBannerIsSuccess) Color(0xFF064E3B) else Color(0xFF7F1D1D)
            val bannerIcon = if (uiState.statusBannerIsSuccess) Icons.Default.CheckCircle else Icons.Default.Error

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bannerBg)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onDismissBanner() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    bannerIcon,
                    contentDescription = null,
                    tint = if (uiState.statusBannerIsSuccess) PrSuccess else Color.Red,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiState.statusBannerMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                if (uiState.isUploading || uiState.isCapturing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                }
            }
        }

        // Main In-App Instagram Engine (WebView)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val targetUrl = uiState.activeTask?.profileUrl ?: "https://www.instagram.com/"

            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewRef = this
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
                        }

                        // Javascript bridge to listen for follow actions
                        addJavascriptInterface(object {
                            @JavascriptInterface
                            fun onFollowClicked(path: String) {
                                post {
                                    onFollowDetected(uiState.activeTask?.targetUsername ?: "instagram") {
                                        webViewRef?.let { wv ->
                                            ScreenshotHelper.captureViewBitmap(wv, uiState.activeTask?.targetUsername ?: "user")
                                        }
                                    }
                                }
                            }
                        }, "InstagramBridge")

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isWebLoading = false

                                // Injected script monitors Follow button click and text change
                                val jsMonitor = """
                                    (function() {
                                        function checkAndHook() {
                                            var buttons = document.querySelectorAll('button, div[role="button"]');
                                            buttons.forEach(function(btn) {
                                                if (btn.__pr_hooked) return;
                                                btn.__pr_hooked = true;
                                                btn.addEventListener('click', function() {
                                                    var text = (btn.innerText || btn.textContent || '').trim().toLowerCase();
                                                    if (text === 'follow' || text === 'অনুসরণ করুন' || text.indexOf('follow') !== -1) {
                                                        if (window.InstagramBridge) {
                                                            window.InstagramBridge.onFollowClicked(window.location.pathname);
                                                        }
                                                    }
                                                });
                                            });
                                        }
                                        setInterval(checkAndHook, 800);
                                    })();
                                """.trimIndent()
                                view?.evaluateJavascript(jsMonitor, null)
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                webProgress = newProgress / 100f
                                if (newProgress == 100) isWebLoading = false
                            }
                        }

                        loadUrl(targetUrl)
                    }
                },
                update = { wv ->
                    if (wv.url != targetUrl && uiState.activeTask != null) {
                        wv.loadUrl(targetUrl)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("instagram_webview")
            )

            // Web Navigation Floating Quick Actions
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { webViewRef?.reload() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reload Instagram",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Floating Auto-Machine Action Dock
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrSurfaceDark.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Sensors,
                                contentDescription = null,
                                tint = PrSuccess,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ফলো ডিটেকশন অটো সক্রিয়",
                                style = MaterialTheme.typography.labelMedium,
                                color = PrSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (uiState.capturedBitmap != null) {
                            Text(
                                text = "✓ স্ক্রিনশট প্রস্তুত",
                                style = MaterialTheme.typography.labelSmall,
                                color = InstaPink,
                                modifier = Modifier
                                    .clickable { showPreviewDialog = true }
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Main Instant Snap & Upload Button
                        Button(
                            onClick = {
                                onManualCapture {
                                    webViewRef?.let { wv ->
                                        ScreenshotHelper.captureViewBitmap(wv, uiState.activeTask?.targetUsername ?: "user")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(2f)
                                .height(50.dp)
                                .testTag("snap_submit_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = InstaPink)
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.isUploading) "আপলোড হচ্ছে..." else "📸 স্ন্যাপ ও অটো-আপলোড",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Next Task Button
                        Button(
                            onClick = onNextTask,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("next_task_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrCardDark)
                        ) {
                            Icon(
                                Icons.Default.FastForward,
                                contentDescription = "Next Task",
                                tint = PrTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Gallery fallback button
                        IconButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(PrCardDark)
                                .testTag("gallery_picker_button")
                        ) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = "Upload from gallery",
                                tint = PrPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    // Proof Preview & Manual Submission Dialog
    if (showPreviewDialog && uiState.capturedBitmap != null) {
        Dialog(onDismissRequest = { showPreviewDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrSurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ক্যাপচার করা স্ক্রিনশট প্রুফ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, PrCardDark, RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = uiState.capturedScreenshotFile,
                            contentDescription = "Captured Screenshot",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "টার্গেট আইডি: @${uiState.activeTask?.targetUsername}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InstaPink
                    )
                    Text(
                        text = "সার্ভার: ${serverConfig.endpointUrl}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showPreviewDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল", color = PrTextSecondary)
                        }

                        Button(
                            onClick = {
                                showPreviewDialog = false
                                onSubmitProof()
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("confirm_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrSuccess)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("সার্ভারে জমা দিন", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
