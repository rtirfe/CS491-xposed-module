package com.example.robel.xposedmodule;
import android.graphics.Color;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

//IXposedHookLoadPackage
//Get notified when an app ("Android package") is loaded.
// This is especially useful to hook some app-specific methods.
public class XposedClass implements IXposedHookLoadPackage {
    //TODO android.content.ContextWrapper.getApplicationContext();
    //Need to be able to isolate a network flow.

    //String array to hold our top 100 android apps class path.
    private String appsToHook []= {
            "com.example.robel.testideavim",
            "com.google.android.youtube",
            "com.facebook.orca",
            "com.king.candycrushsaga",
            "com.twitter.android",
            "android.content.ContextWrapper.getApplicationContext()",
            "com.android.inputmethod.keyboard"};

    //handleLoadPackage
    //This method is called when an app is loaded. It's called very early, even before Application.onCreate() is called.
    //Modules can set up their app-specific hooks here.
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loaded app: " + lpparam.packageName);

        switch (lpparam.packageName){
            case "android.content.ContextWrapper.getApplicationContext()":
                XposedBridge.log("Hooking on getApplicationContext");
                break;
            case "com.android.inputmethod.keyboard":
                XposedBridge.log("Hooking on keyboard");
                break;
            case "com.example.robel.testideavim":
                XposedBridge.log("Hooking on TestIdeaVim");
                break;
            case "com.google.android.youtube":
                XposedBridge.log("Hooking on YouTube");
                break;
            case "com.facebook.orca":
                XposedBridge.log("Hooking on FaceBook");
                break;
            case "com.king.candycrushsaga":
                XposedBridge.log("Hooking on candy crush saga");
                break;
            case "com.twitter.android":
                XposedBridge.log("Hooking on twitter");
                break;
            default:
                break;
        }

        String classToHook = "com.example.robel.testideavim.MainActivity";
        String functionToHook = "setOutput";
        String functionToHook2 = "getSystemService";

        if(lpparam.packageName.equals("com.example.robel.testideavim")){
            XposedBridge.log("Loaded app: " + lpparam.packageName);

//            findAndHookMethod(classToHook,lpparam.classLoader, functionToHook, int.class, new XC_MethodHook(){
            findAndHookMethod(classToHook,lpparam.classLoader, functionToHook, int.class, new XC_MethodHook(){
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
                    param.args[0] = 1;
                    XposedBridge.log("value of i after hooking" +  param.args[0]);
                }
            });
//            findAndHookMethod(classToHook, lpparam.classLoader, functionToHook2, String.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    XposedBridge.log("Hooking to network context");
//                    XposedBridge.log("Value of first param: " +  param.args[0]);
//                }
//
//            });

            findAndHookMethod("android.net.ConnectivityManager", lpparam.classLoader, "getActiveNetworkInfo", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("%nInside android.net.ConnectivityManager");
                }
            });
            findAndHookMethod("android.net.NetworkInfo", lpparam.classLoader, "getTypeName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("%nInside android.net.NetworkInfo");
                }
            });
//            findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "updateClock", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    XposedBridge.log("%nInside com.android.systemui.statusbar.policy.Clock");
//                    TextView tv = (TextView) param.thisObject;
//                    String text = tv.getText().toString();
//                    tv.setText(text + " :)");
//                    tv.setTextColor(Color.RED);
//                }
//            });
        }
//        if(lpparam.packageName.equals(classToHook2)){
//            XposedBridge.log("Hooking on YouTube");
//        }
//

    }

}
