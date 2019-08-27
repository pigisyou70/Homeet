package com.example.mainpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {
    TextView mPhone;
    EditText mFirstName, mLastName, mAge, mNickName, mInteresting, mSelf_introduction;
    Spinner mGender;
    String phoneNumber;
    FirebaseAuth mAuth;
    private String TAG = "EditProFileActivity";
    private ArrayList<Contact> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //狀態列顏色設定
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorMainBlue));

        mAuth = FirebaseAuth.getInstance();
        phoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("member/" + phoneNumber);
        reference_contacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                list.add(contact);
                SetProfile();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void SetProfile(){
        mFirstName = findViewById(R.id.mFirstName);
        mLastName = findViewById(R.id.mLastName);
        mNickName = findViewById(R.id.mNickName);
        mGender = findViewById(R.id.mGender);
        mAge = findViewById(R.id.mAge);
        mPhone = findViewById(R.id.mPhone);
        mInteresting = findViewById(R.id.mInteresting);
        mSelf_introduction = findViewById(R.id.mSelf_introduction);

        // 大頭貼
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("member/"+phoneNumber+"/pic.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ImageView imageView = findViewById(R.id.imageView);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        // 姓名
        mFirstName.setText(list.get(0).FirstName.toString());
        mLastName.setText(list.get(0).LastName.toString());

        // 暱稱
        mNickName.setText(list.get(0).NickName.toString());

        // 性別
        Resources res = getResources();
        String[] county = res.getStringArray(R.array.spn_list);
        int counts = 0;
        for (String i : county) {
//            Log.d(TAG,i);
            if( i.equals(list.get(0).Gender.toString())){
//                Log.d(TAG,"找到相同的性別了");
                break;
            }
            counts++;
        }
        mGender.setSelection(counts);

        // 年齡
        mAge.setText(list.get(0).Age.toString());

        // 電話
        mPhone.setText(list.get(0).PhoneNumber.toString());

        // 興趣
        mInteresting.setText(list.get(0).Interesting.toString());

        // 自我介紹
        mSelf_introduction.setText(list.get(0).Selfintroduction.toString());
    }
    //取得相片後返回的監聽式
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //當使用者按下確定後
        if (resultCode == RESULT_OK) {
            //取得圖檔的路徑位置
            Uri uri = data.getData();
            //寫log
            Log.e("uri", uri.toString());
            //抽象資料的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //由抽象資料接口轉換圖檔路徑為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                UploadPicture(bitmap,phoneNumber);
                //取得圖片控制項ImageView
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                // 將Bitmap設定到ImageView
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void UploadPicture(Bitmap bitmap,String number){
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

        StorageReference mStorageRef = mFirebaseStorage
                .getReference()
                .child("member")
                .child(number)
                .child("pic.jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageData = baos.toByteArray();

        StorageMetadata storageMetadata = new StorageMetadata
                .Builder()
                .setCustomMetadata("MyKey", "MyValue")
                .build();
        UploadTask uploadTask = mStorageRef.putBytes(imageData, storageMetadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(EditProfileActivity.this,"上傳失敗！", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(EditProfileActivity.this,"上傳成功！", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progressPersentage = (int)((taskSnapshot.getBytesTransferred()*100)/(taskSnapshot.getTotalByteCount()));
//                Log.d("uploading", progressPersentage+"%");
            }
        });
    }

    // 讀取手機內部媒體
    public void ReadExternal(View v){
        Intent intent = new Intent();

        //開啟Pictures畫面Type設定為image
        intent.setType("image/*");

        // 使用 Intent.ACTION_GET_CONTENT 這個 Action
        // 會開啟選取圖檔視窗讓您選取手機內圖檔
        intent.setAction(Intent.ACTION_GET_CONTENT);

        //取得相片後返回本畫面
        startActivityForResult(intent, 1);
    }

    public void confirmProfile(View v) {
        EditText mFirstName = findViewById(R.id.mFirstName);
        EditText mLastName = findViewById(R.id.mLastName);
        EditText mNickName = findViewById(R.id.mNickName);
        Spinner mGender = findViewById(R.id.mGender);
        EditText mAge = findViewById(R.id.mAge);
        EditText mInteresting = findViewById(R.id.mInteresting);
        EditText mSelf_introduction = findViewById(R.id.mSelf_introduction);

        String S_FirstName = mFirstName.getText().toString();
        String S_LastName = mLastName.getText().toString();
        String S_NickName = mNickName.getText().toString();
        String S_Gender = mGender.getSelectedItem().toString();
        String S_Age = mAge.getText().toString();

        String S_Interesting = mInteresting.getText().toString();
        String S_Self_introduction = mSelf_introduction.getText().toString();

        if (S_FirstName.isEmpty() ||  S_LastName.isEmpty()  ||S_Age.isEmpty() || S_Gender.isEmpty()  || S_NickName.isEmpty() )
            Toast.makeText(EditProfileActivity.this, "請輸入必填欄位", Toast.LENGTH_LONG).show();
        else {
            FirebaseDatabase.getInstance().getReference().child("member/" + phoneNumber + "/FirstName").setValue(S_FirstName);
            FirebaseDatabase.getInstance().getReference().child("member/" + phoneNumber + "/LastName").setValue(S_LastName);
            FirebaseDatabase.getInstance().getReference().child("member/" + phoneNumber + "/Age").setValue(S_Age);
            FirebaseDatabase.getInstance().getReference().child("member/" + phoneNumber + "/Gender").setValue(S_Gender);
            FirebaseDatabase.getInstance().getReference().child("member/" + phoneNumber + "/Interesting").setValue(S_Interesting);
            FirebaseDatabase.getInstance().getReference().child("member/" + phoneNumber + "/Selfintroduction").setValue(S_Self_introduction);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UserProfileChangeRequest profileUpdates =
                    new UserProfileChangeRequest
                            .Builder()
                            .setDisplayName(S_FirstName+S_LastName)
                            .build();
            user.updateProfile(profileUpdates);

            Toast.makeText(EditProfileActivity.this, "已儲存資料", Toast.LENGTH_LONG).show();

            this.finish();
        }

        if(!S_NickName.isEmpty())
            FirebaseDatabase.getInstance().getReference().child("member/" + phoneNumber + "/NickName").setValue(S_NickName);
    }

    public void returnProfile(View v){
        this.finish();
    }
}
