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

        checkBoxConcluida?.isChecked = isConcluidaMap[currentTarefa?.id] ?: false

        nomeTextView?.let { textView ->
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
                nomeTextView?.let { textView ->
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
        tarefas.clear()
        tarefas.addAll(newTarefas)
        notifyDataSetChanged()
    }
}