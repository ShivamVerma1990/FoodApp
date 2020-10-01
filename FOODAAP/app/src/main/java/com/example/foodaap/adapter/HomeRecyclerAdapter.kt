package com.example.foodaap.adapter

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodaap.Module.Restaurant
import com.example.foodaap.R
import com.example.foodaap.database.RestaurantDatabase
import com.example.foodaap.database.RestaurantEntity
import com.example.foodaap.fragment.MenuFragment
import com.squareup.picasso.Picasso
import kotlin.collections.List

class HomeRecyclerAdapter(val context: Context,val itemList:ArrayList<Restaurant>):RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val imgImage: ImageView = view.findViewById(R.id.imgImage)
        val imgLove:ImageView=view.findViewById(R.id.imgLove) as ImageView
        val llContent: LinearLayout = view.findViewById(R.id.llContent)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclercool, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(p0: HomeViewHolder, p1: Int) {
        val Restaurant = itemList[p1]
        p0.txtName.text = Restaurant.name
        p0.txtPrice.text = "Rs.${Restaurant.cost_for_one}/person"
        p0.txtRating.text = Restaurant.rating
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            p0.imgImage.clipToOutline = true
        }
        Picasso.get().load(Restaurant.restaurantImage).error(R.drawable.default_restaurant).into(p0.imgImage)
        p0.llContent.setOnClickListener {
            val fragment = MenuFragment()
            val args = Bundle()
            args.putInt("id", Restaurant.id as Int )
            args.putString("name", Restaurant.name)
            fragment.arguments = args
            val transition =
                (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transition.replace(R.id.frame, fragment)
            transition.commit()
            (context as AppCompatActivity).supportActionBar?.title =
                p0.txtName.text.toString()
        }
        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()

        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(Restaurant.id.toString())) {
            p0.imgLove.setBackgroundResource(R.drawable.ic_faw)
        } else {
            p0.imgLove.setBackgroundResource(R.drawable.ic_fa)
        }


            p0.imgLove.setOnClickListener {
                val restaurantEntity = RestaurantEntity(
                    Restaurant.id           ,
                    Restaurant.name,
                    Restaurant.rating,
                    Restaurant.cost_for_one,
                    Restaurant.restaurantImage
                )

                if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                    val async =
                        DBAsyncTask(context, restaurantEntity, 2).execute()
                    val result = async.get()
                    if (result) {
                        Toast.makeText(context, "Added to favourite", Toast.LENGTH_SHORT).show()

                        p0.imgLove.setBackgroundResource(R.drawable.ic_faw)
                    }
                } else {
                    val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                    val result = async.get()

                    if (result) {
                        Toast.makeText(context, "Removed from favourite", Toast.LENGTH_SHORT).show()

                        p0.imgLove.setBackgroundResource(R.drawable.ic_fa)

                    }
                }
            }

         }




class DBAsyncTask(
    context: Context,
    private val restaurantEntity: RestaurantEntity,
    private val mode: Int
) :
    AsyncTask<Void, Void, Boolean>() {

    val db =
        Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

    override fun doInBackground(vararg params: Void?): Boolean {

        when (mode) {

            1 -> {
                val res: RestaurantEntity? =
                    db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                db.close()
                return res != null
            }

            2 -> {
                db.restaurantDao().insertRestaurant(restaurantEntity)
                db.close()
                return true
            }

            3 -> {
                db.restaurantDao().deleteRestaurant(restaurantEntity)
                db.close()
                return true
            }
        }

        return false
    }

}

class GetAllFavAsyncTask(context: Context) : AsyncTask<Void, Void, List<String>>() {
    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
    override fun doInBackground(vararg params: Void?): List<String> {

        val list = db.restaurantDao().getAllRestaurants()
        val listOfIds = arrayListOf<String>()
        for (i in list) {
            listOfIds.add(i .id.toString())
        }
        return listOfIds
    }
}
}
