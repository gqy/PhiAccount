package com.feixun.phiaccount.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.feixun.phiaccount.ui.PaIndexPageActivity;
import com.feixun.phiaccount.ui.PaWebViewActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

/**
 * Created by He on 2016/3/7 0007.
 */
public class PaAccountManager extends AccountAuthenticatorActivity {
    private static final String TAG = PaAccountManager.class.getSimpleName();
    private AccountManager mAccountManager;
    private static PaAccountManager uniqueInstance;
    private Context mContext;
    private String mLoginInfo = null;
    private IWXAPI api;
    private final JavascriptObjectMethod mJavascript = new JavascriptObjectMethod();
    private PaAccountManager(Context context) {
        mContext = context;
        api= WXAPIFactory.createWXAPI(context, PaAccountCons.WX_APP_ID, true);
        api.registerApp(PaAccountCons.WX_APP_ID);
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAccountManager = AccountManager.get(this);
    }
    public static synchronized PaAccountManager getInstance(Context context) {
        if (uniqueInstance == null) {
            uniqueInstance = new PaAccountManager(context.getApplicationContext());
        }
        return uniqueInstance;
    }


    public Activity activity = null;
    public void setActivity(Activity act) {
        this.activity = act;
    }

    public JavascriptObjectMethod getJavascriptObject() {
        return mJavascript;
    }

    public class JavascriptObjectMethod {
        @android.webkit.JavascriptInterface
        public void loginSuccess(String loginInfo) {
            PaAccountManager.getInstance(mContext).mLoginInfo = loginInfo;
            Log.i("K-123", "logininfo="+loginInfo);
            PaAccountManager.getInstance(mContext).analyze();
//            PaAccountManager.getInstance(mContext).analyze();
//            Intent intent = new Intent();
//            intent.putExtra("result", "login_success");
//            activity.setResult(activity.RESULT_OK, intent);
//            activity.finish();
            activity.startActivity(new Intent(mContext, PaIndexPageActivity.class));
        }
        @android.webkit.JavascriptInterface
        public void getUpdateInfo(String update_phicommId, String update_key, String update_value) {
            AccountManager am = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
            Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
            am.setUserData(accounts[0], update_key, update_value);
        }
        @android.webkit.JavascriptInterface
        public void finishSelf() {
            activity.finish();
        }

        @android.webkit.JavascriptInterface
        public void back() {
            ((PaWebViewActivity)activity).goBack();
        }

        @android.webkit.JavascriptInterface
        public void exitAccount() {
            PaAccountCons.getInstance(mContext).alertDeleteAccount();
        }
        @android.webkit.JavascriptInterface
        public void weixinLogin(){
            Log.i(TAG,"weixinLogin();");
            SendRequestToWeiXin();
        }
        public void SendRequestToWeiXin(){

            final SendAuth.Req req=new SendAuth.Req();
            req.scope="snsapi_userinfo";
            req.state="ssa";
            api.sendReq(req);
        }
    }

    public PaUser analyze() {
        PaUser user = new PaUser();
        try {
            JSONObject mJsonob = new JSONObject(mLoginInfo);
            JSONObject data = mJsonob.getJSONObject("data");
            user.setPhicommId(data.getString("phicommId"));
            Log.i("K-123", data.getString("phicommId"));
            user.setNickname(data.getString("nickname"));
            user.setUserPhoneNumb(data.getString("userPhoneNumb"));
            user.setFigureurl(data.getString("figureurl"));
            user.setBirthday(data.getString("birthday"));
            user.setSex(data.getString("sex"));
            user.setAccessToken(data.getString("accessToken"));
            Log.i("K-123", data.getString("accessToken"));
            user.setOpenId(data.getString("openId"));
            Log.i("K-123", data.getString("openId"));
            user.setExpireseIn(data.getString("expireseIn"));
            Log.i("K-123", data.getString("expireseIn"));
            user.setOpenqq(data.getString("openqq"));
            user.setOpenqqInfo(data.getString("openqqinfo"));
            user.setOpenweixin(data.getString("openweixin"));
            user.setOpenweixinInfo(data.getString("openweixininfo"));
            ((PaWebViewActivity)activity).finishLogin(user);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }

    public  void exitAccount(Context context){
        android.accounts.AccountManager mgr = android.accounts.AccountManager.get(context);
        Account[] accounts = mgr.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        if (accounts != null && accounts.length > 0) {
            mgr.removeAccount(accounts[0], null, null);
        }
    }
    public  void exitAccount(Context context,AccountManagerCallback<Boolean> callback){
        android.accounts.AccountManager mgr = android.accounts.AccountManager.get(context);
        Account[] accounts = mgr.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
        if (accounts != null && accounts.length > 0) {
            mgr.removeAccount(accounts[0], callback, null);
        }
    }
}
