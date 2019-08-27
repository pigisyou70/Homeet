package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ShowMemberActivity extends AppCompatActivity {

    private TextView mName, mGender, mAge, mPhone, mNickName,mInteresting,mSelf_Introduction;
    private ImageView imageView;
    private Button btn_Message,btn_Obj;

    private ArrayList<Contact> list = new ArrayList<>();

    private FirebaseAuth mAuth;
    String MemberID;
    String HNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_member);
        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMainBlue));



        Intent intent = getIntent();
        MemberID = intent.getStringExtra("MemberID");
        HNum=intent.getStringExtra("ObjectID");
        init();
    }

    private void init(){

        mName = findViewById(R.id.mFirstName);
        mNickName = findViewById(R.id.mNickName);
        mGender = findViewById(R.id.mGender);
        mInteresting = findViewById(R.id.mInteresting);
        mSelf_Introduction = findViewById(R.id.mSelf_introduction);
        mAge = findViewById(R.id.mAge);
        mPhone = findViewById(R.id.mPhone);
        imageView = findViewById(R.id.imageView);

        btn_Obj=findViewById(R.id.btn_Obj);
        btn_Obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowMemberActivity.this,DetailActivity.class);
                intent.putExtra("ObjectID", HNum);
                startActivity(intent);
            }
        });
        if(HNum.substring(0,1).equals("N")){
            btn_Obj.setVisibility(View.GONE);
        }
        mAuth = FirebaseAuth.getInstance();
        btn_Message = findViewById(R.id.btn_Message);
        if(MemberID.equals(mAuth.getCurrentUser().getPhoneNumber())){
            btn_Message.setVisibility(View.GONE);
        }
        btn_Message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userPhone = mAuth.getCurrentUser().getPhoneNumber(); // 自己的手機號碼
                final String anotherPhone = MemberID; // 別人的手機號碼
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

                Intent intent = new Intent(ShowMemberActivity.this, MainActivity.class);
                intent.putExtra("id",1);
                startActivity(intent);

            }
        });

        // 把 Firebase 登入驗證實體化

    }


    @Override
    public void onResume(){
        super.onResume();

        // 資料庫參照, 並取得會員資料
        DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("member/" + MemberID);
        reference_contacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                list.add(contact);
                ReadProfile();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void ReadProfile() {
        mName.setText(list.get(0).FirstName.toString() +"  "+ list.get(0).LastName.toString());
        mNickName.setText(list.get(0).NickName.toString());
        mInteresting.setText(list.get(0).Interesting.toString());
        mSelf_Introduction.setText(list.get(0).Selfintroduction.toString());
        mGender.setText(list.get(0).Gender.toString());
        mAge.setText(list.get(0).Age.toString());
        mPhone.setText(list.get(0).PhoneNumber.toString());

        ReadPicture("member/" + MemberID + "/pic.jpg", imageView);
    }

    public void ReadPicture(String path, final ImageView v){
        // 取得　Storage 實體
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference islandRef = storage.getReference().child(path);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                v.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
