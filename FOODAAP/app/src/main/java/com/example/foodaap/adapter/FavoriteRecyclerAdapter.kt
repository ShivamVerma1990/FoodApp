package com.example.foodaap.adapter

import android.content.Context
import android.os.Build
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodaap.R
import com.example.foodaap.database.RestaurantEntity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclercool.view.*

class FavoriteRecyclerAdapter(context:Context, private val restaurantList:List<RestaurantEntity>):RecyclerView.Adapter<FavoriteRecyclerAdapter.FavoriteViewHolder>()
{

class FavoriteViewHolder(view: View):RecyclerView.ViewHolder(view)
{
   val imgImage:ImageView=view.findViewById(R.id.imgImage)
   val txtName:TextView=view.findViewById(R.id.txtName)
  val  txtPrice:TextView=view.findViewById(R.id.txtPrice)
   val txtRating:TextView=view.findViewById(R.id.txtRating)
   val imgLove:ImageView=view.findViewById(R.id.imgLove)


}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {

val view=LayoutInflater.from(parent.context).inflate(R.layout.favorite_recycler_cool,parent,false)
        return FavoriteViewHolder(view)
    }

    override fun getItemCount(): Int {
       return restaurantList.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
val Favorite=restaurantList[position]
    holder.txtName.text=Favorite.name
    holder.txtPrice.text="Rs.${Favorite.cost_for_one}/person"
    holder.txtRating.text=Favorite.rating
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.imgImage.clipToOutline = true
        }
        Picasso.get().load(Favorite.restaurantImage).error(R.drawable.default_restaurant).into(holder.imgImage)





    }
}
