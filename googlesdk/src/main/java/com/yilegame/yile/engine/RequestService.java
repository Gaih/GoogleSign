package com.yilegame.yile.engine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.yilegame.http.Mlog;
import com.yilegame.http.constant.QYConstant;
import com.yilegame.http.constant.UserInfos;
import com.yilegame.http.uti.AES;
import com.yilegame.http.uti.Base64;
import com.yilegame.http.uti.PhoneUtils;
import com.yilegame.http.uti.StreamTools;

public class RequestService {
	private static Map<String, String> resultMap;
	private static Map<String, String> parseJson;
	private static String username;

	/**
	 * 获取支付结果
	 * 
	 * 
	 */
	public static void Post2Service4PayResult(Handler handler, Context mcon,
			String url) {
		HttpURLConnection mHttpURLConnection = null;
		OutputStream mOutputStream = null;
		byte[] encode;
		try {
			mHttpURLConnection = ((HttpURLConnection) new URL(url)
					.openConnection());
			mHttpURLConnection.setRequestMethod("POST");
			mHttpURLConnection.setRequestProperty("connection", "close");
			mHttpURLConnection.setRequestProperty("Content-Type", "text/html");
			mHttpURLConnection.setConnectTimeout(5000);
			mHttpURLConnection.setDoOutput(true);
			mOutputStream = mHttpURLConnection.getOutputStream();
			mOutputStream.flush();
			Mlog.i("fengge", "localHttpURLConnection.getResponseCode():"
					+ mHttpURLConnection.getResponseCode());
			if (mHttpURLConnection.getResponseCode() == 200) {
				String returnData = StreamTools
						.readFromStream(mHttpURLConnection.getInputStream());
				JSONObject json = new JSONObject(returnData);
				if (json.get("code") != null && returnData != null) {
					Message msg = new Message();
					msg.what = Integer.parseInt((String) json.get("code"));
					Bundle b = new Bundle();
					b.putString("code", (String) json.get("code"));
					b.putString("msg", (String) json.get("msg"));
					msg.setData(b);
					QYSDKManager.handler.sendMessage(msg);
				}
				if (mOutputStream != null) {
					mOutputStream.close();
				}
			}
			if (mHttpURLConnection.getResponseCode() == 408) {
				Toast.makeText(mcon, "请求超时，请稍后再试", 1).show();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mHttpURLConnection != null) {
				mHttpURLConnection.disconnect();
			}
		}
	}

	/**
	 * 获取手机卡支付信息
	 * 
	 * 
	 */
	public static List<String> temp;
	private static Map<String, String> resultMap2;
	private static Map<String, String> payResultMap;

