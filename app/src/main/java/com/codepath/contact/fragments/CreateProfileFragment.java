package com.codepath.contact.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
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
import com.codepath.contact.tasks.PhotoReader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateProfileFragment extends Fragment {
    private static final String TAG = "CreateProfileFragment";

    private static final int SELECT_PICTURE_REQUEST_CODE = 3237;
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

    private Uri outputFileUri;


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

        if (!currentUser.isDirty()){
            // TODO this doesn't actually work, but it would be cool if it did
            Log.d(TAG, "None of the user's data has changed, so nothing is being saved to Parse.");
            getActivity().finish();
            return;
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
        if (currentUser == null){
            currentUser = new ContactInfo();
            return;
        }
        currentUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null){
                    setCurrentValues();
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private void setCurrentValues(){
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });
        byte[] photo = currentUser.getProfileImage();
        if (photo != null){
            setProfileImage(photo);
        } else {
            Log.d(TAG, "user's photo is null");
        }
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

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }
                if (selectedImageUri != null){
                    PhotoReader reader = new PhotoReader(new PhotoReader.PhotoReadCompletionListener() {
                        @Override
                        public void onPhotoReadCompletion(byte[] photoBytes) {
                            setProfileImage(photoBytes);
                            currentUser.setProfileImage(photoBytes);
                        }
                    });
                    reader.execute(selectedImageUri);
                }
            }
        }
    }

    private void setProfileImage(byte[] photo){
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        ivProfileImage.setImageBitmap(bitmap);
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
