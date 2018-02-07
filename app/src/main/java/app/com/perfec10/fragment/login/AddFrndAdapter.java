package app.com.perfec10.fragment.login;

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
import app.com.perfec10.fragment.profile.FriendProfile;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.helper.RoundedImageView;
import app.com.perfec10.model.AddFrndGS;
import app.com.perfec10.model.FbFrndsGS;
import app.com.perfec10.network.NetworkConstants;

/**
 * Created by fluper on 5/1/18.
 */

public class AddFrndAdapter extends RecyclerView.Adapter<AddFrndAdapter.Holder>{
    private MainActivity mainActivity;
    private ArrayList<AddFrndGS> searchedList;
    private ImageLoader imageLoader;
    private final String type;
    private ArrayList<FbFrndsGS> fbFriends;
    private String TAG = "AddFrndAdapter";

    public AddFrndAdapter(MainActivity mainActivity, ArrayList<AddFrndGS> searchedList,
                            String type){
        this.mainActivity = mainActivity;
        this.searchedList = searchedList;
        this.type = "email";
        Log.d(TAG, "inside adaper email "+type);
    }

    public AddFrndAdapter(MainActivity mainActivity, ArrayList<FbFrndsGS> fbFriends){
        this.mainActivity = mainActivity;
        this.fbFriends = fbFriends;
        this.type = "fb";
        Log.d(TAG, "inside adaper email "+type);
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.addfrnd_adapter, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        Log.d(TAG, "inside setting type "+this.type);
        if (this.type.equals("email")){

            imageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();
            Log.d(TAG, "setting data "+searchedList.get(position).getName());
            holder.tv_name_addfrndrow.setText(searchedList.get(position).getName());
            holder.tv_email_addfrndrow.setText(searchedList.get(position).getEmail());
            imageLoader.get(NetworkConstants.imageBaseUrl +
                    searchedList.get(position).getImage(), ImageLoader.getImageListener
                    (holder.riv_addfrnd_row, R.mipmap.user, R.mipmap.user));
            holder.riv_addfrnd_row.setImageUrl(NetworkConstants.imageBaseUrl +
                    searchedList.get(position).getImage(), imageLoader);

            holder.riv_addfrnd_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Profile.changeProfileFragment(new FriendProfile(mainActivity, searchedList.get(position).getUserId()), "friend_pro");
                }
            });
            if (searchedList.get(position).getSelected().equals("1")){
                holder.iv_select_addfrndrow.setImageResource(R.mipmap.selected);
            }else {
                holder.iv_select_addfrndrow.setImageResource(R.mipmap.unselect);
            }

            holder.iv_select_addfrndrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String status = searchedList.get(position).getSelected();
                    if (status.equals("0")){
                        holder.iv_select_addfrndrow.setImageResource(R.mipmap.selected);
                        searchedList.get(position).setSelected("1");
                    }else {
                        holder.iv_select_addfrndrow.setImageResource(R.mipmap.unselect);
                        searchedList.get(position).setSelected("0");
                    }
                    notifyDataSetChanged();
                }
            });
        }else {
            imageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();
            holder.tv_name_addfrndrow.setText(fbFriends.get(position).getName());
            holder.tv_email_addfrndrow.setVisibility(View.GONE);
            Log.d(TAG, "image fb frnd "+fbFriends.get(position).getImage());
            String url = fbFriends.get(position).getImage();
            if (url.contains("https")){
                if (!fbFriends.get(position).getImage().equals("null")){
                    imageLoader.get(fbFriends.get(position).getImage(), ImageLoader.getImageListener
                            (holder.riv_addfrnd_row, R.mipmap.user, R.mipmap.user));
                    holder.riv_addfrnd_row.setImageUrl(fbFriends.get(position).getImage(), imageLoader);
                }
            }else {
                if (!fbFriends.get(position).getImage().equals("null")){
                    imageLoader.get(NetworkConstants.imageBaseUrl+fbFriends.get(position).getImage(), ImageLoader.getImageListener
                            (holder.riv_addfrnd_row, R.mipmap.user, R.mipmap.user));
                    holder.riv_addfrnd_row.setImageUrl(NetworkConstants.imageBaseUrl+fbFriends.get(position).getImage(), imageLoader);
                }
            }


            if (fbFriends.get(position).getStatus().equals("1")){
                holder.iv_select_addfrndrow.setImageResource(R.mipmap.selected);
            }else {
                holder.iv_select_addfrndrow.setImageResource(R.mipmap.unselect);
            }
            holder.iv_select_addfrndrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String status = fbFriends.get(position).getStatus();
                    if (status.equals("0")){
                        holder.iv_select_addfrndrow.setImageResource(R.mipmap.selected);
                        fbFriends.get(position).setStatus("1");
                    }else {
                        holder.iv_select_addfrndrow.setImageResource(R.mipmap.unselect);
                        fbFriends.get(position).setStatus("0");
                    }
                    notifyDataSetChanged();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (this.type.equals("email")){
            return searchedList.size();
        }else {
            return fbFriends.size();
        }

    }

    public class Holder extends RecyclerView.ViewHolder{
    private RoundedImageView riv_addfrnd_row;
    private TextView tv_name_addfrndrow, tv_email_addfrndrow;
    private ImageView iv_select_addfrndrow;
        public Holder(View itemView) {
            super(itemView);
            riv_addfrnd_row = (RoundedImageView) itemView.findViewById(R.id.riv_addfrnd_row);
            tv_name_addfrndrow = (TextView) itemView.findViewById(R.id.tv_name_addfrndrow);
            tv_email_addfrndrow = (TextView) itemView.findViewById(R.id.tv_email_addfrndrow);
            iv_select_addfrndrow = (ImageView) itemView.findViewById(R.id.iv_select_addfrndrow);
        }
    }
}
