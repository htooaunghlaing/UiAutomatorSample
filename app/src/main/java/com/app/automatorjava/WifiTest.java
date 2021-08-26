package com.app.automatorjava;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.RelativeLayout;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.Direction;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.uiautomator.Until.findObject;
import static java.lang.Thread.sleep;

@RunWith(AndroidJUnit4.class)
public class WifiTest {

    private static final Object DEFAULT_TIMEOUT = 2500;
    private UiDevice mDevice;

    @Before
    public void setup() throws UiObjectNotFoundException {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressHome();

    }

    @Test
    public void testWifi() throws InterruptedException {
        //open list of application
        UiObject2 allApps = mDevice.findObject(By.res("com.google.android.apps.nexuslauncher:id/workspace"));
        allApps.click();

        allApps.scroll(Direction.DOWN, 2.0f);

        sleep(2500);

        //open Setting application
        UiObject2 settingApp = mDevice.wait(findObject(By.text("Settings")), 3000);
                // mDevice.findObject(By.res("com.google.android.apps.pixellauncher:id/icon").text("Settings"));
        //mDevice.wait(findObject(By.res("com.google.android.apps.pixellauncher:id/icon").text("Settings")));//
        settingApp.click();

        // Wait up to 2 seconds for the element be displayed on screen
        UiObject2 networkAndInternet = mDevice.wait(findObject(By.text("Network & internet")), 3000);
        networkAndInternet.click();

        // Click on element with text "Wi‑Fi"
        UiObject2 wifi = mDevice.wait(findObject(By.text("Wi‑Fi")), 2500);
        wifi.click();

        // Click on element with text "Add network"
        UiObject2 addNetwork = mDevice.wait(findObject(By.text("Add network")), 2500);
        addNetwork.click();

        // Obtain an instance of UiObject2 of the text field
        UiObject2 ssidField = mDevice.wait(findObject(By.res("com.android.settings:id/ssid")), 2500);
        // Call the setText method using  Kotlin's property access syntax
        String ssid = "AndroidWifi";
        ssidField.setText(ssid);

        //Click on Save button
        UiObject2 saveBtn = mDevice.wait(findObject(By.res("android:id/button1")), 1000);
        //mDevice.findObject(By.res("android:id/button1").text("Save"));
        //if(saveBtn != null){
            saveBtn.click();
//        }else{
//            System.out.println("UI Obj Null");
//        }


        // BySelector matching the just added Wi-Fi
        BySelector ssidSelector = By.text(ssid).res("android:id/title");

        // BySelector matching the connected status
        BySelector status = By.text("Connected").res("android:id/summary");

        // BySelector matching on entry of Wi-Fi list with the desired SSID and status
        BySelector networkEntrySelector = By.clazz(RelativeLayout.class)
            .hasChild(ssidSelector)
                .hasChild(status);

        sleep(3000);

        // Perform the validation using hasObject
        // Wait up to 5 seconds to find the element we're looking for
        Boolean isConnected = mDevice.wait(Until.hasObject(networkEntrySelector), 5000);
        Assert.assertTrue("Verify if device is connected to added Wi-Fi", isConnected);

//        // Perform the validation using Android APIs
//        String connectedWifi = getCurrentWifiSsid();
//        Assert.assertEquals("Verify if is connected to the Wifi", ssid, connectedWifi);

    }

    @After
    public void tearDown(){
        mDevice.pressHome();
    }

    private String getCurrentWifiSsid() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // The SSID is quoted, then we need to remove quotes
        return wifiInfo.getSSID().replace("\"", "");
    }

}

