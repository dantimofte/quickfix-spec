package ac.quant.quickfixspec.ui

import com.intellij.notification.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys


class ShowNotificationSampleAction : AnAction() {
    private val GROUP_DISPAY_ID = "UI Samples"
    private val messageTitle = "Title of notification"
    private val messageDetails = "Details of notification"

    override fun actionPerformed(e: AnActionEvent) {
        aNotification()
    }

    private fun aNotification() {
        val notification = Notification(GROUP_DISPAY_ID,
            "1 .$messageTitle",
            "1 .$messageDetails",
            NotificationType.INFORMATION)
        Notifications.Bus.notify(notification)
    }
}