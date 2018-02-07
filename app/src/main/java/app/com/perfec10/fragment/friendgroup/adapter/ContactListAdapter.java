package app.com.perfec10.fragment.friendgroup.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.ContactList;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.helper.RoundedImageView;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.NetworkConstants;

/**
 * Created by fluper on 7/12/17.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.Holder>{
    private MainActivity mainActivity;
    private ArrayList<FriendListGS> contactList;
    private ImageLoader mImageLoader;
    private Typeface regular;
    private String TAG = "ContactListAdapter";

    public ContactListAdapter(MainActivity mainActivity, ArrayList<FriendListGS> contactList){
        this.mainActivity = mainActivity;
        this.contactList = contactList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mainActivity).inflate(R.layout.contactlist_row, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        String name = contactList.get(position).getName();
        name = name.replace("\"", "");
        holder.tv_name_cont_row.setText(name);
        String url = contactList.get(position).getImage();
        url = url.replace("\"","");
        Log.d(TAG+" image profile ", NetworkConstants.imageBaseUrl+url);
        mImageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();
        mImageLoader.get(NetworkConstants.imageBaseUrl+url, ImageLoader.getImageListener(holder.riv_cont_row,
                R.mipmap.user, R.mipmap.user));
        holder.riv_cont_row.setImageUrl(NetworkConstants.imageBaseUrl+url, mImageLoader);
        holder.iv_select_cont_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactList.get(position).getStatus()){
                    holder.iv_select_cont_row.setImageResource(R.mipmap.unselect);
                    contactList.get(position).setStatus(false);
                }else {
                    holder.iv_select_cont_row.setImageResource(R.mipmap.selected);
                    contactList.get(position).setStatus(true);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
    private RoundedImageView riv_cont_row;
    private TextView tv_name_cont_row;
    private View view_cont_row;
    private ImageView iv_select_cont_row;
        public Holder(View itemView) {
            super(itemView);
            view_cont_row = (View) itemView.findViewById(R.id.view_cont_row);
            tv_name_cont_row = (TextView) itemView.findViewById(R.id.tv_name_cont_row);
            riv_cont_row = (RoundedImageView) itemView.findViewById(R.id.riv_cont_row);
            iv_select_cont_row = (ImageView) itemView.findViewById(R.id.iv_select_cont_row);
        }
    }
}
