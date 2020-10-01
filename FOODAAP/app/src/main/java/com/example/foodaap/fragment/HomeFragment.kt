package com.example.foodaap.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodaap.Module.Restaurant

import com.example.foodaap.R
import com.example.foodaap.adapter.ConnectionManager
import com.example.foodaap.adapter.HomeRecyclerAdapter

import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var recyclerHome:RecyclerView
   lateinit  var layoutManager: RecyclerView.LayoutManager
lateinit var recyclerAdapter: HomeRecyclerAdapter
   lateinit var progressLayout:RelativeLayout
lateinit var progressBar:ProgressBar


    val list= arrayListOf<Restaurant>()


    var ratingComparator = Comparator<Restaurant>{restaurant1, restaurant2 ->
        if (restaurant1.rating.compareTo(restaurant2.rating,true) == 0){
            restaurant1.name.compareTo(restaurant2.name,true)
        }else {
            restaurant1.rating.compareTo(restaurant2.rating, true)
        }
    }
    var costLowToHighComparator = Comparator<Restaurant>{restaurant1, restaurant2 ->
        restaurant1.cost_for_one.compareTo(restaurant2.cost_for_one,false)
    }
    var costHightToLowComparator = Comparator<Restaurant>{ restaurant1, restaurant2 ->
        restaurant1.cost_for_one.compareTo(restaurant2.cost_for_one,true)
    }
    var nameComparator = Comparator<Restaurant>{restaurant1, restaurant2 ->
        restaurant1.name.compareTo(restaurant2.name,true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)
progressBar=view.findViewById(R.id.progressBar)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE
        setHasOptionsMenu(true)
        recyclerHome=view.findViewById(R.id.recyclerHome)

        layoutManager=LinearLayoutManager(activity)


        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest = object : JsonObjectRequest (Method.GET, url, null, Response.Listener<JSONObject> { response ->
                try {

                    val data = response.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success){
                   progressLayout.visibility=View.GONE

                        val resArray = data.getJSONArray("data")
                        for (i in 0 until resArray.length()){
                            val resObject = resArray.getJSONObject(i)
                            val res = Restaurant(
                                resObject.getString("id").toInt(),
                                resObject.getString("name"),
                                resObject.getString("rating"),
                                resObject.getString("cost_for_one"),
                                resObject.getString("image_url")
                            )
                            list.add(res)
                            if (activity != null) {
                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, list)
                                val layoutManger =LinearLayoutManager(activity)
                                recyclerHome.layoutManager = layoutManger
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.setHasFixedSize(true)
                            }
                        }
                    }else{
                        Toast.makeText(activity as Context,"Some Error Occurred!!",Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException){
                    Toast.makeText(activity as Context,"Some unexpected error occurred!!",Toast.LENGTH_SHORT).show()
                }
            },Response.ErrorListener {
                Toast.makeText(activity as Context,"Volley error occurred!!",Toast.LENGTH_SHORT).show()
                println("Error is $it")
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val header = HashMap<String, String>()
                    header["Content-type"]="application/json"
                    header["token"]="801055bd4f7d19"
                    return header
                }
            }
            queue.add(jsonObjectRequest)
        }else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failure")
            dialog.setMessage("Internet connection not found")
            dialog.setPositiveButton("Open Setting"){text, listner ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text,listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_items, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if(id == R.id.sort_rating){
            Collections.sort(list, ratingComparator)
            list.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        if(id == R.id.sort_lowToHigh){
            Collections.sort(list, costLowToHighComparator)
        }
        if(id == R.id.sort_HighToLow){
            Collections.sort(list, costHightToLowComparator)
            list.reverse()
        }
        if (id == R.id.sort_name){
            Collections.sort(list, nameComparator)
        }
        return super.onOptionsItemSelected(item)
    }



}
