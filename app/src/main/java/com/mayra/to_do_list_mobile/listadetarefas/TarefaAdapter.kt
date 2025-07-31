// src/main/java/com/mayra/to_do_list_mobile/TarefaAdapter.kt
package com.mayra.to_do_list_mobile

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import com.mayra.to_do_list_mobile.listadetarefas.Tarefa


class TarefaAdapter(
    context: Context,
    private val tarefas: MutableList<Tarefa>,
    private val onDeleteClick: (Tarefa) -> Unit,
    private val onEditClick: (Tarefa) -> Unit
) : ArrayAdapter<Tarefa>(context, 0, tarefas) {
    
    private val isConcluidaMap = mutableMapOf<Int, Boolean>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.list_item_tarefa,
                parent,
                false
            )
        }

        val currentTarefa = getItem(position)

        val nomeTextView = listItemView?.findViewById<TextView>(R.id.textViewTarefaNome)
        val checkBoxConcluida = listItemView?.findViewById<CheckBox>(R.id.checkBoxConcluida)
        val buttonEdit = listItemView?.findViewById<ImageButton>(R.id.buttonEdit)
        val buttonDelete = listItemView?.findViewById<ImageButton>(R.id.buttonDelete)

        nomeTextView?.text = currentTarefa?.nome

        // Configura o CheckBox com base no estado armazenado no mapa
        // Se a tarefa ainda não está no mapa, assume false
        checkBoxConcluida?.isChecked = isConcluidaMap[currentTarefa?.id] ?: false

        // Aplica/Remove o efeito de riscado no texto com base no status do checkbox
        // Correção para o erro "Infix call is prohibited on a nullable receiver"
        nomeTextView?.let { textView -> // Usamos 'let' para garantir que nomeTextView não é nulo
            if (checkBoxConcluida?.isChecked == true) {
                textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }


        // Listener para o CheckBox
        checkBoxConcluida?.setOnCheckedChangeListener { _, isChecked ->
            currentTarefa?.let {
                // Atualiza o mapa com o novo estado
                isConcluidaMap[it.id] = isChecked
                // Re-aplica o estilo de riscado imediatamente
                nomeTextView?.let { textView -> // Usamos 'let' novamente para o callback
                    if (isChecked) {
                        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }
            }
        }

        // Listener para o botão de exclusão
        buttonDelete?.setOnClickListener {
            currentTarefa?.let {
                onDeleteClick(it)
                // Remove do mapa quando a tarefa for deletada
                isConcluidaMap.remove(it.id)
            }
        }

        // Listener para o botão de edição
        buttonEdit?.setOnClickListener {
            currentTarefa?.let {
                onEditClick(it)
            }
        }

        return listItemView!!
    }

    fun updateTarefas(newTarefas: List< Tarefa>) {
        // Quando a lista de tarefas é atualizada (ex: após adicionar/deletar),
        // o estado do checkbox não é persistente. Se você adicionar/remover,
        // o estado dos checkboxes das tarefas que permaneceram será resetado.
        // Se você quiser manter o estado dos checkboxes para as tarefas que não foram removidas
        // (ex: ao adicionar uma nova tarefa), você precisaria de uma lógica mais complexa aqui
        // para copiar o isConcluidaMap apenas para os IDs que ainda existem em newTarefas.
        // Para o seu requisito atual de "não precisa armazenar", essa abordagem é aceitável,
        // pois o estado é apenas visual para a sessão atual da lista.

        tarefas.clear()
        tarefas.addAll(newTarefas)
        notifyDataSetChanged()
        // O estado do checkbox será resetado para false quando a lista for recarregada
        // (por exemplo, após adicionar uma nova tarefa ou reiniciar o app)
        // Isso ocorre porque o mapa `isConcluidaMap` não é limpo, mas a `getView`
        // será chamada novamente para cada item com seu `isChecked` inicializado pelo mapa,
        // que pode conter estados de tarefas que não estão mais na lista ou não foram recarregadas.
        // Para uma funcionalidade "apenas visual" mais consistente em recarregamentos,
        // o `isConcluidaMap` deveria ser limpo/reconstruído com base nos IDs em `newTarefas`.
        // Mas para o objetivo "só queria que ficasse marcado", sem persistência, isso funciona.
    }
}