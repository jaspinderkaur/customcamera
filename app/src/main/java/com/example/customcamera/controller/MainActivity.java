package com.example.customcamera.controller;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.customcamera.R;
import com.example.customcamera.adapter.GalleryImageAdapter;
import com.example.customcamera.helper.RuntimePermissionsActivity;
import com.example.customcamera.model.GalleryItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends RuntimePermissionsActivity {

  private TextView mMessage;
  private RecyclerView mGallery;
  private GalleryImageAdapter mAdapter;
  private List<GalleryItem> mImageList = new ArrayList<>();

  private static final int REQUEST_PERMISSIONS = 20;
  private static final String MY_FOLDER = "MyCameraApp";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mMessage = (TextView) findViewById(R.id.tvMessage);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        MainActivity.super.requestAppPermissions(new String[]{permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.string.camera_permissions_denied
            , REQUEST_PERMISSIONS);
      }
    });
    setImageGallery();
  }

  @Override
  protected void onResume() {
    super.onResume();
    getGalleryImageList();
  }

  private void getGalleryImageList() {
    String ExternalStorageDirectoryPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES).getAbsolutePath();
    String targetPath = ExternalStorageDirectoryPath + "/" + MY_FOLDER + "/";
    File targetDirector = new File(targetPath);
    File[] files = targetDirector.listFiles();
    mImageList.clear();
    if (files != null) {
      for (File file : files) {
        mImageList.add(new GalleryItem(file.getAbsolutePath()));
      }
    }
    mMessage.setVisibility(mImageList.isEmpty() ? View.VISIBLE : View.GONE);
    if (mAdapter != null) {
      mAdapter.notifyDataSetChanged();
    }
  }

  private void setImageGallery() {
    mGallery = (RecyclerView) findViewById(R.id.rvGallery);
    LayoutManager layoutManager = new LinearLayoutManager(this);
    mGallery.setLayoutManager(layoutManager);
    mAdapter = new GalleryImageAdapter(this, mImageList);
    mGallery.setAdapter(mAdapter);
  }

  @Override
  public void onPermissionsGranted(int requestCode) {
    Intent cameraIntent = new Intent(MainActivity.this, CustomCameraActivity.class);
    startActivity(cameraIntent);
  }
}
