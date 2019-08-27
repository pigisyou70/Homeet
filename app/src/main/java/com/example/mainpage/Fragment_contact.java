package com.example.mainpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static com.example.mainpage.MainActivity.ContactPersonMessages;

public class Fragment_contact extends Fragment implements FragmentBackHandler{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mAuth;

    // Component
    public static ArrayList<String> ContactArray;
    private ListView listOfMessages ;

    // 儲存聯絡人暱稱
    private ArrayList<String> Contact_Person;

    private ArrayList<Contact> Person = new ArrayList<>();
    public static ArrayList<String> Message;
    private ArrayList<String> Names;
    private ArrayList<Integer> Stickers;
    private ArrayList<ImageView> Stickerss;
    private ArrayList<Integer> Reads;

    private int ContactNumber = 0;
    private int Number = 0;
    private int i = 1;
    private String TAG = "Fragment_Contact";
    TextView ReadyMessages ;


    static public boolean Contactcounter = false;


    public Fragment_contact() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_message newInstance(String param1, String param2) {
        Fragment_message fragment = new Fragment_message();
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
        View view = inflater.inflate( R.layout.fragment_contact, null);
        View view1 = inflater.inflate(R.layout.simple_contact, null);

        ReadyMessages = view1.findViewById(R.id.ReadyMessages);
        listOfMessages = (ListView)view.findViewById(R.id.list_of_messages);

        // ArrayList init
        ContactArray = new ArrayList<>();
        Contact_Person = new ArrayList<>();
        Message = new ArrayList<>();
        Names = new ArrayList<>();
        Stickers = new ArrayList<>();
        Reads = new ArrayList<>();


        Stickerss = new ArrayList<>();



        Number = 0;

        return view;
    }


    // 顯示聯絡人清單
    public void displayContactList() {
        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        // 抓全部聯絡人
        DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("member/" + mAuth.getCurrentUser().getPhoneNumber()+"/Content");
        reference_contacts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Number = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.exists()){
                        // 加入資料
                        ContactArray.add(ds.getKey());
                    }
                }
                // 先取得聯絡清單有多少人，
                ContactNumber = ContactArray.size(); // 內有幾個聯絡人，就有幾個
                // 因為 ContactArray 從 0 開始 , 所以需要先 -1
                ContactNumber--; // 1
                // 假如我有聯絡人清單 ，就抓取他們的 的 個人資料全部
                while (Number <= ContactNumber){
                    final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("member/"+ContactArray.get(Number));
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Contact ct = dataSnapshot.getValue(Contact.class);
//                            Log.d("測試","目前讀取到用戶 - "+ct.NickName);
                            Person.add(ct);
                            mRef.child("Content").child(mAuth.getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    i = 1;
                                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                                        if (i == dataSnapshot.getChildrenCount()){
                                            ChatMessage chatMessage = ds.getValue(ChatMessage.class);
//                                            Log.d("測試","的最新消息為 - "+chatMessage.getMessageText());

                                            Message.add(chatMessage.getMessageText());
                                        }
                                        i++;
                                    }
                                    // 表示他是新增的好友
                                    if (dataSnapshot.getChildrenCount() == 0)
                                        Message.add("");


                                    if (Message.size() == Person.size())
                                        Show();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Number++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Show(){
        // 當按下 下方的留言按鈕，就會觸發這個涵式
        List<Map<String, Object>> listitem = new ArrayList<>();
        for (int j = 0; j < Person.size(); j ++){
            Map<String, Object> showitem = new HashMap<>();

            Names.add(Person.get(j).NickName.toString());
//            Log.d("執行Show","現在準備顯示第"+j+"個 - "+Names.get(j));

            Stickers.add(R.drawable.icon_member);

//            Log.d("執行Show","有多少個他的未讀訊息 - " + ContactPersonMessages);
            // 這邊是小紅點的顯示
            if (ContactPersonMessages == null)
                return;

            if (ContactPersonMessages.get(j) >= 0){
                Reads.add(ContactPersonMessages.get(j));
                showitem.put("Reads", Reads.get(j));
            }

            showitem.put("Stickers", Stickers.get(j));
            showitem.put("Names", Names.get(j));
            showitem.put("Says", Message.get(j));
            listitem.add(showitem);
        }

        if (getActivity() == null)
            return;
        ReadAdapter myAdapter = new ReadAdapter(
                getActivity(),
                listitem,
                R.layout.simple_contact,
                new String[]{"Stickers", "Names", "Says","Reads"},
                new int[]{R.id.Sticker, R.id.name, R.id.says, R.id.ReadyMessages});

        Contactcounter = true;

        listOfMessages.setAdapter(myAdapter);
        listOfMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView list = (ListView)adapterView;
                list.getItemAtPosition(i).toString(); // 顯示所點擊的 TextView
                Fragment_message fragmentMessage = new Fragment_message();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.id_frame,fragmentMessage.newInstance(Names.get(i),ContactArray.get(i)))
                        .addToBackStack(null) // 按下返回鍵會返回此Fragment
                        .commit();

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
    public void onResume(){
        super.onResume();
        displayContactList();
//        Log.d("FragmentContact", "onResume");
    }


    @Override
    public void onPause(){
        super.onPause();
//        Log.d("FragmentContact", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.d("FragmentContact", "onStop");

        ContactArray.clear();
        Contact_Person.clear();
        Person.clear();
        Message.clear();
        Names.clear();
        Stickers.clear();
        Reads.clear();

        Contactcounter = false;
    }

    public void onDestroy(){
        super.onDestroy();
//        Log.d("FragmentContact", "onDestroy");
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
