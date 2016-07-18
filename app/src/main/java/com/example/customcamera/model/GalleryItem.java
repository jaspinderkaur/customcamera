package com.example.customcamera.model;

/**
 * Created by jaspinder on 7/17/16.
 */
public class GalleryItem {

  private String mImagePath = "";

  public GalleryItem(String imagePath) {
    mImagePath = imagePath;
  }

  public String getImagePath() {
    return mImagePath;
  }

  public void setImagePath(String imagePath) {
    mImagePath = imagePath;
  }
}
