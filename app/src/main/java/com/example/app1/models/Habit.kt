package com.example.app1.models

import java.util.Date

data class Habit(
    val id: String = "",
    val name: String = "",
    val frequency: Frequency = Frequency.DAILY,
    val reminderTime: Date? = null,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val createdAt: Date = Date(),
    val lastTracked: Date? = null
) {
    enum class Frequency {
        DAILY, WEEKLY, MONTHLY
    }
}
