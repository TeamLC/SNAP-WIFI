package com.Dev.unknown.snapwifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.developer.kalert.KAlertDialog;
import com.kakao.adfit.ads.ba.BannerAdView;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.Dev.unknown.snapwifi.Cam.Result_Text;

public class Load extends AppCompatActivity {
    private static final String TAG = "To WIFI";
    //BackPress values
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    //AdFit
    private BannerAdView adView;
    private BannerAdView adView2;
    // WIFI & network
    WifiManager WIFI_Manger;
    ConnectivityManager connectivityManager;
    NetworkInfo WIFI;
    //Save WIFI AP list
    private ArrayList List = new ArrayList<String>();
    //String files Text
    private String onWIFIing;
    private String Close;
    private String backFirst;
    private String backOnemoreClose;
    private String scanningWIFI;
    private String wifiConnectSuccess;
    private String wifiConnectFail;
    private String wifiConnectError;
    private String wifiSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);

        Log.d(TAG, Result_Text);

        //Retrieve string files Text
        onWIFIing = getResources().getString(R.string.on_wifi_ing);
        String onWIFI = getResources().getString(R.string.on_wifi);
        String OK = getResources().getString(R.string.ok);
        Close = getResources().getString(R.string.close);
        backFirst = getResources().getString(R.string.back_first);
        backOnemoreClose = getResources().getString(R.string.back_onemore_close);
        scanningWIFI = getResources().getString(R.string.scanning_wifi);
        wifiConnectSuccess = getResources().getString(R.string.wifi_connect_success);
        wifiConnectFail = getResources().getString(R.string.wifi_connect_fial);;
        wifiConnectError = getResources().getString(R.string.wifi_connect_error);;
        wifiSetting = getResources().getString(R.string.wifi_setting);;

        //Load adfit
        adView = findViewById(R.id.adView);
        adView.setClientId("DAN-t4yy5bfqsj8i");
        adView.loadAd();
        adView2 = findViewById(R.id.addView);
        adView2.setClientId("DAN-t4yy5bfqsj8i");
        adView2.loadAd();

        //Retrieve WIFI & Network status
        WIFI_Manger = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        WIFI = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //IF WIFI is OFF
        if (WIFI_Manger.isWifiEnabled() == false && Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
        {
            Log.d(TAG,"New OS");
            KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE);
            pDialog.setTitleText(onWIFI);
            pDialog.setConfirmText(OK);
            pDialog.setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                @Override
                public void onClick(KAlertDialog kAlertDialog) {
                    if (WIFI_Manger.isWifiEnabled() == true)
                    {
                        kAlertDialog.cancel();
                        WIFI_Connected();
                    }
                }
            });
            pDialog.setCancelable(false);
            pDialog.show();
        }
        else if (WIFI_Manger.isWifiEnabled() == false)
        {
            Log.d(TAG,"Old OS");
            WIFI_Manger.setWifiEnabled(true);
            WIFI_Connected();
        }
        else
            WIFI_Connected();
    }

    //Try to WIFI connect
    public void WIFI_Connected()
    {
        if (WIFI_Manger.isWifiEnabled() == true)
        {
            //IF already WIFI connected
            if (WIFI.isConnected())
            {
                KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE);
                pDialog.setTitleText(onWIFIing);
                pDialog.setConfirmText(Close);
                pDialog.setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        ActivityCompat.finishAffinity(Load.this);
                    }
                });
                pDialog.setCancelText(backFirst);
                pDialog.setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        finish();
                        startActivity(new Intent(Load.this, Cam.class));
                    }
                });
                pDialog.setCancelable(false);
                pDialog.show();
            }
            else
            {
                new SpotsDialog.Builder()
                        .setContext(this)
                        .setMessage(scanningWIFI)
                        .setCancelable(false)
                        .build()
                        .show();
                initWIFIScan();
            }
        }
    }

    //init WIFI SCAN
    public void initWIFIScan()
    {
        Log.d(TAG,"start to scan");
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    Log.d(TAG,"success!");
                    scanSuccess();
                } else {
                    // scan failure handling
                    Log.d(TAG,"fail...");
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = WIFI_Manger.startScan();
        if (!success) {
            Log.d(TAG,"scan start failed... ");
            // scan failure handling
            scanFailure();
        }
    }

    SpotsDialog.Builder builder;
    //Success scan
    private void scanSuccess() {
        Log.d(TAG,"success scan method");
        List<ScanResult> results = WIFI_Manger.getScanResults();
        Log.d(TAG,"Result success : " + results);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
        {

        }
        else
        {
            WifiConfiguration WIFI_Config = new WifiConfiguration();
            WIFI_Config.SSID = String.format("\"%s\"", "");
            WIFI_Config.preSharedKey = String.format("\"%s\"", Result_Text);
            int netId = WIFI_Manger.addNetwork(WIFI_Config);
            //wifimanager.disconnect();
            WIFI_Manger.enableNetwork(netId,false);
            WIFI_Manger.reconnect();
        }

        for (int i = 0; i < results.size(); i++)
        {
            ScanResult Result = results.get(i);
            String Capabilities =  Result.capabilities;
            Log.d(TAG, "Index : " + i);
            //Blocking Free carrier WIFI
            if(Capabilities.contains("EAP"))
            {
                continue;
            }
            //Automatic connection
            else
            {
//                builder.setContext(Load.this)
//                        .setMessage(Result.SSID)
//                        .setCancelable(true)
//                        .build()
//                        .show();
//
//                builder.setCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialogInterface) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                new SpotsDialog.Builder()
//                        .setContext(this)
//                        .setMessage(Result.SSID + "에 연결 시도중...")
//                        .setCancelable(false)
//                        .build()
//                        .show();
                List.add(Result.SSID);
                Log.d(TAG,"SSID : " + Result.SSID);
                WifiConfiguration WIFI_Config = new WifiConfiguration();
                WIFI_Config.SSID = String.format("\"%s\"", Result.SSID);
                WIFI_Config.preSharedKey = String.format("\"%s\"", Result_Text);
                int netId = WIFI_Manger.addNetwork(WIFI_Config);
                //wifimanager.disconnect();
                WIFI_Manger.enableNetwork(netId,false);
                WIFI_Manger.reconnect();
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if (WIFI.isConnected())
//                    Show_Result();
            }
        }
        Show_Result();
    }

    //Fail scan
    private void scanFailure() {
        Log.d(TAG,"fail scan method");
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = WIFI_Manger.getScanResults();
        Log.d(TAG,"Result fail : " + results);
    }

    //Remove all AP except connected AP
    private void Show_Result()
    {
        Log.d(TAG,"Done!");
//        WifiInfo wifiInfo = WIFI_Manger.getConnectionInfo();
//        String SSID = new String(wifiInfo.getSSID());
//        SSID = SSID.substring(1, SSID.length()-1);
//        Log.d("List of SSID : ", SSID);
//
//        for (int i = 0; i < List.size(); i++)
//        {
//            WifiConfiguration WIFI_Config = new WifiConfiguration();
//            WIFI_Config.SSID = String.format("\"%s\"", List.get(i));
//            WIFI_Config.preSharedKey = String.format("\"%s\"", Result_Text);
//            int netId = WIFI_Manger.addNetwork(WIFI_Config);
//
//            if(SSID.equals(List.get(i)))
//            {
//                continue;
//            }
//            else
//            {
//                Log.d("WIFI AP?", (String) List.get(i));
//                WIFI_Manger.removeNetwork(netId);
//                WIFI_Manger.saveConfiguration();
//            }
//        }
//
        //Show wifi connection result
        if (WIFI.isConnected())
        {
            KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
            pDialog.setTitleText(wifiConnectSuccess);
            pDialog.setConfirmText(Close);
            pDialog.setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog kAlertDialog) {
                            moveTaskToBack(true);
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
            pDialog.setCancelText(backFirst);
            pDialog.setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog kAlertDialog) {
                            System.exit(0);
                        }
                    });
            pDialog.setCancelable(false);
            pDialog.show();
        }
        else
        {
            KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
            pDialog.setTitleText(wifiConnectFail);
            pDialog.setContentText(wifiConnectFail);
            pDialog.setConfirmText(wifiSetting);
            pDialog.setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                @Override
                public void onClick(KAlertDialog kAlertDialog) {
                    /*moveTaskToBack(true);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());*/
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                }
            });
            pDialog.setCancelText(backFirst);
            pDialog.setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                @Override
                public void onClick(KAlertDialog kAlertDialog) {
                    System.exit(0);
                }
            });
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }
}
