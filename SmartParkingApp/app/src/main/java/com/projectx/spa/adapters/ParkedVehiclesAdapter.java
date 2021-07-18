package com.projectx.spa.adapters;

import android.app.AlertDialog;
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
import com.projectx.spa.activities.VehicleExitActivity;
import com.projectx.spa.helpers.Constants;
import com.projectx.spa.models.ParkedVehicle;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ParkedVehiclesAdapter extends RecyclerView.Adapter<ParkedVehiclesAdapter.MyOwnHolder> {
    Context context;
    List<ParkedVehicle> parkedVehicles;

    public ParkedVehiclesAdapter(Context context, List<ParkedVehicle> parkedVehicles) {
        this.context = context;
        this.parkedVehicles = parkedVehicles;
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
        ParkedVehicle vehicle = parkedVehicles.get(position);
        holder.vehicleNoTextView.setText(vehicle.getVehicleNumber());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);

        String entryTimeStr = "Entry : " + dateFormat.format(vehicle.getEntryTime().toDate());
        holder.entryTimeTextView.setText(entryTimeStr);

        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                        .setTitle("Vehicle Exit?")
                        .setMessage("Mark as vehicle (" + vehicle.getVehicleNumber() + ") out")
                        .setNeutralButton("Cancel", (dialogInterface, i) -> {
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                        })
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            Intent intent = new Intent(context, VehicleExitActivity.class);
                            intent.putExtra(Constants.VEHICLE_NUMBER, vehicle.getVehicleNumber());
                            intent.putExtra(Constants.VEHICLE_ID, vehicle.getId());
                            context.startActivity(intent);
                        });

                alertDialogBuilder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkedVehicles.size();
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