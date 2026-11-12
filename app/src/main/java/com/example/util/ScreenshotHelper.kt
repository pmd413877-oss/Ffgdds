package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ScreenshotHelper {

    fun captureViewBitmap(view: View, targetUsername: String): Bitmap {
        val width = if (view.width > 0) view.width else 1080
        val height = if (view.height > 0) view.height else 1920

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        // Draw professional automation proof watermark banner at the top or bottom
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 36f
            isFakeBoldText = true
        }
        val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            textSize = 28f
        }

        // Draw banner at top
        val bannerHeight = 120f
        paint.color = Color.argb(220, 15, 23, 42) // Slate dark
        canvas.drawRect(0f, 0f, width.toFloat(), bannerHeight, paint)

        // Draw accent border
        paint.color = Color.argb(255, 225, 48, 108) // Instagram pink
        canvas.drawRect(0f, bannerHeight - 4f, width.toFloat(), bannerHeight, paint)

        // Draw watermark text
        canvas.drawText("PR. Social Auto Machine • Proof Verified", 32f, 50f, textPaint)
        val timeStr = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault()).format(Date())
        canvas.drawText("Target: @$targetUsername | Followed at $timeStr", 32f, 95f, subTextPaint)

        return bitmap
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, taskId: String): File {
        val proofsDir = File(context.cacheDir, "proofs").apply { mkdirs() }
        val filename = "proof_${taskId}_${System.currentTimeMillis()}.jpg"
        val file = File(proofsDir, filename)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file
    }

    fun generateSampleProofBitmap(width: Int = 1080, height: Int = 1920, username: String): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        val bgPaint = Paint().apply { color = Color.parseColor("#121212") }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Top bar
        val barPaint = Paint().apply { color = Color.parseColor("#1F1F1F") }
        canvas.drawRect(0f, 0f, width.toFloat(), 160f, barPaint)

        val headerText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 52f
            isFakeBoldText = true
        }
        canvas.drawText("@$username", 60f, 105f, headerText)

        // Profile Avatar Circle
        val avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#833AB4") }
        canvas.drawCircle(180f, 320f, 100f, avatarPaint)

        // Following Badge
        val btnPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#0095F6") }
        val btnRect = RectF(60f, 480f, width.toFloat() - 60f, 580f)
        canvas.drawRoundRect(btnRect, 16f, 16f, btnPaint)

        val btnText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 42f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("✓ Following / অনুসরণ করছেন", width / 2f, 545f, btnText)

        // Watermark header
        val wmPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(240, 11, 15, 25)
        }
        canvas.drawRect(0f, height - 160f, width.toFloat(), height.toFloat(), wmPaint)

        val wmText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#EC4899")
            textSize = 38f
            isFakeBoldText = true
        }
        canvas.drawText("PR. Social Auto Machine Proof • Submitted to Server", 40f, height - 95f, wmText)

        val subText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#94A3B8")
            textSize = 30f
        }
        val timeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        canvas.drawText("Account: @$username | Timestamp: $timeStr", 40f, height - 45f, subText)

        return bitmap
    }
}
