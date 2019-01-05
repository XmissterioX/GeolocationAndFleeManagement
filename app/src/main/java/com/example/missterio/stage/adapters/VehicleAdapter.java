package com.example.missterio.stage.adapters;


import android.content.Context;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.missterio.stage.R;
import com.example.missterio.stage.models.Vehicule;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder>
        implements Filterable {

    private List<Vehicule> vehiculeList;
    private List<Vehicule> vehiculesListFiltered;
    private Context mContext;


    public VehicleAdapter(@NonNull Context context, List<Vehicule> vehiculeList) {
        this.vehiculeList = vehiculeList;
        this.mContext = context;
        this.vehiculesListFiltered = vehiculeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicule_row, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Vehicule v = vehiculesListFiltered.get(position);
        Log.i("azerty",v.getFullName()+"azerty");
        if( v.getConnected() == true){
            holder.imgEtat.setImageResource(R.drawable.green_indicator);
        }
        else
        {
            holder.imgEtat.setImageResource(R.drawable.red_indicator);
        }
        holder.txtDate.setText(v.getAcquisitionTimeString());
        holder.txtEmplacement.setText(v.getNearestPlace());
        holder.txtMatricule.setText(v.getFullName());
        if(v.getDriverName().isEmpty()){
            holder.txtChauffeur.setText("Aucun chauffeur actuellement ");
            holder.txtChauffeur.setTextColor(Color.RED);
        }
        else {
            holder.txtChauffeur.setText(v.getDriverName());
            holder.txtChauffeur.setTextColor(Color.parseColor("#0cd140"));
        }

    }




    @Override
    public int getItemCount() {
        return vehiculesListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    vehiculesListFiltered = vehiculeList;
                } else {
                    List<Vehicule> filteredList = new ArrayList<>();
                    for (Vehicule row : vehiculeList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getFullName().toLowerCase().contains(charString.toLowerCase()) || row.getDriverName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    vehiculesListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = vehiculesListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                vehiculesListFiltered = (ArrayList<Vehicule>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imgEtat;
        protected TextView txtMatricule,txtEmplacement,txtDate,txtChauffeur;

        public ViewHolder(View view) {
            super(view);
            this.imgEtat =  view.findViewById(R.id.imgEtat);
            this.txtMatricule =  view.findViewById(R.id.txtMatricule);
            this.txtEmplacement = view.findViewById(R.id.txtEmplacement);
            this.txtDate =  view.findViewById(R.id.txtDate);
            this.txtChauffeur = view.findViewById(R.id.txtChauffeur);
        }
    }

    public List<Vehicule> getVehiculesListFiltered() {
        return vehiculesListFiltered;
    }
}
