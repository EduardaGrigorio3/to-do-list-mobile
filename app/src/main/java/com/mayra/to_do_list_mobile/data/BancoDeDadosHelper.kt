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
        private const val DATABASE_VERSION = 1
        private const val TABLE_TAREFAS = "tarefas"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOME = "nome"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSql = """
            CREATE TABLE $TABLE_TAREFAS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOME TEXT
            )
        """.trimIndent()
        db.execSQL(createTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Quando o banco de dados é atualizado, dropa a tabela antiga e cria uma nova.
        // Em um app de produção, você faria um ALTER TABLE para preservar dados.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAREFAS")
        onCreate(db)
    }

    // --- Operações CRUD ---

    // CREATE
    // Recebe a instância do banco de dados (db)
    fun inserirTarefa(db: SQLiteDatabase, nome: String): Long {
        val valores = ContentValues().apply {
            put(COLUMN_NOME, nome)
        }
        val id = db.insert(TABLE_TAREFAS, null, valores)
        // db.close() REMOVIDO
        return id
    }

    // READ (todas as tarefas)
    // Recebe a instância do banco de dados (db)
    fun buscarTodasTarefas(db: SQLiteDatabase): List<Tarefa> {
        val tarefas = mutableListOf<Tarefa>()
        // Usamos db.rawQuery diretamente na instância passada
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TAREFAS ORDER BY $COLUMN_ID DESC", null)

        cursor?.use { // 'use' garante que o cursor será fechado, mesmo sem fechar o db
            if (it.moveToFirst()) {
                val idColumnIndex = it.getColumnIndex(COLUMN_ID)
                val nomeColumnIndex = it.getColumnIndex(COLUMN_NOME)

                do {
                    val id = it.getInt(idColumnIndex)
                    val nome = it.getString(nomeColumnIndex)
                    tarefas.add(Tarefa(id, nome))
                } while (it.moveToNext())
            }
        }
        // db.close() REMOVIDO
        return tarefas
    }

    // UPDATE
    // Recebe a instância do banco de dados (db)
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
        // db.close() REMOVIDO
        return rowsAffected
    }

    // DELETE
    // Recebe a instância do banco de dados (db)
    fun deletarTarefa(db: SQLiteDatabase, id: Int): Int {
        val rowsAffected = db.delete(
            TABLE_TAREFAS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        // db.close() REMOVIDO
        return rowsAffected
    }
}