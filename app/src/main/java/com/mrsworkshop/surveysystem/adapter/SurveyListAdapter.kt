package com.mrsworkshop.surveysystem.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mrsworkshop.surveysystem.R
import com.mrsworkshop.surveysystem.model.SurveyData

class SurveyListAdapter(
    private val mContext : Context,
    private val surveyList : MutableList<SurveyData>?,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class SurveyDetailsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val cardViewSurveyItem : CardView = itemView.findViewById(R.id.cardViewSurveyItem)
        val txtSurveyTitle : TextView = itemView.findViewById(R.id.txtSurveyTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_survey_list, parent, false)
        return SurveyDetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return surveyList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val surveyItem = surveyList?.get(position)
        val surveyViewHolder = holder as SurveyDetailsViewHolder

        surveyViewHolder.txtSurveyTitle.text = surveyItem?.title
    }

}