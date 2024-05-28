package com.mrsworkshop.surveysystem.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mrsworkshop.surveysystem.R
import com.mrsworkshop.surveysystem.databinding.ActivityCreateSurveyBinding
import com.mrsworkshop.surveysystem.model.QuestionsData
import com.mrsworkshop.surveysystem.model.SurveyData
import com.mrsworkshop.surveysystem.repository.SurveyRepository

class CreateSurveyActivity : BaseActivity(), SurveyRepository.SaveSurveyCallback {
    private lateinit var binding: ActivityCreateSurveyBinding

    private var questionList : MutableList<QuestionsData>? = mutableListOf()
    private var surveyData : SurveyData = SurveyData()

    private val surveyRepository : SurveyRepository = SurveyRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setupComponentListener()
    }

    override fun onSuccess() {
        dismissLoadingViewDialog()
        val returnIntent = Intent()
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    override fun onFailure(exception: Exception?) {
        dismissLoadingViewDialog()
        println("failed")
    }

    /**
     * private function
     */

    private fun initUI() {
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.Outlet,
            R.layout.spinner_item_design
        )

        adapter.setDropDownViewResource(R.layout.spinner_item_design)
        binding.spinnerOutlet.adapter = adapter

        questionList?.add(QuestionsData(title = ""))
        addQuestionView()
    }

    private fun setupComponentListener() {
        binding.btnSaveAndSubmit.setOnClickListener {
            saveSurvey()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addQuestionView() {
        binding.layoutSurveyQuestions.removeAllViews()
        for (index in 0 until (questionList?.size ?: 0)) {
            val questionItem = questionList?.get(index)

            val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val questionLayoutView : View = layoutInflater.inflate(R.layout.item_survey_questions, null)

            val txtCountSurveyQuestions = questionLayoutView.findViewById<TextView>(R.id.txtCountSurveyQuestions)
            val etSurveyQuestionTitle = questionLayoutView.findViewById<EditText>(R.id.etSurveyQuestionTitle)

            val radioGroupSurveyOptions = questionLayoutView.findViewById<RadioGroup>(R.id.radioGroupSurveyOptions)
            val btnAddOption = questionLayoutView.findViewById<Button>(R.id.btnAddOption)

            val layoutAddOption = questionLayoutView.findViewById<LinearLayout>(R.id.layoutAddOption)
            val etOptionTitle = questionLayoutView.findViewById<EditText>(R.id.etOptionTitle)
            val txtSaveOption = questionLayoutView.findViewById<TextView>(R.id.txtSaveOption)
            val imgDeleteOption = questionLayoutView.findViewById<ImageView>(R.id.imgDeleteOption)
            val btnAddQuestion = questionLayoutView.findViewById<Button>(R.id.btnAddQuestion)

            val count = index + 1
            txtCountSurveyQuestions.text = "$count."

            if (!questionItem?.title.isNullOrEmpty()) {
                etSurveyQuestionTitle.setText(questionItem?.title)
            }

            radioGroupSurveyOptions.removeAllViews()
            for (i in 0 until (questionItem?.choices?.size ?: 0)) {
                val radioButton = RadioButton(this)
                radioButton.text = questionItem?.choices?.get(i)
                radioButton.setTextColor(ContextCompat.getColor(this, R.color.black_32))
                radioGroupSurveyOptions.addView(radioButton)
            }

            etSurveyQuestionTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // This method is called to notify you that the text is about to be changed.
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // This method is called to notify you that the text has changed.
                    questionItem?.title = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                    // This method is called to notify you that somewhere within s, the text has been changed.
                }
            })

            btnAddOption.setOnClickListener {
                layoutAddOption.visibility = View.VISIBLE
            }

            txtSaveOption.setOnClickListener {
                if (!etOptionTitle.text.isNullOrEmpty()) {
                    questionItem?.choices?.add((etOptionTitle.text ?: "").toString())
                    radioGroupSurveyOptions.removeAllViews()
                    for (i in 0 until (questionItem?.choices?.size ?: 0)) {
                        val radioButton = RadioButton(this)
                        radioButton.text = questionItem?.choices?.get(i)
                        radioButton.setTextColor(ContextCompat.getColor(this, R.color.black_32))
                        radioGroupSurveyOptions.addView(radioButton)
                    }
                    etOptionTitle.setText("")
                    layoutAddOption.visibility = View.GONE
                }
            }

            imgDeleteOption.setOnClickListener {
                layoutAddOption.visibility = View.GONE
            }

            if (index == questionList?.size?.minus(1)) {
                btnAddQuestion.visibility = View.VISIBLE
            }
            else {
                btnAddQuestion.visibility = View.GONE
            }

            btnAddQuestion.setOnClickListener {
                questionList?.add(QuestionsData(title = ""))
                addQuestionView()
            }

            binding.layoutSurveyQuestions.addView(questionLayoutView)
        }
    }

    private fun saveSurvey() {
        showLoadingViewDialog()
        surveyData.title = binding.etSurveyTitle.text.toString()
        surveyData.outlet = binding.spinnerOutlet.selectedItem.toString()
        surveyData.questions = questionList

        surveyRepository.saveSurvey(surveyData, this)
    }
}