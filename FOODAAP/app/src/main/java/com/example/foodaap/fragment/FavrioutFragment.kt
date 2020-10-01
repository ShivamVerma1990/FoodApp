package com.example.foodaap.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.example.foodaap.R
import com.example.foodaap.adapter.FavoriteRecyclerAdapter
import com.example.foodaap.database.RestaurantDatabase
import com.example.foodaap.database.RestaurantEntity

/**
 * A simple [Fragment] subclass.
 */
class FavrioutFragment : Fragment() {
    lateinit var recyclerFav: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var favAdapter: FavoriteRecyclerAdapter
    lateinit var lPr: RelativeLayout
lateinit var rLNoFav:RelativeLayout
    lateinit var progressBar: ProgressBar
    var listFav = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favriout, container, false)
        recyclerFav = view.findViewById(R.id.recyclerFav) as RecyclerView
        layoutManager = LinearLayoutManager(activity)
        lPr = view.findViewById(R.id.lPr)
        listFav = GetAllFav(activity as Context).execute().get()
        rLNoFav=view.findViewById(R.id.rLNoFav)
        lPr.visibility = View.VISIBLE
        progressBar = view.findViewById(R.id.progressBar)
progressBar.visibility=View.VISIBLE
        if (listFav.isEmpty()) {
            lPr.visibility = View.GONE
rLNoFav.visibility=View.VISIBLE
        } else {
            lPr.visibility = View.GONE
            rLNoFav.visibility=View.GONE
            favAdapter = FavoriteRecyclerAdapter(activity as Context, listFav)
            recyclerFav.layoutManager = layoutManager
            recyclerFav.adapter = favAdapter
        }
        return view
    }

    class GetAllFav(val context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>() {

        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
             return  db.restaurantDao().getAllRestaurants()

        }

    }
}
