package com.example.robel.xposedmodule;

import android.app.Activity;
import android.os.Environment;

import com.example.robel.xposedmodule.Data.PIIJson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/*
 *IXposedHookLoadPackage
 *Get notified when an app ("Android package") is loaded.
 *This is especially useful to hook some app-specific methods.
 **/

public class XposedClass implements IXposedHookLoadPackage {

    //ArrayList to hold 100 test android packages.
    private ArrayList<String> packageList;
    private List<PIIJson> PIIObjectList= new ArrayList<>();
    /*
     * handleLoadPackage get notified when an app ("Android package") is loaded.
     * */
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) {

        packageList = new ArrayList<>();//ArrayList to store top 100 apps package name.
        packageList.add("com.example.robel.testideavim");
        packageList.add("teamtreehouse.com.iamhere");

        this.hookOnPackage(lpparam);
    }

    /*
     * Method that verifies whether to hook on package or not.
     * i.e it will only hook to packages that are in the packageList arrayList,
     * , which stores those 100 apps that need to be tested.
     * */
    private void hookOnPackage(LoadPackageParam lpparam){
        //Log what "Android package" is loaded.
        XposedBridge.log("\tLoaded app: " + lpparam.packageName);

        //Verifies and hooks only to package that are in the PackageList array.
        if(packageList.contains(lpparam.packageName)) {
            hookOnAppLifecycle(lpparam);
            hookOnDummyApp(lpparam); //com.robel.testIdeaVim.setOutput()
            hookOnGetTypeName(lpparam); //android.net.NetworkInfo.getTypeName()
            hookGetActiveNetworkInfo(lpparam); //android.net.ConnectivityManager.getActiveNetworkInfo()
            hookLocationServiceObject(lpparam);
            hookBuild(lpparam); //com.google.android.gms.common.api.GoogleApiClient.Builder.build();
            hookGetLastLocation(lpparam);//com.google.android.gms.internal.lu
            hookRequestLocationUpdates(lpparam);//com.google.android.gms.internal.lu.requestLocationUpdates
            hookOpenConnection(lpparam);//java.net.URL.openConnection

            hookGetIPAddress(lpparam); //android.net.wifi.WifiInfo.getIPAddress
            hookGetMacAddress(lpparam);//android.net.wifi.WifiInfo.getMacAddress
            hookGetBssid(lpparam);//android.net.wifi.WifiInfo.getBSSID
            hookGetRssi(lpparam);//android.net.wifi.WifiInfo.getRssi
            hookGetSsid(lpparam);//android.net.wifi.WifiInfo.getSSID
            hookGetNetworkId(lpparam);//android.net.wifi.WifiInfo.getNetworkId
            hookGetSimSerialNumber(lpparam);//android.telephony.TelephonyManager.getSimSerialNumber
            hookGetNetworkCountryIso(lpparam);//android.telephony.TelephonyManager.getNetworkCountryIso
            hookGetSimCountryIso(lpparam);//android.telephony.TelephonyManager.getSimCountryIso
            hookGetDeviceSoftwareVersion(lpparam);//android.telephony.TelephonyManager.getDeviceSoftwareVersion
            hookGetVoicemailNumber(lpparam);//android.telephony.TelephonyManager.getVoicemailNumber
            hookGetImei(lpparam);//android.telephony.TelephonyManager.Imei
            hookGetSubscriberId(lpparam);//android.telephony.TelephonyManager.getSubscriberId
            hookGetLine1Number(lpparam);//android.telephony.TelephonyManager.getLine1Number
            hookGetBluetoothAddress(lpparam);//android.bluetooth.BluetoothAdapter.getAddress
        }
    }

    /**
     ***********************************************************
     ******                Hooking Methods                ******
     ***********************************************************
     ** */
    /*
     * This method will hook activity/app lifecycle.
     * OnStart writes a metaData to db.json
     * OnStop writes the actual json object array to db.json file
     * */
    //@GOOD
    private void hookOnAppLifecycle(final LoadPackageParam lpparam) {

        /*
         * public void onCreate(@Nullable Bundle savedInstanceState.
         * */
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", "android.os.Bundle","android.os.PersistableBundle", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                XposedBridge.log("\tInside Activity.class.onCreate() <- called by "+lpparam.packageName);
                String description = "Method name: Activity.class.onCreate()";
                PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.onCreate, description));
