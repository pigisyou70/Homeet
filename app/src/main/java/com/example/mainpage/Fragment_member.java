package com.example.mainpage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Fragment_member extends Fragment implements FragmentBackHandler{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;     // 存放上一個程式丟過來的 電話號碼
    private OnFragmentInteractionListener mListener;


    // 宣告一個存放資料的 ArrayList
    private ArrayList<Contact> list = new ArrayList<>();

    private static final String TAG = "Fragment_member";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 3;
    private FirebaseAuth mAuth;

    private ImageView imageView;
    private TextView mName, mGender, mAge, mPhone, mNickName, mInteresting, mSelfintroduction;


    public Fragment_member() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_member newInstance(String param1, String param2) {
        Fragment_member fragment = new Fragment_member();
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
        // 獲取 Layout
        View view = inflater.inflate(R.layout.fragment_member, null);

        imageView = view.findViewById(R.id.imageView);
        mName = view.findViewById(R.id.mFirstName);
        mNickName = view.findViewById(R.id.mNickName);
        mGender = view.findViewById(R.id.mGender);
        mAge = view.findViewById(R.id.mAge);
        mPhone = view.findViewById(R.id.mPhone);
        mInteresting = view.findViewById(R.id.mInteresting);
        mSelfintroduction = view.findViewById(R.id.mSelf_introduction);


        ImageButton btn_Publicsh = (ImageButton) view.findViewById(R.id.btn_Publicsh);
        btn_Publicsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PublishListActivity.class);
                startActivity(intent);
            }
        });


        ImageButton btn_Reservation = (ImageButton) view.findViewById(R.id.btn_Reservation);
        btn_Reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),QueryCalendar.class);
                startActivity(intent);
            }
        });


        ImageButton btn_Edit = (ImageButton) view.findViewById(R.id.btn_Edit);
        btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                list.clear();
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 資料庫參照, 並取得會員資料
        DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("member/" + mParam2);
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

        // 把 Firebase 登入驗證實體化
        mAuth = FirebaseAuth.getInstance();
        MainActivity.getMemberStatus = true;
    }

    public void onResume(){
        super.onResume();

        // 資料庫參照, 並取得會員資料
        DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("member/" + mParam2);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void CheckPermission(){
        // 一開始可以使用checkSelfPermission來檢查是否擁有這個權限。
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        // 你會得到 PackageManager.PERMISSION_GRANTED 跟 PackageManager.PERMISSION_DENIED 兩種結果。
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
//            Log.d("Hello","我沒有讀取檔案的權限");
            QuestPermission();

        }
        else if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
//            ("Hello","我擁有讀取檔案的權限");
        }
    }

    public void QuestPermission(){
        new AlertDialog.Builder(getActivity())
                .setMessage("上傳相片需要取得讀取資料的權限")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                },
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            finish();
                    }
                })
                .show();
    }

    private void ReadProfile() {
        mName.setText(list.get(0).FirstName.toString() +"  "+ list.get(0).LastName.toString());
        mNickName.setText(list.get(0).NickName.toString());
        mGender.setText(list.get(0).Gender.toString());
        mAge.setText(list.get(0).Age.toString());
        mPhone.setText(list.get(0).PhoneNumber.toString());
        mInteresting.setText(list.get(0).Interesting.toString());
        mSelfintroduction.setText(list.get(0).Selfintroduction.toString());

        ReadPicture("member/"+mParam2+"/pic.jpg", imageView);
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

    @Override
    public boolean onBackPressed(){
        return BackHandlerHelper.handleBackPress(this);
    }


}
