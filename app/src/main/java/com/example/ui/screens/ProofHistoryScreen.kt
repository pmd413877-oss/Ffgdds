package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.ProofSubmission
import com.example.data.model.UploadStatus
import com.example.ui.theme.InstaPink
import com.example.ui.theme.PrCardDark
import com.example.ui.theme.PrError
import com.example.ui.theme.PrNavyDark
import com.example.ui.theme.PrSuccess
import com.example.ui.theme.PrSurfaceDark
import com.example.ui.theme.PrTextPrimary
import com.example.ui.theme.PrTextSecondary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProofHistoryScreen(
    submissions: List<ProofSubmission>,
    onRetrySubmission: (ProofSubmission) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProof by remember { mutableStateOf<ProofSubmission?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrNavyDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = PrSurfaceDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "সার্ভার আপলোড ও সাবমিশন লগ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrTextPrimary
                    )
                    Text(
                        text = "অটো-মেশিনের মাধ্যমে পাঠানো সব স্ক্রিনশট এবং সার্ভার রেসপন্স",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrTextSecondary
                    )
                }
            }

            if (submissions.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CloudDone,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = PrCardDark
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "কোনো প্রুফ এখনও জমা হয়নি",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "অটো মেশিনে ইনস্টাগ্রাম আইডি ফলো করলে স্ক্রিনশট স্বয়ংক্রিয়ভাবে এখানে যুক্ত হবে।",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrTextSecondary.copy(alpha = 0.7f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(submissions, key = { it.id }) { proof ->
                        ProofItemCard(
                            proof = proof,
                            onClick = { selectedProof = proof }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    // Detail Proof Modal
    if (selectedProof != null) {
        val proof = selectedProof!!
        val file = File(proof.screenshotPath)

        Dialog(onDismissRequest = { selectedProof = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrSurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "প্রুফ ও সার্ভার ডিটেইলস",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrTextPrimary
                        )
                        StatusBadge(proof.status, proof.responseCode)
                    }

                    // Large Screenshot View
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, PrCardDark, RoundedCornerShape(12.dp))
                    ) {
                        if (file.exists()) {
                            AsyncImage(
                                model = file,
                                contentDescription = "Proof Screenshot",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("ফাইল পাওয়া যায়নি", color = PrTextSecondary)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrCardDark)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "টার্গেট: @${proof.targetUsername}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = InstaPink
                        )
                        Text(
                            text = "সার্ভার: ${proof.serverUrl}",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val timeFormatted = SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault()).format(Date(proof.timestamp))
                        Text(
                            text = "সময়: $timeFormatted",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrTextSecondary
                        )

                        if (proof.responseBody.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "সার্ভার রেসপন্স:",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = proof.responseBody,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                                color = PrSuccess,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Button(
                        onClick = { selectedProof = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrCardDark)
                    ) {
                        Text("বন্ধ করুন", color = PrTextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun ProofItemCard(
    proof: ProofSubmission,
    onClick: () -> Unit
) {
    val file = File(proof.screenshotPath)
    val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(proof.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("proof_card_${proof.id}"),
        colors = CardDefaults.cardColors(containerColor = PrSurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrCardDark),
                contentAlignment = Alignment.Center
            ) {
                if (file.exists()) {
                    AsyncImage(
                        model = file,
                        contentDescription = "Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Image, contentDescription = null, tint = PrTextSecondary)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "@${proof.targetUsername}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrTextPrimary
                )
                Text(
                    text = "ফলো প্রুফ • $timeFormatted",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrTextSecondary
                )
            }

            StatusBadge(proof.status, proof.responseCode)
        }
    }
}

@Composable
fun StatusBadge(status: UploadStatus, responseCode: Int) {
    val (bgColor, textColor, label, icon) = when (status) {
        UploadStatus.SUBMITTED -> Quad(
            Color(0xFF064E3B),
            PrSuccess,
            if (responseCode > 0) "সাবমিট সম্পন্ন ($responseCode)" else "সাবমিট সম্পন্ন",
            Icons.Default.CheckCircle
        )
        UploadStatus.UPLOADING -> Quad(
            Color(0xFF1E3A8A),
            Color(0xFF60A5FA),
            "আপলোড হচ্ছে...",
            Icons.Default.Sync
        )
        UploadStatus.FAILED -> Quad(
            Color(0xFF7F1D1D),
            PrError,
            "ব্যর্থ হয়েছে",
            Icons.Default.Error
        )
        UploadStatus.QUEUED -> Quad(
            Color(0xFF374151),
            Color.LightGray,
            "অপেক্ষমাণ",
            Icons.Default.Sync
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
