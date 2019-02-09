package com.example.robel.xposedmodule.Data;

import java.util.Date;

public class PIIJson {
    private Date date;
    private String packageName;
    private String description;
    private PIIAPIs APIUsed;

    public PIIJson(Date date, String packageName, PIIAPIs APIUsed, String description) {
        this.date = date;
        this.packageName = packageName;
        this.APIUsed = APIUsed;
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public PIIAPIs getAPIUsed() {
        return APIUsed;
    }

    public void setAPIUsed(PIIAPIs APIUsed) {
        this.APIUsed = APIUsed;
    }

    @Override
    public String toString() {
        return "PIIJson{" +
                "date=" + date +
                ", packageName='" + packageName + '\'' +
                ", APIUsed=" + APIUsed +
                '}';
    }

    public enum PIIAPIs{
        //TODO fill this enum with PII APIs
        //Internet usage APIs
        openConnection, getActiveNetworkInfo, isConnectedOrConnecting, getConnectionInfo, getAllNetworks, getType,build,

        //End-Users Location APIs
        getCellLocation, getNeighboringCellInfo, getLastKnownLocation, requestLocationUpdates, getLastLocation, requestLocationUdate,

        //App usage status APIs
        onCreate, onStart, onResume, onPause, onStop, onRestart, onDestroy
    }
}

