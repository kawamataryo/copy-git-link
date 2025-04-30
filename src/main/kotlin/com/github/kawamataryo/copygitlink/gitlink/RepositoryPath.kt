package com.github.kawamataryo.copygitlink.gitlink

/**
 * Extracts the repository path from a remote URL.
 * This handles various formats including:
 * - git@github.com:user/repo.git
 * - https://github.com/user/repo.git
 * - git@bitbucket.org:user/repo.git
 * - https://gitlab.self-hosted.com:8443/projgroup/projectname
 */
fun getRepositoryPathFromRemoteUrl(remoteUrl: String): String {
    // Special case for Azure DevOps
    if (remoteUrl.contains("dev.azure.com") || remoteUrl.contains("ssh.dev.azure.com")) {
        if (remoteUrl.startsWith("git@ssh.dev.azure.com:")) {
            // SSH Azure DevOps format: git@ssh.dev.azure.com:v3/org/project/repo
            val pattern = Regex("git@ssh\\.dev\\.azure\\.com:(.+)")
            val match = pattern.find(remoteUrl)
            if (match != null) {
                return "ssh.dev.azure.com/" + match.groupValues[1]
            }
        } else if (remoteUrl.contains("dev.azure.com")) {
            // HTTPS Azure DevOps format: https://dev.azure.com/org/project/_git/repo
            val pattern = Regex("https://(?:[^@]+@)?dev\\.azure\\.com/(.+)")
            val match = pattern.find(remoteUrl)
            if (match != null) {
                return "dev.azure.com/" + match.groupValues[1]
            }
        }
    }

    // Handle SSH URLs like git@github.com:user/repo.git
    val sshPattern = Regex("git@([^:]+):(.+?)(?:\\.git)?$")
    val sshMatch = sshPattern.find(remoteUrl)
    if (sshMatch != null) {
        val (domain, path) = sshMatch.destructured
        return "$domain/$path"
    }

    // Handle SSH URLs with ssh:// protocol like ssh://git@github.com:user/repo.git
    val sshAltPattern = Regex("ssh://git@([^:]+):(.+?)(?:\\.git)?$")
    val sshAltMatch = sshAltPattern.find(remoteUrl)
    if (sshAltMatch != null) {
        val (domain, path) = sshAltMatch.destructured
        return "$domain/$path"
    }

    // Handle HTTPS and git:// URLs
    val httpsPattern = Regex("(?:https|git)://(?:[^@]+@)?([^:/]+)(?::[0-9]+)?/(.+?)(?:\\.git)?$")
    val httpsMatch = httpsPattern.find(remoteUrl)
    if (httpsMatch != null) {
        val (domain, path) = httpsMatch.destructured
        return "$domain/$path"
    }

    throw IllegalArgumentException("Failed to parse remote URL: $remoteUrl")
}
