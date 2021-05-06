package fr.vannsuplabs.tp_final_android.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import fr.vannsuplabs.tp_final_android.R
import fr.vannsuplabs.tp_final_android.data.CAMERA_CODE
import fr.vannsuplabs.tp_final_android.data.PAS_IMAGE_DISPONIBLE
import fr.vannsuplabs.tp_final_android.data.QUESTION_COLLECTION
import fr.vannsuplabs.tp_final_android.data.QUESTION_ID
import fr.vannsuplabs.tp_final_android.data.model.Question
import fr.vannsuplabs.tp_final_android.utils.disable
import fr.vannsuplabs.tp_final_android.utils.hide
import fr.vannsuplabs.tp_final_android.utils.show
import kotlinx.android.synthetic.main.activity_form.*
import java.io.File

class FormActivity : AppCompatActivity() {
    private var questionId: String? = null
    private var questionFromDB: Question? = null
    private val database = FirebaseFirestore.getInstance()
    private val questionReference = database.collection(QUESTION_COLLECTION)

    private var photoImagePath = ""
    private val storageReference = FirebaseStorage.getInstance().reference
    private var file: File = File("", "")
    private var fileUri: Uri = Uri.fromFile(file)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        questionId = intent.getStringExtra(QUESTION_ID)
        initView()
        if(!questionId.isNullOrEmpty()){
            retrieveData(questionId!!)
        }
        setupActionButton()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
        )
        this.finish()
        super.onBackPressed()
    }

    private fun initView(){
        if(!questionId.isNullOrEmpty()){
            delete.show()
            text_input_layout_response.show()
            question.disable()
            add_button_form.setImageDrawable(getDrawable(R.drawable.ic_pen))
        }
    }

    private fun retrieveData(id : String) {
        questionReference.document(id).get().addOnCompleteListener{ task  ->
            if (task.isSuccessful)
            {
                questionFromDB = task.result?.toObject(Question::class.java)?.apply {
                    this.firebaseId = task.result?.id!!
                }
                question.setText(questionFromDB?.questionText)
                response.setText(questionFromDB?.response)
                if (!questionFromDB?.image.isNullOrEmpty()){
                    Glide.with(this)
                            .load(questionFromDB?.image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .circleCrop()
                            .into(image_view)
                    photoImagePath = questionFromDB?.image!!
                }else{
                    Glide.with(this)
                            .load(PAS_IMAGE_DISPONIBLE)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .circleCrop()
                            .into(image_view)
                }
            }
        }
    }
    private fun setupActionButton() {
        add_button_form.setOnClickListener {
            if (questionId.isNullOrEmpty()) {
                addQuestion(Question("",question.text.toString(),"",photoImagePath))
            }
            else {
                updateQuestion(Question(questionId!!,questionFromDB!!.questionText,response.text.toString(),photoImagePath))
            }
        }

        delete.setOnClickListener {
            deleteQuestion(Question(questionId!!,"","",""))
        }

        image_view.setOnClickListener {
            showCameraInterface()
        }
    }

    // region FireBaseAction
    private fun addQuestion(question:Question){
        val questionMap = question.ToHashMap()

        questionReference
                .add(questionMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Ajouté avec succès", Toast.LENGTH_LONG).show()
                    onBackPressed()
                }
    }

    private fun updateQuestion(question: Question){
        val questionMap = question.ToHashMap()

        questionReference
                .document(question.firebaseId)
                .update(questionMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Modifier avec succès", Toast.LENGTH_LONG).show()
                    onBackPressed()
                }
    }

    private fun deleteQuestion(question: Question){
        questionReference.document(question.firebaseId).delete().addOnSuccessListener {
            Toast.makeText(this, "Supprimer avec succès", Toast.LENGTH_LONG).show()
            onBackPressed()
        }
    }
    // endregion

    // region Photo
    private fun showCameraInterface() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        file = File(this.externalCacheDir, (System.currentTimeMillis()).toString() + ".jpg")
        fileUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(intent, CAMERA_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            photo_detail_progress_bar_text.show()
            photo_detail_progress_bar.show()
            val ref = storageReference.child("images/" + System.currentTimeMillis())
            val uploadTask = ref.putFile(fileUri)
            uploadTask.addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
                photo_detail_progress_bar.progress =  progress.toInt()
            }.addOnFailureListener {
                Toast.makeText(this, "Téléchargement échoué", Toast.LENGTH_SHORT).show()
                photo_detail_progress_bar_text.hide()
                photo_detail_progress_bar.hide()
            }.addOnSuccessListener {
                Toast.makeText(this, "Image téléchargée avec succès", Toast.LENGTH_SHORT).show()
                photo_detail_progress_bar_text.hide()
                photo_detail_progress_bar.hide()
            }.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Glide.with(this)
                            .load(downloadUri)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .circleCrop()
                            .into(image_view)
                    photoImagePath = downloadUri.toString()
                }
            }
        }
    }
    //endregion

}