package com.example.mybeauty.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mybeauty.data.Procedure
import com.example.mybeauty.data.ProcedureRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    procedureId: String?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ProcedureRepository(context) }
    val existing = remember(procedureId) { if (procedureId != null) repository.getById(procedureId) else null }

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var date by remember { mutableStateOf(existing?.date ?: "") }
    var time by remember { mutableStateOf(existing?.time ?: "") }
    var price by remember { mutableStateOf(if (existing?.price != null && existing.price > 0) existing.price.toString() else "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    var reminder by remember { mutableStateOf(existing?.reminder ?: false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing != null) "Редактирование" else "Новая процедура") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; errorMessage = null },
                label = { Text("Название процедуры *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = date,
                onValueChange = { date = it; errorMessage = null },
                label = { Text("Дата (ГГГГ-ММ-ДД) *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Время (ЧЧ:ММ)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Стоимость (₽)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Заметки") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Напоминание о записи", modifier = Modifier.weight(1f))
                Switch(checked = reminder, onCheckedChange = { reminder = it })
            }

            if (existing != null && existing.history.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("История посещений:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        existing.history.forEach { entry ->
                            Text(entry, fontSize = 13.sp)
                        }
                    }
                }
            }

            errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = "Введите название процедуры"
                        } else if (date.isBlank()) {
                            errorMessage = "Выберите дату"
                        } else {
                            val procedure = Procedure(
                                id = existing?.id ?: java.util.UUID.randomUUID().toString(),
                                name = name,
                                date = date,
                                time = time,
                                price = price.toDoubleOrNull() ?: 0.0,
                                notes = notes,
                                reminder = reminder,
                                history = existing?.history ?: emptyList()
                            )
                            repository.save(procedure)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Сохранить")
                }

                if (existing != null) {
                    Spacer(Modifier.width(12.dp))
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Удалить")
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить процедуру?") },
            text = { Text("Вы уверены, что хотите удалить эту процедуру?") },
            confirmButton = {
                TextButton(onClick = {
                    procedureId?.let { repository.delete(it) }
                    showDeleteDialog = false
                    onNavigateBack()
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            }
        )
    }
}