package com.yilegame.googlesdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.yilegame.http.Mlog;
import com.yilegame.http.constant.UserInfos;
import java.util.List;
import static com.yilegame.http.constant.QYConstant.testMode;

public class QYConstant {
	//sdk的版本号
	public static String sdkVersion = "37sdk_release_v2.1.2_20151117";
    public static String mybackkey="qinGyou.yiLe2014_08_11#$";
	public static final int QYCODE_REQUEST_TIMEOUT = 1;

	public static final String QYCODE_REQUEST_TIMEOUT_MSG = "请求超时，请稍后重试" ;
	public static final String APP_NAME = "谷歌支付";

	public static String ip="";
	public static String loginUrl="";
	public static String payUrl= "";
	public static String updateUrl="";
	public static String notifyUrl="";
	public static String activateUrl="";
	public  static String googlepayUrl="";
	// 登陆平台成功以后返回的参数
	public static String userId="";
	public static String account ="";
	public static String sign = "";

	private static final String TEST_URL = "http://115.28.84.240:10075";
	private static final String PUBLISH_URL = "http://lfserver.gurumcompany.com";

	////服务器的测试模式，true测试ip，false外网的ip
	public static void setTestMode(boolean testMode){
		ip=testMode? TEST_URL:PUBLISH_URL;

		payUrl=ip+"/platform/sdk/channelorder.action";
		activateUrl =ip+"/platform/sdk/activating.action";
		googlepayUrl=ip+"/platform/google/googleconsumernotify.action";

		loginUrl =ip+"/platform/google/googlelogin.action";
        //青游
        if (testMode) {
            Mlog.setLevel(999);
        }else{
            Mlog.setLevel(-999);
        }
	}



    private static QYConstant instance = null;
    private static SharedPreferences sp;
    public static List<String> passNum;
    public static List<String> Name;
    public static List<String> payId;
    public static List<String> channelId;
    public static List<List<String>> cardMoney;
    public static List<String> cardNum;
    private static final String TEST_URL_QY = "http://ucentertest.qingyou.cn";
    private static final String PUBLISH_URL_QY = "http://ucenter.qingyou.cn";
    private static final String TEST_URL_PAY_QY = "http://pcentertest.qingyou.cn";
    private static final String PUBLISH_URL_PAY_QY = "http://pcenter.qingyou.cn";
    private QYConstant() {

    }

    public static QYConstant getInstance() {
        if (instance == null) {
            instance = new QYConstant();
        }

        return instance;
    }
    // 现网
    // public String payUrl=ip+"/platform/sdk/pay.action";
    // public String urlRegister=ip+"/platform/sdk/register.action";
    // public String urlTalkingData=ip+"/platform/sdk/gettallkdataid.action";
    // public String urlLogin=ip+"/platform/sdk/login.action";
    // public String urlUpdate=ip+"/platform/sdk/updatepacket.action";
    // public String urlPayResult=ip+"/platform/sdk/payresult.action";
    public String AESkey = "ytr9uyfdgd0op23w";

    public String getIp(Context mcon, String Type) {
        String ip;
        String payIp;
        ip = testMode?TEST_URL:PUBLISH_URL;
        payIp = testMode?TEST_URL_PAY_QY:PUBLISH_URL_PAY_QY;
        if (Type.equals("urlYeePay")) {
            return payIp + "/platform/page/pay/myeepay.jsp";
        } else if (Type.equals("urlRegister")) {
            return ip + "/platform/sdk/register.action";
        } else if (Type.equals("urlTalkingData")) {
            return ip + "/platform/sdk/gettallkdataid.action";
        } else if (Type.equals("urlLogin")) {
            return ip + "/platform/sdk/login.action";
        } else if (Type.equals("urlUpdate")) {
            return ip + "/platform/sdk/updatepacket.action";
        } else if (Type.equals("urlPayResult")) {
            return payIp + "/platform/sdk/payresult.action";
        } else if (Type.equals("urlDeviceLogin")) {
            return ip + "/platform/sdk/devicelogin.action";
        } else if (Type.equals("urlalipayreturn")) {
            return payIp + "/platform/sdk/alipayfastnotify.action";
            // return ip + "/platform/sdk/alipaynotify.action";
        } else if (Type.equals("urlalipayOrderId")) {
            return payIp + "/platform/sdk/channelorder.action";
        } else if (Type.equals("urlcheckregisterdeviceid")) {
            return ip + "/platform/sdk/checkregisterdeviceid.action";
        } else if (Type.equals("urlresetpassword")) {
            return ip + "/platform/sdk/resetpassword.action";
        } else if (Type.equals("url19payinfo")) {
            return payIp + "/platform/sdk/19paychannel.action";
        } else if (Type.equals("url19pay")) {
            return payIp + "/platform/sdk/19pay.action";
        } else if (Type.equals("payresult")) {
            return payIp + "/platform/sdk//payresult.action";
        }else if (Type.equals("activating")) {
            return ip + "/platform/sdk/activating.action";
        }else if(Type.equals("bindphonesendsms")){
            return ip+"/platform/api/bindphone.action";
        }else if(Type.equals("phonesendsms")){
            return ip+"/platform/api/sendsmsforregister.action";
        }else if(Type.equals("bindphone")){
            return ip+"/platform/api/checkphone.action";
        }else if(Type.equals("findpassforphonegetcode")){
            return ip+"/platform/api/findpasswordbyphone.action";
        }else if(Type.equals("resetpassphone"))
        {
            return ip+"/platform/api/resettingpassword.action";
        }else if(Type.equals("wxnotifyUrl"))
        {
            return payIp + "/platform/sdk/nowpaynotifyv1.action";
        }else if(Type.equals("caifu")){
            return payIp+"/platform/tenpay/wtenpay.jsp";
        }else if(Type.equals("nowsign"))
        {
            return payIp+"/platform/sdk/generalnowpaysignv1.action";
        }
        return null;
    }
    public static void setNoPwdQuestion(Context mcon,boolean ischeck){
        SharedPreferences sp = mcon.getSharedPreferences("xknj"+UserInfos.userId, mcon.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("hasset", ischeck);
        edit.commit();
    }
    public static boolean getPwdInfo(Context mcon) {
        SharedPreferences sp = mcon.getSharedPreferences("xknj"+UserInfos.userId, mcon.MODE_PRIVATE);
        return sp.getBoolean("hasset", false);
    }
    /*
     * 设置密保问题提示（第三次登陆提示）
     * */
    public static void setPwdProtectedTips(Context mcon,int cishu){
        SharedPreferences sp = mcon.getSharedPreferences("asd"+UserInfos.userId, mcon.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putInt("cishu", cishu);

        edit.commit();
    }

    public static int getPwdProtectedTips(Context mcon) {
        SharedPreferences sp = mcon.getSharedPreferences("asd"+ UserInfos.userId, mcon.MODE_PRIVATE);
        return sp.getInt("cishu", 0);
    }

    /*
     * 设置检查手机绑定次数（第六次登陆提示）
     */
    public static int getPwdPhoneProtectedTips(Context mcon){
        SharedPreferences sp = mcon.getSharedPreferences("phone"+UserInfos.userId, mcon.MODE_PRIVATE);
        return sp.getInt("phonetip", 0);
    }

    public static void setPwdPhoneProtectedTips(Context mcon,int cishu){
        SharedPreferences sp = mcon.getSharedPreferences("phone"+UserInfos.userId, mcon.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putInt("phonetip", cishu);

        edit.commit();
    }
}
