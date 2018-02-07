package app.com.perfec10.fragment.friendgroup.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.profile.FriendProfile;
import app.com.perfec10.fragment.friendgroup.GroupDetail;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.helper.RoundedImageView;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.NetworkConstants;

/**
 * Created by fluper on 20/11/17.
 */

public class GroupFriendAdapter extends RecyclerView.Adapter<GroupFriendAdapter.Holder>{
    private MainActivity mainActivity;
    private ArrayList<FriendListGS> friendList;
    private ImageLoader mImageLoader;
    private String fors;
    private Typeface regular;
    private String TAG = "GroupFriendAdapter";
    public GroupFriendAdapter(MainActivity mainActivity, ArrayList<FriendListGS> friendList, String fors){
        this.mainActivity = mainActivity;
        this.friendList = friendList;
        this.fors = fors;
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.group_frnd_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        holder.tv_name_grpfrnd_row.setTypeface(regular);
        String name = friendList.get(position).getName();
        name = name.replace("\"","");
        holder.tv_name_grpfrnd_row.setText(name);
        String url = friendList.get(position).getImage();
        url = url.replace("\"","");
        Log.d(TAG+" image profile ", NetworkConstants.imageBaseUrl+url);
        mImageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();

        if (fors.equals("group")){
            mImageLoader.get(NetworkConstants.imageBaseUrl+url, ImageLoader.getImageListener(holder.riv_grp_row,
                    R.mipmap.user, R.mipmap.user));
            holder.riv_grp_row.setImageUrl(NetworkConstants.imageBaseUrl+url, mImageLoader);
        }else if (fors.equals("friend")){
            mImageLoader.get(NetworkConstants.imageBaseUrl+url, ImageLoader.getImageListener(holder.riv_grp_row,
                    R.mipmap.user, R.mipmap.user));
            holder.riv_grp_row.setImageUrl(NetworkConstants.imageBaseUrl+url, mImageLoader);
        }
        if (position+1==friendList.size()){
            holder.view_grpfrnd_row.setVisibility(View.GONE);
        }

        holder.ll_click_grpfrnd_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fors.equals("group")){
                    Profile.changeProfileFragment(new GroupDetail(mainActivity, friendList.get(position).getGroupId()), "group_detail");
                }else if (fors.equals("friend")){
                    Profile.changeProfileFragment(new FriendProfile(mainActivity, friendList.get(position).getUserId()), "friend_profile");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        private RoundedImageView riv_grp_row;
        private TextView tv_name_grpfrnd_row;
        private View view_grpfrnd_row;
        private LinearLayout ll_click_grpfrnd_row;
        public Holder(View itemView) {
            super(itemView);
            tv_name_grpfrnd_row = (TextView) itemView.findViewById(R.id.tv_name_grpfrnd_row);
            riv_grp_row = (RoundedImageView) itemView.findViewById(R.id.riv_grp_row);
            view_grpfrnd_row = (View) itemView.findViewById(R.id.view_grpfrnd_row);
            ll_click_grpfrnd_row = (LinearLayout) itemView.findViewById(R.id.ll_click_grpfrnd_row);
        }
    }
}
