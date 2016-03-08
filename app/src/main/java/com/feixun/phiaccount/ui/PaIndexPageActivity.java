package com.feixun.phiaccount.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feixun.phiaccount.R;
import com.feixun.phiaccount.account.PaAccountCons;
import com.feixun.phiaccount.account.PaAccountManager;
import com.feixun.phiaccount.tools.PaSysApplication;
import com.feixun.phiaccount.tools.PhiAlertDialog;

import java.io.IOException;

/**
 * Created by He on 2016/3/2 0002.
 */
public class PaIndexPageActivity extends Activity implements View.OnClickListener {
    private static final String TAG = PaIndexPageActivity.class.getSimpleName();

    private ImageView mPortrait;
    private Button mExitAccount;
    private TextView mAccountName;
    private RelativeLayout mFxCloud;
    private RelativeLayout mSport;
    private RelativeLayout mMarket;
    private RelativeLayout mBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaSysApplication.getInstance().addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.paactivity_indexpage);
        PaAccountCons.getInstance(this).setActivityCons(this);
        initView();
        updateView();
    }

    private void initView() {
        mPortrait = (ImageView) findViewById(R.id.portrait);
        mPortrait.setOnClickListener(this);
        mAccountName = (TextView) findViewById(R.id.account_name);
        mFxCloud = (RelativeLayout) findViewById(R.id.application_fxcloud);
        mFxCloud.setOnClickListener(this);
        mSport = (RelativeLayout) findViewById(R.id.application_sport);
        mSport.setOnClickListener(this);
        mMarket = (RelativeLayout) findViewById(R.id.application_market);
        mMarket.setOnClickListener(this);
        mBind = (RelativeLayout) findViewById(R.id.bind_account);
        mBind.setOnClickListener(this);
        mExitAccount = (Button) findViewById(R.id.exit_account_bt);
        mExitAccount.setOnClickListener(this);
    }

    private void updateView() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.portrait:
                AccountManager am = (AccountManager) getSystemService(ACCOUNT_SERVICE);
                Account accounts[] = am.getAccountsByType(PaAccountCons.ACCOUNT_TYPE);
                Intent intent = new Intent();
                intent.putExtra("url", PaAccountCons.PORTRAIT_URI +
                        "?phicommid=" + am.getUserData(accounts[0], "phicommId") +
                        "&openId=" + am.getUserData(accounts[0], "openId") +
                        "&expireseIn=" + am.getUserData(accounts[0], "expireseIn") +
                        "&accessToken=" + am.getUserData(accounts[0], "accessToken"));

                intent.setClass(PaIndexPageActivity.this, PaWebViewActivity.class);
                startActivity(intent);

                break;
            case R.id.application_fxcloud:
                try {
                    Intent intent2 = new Intent();
                    intent2.setClassName("com.feixun.fxcloud", "com.feixun.fxcloud.SplashActivity");
                    startActivity(intent2);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.installFxCloud, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.application_sport:
                try {
                    Intent intent3 = new Intent();
                    intent3.setClassName("com.phicomm.mobilecbb.sport", "com.phicomm.mobilecbb.sport.Splash");
                    startActivity(intent3);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.installSports, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.application_market:
//                try {
//                    Intent intent4 = new Intent();
//                    intent4.setClassName("com.feixun.market", "com.feixun.market.Splash");
//                    startActivity(intent4);
//                } catch (Exception e) {
//                    Toast.makeText(this, R.string.installMarket, Toast.LENGTH_SHORT).show();
//                }

                break;
            case R.id.bind_account:
                Toast.makeText(this, R.string.jumpThird, Toast.LENGTH_SHORT).show();
                break;
            case R.id.exit_account_bt:
//                showExitAccoutDialog();
                PaAccountCons.getInstance(PaIndexPageActivity.this).alertDeleteAccount();
                break;


        }
    }

    private static boolean isExit = false;

    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), R.string.buttonAgain,
                    Toast.LENGTH_SHORT).show();

            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Log.e(TAG, "exit application");
            PaSysApplication.getInstance().exit();

        }
    }

    public void showExitAccoutDialog() {
        PhiAlertDialog.Builder builder = new PhiAlertDialog.Builder(this);
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
                        PaIndexPageActivity.this.sendBroadcast(intent);

                        // 延迟500ms删除账号并退出应用
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PaAccountManager accountManager = PaAccountManager.getInstance(PaIndexPageActivity.this);
                                accountManager.exitAccount(PaIndexPageActivity.this);
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
}
