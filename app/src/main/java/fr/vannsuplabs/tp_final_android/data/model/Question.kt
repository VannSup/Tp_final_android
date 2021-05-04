package fr.vannsuplabs.tp_final_android.data.model

import java.io.Serializable


data class Question(
    var firebaseId: String = "",
    var questionText: String = "",
    var response: String =""
) : Serializable
