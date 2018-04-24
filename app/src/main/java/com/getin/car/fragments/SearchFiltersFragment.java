package com.getin.car.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.getin.car.R;
import com.getin.car.authentication.Search;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.getin.car.activities.BaseActivity.search;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SearchFiltersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFiltersFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener ,
        TimePickerDialog.OnTimeSetListener {

    private OnFragmentInteractionListener mListener;
    private static String TAG = SearchFiltersFragment.class.getSimpleName();
    private Context getActivityContext;



    private enum SettingType {
        FROM_DATE_TIME,
        UNTIL_DATE_TIME,
        TRANSPORTATION,
        COST,
        GENDER,
        CHAT,
        CURSING,
        SMOKING ,
        MUSIC,
        DRIVING
    }

    public static final int FROM_DATE_TIME_PICKER = 1;
    public static final int UNTIL_DATE_TIME_PICKER = 2;
    public static final int COST_PICKER = 3;
    public static final int TRANSPORTATION_PICKER = 4;


    private Button mStartSeatch;
    public FragmentManager fragmentManager;
    private SharedPreferences mTripsPreference;


    static TripInfoAdapter settingsAdapter;
    //private static ProgressDialog progressDialog;

    private static int mHourOfDay, mMinute;

    public SearchFiltersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment TripInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFiltersFragment newInstance() {
        SearchFiltersFragment fragment = new SearchFiltersFragment();
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
        View fragView = inflater.inflate(R.layout.fragment_search_filters, container, false);
        mStartSeatch = fragView.findViewById(R.id.start_search);

        // read and write Shared Preference key values form Preference file
        mTripsPreference = PreferenceManager.getDefaultSharedPreferences(getActivityContext);
        String orderBy = mTripsPreference.getString("orderBy", "date");

        mStartSeatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("searchFiltersFragment");
            }
        });
        // Setup the list of settings.  Each setting is represented by a Setting
        // object.  Create one here for each setting type.
        final ArrayList<Setting> settingsObjects =
                new ArrayList<>(SettingType.values().length);
        // Only display AlarmInfo if the user is editing an actual alarm (as
        // opposed to the default application settings).

        if(orderBy.equalsIgnoreCase("date")){
            // The Trip from date.
            settingsObjects.add(new Setting() {
                @Override
                public String name() { return getString(R.string.from_date); }
                @Override
                public String value() {
                    if(search.getFromDate() !=null){
                    /*SimpleDateFormat dateFormatter = new SimpleDateFormat("E, MMMM d, yyyy");
                    dateFormatter.format(trip.getDate());
                    //DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format.format(trip.getDate());*/
                        return DateFormat.getDateTimeInstance(DateFormat.FULL , DateFormat.SHORT).format(search.getFromDate());

                    }else{
                        return getString(R.string.none);
                    }

                }
                @Override
                public SettingType type() { return SettingType.FROM_DATE_TIME; }
            });

            // The Trip until date.
            settingsObjects.add(new Setting() {
                @Override
                public String name() { return getString(R.string.until_date); }
                @Override
                public String value() {
                    if(search.getTillDate() !=null){
                    /*SimpleDateFormat dateFormatter = new SimpleDateFormat("E, MMMM d, yyyy");
                    dateFormatter.format(trip.getDate());
                    //DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format.format(trip.getDate());*/
                        return DateFormat.getDateTimeInstance(DateFormat.FULL , DateFormat.SHORT).format(search.getTillDate());

                    }else{
                        return getString(R.string.none);
                    }

                }
                @Override
                public SettingType type() { return SettingType.UNTIL_DATE_TIME; }
            });
        }else if(orderBy.equalsIgnoreCase("cost")){
            // The Trip cost type
            settingsObjects.add(new Setting() {
                @Override
                public String name() { return getString(R.string.cost_dialogue_title); }
                @Override
                public String value() {
                    if(search.getMinCost() != null && search.getMaxCost() != null){
                        Log.d(TAG, "getMinCost" + search.getMinCost()+ "getMaxCost"+search.getMaxCost());
                        return getString(R.string.cost_min_max,search.getMinCost(), search.getMaxCost());
                    }else if (search.getMinCost() != null){
                        return getString(R.string.cost_min_val,search.getMinCost());
                    }else if (search.getMaxCost() != null){
                        return getString(R.string.cost_max_val,search.getMaxCost());
                    }else{
                        return getString(R.string.none);
                    }
                }
                @Override
                public SettingType type() { return SettingType.COST; }
            });
        }
        // The Trip Transportation type
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.transportation_dialogue_title); }
            @Override
            public String value() {
                if(search.hasTransportationType()){
                    Log.d(TAG, "getTransportationType" + search.getTransportationType());
                    return search.getTransportationType();
                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.TRANSPORTATION; }
        });

        // The gender Objects
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.gender); }
            @Override
            public String value() {
                if(search.hasGender()){
                    Log.d(TAG, "getGender=" +search.getGender());
                    return search.getGender();
                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.GENDER; }
        });

        // The chat Objects
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.chat); }
            @Override
            public String value() {
                if(search.hasChat()){
                    Log.d(TAG, "getGender=" +search.getChat());
                    return search.getChat();
                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.CHAT; }
        });

        // The CURSING Objects
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.cursing); }
            @Override
            public String value() {
                if(search.hasCursing()){
                    Log.d(TAG, "getCursing=" +search.getCursing());
                    return search.getCursing();
                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.CURSING; }
        });

        // The SMOKING  Objects
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.smoking); }
            @Override
            public String value() {
                if(search.hasSmoking()){
                    Log.d(TAG, "getSmoking=" +search.getSmoking());
                    return search.getSmoking();
                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.SMOKING ; }
        });

        // The MUSIC  Objects
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.music); }
            @Override
            public String value() {
                if(search.hasMusic()){
                    Log.d(TAG, "getMusic=" +search.getMusic());
                    return search.getMusic();
                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.MUSIC ; }
        });

        // The DRIVING  Objects
        settingsObjects.add(new Setting() {
            @Override
            public String name() { return getString(R.string.driving_style); }
            @Override
            public String value() {
                if(search.hasDriving()){
                    Log.d(TAG, "getDriving=" +search.getDriving());
                    return search.getDriving();
                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public SettingType type() { return SettingType.DRIVING ; }
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
        getActivityContext = context;
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
                SearchFiltersFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        if(view.getTag().equalsIgnoreCase("fromTimePickerDialog")){// it's from picker
            dpd.show(getActivity().getFragmentManager(), "fromDatePickerDialog");

        }else{
            dpd.show(getActivity().getFragmentManager(), "untilDatePickerDialog");
        }

        //settingsAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth, mHourOfDay,mMinute).getTime();
        if (view.getTag().equalsIgnoreCase("fromDatePickerDialog")) {
            search.setFromDate(date);
        }else{
            search.setTillDate(date);
        }
        settingsAdapter.notifyDataSetChanged();

        /*Long Date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis();
        Log.d(TAG, "day =" + day);
        Log.d(TAG, "Date InMillis =" + Date);*/

        /*Calendar c = Calendar.getInstance();
        c.set(Syear, Smonth, Sday, Shours, Sminute);
        long startDate = c.getTimeInMillis();*/

    }

   /* private static boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }*/


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
                case FROM_DATE_TIME:
                    Log.d(TAG, "AdapterType" + type);
                    TimePickerDialog  FromTpd = TimePickerDialog.newInstance(
                            SearchFiltersFragment.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            false // false for 12 hour alarm
                    );
                    FromTpd.show(getActivity().getFragmentManager(), "fromTimePickerDialog");

                    break;
                case UNTIL_DATE_TIME:
                    Log.d(TAG, "AdapterType" + type);
                    TimePickerDialog  untilTpd = TimePickerDialog.newInstance(
                            SearchFiltersFragment.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            false // false for 12 hour alarm
                    );
                    untilTpd.show(getActivity().getFragmentManager(), "untilTimePickerDialog");

                    break;
                case TRANSPORTATION :
                    Log.d(TAG, "AdapterType" + type);
                    //showDialogFragment(TRANSPORTATION_PICKER);
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
        dialog.show(SearchFiltersFragment.this.getActivity().getFragmentManager(), "ActivityDialogFragment");
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
            final MaterialEditText minCostLabel;
            final MaterialEditText maxCostLabel;

            final TextView serviceLabel;
            final AlertDialog.Builder nameBuilder;
            final Spinner transportationType;


            switch (getArguments().getInt("id")) {

                /*case TRANSPORTATION_PICKER :
                    nameView = View.inflate(getActivity(),
                            R.layout.transportation_settings_dialog, null);
                    // hide model, it's not used for search
                    serviceLabel = (TextView) nameView.findViewById(R.id.modelText);
                    label = (EditText) nameView.findViewById(R.id.model_editText);
                    label.setVisibility(View.GONE);
                    serviceLabel.setVisibility(View.GONE);
                    //label.setInputType(InputType.TYPE_CLASS_TEXT);
                    *//*label.setMaxLines(1);
                    label.setHint(R.string.transportation_model_hint);*//*

                    transportationType = (Spinner) nameView.findViewById(R.id.transportation_type_spinner);

                    if(search.getTransportationType()!= null){
                        switch (search.getTransportationType()){ // display mode spinner value from shared preference
                            case "Any":
                                transportationType.setSelection(0);
                                break;
                            case "Private car":
                                transportationType.setSelection(1);
                                break;
                            case "Ride sharing service":
                                transportationType.setSelection(2);
                                break;
                            case "Taxi":
                                transportationType.setSelection(3);
                                break;
                            case "Motorcycle":
                                transportationType.setSelection(4);
                                break;
                            case "Bus":
                                transportationType.setSelection(5);
                                break;
                            case "Boat":
                                transportationType.setSelection(6);
                                break;
                        }
                    }

                    *//*transportationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

                    });*//*

                    nameBuilder = new AlertDialog.Builder(getActivity());
                    nameBuilder.setTitle(R.string.transportation_dialogue_title);
                    nameBuilder.setView(nameView);
                    nameBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            switch (transportationType.getSelectedItemPosition()){ //switch quality spinner position
                                case 0:
                                    search.setTransportationType("Any");
                                    break;
                                case 1:
                                    search.setTransportationType("Private car");
                                    break;
                                case 2:
                                    search.setTransportationType("Ride sharing service");
                                    break;
                                case 3:
                                    search.setTransportationType("Taxi");
                                    break;
                                case 4:
                                    search.setTransportationType("Motorcycle");
                                    break;
                                case 5:
                                    search.setTransportationType("Bus");
                                    break;
                                case 6:
                                    search.setTransportationType("Boat");
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
                    return nameBuilder.create();*/
                case COST_PICKER :
                    nameView = View.inflate(getActivity(),
                            R.layout.cost_min_max_dialog, null);
                    minCostLabel = (MaterialEditText) nameView.findViewById(R.id.min_cost_editText);
                    if(search.getMinCost()!= null){
                        minCostLabel.setText(String.valueOf(search.getMinCost()));
                    }
                    maxCostLabel = (MaterialEditText) nameView.findViewById(R.id.max_cost_editText);
                    if(search.getMaxCost()!= null){
                        maxCostLabel.setText(String.valueOf(search.getMaxCost()));
                    }
                    nameBuilder = new AlertDialog.Builder(getActivity());
                    nameBuilder.setTitle(R.string.cost_dialogue_title);
                    nameBuilder.setView(nameView);
                    nameBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!TextUtils.isEmpty(minCostLabel.getEditableText())){
                                search.setMinCost(Integer.parseInt(minCostLabel.getEditableText().toString()));
                            }else{
                                search.setMinCost(null);
                            }
                            if(!TextUtils.isEmpty(maxCostLabel.getEditableText())){
                                search.setMaxCost(Integer.parseInt(maxCostLabel.getEditableText().toString()));
                            }else{
                                search.setMaxCost(null);
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
        public abstract SearchFiltersFragment.SettingType type();
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

            switch (settingsObjects.get(position).type()) {
                case TRANSPORTATION:
                    convertView = inflater.inflate(R.layout.spinner_item, parent,
                            false);
                    break;
                case GENDER:
                    convertView = inflater.inflate(R.layout.spinner_item, parent,
                            false);
                    break;
                case CHAT:
                    convertView = inflater.inflate(R.layout.spinner_item, parent,
                            false);

                    break;
                case CURSING:
                   Log.d(TAG, "TRANSPORTATION View=" );
                    convertView = inflater.inflate(R.layout.spinner_item, parent,
                            false);
                    break;

                case SMOKING:
                    Log.d(TAG, "SMOKING View=" );
                    convertView = inflater.inflate(R.layout.spinner_item, parent,
                            false);
                    break;
                case MUSIC:
                    Log.d(TAG, "MUSIC View=" );
                    convertView = inflater.inflate(R.layout.spinner_item, parent,
                            false);
                    break;
                case DRIVING:
                    Log.d(TAG, "DRIVING View=" );
                    convertView = inflater.inflate(R.layout.spinner_item, parent,
                            false);
                    break;
                default:
                    convertView = inflater.inflate(R.layout.trip_info_item, parent,
                            false);
                    break;

            }


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

            ArrayAdapter<CharSequence> spinnerAdapter;
            Spinner spinner;

            ((TextView) row.findViewById(R.id.setting_name)).
                    setText(setting.name());

            switch (setting.type()) {
                case TRANSPORTATION:
                    spinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "TRANSPORTATION =" + search.getTransportationType());

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.search_transportation_type, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);

                    if (search.hasTransportationType()) {
                        switch (search.getTransportationType()){ // display mode spinner value from shared preference
                            case "Any":
                                spinner.setSelection(0);
                                break;
                            case "Private car":
                                spinner.setSelection(1);
                                break;
                            case "Ride sharing service":
                                spinner.setSelection(2);
                                break;
                            case "Taxi":
                                spinner.setSelection(3);
                                break;
                            case "Motorcycle":
                                spinner.setSelection(4);
                                break;
                            case "Bus":
                                spinner.setSelection(5);
                                break;
                            case "Boat":
                                spinner.setSelection(6);
                                break;
                        }
                    }

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position){ //switch quality spinner position
                                case 0:
                                    search.setTransportationType("Any");
                                    Log.d(TAG, "TransportationSpinner=" + search.getTransportationType());
                                    break;
                                case 1:
                                    search.setTransportationType("Private car");
                                    Log.d(TAG, "TransportationSpinner=" + search.getTransportationType());
                                    break;
                                case 2:
                                    search.setTransportationType("Ride sharing service");
                                    Log.d(TAG, "TransportationSpinner=" + search.getTransportationType());
                                    break;
                                case 3:
                                    search.setTransportationType("Taxi");
                                    Log.d(TAG, "TransportationSpinner=" + search.getTransportationType());
                                    break;
                                case 4:
                                    search.setTransportationType("Motorcycle");
                                    Log.d(TAG, "TransportationSpinner=" + search.getTransportationType());
                                    break;
                                case 5:
                                    search.setTransportationType("Bus");
                                    Log.d(TAG, "TransportationSpinner=" + search.getTransportationType());
                                    break;
                                case 6:
                                    search.setTransportationType("Boat");
                                    Log.d(TAG, "TransportationSpinner=" + search.getTransportationType());
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });

                    ((TextView) row.findViewById(R.id.setting_value)).
                            setText(setting.value());

                    settingsAdapter.notifyDataSetChanged();
                    break;
                case GENDER:
                    spinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "GENDER =" + search.getGender());

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.gender_search, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);

                    if (search.getGender() != null) {
                        switch (search.getGender()) { // display mode spinner value from shared preference
                            case "Any":
                                spinner.setSelection(0);
                                break;
                            case "Females only":
                                spinner.setSelection(1);
                                break;
                            case "Males only":
                                spinner.setSelection(2);
                                break;
                        }
                    }

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position) { //switch quality spinner position
                                case 0:
                                    search.setGender("Any");
                                    Log.d(TAG, "genderSpinner Any=" + search.getGender());
                                    break;
                                case 1:
                                    search.setGender("Females only");
                                    Log.d(TAG, "genderSpinner Females=" + search.getGender());

                                    break;
                                case 2:
                                    search.setGender("Males only");
                                    Log.d(TAG, "genderSpinner Males=" + search.getGender());
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });

                    ((TextView) row.findViewById(R.id.setting_value)).
                            setText(setting.value());

                    settingsAdapter.notifyDataSetChanged();
                    break;

                case CHAT:
                    spinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "getChat =" + search.getChat());

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.spinner_welcomed_search, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);

                    if (search.hasChat()) {
                        switch (search.getChat()) { // display mode spinner value from shared preference
                            case "Any":
                                spinner.setSelection(0);
                                break;
                            case "Yes":
                                spinner.setSelection(1);
                                break;
                            case "No":
                                spinner.setSelection(2);
                                break;
                        }
                    }

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position) { //switch quality spinner position
                                case 0:
                                    search.setChat("Any");
                                    Log.d(TAG, "chatSpinner =" + search.getChat());

                                    break;
                                case 1:
                                    search.setChat("Yes");
                                    Log.d(TAG, "chatSpinner =" + search.getChat());

                                    break;
                                case 2:
                                    search.setChat("No");
                                    Log.d(TAG, "chatSpinner =" + search.getChat());
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });

                    ((TextView) row.findViewById(R.id.setting_value)).
                            setText(setting.value());

                    settingsAdapter.notifyDataSetChanged();
                    break;

                case CURSING:
                    spinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "getCursing =" + search.getCursing());

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.spinner_welcomed_search, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);

                    if (search.hasCursing()) {
                        switch (search.getCursing()) { // display mode spinner value from shared preference
                            case "Any":
                                spinner.setSelection(0);
                                break;
                            case "Yes":
                                spinner.setSelection(1);
                                break;
                            case "No":
                                spinner.setSelection(2);
                                break;
                        }
                    }

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position) { //switch quality spinner position
                                case 0:
                                    search.setCursing("Any");
                                    Log.d(TAG, "CurseSpinner =" + search.getCursing());
                                    break;
                                case 1:
                                    search.setCursing("Yes");
                                    Log.d(TAG, "CurseSpinner =" + search.getCursing());
                                    break;
                                case 2:
                                    search.setCursing("No");
                                    Log.d(TAG, "CurseSpinner =" + search.getCursing());
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }
                    });

                    ((TextView) row.findViewById(R.id.setting_value)).
                            setText(setting.value());

                    settingsAdapter.notifyDataSetChanged();
                    break;

                case SMOKING:
                    spinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "getSmoking =" + search.getSmoking());

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.spinner_allowed_search, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);

                    if (search.hasSmoking()) {
                        switch (search.getSmoking()) { // display mode spinner value from shared preference
                            case "Any":
                                spinner.setSelection(0);
                                break;
                            case "Yes":
                                spinner.setSelection(1);
                                break;
                            case "No":
                                spinner.setSelection(2);
                                break;
                        }
                    }

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position) { //switch quality spinner position
                                case 0:
                                    search.setSmoking("Any");
                                    Log.d(TAG, "SmokingSpinner =" + search.getSmoking());
                                    break;
                                case 1:
                                    search.setSmoking("Yes");
                                    Log.d(TAG, "SmokingSpinner =" + search.getSmoking());
                                    break;
                                case 2:
                                    search.setSmoking("No");
                                    Log.d(TAG, "SmokingSpinner =" + search.getSmoking());
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }
                    });

                    ((TextView) row.findViewById(R.id.setting_value)).
                            setText(setting.value());

                    settingsAdapter.notifyDataSetChanged();
                    break;

                case MUSIC:
                    spinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "getMusic =" + search.getMusic());

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.music_any, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);

                    if (search.hasMusic()) {
                        switch (search.getMusic()) { // display mode spinner value from shared preference
                            case "Any":
                                spinner.setSelection(0);
                                break;
                            case "no music":
                                spinner.setSelection(1);
                                break;
                            case "driver":
                                spinner.setSelection(2);
                                break;
                            case "passenger":
                                spinner.setSelection(3);
                                break;
                            default:
                                spinner.setSelection(0);
                                break;
                        }
                    }

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position) { //switch quality spinner position
                                case 0:
                                    search.setMusic("Any");
                                    Log.d(TAG, "MusicSpinner =" + search.getMusic());
                                    break;
                                case 1:
                                    search.setMusic("no music");
                                    Log.d(TAG, "MusicSpinner =" + search.getMusic());
                                    break;
                                case 2:
                                    search.setMusic("driver");
                                    Log.d(TAG, "MusicSpinner =" + search.getMusic());
                                    break;
                                case 3:
                                    search.setMusic("passenger");
                                    Log.d(TAG, "MusicSpinner =" + search.getMusic());
                                    break;
                                default:
                                    search.setMusic("any");
                                    Log.d(TAG, "MusicSpinner =" + search.getMusic());
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }
                    });

                    ((TextView) row.findViewById(R.id.setting_value)).
                            setText(setting.value());

                    settingsAdapter.notifyDataSetChanged();
                    break;

                case DRIVING:
                    spinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "getDriving =" + search.getDriving());

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.driving_any, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerAdapter);

                    if (search.hasDriving()) {
                        switch (search.getDriving()) { // display mode spinner value from shared preference
                            case "Any":
                                spinner.setSelection(0);
                                break;
                            case "Safe driving":
                                spinner.setSelection(1);
                                break;
                            case "Rash driving":
                                spinner.setSelection(2);
                                break;
                        }
                    }

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position) { //switch quality spinner position
                                case 0:
                                    search.setDriving("Any");
                                    Log.d(TAG, "DrivingSpinner =" + search.getDriving());

                                    break;
                                case 1:
                                    search.setDriving("Safe driving");
                                    Log.d(TAG, "DrivingSpinner =" + search.getDriving());

                                    break;
                                case 2:
                                    search.setDriving("Rash driving");
                                    Log.d(TAG, "DrivingSpinner =" + search.getDriving());
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }
                    });

                    ((TextView) row.findViewById(R.id.setting_value)).
                            setText(setting.value());

                    settingsAdapter.notifyDataSetChanged();
                    break;

                default:
                    ((TextView) row.findViewById(R.id.setting_value)).
                            setText(setting.value());
                    break;

            }

        }
    }
}
