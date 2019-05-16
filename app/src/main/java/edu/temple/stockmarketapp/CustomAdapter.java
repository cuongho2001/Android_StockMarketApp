package edu.temple.stockmarketapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Admin on 4/28/2018.
 */

class CustomAdapter extends BaseAdapter {
    Context context;
    List<String> sList;

    public CustomAdapter(Context con, List<String> list) {
        context = con;
        sList = list;
    }

    @Override
    public int getCount() {
        return sList.size();
    }

    @Override
    public Object getItem(int i) {
        return sList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView result = new TextView(context);
        result.setText(sList.get(i));
        return result;
    }
}
