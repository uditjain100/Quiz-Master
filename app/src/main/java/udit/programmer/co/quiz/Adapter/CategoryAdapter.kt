package udit.programmer.co.quiz.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_layout.view.*
import udit.programmer.co.quiz.Interface.OnRecyclerViewItemClickListener
import udit.programmer.co.quiz.Models.Category
import udit.programmer.co.quiz.R

class CategoryAdapter(var list: MutableList<Category>) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    var onRecyclerViewItemClickListener: OnRecyclerViewItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.category_layout, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.category_tv.setOnClickListener {
            onRecyclerViewItemClickListener?.onClick(list[position])
        }
    }
}