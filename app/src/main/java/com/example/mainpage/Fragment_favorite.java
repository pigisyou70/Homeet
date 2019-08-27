package com.example.mainpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragment_favorite extends Fragment implements FragmentBackHandler{



    private OnFragmentInteractionListener mListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    String Phone;
    ArrayList<String> myList;
    int serialNum;
    LinearLayout favoriteList;
    public Fragment_favorite() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_favorite.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_favorite newInstance(String param1, String param2) {
        Fragment_favorite fragment = new Fragment_favorite();
        Bundle args = new Bundle();
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate( R.layout.fragment_favorite, container, false );

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init();
    }
    public void Init(){
        favoriteList=getView().findViewById(R.id.FavoriteList);
        favoriteList.removeAllViews();
        myList=new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        Phone=mAuth.getCurrentUser().getPhoneNumber();
        myRef=database.getReference("member/"+Phone+"/Favorite");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(! dataSnapshot.exists()){
                    TextView nodata=new TextView(getContext());
                    nodata.setText("(尚無收藏)");
                    favoriteList.addView(nodata);

                }else{
                    serialNum=0;
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        String HNum=ds.getKey();
                        boolean hasAdd=ds.getValue(Boolean.class);

                        if(hasAdd)
                            getObj(HNum);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getObj(final String HNum){
        myRef=database.getReference("RentObj/"+HNum);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title="";
                String price="";
                String discript="";

                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().equals("title")){
                        title=ds.getValue(String.class);
                    }
                    if(ds.getKey().equals("discript")){
                        discript=ds.getValue(String.class);
                    }
                    if(ds.getKey().equals("price")){
                        price=ds.getValue(String.class);
                    }
                }

                Layout(title,price,HNum,discript);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Layout(String title,String price,final String HNum,String discript){
        if(discript.length()>25){
            discript=discript.substring(0,24)+"...";
        }

        if (getContext() == null)
            return;
        LinearLayout FavoriteObj=new LinearLayout(getContext());
        FavoriteObj.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.layout_contact) );
        FavoriteObj.setPadding(50,35,50,20);
        LinearLayout.LayoutParams ObjLayout=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,350);
        ObjLayout.setMargins(0,0,0,25);
        FavoriteObj.setLayoutParams(ObjLayout);
        FavoriteObj.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams aa=new LinearLayout.LayoutParams(800, ViewGroup.LayoutParams.WRAP_CONTENT);
        aa.gravity=Gravity.CENTER;
        LinearLayout.LayoutParams bb=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bb.gravity=Gravity.CENTER;
        LinearLayout.LayoutParams cc=new LinearLayout.LayoutParams(885, ViewGroup.LayoutParams.WRAP_CONTENT);



        LinearLayout brief=new LinearLayout(getContext());
        brief.setOrientation(LinearLayout.HORIZONTAL);
        brief.setLayoutParams(bb);


        TextView titleView=new TextView(getContext());
        titleView.setLayoutParams(aa);
        titleView.setText(title);
        titleView.setTextSize(24);
        titleView.setHeight( 120 );
        titleView.setTextColor(Color.rgb(0,87,75));
        brief.addView(titleView);

        TextView priceView=new TextView(getContext());
        priceView.setLayoutParams(bb);
        priceView.setText(price+"/月");
        priceView.setGravity(Gravity.RIGHT);
        priceView.setTextSize(18);
        brief.addView(priceView);

        FavoriteObj.addView(brief);


        LinearLayout discrpitline=new LinearLayout(getContext());
        TextView discrpitView=new TextView(getContext());
        discrpitView.setLayoutParams(cc);
        discrpitView.setText(discript);
        discrpitView.setTextSize(16);
        discrpitline.addView(discrpitView);

        FavoriteObj.addView(discrpitline);




        FavoriteObj.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if  (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    view.setElevation( 15 );

                    return true ;
                }
                else if  (motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    view.setElevation( 0 );

                    Intent intent = new Intent(getContext(),DetailActivity.class);
                    intent.putExtra("ObjectID", HNum);
                    startActivityForResult(intent,20);
                    return true ;
                }
                return false;
            }
        });

        favoriteList.addView(FavoriteObj);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==20){
            Init();
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
