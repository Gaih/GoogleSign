package com.yilegame.googlesign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.yilegame.yile.engine.OverSeasConductor;
import com.yilegame.sdk.common.YLGameCode;
import com.yilegame.yile.ui.BaseActivity;

import java.util.ArrayList;

import static com.yilegame.googlesdk.YLGameSDK.YLGameSDK;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private class BwHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            String tip = "";
            Bundle b = msg.getData();
            tip = b.getString("msg");
            if (code == YLGameCode.YLGAMECODE_REQUEST_OVERTIME) {
                tip = "连接超时";
            } else if (code == YLGameCode.YLGAMECODE_ACTIVATE_SUCCESSFUL) {// 激活成功
                tip = "m激活成功";
            } else if (code == YLGameCode.YLGAMECODE_ACTIVATE_ERROR) {// 激活失败
                tip = "m激活失败";
            } else if (code == YLGameCode.YLGAMECODE_LOGIN_SUCCESSFUL) {// 登陆成功
                String userid = b.getString("userid");
                String account = b.getString("account");
                String sign = b.getString("sign");
                tip = "登陆成功;userid = " + userid + ";account:" + account+ "sign = " + sign;
            } else if (code == YLGameCode.YLGAMECODE_LOGIN_FAIL) {// 登陆失败*
                // 调用游戏的nativeLoginFail
                tip = "登录失败";
            } else if (code == YLGameCode.YLGAMECODE_LOGIN_CANCEL) {// 登陆取消 *
                // 调用游戏的nativeLoginFail
                tip = "登陆取消";
            } else if (code == YLGameCode.YLGAMECODE_LOGIN_OUT) {// 注销用户
                // 调用游戏的nativeReLogin
                tip = "注销用户";
                //YLGameSDK.getInstance().doLogin(EPlatform.ePlatform_QQ,MainActivity.this);
            } else if (code == YLGameCode.YLGAMECODE_REGISTER_SUCCESSFUL) {// 需要更新
                tip = "发现更新地址";
                String updateURL = b.getString("updateUrl");
                //	LogUtil.i(TAG, "获取更新地址成功,URL: " + updateURL);
            } else if (code == YLGameCode.YLGAMECODE_UPDATE_NO) { // 暂时没有更新
                tip = "暂时没有更新";
            } else if (code == YLGameCode.YLGAMECODE_ORDER_SUCCESSFUL) {// 支付成功
                tip = "支付成功";
            } else if (code == YLGameCode.YLGAMECODE_ORDER_ERROR) {// 支付失败
                tip = "支付失败";
            } else if (code == YLGameCode.YLGAMECODE_PLATFORM_ORDER_ERROR) {
                tip = "平台下单失败";
            }else {
                //Log.i("gaolingshi", "else:" + tip);
            }
            if (tip != "" && tip != null) {
                Log.i("gaolingshi", "else:" + tip);
                Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //facebooksdk初始化
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        ArrayList<String> arrayList=new ArrayList<String>();
        arrayList.add("lfantasy002200");
        arrayList.add("lfantasy005500");
        arrayList.add("lfantasy011000");
        arrayList.add("lfantasy033000");
        arrayList.add("lfantasy055000");
        arrayList.add("lfantasy110000");
        Handler handler=new BwHandler();
//        YLGameSDK.getInstance().init(this, handler,true, "2101", "2","BB89193D948C8901FC615D92D630A982", arrayList);
        OverSeasConductor.getInstance().init(this, handler, "199","29",  "62348D673CC9A0C61BCA8A051E212D83",this,false,true);

        findViewById(R.id.googleSign).setOnClickListener(this);
        findViewById(R.id.googlePay).setOnClickListener(this);
        findViewById(R.id.googleDismiss).setOnClickListener(this);
        findViewById(R.id.googleExit).setOnClickListener(this);
        findViewById(R.id.qingyou).setOnClickListener(this);
//        LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_sign);
//        loginButton.setReadPermissions("email");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        YLGameSDK.getInstance().onActivityResult(requestCode,resultCode,data);
    }

    public void doGameServer(String gameServer){

    }

    @Override
    protected void onStart() {
        super.onStart();
//        YLGameSDK.getInstance().onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        YLGameSDK.getInstance().onDestroy();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.googleSign:
                YLGameSDK.getInstance().signIn();
                break;
            case R.id.facebook_sign:

                break;
            case R.id.googlePay:

                break;
            case R.id.googleExit:
                YLGameSDK.getInstance().logout();
                break;
            case R.id.googleDismiss:
                YLGameSDK.getInstance().dismiss();
                break;
            case R.id.qingyou:
                OverSeasConductor.getInstance().login();


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        OverSeasConductor.getInstance().OnResume(this);
//        YLGameSDK.getInstance().onResume(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
//        YLGameSDK.getInstance().onPause(this);
    }
}

