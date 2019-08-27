package com.example.mainpage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MatchActivity extends AppCompatActivity {

    Button Match_Save, Match_Cancel;
    Spinner addressSpinner, priceSpinner, sizeSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMainBlue));

        Init();
        SetEvent();
    }

    private void Init(){
        Match_Save = findViewById(R.id.Match_Save);
        Match_Cancel = findViewById(R.id.Match_Cancel);

        addressSpinner = findViewById(R.id.addressSpinner);
        priceSpinner = findViewById(R.id.priceSpinner);
        sizeSpinner = findViewById(R.id.sizeSpinner);

    }
    private void SetEvent(){
        Match_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateData();
                MatchActivity.this.finish();
            }
        });

        Match_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatchActivity.this.finish();
            }
        });
    }

    private void UpdateData(){
        String a = addressSpinner.getSelectedItem().toString();
        String b = priceSpinner.getSelectedItem().toString();
        String c = sizeSpinner.getSelectedItem().toString();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        MatchData matchdata = new MatchData(a, b, c );
        FirebaseDatabase.getInstance().getReference().child("member/"+phoneNumber+"/MatchObject").setValue(matchdata);

    }

    class MatchData{
        public String setAddress;
        public String setPrice;
        public String setSize;

        MatchData(String selectAddress, String selectPrice, String selectSize){
            setAddress = selectAddress;
            setPrice = selectPrice;
            setSize = selectSize;
        }
    }
}
