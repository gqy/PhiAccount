package com.feixun.phiaccount.ui;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.feixun.phiaccount.R;
import com.feixun.phiaccount.account.PaAccountCons;
import com.feixun.phiaccount.account.PaAccountManager;
import com.feixun.phiaccount.account.PaUser;
import com.feixun.phiaccount.tools.PaSysApplication;

/**
 * Created by He on 2016/3/2 0002.
 */
public class PaWebViewActivity extends AccountAuthenticatorActivity {
    private static final String TAG = PaWebViewActivity.class.getSimpleName();
    private WebView webView;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaSysApplication.getInstance().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.paactivity_webview);
        mAccountManager = AccountManager.get(this);
        PaAccountManager.getInstance(this).setActivity(this);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(PaAccountManager.getInstance(this).getJavascriptObject(), "geqinyangObj");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

        });
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        webView.loadUrl(url);
    }

    public void finishLogin(PaUser user){
        Log.i("K-123", "finishLogin()");
        Account account = new Account(user.getPhicommId(), PaAccountCons.ACCOUNT_TYPE);
        mAccountManager.addAccountExplicitly(account, user.getAccessToken(), null);
        mAccountManager.setUserData(account, "phicommId", user.getPhicommId());
        mAccountManager.setUserData(account, "openId", user.getOpenId());
        mAccountManager.setUserData(account, "expireseIn", user.getExpireseIn());
        mAccountManager.setUserData(account, "accessToken", user.getAccessToken());
        mAccountManager.setUserData(account, "nickname", user.getNickname());
        mAccountManager.setUserData(account, "userPhoneNumb", user.getUserPhoneNumb());
        mAccountManager.setUserData(account, "figureurl", user.getFigureurl());
        mAccountManager.setUserData(account, "birthday", user.getBirthday());
        mAccountManager.setUserData(account, "sex", user.getSex());
        mAccountManager.setUserData(account, "openqq", user.getOpenqq());
        mAccountManager.setUserData(account, "openqqinfo", user.getOpenqqInfo());
        mAccountManager.setUserData(account, "openweixin", user.getOpenweixin());
        mAccountManager.setUserData(account, "openweixininfo", user.getOpenweixinInfo());
        mAccountManager.addAccountExplicitly(account, user.getAccessToken(), null);

        Log.i("K-123", "addAccountExplicitly");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void goBack(){
        webView.goBack();
    }
}
