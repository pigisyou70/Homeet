package com.example.mainpage;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class Fragment_login extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Fragment_member fragmentMember;
    private Button button4;
    private String verifiID;
    private EditText phoneNumber, verifyCode;
    private TextView message;
    private FirebaseAuth mAuth;
    private String userToken;

    private static int Timeout = 60;
    private boolean dontplay = false;

    private Handler handler;
    private ss thread;
    private String TAG = "Fragment_login";

    public Fragment_login() {
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

        View v = inflater.inflate(R.layout.fragment_login,
                container, false);

        // 請求簡訊認證碼
        Button button1 = (Button) v.findViewById(R.id.btn_GetSMSCode);
        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(phoneNumber.getText().toString().length() == 10 && !dontplay){
                    String userPhone = phoneNumber.getText().toString();
                    userPhone = userPhone.substring(1);
                    userPhone = "+886" + userPhone;
//                    Log.d("請輸入手機號碼" , userPhone);

                    dontplay = true;
                    // 建立認證碼 Callback Function
                    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                            Log.d("onVerificationCompleted", phoneAuthCredential.toString());
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                        }

                        public void onCodeSent(String verificationid, PhoneAuthProvider.ForceResendingToken token){
                            verifiID = verificationid;
                        }
                    };

                    // 執行要求認證
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(userPhone,60,TimeUnit.SECONDS,getActivity(),mCallbacks);
                    Timeout = 60;
                    message.setText("請在 1 分鐘內輸入驗證碼, 否則將失效");
                    thread = new ss();
                    thread.start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 延時執行的程式
                        }
                    },500); // 延时0.5秒

                    button4.setEnabled(true);
                }
                else{
                    message.setText("請確認電話號碼及驗證碼是否輸入正確 !");
                }
            }
        });

        // 確認驗證碼並登入
        Button button2 = (Button) v.findViewById(R.id.btn_Login);
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(verifyCode.getText().length() == 6 ){
                    String code = verifyCode.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifiID, code);
                    mAuth.signInWithCredential(credential)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // 取得驗證結果.取得使用者.取得電話號碼
                                    final String phoneNum = authResult.getUser().getPhoneNumber();

                                    // 讀取資料庫查看是否有資料存在, foreach 持續讀取 result 判定
                                    DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("member");
                                    reference_contacts.addValueEventListener(new ValueEventListener() {
                                        boolean result = false;
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                                if(ds.getKey().equals(phoneNum)){
                                                    result = true;
                                                }
                                            }
                                            if( !result ){
                                                // 原本電話號碼為 國碼 + 電話號碼, 把國碼去掉
                                                String str = phoneNum.substring(4);
                                                // 補上 0
                                                str = "0" + str;

                                                initData data = new initData(str);
                                                FirebaseDatabase.getInstance().getReference().child("member/"+phoneNum).setValue(data);

                                                // 儲存內建圖片
                                                Resources res = getContext().getResources();
                                                int id = R.mipmap.ic_user_60dp;
                                                Bitmap b = BitmapFactory.decodeResource(res, id);
//                                                EditProfileActivity used = new EditProfileActivity();
//                                                used.UploadPicture(b,mAuth.getCurrentUser().getPhoneNumber());
                                            }
                                            // 跳轉至 Profile 頁面
                                            JumpActivity(phoneNum);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
                else{
                    message.setText("請確認驗證碼是否輸入正確 !");
                }
            }
        });
        return v;
    }

    // 原本在 Activity onCreate 改成寫在此
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneNumber = getView().findViewById(R.id.phoneNumber);
        verifyCode = getView().findViewById(R.id.verifyCode);
        message = getView().findViewById(R.id.messageTexe);
        button4 = getView().findViewById(R.id.btn_Login);

        mAuth = FirebaseAuth.getInstance();
        fragmentMember = new Fragment_member();

        // 更新登入狀態
        if(mAuth.getCurrentUser() != null){
            // 將自己的手機號碼訂閱至 FCM
            String phone = mAuth.getCurrentUser().getPhoneNumber();
            phone = phone.substring(4);
            phone = "0"+phone;
            FirebaseMessaging.getInstance().subscribeToTopic(phone);

            // Fragment 跳轉至 Fragment
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.id_frame,Fragment_member.newInstance("CurrentPhoneNumber",mAuth.getCurrentUser().getPhoneNumber()))
//                    .addToBackStack(null) // 按下返回鍵會返回此Fragment
                    .remove(this)
                    .commit();
        }

        // Origin Problem: 在 Thread 修改 Button.setText, 會造成錯誤
        // 利用 Handler 去執行這個行為
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.getData().getInt("times") > 0)
                    message.setText("請在 1 分鐘內輸入驗證碼, 否則將失效\n"
                            +"剩餘輸入驗證碼時間為："+msg.getData().getInt("times")+"sec");

                else if(msg.getData().getInt("times") == 0){
                    message.setText("請在 1 分鐘內輸入驗證碼, 否則將失效\n"
                            +"剩餘輸入驗證碼時間為："+msg.getData().getInt("times")+"sec\n"
                            +"驗證碼已失效, 請重新獲取認證碼");
                    button4.setEnabled(false);
                    dontplay = false;

                }
            }
        };
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

    // 跳轉頁面
    private void JumpActivity(String phoneNum){
        // 把驗證碼時效取消
        Timeout = -1;
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.id_frame,Fragment_member.newInstance("CurrentPhoneNumber",mAuth.getCurrentUser().getPhoneNumber()))
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void getToken(){
        // 獲取手機的 token, 給予 FCM 做推播使用
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                if( task.getResult() == null) return;
                // Get new Instance ID token
                userToken = task.getResult().getToken();
                // Log and toast
                Log.i("token",""+userToken);
                // 將此用戶訂閱主題
                FirebaseMessaging.getInstance().subscribeToTopic("news");
            }
        });
    }

    class ss extends Thread{
        @Override
        public void run(){
            while(Timeout >= 0 ){
                Bundle d = new Bundle();
                d.putInt("times",Timeout);
                Message a = new Message();
                a.setData(d);
                handler.sendMessage(a);
                Timeout--;
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class initData {
        public String FirstName;
        public String LastName;
        public String NickName;
        public String Gender;
        public String Age;
        public String PhoneNumber;
        public String Reservation ;
        public String MyRentObj;
        public String Content;

        public String Interesting; // 興趣
        public String Selfintroduction; // 興趣


        initData(String phone){
            FirstName = "First";
            LastName = "Last";
            NickName = "我愛Homeet";
            Gender ="男性";
            Age = "99";
            PhoneNumber = phone;
            Reservation = "null";
            MyRentObj = "null";
            Content = "null";
            Interesting = "";
            Selfintroduction = "";
        }
    }

}
