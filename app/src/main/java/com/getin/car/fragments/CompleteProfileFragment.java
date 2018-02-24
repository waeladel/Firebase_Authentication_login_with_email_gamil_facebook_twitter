package com.getin.car.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getin.car.R;
import com.getin.car.authentication.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompleteProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompleteProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompleteProfileFragment extends Fragment {

    private static String TAG = CompleteProfileFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_USERID = "userId";
    private static final String ARG_PARAM_DISPLAYNAME = "displayName";
    private static final String ARG_PARAM_EMAIL = "email";
    private static final String ARG_PARAM_PHOTOURL = "photoUrl";
    private static final String ARG_PARAM_ISVERIFIED = "isEmailVerified";


    // TODO: Rename and change types of parameters
    private String mParamUserId ;
    private String mParamDisplayName;
    private String mParamEmail;
    private Uri mParamPhotoUrl;
    private Boolean mParamIsEmailVerified;

    private OnFragmentInteractionListener mListener;

    private EditText mNameField;
    private EditText mEmailField;
    private Button mSubmitButton;
    private ImageView mProfileImageButton;
    private ImageButton mCameraSelectButton;
    private ImageButton mGallerySelectButton;


    private static Uri sPhotoResultUri;

    private static String sname;
    private static String sEmail;

    private static final int SELECT_PICTURE = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;

    /*//initialize the FirebaseAuth instance
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;*/

    //initialize the Firebase storage reference
    private StorageReference mStorageRef;

    //initialize the Firebase Database
    private FirebaseDatabase mDatabase;

    //initialize the Firebase UsersReference
    private DatabaseReference mUsersRef;

    /*private String mName;
    private String mEmail;
    private Uri mPhotoUrl;*/

    private ProgressDialog mProgress;

    public CompleteProfileFragment() {
        // Required empty public constructor
    }

    public static CompleteProfileFragment newInstance(String userId, String displayName, String email, Uri photoUrl, Boolean isEmailVerified) {
        CompleteProfileFragment fragment = new CompleteProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_USERID, userId);
        args.putString(ARG_PARAM_DISPLAYNAME, displayName);
        args.putString(ARG_PARAM_EMAIL, email);
        if (photoUrl != null){
            args.putString(ARG_PARAM_PHOTOURL, photoUrl.toString());
        }
        args.putBoolean(ARG_PARAM_ISVERIFIED, isEmailVerified);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamUserId = getArguments().getString(ARG_PARAM_USERID);
            mParamDisplayName = getArguments().getString(ARG_PARAM_DISPLAYNAME);
            mParamEmail = getArguments().getString(ARG_PARAM_EMAIL);
            if (getArguments().getString(ARG_PARAM_PHOTOURL) != null){
                mParamPhotoUrl = Uri.parse(getArguments().getString(ARG_PARAM_PHOTOURL));
            }
            mParamIsEmailVerified = getArguments().getBoolean(ARG_PARAM_ISVERIFIED);

            Log.d(TAG, "mParamUserId= "+ mParamUserId);
            Log.d(TAG, "mParamDisplayName= "+ mParamDisplayName);
            Log.d(TAG, "mParamEmail= "+ mParamEmail);
            Log.d(TAG, "mParamPhotoUrl= "+ mParamPhotoUrl);
            Log.d(TAG, "mParamIsEmailVerified= "+ mParamIsEmailVerified);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_complete_profile, container, false);

        mNameField = (EditText)fragView.findViewById(R.id.edit_name_editText);
        if(mParamDisplayName != null){
            mNameField.setText(mParamDisplayName);
        }
        mNameField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "Editable Name= "+ editable.toString());
                String EditableName = editable.toString().trim();
                if(TextUtils.isEmpty(EditableName)){
                    mNameField.setError(getActivity().getString(R.string.required));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });

        mEmailField = (EditText)fragView.findViewById(R.id.edit_email_editText);
        if(mParamEmail != null){
            mEmailField.setText(mParamEmail);
        }

        mEmailField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "Editable email= "+ editable.toString());
                String EditableEmail = editable.toString().trim();
                if(TextUtils.isEmpty(EditableEmail)){
                    mEmailField.setError(getActivity().getString(R.string.required));
                }else if(!TextUtils.isEmpty(EditableEmail)&& !FirebaseUtils.isValidEmail(EditableEmail)){
                    mEmailField.setError(getActivity().getString(R.string.email_is_not_valid));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });

        mSubmitButton = (Button)fragView.findViewById(R.id.submit_profile_btn);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onButtonPressed("submitProfileClicked");
                Log.d(TAG, "mSubmitButton clicked ");
                submitProfile();
            }
        });

        mProfileImageButton = (ImageView)fragView.findViewById(R.id.profile_image_btn);
        mGallerySelectButton = (ImageButton)fragView.findViewById(R.id.select_image_btn);

        Log.d(TAG, "sPhotoResultUri = " +sPhotoResultUri);

        if (mParamPhotoUrl != null && sPhotoResultUri == null){
            Glide.with(this).load(mParamPhotoUrl).into(mProfileImageButton);
            Log.d(TAG, "mProfileImageButton mParamPhotoUrl= " +mParamPhotoUrl);
        }
        else if( mParamPhotoUrl == null && sPhotoResultUri != null){
            mProfileImageButton.setImageURI(sPhotoResultUri);
            Log.d(TAG, "mProfileImageButton sPhotoResultUri= " +sPhotoResultUri);
        }

        mGallerySelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "mProfileImageButton clicked ");
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, SELECT_PICTURE);
            }
        });

        mCameraSelectButton = (ImageButton)fragView.findViewById(R.id.take_image_btn);

        mCameraSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "mCameraSelectButton clicked ");
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });

        // Obtain the FirebaseStorage instance.
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Obtain the FirebaseDatabase instance.
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference().child("users");

        mProgress = new ProgressDialog(this.getActivity());

        /*mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getDisplayName:" + user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getEmail():" + user.getEmail());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getPhotoUrl():" + user.getPhotoUrl());
                    Log.d(TAG, "onAuthStateChanged:signed_in_emailVerified?:" + user.isEmailVerified());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };*/

        return fragView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String fragmentName) {
        if (mListener != null) {
            mListener.onFragmentInteraction(fragmentName);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        /*if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode ="+ requestCode);

        switch (requestCode){
            case SELECT_PICTURE:
                Log.d(TAG, "SELECT_PICTURE requestCode="+ requestCode);
                Log.d(TAG, "SELECT_PICTURE resultCode ="+ resultCode);
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    CropImage.activity(imageUri)
                            //.setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            //.setMaxCropResultSize(600, 600)
                            .setMinCropResultSize(300,300)
                            .setRequestedSize(300,300) //resize
                            .start(getContext(), this);
                } else {
                    //Exception error = result.getError();
                    Toast.makeText(getActivity(), R.string.error,
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case REQUEST_IMAGE_CAPTURE:
                Log.d(TAG, "SELECT_PICTURE requestCode="+ requestCode);
                Log.d(TAG, "SELECT_PICTURE resultCode ="+ resultCode);
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    CropImage.activity(imageUri)
                            //.setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            //.setMaxCropResultSize(600, 600)
                            .setMinCropResultSize(300,300)
                            .setRequestedSize(300,300) //resize
                            .start(getContext(), this);
                } else {
                    //Exception error = result.getError();
                    Toast.makeText(getActivity(), R.string.error,
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                Log.d(TAG, "CROP_PICTURE ="+ requestCode);
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    sPhotoResultUri = result.getUri();
                    mProfileImageButton.setImageURI(sPhotoResultUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(getActivity(), error.toString(),
                            Toast.LENGTH_LONG).show();
                }


                break;
            default: //do twitter again just in case
                break;
        }
    }

    private void submitProfile() {
        sname = mNameField.getText().toString().trim();
        sEmail = mEmailField.getText().toString().trim();

        Log.d(TAG, "CROP_PICTURE_sPhotoResultUri ="+ sPhotoResultUri);

        if(FirebaseUtils.isValidEmail(sEmail) && FirebaseUtils.isValidName(sname)){
            mProgress.setMessage(this.getActivity().getString(R.string.submitting_in_progress));
            mProgress.show();

            mUsersRef.child(mParamUserId).child("name").setValue(sname);
            mUsersRef.child(mParamUserId).child("email").setValue(sEmail);
            if(sPhotoResultUri != null){
                uploadAvatar();
            }else{
                mUsersRef.child(mParamUserId).child("avatar").setValue("https://firebasestorage.googleapis.com/v0/b/get-in-3dac6.appspot.com/o/images%2Favatars%2Fdefult_avatar.png?alt=media&token=fba62476-b1ec-4333-9409-b29f671ff241");
            }


        }else{
            mNameField.setError(getActivity().getString(R.string.required));
            mEmailField.setError(getActivity().getString(R.string.email_is_not_valid));
            Log.d(TAG, "Both are empty");
            Toast.makeText(getActivity(), R.string.empty_email_name,
                    Toast.LENGTH_LONG).show();
        }

    }

    private void uploadAvatar() {

        StorageReference avatarRef = mStorageRef.child("images")
                .child("avatars")
                .child(mParamUserId)
                //.child(sPhotoResultUri.getLastPathSegment());
                .child(sname);

        avatarRef.putFile(sPhotoResultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "uploadFromUri:onSuccess");

                        // Get a URL to the uploaded content
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if(downloadUrl != null){
                            mUsersRef.child(mParamUserId).child("avatar").setValue(downloadUrl.toString());
                        }
                        mProgress.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.w(TAG, "uploadFromUri:onFailure", exception);
                        mProgress.dismiss();
                    }
                });
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
        // TODO: Update argument type and name
        void onFragmentInteraction(String FragmentName);
    }
}
