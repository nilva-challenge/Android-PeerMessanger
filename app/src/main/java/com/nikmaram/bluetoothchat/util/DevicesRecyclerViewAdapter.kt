package com.nikmaram.bluetoothchat.util
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nikmaram.bluetoothchat.R
import com.nikmaram.bluetoothchat.databinding.RecyclerviewSingleItemBinding
import com.nikmaram.bluetoothchat.model.DeviceData


class DevicesRecyclerViewAdapter(val mDeviceList: List<DeviceData>, val context: Context) :
        RecyclerView.Adapter<DevicesRecyclerViewAdapter.VH>() {


    private var listener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            RecyclerviewSingleItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.data=mDeviceList[position]
    }

    override fun getItemCount(): Int {
        return mDeviceList.size
    }

    inner class VH(val binding: RecyclerviewSingleItemBinding) : RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener{
                listener?.itemClicked(mDeviceList[adapterPosition])
            }
        }
    }

    fun setItemClickListener(listener: ItemClickListener){
        this.listener = listener
    }

    interface ItemClickListener{
        fun itemClicked(deviceData: DeviceData)
    }
}