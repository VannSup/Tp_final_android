package fr.vannsuplabs.tp_final_android.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bd_mobile.utils.SwipeToDeleteCallback
import com.google.firebase.firestore.FirebaseFirestore
import fr.vannsuplabs.tp_final_android.R
import fr.vannsuplabs.tp_final_android.data.QUESTION_COLLECTION
import fr.vannsuplabs.tp_final_android.data.QUESTION_ID
import fr.vannsuplabs.tp_final_android.data.model.Question
import fr.vannsuplabs.tp_final_android.ui.adapter.QuestionAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: QuestionAdapter
    private val database = FirebaseFirestore.getInstance()
    private val questionReference = database.collection(QUESTION_COLLECTION)
    private var originalList = mutableListOf<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecyclerView()
        retrieveData()
        setupActionButton()
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = QuestionAdapter()
        adapter.onClickListener = this::onClickListener
        adapter.onLongClickListener = this::onLongClickListener
        recycler_view.adapter = adapter
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val questionToRemove = adapter.getQuestionAt(viewHolder.adapterPosition)
                adapter.removeAt(viewHolder.adapterPosition)
                deleteQuestion(questionToRemove)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recycler_view)
    }

    private fun retrieveData() {
        originalList.clear()
         questionReference.get().addOnCompleteListener{ task ->
             if (task.isSuccessful) {
                 task.result?.map { document ->
                     val question = document.toObject(Question::class.java).apply {
                         this.firebaseId = document.id
                     }
                     originalList.add(question)
                 }
                 if (originalList.isNotEmpty()){
                     setupRecyclerView(originalList)
                 }
             }
         }
    }

    private fun setupRecyclerView(questionList: MutableList<Question>) {
        adapter.setList(questionList)
    }

    private fun onClickListener(question: Question, position: Int) {
        val intent = Intent(this, FormActivity::class.java)
        intent.putExtra(QUESTION_ID, question.firebaseId)
        startActivity(intent)
        overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        )
        this.finish()
    }

    private fun onLongClickListener(question: Question, position: Int){}

    private fun setupActionButton() {
        add_button_main.setOnClickListener{
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            )
            this.finish()
        }
    }

    // region FireBaseAction
    private fun deleteQuestion(question: Question){
        questionReference.document(question.firebaseId).delete().addOnSuccessListener {
            Toast.makeText(this, "Supprimer avec succès", Toast.LENGTH_LONG).show()
        }
    }
    // endregion
}