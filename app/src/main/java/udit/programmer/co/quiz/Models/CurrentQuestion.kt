package udit.programmer.co.quiz.Models

import udit.programmer.co.quiz.Common.Common

data class CurrentQuestion (
    var questionIndex : Int,
    var type : Common.ANSWER_TYPE
)