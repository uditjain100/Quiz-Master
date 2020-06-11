package udit.programmer.co.quiz.Interface

import udit.programmer.co.quiz.Models.CurrentQuestion

interface SelectAnswer {
    fun selectedAnswer(): CurrentQuestion
    fun showCorrectAnswer()
    fun disableAnswer()
    fun resetQuestion()
}