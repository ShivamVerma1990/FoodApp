package com.example.foodaap.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodaap.Module.FoodItem
import com.example.foodaap.R
import kotlinx.android.synthetic.main.cartadapter.view.*

class CartRecyclerAdapter(private val cart:ArrayList<FoodItem>, val context: Context):RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>(){
  class CartViewHolder(view: View):RecyclerView.ViewHolder(view)
  {
      val txtCartItemName:TextView=view.findViewById(R.id.txtCartItemName)
      val txtCartPrice:TextView=view.findViewById(R.id.txtCartPrice)

  }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.cartadapter,parent,false)
    return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cart.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartList=cart[position]
holder.txtCartItemName.text=cartList.name
    val cost="Rs.${cartList.cost}"
    holder.txtCartPrice.text=cost

    }
}