//                StringBuilder metaData = new StringBuilder();
//                metaData.append(lpparam.packageName);
//                writeToFile(metaData.toString(), lpparam);
            }
        });

        /*
         * protected void onStart()
         * Creates and writes a meta data.
         */
        XposedHelpers.findAndHookMethod(Activity.class, "onStart", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                XposedBridge.log("\tInside Activity.class.onStart() <- called by "+lpparam.packageName);
                String description = "Method name: Activity.class.onStart()";
                PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.onStart, description));
                StringBuilder metaData = new StringBuilder();
                metaData.append(lpparam.packageName);
                writeToFile(metaData.toString(), lpparam);
            }
        });

        //protected void onPause()
        XposedHelpers.findAndHookMethod(Activity.class, "onPause", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                XposedBridge.log("\tInside Activity.class.onPause() <- called by "+lpparam.packageName);
                String description = "Method name: Activity.class.onPause()";
                PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.onPause, description));
            }
        });

        //protected void onRestart()
        XposedHelpers.findAndHookMethod(Activity.class, "onRestart", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                XposedBridge.log("\tInside Activity.class.onRestart() <- called by "+lpparam.packageName);
                String description = "Method name: Activity.class.onRestart()";
                PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.onRestart, description));
            }
        });

        //protected void onResume();
        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                XposedBridge.log("\tInside Activity.class.onResume() <- called by "+lpparam.packageName);
                String description = "Method name: Activity.class.onResume()";
                PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.onResume, description));
            }
        });

        /*
         * protected void onStop()
         * Write the json file to db.json before onStop()
         * */
        XposedHelpers.findAndHookMethod(Activity.class, "onStop", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                XposedBridge.log("\tInside Activity.class.onStop() <- called by "+lpparam.packageName);
                String description = "Method name: Activity.class.onStop()";
                PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.onStop, description));
                Gson gson = new Gson();
                Type type = new TypeToken<List<PIIJson>>() {}.getType();
                String string = gson.toJson(PIIObjectList, type);
                writeToFile(string, lpparam);

            }
        });

        /*
         * protected void onDestroy()
         * */
        XposedHelpers.findAndHookMethod(Activity.class, "onDestroy", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                XposedBridge.log("\tInside Activity.class.onDestroy() <- called by "+lpparam.packageName);
                String description = "Method name: Activity.class.onDestroy()";
                PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.onDestroy, description));

//                Gson gson = new Gson();
//                Type type = new TypeToken<List<PIIJson>>() {}.getType();
//                String string = gson.toJson(PIIObjectList, type);
//                writeToFile(string, lpparam);
            }
        });

    }

    /*
     *This method will hook android.net.NetworkInfo.getTypeName() function call
     * Return a human-readable name describe the type of the network, for example "WIFI" or "MOBILE".
     * */
    //@GOOD
    private void hookOnGetTypeName(final LoadPackageParam lpparam){
        try {
            findAndHookMethod("android.net.NetworkInfo",
                    lpparam.classLoader,
                    "getTypeName",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("\tInside android.net.NetworkInfo.getTypeName() <- called by "+lpparam.packageName);

                            String description = "Method name: Android.net.NetworkInfo.getTypeName(), " +
                                    "Network Type Name: "+ param.getResult();
                            XposedBridge.log(description);
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getTypeName, description));

                        }
                    });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("METHOD NOT FOUND -> android.net.NetworkInfo.getTypeName() <- called by " + lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND ->android.net.NetworkInfo");
        }
    }

    /*
     *This method will hook android.net.connectivityManager.getActiveNetworkInfo() function call
     * Returns details about the currently active default data network.
     * */
    //@GOOD
    private void hookGetActiveNetworkInfo(final LoadPackageParam lpparam) {
        try {
            findAndHookMethod("android.net.ConnectivityManager",
                    lpparam.classLoader,
                    "getActiveNetworkInfo",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("\tInside android.net.ConnectivityManager.getActiveNetworkInfo() <- called by " + lpparam.packageName);
                            String description = "Method name: Android.net.ConnectivityManager.getActiveNetworkInfo";
                            XposedBridge.log(description);
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getActiveNetworkInfo, description));
                        }
                    });
        }
        catch (NoSuchMethodError e) {
            XposedBridge.log("METHOD NOT FOUND -> android.net.ConnectivityManager.getActiveNetworkInfo() <- called by " + lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.net.ConnectivityManager");
        }

    }

    /*
     *This method will hook com.google.android.gms.common.api.GoogleApiClient.Builder.build() function call
     * */
    //@GOOD
    private void hookBuild(final LoadPackageParam lpparam){
        try {
            findAndHookMethod("com.google.android.gms.common.api.GoogleApiClient.Builder",
                    lpparam.classLoader,
                    "build",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("\tInside com.google.android.gms.common.api.GoogleApiClient.Builder.build() <- called by " + lpparam.packageName);
                            String description = "Method name: com.google.android.gms.common.api.GoogleApiClient.Builder.build" ;
                            XposedBridge.log(description);
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.build, description));
                        }
                    });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("METHOD NOT FOUND -> com.google.android.gms.common.api.GoogleApiClient.Builder.build() <- called by " + lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND ->com.google.android.gms.common.api.GoogleApiClient.Builder <- for build()");
        }
    }

    /*
     *This method will hook com.google.android.gms.common.api.GoogleApiClient.getLastLocation() function call
     * */
    //@GOOD
    private void hookGetLastLocation(final LoadPackageParam lpparam) {
        try {
            findAndHookMethod("com.google.android.gms.internal.lu",
                    lpparam.classLoader,
                    "getLastLocation",
                    "com.google.android.gms.common.api.GoogleApiClient", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("\tcom.google.android.gms.location.FusedLocationProviderApi.getLastLocation() <- called by " + lpparam.packageName);
                            android.location.Location  location = (android.location.Location) param.getResult();
                            String description = "Method name: com.google.android.gms.location.FusedLocationProviderApi.getLastLocation()" +
                                    ", Latitude : " + location.getLatitude() +
                                    ", Longitude : " + location.getLongitude();
                            XposedBridge.log(description);
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getLastLocation, description));
                        }
                    });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("METHOD NOT FOUND -> com.google.android.gms.location.FusedLocationProviderApi.getLastLocation() <- called by" + lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> com.google.android.gms.location.FusedLocationProviderApi.getLastLocation");
        }
    }

    /*
     *This method will hook com.google.android.gms.location.FusedLocationProviderApi.requestLocationUpdate() function call
     * */
    //@GOOD
    private void hookRequestLocationUpdates(final LoadPackageParam lpparam) {
        try {
            //public PendingResult<Status> requestLocationUpdates(GoogleApiClient client, final LocationRequest request, final LocationListener listener)
            findAndHookMethod("com.google.android.gms.internal.lu",
                    lpparam.classLoader,
                    "requestLocationUpdates",
                    "com.google.android.gms.common.api.GoogleApiClient",
                    "com.google.android.gms.location.LocationRequest" ,
                    "com.google.android.gms.location.LocationListener",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("\tcom.google.android.gms.location.FusedLocationProviderApi.requestLocationUpdate() <- called by " + lpparam.packageName);
                            String description ="Method name : com.android.gms.internal.lu.requestLocationUpdates" ;
                            XposedBridge.log(description);
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.requestLocationUpdates, description));
                        }
                    });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("METHOD NOT FOUND -> com.google.android.gms.location.FusedLocationProviderApi.requestLocationUpdate() <- called by" + lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> com.google.android.gms.location.FusedLocationProviderApi.requestLocationUpdate()");
        }
    }

    /*
     * This method will hook java.net.URL.openConnection() function call
     * */
    //@GOOD
    private void hookOpenConnection(final LoadPackageParam lpparam) {
        //TODO get the URL object here to extract host and protocol type
        try {
            findAndHookMethod("java.net.URL",
                    lpparam.classLoader,
                    "openConnection", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            URL url = (URL)param.thisObject;
                            String description ="Method name: java.net.URL.openConnection" +
                                    ", host : " + url.getHost() +
                                    ", port : " + url.getPort() +
                                    ", Protocol : " + url.getProtocol() +
                                    ", UserInfo : " + url.getUserInfo() +
                                    ", Query : " + url.getQuery();

                            XposedBridge.log(description);
                            XposedBridge.log("\tInside java.net.URL.openConnection() <- called by " + lpparam.packageName);

                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.openConnection, description));
                            //This will create and populate a new Json file then write it to db.json
                            ////writeToFile(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.openConnection, description),
                            ////       AndroidAppHelper.currentApplication());
                        }
                    });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("METHOD NOT FOUND ->Inside java.net.URL.openConnection() <- called by " + lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND ->java.net.URL <- for openConnection()");
        }
    }

    /*
     * Write the given string to db.json file.
     * */
    //@GOOD
    private void writeToFile(String string, LoadPackageParam lpparam){
        XposedBridge.log("\tInside writeToFile <- called by " + lpparam.packageName);

        File directory = Environment.getExternalStorageDirectory();
        File file = new File( directory + "/db.json");
        if(!file.exists()){
            System.out.println("Creating " + directory.toString() +"/db.json");
        }
        BufferedWriter writer = null;
        FileWriter fileWriter = null;

        try{
            fileWriter = new FileWriter(file,true);
            writer = new BufferedWriter(fileWriter);
            writer.append(string);
            writer.newLine();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /*
     ***********************************************************
     ******                 ANDREW                        ******
     ***********************************************************
     * */

    //This method will hook android.net.wifi.WifiInfo.getIPAddress()
    //@GOOD
    private void hookGetIPAddress(final LoadPackageParam lpparam){
        try{
            //maybe just android.net.wifi
            findAndHookMethod("android.net.wifi.WifiInfo",
                    lpparam.classLoader,
                    "getIPAddress", new XC_MethodHook(){
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.net.wifi.WifiInfo.getIPAddress() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.net.wifi.WifiInfo.getIPAddress");
                            // getResult() will give us the return values.
                            description.append( ", IP Address: "+ param.getResult());
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getIPAddress, description.toString()));
                        }
                    });
        } catch(NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.net.wifi.WifiInfo.getIPAddress() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.net.wifi.WifiInfo <- for getIPAddress()");
        }
    }

    //This method will hook android.net.wifi.wifiInfo.getMacAddress()
    //@GOOD
    private void hookGetMacAddress(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.net.wifi.WifiInfo",
                    lpparam.classLoader,
                    "getMacAddress", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {

                            XposedBridge.log("Inside android.net.wifi.WifiInfo.getMacAddress() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.net.wifi.WifiInfo.getMACAddress");
                            // getResult() will give us the return values, witch is MACAddress.
                            description.append( ", MACAddress: "+ param.getResult());
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getMacAddress, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.net.wifi.WifiInfo.getMacAddress() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.net.wifi.WifiInfo <- for getMacAddress()");
        }
    }

    //This method will hook android.net.wifi.wifiInfo.getBSSID()
    //Return the basic service set identifier (BSSID) of the current access point
    //@GOOD
    private void hookGetBssid(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.net.wifi.WifiInfo",
                    lpparam.classLoader,
                    "getBSSID", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.net.wifi.WifiInfo.getBSSID() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.net.wifi.WifiInfo.getBSSID");
                            // getResult() will give us the return values.
                            description.append( ", BSSID: "+ param.getResult());
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getBSSID, description.toString()));

                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.net.wifi.WifiInfo.getBSSID() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.net.wifi.WifiInfo <- for getBSSID()");
        }
    }

    //This method will hook android.net.wifi.wifiInfo.getRssi()
    //Returns the received signal strength indicator of the current 802.11 network, in dBm
    //@GOOD
    private void hookGetRssi(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.net.wifi.WifiInfo",
                    lpparam.classLoader,
                    "getRssi", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {

                            XposedBridge.log("Inside android.net.wifi.WifiInfo.getRssi() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.net.wifi.WifiInfo.getRssi");
                            // getResult() will give us the return values.
                            description.append( ", Rssi: "+ param.getResult());
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getRssi, description.toString()));

                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.net.wifi.WifiInfo.getRssi() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.net.wifi.WifiInfo <- for getRssi()");
        }
    }

    //This method will hook android.net.wifi.wifiInfo.getSSID()
    //Returns the service set identifier (SSID) of the current 802.11 network.
    //@GOOD
    private void hookGetSsid(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.net.wifi.WifiInfo",
                    lpparam.classLoader,
                    "getSSID", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.net.wifi.WifiInfo.getSSID() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.net.wifi.WifiInfo.getSSID");
                            // getResult() will give us the return values.
                            description.append( ", SSID: "+ param.getResult());
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getSSID, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.net.wifi.WifiInfo.getSSID() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.net.wifi.WifiInfo <- for getSSID()");
        }
    }

    //This method will hook android.net.wifi.wifiInfo.getNetworkId
    //@GOOD
    private void hookGetNetworkId(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.net.wifi.WifiInfo",
                    lpparam.classLoader,
                    "getNetworkId", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.net.wifi.WifiInfo.getNetworkId() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.net.wifi.WifiInfo.getNetworkId");
                            description.append( ", Network Id: "+ param.getResult());
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getNetworkId, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.net.wifi.WifiInfo.getNetworkId() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.net.wifi.WifiInfo <- for getNetworkId()");
        }
    }

    //This method will hook android.telephony.TelephonyManager.getSimSerialNumber()
    //Returns the serial number of the SIM, if applicable. Return null if it is unavailable.
    //@GOOD
    private void hookGetSimSerialNumber(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.telephony.TelephonyManager",
                    lpparam.classLoader,
                    "getSimSerialNumber", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {

                            XposedBridge.log("Inside android.telephony.TelephonyManager.SimSerialNumber() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.telephony.TelephonyManager.SimSerialNumber");
                            //If serial number exists , log that or log 'no serial number '
                            description.append( ", Sim Serial Number: "+ ( (param.getResult()== null) ? "No serial number" : param.getResult()) );
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.SimSerialNumber, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.telephony.TelephonyManager.getSimSerialNumber() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.telephony.TelephonyManager <- for getSimSerialNumber()");
        }
    }

    //This method will hook android.telephony.TelephonyManager.getNetworkCountryIso()
    //Returns the ISO country code equivalent of the MCC (Mobile Country Code)
    //@GOOD
    private void hookGetNetworkCountryIso(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.telephony.TelephonyManager",
                    lpparam.classLoader,
                    "getNetworkCountryIso", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.telephony.TelephonyManager.getNetworkCountryIso() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.telephony.TelephonyManager.getNetworkCountryIso");
                            description.append( ", Network Country Iso: "+ param.getResult());
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(), lpparam.packageName, PIIJson.PIIAPIs.getNetworkCountryIso, description.toString()));

                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.telephony.TelephonyManager.getNetworkCountryIso() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.telephony.TelephonyManager <- for getNetworkCountryIso()");
        }
    }

    //This method will hook android.telephony.TelephonyManager.getSimCountryIso()
    //Returns the ISO country code equivalent for the SIM provider's country code.
    //@GOOD
    private void hookGetSimCountryIso(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.telephony.TelephonyManager",
                    lpparam.classLoader,
                    "getSimCountryIso", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.telephony.TelephonyManager.getSimCountryIso() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.telephony.TelephonyManager.getSimCountryIso");
                            description.append( ", Sim Country Iso: "+ param.getResult());
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(), lpparam.packageName, PIIJson.PIIAPIs.getSimCountryIso, description.toString()));

                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.telephony.TelephonyManager.getSimCountryIso() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.telephony.TelephonyManager <- for getSimCountryIso()");
        }
    }

    //This method will hook android.telephony.TelephonyManager.getSoftwareVersion()
    //Returns the software version number for the device, for example, the IMEI/SV for GSM phones. Return null if the software version is not available.
    //@GOOD
    private void hookGetDeviceSoftwareVersion(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.telephony.TelephonyManager",
                    lpparam.classLoader,
                    "getDeviceSoftwareVersion", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.telephony.TelephonyManager.getDeviceSoftwareVersion() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.telephony.TelephonyManager.getDeviceSoftwareVersion");
                            //If software version exists ,log that or log No device Software Version '
                            description.append( ", Sim Serial Number: "+ ( (param.getResult()== null) ? " No device Software Version" : param.getResult()) );
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(), lpparam.packageName, PIIJson.PIIAPIs.getDeviceSoftwareVersion, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.telephony.TelephonyManager.getSoftwareVersion() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.telephony.TelephonyManager <- for getSoftwareVersion()");
        }
    }

    //This method will hook android.telephony.TelephonyManager.getVoicemailNumber()
    //Returns the voice mail number. Return null if it is unavailable.
    //@GOOD
    private void hookGetVoicemailNumber(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.telephony.TelephonyManager",
                    lpparam.classLoader,
                    "getVoiceMailNumber", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.telephony.TelephonyManager.getVoiceMailNumber() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name : android.telephony.TelephonyManager.getVoiceMailNumber");
                            description.append( ", Voice Mail Number : "+ param.getResult());
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(), lpparam.packageName, PIIJson.PIIAPIs.getVoiceMailNumber, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.telephony.TelephonyManager.getVoiceMailNumber() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.telephony.TelephonyManager <- for getVoiceMailNumber()");
        }
    }

    //This method will hook android.telephony.TelephonyManager.getImei()
    //Returns the IMEI (International Mobile Equipment Identity). Return null if IMEI is not available.
    //@GOOD
    private void hookGetImei(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.telephony.TelephonyManager",
                    lpparam.classLoader,
                    "getImei", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.telephony.TelephonyManager.getImei() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.telephony.TelephonyManager.getImei");
                            //If IMEI exists, log that or log 'no IMEI number '
                            description.append( ", IMEI : "+ ( (param.getResult()== null) ? "No IMEI number" : param.getResult()) );
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getImei, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.telephony.TelephonyManager.getImei() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.telephony.TelephonyManager <- for getImei()");
        }
    }

    //This method will hook android.telephony.TelephonyManager.getSubscriberId()
    //Returns the unique subscriber ID, for example, the IMSI for a GSM phone. Return null if it is unavailable.
    //@GOOD
    private void hookGetSubscriberId(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.telephony.TelephonyManager",
                    lpparam.classLoader,
                    "getSubscriberId", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {

                            XposedBridge.log("Inside android.telephony.TelephonyManager.getSubscriberId() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.telephony.TelephonyManager.getSubscriberId");
                            //If ID exists ,log that or log 'no SubscriberId'
                            description.append( ", IMEI : "+ ( (param.getResult()== null) ? "No SubscriberID" : param.getResult()) );
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getSubscriberId, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.telephony.TelephonyManager.getSubscriberId() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.telephony.TelephonyManager <- for getSubscriberId()");
        }
    }

    //This method will hook android.telephony.TelephonyManager.getLine1Number()
    //Returns the phone number string for line 1. Return null if it is unavailable.
    //@GOOD
    private void hookGetLine1Number(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.telephony.TelephonyManager",
                    lpparam.classLoader,
                    "getLine1Number", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.telephony.TelephonyManager.getLine1Number() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.telephony.TelephonyManager.getLine1Number");
                            //If number exists ,log that or log 'No Line1Number'
                            description.append( ", Line 1 Number : "+ ( (param.getResult()== null) ? "No Line1Number" : param.getResult()) );
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getLine1Number, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.telephony.TelephonyManager.getLine1Number() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.telephony.TelephonyManager <- for getLine1Number()");
        }
    }

    //This method will hook android.bluetooth.BluetoothAdapter.getAddress()
    //Returns the hardware address of the local Bluetooth adapter.
    //@GOOD
    private void hookGetBluetoothAddress(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.bluetooth.BluetoothAdapter",
                    lpparam.classLoader,
                    "getAddress", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {

                            XposedBridge.log("Inside android.bluetooth.BluetoothAdapter.getAddress() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.bluetooth.BluetoothAdapter.getAddress");
                            description.append( ", Bluetooth Address : " + param.getResult() );
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getAddress, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.bluetooth.BluetoothAdapter.getAddress() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.bluetooth.BluetoothAdapter <- for getAddress()");
        }
    }

    //This method will hook android.bluetooth.BluetoothAdapter.getName()
    //Get the friendly Bluetooth name of the local Bluetooth adapter.
    //@GOOD
    private void hookGetBluetoothName(final LoadPackageParam lpparam){
        try{
            findAndHookMethod("android.bluetooth.BluetoothAdapter",
                    lpparam.classLoader,
                    "getName", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("Inside android.bluetooth.BluetoothAdapter.getName() <- called by " + lpparam.packageName);
                            StringBuilder description = new StringBuilder( "Method name: android.bluetooth.BluetoothAdapter.getName");
                            description.append( ", Bluetooth Name: " + param.getResult() );
                            XposedBridge.log(description.toString());
                            PIIObjectList.add(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getName, description.toString()));
                        }
                    });
        }catch (NoSuchMethodError e){
            XposedBridge.log("METHOD NOT FOUND -> android.bluetooth.BluetoothAdapter.getName() <- called by " + lpparam.packageName);
        }catch(XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> android.bluetooth.BluetoothAdapter <- for getName()");
        }
    }

    /**
     ************************************************
     ** methods blow are for testing purpose only. **
     ************************************************
     ** */

    //@TEST
    private void hookIsInvalid(final LoadPackageParam lpparam){
        try {
            findAndHookMethod("java.io.File",
                    lpparam.classLoader,
                    "isInvalid", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("=>=> Inside isInvalid: result was: "+ param.getResult() + " <- called by " +param.thisObject.getClass().getName());
                            boolean result = false;
                            param.setResult(result);
                        }
                    });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("METHOD NOT FOUND ->Inside java.net.URL.openConnection() <- called by " + lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND ->java.net.URL <- for openConnection()");
        }
    }
    /*
     *Hooks the dummy app
     * */
    //@TEST
    private void hookOnDummyApp(final LoadPackageParam lpparam) {
        try {
            findAndHookMethod("com.robel.testideavim.Main.MainActivity", lpparam.classLoader,
                    "setOutput",
                    int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            param.args[0] = 1;
                            XposedBridge.log("value of i after hooking" + param.args[0]);

                            XposedBridge.log("lpparm.classLoader is: " + lpparam.classLoader.toString());
                            Class<?> myClass = XposedHelpers.findClass("com.example.robel.testideavim.MainActivity", lpparam.classLoader);

                            XposedBridge.log("myClass is: " + myClass.getName());
                            int i = (int) XposedHelpers.findField(myClass,"count").get(param.thisObject);
                            XposedBridge.log("count is: "+ i);
                        }
                    });
        } catch (NoSuchMethodError e ) {
            XposedBridge.log("METHOD NOT FOUND -> com.robel.testideavim.Main.MainActivity.setOutput <- called by " +lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> " +lpparam.packageName +".Main.MainActivity");
        }
//        catch (IllegalAccessException e){
//            XposedBridge.log("IllegalAccessException -> com.robel.testideavim.setOutput <- called by " +lpparam.packageName);
//        }
    }

    /*
     * An example/ Sample code: How to hook a class
     * */
    //@TEST
    private void hookLocationServiceObject(final LoadPackageParam lpparam){

        //This will check to see if "teamtreehouse.com.iamhere" instantiated a LocationServices object, if not return is null.
        Class<?> myLocationServicesClass = XposedHelpers.findClassIfExists("com.google.android.gms.location.LocationServices", lpparam.classLoader);

        if (myLocationServicesClass != null)
            XposedBridge.log("CLASS -> com.google.gms.location.LocationServices " + myLocationServicesClass.getName());
    }
}
