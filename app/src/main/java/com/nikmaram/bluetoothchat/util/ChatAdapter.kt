package com.nikmaram.bluetoothchat.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nikmaram.bluetoothchat.R
import com.nikmaram.bluetoothchat.databinding.ReceivedLayoutBinding
import com.nikmaram.bluetoothchat.databinding.SentLayoutBinding
import com.nikmaram.bluetoothchat.model.Message
import java.text.SimpleDateFormat
import java.util.*



class ChatAdapter(val chatData: List<Message>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val SENT = 0
    val RECEIVED = 1
    var df: SimpleDateFormat = SimpleDateFormat("hh:mm a",Locale.getDefault())

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder.itemViewType){

            SENT -> {
                val holder: SentHolder = holder as SentHolder
                    holder.binding.data=chatData[position]

            }
            RECEIVED -> {
                val holder: ReceivedHolder = holder as ReceivedHolder
                holder.binding.data=chatData[position]
            }

        }
    }

    override fun getItemViewType(position: Int): Int {

        when(chatData[position].type){
            Constants.MESSAGE_TYPE_SENT -> return SENT
            Constants.MESSAGE_TYPE_RECEIVED -> return RECEIVED
        }

        return -1
    }

    override fun getItemCount(): Int {
        return chatData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            SENT -> {
                 SentHolder(
                    SentLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                 ReceivedHolder(
                    ReceivedLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

        }
    }

     class SentHolder(val binding: SentLayoutBinding) : RecyclerView.ViewHolder(binding.root)

     class ReceivedHolder(val binding: ReceivedLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}