package com.example.mainpage;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by droidNinja on 29/07/16.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.FileViewHolder> {

  private final ArrayList<String> paths;
  private final Context context;
  private int imageSize;
  public ImageAdapter(Context context, ArrayList<String> paths) {
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
  public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate( R.layout.griditem_addpic, parent, false);

    return new FileViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(FileViewHolder holder, int position) {
    String path = paths.get(position);
    byte[] aa = path.getBytes();
    Glide.with(context)
        .load(new File(path))

//        .apply( RequestOptions.centerCropTransform()
//        .dontAnimate()
//        .override(imageSize, imageSize).fitCenter()
//        .placeholder(droidninja.filepicker.R.drawable.image_placeholder))
//        .thumbnail(0.5f)
        .into(holder.imageView);
  }

  @Override
  public int getItemCount() {
    return paths.size();
  }

  public static class FileViewHolder extends RecyclerView.ViewHolder {

    AppCompatImageView imageView;

    public FileViewHolder(View itemView) {
      super(itemView);
      imageView = itemView.findViewById( R.id.iv_photo);
    }
  }
}