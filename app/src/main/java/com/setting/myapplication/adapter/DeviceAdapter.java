package com.setting.myapplication.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.setting.myapplication.R;

import java.util.ArrayList;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder> {

    public ArrayList<BluetoothDevice> outDataList;
    Context context;
    View.OnClickListener clickListener;

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public DeviceAdapter(Context context, ArrayList<BluetoothDevice> outDataList) {
        this.context = context;
        this.outDataList = outDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @SuppressLint({"ResourceAsColor", "MissingPermission"})
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Log.e("item",outDataList.get(position).getName()+" "+outDataList.get(position).getAddress());

        holder.tv_name.setText(outDataList.get(position).getName());

        holder.tv_address.setText(outDataList.get(position).getAddress());

        //holder.tv_group_name.setText(outDataList.get(position).get);

        holder.myLayout.setTag(position);
        holder.myLayout.setOnClickListener(clickListener);


    }



    @Override
    public int getItemCount() {
        return outDataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_name;
        public TextView tv_address;


        LinearLayout myLayout;
        LinearLayout layout;

        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView;

            //myLayout.setBackgroundColor(Color.RED);
            layout = itemView.findViewById(R.id.layout);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);

            context = itemView.getContext();

        }


    }
}