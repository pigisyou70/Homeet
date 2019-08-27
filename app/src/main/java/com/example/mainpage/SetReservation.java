package com.example.mainpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SetReservation extends AppCompatActivity {
    Button bt;
    CheckBox[][] cb=new CheckBox[2][3];
    public ArrayList<Integer> check=new ArrayList<Integer>();

    int HNum;

    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setreservation_layout);
        HNum=getIntent().getIntExtra("objNum",-1);
        for(int i=0;i<cb.length;i++){
            for(int j=0;j<cb[0].length;j++){
                cb[i][j]=new CheckBox(SetReservation.this);
            }
        }

        cb[0][0]=findViewById(R.id.cb00);
        cb[0][1]=findViewById(R.id.cb01);
        cb[0][2]=findViewById(R.id.cb02);
        cb[1][0]=findViewById(R.id.cb10);
        cb[1][1]=findViewById(R.id.cb11);
        cb[1][2]=findViewById(R.id.cb12);

        bt=findViewById(R.id.SetFinish);
        //myRef=database.getReference("RentObj/"+HNum + "/Setting");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<cb.length;i++){
                    for(int j=0;j<cb[0].length;j++){
                        if(cb[i][j].isChecked()){

                            for(int k=0;k<10;k++){
                                check.add(1);
                            }
                        }else{
                            for(int k=0;k<10;k++){
                                check.add(0);
                            }
                        }


                    }
                }
                //myRef.setValue(check);
                Bundle data = new Bundle();
                data.putIntegerArrayList("check",check);
                getIntent().putExtras(data);
                setResult(2,getIntent());
                finish();
            }
        });
    }
}
