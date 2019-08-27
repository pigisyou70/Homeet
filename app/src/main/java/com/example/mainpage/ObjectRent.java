package com.example.mainpage;

import java.util.HashMap;

public class ObjectRent {
    private String  address; // 地址
    private String  city; // 區域
    private String  county; // 縣市
    private String  currentfloor; // 房屋曾數
    private String  deposit; // 面談*****
    private HashMap<String,String> device; // 這裡面還有資料 , 包含設備*****
    private String  discript; // 描述
    private String  genderLimit;
    private String  lat; // 經度*****
    private String  lng; // 緯度*****
    private String  maxfloor; // 最高層數
    private String  moveInDate; // 可遷入日
    private String  pattern; // 格局
    private int picNumber;
    private String  price; // 租金
    private HashMap<String,String>  priceInclude; // 裡面還有資料, 租金包含*****
    private String rentTime;
    private boolean  reservation; // 預約功能
    private String  size; // 坪數
    private String  status; // 現況
    private String  title; // 標題
    private String  type; // 型態
    private String  updateTime; // 更新時間
    private String  userPhone; // 刊登人手機
    private boolean  visible; // 可視度

    ObjectRent(){

    }

    public int getPicNumber(){return picNumber;}
    public String getGenderLimit(){
        return genderLimit;
    }
    public String getAddress(){
        return address;
    }
    public String getCity(){
        return city;
    }
    public String getCounty(){
        return county;
    }
    public String getCurrentfloor(){
        return currentfloor;
    }
    public String getDiscript(){
        return discript;
    }
    public String getLat(){
        return lat;
    }
    public String getLng(){
        return lng;
    }
    public String getMaxfloor(){
        return maxfloor;
    }
    public String getMoveInDate(){
        return moveInDate;
    }
    public String getPattern(){
        return pattern;
    }
    public String getPrice(){
        return price;
    }
    public String getRentTime(){
        return rentTime;
    }
    public boolean getReservation(){
        return reservation;
    }
    public String getSize(){
        return size;
    }
    public String getStatus(){
        return status;
    }
    public String getTitle(){
        return title;
    }
    public String getType(){
        return type;
    }
    public String getUpdateTime(){
        return updateTime;
    }
    public String getUserPhone(){
        return userPhone;
    }
    public boolean getVisible(){
        return visible;
    }
    public String getDeposit(){
        return deposit;
    }

    public HashMap<String, String> getDevice() {
        return device;
    }
    public HashMap<String, String> getpriceInclude() {
        return priceInclude;
    }
    }
