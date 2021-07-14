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
import com.projectx.spa.models.ParkedHistory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ParkedHistoryAdapter extends RecyclerView.Adapter<ParkedHistoryAdapter.MyOwnHolder> {
    Context context;
    List<ParkedHistory> parkedHistories;

    public ParkedHistoryAdapter(Context context, List<ParkedHistory> parkedHistories) {
        this.context = context;
        this.parkedHistories = parkedHistories;
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
        holder.vehicleNoTextView.setText(parkedHistories.get(position).getVehicleNumber());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);

        String entryTimeStr = "Entry : " + dateFormat.format(parkedHistories.get(position).getEntryTime().toDate());
        holder.entryTimeTextView.setText(entryTimeStr);

        String exitTimeStr = "Exit : " + dateFormat.format(parkedHistories.get(position).getExitTime().toDate());
        holder.exitTimeTextView.setText(exitTimeStr);


        double amount = parkedHistories.get(position).getAmountPaid();
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        decimalFormat.setPositivePrefix("₹ ");
        holder.amountPaidTextView.setText(decimalFormat.format(amount));
//        holder.amountPaidTextView.append("\n" + "₨" + "500" );

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
        return parkedHistories.size();
    }

    public static class MyOwnHolder extends RecyclerView.ViewHolder {
        TextView vehicleNoTextView;
        TextView entryTimeTextView;
        TextView exitTimeTextView;
        TextView amountPaidTextView;
        LinearLayout cardLayout;

        public MyOwnHolder(View itemView) {
            super(itemView);
            vehicleNoTextView = itemView.findViewById(R.id.item_vehicle_number);
            entryTimeTextView = itemView.findViewById(R.id.item_entry_time);
            exitTimeTextView = itemView.findViewById(R.id.item_exit_time);
            amountPaidTextView = itemView.findViewById(R.id.item_amount_paid);
            cardLayout = itemView.findViewById(R.id.parked_history_card_layout);
        }
    }
}