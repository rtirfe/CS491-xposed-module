package com.example.robel.xposedmodule;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class XposedClass implements IXposedHookLoadPackage {

    //TODO
    //Need to be able to find a method and hook to it with out a giving package path.
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        String classToHook = "com.example.robel.TestIdeaVim.MainActivity";
        String functionToHook = "setOutput";

        if(lpparam.packageName.equals("com.example.robel.TestIdeaVim")){
            XposedBridge.log("Loaded app: " + lpparam.packageName);
            findAndHookMethod(classToHook,lpparam.classLoader, functionToHook, int.class,
                    new XC_MethodHook(){
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
                            param.args[0] = 1;
                            XposedBridge.log("value of i after hooking" +  param.args[0]);
                        }
                    });
        }

    }
}
