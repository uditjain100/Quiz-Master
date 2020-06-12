package udit.programmer.co.quiz.Adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_result_item.view.*
import udit.programmer.co.quiz.Common.Common
import udit.programmer.co.quiz.Models.CurrentQuestion
import udit.programmer.co.quiz.Models.Question
import udit.programmer.co.quiz.R
import java.lang.StringBuilder

class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(question: CurrentQuestion) {
        itemView.btn_question.text = StringBuilder("Question ").append(question.questionIndex + 1)
        if (question.type == Common.ANSWER_TYPE.WRONG_ANSWER) {
            itemView.apply {
                btn_question.setBackgroundColor(R.drawable.grid_wrong_answer_layout)
                btn_question.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    R.drawable.ic_baseline_close_24
                )
            }
        } else if (question.type == Common.ANSWER_TYPE.RIGHT_ANSWER) {
            itemView.apply {
                btn_question.setBackgroundColor(R.drawable.grid_right_answer_layout)
                btn_question.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    R.drawable.ic_baseline_done_outline_24
                )
            }
        } else if (question.type == Common.ANSWER_TYPE.NO_ANSWER) {
            itemView.apply {
                btn_question.setBackgroundColor(R.drawable.grid_item_no_answer)
                btn_question.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    R.drawable.ic_baseline_error_outline_24
                )
            }
        }
    }
}