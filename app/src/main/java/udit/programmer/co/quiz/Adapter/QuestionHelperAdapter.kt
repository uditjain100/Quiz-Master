package udit.programmer.co.quiz.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_question_list_helper_item.view.*
import udit.programmer.co.quiz.Common.Common
import udit.programmer.co.quiz.Interface.OnHelperRecyclerViewClickListener
import udit.programmer.co.quiz.Models.CurrentQuestion
import udit.programmer.co.quiz.R

class QuestionHelperAdapter(var answerSheetList: MutableList<CurrentQuestion>) :
    RecyclerView.Adapter<QuestionHelperViewHolder>() {

    var onHelperRecyclerViewClickListener: OnHelperRecyclerViewClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionHelperViewHolder {
        return QuestionHelperViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_question_list_helper_item, parent, false)
        )
    }

    override fun getItemCount(): Int = answerSheetList.size

    override fun onBindViewHolder(holder: QuestionHelperViewHolder, position: Int) {
        holder.bind(answerSheetList[position])
        holder.itemView.setOnClickListener {
            onHelperRecyclerViewClickListener?.onClick(position)
        }
    }

}