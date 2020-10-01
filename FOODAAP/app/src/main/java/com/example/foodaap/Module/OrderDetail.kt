package com.example.foodaap.Module


import org.json.JSONArray

data class OrderDetail(
    val orderId: Int,
    val resName: String,
    val orderDate: String,
    val foodItem: JSONArray

)