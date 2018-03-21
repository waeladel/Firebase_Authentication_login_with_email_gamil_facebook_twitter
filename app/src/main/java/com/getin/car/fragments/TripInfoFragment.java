package com.getin.car.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.getin.car.App;
import com.getin.car.R;
import com.getin.car.authentication.AlarmTime;
import com.getin.car.authentication.Trip;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.getin.car.activities.BaseActivity.trip;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TripInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripInfoFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener ,
        TimePickerDialog.OnTimeSetListener {

    private OnFragmentInteractionListener mListener;
    private static String TAG = TripInfoFragment.class.getSimpleName();



    private enum SettingType {
        DATE_TIME,
        LABLE,
        ROUTE_DETAILS ,
        SEATS,
        TRANSPORTATION,
        COST

    }

    public static final int DATE_TIME_PICKER = 1;
    public static final int LABLE_PICKER = 2;
    public static final int DETAILS_PICKER = 3;
    public static final int SEATS_PICKER = 4;
    public static final int TRANSPORTATION_PICKER = 5;
    public static final int COST_PICKER = 6;



    private Button mRules;
    private TripRulesFragment tripRulesFragment;
    public FragmentManager fragmentManager;



    static TripInfoAdapter settingsAdapter;
    //private static ProgressDialog progressDialog;

    private static int mHourOfDay, mMinute;

    public TripInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment TripInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripInfoFragment newInstance() {
        TripInfoFragment fragment = new TripInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_trip_info, container, false);
        mRules = fragView.findViewById(R.id.add_rules);

        mRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("tripRulesFragment");
            }
        });
        // Setup the list of settings.  Each setting is represented by a Setting
        // object.  Create one here for each setting type.
        final ArrayList<Setting> settingsObjects =
                new ArrayList<>(SettingType.values().length);
        // Only display AlarmInfo if the user is editing an actual alarm (as
        // opposed to the default application settings).

        // The Trip date.
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.date); }
            @Override
            public String value() {
                if(trip.getDate() !=null){
                    /*SimpleDateFormat dateFormatter = new SimpleDateFormat("E, MMMM d, yyyy");
                    dateFormatter.format(trip.getDate());
                    //DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format.format(trip.getDate());*/
                    return DateFormat.getDateTimeInstance(DateFormat.FULL , DateFormat.SHORT).format(trip.getDate());

                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.DATE_TIME; }
        });


        // The Trip available seats.
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.seats); }
            @Override
            public String value() {
                if(trip.getSeats()!=0){
                    Log.d(TAG, "getSeats" + trip.getSeats());

                    return Integer.toString(trip.getSeats());

                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.SEATS; }
        });

        // The Trip cost type
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.cost_dialogue_title); }
            @Override
            public String value() {
                if(trip.getCost() != 0){
                    Log.d(TAG, "getCost" + trip.getCost());
                    return Integer.toString(trip.getCost());
                }else{
                    return getString(R.string.none);
                }
            }
            @Override
            public SettingType type() { return SettingType.COST; }
        });


        // The Trip Transportation type
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.transportation_dialogue_title); }
            @Override
            public String value() {
                if(trip.getTransportationType() !=null){
                    Log.d(TAG, "getSeats" + trip.getTransportationType());

                    if(trip.getTransportationModel() != null){
                        return trip.getTransportationType()+": "+ trip.getTransportationModel();

                    }else{
                        return trip.getTransportationType();
                    }

                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.TRANSPORTATION; }
        });

        // The Trip lable.
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.label); }
            @Override
            public String value() {
                if(trip.getLabel() !=null){
                    Log.d(TAG, "getLabel" + trip.getLabel());
                    return trip.getLabel();
                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.LABLE; }
        });

        // The Trip route details.
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.route_details); }
            @Override
            public String value() {
                if(trip.getDetails() !=null){
                    Log.d(TAG, "getDetails" + trip.getDetails());
                    if(trip.getDetails().contains("null")){
                        return getString(R.string.none);
                    }else{
                        return trip.getDetails();
                    }
                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.ROUTE_DETAILS; }
        });





        final ListView tripInfoList = (ListView) fragView.findViewById(R.id.trip_info_list);
        settingsAdapter = new TripInfoAdapter(getContext(),
                settingsObjects);
        tripInfoList.setAdapter(settingsAdapter);
        tripInfoList.setOnItemClickListener(new tripInfoListClickListener());


        return fragView;
    }

    // Rename method, update argument and hook method into UI event
    public void onButtonPressed(String fragName) {
        if (mListener != null) {
            mListener.onFragmentInteraction(fragName);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String selectedTime = "You picked the following time: "+hourOfDay+"h"+minute+"m"+second;
        //timeTextView.setText(time);
        //trip.setTime(new AlarmTime(hourOfDay, minute, second));
        mHourOfDay = hourOfDay;
        mMinute = minute;

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                TripInfoFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

        //settingsAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth, mHourOfDay,mMinute).getTime();
        trip.setDate(date);
        settingsAdapter.notifyDataSetChanged();


        /*Long Date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis();
        Log.d(TAG, "day =" + day);
        Log.d(TAG, "Date InMillis =" + Date);*/

        /*Calendar c = Calendar.getInstance();
        c.set(Syear, Smonth, Sday, Shours, Sminute);
        long startDate = c.getTimeInMillis();*/

    }


    /**
     * This is a helper class for mapping SettingType to action.  Each Setting
     * in the list view returns a unique SettingType.  Trigger a dialog
     * based off of that SettingType.
     */
    private final class tripInfoListClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final TripInfoAdapter adapter = (TripInfoAdapter) parent.getAdapter();
            Calendar now = Calendar.getInstance();

            SettingType type = null;
            try {
                type = adapter.getItem(position).type();
            } catch (Exception e) {
                e.printStackTrace();
            }

            switch (type) {
                case DATE_TIME:
                    Log.d(TAG, "AdapterType" + type);
                    TimePickerDialog  tpd = TimePickerDialog.newInstance(
                            TripInfoFragment.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            false // false for 12 hour alarm
                    );
                    tpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

                    break;

                case LABLE:
                    Log.d(TAG, "AdapterType" + type);
                    showDialogFragment(LABLE_PICKER);
                    break;

                case ROUTE_DETAILS:
                    Log.d(TAG, "AdapterType" + type);
                    showDialogFragment(DETAILS_PICKER);

                    break;

                case SEATS:
                    Log.d(TAG, "AdapterType" + type);
                    showDialogFragment(SEATS_PICKER);
                    break;
                case TRANSPORTATION :
                    Log.d(TAG, "AdapterType" + type);
                    showDialogFragment(TRANSPORTATION_PICKER);
                    break;
                case COST:
                    Log.d(TAG, "AdapterType" + type);
                    showDialogFragment(COST_PICKER);
                    break;

            }
        }
    }

    private void showDialogFragment(int id) {
        DialogFragment dialog = new ActivityDialogFragment().newInstance(
                id);
        dialog.show(TripInfoFragment.this.getActivity().getFragmentManager(), "ActivityDialogFragment");
    }

    public static class ActivityDialogFragment extends DialogFragment {

        public ActivityDialogFragment newInstance(int id) {
            ActivityDialogFragment fragment = new ActivityDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final View nameView ;
            final EditText label;
            final MaterialEditText costLabel;
            final TextView serviceLabel;
            final AlertDialog.Builder nameBuilder;
            final Spinner transportationType;


            switch (getArguments().getInt("id")) {
                case SEATS_PICKER:
                    nameView = View.inflate(getActivity(),
                            R.layout.name_settings_dialog, null);
                    label = (EditText) nameView.findViewById(R.id.name_label);
                    if(trip.getSeats()!=0){
                        label.setText(String.valueOf(trip.getSeats()));
                    }
                    nameBuilder = new AlertDialog.Builder(getActivity());
                    nameBuilder.setTitle(R.string.seats);
                    nameBuilder.setView(nameView);
                    nameBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            trip.setSeats(Integer.parseInt(label.getEditableText().toString()));
                            settingsAdapter.notifyDataSetChanged();
                            dismiss();
                        }
                    });
                    nameBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                    return nameBuilder.create();
                case DETAILS_PICKER:
                    nameView = View.inflate(getActivity(),
                            R.layout.name_settings_dialog, null);
                    label = (EditText) nameView.findViewById(R.id.name_label);
                    label.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    label.setMaxLines(6);
                    label.setHint(R.string.details_hint);
                    if(trip.getDetails()!= null){
                        label.setText(trip.getDetails());
                    }
                    nameBuilder = new AlertDialog.Builder(getActivity());
                    nameBuilder.setTitle(R.string.route_details);
                    nameBuilder.setView(nameView);
                    nameBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            trip.setDetails(label.getEditableText().toString());
                            settingsAdapter.notifyDataSetChanged();
                            dismiss();
                        }
                    });
                    nameBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                    return nameBuilder.create();
                case LABLE_PICKER :
                    nameView = View.inflate(getActivity(),
                            R.layout.name_settings_dialog, null);
                    label = (EditText) nameView.findViewById(R.id.name_label);
                    label.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    label.setMaxLines(2);
                    label.setHint(R.string.label_hint);
                    if(trip.getLabel()!= null){
                        label.setText(trip.getLabel());
                    }
                    nameBuilder = new AlertDialog.Builder(getActivity());
                    nameBuilder.setTitle(R.string.post_label);
                    nameBuilder.setView(nameView);
                    nameBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            trip.setLabel(label.getEditableText().toString());
                            settingsAdapter.notifyDataSetChanged();
                            dismiss();
                        }
                    });
                    nameBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                    return nameBuilder.create();
                case TRANSPORTATION_PICKER :
                    nameView = View.inflate(getActivity(),
                            R.layout.transportation_settings_dialog, null);
                    serviceLabel = (TextView) nameView.findViewById(R.id.modelText);
                    label = (EditText) nameView.findViewById(R.id.model_editText);
                    //label.setInputType(InputType.TYPE_CLASS_TEXT);
                    label.setMaxLines(1);
                    label.setHint(R.string.transportation_model_hint);

                    transportationType = (Spinner) nameView.findViewById(R.id.transportation_type_spinner);

                    if(trip.getTransportationType()!= null){
                        switch (trip.getTransportationType()){ // display mode spinner value from shared preference
                            case "Private car":
                                transportationType.setSelection(0);
                                break;
                            case "Ride sharing service":
                                transportationType.setSelection(1);
                                break;
                            case "Taxi":
                                transportationType.setSelection(2);
                                break;
                            case "Motorcycle":
                                transportationType.setSelection(3);
                                break;
                            case "Bus":
                                transportationType.setSelection(4);
                                break;
                            case "Boat":
                                transportationType.setSelection(5);
                                break;
                        }
                    }

                    transportationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position){ //switch quality spinner position
                                case 1:
                                    label.setHint(R.string.transportation_service_hint);
                                    serviceLabel.setText(R.string.transportation_service_title);
                                    Log.d(TAG, "ride_sharing spinner selected");
                                    break;
                                case 2:
                                    label.setHint(R.string.transportation_texi_hint);
                                    serviceLabel.setText("");
                                    Log.d(TAG, "Taxi spinner selected");
                                    break;
                                case 4:
                                    label.setHint(R.string.transportation_Bus_hint);
                                    serviceLabel.setText("");
                                    Log.d(TAG, "Bus spinner selected");
                                    break;
                                case 5:
                                    label.setHint(R.string.transportation_boat_hint);
                                    serviceLabel.setText("");
                                    Log.d(TAG, "Bus spinner selected");
                                    break;
                                default:
                                    label.setHint(R.string.transportation_model_hint);
                                    serviceLabel.setText(R.string.model_title);
                                    break;

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });

                    if(trip.getTransportationModel()!= null){
                        label.setText(trip.getTransportationModel());
                    }

                    nameBuilder = new AlertDialog.Builder(getActivity());
                    nameBuilder.setTitle(R.string.transportation_dialogue_title);
                    nameBuilder.setView(nameView);
                    nameBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            trip.setTransportationModel(label.getEditableText().toString());

                            switch (transportationType.getSelectedItemPosition()){ //switch quality spinner position
                                case 0:
                                    trip.setTransportationType("Private car");
                                    break;
                                case 1:
                                    trip.setTransportationType("Ride sharing service");
                                    break;
                                case 2:
                                    trip.setTransportationType("Taxi");
                                    break;
                                case 3:
                                    trip.setTransportationType("Motorcycle");
                                    break;
                                case 4:
                                    trip.setTransportationType("Bus");
                                    break;
                                case 5:
                                    trip.setTransportationType("Boat");
                                    break;
                            }

                            settingsAdapter.notifyDataSetChanged();
                            dismiss();
                        }
                    });
                    nameBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                    return nameBuilder.create();
                case COST_PICKER :
                    nameView = View.inflate(getActivity(),
                            R.layout.cost_settings_dialog, null);
                    costLabel = (MaterialEditText) nameView.findViewById(R.id.cost_editText);
                    if(trip.getCost()!=0){
                        costLabel.setText(String.valueOf(trip.getCost()));
                    }
                    secToHours(trip.getDistance());
                    costLabel.setHelperText(getString(R.string.distance_duration ,trip.getDistance()/1000, secToHours(trip.getDuration())));
                    costLabel.setHelperTextAlwaysShown(true);
                    nameBuilder = new AlertDialog.Builder(getActivity());
                    nameBuilder.setTitle(R.string.cost_dialogue_title);
                    nameBuilder.setView(nameView);
                    nameBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            trip.setCost(Integer.parseInt(costLabel.getEditableText().toString()));
                            settingsAdapter.notifyDataSetChanged();
                            dismiss();
                        }
                    });
                    nameBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                    return nameBuilder.create();
                default:
                    return super.onCreateDialog(savedInstanceState);
            }
        }

        private String secToHours(int sec ) {
            /*int seconds = sec % 60;
            int minutes = sec / 60;
            if (minutes >= 60) {
                int hours = minutes / 60;
                minutes %= 60;
                if( hours >= 24) {
                    int days = hours / 24;
                    return String.format("%d days %02d:%02d:%02d", days,hours%24, minutes, seconds);
                }
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }
            return String.format("00:%02d:%02d", minutes, seconds);*/
            return DateUtils.formatElapsedTime(sec);
        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
   public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String fragName);
    }

    private void showProgressDialog() {
        /*progressDialog = ProgressDialog.show(getContext(),
                getString(R.string.loading),
                getString(R.string.please_wait), true, true);*/
        /*ProgressBar progressBar = new ProgressBar(TripInfoFragment.this.getActivity(),null,android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        fragView.addView(progressBar,params);
        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
        progressBar.setVisibility(View.GONE);     // To Hide ProgressBar*/
    }

    /**
     * A helper interface to encapsulate the data displayed in the list view of
     * this activity.  Consists of a setting name, a setting value, and a type.
     * The type is used to trigger the appropriate action from the onClick
     * handler.
     */
    private abstract class Setting {
        public abstract String name();
        public abstract String value();
        public abstract TripInfoFragment.SettingType type();
    }

    /**
     * This adapter populates the settings_items view with the data encapsulated
     * in the individual Setting objects.
     */
    private final class TripInfoAdapter extends ArrayAdapter<Setting> {

        List<Setting> settingsObjects;

        public TripInfoAdapter(Context context, List<Setting> settingsObjects) {
            super(context, 0, settingsObjects);

            this.settingsObjects = settingsObjects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            LayoutInflater inflater = getLayoutInflater();


            convertView = inflater.inflate(R.layout.trip_info_item, parent,
                        false);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);

            /*if (settingsObjects.get(position).name().
                    equalsIgnoreCase(getString(R.string.visibility_button))) {

                if(settings.getShown()){
                    convertView.setAlpha(1);
                }else {
                    convertView.setAlpha(0.2f);
                    //holder.row.setAlpha(0.2f);
                }
            }*/

            holder.populateFrom(settingsObjects.get(position));

            return(convertView);
        }

    }

    private class ViewHolder {

        private View row;

        private SeekBar seekBar;


        ViewHolder(View row) {
            this.row = row;
        }

        void populateFrom(Setting setting) {
            ((TextView) row.findViewById(R.id.setting_name)).
                    setText(setting.name());

            ((TextView) row.findViewById(R.id.setting_value)).
                    setText(setting.value());

        }

    }
}
