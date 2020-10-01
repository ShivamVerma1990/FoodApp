package com.example.foodaap.utill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodaap.Module.Validations
import com.example.foodaap.R
import com.example.foodaap.adapter.ConnectionManager

import org.json.JSONObject

class ResetActivity : AppCompatActivity() {
    lateinit var etOtp: EditText
    lateinit var etNewpassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button
    lateinit var mobileNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)
        etOtp = findViewById(R.id.etOtp)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etNewpassword = findViewById(R.id.etNewpassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        if (intent != null) {
            mobileNumber = intent.getStringExtra("user_mobile") as String
        }



        btnSubmit.setOnClickListener {

            val newPassword = etNewpassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val Otp = etOtp.text.toString()
            if (ConnectionManager().checkConnectivity(this@ResetActivity)) {
                if (Otp.length == 4) {
                    if (Validations.validatePasswordLength(etNewpassword.text.toString())) {
                        if (Validations.validatematchPassword(etNewpassword.text.toString(), etConfirmPassword.text.toString())) {


                            sendRequestOTP(
                                mobileNumber,
                                etNewpassword.text.toString(),
                                etOtp.text.toString()
                            )


                        } else {
                            Toast.makeText(
                                this@ResetActivity,
                                "Password did not match",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            etConfirmPassword.error = "Wrong Password"
                        }


                    } else {
                        etNewpassword.error = "invalid password"
                    }


                }
                    else {
                        etOtp.error = "Wrong OTP"

                        Toast.makeText(this@ResetActivity, "CHECK YOUR OTP", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(
                        this@ResetActivity,
                        "Internet is not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }

    }

    fun sendRequestOTP(mobileNumber: String, password: String, otp: String) {
        val queue = Volley.newRequestQueue(this@ResetActivity)
        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("password", password)
        jsonParams.put("otp", otp)
        val RESET_PASSWORD = "http://13.235.250.119/v2/reset_password/fetch_result"

     val jsonRequest=   object : JsonObjectRequest(Method.POST, RESET_PASSWORD, jsonParams, Response.Listener {
            try {


                val data = it.getJSONObject("data")
                val success = data.getBoolean("success")

                if (success) {

                    val dialog = AlertDialog.Builder(this@ResetActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("otp send recive")
                    dialog.setCancelable(false)
                    dialog.setPositiveButton("ok") { _, _ ->
                        val intent = Intent(this@ResetActivity, LoginActivity::class.java)
                        startActivity(intent)



                    }
                    dialog.create().show()


                } else {

                    val error = data.getString("errorMessage")
                    Toast.makeText(
                        this@ResetActivity,
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ResetActivity,
                    "JSON EXCEPTION",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()


            }

        }, Response.ErrorListener {


         VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
         Toast.makeText(this@ResetActivity, it.message, Toast.LENGTH_SHORT).show()
        })

        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>()
                headers["Content-type"]="application/json"
                headers["token"]="801055bd4f7d19"
    return headers
            }


    }

queue.add(jsonRequest)
}










}
