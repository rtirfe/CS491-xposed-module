package com.example.robel.xposedmodule;


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

    //String array to hold our top 100 android apps class path.
    private String appsToHook []= {"com.example.robel.testideavim",
    "teamtreehouse.com.iamhere"};

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("\tLoaded app: " + lpparam.packageName);
        hookOnpackage(lpparam);
    }

    public void hookOnpackage(LoadPackageParam lpparam){
        //Let's restric this hooking to only sample apps
        if(lpparam.packageName.equals(appsToHook[0]) || lpparam.packageName.equals(appsToHook[1])) {
            //TODO these hooking methods have duplicate codes.
            hookOnDummyApp(lpparam); //com.robel.testIdeaVim.setOutput()
            hookOnNetworkInfo(lpparam); //android.net.NetworkInfo.getTypeName()
            hookGetActiveNetworkInfo(lpparam); //android.net.ConnectivityManager.getActiveNetworkInfo()
            hookAddApi(lpparam);
            hookBuild(lpparam); //com.google.android.gms.common.api.GoogleApiClient.Builder.build();

            //TODO implement getLastLocation() and requestLocationUpdates()
           // hookRequestLocationUpdates(lpparam);//TODO try to find a method that hooks a methods with interface arguments. //can not hook interfaces; it found the found GoogleApiClient class though

           // hookGetLastLocation(lpparam);
        }
    }

    // Below are hooking methods

    private void hookOnDummyApp(final LoadPackageParam lpparam) {
        try {
            findAndHookMethod(lpparam.packageName + ".MainActivity", lpparam.classLoader, "setOutput", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.args[0] = 1;
                    XposedBridge.log("value of i after hooking" +  param.args[0]);
                }
            });
        } catch (NoSuchMethodError e ) {
            XposedBridge.log("METHOD NOT FOUND -> com.robel.testideavim.setOutput <- called by " +lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND -> " +lpparam.packageName +".MainActivity");
        }
    }

    private void hookOnNetworkInfo(final LoadPackageParam lpparam){
        try {
            findAndHookMethod("android.net.NetworkInfo", lpparam.classLoader, "getTypeName", new XC_MethodHook() {
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

    private void hookBuild(final LoadPackageParam lpparam){
        //TODO other API types different form LocationService.API can trigger this call
        try {
            findAndHookMethod("com.google.android.gms.common.api.GoogleApiClient.Builder", lpparam.classLoader, "build", new XC_MethodHook() {
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

    private void hookAddApi(final LoadPackageParam lpparam){
        //TODO other API types different form LocationService.API can trigger this call
        //"com.google.android.gms.common.api.Api",//NOT sure if we need the LocationServices.API here
        try {
            findAndHookMethod("com.google.android.gms.common.api.GoogleApiClient.Builder"
                    , lpparam.classLoader,
                    "addApi",
                    "com.google.android.gms.common.api.Api",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            param.thisObject
                            XposedBridge.log("\tInside com.google.android.gms.common.api.GoogleApiClient.Builder.addApi() <- called by "
                                    + lpparam.packageName);
                        }
                    });
        } catch (NoSuchMethodError e) {
            XposedBridge.log("METHOD NOT FOUND -> com.google.android.gms.common.api.GoogleApiClient.Builder.addApi() <- called by " + lpparam.packageName);
        }
        catch (XposedHelpers.ClassNotFoundError error){
            XposedBridge.log("CLASS NOT FOUND ->com.google.android.gms.common.api.GoogleApiClient.Builder <- for addApi()");
        }
    }

    private void hookGetLastLocation(final LoadPackageParam lpparam) {
        try {
            findAndHookMethod("com.google.android.gms.location.FusedLocationProviderApi",
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

    private void hookRequestLocationUpdates(final LoadPackageParam lpparam) {
    }
}
