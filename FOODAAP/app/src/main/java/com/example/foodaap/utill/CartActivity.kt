package com.example.foodaap.utill

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodaap.Module.FoodItem
import com.example.foodaap.R
import com.example.foodaap.adapter.CartRecyclerAdapter
import com.example.foodaap.adapter.RestaurantMenuAdapter
import com.example.foodaap.database.OrderEntity
import com.example.foodaap.database.RestaurantDatabase
import com.example.foodaap.fragment.MenuFragment
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var toolbars: Toolbar
    private lateinit var recyclerCartItems: RecyclerView
    private lateinit var cartItemAdapter: CartRecyclerAdapter
    private var orderList = ArrayList<FoodItem>()
    private lateinit var txtResName: TextView
    private lateinit var rlLoading: RelativeLayout
    private lateinit var rlCart: RelativeLayout
    private lateinit var btnPlaceOrder: Button
    private var resId: Int = 0
    private var resName: String = ""
lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        init()
        setUpToolbar()
        setUpCartList()
        placeOrder()

    }

    private fun init() {
        rlLoading = findViewById(R.id.rlLoading)
        rlCart = findViewById(R.id.rlCart)
        txtResName = findViewById(R.id.txtCartResName)
        txtResName.text = MenuFragment.resName

        if(intent!=null) {

            val bundle = intent.getBundleExtra("data")
            resId = bundle?.getInt("resId", 0) as Int
            resName = bundle.getString("resName", "") as String
        }
        else {

        Toast.makeText(this,"error",Toast.LENGTH_SHORT).show()
        }
        }


     private fun setUpToolbar() {
        toolbars = findViewById(R.id.toolbars)
        setSupportActionBar(toolbars)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private fun setUpCartList() {
        recyclerCartItems= findViewById(R.id.recyclerCartItems)

        val dbList = GetItemsFromDBAsync(
            applicationContext
        ).execute().get()


        for (element in dbList) {
            orderList.addAll(
                Gson().fromJson(element.foodItems, Array<FoodItem>::class.java).asList()
            )
        }


        if (orderList.isEmpty()) {
            rlCart.visibility = View.GONE
            rlLoading.visibility = View.VISIBLE
        } else {
            rlCart.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
        }


        cartItemAdapter = CartRecyclerAdapter(orderList,this@CartActivity)
        val mLayoutManager = LinearLayoutManager(this@CartActivity)
        recyclerCartItems.layoutManager = mLayoutManager
        recyclerCartItems.itemAnimator = DefaultItemAnimator()
        recyclerCartItems.adapter = cartItemAdapter
    }


    private fun placeOrder() {
        btnPlaceOrder = findViewById(R.id.btnConfirmOrder)

        /*Before placing the order, the user is displayed the price or the items on the button for placing the orders*/
        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].cost as Int
        }
        val total = "Place Order(Total: Rs. $sum)"
        btnPlaceOrder.text = total

        btnPlaceOrder.setOnClickListener {
            rlLoading.visibility = View.VISIBLE
            rlCart.visibility = View.INVISIBLE

            sendServerRequest()
        }
    }

    private fun sendServerRequest() {

            val queue = Volley.newRequestQueue(this)

            /*Creating the json object required for placing the order*/
            val jsonParams = JSONObject()
        jsonParams.put(
            "user_id",
            this@CartActivity.getSharedPreferences("FoodAap", Context.MODE_PRIVATE).getString(
                "user_id",
                null
            ) as String
        )

            jsonParams.put("restaurant_id", MenuFragment.resId?.toString() as String)
            var sum = 0
            for (i in 0 until orderList.size) {
                sum += orderList[i].cost as Int
            }
            jsonParams.put("total_cost", sum.toString())
            val foodArray = JSONArray()
            for (i in 0 until orderList.size) {
                val foodId = JSONObject()
                foodId.put("food_item_id", orderList[i].id)
                foodArray.put(i, foodId)
            }
            jsonParams.put("food", foodArray)

            val PLACE_ORDER = "http://13.235.250.119/v2/place_order/fetch_result/"
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, PLACE_ORDER, jsonParams, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val clearCart =
                                ClearDBAsync(
                                    applicationContext,
                                    resId.toString()
                                ).execute().get()
                            RestaurantMenuAdapter.isCartEmpty = true
                            val dialog = Dialog(
                                this@CartActivity,
                                android.R.style.Theme_Black_NoTitleBar_Fullscreen
                            )
                            dialog.setContentView(R.layout.order_placed_dialog)
                            dialog.show()
                            dialog.setCancelable(false)
                            val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                            btnOk.setOnClickListener {
                                dialog.dismiss()
                                startActivity(Intent(this@CartActivity, MainActivity::class.java))
                                ActivityCompat.finishAffinity(this@CartActivity)
                            }
                        } else {
                            rlCart.visibility = View.VISIBLE
                            Toast.makeText(
                                this@CartActivity,
                                "Some Error occurred",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } catch (e: Exception) {
                        rlCart.visibility = View.VISIBLE
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    rlCart.visibility = View.VISIBLE
                    Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"

                        //The below used token will not work, kindly use the token provided to you in the training
                        headers["token"] = "801055bd4f7d19"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)



    }
    /*Asynctask class for extracting the items from the database*/
    class GetItemsFromDBAsync(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

            return db.orderDao().getAllOrders()
        }

    }

    /*Asynctask class for clearing the recently added items from the database*/
    class ClearDBAsync(val context: Context, private val resId: String) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }

    }


    override fun onBackPressed() {
        var clearCart =
            ClearDBAsync(
                applicationContext,
                resId.toString()
            ).execute().get()
        RestaurantMenuAdapter.isCartEmpty = true





        super.onBackPressed()
    }


    }


