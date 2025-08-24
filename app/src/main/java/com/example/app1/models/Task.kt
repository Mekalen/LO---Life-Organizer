package com.example.app1.models

import java.util.Date

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: Date? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: Category = Category.PERSONAL,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val completedAt: Date? = null
) {
    enum class Priority {
        HIGH, MEDIUM, LOW
    }
    
    enum class Category {
        WORK, PERSONAL, HEALTH, LEARNING
    }
}
