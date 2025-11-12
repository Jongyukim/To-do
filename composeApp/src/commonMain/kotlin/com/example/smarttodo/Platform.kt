package com.example.smarttodo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform