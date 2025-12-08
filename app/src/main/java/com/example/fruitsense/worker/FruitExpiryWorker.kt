package com.example.fruitsense.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fruitsense.data.model.FruitItem
import com.example.fruitsense.utils.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class FruitExpiryWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        // Gunakan data dummy untuk testing worker
        val fruitList = getMockFruitData()

        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        fruitList.forEach { fruit ->
            // [FIX] Parsing String Date ke Long
            val createdTime = try {
                dateFormat.parse(fruit.dateAdded ?: "")?.time ?: currentTime
            } catch (e: Exception) {
                currentTime
            }

            // [FIX] Logika manual: Expired = 7 hari setelah dateAdded
            val expiryTime = createdTime + TimeUnit.DAYS.toMillis(7)
            val diffInMillis = expiryTime - currentTime
            val daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()

            if (daysLeft in -1..2) {
                notificationHelper.showExpiryNotification(fruit.name, daysLeft)
            }
        }

        return Result.success()
    }

    // [FIX] Sesuaikan dengan konstruktor FruitItem terbaru
    private fun getMockFruitData(): List<FruitItem> {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
        return listOf(
            FruitItem(id = "1", name = "Pisang", dateAdded = now, freshness = 40, grade = "B"),
            FruitItem(id = "2", name = "Apel", dateAdded = now, freshness = 85, grade = "A")
        )
    }
}