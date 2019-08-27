package com.example.mainpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {

    private View view;
    private ImageView welIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_welcome );
        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMainBlue));
        view = findViewById( R.id.id_welView);
        welIcon = findViewById( R.id.id_welIcon );

        Animation myanim = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.fadein);
        view.startAnimation( myanim );
        welIcon.startAnimation( myanim );

        final Intent gotomain = new Intent( WelcomeActivity.this,MainActivity.class );
        Thread timer = new Thread(  ){
            @Override
            public void run(){
                try{sleep( 3000 );
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    startActivity( gotomain );
                    finish();
                }
            }
        };
        timer.start();
    }
}