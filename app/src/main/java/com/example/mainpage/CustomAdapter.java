package com.example.mainpage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.load.engine.Resource;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

import static com.example.mainpage.ChatMessage.TYPE_RECEIVED;
import static com.example.mainpage.ChatMessage.TYPE_RECEIVED_REQUEST;
import static com.example.mainpage.ChatMessage.TYPE_SENT;
import static com.example.mainpage.ChatMessage.TYPE_SENT_REQUEST;

public class CustomAdapter extends SimpleAdapter {
    Context context;
    int count;
    String Phone;
    String landlord;

    int year;
    int month;
    int day;
    String time;
    String HNum;

    public CustomAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,String Phone,String landlord){
        super(context, data, resource,from,to);

        this.context=context;
        this.Phone=Phone;
        this.landlord=landlord;
    }


    public void getCount(int count){
        this.count=count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        super.getView(position,convertView,parent);
        View view=super.getView(position,convertView,parent);
        final TextView t=view.findViewById(R.id.lRequestText);
        t.setTextColor(Color.BLUE);

        return view;
    }


}
