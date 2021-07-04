package com.projectx.spa.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectx.spa.R;
import com.projectx.spa.activities.TestActivity;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.models.ParkingSlot;

import java.util.List;

public class AvailableSlotsAdapter extends RecyclerView.Adapter<AvailableSlotsAdapter.MyOwnHolder> {
    Context context;
    List<ParkingSlot> parkingSlots;

    public AvailableSlotsAdapter(Context context, List<ParkingSlot> parkingSlots) {
        this.context = context;
        this.parkingSlots = parkingSlots;
    }

    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_available_slots, parent, false);
        return new MyOwnHolder(view);
    }

    @Override
    public void onBindViewHolder(AvailableSlotsAdapter.MyOwnHolder holder, int position) {
        holder.location.setText(parkingSlots.get(position).getBuilding());

        holder.landmark.setText(parkingSlots.get(position).getAddress());

        String totalSpaceStr = "Total Space: " + parkingSlots.get(position).getTotalSpace();
        holder.totalSpace.setText(totalSpaceStr);

        String availableSpaceStr = "Available Space: " + parkingSlots.get(position).getAvailableSpace();
        holder.availableSpace.setText(availableSpaceStr);

        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TestActivity.class);
                intent.putExtra(Constants.SAMPLE_KEY, parkingSlots.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkingSlots.size();
    }

    public static class MyOwnHolder extends RecyclerView.ViewHolder {
        TextView landmark, location, totalSpace, availableSpace;
        LinearLayout cardLayout;

        public MyOwnHolder(View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.building);
            landmark = itemView.findViewById(R.id.address);
            totalSpace = itemView.findViewById(R.id.total_space);
            availableSpace = itemView.findViewById(R.id.available_space);
            cardLayout = itemView.findViewById(R.id.card_layout);
        }
    }
}