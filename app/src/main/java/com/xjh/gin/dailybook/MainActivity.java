package com.xjh.gin.dailybook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView costList;
    private List<CostBean> mCostBeanList;
    private DatabaseHelper mDatabaseHelper;
    private CostListAdapter mAdapter;
    private MyBroadCastReceiver myBroadCastReceiver;
    private int num;

    private int CostbeanNum;
    private int pageSize = 8;//每页的长度
    private int pageNum;//页码
    private int currentPage=1;//当前页码
    private boolean isDivPage;//是否分页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //注册广播
        IntentFilter filter = new IntentFilter();
        myBroadCastReceiver = new MyBroadCastReceiver();
        filter.addAction("MY_Delete");
        filter.addAction("MY_Update");
        registerReceiver(myBroadCastReceiver, filter);

        initView();
        initEvent();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e("TAGS","1");
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View viewDialog = inflater.inflate(R.layout.new_cost_data, null);
                final EditText title = viewDialog.findViewById(R.id.et_cost_title);
                final EditText money = viewDialog.findViewById(R.id.et_cost_money);
                final DatePicker date = viewDialog.findViewById(R.id.dp_cost_date);
                final RadioGroup redio=viewDialog.findViewById(R.id.redio);
                builder.setTitle("New Daily");
                builder.setView(viewDialog);
                //Log.e("TAGS","2");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CostBean costBean = new CostBean();
                        costBean.costTitle = title.getText().toString();
                        costBean.costDate = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDayOfMonth();
                        String str = money.getText().toString();
                        for (int i = 0; i < str.length(); i++) {
                            char c = str.charAt(i);
                            costBean.costMoney = costBean.costMoney * 10 + (c - '0');
                        }
                        if(redio.getCheckedRadioButtonId()==R.id.expense){
                            costBean.costMoney = -costBean.costMoney;
                        }
                        costBean.id = num++;
                        mDatabaseHelper.insertCost(costBean);
                        mCostBeanList.add(costBean);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadCastReceiver);
    }

    private void initView() {
        num = 0;
        costList = findViewById(R.id.lv_view);
        mCostBeanList = new ArrayList<>();
        mDatabaseHelper = new DatabaseHelper(this);
        initCostData();
        mAdapter = new CostListAdapter(this, mCostBeanList);
        costList.setAdapter(mAdapter);
    }

    private void initEvent() {
        costList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(isDivPage&&AbsListView.OnScrollListener.SCROLL_STATE_IDLE==scrollState){
                    if(currentPage<pageNum){
                        currentPage++;
                        Cursor cursor = mDatabaseHelper.getCostDateByLimit(currentPage,pageSize);
                        if (cursor != null) {
                            while (cursor.moveToNext()) {
                                CostBean costBean = new CostBean();
                                costBean.costTitle = cursor.getString(cursor.getColumnIndex("cost_title"));
                                costBean.costDate = cursor.getString(cursor.getColumnIndex("cost_date"));
                                costBean.costMoney = cursor.getInt(cursor.getColumnIndex("cost_money"));
                                costBean.id = cursor.getInt(cursor.getColumnIndex("id"));
                                mCostBeanList.add(costBean);
                            }
                        }
                        cursor.close();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isDivPage = ((firstVisibleItem+visibleItemCount)==totalItemCount);
            }
        });
    }

    private void initCostData() {
        //全部获取
//        Cursor cursor = mDatabaseHelper.getAllCostDate();
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                CostBean costBean = new CostBean();
//                costBean.costTitle = cursor.getString(cursor.getColumnIndex("cost_title"));
//                costBean.costDate = cursor.getString(cursor.getColumnIndex("cost_date"));
//                costBean.costMoney = cursor.getInt(cursor.getColumnIndex("cost_money"));
//                costBean.id = cursor.getInt(cursor.getColumnIndex("id"));
//                mCostBeanList.add(costBean);
//            }
//        }
//        cursor.close();
        //分页获取
        CostbeanNum = mDatabaseHelper.getDataCount();
        pageNum = (int) Math.ceil(CostbeanNum/(double)pageSize);
        Cursor cursor = mDatabaseHelper.getCostDateByLimit(1,pageSize);
        if(currentPage==1){
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    CostBean costBean = new CostBean();
                    costBean.costTitle = cursor.getString(cursor.getColumnIndex("cost_title"));
                    costBean.costDate = cursor.getString(cursor.getColumnIndex("cost_date"));
                    costBean.costMoney = cursor.getInt(cursor.getColumnIndex("cost_money"));
                    costBean.id = cursor.getInt(cursor.getColumnIndex("id"));
                    mCostBeanList.add(costBean);
                }
            }
            cursor.close();
        }
        num=mDatabaseHelper.getMaxId()+1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_AllMoney) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View viewDialog = inflater.inflate(R.layout.all_money, null);
            final TextView mAllmoney = viewDialog.findViewById(R.id.id_Allmoney);
            final TextView mIncome = viewDialog.findViewById(R.id.id_Income);
            final TextView mExpenditure = viewDialog.findViewById(R.id.id_Expenditure);
            mAllmoney.setText(mDatabaseHelper.getAllMoney()+"元");
            mIncome.setText(mDatabaseHelper.getIncome()+"元");
            mExpenditure.setText(mDatabaseHelper.getExpenditure()+"元");
            builder.setView(viewDialog);
            builder.setPositiveButton("OK",null);
            builder.create();
            builder.show();
            return true;
        } else if (id == R.id.action_delete) {
            mDatabaseHelper.deleteAllData();
            mCostBeanList.clear();
            mAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            switch (intent.getAction()) {
                case "MY_Delete":
                    int deleteid = intent.getIntExtra("id", 0);
                    //Log.e("TAGS", "" + id);
                    mDatabaseHelper.deleteData(deleteid);
                    for (int i = 0; i < mCostBeanList.size(); i++) {
                        if (mCostBeanList.get(i).id == deleteid) {
                            mCostBeanList.remove(i);//删除list
                            break;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                case "MY_Update":
                    final int updateid = intent.getIntExtra("id", 0);
                    Cursor cursor = mDatabaseHelper.getCostDate(updateid);
                    cursor.moveToNext();
                    CostBean costBean = new CostBean();
                    costBean.costTitle = cursor.getString(cursor.getColumnIndex("cost_title"));
                    costBean.costDate = cursor.getString(cursor.getColumnIndex("cost_date"));
                    costBean.costMoney = cursor.getInt(cursor.getColumnIndex("cost_money"));
                    costBean.id = cursor.getInt(cursor.getColumnIndex("id"));
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    View viewDialog = inflater.inflate(R.layout.cost_data, null);
                    final EditText title = viewDialog.findViewById(R.id.et_cost_title);
                    final EditText money = viewDialog.findViewById(R.id.et_cost_money);
                    final DatePicker date = viewDialog.findViewById(R.id.dp_cost_date);
                    final TextView id = viewDialog.findViewById(R.id.tv_id);
                    final RadioGroup redio = viewDialog.findViewById(R.id.redio);
                    id.setText(costBean.id+"");
                    title.setText(costBean.costTitle);
                    money.setText((costBean.costMoney > 0 ? "+" : "") + costBean.costMoney );
                    //Date怎么写内定

                    builder.setTitle("UpDate");
                    builder.setView(viewDialog);
                    //Log.e("TAGS","2");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CostBean costBean = new CostBean();
                            costBean.id=Integer.parseInt(id.getText().toString());
                            costBean.costTitle = title.getText().toString();
                            costBean.costDate = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDayOfMonth();
                            String str = money.getText().toString();
                            for (int i = 0; i < str.length(); i++) {
                                char c = str.charAt(i);
                                costBean.costMoney = costBean.costMoney * 10 + (c - '0');
                            }
                            if(redio.getCheckedRadioButtonId()==R.id.expense){
                                costBean.costMoney = -costBean.costMoney;
                            }
                            for (int i = 0; i < mCostBeanList.size(); i++) {
                                if (mCostBeanList.get(i).id == updateid) {
                                    mDatabaseHelper.upDate(costBean);
                                    mCostBeanList.remove(i);//更新list
                                    mCostBeanList.add(i,costBean);
                                    break;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.create();
                    builder.show();
                    break;
                case "MY_Fail":
                    Toast.makeText(MainActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        }
    }
}