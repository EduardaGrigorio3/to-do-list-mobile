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
    private val onEditClick: (Tarefa) -> Unit,
    private val onToggleConcluida: (Tarefa, Boolean) -> Unit
) : ArrayAdapter<Tarefa>(context, 0, tarefas) {

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

        checkBoxConcluida?.isChecked = currentTarefa?.isConcluida ?: false

        nomeTextView?.let { textView ->
            if (currentTarefa?.isConcluida == true) {
                textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        checkBoxConcluida?.setOnCheckedChangeListener { _, isChecked ->
            currentTarefa?.let {

                it.isConcluida = isChecked

                onToggleConcluida(it, isChecked)

                nomeTextView?.let { textView ->
                    if (isChecked) {
                        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }
            }
        }

        buttonDelete?.setOnClickListener {
            currentTarefa?.let {
                onDeleteClick(it)
            }
        }

        buttonEdit?.setOnClickListener {
            currentTarefa?.let {
                onEditClick(it)
            }
        }

        return listItemView!!
    }

    fun updateTarefas(newTarefas: List<Tarefa>) {
        tarefas.clear()
        tarefas.addAll(newTarefas)
        notifyDataSetChanged()
    }
}