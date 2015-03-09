package com.codepath.contact.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.contact.R;
import com.codepath.contact.models.Request;
import com.parse.ParseException;
import com.parse.ParseUser;

public class SearchUsernameFragment extends Fragment{
    private static final String TAG = "SearchUsernameFragment";

    Button btnSearch;
    EditText etUsername;
    SearchUsernameFragmentListener listener;

    public interface SearchUsernameFragmentListener{
        public void searchSuccess(Request request);
        public void searchFailure();
    }

    public static SearchUsernameFragment newInstance(SearchUsernameFragmentListener searchUsernameFragmentListener){
        // TODO move this to onAttach
        SearchUsernameFragment fragment = new SearchUsernameFragment();
        fragment.listener = searchUsernameFragmentListener;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_search_username, container, false);

        btnSearch = (Button) v.findViewById(R.id.btnSearchContactButton);
        etUsername = (EditText) v.findViewById(R.id.etAddContactUsername);

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

        return v;
    }

    //TODO implement proper onAttach
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    private void searchForUsername(final String username){
        Request.makeRequestForUsername(username, new Request.requestAttemptHandler(){
            @Override
            public void onSuccess(ParseUser requestee, Request request){
                Toast.makeText(getActivity(), "Request to " + requestee.getUsername() + " sent.", Toast.LENGTH_SHORT).show();
                listener.searchSuccess(request);
            }

            @Override
            public void onFailure(ParseException e, int numberOfMatches){
                Toast.makeText(getActivity(), "Request to " + username + " failed.", Toast.LENGTH_SHORT).show();
                btnSearch.setEnabled(false); // make user change username to request again
                listener.searchFailure();
            }
        });
    }
}
