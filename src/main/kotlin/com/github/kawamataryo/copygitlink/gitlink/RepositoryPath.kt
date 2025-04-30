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
    val result = Regex(".*(?:@|//)(.[^:/]*)(:?:[0-9]{1,4})?.([^.]+)(\\.git)?$").matchEntire(remoteUrl)
    return if (result != null) {
        result.groupValues[1] + "/" + result.groupValues[3]
    } else {
        ""
    }
}
