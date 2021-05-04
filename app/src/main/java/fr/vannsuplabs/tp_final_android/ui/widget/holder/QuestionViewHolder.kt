package fr.vannsuplabs.tp_final_android.ui.widget.holder

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import fr.vannsuplabs.tp_final_android.R
import fr.vannsuplabs.tp_final_android.data.model.Question

class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cardView: CardView = itemView.findViewById(R.id.holder_card)
    private val questionText: TextView = itemView.findViewById(R.id.holder_question)
    private val isChecked: TextView = itemView.findViewById(R.id.holder_checked)
    private val response: TextView = itemView.findViewById(R.id.holder_response)

    fun bindData(
        question: Question,
        position: Int,
        listener: (Question, Int) -> Unit,
        longListener: (Question, Int) -> Unit
    ){

        cardView.setOnClickListener { listener(question, position) }
        cardView.setOnLongClickListener {
            longListener(question, position)
            return@setOnLongClickListener true
        }

        questionText.text = question.questionText
        response.text = question.response

        if (question.response.isEmpty()){
            isChecked.text = "KO"
            isChecked.setTextColor(Color.parseColor("#FFFFFF"))
        }else{
            isChecked.text = "OK"
            isChecked.setTextColor(Color.parseColor("#00FF00"))
        }
    }
}