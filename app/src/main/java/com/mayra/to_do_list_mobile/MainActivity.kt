package com.mayra.to_do_list_mobile

import android.database.sqlite.SQLiteDatabase // Importar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mayra.to_do_list_mobile.R
import com.mayra.to_do_list_mobile.TarefaAdapter
import com.mayra.to_do_list_mobile.data.BancoDeDadosHelper
import com.mayra.to_do_list_mobile.listadetarefas.Tarefa

class MainActivity : AppCompatActivity() {

    private lateinit var bancoHelper: BancoDeDadosHelper
    private lateinit var editTextTarefa: EditText
    private lateinit var buttonAdicionar: Button
    private lateinit var buttonAtualizar: Button
    private lateinit var listViewTarefas: ListView
    private lateinit var tarefaAdapter: TarefaAdapter
    private lateinit var buttonLimparConcluidas: Button

    private var tarefaSelecionada: Tarefa? = null
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bancoHelper = BancoDeDadosHelper(this)
        db = bancoHelper.writableDatabase

        editTextTarefa = findViewById(R.id.editTextTarefa)
        buttonAdicionar = findViewById(R.id.buttonAdicionar)
        buttonAtualizar = findViewById(R.id.buttonAtualizar)
        listViewTarefas = findViewById(R.id.listViewTarefas)
        buttonLimparConcluidas = findViewById(R.id.buttonLimparConcluidas)

        tarefaAdapter = TarefaAdapter(
            this,
            mutableListOf(),
            onDeleteClick = { tarefa ->
                mostrarDialogoExclusao(tarefa)
            },
            onEditClick = { tarefa ->
                tarefaSelecionada = tarefa
                editTextTarefa.setText(tarefa.nome)
                buttonAdicionar.isEnabled = false
                buttonAtualizar.isEnabled = true
                editTextTarefa.requestFocus()
                editTextTarefa.setSelection(editTextTarefa.text.length)
            },
            onToggleConcluida = { tarefa, isChecked ->
                atualizarEstadoTarefa(tarefa, isChecked)
            }
        )
        listViewTarefas.adapter = tarefaAdapter

        carregarTarefas()

        buttonAdicionar.setOnClickListener {
            adicionarTarefa()
        }

        buttonAtualizar.setOnClickListener {
            atualizarTarefa()
        }

        buttonLimparConcluidas.setOnClickListener {
            mostrarDialogoLimparConcluidas()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }

    private fun carregarTarefas() {

        val tarefas = bancoHelper.buscarTodasTarefas(db)
        tarefaAdapter.updateTarefas(tarefas)
        limparCampos()
    }

    private fun adicionarTarefa() {
        val nomeTarefa = editTextTarefa.text.toString().trim()
        if (nomeTarefa.isNotBlank()) {

            val id = bancoHelper.inserirTarefa(db, nomeTarefa)
            if (id != -1L) {
                Toast.makeText(this, "Tarefa adicionada!", Toast.LENGTH_SHORT).show()
                carregarTarefas()
            } else {
                Toast.makeText(this, "Erro ao adicionar tarefa.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Por favor, digite uma tarefa.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarTarefa() {
        val novoNome = editTextTarefa.text.toString().trim()
        val tarefa = tarefaSelecionada

        if (novoNome.isNotBlank() && tarefa != null) {
            val tarefaAtualizada = Tarefa(tarefa.id, novoNome)

            val rowsAffected = bancoHelper.atualizarTarefa(db, tarefaAtualizada)

            if (rowsAffected > 0) {
                Toast.makeText(this, "Tarefa atualizada!", Toast.LENGTH_SHORT).show()
                carregarTarefas()
            } else {
                Toast.makeText(this, "Erro ao atualizar tarefa.", Toast.LENGTH_SHORT).show()
            }
        } else if (tarefa == null) {
            Toast.makeText(this, "Nenhuma tarefa selecionada para atualizar.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Por favor, digite um novo nome para a tarefa.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarEstadoTarefa(tarefa: Tarefa, isConcluida: Boolean) {
        val rowsAffected = bancoHelper.atualizarEstadoConcluida(db, tarefa.id, isConcluida)
        if (rowsAffected > 0) {

        } else {
            Toast.makeText(this, "Erro ao atualizar estado da tarefa.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun mostrarDialogoExclusao(tarefa: Tarefa) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Tarefa")
            .setMessage("Tem certeza que deseja excluir a tarefa \"${tarefa.nome}\"?")
            .setPositiveButton("Sim") { dialog, _ ->
                deletarTarefa(tarefa.id)
                dialog.dismiss()
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deletarTarefa(id: Int) {

        val rowsAffected = bancoHelper.deletarTarefa(db, id)
        if (rowsAffected > 0) {
            Toast.makeText(this, "Tarefa excluída!", Toast.LENGTH_SHORT).show()
            carregarTarefas()
        } else {
            Toast.makeText(this, "Erro ao excluir tarefa.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limparCampos() {
        editTextTarefa.setText("")
        editTextTarefa.clearFocus()
        tarefaSelecionada = null
        buttonAdicionar.isEnabled = true
        buttonAtualizar.isEnabled = false
    }

    private fun mostrarDialogoLimparConcluidas() {
        AlertDialog.Builder(this)
            .setTitle("Limpar Tarefas Concluídas")
            .setMessage("Tem certeza que deseja excluir todas as tarefas marcadas como concluídas?")
            .setPositiveButton("Sim") { dialog, _ ->
                limparTarefasConcluidas()
                dialog.dismiss()
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun limparTarefasConcluidas() {
        val rowsAffected = bancoHelper.deletarTarefasConcluidas(db)
        if (rowsAffected > 0) {
            Toast.makeText(this, "$rowsAffected tarefas concluídas excluídas!", Toast.LENGTH_SHORT).show()
            carregarTarefas() // Recarrega a lista para mostrar as remoções
        } else {
            Toast.makeText(this, "Nenhuma tarefa concluída para excluir.", Toast.LENGTH_SHORT).show()
        }
    }
}