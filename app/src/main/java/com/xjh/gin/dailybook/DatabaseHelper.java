package com.xjh.gin.dailybook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gin on 2017/12/5.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String COST_TITLE = "cost_title";
    public static final String COST_DATE = "cost_date";
    public static final String COST_MONEY = "cost_money";
    public static final String TABLE = "Class_daily";

    public DatabaseHelper(Context context) {
        super(context, TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists Class_daily(" +
                "id integer primary key," +
                "cost_title varchar," +
                "cost_date varchar," +
                "cost_money integer);");
    }

    public void insertCost(CostBean costBean) {
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", costBean.id);
        contentValues.put(COST_TITLE, costBean.costTitle);
        contentValues.put(COST_DATE, costBean.costDate);
        contentValues.put(COST_MONEY, costBean.costMoney);
        database.insert(TABLE, null, contentValues);
    }

    public Cursor getAllCostData() {
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        return database.query(TABLE, null, null, null, null, null, "cost_date " + "ASC");//null默认查询全部,ASC顺序排练
    }

    public void deleteAllData() {//一键清空
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        database.delete(TABLE, null, null);//条件语句
    }

    public void deleteData(int id) {//删除
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        database.delete(TABLE, "id like ?", new String[]{"" + id});//条件语句
    }

    public int getAllMoney() {
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        Cursor cursor = database.query(TABLE, new String[]{"SUM(cost_money)"}, null, null, null, null, null);
        int money = 0;
        if(cursor!=null){
            cursor.moveToNext();
            money = cursor.getInt(cursor.getColumnIndex("SUM(cost_money)"));
        }
        return money;
    }

    public int getIncome(){
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        Cursor cursor = database.query(TABLE, new String[]{"SUM(cost_money)"}, "cost_money>?", new String[]{"0"}, null, null, null);
        int money = 0;
        if(cursor!=null){
            cursor.moveToNext();
            money = cursor.getInt(cursor.getColumnIndex("SUM(cost_money)"));
        }
        return money;
    }

    public int getExpenditure(){
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        Cursor cursor = database.query(TABLE, new String[]{"SUM(cost_money)"}, "cost_money<?", new String[]{"0"}, null, null, null);
        int money = 0;
        if(cursor!=null){
            cursor.moveToNext();
            money = cursor.getInt(cursor.getColumnIndex("SUM(cost_money)"));
        }
        return money;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
