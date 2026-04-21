package org.bhakin.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform