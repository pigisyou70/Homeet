package com.example.mainpage;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuerryData {
    int hour;
    int minute;
    String HNum;
    String Rtime;
    String Renter;
    String title;
    String Name;
    public QuerryData(String HNum,String time){
        Rtime=time;
        hour=Integer.parseInt(time.substring(0,2));
        minute=Integer.parseInt(time.substring(3,4));
        this.HNum=HNum;
    }
    public QuerryData(String HNum,String time,String Name,String title,String Renter){
        Rtime=time;
        hour=Integer.parseInt(time.substring(0,2));
        minute=Integer.parseInt(time.substring(3,4));
        this.Renter=Renter;
        this.HNum=HNum;
        this.title=title;
        this.Name=Name;
    }
}
