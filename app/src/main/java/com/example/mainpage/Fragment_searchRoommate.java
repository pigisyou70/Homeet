package com.example.mainpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Fragment_searchRoommate extends Fragment implements FragmentBackHandler{
    List<String> arrGender;
    String [] arrprice;
    String [] arrhouse;
    String[][] chooseDist;
    String[] arrCounty ;
    String[] arrKeelongCity;
    String[] arrTaipeiCity;
    String[] arrNewTaipeiCity ;
    String[] arrTaoyuanCity;
    String[] arrHsinchu1;
    String[] arrHsinchu2;
    String[] arrMiaoli;
    String[] arrTaichung;
    String[] arrChanghua;
    String[] arrNantou;
    String[] arrYunlin;
    String[] arrChiayi1;
    String[] arrChiayi2;
    String[] arrTainan;
    String[] arrKaohsiung;
    String[] arrPingtung;
    String[] arrTaitang;
    String[] arrHoalian;
    String[] arrIlan;
    String[] arrWuhu;
    String[] arrJinmen;
    String[] arrLianjiang;

    LinearLayout myList;
    Button Mapbt;
    Button Listbt;


    ImageButton Searchbt;
    EditText Search;
    String keyword;

    Spinner countySpinner;
    Spinner priceSpinner;
    Spinner genderSpinner;
    Spinner houseSpinner;
    Spinner citySpinner;
    Spinner sortingSpinner;

    String countySelect;
    String citySelect;
    String houseSelect;
    String genderSelect;
    int minpriceSelect;
    int maxpriceSelect;

    int sortingType;
    int nameGetCount1;
    int nameGetCount2;
    boolean click;
    boolean secondtime1;
    boolean secondtime2;
    boolean secondtime3;
    boolean secondtime4;
    boolean secondtime5;

    ArrayList<MySorting> sortingRoommate;
    FirebaseAuth mAuth;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    private OnFragmentInteractionListener mListener;

    public Fragment_searchRoommate() {}
    // TODO: Rename and change types and number of parameters
    public static Fragment_search newInstance(String param1, String param2) {
        Fragment_search fragment = new Fragment_search();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }
    @Override
    public void onPause() {

        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate( R.layout.fragment_search_roommate, container, false );

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void Init(){
        countySelect="***";
        genderSelect="***";
        citySelect="***";
        houseSelect="***";

        click=true;
        mAuth = FirebaseAuth.getInstance();
        sortingRoommate=new ArrayList<>();
        myList=getView().findViewById(R.id.SearchResult);
        Mapbt=getView().findViewById(R.id.MapButton);
        Listbt=getView().findViewById(R.id.ListButton);
        Searchbt=getView().findViewById(R.id.SearchButton);
        Search=getView().findViewById(R.id.SearchView);
        citySelect="nothing";

        SpinnerSetting();

        Listbt.setEnabled(false);


        setButtonClick();


        keyword=Search.getText().toString();

        Search2(keyword);
    }

    public void setButtonClick(){
        Listbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                Mapbt.setEnabled(true);
            }
        });
        Mapbt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mapbt.setEnabled( false );
                Listbt.setEnabled( true );
                Fragment_mapRoommate fragment_map = new Fragment_mapRoommate();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.id_frame, fragment_map);
                fragmentTransaction.commit();
            }
        } );

        Searchbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                keyword=Search.getText().toString();

                Search2(keyword);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });
    }

    public void SpinnerSetting(){

        //下拉式選單設定
        arrGender=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.spn_list)));
        arrGender.add(0,"不限");
        arrprice=getResources().getStringArray(R.array.price_search);
        arrhouse=getResources().getStringArray(R.array.spn_housetypelist);
        arrCounty=getResources().getStringArray(R.array.county_search);
        arrKeelongCity=getResources().getStringArray(R.array.keelung_search);
        arrTaipeiCity=getResources().getStringArray(R.array.taipei_search);
        arrNewTaipeiCity=getResources().getStringArray(R.array.newtaipei_search);
        arrTaoyuanCity=getResources().getStringArray(R.array.taoyuang_search);
        arrHsinchu1=getResources().getStringArray(R.array.Hsinchu1_search);
        arrHsinchu2=getResources().getStringArray(R.array.Hsinchu2_search);
        arrMiaoli=getResources().getStringArray(R.array.Miaoli_search);
        arrTaichung=getResources().getStringArray(R.array.Taichung_search);
        arrChanghua=getResources().getStringArray(R.array.Changhua_search);
        arrNantou=getResources().getStringArray(R.array.Nantou_search);
        arrYunlin=getResources().getStringArray(R.array.Yunlin_search);
        arrChiayi1=getResources().getStringArray(R.array.Chiayi1_search);
        arrChiayi2=getResources().getStringArray(R.array.Chiayi2_search);
        arrTainan=getResources().getStringArray(R.array.Tainan_search);
        arrKaohsiung=getResources().getStringArray(R.array.Kaohsiung_search);
        arrPingtung=getResources().getStringArray(R.array.Pingtung_search);
        arrTaitang=getResources().getStringArray(R.array.Taitang_search);
        arrHoalian=getResources().getStringArray(R.array.Hoalian_search);
        arrIlan=getResources().getStringArray(R.array.Ilan_search);
        arrWuhu=getResources().getStringArray(R.array.Wuhu_search);
        arrJinmen=getResources().getStringArray(R.array.Jinmen_search);
        arrLianjiang=getResources().getStringArray(R.array.Lianjiang_search);

        countySpinner=getView().findViewById(R.id.countySpinner);
        priceSpinner=getView().findViewById(R.id.priceSpinner);
        genderSpinner=getView().findViewById(R.id.genderSpinner);
        houseSpinner=getView().findViewById(R.id.houseSpinner);
        citySpinner=getView().findViewById(R.id.dictSpinner);
        sortingSpinner=getView().findViewById(R.id.sortingSpinner);
        chooseDist=new String[][]{arrKeelongCity,arrTaipeiCity,arrNewTaipeiCity,arrTaoyuanCity,arrHsinchu1,arrHsinchu2,arrMiaoli,arrTaichung,arrChanghua,arrNantou,arrYunlin,arrChiayi1,arrChiayi2,arrTainan,arrKaohsiung,arrPingtung,arrTaitang,arrHoalian,arrIlan,arrWuhu,arrJinmen,arrLianjiang};

        //縣市;
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, arrCounty){
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                if(position==0)
                    tv.setText("縣市");
                return tv;
            }
        };
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpinner.setAdapter(adapter1);

        //性別
        ArrayAdapter<String> adapter2= new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, arrGender){
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                if(position==0)
                    tv.setText("性別");
                return tv;
            }
        };
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter2);

        //合租類型
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, arrhouse){
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                if(position==0)
                    tv.setText("合租類型");
                return tv;
            }
        };
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        houseSpinner.setAdapter(adapter4);

        //租金
        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, arrprice){
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                if(position==0)
                    tv.setText("租金");
                return tv;
            }
        };
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(adapter5);

        countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                countySelect=countySpinner.getItemAtPosition(i).toString();
                //區域
                if(i>0){
                    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, chooseDist[i-1]){
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView tv = (TextView) super.getView(position, convertView, parent);
                            if(position==0)
                                tv.setText("區域");

                            return tv;
                        }
                    };
                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(adapter3);
                    citySpinner.setEnabled(true);
                    citySpinner.setClickable(true);
                }else{
                    citySpinner.setEnabled(false);
                    citySpinner.setClickable(false);
                    citySpinner.setAdapter(null);
                }
                if(secondtime1){
                    Search2(keyword);
                }
                else
                    secondtime1=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                citySelect=citySpinner.getItemAtPosition(i).toString();
                if(secondtime2){
                    Search2(keyword);
                }
                else
                    secondtime2=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderSelect=genderSpinner.getItemAtPosition(i).toString();
                if(secondtime3){
                    Search2(keyword);
                }
                else
                    secondtime3=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        houseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                houseSelect=houseSpinner.getItemAtPosition(i).toString();
                if(secondtime4){
                    Search2(keyword);
                }
                else
                    secondtime4=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    minpriceSelect=0;
                    maxpriceSelect=0;
                }else if(i==5){
                    minpriceSelect=(i-1)*5000;
                    maxpriceSelect=-1;//無限大以-1表示

                }else{
                    maxpriceSelect=i*5000;
                    minpriceSelect=(i-1)*5000;
                }
                if(secondtime5){
                    Search2(keyword);
                }
                else
                    secondtime5=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(myList.getChildCount()!=0){
                    sortingType=i;
                    Show2();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    public void Search2(final String s){

        myRef=database.getReference("FindRoommate");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sortingRoommate.clear();
                //不管有無房
                for(DataSnapshot ds:dataSnapshot.getChildren()){



                    //所有的節點
                    for(DataSnapshot dss:ds.getChildren()){
                        int requirement=0;

                        String name=null;
                        String title=null;
                        String county=null;
                        String city=null;
                        String price=null;
                        String discript=null;
                        int picNumber=0;

                        long times=0;

                        //要印出的欄位
                        for(DataSnapshot dsss:dss.getChildren()){
                            //目前欄位名稱存給key
                            String key=dsss.getKey();
                            if(key.equals("title")){
                                title=dsss.getValue(String.class);

                                if(title.contains(s)||s.equals("")){
                                    requirement++;
                                }
                            }

                            if(key.equals("discript")){
                                discript=dsss.getValue(String.class);
                            }

                            if(key.equals("county")){
                                county=dsss.getValue(String.class);
                                if(county.equals(countySelect)||countySelect.equals("不限")){
                                    requirement++;
                                }
                            }
                            if(key.equals("city")){
                                city=dsss.getValue(String.class);
                                if(city.equals(citySelect)||
                                        citySelect.equals("不限")||
                                        countySelect.equals("不限")){
                                    requirement++;
                                }
                            }



                            if(key.equals("price")){
                                price=dsss.getValue(String.class);

                                int keys=Integer.parseInt(price);
                                //沒有限制
                                if(maxpriceSelect==0){
                                    requirement++;
                                }
                                //超過2萬
                                else if(maxpriceSelect==-1){
                                    if(minpriceSelect<=keys)
                                        requirement++;
                                }
                                //一般範圍限定
                                else if(minpriceSelect<=keys && keys <maxpriceSelect){
                                    requirement++;
                                }
                            }
                            if(key.equals("userPhone")){
                                name=dsss.getValue(String.class);
                            }
                            if(key.equals("updateTime")){
                                times=Long.parseLong(dsss.getValue(String.class));
                            }
                            if(key.equals("picNumber")){
                                picNumber=dsss.getValue(Integer.class);
                            }
                            if(key.equals("visible")){
                                if(dsss.getValue(Boolean.class)){
                                    requirement++;
                                }
                                if(ds.getKey().equals("HaveHouse")){
                                    if(houseSelect.equals("有房")||houseSelect.equals("不限")){
                                        requirement++;
                                    }
                                }else if(ds.getKey().equals("NoHouse")){
                                    if(houseSelect.equals("無房")||houseSelect.equals("不限")){
                                        requirement++;
                                    }
                                }
                            }
                        }
                        //符合5個條件就加入arraylist中
                        if(requirement==6){
                            String HNum=dss.getKey();
                            MySorting mysorting=new MySorting(name,title,county,city,price,times,HNum,discript,picNumber);
                            sortingRoommate.add(mysorting);
                        }
                    }
                }
                Show2();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    public void Show2(){
        if(sortingRoommate.size()>0){
            if(myList !=null)
                myList.removeAllViews();
            ArrayList<MySorting> sortingRoommate2=new ArrayList<>();
            for(MySorting searchObj:sortingRoommate){
                nameGetCount1++;

                ChangeName(sortingRoommate2,searchObj.name,searchObj.title,searchObj.county,searchObj.city,searchObj.price+"",null,searchObj.times,searchObj.HNum,searchObj.discript,searchObj.picNumber);
            }
        }
        else {
            if(myList !=null)
                myList.removeAllViews();
            TextView nodataView=new TextView(getContext());
            nodataView.setText("(查無搜尋結果)");
            myList.addView(nodataView);
        }
    }


    public void sorting(ArrayList<MySorting> sortingRoommate2){
        switch (sortingType){
            case 0:
                //排時間
                for(int i=0;i<sortingRoommate2.size()-1;i++){
                    for(int j=i+1;j<sortingRoommate2.size();j++){
                        if(sortingRoommate2.get(i).times<sortingRoommate2.get(j).times){
                            Collections.swap(sortingRoommate2,i,j);
                        }
                    }
                }
                break;
            case 1:
                //price高
                for(int i=0;i<sortingRoommate2.size()-1;i++){
                    for(int j=i+1;j<sortingRoommate2.size();j++){
                        if(sortingRoommate2.get(i).price<sortingRoommate2.get(j).price){
                            Collections.swap(sortingRoommate2,i,j);
                        }
                    }
                }
                break;
            case 2:
                //price低
                for(int i=0;i<sortingRoommate2.size()-1;i++){
                    for(int j=i+1;j<sortingRoommate2.size();j++){
                        if(sortingRoommate2.get(i).price>sortingRoommate2.get(j).price){
                            Collections.swap(sortingRoommate2,i,j);
                        }
                    }
                }
                break;
        }
    }

    private String addunit(long postTime) {
        String timeFormate=null;
        Date d=new Date();
        long now = d.getTime( );
        long interval;
        boolean toolong=false;
        String unit="分鐘前";

        interval=(now-postTime)/1000/60;//毫秒/1000   換算分鐘

        if(interval>59){
            interval/=60;//換算小時
            unit="小時前";
            if(interval>23){
                interval/=24;//換算天數
                unit="天前";
                if(interval>29){
                    //太久就放出他的po文時間
                    Calendar c=Calendar.getInstance();
                    c.setTimeInMillis(postTime);
                    timeFormate=c.get(Calendar.YEAR)+"年"+(c.get(Calendar.MONTH)+1)+"月"+(c.get(Calendar.DATE)+1)+"日";
                    toolong=true;
                }
            }
        }
        if(!toolong)
            timeFormate=interval+unit;
        return timeFormate;
    }

    public void ReadPicture(String path, final ImageView v){
        // 取得　Storage 實體
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                v.setImageBitmap(bitmap);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //((LinearLayout)v.getParent()).removeView(v);
                //v.setImageResource(R.drawable.posthouse);
            }
        });
    }

    public void ChangeName(final ArrayList<MySorting> sortingRoommate2,final String name, final String title, final String county, final String city, final String price, String size, final long time, final String HNum,final String discript,final int picNumber){

        myRef=database.getReference("member/"+name);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nickName="***";
                String gender="***";
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().equals("NickName")){
                        nickName=ds.getValue(String.class);
                    }
                    if(ds.getKey().equals("Gender")){
                        gender=ds.getValue(String.class);
                    }
                }
                if(genderSelect.equals(gender)||genderSelect.equals("不限")) {
                    MySorting a = new MySorting(nickName, name, title, county, city, price, "0", time, HNum,gender,discript,picNumber);
                    sortingRoommate2.add(a);
                }

                nameGetCount2++;
                //全部執行完後
                if(nameGetCount1==nameGetCount2){
                    if(sortingRoommate2.size()==0){
                        TextView nodataView=new TextView(getContext());
                        nodataView.setText("(查無搜尋結果)");
                        myList.addView(nodataView);
                        return;
                    }else{
                        sorting(sortingRoommate2);
                        for(MySorting mySorting : sortingRoommate2){

                            layout(mySorting.Gender,mySorting.NickName,mySorting.name,mySorting.title,mySorting.county,mySorting.city,mySorting.price+"","0",mySorting.times,mySorting.HNum,mySorting.discript,mySorting.picNumber);
                        }
                    }
                }



            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    public void layout(String gender,final String nickname,final String name, String title, String county, String city, String price, String size, long time, final String HNum,String discript,int picNumber){

        //初始設定
        if(getContext() == null){
            return;
        }
        final LinearLayout SearchObj=new LinearLayout(getContext());
        LinearLayout.LayoutParams objLayout=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        objLayout.setMargins(0,0,0,50);
        SearchObj.setOrientation(LinearLayout.VERTICAL);
        SearchObj.setLayoutParams(objLayout);
        SearchObj.setPadding( 30,30,30,30 );
        SearchObj.setBackground( getResources().getDrawable(R.drawable.searchobj_layout  ) );

        //定義第一欄
        LinearLayout.LayoutParams RowLayout=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams firstLayout=new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,0.85f));
        firstLayout.gravity=Gravity.CENTER;
        RowLayout.setMargins(0,0,0,25);
        LinearLayout firstRow=new LinearLayout(getContext());
        firstRow.setOrientation(LinearLayout.HORIZONTAL);
        firstRow.setLayoutParams(RowLayout);

        //頭像

        final de.hdodenhof.circleimageview.CircleImageView profile=new de.hdodenhof.circleimageview.CircleImageView(getContext());
        LinearLayout.LayoutParams profileLayout=new LinearLayout.LayoutParams(150 , 150 );
        profileLayout.gravity=Gravity.CENTER;
        profileLayout.setMargins(0,0,20,0);
        profile.setImageResource(R.drawable.icon_member);
        profile.setLayoutParams(profileLayout);
        profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap z=((BitmapDrawable)profile.getDrawable()).getBitmap();
                magnifyPhoto(view,name,nickname,z); // view 是圓形頭像 , name 是該人+886手機號碼 , nickname 是該人暱稱 , z 是圖片 byte[]

            }
        });
        ReadPicture("member/"+name+"/pic.jpg", profile);
        firstRow.addView(profile);

        //名字+時間欄
        LinearLayout TimeName=new LinearLayout(getContext());
        TimeName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        TimeName.setOrientation(LinearLayout.VERTICAL);
        firstRow.addView(TimeName);

        //名字+性別
        LinearLayout gendername=new LinearLayout(getContext());

        ImageView genderIcon=new ImageView(getContext());
        LinearLayout.LayoutParams bb=new LinearLayout.LayoutParams(50 ,50);
        bb.setMargins(15,0,0,0);
        bb.gravity=Gravity.CENTER;
        genderIcon.setLayoutParams(bb);
        if(gender.equals("男性")){
            genderIcon.setImageResource(R.mipmap.icon_man);
        }else
            genderIcon.setImageResource(R.mipmap.icon_women);


        TextView nameView=new TextView(getContext());
        nameView.setText(nickname);
        nameView.setTextSize(16);

        gendername.addView(nameView);
        gendername.addView(genderIcon);
        TimeName.addView(gendername);

        //時間
        TextView timeView=new TextView(getContext());
        timeView.setText(addunit(time));
        timeView.setTextSize(12);
        TimeName.addView(timeView);


        //價錢
        TextView priceView=new TextView(getContext());
        priceView.setText(price+"/月");
        priceView.setGravity(Gravity.RIGHT);
        priceView.setLayoutParams(firstLayout);
        firstRow.addView(priceView);

        SearchObj.addView(firstRow);

        //定義標題列
        LinearLayout titleline=new LinearLayout(getContext());
        LinearLayout.LayoutParams cc=new LinearLayout.LayoutParams(1000, 125);
        cc.setMargins(20,0,0,0);
        cc.gravity=Gravity.CENTER;
        TextView housetag=new TextView(getContext());
        String tag=HNum.substring(0,1);

        if(tag.equals("H")){
            housetag.setText("有房");
            housetag.setBackgroundColor(Color.argb(50,175,175,250));
        }else{
            housetag.setText("無房");
            housetag.setBackgroundColor(Color.argb(50,250,175,175));
        }

        housetag.setTextSize(14);
        housetag.setPadding(20,10 ,20,10);
        LinearLayout.LayoutParams ab=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ab.gravity=Gravity.CENTER_VERTICAL;
        housetag.setLayoutParams(ab);

        titleline.addView(housetag);

        TextView titleView=new TextView(getContext());
        titleView.setText(title);
        titleView.setTextSize(22);
        titleView.setSingleLine(true);
        titleView.setMaxLines(1);
        timeView.setTextColor(Color.argb(255,0,0,0));
        //titleView.setGravity(Gravity.CENTER);
        titleView.setLayoutParams(cc);
        titleline.addView(titleView);

        SearchObj.addView(titleline);

        LinearLayout second2=new LinearLayout(getContext());
        LinearLayout.LayoutParams disciptlayout=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        disciptlayout.setMargins(35,10,35,10);
        TextView discriptView=new TextView(getContext());
        discriptView.setLayoutParams(disciptlayout);
        discriptView.setText(discript);
        second2.addView(discriptView);

        SearchObj.addView(second2);



        //定義第二欄
        LinearLayout secondRow=new LinearLayout(getContext());
        secondRow.setOrientation(LinearLayout.HORIZONTAL);
        secondRow.setLayoutParams(RowLayout);


        LinearLayout.LayoutParams imgLayout=new LinearLayout.LayoutParams(415,415,1.0f);

        if(picNumber>3){
            picNumber=3;
        }
        if(tag.equals("H")){
            for(int i =0;i<picNumber;i++){
                ImageView photo=new ImageView(getContext());
                photo.setImageResource(R.drawable.icon_house);
                ReadPicture("FindRoommate/HaveHouse/"+HNum+"/pic0"+i+".jpg", photo);
                photo.setLayoutParams(imgLayout);
                photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                secondRow.addView(photo);
            }
        }
        SearchObj.addView(secondRow);

        //定義第三欄
        LinearLayout thirdRow=new LinearLayout(getContext());
        thirdRow.setOrientation(LinearLayout.HORIZONTAL);
        thirdRow.setLayoutParams(RowLayout);

        //縣市
        TextView dictView=new TextView(getContext());
        dictView.setText(county);
        thirdRow.addView(dictView);

        //地區
        TextView cityView=new TextView(getContext());
        cityView.setText(city);
        thirdRow.addView(cityView);



        SearchObj.addView(thirdRow);

        SearchObj.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if  (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    SearchObj.setElevation( 15 );

                    return true ;
                }
                else if  (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {

                    SearchObj.setElevation(0);

                    if(mAuth.getCurrentUser()!=null){
                        Intent intent = new Intent(getContext(),ShowMemberActivity.class);
                        intent.putExtra("MemberID", name);
                        intent.putExtra("ObjectID", HNum);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getContext(), "請先登入會員", Toast.LENGTH_SHORT).show();
                    }


//                    Intent intent = new Intent(getContext(),DetailActivity.class);
//                    intent.putExtra("ObjectID", HNum);
//                    startActivity(intent);

                    return true ;
                }
                SearchObj.setElevation( 0 );
                return  false;
            }
        });

        //加入myList中
        myList.addView(SearchObj);
    }

    public void magnifyPhoto(View view,String name,String nickname,Bitmap z){
        //建立彈出視窗
        final PopupWindow big=new PopupWindow(getContext()); // getContext = PopupWindow
        big.setOutsideTouchable(true);
        big.setFocusable(true);
        big.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                click=true;
            }
        });



        //設定Layout
        LinearLayout whole=new LinearLayout(getContext()); // LinearLayout
        whole.setOrientation(LinearLayout.VERTICAL);

        //設定title
        LinearLayout titleLine=new LinearLayout(getContext()); // LinearLayout
        titleLine.setOrientation(LinearLayout.HORIZONTAL);
        titleLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,150));
        titleLine.setBackgroundColor(Color.GRAY);
        TextView title=new TextView(getContext()); // TextView
        LinearLayout.LayoutParams aa=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,100);
        aa.gravity=Gravity.CENTER;
        aa.setMargins(0,25,0,25);
        title.setGravity(Gravity.CENTER);
        title.setText(nickname);
        title.setTextSize(24);
        title.setTextColor(Color.rgb(255,255,255));
        title.setLayoutParams(aa);
        titleLine.addView(title);

        //設定圖片
        ImageView tv =new ImageView(getContext()); // ImageView
        tv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

        //加入layout中
        whole.addView(titleLine);
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
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public boolean onBackPressed(){
        return BackHandlerHelper.handleBackPress(this);
    }
}


