package com.mrsworkshop.surveysystem.model

data class SurveyData (
    var title : String? = null,
    var outlet : String? = null,
    var questions : MutableList<QuestionsData>? = mutableListOf()
)