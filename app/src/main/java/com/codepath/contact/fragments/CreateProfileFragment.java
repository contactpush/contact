package com.codepath.contact.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.contact.R;
import com.codepath.contact.models.ContactInfo;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
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

    private ContactInfo currentUser;


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
        fetchCurrentUser();
    }

    private void save(){
        currentUser.setFirstName(etFirstName.getText().toString());
        currentUser.setMiddleName(etMiddleName.getText().toString());
        currentUser.setLastName(etLastName.getText().toString());
        currentUser.setCompany(etCompany.getText().toString());

        if (etPhone.getText().toString() != null
                && etPhone.getText().toString().trim().length() > 0){
            currentUser.setPhoneType(spPhoneType.getSelectedItem().toString());
            currentUser.setPhone(etPhone.getText().toString());
        }

        if (etEmail.getText().toString() != null
                && etEmail.getText().toString().trim().length() > 0){
            currentUser.setEmailType(spEmailType.getSelectedItem().toString());
            currentUser.setEmail(etEmail.getText().toString());
        }

        if (etAddress.getText().toString() != null
                && etAddress.getText().toString().trim().length() > 0){
            currentUser.setAddressType(spAddressType.getSelectedItem().toString());
            currentUser.setAddress(etAddress.getText().toString());
        }

        if (etSocialProfile.getText().toString() != null
                && etSocialProfile.getText().toString().trim().length() > 0){
            currentUser.setSocialProfileType(spSocialProfileType.getSelectedItem().toString());
            currentUser.setSocialProfile(etSocialProfile.getText().toString());
        }

        ParseUser user = ParseUser.getCurrentUser();
        user.put(ContactInfo.CONTACT_INFO_TABLE_NAME, currentUser);

        user.saveInBackground(new SaveCallback() {
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

    private void fetchCurrentUser(){
        ParseUser user = ParseUser.getCurrentUser();
        currentUser = (ContactInfo) user.get(ContactInfo.CONTACT_INFO_TABLE_NAME);
        currentUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null){
                    setCurrentValues();
                } else {
                    Log.e(TAG, e.getMessage());
                    currentUser = new ContactInfo();
                }
            }
        });
    }

    private void setCurrentValues(){
        // TODO set ivProfileImage...
        etFirstName.setText(currentUser.getFirstName());
        etMiddleName.setText(currentUser.getMiddleName());
        etLastName.setText(currentUser.getLastName());
        etCompany.setText(currentUser.getCompany());

        if (currentUser.getPhone() != null
                && currentUser.getPhone().trim().length() > 0){
            spPhoneType.setSelection(((ArrayAdapter)spPhoneType.getAdapter()).getPosition(currentUser.getPhoneType()));
            etPhone.setText(currentUser.getPhone());
        }

        if (currentUser.getEmail() != null
                && currentUser.getEmail().trim().length() > 0){
            spEmailType.setSelection(((ArrayAdapter)spEmailType.getAdapter()).getPosition(currentUser.getEmailType()));
            etEmail.setText(currentUser.getEmail());
        }

        if (currentUser.getAddress() != null
                && currentUser.getAddress().trim().length() > 0){
            spAddressType.setSelection(((ArrayAdapter)spAddressType.getAdapter()).getPosition(currentUser.getAddress()));
            etAddress.setText(currentUser.getAddress());
        }

        if (currentUser.getSocialProfile() != null
                && currentUser.getSocialProfile().trim().length() > 0){
            spSocialProfileType.setSelection(((ArrayAdapter)spSocialProfileType.getAdapter())
                    .getPosition(currentUser.getSocialProfile()));
            etSocialProfile.setText(currentUser.getSocialProfile());
        }
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
