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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.projectx.spa.R;
import com.projectx.spa.activities.BillsPageActivity;
import com.projectx.spa.models.Vehicles;

import java.text.SimpleDateFormat;
import java.util.List;

public class ParkedVehiclesAdapter extends RecyclerView.Adapter<ParkedVehiclesAdapter.MyOwnHolder> {
    Context context;
    List<Vehicles> parkedVehiclesList;

    public ParkedVehiclesAdapter(Context context, List<Vehicles> parkedVehiclesList) {
        this.context = context;
        this.parkedVehiclesList = parkedVehiclesList;
    }

    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_parked_vehicles, parent, false);
        return new MyOwnHolder(view);
    }

    @Override
    public void onBindViewHolder(ParkedVehiclesAdapter.MyOwnHolder holder, int position) {
        Vehicles vehicle = parkedVehiclesList.get(position);
        holder.vehicleNoTextView.setText(vehicle.getVehicleNumber());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

        String entryTimeStr = "Entry Time: " + dateFormat.format(vehicle.getEntryTime().toDate());
        holder.entryTimeTextView.setText(entryTimeStr);

        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context)
                        .setTitle("Are you sure")
                        .setMessage("Mark as vehicle (" + vehicle.getVehicleNumber() + ") out")
                        .setNeutralButton("Cancel", (dialogInterface, i) -> {
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                        })
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            Intent it1 = new Intent(context, BillsPageActivity.class);
                            it1.putExtra("number", vehicle.getVehicleNumber());
                            it1.putExtra("id", vehicle.getId());
                            context.startActivity(it1);
                        });

                alertDialogBuilder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkedVehiclesList.size();
    }

    public static class MyOwnHolder extends RecyclerView.ViewHolder {
        TextView vehicleNoTextView;
        TextView entryTimeTextView;
        LinearLayout cardLayout;

        public MyOwnHolder(View itemView) {
            super(itemView);
            vehicleNoTextView = itemView.findViewById(R.id.item_parked_vehicle_vehicle_number);
            entryTimeTextView = itemView.findViewById(R.id.item_parked_vehicle_entry_time);
            cardLayout = itemView.findViewById(R.id.parked_vehicles_card_layout);
        }
    }
}