package com.example.mainpage;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

public class OneDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    private final Drawable highlightDrawable;
    static final int color=Color.argb(50,200,100,150);
    ArrayList<CalendarDay> MemoList;

    public OneDayDecorator() {
        highlightDrawable = new ColorDrawable(color);
    }

    public  OneDayDecorator(ArrayList<CalendarDay> arrayList){
        MemoList=arrayList;
        highlightDrawable = new ColorDrawable(color);
    }


    @Override//條件
    public boolean shouldDecorate(CalendarDay day) {
        return (MemoList.contains(day));
    }

    @Override//執行
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        //view.addSpan(new RelativeSizeSpan(1.2f));
        view.setBackgroundDrawable(highlightDrawable);

    }

}