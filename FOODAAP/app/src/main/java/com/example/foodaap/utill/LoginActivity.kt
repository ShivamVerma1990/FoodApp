package com.example.foodaap.utill

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodaap.Module.SessionManager
import com.example.foodaap.Module.Validations
import com.example.foodaap.R
import com.example.foodaap.adapter.ConnectionManager
import org.json.JSONException
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    lateinit var btnLogin: Button
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var txtRegister: TextView
    lateinit var txtForgot: TextView
    lateinit var sharedpreferences: SharedPreferences
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        txtForgot = findViewById(R.id.txtForgot)
        txtRegister = findViewById(R.id.txtRegister)
        sessionManager = SessionManager(this)


        sharedpreferences =
            this.getSharedPreferences(sessionManager.PREF_NAME, sessionManager.PRIVATE_MODE)

        val isLog = sharedpreferences.getBoolean("isLog", false)
        if (isLog) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }


        /*Clicking on this text takes you to the forgot password activity*/
        txtForgot.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotActivity::class.java))
        }

        /*Clicking on this text takes you to the forgot password activity*/
        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val mobileNumber = etMobileNumber.text.toString()
        val password = etPassword.text.toString()

        /*Start the login process when the user clicks on the login button*/
        btnLogin.setOnClickListener {
            /*Hide the login button when the process is going on*/
            btnLogin.visibility = View.INVISIBLE
            /*First validate the mobile number and password length*/
            if (Validations.validateMobile(etMobileNumber.text.toString()) && Validations.validatePasswordLength(
                    etPassword.text.toString()
                )
            ) {
                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {

                    /*Create the queue for the request*/
                    val queue = Volley.newRequestQueue(this@LoginActivity)

                    /*Create the JSON parameters to be sent during the login process*/
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", etMobileNumber.text.toString())
                    jsonParams.put("password", etPassword.text.toString())


                    val LOGIN = "http://13.235.250.119/v2/login/fetch_result"
                    /*Finally send the json object request*/
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, LOGIN, jsonParams,
                            Response.Listener {

                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    if (success) {
                                        val response = data.getJSONObject("data")
                                        sharedpreferences.edit()
                                            .putString("user_id", response.getString("user_id"))
                                            .apply()
                                        sharedpreferences.edit()
                                            .putString("user_name", response.getString("name"))
                                            .apply()
                                        sharedpreferences.edit()
                                            .putString(
                                                "user_mobile_number",
                                                response.getString("mobile_number")
                                            )
                                            .apply()
                                        sharedpreferences.edit()
                                            .putString(
                                                "user_address",
                                                response.getString("address")
                                            )
                                            .apply()
                                        sharedpreferences.edit()
                                            .putString("user_email", response.getString("email"))
                                            .apply()
                                  savePrefrence()
                                        sessionManager.setLogin(true)
                                        startActivity(
                                            Intent(
                                                this@LoginActivity, MainActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        btnLogin.visibility = View.VISIBLE
                                        txtForgot.visibility = View.VISIBLE
                                        btnLogin.visibility = View.VISIBLE
                                        val errorMessage = data.getString("errorMessage")
                                        Toast.makeText(
                                            this@LoginActivity,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    btnLogin.visibility = View.VISIBLE
                                    txtForgot.visibility = View.VISIBLE
                                    txtRegister.visibility = View.VISIBLE
                                    e.printStackTrace()
                                }
                            },
                            Response.ErrorListener {
                                btnLogin.visibility = View.VISIBLE
                                txtForgot.visibility = View.VISIBLE
                                txtRegister.visibility = View.VISIBLE
                                Log.e("Error::::", "/post request fail! Error: ${it.message}")
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"

                                /*The below used token will not work, kindly use the token provided to you in the training*/
                                headers["token"] = "801055bd4f7d19"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)

                } else {
                    btnLogin.visibility = View.VISIBLE
                    txtForgot.visibility = View.VISIBLE
                    txtRegister.visibility = View.VISIBLE
                    Toast.makeText(this@LoginActivity, "No internet Connection", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                btnLogin.visibility = View.VISIBLE
                txtForgot.visibility = View.VISIBLE
                txtRegister.visibility = View.VISIBLE
                Toast.makeText(this@LoginActivity, "Invalid Phone or Password", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }
    fun savePrefrence()
    {
        sharedpreferences.edit().putBoolean("isLog",true).apply()
}
}

