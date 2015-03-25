package com.codepath.contact.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.contact.R;
import com.codepath.contact.models.ContactInfo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class DetailsFragment extends Fragment {
    public static final String OBJECT_ID = "objectId";
    private static final String TAG = "DetailsFragment";
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvAddress;
    private String objectId;
    private ContactInfo contactInfo;
    private int primaryLight;
    private int accentColor;
    private int primaryText;
    private int secondaryText;
    private SupportMapFragment mapFragment;
    private View fabPhone;
    private View fabEmail;
    private Transition.TransitionListener mEnterTransitionListener;
   // private DetailsFragmentListener listener;

    public static DetailsFragment newInstance(String objectId) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(OBJECT_ID, objectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objectId = getArguments().getString(OBJECT_ID);
        Log.d(TAG, "onCreate received objectId=" + (objectId == null ? "NULL" : objectId));
        accentColor = getActivity().getResources().getColor(R.color.accent);
        secondaryText = getActivity().getResources().getColor(R.color.secondary_text);
        primaryLight = getActivity().getResources().getColor(R.color.primary_light);
        primaryText = getActivity().getResources().getColor(R.color.primary_text);
        mEnterTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Log.d(TAG, "calling enterReveal");
                enterReveal();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };
        getActivity().getWindow().getEnterTransition().addListener(mEnterTransitionListener);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        tvName = (TextView) v.findViewById(R.id.tvName);
        tvAddress = (TextView) v.findViewById(R.id.tvAddress);
        fabPhone = v.findViewById(R.id.fabPhone);
        fabPhone.setVisibility(View.INVISIBLE);
        // Dial contact's number.
        // This shows the dialer with the number, allowing you to explicitly initiate the call.
        fabPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + contactInfo.getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });
        fabEmail = v.findViewById(R.id.fabEmail);
        fabEmail.setVisibility(View.INVISIBLE);
        // Dial contact's number.
        // This shows the dialer with the number, allowing you to explicitly initiate the call.
        fabEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] emails = new String[]{contactInfo.getEmail()};
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, emails);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
        ContactInfo.getContactInfo(objectId, new ContactInfo.OnContactReturnedListener() {
            @Override
            public void receiveContact(ContactInfo contactInfo) {
                DetailsFragment.this.contactInfo = contactInfo;
                setValues();
            }
        });
        return v;
    }

    private void setValues(){
        String imageFileUrl = contactInfo.getProfileImage();
        if (imageFileUrl != null){
            Picasso.with(getActivity()).load(imageFileUrl).into(ivProfileImage);
        }
        tvName.setText(contactInfo.getName());
        tvName.setTextColor(primaryText);
        tvAddress.setText(contactInfo.getAddress());
        tvAddress.setTextColor(primaryText);
        setUpMapIfNeeded();
        enterReveal();
    }

    protected void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment == null){
            mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)); //  getActivity().getSupportFragmentManager().findFragmentById(R.id.map));
        }

        // Check if we were successful in obtaining the map.
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        }
    }

    // The Map is verified. It is now safe to manipulate the map.
    protected void loadMap(final GoogleMap googleMap) {
        if (googleMap != null) {
            final ParseUser user = (ParseUser) contactInfo.get("User");
            if(user != null){
                user.fetchIfNeededInBackground(new GetCallback<ParseObject>(){
                    @Override
                    public void done(ParseObject parseObject, ParseException e){
                        if(e == null){
                            ParseGeoPoint geoPoint = user.getParseGeoPoint("lastLocation");
                            if(geoPoint != null){
                                LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                                googleMap.animateCamera(cameraUpdate);
                                shouldShowMap(true);

                                // Define color of marker icon
                                BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

                                // Creates and adds marker to the map
                                Marker marker = googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(defaultMarker));

                                Log.d(TAG, "found lat/long and updated map with location:" + latLng.toString());
                                return;
                            }else{
                                Log.e(TAG, "could not find lat/long to update map.");
                                shouldShowMap(false);
                            }
                        }else{
                            Log.e(TAG, "error fetching user data.", e);
                            shouldShowMap(false);
                        }
                    }
                });

                return;

            }else{
                Log.e(TAG, "could not find user to update map.");
            }
        }else{
            Log.e(TAG, "could not find map to update map.");
        }
        shouldShowMap(false);
    }

    private void shouldShowMap(boolean shouldShow){
        if(shouldShow){
            this.mapFragment.getView().setVisibility(View.VISIBLE);
        }else{
            this.mapFragment.getView().setVisibility(View.GONE);
        }
    }

    void enterReveal() {
        if (contactInfo.getEmail() != null) revealEmail();
        if (contactInfo.getPhone() != null) revealPhone();
    }

    private void revealEmail(){
        // get the center for the clipping circle
        int cx = fabEmail.getMeasuredWidth() / 2;
        int cy = fabEmail.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(fabEmail.getWidth(), fabEmail.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(fabEmail, cx, cy, 0, finalRadius);

        //anim.setInterpolator(new AccelerateInterpolator(1.0f));
        anim.setDuration(500);

        // make the view visible and start the animation

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getActivity().getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fabEmail.setVisibility(View.VISIBLE);
        anim.start();
    }

    private void revealPhone(){
        // get the center for the clipping circle
        int cx = fabPhone.getMeasuredWidth() / 2;
        int cy = fabPhone.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(fabPhone.getWidth(), fabPhone.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(fabPhone, cx, cy, 0, finalRadius);

        //anim.setInterpolator(new AccelerateInterpolator(1.0f));
        anim.setDuration(500);

        // make the view visible and start the animation

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getActivity().getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fabPhone.setVisibility(View.VISIBLE);
        anim.start();
    }

    void exitReveal() {
        // get the center for the clipping circle
        int cx = fabPhone.getMeasuredWidth() / 2;
        int cy = fabPhone.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = fabPhone.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(fabPhone, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fabPhone.setVisibility(View.INVISIBLE);
                getActivity().supportFinishAfterTransition();
            }
        });

        // start the animation
        anim.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                exitReveal();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    @Override
    public void onBackPressed() {
        exitReveal();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (DetailsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DetailsFragmentListener");
        }
    }

    public interface DetailsFragmentListener{
        void onBackPressed();
    } */
}
