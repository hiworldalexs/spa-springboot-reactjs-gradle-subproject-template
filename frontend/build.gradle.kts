import com.github.gradle.node.npm.proxy.ProxySettings

plugins {
    id("com.github.node-gradle.node") version "7.0.0"
}

node {
    version.set("18.18.0")
    npmVersion.set("")
    yarnVersion.set("")
    distBaseUrl.set("https://nodejs.org/dist")
    download.set(true)
    workDir.set(file("${project.projectDir}/.cache/nodejs"))
    yarnWorkDir.set(file("${project.projectDir}/.cache/yarn"))
    nodeProjectDir.set(file("${project.projectDir}"))
    nodeProxySettings.set(ProxySettings.SMART)
}