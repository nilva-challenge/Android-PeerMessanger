package com.example.nilvabtchallenge;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.example.nilvabtchallenge.model.BluetoothDeviceModel;

import java.util.ArrayList;
import java.util.List;

public class PairDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<BluetoothDeviceModel> listName = new ArrayList<>();
    private AddressListener addressListener;

    public PairDeviceAdapter(Context context, AddressListener addressListener) {
        this.context = context;
        this.addressListener = addressListener;
//        this.listName = listName;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pair_list_adapter, parent, false);
        return new PairListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PairListViewHolder viewHolder = (PairListViewHolder) holder;
        viewHolder.onBind(context, listName.get(position), addressListener);
    }

    @Override
    public int getItemCount() {
        return listName.size();
    }

    public static class PairListViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private CardView deviceCardView;

        public PairListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.deviceName);
            deviceCardView = itemView.findViewById(R.id.deviceLayout);
        }

        public void onBind(Context context, BluetoothDeviceModel s, AddressListener addressListener) {
            name.setText(s.getName());

            deviceCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (s.getAddress() == null) {
                        Toast.makeText(context, "Can't Connect to this Device", Toast.LENGTH_SHORT).show();
                    } else {
                        addressListener.onAddressListener(s.getAddress());
                    }
                }
            });
        }
    }

    public void addPairDevice(List<BluetoothDeviceModel> lists) {
        final PairListDiffUtils diffUtilsCallBack = new PairListDiffUtils(listName, lists);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilsCallBack);
        listName = lists;
        diffResult.dispatchUpdatesTo(this);
    }

}