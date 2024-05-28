package com.mrsworkshop.surveysystem.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mrsworkshop.surveysystem.R
import com.mrsworkshop.surveysystem.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : BaseActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var mAuth : FirebaseAuth

    private var isSignUp : Boolean = false

    private var isSignUpValid : Boolean = false
    private var signUpEmail : String? = null
    private var signUpPassword : String? = null

    private var isLoginValid : Boolean = false
    private var loginEmail : String? = null
    private var loginPassword : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null) {
            val intent = Intent(this@AuthenticationActivity, SurveyDashboardActivity::class.java)
            startActivity(intent)
        }

        checkButtonValidation(false)

        setupComponentListener()
    }

    /**
     * private function
     */

    private fun setupComponentListener() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })

        binding.etEditTextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    binding.tilOutlinedEmailTextField.error = getString(R.string.auth_activity_sign_up_email_error_text)
                    checkButtonValidation(false)
                }
                else {
                    binding.tilOutlinedEmailTextField.error = null
                    checkButtonValidation(true)
                }

                if (isSignUp) {
                    signUpEmail = s.toString()
                }
                else {
                    loginEmail = s.toString()
                }
            }
        })

        binding.etEditTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                val specialCharacterRegex = "[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]"
                val letterRegex = "[a-zA-Z]"
                val numberRegex = "\\d"

                if (isSignUp) {
                    if (!s.contains(Regex(specialCharacterRegex)) || !s.contains(Regex(letterRegex)) || !s.contains(Regex(numberRegex)) || s.length < 8) {
                        binding.tilOutlinedPasswordTextField.error = getString(R.string.auth_activity_sign_up_password_error_text)
                        checkButtonValidation(false)
                    }
                    else {
                        binding.tilOutlinedPasswordTextField.error = null
                        checkButtonValidation(true)
                    }
                    signUpPassword = s.toString()
                }
                else {
                    binding.tilOutlinedPasswordTextField.error = null
                    loginPassword = s.toString()
                    checkButtonValidation(true)
                }
            }
        })

        binding.txtLoginSignUp.setOnClickListener {
            isSignUp = !isSignUp
            if (!isSignUp) {
                binding.btnAuthenticationLoginSignUp.text = getString(R.string.auth_activity_login_text)
                binding.txtAccountExistedQuestion.text = getString(R.string.auth_activity_do_not_have_account_text)
                binding.txtLoginSignUp.text = getString(R.string.auth_activity_signup_text)
                binding.etEditTextEmail.setText("")
                binding.etEditTextPassword.setText("")
                binding.tilOutlinedEmailTextField.error = null
                binding.tilOutlinedPasswordTextField.error = null
            }
            else {
                binding.btnAuthenticationLoginSignUp.text = getString(R.string.auth_activity_signup_text)
                binding.txtAccountExistedQuestion.text = getString(R.string.auth_activity_already_have_account_text)
                binding.txtLoginSignUp.text = getString(R.string.auth_activity_login_text)
                binding.etEditTextEmail.setText("")
                binding.etEditTextPassword.setText("")
                binding.tilOutlinedEmailTextField.error = null
                binding.tilOutlinedPasswordTextField.error = null
            }
        }

        binding.btnAuthenticationLoginSignUp.setOnClickListener {
            if (isSignUp) {
                registerUser(signUpEmail ?: "", signUpPassword ?: "")
            }
            else {
                loginUser(loginEmail ?: "", loginPassword ?: "")
            }
        }
    }

    private fun checkButtonValidation(isValid: Boolean) {
        if (isSignUp) {
            if (!isValid || signUpEmail.isNullOrEmpty() || signUpPassword.isNullOrEmpty()) {
                binding.btnAuthenticationLoginSignUp.alpha = 0.5f
                binding.btnAuthenticationLoginSignUp.isEnabled = false
                isSignUpValid = false
            }
            else {
                binding.btnAuthenticationLoginSignUp.alpha = 1f
                binding.btnAuthenticationLoginSignUp.isEnabled = true
                isSignUpValid = true
            }
        }
        else {
            if (!isValid || loginEmail.isNullOrEmpty() || loginPassword.isNullOrEmpty()) {
                binding.btnAuthenticationLoginSignUp.alpha = 0.5f
                binding.btnAuthenticationLoginSignUp.isEnabled = false
                isLoginValid = false
            }
            else {
                binding.btnAuthenticationLoginSignUp.alpha = 1f
                binding.btnAuthenticationLoginSignUp.isEnabled = true
                isLoginValid = true
            }
        }
    }

    private fun registerUser(email : String, password : String) {
        showLoadingViewDialog()
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@AuthenticationActivity, getString(R.string.auth_activity_sign_up_successful_text), Toast.LENGTH_LONG).show()
                    dismissLoadingViewDialog()

                    val intent = Intent(this@AuthenticationActivity, SurveyDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@AuthenticationActivity, getString(R.string.auth_activity_sign_up_failed_text), Toast.LENGTH_LONG).show()
                    dismissLoadingViewDialog()
                }
            }
    }

    private fun loginUser(email : String, password : String) {
        showLoadingViewDialog()
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dismissLoadingViewDialog()

                    val intent = Intent(this@AuthenticationActivity, SurveyDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@AuthenticationActivity, getString(R.string.auth_activity_login_failed_text), Toast.LENGTH_LONG).show()
                    dismissLoadingViewDialog()
                }
            }
    }
}