package com.bhushantechsolutions.eventmanagementsystem

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform