package com.getin.car.authentication;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getin.car.R;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by hp on 22/03/2018.
 */

public class TripsListAdapter extends RecyclerView.Adapter<TripsListAdapter.ViewHolder> {

    private final static String TAG = TripsListAdapter.class.getSimpleName();

    public List<Trip> TripsArrayList;
    public Context context;
    public String tripPostId;
    public String ownerId;

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

        if(TripsArrayList.get(position).getCost() != null){
            holder.cost.setText(String.format(Locale.getDefault(),"%d", TripsArrayList.get(position).getCost()));
        }else{
            holder.cost.setText(R.string.none);
            //holder.carIcon.setImageResource(R.drawable.ic_car_grey_rounded);
        }

        if(TripsArrayList.get(position).getTransportationType()!= null){
            switch (TripsArrayList.get(position).getTransportationType()) {
                case "Private car":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.private_car_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.private_car);
                    }
                    holder.carIcon.setImageResource(R.drawable.ic_car_black_rounded);
                    break;
                case "Ride sharing service":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.ride_sharing_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.ride_sharing);
                    }
                    holder.carIcon.setImageResource(R.drawable.ic_sedan_car_front);
                    break;
                case "Taxi":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.taxi_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.taxi);
                    }
                    holder.carIcon.setImageResource(R.drawable.ic_cab_taxi_old);
                    break;
                case "Motorcycle":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.motorcycle_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.motorcycle);
                    }
                    holder.carIcon.setImageResource(R.drawable.ic_motorcycle_black_48px);
                    break;
                case "Bus":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.bus_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.bus);
                    }
                    holder.carIcon.setImageResource(R.drawable.ic_directions_bus_black_48px);
                    break;
                case "Boat":
                    if(TripsArrayList.get(position).getTransportationModel()!= null){
                        holder.transportationType.setText(context.getString(R.string.boat_and_model, TripsArrayList.get(position).getTransportationModel()));
                    }else{
                        holder.transportationType.setText(R.string.boat);
                    }
                    holder.carIcon.setImageResource(R.drawable.ic_directions_boat_black_48px);
                    break;
                default:
                    holder.transportationType.setText(R.string.none);
                    holder.carIcon.setImageResource(R.drawable.ic_car_grey_rounded);
                    break;
            }
        }else{
            holder.transportationType.setText(R.string.none);
            holder.carIcon.setImageResource(R.drawable.ic_car_grey_rounded);
        }


        holder.totalSeat.setText(String.format(Locale.getDefault(),"%d", TripsArrayList.get(position).getSeats()));
        holder.takenSeat.setText(String.format(Locale.getDefault(),"%d", TripsArrayList.get(position).getTakenSeats()));
        holder.freeSeat.setText(String.format(Locale.getDefault(),"%d", TripsArrayList.get(position).getFreeSeats()));
        holder.ownerName.setText(TripsArrayList.get(position).getOwnerName());


        if(TripsArrayList.get(position).getChat() != null && !TripsArrayList.get(position).getChat().equalsIgnoreCase("Any")){
            if(TripsArrayList.get(position).getChat().equalsIgnoreCase("Yes")){
                holder.chatIcon.setImageResource(R.drawable.ic_chat_green);
            }else if (TripsArrayList.get(position).getChat().equalsIgnoreCase("No")){
                holder.chatIcon.setImageResource(R.drawable.ic_chat_red);
            }
        }else{
            holder.chatIcon.setImageResource(R.drawable.ic_chat_grey);
        }

        if(TripsArrayList.get(position).getCursing() != null && !TripsArrayList.get(position).getCursing().equalsIgnoreCase("Any")){
            if(TripsArrayList.get(position).getCursing().equalsIgnoreCase("Yes")){
                holder.cursingIcon.setImageResource(R.drawable.ic_swearing_green);
            }else if(TripsArrayList.get(position).getCursing().equalsIgnoreCase("No")){
                holder.cursingIcon.setImageResource(R.drawable.ic_swearing_red);
            }
        }else{
            holder.cursingIcon.setImageResource(R.drawable.ic_swearing_grey);

        }

        if(TripsArrayList.get(position).getSmoking() != null && !TripsArrayList.get(position).getSmoking().equalsIgnoreCase("Any")){
            if(TripsArrayList.get(position).getSmoking().equalsIgnoreCase("Yes")){
                holder.smokingIcon.setImageResource(R.drawable.ic_smoking_rooms_green);
            }else if (TripsArrayList.get(position).getSmoking().equalsIgnoreCase("No")){
                holder.smokingIcon.setImageResource(R.drawable.ic_smoke_red);
            }
        }else{
            holder.smokingIcon.setImageResource(R.drawable.ic_smoke_free_grey);

        }

        if(TripsArrayList.get(position).getMusic()!= null){
            switch (TripsArrayList.get(position).getMusic()) {
                case "no music":
                    holder.musicIcon.setImageResource(R.drawable.ic_music_note_red_48px);
                    break;
                case "driver":
                    holder.musicIcon.setImageResource(R.drawable.ic_music_note_green_48px);
                    break;
                case "passenger":
                    holder.musicIcon.setImageResource(R.drawable.ic_music_note_green_48px);
                    break;
                default:
                    holder.musicIcon.setImageResource(R.drawable.ic_music_note_grey_48px);
                    break;

            }
        }else{
            holder.musicIcon.setImageResource(R.drawable.ic_music_note_grey_48px);
        }

        if(TripsArrayList.get(position).getGender()!= null){
            switch (TripsArrayList.get(position).getGender()) {
                case "Any":
                    holder.genderIcon.setImageResource(R.drawable.ic_men_and_women_toilet);
                    break;
                case "Females only":
                    holder.genderIcon.setImageResource(R.drawable.ic_business_woman);
                    break;
                case "Males only":
                    holder.genderIcon.setImageResource(R.drawable.ic_business_man);
                    break;
                default:
                    holder.genderIcon.setImageResource(R.drawable.ic_men_and_women_toilet);
                    break;

            }
        }else {
            holder.genderIcon.setImageResource(R.drawable.ic_men_and_women_toilet);
        }




        if(TripsArrayList.get(position).getDate() != null){
            holder.startTime.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM , DateFormat.SHORT).format(TripsArrayList.get(position).getDate()));
            holder.startTimeIcon.setImageResource(R.drawable.ic_checkered_flag);
        }else{
            holder.startTime.setText(R.string.none);
            holder.startTimeIcon.setImageResource(R.drawable.ic_checkered_flag_grey);

        }

        if (TripsArrayList.get(position).getCreated() != null) {
            holder.publishedOn.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM , DateFormat.SHORT).format(TripsArrayList.get(position).getCreated()));
        }

        if(TripsArrayList.get(position).getOwnerPic()!= null){
            Glide.with(context).load(TripsArrayList.get(position).getOwnerPic()).into(holder.ownerPic);
        }else{
            holder.ownerPic.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
        }

        // add listner for the entire row
        tripPostId = TripsArrayList.get(position).TripPostId; // document ID

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "document id="+tripPostId,
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "document id="+tripPostId);

            }
        });

        ownerId = TripsArrayList.get(position).getOwnerId();

        holder.ownerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "user id="+ownerId,
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "user id="+ownerId);

            }
        });

        holder.ownerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "user id="+ownerId,
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "user id="+ownerId);

            }
        });
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

        public ImageView ownerPic, startTimeIcon, carIcon,
                genderIcon, chatIcon, musicIcon,
                cursingIcon, smokingIcon ;

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
            startTimeIcon = row.findViewById(R.id.time_image);
            carIcon = row.findViewById(R.id.car_icon);
            genderIcon =row.findViewById(R.id.gender_icon);
            chatIcon =row.findViewById(R.id.chat_icon);
            musicIcon = row.findViewById(R.id.music_icon);
            cursingIcon = row.findViewById(R.id.cursing_icon);
            smokingIcon = row.findViewById(R.id.smoking_icon );

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
