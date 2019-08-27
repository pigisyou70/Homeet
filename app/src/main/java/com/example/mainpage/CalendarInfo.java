package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.example.mainpage.ChatMessage.TYPE_RECEIVED;
import static com.example.mainpage.ChatMessage.TYPE_RECEIVED_REQUEST;
import static com.example.mainpage.ChatMessage.TYPE_SENT;
import static com.example.mainpage.ChatMessage.TYPE_SENT_REQUEST;

public class CalendarInfo extends AppCompatActivity {
    ArrayList<QuerryData> QQ;
    ArrayList<QuerryData> QQQ;
    int firstcount;
    int secondcount;
    int year;
    int month;
    int day;
    int weekday;
    TextView SelectDate2;
    LinearLayout linearLayout;

    ArrayList<HashMap<String,String>> list2 ;
    String Phone;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_info);

        list2 = new ArrayList<>();
        SetValue();
        LoadMemo();

    }
    public void SetValue(){
        linearLayout=findViewById(R.id.memolist);
        QQ=new ArrayList<>();
        QQQ=new ArrayList<>();
        Phone=mAuth.getCurrentUser().getPhoneNumber();
        weekday = getIntent().getIntExtra("weekDay", 0);
        SelectDate2 = findViewById(R.id.SelectDate2);
        year = getIntent().getIntExtra("year", 0);
        month = getIntent().getIntExtra("month", 0);
        day = getIntent().getIntExtra("day", 0);
        String date = year + "年" + month + "月" + day + "日";
        SelectDate2.setText(date);
    }
    public void LoadMemo(){
        myRef=database.getReference("member/"+Phone+"/Reservation/" + year + "/" + month + "/" + day);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                QQ.clear();
                QQQ.clear();
                linearLayout.removeAllViews();
                String RHNum=null;
                String Rtime=null;

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    for(DataSnapshot dss : ds.getChildren()){
                        RHNum=ds.getKey();
                        Rtime=dss.getKey();
                        int check=dss.getValue(Integer.class);
                        if(check==1){
                            QuerryData querryData=new QuerryData(RHNum,Rtime);
                            QQ.add(querryData);
                        }

                    }
                }
                if(QQ.size()==0){
                    TextView tv=new TextView(CalendarInfo.this);
                    tv.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, 250));
                    tv.setText("(今日無任何安排)");
                    tv.setPadding(100,50,100,50);
                    tv.setTextSize(15);
                    linearLayout.addView(tv);
                }
                if(QQ.size()>0)
                    getRenter(QQ);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<QuerryData>  sorting(ArrayList<QuerryData> list){
        for(int i=0;i<list.size()-1;i++){
            for(int j=i+1;j<list.size();j++){
                if(list.get(i).hour>list.get(j).hour){
                    Collections.swap(list,i,j);
                }else if(list.get(i).hour==list.get(j).hour && list.get(i).minute>list.get(j).minute){
                    Collections.swap(list,i,j);
                }
            }
        }
        return list;
    }
    public void getRenter(ArrayList<QuerryData> myList){
        for(int i=0;i<myList.size();i++){
            final String RHNum=myList.get(i).HNum;
            final String Rtime=myList.get(i).Rtime;


            DatabaseReference myRef2=database.getReference("RentObj/"+RHNum);
            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String title1="";
                    String landlord1="";
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        if(ds.getKey().equals("userPhone")){
                            landlord1=ds.getValue(String.class);
                        }
                        if(ds.getKey().equals("title")){
                            title1=ds.getValue(String.class);
                        }
                    }
                    final String title= title1;
                    final String landlord=landlord1;

                    DatabaseReference myRef3=database.getReference("RentObj/"+RHNum+"/reservedPeople/"+year+"/"+month+"/"+day);
                    myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String Renter="";
                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                for(DataSnapshot dss:ds.getChildren()){
                                    //在該物件下方若有任一個欄位的值相同(相同時間)
                                    if(dss.getKey().equals(Rtime)){
                                        //抓取該登記人姓名
                                        Renter=ds.getKey();

                                        //如果自己就是預約人就抓房東的
                                        if(Renter.equals(Phone)){
                                            Renter=landlord;
                                        }
                                        findName(Rtime,RHNum, Renter,title);

                                        firstcount++;
                                    }
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
    }

    public void findName(final String time, final String HNum, final String Renter, final String title){

        DatabaseReference myRef2=database.getReference("member/"+Renter);
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String FirstName="";
                String Gender="";
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().equals("FirstName")){
                        FirstName=ds.getValue(String.class);
                    }
                    if(ds.getKey().equals("Gender")){
                        if(ds.getValue(String.class).equals("男性")){
                            Gender="先生";
                        }else{
                            Gender="小姐";
                        }
                    }

                }
                FirstName+=Gender;
                QuerryData querryData=new QuerryData(HNum,time, FirstName,title,Renter);
                QQQ.add(querryData);
                //test
