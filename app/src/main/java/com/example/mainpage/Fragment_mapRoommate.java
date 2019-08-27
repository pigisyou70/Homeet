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
import android.location.LocationListener;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_mapRoommate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_mapRoommate extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    Object lat,lng,title,price,county,city,gender ;
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

    Spinner countySpinner;
    Spinner priceSpinner;
    Spinner genderSpinner;
    Spinner houseSpinner;
    Spinner citySpinner;

    Button Mapbt;
    Button Listbt;
    ImageButton Searchbt;
    EditText Search;
    String keyword;
    Marker[] markersHH;
    Marker[] markersNH;
    String countySelect;
    String citySelect;
    String houseSelect;
    String genderSelect;

    int minpriceSelect;
    int maxpriceSelect;

    boolean click;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private OnFragmentInteractionListener mListener;
    private static final int REQUEST_LOCATION=2;
    private GoogleMap mMap;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_mapRoommate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_mapRoommate.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_mapRoommate newInstance(String param1, String param2) {
        Fragment_mapRoommate fragment = new Fragment_mapRoommate();
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
        return inflater.inflate( R.layout.fragment_map_roommate, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        Init();
        InitFragment();
    }
    public void Init(){
        countySelect="***";
        genderSelect="***";
        citySelect="***";
        houseSelect="***";
        keyword="";

        click=true;

        Mapbt=getView().findViewById( R.id.MapButton);
        Listbt=getView().findViewById( R.id.ListButton);
        Searchbt=getView().findViewById( R.id.SearchButton);
        Search=getView().findViewById( R.id.SearchView);
        citySelect="nothing";

        SpinnerSetting();
        Listbt.setEnabled(true);
        Mapbt.setEnabled(false);
        setButtonClick();
        Searchbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword=Search.getText().toString();
                Toast toast = Toast.makeText(getActivity(), "搜尋中...", Toast.LENGTH_SHORT);
                toast.setGravity( Gravity.CENTER, 0, 0);
                toast.show();
                mMap.clear();
                setupMapHaveHouse();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }
    public void setButtonClick(){
        Listbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mapbt.setEnabled( true );
                Listbt.setEnabled( false );
                Fragment_searchRoommate fragment_searchRoommate = new Fragment_searchRoommate();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace( R.id.id_frame, fragment_searchRoommate);
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
                setupMapHaveHouse();
            }
        });
    }
    public void SpinnerSetting(){
        //下拉式選單設定
        arrGender=new ArrayList<>(Arrays.asList(getResources().getStringArray( R.array.spn_list)));
        arrGender.add(0,"不限");
        arrprice=getResources().getStringArray( R.array.price_search);
        arrhouse=getResources().getStringArray( R.array.spn_housetypelist);
        arrCounty=getResources().getStringArray( R.array.county_search);
        arrKeelongCity=getResources().getStringArray( R.array.keelung_search);
        arrTaipeiCity=getResources().getStringArray( R.array.taipei_search);
        arrNewTaipeiCity=getResources().getStringArray( R.array.newtaipei_search);
        arrTaoyuanCity=getResources().getStringArray( R.array.taoyuang_search);
        arrHsinchu1=getResources().getStringArray( R.array.Hsinchu1_search);
        arrHsinchu2=getResources().getStringArray( R.array.Hsinchu2_search);
        arrMiaoli=getResources().getStringArray( R.array.Miaoli_search);
        arrTaichung=getResources().getStringArray( R.array.Taichung_search);
        arrChanghua=getResources().getStringArray( R.array.Changhua_search);
        arrNantou=getResources().getStringArray( R.array.Nantou_search);
        arrYunlin=getResources().getStringArray( R.array.Yunlin_search);
        arrChiayi1=getResources().getStringArray( R.array.Chiayi1_search);
        arrChiayi2=getResources().getStringArray( R.array.Chiayi2_search);
        arrTainan=getResources().getStringArray( R.array.Tainan_search);
        arrKaohsiung=getResources().getStringArray( R.array.Kaohsiung_search);
        arrPingtung=getResources().getStringArray( R.array.Pingtung_search);
        arrTaitang=getResources().getStringArray( R.array.Taitang_search);
        arrHoalian=getResources().getStringArray( R.array.Hoalian_search);
        arrIlan=getResources().getStringArray( R.array.Ilan_search);
        arrWuhu=getResources().getStringArray( R.array.Wuhu_search);
        arrJinmen=getResources().getStringArray( R.array.Jinmen_search);
        arrLianjiang=getResources().getStringArray( R.array.Lianjiang_search);

        countySpinner=getView().findViewById( R.id.countySpinner);
        priceSpinner=getView().findViewById( R.id.priceSpinner);
        genderSpinner=getView().findViewById( R.id.genderSpinner);
        houseSpinner=getView().findViewById( R.id.houseSpinner);
        citySpinner=getView().findViewById( R.id.dictSpinner);
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
                mMap.clear();
                if(countySpinner.getSelectedItemPosition()!=0){
                    cameraFollow(String.valueOf( countySpinner.getSelectedItem() )  );
                }
                setupMapHaveHouse();

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
                setupMapHaveHouse();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderSelect=genderSpinner.getItemAtPosition(i).toString();
                mMap.clear();
                setupMapHaveHouse();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        houseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                houseSelect=houseSpinner.getItemAtPosition(i).toString();
                mMap.clear();
                setupMapHaveHouse();
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
                setupMapHaveHouse();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

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
                    setupMapHaveHouse();
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
    protected void setupMapHaveHouse(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("FindRoommate").child( "HaveHouse");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long objCount = dataSnapshot.getChildrenCount(); //計算資料筆數
                IconGenerator iconFactory = new IconGenerator(getActivity());
                iconFactory.setStyle( IconGenerator.STYLE_BLUE );
                markersHH = new Marker[(int)objCount];
                int i = 0;
                int objScore;

                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    objScore=0;
                    String key=ds.getKey();
                    String phoneNumber;
                    lat =dataSnapshot.child(key).child("lat").getValue(); //取得資料
                    lng =dataSnapshot.child(key).child("lng").getValue();
                    title =dataSnapshot.child(key).child("title").getValue();
                    price = dataSnapshot.child( key ).child( "price" ).getValue();
                    county =dataSnapshot.child(key).child("county").getValue();
                    city = dataSnapshot.child(key).child("city").getValue();
                    gender=dataSnapshot.child( key ).child("gender").getValue();
                    Boolean visible =dataSnapshot.child( key ).child( "visible" ).getValue(Boolean.class);

                    String Stitle = title.toString();
                    String Sprice = price.toString();
                    String Scounty= county.toString();
                    String Scity=city.toString();
                    String Sgender=gender.toString();
                    String keyTag=key.substring(0,1);  // it is "H"

                    if(Stitle.contains(keyword)||keyword.equals("")){objScore++;}
                    if(countySpinner.getSelectedItemPosition()==0){objScore++;}
                    if(countySpinner.getSelectedItemPosition()==0||citySpinner.getSelectedItemPosition()==0){objScore++;}
                    if(priceSpinner.getSelectedItemPosition()==0){objScore++;}
                    if(houseSpinner.getSelectedItemPosition()==0){objScore++;}
                    if(genderSpinner.getSelectedItemPosition()==0){objScore++;}

                    if(houseSelect.equals(keyTag)){objScore++;}
                    if(citySelect.equals(Scity)){objScore++;}
                    if(countySelect.equals(Scounty)){objScore++;}
                    if(Integer.valueOf(Sprice)<=maxpriceSelect&&Integer.valueOf(Sprice)>=minpriceSelect){objScore++;}
                    if(genderSelect.equals( Sgender )){objScore++;}
//                    Log.d("objSCore",String.valueOf(objScore));

                    if(lat!=null&&lng!=null&&title!=null&&visible==true&&objScore==6){  //如果有經緯度
                        double Dlat =Double.valueOf(lat.toString()); //經緯度string 轉double
                        double Dlng =Double.valueOf(lng.toString());
                        // 增加地標與移動相機
                        LatLng target = new LatLng(Dlat,Dlng);
                        markersHH[i]=mMap.addMarker(new MarkerOptions()
                                .position(target)
                                .icon( BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(Stitle+" NT$"+Sprice)))
                                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
                        markersHH[i].setTag( key);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
