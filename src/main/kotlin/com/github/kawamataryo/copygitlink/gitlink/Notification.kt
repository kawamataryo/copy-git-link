package com.github.kawamataryo.copygitlink.gitlink

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * Shows a notification in the IDE.
 */
fun showNotification(project: Project, type: NotificationType, title: String, content: String) {
    NotificationGroupManager.getInstance()
        .getNotificationGroup("CopyGitlink Notification Group")
        .createNotification(content, type)
        .setTitle(title)
        .notify(project)
}
