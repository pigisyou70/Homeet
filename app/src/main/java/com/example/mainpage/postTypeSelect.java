package com.example.mainpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class postTypeSelect extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_post_type_select );
        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMainBlue));
    }
    public void gotoNewObj(View v){
        Intent intent = new Intent( postTypeSelect.this,newObjActivity.class );
        startActivity( intent );
    }
    public void gotoNewRoommate(View v){
        Intent intent=new Intent( postTypeSelect.this,newRoommate_selActivity.class );
        //intent.putExtra("phone", mAuth.getCurrentUser().getPhoneNumber());
        startActivity( intent );
    }
}
