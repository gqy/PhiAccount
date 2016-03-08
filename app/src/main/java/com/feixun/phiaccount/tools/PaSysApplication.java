package com.feixun.phiaccount.tools;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

public class PaSysApplication extends Application {
    private List<Activity> mList = new LinkedList<Activity>();
    private static PaSysApplication instance;

    private PaSysApplication() {
    }
    public synchronized static PaSysApplication getInstance() {
        if (null == instance) {
            instance = new PaSysApplication();
        }
        return instance;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}