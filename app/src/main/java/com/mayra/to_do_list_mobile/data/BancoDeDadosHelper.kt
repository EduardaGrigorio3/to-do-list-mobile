package com.mayra.to_do_list_mobile.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mayra.to_do_list_mobile.listadetarefas.Tarefa // Certifique-se de que Tarefa está neste pacote ou ajuste a importação

class BancoDeDadosHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tarefas.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_TAREFAS = "tarefas"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOME = "nome"
        private const val COLUMN_CONCLUIDA = "concluida"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSql = """
        CREATE TABLE $TABLE_TAREFAS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NOME TEXT, 
            $COLUMN_CONCLUIDA INTEGER DEFAULT 0
        )
    """.trimIndent()
        db.execSQL(createTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAREFAS")
        onCreate(db)
    }

    // --- Operações CRUD ---

    // CREATE
    fun inserirTarefa(db: SQLiteDatabase, nome: String): Long {
        val valores = ContentValues().apply {
            put(COLUMN_NOME, nome)
            put(COLUMN_CONCLUIDA, 0)
        }
        val id = db.insert(TABLE_TAREFAS, null, valores)
        return id
    }

    // READ (todas as tarefas)
    fun buscarTodasTarefas(db: SQLiteDatabase): List<Tarefa> {
        val tarefas = mutableListOf<Tarefa>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TAREFAS ORDER BY $COLUMN_ID DESC", null)

        cursor?.use {
            if (it.moveToFirst()) {
                val idColumnIndex = it.getColumnIndex(COLUMN_ID)
                val nomeColumnIndex = it.getColumnIndex(COLUMN_NOME)
                val concluidaColumnIndex = it.getColumnIndex(COLUMN_CONCLUIDA)

                do {
                    val id = it.getInt(idColumnIndex)
                    val nome = it.getString(nomeColumnIndex)
                    val isConcluida = it.getInt(concluidaColumnIndex) == 1
                    tarefas.add(Tarefa(id, nome, isConcluida))
                } while (it.moveToNext())
            }
        }
        return tarefas
    }

    // UPDATE
    fun atualizarTarefa(db: SQLiteDatabase, tarefa: Tarefa): Int {
        val valores = ContentValues().apply {
            put(COLUMN_NOME, tarefa.nome)
        }
        val rowsAffected = db.update(
            TABLE_TAREFAS,
            valores,
            "$COLUMN_ID = ?",
            arrayOf(tarefa.id.toString())
        )
        return rowsAffected
    }

    // DELETE
    fun deletarTarefa(db: SQLiteDatabase, id: Int): Int {
        val rowsAffected = db.delete(
            TABLE_TAREFAS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        return rowsAffected
    }

    // UPDATE CONCLUÍDA
    fun atualizarEstadoConcluida(db: SQLiteDatabase, id: Int, isConcluida: Boolean): Int {
        val valores = ContentValues().apply {
            put(COLUMN_CONCLUIDA, if (isConcluida) 1 else 0)
        }
        val rowsAffected = db.update(
            TABLE_TAREFAS,
            valores,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        return rowsAffected
    }


    // DELETE CONCLUÍDA
    fun deletarTarefasConcluidas(db: SQLiteDatabase): Int {
        val rowsAffected = db.delete(
            TABLE_TAREFAS,
            "$COLUMN_CONCLUIDA = ?",
            arrayOf("1")
        )
        return rowsAffected
    }
}