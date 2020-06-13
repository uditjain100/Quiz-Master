package udit.programmer.co.quiz.Common

import udit.programmer.co.quiz.Models.Category
import udit.programmer.co.quiz.Models.CurrentQuestion
import udit.programmer.co.quiz.Models.Question
import udit.programmer.co.quiz.QuestionFragment
import java.lang.StringBuilder

object Common {

    val TOTAL_TIME = 20 * 60 * 1000
    var answer_sheet_list: MutableList<CurrentQuestion> = ArrayList()
    var answer_sheet_list_filtered: MutableList<CurrentQuestion> = ArrayList()
    var questionList: MutableList<Question> = ArrayList()
    var selectedCategory: Category? = null
    var fragmentList: MutableList<QuestionFragment> = ArrayList()
    var selected_values: MutableList<String> = ArrayList()

    val KEY_GO_TO_QUESTION: String? = "position_go_to"
    val KEY_BACK_FROM_RESULT: String? = "back_from_result"
    val KEY_ONLINE_MODE = "ONLINE_MODE"

    var isOnline = false
    var timer = 0
    var right_answer_count = 0
    var wrong_answer_count = 0
    var no_answer_count = 0
    var data_question = StringBuilder()

    enum class ANSWER_TYPE {
        NO_ANSWER,
        RIGHT_ANSWER,
        WRONG_ANSWER
    }

}