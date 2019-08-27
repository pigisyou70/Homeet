package com.example.mainpage;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.mainpage.ChatMessage.TYPE_RECEIVED;
import static com.example.mainpage.ChatMessage.TYPE_SENT;

public class Fragment_message extends Fragment implements FragmentBackHandler{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1; // 所點擊人  暱稱
    private String mParam2; // 所點擊人  電話號碼

    private OnFragmentInteractionListener mListener;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrent_user;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef;
    private DatabaseReference myRef;
    private MessageListener messageListener;

    // Variable
    private ArrayList<HashMap<String,String>> list2 = new ArrayList<>();
    private ArrayList<RequestItem> list=new ArrayList<>();

    // Component init
    private ListView listOfMessages ;
    private EditText input;
    private TextView titleBar;
    private ChatMessage message;
    private int count = 0 ;


    int year;
    int month;
    int day;
    String time;
    String HNum;
    private String Phone;
    private String landlord;
    private String userName;
    private String userPhone;

    public Fragment_message() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_message newInstance(String param1, String param2) {
        Fragment_message fragment = new Fragment_message();
        Bundle args = new Bundle();
        args.putString( ARG_PARAM1, param1 );
        args.putString( ARG_PARAM2, param2 );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {
            mParam1 = getArguments().getString( ARG_PARAM1 );
            mParam2 = getArguments().getString( ARG_PARAM2 );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_message, null);

        FloatingActionButton fab;

        titleBar = getActivity().findViewById(R.id.id_titleBar);
        titleBar.setText(mParam1);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user = mAuth.getCurrentUser();

        userName = mCurrent_user.getDisplayName();
        userPhone = mCurrent_user.getPhoneNumber().substring(4);
        userPhone = "0" + userPhone;


        // ListView
        listOfMessages = (ListView)view.findViewById(R.id.list_of_messages);
        input = (EditText)view.findViewById(R.id.input);

        messageListener = new MessageListener();


        // FloatingActionButton init, Set Listener
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 讀取輸入的文字, 並儲存於 Firebase Database
                FirebaseDatabase
                        .getInstance()
                        .getReference("member/" + mCurrent_user.getPhoneNumber() +"/Content/"+mParam2+"/"+count)
                        .setValue(new ChatMessage(input.getText().toString(), TYPE_SENT));

                FirebaseDatabase
                        .getInstance()
                        .getReference("member/" + mParam2+"/Content/"+ mCurrent_user.getPhoneNumber()+"/"+count)
                        .setValue(new ChatMessage(input.getText().toString(), TYPE_RECEIVED)
                        );
                // 用途：推播訊息
                String RecivePhone = mParam2.substring(4);
                RecivePhone = "0" + RecivePhone;

                FirebaseDatabase
                        .getInstance()
                        .getReference("Message/" + RecivePhone +"/")
                        .setValue(new NotificationData(userName, input.getText().toString()));

                // Clear the input
                input.setText("");
            }
        });


        return view;
    }


    private void AddData(ChatMessage c){
        HashMap<String,String> item = new HashMap<>();

        switch (c.getMessageType().intValue()){
            case 0: // 接收方
                item.put("lText",c.getMessageText());
                item.put("lTime",c.getMessageTime());
                list.add(null);
                list2.add(item);
                Show(list2);
                break;
            case 1: // 發送方
                item.put("rText",c.getMessageText());
                item.put("rTime",c.getMessageTime());
                item.put("rReadStatus",c.getMessageReady());
                list.add(null);
                list2.add(item);
                Show(list2);
                break;
            case 2:
                String temp=c.getMessageText();

                int firstdot=temp.indexOf(".");
                int year=Integer.parseInt(temp.substring(0,firstdot));
                temp=temp.substring(firstdot+1,temp.length());

                int seconddot=temp.indexOf(".");
                int month=Integer.parseInt(temp.substring(0,seconddot));
                temp=temp.substring(seconddot+1,temp.length());

                int thirddot=temp.indexOf(".");
                int day=Integer.parseInt(temp.substring(0,thirddot));
                temp=temp.substring(thirddot+1,temp.length());

                int lastdot=temp.indexOf(".");
                String time=temp.substring(0,lastdot);
                temp=temp.substring(lastdot+1,temp.length());

                String HNum=temp;
                RequestItem myrequest=new RequestItem(year,month,day,time,HNum);

                String msg="我已完成預約\n"+year+"."+month+"."+day+"."+time+"\n物件編號 : "+HNum+"\n請點擊做回覆!";

                item.put("lRequestText",msg);
                item.put("lRequestTime",c.getMessageTime());
                list.add(myrequest);
                list2.add(item);
                Show(list2);
                break;
            case 3:
                item.put("rRequestText",c.getMessageText());
                item.put("rRequestTime",c.getMessageTime());
                list.add(null);
                list2.add(item);
                Show(list2);
                break;
        }

    }

    public void Show(ArrayList<HashMap<String,String>> c){
        CustomAdapter adapter = new CustomAdapter(
                getContext(),
                c,
                R.layout.message,
                new String[]{"lText", "lTime","rText","rTime","lRequestText","lRequestTime","rRequestText","rRequestTime", "rReadStatus"},
                new int[]{R.id.lText, R.id.lTime, R.id.rText, R.id.rTime,R.id.lRequestText, R.id.lRequestTime, R.id.rRequestText, R.id.rRequestTime, R.id.rReadStatus},
                mAuth.getCurrentUser().getPhoneNumber(),
                mParam2);

        listOfMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final  int position, long l) {
//                Log.d("QQQ",list.size()+"");
                if(list.get(position)!=null){
                    Phone=mAuth.getCurrentUser().getPhoneNumber();
                    landlord=mParam2;

                    year=list.get(position).year;
                    month=list.get(position).month;
                    day=list.get(position).day;
                    time=list.get(position).time;
                    HNum=list.get(position).HNum;


                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("預約回覆");
                    dialog.setMessage("確認預約?");
                    dialog.setPositiveButton("預約完成", new DialogInterface.OnClickListener() {
                        //點選yes即送出預約請求
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //回覆預約完成
                            ReplyRequest(year+" "+month+"/"+day+"  "+time+"\n物件編號 : "+HNum+"\n預約完成，請確認行事曆!");

                            //更改已回覆訊息
                            String msg="我已完成預約\n"+year+" "+month+"/"+day+"  "+time+"\n物件編號 : "+HNum+"\n(已回覆)";
                            myRef=database.getReference("member/"+Phone+"/Content/"+landlord+"/"+position);
                            myRef.child("messageType").setValue(TYPE_RECEIVED);
                            myRef.child("messageText").setValue(msg);

                            //加入自己的行事曆
                            myRef = database.getReference("member/"+Phone+"/Reservation/" + year + "/" + month + "/" + day + "/" + HNum+"/"+time);
                            myRef.setValue(ReservationTime.Acept);
                            //加入物件的預約時間表
                            myRef = database.getReference("RentObj/"+HNum +"/reservedPeople/" + year + "/" + month + "/" + day + "/" + landlord+"/"+time);
                            myRef.setValue(ReservationTime.Acept);
                            //加入房東的行事曆
                            myRef = database.getReference("member/"+landlord+"/Reservation/" + year + "/" + month + "/" + day + "/" + HNum+"/"+time);
                            myRef.setValue(ReservationTime.Acept);

                        }
                    });
                    dialog.setNegativeButton("當天不便", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //回覆訊息
                            ReplyRequest("不好意思，該時段已有其他安排，請選擇其他時間\n謝謝!");

                            //更改已回覆訊息
                            String msg="我已完成預約\n"+year+" "+month+"/"+day+"  "+time+"\n物件編號 : "+HNum+"\n(已回覆)";
                            myRef=database.getReference("member/"+Phone+"/Content/"+landlord+"/"+position);
                            myRef.child("messageType").setValue(TYPE_RECEIVED);
                            myRef.child("messageText").setValue(msg);

                            //加入自己的行事曆
                            myRef = database.getReference("member/"+Phone+"/Reservation/" + year + "/" + month + "/" + day + "/" + HNum+"/"+time);
                            myRef.removeValue();
                            //加入物件的預約時間表
                            myRef = database.getReference("RentObj/"+HNum +"/reservedPeople/" + year + "/" + month + "/" + day + "/" + landlord+"/"+time);
                            myRef.removeValue();
                            //加入房東的行事曆
                            myRef = database.getReference("member/"+landlord+"/Reservation/" + year + "/" + month + "/" + day + "/" + HNum+"/"+time);
                            myRef.removeValue();

                        }
                    });
                    dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.show();
                }
            }
        });
        listOfMessages.setAdapter(adapter);
        listOfMessages.setSelection(ListView.FOCUS_DOWN);
    }

    public void ReplyRequest(String msg){
        //toast訊息已發送
        FirebaseDatabase
                .getInstance()
                .getReference("member/" + Phone+"/Content/"+landlord+"/"+count)
                .setValue(new ChatMessage(msg,TYPE_SENT));

        FirebaseDatabase
                .getInstance()
                .getReference("member/" + landlord+"/Content/"+ Phone+"/"+count)
                .setValue(new ChatMessage(msg,TYPE_RECEIVED));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction( uri );
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException( context.toString()
//                    + " must implement OnFragmentInteractionListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
//        Log.d("FragmentMessage", "onDetach");
        FirebaseMessaging.getInstance().subscribeToTopic(userPhone);

    }

    @Override
    public void onResume(){
        super.onResume();
//        Log.d("FragmentMessage", "onResume");
        mRef = FirebaseDatabase.getInstance().getReference("member/" + mAuth.getCurrentUser().getPhoneNumber()+"/Content/"+mParam2);
        count = 0;
        FirebaseMessaging.getInstance().unsubscribeFromTopic(userPhone);
        list2.clear();
        mRef.addChildEventListener(messageListener);
    }

    @Override
    public void onStop(){
        super.onStop();
//        Log.d("FragmentMessage", "onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.d("FragmentMessage", "onPause");
        mRef.removeEventListener(messageListener);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public boolean onBackPressed(){
        titleBar.setText("我的訊息");
        return BackHandlerHelper.handleBackPress(this);
    }

    class MessageListener implements ChildEventListener{
        private int times = 0 ;
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            message = dataSnapshot.getValue(ChatMessage.class);
            AddData(message);

            if(message.getMessageType() == 0 || message.getMessageType() == 2){
                FirebaseDatabase
                        .getInstance()
                        .getReference("member/" + mParam2 + "/Content/" + mAuth.getCurrentUser().getPhoneNumber() + "/" + count + "/messageReady")
                        .setValue("已讀")
                ;

                FirebaseDatabase
                        .getInstance()
                        .getReference("member/" + mAuth.getCurrentUser().getPhoneNumber() + "/Content/"  + mParam2+ "/" + count + "/messageReady")
                        .setValue("已讀")
                ;
            }
            count++;
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            message = dataSnapshot.getValue(ChatMessage.class);
            times = Integer.parseInt(dataSnapshot.getKey());
            HashMap<String,String> item = new HashMap<>();
            // 尋找位置
            switch (message.getMessageType().intValue()){
                case 0:
                    item.put("lText",message.getMessageText());
                    item.put("lTime",message.getMessageTime());
                    list2.set(times,item);
                    Show(list2);
                    break;
                case 1:
                    item.put("rText",message.getMessageText());
                    item.put("rTime",message.getMessageTime());
                    item.put("rReadStatus",message.getMessageReady());
                    list2.set(times,item);
                    Show(list2);
                    break;
            }
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


    }

    public static class NotificationData {
        public String title; // 發送者
        public String content; // 訊息內容
        NotificationData(String messageTitle, String messageContent){
            this.title = messageTitle;
            this.content = messageContent;
        }
    }
}
