package com.github.kawamataryo.copygitlink.gitlink

import com.intellij.openapi.editor.LogicalPosition

class LinkMaker(
    private val repoUrl: String,
    private val repoRoot: String,
    private val path: String,
    private val logicalStartPosition: LogicalPosition,
    private val logicalEndPosition: LogicalPosition,
    private val revision: String = "",
    private val branch: String = ""
) {
    val linePath: String
        get() {
            val start = logicalStartPosition.line + 1
            val end = if (logicalEndPosition.column == 0 && logicalStartPosition.line != logicalEndPosition.line) logicalEndPosition.line else logicalEndPosition.line + 1
            return if (start == end) "#L$start" else "#L$start-L$end"
        }

    val repositoryPath: String
        get() = getRepositoryPathFromRemoteUrl(repoUrl)

    val relativePath: String
        get() = path.replace(repoRoot, "")

    val permalink: String
        get() = makeUrl(revision)

    val branchLink: String
        get() = makeUrl(branch)

    private fun makeUrl(ref: String): String {
        return "https://$repositoryPath/blob/$ref$relativePath$linePath"
    }
}
