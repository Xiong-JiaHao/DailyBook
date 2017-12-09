package com.xjh.gin.dailybook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gin on 2017/12/5.
 */

public class CostListAdapter extends BaseAdapter {
    private List<CostBean> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public CostListAdapter(Context context, List<CostBean> list) {
        mList = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.list_item, null);
            viewHolder.mTvCostTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.mTvCostDate = convertView.findViewById(R.id.tv_date);
            viewHolder.mTvCostMoney = convertView.findViewById(R.id.tv_cost);
            viewHolder.id = convertView.findViewById(R.id.cost_id);
            viewHolder.delete = convertView.findViewById(R.id.delete_button);
            viewHolder.update = convertView.findViewById(R.id.update_button);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Log.e("TAGS", String.valueOf(viewHolder.id.getText()));
                    Intent intent = new Intent("MY_Delete");
                    intent.putExtra("id", Integer.valueOf(String.valueOf(viewHolder.id.getText())));
                    mContext.sendBroadcast(intent);
                }
            });
            viewHolder.update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("MY_Update");
                    intent.putExtra("id", Integer.valueOf(String.valueOf(viewHolder.id.getText())));
                    mContext.sendBroadcast(intent);
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CostBean bean = mList.get(position);
        viewHolder.mTvCostTitle.setText(bean.costTitle);
        viewHolder.mTvCostDate.setText(bean.costDate);
        viewHolder.mTvCostMoney.setText((bean.costMoney > 0 ? "+" : "") + bean.costMoney );
        viewHolder.id.setText("" + bean.id);
        return convertView;
    }

    private static class ViewHolder {
        public TextView id;
        public TextView mTvCostTitle;
        public TextView mTvCostDate;
        public TextView mTvCostMoney;
        View delete,update;
    }
}
