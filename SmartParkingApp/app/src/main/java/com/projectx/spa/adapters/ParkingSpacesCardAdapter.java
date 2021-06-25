package com.projectx.spa.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.projectx.spa.R;
import com.projectx.spa.activities.SerializationTest;
import com.projectx.spa.models.ParkingSlot;

import java.util.List;

public class ParkingSpacesCardAdapter extends RecyclerView.Adapter<ParkingSpacesCardAdapter.MyOwnHolder> {
    Context context;
    List<ParkingSlot> parkingSlots;

    public ParkingSpacesCardAdapter(Context context, List<ParkingSlot> parkingSlots) {
        this.context = context;
        this.parkingSlots = parkingSlots;
    }

    @Override
    public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.available_spaces_card, parent, false);
        return new MyOwnHolder(view);
    }

    @Override
    public void onBindViewHolder(ParkingSpacesCardAdapter.MyOwnHolder holder, int position) {
        holder.location.setText(parkingSlots.get(position).getLocation());

        holder.landmark.setText(parkingSlots.get(position).getLandmark());

        String totalSpaceStr = "Total Space: " + parkingSlots.get(position).getTotalSpace();
        holder.totalSpace.setText(totalSpaceStr);

        String availableSpaceStr = "Available Space: " + parkingSlots.get(position).getAvailableSpace();
        holder.availableSpace.setText(availableSpaceStr);

        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SerializationTest.class);
                intent.putExtra("key", parkingSlots.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkingSlots.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {
        TextView landmark, location, totalSpace, availableSpace;
        LinearLayout cardLayout;

        public MyOwnHolder(View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.placeName);
            landmark = itemView.findViewById(R.id.landmark);
            totalSpace = itemView.findViewById(R.id.totalSpace);
            availableSpace = itemView.findViewById(R.id.availableSpace);
            cardLayout = itemView.findViewById(R.id.cardLayout);
        }
    }
}
