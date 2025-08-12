package com.example.cleanerapplication

import android.app.ActivityManager
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val batteryProgress = findViewById<ProgressBar>(R.id.batteryProgress)
        val batteryPercentage = findViewById<TextView>(R.id.batteryPercentage)
        val batteryHealth = findViewById<TextView>(R.id.batteryHealth)
        val ramInfo = findViewById<TextView>(R.id.tvRamInfo)
        val storageInfo = findViewById<TextView>(R.id.tvStorageInfo)
        val btnOptimize = findViewById<Button>(R.id.btnOptimize)

        // Batarya bilgilerini al
        val batteryInfo = getBatteryInfo()
        batteryProgress.progress = batteryInfo.first
        batteryPercentage.text = "${batteryInfo.first}%"
        batteryHealth.text = "Durum: ${batteryInfo.second}"

        // RAM bilgilerini göster
        val ramData = getRamInfo()
        ramInfo.text = "Kullanılan RAM: ${ramData.first} / ${ramData.second} MB"

        // Depolama bilgilerini göster
        val storageData = getStorageInfo()
        storageInfo.text = "Kullanılan Depolama: ${storageData.first} / ${storageData.second} GB"

        // Optimize Et butonu
        btnOptimize.setOnClickListener {
            freeRam()
            Toast.makeText(this, "RAM temizlendi!", Toast.LENGTH_SHORT).show()

            btnOptimize.text = "Optimize Edildi ✓"
            btnOptimize.isEnabled = false
        }
    }

    // Batarya bilgilerini al
    private fun getBatteryInfo(): Pair<Int, String> {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = registerReceiver(null, intentFilter)
        val btnCleanRam = findViewById<Button>(R.id.btnCleanRam)

        btnCleanRam.setOnClickListener {
            // Burada simülasyon yapıyoruz, çünkü tam RAM temizlemek için system-level izin gerekir
            Toast.makeText(this, "RAM temizleniyor...", Toast.LENGTH_SHORT).show()

            // Basit bir simülasyon olarak buton metnini değiştiriyoruz
            btnCleanRam.text = "RAM Temizlendi ✓"
            btnCleanRam.isEnabled = false
        }

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = (level * 100) / scale

        val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val healthString = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "İyi"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Aşırı Isınmış"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Ölü"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Yüksek Voltaj"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Arızalı"
            else -> "Bilinmiyor"

        }

        return Pair(batteryPct, healthString)
    }

    // RAM bilgilerini al
    private fun getRamInfo(): Pair<Long, Long> {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRam = memoryInfo.totalMem / (1024 * 1024)
        val usedRam = totalRam - (memoryInfo.availMem / (1024 * 1024))

        return Pair(usedRam, totalRam)
    }

    // Depolama bilgilerini al
    private fun getStorageInfo(): Pair<Long, Long> {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalStorage = (totalBlocks * blockSize) / (1024 * 1024 * 1024)
        val usedStorage = totalStorage - ((availableBlocks * blockSize) / (1024 * 1024 * 1024))

        return Pair(usedStorage, totalStorage)
    }

    // RAM boşaltma (simülasyon)
    private fun freeRam() {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.clearApplicationUserData() // Bu uygulamanın verilerini temizler
    }
}
