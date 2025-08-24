package com.example.app1.models

import java.util.Date

data class Goal(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val deadline: Date? = null,
    val progress: Int = 0, // 0-100%
    val targetValue: String = "",
    val currentValue: String = "",
    val createdAt: Date = Date(),
    val isCompleted: Boolean = false,
    val completedAt: Date? = null
)
