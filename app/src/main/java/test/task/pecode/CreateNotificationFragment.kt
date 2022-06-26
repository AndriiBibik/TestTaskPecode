package test.task.pecode

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_create_notification.view.*

class CreateNotificationFragment(private val fragmentNumber: Int) : Fragment() {

    companion object {
        const val NOTIFICATION_FROM_POSITION_KEY = "notification_id_key"
    }

    private lateinit var fragmentLayout: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLayout = inflater.inflate(R.layout.fragment_create_notification, container, false)
        updateFragmentNumberTextView()
        setButtonsListeners()
        hideRemoveFragmentButtonIfFirst()
        return fragmentLayout
    }

    private fun updateFragmentNumberTextView() {
        fragmentLayout.fragment_number.text = fragmentNumber.toString()
    }

    private fun setButtonsListeners() {
        setRemoveLastFragmentButtonListener()
        setAddNewFragmentButtonListener()
        setCreateNotificationButtonListener()
    }

    private fun setRemoveLastFragmentButtonListener() =
        fragmentLayout.remove_fragment_button.setOnClickListener {
            val lastFragmentNumber = (activity as MainActivity).getLastFragmentNumber()
            (activity as MainActivity).removeLastFragmentUpdateItsNumberSlideIfNeeded()
            cancelNotification(lastFragmentNumber)
        }

    private fun cancelNotification(notificationId: Int) {
        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.cancel(notificationId)
    }

    private fun setAddNewFragmentButtonListener() =
        fragmentLayout.add_fragment_button.setOnClickListener {
            (activity as MainActivity).addNewFragmentUpdateItsNumberSlideForwardIfNeeded()
        }

    private fun setCreateNotificationButtonListener() {
        fragmentLayout.create_notification_button.setOnClickListener { createNewNotification() }
    }

    private fun createNewNotification() {
        val notificationManager = NotificationManagerCompat.from(requireContext())
        val activityIntent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra(NOTIFICATION_FROM_POSITION_KEY, (fragmentNumber - 1))
        }
        val contentIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            activityIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notification =
            NotificationCompat.Builder(requireContext(), TestTaskPecodeApp.CHANNEL_ID).apply {
                setSmallIcon(R.drawable.notification_icon)
                setContentTitle(getString(R.string.notification_title))
                setContentText("${getString(R.string.notification_text)} $fragmentNumber")
                setContentIntent(contentIntent)
                setAutoCancel(true)
                setDefaults(NotificationCompat.DEFAULT_ALL)
                setPriority(NotificationCompat.PRIORITY_HIGH)
                setCategory(NotificationCompat.CATEGORY_MESSAGE)
            }.build()
        notificationManager.notify(fragmentNumber, notification)
    }

    private fun hideRemoveFragmentButtonIfFirst() {
        if (fragmentNumber == 1) fragmentLayout.remove_fragment_button.visibility = View.GONE
    }
}