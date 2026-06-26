package com.example.mybeauty.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mybeauty.data.ProcedureRepository

@Composable
fun StatsScreen() {
    val context = LocalContext.current
    val repository = remember { ProcedureRepository(context) }
    val procedures = remember { repository.getAll() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Статистика", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Text("Всего процедур: ${procedures.size}", fontSize = 18.sp)
        Text("Потрачено: %.2f ₽".format(procedures.sumOf { it.price }), fontSize = 18.sp)
    }
}