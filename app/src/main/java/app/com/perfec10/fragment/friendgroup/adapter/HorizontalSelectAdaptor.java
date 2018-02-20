package app.com.perfec10.fragment.friendgroup.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import java.util.regex.Pattern;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.CreateGroup;
import app.com.perfec10.fragment.friendgroup.CreateGrpSubject;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.helper.RoundedImageView;
import app.com.perfec10.network.NetworkConstants;


/**
 * Created by Dell pc on 01-12-2017.
 */

public class HorizontalSelectAdaptor extends RecyclerView.Adapter<HorizontalSelectAdaptor.CustomViewHolder> {
    private MainActivity mainActivity;
    private String from;
    private String TAG = "HorizontalSelectAdaptor";

    public HorizontalSelectAdaptor(MainActivity mainActivity, String from ) {
        this.mainActivity = mainActivity;
        this.from = from;
        Log.d(TAG+" inside horizontal  ", " adaptor");
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.horizontal_select_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        ImageLoader mImageLoader = CustomVolleyRequestQueue.getInstance(mainActivity)
                .getImageLoader();
        //final String url = "http://25.media.tumblr.com/af50758346e388e6e69f4c378c4f264f/tumblr_mzgzcdEDTL1st5lhmo1_1280.jpg";
        String url = NetworkConstants.imageBaseUrl+CreateGroupFriendsAdapter.selectedContactList.get(position).getImage();
        url = url.replace("\"","");
        mImageLoader.get(url, ImageLoader.getImageListener(holder.iv_rounded,
                R.mipmap.user, R.mipmap.user));
        holder.iv_rounded.setImageUrl(url, mImageLoader);
        String name = CreateGroupFriendsAdapter.selectedContactList.get(position).getName();
        name = name.replace("\"", "");
        holder.tv_selct_name.setText(name);
        holder.iv_rounded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals("subject")){

                    String last = (String) CreateGrpSubject.tv_particitants_crtgrpsub.getText();
                    final String[] tokens = last.split(Pattern.quote("/"));
                    String str = last.substring(0, last.length()-1);
                    CreateGrpSubject.tv_particitants_crtgrpsub.setText("Participants: "+( CreateGroupFriendsAdapter.selectedContactList.size()-1)+" / "+tokens[1 ]);
                }
                String name = CreateGroupFriendsAdapter.selectedContactList.get(position).getName();
               // Toast.makeText(mainActivitySignUP, name + " removed", Toast.LENGTH_SHORT).show();
                CreateGroupFriendsAdapter.selectedContactList.remove(position);

                // the below line of code is written to unselect from vertical Adapter
                for (int i=0; i<CreateGroupFriendsAdapter.contactList.size(); i++) {
                    if (name.equalsIgnoreCase(CreateGroupFriendsAdapter.contactList.get(i).getName())) {
                        CreateGroupFriendsAdapter.contactList.get(i).setStatus(false);
                        break;
                    }
                }
                notifyDataSetChanged();
                CreateGroup.createGroupFriendsAdapter.notifyDataSetChanged();
                CreateGroup.rv_all_crt_grp.setLayoutManager(new LinearLayoutManager(mainActivity));
                CreateGroup.createGroupFriendsAdapter.notifyDataSetChanged();
                if (CreateGroupFriendsAdapter.selectedContactList.size() > 0){
                    CreateGroup.view_crtgrp.setVisibility(View.VISIBLE);
                }else {
                    CreateGroup.view_crtgrp.setVisibility(View.GONE);
                }
                Log.d(TAG+" selectedContactList", "list is horizzzzz" + CreateGroupFriendsAdapter.selectedContactList + " and size is " + CreateGroupFriendsAdapter.selectedContactList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return CreateGroupFriendsAdapter.selectedContactList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView iv_rounded;
        private TextView tv_selct_name;

        CustomViewHolder(View itemView) {
            super(itemView);
            iv_rounded = (RoundedImageView) itemView.findViewById(R.id.iv_rounded);
            tv_selct_name = (TextView) itemView.findViewById(R.id.tv_selct_name);
        }
    }
}
