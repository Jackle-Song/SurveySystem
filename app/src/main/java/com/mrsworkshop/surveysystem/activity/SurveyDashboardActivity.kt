package com.mrsworkshop.surveysystem.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mrsworkshop.surveysystem.adapter.SurveyListAdapter
import com.mrsworkshop.surveysystem.databinding.ActivitySurveyDashboardBinding
import com.mrsworkshop.surveysystem.model.SurveyData

class SurveyDashboardActivity : BaseActivity() {
    private lateinit var binding: ActivitySurveyDashboardBinding
    private lateinit var database: DatabaseReference
    private lateinit var surveyListAdapter: SurveyListAdapter

    private var surveyList : MutableList<SurveyData>? = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        initUI()
        getSurveyData()
        setupComponentListener()
    }

    /**
     * private function
     */

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            surveyList?.clear()
            getSurveyData()
        }
    }


    private fun initUI() {
        surveyListAdapter = SurveyListAdapter(this@SurveyDashboardActivity, surveyList)
        binding.recyclerviewSurveyList.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewSurveyList.adapter = surveyListAdapter
    }

    private fun setupComponentListener() {
        binding.btnCreateSurvey.setOnClickListener {
            val intent = Intent(this@SurveyDashboardActivity, CreateSurveyActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun getSurveyData() {
        database.child("surveys").addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val survey = snapshot.getValue(SurveyData::class.java)
                    if (survey != null) {
                        surveyList?.add(survey)
                    }
                }
                surveyListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
                println("Error: ${databaseError.message}")
            }
        })
    }
}