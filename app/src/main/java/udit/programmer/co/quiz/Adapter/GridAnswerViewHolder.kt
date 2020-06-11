package udit.programmer.co.quiz.Adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.grid_answer_layout.view.*
import udit.programmer.co.quiz.Common.Common
import udit.programmer.co.quiz.Models.CurrentQuestion
import udit.programmer.co.quiz.R

class GridAnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(question: CurrentQuestion) {
        when (question.type) {
            Common.ANSWER_TYPE.NO_ANSWER -> {
                itemView.question_item.setBackgroundResource(R.drawable.grid_item_no_answer)
            }
            Common.ANSWER_TYPE.RIGHT_ANSWER -> {
                itemView.question_item.setBackgroundResource(R.drawable.grid_right_answer_layout)
            }
            Common.ANSWER_TYPE.WRONG_ANSWER -> {
                itemView.question_item.setBackgroundResource(R.drawable.grid_wrong_answer_layout)
            }
        }
    }
}