Android-PeerMessanger<br />
Step 1:We need the Android Bluetooth service for this tutorial to work. In order to use Bluetooth service, declare BLUETOOTH permission in manifest file. <br />
Step 2:Now to check whether Bluetooth is supported on device or not, we use object of BluetoothAdapter class.<br />
Step 3:If Bluetooth is disabled then we request the user to enable it. And we perform this action by calling startActivityForResult() with REQUEST_ENABLE_BT action. <br />This will open dialog to enable Bluetooth on the device. <br />
Step 4:Now in android, device is not discoverable by default. To make device discoverable, call startActivityForResult() with BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE action. <br />
Step5:To start the chat, we first need to establish connection with the desired device. And before starting scanning for available devices, we usually get paired devices first in the list<br />
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() <br />
    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices() <br />
Step6:To connect two devices, we must implement server side and client side mechanism. One device shall open the server socket and another should initiate the connection. Both are connected when BluetoothSocket is connected on the same RFCOMM channel. <br />
During connection procedure android framework automatically shows pairing dialog. <br />
<br />
![alt text](https://github.com/hoseinnikmaram/Android-PeerMessanger/blob/develop/screenshot/photo_1.jpg)
<br />
Step 7: Read and Write Data
<br />
    After establishing connection successfully, each device has connected BluetoothSocket. <br />
    Now one can Read and write data to the streams using read(byte[]) and write(byte[]).
<br />
![alt text](https://github.com/hoseinnikmaram/Android-PeerMessanger/blob/develop/screenshot/photo_2.jpg)
<br />
