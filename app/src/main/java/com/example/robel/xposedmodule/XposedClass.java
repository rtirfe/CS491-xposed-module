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
    private String appsToHook []= {"com.example.robel.testideavim"};

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("\tLoaded app: " + lpparam.packageName);
        hookOnpackage(lpparam);
    }

    public void hookOnpackage(LoadPackageParam lpparam){
        if(lpparam.packageName.equals("com.example.robel.testideavim")){
            hookOnDummyApp(lpparam);
            hookOnNetworkInfo(lpparam);
            hookGetActiveNetworkInfo(lpparam);
        }

        if(lpparam.packageName.equals("teamtreehouse.com.iamhere")){
            //TODO create a hooking for build
            //com.google.android.gms.common.api.GoogleApiClient.Builder .build();
            hookBuild(lpparam);
            hookOnDummyApp(lpparam);// used for error handling, should expect and error,
        }

    }

    private void hookOnDummyApp(LoadPackageParam lpparam) {
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
                    XposedBridge.log("\tInside android.net.NetworkInfo.getTypeName <- called by "+lpparam.packageName);
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

        findAndHookMethod("android.net.ConnectivityManager", lpparam.classLoader, "getActiveNetworkInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("\tInside android.net.ConnectivityManager <- called by "+ lpparam.packageName);
            }
        });
    }

    private void hookBuild(final LoadPackageParam lpparam){

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
            XposedBridge.log("CLASS NOT FOUND ->com.google.android.gms.common.api.GoogleApiClient.Builder");
        }
    }
}
