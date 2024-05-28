package com.mrsworkshop.surveysystem.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mrsworkshop.surveysystem.model.SurveyData

class SurveyRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    interface SaveSurveyCallback {
        fun onSuccess()
        fun onFailure(exception: Exception?)
    }

    fun saveSurvey(surveyData: SurveyData, callback: SaveSurveyCallback) {
        // Generate a unique key for each survey
        val surveyId = database.child("surveys").push().key
        if (surveyId != null) {
            database.child("surveys").child(surveyId).setValue(surveyData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Successfully saved the survey data
                        println("Survey saved successfully.")
                        callback.onSuccess()
                    } else {
                        // Failed to save the survey data
                        task.exception?.printStackTrace()
                        callback.onFailure(task.exception)
                    }
                }
        }
    }
}