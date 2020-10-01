package com.example.foodaap.utill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodaap.Module.Validations
import com.example.foodaap.R
import com.example.foodaap.adapter.ConnectionManager
import org.json.JSONObject


class ForgotActivity : AppCompatActivity() {
    lateinit var toolBar: Toolbar
    lateinit var etForgotMobile: EditText
    lateinit var etForgotEmail: EditText
    lateinit var btnNext: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        etForgotMobile = findViewById(R.id.etForgotMobile)
        etForgotEmail = findViewById(R.id.etForgotEmail)
        btnNext = findViewById(R.id.btnNext)




        btnNext.setOnClickListener {

            val forgotMobileNumber = etForgotMobile.text.toString()
            if (Validations.validateMobile(forgotMobileNumber)) {
                etForgotMobile.error = null
                if (Validations.validateEmail(etForgotEmail.text.toString())) {
                    if (ConnectionManager().checkConnectivity(this@ForgotActivity)) {



                        sendOTP(etForgotMobile.text.toString(), etForgotEmail.text.toString())
                    } else {

                        Toast.makeText(
                            this@ForgotActivity,
                            "No Internet Connection!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {

                    etForgotEmail.error = "Invalid Email"
                }
            } else {

                etForgotMobile.error = "Invalid Mobile Number"
            }
        }
    }

    private fun sendOTP(mobileNumber: String, email: String) {
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("email", email)
        val FORGOT_PASSWORD= "http://13.235.250.119/v2/forgot_password/fetch_result"
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, FORGOT_PASSWORD, jsonParams, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")

                    if (success) {
                        val firstTry = data.getBoolean("first_try")
                        if (firstTry) {

                            val builder = AlertDialog.Builder(this@ForgotActivity)
                            builder.setTitle("Information")
                            builder.setMessage("Please check your registered Email for the OTP.")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") { _, _ ->
                                val intent = Intent(
                                    this@ForgotActivity,
                                    ResetActivity::class.java
                                )
                                intent.putExtra("user_mobile", mobileNumber)
                                startActivity(intent)
                            }
                            builder.create().show()
                        } else {

                            val builder = AlertDialog.Builder(this@ForgotActivity)
                            builder.setTitle("Information")
                            builder.setMessage("Please refer to the previous email for the OTP.")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") { _, _ ->
                                val intent = Intent(
                                    this@ForgotActivity,
                                    ResetActivity::class.java
                                )
                                intent.putExtra("user_mobile", mobileNumber)
                                startActivity(intent)
                            }
                            builder.create().show()
                        }
                    } else {

                        Toast.makeText(
                            this@ForgotActivity,
                            "Mobile number not registered!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                    Toast.makeText(
                        this@ForgotActivity,
                        "Incorrect response error!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {

                VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
                Toast.makeText(this@ForgotActivity, it.message, Toast.LENGTH_SHORT).show()
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

    }


    fun setUpToolbar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = "Back"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}