	public static void Post2ServiceGetPayPhoneCard(Handler handler,
			Context mcon, String url) {
		HttpURLConnection mHttpURLConnection = null;
		OutputStream mOutputStream = null;
		byte[] encode;
		try {
			mHttpURLConnection = ((HttpURLConnection) new URL(url)
					.openConnection());
			mHttpURLConnection.setRequestMethod("POST");
			mHttpURLConnection.setRequestProperty("connection", "close");
			mHttpURLConnection.setRequestProperty("Content-Type", "text/html");
			mHttpURLConnection.setConnectTimeout(5000);
			mHttpURLConnection.setDoOutput(true);
			mOutputStream = mHttpURLConnection.getOutputStream();
			mOutputStream.flush();
			Mlog.i("fengge", "localHttpURLConnection.getResponseCode():"
					+ mHttpURLConnection.getResponseCode());
			if (mHttpURLConnection.getResponseCode() == 200) {
				String returnData = StreamTools
						.readFromStream(mHttpURLConnection.getInputStream());
				JSONObject json = new JSONObject(returnData);
				Log.i("feng","returnData="+ returnData);
				if (json.get("code") != null && returnData != null) {
					QYConstant.passNum = new ArrayList<String>();
					QYConstant.Name = new ArrayList<String>();
					QYConstant.payId = new ArrayList<String>();
					QYConstant.channelId = new ArrayList<String>();
					QYConstant.cardMoney = new ArrayList<List<String>>();
					QYConstant.cardNum = new ArrayList<String>();
					Mlog.i("feng", "json" + json.toString());
					Mlog.i("feng", "phonecard:code:" + json.get("code"));
					Mlog.i("feng", "phonecard:data:"
							+ json.get("data").toString());
					JSONObject jsonData = (JSONObject) json.get("data");
					JSONArray jsonArray = jsonData.getJSONArray("channels");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject mjsonObject = (JSONObject) jsonArray.get(i);
						String cardMoneyStr = mjsonObject
								.getString("cardMoney");
						Log.i("feng","cardMoneyStr"+ cardMoneyStr);
						String[] split = cardMoneyStr.split(",");
						Log.i("feng","split="+ split.length);
						temp = new ArrayList<String>();
						for (String s : split) {
							Log.i("feng","split->s=="+ s);
							temp.add(s);
						}

						QYConstant.passNum
								.add(mjsonObject.getString("passNum"));
						QYConstant.Name.add(mjsonObject.getString("Name"));
						QYConstant.payId.add(mjsonObject.getString("payId"));
						QYConstant.channelId.add(mjsonObject
								.getString("channelId"));
						QYConstant.cardMoney.add(temp);
						QYConstant.cardNum
								.add(mjsonObject.getString("cardNum"));
						temp = null;
					}
					Mlog.e("feng", QYConstant.passNum.toString());
					Mlog.e("feng", QYConstant.Name.toString());
					Mlog.e("feng", QYConstant.payId.toString());
					Mlog.e("feng", QYConstant.channelId.toString());
					Mlog.e("feng", QYConstant.cardMoney.toString());
					Mlog.e("feng", QYConstant.cardNum.toString());
					Message msg = new Message();
					msg.what = Integer.parseInt((String) json.get("code"));
					Bundle b = new Bundle();
					b.putString("code", (String) json.get("code"));
					msg.setData(b);
					QYSDKManager.handler.sendMessage(msg);
				}
				if (mOutputStream != null) {
					mOutputStream.close();
				}
			}
			if (mHttpURLConnection.getResponseCode() == 408) {
				Toast.makeText(mcon, "请求超时，请稍后再试", 1).show();
			}
		}  catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mHttpURLConnection != null) {
				mHttpURLConnection.disconnect();
			}
		}
	}


	/**
	 * 支付下单结果查询(通用)
	 * 
	 * 
	 */
	public static Map<String, String> Post2Service4PayResult(Context mcon,
			String url, String orderId) {
		HttpURLConnection localHttpURLConnection = null;
		OutputStream localOutputStream = null;
		byte[] encode;
		try {
			localHttpURLConnection = ((HttpURLConnection) new URL(url
					+ "?orderId=" + orderId).openConnection());
			localHttpURLConnection.setRequestMethod("POST");
			localHttpURLConnection.setRequestProperty("Content-Type",
					"text/html");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ordereId", orderId);
			encode = jsonObject.toString().getBytes("UTF-8");
			localHttpURLConnection.setConnectTimeout(5000);
			localHttpURLConnection.setDoOutput(true);
			localOutputStream = localHttpURLConnection.getOutputStream();
			localOutputStream.write(encode);
			localOutputStream.flush();
			Mlog.i("fengge", "localHttpURLConnection.getResponseCode():"
					+ localHttpURLConnection.getResponseCode());
			if (localHttpURLConnection.getResponseCode() == 200) {
				String returnData = StreamTools
						.readFromStream(localHttpURLConnection.getInputStream());
				JSONObject json = new JSONObject(returnData);
				if (json.get("code") != null && returnData != null) {
					payResultMap = new HashMap<String, String>();
					try {
						payResultMap.put("code", json.getString("code"));
						payResultMap.put("msg", json.getString("msg"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return payResultMap;
				}
				if (localOutputStream != null) {
					localOutputStream.close();
				}
			}
			if (localHttpURLConnection.getResponseCode() == 408) {
				Toast.makeText(mcon, "请求超时，请稍后再试", 1).show();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		}  catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (localHttpURLConnection != null) {
				localHttpURLConnection.disconnect();
			}
		}
		return null;
	}

	/**
	 * 91pay支付提交
	 * 
	 * @param num2
	 *            ,String productName
	 * @param payMoney
	 * @param num1
	 * @param payId
	 * @param payChannelId
	 * @param orderId
	 * @param extraData
	 * @param gameServerZone
	 * 
	 * 
	 */
	public static Map<String, String> Post2ServiceGetPayPhoneCard(Context mcon,
			String url, String orderId, String payChannelId, String payId,
			String num1, int payMoney, int num2, String productName,
			String gameServerZone, String extraData,String roleId) {
		HttpURLConnection localHttpURLConnection = null;
		OutputStream localOutputStream = null;
		byte[] encode;
		try {
			localHttpURLConnection = ((HttpURLConnection) new URL(url)
					.openConnection());
			localHttpURLConnection.setRequestMethod("POST");
			localHttpURLConnection.setRequestProperty("Content-Type",
					"text/html");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("orderId", orderId);
			jsonObject.put("payChannelId", payChannelId);
			jsonObject.put("payId", payId);
			jsonObject.put("num1", num1);
			jsonObject.put("money", payMoney);// int
			jsonObject.put("num2", num2);// int
			jsonObject.put("type", 1);
			jsonObject.put("productName", productName);
			jsonObject.put("serverCode", gameServerZone);
			jsonObject.put("extraData", extraData);
			jsonObject.put("roleId", roleId);
			encode = Base64
					.encode(AES.aesEncryptWithKey(
							UserInfos.AESkey, jsonObject
									.toString().getBytes(), 0));
			localHttpURLConnection.setConnectTimeout(5000);
			localHttpURLConnection.setDoOutput(true);
			localOutputStream = localHttpURLConnection.getOutputStream();
			localOutputStream.write(encode);
			localOutputStream.flush();
			Mlog.i("fengge", "localHttpURLConnection.getResponseCode():"
					+ localHttpURLConnection.getResponseCode());
			if (localHttpURLConnection.getResponseCode() == 200) {
				resultMap2 = new HashMap<String, String>();
				String returnData = StreamTools
						.readFromStream(localHttpURLConnection.getInputStream());
				JSONObject json = new JSONObject(returnData);
				Mlog.i("fengge", "19点卡支付" + json.get("code"));
				if (json.get("code") != null && returnData != null) {
					try {
						if ("1733".equals(json.get("code"))) {
							Mlog.i("fengge", "19点卡支付下单那成功" + json.get("code"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					resultMap2.put("code", json.getString("code"));
					resultMap2.put("msg", json.getString("msg"));
					return resultMap2;
				}
				if (localOutputStream != null) {
					localOutputStream.close();
				}
			}
			if (localHttpURLConnection.getResponseCode() == 408) {
				Toast.makeText(mcon, "请求超时，请稍后再试", 1).show();
			}
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (localHttpURLConnection != null) {
				localHttpURLConnection.disconnect();
			}
		}
		return null;
	}

	/**
	 * 
	 * 获取充值记录
	 * 
	 */
	public static void Post2ServiceCharge(Handler handler, Context mcon,
			String url, String type) {
		HttpURLConnection localHttpURLConnection = null;
		OutputStream localOutputStream = null;
		byte[] encode;
		try {
			localHttpURLConnection = ((HttpURLConnection) new URL(url)
					.openConnection());
			localHttpURLConnection.setRequestMethod("POST");
			localHttpURLConnection.setRequestProperty("Content-Type",
					"text/html");
			localHttpURLConnection.setConnectTimeout(5000);
			localHttpURLConnection.setDoOutput(true);
			localOutputStream = localHttpURLConnection.getOutputStream();
			localOutputStream.flush();
			Mlog.i("fengge", "localHttpURLConnection.getResponseCode():"
					+ localHttpURLConnection.getResponseCode());
			if (localHttpURLConnection.getResponseCode() == 200) {
				String returnData = StreamTools
						.readFromStream(localHttpURLConnection.getInputStream());
				JSONObject json = new JSONObject(returnData);
				if (json.get("code") != null && returnData != null) {
					try {
						JSONArray ja = (JSONArray) json.get("orderid");
						for (int i = 0; i < ja.length(); i++) {
							JSONObject o = (JSONObject) ja.get(i);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Message msg = new Message();
					msg.what = 1;
					Bundle b = new Bundle();
					b.putString("msg", (String) json.get("msg"));
					handler.sendMessage(msg);
				}
				if (localOutputStream != null) {
					localOutputStream.close();
				}
			}
			if (localHttpURLConnection.getResponseCode() == 408) {
				Toast.makeText(mcon, "请求超时，请稍后再试", 1).show();
			}
		}  catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		}  catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (localHttpURLConnection != null) {
				localHttpURLConnection.disconnect();
			}
		}
	}
	
	/**
	 * 支付下单
	 * 
	 * 
	 */
  public static String Post2ServiceCharge(Handler handler,Context mcon, String url,
		  String money,String productName,String gameId,String serverCode,String extraData,String channelId,String orderId,String roleId) {
	  money=""+(Integer.parseInt(money))*100;
	  HttpURLConnection localHttpURLConnection = null;
	  OutputStream localOutputStream = null;
	  byte[] encode;
	  Log.i("feng", "orderId:" + orderId+"--->userId="+UserInfos.userId);
	  try {
		  localHttpURLConnection = ((HttpURLConnection) new URL(url+"?userId="+UserInfos.userId)
		  .openConnection());
		  localHttpURLConnection.setRequestMethod("POST");
		  localHttpURLConnection.setRequestProperty("Content-Type",
				  "text/html");
		  JSONObject jsonObject=new JSONObject();
		  jsonObject.put("sdkVersion", UserInfos.sdkVersion);
			jsonObject.put("gameId", gameId);
			jsonObject.put("channelId", channelId);
			jsonObject.put("userId", UserInfos.userId);
			jsonObject.put("ordereId",orderId );
			jsonObject.put("roleId", roleId);
			jsonObject.put("rechargeAmount", money);
			Mlog.e("feng", "newmoney;"+money);
			jsonObject.put("currency", "CNY");
			jsonObject.put("productName", productName);
			jsonObject.put("ProductDes", "");
			jsonObject.put("serverCode", serverCode);
			jsonObject.put("extraData", extraData);
			jsonObject.put("payType", "5");
			jsonObject.put("imei", PhoneUtils.getIMEI(mcon));
			jsonObject.put("mac", PhoneUtils.getMAC(mcon));
			encode = Base64.encode(AES.aesEncryptWithKey(QYConstant.getInstance().AESkey,
					jsonObject.toString().getBytes(), 0));
		  localHttpURLConnection.setConnectTimeout(5000);
		  localHttpURLConnection.setDoOutput(true);
		  localOutputStream = localHttpURLConnection.getOutputStream();
		  localOutputStream.write(encode);
		  localOutputStream.flush();
		  Mlog.i("fengge", "localHttpURLConnection.getResponseCode():"
				  + localHttpURLConnection.getResponseCode());
		  if (localHttpURLConnection.getResponseCode() == 200) {
			  String returnData = StreamTools
					  .readFromStream(localHttpURLConnection.getInputStream());
			  JSONObject json=new JSONObject(returnData);
			  Mlog.i("fengge", "支付"+json.get("code"));
			  if (json.get("code")!=null&&returnData!=null) {
					try {
						if ("1700".equals(json.get("code"))) {
							  Mlog.i("fengge", "下单那成功"+json.get("code"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				  Message msg=new Message();
				  msg.what=1;
				  Bundle b=new Bundle();
				  b.putString("msg", (String) json.get("msg"));
				  handler.sendMessage(msg);
				  return ""+json.get("code");
			  }
			  if (localOutputStream != null) {
				  localOutputStream.close();
			  }
		  }
		  if (localHttpURLConnection.getResponseCode() == 408) {
			  Toast.makeText(mcon, "请求超时，请稍后再试", 1).show();
		  }
	  } catch (InvalidKeyException e) {
		  e.printStackTrace();
	  } catch (MalformedURLException e) {
		  e.printStackTrace();
	  } catch (ProtocolException e) {
		  e.printStackTrace();
	  } catch (NoSuchAlgorithmException e) {
		  e.printStackTrace();
	  } catch (NoSuchPaddingException e) {
		  e.printStackTrace();
	  } catch (IllegalBlockSizeException e) {
		  e.printStackTrace();
	  } catch (BadPaddingException e) {
		  e.printStackTrace();
	  } catch (UnsupportedEncodingException e) {
		  e.printStackTrace();
	  } catch (IOException e) {
		  e.printStackTrace();
	  } catch (JSONException e) {
		  e.printStackTrace();
	  } catch (Exception e) {
		  e.printStackTrace();
	  } finally {
		  if (localHttpURLConnection != null) {
			  localHttpURLConnection.disconnect();
		  }
	  }
	return "";
  }
	/**
	 * 登陆,talkingdata初始化,快速注册
	 * 
	 * @param
	 * @return
	 * */
	public static Map<String, String> Post2Service(String infoOne, String infoTwo,
			Context mcon, String url, String type,String gameId,String channelId) {
		username = infoOne;
		Mlog.i("feng", "QYSDKManager  POST提交至服务器");
		HttpURLConnection localHttpURLConnection = null;
		OutputStream localOutputStream = null;
		byte[] encode;
		try {
			localHttpURLConnection = ((HttpURLConnection) new URL(url)
					.openConnection());
			localHttpURLConnection.setRequestMethod("POST");
			JSONObject jsonObject = new JSONObject();
			localHttpURLConnection.setRequestProperty("Content-Type",
					"text/html");
			localHttpURLConnection.setRequestProperty("connection",
					"close");
			if ("talkdata".equals(type) || "update".equals(type)) {
				encode = url.toString().getBytes();
			} else {
				jsonObject.put("account", infoOne);
				jsonObject.put("password", infoTwo);
				jsonObject.put("gameid", gameId);
				jsonObject.put("channelid", channelId);
				jsonObject.put("devtype", "1");
				jsonObject.put("imei", PhoneUtils.getIMEI(mcon));
				jsonObject.put("mac", PhoneUtils.getMAC(mcon));
				if ("fastRegister".equals(type)) {
					jsonObject.put("type", "0");
				} else if ("normalRegister".equals(type)) {
					jsonObject.put("type", "1");
				}
				jsonObject.put("brand", PhoneUtils.getModelName());
				encode = Base64.encode(AES.aesEncryptWithKey(QYConstant.getInstance().AESkey,
						jsonObject.toString().getBytes(), 0));
			}
			localHttpURLConnection.setConnectTimeout(5000);
			localHttpURLConnection.setDoOutput(true);
			localOutputStream = localHttpURLConnection.getOutputStream();

			localOutputStream.write(encode);
			localOutputStream.flush();
			Mlog.i("fengge", "localHttpURLConnection.getResponseCode():"
					+ localHttpURLConnection.getResponseCode()+type);
			if (localHttpURLConnection.getResponseCode() == 200) {
				String returnData = StreamTools
						.readFromStream(localHttpURLConnection.getInputStream());
				if ("talkdata".equals(type)) {
					parseJson = parseJsonForTalkData(returnData);
				} else if ("update".equals(type)) {
					parseJson = parseJsonForUpdate(returnData);
				} else if ("fastRegister".equals(type)) {
					Mlog.i("feng", "fastRegister:start");
					parseJson=parseJsonForFastRegister(returnData);
					Mlog.i("feng", "fastRegister:over");
				} else if ("normalRegister".equals(type)) {
					parseJson=parseJsonFornormalRegister(returnData);
				}
				Mlog.i("fengge", "returnData:" + returnData);
				Mlog.i("fengge", "whatwhat");
				Mlog.i("fengge", "parseJson:" + parseJson.toString());
				return parseJson;
			}
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;

	}
	/**
	 * 
	 * 游戏激活
	 * 
	 */
	public static boolean Post2Service(Context mcon, Handler handler,
			String url, String gameId, String channelId) {
		HttpURLConnection localHttpURLConnection = null;
		OutputStream localOutputStream = null;
		byte[] encode;
		try {
			localHttpURLConnection = ((HttpURLConnection) new URL(url
					+ "?gameId=" + gameId + "&channelId=" + channelId + "&mac="
					+ PhoneUtils.getMAC(mcon) + "&imei="
					+ PhoneUtils.getIMEI(mcon)).openConnection());
			localHttpURLConnection.setRequestMethod("POST");
			localHttpURLConnection.setRequestProperty("Content-Type",
					"text/html");
			localHttpURLConnection.setConnectTimeout(5000);
			localHttpURLConnection.setDoOutput(true);
			localOutputStream = localHttpURLConnection.getOutputStream();
			localOutputStream.flush();
			Mlog.i("fengge",
					"游戏激活.ResponseCode:"
							+ localHttpURLConnection.getResponseCode());
			if (localHttpURLConnection.getResponseCode() == 200) {
				String returnData = StreamTools
						.readFromStream(localHttpURLConnection.getInputStream());
				JSONObject json = new JSONObject(returnData);
				Mlog.e("feng", "codenum:" + json.get("code"));
				if (json.get("code") != null && returnData != null) {
					try {
						if ("4000".equals(json.get("code"))) {
							return true;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				Message msg = new Message();
				msg.what = 1;
				Bundle b = new Bundle();
				b.putString("msg", (String) json.get("msg"));
				handler.sendMessage(msg);
				if (localOutputStream != null) {
					localOutputStream.close();
				}
			}
			if (localHttpURLConnection.getResponseCode() == 408) {
				Toast.makeText(mcon, "请求超时，请稍后再试", 1).show();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (localHttpURLConnection != null) {
				localHttpURLConnection.disconnect();
			}
		}
		return false;
	}

	/**
	 * 获取服务器账号记录
	 * 
	 * 
	 */
	public static List<String> Post2ServiceDeviceLogin(Handler handler,
			Context mcon, String url) {
		HttpURLConnection localHttpURLConnection = null;
		OutputStream localOutputStream = null;
		byte[] encode;
		try {
			localHttpURLConnection = ((HttpURLConnection) new URL(url
					+ "?deviceId=" + PhoneUtils.getIMEI(mcon)).openConnection());
			localHttpURLConnection.setRequestMethod("POST");
			localHttpURLConnection.setRequestProperty("Content-Type",
					"text/html");
			localHttpURLConnection.setRequestProperty("connection", "close");
			localHttpURLConnection.setConnectTimeout(5000);
			localHttpURLConnection.setDoOutput(true);
			localOutputStream = localHttpURLConnection.getOutputStream();
			localOutputStream.flush();
			Mlog.i("fengge", "DeviceLogin.getResponseCode():"
					+ localHttpURLConnection.getResponseCode());
			if (localHttpURLConnection.getResponseCode() == 200) {
				String returnData = StreamTools
						.readFromStream(localHttpURLConnection.getInputStream());
				Mlog.i("fengge", "returnData:" + returnData);
				JSONObject json = new JSONObject(returnData);
				if (json.get("code") != null && returnData != null) {
					if (json.getInt("code") == 3100) {
						JSONArray jsonArray = json.getJSONArray("accounts");
						List<String> accountList = new ArrayList<String>();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonobj = jsonArray.getJSONObject(i);
							if (jsonArray.length() < 1) {
								return null;
							}
							accountList.add((String) jsonobj.get("account"));
							Mlog.i("feng", "account:" + jsonobj.get("account"));
						}
						return accountList;
					}
					Mlog.i("feng", "code" + json.get("code"));
				}
				if (localOutputStream != null) {
					localOutputStream.close();
				}
			}
			if (localHttpURLConnection.getResponseCode() == 408) {
				Toast.makeText(mcon, "请求超时，请稍后再试", 1).show();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (localHttpURLConnection != null) {
				localHttpURLConnection.disconnect();
			}
		}
		return null;
	}


	/**
	 * 解析最终注册返回数据
	 * 
	 */
	private static Map<String, String> parseJsonFornormalRegister(
			String strResult) {

		try {
			if (resultMap == null) {
				resultMap = new HashMap<String, String>();
				Mlog.i("feng", "resultMap == null");
			}
			Mlog.i("feng", "resultMap != null");
			JSONObject jsonObj = new JSONObject(strResult);
			String valueCode = jsonObj.getString("code");
			String valueMsg = jsonObj.getString("msg");
			Mlog.i("feng", "valueCode:" + valueCode);
			if ("1100".equals(valueCode)) {
				String data = jsonObj.getString("data");
				String dataInfo = new String(
						AES.aesDecryptWithKey(UserInfos.AESkey,
								Base64.decode(data), 0), "utf-8");
				JSONObject jsonInfo = new JSONObject(dataInfo);
				resultMap.put("account", jsonInfo.getString("account"));
				resultMap.put("password", jsonInfo.getString("password"));
				Mlog.i("feng", "1100");
			}
			resultMap.put("code", valueCode);
			resultMap.put("msg", valueMsg);
			return resultMap;
		} catch (JSONException e) {
			System.out.println("Json parse error");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 解析快速注册返回数据
	 * 
	 */
	private static Map<String, String> parseJsonForFastRegister(String strResult) {

		try {
			if (resultMap == null) {
				resultMap = new HashMap<String, String>();
			}
			JSONObject jsonObj = new JSONObject(strResult);
			String valueCode = jsonObj.getString("code");
			String valueMsg = jsonObj.getString("msg");
			Mlog.i("feng", "valueCode:" + valueCode);
			if ("1100".equals(valueCode)) {
				String data = jsonObj.getString("data");
				String dataInfo = new String(
						AES.aesDecryptWithKey(UserInfos.AESkey,
								Base64.decode(data), 0), "utf-8");
				JSONObject jsonInfo = new JSONObject(dataInfo);
				resultMap.put("account", jsonInfo.getString("account"));
				resultMap.put("password", jsonInfo.getString("password"));
				Mlog.i("feng", "1100");
			}
			resultMap.put("code", valueCode);
			resultMap.put("msg", valueMsg);
			return resultMap;
		} catch (JSONException e) {
			System.out.println("Json parse error");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 解析TalkData返回数据
	 * 
	 */
	private static Map<String, String> parseJsonForTalkData(String strResult) {
		try {
			if (resultMap == null) {
				resultMap = new HashMap<String, String>();
			}
			JSONObject jsonObj = new JSONObject(strResult);
			String valueCode = jsonObj.getString("code");
			resultMap.put("code", valueCode);
			Mlog.i("feng", "11-14paserjson:" + jsonObj.toString());
			resultMap.put("id", jsonObj.getString("id"));
			return resultMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析更新返回数据
	 * 
	 */
	private static Map<String, String> parseJsonForUpdate(String strResult) {
		try {
			if (resultMap == null) {
				resultMap = new HashMap<String, String>();
			}
			JSONObject jsonObj = new JSONObject(strResult);
			resultMap.put("code", jsonObj.getString("code"));
			resultMap.put("msg", jsonObj.getString("msg"));
			if (jsonObj.getString("code").equals("1600")) {
				resultMap.put("url", jsonObj.getString("url"));
			}
			return resultMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 
	 * 验证设备是否是注册时设备
	 * 
	 */
	public static int Post2ServiceCheckDevice(Context mcon, String url,
			String account) {
		HttpURLConnection localHttpURLConnection = null;
		OutputStream localOutputStream = null;
		byte[] encode;
		try {
			localHttpURLConnection = ((HttpURLConnection) new URL(url
					+ "?deviceId=" + PhoneUtils.getIMEI(mcon) + "&account="
					+ account).openConnection());
			localHttpURLConnection.setRequestMethod("POST");
			localHttpURLConnection.setRequestProperty("Content-Type",
					"text/html");
			localHttpURLConnection.setConnectTimeout(5000);
			localHttpURLConnection.setDoOutput(true);
			localOutputStream = localHttpURLConnection.getOutputStream();
			localOutputStream.flush();
			Mlog.i("fengge", "localHttpURLConnection.getResponseCode():"
					+ localHttpURLConnection.getResponseCode());
			if (localHttpURLConnection.getResponseCode() == 200) {
				String returnData = StreamTools
						.readFromStream(localHttpURLConnection.getInputStream());
				JSONObject json = new JSONObject(returnData);
				Mlog.i("feng",
						"Post2ServiceCheckDevicecode:" + json.get("code"));
				if (json.get("code") != null && returnData != null) {
					if ("3202".equals(json.get("code"))
							|| "3203".equals(json.get("code"))) {
						return Integer.parseInt((String) json.get("code"));
					}
				}
				if (localOutputStream != null) {
					localOutputStream.close();
				}
			}
			if (localHttpURLConnection.getResponseCode() == 408) {
				Toast.makeText(mcon, "请求超时，请稍后再试", 1).show();
			}
		}  catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		}  catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (localHttpURLConnection != null) {
				localHttpURLConnection.disconnect();
			}
		}
		return -1;
	}

	/**
	 * 修改密码
	 * 
	 * 
	 */
	public static boolean Post2ServiceReSetPwd(Context mcon, String url,
			String account, String password) {
		HttpURLConnection localHttpURLConnection = null;
		OutputStream localOutputStream = null;
		byte[] encode;
		try {
			localHttpURLConnection = ((HttpURLConnection) new URL(url
					+ "?userId=" +UserInfos.userId)
					.openConnection());
			localHttpURLConnection.setRequestMethod("POST");
			localHttpURLConnection.setRequestProperty("Content-Type",
					"text/html");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("account", account);
			jsonObject.put("password", password);
			jsonObject.put("deviceId", PhoneUtils.getIMEI(mcon));
			encode = Base64
					.encode(AES.aesEncryptWithKey(
							UserInfos.AESkey, jsonObject
									.toString().getBytes(), 0));
			localHttpURLConnection.setConnectTimeout(5000);
			localHttpURLConnection.setDoOutput(true);
			localOutputStream = localHttpURLConnection.getOutputStream();
			localOutputStream.write(encode);
			localOutputStream.flush();
			Mlog.i("fengge", "localHttpURLConnection.getResponseCode():"
					+ localHttpURLConnection.getResponseCode());
			if (localHttpURLConnection.getResponseCode() == 200) {
				String returnData = StreamTools
						.readFromStream(localHttpURLConnection.getInputStream());
				JSONObject json = new JSONObject(returnData);
				Mlog.i("fengge", "json.get(code):" + json.get("code"));
				if (json.get("code") != null && returnData != null) {
					try {
						if ("3200".equals(json.get("code"))) {
							return true;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if (localOutputStream != null) {
					localOutputStream.close();
				}
			}
			if (localHttpURLConnection.getResponseCode() == 408) {
				Toast.makeText(mcon, "请求超时，请稍后再试", 1).show();
			}
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (localHttpURLConnection != null) {
				localHttpURLConnection.disconnect();
			}
		}
		return false;
	}
}
