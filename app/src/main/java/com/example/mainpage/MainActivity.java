package com.example.mainpage;

import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;

import java.util.ArrayList;

import static com.example.mainpage.Fragment_contact.Contactcounter;

public class MainActivity extends FragmentActivity{
    private BottomNavigationView navView;
    private MenuItem item1;
    private MenuItem item2;
    private MenuItem item4;
    private MenuItem item5;
    private FirebaseAuth mAuth;
    private Fragment_search fragmentSearch;
    private Fragment_searchRoommate fragment_searchRoommate;
    private Fragment_favorite fragmentFavorite;
    private Fragment_contact fragmentContact;
    private Fragment_login fragmentLogin;
    int searchType;


    private long lastBackPress;
    private int NotRead = 0;
    static public ArrayList<String> ContactPerson = new ArrayList<>();
    static public ArrayList<Integer> ContactPersonMessages = new ArrayList<>();

    // 讓使用者要先執行會員登入，才能執行訊息功能
    public static boolean getMemberStatus = false;
    public static int counter = 0;

    DatabaseReference mRef;

    //導航列listen事件
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            refreshItemIcon();
            Intent intent;
            switch (item.getItemId()) {
                case R.id.id_nav_search:
                    counter = 0;

                    item1.setIcon(R.drawable.icon_icon_search_click);
                    if(searchType==0){
                        getSupportFragmentManager().beginTransaction().replace( R.id.id_frame,fragmentSearch ).commit();
                    }else if(searchType==1){
                        getSupportFragmentManager().beginTransaction().replace( R.id.id_frame,fragment_searchRoommate ).commit();
                    }
                    //commit是每操作一次都要下的指令
                    titleBar.setText( "Homeet" );
                    btnFindObj.setVisibility( View.VISIBLE);
                    btnFindRoommate.setVisibility(View.VISIBLE);
                    return true;

                case R.id.id_nav_favorite:
                    if(mAuth.getCurrentUser() == null){
                        Toast.makeText(MainActivity.this,"請先登入會員，才可使用收藏功能！",Toast.LENGTH_LONG).show();
                    }else{
                        counter = 0;

                        item2.setIcon(R.drawable.icon_icon_favorite_click);
                        getSupportFragmentManager().beginTransaction().replace( R.id.id_frame,fragmentFavorite ).commit();
                        titleBar.setText( "我的收藏" );
                        btnFindObj.setVisibility( View.INVISIBLE );
                        btnFindRoommate.setVisibility(View.INVISIBLE);
                        return true;
                    }
                case R.id.id_nav_new_obj:
                    if(mAuth.getCurrentUser() == null){
                        Toast.makeText(MainActivity.this,"請先登入會員，才可使用刊登功能！",Toast.LENGTH_LONG).show();
                    }else{
                        counter = 0;

                        intent = new Intent( MainActivity.this,postTypeSelect.class );
                        startActivity( intent );
                        return true;
                    }

                case R.id.id_nav_message:
//                    Log.d("fdfsda","你按了下方的留言按鍵");
                    if(mAuth.getCurrentUser() == null){
                        Toast.makeText(MainActivity.this,"請先登入會員，才可使用訊息功能！",Toast.LENGTH_LONG).show();
                    }else{
                        counter = 1;


                        getSupportFragmentManager().beginTransaction().replace( R.id.id_frame,fragmentContact ).commit();
                        item4.setIcon(R.drawable.icon_icon_message_click);
                        titleBar.setText( "我的訊息" );
                        btnFindObj.setVisibility( View.INVISIBLE );
                        btnFindRoommate.setVisibility(View.INVISIBLE);
                        return true;
                    }

                case R.id.id_nav_member:
                    counter = 0;

                    item5.setIcon(R.drawable.icon_icon_member_click);
                    getSupportFragmentManager().beginTransaction().replace( R.id.id_frame,fragmentLogin ).commit();
                    titleBar.setText( "個人資料" );
                    btnFindObj.setVisibility( View.INVISIBLE );
                    btnFindRoommate.setVisibility(View.INVISIBLE);
                    return true;
            }
            return false;
        }
    };
    private Button btnFindObj;
    private Button btnFindRoommate;
    private TextView titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMainBlue));

        setup();  //初始畫面設定

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            // 將自己的手機號碼訂閱至 FCM
            String phone = mAuth.getCurrentUser().getPhoneNumber();
            phone = phone.substring(4);
            phone = "0"+phone;
