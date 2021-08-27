package com.app.automatorjava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.Until;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.util.ArrayList;
import static androidx.test.uiautomator.Until.findObject;
import static java.lang.Thread.sleep;

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
        

        //open list of application
        UiObject2 allApps = mDevice.findObject(By.res("com.google.android.apps.nexuslauncher:id/workspace"));
        allApps.click();

        allApps.scroll(Direction.DOWN, 2.0f);

        sleep(2000);

        //open Youtube application
        UiObject2 youtubeApp = mDevice.wait(findObject(By.text("YouTube")), 3000);
        youtubeApp.click();

        // Wait up to 4 seconds for the Youtube splash screen
        sleep(4000);

        BySelector logoSelector = By.res("com.google.android.youtube:id/image");


        BySelector status = By.descContains("Account").res("com.google.android.youtube:id/image");


        BySelector networkEntrySelector = By.clazz(FrameLayout.class)
                .hasChild(logoSelector)
                .hasChild(status);

        sleep(3000);

        Boolean isReady = mDevice.wait(Until.hasObject(networkEntrySelector), 3000);
        if(isReady){
            //TODO something
        }

        BySelector searchSelector = By.descContains("Search").res("com.google.android.youtube:id/menu_item_view");
        Boolean checkSearchBtn = mDevice.wait(Until.hasObject(searchSelector), 3000);
        if(checkSearchBtn){
            UiObject2 search = mDevice
                    .wait(Until.findObject(searchSelector),
                            500);

            search.click();
        }

        //search Voyager 2
        UiObject2 search_edit_text = mDevice.wait(findObject(By.res("com.google.android.youtube:id/search_edit_text")), 2000);
        search_edit_text.click();
        search_edit_text.setText("Voyager 2");

        mDevice.pressEnter();

        UiObject2 searchRecycler = mDevice.wait(findObject(By.res("com.google.android.youtube:id/results")), 6000);

        if(searchRecycler.getChildren().size() > 0){
            //click detail
            searchRecycler.getChildren().get(0).click();
        }

        Thread.sleep(5000);

        //go and check video details
        BySelector videoDetails = By.descContains("Video player").res("com.google.android.youtube:id/watch_player");
        Boolean videoViewExist = mDevice.wait(Until.hasObject(videoDetails), 3000);
        if(videoViewExist){
            //Click Like Video
            ArrayList<UiObject2> buttonList = (ArrayList<UiObject2>) this.mDevice.findObject(By.clazz("com.google.android.youtube:id/like_button")).getChildren();
            UiObject2 likeBtn = buttonList.get(0);
            likeBtn.click();

            UiObject2 likeToast = mDevice.wait(Until.findObject(By.descContains("Added to Liked videos").res("com.google.android.youtube:id/message")), 500);

            UiObject2 subscribeBtn = mDevice.wait(findObject(By.res("com.google.android.youtube:id/subscribe_button")), 2000);
            if(subscribeBtn.getText().equals("SUBSCRIBE")){
                subscribeBtn.click();
            }

            boolean screenshot = mDevice.takeScreenshot(new File("/sdcard/Download/youtube.jpg"));

            Bundle bundle = new Bundle();
            bundle.putBoolean("SCREENSHOT ", screenshot);
            InstrumentationRegistry.getInstrumentation().sendStatus(0, bundle);


            UiObject2 channelTitle = mDevice.wait(findObject(By.res("com.google.android.youtube:id/channel_title")), 2000);

            Toast.makeText(InstrumentationRegistry.getInstrumentation().getContext(),
                    "You are now subscribed to : "+channelTitle.getText(), Toast.LENGTH_LONG).show();

            UiObject2 subscribeToast = mDevice.wait(Until.findObject(By.res("com.google.android.youtube:id/message")),
                            500);

            if(subscribeToast.getText().equals(STRING_TOAST_RESULT)){
                bundle.putString("SUBSCRIBE ", "SUCCESS");
                InstrumentationRegistry.getInstrumentation().sendStatus(0, bundle);
            }else{
                bundle.putString("SUBSCRIBE ", "ERROR");
                InstrumentationRegistry.getInstrumentation().sendStatus(0, bundle);
            }
        }
    }

    //TODO need to make compatiable with Android R,Q Launcher
    private void openApp(){
        String launcherPackage = mDevice.getLauncherPackageName();
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(YOUTUBE_PKG_NAME);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // Make sure each launch is a new task
            context.startActivity(intent);
            mDevice.wait(Until.hasObject(By.pkg(YOUTUBE_PKG_NAME).depth(0)), LAUNCH_TIMEOUT);
        } else {
            String err = String.format("(%s) No launchable Activity.\n", YOUTUBE_PKG_NAME);
            Log.e("YoutubeTest", err);
            Bundle bundle = new Bundle();
            bundle.putString("ERROR", err);
            InstrumentationRegistry.getInstrumentation().finish(1, bundle);
        }
    }

    //later usage
    private void takeScreenshot() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "app_screenshots");

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d("Screenshot Test", "Failed create directory");
            }
        }

        File file = new File(dir.getPath() + File.separator + "youtube_test" + ".png");

        mDevice.takeScreenshot(file);
    }


    @After
    public void tearDown() {
        mDevice.pressHome();
    }
}
