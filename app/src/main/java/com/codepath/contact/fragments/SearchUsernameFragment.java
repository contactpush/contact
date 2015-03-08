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

import com.codepath.contact.R;

public class SearchUsernameFragment extends Fragment{
    private static final String TAG = "SearchUsernameFragment";

    public static SearchUsernameFragment newInstance(){
        SearchUsernameFragment fragment = new SearchUsernameFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_search_username, container, false);

        final Button btnSearch = (Button) v.findViewById(R.id.btnSearchContactButton);
        final EditText etUsername = (EditText) v.findViewById(R.id.etAddContactUsername);

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

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    private void searchForUsername(String username){
        // TODO search on Parse for this user and add a connection request
    }
}
