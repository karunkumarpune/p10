package app.com.perfec10.fragment.measure.adapater;

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
import app.com.perfec10.fragment.measure.ShareContactList;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.helper.RoundedImageView;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.NetworkConstants;

/**
 * Created by fluper on 10/12/17.
 */

public class ShareContactsAdapter extends RecyclerView.Adapter<ShareContactsAdapter.Holder>{
    private MainActivity mainActivity;
    private ArrayList<FriendListGS> contactList;
    private ImageLoader mImageLoader;
    private Typeface regular;
    private String TAG = "ShareContactsAdapter";

    public ShareContactsAdapter(MainActivity mainActivity, ArrayList<FriendListGS> contactList){
        this.mainActivity = mainActivity;
        this.contactList = contactList;
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
      //  Log.e("recalled ", " "+contactList.get(0).getStatus());
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.sharecontact_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        Log.e(TAG+"position ", position+" ");
        holder.tv_name_sharecon_row.setTypeface(regular);
        holder.tv_name_sharecon_row.setText(contactList.get(position).getName());
        if (contactList.get(position).getStatus()){
            Log.e(TAG+" selected ", "jkh");
            holder.iv_check_sharecon_row.setImageResource(R.mipmap.selected);
        }else {
            Log.e(TAG+" not ", "selected");
            holder.iv_check_sharecon_row.setImageResource(R.mipmap.unselect);
        }
        String url = contactList.get(position).getImage();
        url = url.replace("\"","");
        Log.d(TAG+" image profile ", NetworkConstants.imageBaseUrl+url);
        mImageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();
        mImageLoader.get(NetworkConstants.imageBaseUrl+url, ImageLoader.getImageListener(holder.riv_sharecon_row,
                R.mipmap.user, R.mipmap.user));
        holder.riv_sharecon_row.setImageUrl(NetworkConstants.imageBaseUrl+url, mImageLoader);
        Log.d(TAG+" position "+position, "size "+contactList.size());
        if (position==contactList.size()-1){
            holder.view_sharecon_row.setVisibility(View.GONE);
        }

        holder.iv_check_sharecon_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!contactList.get(position).getStatus()){
                    holder.iv_check_sharecon_row.setImageResource(R.mipmap.selected);
                    contactList.get(position).setStatus(true);
                }else {
                    holder.iv_check_sharecon_row.setImageResource(R.mipmap.unselect);
                    contactList.get(position).setStatus(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        private RoundedImageView riv_sharecon_row;
        private TextView tv_name_sharecon_row;
        private ImageView iv_check_sharecon_row;
        private View view_sharecon_row;
        public Holder(View itemView) {
            super(itemView);
            iv_check_sharecon_row = (ImageView) itemView.findViewById(R.id.iv_check_sharecon_row);
            tv_name_sharecon_row = (TextView) itemView.findViewById(R.id.tv_name_sharecon_row);
            riv_sharecon_row = (RoundedImageView) itemView.findViewById(R.id.riv_sharecon_row);
            view_sharecon_row = (View) itemView.findViewById(R.id.view_sharecon_row);
        }
    }
}
