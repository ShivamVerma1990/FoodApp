package com.example.foodaap.Module

import android.content.Context

class SessionManager(context: Context)
{
var PREF_NAME="FoodAap"
var PRIVATE_MODE=0
val KEY_IS_LOGGEDIN="isLoggedIn"
var pref=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE)
var editor=pref.edit()
fun setLogin(isLoggedIn:Boolean){
    editor.putBoolean(KEY_IS_LOGGEDIN,isLoggedIn)
    editor.apply()

}
fun isLoggedIn():Boolean{
    return pref.getBoolean(KEY_IS_LOGGEDIN,true)
}


}