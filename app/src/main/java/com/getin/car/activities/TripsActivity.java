package com.getin.car.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.getin.car.R;
import com.getin.car.authentication.Trip;
import com.getin.car.authentication.TripsListAdapter;
import com.getin.car.fragments.CompleteProfileFragment;
import com.getin.car.fragments.EditProfileFragment;
import com.getin.car.fragments.PostFragment;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static com.google.firebase.firestore.DocumentChange.Type.ADDED;
//import com.twitter.sdk.android.Twitter;

public class TripsActivity extends BaseActivity implements CompleteProfileFragment.OnFragmentInteractionListener
        , EditProfileFragment.OnFragmentInteractionListener
        , PostFragment.OnFragmentInteractionListener{

    private final static String TAG = TripsActivity.class.getSimpleName();
    private static final int REQUEST_INVITE = 13;

    //initialize the FirebaseAuth instance
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;

    public String currentUserId;
    public String currentUserName;
    public String currentUserEmail;
    public Uri currentUserPhoto;
    public Boolean currentUserVerified;
    public boolean userExist;

    private RecyclerView mTripsRecycler;
    private List<Trip> mTripsArrayList;
    private TripsListAdapter tripsListAdapter;

    //initialize the Firebase Database
    private FirebaseFirestore db;
    private CollectionReference tripsCollRef;

    private Trip tripSnapshot;

    private static final int QUERY_LIMIT = 3;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true; // to display other users's posts on top because it's recent

    private SharedPreferences mTripsPreference;
    private SharedPreferences.Editor editor;
    private Spinner mSortSpinner;

    private CardView mSeeNewCardView;
    private TextView mSeeNewText;
    private ListenerRegistration registration;
    private List<Integer> mNewPostsCounts;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "TripsActivity onCreate");
        Log.d(TAG, "savedInstanceState:" + savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // read and write Shared Preference key values form Preference file
        mTripsPreference = PreferenceManager.getDefaultSharedPreferences(this);
        //save Preferences
        editor = mTripsPreference.edit();


        mSortSpinner = findViewById(R.id.sort_spinner);
        mSeeNewCardView = findViewById(R.id.seeNew_cardView);
        mSeeNewText = findViewById(R.id.see_more_txt);

        mNewPostsCounts = new ArrayList<Integer>();// to add the new posts count to list


        // display new posts when click on see new CardView
        mSeeNewCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeeNewCardView.setVisibility(View.GONE);
                // reset the new posts counter to zero
                if (mNewPostsCounts.size() > 0) {
                    /*for (int i = 0; i < mNewPostsCounts.size(); i++) {
                        mNewPostsCounts.remove(i);
                    }*/
                    mNewPostsCounts.clear();
                }
                Log.d(TAG, "mSeeNewCardView clicked: mNewPostsCounts.size+ "+mNewPostsCounts.size());

                tripsListAdapter.notifyDataSetChanged();

                // move to the top
                LinearLayoutManager layoutManager = (LinearLayoutManager) mTripsRecycler
                        .getLayoutManager();
                layoutManager.scrollToPositionWithOffset(0, 0);
            }
        });

        // listner for sorting spinner ///
        switch (mTripsPreference.getInt("sortingID", 0)){ // display sorting option selected from shared preference
            case 0:
                mSortSpinner.setSelection(0);
                Log.d(TAG, "display 0 option on sorting spinner");
                break;
            case 1:
                mSortSpinner.setSelection(1);
                Log.d(TAG, "display 1 option on sorting spinner");
                break;
            case 2:
                mSortSpinner.setSelection(2);
                Log.d(TAG, "display 2 option on sorting spinner");
                break;
            case 3:
                mSortSpinner.setSelection(3);
                Log.d(TAG, "display 3 option on sorting spinner");
                break;
            case 4:
                mSortSpinner.setSelection(4);
                Log.d(TAG, "display 4 option on sorting spinner");
                break;
            case 5:
                mSortSpinner.setSelection(5);
                Log.d(TAG, "display 5 option on sorting spinner");
                break;
        }

        // Listener for item selected on the spinner ////
        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position){ //switch quality spinner position
                    case 0:
                        editor.putInt("sortingID", 0);
                        editor.putString("orderBy", "date");
                        editor.putString("direction", "ascending");
                        editor.apply();
                        Log.d(TAG, "spinner Listener: 0 selected");
                        isFirstPageFirstLoad = true;
                        setFirstQuery("date" , Query.Direction.ASCENDING);
                        break;
                    case 1:
                        editor.putInt("sortingID", 1);
                        editor.putString("orderBy", "date");
                        editor.putString("direction", "descending");
                        editor.apply();
                        Log.d(TAG, "spinner Listener: 1 selected");
                        isFirstPageFirstLoad = true;
                        setFirstQuery("date" , Query.Direction.DESCENDING);
                        break;
                    case 2:
                        editor.putInt("sortingID", 2);
                        editor.putString("orderBy", "created");
                        editor.putString("direction", "descending");
                        editor.apply();
                        Log.d(TAG, "spinner Listener: 2 selected");
                        isFirstPageFirstLoad = true;
                        setFirstQuery("created" , Query.Direction.DESCENDING);
                        break;
                    case 3:
                        editor.putInt("sortingID", 3);
                        editor.putString("orderBy", "created");
                        editor.putString("direction", "ascending");
                        editor.apply();
                        Log.d(TAG, "spinner Listener: 3 selected");
                        isFirstPageFirstLoad = true;
                        setFirstQuery("created" , Query.Direction.ASCENDING);
                        break;
                    case 4:
                        editor.putInt("sortingID", 4);
                        editor.putString("orderBy", "cost");
                        editor.putString("direction", "ascending");
                        editor.apply();
                        Log.d(TAG, "spinner Listener: 4 selected");
                        isFirstPageFirstLoad = true;
                        setFirstQuery("cost" , Query.Direction.ASCENDING);
                        break;
                    case 5:
                        editor.putInt("sortingID", 5);
                        editor.putString("orderBy", "cost");
                        editor.putString("direction", "descending");
                        editor.apply();
                        Log.d(TAG, "spinner Listener: 5 selected");
                        isFirstPageFirstLoad = true;
                        setFirstQuery("cost" , Query.Direction.DESCENDING);
                        break;
                    default:
                        editor.putInt("sortingID", 0);
                        editor.putString("orderBy", "date");
                        editor.putString("direction", "descending");
                        editor.apply();
                        Log.d(TAG, "spinner Listener: 0 default");
                        isFirstPageFirstLoad = true;
                        setFirstQuery("date" , Query.Direction.DESCENDING);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Log.d(TAG, "spinner NothingSelected");
                editor.putInt("sortingID", 0);
                editor.putString("orderBy", "date");
                editor.putString("direction", "descending");
                editor.apply();
                isFirstPageFirstLoad = true;
                setFirstQuery("date" , Query.Direction.DESCENDING);
            }

        });


            // prepare the Adapter
        mTripsArrayList = new ArrayList<>();
        tripsListAdapter = new TripsListAdapter(getApplicationContext(),mTripsArrayList);

        // Initiate the RecyclerView
        mTripsRecycler = (RecyclerView) findViewById(R.id.trips_list);
        mTripsRecycler.setHasFixedSize(true);
        mTripsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mTripsRecycler.setAdapter(tripsListAdapter);

        // check if recycler view reached it's bottom to load more posts ////
        mTripsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if(reachedBottom){
                    Log.d(TAG, "reached Bottom" );
                    String direction = mTripsPreference.getString("direction", "descending");
                    String orderBy = mTripsPreference.getString("orderBy", "date");
                    loadMorePost(orderBy, getSortDirection(direction));
                }

            }
        });

        // Obtain the FirebaseDatabase instance.
        db =  FirebaseFirestore.getInstance();

        tripsCollRef = db.collection("trips");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*if(fragmentManager.findFragmentByTag("PostFrag") == null){ // to create only one instant of postFrag
                    PostFragment PostFrag  = new PostFragment();
                    //fragmentManager.beginTransaction().replace(R.id.content_main, mRegisterFragment,"mRegisterFragment").commit();
                    FragmentTransaction PostTransaction =fragmentManager.beginTransaction();
                    PostTransaction.add(R.id.content_profile, PostFrag,"PostFrag");
                    PostTransaction.addToBackStack("PostFrag");
                    PostTransaction.commit();
                }*/
                Intent mIntent = new Intent(TripsActivity.this, PostActivity.class);
                //mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(mIntent);
                //finish();

            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    currentUserId = user.getUid();
                    currentUserName = user.getDisplayName();
                    currentUserEmail = user.getEmail();
                    currentUserPhoto = user.getPhotoUrl();
                    currentUserVerified = user.isEmailVerified();

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getDisplayName:" + user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getEmail():" + user.getEmail());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getPhotoUrl():" + user.getPhotoUrl());
                    Log.d(TAG, "onAuthStateChanged:signed_in_emailVerified?:" + user.isEmailVerified());

                    isUserExist(currentUserId); // if not start complete profile

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    //  Removing Fragments if Exists and their back stacks
                    Intent mIntent = new Intent(TripsActivity.this, MainActivity.class);
                    //mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(mIntent);
                    finish();
                }
                // ...
            }
        };
    }// end of on create

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Log.d(TAG, "MenuItem = 0");
                break;
            case R.id.action_edit_profile:
                Log.d(TAG, "MenuItem = 1");
                if(fragmentManager.findFragmentByTag("editProfileFrag") == null) { // to create only one instant of postFrag
                    editProfileFragment  = EditProfileFragment.newInstance(currentUserId);//new EditProfileFrag();
                    //fragmentManager.beginTransaction().replace(R.id.content_main, mRegisterFragment,"mRegisterFragment").commit();
                    FragmentTransaction editTransaction =fragmentManager.beginTransaction();
                    editTransaction.add(R.id.content_profile, editProfileFragment,"editProfileFrag");
                    editTransaction.addToBackStack("editProfileFrag");
                    editTransaction.commit();
                }

                break;
            case R.id.action_menu_invite:
                Log.d(TAG, "MenuItem = 2  INVITE clicked ");
                onInviteClicked();
                break;
            case R.id.action_log_out:

                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "MenuItem = 3");
                            }
                        });
                /*FirebaseAuth.getInstance().signOut(); // logout firebase user
                LoginManager.getInstance().logOut();// logout from facebook too
                Twitter.logOut(); // logout from twitter too
                // Google sign out
               Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                //updateUI(null);
                                Log.d(TAG, "Google sign out succeeded");
                            }
                        });*/
                break;
        }

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                Toast.makeText(TripsActivity.this, getString(R.string.invitation_failed),
                        Toast.LENGTH_LONG).show();
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }
    // [END on_activity_result]

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registration.remove();
        Log.d(TAG, "onDestroy()");

    }

    @Override
    public void onFragmentInteraction(String FragmentName) {// listens to login fragments buttons
        Log.d(TAG, "FragmentName = "+ FragmentName);
        Log.d(TAG, "FragmentName = "+  fragmentManager.findFragmentByTag("completeProfileFrag"));

        switch (FragmentName){
            /*case "EditProfile":
                //Replace current fragment with Register Fragment
                mRegisterFragment  = new RegisterFragment();
                //fragmentManager.beginTransaction().replace(R.id.content_main, mRegisterFragment,"mRegisterFragment").commit();
                FragmentTransaction RegisterTransaction =fragmentManager.beginTransaction();
                RegisterTransaction.replace(R.id.content_main, mRegisterFragment,"mRegisterFragment");
                //RegisterTransaction.addToBackStack("RegisterClicked");
                RegisterTransaction.commit();
                break;*/
            /*case "LoginClicked":
                //Replace current fragment with Register Fragment
                LoginFragment mLoginFragment = (LoginFragment) fragmentManager.findFragmentByTag("mLoginFragment");
                FragmentTransaction RemoveLoginTransaction =fragmentManager.beginTransaction();
                //RemoveLoginTransaction.addToBackStack(null);
                if (mLoginFragment != null){
                    RemoveLoginTransaction.remove(mLoginFragment);
                    RemoveLoginTransaction.commit();
                }
                break;*/
            case "completeProfile":
                //fragmentManager.popBackStack();
                CompleteProfileFragment CompleteProfile = (CompleteProfileFragment) fragmentManager.findFragmentByTag("completeProfileFrag");
                FragmentTransaction RemoveTransaction =fragmentManager.beginTransaction();
                Log.d(TAG, "FragmentName CompleteProfile= "+ CompleteProfile);
                //RemoveLoginTransaction.addToBackStack(null);
                if (CompleteProfile != null){
                    RemoveTransaction.remove(CompleteProfile);
                    RemoveTransaction.commit();
                }

                break;
            case "submitProfile":
                fragmentManager.popBackStack("editProfileFrag",POP_BACK_STACK_INCLUSIVE);
                break;
            default:
                break;
        }
    }

    private void  setFirstQuery(String orderBy , Query.Direction direction){
        // Listen to database changes ////
        Log.d(TAG, "FirstQuery orderBy="+ orderBy+ "direction"+direction);

        Query firstQuery = tripsCollRef.orderBy( orderBy, direction).limit(QUERY_LIMIT);
        registration = firstQuery.addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                try {
                    if (!documentSnapshots.isEmpty()) {
                        Log.d(TAG, "documentSnapshots.size="+ documentSnapshots.size());

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            mTripsArrayList.clear();
                        }

                        for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "firstQuery snapshots added: " + dc.getDocument().getData());

                                    String tripDocumentId = dc.getDocument().getId();

                                    tripSnapshot = dc.getDocument().toObject(Trip.class).withId(tripDocumentId);
                                    //Log.d(TAG, "tripSnapshot: " + dc.getDocument().getData());

                                    if (isFirstPageFirstLoad) {
                                        Log.d(TAG, "loaded for the first time ");
                                        mTripsArrayList.add(tripSnapshot);
                                        tripsListAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.d(TAG, "loaded for the second time ");
                                        mNewPostsCounts.add(1); // add new post to the counter

                                        if (mNewPostsCounts.size() <= 1) {// for singular
                                            mSeeNewText.setText(getString(R.string.see_new_post, mNewPostsCounts.size()));
                                        }else{  // for plural
                                            mSeeNewText.setText(getString(R.string.see_new_posts, mNewPostsCounts.size()));
                                        }
                                        mSeeNewCardView.setVisibility(View.VISIBLE);
                                        mTripsArrayList.add(0, tripSnapshot);
                                        //tripsListAdapter.notifyDataSetChanged();
                                        Log.d(TAG, "mNewPostsCounts.size= " +mNewPostsCounts.size());
                                    }

                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                        isFirstPageFirstLoad = false;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });

    }


    private void isUserExist(final String userId){
        // Read from the database just once
        Log.d(TAG, "userId Value is: " + userId);
        DocumentReference UserDoc = db.collection("users").document(userId);
        UserDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "user exist DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such user");
                        completeProfile(currentUserId, currentUserName, currentUserEmail, currentUserPhoto, currentUserVerified);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        /*UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value.
                //String value = dataSnapshot.getValue(String.class);
                //Log.d(TAG, "Value is: " + value);
                if(dataSnapshot.hasChild(userId)){
                    Log.d(TAG, "user exist");
                    //Log.d(TAG, "Value is: " + value);
                    userExist = true;
                }else{
                    Log.d(TAG, "User dose not exist");
                    userExist = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
    }

    private void completeProfile( String userId, String displayName, String email, Uri photoUrl, Boolean isEmailVerified){
        //Replace current fragment with Register Fragment
        Log.d(TAG, "completeProfileFrag = "+completeProfileFrag+ fragmentManager.findFragmentByTag("completeProfileFrag"));
        if(fragmentManager.findFragmentByTag("completeProfileFrag") == null){
            completeProfileFrag  = completeProfileFrag.newInstance(userId, displayName, email, photoUrl,isEmailVerified);//new CompleteProfileFragment();
            //fragmentManager.beginTransaction().replace(R.id.content_main, mRegisterFragment,"mRegisterFragment").commit();
            FragmentTransaction completeTransaction =fragmentManager.beginTransaction();
            completeTransaction.add(R.id.content_profile, completeProfileFrag,"completeProfileFrag");
            //completeTransaction.addToBackStack("completeProfileClicked");
            completeTransaction.commit();
        }

    }

    public Query.Direction getSortDirection(String direction) {
        if(direction.equalsIgnoreCase("ascending")){
            return Query.Direction.ASCENDING;
        }else if(direction.equalsIgnoreCase("descending ")){
            return Query.Direction.DESCENDING;
        }

        return Query.Direction.DESCENDING;
    }

    public void loadMorePost(String orderBy, Query.Direction direction) {

        Log.d(TAG, "FirstQuery orderBy="+ orderBy+ "direction"+direction);
        // Listen to database changes ////
        Query nextQuery = tripsCollRef
                .orderBy(orderBy, direction)
                .startAfter(lastVisible)
                .limit(QUERY_LIMIT);

        registration = nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                try {
                    if (!documentSnapshots.isEmpty()) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "nextQuery snapshots added: " + dc.getDocument().getData());

                                    String tripDocumentId = dc.getDocument().getId(); // to pass the doc id to adapter

                                    tripSnapshot = dc.getDocument().toObject(Trip.class).withId(tripDocumentId);
                                    //Log.d(TAG, "tripSnapshot: " + dc.getDocument().getData());

                                    mTripsArrayList.add(tripSnapshot);

                                    tripsListAdapter.notifyDataSetChanged();
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }else{
                        Toast.makeText(TripsActivity.this, getString(R.string.no_more_trips),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

}
