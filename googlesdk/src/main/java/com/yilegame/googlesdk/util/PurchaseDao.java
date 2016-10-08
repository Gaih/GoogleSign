package com.yilegame.googlesdk.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yilegame.sdk.utils.LogUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/11.
 */
public class PurchaseDao {
    private Context context;
    private static final String TABLE_PURCGASE = "purchase_info";//专辑库
    private static PurchaseDao purchaseDao;

    public static PurchaseDao getInstatncePurchase(Context context){
        if(purchaseDao==null){
            purchaseDao=new PurchaseDao(context);
        }
        return purchaseDao;
    }
    private PurchaseDao(Context context){
        this.context=context;
    }

    public List<Purchase> getPurchase(){
        DatabaseHelper  helper=com.yilegame.googlesdk.util.DatabaseHelper.getHelper(context);
        SQLiteDatabase Database = helper.getReadableDatabase();
        String sql="select* from "+TABLE_PURCGASE;
        List<Purchase> list=parseCursor(Database,Database.rawQuery(sql, null));
        LogUtil.i("gaolingshi", "查询数据库");
        return  list;
    }

    /**
     *      +  "orderId varchar(max),"
     +  "signedData varchar(max),"
     +  "signature varchar(max)");
     */
    public List<Purchase> parseCursor(SQLiteDatabase Database, Cursor cursor){
        List<Purchase> list=new ArrayList<Purchase>();
        while(cursor.moveToNext()){
            try{
                String purchaseData=cursor.getString(cursor.getColumnIndex("purchaseData"));
                String signature=cursor.getString(cursor.getColumnIndex("signature"));
                list.add(new Purchase(IabHelper.ITEM_TYPE_INAPP, purchaseData, signature));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        cursor.close();
        Database.close();
        return list;
    }

    public void delectPurchase(Purchase  purchase){
        DatabaseHelper  helper=com.yilegame.googlesdk.util.DatabaseHelper.getHelper(context);
        SQLiteDatabase db=helper.getWritableDatabase();
        String orderId=purchase.getOrderId();
        db.delete(TABLE_PURCGASE, "orderId=?", new String[]{orderId});
        LogUtil.i("gaolingshi", "删除数据库");
        db.close();
    }

    /**
     *
     * @param puchasedata
     * @param signature
     */
    public long savePurchase(String puchasedata, String signature){
        DatabaseHelper  helper=com.yilegame.googlesdk.util.DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        long result=0;
        try{
            Purchase purchase=new Purchase(IabHelper.ITEM_TYPE_INAPP,puchasedata,signature);
            cv.put("orderId",purchase.getOrderId());
            cv.put("puchasedata",puchasedata);
            cv.put("signature",signature);
            result=db.insert(TABLE_PURCGASE, null, cv);
            db.close();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param purchase
     * @return
     *            + TABLE_PURCGASE
    + "(_id integer primary key autoincrement,"
    +  "orderId text,"
    +  "purchaseData text,"
    +  "signature text)");
     */
    public long savePurchase(Purchase purchase){
        DatabaseHelper  helper=com.yilegame.googlesdk.util.DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        //db.execSQL("insert into person(name, age) values(?,?)", new Object[]{"传智播客", 4});
        db.execSQL("insert into "+PurchaseDao.TABLE_PURCGASE+"(orderId,purchaseData,signature) values(?,?,?)",new Object[]{purchase.getOrderId(),purchase.getOriginalJson(),purchase.getSignature()});
        LogUtil.i("gaolingshi", "保存数据库");
        db.close();
        return 0;
    }
}
