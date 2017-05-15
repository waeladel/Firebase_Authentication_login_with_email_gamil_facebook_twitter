package com.getin.car.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.getin.car.R;
import com.getin.car.authentication.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static com.getin.car.activities.MainActivity.mAuth;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private static String TAG = RegisterFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private EditText EmailField;
    private EditText PasswordField;
    private Button RegisterButton;
    private String mEmail;
    private String mPassword;
    private ProgressDialog mProgress;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    //  Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View fragView = inflater.inflate(R.layout.fragment_register, container, false);
        EmailField  = (EditText)fragView.findViewById(R.id.create_email_address_editText);
        EmailField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "Editable email= "+ editable.toString());
                String EditableEmail = editable.toString().trim();
                if(TextUtils.isEmpty(EditableEmail)){
                    EmailField.setError(getActivity().getString(R.string.required));
                }else if(!TextUtils.isEmpty(EditableEmail)&& !FirebaseUtils.isValidEmail(EditableEmail)){
                    EmailField.setError(getActivity().getString(R.string.email_is_not_valid));
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

        PasswordField = (EditText)fragView.findViewById(R.id.create_password_editText);
        PasswordField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "Editable password= "+ editable.toString());
                String EditablePassword = editable.toString().trim();
                if(TextUtils.isEmpty(EditablePassword)){
                    PasswordField.setError(getActivity().getString(R.string.required));
                }else if(!TextUtils.isEmpty(EditablePassword)&& !FirebaseUtils.isValidPassword(EditablePassword)){
                    PasswordField.setError(getActivity().getString(R.string.password_must_be_six));
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

        RegisterButton = (Button)fragView.findViewById(R.id.register_btn);
        mProgress = new ProgressDialog(this.getActivity());

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onButtonPressed("RegisterClicked");
                Log.d(TAG, "RegisterButton clicked ");
                RegisterWithEmail();
            }
        });

        return fragView;
        //return inflater.inflate(R.layout.fragment_register, container, false);
    }

    //  Rename method, update argument and hook method into UI event
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mProgress.dismiss();
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

    private void RegisterWithEmail() {
        mEmail = EmailField.getText().toString().trim();
        mPassword = PasswordField.getText().toString().trim();
        if(FirebaseUtils.isValidEmail(mEmail) && FirebaseUtils.isValidPassword(mPassword)){
            Log.d(TAG, "Both are not empty");

            mProgress.setMessage(this.getActivity().getString(R.string.signing_up_progress));
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            mProgress.dismiss();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(getActivity(), R.string.register_failed,
                                        Toast.LENGTH_LONG).show();
                            }else{
                                Log.d(TAG, "RegisterWithEmail:succeeded");
                            }
                        }
                    });
        }else{
            Log.d(TAG, "Both are empty");
            Toast.makeText(getActivity(), R.string.empty_email_password,
                    Toast.LENGTH_LONG).show();
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String FragmentName);
    }
}
