package com.example.fruitsense.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fruitsense.R
import kotlin.random.Random

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "fruit_expiry_channel"
        const val CHANNEL_NAME = "Notifikasi Kesegaran Buah"
        const val CHANNEL_DESC = "Memberi tahu jika buah akan segera busuk"
    }

    // 1. Membuat Saluran Notifikasi (Wajib untuk Android 8.0+)
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 2. Menampilkan Notifikasi
    fun showExpiryNotification(fruitName: String, daysLeft: Int) {
        // Pesan berbeda tergantung sisa hari
        val message = when {
            daysLeft <= 0 -> "Peringatan! $fruitName mungkin sudah busuk. Cek sekarang!"
            daysLeft == 1 -> "Perhatian! $fruitName akan busuk besok."
            else -> "$fruitName akan busuk dalam $daysLeft hari lagi. Segera konsumsi!"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.fruitsense) // Pastikan ada icon ini atau ganti ic_launcher
            .setContentTitle("Update Inventaris Buah")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Memunculkan notifikasi
        // Pastikan izin POST_NOTIFICATIONS sudah diberikan di Android 13+
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(Random.nextInt(), builder.build())
            }
        } catch (e: SecurityException) {
            // Handle jika izin belum diberikan
            e.printStackTrace()
        }
    }
}