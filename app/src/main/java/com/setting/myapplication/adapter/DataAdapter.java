package com.setting.myapplication.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.setting.myapplication.R;
import com.setting.myapplication.SaveData;

import java.util.ArrayList;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

    public ArrayList<SaveData> outDataList;
    Context context;
    View.OnClickListener clickListener;

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public DataAdapter(Context context, ArrayList<SaveData> outDataList) {
        this.context = context;
        this.outDataList = outDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @SuppressLint({"ResourceAsColor", "MissingPermission"})
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {



        holder.tv_roll1.setText(outDataList.get(position).getRoll1()+"");
        holder.tv_roll2.setText(outDataList.get(position).getRoll2()+"");
        holder.tv_roll3.setText(outDataList.get(position).getRoll3()+"");
        holder.tv_roll4.setText(outDataList.get(position).getRoll4()+"");

        holder.tv_pitch1.setText(outDataList.get(position).getPitch1()+"");
        holder.tv_pitch2.setText(outDataList.get(position).getPitch2()+"");
        holder.tv_pitch3.setText(outDataList.get(position).getPitch3()+"");
        holder.tv_pitch4.setText(outDataList.get(position).getPitch4()+"");

        holder.tv_yaw1.setText(outDataList.get(position).getYaw1()+"");
        holder.tv_yaw2.setText(outDataList.get(position).getYaw2()+"");
        holder.tv_yaw3.setText(outDataList.get(position).getYaw3()+"");
        holder.tv_yaw4.setText(outDataList.get(position).getYaw4()+"");

        holder.tv_depth.setText(String.format("%.2f",outDataList.get(position).getDepth()));
        holder.tv_kd.setText(String.format("%.2f",outDataList.get(position).getKd()));

        //holder.tv_group_name.setText(outDataList.get(position).get);

        holder.myLayout.setTag(position);
        holder.myLayout.setOnClickListener(clickListener);


    }



    @Override
    public int getItemCount() {
        return outDataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_roll1,tv_pitch1,tv_yaw1;
        public TextView tv_roll2,tv_pitch2,tv_yaw2;
        public TextView tv_roll3,tv_pitch3,tv_yaw3;
        public TextView tv_roll4,tv_pitch4,tv_yaw4;
        public TextView tv_depth,tv_kd;


        LinearLayout myLayout;
        LinearLayout layout;

        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView;

            //myLayout.setBackgroundColor(Color.RED);
            layout = itemView.findViewById(R.id.layout);

            tv_roll1 = (TextView) itemView.findViewById(R.id.tv_roll1);
            tv_pitch1 = (TextView) itemView.findViewById(R.id.tv_pitch1);
            tv_yaw1 = (TextView) itemView.findViewById(R.id.tv_yaw1);

            tv_roll2 = (TextView) itemView.findViewById(R.id.tv_roll2);
            tv_pitch2 = (TextView) itemView.findViewById(R.id.tv_pitch2);
            tv_yaw2 = (TextView) itemView.findViewById(R.id.tv_yaw2);

            tv_roll3 = (TextView) itemView.findViewById(R.id.tv_roll3);
            tv_pitch3 = (TextView) itemView.findViewById(R.id.tv_pitch3);
            tv_yaw3 = (TextView) itemView.findViewById(R.id.tv_yaw3);

            tv_roll4 = (TextView) itemView.findViewById(R.id.tv_roll4);
            tv_pitch4 = (TextView) itemView.findViewById(R.id.tv_pitch4);
            tv_yaw4 = (TextView) itemView.findViewById(R.id.tv_yaw4);

            tv_depth = (TextView) itemView.findViewById(R.id.tv_depth);

            tv_kd = (TextView) itemView.findViewById(R.id.tv_kd);

            context = itemView.getContext();

        }


    }
}