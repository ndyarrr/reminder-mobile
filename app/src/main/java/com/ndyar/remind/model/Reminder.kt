package com.ndyar.remind.model

data class Reminder(
    val id: Int,
    val title: String,
    val description: String,
    val time: Long,
    val deadlineTime: Long? = null,
    val isCompleted: Boolean = false,
    val isDaily: Boolean = false,
    val isPermanent: Boolean = false
)
