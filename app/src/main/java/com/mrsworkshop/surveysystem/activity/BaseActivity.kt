package com.mrsworkshop.surveysystem.activity

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.mrsworkshop.surveysystem.component.LoadingDialog

open class BaseActivity : AppCompatActivity() {

    private lateinit var loadingViewDialog: LoadingDialog

    fun setStatusBarLightTheme(option : Boolean) {
        val windowInsetsController = WindowCompat.getInsetsController(
            window, window.decorView
        )
        windowInsetsController.isAppearanceLightStatusBars = option
    }

    fun setStatusBarColor(color : Int) {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, color)
    }

    fun showLoadingViewDialog() {
        loadingViewDialog = LoadingDialog.show(supportFragmentManager)
    }

    fun dismissLoadingViewDialog() {
        loadingViewDialog.dismiss()
    }
}