//            Log.d("訂閱推播Topic", ""+phone);
            FirebaseMessaging.getInstance().subscribeToTopic(phone);

            if(!getMemberStatus)
                Toast.makeText(this,"已自動登入!",Toast.LENGTH_LONG).show();

            getMemberStatus = true;

            // 讀取是否有未讀訊息，並在訊息功能上顯示小紅點
            GetContactList();
        }

        // 從其他 Activity 跳回來，又轉去其他 Fragment 用的
        int id = getIntent().getIntExtra("id", 0);
        if (id == 1) {
            btnFindObj.setVisibility( View.INVISIBLE );
            btnFindRoommate.setVisibility(View.INVISIBLE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.id_frame,new Fragment_contact())
                    .addToBackStack(null)
                    .commit();
        }
        findViewById( R.id.search_findObj ).setEnabled(false);
        findViewById( R.id.search_findObj ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchType=0;
                findViewById( R.id.search_findRoomate ).setEnabled(true);
                findViewById( R.id.search_findObj ).setEnabled(false);
                getSupportFragmentManager().beginTransaction().replace( R.id.id_frame,new Fragment_search() ).commit();
            }
        } );
        findViewById( R.id.search_findRoomate ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchType=1;
                findViewById( R.id.search_findRoomate ).setEnabled(false);
                findViewById( R.id.search_findObj ).setEnabled(true);
                getSupportFragmentManager().beginTransaction().replace( R.id.id_frame,new Fragment_searchRoommate() ).commit();

            }
        } );
    }



    // 讀取是否有未讀訊息寫在此處的用意是，假如用戶沒有去點擊訊息功能，就不會觸發
    // 不會觸發就不會看到小紅點
    private void GetContactList(){
        String path = "member/" + mAuth.getCurrentUser().getPhoneNumber() + "/Content";
        mRef = FirebaseDatabase.getInstance().getReference(path);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NotRead = 0;
                ContactPerson.clear();
                ContactPersonMessages.clear();
                //抓全部聯絡人
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String contactPeople = ds.getKey();
                    int count=0;
                    //抓該聯絡人的所有節點
                    for(DataSnapshot dss:ds.getChildren()){
                        int requirement=0;
                        //抓該節點的資料
                        for(DataSnapshot dsss: dss.getChildren()){
                            if(dsss.getKey().equals("messageReady")){
                                //如果是未讀
                                if(dsss.getValue(String.class).equals("")){
                                    requirement++;
                                }
                            }

                            if(dsss.getKey().equals("messageType")){
                                int type=dsss.getValue(Integer.class);
                                //如果是對方發送的訊息
                                if(type == 0 || type == 2){
                                    requirement++;
                                }
                            }
                        }

                        if(requirement == 2){
                            //符合以上條件的節點
                            count++;
                        }
                    }
                    NotRead += count;
//                    Log.d("各聯絡人未讀訊息數",contactPeople + "+" + count);
                    if (contactPeople != null){
                        ContactPerson.add(contactPeople);
                        ContactPersonMessages.add(count);

                    }

                }
//                Log.d("總未讀訊息數","總共" + NotRead + "則未讀");

                //執行一個需要NotRead的Function後再歸零---------------------------------------------------------
                SetRed();

            } // Listener 結束

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SetRed(){
        if (NotRead > 0){
            item4.setIcon(R.drawable.icon_message_reddot);
        }else{
            item4.setIcon(R.drawable.icon_icon_message);
        }
        if (counter == 1 && Contactcounter){
            getSupportFragmentManager().beginTransaction()
                    .detach(fragmentContact)
                    .attach(fragmentContact)
                    .addToBackStack(null) // 按下返回鍵會返回此Fragment
                    .commitAllowingStateLoss();
        }
    }

    //初始畫面設定
    public void setup(){
        navView = findViewById( R.id.nav_view );//find 底層導航列
        navView.setItemIconTintList( null ); //把預設的顏色設計關掉
        navView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );
        titleBar = findViewById( R.id.id_titleBar );
        item1 = navView.getMenu().findItem(R.id.id_nav_search);
        item2 = navView.getMenu().findItem(R.id.id_nav_favorite);
        item4 = navView.getMenu().findItem(R.id.id_nav_message);
        item5 = navView.getMenu().findItem(R.id.id_nav_member);
        item1.setIcon(R.drawable.icon_icon_search_click);//預設是搜尋被click的icon
        btnFindObj = findViewById( R.id.search_findObj );
        btnFindRoommate = findViewById( R.id.search_findRoomate );
        btnFindObj.setEnabled( false );
        btnFindRoommate.setEnabled( true );


        fragmentSearch=new Fragment_search();
        fragment_searchRoommate=new Fragment_searchRoommate();
        fragmentFavorite=new Fragment_favorite();
        fragmentContact = new Fragment_contact();
        fragmentLogin = new Fragment_login();
        if(searchType==0){
            getSupportFragmentManager().beginTransaction().replace( R.id.id_frame,fragmentSearch ).commit();
        }else if(searchType==1){
            getSupportFragmentManager().beginTransaction().replace( R.id.id_frame,fragment_searchRoommate ).commit();
        }

    }

    public void refreshItemIcon() {  //刷新預設的icon
        item1.setIcon(R.drawable.icon_icon_search);
        item2.setIcon(R.drawable.icon_icon_favorite);

        if (NotRead > 0){
            item4.setIcon(R.drawable.icon_message_reddot);
        }else{
            item4.setIcon(R.drawable.icon_icon_message);
        }

        item5.setIcon(R.drawable.icon_icon_member);
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            if (System.currentTimeMillis() - lastBackPress < 1000) {
                super.onBackPressed();
            } else {
                lastBackPress = System.currentTimeMillis();
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
