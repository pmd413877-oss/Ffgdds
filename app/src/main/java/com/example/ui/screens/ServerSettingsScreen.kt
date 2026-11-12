package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ServerConfig
import com.example.ui.theme.InstaPink
import com.example.ui.theme.PrCardDark
import com.example.ui.theme.PrNavyDark
import com.example.ui.theme.PrPrimary
import com.example.ui.theme.PrSuccess
import com.example.ui.theme.PrSurfaceDark
import com.example.ui.theme.PrTextPrimary
import com.example.ui.theme.PrTextSecondary

@Composable
fun ServerSettingsScreen(
    currentConfig: ServerConfig,
    serverPingStatus: String?,
    onSaveConfig: (ServerConfig) -> Unit,
    onTestConnection: () -> Unit,
    modifier: Modifier = Modifier
) {
    var endpointUrl by remember(currentConfig) { mutableStateOf(currentConfig.endpointUrl) }
    var authToken by remember(currentConfig) { mutableStateOf(currentConfig.authToken) }
    var autoSubmit by remember(currentConfig) { mutableStateOf(currentConfig.autoSubmitEnabled) }
    var autoScreenshot by remember(currentConfig) { mutableStateOf(currentConfig.autoScreenshotEnabled) }
    var autoNext by remember(currentConfig) { mutableStateOf(currentConfig.autoNextTask) }
    var delayMs by remember(currentConfig) { mutableFloatStateOf(currentConfig.screenshotDelayMs.toFloat()) }
    var isSavedNotice by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrNavyDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header
            Surface(
                color = PrSurfaceDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "সার্ভার ও অটোমেশন কনফিগারেশন",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrTextPrimary
                    )
                    Text(
                        text = "স্ক্রিনশট আপলোড সার্ভার ইউআরএল এবং অটো মেশিন সেটিংস",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrTextSecondary
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Server Target Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrSurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Cloud, contentDescription = null, tint = InstaPink)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "নির্দিষ্ট সার্ভার ইউআরএল (Server Endpoint)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrTextPrimary
                            )
                        }

                        Text(
                            text = "অটো-স্ক্রিনশট এই সার্ভারে POST রিকোয়েস্টে multipart/form-data আকারে জমা হবে।",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrTextSecondary
                        )

                        OutlinedTextField(
                            value = endpointUrl,
                            onValueChange = {
                                endpointUrl = it
                                isSavedNotice = false
                            },
                            label = { Text("Server POST Endpoint") },
                            placeholder = { Text("https://your-server.com/api/proofs") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = InstaPink,
                                unfocusedBorderColor = PrCardDark,
                                focusedTextColor = PrTextPrimary,
                                unfocusedTextColor = PrTextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("server_url_input")
                        )

                        OutlinedTextField(
                            value = authToken,
                            onValueChange = {
                                authToken = it
                                isSavedNotice = false
                            },
                            label = { Text("Worker / API Auth Token") },
                            placeholder = { Text("Bearer token / secret key") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = InstaPink,
                                unfocusedBorderColor = PrCardDark,
                                focusedTextColor = PrTextPrimary,
                                unfocusedTextColor = PrTextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("server_token_input")
                        )

                        // Test Connection Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = onTestConnection,
                                modifier = Modifier.testTag("test_server_button")
                            ) {
                                Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = PrPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("সার্ভার পিং টেস্ট", color = PrPrimary)
                            }

                            if (serverPingStatus != null) {
                                Text(
                                    text = serverPingStatus,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (serverPingStatus.contains("সফল")) PrSuccess else PrTextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Automation Behavior Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrSurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "অটোমেশন কন্ট্রোলস",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrTextPrimary
                        )

                        // Auto Submit Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "অটো-সাবমিট (Auto Submit)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrTextPrimary
                                )
                                Text(
                                    text = "স্ক্রিনশট নেওয়ার সাথে সাথেই সার্ভারে স্বয়ংক্রিয়ভাবে আপলোড করবে",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrTextSecondary
                                )
                            }
                            Switch(
                                checked = autoSubmit,
                                onCheckedChange = {
                                    autoSubmit = it
                                    isSavedNotice = false
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = InstaPink, checkedTrackColor = InstaPink.copy(alpha = 0.5f)),
                                modifier = Modifier.testTag("auto_submit_switch")
                            )
                        }

                        // Auto Screenshot on Follow
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "অটো-স্ক্রিনশট (Auto Screenshot)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrTextPrimary
                                )
                                Text(
                                    text = "ইনস্টাগ্রামে Follow বাটনে ক্লিক ডিটেক্ট হলে স্বয়ংক্রিয়ভাবে স্ক্রিনশট নিবে",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrTextSecondary
                                )
                            }
                            Switch(
                                checked = autoScreenshot,
                                onCheckedChange = {
                                    autoScreenshot = it
                                    isSavedNotice = false
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = InstaPink, checkedTrackColor = InstaPink.copy(alpha = 0.5f))
                            )
                        }

                        // Auto Next Task
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "পরবর্তী টাস্কে অটো নিয়ে যাওয়া",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrTextPrimary
                                )
                                Text(
                                    text = "সার্ভারে সাবমিট সম্পন্ন হলে পরবর্তী অ্যাকাউন্টে নিয়ে যাবে",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrTextSecondary
                                )
                            }
                            Switch(
                                checked = autoNext,
                                onCheckedChange = {
                                    autoNext = it
                                    isSavedNotice = false
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = InstaPink, checkedTrackColor = InstaPink.copy(alpha = 0.5f))
                            )
                        }

                        // Delay Slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ফলো করার পর স্ক্রিনশট বিলম্ব (Delay)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PrTextPrimary
                                )
                                Text(
                                    text = "${(delayMs / 1000f).let { String.format("%.1f", it) }} সেকেন্ড",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = InstaPink
                                )
                            }
                            Text(
                                text = "ইনস্টাগ্রামে Following টেক্সট রেন্ডার হওয়ার সময়",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrTextSecondary
                            )
                            Slider(
                                value = delayMs,
                                onValueChange = {
                                    delayMs = it
                                    isSavedNotice = false
                                },
                                valueRange = 500f..3000f,
                                steps = 5,
                                colors = SliderDefaults.colors(thumbColor = InstaPink, activeTrackColor = InstaPink)
                            )
                        }
                    }
                }

                // Save Button
                Button(
                    onClick = {
                        val newConfig = currentConfig.copy(
                            endpointUrl = endpointUrl.trim(),
                            authToken = authToken.trim(),
                            autoSubmitEnabled = autoSubmit,
                            autoScreenshotEnabled = autoScreenshot,
                            autoNextTask = autoNext,
                            screenshotDelayMs = delayMs.toLong()
                        )
                        onSaveConfig(newConfig)
                        isSavedNotice = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_settings_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = InstaPink)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSavedNotice) "✓ সেটিংস সংরক্ষিত হয়েছে!" else "সেটিংস সংরক্ষণ করুন",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
