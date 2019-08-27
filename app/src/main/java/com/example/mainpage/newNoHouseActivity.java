package com.example.mainpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class newNoHouseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    ArrayAdapter<String> adapterCounty;
    ArrayAdapter<String> adapterCity;

    private Context context = this;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference( "FindRoommate").child( "NoHouse" );
    DatabaseReference memberRef = database.getReference( "member" );
    Integer objNum;
    private String TAG = "DEBUG:";

    private EditText title;
    private EditText discript;
    private Spinner county;
    private Spinner city;
    private EditText price;
    private Spinner spinnerCounty;//縣下拉選單
    private Spinner spinnerCity;//市下拉選單
    private Spinner genderLimit;
    private List<List> citySelector;
    double[] objlatlng = new double[2];

    private String time;
    private boolean visible;
    private String gender;

    private FirebaseAuth mAuth;
    private String phoneNumber;
    private ArrayList<Integer> setting=new ArrayList<>(  );


    private Bitmap bmp; //臨時圖片
    View view;
    private int picNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_new_no_house );
        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor( R.color.colorMainBlue));
        setUp();
        setupCountyCity();
        loadFirebase();
    }
    @Override
    protected void onStart() {
        super.onStart();
        picNumber=0;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    public void loadFirebase() {
        //取得最後一筆資料的數字  下一個+1(物件項目有增減都會讀取)
        myRef.orderByKey().limitToLast( 1 ).addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.exists()){  //如果資料庫沒東西 objNum從0開始
                    objNum=0;
                }else {
                    objNum = Integer.parseInt( String.valueOf(dataSnapshot.child( "objNum" ).getValue())) + 1;
                }
//                Log.d( "objNum", objNum.toString());
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
        Log.d( "TAG", phoneNumber );
        time = String.valueOf( date.getTime() );
        title = findViewById( R.id.id_titleText );
        discript = findViewById( R.id.id_discript );
        county = findViewById( R.id.id_objCounty );
        city = findViewById( R.id.id_objCity );
        price = findViewById( R.id.id_Price );
        genderLimit=findViewById( R.id.id_genderLimit );
        visible = true; //房東是否顯示這筆物件
        final DatabaseReference memberRef = database.getReference("member").child( phoneNumber);
        memberRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gender =dataSnapshot.child( "Gender" ).getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
    private void setupCountyCity(){
        citySelector = new ArrayList<>();
        //程式剛啟始時載入第一個下拉選單
        List<String> county = new ArrayList<String>(Arrays.asList(getResources().getStringArray( R.array.county_search)));
        county.remove( 0 );

        adapterCounty = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, county );
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
        adapterCity = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, citySelector.get( 0 ) );
        adapterCity.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerCity = findViewById( R.id.id_objCity );
        spinnerCity.setAdapter( adapterCity );
    }
    //城市選單
    private AdapterView.OnItemSelectedListener selectedListener
            =new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            // TODO Auto-generated method stub
            int pos = spinnerCounty.getSelectedItemPosition();
            adapterCity = new ArrayAdapter<>( context, android.R.layout.simple_spinner_item, citySelector.get( pos ) );
            //載入第二個下拉選單Spinner
            spinnerCity.setAdapter(adapterCity);
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };
    public void update(View v){
        //trim() 之後才能判斷getText是否為空值
        String S_title =title.getText().toString().trim();
        String S_discript=discript.getText().toString().trim();
        String S_county=county.getSelectedItem().toString().trim();
        String S_city=city.getSelectedItem().toString().trim();
        String S_price=price.getText().toString().trim();
        String S_genderLimit=genderLimit.getSelectedItem().toString().trim();

        if(S_title.isEmpty()||S_county.isEmpty()
                ||S_city.isEmpty()||S_price.isEmpty())
        {
            Toast toast = Toast.makeText( newNoHouseActivity.this, "請輸入*必填欄位", Toast.LENGTH_LONG);
            //顯示Toast
            toast.show();
        } else{
            myRef.child( "N"+objNum.toString()).child( "title" ).setValue(S_title);
            myRef.child( "N"+objNum.toString()).child( "discript" ).setValue(S_discript);
            myRef.child( "N"+objNum.toString()).child( "county" ).setValue(S_county);
            myRef.child( "N"+objNum.toString()).child( "city" ).setValue( S_city);
            myRef.child( "N"+objNum.toString()).child( "price" ).setValue(S_price);
            myRef.child( "N"+objNum.toString()).child( "updateTime" ).setValue( time );
            myRef.child( "N"+objNum.toString()).child( "visible" ).setValue( visible);
            myRef.child( "N"+objNum.toString()).child("userPhone").setValue(phoneNumber);
            myRef.child( "N"+objNum.toString() ).child( "setting" ).setValue(setting);
            myRef.child( "N"+objNum.toString() ).child( "picNumber" ).setValue(picNumber);
            myRef.child( "N"+objNum.toString() ).child( "objNum" ).setValue(objNum);
            myRef.child( "N"+objNum.toString() ).child( "gender" ).setValue(gender);
            myRef.child( "N"+objNum.toString()).child( "genderLimit" ).setValue(S_genderLimit);

            //在members那邊新增 找室友PO文的物件編號
            memberRef.child( phoneNumber ).child( "FindRoommate/NoHouse").child( "N"+objNum.toString()).setValue( S_title );
                Toast.makeText( newNoHouseActivity.this, "上傳成功!!", Toast.LENGTH_LONG).show();
                Intent intent =new Intent( newNoHouseActivity.this, MainActivity.class );
                startActivity(intent);
        }
    }

    // 回到檢視會員頁面
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
