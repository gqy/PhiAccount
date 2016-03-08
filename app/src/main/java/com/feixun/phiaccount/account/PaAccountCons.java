package com.feixun.phiaccount.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.feixun.phiaccount.R;
import com.feixun.phiaccount.tools.PaSysApplication;
import com.feixun.phiaccount.tools.PhiAlertDialog;

import java.io.IOException;

/**
 * Created by He on 2016/3/7 0007.
 */
public class PaAccountCons {
    private static final String TAG = PaAccountCons.class.getSimpleName();

    public static final String ACCOUNT_TYPE = "PhiAccount";
    public static final String AUTHTOKEN_TYPE = "PhiAccount";
    public static final String WX_APP_ID = "wx9ba209d200d40b7e";
    public static final String WX_APP_SECRET = "28bafede14b2059d3a16762c3d38e512";

    //private static final String DEBUG_SERVER_IP = "http://114.141.173.17";
    private static final String DEBUG_SERVER_IP = "http://192.168.2.107";
    private static final String ACCOUNT_URL = DEBUG_SERVER_IP;
    public static final String BASE_URL = ACCOUNT_URL + "/mobile-user/";
    public static final String LOGIN_URI = BASE_URL + "generalunify/tologin";
    public static final String PHONE_REGISTER = BASE_URL + "generalunify/toregisterphone";
    public static final String MAIL_REGISTER = BASE_URL + "generalunify/toregistermail";
    public static final String PORTRAIT_URI = BASE_URL + "unify/topersoninfo";

    public static final int LOGIN_SUCCESS_CODE = 1010;
    private Context mContext;
    private static PaAccountCons uniqueInstance;
    private Activity activity = null;

    public void setActivityCons(Activity act) {
        this.activity = act;
    }

    private PaAccountCons(Context context) {
        mContext = context;
    }

    public static synchronized PaAccountCons getInstance(Context context) {
        if (uniqueInstance == null) {
            uniqueInstance = new PaAccountCons(context);
        }
        return uniqueInstance;
    }

    public void alertDeleteAccount() {
        PhiAlertDialog.Builder builder = new PhiAlertDialog.Builder(activity);
        builder.setTitle(R.string.pa_exit_account);
        builder.setCancelable(true);
        builder.setMessage(R.string.pa_exit_accout_hint);
        builder.setPositiveButton(R.string.pa_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 发送广播，通知其它应用斐讯账号即将退出
                        Intent intent = new Intent();
                        intent.setAction("com.feixun.phiaccount");
                        intent.putExtra("PhiAccount", "exitAccount");
                        activity.sendBroadcast(intent);

                        // 延迟500ms删除账号并退出应用
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PaAccountManager accountManager = PaAccountManager.getInstance(activity);
                                accountManager.exitAccount(activity);
                                PaSysApplication.getInstance().exit();
                            }
                        }, 1200);
                        dialogInterface.dismiss();
                        Log.d(TAG, "dialog dismiss");
                    }
                });
        builder.setNegativeButton(R.string.pa_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    public boolean hasUser() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        if (accounts == null || (accounts != null && accounts.length == 0)) {
            return false;
        } else {
            return true;
        }
    }

    public String getPhicommId() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String PhicommId = am.getUserData(accounts[0], "phicommId");
        return PhicommId;
    }

    public String getNickName() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String nickName = am.getUserData(accounts[0], "nickname");
        return nickName;
    }

    public String getUserPhoneNumb() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String userPhoneNumb = am.getUserData(accounts[0], "userPhoneNumb");
        return userPhoneNumb;
    }

    public String getFigureUrl() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String figureurl = am.getUserData(accounts[0], "figureurl");
        return figureurl;
    }

    public String getBirthday() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String birthday = am.getUserData(accounts[0], "birthday");
        return birthday;
    }

    public String getSex() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String sex = am.getUserData(accounts[0], "sex");
        return sex;
    }

    public String getAccessToken() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String accessToken = am.getUserData(accounts[0], "accessToken");
        return accessToken;
    }

    public String getOpenId() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String openId = am.getUserData(accounts[0], "openId");
        return openId;
    }

    public String getExpireseIn() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String expireseIn = am.getUserData(accounts[0], "expireseIn");
        return expireseIn;
    }

    public String getOpenqq() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String openqq = am.getUserData(accounts[0], "openqq");
        return openqq;
    }

    public String getOpenqqInfo() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String openqqinfo = am.getUserData(accounts[0], "openqqinfo");
        return openqqinfo;
    }

    public String getOpenweixin() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String openweixin = am.getUserData(accounts[0], "openweixin");
        return openweixin;
    }

    public String getOpenweixininfo() {
        AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        String openweixininfo = am.getUserData(accounts[0], "openweixininfo");
        return openweixininfo;
    }
}
