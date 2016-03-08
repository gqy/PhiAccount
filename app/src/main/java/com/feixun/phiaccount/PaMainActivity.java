package com.feixun.phiaccount;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.feixun.phiaccount.account.PaAccountCons;
import com.feixun.phiaccount.ui.PaIndexPageActivity;
import com.feixun.phiaccount.ui.PaWebViewActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class PaMainActivity extends AppCompatActivity {
    private static final String TAG = PaMainActivity.class.getSimpleName();
    private static  final String APP_ID="wx9ba209d200d40b7e";
    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (!PaAccountCons.getInstance(PaMainActivity.this).hasUser()) {
            regToWx();
            Intent intent = new Intent();
            intent.putExtra("url", PaAccountCons.LOGIN_URI);
            intent.setClass(this, PaWebViewActivity.class);
            startActivity(intent);
            finish();
        } else{
            Intent intent = new Intent(PaMainActivity.this, PaIndexPageActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void regToWx(){
        api= WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
    }
}
