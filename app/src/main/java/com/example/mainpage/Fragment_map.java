package com.example.mainpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_map#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_map extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private GoogleMap mMap;
    Object lat,lng,title,price,county,city,type,size ; //取得資料

    List<String> arrType;
    String [] arrprice;
    String [] arrsize;
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

    Spinner countySpinner;
    Spinner priceSpinner;
    Spinner typeSpinner;
    Spinner sizeSpinner;
    Spinner citySpinner;
    Spinner sortingSpinner;

    LinearLayout myList;
    Button Mapbt;
    Button Listbt;
    ImageButton Searchbt;
    EditText Search;
    String keyword;
    Marker[] markers;
    String countySelect;
    String citySelect;
    int minsizeSelect;
    int maxsizeSelect;
    int minpriceSelect;
    int maxpriceSelect;
    String typeSelect;

    boolean click;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    private static final int REQUEST_LOCATION=2;

    public Fragment_map() {

    }

    public static Fragment_map newInstance(String param1, String param2) {
        Fragment_map fragment = new Fragment_map();
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
        View view= inflater.inflate( R.layout.fragment_map, container, false );
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        Init();
        InitFragment();
    }

    public void InitFragment(){
        //在fragment 中使用fragment 要用support-------
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.mapview);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap=googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                if (getActivity().checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission
                        ( Manifest.permission.ACCESS_COARSE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
//                    fragment內用 requestPermission
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }else{
                    setupMap();
                    gpsbtnClick();
                    cameraMovetoLocation();
                }
                mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra("ObjectID", String.valueOf(marker.getTag()));
                        startActivity( intent );
                        return true;
                    }
                } );

            }
        });
    }
    public void Init(){
        countySelect="***";
        typeSelect="***";
        citySelect="***";
        keyword="";
        click=true;
        Mapbt=getView().findViewById(R.id.MapButton);
        Listbt=getView().findViewById(R.id.ListButton);
        Searchbt=getView().findViewById(R.id.SearchButton);
        Search=getView().findViewById(R.id.SearchView);

        citySelect="nothing";
        SpinnerSetting();

        Listbt.setEnabled(true);
        Mapbt.setEnabled(false);
        Listbt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mapbt.setEnabled( true );
                Listbt.setEnabled( false );
                Fragment_search fragment_search = new Fragment_search();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.id_frame, fragment_search);
                fragmentTransaction.commit();
            }
        } );
        Searchbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword=Search.getText().toString();
                Toast toast = Toast.makeText(getActivity(), "搜尋中...", Toast.LENGTH_SHORT);
                toast.setGravity( Gravity.CENTER, 0, 0);
                toast.show();
                mMap.clear();
                setupMap();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }
    public void SpinnerSetting(){

        //下拉式選單設定
        arrType=new ArrayList<>( Arrays.asList(getResources().getStringArray(R.array.spn_typeList)));
        arrType.add(0,"不限");
        arrprice=getResources().getStringArray(R.array.price_search);
        arrsize=getResources().getStringArray(R.array.size_search);
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
        typeSpinner=getView().findViewById(R.id.typeSpinner);
        sizeSpinner=getView().findViewById(R.id.sizeSpinner);
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
        //型態
        ArrayAdapter<String> adapter2= new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, arrType){
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                if(position==0)
                    tv.setText("型態");
                return tv;
            }
        };
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter2);

        //坪數
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, arrsize){
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                if(position==0)
                    tv.setText("坪數");
                return tv;
            }
        };
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(adapter4);

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
                mMap.clear();
                if(countySpinner.getSelectedItemPosition()!=0){
                    cameraFollow(String.valueOf( countySpinner.getSelectedItem() )  );
                }
                setupMap();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                citySelect=citySpinner.getItemAtPosition(i).toString();
                mMap.clear();
                if(citySpinner.getSelectedItemPosition()!=0&&countySpinner.getSelectedItemPosition()!=0){
                    String address=String.valueOf( countySpinner.getSelectedItem())+String.valueOf( citySpinner.getSelectedItem());
                    cameraFollow(String.valueOf( citySpinner.getSelectedItem() )  );
                }
                setupMap();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    minsizeSelect=0;
                    maxsizeSelect=0;
                }else if(i==6){
                    minsizeSelect=(i-1)*10;
                    maxsizeSelect=-1;//無限大以-1表示

                }else{
                    maxsizeSelect=i*10;
                    minsizeSelect=(i-1)*10;
                }
                mMap.clear();
                setupMap();

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeSelect=typeSpinner.getItemAtPosition(i).toString();
                mMap.clear();
                setupMap();

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
                mMap.clear();
                setupMap();

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d( "request",String.valueOf( REQUEST_LOCATION) );
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gpsbtnClick();
                    cameraMovetoLocation();
                } else {
                    mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(new LatLng(25.033671,121.564427),15)  ));
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void setupMap(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("RentObj");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long objCount = dataSnapshot.getChildrenCount(); //計算資料筆數
                IconGenerator iconFactory = new IconGenerator(getActivity());
                iconFactory.setStyle( IconGenerator.STYLE_BLUE );
                markers = new Marker[(int)objCount];
                int i = 0;
                int objScore;

                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    objScore=0;
                    String key=ds.getKey();
                        lat =dataSnapshot.child(key).child("lat").getValue(); //取得資料
                        lng =dataSnapshot.child(key).child("lng").getValue();
                        title =dataSnapshot.child(key).child("title").getValue();
                        price = dataSnapshot.child( key ).child( "price" ).getValue();
                        county =dataSnapshot.child(key).child("county").getValue();
                        city = dataSnapshot.child(key).child("city").getValue();
                        type=dataSnapshot.child(key).child("type").getValue();
                        size=dataSnapshot.child(key).child("size").getValue();
                        Boolean visible =dataSnapshot.child( key ).child( "visible" ).getValue(Boolean.class);

                        String Stitle = title.toString();
                        String Sprice = price.toString();
                        String Scounty= county.toString();
                        String Scity=city.toString();
                        String Stype = type.toString();
                        String Ssize= size.toString();

                        if(Stitle.contains(keyword)||keyword.equals("")){objScore++;}
                        if(countySpinner.getSelectedItemPosition()==0){objScore++;}
                        if(citySpinner.getSelectedItemPosition()==0||countySpinner.getSelectedItemPosition()==0){objScore++;}
                        if(priceSpinner.getSelectedItemPosition()==0){objScore++;}
                        if(typeSpinner.getSelectedItemPosition()==0){objScore++;}
                        if(sizeSpinner.getSelectedItemPosition()==0){objScore++;}

                        if(String.valueOf(citySelect).equals(Scity)){objScore++;}
                        if(String.valueOf(countySelect).equals(Scounty)){objScore++;}
                        if((String.valueOf(typeSelect).equals(Stype))){objScore++;}
                        if(Integer.valueOf(Sprice)<=maxpriceSelect&&Integer.valueOf(Sprice)>=minpriceSelect){
                            objScore++;
                        }
                        if(Integer.valueOf( Ssize )<=maxsizeSelect&&Integer.valueOf( Ssize )>=minsizeSelect){
                            objScore++;
                        }

                        if(lat!=null&&lng!=null&&title!=null&&visible==true&&objScore==6){  //如果有經緯度
                             Double Dlat =Double.valueOf(lat.toString()); //經緯度string 轉double
                             Double Dlng =Double.valueOf(lng.toString());
                            // 增加地標與移動相機
                            LatLng target = new LatLng(Dlat,Dlng);
                            markers[i]=mMap.addMarker(new MarkerOptions()
                                    .position(target)
                                    .icon( BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(Stitle+" NT$"+Sprice)))
                                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
                            String[] strDetail ={key,Stitle,Sprice,Scounty,Scity,Stype,Ssize};
                            String detailArr=Arrays.toString(strDetail);
                            markers[i].setTag( detailArr );
                            i++;
                            mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(new LatLng(Dlat,Dlng),15)  ));
                        }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void cameraFollow(String address){
        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
        String newAddress =address;
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

        Log.d("latlng",String.valueOf( latitude ));
        Log.d("latlng",String.valueOf( longitude ));

        mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15)  ));

    }
    @SuppressLint("MissingPermission")
    private void gpsbtnClick() {
        mMap.setMyLocationEnabled( true );
        mMap.setOnMyLocationButtonClickListener( new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                cameraMovetoLocation();
                return false;
            }
        } );
    }
    @SuppressLint("MissingPermission")
    private void cameraMovetoLocation(){
        LocationManager locationManager=(LocationManager)getActivity().getSystemService( Context.LOCATION_SERVICE );
        Criteria criteria = new Criteria(  );
        //設定存取標準為精確
        criteria.setAccuracy( Criteria.ACCURACY_FINE );
        //向系統查詢最適合的服務提供者(通常是gps)
        String provider = locationManager.getBestProvider( criteria,true );
        Location location=locationManager.getLastKnownLocation( provider );
        if(location!=null){
            // mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( location,15));
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( location.getLatitude(),location.getLongitude() ), 15 ) );
        }else {
            mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(new LatLng(25.033671,121.564427),15)  ));
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction( uri );
        }
    }





    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
