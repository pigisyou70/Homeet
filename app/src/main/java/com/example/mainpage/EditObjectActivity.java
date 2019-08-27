package com.example.mainpage;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class EditObjectActivity extends AppCompatActivity {

    private String TAG = "EditObjectActivity";
    private String ObjectID, Time;
    private int picNumber, showPicNumber;
    private boolean Visible ,click;
    private ArrayList<ObjectRent> ObjectList;
    private ArrayList<String> options1Items;
    private ArrayList<ArrayList<String>> options2Items;
    private ArrayList<ArrayList<ArrayList<String>>> options3Items;
    private Context context;
    // 上傳相片
    private ArrayList<byte[]> photoPaths = new ArrayList<>();
    private ArrayList<String> photoPath = new ArrayList<>();
    private static final int CUSTOM_REQUEST_CODE = 303;
    public static final int RC_PHOTO_PICKER_PERM = 123;
    private ArrayList<Integer> setting=new ArrayList<>(  );


    // 可遷入日
    private DatePickerDialog datePick;


    private Button btnManage, btnNet, btnWater, btnElec;
    private Button washMachine, Refrig, TV, Cable, waterHeater, Internet, Gas, Bed, Wardrobe, Sofa, tableChair, airCondition;
    private EditText Title, Descrip, Address, maxFloor, currentFloor, Price, moveInDate, RentTime, Size;
    private TextView Pattern;
    private Switch Reservation;
    private Spinner houseStatus, houseType, County, City, Deposit; // id_deposit 押金

    private Bitmap bmp; //臨時圖片
    private Date date = new Date( System.currentTimeMillis() );
    private DatabaseReference myRef;

    private OptionsPickerView patternOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_object );



        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMainBlue));

        Intent intent = getIntent();
        ObjectID = intent.getStringExtra("ObjectID");
        myRef = FirebaseDatabase.getInstance().getReference("RentObj/" + ObjectID);

        init();
        btnListner();
        Read(ObjectID);


        Time = String.valueOf( date.getTime() );
        Visible = true;
    }

    private void init(){
        // EditText init
        Title = findViewById(R.id.id_titleText);
        Descrip = findViewById(R.id.id_discript);
        Address = findViewById(R.id.id_addressText);
        maxFloor = findViewById(R.id.id_maxFloor);
        currentFloor = findViewById(R.id.id_curFloor);
        Price = findViewById(R.id.id_Price);
        Size = findViewById(R.id.id_size);
        RentTime = findViewById(R.id.id_rentTime);

        // Spinner init
        houseStatus = findViewById(R.id.id_objStatus);
        houseType = findViewById(R.id.id_objType);
        County = findViewById(R.id.id_objCounty);
        City = findViewById(R.id.id_objCity);
        Deposit = findViewById(R.id.id_deposit);


        moveInDate = findViewById(R.id.id_moveInDate);

        // Button init
        btnManage = findViewById(R.id.id_btnManage);
        btnNet = findViewById(R.id.id_btnNet);
        btnWater = findViewById(R.id.id_btnWater);
        btnElec = findViewById(R.id.id_btnElec);

        washMachine = findViewById(R.id.id_washingMachine);
        Refrig = findViewById(R.id.id_refrig);
        TV = findViewById(R.id.id_tv);
        Cable = findViewById(R.id.id_cable);
        waterHeater = findViewById(R.id.id_waterHeater);
        Internet = findViewById(R.id.id_Internet);
        Gas = findViewById(R.id.id_gus);
        Bed = findViewById(R.id.id_bed);
        Wardrobe = findViewById(R.id.id_wardrobe);
        Sofa = findViewById(R.id.id_sofa);
        tableChair = findViewById(R.id.id_tableChair);
        airCondition = findViewById(R.id.id_airCondition);

        // 設定上傳圖片按鈕 的程式
        findViewById( R.id.addPhotoBtn).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPicBtnClick();
            }
        });

        // Switch init
        Reservation = findViewById(R.id.id_reservation);


        // TextView init
        Pattern = findViewById(R.id.id_pattern);

        ObjectList = new ArrayList<>();

