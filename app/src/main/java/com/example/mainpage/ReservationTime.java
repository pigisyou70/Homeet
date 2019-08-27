package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.mainpage.ChatMessage.TYPE_RECEIVED_REQUEST;
import static com.example.mainpage.ChatMessage.TYPE_SENT;
import static com.example.mainpage.ChatMessage.TYPE_SENT_REQUEST;

public class ReservationTime extends AppCompatActivity {
    int itemNum;
    TextView SelectDate;
    int year;
    int month;
    int day;
    int weekday;
    int count;

    boolean haveArrange;

    public static final int Refusing = -1;     //訊息型別-發出的訊息
    public static final int Requesting = 0;     //訊息型別-發出的訊息
    public static final int Acept = 1;     //訊息型別-發出的訊息

    //for test
    String HNum;
    String Phone;
    String landlordPhone;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    DatabaseReference btRef;
    ArrayList<Integer> NormalCheck = new ArrayList<Integer>();
    ArrayList<Integer> WeekendCheck = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservatio_time_layout);

        SetSwitch();

        SetDate();
        getNum();
    }
    public void getNum(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("member/" + mAuth.getCurrentUser().getPhoneNumber()+"/Content/"+landlordPhone);
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                count++;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });
    }

    public void SetDate() {
        itemNum = 30;
        Phone=mAuth.getCurrentUser().getPhoneNumber();
        landlordPhone=getIntent().getStringExtra("Lord");
        weekday = getIntent().getIntExtra("weekDay", 0);
        SelectDate = findViewById(R.id.SelectDate);
        year = getIntent().getIntExtra("year", 0);
        month = getIntent().getIntExtra("month", 0);
        day = getIntent().getIntExtra("day", 0);
        String date = year + "年" + month + "月" + day + "日";
        SelectDate.setText(date);
    }

    public void createList(int itemNum) {
        for (int i = 0; i < itemNum; i++) {
            //時間設定
            int hour = 7 + i / 2;
            String min = "30";
            int color = 50;
            int height = 200;
            if (i % 2 == 0) {
                color = 0;
                min = "00";
            }
            final String time;
            if (hour < 10) {
                time = "0" + hour + ":" + min;

            } else {
                time = hour + ":" + min;
            }

            //按鍵設定
            final Button bt = new Button(this);
            bt.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, height));
            bt.setBackgroundColor(Color.argb(50, 200, 200, 255 - color));
            bt.setText("(空)");

            //周末分辨
            if (weekday == 0 || weekday == 6) {
                if(WeekendCheck.size()!=0)
                bt.setEnabled(WeekendCheck.get(i)==1);
            } else {
                if(NormalCheck.size()!=0)
                bt.setEnabled(NormalCheck.get(i)==1);
            }


            LinearLayout linearLayout;
            linearLayout = findViewById(R.id.btnList);
            linearLayout.addView(bt);

            //連結Firebase 讀取資料
            setButton(time, bt);
            checkArrange(time,bt);
            bt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    ChooseTime(time, bt);



                }
            });


            //設定時間列
            TextView tv = new TextView(this);
            tv.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, height));
            tv.setBackgroundColor(Color.argb(50, 200, 200, 255 - color));
            tv.setText(time);
            tv.setGravity(Gravity.CENTER);
            LinearLayout linearLayout2;
            linearLayout2 = findViewById(R.id.TimeList);
            linearLayout2.addView(tv);

        }

    }
    public void checkArrange(final String time,final Button bt){
        myRef = database.getReference("member/"+Phone+"/Reservation/" + year + "/" + month + "/" + day);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    for(DataSnapshot dss:ds.getChildren()){
                        if(dss.getKey().equals(time)){

                            if(!ds.getKey().equals(HNum)){
                                bt.setEnabled(false);
                                bt.setText("該時段您已預約其他物件！");
                            }else if(dss.getValue(Integer.class)==0){
                                bt.setEnabled(false);
                                bt.setText("已送出預約請求");
                            }
                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void ChooseTime(final String time, final Button bt) {
        //到時候的送出預約請求從這裡改
        AlertDialog.Builder dialog = new AlertDialog.Builder(ReservationTime.this);
        dialog.setTitle("預約看屋");
        dialog.setMessage("您要預約 " + time + " 嗎?");
        dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            //點選yes即送出預約請求
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //如果有人同時間預約
                if(haveArrange){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ReservationTime.this);
                    dialog.setTitle("發生錯誤");
                    dialog.setMessage("已有人預約!");
                    dialog.show();
                    haveArrange=false;
                    return;
                }
                SendRequest(time);

                //這裡會改由房東確認後執行---------
                //加入自己的行事曆
                myRef = database.getReference("member/"+Phone+"/Reservation/" + year + "/" + month + "/" + day + "/" + HNum+"/"+time);
                myRef.setValue(Requesting);
                //加入物件的預約時間表
                myRef = database.getReference("RentObj/"+HNum +"/reservedPeople/" + year + "/" + month + "/" + day + "/" + Phone+"/"+time);
                myRef.setValue(Requesting);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ReservationTime.this);
                dialog.setTitle("已送出預約請求");
                dialog.setMessage("待屋主確認後即可完成預約\n可於訊息中確認!");
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finish();
                    }
                });
                dialog.show();







            }
        });
        dialog.setNegativeButton("no", null);
        dialog.show();
    }


    public void SendRequest(String time){
        //toast訊息已發送
        String receivedmsg=year+"."+month+"."+day+"."+time+"."+HNum;
        String sendmsg="我已完成預約"+"\n"+year+"."+month+"."+day+" "+time+"\n"+"物件編號 : "+HNum;



        FirebaseDatabase
                .getInstance()
                .getReference("member/" + Phone+"/Content/"+landlordPhone+"/"+count)
                .setValue(new ChatMessage(sendmsg,TYPE_SENT));

        FirebaseDatabase
                .getInstance()
                .getReference("member/" + landlordPhone+"/Content/"+ Phone+"/"+count)
                .setValue(new ChatMessage(receivedmsg,TYPE_RECEIVED_REQUEST));
    }

    public void setButton(final String time, final Button bt) {
        btRef = database.getReference("RentObj/"+HNum +"/reservedPeople/" + year + "/" + month + "/" + day );

        btRef.addValueEventListener(new ValueEventListener() {
            String checkTime;
            String checkPhone;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //getChildren可以抓到該節點下一層所有的欄位
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for(DataSnapshot dss:ds.getChildren()){
                        checkPhone = ds.getKey();//抓到電話
                        checkTime = dss.getKey();//抓到時間
                        int request=dss.getValue(Integer.class);

                        if (checkPhone.equals(Phone)) {//如果有自己的電話 則反灰
                            bt.setEnabled(false);
                            if (checkTime.equals(time)&&request==1) {//如果時間與該格相同

                                bt.setText("您已預約");
                            }
                        } else if (checkTime.equals(time)) {//如果不是你的電話但有該時間
                            bt.setText("別人已預約");
                            bt.setEnabled(false);
                            break;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void SetSwitch() {
        HNum=getIntent().getStringExtra("HNum");
        myRef = database.getReference("RentObj/"+HNum+"/setting");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int i = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //getChildren可以抓到該節點下一層所有的欄位
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (i < itemNum) {
                        NormalCheck.add(ds.getValue(Integer.class));
                    } else {
                        WeekendCheck.add(ds.getValue(Integer.class));
                    }
                    i++;
                }
                //確定抓完資料後再call 不然會抓不到值
                createList(itemNum);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
