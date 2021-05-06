package fr.vannsuplabs.tp_final_android.ui.widget.holder

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import fr.vannsuplabs.tp_final_android.R
import fr.vannsuplabs.tp_final_android.data.PAS_IMAGE_DISPONIBLE
import fr.vannsuplabs.tp_final_android.data.model.Question
import kotlinx.android.synthetic.main.activity_form.*

class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cardView: CardView = itemView.findViewById(R.id.holder_card)
    private val questionText: TextView = itemView.findViewById(R.id.holder_question)
    private val isChecked: TextView = itemView.findViewById(R.id.holder_checked)
    private val response: TextView = itemView.findViewById(R.id.holder_response)
    private  val image: AppCompatImageView = itemView.findViewById(R.id.holder_item_image)

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

        if(question.image.isEmpty()){
            Glide.with(itemView)
                .load(PAS_IMAGE_DISPONIBLE)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .into(image)
        }else{
            Glide.with(itemView)
                .load(question.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .into(image)
        }

        if (question.response.isEmpty()){
            isChecked.text = "KO"
            isChecked.setTextColor(Color.RED)
        }else{
            isChecked.text = "OK"
            isChecked.setTextColor(Color.GREEN)
        }
    }
}