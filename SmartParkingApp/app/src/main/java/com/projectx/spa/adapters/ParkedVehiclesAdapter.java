package com.projectx.spa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectx.spa.R;
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
        holder.vehicleNoTextView.setText(parkedVehiclesList.get(position).getVehicleNumber());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

        String entryTimeStr = "Entry Time: " + dateFormat.format(parkedVehiclesList.get(position).getEntryTime().toDate());
        holder.entryTimeTextView.setText(entryTimeStr);

        /*holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Test2Activity.class);
                intent.putExtra(Constants.SAMPLE_KEY, parkingSlots.get(position));
                context.startActivity(intent);
            }
        });*/
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
            cardLayout = itemView.findViewById(R.id.parked_history_card_layout);
        }
    }
}