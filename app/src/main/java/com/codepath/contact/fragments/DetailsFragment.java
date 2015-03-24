package com.codepath.contact.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
    private String objectId;
    private ContactInfo contactInfo;
    private int primaryLight;
    private int accentColor;
    private int primaryText;
    private int secondaryText;
    private SupportMapFragment mapFragment;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        tvName = (TextView) v.findViewById(R.id.tvName);
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
        setUpMapIfNeeded();
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
}
