package com.example.mainpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class newRoommate_selActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_new_roommate_sel );
        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMainBlue));

        findViewById( R.id.id_havehouse ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(newRoommate_selActivity.this,newHaveHouseActivity.class);
                startActivity(intent);
            }
        } );
        findViewById( R.id.id_nohouse ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(newRoommate_selActivity.this,newNoHouseActivity.class);
                startActivity(intent);
            }
        } );
    }
}
