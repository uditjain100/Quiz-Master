package udit.programmer.co.quiz.Interface

import udit.programmer.co.quiz.Models.Category

interface OnRecyclerViewItemClickListener {
    fun onClick(category: Category)
}