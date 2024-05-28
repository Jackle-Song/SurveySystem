package com.mrsworkshop.surveysystem.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.mrsworkshop.surveysystem.R
import com.mrsworkshop.surveysystem.activity.SurveyDashboardActivity.Companion.INTENT_SURVEY_DETAILS
import com.mrsworkshop.surveysystem.databinding.ActivitySurveyResponseBinding
import com.mrsworkshop.surveysystem.model.SurveyData
import com.mrsworkshop.surveysystem.model.SurveyQuestions
import com.mrsworkshop.surveysystem.model.SurveyResponseData
import com.mrsworkshop.surveysystem.repository.SurveyRepository

class SurveyResponseActivity : BaseActivity(), SurveyRepository.SaveSurveyCallback {
    private lateinit var binding: ActivitySurveyResponseBinding

    private var surveyData : SurveyData = SurveyData()
    private var surveyResponseData : SurveyResponseData = SurveyResponseData()

    private val surveyRepository : SurveyRepository = SurveyRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val surveyDetails = intent.getStringExtra(INTENT_SURVEY_DETAILS)
        surveyData = Gson().fromJson(surveyDetails, SurveyData::class.java)

        initUI()
        setupComponentListener()
    }

    override fun onSuccess() {
        dismissLoadingViewDialog()
        finish()
    }

    override fun onFailure(exception: Exception?) {
        dismissLoadingViewDialog()
        println("save survey failed")
    }

    /**
     * private function
     */

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.Outlet,
            R.layout.spinner_item_design
        )

        adapter.setDropDownViewResource(R.layout.spinner_item_design)
        binding.spinnerOutlet.adapter = adapter

        binding.txtSurveyTitle.text = surveyData.title

        binding.layoutSurveyQuestions.removeAllViews()
        for (index in 0 until (surveyData.questions?.size ?: 0)) {
            val questionItem = surveyData.questions?.get(index)

            val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val questionLayoutView : View = layoutInflater.inflate(R.layout.item_survey_response, null)

            val txtCountSurveyQuestions = questionLayoutView.findViewById<TextView>(R.id.txtCountSurveyQuestions)
            val txtSurveyQuestionTitle = questionLayoutView.findViewById<TextView>(R.id.txtSurveyQuestionTitle)
            val radioGroupSurveyOptions = questionLayoutView.findViewById<RadioGroup>(R.id.radioGroupSurveyOptions)

            val count = index + 1
            txtCountSurveyQuestions.text = "$count."

            txtSurveyQuestionTitle.text = questionItem?.title

            for (i in 0 until (questionItem?.choices?.size ?: 0)) {
                val radioButton = RadioButton(this)
                radioButton.text = questionItem?.choices?.get(i)
                radioButton.setTextColor(ContextCompat.getColor(this, R.color.black_32))
                radioGroupSurveyOptions.addView(radioButton)
            }

            binding.layoutSurveyQuestions.addView(questionLayoutView)
        }
    }

    private fun setupComponentListener() {
        binding.btnSaveAndSubmit.setOnClickListener {
            saveSurveyData()
        }
    }

    private fun saveSurveyData() {
        showLoadingViewDialog()
        surveyResponseData.title = surveyData.title
        surveyResponseData.name = binding.etSurveyName.text.toString()
        surveyResponseData.age = binding.etSurveyAge.text.toString().toInt()
        surveyResponseData.outlet = binding.spinnerOutlet.selectedItem.toString()

        for (i in 0 until binding.layoutSurveyQuestions.childCount) {
            val questionLayoutView = binding.layoutSurveyQuestions.getChildAt(i)
            if (questionLayoutView is LinearLayout) {
                val radioGroup = questionLayoutView.findViewById<RadioGroup>(R.id.radioGroupSurveyOptions)
                val selectedRadioButtonId = radioGroup.checkedRadioButtonId
                if (selectedRadioButtonId != -1) {
                    val selectedRadioButton = radioGroup.findViewById<RadioButton>(selectedRadioButtonId)
                    val answer = selectedRadioButton.text.toString()
                    surveyResponseData.questions?.add(SurveyQuestions(surveyData.questions?.get(i)?.title, answer))
                }
            }
        }

        surveyRepository.saveSurveyResponse(surveyResponseData, this)
    }
}