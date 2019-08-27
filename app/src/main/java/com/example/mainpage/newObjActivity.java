package com.example.mainpage;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class newObjActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    ArrayAdapter<String> adapterCounty;
    ArrayAdapter<String> adapterCity;

    private Context context = this;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference( "RentObj" );
    DatabaseReference memberRef = database.getReference( "member" );
    Integer objNum;

    private EditText title;
    private EditText discript;
    private Spinner status;
    private Spinner type;
    private Spinner county;
    private Spinner city;
    private EditText address;
    private EditText price;
    private Spinner spinnerCounty;//縣下拉選單
    private Spinner spinnerCity;//市下拉選單
    private List<List> citySelector;
    private Button btnManage;
    private Button btnNet;
    private Button btnWater;
    private Button btnElec;
    private Spinner deposit;
    private Spinner genderLimit;
    private TextView moveInDate;
    private EditText rentTime;
    private DatePickerDialog datePick;
    private Switch reservation;
    private Button washMachine;
    private Button refrig;
    private Button tv;
    private Button cable;
    private Button waterHeater;
    private Button internet;
    private Button gus;
    private Button bed;
    private Button wardrobe;
    private Button sofa;
    private Button tableChair;
    private Button airCondition;
    double[] objlatlng = new double[2];
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 3;
    private TextView size;
    private String time;
    private boolean visible;
    private TextView pattern;
    private OptionsPickerView patternOptions;  //格局
    private ArrayList<String> options1Items;
    private ArrayList<ArrayList<String>> options2Items;
    private ArrayList<ArrayList<ArrayList<String>>> options3Items;

    private TextView floor;
    private OptionsPickerView floorOptions;
    private ArrayList<String> maxFloorItems;
    private ArrayList<ArrayList<String>> curFloorItems;
    private String S_maxFloor;
    private String S_curFloor;
    private FirebaseAuth mAuth;
    private String phoneNumber;
    private ArrayList<Integer> setting=new ArrayList<>(  );

    //private String pathImage; //選擇照片路徑
    private final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_READ_STORAGE=3;
    private Bitmap bmp; //臨時圖片
    View view;
    private static final int CUSTOM_REQUEST_CODE = 303;
    public static final int RC_PHOTO_PICKER_PERM = 123;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private int picNumber;
    private boolean latlngisOkey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_new_obj );
        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor( R.color.colorMainBlue));
        setUp();
        setupCountyCity();
        btnListner();
        loadFirebase();
       // CheckPermission();

    }
    @Override
    protected void onStart() {
        super.onStart();
        picNumber = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        moveInDateListen();
    }

    public void loadFirebase() {
        //取得最後一筆資料的數字  下一個+1(物件項目有增減都會讀取)
        myRef.orderByKey().limitToLast( 1 ).addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                objNum=0;
                if(dataSnapshot.exists()){objNum = Integer.parseInt( dataSnapshot.getKey() ) + 1;}  //如果資料庫沒東西 objNum從0開始
                Log.d( "objNum", objNum.toString());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        } );
    }

    private void setUp() {
        //取得時間
        Date date = new Date( System.currentTimeMillis() );
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// HH:mm:ss
        // time = simpleDateFormat.format(date);
        mAuth = FirebaseAuth.getInstance();
        phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
//        Log.d( "TAG", phoneNumber );

        findViewById( R.id.addPhotoBtn).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPicBtnClick();
            }
        });

        time = String.valueOf( date.getTime() );
        title = findViewById( R.id.id_titleText );
        discript = findViewById( R.id.id_discript );
        status = findViewById( R.id.id_objStatus );
        type = findViewById( R.id.id_objType );
        county = findViewById( R.id.id_objCounty );
        city = findViewById( R.id.id_objCity );
        address = findViewById( R.id.id_addressText );
        price = findViewById( R.id.id_Price );
        btnManage = findViewById( R.id.id_btnManage );
        btnNet = findViewById( R.id.id_btnNet );
        btnWater = findViewById( R.id.id_btnWater );
        btnElec = findViewById( R.id.id_btnElec );
        deposit = findViewById( R.id.id_deposit );
        genderLimit = findViewById( R.id.id_genderLimit );
        moveInDate = findViewById( R.id.id_moveInDate );
        reservation = findViewById( R.id.id_reservation );
        washMachine = findViewById( R.id.id_washingMachine );
        refrig = findViewById( R.id.id_refrig );
        tv = findViewById( R.id.id_tv );
        cable = findViewById( R.id.id_cable );
        waterHeater = findViewById( R.id.id_waterHeater );
        internet = findViewById( R.id.id_Internet );
        gus = findViewById( R.id.id_gus );
        bed = findViewById( R.id.id_bed );
        wardrobe = findViewById( R.id.id_wardrobe );
        sofa = findViewById( R.id.id_sofa );
        tableChair = findViewById( R.id.id_tableChair );
        airCondition = findViewById( R.id.id_airCondition );
        size = findViewById( R.id.id_size );
        rentTime = findViewById( R.id.id_rentTime );
        visible = true; //房東是否顯示這筆物件
        pattern = findViewById( R.id.id_pattern );
        //格局的選項 房
        options1Items = new ArrayList<>();
        //格局的選項 廳
        options2Items = new ArrayList<>();
        //格局的選項 衛浴
        options3Items = new ArrayList<>();

        //樓層設定
        floor = findViewById( R.id.id_floor );
        maxFloorItems = new ArrayList<>();
        curFloorItems = new ArrayList<>();

        patternOptionData();//初始化格局選單內容
        patternOptionPicker();
        floorOptionData();
        floorOptionPicker();
        //存取照片設定
        latlngisOkey = false;
    }
    private void setupCountyCity(){
        citySelector = new ArrayList<>(  );
        //程式剛啟始時載入第一個下拉選單
        List<String> county = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.county_search)));
        county.remove( 0 );

        adapterCounty = new ArrayAdapter<>( this, android.R.layout.simple_spinner_dropdown_item, county );
        adapterCounty.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerCounty = findViewById( R.id.id_objCounty );
        spinnerCounty.setAdapter( adapterCounty );
        spinnerCounty.setOnItemSelectedListener( selectedListener );

        List<String> keelung = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.keelung_search)));
        keelung.remove(0);
        citySelector.add( keelung );
        List<String> taipei = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.taipei_search)));
        taipei.remove(0);
        citySelector.add( taipei );
        List<String> newtaipei = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.newtaipei_search)));
        newtaipei.remove(0);
        citySelector.add( newtaipei );
        List<String> taoyuang = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.taoyuang_search)));
        taoyuang.remove(0);
        citySelector.add( taoyuang );
        List<String> Hsinchu1 = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Hsinchu1_search)));
        Hsinchu1.remove(0);
        citySelector.add( Hsinchu1 );
        List<String> Hsinchu2 = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Hsinchu2_search)));
        Hsinchu2.remove(0);
        citySelector.add( Hsinchu2 );
        List<String> Miaoli = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Miaoli_search)));
        Miaoli.remove(0);
        citySelector.add( Miaoli );
        List<String> Taichung = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Taichung_search)));
        Taichung.remove(0);
        citySelector.add( Taichung );
        List<String> Changhua = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Changhua_search)));
        Changhua.remove(0);
        citySelector.add( Changhua );
        List<String> Nantou = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Nantou_search)));
        Nantou.remove(0);
        citySelector.add( Nantou );
        List<String> Yunlin = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Yunlin_search)));
        Yunlin.remove(0);
        citySelector.add( Yunlin );
        List<String> Chiayi1 = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Chiayi1_search)));
        Chiayi1.remove(0);
        citySelector.add( Chiayi1 );
        List<String> Chiayi2 = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Chiayi2_search)));
        Chiayi2.remove(0);
        citySelector.add( Chiayi2 );
        List<String> Tainan = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Tainan_search)));
        Tainan.remove(0);
        citySelector.add( Tainan );
        List<String> Kaohsiung = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Kaohsiung_search)));
        Kaohsiung.remove(0);
        citySelector.add( Kaohsiung );
        List<String> Pingtung = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Pingtung_search)));
        Pingtung.remove(0);
        citySelector.add( Pingtung );
        List<String> Taitang = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Taitang_search)));
        Taitang.remove(0);
        citySelector.add( Taitang );
        List<String> Hoalian = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Hoalian_search)));
        Hoalian.remove(0);
        citySelector.add( Hoalian );
        List<String> Ilan = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Ilan_search)));
        Ilan.remove(0);
        citySelector.add( Ilan );
        List<String> Wuhu = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Wuhu_search)));
        Wuhu.remove(0);
        citySelector.add( Wuhu );
        List<String> Jinmen = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Jinmen_search)));
        Jinmen.remove(0);
        citySelector.add( Jinmen );
        List<String> Lianjiang = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.Lianjiang_search)));
        Lianjiang.remove(0);
        citySelector.add( Lianjiang );

        //因為下拉選單第一個為基隆市，所以城市先載入基隆地區選單
        adapterCity = new ArrayAdapter<>( this, android.R.layout.simple_spinner_dropdown_item, citySelector.get( 0 ) );
        adapterCity.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerCity = findViewById( R.id.id_objCity );
        spinnerCity.setAdapter( adapterCity );
    }

    @AfterPermissionGranted(RC_PHOTO_PICKER_PERM)
    public void addPicBtnClick() {
        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickPhoto();
        } else {
            EasyPermissions.requestPermissions(this, getString( R.string.rationale_photo_picker),
                    RC_PHOTO_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
        }
    }
    private void onPickPhoto(){
        FilePickerBuilder.getInstance()
                .setMaxCount( 10 )
                .setSelectedFiles( photoPaths )
                .setActivityTheme( R.style.FilePickerTheme )
                .setActivityTitle( "Please select media" )
                .enableVideoPicker( false )
                .enableCameraSupport( true )
                .showGifs( true )
                .showFolderView( true )
                .enableSelectAll( false )
                .enableImagePicker( true )
                .setCameraPlaceholder( R.drawable.icon_camera )
                .withOrientation( ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED )
                .pickPhoto( this, CUSTOM_REQUEST_CODE );
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
        //打開圖片
        if (resultCode == Activity.RESULT_OK && data != null&&requestCode==CUSTOM_REQUEST_CODE) {
            photoPaths = new ArrayList<>();
            photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
            addPicResume(photoPaths);
        }
    }
    protected void addPicResume(ArrayList<String> imagePaths) {
        ArrayList<String> filePaths = new ArrayList<>();
        if (imagePaths != null) filePaths.addAll(imagePaths);

        RecyclerView recyclerView = findViewById( R.id.recyclerview);
        if (recyclerView != null) {
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(
                    StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);

            ImageAdapter imageAdapter = new ImageAdapter(this,filePaths);

            recyclerView.setAdapter(imageAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }
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
            // 取得 Auth 驗證
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            // 取得　Storage 實體
            FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

            // 取得 /member/%phonenumber%/pic.jpg 的參考
            StorageReference mStorageRef = mFirebaseStorage
                    .getReference()
                    .child("RentObj")
                    .child(objNum.toString())
                    .child(String.valueOf("pic0" + i + ".jpg"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            //
            // 建立自訂　Metadata壓縮檔案
            StorageMetadata storageMetadata = new StorageMetadata
                    .Builder()
                    .setCustomMetadata("MyKey", "MyValue")
                    .build();
            UploadTask uploadTask = mStorageRef.putFile(uriPath.get( i ), storageMetadata);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( newObjActivity.this,"照片出了點問題，再上傳一次看看吧！", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progressPersentage = (int)((taskSnapshot.getBytesTransferred()*100)/(taskSnapshot.getTotalByteCount()));
//                    Log.d("uploading", progressPersentage+"%");
                }
            });
        }
    }
//
    private void moveInDateListen(){  //可遷入日
        moveInDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                datePick = new DatePickerDialog( newObjActivity.this,
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
    //城市選單
    private AdapterView.OnItemSelectedListener selectedListener
            =new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            // TODO Auto-generated method stub
            int pos = spinnerCounty.getSelectedItemPosition();
            adapterCity = new ArrayAdapter<>( context, android.R.layout.simple_spinner_item, citySelector.get( pos ) );
            adapterCity.setDropDownViewResource( R.layout.support_simple_spinner_dropdown_item );

            //載入第二個下拉選單Spinner
            spinnerCity.setAdapter(adapterCity);
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private void btnListner() {
        btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnManage.isSelected()){
                    btnManage.setSelected(false);
                }else {
                    btnManage.setSelected(true);
                }
            }
        });
        btnNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnNet.isSelected()){
                    btnNet.setSelected(false);
                }else {
                    btnNet.setSelected(true);
                }
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
        refrig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(refrig.isSelected()){
                    refrig.setSelected(false);
                }else {
                    refrig.setSelected(true);
                }
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv.isSelected()){
                    tv.setSelected(false);
                }else {
                    tv.setSelected(true);
                }
            }
        });
        cable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cable.isSelected()){
                    cable.setSelected(false);
                }else {
                    cable.setSelected(true);
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
        internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet.isSelected()){
                    internet.setSelected(false);
                }else {
                    internet.setSelected(true);
                }
            }
        });
        gus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gus.isSelected()){
                    gus.setSelected(false);
                }else {
                    gus.setSelected(true);
                }
            }
        });
        bed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bed.isSelected()){
                    bed.setSelected(false);
                }else {
                    bed.setSelected(true);
                }
            }
        });
        wardrobe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wardrobe.isSelected()){
                    wardrobe.setSelected(false);
                }else {
                    wardrobe.setSelected(true);
                }
            }
        });
        sofa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sofa.isSelected()){
                    sofa.setSelected(false);
                }else {
                    sofa.setSelected(true);
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
    }
    public void update(View v){

        //trim() 之後才能判斷getText是否為空值
        String S_title =title.getText().toString().trim();
        String S_discript=discript.getText().toString().trim();
        String S_status=status.getSelectedItem().toString().trim();
        String S_type=type.getSelectedItem().toString().trim();
        String S_county=county.getSelectedItem().toString().trim();
        String S_city=city.getSelectedItem().toString().trim();
        String S_address=address.getText().toString().trim();
        String S_floor=floor.getText().toString().trim();
        String S_price=price.getText().toString().trim();
        String S_deposit=deposit.getSelectedItem().toString().trim();
        String S_genderLimit=genderLimit.getSelectedItem().toString().trim();
        String S_moveInDate = moveInDate.getText().toString().trim();
        String S_size=size.getText().toString().trim();
        String S_pattern=pattern.getText().toString().trim();
        String S_rentTime=rentTime.getText().toString().trim();

        if(S_title.isEmpty()||S_status.isEmpty()||S_type.isEmpty()||S_county.isEmpty()
                ||S_city.isEmpty()||S_address.isEmpty()||S_floor.isEmpty()
                ||S_price.isEmpty()||S_size.isEmpty()||S_deposit.isEmpty())
        {
            Toast toast = Toast.makeText( newObjActivity.this, "請輸入*必填欄位", Toast.LENGTH_LONG);
            //顯示Toast
            toast.show();
        } else{
            //將地址 轉成經緯度s
            try{
                objlatlng=getLocationFromAddress(S_county, S_city,S_address);
                myRef.child( objNum.toString() ).child( "lat" ).setValue(String.valueOf(objlatlng[0]));
                myRef.child( objNum.toString() ).child( "lng" ).setValue(String.valueOf(objlatlng[1]));
                latlngisOkey=true;
                Thread timer = new Thread(  ){
                    @Override
                    public void run(){
                        try{sleep( 1500 );
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                };
                timer.start();
            }catch (NullPointerException e){
                Toast.makeText( newObjActivity.this, "地址出了點問題，再上傳看看吧!", Toast.LENGTH_LONG).show();
                latlngisOkey=false;
            }
            if(latlngisOkey){
                //相片處理 如果
                    myRef.child( objNum.toString()).child( "title" ).setValue(S_title);
                    myRef.child( objNum.toString()).child( "discript" ).setValue(S_discript);
                    myRef.child( objNum.toString()).child( "status" ).setValue(S_status);
                    myRef.child( objNum.toString()).child( "type" ).setValue( S_type);
                    myRef.child( objNum.toString()).child( "county" ).setValue(S_county);
                    myRef.child( objNum.toString()).child( "city" ).setValue( S_city);
                    myRef.child( objNum.toString()).child( "address" ).setValue( S_address);
                    myRef.child( objNum.toString()).child( "maxfloor" ).setValue(S_maxFloor);
                    myRef.child( objNum.toString()).child( "currentfloor" ).setValue(S_curFloor);
                    myRef.child( objNum.toString()).child( "price" ).setValue(S_price);
                    myRef.child( objNum.toString()).child( "deposit" ).setValue(S_deposit);
                    myRef.child( objNum.toString()).child( "genderLimit" ).setValue(S_genderLimit);
                    myRef.child( objNum.toString()).child( "moveInDate" ).setValue( S_moveInDate);
                    myRef.child( objNum.toString()).child("size").setValue(S_size);
                    myRef.child( objNum.toString()).child( "updateTime" ).setValue( time );
                    myRef.child(objNum.toString()).child( "visible" ).setValue( visible);
                    myRef.child(objNum.toString()).child( "pattern" ).setValue(S_pattern);
                    myRef.child( objNum.toString()).child("userPhone").setValue(phoneNumber);
                    myRef.child( objNum.toString() ).child( "rentTime" ).setValue(S_rentTime);
                    myRef.child( objNum.toString() ).child( "setting" ).setValue(setting);

                    if(reservation.isChecked()){
                        myRef.child( objNum.toString() ).child("reservation").setValue(true) ;
                    }else {
                        myRef.child( objNum.toString() ).child( "reservation" ).setValue( false );
                    }
                    priceIncludeValue();
                    deviceValue();

                    //在members那邊新增 房東PO文的物件編號
                    memberRef.child( phoneNumber ).child( "MyRentObj").child( objNum.toString()).setValue( S_title );
                if(photoPaths!=null){
                    updatePicture();
                    picNumber=photoPaths.size();
                    myRef.child( objNum.toString() ).child( "picNumber" ).setValue(picNumber);
                    Toast.makeText( newObjActivity.this, "上傳成功!", Toast.LENGTH_LONG).show();
                    Intent intent =new Intent( newObjActivity.this, MainActivity.class );
                    startActivity(intent);
                }else{
                    myRef.child( objNum.toString() ).removeValue();
                    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                    StorageReference mStorageRef = mFirebaseStorage
                            .getReference()
                            .child("RentObj")
                            .child(objNum.toString());
                    mStorageRef.delete();
                    Toast.makeText( newObjActivity.this, "上傳失敗，請再試試!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    private void priceIncludeValue(){
        if(btnManage.isSelected()){
            myRef.child( objNum.toString() ).child( "priceInclude" ).child( "manage" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "priceInclude" ).child( "manage" ).setValue("false");
        }
        if(btnNet.isSelected()){
            myRef.child( objNum.toString() ).child( "priceInclude" ).child( "Net" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "priceInclude" ).child( "Net" ).setValue("false");
        }
        if(btnElec.isSelected()){
            myRef.child( objNum.toString() ).child( "priceInclude" ).child( "Elec" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "priceInclude" ).child( "Elec" ).setValue("false");
        }
        if(btnWater.isSelected()){
            myRef.child( objNum.toString() ).child( "priceInclude" ).child( "Water" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "priceInclude" ).child( "Water" ).setValue("false");
        }

    }
    public void patternOptionClick(View view){
        if(patternOptions!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(newObjActivity.this.getCurrentFocus().getWindowToken(), 0);
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
                pattern.setText( tx );
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
    public void floorOptionClick(View view){
        if(floorOptions!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(newObjActivity.this.getCurrentFocus().getWindowToken(), 0);

            floorOptions.show();

        }
    }
    private void floorOptionData(){
        ArrayList<String>[] option2 = new ArrayList[108];
        maxFloorItems.add("總樓層");
        for (int i = 6; i >0; i--) {
            maxFloorItems.add( "B"+String.valueOf(i));
        }
        for (int i =1;i<=101;i++){
            maxFloorItems.add(String.valueOf( i )+"F");
        }
        for(int i=0;i<maxFloorItems.size();i++){
            option2[i] = new ArrayList<>();
            option2[i].add( "所在樓層" );
            for(int j=6;j>0;j--){
                option2[i].add( "B"+String.valueOf(j));
            }
            for (int j =1;j<=(i-6);j++){
                option2[i].add(String.valueOf( j )+"F");
            }
            curFloorItems.add(option2[i]);
        }
    }
    private void floorOptionPicker() {  //格局滾輪
        floorOptions = new OptionsPickerView.Builder( this,new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                if(options1!=0&&options2!=0){
                    String tx = maxFloorItems.get( options1 ) +" / "+
                            curFloorItems.get( options1 ).get( options2 );
                    floor.setText( tx );
                    S_maxFloor = maxFloorItems.get(options1);
                    S_curFloor = curFloorItems.get(options1).get( options2 );
                }else{
                    floor.setText( "" );
                    Toast.makeText( newObjActivity.this, "請選擇樓層!!", Toast.LENGTH_LONG).show();
                }
            }
        } ).setTitleText( "請選擇所在樓層" ) // 選擇器標題
                .setContentTextSize( 20)//設定滾輪文字大小
                .setDividerColor( Color.CYAN )//設定分割線顏色
                .setSelectOptions( 0,0)//默認選中值
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
        floorOptions.setPicker(maxFloorItems,curFloorItems,null);
    }

    private void deviceValue(){
        if(washMachine.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "washMachine" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "washMachine" ).setValue("false");
        }
        if(refrig.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "refrig" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "refrig" ).setValue("false");
        }
        if(tv.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "tv" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "tv" ).setValue("false");
        }
        if(cable.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "cable" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "cable" ).setValue("false");
        }
        if(waterHeater.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "waterHeater" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "waterHeater" ).setValue("false");
        }
        if(internet.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "internet" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "internet" ).setValue("false");
        }
        if(gus.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "gus" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "gus" ).setValue("false");
        }
        if(bed.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "bed" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "bed" ).setValue("false");
        }
        if(wardrobe.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "wardrobe" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "wardrobe" ).setValue("false");
        }
        if(sofa.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "sofa" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "sofa" ).setValue("false");
        }
        if(tableChair.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "tableChair" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "tableChair" ).setValue("false");
        }
        if(airCondition.isSelected()){
            myRef.child( objNum.toString() ).child( "device" ).child( "airCondition" ).setValue("true");
        }else{
            myRef.child( objNum.toString() ).child( "device" ).child( "airCondition" ).setValue("false");
        }
    }
    public void reservationClick(View v){
        if(reservation.isChecked()){
            Intent intent = new Intent( newObjActivity.this, SetReservation.class);
            intent.putExtra( "objNum",objNum );
            startActivityForResult(intent,9);
        }
    }
    public double[] getLocationFromAddress(String county,String city,String address) {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        String newAddress =county+city+address;
        List<Address> addressLocation = null;
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

    public void returnProfile(View v){
        this.finish();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}