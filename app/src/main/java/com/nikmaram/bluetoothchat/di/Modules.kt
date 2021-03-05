package com.nikmaram.bluetoothchat.di


import org.koin.dsl.module

val applicationModule= module {
    single { provideBluetoothAdapter() }
    single { provideChatFragment() }
    factory { provideDevicesRecyclerViewAdapter(get()) }

}




