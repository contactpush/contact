package com.codepath.contact.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.contact.R;
import com.codepath.contact.models.Request;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.parse.ParseException;
import com.parse.ParseUser;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SearchUsernameFragment extends Fragment{
    private static final String TAG = "SearchUsernameFragment";

    private Button btnSearch;
    private EditText etUsername;
    private ProgressBar pbTryingRequest;
    private ZXingScannerView mScannerView;
    private SearchUsernameFragmentListener listener;

    public interface SearchUsernameFragmentListener{
        public void searchSuccess(Request request);
        public void searchFailure();
    }

    /**
     * Constructor for the SearchUsernameFragmenr
     * @param searchUsernameFragmentListener SearchUsernameFragmentListener object that will receive events about this fragment.
     *                                       Set here and not in OnAttach(Activity) because the listener doesn't have to be an activity
     * @return A new instance of the SearchUsernameFragment
     */
    public static SearchUsernameFragment newInstance(SearchUsernameFragmentListener searchUsernameFragmentListener){
        SearchUsernameFragment fragment = new SearchUsernameFragment();
        fragment.listener = searchUsernameFragmentListener;

        // avoid null pointer exceptions later by using a dumb listener if none are set
        if(fragment.listener == null){
            Log.w(TAG, "No listener set in SearchUserFragment.newInstance() call.");
            fragment.listener = new SearchUsernameFragmentListener(){
                @Override
                public void searchSuccess(Request request){}
                @Override
                public void searchFailure(){}
            };
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_Contact);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View v = localInflater.inflate(R.layout.fragment_search_username, container, false);
        etUsername = (EditText) v.findViewById(R.id.etAddContactUsername);
        btnSearch = (Button) v.findViewById(R.id.btnSearchContactButton);
        etUsername = (EditText) v.findViewById(R.id.etAddContactUsername);
        pbTryingRequest = (ProgressBar) v.findViewById(R.id.pbTryingRequest);

        etUsername.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
                if(s.toString().trim().length() > 0){
                    btnSearch.setEnabled(true);
                }else{
                    btnSearch.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s){}
        });

        btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(btnSearch.getText().toString().trim().length() <= 0){
                    return;
                }
                SearchUsernameFragment.this.searchForUsername(etUsername.getText().toString());
            }
        });

        Button btnQR = (Button) v.findViewById(R.id.btnAddByQR);
        ImageView image = (ImageView) v.findViewById(R.id.ivQRCode);
        mScannerView = new ZXingScannerView((Context) listener);

        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) listener).setContentView(mScannerView);
            }
        });

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix qrBitMatrix = qrCodeWriter.encode(ParseUser.getCurrentUser().getUsername(), BarcodeFormat.QR_CODE, 400, 400);
            Bitmap qrBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565);
            for (int x = 0; x < 400; x++){
                for (int y = 0; y < 400; y++){
                    qrBitmap.setPixel(x, y, qrBitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                }
            }
            image.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler((ZXingScannerView.ResultHandler) listener);
        mScannerView.startCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        mScannerView.stopCamera();
    }

    public void searchForUsername(final String username){
        pbTryingRequest.setVisibility(ProgressBar.VISIBLE);
        Request.makeRequestForUsername(username, new Request.requestAttemptHandler(){
            @Override
            public void onSuccess(ParseUser requestee, Request request){
                pbTryingRequest.setVisibility(ProgressBar.INVISIBLE);
                Toast.makeText(getActivity(), "Request to " + requestee.getUsername() + " sent.", Toast.LENGTH_SHORT).show();
                listener.searchSuccess(request);
            }

            @Override
            public void onFailure(ParseException e, RequestFailureReason requestFailureReason){
                Toast.makeText(getActivity(), "Request to " + username + " failed: " + requestFailureReason.toString(), Toast.LENGTH_SHORT).show();
                btnSearch.setEnabled(false); // make user change username to request again
                listener.searchFailure();
            }
        });
    }
}
