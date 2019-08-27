package com.example.mainpage;

public class MySorting {
    String name;
    String title;
    String county;
    String city;
    int price;
    float size;
    long times;
    String HNum;
    String NickName;
    int picNumber;
    String Gender;
    String discript;

    MySorting(){

    }
    MySorting(String name,String title,String county,String city, String price,long time,String HNum,String discript,int picNumber){
        this.name=name;
        this.title=title;
        this.county=county;
        this.city=city;
        //比較值
        this.price=Integer.parseInt(price);
        this.times=time;
        this.HNum=HNum;
        this.discript=discript;
        this.picNumber=picNumber;
    }
//    MySorting(String name,String title,String county,String city, String price,String size,long time,String HNum){
//        this.name=name;
//        this.title=title;
//        this.county=county;
//        this.city=city;
//        //比較值
//        this.price=Integer.parseInt(price);
//        this.size=Float.parseFloat(size);
//        this.times=time;
//        this.HNum=HNum;
//    }
    //找房用
    MySorting(String name,String title,String county,String city, String price,String size,long time,String HNum,int picNumber){
        this.name=name;
        this.title=title;
        this.county=county;
        this.city=city;
        //比較值
        this.price=Integer.parseInt(price);
        this.size=Float.parseFloat(size);
        this.times=time;
        this.HNum=HNum;
        this.picNumber=picNumber;
    }
    //找房用  換名字
    MySorting(String NickName,String name,String title,String county,String city, String price,String size,long time,String HNum,int picNumber){
        this.name=name;
        this.NickName=NickName;
        this.title=title;
        this.county=county;
        this.city=city;
        //比較值
        this.price=Integer.parseInt(price);
        this.size=Float.parseFloat(size);
        this.times=time;
        this.HNum=HNum;
        this.picNumber=picNumber;
    }

    //找室友  加入名字性別
    MySorting(String NickName,String name,String title,String county,String city, String price,String size,long time,String HNum,String Gender,String discript,int picNumber){
        this.name=name;
        this.NickName=NickName;
        this.title=title;
        this.county=county;
        this.city=city;
        //比較值
        this.price=Integer.parseInt(price);
        this.size=Float.parseFloat(size);
        this.times=time;
        this.HNum=HNum;
        this.Gender=Gender;
        this.discript=discript;
        this.picNumber=picNumber;
    }
}
