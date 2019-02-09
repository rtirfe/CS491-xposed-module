package com.example.robel.xposedmodule;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.net.NetworkInfo;
import android.os.Environment;

import com.example.robel.xposedmodule.Data.PIIJson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

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
            //TODO how to deal with method overloading.
            hookOnDummyApp(lpparam); //com.robel.testIdeaVim.setOutput()
            hookOnNetworkInfo(lpparam); //android.net.NetworkInfo.getTypeName()
            hookGetActiveNetworkInfo(lpparam); //android.net.ConnectivityManager.getActiveNetworkInfo()
            hookLocationServiceObject(lpparam);
            hookBuild(lpparam); //com.google.android.gms.common.api.GoogleApiClient.Builder.build();
            hookGetLastLocation(lpparam);//com.google.android.gms.internal.lu
            hookRequestLocationUpdates(lpparam);//com.google.android.gms.internal.lu.requestLocationUpdates
            hookOpenConnection(lpparam);//java.net.URL.openConnection
        }
    }

   /*
   * Below are hooking methods
   * */

    /*
    *This method will hook android.net.NetworkInfo.getTypeName() function call
    * */
    //@GOOD
    private void hookOnNetworkInfo(final LoadPackageParam lpparam){
        try {
            findAndHookMethod("android.net.NetworkInfo",
                    lpparam.classLoader,
                    "getTypeName",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    XposedBridge.log("\tInside android.net.NetworkInfo.getTypeName() <- called by "+lpparam.packageName);

                    String description = "Method name: Android.net.NetworkInfo.getTypeName(), " +
                            "getTypeName: "+ param.getResult();
                    writeJsonToFile(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getType , description),
                            AndroidAppHelper.currentApplication());
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
    * */
    //@Good
    private void hookGetActiveNetworkInfo(final LoadPackageParam lpparam) {
        try {
            findAndHookMethod("android.net.ConnectivityManager",
                    lpparam.classLoader,
                    "getActiveNetworkInfo",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedBridge.log("\tInside android.net.ConnectivityManager.getActiveNetworkInfo() <- called by " + lpparam.packageName);
                            NetworkInfo networkInfo = (NetworkInfo) param.getResult();
                            String description = "Method name: Android.net.ConnectivityManager.getActiveNetworkInfo, " +
                                    "getActiveNetworkInfo: "+ networkInfo.getTypeName();//Return a human-readable name describe the type of the network.
                            writeJsonToFile(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getType , description),
                                    AndroidAppHelper.currentApplication());
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
    //@Good
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
                    writeJsonToFile(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.build, description),
                            AndroidAppHelper.currentApplication());
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
    //@Good
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
                            writeJsonToFile(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.getLastLocation, description),
                                    AndroidAppHelper.currentApplication());
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
    //@Good
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
                            String description ="Method name: com.android.gms.internal.lu.requestLocationUpdates" ;
                            writeJsonToFile(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.requestLocationUpdates, description),
                                    AndroidAppHelper.currentApplication());
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
                                    ", host is: " + url.getHost() +
                                    ", port : " + url.getPort() +
                                    ", Protocol :" + url.getProtocol() +
                                    ", UserInfo :" + url.getUserInfo() +
                                    ", Query : " + url.getQuery();

                            XposedBridge.log(description);
                            XposedBridge.log("\tInside java.net.URL.openConnection() <- called by " + lpparam.packageName);

                            //This will create and populate a new Json file then write it to db.json
                            writeJsonToFile(new PIIJson(new Date(),lpparam.packageName, PIIJson.PIIAPIs.openConnection, description),
                                    AndroidAppHelper.currentApplication());
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
    * A method that creates a json object and writes that object to db.json file.
    * */
    private void writeJsonToFile(PIIJson piiJson, Context context ){
        Gson gson = new Gson();
        Type type = new TypeToken<PIIJson>() {}.getType();
        String json = gson.toJson(piiJson, type);

        try (FileOutputStream outputStream = context.openFileOutput( "db.json", Context.MODE_PRIVATE)){
            outputStream.write(json.getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /*
    * methods blow are for testing purpose only.
    * */

    /*
    * Still under construction
    * */
    //@TEST
    private void readFileFromSDCard() {
        File directory = Environment.getDataDirectory();
        // assumes that a file article.rss is available on the SD card
        File file = new File(directory + "/db.json");
        if (!file.exists()) {

            throw new RuntimeException("File not found");
        }
//        Log.e("Testing", "Starting to read");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    *Hooks the dummy app
    * */
    //@TEST
    private void hookOnDummyApp(final LoadPackageParam lpparam) {
        try {
            findAndHookMethod(lpparam.packageName +
                            ".MainActivity", lpparam.classLoader,
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

                            XposedBridge.log("myClass is:" + myClass.getName());
                            int i = (int) XposedHelpers.findField(myClass,"count").get(param.thisObject);
                            XposedBridge.log("count is: "+ i);
                        }
                    });
        } catch (NoSuchMethodError e ) {
            XposedBridge.log("METHOD NOT FOUND -> com.robel.testideavim.setOutput <- called by " +lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> " +lpparam.packageName +".MainActivity");
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
