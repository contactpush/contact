package com.codepath.contact.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.contact.R;
import com.codepath.contact.models.ContactInfo;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class CreateProfileFragment extends Fragment {
    private static final String TAG = "CreateProfileFragment";
    //private OnFragmentInteractionListener mListener;
    private Button btDone;
    private ImageView ivProfileImage;
    private EditText etFirstName;
    private EditText etMiddleName;
    private EditText etLastName;
    private EditText etCompany;

    private Spinner spPhoneType;
    private EditText etPhone;

    private Spinner spEmailType;
    private EditText etEmail;

    private Spinner spAddressType;
    private EditText etAddress;

    private Spinner spSocialProfileType;
    private EditText etSocialProfile;


    public static CreateProfileFragment newInstance() {
        CreateProfileFragment fragment = new CreateProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_profile, container, false);
        setUpViews(v);
        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        return v;
    }

    private void setUpViews(View v){
        btDone = (Button) v.findViewById(R.id.btDone);
        ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        etFirstName = (EditText) v.findViewById(R.id.etFirstName);
        etMiddleName = (EditText) v.findViewById(R.id.etMiddleName);
        etLastName = (EditText) v.findViewById(R.id.etLastName);
        etCompany = (EditText) v.findViewById(R.id.etCompany);

        spPhoneType = (Spinner) v.findViewById(R.id.spPhoneType);
        etPhone = (EditText) v.findViewById(R.id.etPhone);

        spEmailType = (Spinner) v.findViewById(R.id.spEmailType);
        etEmail = (EditText) v.findViewById(R.id.etEmail);

        spAddressType = (Spinner) v.findViewById(R.id.spAddressType);
        etAddress = (EditText) v.findViewById(R.id.etAddress);

        spSocialProfileType = (Spinner) v.findViewById(R.id.spSocialProfileType);
        etSocialProfile = (EditText) v.findViewById(R.id.etSocialProfile);
    }

    private void save(){
        ContactInfo c = new ContactInfo();
        c.setFirstName(etFirstName.getText().toString());
        c.setMiddleName(etMiddleName.getText().toString());
        c.setLastName(etLastName.getText().toString());
        c.setCompany(etCompany.getText().toString());

        // TODO figure out how to save spinner's position
        //c.setPhoneType(spPhoneType.get);
        c.setPhone(etPhone.getText().toString());
        c.setEmail(etEmail.getText().toString());
        c.setAddress(etAddress.getText().toString());
        c.setSocialProfile(etSocialProfile.getText().toString());
        c.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Toast.makeText(getActivity(), "Save successful", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Save successful!");
                    getActivity().finish();
                } else {
                    Log.e(TAG, "Save failed! " + e.getMessage());
                    Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        });
    }

   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }*/

}
