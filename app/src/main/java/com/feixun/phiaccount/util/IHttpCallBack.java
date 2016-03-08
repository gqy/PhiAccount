package com.feixun.phiaccount.util;

public abstract class IHttpCallBack {

    public void onStart() {
    }

    public void onCancel() {
    }

    public void onLoad(int current, int total) {

    }

    public abstract void onSuccess(byte[] result);

    public abstract void onFailure(String msg);
}
