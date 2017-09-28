package com.example.admin.locationassigment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.locationassigment.model.GeocodeResponse;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Admin on 9/28/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    Context context;
    List<GeocodeResponse> geocodeResponseList;

    public RVAdapter(Context context, List<GeocodeResponse> geocodeResponseList) {
        this.context = context;
        this.geocodeResponseList = geocodeResponseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GeocodeResponse ge = geocodeResponseList.get(position);
        holder.txtAddress.setText(ge.getResults().get(0).getFormattedAddress());

    }
    public void ItemAdded(List<GeocodeResponse> geoNewList)
    {
        geocodeResponseList = geoNewList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return geocodeResponseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAddress;
        public ViewHolder(View itemView) {
            super(itemView);
            txtAddress = (TextView) itemView.findViewById(R.id.lyTxtAddress);
        }
    }
}
