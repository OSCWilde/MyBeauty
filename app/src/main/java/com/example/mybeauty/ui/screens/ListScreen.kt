package com.example.mybeauty.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mybeauty.data.Procedure
import com.example.mybeauty.data.ProcedureRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNew: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ProcedureRepository(context) }
    var procedures by remember { mutableStateOf(repository.getAll()) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    val filtered = remember(procedures, searchQuery) {
        if (searchQuery.isBlank()) procedures
        else repository.search(searchQuery)
    }

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { showSearch = false },
                    active = false,
                    onActiveChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск процедур...") },
                    leadingIcon = { Icon(Icons.Filled.Search, "Поиск") },
                    trailingIcon = {
                        TextButton(onClick = {
                            searchQuery = ""
                            showSearch = false
                        }) { Text("Закрыть") }
                    }
                ) {}
            } else {
                TopAppBar(
                    title = { Text("Мои процедуры", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Filled.Search, "Поиск")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNew) {
                Icon(Icons.Filled.Add, "Добавить")
            }
        }
    ) { padding ->
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotBlank()) "Ничего не найдено"
                    else "Список процедур пуст\nНажмите + чтобы добавить",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(filtered, key = { it.id }) { procedure ->
                    ProcedureCard(
                        procedure = procedure,
                        onClick = { onNavigateToDetail(procedure.id) },
                        onDelete = {
                            repository.delete(procedure.id)
                            procedures = repository.getAll()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProcedureCard(
    procedure: Procedure,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(procedure.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${procedure.date} ${procedure.time}", fontSize = 14.sp)
                if (procedure.price > 0) {
                    Text(
                        "%.2f ₽".format(procedure.price),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (procedure.notes.isNotBlank()) {
                    Text(
                        procedure.notes,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Filled.Delete,
                    "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить процедуру?") },
            text = { Text("Вы уверены, что хотите удалить «${procedure.name}»?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            }
        )
    }
}