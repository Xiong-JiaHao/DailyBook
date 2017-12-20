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

    public static final String TABLE = "Class_daily";
    public static final String COST_ID = "id";
    public static final String COST_TITLE = "cost_title";
    public static final String COST_DATE = "cost_date";
    public static final String COST_MONEY = "cost_money";

    public DatabaseHelper(Context context) {
        super(context, TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE+"(" +
                COST_ID +" integer primary key," +
                COST_TITLE +" varchar," +
                COST_DATE +" varchar," +
                COST_MONEY+" integer);");
    }

    public int getDataCount(){
        int count = 0;
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        if(database!=null){
            String sql="select * from "+TABLE;
            Cursor cursor = database.rawQuery(sql,null);//sql和占位符
            count=cursor.getCount();
        }
        return count;
    }

    public void insertCost(CostBean costBean) {
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        //API
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COST_ID, costBean.id);
//        contentValues.put(COST_TITLE, costBean.costTitle);
//        contentValues.put(COST_DATE, costBean.costDate);
//        contentValues.put(COST_MONEY, costBean.costMoney);
//        database.insert(TABLE, null, contentValues);
        //SQL语句：
        String sql="insert into "+TABLE+" values("+costBean.id+",'"+costBean.costTitle+"','"+costBean.costDate+"',"+costBean.costMoney+")";
        database.execSQL(sql);
    }

    public Cursor getAllCostDate() {
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        //API
//        Cursor cursor = database.query(TABLE, null, null, null, null, null, "cost_date " + "ASC");//null默认查询全部,ASC顺序排练
        //SQL语句
        String sql="select * from "+TABLE;
        Cursor cursor = database.rawQuery(sql,null);//sql和占位符
        return cursor;
    }

    public Cursor getCostDateByLimit(int begin,int len){
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        begin=(begin-1)*len;
        //API
//        Cursor cursor = database.query(TABLE, null, null, null, null, null, "cost_date " + "ASC",""+begin+","+len);//null默认查询全部,ASC顺序排练
        //SQL语句
        String sql="select * from "+TABLE+" limit ?,?";
        Cursor cursor = database.rawQuery(sql,new String[]{begin+"",len+""});//sql和占位符
        return cursor;
    }

    public void deleteAllData() {//一键清空
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        //API
//        database.delete(TABLE, null, null);//条件语句
        //SQL语句
        String sql="delete from "+TABLE;
        database.execSQL(sql);
    }

    public void deleteData(int id) {//删除
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        //API
//        database.delete(TABLE, "id like ?", new String[]{"" + id});//条件语句
        //SQL语句
        String sql="delete from "+TABLE+" where "+COST_ID+"="+id;
        database.execSQL(sql);
    }

    public Cursor getCostDate(int id){
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        //API
//        Cursor cursor = database.query(TABLE, null, "id like ?", new String[]{"" + id}, null, null, null);
        //SQL语句
        String sql="select * from "+TABLE + " where "+COST_ID+"="+id;
        Cursor cursor = database.rawQuery(sql,null);//sql和占位符
        return cursor;
    }

    public void upDate(CostBean costBean){//更新数据
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        //API
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COST_TITLE, costBean.costTitle);
//        contentValues.put(COST_DATE, costBean.costDate);
//        contentValues.put(COST_MONEY, costBean.costMoney);
//        database.update(TABLE,contentValues,"id like ?", new String[]{"" + costBean.id});
        //SQL语句
        String sql="update "+TABLE+" set "+COST_TITLE+"='"+costBean.costTitle+"',"+COST_DATE+"='"+costBean.costDate+"',"+COST_MONEY+"="+costBean.costMoney+" where "+COST_ID+"="+costBean.id;
        database.execSQL(sql);
    }

    public int getAllMoney() {
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        //API
//        Cursor cursor = database.query(TABLE, new String[]{"SUM(cost_money)"}, null, null, null, null, null);
        //SQL语句
        String sql="select SUM("+COST_MONEY+") from "+TABLE;
        Cursor cursor = database.rawQuery(sql,null);//sql和占位符
        int money = 0;
        if(cursor!=null){
            cursor.moveToNext();
            money = cursor.getInt(cursor.getColumnIndex("SUM("+COST_MONEY+")"));
        }
        return money;
    }

    public int getIncome(){
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        //API
//        Cursor cursor = database.query(TABLE, new String[]{"SUM(cost_money)"}, "cost_money>?", new String[]{"0"}, null, null, null);
        //SQL语句
        String sql="select SUM("+COST_MONEY+") from "+TABLE + " where "+COST_MONEY+">0";
        Cursor cursor = database.rawQuery(sql,null);//sql和占位符
        int money = 0;
        if(cursor!=null){
            cursor.moveToNext();
            money = cursor.getInt(cursor.getColumnIndex("SUM("+COST_MONEY+")"));
        }
        return money;
    }

    public int getExpenditure(){
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        //API
//        Cursor cursor = database.query(TABLE, new String[]{"SUM(cost_money)"}, "cost_money<?", new String[]{"0"}, null, null, null);
        //SQL语句
        String sql="select SUM("+COST_MONEY+") from "+TABLE + " where "+COST_MONEY+"<0";
        Cursor cursor = database.rawQuery(sql,null);//sql和占位符
        int money = 0;
        if(cursor!=null){
            cursor.moveToNext();
            money = cursor.getInt(cursor.getColumnIndex("SUM("+COST_MONEY+")"));
        }
        return money;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public int getMaxId() {
        SQLiteDatabase database = getWritableDatabase();//获得数据库对象
        //API
//        Cursor cursor = database.query(TABLE, new String[]{"MAX("+COST_ID+")"}, null, null, null, null, null);
        //SQL语句
        String sql="select MAX("+COST_ID+") from "+TABLE;
        Cursor cursor = database.rawQuery(sql,null);//sql和占位符
        int maxId = 0;
        if(cursor!=null){
            cursor.moveToNext();
            maxId = cursor.getInt(cursor.getColumnIndex("MAX("+COST_ID+")"));
        }
        return maxId;
    }
}

//SQLiteDatabase database = getWritableDatabase();//获得数据库对象
//database.beginTransaction();
//try{
//    //do
//    for(){
//
//    }
//    database.setTransactionSuccessful();
//}
//catch (Exception e){
//
//}
//finally {
//    database.endTransaction();
//}
