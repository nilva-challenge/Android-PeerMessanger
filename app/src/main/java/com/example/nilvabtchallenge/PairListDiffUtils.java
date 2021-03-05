package com.example.nilvabtchallenge;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.nilvabtchallenge.model.BluetoothDeviceModel;

import java.util.List;


public class PairListDiffUtils extends DiffUtil.Callback {
    private List<BluetoothDeviceModel> oldList;
    private List<BluetoothDeviceModel> newList;

    public PairListDiffUtils(List<BluetoothDeviceModel> oldList, List<BluetoothDeviceModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getAddress().equals(newList.get(newItemPosition).getAddress());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        BluetoothDeviceModel oldList = this.oldList.get(oldItemPosition);
        BluetoothDeviceModel newList = this.newList.get(newItemPosition);
        return oldList == newList;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
