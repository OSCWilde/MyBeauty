package com.example.mybeauty.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class ProcedureRepository(private val appContext: Context) {

    private val fileName = "procedures.json"
    private val gson = Gson()

    private val file: File
        get() = File(appContext.filesDir, fileName)

    fun getAll(): List<Procedure> {
        if (!file.exists()) return emptyList()
        return try {
            val json = file.readText()
            val type = object : TypeToken<List<Procedure>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getById(id: String): Procedure? = getAll().find { it.id == id }

    fun save(procedure: Procedure) {
        val list = getAll().toMutableList()
        val index = list.indexOfFirst { it.id == procedure.id }
        if (index >= 0) {
            val old = list[index]
            val updatedHistory = procedure.history.toMutableList()
            if (old.date != procedure.date && procedure.date.isNotBlank()) {
                updatedHistory.add("${old.date} ${old.time} — ${old.name}")
            }
            list[index] = procedure.copy(history = updatedHistory)
        } else {
            list.add(procedure)
        }
        file.writeText(gson.toJson(list))
    }

    fun delete(id: String) {
        val list = getAll().toMutableList()
        list.removeAll { it.id == id }
        file.writeText(gson.toJson(list))
    }

    fun search(query: String): List<Procedure> {
        return getAll().filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.notes.contains(query, ignoreCase = true)
        }
    }
}