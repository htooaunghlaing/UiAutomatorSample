package com.app.automatorjava;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.util.ArrayList;
import static androidx.test.uiautomator.Until.findObject;
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class YoutubeTest {

    private static final String YOUTUBE_PKG_NAME = "com.google.android.youtube";
    private UiDevice mDevice;
    private static final String STRING_TOAST_RESULT = "Subscription added";
    private static final int LAUNCH_TIMEOUT = 5000;

    @Before
    public void setup() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressHome();

    }

    @Test
    public void testYouTubeApp() throws InterruptedException, UiObjectNotFoundException {
        
        openApp();
        //open list of application
//        UiObject2 allApps = mDevice.findObject(By.res("com.google.android.apps.nexuslauncher:id/workspace"));
//        allApps.click();
//
//        allApps.scroll(Direction.DOWN, 2.0f);
//
//        sleep(2000);
//
//        //open Youtube application
//        UiObject2 youtubeApp = mDevice.wait(findObject(By.text("YouTube")), 3000);
//        youtubeApp.click();

        // Wait up to 4 seconds for the Youtube splash screen
        sleep(4000);

        grantPermission();

        BySelector ssidSelector = By.res("com.google.android.youtube:id/image");


        BySelector status = By.descContains("Account").res("com.google.android.youtube:id/image");


        BySelector networkEntrySelector = By.clazz(FrameLayout.class)
                .hasChild(ssidSelector)
                .hasChild(status);

        sleep(3000);

        Boolean isReady = mDevice.wait(Until.hasObject(networkEntrySelector), 3000);
        if(isReady){
            grantPermission();
        }

        ArrayList<UiObject2> menuList = (ArrayList<UiObject2>) this.mDevice.findObject(By.clazz("android.support.v7.widget.LinearLayoutCompat")).getChildren();

//        for (UiObject2 button : menuList) {
//            for (UiObject2 innerItem : button.getChildren()) {
//                    for(UiObject2 searchBtn : innerItem.getChildren()){
//                        if(searchBtn.getContentDescription().equals("Search")){
//                            searchBtn.click();
//                        }
//                    }
//            }
//        }
//        /* click the menu item at index 1 */
        UiObject2 menu_action = menuList.get(1);
        menu_action.click();

        UiObject2 search_edit_text = mDevice.wait(findObject(By.res("com.google.android.youtube:id/search_edit_text")), 2000);
        search_edit_text.click();
        search_edit_text.setText("Voyager 2");

        mDevice.pressEnter();

        UiObject2 searchRecycler = mDevice.wait(findObject(By.res("com.google.android.youtube:id/results")), 6000);


        while (mDevice.hasObject(By.res(mDevice.getCurrentPackageName(), "com.google.android.youtube:id/results"))) {

            ArrayList<UiObject2> searchList = (ArrayList<UiObject2>) searchRecycler.getChildren();
            searchList.get(0).click();

            ArrayList<UiObject2> buttonList = (ArrayList<UiObject2>) this.mDevice.findObject(By.clazz("com.google.android.youtube:id/button_container")).getChildren();
            UiObject2 likeBtn = buttonList.get(0);
            likeBtn.click();

            UiObject2 subscribeBtn = mDevice.wait(findObject(By.res("com.google.android.youtube:id/subscribe_button")), 2000);
            assertEquals("SUBSCRIBED", subscribeBtn.getText());

            takeScreenshot();

            // Verify the test is displayed in the Ui
            UiObject2 changedText = mDevice
                    .wait(Until.findObject(By.res("com.google.android.youtube:id/message")),
                            500 /* wait 500ms */);

            assertThat(changedText.getText(), is(equalTo(STRING_TOAST_RESULT)));
        }
    }

    private void openApp(){
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(YOUTUBE_PKG_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);
        mDevice.wait(Until.hasObject(By.pkg(YOUTUBE_PKG_NAME).depth(0)), LAUNCH_TIMEOUT);
    }

    private void takeScreenshot() {
//        File path = new File("/sdcard/Pictures/test.png");
//        mDevice.takeScreenshot(path);
        File dir =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "app_screenshots");

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d("Screenshot Test", "Failed create directory");
            }
        }

        File file = new File(dir.getPath() + File.separator + "youtube_test" + ".jpg");

        mDevice.takeScreenshot(file);
    }

    private void grantPermission() throws UiObjectNotFoundException {
        if (Build.VERSION.SDK_INT >= 23) {
            String text;
            if(Build.VERSION.SDK_INT == 23){
                text = "Allow";
            }else if(Build.VERSION.SDK_INT <= 28){
                text = "ALLOW";
            }else if(Build.VERSION.SDK_INT == 29){
                text = "Allow only while using the app";
            }else{
                text = "While using the app";
            }
            UiObject allowPermission = mDevice.findObject(new UiSelector().text(text));
            if (allowPermission.exists()) {
                allowPermission.click();
            }
        }
    }

    @After
    public void tearDown() {
        mDevice.pressHome();
    }


}
