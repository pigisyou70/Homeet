package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;

public class QueryCalendar extends AppCompatActivity {
    MaterialCalendarView calendarview;
    public Calendar calendar=Calendar.getInstance();
    ArrayList<CalendarDay> MemoList=new ArrayList<CalendarDay>();

    int year;
    int month;
    int day;

    String Phone ;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.querycalendar_layout);
        calendarview=findViewById(R.id.MyCalendar);
        Phone=mAuth.getCurrentUser().getPhoneNumber();
        LoadData();


        calendarview.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                year= date.getYear();
                month=(date.getMonth()+1);//1月為0 故+1
                day=date.getDay();

                calendar.set(year,(month-1),day);
                int weekDay=calendar.get(Calendar.DAY_OF_WEEK)-1;//星期天為1 故-1

                Intent intent = new Intent(QueryCalendar.this, CalendarInfo.class);
                intent.putExtra("year",year);
                intent.putExtra("month",month);
                intent.putExtra("day",day);
                intent.putExtra("weekDay",weekDay);

                startActivity(intent);
            }

        });

    }

    public void LoadData() {

        myRef = database.getReference("member/"+Phone+"/Reservation");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MemoList.clear();
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
                            for(DataSnapshot dssss :dsss.getChildren()){
                                for(DataSnapshot dsssss :dssss.getChildren()){
                                    int type=dsssss.getValue(Integer.class);
                                    if(type==1){
                                        CalendarDay memoday=CalendarDay.from(Ryear,(Rmonth-1),Rday);

                                        MemoList.add(memoday);
                                    }
                                }


                            }
                            //將抓到的值寫入Calendarday的ArrayList


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
