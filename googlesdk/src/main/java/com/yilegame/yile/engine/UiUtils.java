package com.yilegame.yile.engine;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yilegame.http.Mlog;
import com.yilegame.http.Impl.UIThreadCallBack;

import com.yilegame.yile.ui.Utils;
import com.yilegame.http.uti.EditTextShake;
import com.yilegame.http.uti.NetworkUtils;

public class UiUtils {
		public static boolean checkEditText(EditText et_username,EditText et_password){
			String username = et_username.getText().toString().trim();
			String password = et_password.getText().toString().trim();
			if (TextUtils.isEmpty(username)) {
				showErrorView(OverSeasConductor.mcon.getString(Utils.getStringId(OverSeasConductor.mcon, "use_cannot_empty")));
				EditTextShake.startAnnmation(OverSeasConductor.mcon, et_username);
				return false;
			}
			if (TextUtils.isEmpty(password)) {
				showErrorView(OverSeasConductor.mcon.getString(Utils.getStringId(OverSeasConductor.mcon, "pwd_cannot_empty")));
				EditTextShake.startAnnmation(OverSeasConductor.mcon, et_password);
				return false;
			}
			if (username.length() < 6) {
				showErrorView(OverSeasConductor.mcon.getString(Utils.getStringId(OverSeasConductor.mcon, "username_min_6")));
				EditTextShake.startAnnmation(OverSeasConductor.mcon, et_username);
				return false;
			} else if (password.length() < 6) {
				showErrorView(OverSeasConductor.mcon.getString(Utils.getStringId(OverSeasConductor.mcon, "password_min_6")));
				EditTextShake.startAnnmation(OverSeasConductor.mcon, et_password);
				return false;
			}
			if (!NetworkUtils.isNetworkAvailable(OverSeasConductor.mcon)) {
				// Toast.makeText(this, "网络连接不可用，请稍后再试", 1).show();
				showErrorView(OverSeasConductor.mcon.getString(Utils.getStringId(OverSeasConductor.mcon, "net_unsupport")));
				return false;
			}
			return true;
		}
		public static int dip2px(Context context, float dipValue){ 
            final float scale = context.getResources().getDisplayMetrics().density; 
            return (int)(dipValue * scale + 0.5f); 
    } 
		private static void showErrorView(String string) {
			Toast.makeText(OverSeasConductor.mcon, string, 1).show();
		}
		public static void listViewParams(ListView lv,Context mcon,ArrayList<String> daoFirst) {
//			if (daoFirst == null || daoFirst.size() < 1) {
//				lv.setLayoutParams(new LinearLayout.LayoutParams(UiUtils.dip2px(mcon, 280), UiUtils.dip2px(mcon, 80)));
//			}else{
				lv.setLayoutParams(new LinearLayout.LayoutParams(UiUtils.dip2px(mcon, 280), UiUtils.dip2px(mcon, 160)));
//			}
		}
		public static void changeUITHread(final UIThreadCallBack callback,Context mcon){
			new Handler(mcon.getMainLooper()).post(new Runnable() {
				
				@Override
				public void run() {
					callback.change2UI();
				}
			});
		}
		public static ArrayList<Activity> list=new ArrayList<Activity>();
		public static void exit(){
			for(Activity instance:list){
				instance.finish();
				 Mlog.i("feng", "onTerminate+"+2.5);
			}
		}
}
