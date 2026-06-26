package com.example.mybeauty.data

import java.util.UUID

data class Procedure(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val date: String = "",
    val time: String = "",
    val price: Double = 0.0,
    val notes: String = "",
    val reminder: Boolean = false,
    val history: List<String> = emptyList()
)