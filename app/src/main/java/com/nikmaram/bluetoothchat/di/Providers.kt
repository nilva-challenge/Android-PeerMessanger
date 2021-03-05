package com.nikmaram.bluetoothchat.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.nikmaram.bluetoothchat.BaseApplication
import com.nikmaram.bluetoothchat.model.DeviceData
import com.nikmaram.bluetoothchat.ui.ChatFragment
import com.nikmaram.bluetoothchat.ui.MainActivity
import com.nikmaram.bluetoothchat.util.BluetoothChatService
import com.nikmaram.bluetoothchat.util.DevicesRecyclerViewAdapter


    fun provideBluetoothAdapter(): BluetoothAdapter {

        return BluetoothAdapter.getDefaultAdapter()
        }


    fun provideDevicesRecyclerViewAdapter(context: Context): DevicesRecyclerViewAdapter {
        return DevicesRecyclerViewAdapter(context =context , mDeviceList = arrayListOf<DeviceData>())

        }

    fun provideChatFragment(): ChatFragment {

        return ChatFragment()
        }
