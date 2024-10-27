package ac.quant.quickfixspec.ui

import com.intellij.notification.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

private const val GROUP_DISPLAY_ID = "Display ID"

class ShowNotificationSampleAction : AnAction() {
    private val messageTitle = "Title of notification"
    private val messageDetails = "Details of notification"

    override fun actionPerformed(e: AnActionEvent) {
        aNotification()
    }

    private fun aNotification() {
        val notification = Notification(GROUP_DISPLAY_ID,
            "1 .$messageTitle",
            "1 .$messageDetails",
            NotificationType.INFORMATION)
        Notifications.Bus.notify(notification)
    }
}
