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

        // Process the ref to remove refs/heads/ prefix if present
        val processedRef = if (ref.startsWith("refs/heads/")) {
            ref.removePrefix("refs/heads/")
        } else {
            ref
        }

        // Encode branch name if it contains slashes
        val encodedRef = processedRef.replace("/", "%2F")

        // Calculate line information for Azure DevOps format
        val startLine = logicalStartPosition.line + 1 // Convert to 1-based

        // Check if this is a zero-length selection (caret at same position)
        val isZeroLengthSelection = logicalStartPosition.line == logicalEndPosition.line &&
                                   logicalStartPosition.column == logicalEndPosition.column

        // For Azure DevOps, we need to handle different cases:
        // 1. Zero-length selection: select the entire line (start to start+1)
        // 2. Multi-line selection with end column 0: we need to add 1 to match expected format
        // 3. Normal selection: just add 1 to convert to 1-based
        val endLine = when {
            isZeroLengthSelection -> startLine + 1 // For zero-length selection, select entire line
            logicalEndPosition.column == 0 && logicalStartPosition.line != logicalEndPosition.line ->
                logicalEndPosition.line + 1 // Add 1 for multi-line selections ending at column 0
            else -> logicalEndPosition.line + 1 // Normal case, just add 1 to convert to 1-based
        }

        // Azure DevOps uses 1-based column numbers
        // For zero-length selections, we want to select the entire line (column 1 to column 1)
        val startColumn = if (isZeroLengthSelection) 1 else logicalStartPosition.column + 1
        val endColumn = if (isZeroLengthSelection) 1 else logicalEndPosition.column + 1

        // Build the URL with line range information
        return "https://dev.azure.com/$organization/$project/_git/$repository?path=$encodedPath&version=$versionPrefix$encodedRef&line=$startLine&lineEnd=$endLine&lineStartColumn=$startColumn&lineEndColumn=$endColumn&lineStyle=plain&_a=contents"
    }

    private fun makeStandardUrl(ref: String): String {
        return "https://$repositoryPath/blob/$ref$relativePath$linePath"
    }
}
