package com.yilegame.googlesdk;

import android.app.Activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;

import android.util.Log;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.yilegame.googlesdk.util.HttpUtils;
import com.yilegame.googlesdk.util.IabBroadcastReceiver;
import com.yilegame.googlesdk.util.IabHelper;
import com.yilegame.googlesdk.util.IabResult;
import com.yilegame.googlesdk.util.Inventory;
import com.yilegame.googlesdk.util.MD5;
import com.yilegame.googlesdk.util.Purchase;
import com.yilegame.googlesdk.util.PurchaseDao;
import com.yilegame.sdk.common.BaseActivity;
import com.yilegame.sdk.common.YLMessage;
import com.yilegame.sdk.protocol.ChannelInitMethod;
import com.yilegame.sdk.protocol.YLGameCallback;
import com.yilegame.sdk.utils.AES;
import com.yilegame.sdk.utils.Base64;
import com.yilegame.sdk.utils.HttpUtil;
import com.yilegame.sdk.utils.LogUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class YLGameSDK extends BaseActivity implements IabBroadcastReceiver.IabBroadcastListener ,GoogleApiClient.OnConnectionFailedListener{
    public static YLGameSDK YLGameSDK;
    public Activity activity;
    public Handler handler;
    public String gameId;
    public String serverCode;
    public String channelId;
    private static final int RC_SIGN_IN = 9002;
    static final String TAG = "YLGameSDK";
    boolean mIsPremium = false;
    static final String SKU_PREMIUM = "premium";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    private List<String> mlhx_Skus;
    // The helper object
    IabHelper mHelper;
    //时候开始做后台遍历
    private boolean IsStartErgodic;
    IabBroadcastReceiver mBroadcastReceiver;
    // Provides purchase notification while this app is running
    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;

    private YLGameSDK() {

    }

    public static YLGameSDK getInstance() {
        if (YLGameSDK == null) {
            YLGameSDK = new YLGameSDK();
            return YLGameSDK;
        }
        return YLGameSDK;
    }

    public void init(Activity activity, Handler handler,
                     final boolean testMode, String gameId,
                     String channelId, String talkingDataId, ArrayList<String> productIds) {
        sdkInit(activity, handler, testMode, gameId, channelId);
        mlhx_Skus = productIds;
        //默认可以进行后台遍历
        IsStartErgodic = true;
//        initGooglePaySdk(testMode);
        //谷歌初始化获得mGoogleApiClient
        super.init(activity, handler, testMode, gameId, channelId,
                talkingDataId, new ChannelInitMethod() {
                    @Override
                    public void doChannelInit() {// 渠道初始化方法
                        doActivate();
                        //TODO
                        initLogin();

                    }
                });
    }

    private void initLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getResources().getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage((AppCompatActivity)activity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Toast.makeText(activity,"初始化成功",Toast.LENGTH_SHORT).show();
    }

    public void onStart() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    //做激活请求
    private void doActivate() {
        super.doActivate(activity, QYConstant.activateUrl);
    }

    //sdk初始化
    private void sdkInit(Activity activity, Handler handler,
                         final boolean testMode, String gameId, String channelId) {
        this.activity = activity;
        this.handler = handler;
        // 设置网络模式
        QYConstant.setTestMode(testMode);
        // 做激活请求
        this.gameId = gameId;
        this.channelId = channelId;
    }
    private void initGooglePaySdk(boolean testMode){

        mHelper = new IabHelper(activity);
        // enable debug logging (for a production application, you should set this to false).
        //开启 log日志形式

        //TODO 是否打印google日志
        mHelper.enableDebugLogging(testMode);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(YLGameSDK.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                activity.registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            doPlatformLogin(null, acct.getIdToken(),null);
            Log.d(TAG,acct.getIdToken());
            Toast.makeText(activity,"登录成功:"+acct.getIdToken(),Toast.LENGTH_SHORT).show();
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(activity,"登录失败",Toast.LENGTH_SHORT).show();

        }
    }
    //TODO
    protected void doPlatformLogin(final String account, final String reserve1,
                                   final String reserve2) {
        BaseActivity.sendUserInfo2Server(QYConstant.loginUrl, account,
                reserve1, reserve2, new YLGameCallback() {
                    @Override
                    public void platFormLoginCallback(String userId,
                                                      String account, String sign) {
                        LogUtil.i(TAG, "login call back:userId:" + userId
                                + ";account:" + account + "；sign:" + sign);
                        QYConstant.userId = userId;
                    }
                });
    }
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }else{
            if (mHelper == null) return;
            LogUtil.i("onActivityResult","onActivityResult"+resultCode+"requestCode"+requestCode);
            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to in-app
                // billing...
                LogUtil.i("onActivityResult","onActivityResult"+resultCode);
                super.onActivityResult(requestCode, resultCode, data);
            }
            else {
                Log.d(TAG, "onActivityResult handled by IABUtil.");
                LogUtil.i("onActivityResult", "fasel");
            }
        }
    }


    IabHelper.OnConsumeFinishedListener FirstConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;
            LogUtil.i("gaolingshi", "FirstConsumeFinishedListener:          " + "OriginalJson:" + purchase.getOriginalJson());
            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                //开始有产品没有消耗 ，所以后台遍历放在这里进行　完成后要把IsStartErgodic重置为true  遍历完成一个删除一个
                if (!IsStartErgodic) {
                    //TODO  完成所有后台遍历重置IsStartErgodic=true
                    List<Purchase> Purchases = PurchaseDao.getInstatncePurchase(activity).getPurchase();
                    for (int i = 0; i < Purchases.size(); i++) {
                        // Map<String, Object> Params,String url, final Purchase purchase, final Activity activity
                        if (Purchases != null && Purchases.size() != 0) {
                            try {
                                JSONObject map = new JSONObject();
                                map.put("signedData", Purchases.get(i).getOriginalJson());
                                map.put("signature", Purchases.get(i).getSignature());
                                map.put("sign", MD5.md5Encoder(Purchases.get(i).getOriginalJson() + Purchases.get(i).getSignature() + QYConstant.mybackkey));
                                byte[] encodes = Base64.encode(AES.aesEncryptWithKey(HttpUtil.AESkey, map.toString().getBytes(), 0));
                                final String body = new String(encodes, "utf-8");
                                final Purchase getPurchase = Purchases.get(i);
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DoBackVerify(body, QYConstant.googlepayUrl, getPurchase, activity);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
//                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
//                saveData();
//                alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
            } else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            Log.d(TAG, "Query inventory finished");
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Failed to query inventory:" + result);
                return;
            }
            Log.d(TAG, "Query inventory was successful");

            // Do we have the premium upgrade? 检查是否要更新
            Purchase premiumPurchase = inv.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null);
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            // 按照产品id遍历持有商品进行消耗
            for (int i = 0; i < mlhx_Skus.size(); i++) {
                //返回被给产品id的已经购买但是未消耗的商品
                Purchase gasPurchase = inv.getPurchase(mlhx_Skus.get(i));
                //检查被给被给物品信是否 没有消耗的 非定制产品  是就消耗它
                if (gasPurchase != null) {
                    IsStartErgodic = false;
                    Log.d(TAG, "We have gas. Consuming it.");
                    //先做本地保存 然后在做消耗
                    long savereturn = PurchaseDao.getInstatncePurchase(activity).savePurchase(gasPurchase);
                    if (savereturn < 0) {
                        LogUtil.i(TAG, "初始话，数据库保存失败");
                    }
                    mHelper.consumeAsync(inv.getPurchase(mlhx_Skus.get(i)), FirstConsumeFinishedListener);
                }
            }
            //没有重置产品，直接进入后台数据的遍历  遍历完成一个 数据库删除一个
            if (IsStartErgodic) {
                List<Purchase> Purchases = PurchaseDao.getInstatncePurchase(activity).getPurchase();
                if (Purchases != null && Purchases.size() != 0) {
                    for (int i = 0; i < Purchases.size(); i++) {
                        // Map<String, Object> Params,String url, final Purchase purchase, final Activity activity
                        try {
                            JSONObject map = new JSONObject();
                            map.put("signedData", Purchases.get(i).getOriginalJson());
                            map.put("signature", Purchases.get(i).getSignature());
                            map.put("sign", MD5.md5Encoder(Purchases.get(i).getOriginalJson() + Purchases.get(i).getSignature() + QYConstant.mybackkey));
                            byte[] encodes = Base64.encode(AES.aesEncryptWithKey(HttpUtil.AESkey, map.toString().getBytes(), 0));
                            final String body = new String(encodes, "utf-8");
                            final Purchase getpuchase = Purchases.get(i);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DoBackVerify(body, QYConstant.googlepayUrl, getpuchase, activity);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            LogUtil.i("gaolingshi", "purchase=====Signature:" + purchase.getSignature() + "       purchaseDate:" + purchase.getOriginalJson() + "");
            Log.d(TAG, "Purchase successful.");

            if (mlhx_Skus.contains(purchase.getSku())) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
                long saveRetrun = PurchaseDao.getInstatncePurchase(activity).savePurchase(purchase);
                if (saveRetrun < 0) {
                    LogUtil.i(TAG, "数据库保存失败");
                }
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            } else if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                mIsPremium = true;
            }
        }
    };
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;
            LogUtil.i("gaolingshi", "mConsumeFinishedListener:" + "false");
            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                //TODO  消耗完成做 后台验证 验证返回200之后删除数据库
                // Map<String, Object> Params,String url, final Purchase purchase, final Activity activity
                try {
                    LogUtil.i("gaolingshi", "mConsumeFinishedListener:" + "true");
                    JSONObject map = new JSONObject();
                    map.put("signedData", purchase.getOriginalJson());
                    map.put("signature", purchase.getSignature());
                    map.put("sign", MD5.md5Encoder(purchase.getOriginalJson() + purchase.getSignature() + QYConstant.mybackkey));
                    byte[] encodes = Base64.encode(AES.aesEncryptWithKey(HttpUtil.AESkey, map.toString().getBytes(), 0));
                    final String body = new String(encodes, "utf-8");
                    final Purchase getpurchase = purchase;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.i("gaolingshi", "mConsumeFinishedListener:" + "+++++请求json" + body.toString());
                            // Toast.makeText(activity,"平台验证",Toast.LENGTH_LONG).show();
                            DoBackVerify(body, QYConstant.googlepayUrl, getpurchase, activity);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                //开始有产品没有消耗 ，所以后台遍历放在这里进行　完成后台遍历
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
//                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
//                saveData();
//                alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
            } else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    //做后台验证
    private void DoBackVerify(final String Params, final String url, final Purchase purchase, final Activity activity) {
        new Thread() {
            @Override
            public void run() {
                String googleJson = HttpUtils.doPost(Params, url, purchase, activity);
                LogUtil.i("gaolingshi", "DoBackVerify:" + googleJson);
                if (googleJson != null && !googleJson.isEmpty()) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(googleJson);
                        int code = object.getInt("errno");
                        if (code == 0) {
//                            YLMessage.sendOrderSuccessMessage(handler);
                        } else {
                            YLMessage.sendOrderFailMessage(handler);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void receivedBroadcast() {
        mHelper.queryInventoryAsync(mGotInventoryListener);
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(activity);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

    }
    public  void onResume(Activity activity) {
        super.onResume(activity);
    }
    public void onDestroy() {
        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }
    public  void onPause(Activity activity) {
        super.onPause(activity);
    }
}
