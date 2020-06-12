package udit.programmer.co.quiz.Interface

import udit.programmer.co.quiz.Models.CurrentQuestion

interface OnResultItemClickListener {
    fun onClick(question: CurrentQuestion)
}