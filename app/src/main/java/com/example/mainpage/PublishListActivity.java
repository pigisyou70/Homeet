package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
// ArrayList<HashMap<String,String>> 取得資料的方法　Line 108
// Adapter 和 ListView 的用法


public class PublishListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ArrayList<String> PublishKey;
    private ArrayList<String> PublishValue;

    private ArrayList<HashMap<String,String>> list = new ArrayList<>();
    private ListView list_of_publish;
    private TextView PublistID, PublishTitle;
    private SimpleAdapter adapter;
    private int Total = 0 ;
    private int Start = 0;
    private String RentObjID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publist);

        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMainBlue));

        mAuth = FirebaseAuth.getInstance();

        list_of_publish = findViewById(R.id.list_of_publish);
        PublishKey = new ArrayList<>();
        PublishValue = new ArrayList<>();

        DatabaseReference reference_contacts =
                FirebaseDatabase.getInstance().getReference("member/" + mAuth.getCurrentUser().getPhoneNumber()+"/MyRentObj");

        reference_contacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    PublishKey.add(ds.getKey());
                    PublishValue.add(ds.getValue().toString());
                    Total++;
                    AddData(PublishKey, PublishValue);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void AddData(ArrayList<String> RentObjID, ArrayList<String> RentObjTitle){
        while( Start < Total)
        {
            HashMap<String,String> item = new HashMap<>();
            item.put("Count", (Start+1)+"");
            item.put("RentObjTitle", RentObjTitle.get(Start));
            item.put("RentObjID", RentObjID.get(Start));
            list.add(item);
            Start++;
        }
        Show(list);
    }

    //帶入對應資料
    public void Show(ArrayList<HashMap<String,String>> arrayList) {
        adapter = new SimpleAdapter(
                PublishListActivity.this,
                arrayList,
                R.layout.publishlist,
                new String[]{"Count", "RentObjTitle"},
                new int[]{R.id.Publist_ID, R.id.Publist_Title}
        );
        list_of_publish.setAdapter(adapter);
        list_of_publish.setOnItemClickListener(onClickeListView);
    }


    private AdapterView.OnItemClickListener onClickeListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            PublistID = view.findViewById(R.id.Publist_ID);
            PublishTitle = view.findViewById(R.id.Publist_Title);
            RentObjID = list.get(position).get("RentObjID");

//            Log.d(TAG,"你點了第"+position+"個\n"+
//                    "ID為"+PublistID.getText()+
//                    "\nTitle為"+PublishTitle.getText());

            new AlertDialog.Builder(PublishListActivity.this)
                    .setItems(R.array.DetailList, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            switch (position){
                                case 0:
//                                    Log.d("Adapter","檢視");
                                    Intent intent = new Intent(PublishListActivity.this, DetailActivity.class);
                                    intent.putExtra("ObjectID",RentObjID);
                                    startActivity(intent);

                                    break;
                                case 1:
//                                    Log.d("Adapter","編輯");
                                    Intent intent1 = new Intent(PublishListActivity.this, EditObjectActivity.class);
                                    intent1.putExtra("ObjectID",RentObjID);
                                    startActivity(intent1);
                                    finish();
                                    break;
                                case 2:
//                                    Log.d("Adapter","刪除");

                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();

                                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("RentObj/"+RentObjID);
                                    DatabaseReference mRef2 =
                                            FirebaseDatabase.getInstance().getReference("member/"+
                                                    mAuth.getCurrentUser().getPhoneNumber()+
                                                    "/MyRentObj/"+
                                                    RentObjID);
                                    mRef.removeValue();
                                    mRef2.removeValue();
                                    recreate();
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .show();
        }
    };
}
