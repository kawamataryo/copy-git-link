package com.github.kawamataryo.intellijpluginsandbox.services

import com.intellij.openapi.project.Project
import com.github.kawamataryo.intellijpluginsandbox.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
