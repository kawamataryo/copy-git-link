import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import java.awt.datatransfer.StringSelection

fun showNotification(project: Project, type: NotificationType, title: String, content: String) {
    NotificationGroupManager.getInstance()
        .getNotificationGroup("CopyGitlink Notification Group")
        .createNotification(content, type)
        .setTitle(title)
        .setListener(NotificationListener.URL_OPENING_LISTENER)
        .notify(project);
}

fun copyToClipboard(text: String) {
    CopyPasteManager.getInstance().setContents(StringSelection(text))
}

fun truncateText(text: String, maxLength: Int): String {
    if (text.length < maxLength) {
        return text
    }
    return "${text.substring(0, maxLength)}..."
}

fun getPermalink(
    repositoryPath: String,
    revision: String,
    filePath: String,
    linePath: String
): String {
    return "https://github.com/$repositoryPath/blob/$revision$filePath$linePath"
}

fun getRepositoryPathFromRemoteUrl(
    remoteUrl: String,
): String {
    val result =
        Regex(".*(?:@|\\/\\/)(.[^:\\/]*)(?::[0-9]{1,4})?.([^\\.]+)(\\.git)?\$").matchEntire(
            remoteUrl
        )
    return result?.groupValues?.get(1) + "/" + result?.groupValues?.get(2) ?: ""
}
