package com.mrsworkshop.surveysystem.model

data class SurveyResponseData (
    var title: String? = null,
    var name : String? = null,
    var age : Int? = null,
    var outlet : String? = null,
    var questions : MutableList<SurveyQuestions>? = mutableListOf()
)

class SurveyQuestions (
    var title : String? = null,
    var answer : String? = null,
)