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

    private val parsedRemote: ParsedRemote
        get() = parseRemoteUrl(repoUrl)

    val repositoryPath: String
        get() = parsedRemote.path

    val relativePath: String
        get() = path.replace(repoRoot, "")

    val permalink: String
        get() = makeUrl(revision)

    val branchLink: String
        get() = makeUrl(branch)

    private fun makeUrl(ref: String): String {
        return when (parsedRemote.hostType) {
            HostType.AZURE_DEVOPS -> makeAzureDevOpsUrl(ref)
            else -> makeStandardUrl(ref)
        }
    }

    private fun makeAzureDevOpsUrl(ref: String): String {
        // Use the parsed organization, project and repository from the ParsedRemote
        val organization = parsedRemote.organization
        val project = parsedRemote.project
        val repository = parsedRemote.repository

        // If any of the required fields are missing, throw an exception
        if (organization.isEmpty()) {
            throw IllegalStateException("Azure DevOps organization not found in URL: $repoUrl")
        }

        if (project.isEmpty()) {
            throw IllegalStateException("Azure DevOps project not found in URL: $repoUrl")
        }

        if (repository.isEmpty()) {
            throw IllegalStateException("Azure DevOps repository not found in URL: $repoUrl")
        }

        // Encode the path for URL
        val encodedPath = relativePath.replace("/", "%2F")

        // Prefix for commit hash or branch name
        val versionPrefix = if (ref.isNotEmpty()) {
            if (ref == branch) "GB" else "GC"
        } else {
            "GC"
        }

        // Encode branch name if it contains slashes
        val encodedRef = ref.replace("/", "%2F")

        return "https://dev.azure.com/$organization/$project/_git/$repository?path=$encodedPath&version=$versionPrefix$encodedRef&_a=contents"
    }

    private fun makeStandardUrl(ref: String): String {
        return "https://$repositoryPath/blob/$ref$relativePath$linePath"
    }
}
