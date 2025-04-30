package com.github.kawamataryo.copygitlink.gitlink

/**
 * Represents a parsed remote URL with information about the host type and path
 */
data class ParsedRemote(
    val path: String,
    val hostType: HostType,
    // Azure DevOps specific fields
    val organization: String = "",
    val project: String = "",
    val repository: String = ""
)

/**
 * Enum representing different Git hosting services
 */
enum class HostType {
    GITHUB,
    GITLAB,
    BITBUCKET,
    AZURE_DEVOPS,
    OTHER
}

/**
 * Extracts the repository path from a remote URL.
 * This handles various formats including:
 * - git@github.com:user/repo.git
 * - https://github.com/user/repo.git
 * - git@bitbucket.org:user/repo.git
 * - https://gitlab.self-hosted.com:8443/projgroup/projectname
 */
fun parseRemoteUrl(remoteUrl: String): ParsedRemote {
    // Special case for Azure DevOps
    if (remoteUrl.contains("dev.azure.com") || remoteUrl.contains("ssh.dev.azure.com")) {
        if (remoteUrl.startsWith("git@ssh.dev.azure.com:")) {
            // SSH Azure DevOps format: git@ssh.dev.azure.com:v3/org/project/repo
            val pattern = Regex("git@ssh\\.dev\\.azure\\.com:(.+)")
            val match = pattern.find(remoteUrl)
            if (match != null) {
                val path = "dev.azure.com/" + match.groupValues[1]
                val parts = path.split("/")

                if (parts.size >= 5 && parts[1] == "v3") {
                    // Format: dev.azure.com/v3/organization/project/repository
                    val organization = parts[2]
                    val project = parts[3]
                    val repository = parts[4]

                    if (organization.isEmpty()) {
                        throw IllegalArgumentException("Could not extract organization from Azure DevOps URL: $remoteUrl")
                    }

                    if (project.isEmpty()) {
                        throw IllegalArgumentException("Could not extract project from Azure DevOps URL: $remoteUrl")
                    }

                    if (repository.isEmpty()) {
                        throw IllegalArgumentException("Could not extract repository from Azure DevOps URL: $remoteUrl")
                    }

                    return ParsedRemote(
                        path = path,
                        hostType = HostType.AZURE_DEVOPS,
                        organization = organization,
                        project = project,
                        repository = repository
                    )
                } else {
                    throw IllegalArgumentException("Invalid Azure DevOps SSH URL format: $remoteUrl")
                }
            }
        } else if (remoteUrl.contains("dev.azure.com")) {
            // HTTPS Azure DevOps format: https://dev.azure.com/org/project/_git/repo
            val pattern = Regex("https://(?:[^@]+@)?dev\\.azure\\.com/(.+)")
            val match = pattern.find(remoteUrl)
            if (match != null) {
                val path = "dev.azure.com/" + match.groupValues[1]
                val parts = path.split("/")

                if (parts.contains("_git")) {
                    // Format: dev.azure.com/organization/project/_git/repository
                    val gitIndex = parts.indexOf("_git")
                    if (gitIndex >= 3) {
                        val organization = parts[gitIndex - 2]
                        val project = parts[gitIndex - 1]
                        val repository = parts[gitIndex + 1]

                        if (organization.isEmpty()) {
                            throw IllegalArgumentException("Could not extract organization from Azure DevOps URL: $remoteUrl")
                        }

                        if (project.isEmpty()) {
                            throw IllegalArgumentException("Could not extract project from Azure DevOps URL: $remoteUrl")
                        }

                        if (repository.isEmpty()) {
                            throw IllegalArgumentException("Could not extract repository from Azure DevOps URL: $remoteUrl")
                        }

                        return ParsedRemote(
                            path = path,
                            hostType = HostType.AZURE_DEVOPS,
                            organization = organization,
                            project = project,
                            repository = repository
                        )
                    } else {
                        throw IllegalArgumentException("Invalid Azure DevOps HTTPS URL format: $remoteUrl")
                    }
                } else {
                    throw IllegalArgumentException("Could not find '_git' in Azure DevOps URL: $remoteUrl")
                }
            }
        }

        throw IllegalArgumentException("Failed to parse Azure DevOps URL: $remoteUrl")
    }

    // Determine host type based on domain
    val hostType = when {
        remoteUrl.contains("github.com") -> HostType.GITHUB
        remoteUrl.contains("gitlab") -> HostType.GITLAB
        remoteUrl.contains("bitbucket") -> HostType.BITBUCKET
        else -> HostType.OTHER
    }

    // Handle SSH URLs like git@github.com:user/repo.git
    val sshPattern = Regex("git@([^:]+):(.+?)(?:\\.git)?$")
    val sshMatch = sshPattern.find(remoteUrl)
    if (sshMatch != null) {
        val (domain, path) = sshMatch.destructured
        return ParsedRemote("$domain/$path", hostType)
    }

    // Handle SSH URLs with ssh:// protocol like ssh://git@github.com:user/repo.git
    val sshAltPattern = Regex("ssh://git@([^:]+):(.+?)(?:\\.git)?$")
    val sshAltMatch = sshAltPattern.find(remoteUrl)
    if (sshAltMatch != null) {
        val (domain, path) = sshAltMatch.destructured
        return ParsedRemote("$domain/$path", hostType)
    }

    // Handle HTTPS and git:// URLs
    val httpsPattern = Regex("(?:https|git)://(?:[^@]+@)?([^:/]+)(?::[0-9]+)?/(.+?)(?:\\.git)?$")
    val httpsMatch = httpsPattern.find(remoteUrl)
    if (httpsMatch != null) {
        val (domain, path) = httpsMatch.destructured
        return ParsedRemote("$domain/$path", hostType)
    }

    throw IllegalArgumentException("Failed to parse remote URL: $remoteUrl")
}
