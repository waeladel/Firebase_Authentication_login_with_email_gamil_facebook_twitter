package com.getin.car.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

import static com.getin.car.activities.BaseActivity.trip;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripRulesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TripRulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripRulesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static String TAG = TripRulesFragment.class.getSimpleName();
    private Button mPostButton;
    private Context getActivityContext;


    private enum SettingType {
        GENDER,
        CHAT,
        CURSING,
        SMOKING ,
        MUSIC,
        DRIVING
    }

    public static final int GENDER_PICKER = 1;
    public static final int CHAT_PICKER = 2;
    public static final int CURSING_PICKER = 3;
    public static final int SMOKING_PICKER = 4;
    public static final int MUSIC_PICKER = 5;
    public static final int DRIVING_PICKER = 6;


    static TripRulesAdapter settingsAdapter;


    public TripRulesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment TripRulesFragment.
     */
    public static TripRulesFragment newInstance() {
        TripRulesFragment fragment = new TripRulesFragment();
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
        View fragView = inflater.inflate(R.layout.fragment_trip_rules, container, false);
        mPostButton = fragView.findViewById(R.id.post_trip);
        // Setup the list of settings.  Each setting is represented by a Setting
        // object.  Create one here for each setting type.
        final ArrayList<TripRulesFragment.Setting> settingsObjects =
                new ArrayList<>(TripRulesFragment.SettingType.values().length);
        // Only display AlarmInfo if the user is editing an actual alarm (as
        // opposed to the default application settings).
        Log.d(TAG, "getGender=" +trip.getGender());

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("postTrip");
            }
        });
        // The gender Objects
        settingsObjects.add(new TripRulesFragment.Setting() {
            @Override
            public String name() { return getString(R.string.gender); }
            @Override
            public String value() {
                if(trip.getGender() !=null){
                    Log.d(TAG, "getGender=" +trip.getGender());
                    return trip.getGender();
                }else{
                    return getString(R.string.none);
                }

            }
            @Override
            public TripRulesFragment.SettingType type() { return SettingType.GENDER; }
        });

        // The chat Objects
        settingsObjects.add(new TripRulesFragment.Setting() {
            @Override
            public String name() { return getString(R.string.chat); }
            @Override
            public String value() {
                if(trip.hasChat()){
                    Log.d(TAG, "getChat=" +trip.getChat());
                    return trip.getChat();
                }else{
                    return getString(R.string.none);
                }
            }
            @Override
            public TripRulesFragment.SettingType type() { return SettingType.CHAT; }
        });

        // The cursing Objects
        settingsObjects.add(new TripRulesFragment.Setting() {
            @Override
            public String name() { return getString(R.string.cursing); }
            @Override
            public String value() {
                if(trip.hasCursing()){
                    Log.d(TAG, "getCursing=" +trip.getCursing());
                    return trip.getCursing();
                }else{
                    return getString(R.string.none);
                }
            }
            @Override
            public TripRulesFragment.SettingType type() { return SettingType.CURSING; }
        });

        // The smoking Objects
        settingsObjects.add(new TripRulesFragment.Setting() {
            @Override
            public String name() { return getString(R.string.smoking); }
            @Override
            public String value() {

                if(trip.hasSmoking()){
                    Log.d(TAG, "getSmoking=" +trip.getSmoking());
                    return trip.getSmoking();
                }else{
                    return getString(R.string.none);
                }
            }
            @Override
            public TripRulesFragment.SettingType type() { return SettingType.SMOKING; }
        });

        // The driving style Objects
        settingsObjects.add(new TripRulesFragment.Setting() {
            @Override
            public String name() { return getString(R.string.driving_style); }
            @Override
            public String value() {
                if (trip.getDriving() != null) {
                    Log.d(TAG, "getDriving=" + trip.getDriving());
                    return trip.getDriving();
                } else {
                    return getString(R.string.none);
                }

            }


            @Override
            public TripRulesFragment.SettingType type() { return SettingType.DRIVING; }
        });

        // The music Objects
        settingsObjects.add(new TripRulesFragment.Setting() {
            @Override
            public String name() { return getString(R.string.music); }
            @Override
            public String value() {
                if (trip.hasMusic()) {
                    switch (trip.getMusic()){ // display mode spinner value from shared preference
                        case "not specified":
                            return getString(R.string.none);
                        case "no music":
                            return getString(R.string.no_music);
                        case "driver":
                            if(trip.getGenre() != null){
                                return getString(R.string.driver)+": "+ trip.getGenre();
                            }else{
                                return getString(R.string.driver);
                            }
                        case "passenger":
                            if(trip.getGenre() != null){
                                return getString(R.string.passenger)+": "+ trip.getGenre();
                            }else{
                                return getString(R.string.passenger);
                            }
                        default:
                            return trip.getMusic();
                    }
                    //Log.d(TAG, "getGender=" + trip.getMusic());
                    //return trip.getMusic();
                } else {
                    return getString(R.string.none);
                }

            }

            @Override
            public TripRulesFragment.SettingType type() { return SettingType.MUSIC; }
        });


        final ListView tripRulesList = (ListView) fragView.findViewById(R.id.trip_rules_list);
        settingsAdapter = new TripRulesAdapter(getContext(),
                settingsObjects);
        tripRulesList.setAdapter(settingsAdapter);
        tripRulesList.setOnItemClickListener(new tripRulesListClickListener());


        return fragView;
    }

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This is a helper class for mapping SettingType to action.  Each Setting
     * in the list view returns a unique SettingType.  Trigger a dialog
     * based off of that SettingType.
     */
    private final class tripRulesListClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final TripRulesAdapter adapter = (TripRulesAdapter) parent.getAdapter();
            SettingType type = null;
            try {
                type = adapter.getItem(position).type();
            } catch (Exception e) {
                e.printStackTrace();
            }

            switch (type) {
                case MUSIC:
                    Log.d(TAG, "MUSIC onItemClick");
                    showDialogFragment(MUSIC_PICKER);
                    break;

            }
        }
    }

    private void showDialogFragment(int id) {
        DialogFragment dialog = new TripRulesFragment.ActivityDialogFragment().newInstance(
                id);
        dialog.show(TripRulesFragment.this.getActivity().getFragmentManager(), "ActivityDialogFragment");
    }

    public static class ActivityDialogFragment extends DialogFragment {

        public TripRulesFragment.ActivityDialogFragment newInstance(int id) {
            TripRulesFragment.ActivityDialogFragment fragment = new TripRulesFragment.ActivityDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final View nameView ;
            final EditText genre;
            final AlertDialog.Builder nameBuilder;
            final Spinner musicSpinner;


            switch (getArguments().getInt("id")) {
                case MUSIC_PICKER:

                    nameView = View.inflate(getActivity(),
                            R.layout.music_settings_dialog, null);

                    genre = (EditText) nameView.findViewById(R.id.genre_editText);
                    musicSpinner = nameView.findViewById(R.id.music_spinner);

                    if(trip.getGenre() != null){
                        genre.setText(trip.getGenre());
                    }

                    if(trip.getMusic()!= null){
                        switch (trip.getMusic()){ // display mode spinner value from shared preference
                            case "not specified":
                                musicSpinner.setSelection(0);
                                genre.setEnabled(false);
                                genre.setHint(R.string.disabled);
                                genre.setText(null);
                                break;
                            case "no music":
                                musicSpinner.setSelection(1);
                                genre.setEnabled(false);
                                genre.setHint(R.string.disabled);
                                genre.setText(null);
                                break;
                            case "driver":
                                musicSpinner.setSelection(2);
                                genre.setEnabled(true);
                                genre.setHint(R.string.music_genre_hint);
                                break;
                            case "passenger":
                                musicSpinner.setSelection(3);
                                genre.setEnabled(true);
                                genre.setHint(R.string.music_genre_hint);
                                break;
                        }
                    }

                    musicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position){ //switch quality spinner position
                                case 0:
                                    //trip.setMusic("none");
                                    genre.setEnabled(false);
                                    genre.setHint(R.string.disabled);
                                    genre.setText(null);
                                    Log.d(TAG, "not specified selected");
                                    break;
                                case 1:
                                    //trip.setMusic("none");
                                    genre.setEnabled(false);
                                    genre.setHint(R.string.disabled);
                                    genre.setText(null);
                                    Log.d(TAG, "no music spinner selected");
                                    break;
                                case 2:
                                    //trip.setMusic("driver");
                                    genre.setEnabled(true);
                                    genre.setHint(R.string.music_genre_hint);
                                    Log.d(TAG, "driver spinner selected");
                                    break;
                                case 3:
                                    //trip.setMusic("passenger");
                                    genre.setEnabled(true);
                                    genre.setHint(R.string.music_genre_hint);
                                    Log.d(TAG, "passenger spinner selected");
                                    break;
                                default:
                                    //label.setVisibility(View.GONE);
                                    //trip.setMusic("none");
                                    genre.setEnabled(false);
                                    genre.setText(null);
                                    Log.d(TAG, "none spinner selected");
                                    break;

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });

                    nameBuilder = new AlertDialog.Builder(getActivity());
                    nameBuilder.setTitle(R.string.music_dialogue_title);
                    nameBuilder.setView(nameView);
                    nameBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(genre.getEditableText() != null && !TextUtils.isEmpty(genre.getEditableText())){
                                trip.setGenre(genre.getEditableText().toString());
                            }else{
                                trip.setGenre(null);
                                Log.d(TAG, "set setGenre to null");

                            }

                            switch (musicSpinner.getSelectedItemPosition()){ //switch quality spinner position
                                case 0:
                                    trip.setMusic("not specified");
                                    break;
                                case 1:
                                    trip.setMusic("no music");
                                    break;
                                case 2:
                                    trip.setMusic("driver");
                                    break;
                                case 3:
                                    trip.setMusic("passenger");
                                    break;
                                default:
                                    trip.setMusic("not specified");
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

                default:
                    return super.onCreateDialog(savedInstanceState);
            }
        }

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
        public abstract SettingType type();
    }

    /**
     * This adapter populates the settings_items view with the data encapsulated
     * in the individual Setting objects.
     */
    private final class TripRulesAdapter extends ArrayAdapter<Setting> {

        List<Setting> settingsObjects;

        public TripRulesAdapter(Context context, List<Setting> settingsObjects) {
            super(context, 0, settingsObjects);

            this.settingsObjects = settingsObjects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater inflater = getLayoutInflater();

            switch (settingsObjects.get(position).type()) {
                case MUSIC:
                    Log.d(TAG, "MUSIC View=" );
                    convertView = inflater.inflate(R.layout.toggled_settings_item, parent,
                            false);
                    break;
                default:
                    convertView = inflater.inflate(R.layout.spinner_item, parent,
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

        private SwitchCompat ruleSwitch;

        ArrayAdapter<CharSequence> spinnerAdapter;

        ViewHolder(View row) {
            this.row = row;
        }

        void populateFrom(Setting setting) {
            ((TextView) row.findViewById(R.id.setting_name)).
                    setText(setting.name());
            switch (setting.type()) {
                case GENDER:
                    Spinner genderSpinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.gender_post, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    genderSpinner.setAdapter(spinnerAdapter);

                    Log.d(TAG, "GENDER ="+trip.getGender() );
                    if(trip.getGender()!= null){
                        switch (trip.getGender()){ // display mode spinner value from shared preference
                            case "Any":
                                genderSpinner.setSelection(0);
                                break;
                            case "Females only":
                                genderSpinner.setSelection(1);
                                break;
                            case "Males only":
                                genderSpinner.setSelection(2);
                                break;
                        }
                    }

                    genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position){ //switch quality spinner position
                                case 0:
                                    trip.setGender("Any");
                                    Log.d(TAG, "genderSpinner Any="+trip.getGender() );

                                    break;
                                case 1:
                                    trip.setGender("Females only");
                                    Log.d(TAG, "genderSpinner Females="+trip.getGender() );

                                    break;
                                case 2:
                                    trip.setGender("Males only");
                                    Log.d(TAG, "genderSpinner Males="+trip.getGender() );

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
                    Spinner chatSpinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "getChat ="+trip.getChat() );

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.spinner_welcomed_post, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chatSpinner.setAdapter(spinnerAdapter);

                    if(trip.hasChat()){
                        switch (trip.getChat()){ // display mode spinner value from shared preference
                            case "Any":
                                chatSpinner.setSelection(0);
                                break;
                            case "Yes":
                                chatSpinner.setSelection(1);
                                break;
                            case "No":
                                chatSpinner.setSelection(2);
                                break;
                        }
                    }

                    chatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position){ //switch quality spinner position
                                case 0:
                                    trip.setChat("Any");
                                    Log.d(TAG, "chatSpinner Any="+trip.getChat() );

                                    break;
                                case 1:
                                    trip.setChat("Yes");
                                    Log.d(TAG, "chatSpinner="+trip.getChat() );

                                    break;
                                case 2:
                                    trip.setChat("No");
                                    Log.d(TAG, "chatSpinner="+trip.getChat() );

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
                    Spinner cursingSpinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "getCursing ="+trip.getCursing() );

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.spinner_welcomed_post, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cursingSpinner.setAdapter(spinnerAdapter);

                    if(trip.hasCursing()){
                        switch (trip.getCursing()){ // display mode spinner value from shared preference
                            case "Any":
                                cursingSpinner.setSelection(0);
                                break;
                            case "Yes":
                                cursingSpinner.setSelection(1);
                                break;
                            case "No":
                                cursingSpinner.setSelection(2);
                                break;
                        }
                    }

                    cursingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position){ //switch quality spinner position
                                case 0:
                                    trip.setCursing("Any");
                                    Log.d(TAG, "cursingSpinner Any="+trip.getChat() );
                                    break;
                                case 1:
                                    trip.setCursing("Yes");
                                    Log.d(TAG, "cursingSpinner="+trip.getChat() );
                                    break;
                                case 2:
                                    trip.setCursing("No");
                                    Log.d(TAG, "cursingSpinner="+trip.getChat() );
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
                    Spinner smokingSpinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "getSmoking ="+trip.getSmoking() );

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.spinner_allowed_post, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    smokingSpinner.setAdapter(spinnerAdapter);

                    if(trip.hasSmoking()){
                        switch (trip.getSmoking()){ // display mode spinner value from shared preference
                            case "Any":
                                smokingSpinner.setSelection(0);
                                break;
                            case "Yes":
                                smokingSpinner.setSelection(1);
                                break;
                            case "No":
                                smokingSpinner.setSelection(2);
                                break;
                        }
                    }

                    smokingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position){ //switch quality spinner position
                                case 0:
                                    trip.setSmoking("Any");
                                    Log.d(TAG, "smokingSpinner Any="+trip.getSmoking() );
                                    break;
                                case 1:
                                    trip.setSmoking("Yes");
                                    Log.d(TAG, "smokingSpinner="+trip.getSmoking() );
                                    break;
                                case 2:
                                    trip.setSmoking("No");
                                    Log.d(TAG, "smokingSpinner="+trip.getSmoking() );
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

                    ruleSwitch = (SwitchCompat) row.findViewById(
                            R.id.setting_toggle_sc);
                    ruleSwitch.setVisibility(View.GONE);

                    ((TextView) row.findViewById(R.id.setting_value)).
                            setText(setting.value());
                    break;
                case DRIVING:
                    Spinner drivingSpinner = (Spinner) row.findViewById(
                            R.id.setting_spinner_sc);
                    Log.d(TAG, "getDriving ="+trip.getDriving() );

                    spinnerAdapter = ArrayAdapter.createFromResource(getActivityContext,
                            R.array.driving, android.R.layout.simple_spinner_item);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    drivingSpinner.setAdapter(spinnerAdapter);

                    if(trip.hasDriving()){
                        switch (trip.getDriving()){ // display mode spinner value from shared preference
                            case "Not Specified":
                                drivingSpinner.setSelection(0);
                                break;
                            case "Safe driving":
                                drivingSpinner.setSelection(1);
                                break;
                            case "Rash driving":
                                drivingSpinner.setSelection(2);
                                break;
                        }
                    }

                    drivingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                            switch (position){ //switch quality spinner position
                                case 0:
                                    trip.setDriving("Not Specified");
                                    Log.d(TAG, "setDriving="+trip.getDriving() );
                                    break;
                                case 1:
                                    trip.setDriving("Safe driving");
                                    Log.d(TAG, "setDriving="+trip.getDriving() );
                                    break;
                                case 2:
                                    trip.setDriving("Rash driving");
                                    Log.d(TAG, "setDriving="+trip.getDriving() );
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
}
