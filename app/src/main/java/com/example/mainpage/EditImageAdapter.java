package com.example.mainpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;

import java.util.ArrayList;

/**
 * https://stackoverflow.com/questions/24471109/recyclerview-onclick
 * https://codertw.com/android-%E9%96%8B%E7%99%BC/349521/
 * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0327/2647.html
 */
public class EditImageAdapter extends RecyclerView.Adapter<EditImageAdapter.FileViewHolder2> {
    private final ArrayList<byte[]> paths;
    private final Context context;
    private int imageSize;
    private static ClickListener clickListener;

    public EditImageAdapter(Context context, ArrayList<byte[]> paths) {
        this.context = context;
        this.paths = paths;
        setColumnNumber(context, 4);// context = EditObjectActivity
    }

    private void setColumnNumber(Context context, int columnNum) {
        WindowManager wm = (WindowManager) context.getSystemService( Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();// DisplayMetrics{density=2.625, width=1080, height=1794, scaledDensity=2.625, xdpi=420.0, ydpi=420.0}
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels; // metrics.widthPixels = 1080
        imageSize = widthPixels / columnNum;
    }

    @Override
    public FileViewHolder2 onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView =
                LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.griditem_addpic, viewGroup, false);

        FileViewHolder2 viewHolder = new FileViewHolder2(itemView);

        // 修改 Item 尺寸
        itemView.getLayoutParams().height = 400;
        itemView.getLayoutParams().width = 400;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FileViewHolder2 holder, int position) {
        byte[] path = paths.get(position);
        Glide.with(context)
            .load(path)
            // 設置等待時的圖片
            .placeholder(droidninja.filepicker.R.drawable.image_placeholder)
            //设置图片加载的优先级
            .priority(Priority.HIGH)
            .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }




    public void setOnItemClickListener(ClickListener clickListener) {
        EditImageAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }



    public static class FileViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        AppCompatImageView imageView;
        AppCompatImageView deleteImage;
        private boolean click;

        public FileViewHolder2(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            imageView = itemView.findViewById( R.id.iv_photo);
            deleteImage = itemView.findViewById( R.id.deleteImage);

            // 這邊的 OnClickListener 優先權 First
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // view = androidx.appcompat.widget.AppCompatImageView {acab980 VFE...CL. ...P.... 0,0-400,400}
                    Log.d("TEST",view+"");

                }
            });
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);

        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClick(getAdapterPosition(), view);
            return false;
        }






    }
}