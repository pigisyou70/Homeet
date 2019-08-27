package com.example.mainpage;

public class RequestItem {
    int year;
    int month;
    int day;
    String HNum;
    String time;

    public RequestItem(){}
    public RequestItem(int year, int month,int day,String time,String HNum){
        this.year=year;
        this.month=month;
        this.day=day;
        this.time=time;
        this.HNum=HNum;
    }
}
