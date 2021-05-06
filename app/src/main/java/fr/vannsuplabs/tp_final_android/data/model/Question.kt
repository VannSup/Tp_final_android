package fr.vannsuplabs.tp_final_android.data.model

import java.io.Serializable


data class Question(
    var firebaseId: String = "",
    var questionText: String = "",
    var response: String ="",
    var image: String=""
) : Serializable
{
    public fun ToHashMap(): HashMap<String, Any>
    {
        val questionMap = HashMap<String, Any>()
        questionMap["questionText"] = this.questionText
        questionMap["response"] = this.response
        questionMap["image"] = this.image
        return  questionMap
    }
}
