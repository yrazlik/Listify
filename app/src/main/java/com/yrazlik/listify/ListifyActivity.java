package com.yrazlik.listify;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yrazlik.listify.connection.ResponseListener;
import com.yrazlik.listify.connection.ServiceRequest;

/**
 * Created by SUUSER on 31.08.2015.
 */
public class ListifyActivity extends Activity implements View.OnClickListener, ResponseListener{

    private EditText edittextArtist;
    private Button buttonCreateListify;
    final ResponseListener listener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listify);
        initUI();
    }

    private void initUI(){
        edittextArtist = (EditText)findViewById(R.id.edittextArtist);
        buttonCreateListify = (Button)findViewById(R.id.buttonCreateListify);
        buttonCreateListify.setOnClickListener(this);
        edittextArtist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 3){
                    ServiceRequest request = new ServiceRequest(getBaseContext(), listener);
                    request.makeGetArtistIdRequest();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonCreateListify){
            String artistName = edittextArtist.getText().toString();
            if(artistName.length() > 0){
                artistName = artistName.trim();
                ServiceRequest request = new ServiceRequest(getBaseContext(), this);

            }else{
                //TODO:show error
            }
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure() {

    }
}
