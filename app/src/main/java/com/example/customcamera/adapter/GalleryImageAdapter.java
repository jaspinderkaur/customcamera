package com.example.customcamera.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.customcamera.R;
import com.example.customcamera.helper.CameraHelper;
import com.example.customcamera.model.GalleryItem;

import java.util.Collections;
import java.util.List;

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.GalleryViewHolder> {

  private List<GalleryItem> mImageList = Collections.emptyList();
  private Context mContext;

  public GalleryImageAdapter(Context context, List<GalleryItem> imageList) {
    mContext = context;
    mImageList = imageList;
  }

  @Override
  public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_gallery_item, parent, false);
    return new GalleryViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final GalleryViewHolder holder, final int position) {
    final GalleryItem galleryItem = mImageList.get(position);
    holder.mGalleryImage.setImageURI(Uri.parse(galleryItem.getImagePath()));
  }

  @Override
  public int getItemCount() {
    return mImageList.size();
  }

  public static class GalleryViewHolder extends RecyclerView.ViewHolder {

    private ImageView mGalleryImage;

    public GalleryViewHolder(View itemView) {
      super(itemView);
      mGalleryImage = (ImageView) itemView.findViewById(R.id.ivGalleryImage);
    }
  }
}
