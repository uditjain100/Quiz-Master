package udit.programmer.co.quiz.Interface

import udit.programmer.co.quiz.Models.Question

interface MyCallback {
    fun setQuestionsList(questionList: MutableList<Question>)
}