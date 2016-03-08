package com.feixun.phiaccount.wxapi;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.feixun.phiaccount.R;
import com.feixun.phiaccount.account.PaAccountCons;
import com.feixun.phiaccount.account.PaAccountManager;
import com.feixun.phiaccount.account.PaUser;
import com.feixun.phiaccount.tools.PaSysApplication;
import com.feixun.phiaccount.ui.PaIndexPageActivity;
import com.feixun.phiaccount.util.AccountAyncTask;
import com.feixun.phiaccount.util.HttpUtil;
import com.feixun.phiaccount.util.IHttpCallBack;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by He on 2016/3/7 0007.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = WXEntryActivity.class.getSimpleName();
    private IWXAPI api;
    private String resultAccessToken = "";
    private String resultUserInfo="";
    private String resultAfterLogin="";
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paactivity_wxentry);
        PaSysApplication.getInstance().addActivity(this);
        api = WXAPIFactory.createWXAPI(this, PaAccountCons.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
        mAccountManager = AccountManager.get(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.i(TAG, "baseReq.errCode");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Bundle bundle = new Bundle();
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                final String token = ((SendAuth.Resp) baseResp).token;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                                + PaAccountCons.WX_APP_ID
                                + "&secret=" + PaAccountCons.WX_APP_SECRET
                                + "&code=" + token + "&grant_type=authorization_code";
                        resultAccessToken = HttpUtil.get(url);
                        try {
                            JSONObject jsonResult = new JSONObject(resultAccessToken);
                            String url_UserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token="
                                    + jsonResult.get("access_token")
                                    + "&openid=" + jsonResult.get("openid");
                            resultUserInfo = HttpUtil.get(url_UserInfo);
                            JSONObject jsonUserInfo = new JSONObject(resultUserInfo);
                            String url_wxlogin = PaAccountCons.BASE_URL+"/union/wxlogin";

                            Map<String, String> params = new HashMap<String,String>();
                            params.put("openid", jsonUserInfo.getString("openid"));
                            String nickname=new String(jsonUserInfo.getString("nickname").getBytes("gbk"),"utf-8");
                            params.put("nickname",nickname );
                            params.put("sex", jsonUserInfo.getString("sex"));
                            params.put("language", jsonUserInfo.getString("language"));
                            params.put("city", jsonUserInfo.getString("city"));
                            params.put("province", jsonUserInfo.getString("province"));
                            params.put("country", jsonUserInfo.getString("country"));
                            params.put("headimgurl", jsonUserInfo.getString("headimgurl"));
                            params.put("unionid", jsonUserInfo.getString("unionid"));

                            AccountAyncTask task = new AccountAyncTask(url_wxlogin, params,
                                    new IHttpCallBack() {

                                        @Override
                                        public void onStart() {
                                            Log.i("K-123", "onStart()");
                                        }

                                        @Override
                                        public void onSuccess(byte[] b) {
                                            Log.i("K-123", "onSuccess()");
                                            String result=new String(b);
                                            Log.i("K-123", "result="+ result);
//                                            PaAccountManager.getInstance(WXEntryActivity.this).analyze();
                                            PaUser user = new PaUser();
                                            try {
                                                JSONObject mJsonob = new JSONObject(result);
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
                                                startActivity(new Intent(WXEntryActivity.this, PaIndexPageActivity.class));
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
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
//                                            finish();
                                            startActivity(new Intent(WXEntryActivity.this, PaIndexPageActivity.class));
                                        }

                                        @Override
                                        public void onFailure(String msg) {
                                            Log.i("K-123", "onFailure()");
                                        }
                                    });
                            task.execute();
                            Log.i("resultAfterLogin", resultAfterLogin);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
    }
}
