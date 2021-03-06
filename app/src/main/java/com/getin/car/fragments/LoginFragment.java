package com.getin.car.fragments;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.getin.car.activities.MainActivity;
import com.getin.car.authentication.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private static String TAG = LoginFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private EditText EmailField;
    private EditText PasswordField;
    private Button LoginButton;
    private Button RegisterButton;
    private String mEmail;
    private String mPassword;

    private ProgressDialog mProgress;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View fragView = inflater.inflate(R.layout.fragment_login, container, false);
        EmailField  = (EditText)fragView.findViewById(R.id.email_address_editText);
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


       /* PasswordField = (EditText)fragView.findViewById(R.id.password_editText);
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

        LoginButton = (Button)fragView.findViewById(R.id.Login_btn);
        RegisterButton = (Button)fragView.findViewById(R.id.create_account_btn);
*/
        mProgress = new ProgressDialog(this.getActivity());

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onButtonPressed("LoginClicked");
                SignInWithEmail();
                Log.d(TAG, "LoginButton clicked ");
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed("RegisterClicked");
                Log.d(TAG, "RegisterButton clicked ");
            }
        });

        return fragView;
    }

    // activate the interface and send a message to activity
    public void onButtonPressed(String FragmentName) {
        if (mListener != null) {
            mListener.onFragmentInteraction(FragmentName);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // make sure that the activity created the implement fist
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
        //getActivity().setTitle(getString(R.string.Login));
        //getActivity().setTitle(getString(R.string.drawer_title_settings).toUpperCase());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    // an interface to communicate with activity
    public interface OnFragmentInteractionListener {
        //method that activity can receive
        void onFragmentInteraction(String FragmentName);
    }

    private void SignInWithEmail() {
        mEmail = EmailField.getText().toString().trim();
        mPassword = PasswordField.getText().toString().trim();

        if(FirebaseUtils.isValidEmail(mEmail) && FirebaseUtils.isValidPassword(mPassword)){
            Log.d(TAG, "Both are not empty");
            mProgress.setMessage(this.getActivity().getString(R.string.signing_in_progress));
            mProgress.show();

            MainActivity.mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            mProgress.hide();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(getActivity(), R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d(TAG, "signInWithEmail:succeeded");
                                //onButtonPressed("LoginClicked");
                            }
                        }
                    });

        }else{
            Log.d(TAG, "Both are empty");
            Toast.makeText(getActivity(), R.string.empty_email_password,
                    Toast.LENGTH_SHORT).show();
        }
    }


}