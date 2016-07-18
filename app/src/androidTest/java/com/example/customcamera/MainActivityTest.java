package com.example.customcamera;

import android.support.design.widget.FloatingActionButton;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import com.example.customcamera.controller.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by jaspinder on 7/17/16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainActivityTest{

  private MainActivity mActivity;
  private FloatingActionButton mFabButton;
  private TextView mMessage;
  private String messageString;

  @Before
  public void createLogHistory() {
    //mActivity = this.getActivity();
    mFabButton = (FloatingActionButton) mActivity.findViewById(R.id.fab);
    mMessage = (TextView) mActivity.findViewById(R.id.tvMessage);
    messageString = mActivity.getString(R.string.help_text);
  }

  @Test
  public void testPreconditions() {
    //assertThat(mFabButton,is(null));
  }

  @Test
  public void testText() {
    //assertEquals(messageString, (String) mMessage.getText());
  }

}
