package com.getin.car.authentication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getin.car.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by hp on 22/03/2018.
 */

public class TripsListAdapter extends RecyclerView.Adapter<TripsListAdapter.ViewHolder> {

    public List<Trip> TripsArrayList;
    public Context context;

    public TripsListAdapter(Context context, List<Trip> TripsArrayList){
        this.TripsArrayList = TripsArrayList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.labelText.setText(TripsArrayList.get(position).getLabel());
        holder.cost.setText(String.format(Locale.getDefault(),"%d", TripsArrayList.get(position).getCost()));

        if(TripsArrayList.get(position).getTransportationType()!= null){
            switch (TripsArrayList.get(position).getTransportationType()) {
                case "Private car":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.private_car_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.private_car);
                    }
                    break;
                case "Ride sharing service":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.ride_sharing_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.ride_sharing);
                    }
                    break;
                case "Taxi":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.taxi_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.taxi);
                    }
                    break;
                case "Motorcycle":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.motorcycle_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.motorcycle);
                    }
                    break;
                case "Bus":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.bus_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.bus);
                    }
                    break;
                case "Boat":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.boat_and_model, TripsArrayList.get(position).getTransportationModel()));
                        holder.transportationType.setText(context.getString(R.string.boat_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.boat);
                    }
                    break;
            }
        }


        holder.totalSeat.setText(String.format(Locale.getDefault(),"%d", TripsArrayList.get(position).getSeats()));
        holder.takenSeat.setText(String.format(Locale.getDefault(),"%d", TripsArrayList.get(position).getTakenSeats()));
        holder.freeSeat.setText(String.format(Locale.getDefault(),"%d", TripsArrayList.get(position).getFreeSeats()));
        holder.ownerName.setText(TripsArrayList.get(position).getOwnerName());

        if(TripsArrayList.get(position).getDate() != null){
            holder.startTime.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM , DateFormat.SHORT).format(TripsArrayList.get(position).getDate()));
        }

        if (TripsArrayList.get(position).getCreated() != null) {
            holder.publishedOn.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM , DateFormat.SHORT).format(TripsArrayList.get(position).getCreated()));
        }

        if(TripsArrayList.get(position).getOwnerPic()!= null){
            Glide.with(context).load(TripsArrayList.get(position).getOwnerPic()).into(holder.ownerPic);
        }

    }

    @Override
    public int getItemCount() {
        return TripsArrayList.size();
    }

    /// ViewHolder for trips list /////
    public class ViewHolder extends RecyclerView.ViewHolder {

        View row;
        public TextView labelText,
                startTime,
                cost,
                transportationType,
                totalSeat, takenSeat, freeSeat,
                ownerName,publishedOn;

        public ImageView ownerPic;

        public ViewHolder(View itemView) {
            super(itemView);
            //itemView = row;
            row = itemView;
            labelText = row.findViewById(R.id.label);
            startTime = row.findViewById(R.id.starting_time);
            cost = row.findViewById(R.id.cost);
            transportationType = row.findViewById(R.id.transportation_type);
            totalSeat = row.findViewById(R.id.total_seat_txt);
            takenSeat = row.findViewById(R.id.taken_seat_txt);
            freeSeat = row.findViewById(R.id.free_seat_txt);
            ownerName = row.findViewById(R.id.owner_name);
            publishedOn = row.findViewById(R.id.published_txt);
            ownerPic = row.findViewById(R.id.owner_pic);

        }

        /*public void setLabel(String label){
            TextView rowLabel = row.findViewById(R.id.label);
            rowLabel.setText(label);
        }

        public void setStartingTime(Date date){
            TextView rowStartingTime = row.findViewById(R.id.starting_time);
            rowStartingTime.setText(date.toString());
        }*/
    }


}