//                HashMap<String,String> list=list=new HashMap<>();
//                String RenterPhone="0"+Renter.substring(4,Renter.length());
//                list.put("time",time);
//                list.put("title",title);
//                list.put("name",FirstName);
//                list.put("Phone",RenterPhone);
//                list2.add(list);


                secondcount++;
                if(secondcount==firstcount){
                    QQQ=sorting(QQQ);
                    linearLayout.removeAllViews();
                    for(QuerryData layoutData:QQQ){
                        Layout(layoutData.Rtime,layoutData.HNum,layoutData.Name,layoutData.title,layoutData.Renter);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Layout(final String Rtime,final String RHNum,String Name,String title,final String Renter){
        String RenterPhone="0"+Renter.substring(4,Renter.length());
        //Layout設定
        LinearLayout.LayoutParams LineLayout=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LineLayout.setMargins(0,0,0,50);
        LinearLayout.LayoutParams LL=new LinearLayout.LayoutParams(250,300);

        LinearLayout QuerryObj=new LinearLayout(this);
        QuerryObj.setOrientation(LinearLayout.HORIZONTAL);
        QuerryObj.setLayoutParams(LineLayout);

        LL.gravity=Gravity.CENTER_HORIZONTAL;
        LineLayout.gravity=Gravity.CENTER;

        //時間
        TextView time=new TextView(CalendarInfo.this);
        time.setLayoutParams(LL);
        time.setGravity(Gravity.CENTER);
        time.setText(Rtime);
        time.setPadding(25,15,25,15);
        time.setTextSize(20);
        time.setTextColor(Color.rgb(0,87,75));
        QuerryObj.addView(time);

        //物件
        LinearLayout line=new LinearLayout(this);
        line.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams ab=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ab.gravity=Gravity.CENTER_VERTICAL;
        line.setLayoutParams(ab);

        final TextView Obj=new TextView(CalendarInfo.this);
        Obj.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,150));
        Obj.setText(title);
        Obj.setPadding(15,30,25,15);
        Obj.setTextSize(24);


        line.addView(Obj);


        //聯繫人
        LinearLayout secondList=new LinearLayout(this);
        secondList.setLayoutParams(LineLayout);

        TextView contact=new TextView(CalendarInfo.this);
        //contact.setLayoutParams(LL);
        contact.setText(Name);
        contact.setGravity(Gravity.CENTER_HORIZONTAL);
        contact.setPadding(15,15,25,15);
        contact.setTextSize(20);
        secondList.addView(contact);

        //電話
        TextView phone=new TextView(this);
        phone.setText(RenterPhone);
        phone.setGravity(Gravity.CENTER_HORIZONTAL);
        phone.setPadding(15,15,25,15);
        phone.setTextSize(18  );
        secondList.addView(phone);


        line.addView(secondList);
        QuerryObj.addView(line);
        QuerryObj.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.layout_contact) );
        QuerryObj.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if  (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    view.setElevation( 15 );

                    return true ;
                }
                else if  (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    view.setElevation( 0 );
                    new AlertDialog.Builder(CalendarInfo.this)
                            .setItems(new String[]{"檢視物件","取消預約"}, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int position) {
                                    switch (position){
                                        case 0:
                                            Intent intent = new Intent(CalendarInfo.this,DetailActivity.class);
                                            intent.putExtra("ObjectID", RHNum);
                                            startActivity(intent);

                                            break;
                                        case 1:
                                            androidx.appcompat.app.AlertDialog.Builder dialog2 = new androidx.appcompat.app.AlertDialog.Builder(CalendarInfo.this);
                                            dialog2.setTitle("取消預約");
                                            dialog2.setMessage("您確定要取消 " + Rtime + "的預約嗎?");
                                            dialog2.setNegativeButton("no",null );
                                            dialog2.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    DatabaseReference Df=database.getReference("member/"+Phone+"/Reservation/"+year+"/"+month+"/"+day+"/"+RHNum);
                                                    Df.removeValue();

                                                    DatabaseReference Ef=database.getReference("member/"+Renter+"/Reservation/"+year+"/"+month+"/"+day+"/"+RHNum);
                                                    Ef.removeValue();

                                                    DatabaseReference Ff=database.getReference("RentObj/"+RHNum +"/reservedPeople/" + year + "/" + month + "/" + day + "/" + Renter+"/"+Rtime);
                                                    Ff.removeValue();

                                                    DatabaseReference HF=database.getReference("RentObj/"+RHNum +"/reservedPeople/" + year + "/" + month + "/" + day + "/" + Phone+"/"+Rtime);
                                                    HF.removeValue();

                                                    getNum(Renter);



                                                }
                                            });
                                            dialog2.show();

                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .show();


                    return true ;
                }
                return  false;
            }
        });

        linearLayout.addView(QuerryObj);




    }
    public void getNum(final String Renter){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("member/" + mAuth.getCurrentUser().getPhoneNumber()+"/Content/"+Renter);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    count++;
                }
                SendConcel(Renter,count);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void SendConcel(String Renter,int count){
        //toast訊息已發送
        String receivedmsg=year+"."+month+"."+day+"\n當天因有其他因素故取消預約\n不好意思!\n(系統已取消)";
        String sendmsg=year+"."+month+"."+day+"\n當天因有其他因素故取消預約\n不好意思!\n(系統已取消)";



        FirebaseDatabase
                .getInstance()
                .getReference("member/" + Phone+"/Content/"+Renter+"/"+count)
                .setValue(new ChatMessage(sendmsg,TYPE_SENT));

        FirebaseDatabase
                .getInstance()
                .getReference("member/" + Renter+"/Content/"+ Phone+"/"+count)
                .setValue(new ChatMessage(receivedmsg,TYPE_RECEIVED));
    }

}
