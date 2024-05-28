package com.mrsworkshop.surveysystem.model

data class QuestionsData (
    var title : String? = null,
    var choices : MutableList<String?>? = mutableListOf(),
)