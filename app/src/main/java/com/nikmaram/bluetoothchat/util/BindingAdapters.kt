package com.nikmaram.bluetoothchat.util

import android.os.Build
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("setTime")
fun TextView.setTime(time:Long){
    val resultdate = Date(time)
    var df: SimpleDateFormat = SimpleDateFormat("hh:mm a",Locale.getDefault())
    text = df.format(resultdate)

}

