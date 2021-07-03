package com.example.nilvabtchallenge;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.nilvabtchallenge.model.MessageChatBody;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_MESSAGE_SENT = 0;
    private final int TYPE_MESSAGE_RECEIVED = 1;

    private List<MessageChatBody> messageList = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        MessageChatBody msgBody = messageList.get(position);
        if (msgBody.getType() == 0) {
            return TYPE_MESSAGE_SENT;
        } else if (msgBody.getType() == 1) {
            return TYPE_MESSAGE_RECEIVED;
        } else return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_MESSAGE_SENT:
                View sendView = inflater.inflate(R.layout.send_message_adapter, parent, false);
                holder = new SentViewHolder(sendView);
                break;
            case TYPE_MESSAGE_RECEIVED:
                View receiveView = inflater.inflate(R.layout.receive_message_adapter, parent, false);
                holder = new ReceiveViewHolder(receiveView);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageChatBody chatBody = messageList.get(position);
        if (chatBody.getType() == 0) {
            SentViewHolder sentViewHolder = (SentViewHolder) holder;
            sentViewHolder.onBind(chatBody.getMessage(), chatBody.getName());
        } else if (chatBody.getType() == 1) {
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            receiveViewHolder.onBind(chatBody.getMessage(), chatBody.getName());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class SentViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.sendMessage);
        }

        public void onBind(String message, String name) {
            messageText.setText(message);
        }
    }

    public static class ReceiveViewHolder extends RecyclerView.ViewHolder {
        private TextView receiveText, senderName;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            receiveText = itemView.findViewById(R.id.receivedMessage);
            senderName = itemView.findViewById(R.id.senderName);
        }


        public void onBind(String message, String name) {
            senderName.setText(name);
            receiveText.setText(message);
        }
    }

    public void addItem(MessageChatBody body) {
        messageList.add(body);
        notifyDataSetChanged();
    }

}


