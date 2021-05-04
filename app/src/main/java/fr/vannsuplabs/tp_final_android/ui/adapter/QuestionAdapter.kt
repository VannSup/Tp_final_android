package fr.vannsuplabs.tp_final_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.vannsuplabs.tp_final_android.R
import fr.vannsuplabs.tp_final_android.data.model.Question
import fr.vannsuplabs.tp_final_android.ui.widget.holder.QuestionViewHolder

class QuestionAdapter : RecyclerView.Adapter<QuestionViewHolder>(){

    lateinit var onClickListener: (Question, Int) -> Unit
    lateinit var onLongClickListener: (Question, Int) -> Unit
    private val list: MutableList<Question> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        return QuestionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.question_holder, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val detail = list[position]
        holder.bindData(detail, position, onClickListener, onLongClickListener)
    }

    fun addItem(question: Question) {
        this.list.add(question)
        notifyItemChanged(this.list.lastIndex)
    }

    fun removeAt(position: Int) {
        this.list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getTaskAt(position: Int): Question {
        return this.list[position]
    }
}