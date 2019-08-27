package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private TextView Title_TextView,  Discript_TextView, Price_TextView, RentInclude_TextView, Pattern_TextView
            , Size_TextView,Floor_TextView ,Type_TextView, Status_TextView, Detail_UserName
            ,Detail_PhoneNumber ,Address_TextView, RentDate_TextView, MoveInDate_TextView,Gender_TextView;
    private ImageView Favorite, Detail_Pic1, Detail_Pic2, Detail_Pic3
            , Detail_Pic4, Detail_Pic5, Detail_Pic6
            , Detail_Pic7, Detail_Pic8, Detail_Pic9
            ,imageView10 ,imageView11 ,imageView12 ,imageView13 ,imageView14
            ,imageView15 ,imageView16 ,imageView17 ,imageView18 ,imageView19
            ,imageView20 ,imageView21 ;
    private Button Detail_Reservation, Detail_Message;
    private ArrayList<ObjectRent> ObjectList;
    private FirebaseAuth mAuth;
    private String Name;
    private String Gender;
    private String TAG = "DetailActivity";
    private String ObjectID;
    private Fragment_contact fragmentContact;
    private int Pics;
    private boolean hasAdd;
    private int ObjType;
    LinearLayout photoList;
    boolean click;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ObjectID = getIntent().getStringExtra("ObjectID");

        init();
        SetEvent();

        // 功能：接收搜尋結果頁面所點擊的物件，物件編號代入即可顯示
        ReadDatabase();

        fragmentContact = new Fragment_contact();
        mAuth = FirebaseAuth.getInstance();

    }


    private void init(){
        // 文字顯示
        click=true;
        if(ObjectID.substring(0,1).equals("H")){
            ObjType=1;
        }

        Address_TextView = findViewById(R.id.Address_TextView);
        Title_TextView = findViewById(R.id.Title_TextView);
        Discript_TextView = findViewById(R.id.Discript_TextView);
        Price_TextView = findViewById(R.id.Price_TextView);
        RentInclude_TextView = findViewById(R.id.RentInclude_TextView);
        Pattern_TextView = findViewById(R.id.Pattern_TextView);
        Size_TextView = findViewById(R.id.Size_TextView);
        Floor_TextView = findViewById(R.id.Floor_TextView);
        Type_TextView = findViewById(R.id.Type_TextView);
        Status_TextView = findViewById(R.id.Status_TextView);
        Detail_UserName = findViewById(R.id.Detail_UserName);
        Detail_PhoneNumber = findViewById(R.id.Detail_PhoneNumber);
        RentDate_TextView = findViewById(R.id.RentDate_TextView);
        MoveInDate_TextView = findViewById(R.id.MoveInDate_TextView);
        Gender_TextView=findViewById(R.id.Gender_TextView);

        // 圖片顯示
        photoList=findViewById(R.id.myphoto);
        Favorite = findViewById(R.id.Favorite);
        imageView10 = findViewById(R.id.imageView10);
        imageView11 = findViewById(R.id.imageView11);
        imageView12 = findViewById(R.id.imageView12);
        imageView13 = findViewById(R.id.imageView13);
        imageView14 = findViewById(R.id.imageView14);
        imageView15 = findViewById(R.id.imageView15);
        imageView16 = findViewById(R.id.imageView16);
        imageView17 = findViewById(R.id.imageView17);
        imageView18 = findViewById(R.id.imageView18);
        imageView19 = findViewById(R.id.imageView19);
        imageView20 = findViewById(R.id.imageView20);
        imageView21 = findViewById(R.id.imageView21);

        // 按鍵
        Detail_Message = findViewById(R.id.Detail_Message);
        Detail_Reservation = findViewById(R.id.Detail_Reservation);
        if(ObjType==1){
            Detail_Message.setVisibility(View.GONE);
            Detail_Reservation.setVisibility(View.GONE);
            Favorite.setVisibility(View.GONE);
        }

        ObjectList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        hasAdd=false;
        Pics = 0;



        if(mAuth.getCurrentUser() != null){

            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("member/"+mAuth.getCurrentUser().getPhoneNumber()+"/Favorite/"+ObjectID);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                        hasAdd=dataSnapshot.getValue(Boolean.class);

                    if(hasAdd){
                        Favorite.setImageResource(R.drawable.icon_icon_favorite2);
                    }

                    else{
                        Favorite.setImageResource(R.drawable.icon_icon_favorite);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Favorite.setEnabled(false);
            Detail_Message.setEnabled(false);
            Detail_Reservation.setEnabled(false);
        }

    }

    private void SetEvent(){
        Detail_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userPhone = mAuth.getCurrentUser().getPhoneNumber(); // 自己的手機號碼
                final String anotherPhone = ObjectList.get(0).getUserPhone(); // 別人的手機號碼
                DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("member/"+userPhone+"/Content");
                reference_contacts.addListenerForSingleValueEvent(new ValueEventListener() {
                    boolean result = true;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){ //
                            if(ds.getKey().equals(anotherPhone)){
                                result = false;
                            }
                        }
                        if(result){
                            if(anotherPhone != mAuth.getCurrentUser().getPhoneNumber())
                                FirebaseDatabase.getInstance().getReference("member/" +userPhone +"/Content/" +anotherPhone)
                                        .setValue("null");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Detail_UserName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DetailActivity.this, ShowMemberActivity.class);
                        intent.putExtra("MemberID", ObjectList.get(0).getUserPhone());
                        startActivity(intent);
                    }
                });



                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.putExtra("id",1);
                startActivity(intent);

            }
        });

        Favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("member/"+mAuth.getCurrentUser().getPhoneNumber()+"/Favorite/"+ObjectID);

                if(!hasAdd){
                    myRef.setValue(true);
                    hasAdd=true;
                    Favorite.setImageResource(R.drawable.icon_icon_favorite2);
                    Toast.makeText(DetailActivity.this, "已加入收藏", Toast.LENGTH_SHORT).show();
                }

                else{
                    myRef.removeValue();
                    hasAdd=false;
                    Favorite.setImageResource(R.drawable.icon_icon_favorite);
                }

            }
        });
        Detail_Reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, ReservationDate.class);
                intent.putExtra("HNum",ObjectID);
                intent.putExtra("Lord",ObjectList.get(0).getUserPhone());
                startActivity(intent);
            }
        });
    }

    private void ReadDatabase(){
        DatabaseReference mRef;
        if(ObjType==1)
            mRef = FirebaseDatabase.getInstance().getReference("FindRoommate/HaveHouse/"+ObjectID);
        else
            mRef = FirebaseDatabase.getInstance().getReference("RentObj/"+ObjectID);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ObjectRent object = dataSnapshot.getValue(ObjectRent.class);
                ObjectList.add(object);
                SetData();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SetData(){
        String price = "";
        String priceInclude = "";
        String Phone = "";

        // 設定標題
        Title_TextView.setText(ObjectList.get(0).getTitle());

        // 設定圖片

        for(; Pics < ObjectList.get(0).getPicNumber(); Pics++){
            // 目前問題就是程式的 執行 速度比 FirebaseStorage 還要快, 還無法得知是否有檔案存在，就已經結束了
            // 需要先確認是否存在檔案，再執行後續的動作
//            Log.d("顯示Pics值",Pics+"");
            LinearLayout.LayoutParams imgLayout=new LinearLayout.LayoutParams(415,415,1.0f);
            imgLayout.setMargins(5,10,5,10);
            final ImageView v=new ImageView(this);
            v.setLayoutParams(imgLayout);
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ReadPicture("RentObj/" + ObjectID + "/pic0" + Pics + ".jpg", v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bitmap z=((BitmapDrawable)v.getDrawable()).getBitmap();
                    magnifyPhoto(view,z);
                }
            });

            photoList.addView(v);


        }

        // 設定租金
        price = ObjectList.get(0).getPrice();
        price = price + "/月";
        Price_TextView.setText(price);

        // 設定租金包含項目
        if(ObjectList.get(0).getpriceInclude().get("Elec").equals("true")) priceInclude = priceInclude + "電費/";
        if(ObjectList.get(0).getpriceInclude().get("Net").equals("true")) priceInclude = priceInclude + "網路費/";
        if(ObjectList.get(0).getpriceInclude().get("Water").equals("true")) priceInclude = priceInclude + "水費/";
        if(ObjectList.get(0).getpriceInclude().get("manage").equals("true")) priceInclude = priceInclude + "管理費/";
        priceInclude = "含" + priceInclude;
        priceInclude = priceInclude.substring(0,priceInclude.length() - 1);
        RentInclude_TextView.setText(priceInclude);

        // 設定格局
        Pattern_TextView.setText(ObjectList.get(0).getPattern());

        // 設定坪數
        Size_TextView.setText(ObjectList.get(0).getSize());

        // 設定樓層
        Floor_TextView.setText(ObjectList.get(0).getCurrentfloor() + "/" + ObjectList.get(0).getMaxfloor());

        // 設定型態
        Status_TextView.setText(ObjectList.get(0).getStatus());

        //設定性別限制
        Gender_TextView.setText(ObjectList.get(0).getGenderLimit());

        // 設定描述
        Discript_TextView.setText(ObjectList.get(0).getDiscript());

        // 設定出租方式
        Type_TextView.setText(ObjectList.get(0).getType());

        // 設定最短租期
        String time = ObjectList.get(0).getRentTime() + " 個月";
        RentDate_TextView.setText(time);



        // 設定可遷入日
        MoveInDate_TextView.setText(ObjectList.get(0).getMoveInDate());

        // 設定屋主名稱
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("member/"+ObjectList.get(0).getUserPhone());
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact n = dataSnapshot.getValue(Contact.class);
                Name = n.FirstName.toString();
                Gender = n.Gender.toString();
                if(Gender.equals("男性")){
                    Name += "先生";
                }else if(Gender.equals("女性")){
                    Name += "小姐";
                }

                Detail_UserName.setText(Name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 屋主手機
        Phone = ObjectList.get(0).getUserPhone();
        Phone = Phone.substring(4);
        Phone = "0" + Phone;
        Detail_PhoneNumber.setText(Phone);


        if(mAuth.getCurrentUser() != null){
            // 留言按鈕
            if(mAuth.getCurrentUser().getPhoneNumber().equals( ObjectList.get(0).getUserPhone())){
                Detail_Message.setVisibility(View.GONE);
                Detail_Reservation.setVisibility(View.GONE);
                Favorite.setVisibility(View.GONE);
            }
        }


        // 設定地址
        Address_TextView.setText(ObjectList.get(0).getCounty()+ObjectList.get(0).getCity()+ObjectList.get(0).getAddress());

        // Device 圖示顯示
        if(ObjectList.get(0).getDevice().get("airCondition").equals("false") ) imageView16.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("bed").equals("false")) imageView12.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("cable").equals("false")) imageView20.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("gus").equals("false")) imageView18.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("internet").equals("false")) imageView19.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("refrig").equals("false")) imageView15.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("sofa").equals("false")) imageView13.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("tableChair").equals("false")) imageView10.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("tv").equals("false")) imageView14.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("wardrobe").equals("false")) imageView11.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("washMachine").equals("false")) imageView17.setImageResource(R.mipmap.step_03);
        if(ObjectList.get(0).getDevice().get("waterHeater").equals("false")) imageView21.setImageResource(R.mipmap.step_03);
    }

    public void ReadPicture(String path, final ImageView v){
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference islandRef = FirebaseStorage.getInstance().getReference().child(path);
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                v.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 刪除 沒用到的 ImageView
                v.setVisibility(View.GONE);
            }
        });
    }
    public void magnifyPhoto(View view,Bitmap z){
        //建立彈出視窗
        final PopupWindow big=new PopupWindow(this);
        big.setOutsideTouchable(true);
        big.setFocusable(true);
        big.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                click=true;
            }
        });

        //設定Layout
        LinearLayout whole=new LinearLayout(this);
        whole.setOrientation(LinearLayout.VERTICAL);

        //設定圖片
        ImageView tv =new ImageView(this);
        tv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

        //加入layout中
//        whole.addView(titleLine);
        whole.addView(tv);

        if(click){
            big.setContentView(whole);
            big.setWidth(1100);
            big.setHeight(1400);

            big.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 0, 0);
            big.update();
            tv.setImageBitmap(z);
            click=false;
        }
    }
}
