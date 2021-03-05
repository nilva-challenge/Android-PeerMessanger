package com.example.nilvabtchallenge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nilvabtchallenge.model.MessageChatBody;


public class BluetoothChatFragment extends Fragment {
    private static final int SEARCH_DEVICE = 6;
    private static final int PAIR_DEVICE = 7;
    private static final int REQUEST_ENABLE_BT = 8;

    private RecyclerView conversationView;
    private EditText msgEditText;
    private TextView deviceState;
    private ImageView pairDevice, newDevice, sendButton;
    private ConstraintLayout discover;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothChatService chatService = null;
    private String connectedDeviceName = null;
    private StringBuffer outStringBuffer;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private ChatListAdapter adapter;


    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bluetooth_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pairDevice = view.findViewById(R.id.pairDeviceImageView);
        newDevice = view.findViewById(R.id.newDeviceImageView);
        deviceState = view.findViewById(R.id.deviceState);
        discover = view.findViewById(R.id.discoverable);
        conversationView = view.findViewById(R.id.in);
        msgEditText = view.findViewById(R.id.edit_text_out);
        sendButton = view.findViewById(R.id.button_send);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }

        pairDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pairIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(pairIntent, PAIR_DEVICE);
            }
        });

        newDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(newIntent, SEARCH_DEVICE);
            }
        });
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ensureDiscoverable();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (chatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatService != null) {
            chatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (chatService != null) {
            if (chatService.getState() == BluetoothChatService.STATE_NONE) {
                chatService.start();
            }
        }
    }


    private void setupChat() {



        adapter = new ChatListAdapter();
        conversationView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        conversationView.setLayoutManager(layoutManager);

        msgEditText.setOnEditorActionListener(mWriteListener);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });

        chatService = new BluetoothChatService(mHandler);

        outStringBuffer = new StringBuffer("");
    }


    private void ensureDiscoverable() {
        if (bluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 360);
            startActivity(discoverableIntent);
        }
    }


    private void sendMessage(String message) {
        if (chatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatService.write(send);

            outStringBuffer.setLength(0);
            msgEditText.setText(outStringBuffer);
        }
    }


    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };


    private void setStatus(int resId) {
        deviceState.setText(resId);
    }

    private void setStatus(CharSequence subTitle) {
        deviceState.setText(subTitle);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, connectedDeviceName));
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    MessageChatBody body = new MessageChatBody(0, writeMessage, "Me");
                    adapter.addItem(body);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    MessageChatBody body1 = new MessageChatBody(1, readMessage, connectedDeviceName);
                    adapter.addItem(body1);
                    break;
                case MESSAGE_DEVICE_NAME:
                    connectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + connectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEARCH_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case PAIR_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Log.d("BT", "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        chatService.connect(device, secure);
    }
}