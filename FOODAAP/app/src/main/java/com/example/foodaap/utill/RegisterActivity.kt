package com.example.foodaap.utill

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodaap.Module.SessionManager
import com.example.foodaap.R
import com.example.foodaap.adapter.ConnectionManager
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    lateinit var toolbar: Toolbar
    lateinit var etName: EditText
    var emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    lateinit var etnumber: EditText
    lateinit var etLocation: EditText
    lateinit var etPassword: EditText
    lateinit var etEmail: EditText
    lateinit var etCpassword: EditText
    lateinit var btnReg: Button
    lateinit var sessionManager: SessionManager
    lateinit var progressBar: ProgressBar
    lateinit var r1Register: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        sessionManager = SessionManager(this)
        sharedPreferences =
            this.getSharedPreferences(sessionManager.PREF_NAME, sessionManager.PRIVATE_MODE)
        toolbar = findViewById(R.id.toolbar)
        etName = findViewById(R.id.etName)
        etCpassword = findViewById(R.id.etCpassword)
        etEmail = findViewById(R.id.etEmail)
        etLocation = findViewById(R.id.etLocation)
        etnumber = findViewById(R.id.etnumber)
        etPassword = findViewById(R.id.etPassword)
        btnReg = findViewById(R.id.btnReg)


        setUpToolbar()
        btnReg.setOnClickListener {

            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val phn = etnumber.text.toString()
            val address = etLocation.text.toString()
            val pwd = etPassword.text.toString()
            val confirmPwd = etCpassword.text.toString()

            if ((name == "") || (email == "") || (phn == "") || (address == "") || (pwd == "") || (confirmPwd == ""))
                Toast.makeText(this@RegisterActivity, "All Fields Required", Toast.LENGTH_SHORT)
                    .show()
            else {
                if (phn.length != 10)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Phone Number Should Be Of Exact 10 Digits",
                        Toast.LENGTH_SHORT
                    ).show()
                if (pwd.length < 6 || confirmPwd.length < 6)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Minimum 4 Characters For Password",
                        Toast.LENGTH_SHORT
                    ).show()
                if (pwd != confirmPwd)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Password And Confirm Password Do Not Match",
                        Toast.LENGTH_SHORT
                    ).show()
                if (!email.trim().matches(emailPattern))
                    Toast.makeText(this@RegisterActivity, "Invalid Email", Toast.LENGTH_SHORT)
                        .show()
                else {

                    val queue = Volley.newRequestQueue(this)
                    val REGISTER = "http://13.235.250.119/v2/register/fetch_result"

                    val jsonParams = JSONObject()
                    jsonParams.put("name", name)
                    jsonParams.put("mobile_number", phn)
                    jsonParams.put("password", pwd)
                    jsonParams.put("address", address)
                    jsonParams.put("email", email)

                    if (ConnectionManager().checkConnectivity(this)) {

                        val jsonRequest =
                            object : JsonObjectRequest(
                                Method.POST, REGISTER, jsonParams,
                                Response.Listener {
                                    try {

                                        val item = it.getJSONObject("data")
                                        Toast.makeText(this, "$item", Toast.LENGTH_SHORT).show()
                                        val success = item.getBoolean("success")
                                        if (success) {

                                            val userInfoJSONObject = item.getJSONObject("data")
                                            sharedPreferences.edit().putString(
                                                "user_id",
                                                userInfoJSONObject.getString("user_id")
                                            ).apply()
                                            sharedPreferences.edit().putString(
                                                "user_name",
                                                userInfoJSONObject.getString("name")
                                            ).apply()
                                            sharedPreferences.edit().putString(
                                                "user_email",
                                                userInfoJSONObject.getString("email")
                                            ).apply()
                                            sharedPreferences.edit().putString(
                                                "user_mobile_number",
                                                userInfoJSONObject.getString("mobile_number")
                                            ).apply()
                                            sharedPreferences.edit().putString(
                                                "user_address",
                                                userInfoJSONObject.getString("address")
                                            ).apply()
                                            sessionManager.setLogin(true)
                                            val intent = Intent(
                                                this@RegisterActivity,
                                                MainActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            val errorMessage = item.getString("errorMessage")
                                            Toast.makeText(
                                                this@RegisterActivity,
                                                errorMessage,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(
                                            this,
                                            "Some Exception Occurred $e",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }, Response.ErrorListener {
                                    Log.e("Error::::", "/post request fail! Error: ${it.message}")

                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] = "801055bd4f7d19"
                                    return headers
                                }
                            }
                        queue.add(jsonRequest)
                    } else {
                        val dialog = AlertDialog.Builder(this)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection Not Found. Turn On Internet Connection And Restart App")
                        dialog.setPositiveButton("Close") { text, listner ->
                            ActivityCompat.finishAffinity(this)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }
            }
        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
        onBackPressed()
        return true
    }
}


















