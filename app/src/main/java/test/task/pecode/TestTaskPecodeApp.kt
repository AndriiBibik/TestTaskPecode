package test.task.pecode

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class TestTaskPecodeApp: Application() {

    companion object {
        const val CHANNEL_ID = "IDofChannel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                resources.getString(R.string.notifications_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = resources.getString(R.string.notifications_channel_description)

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}