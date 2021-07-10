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

import java.util.List;

public class ParkedHistoryAdapter extends RecyclerView.Adapter<ParkedHistoryAdapter.MyOwnHolder> {
    Context context;
    List<Vehicles> parkedHistoryList;

    public ParkedHistoryAdapter(Context context, List<Vehicles> parkedHistoryList) {
        this.context = context;
        this.parkedHistoryList = parkedHistoryList;
    }

    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_parked_history, parent, false);
        return new MyOwnHolder(view);
    }

    @Override
    public void onBindViewHolder(ParkedHistoryAdapter.MyOwnHolder holder, int position) {
        holder.vehicleNoTextView.setText(parkedHistoryList.get(position).getVehicleNumber());

        holder.entryTimeTextView.setText(parkedHistoryList.get(position).getEntryTime().toDate().toString());

        holder.exitTimeTextView.setText(parkedHistoryList.get(position).getExitTime().toDate().toString());

//        String totalSpaceStr = "Entry Time: " + parkedHistoryList.get(position).getTotalSpace();
//        holder.totalSpace.setText(totalSpaceStr);

//        int availableSpace = parkedHistoryList.get(position).getAvailableSpace();
//        String availableSpaceStr = "Exit Time: " + availableSpace;
//        holder.availableSpace.setText(availableSpaceStr);

        /*if (availableSpace <= 0) {
            holder.cardLayout.setEnabled(false);
            holder.cardLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.red_100));
        } else {
            holder.cardLayout.setEnabled(true);
            holder.cardLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.green_100));
        }*/

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
        return parkedHistoryList.size();
    }

    public static class MyOwnHolder extends RecyclerView.ViewHolder {
        TextView vehicleNoTextView, entryTimeTextView, exitTimeTextView;
        LinearLayout cardLayout;

        public MyOwnHolder(View itemView) {
            super(itemView);
            vehicleNoTextView = itemView.findViewById(R.id.item_vehicle_number);
            entryTimeTextView = itemView.findViewById(R.id.item_entry_time);
            exitTimeTextView = itemView.findViewById(R.id.item_exit_time);
            cardLayout = itemView.findViewById(R.id.parked_history_card_layout);
        }
    }
}