//         Pattern
//        格局的選項 房
        options1Items = new ArrayList<>();
        //格局的選項 廳
        options2Items = new ArrayList<>();
        //格局的選項 衛浴
        options3Items = new ArrayList<>();

        patternOptionData();//初始化格局選單內容
        patternOptionPicker();
    }

    private void Read(String ID){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference( "RentObj/" + ID);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ObjectRent object = dataSnapshot.getValue(ObjectRent.class);
                ObjectList.add(object);
                Setting();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Setting(){
        int counts = 0;
        Title.setText(ObjectList.get(0).getTitle());// 標題
        Descrip.setText(ObjectList.get(0).getDiscript()); // 描述

        // 圖片
        // 取得圖片數量
        showPicNumber = ObjectList.get(0).getPicNumber();
        photoPaths = new ArrayList<>();
        if (showPicNumber > 0){
            for (int i = 0; i < showPicNumber; i++){
                final StorageReference islandRef = FirebaseStorage.getInstance().getReference("RentObj/" + ObjectID).child("pic0" + i + ".jpg");
                final long ONE_MEGABYTE = 1024 * 1024;
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        photoPaths.add(bytes);
                        addPicResume(photoPaths);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        }


        // 出租方式
        Resources res = getResources();
        String[] status = res.getStringArray(R.array.spn_statusList);
        for (String i : status) {
            if( i.equals(ObjectList.get(0).getStatus())){
                break;
            }
            counts++;
        }
        houseStatus.setSelection(counts);


        // 房屋型態
        res = getResources();
        String[] type = res.getStringArray(R.array.spn_typeList);
        for (String i : type) {
            if( i.equals(ObjectList.get(0).getType())){
                break;
            }
            counts++;
        }
        houseType.setSelection(counts);



        // 選擇縣市
        res = getResources();
        String[] county = res.getStringArray(R.array.county_search);
        counts = 0;
        for (String i : county) {
            if( i.equals(ObjectList.get(0).getCounty())){
                break;
            }
            counts++;
        }
        County.setSelection(counts);



        // 選擇區域
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,getCity(counts),R.layout.simple_list_item_1);
        City.setAdapter(arrayAdapter);
        res = getResources();
        String[] cities = res.getStringArray(getCity(counts));
        counts = 0;
        for (String i : cities) {
            if( i.equals(ObjectList.get(0).getCity())){
                break;
            }
            counts++;
        }
        City.setSelection(counts);

        Address.setText(ObjectList.get(0).getAddress()); // 地址
        maxFloor.setText(ObjectList.get(0).getMaxfloor()); // 最高樓層
        currentFloor.setText(ObjectList.get(0).getCurrentfloor()); // 所在樓層
        Price.setText(ObjectList.get(0).getPrice()); // 租金

        // 租金包含
        if(ObjectList.get(0).getpriceInclude().get("Elec").equals("true")) btnElec.setSelected(true);
        if(ObjectList.get(0).getpriceInclude().get("Net").equals("true")) btnNet.setSelected(true);
        if(ObjectList.get(0).getpriceInclude().get("Water").equals("true")) btnWater.setSelected(true);
        if(ObjectList.get(0).getpriceInclude().get("manage").equals("true")) btnManage.setSelected(true);

        // 設備家具
        if(ObjectList.get(0).getDevice().get("airCondition").equals("true")) airCondition.setSelected(true);
        if(ObjectList.get(0).getDevice().get("bed").equals("true")) Bed.setSelected(true);
        if(ObjectList.get(0).getDevice().get("cable").equals("true")) Cable.setSelected(true);
        if(ObjectList.get(0).getDevice().get("gus").equals("true")) Gas.setSelected(true);
        if(ObjectList.get(0).getDevice().get("internet").equals("true")) Internet.setSelected(true);
        if(ObjectList.get(0).getDevice().get("refrig").equals("true")) Refrig.setSelected(true);
        if(ObjectList.get(0).getDevice().get("sofa").equals("true")) Sofa.setSelected(true);
        if(ObjectList.get(0).getDevice().get("tableChair").equals("true")) tableChair.setSelected(true);
        if(ObjectList.get(0).getDevice().get("tv").equals("true")) TV.setSelected(true);
        if(ObjectList.get(0).getDevice().get("wardrobe").equals("true")) Wardrobe.setSelected(true);
        if(ObjectList.get(0).getDevice().get("washMachine").equals("true")) washMachine.setSelected(true);
        if(ObjectList.get(0).getDevice().get("waterHeater").equals("true")) waterHeater.setSelected(true);

        // 格局
        Pattern.setText(ObjectList.get(0).getPattern());

        // 坪數
        Size.setText(ObjectList.get(0).getSize());

        // 最短租期
        RentTime.setText(ObjectList.get(0).getRentTime());

        // 可遷入日
        moveInDate.setText(ObjectList.get(0).getMoveInDate());

        // 預約功能
    }

    public int getCity (int counts){
        switch (counts){
            case 1: return R.array.keelung_search;
            case 2: return R.array.taipei_search;
            case 3: return R.array.newtaipei_search;
            case 4: return R.array.taoyuang_search;
            case 5: return R.array.Hsinchu1_search;
            case 6: return R.array.Hsinchu2_search;
            case 7: return R.array.Miaoli_search;
            case 8: return R.array.Taichung_search;
            case 9: return R.array.Changhua_search;
            case 10: return R.array.Nantou_search;
            case 11: return R.array.Yunlin_search;
            case 12: return R.array.Chiayi1_search;
            case 13: return R.array.Chiayi2_search;
            case 14: return R.array.Tainan_search;
            case 15: return R.array.Kaohsiung_search;
            case 16: return R.array.Pingtung_search;
            case 17: return R.array.Taitang_search;
            case 18: return R.array.Hoalian_search;
            case 19: return R.array.Ilan_search;
            case 20: return R.array.Wuhu_search;
            case 21: return R.array.Jinmen_search;
            case 22: return R.array.Lianjiang_search;
            default:
                return 0;
        }
    }

    private void btnListner() {
        btnElec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnElec.isSelected()){
                    btnElec.setSelected(false);
                }else {
                    btnElec.setSelected(true);
                }
            }
        });

        btnNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (btnNet.isSelected()) btnNet.setSelected(false);
                else btnNet.setSelected(true);
            }
        });

        btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnWater.isSelected()){
                    btnWater.setSelected(false);
                }else {
                    btnWater.setSelected(true);
                }
            }
        });

        btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnManage.isSelected()) btnManage.setSelected(false);
                else btnManage.setSelected(true);
            }
        });





        airCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(airCondition.isSelected()){
                    airCondition.setSelected(false);
                }else {
                    airCondition.setSelected(true);
                }
            }
        });

        Bed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bed.isSelected()){
                    Bed.setSelected(false);
                }else {
                    Bed.setSelected(true);
                }
            }
        });

        Cable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Cable.isSelected()){
                    Cable.setSelected(false);
                }else {
                    Cable.setSelected(true);
                }
            }
        });

        Gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Gas.isSelected()){
                    Gas.setSelected(false);
                }else {
                    Gas.setSelected(true);
                }
            }
        });

        Internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Internet.isSelected()){
                    Internet.setSelected(false);
                }else {
                    Internet.setSelected(true);
                }
            }
        });

        Refrig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Refrig.isSelected()){
                    Refrig.setSelected(false);
                }else {
                    Refrig.setSelected(true);
                }
            }
        });

        Sofa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Sofa.isSelected()){
                    Sofa.setSelected(false);
                }else {
                    Sofa.setSelected(true);
                }
            }
        });

        tableChair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tableChair.isSelected()){
                    tableChair.setSelected(false);
                }else {
                    tableChair.setSelected(true);
                }
            }
        });

        TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TV.isSelected()){
                    TV.setSelected(false);
                }else {
                    TV.setSelected(true);
                }
            }
        });

        Wardrobe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Wardrobe.isSelected()){
                    Wardrobe.setSelected(false);
                }else {
                    Wardrobe.setSelected(true);
                }
            }
        });

        washMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(washMachine.isSelected()){
                    washMachine.setSelected(false);
                }else {
                    washMachine.setSelected(true);
                }
            }
        });

        waterHeater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(waterHeater.isSelected()){
                    waterHeater.setSelected(false);
                }else {
                    waterHeater.setSelected(true);
                }
            }
        });

    }

    public void Save(View v){
        //trim() 之後才能判斷getText是否為空值
        String S_title = Title.getText().toString().trim();
        String S_discript = Descrip.getText().toString().trim();
        String S_moveInDate = moveInDate.getText().toString().trim();
        String S_size = Size.getText().toString().trim();
        String S_pattern = Pattern.getText().toString().trim();
        String S_address = Address.getText().toString().trim();
        String S_maxfloor = maxFloor.getText().toString().trim();
        String S_currentfloor = currentFloor.getText().toString().trim();
        String S_price = Price.getText().toString().trim();
        String S_rentTime = RentTime.getText().toString().trim();


        String S_status = houseStatus.getSelectedItem().toString().trim();
        String S_type = houseType.getSelectedItem().toString().trim();
        String S_county = County.getSelectedItem().toString().trim();
        String S_city = City.getSelectedItem().toString().trim();
        String S_deposit = Deposit.getSelectedItem().toString().trim();


        if(S_title.isEmpty() || S_status.isEmpty() || S_type.isEmpty() || S_county.isEmpty() ||
           S_city.isEmpty() || S_address.isEmpty() || S_maxfloor.isEmpty() || S_currentfloor.isEmpty() ||
           S_price.isEmpty() || S_size.isEmpty() || S_deposit.isEmpty())
        {
            Toast toast = Toast.makeText( this, "請輸入*必填欄位", Toast.LENGTH_LONG);
            //顯示Toast
            toast.show();
        } else{
            myRef.child( "title" ).setValue(S_title);
            myRef.child( "discript" ).setValue(S_discript);
            myRef.child( "status" ).setValue(S_status);
            myRef.child( "type" ).setValue( S_type);
            myRef.child( "county" ).setValue(S_county);
            myRef.child( "city" ).setValue( S_city);
            myRef.child( "address" ).setValue( S_address);
            myRef.child( "maxfloor" ).setValue(S_maxfloor);
            myRef.child( "currentfloor" ).setValue(S_currentfloor);
            myRef.child( "price" ).setValue(S_price);
            myRef.child( "deposit" ).setValue(S_deposit);
            myRef.child( "moveInDate" ).setValue( S_moveInDate);
            myRef.child( "size").setValue(S_size);
            myRef.child( "rentTime").setValue(S_rentTime);

            myRef.child( "updateTime" ).setValue( Time );
            myRef.child( "visible" ).setValue( Visible );
            myRef.child( "pattern" ).setValue(S_pattern);
            myRef.child( "userPhone").setValue(ObjectList.get(0).getUserPhone());

            if(Reservation.isChecked()){
                myRef.child("reservation").setValue(true) ;
            }else
                myRef.child("reservation").setValue(false) ;

            priceIncludeValue();
            deviceValue();

            //將地址 轉成經緯度
//            objlatlng=getLocationFromAddress(S_county, S_city,S_address);
//            myRef.child( "lat" ).setValue(String.valueOf(objlatlng[0]));
//            myRef.child( "lng" ).setValue(String.valueOf(objlatlng[1]));

            //在members那邊新增 房東PO文的物件編號
            DatabaseReference memRef = FirebaseDatabase.getInstance()
                    .getReference("member/"+ObjectList.get(0).getUserPhone()+"/MyRentObj/"+ObjectID);

            memRef.setValue( S_title );

            //相片處理 如果
            if(photoPaths!=null){
                updatePicture();
                // 把圖片數量寫到資料庫
                picNumber=photoPaths.size();
                myRef.child( "picNumber" ).setValue(picNumber);
            }else{
                myRef.removeValue();
                StorageReference mStorageRef = FirebaseStorage.getInstance()
                        .getReference("RentObj/"+ObjectID);
                mStorageRef.delete();
                Toast.makeText( EditObjectActivity.this, "上傳失敗，請再試試!", Toast.LENGTH_LONG).show();
            }

            Intent intent = new Intent(EditObjectActivity.this, DetailActivity.class);
            intent.putExtra("ObjectID", ObjectID);
            startActivity(intent);
            finish();
        }
    }

    private void priceIncludeValue(){
        if(btnManage.isSelected()){
            myRef.child( "priceInclude" ).child( "manage" ).setValue("true");
        }else{
            myRef.child( "priceInclude" ).child( "manage" ).setValue("false");
        }
        if(btnNet.isSelected()){
            myRef.child( "priceInclude" ).child( "Net" ).setValue("true");
        }else{
            myRef.child( "priceInclude" ).child( "Net" ).setValue("false");
        }
        if(btnElec.isSelected()){
            myRef.child( "priceInclude" ).child( "Elec" ).setValue("true");
        }else{
            myRef.child( "priceInclude" ).child( "Elec" ).setValue("false");
        }
        if(btnWater.isSelected()){
            myRef.child( "priceInclude" ).child( "Water" ).setValue("true");
        }else{
            myRef.child( "priceInclude" ).child( "Water" ).setValue("false");
        }
    }

    private void deviceValue(){
        if(washMachine.isSelected()){
            myRef.child( "device" ).child( "washMachine" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "washMachine" ).setValue("false");
        }
        if(Refrig.isSelected()){
            myRef.child( "device" ).child( "refrig" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "refrig" ).setValue("false");
        }
        if(TV.isSelected()){
            myRef.child( "device" ).child( "tv" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "tv" ).setValue("false");
        }
        if(Cable.isSelected()){
            myRef.child( "device" ).child( "cable" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "cable" ).setValue("false");
        }
        if(waterHeater.isSelected()){
            myRef.child( "device" ).child( "waterHeater" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "waterHeater" ).setValue("false");
        }
        if(Internet.isSelected()){
            myRef.child( "device" ).child( "internet" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "internet" ).setValue("false");
        }
        if(Gas.isSelected()){
            myRef.child( "device" ).child( "gus" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "gus" ).setValue("false");
        }
        if(Bed.isSelected()){
            myRef.child( "device" ).child( "bed" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "bed" ).setValue("false");
        }
        if(Wardrobe.isSelected()){
            myRef.child( "device" ).child( "wardrobe" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "wardrobe" ).setValue("false");
        }
        if(Sofa.isSelected()){
            myRef.child( "device" ).child( "sofa" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "sofa" ).setValue("false");
        }
        if(tableChair.isSelected()){
            myRef.child( "device" ).child( "tableChair" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "tableChair" ).setValue("false");
        }
        if(airCondition.isSelected()){
            myRef.child( "device" ).child( "airCondition" ).setValue("true");
        }else{
            myRef.child( "device" ).child( "airCondition" ).setValue("false");
        }
    }

    public double[] getLocationFromAddress(String county,String city,String address) {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        String newAddress =county+city+address;
        List<android.location.Address> addressLocation = null;
        double[] latlng = new double[2];
        try {
            addressLocation = geoCoder.getFromLocationName(newAddress, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        double latitude = addressLocation.get(0).getLatitude();
        double longitude = addressLocation.get(0).getLongitude();
        latlng[0]=latitude;
        latlng[1]=longitude;
//        Log.d("Debug",String.valueOf( latitude ));
//        Log.d("Debug",String.valueOf( longitude ));
        return latlng;
    }

    // 預約功能
    public void reservationClick(View v){
        if(Reservation.isChecked()){
            Intent intent = new Intent( EditObjectActivity.this, SetReservation.class);
            intent.putExtra( "objNum",ObjectID );
            startActivity(intent);
        }
    }

    // 格局
    public void patternOptionClick(View view){
        if(patternOptions!=null){
            patternOptions.show();
        }
    }

    private void patternOptionData() {
        ArrayList<String>[] option2 = new ArrayList[10];
        ArrayList<ArrayList<String>>[] option3 = new ArrayList[10];
        ArrayList<String>[] option3child = new ArrayList[10];
        //選項1
        for (int i = 0; i <10; i++) {
            options1Items.add( String.valueOf(i) + "房");
        }
        for(int i=0;i<option2.length;i++){
            option2[i] = new ArrayList<>();
            for(int j=0;j<10;j++){
                option2[i].add(String.valueOf( j )+"廳");
            }
            options2Items.add(option2[i]);
        }
        for(int i=0;i<option3.length;i++){
            option3[i] = new ArrayList<>();
            for(int j=0;j<option3child.length;j++){
                option3child[j]=new ArrayList<>();
                for(int k=0;k<10;k++){
                    option3child[j].add(String.valueOf( k )+"衛浴");
                }
                option3[i].add(option3child[j]);
            }
            options3Items.add(option3[i]);
        }
    }

    private void patternOptionPicker() {  //格局滾輪
        patternOptions = new OptionsPickerView.Builder( this,new OptionsPickerView.OnOptionsSelectListener() {
            //onOptionSelect是滾輪選好會做的事
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String tx = options1Items.get( options1 ) +
                        options2Items.get( options1 ).get( options2 )+options3Items.get( options1 ).get(options2).get(options3);
                Pattern.setText( tx );
            }
        } ).setTitleText( "請選擇格局" ) // 選擇器標題
                .setContentTextSize( 20 )//設定滾輪文字大小
                .setDividerColor( Color.CYAN )//設定分割線顏色
                .setSelectOptions( 0,0,0)//默認選中值
                .setBgColor( Color.WHITE )
                .setTitleBgColor( Color.WHITE )
                .setTitleColor( Color.LTGRAY )
                .setCancelColor( Color.DKGRAY)
                .setSubmitColor( Color.DKGRAY )
                .setCancelText( "取消" )
                .setSubmitText( "確定" )
                .setTextColorCenter( Color.DKGRAY)
                .setBackgroundId( 0x66000000 ) //設定外部遮罩顏色
                .build();

        patternOptions.setPicker(options1Items, options2Items,options3Items);
    }

    public void ReadPicture(String path, final ImageView v){
        StorageReference islandRef = FirebaseStorage.getInstance().getReference().child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                v.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 想要刪除 沒用到的 ImageView
                ////                ((LinearLayout)v.getParent()).removeView(v); // 目前有問題
                //                // Handle any errors
            }
        });
    }

    // Android 動態權限，EasyPermissions
    @AfterPermissionGranted(RC_PHOTO_PICKER_PERM)
    public void addPicBtnClick() {
        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            // 假如有取得權限 , 執行下列程式
            onPickPhoto();

        } else {
            // 請求權限
            EasyPermissions.requestPermissions(this, getString( R.string.rationale_photo_picker),
                    RC_PHOTO_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //獲取圖片 startActivityForResult    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==9){
            if(resultCode==2){
                setting=data.getIntegerArrayListExtra("check");
            }
        }
    }

    // 取得手機內部相片的 第三方 API
    private void onPickPhoto(){
        FilePickerBuilder.getInstance()
                .setMaxCount( 10 )
                .setSelectedFiles( photoPath )
                .setActivityTheme( R.style.FilePickerTheme )
                .setActivityTitle( "選擇圖片或相機" )
                .enableVideoPicker( false )
                .enableCameraSupport( true )
                .enableSelectAll( false )
                .enableImagePicker( true )
                .showGifs( false )
                .showFolderView( true )
                .setCameraPlaceholder( R.drawable.icon_camera )
                .withOrientation( ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED )
                .pickPhoto( this, CUSTOM_REQUEST_CODE );
    }

    protected void addPicResume(ArrayList<byte[]> imagePaths) {
        ArrayList<byte[]> filePaths = new ArrayList<>();

        if (imagePaths != null) {
            // 選擇圖片後 , 按下右上角的 Done 會取得路徑
            filePaths.addAll(imagePaths);
        }

        RecyclerView recyclerView = findViewById( R.id.EditRecyclerview);
        if (recyclerView != null) {
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL);

            layoutManager.setGapStrategy(
                    StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);

            EditImageAdapter imageAdapter = new EditImageAdapter(this, filePaths);

            recyclerView.setAdapter(imageAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    public void magnifyPhoto(View view , Bitmap z){
        //建立彈出視窗
        final PopupWindow popupWindow = new PopupWindow(this); // getContext = PopupWindow
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                click = true;
            }
        });

        //設定Layout
        LinearLayout linearLayout = new LinearLayout(this); // LinearLayout
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //設定圖片
        ImageView imageView =new ImageView(this); // ImageView
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

        //加入layout中
        linearLayout.addView(imageView);


        if(click){
            popupWindow.setContentView(linearLayout);
            popupWindow.setWidth(1100);
            popupWindow.setHeight(1400);

            popupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 0, 0);
            popupWindow.update();
            imageView.setImageBitmap(z);
            click=false;
        }
    }



    // 上傳圖片
    public void updatePicture(){
        ArrayList<Uri> uriPath = new ArrayList<>(  );
        for(int i =0 ;i<photoPaths.size();i++){
            ContentResolver cr = this.getContentResolver();
            uriPath.add(Uri.parse("file://" + photoPaths.get( i )));
            try {
                //由抽象資料接口轉換圖檔路徑為Bitmap
//                Log.d("picUri",i+":"+photoPaths.get( i ));
                bmp = BitmapFactory.decodeStream(cr.openInputStream(uriPath.get( i )));
            } catch (Exception e) {
                // Log.e("Exception", e.getMessage(),e);
            }
            StorageReference mStorageRef = FirebaseStorage.getInstance()
                    .getReference()
                    .child("RentObj")
                    .child(ObjectID)
                    .child("pic0" + i + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);

            // 建立自訂　Metadata
            StorageMetadata storageMetadata = new StorageMetadata
                    .Builder()
                    .setCustomMetadata("MyKey", "MyValue")
                    .build();
            UploadTask uploadTask = mStorageRef.putFile(uriPath.get( i ), storageMetadata);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( EditObjectActivity.this,"上傳失敗！", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText( EditObjectActivity.this,"上傳成功！", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progressPersentage = (int)((taskSnapshot.getBytesTransferred()*100)/(taskSnapshot.getTotalByteCount()));
                }
            });
        }
    }

    public void onResume(){
        super.onResume();
        moveInDateListen();
    }

    //可遷入日
    private void moveInDateListen(){
        moveInDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datePick = new DatePickerDialog( EditObjectActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                moveInDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePick.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
                datePick.show();
            }
        });
    }


}

