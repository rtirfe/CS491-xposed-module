package com.example.robel.xposedmodule;

import java.net.URL;
import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

//IXposedHookLoadPackage
//Get notified when an app ("Android package") is loaded.
// This is especially useful to hook some app-specific methods.
public class XposedClass implements IXposedHookLoadPackage {

    //ArrayList to hold 100 test android packages.
    private ArrayList<String> packageList;

    /*  Get notified when an app ("Android package") is loaded.
     */
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        packageList = new ArrayList<>();//ArrayList to store top 100 apps package name.
        packageList.add("com.example.robel.testideavim");
        packageList.add("teamtreehouse.com.iamhere");

        this.hookOnpackage(lpparam);
    }

    public void hookOnpackage(LoadPackageParam lpparam){
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

    // Below are hooking methods
    /*
    *Hooks the dummy app
    * */
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
    *This method will hook android.net.NetworkInfo.getTypeName() function call
    * */
    private void hookOnNetworkInfo(final LoadPackageParam lpparam){
        try {
            findAndHookMethod("android.net.NetworkInfo",
                    lpparam.classLoader,
                    "getTypeName",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("\tInside android.net.NetworkInfo.getTypeName() <- called by "+lpparam.packageName);
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
    private void hookGetActiveNetworkInfo(final LoadPackageParam lpparam) {
        try {
            findAndHookMethod("android.net.ConnectivityManager",
                    lpparam.classLoader,
                    "getActiveNetworkInfo",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("\tInside android.net.ConnectivityManager.getActiveNetworkInfo() <- called by " + lpparam.packageName);
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
    private void hookBuild(final LoadPackageParam lpparam){
        try {
            findAndHookMethod("com.google.android.gms.common.api.GoogleApiClient.Builder",
                    lpparam.classLoader,
                    "build",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("\tInside com.google.android.gms.common.api.GoogleApiClient.Builder.build() <- called by " + lpparam.packageName);
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
    * An example/ Sample code: How to hook a class
    * */
    private void hookLocationServiceObject(final LoadPackageParam lpparam){

        //This will check to see if "teamtreehouse.com.iamhere" instantiated a LocationServices object, if not return is null.
        Class<?> myLocationServicesClass = XposedHelpers.findClassIfExists("com.google.android.gms.location.LocationServices", lpparam.classLoader);

        if (myLocationServicesClass != null)
            XposedBridge.log("CLASS -> com.google.gms.location.LocationServices " + myLocationServicesClass.getName());
    }

    /* This method will hook com.google.android.gms.common.api.GoogleApiClient.getLastLocation() function call
    * */
    private void hookGetLastLocation(final LoadPackageParam lpparam) {
        try {
            findAndHookMethod("com.google.android.gms.internal.lu",
                    lpparam.classLoader,
                    "getLastLocation",
                    "com.google.android.gms.common.api.GoogleApiClient", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("\tcom.google.android.gms.location.FusedLocationProviderApi.getLastLocation() <- called by " + lpparam.packageName);
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
    private void hookRequestLocationUpdates(final LoadPackageParam lpparam) {
        //TODO more than one variables of requestLocationUpdates() method
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
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("\tcom.google.android.gms.location.FusedLocationProviderApi.requestLocationUpdate() <- called by " + lpparam.packageName);
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
    private void hookOpenConnection(final LoadPackageParam lpparam) {
        //TODO get the URL object here to extract host and protocol type
        try {
            findAndHookMethod("java.net.URL",
                    lpparam.classLoader,
                    "openConnection", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            URL url = (URL)param.thisObject;
                            XposedBridge.log("\n\thost is: " + url.getHost() +
                                    "\n\tport is: " + url.getPort() +
                                    "\n\tProtocol is:" + url.getProtocol() +
                                    "\n\tUserInfor is:" + url.getUserInfo() +
                                    "\n\tQuery is: " + url.getQuery());
                            XposedBridge.log("\tInside java.net.URL.openConnection() <- called by " + lpparam.packageName);
                        }
                    });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("METHOD NOT FOUND ->Inside java.net.URL.openConnection() <- called by " + lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND ->java.net.URL <- for openConnection()");
        }
    }
}
