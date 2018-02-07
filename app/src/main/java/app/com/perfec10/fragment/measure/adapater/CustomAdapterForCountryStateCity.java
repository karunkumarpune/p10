package app.com.perfec10.fragment.measure.adapater;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.model.FriendListGS;


/**
 * Created by elite on 4/1/17.
 */

public class CustomAdapterForCountryStateCity extends BaseAdapter {

    ArrayList<FriendListGS> categoryName;
    MainActivity mainActivity;


    public CustomAdapterForCountryStateCity(MainActivity mainActivity, ArrayList<FriendListGS> categoryName) {
        this.mainActivity = mainActivity;
        this.categoryName = categoryName;
        Log.d("inside adapter spinnser", "ds");
    }

    @Override
    public int getCount() {

        return categoryName.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder myViewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_row_for_country, parent, false);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }

        Log.d("inside adapter spinnser", categoryName.get(position).getName());
      //  myViewHolder.tv_spinner_row_item.setText(categoryName.get(position).getName());
        /*try {
            if (position == 0) {


            } else {

                myViewHolder.tv_spinner_row_item.setText(categoryName.get(position).getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return convertView;
    }

    private class MyViewHolder {
        TextView tv_spinner_row_item;

        public MyViewHolder(View itemView) {
            tv_spinner_row_item = (TextView) itemView.findViewById(R.id.tv_spinner_row_item);
        }
    }
}

