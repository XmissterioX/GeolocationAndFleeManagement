package com.example.missterio.stage.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.missterio.stage.R;
import com.example.missterio.stage.models.Alerte;

import java.util.ArrayList;
import java.util.List;

public class AllAlertsAdapter extends RecyclerView.Adapter<AllAlertsAdapter.ViewHolder>
        implements Filterable {
    private List<Alerte> alertList;
    private List<Alerte> alertListFiltred;
    private Context mContext;

    public AllAlertsAdapter(Context mContext, List<Alerte> alertList) {
        this.alertList = alertList;
        this.alertListFiltred = alertList;
        this.mContext = mContext;
    }

    @NonNull
    @Override



    public AllAlertsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allalert_row, null);
        AllAlertsAdapter.ViewHolder viewHolder = new AllAlertsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllAlertsAdapter.ViewHolder holder, int position) {
        Alerte a = alertListFiltred.get(position);
        Log.d("nizar",a.getDate());
        holder.txtAlertDate.setText(a.getDate());
        holder.txtAlertType.setText(a.getAlertType());
        holder.txtAlertAddress.setText(a.getAdresse());
        holder.txtAlertSpeed.setText(a.getVitesse());
        holder.txtMatriculeAlert.setText(a.getMatricule());
    }

    @Override
    public int getItemCount() {
        return  alertListFiltred.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    alertListFiltred = alertList;
                } else {
                    List<Alerte> filteredList = new ArrayList<>();
                    for (Alerte row : alertList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getDate().toLowerCase().contains(charString.toLowerCase()) || row.getAlertType().toLowerCase().contains(charString.toLowerCase())
                        || row.getMatricule().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    alertListFiltred = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = alertListFiltred;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                alertListFiltred = (ArrayList<Alerte>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView txtAlertDate,txtAlertType,txtAlertAddress,txtAlertSpeed,txtMatriculeAlert;

        public ViewHolder(View itemView) {
            super(itemView);
            this.txtAlertDate = itemView.findViewById(R.id.txtAlertDateALLAlertsROW);
            this.txtAlertType = itemView.findViewById(R.id.txtAlertTypeALLAlertsROW);
            this.txtAlertAddress = itemView.findViewById(R.id.txtAlertAddressALLAlertsROW);
            this.txtAlertSpeed = itemView.findViewById(R.id.txtAlertALLAlertsROW);
            this.txtMatriculeAlert = itemView.findViewById(R.id.txtMatriculeAlertALLAlertsROW);
        }
    }
}
