package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ReservationDate extends AppCompatActivity {
    MaterialCalendarView calendarview;
    public Calendar calendar=Calendar.getInstance();
    String LandLord;
    String HNum;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    ArrayList<CalendarDay> MemoList=new ArrayList<CalendarDay>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_date_layout);
        HNum=getIntent().getStringExtra("HNum");
        LandLord=getIntent().getStringExtra("Lord");


        calendarview=findViewById(R.id.calendarView);
        calendarview.addDecorators(new PassDay());//過去的時間不能點選
        LoadData();
        calendarview.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int year= date.getYear();
                int month=(date.getMonth()+1);//1月為0 故+1
                int day=date.getDay();
                calendar.set(year,(month-1),day);
                int weekDay=calendar.get(Calendar.DAY_OF_WEEK)-1;//星期天為1 故-1

                Intent intent = new Intent(ReservationDate.this, ReservationTime.class);
                intent.putExtra("HNum",HNum);
                intent.putExtra("Lord",LandLord);
                intent.putExtra("year",year);
                intent.putExtra("month",month);
                intent.putExtra("day",day);
                intent.putExtra("weekDay",weekDay);

                startActivity(intent);

            }
        });
    }

    public void LoadData(){
        myRef=database.getReference("RentObj/"+HNum+"/reservedPeople");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //抓年份
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key1 = ds.getKey();
                    int Ryear = Integer.parseInt(key1);
                    //抓月份
                    for(DataSnapshot dss : ds.getChildren()){
                        String key2=dss.getKey();
                        int Rmonth=Integer.parseInt(key2);
                        //抓哪一天
                        for(DataSnapshot dsss :dss.getChildren()){
                            String key3=dsss.getKey();
                            int Rday=Integer.parseInt(key3);
                            //將抓到的值寫入Calendarday的ArrayList
                            CalendarDay memoday=CalendarDay.from(Ryear,(Rmonth-1),Rday);
                            MemoList.add(memoday);

                        }

                    }

                }
                //改變樣式
                calendarview.addDecorators(new OneDayDecorator(MemoList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


}
