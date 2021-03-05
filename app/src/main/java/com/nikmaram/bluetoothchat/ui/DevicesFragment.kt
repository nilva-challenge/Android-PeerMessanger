package com.nikmaram.bluetoothchat.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nikmaram.bluetoothchat.R
import com.nikmaram.bluetoothchat.databinding.FragmentDevicesBinding
import com.nikmaram.bluetoothchat.model.DeviceData
import com.nikmaram.bluetoothchat.util.BluetoothChatService
import com.nikmaram.bluetoothchat.util.Constants
import com.nikmaram.bluetoothchat.util.DevicesRecyclerViewAdapter
import org.koin.android.ext.android.inject
import org.koin.experimental.property.inject

class DevicesFragment : Fragment(), DevicesRecyclerViewAdapter.ItemClickListener,
    ChatFragment.CommunicationListener {

    lateinit var binding: FragmentDevicesBinding
    private val REQUEST_ENABLE_BT = 123
    private val TAG = javaClass.simpleName
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewPaired: RecyclerView
    private val mDeviceList = arrayListOf<DeviceData>()
    private val devicesAdapter: DevicesRecyclerViewAdapter by inject()
    private val mBtAdapter: BluetoothAdapter by inject()
    private val PERMISSION_REQUEST_BLUETOOTH = 1
    private val PERMISSION_REQUEST_BLUETOOTH_KEY = "PERMISSION_REQUEST_LOCATION"
    private var alreadyAskedForPermission = false
    private lateinit var headerLabel: TextView
    private lateinit var headerLabelPaired: TextView
    private lateinit var headerLabelContainer: LinearLayout
    private lateinit var status: TextView
    private lateinit var connectionDot: ImageView
    private lateinit var  mConnectedDeviceName: String
    private var connected: Boolean = false

    private var mChatService: BluetoothChatService? = null
    private val chatFragment:ChatFragment by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDevicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        progressBar = binding.progressBar
        recyclerView =binding.recyclerView
        recyclerViewPaired = binding.recyclerViewPaired
        headerLabel = binding.headerLabel
        headerLabelPaired = binding.headerLabelPaired
        headerLabelContainer = binding.headerLabelContainer
        status = MainActivity.bindingActivity.status
        connectionDot = MainActivity.bindingActivity.connectionDot

        status.text = getString(R.string.bluetooth_not_enabled)

        headerLabelContainer.visibility = View.INVISIBLE

        if (savedInstanceState != null)
            alreadyAskedForPermission = savedInstanceState.getBoolean(PERMISSION_REQUEST_BLUETOOTH_KEY, false)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewPaired.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.isNestedScrollingEnabled = false
        recyclerViewPaired.isNestedScrollingEnabled = false

        binding.searchDevices.setOnClickListener {
            findDevices()
        }

        binding.makeVisible.setOnClickListener {
            makeVisible()
        }

        recyclerView.adapter = devicesAdapter
        devicesAdapter.setItemClickListener(this)

        // Register for broadcasts when a device is discovered.
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(mReceiver, filter)

        // Register for broadcasts when discovery has finished
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        requireActivity().registerReceiver(mReceiver, filter)

        // Get the local Bluetooth adapter

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = BluetoothChatService(requireContext(), mHandler)

        if (mBtAdapter == null){
            showAlertAndExit()
            return
        }

        if (mBtAdapter.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            status.text = getString(R.string.not_connected)
        }

        // Get a set of currently paired devices
        val pairedDevices = mBtAdapter.bondedDevices
        val mPairedDeviceList = arrayListOf<DeviceData>()

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices?.size ?: 0 > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (device in pairedDevices!!) {
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                mPairedDeviceList.add(DeviceData(deviceName,deviceHardwareAddress))
            }

            val devicesAdapter = DevicesRecyclerViewAdapter(context = requireContext(), mDeviceList = mPairedDeviceList)
            recyclerViewPaired.adapter = devicesAdapter
            devicesAdapter.setItemClickListener(this)
            headerLabelPaired.visibility = View.VISIBLE

        }



    }


    private fun makeVisible() {

        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        startActivity(discoverableIntent)

    }

    private fun checkPermissions() {

        if (alreadyAskedForPermission) {
            // don't check again because the dialog is still open
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (requireActivity().checkSelfPermission(Manifest.permission.BLUETOOTH) !=
                PackageManager.PERMISSION_GRANTED) {

                    alreadyAskedForPermission = true
                    requestPermissions(arrayOf(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                    ), PERMISSION_REQUEST_BLUETOOTH)


            } else {
                startDiscovery()
            }
        } else {
            startDiscovery()
            alreadyAskedForPermission = true
        }

    }

    private fun showAlertAndExit() {

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.not_compatible))
            .setMessage(getString(R.string.no_support))
            .setPositiveButton("Exit", { _, _ -> System.exit(0) })
            .show()
    }

    private fun findDevices() {

        checkPermissions()
    }

    private fun startDiscovery() {

        headerLabelContainer.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        headerLabel.text = getString(R.string.searching)
        mDeviceList.clear()

        // If we're already discovering, stop it
        if (mBtAdapter?.isDiscovering ?: false)
            mBtAdapter?.cancelDiscovery()

        // Request discover from BluetoothAdapter
        mBtAdapter?.startDiscovery()
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val mReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val action = intent.action

            if (BluetoothDevice.ACTION_FOUND == action) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device?.name
                val deviceHardwareAddress = device?.address // MAC address

                val deviceData = deviceHardwareAddress?.let { DeviceData(deviceName, it) }
                mDeviceList.add(deviceData!!)

                val setList = HashSet<DeviceData>(mDeviceList)
                mDeviceList.clear()
                mDeviceList.addAll(setList)

                devicesAdapter.notifyDataSetChanged()
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                progressBar.visibility = View.INVISIBLE
                headerLabel.text = getString(R.string.found)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        progressBar.visibility = View.INVISIBLE

        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            //Bluetooth is now connected.
            status.text = getString(R.string.not_connected)

            // Get a set of currently paired devices
            val pairedDevices = mBtAdapter?.bondedDevices
            val mPairedDeviceList = arrayListOf<DeviceData>()

            mPairedDeviceList.clear()

            // If there are paired devices, add each one to the ArrayAdapter
            if (pairedDevices?.size ?: 0 > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (device in pairedDevices!!) {
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    mPairedDeviceList.add(DeviceData(deviceName,deviceHardwareAddress))
                }

                val devicesAdapter = DevicesRecyclerViewAdapter(context = requireContext(), mDeviceList = mPairedDeviceList)
                recyclerViewPaired.adapter = devicesAdapter
                devicesAdapter.setItemClickListener(this)
                headerLabelPaired.visibility = View.VISIBLE

            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(PERMISSION_REQUEST_BLUETOOTH_KEY, alreadyAskedForPermission)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {

            PERMISSION_REQUEST_BLUETOOTH -> {
                // the request returned a result so the dialog is closed
                alreadyAskedForPermission = false
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //Log.d(TAG, "Coarse and fine location permissions granted")
                    startDiscovery()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(getString(R.string.fun_limted))
                        builder.setMessage(getString(R.string.since_perm_not_granted))
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.show()
                    }
                }
            }
        }
    }

    override fun itemClicked(deviceData: DeviceData) {
        connectDevice(deviceData) }

    private fun connectDevice(deviceData: DeviceData) {

        // Cancel discovery because it's costly and we're about to connect
        mBtAdapter.cancelDiscovery()
        val deviceAddress = deviceData.deviceHardwareAddress

        val device = mBtAdapter.getRemoteDevice(deviceAddress)

        status.text = getString(R.string.connecting)

        // Attempt to connect to the device
        mChatService?.connect(device, true)

    }


    override fun onResume() {
        super.onResume()
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService?.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService?.start()
            }
        }

        if(connected)
            showChatFragment()

    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(mReceiver)
    }


    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {

                Constants.MESSAGE_STATE_CHANGE -> {

                    when (msg.arg1) {

                        BluetoothChatService.STATE_CONNECTED -> {

                            status.text = getString(R.string.connected_to) + " "+ mConnectedDeviceName
                            Snackbar.make(binding.root,"Connected to " + mConnectedDeviceName,
                                Snackbar.LENGTH_SHORT).show()
                            //mConversationArrayAdapter.clear()
                            connected = true
                        }

                        BluetoothChatService.STATE_CONNECTING -> {
                            status.text = getString(R.string.connecting)
                            connected = false
                        }

                        BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> {
                            status.text = getString(R.string.not_connected)
                            Snackbar.make(binding.root,getString(R.string.not_connected),
                                Snackbar.LENGTH_SHORT).show()
                            connected = false
                        }
                    }
                }

                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)

                    val milliSecondsTime = System.currentTimeMillis()
                    chatFragment.communicate(com.nikmaram.bluetoothchat.model.Message(writeMessage,milliSecondsTime,Constants.MESSAGE_TYPE_SENT))

                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)
                    val milliSecondsTime = System.currentTimeMillis()
                    chatFragment.communicate(com.nikmaram.bluetoothchat.model.Message(readMessage,milliSecondsTime,Constants.MESSAGE_TYPE_RECEIVED))
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME).toString()
                    status.text = getString(R.string.connected_to) + " " +mConnectedDeviceName
                    Snackbar.make(binding.root,"Connected to " + mConnectedDeviceName,
                        Snackbar.LENGTH_SHORT).show()
                    connected = true
                    showChatFragment()
                }
                Constants.MESSAGE_TOAST -> {
                    status.text = getString(R.string.not_connected)
                    connected = false
                }
            }
        }
    }




    private fun sendMessage(message: String) {

        // Check that we're actually connected before trying anything
        if (mChatService?.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(requireContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return
        }

        // Check that there's actually something to send
        if (message.isNotEmpty()) {
            // Get the message bytes and tell the BluetoothChatService to write
            val send = message.toByteArray()
            mChatService?.write(send)

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0)
            //mOutEditText.setText(mOutStringBuffer)
        }
    }

    private fun showChatFragment() {

        if(!requireActivity().isFinishing) {

            chatFragment.setCommunicationListener(this)

            findNavController().navigate(R.id.action_devicesFragment_to_chatFragment2)

        }
    }

    override fun onCommunication(message: String) {
        sendMessage(message)
    }






}