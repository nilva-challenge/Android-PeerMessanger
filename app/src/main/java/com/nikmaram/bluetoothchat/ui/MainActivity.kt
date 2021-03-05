package com.nikmaram.bluetoothchat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.DataBindingUtil
import com.nikmaram.bluetoothchat.R
import com.nikmaram.bluetoothchat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object{
        lateinit var bindingActivity: ActivityMainBinding
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }


}