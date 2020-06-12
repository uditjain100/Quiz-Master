package udit.programmer.co.quiz.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import udit.programmer.co.quiz.Interface.OnResultItemClickListener
import udit.programmer.co.quiz.Models.CurrentQuestion
import udit.programmer.co.quiz.R

class ResultAdapter(internal var list: MutableList<CurrentQuestion>) :
    RecyclerView.Adapter<ResultViewHolder>() {

    var onResultItemClickListener: OnResultItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_result_item, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            onResultItemClickListener?.onClick(list[position])
        }
    }
}