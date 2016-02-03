package com.teenvan.wifidirect;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teenvan.wifidirect.model.DeviceClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navneet on 30/10/15.
 */
public class DevicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Declaration of member variables
    private Context mContext;
    private ArrayList<DeviceClass> devices;

    DevicesAdapter(ArrayList<DeviceClass> mPerks,
                   Context context){
        this.devices = mPerks;
        mContext = context;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.perk_card_layout, parent,
                    false);
            return new VHItem(v);


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      if(holder instanceof VHItem){
            VHItem item = (VHItem)holder;
            item.mDevice.setText(devices.get(position).getDeviceName());
        }


    }


    @Override
    public int getItemCount() {
        return devices.size();
    }


    public static class VHItem extends RecyclerView.ViewHolder {
        TextView mDevice;

        VHItem(View itemView) {
            super(itemView);
            mDevice = (TextView)itemView.findViewById(R.id.deviceTitle);

        }
    }





}
