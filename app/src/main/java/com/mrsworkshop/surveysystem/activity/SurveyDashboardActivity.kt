package com.mrsworkshop.surveysystem.activity

import android.os.Bundle
import com.mrsworkshop.surveysystem.databinding.ActivitySurveyDashboardBinding

class SurveyDashboardActivity : BaseActivity() {
    private lateinit var binding: ActivitySurveyDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}