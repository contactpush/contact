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
import android.widget.TextView;
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

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    public final static int PICK_PHOTO_CODE = 1046;

    public String PHOTO_FILE_NAME = "photo.jpg";

    public static final String OBJECT_ID = "objectId";
    //private OnFragmentInteractionListener mListener;
    private Button btDone;
    private Button btEdit;

    enum ProfileMode{
        EDIT,
        VIEW
    }

    private ProfileMode profileMode;

    private ImageView ivProfileImage;

    private TextView tvFirstName;
    private EditText etFirstName;

    private TextView tvMiddleName;
    private EditText etMiddleName;

    private TextView tvLastName;
    private EditText etLastName;

    private TextView tvCompany;
    private EditText etCompany;

    private TextView tvPhoneType;
    private Spinner spPhoneType;

    private TextView tvPhone;
    private EditText etPhone;

    private TextView tvEmailType;
    private Spinner spEmailType;

    private TextView tvEmail;
    private EditText etEmail;

    private TextView tvAddressType;
    private Spinner spAddressType;

    private TextView tvAddress;
    private EditText etAddress;

    private TextView tvSocialProfileType;
    private Spinner spSocialProfileType;

    private TextView tvSocialProfile;
    private EditText etSocialProfile;

    private ContactInfo currentUser;

    private Uri outputFileUri;

    private String objectId;


    public static CreateProfileFragment newInstance(String objectId) {
        CreateProfileFragment fragment = new CreateProfileFragment();
        Bundle args = new Bundle();
        args.putString(OBJECT_ID, objectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objectId = getArguments().getString(OBJECT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        long start = System.currentTimeMillis();
        View v = inflater.inflate(R.layout.fragment_create_profile, container, false);
        setUpViews(v);
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        Log.d(TAG, "onCreateView took: " + elapsed);
        return v;
    }

    private void setUpViews(View v){
        ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);

        btDone = (Button) v.findViewById(R.id.btDone);
        btEdit = (Button) v.findViewById(R.id.btEdit);

        tvFirstName = (TextView) v.findViewById(R.id.tvFirstName);
        etFirstName = (EditText) v.findViewById(R.id.etFirstName);

        tvMiddleName = (TextView) v.findViewById(R.id.tvMiddleName);
        etMiddleName = (EditText) v.findViewById(R.id.etMiddleName);

        tvLastName = (TextView) v.findViewById(R.id.tvLastName);
        etLastName = (EditText) v.findViewById(R.id.etLastName);

        tvCompany = (TextView) v.findViewById(R.id.tvCompany);
        etCompany = (EditText) v.findViewById(R.id.etCompany);

        tvPhoneType = (TextView) v.findViewById(R.id.tvPhoneType);
        spPhoneType = (Spinner) v.findViewById(R.id.spPhoneType);

        tvPhone = (TextView) v.findViewById(R.id.tvPhone);
        etPhone = (EditText) v.findViewById(R.id.etPhone);

        tvEmailType = (TextView) v.findViewById(R.id.tvEmailType);
        spEmailType = (Spinner) v.findViewById(R.id.spEmailType);

        tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        etEmail = (EditText) v.findViewById(R.id.etEmail);

        tvAddressType = (TextView) v.findViewById(R.id.tvAddressType);
        spAddressType = (Spinner) v.findViewById(R.id.spAddressType);

        tvAddress = (TextView) v.findViewById(R.id.tvAddress);
        etAddress = (EditText) v.findViewById(R.id.etAddress);

        tvSocialProfileType = (TextView) v.findViewById(R.id.tvSocialProfileType);
        spSocialProfileType = (Spinner) v.findViewById(R.id.spSocialProfileType);

        tvSocialProfile = (TextView) v.findViewById(R.id.tvSocialProfile);
        etSocialProfile = (EditText) v.findViewById(R.id.etSocialProfile);

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                showTextViews();
            }
        });

        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTexts();
            }
        });

        showTextViews();
        fetchUser();
    }

    private void setUpEmailAndPhoneOnClick(){
        tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = tvPhone.getText().toString().trim();
                if (phoneNumber != null && phoneNumber.length() > 0){
                    phoneNumber = "tel:" + phoneNumber;
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(phoneNumber));
                    startActivity(intent);
                }
            }
        });

        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                if (email != null && email.length() > 0){
                    String[] emails = new String[]{email};
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_EMAIL, emails);
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }
            }
        });
    }

    private void showEditTexts(){
        this.profileMode = ProfileMode.EDIT;

        btEdit.setVisibility(View.INVISIBLE);
        btDone.setVisibility(View.VISIBLE);

        etFirstName.setVisibility(View.VISIBLE);
        tvFirstName.setVisibility(View.INVISIBLE);

        tvMiddleName.setVisibility(View.INVISIBLE);
        etMiddleName.setVisibility(View.VISIBLE);

        tvLastName.setVisibility(View.INVISIBLE);
        etLastName.setVisibility(View.VISIBLE);

        tvCompany.setVisibility(View.INVISIBLE);
        etCompany.setVisibility(View.VISIBLE);

        tvPhoneType.setVisibility(View.INVISIBLE);
        spPhoneType.setVisibility(View.VISIBLE);

        tvPhone.setVisibility(View.INVISIBLE);
        etPhone.setVisibility(View.VISIBLE);

        tvEmailType.setVisibility(View.INVISIBLE);
        spEmailType.setVisibility(View.VISIBLE);

        tvEmail.setVisibility(View.INVISIBLE);
        etEmail.setVisibility(View.VISIBLE);

        tvEmail.setVisibility(View.INVISIBLE);
        etEmail.setVisibility(View.VISIBLE);

        tvAddressType.setVisibility(View.INVISIBLE);
        spAddressType.setVisibility(View.VISIBLE);

        tvAddress.setVisibility(View.INVISIBLE);
        etAddress.setVisibility(View.VISIBLE);

        tvSocialProfileType.setVisibility(View.INVISIBLE);
        spSocialProfileType.setVisibility(View.VISIBLE);

        tvSocialProfile.setVisibility(View.INVISIBLE);
        etSocialProfile.setVisibility(View.VISIBLE);
    }

    private void showTextViews(){
        this.profileMode = ProfileMode.VIEW;

        btEdit.setVisibility(View.VISIBLE);
        btDone.setVisibility(View.INVISIBLE);

        etFirstName.setVisibility(View.INVISIBLE);
        tvFirstName.setVisibility(View.VISIBLE);

        tvMiddleName.setVisibility(View.VISIBLE);
        etMiddleName.setVisibility(View.INVISIBLE);

        tvLastName.setVisibility(View.VISIBLE);
        etLastName.setVisibility(View.INVISIBLE);

        tvCompany.setVisibility(View.VISIBLE);
        etCompany.setVisibility(View.INVISIBLE);

        tvPhoneType.setVisibility(View.VISIBLE);
        spPhoneType.setVisibility(View.INVISIBLE);

        tvPhone.setVisibility(View.VISIBLE);
        etPhone.setVisibility(View.INVISIBLE);

        tvEmailType.setVisibility(View.VISIBLE);
        spEmailType.setVisibility(View.INVISIBLE);

        tvEmail.setVisibility(View.VISIBLE);
        etEmail.setVisibility(View.INVISIBLE);

        tvEmail.setVisibility(View.VISIBLE);
        etEmail.setVisibility(View.INVISIBLE);

        tvAddressType.setVisibility(View.VISIBLE);
        spAddressType.setVisibility(View.INVISIBLE);

        tvAddress.setVisibility(View.VISIBLE);
        etAddress.setVisibility(View.INVISIBLE);

        tvSocialProfileType.setVisibility(View.VISIBLE);
        spSocialProfileType.setVisibility(View.INVISIBLE);

        tvSocialProfile.setVisibility(View.VISIBLE);
        etSocialProfile.setVisibility(View.INVISIBLE);
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
                    setCurrentValues();
                } else {
                    Log.e(TAG, "Save failed! " + e.getMessage());
                    Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
                    setCurrentValues();
                }
            }
        });
    }

    private void fetchUser(){
        if (objectId == null){
            fetchCurrentUser();
        } else {
            setUpEmailAndPhoneOnClick();
            ContactInfo.getContactInfo(objectId, new ContactInfo.OnContactReturnedListener() {
                @Override
                public void receiveContact(ContactInfo contactInfo) {
                    btDone.setVisibility(View.INVISIBLE);
                    btEdit.setVisibility(View.INVISIBLE);
                    currentUser = contactInfo;
                    setCurrentValues();
                }
            });
        }
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
                if (e == null) {
                    long start = System.currentTimeMillis();
                    setCurrentValues();
                    long end = System.currentTimeMillis();
                    long elapsed = end - start;
                    Log.d(TAG, "setCurrentValues took: " + elapsed);
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
                if(CreateProfileFragment.this.profileMode == ProfileMode.EDIT){
                    openImageIntent();
                }
            }
        });
        byte[] photo = currentUser.getProfileImage();
        if (photo != null){
            setProfileImage(photo);
        } else {
            Log.d(TAG, "user's photo is null");
        }

        tvFirstName.setText(currentUser.getFirstName());
        etFirstName.setText(currentUser.getFirstName());

        tvMiddleName.setText(currentUser.getMiddleName());
        etMiddleName.setText(currentUser.getMiddleName());

        tvLastName.setText(currentUser.getLastName());
        etLastName.setText(currentUser.getLastName());

        tvCompany.setText(currentUser.getCompany());
        etCompany.setText(currentUser.getCompany());

        if (currentUser.getPhone() != null
                && currentUser.getPhone().trim().length() > 0){
            tvPhoneType.setText(currentUser.getPhoneType());
            spPhoneType.setSelection(((ArrayAdapter)spPhoneType.getAdapter()).getPosition(currentUser.getPhoneType()));

            tvPhone.setText(currentUser.getPhone());
            etPhone.setText(currentUser.getPhone());
        }

        if (currentUser.getEmail() != null
                && currentUser.getEmail().trim().length() > 0){
            tvEmailType.setText(currentUser.getEmailType());
            spEmailType.setSelection(((ArrayAdapter)spEmailType.getAdapter()).getPosition(currentUser.getEmailType()));

            tvEmail.setText(currentUser.getEmail());
            etEmail.setText(currentUser.getEmail());
        }

        if (currentUser.getAddress() != null
                && currentUser.getAddress().trim().length() > 0){
            tvAddressType.setText(currentUser.getAddressType());
            spAddressType.setSelection(((ArrayAdapter)spAddressType.getAdapter()).getPosition(currentUser.getAddressType()));

            tvAddress.setText(currentUser.getAddress());
            etAddress.setText(currentUser.getAddress());
        }

        if (currentUser.getSocialProfile() != null
                && currentUser.getSocialProfile().trim().length() > 0){
            tvSocialProfileType.setText(currentUser.getSocialProfileType());
            spSocialProfileType.setSelection(((ArrayAdapter)spSocialProfileType.getAdapter())
                    .getPosition(currentUser.getSocialProfileType()));

            tvSocialProfile.setText(currentUser.getSocialProfile());
            etSocialProfile.setText(currentUser.getSocialProfile());
        }
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
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

        // photos
        Intent photoPicker = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(photoPicker, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // camera
        if (requestCode == SELECT_PICTURE_REQUEST_CODE && data == null) {
            if (resultCode == getActivity().RESULT_OK) {
                setProfileImage(getPhotoFileUri(PHOTO_FILE_NAME));
            } else { // Result was a failure
                Log.d(TAG, "Picture wasn't taken!");
            }
        }

        // photo
        if (requestCode == SELECT_PICTURE_REQUEST_CODE && data != null) {
            if (resultCode == getActivity().RESULT_OK) {
                setProfileImage(data.getData());
            }
        }
    }

    private void setProfileImage(Uri photoUri){
        if (photoUri != null){
            PhotoReader reader = new PhotoReader(getActivity(), new PhotoReader.PhotoReadCompletionListener() {
                @Override
                public void onPhotoReadCompletion(byte[] photo) {
                    currentUser.setProfileImage(photo);
                    setProfileImage(photo);
                }
            });
            reader.execute(photoUri);
        }
    }

    private void setProfileImage(byte[] photo){
        Log.d(TAG, "photo is " + photo.length + " bytes");
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        ivProfileImage.setImageBitmap(bitmap);
        currentUser.setProfileImage(photo);
    }
}
