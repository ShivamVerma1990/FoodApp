package com.example.foodaap.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

import com.example.foodaap.R
import org.w3c.dom.Text

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var txtUserName: TextView
    private lateinit var txtPhone: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtEmail: TextView
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        /*(activity as DrawerLocker).setDrawerEnabled(true)*/
        sharedPrefs = (activity as FragmentActivity).getSharedPreferences("FoodAap", Context.MODE_PRIVATE)
        txtUserName = view.findViewById(R.id.profileName)
        txtPhone = view.findViewById(R.id.profileNumber)
        txtEmail = view.findViewById(R.id.profileEmail)
        txtAddress = view.findViewById(R.id.profileAddress)
        txtUserName.text = sharedPrefs.getString("user_name", null)
        val phoneText = "+91-${sharedPrefs.getString("user_mobile_number", null)}"
        txtPhone.text = phoneText
        txtEmail.text = sharedPrefs.getString("user_email", null)
        val address = sharedPrefs.getString("user_address", null)
        txtAddress.text = address
        return view
    }
}
