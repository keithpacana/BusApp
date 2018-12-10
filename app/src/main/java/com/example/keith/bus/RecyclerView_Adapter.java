package com.example.keith.bus;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecyclerView_Adapter extends RecyclerView.Adapter<RecyclerView_Adapter.viewHolder>
{
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView background;
        TextView bus_name;
        TextView stop_name;
        ImageView stop_icon;
        TextView to_name;
        ImageView to_icon;
        RelativeLayout parentlayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            stop_icon = itemView.findViewById(R.id.stop_icon);
            to_icon = itemView.findViewById(R.id.to_icon);
            to_name = itemView.findViewById(R.id.to_text);
            stop_name = itemView.findViewById(R.id.stop_text);
            bus_name = itemView.findViewById(R.id.bus_name);
        }
    }
}
