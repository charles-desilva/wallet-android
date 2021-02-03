package com.vaofim.boffin;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import Models.ReportTable;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder>{

    private List<ReportTable> customerlist;


    public ReportAdapter(List<ReportTable> customerlist) {
        this.customerlist = customerlist;
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder{
        public TextView name,value;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.tablerowname);
            value=itemView.findViewById(R.id.tablerowvalue);
        }
    }

    @NonNull
    @Override
    public ReportAdapter.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_tablerow, viewGroup, false);

        return new ReportAdapter.ReportViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ReportViewHolder customerViewHolder, int i) {
        final ReportTable report=customerlist.get(i);
        customerViewHolder.name.setText(report.getName());
        customerViewHolder.value.setText(report.getValue());
    }

    @Override
    public int getItemCount() {
        return customerlist.size();
    }

}
