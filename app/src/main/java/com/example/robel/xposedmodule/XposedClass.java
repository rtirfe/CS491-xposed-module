package com.example.robel.xposedmodule;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
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
            //TODO create a hooking for getLocation here
        }

    }

    private void hookOnDummyApp(LoadPackageParam lpparam) {
        findAndHookMethod(lpparam.packageName + ".MainActivity", lpparam.classLoader, "setOutput", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.args[0] = 1;
                XposedBridge.log("value of i after hooking" +  param.args[0]);
            }
        });
    }

    private void hookOnNetworkInfo(final LoadPackageParam lpparam){
        findAndHookMethod("android.net.NetworkInfo", lpparam.classLoader, "getTypeName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("\tInside android.net.NetworkInfo <- called by "+lpparam.packageName);
            }
        });
    }

    private void hookGetActiveNetworkInfo(final LoadPackageParam lpparam) {

        findAndHookMethod("android.net.ConnectivityManager", lpparam.classLoader, "getActiveNetworkInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("\tInside android.net.ConnectivityManager <- called by "+ lpparam.packageName);
            }
        });
    }


}
