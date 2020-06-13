package udit.programmer.co.quiz.Adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_question_list_helper_item.view.*
import udit.programmer.co.quiz.Common.Common
import udit.programmer.co.quiz.Models.CurrentQuestion
import udit.programmer.co.quiz.R

class QuestionHelperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(currentQuestion: CurrentQuestion) {
        itemView.text_question_num.text = (position + 1).toString()
        itemView.text_question_num.setTextColor(Color.BLACK)
        if (currentQuestion.type == Common.ANSWER_TYPE.RIGHT_ANSWER) {
            itemView.layout_wrapper.setBackgroundResource(R.drawable.grid_right_answer_layout)
        } else if (currentQuestion.type == Common.ANSWER_TYPE.WRONG_ANSWER) {
            itemView.layout_wrapper.setBackgroundResource(R.drawable.grid_wrong_answer_layout)
        } else {
            itemView.layout_wrapper.setBackgroundResource(R.drawable.grid_item_no_answer)
        }
    }
}