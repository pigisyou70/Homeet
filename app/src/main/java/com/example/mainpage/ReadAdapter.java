package com.example.mainpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;

import static com.example.mainpage.Fragment_contact.ContactArray;
import static com.example.mainpage.Fragment_contact.Message;

public class ReadAdapter extends SimpleAdapter {

    public ReadAdapter(Context context,
                       List<? extends Map<String, ?>> data,
                       int resource,
                       String[] from,
                       int[] to) {

        super(context, data, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        super.getView(position,convertView,parent);

        View view = super.getView(position,convertView,parent);

        final TextView t = view.findViewById(R.id.ReadyMessages);
        final ImageView imageView = view.findViewById(R.id.Sticker);
        final TextView says = view.findViewById(R.id.says);

        if(MainActivity.ContactPersonMessages.get(position) == 0){
            t.setVisibility(View.GONE);
        }

        ReadPicture(ContactArray.get(position)+"/pic.jpg",imageView);

        if ( Message.get(position).length() > 16 ){
            says.setText(Message.get(position).substring(0,15) +" ...");
        }

        return view;
    }

    public void ReadPicture(String path, final ImageView v){
        // 取得　Storage 實體
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("member").child(path); // ------ PhoneNumber + "/pic.jpg"

        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
