package com.mayra.to_do_list_mobile.listadetarefas

data class Tarefa(val id: Int, val nome: String, var isConcluida: Boolean = false)

// Data Class é uma classe do próprio Android usada para armazenar dados, então ela está armazenando
// os valores do meu banco em tarefa que são id, nome e isConcluida
// Ele gera automáticamente funcionalidades para ajudar no código