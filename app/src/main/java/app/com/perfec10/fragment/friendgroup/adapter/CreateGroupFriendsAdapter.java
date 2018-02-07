package app.com.perfec10.fragment.friendgroup.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.CreateGroup;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.NetworkConstants;

/**
 * Created by fluper on 30/11/17.
 */

public class CreateGroupFriendsAdapter extends RecyclerView.Adapter<CreateGroupFriendsAdapter.Holder>{
    private MainActivity mainActivity;
    private ImageLoader imageLoader;
    public static ArrayList<FriendListGS> contactList;
    public static ArrayList<FriendListGS> selectedContactList;
    private RecyclerView rv_horizontal;
    private String TAG = "CreateGroupFriendsAdapter";

    public CreateGroupFriendsAdapter(MainActivity mainActivity,
                                     ArrayList<FriendListGS> friendList, RecyclerView rv_horizontal){
        this.mainActivity = mainActivity;
        this. contactList = friendList;
        this. rv_horizontal = rv_horizontal;
        Log.d(TAG+"back from ", CreateGroup.backFrom+" ");
        if (CreateGroup.backFrom != null && CreateGroup.backFrom.equals("sub")){

        }
        selectedContactList = new ArrayList<>();
        if (selectedContactList.size() > 0){
            CreateGroup.view_crtgrp.setVisibility(View.VISIBLE);
        }else {
            CreateGroup.view_crtgrp.setVisibility(View.GONE);
        }
        HorizontalSelectAdaptor horizontalRecyclerAdapter = new HorizontalSelectAdaptor(mainActivity, "create");
        rv_horizontal.setAdapter(horizontalRecyclerAdapter);
        horizontalRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.create_group_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final FriendListGS friendListGS = new FriendListGS();
        String name = contactList.get(position).getName();
        name = name.replace("\"", "");
        holder.tv_name_crtgrp_row.setText(name);
        String image = contactList.get(position).getImage();
        image = image.replace("\"", "");
        imageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();
        imageLoader.get(NetworkConstants.imageBaseUrl+image,
                ImageLoader.getImageListener(holder.riv_crtgrp_row,
                R.mipmap.user, R.mipmap.user));
        holder.riv_crtgrp_row.setImageUrl(NetworkConstants.imageBaseUrl+image, imageLoader);

        if (contactList.get(position).getStatus()) {
            holder.iv_select_crt_grp.setImageResource(R.mipmap.selected);
        } else {
            holder.iv_select_crt_grp.setImageResource(R.mipmap.unselect);
        }

        holder.iv_select_crt_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = contactList.get(position).getName();
                String uId = contactList.get(position).getUserId();
                String img = contactList.get(position).getImage();
                if (contactList.get(position).getStatus()) {
                    contactList.get(position).setStatus(false);
                    holder.iv_select_crt_grp.setImageResource(R.mipmap.unselect);

                    Iterator<FriendListGS> iterator = selectedContactList.iterator();
                    while (iterator.hasNext()) {
                        String value = String.valueOf(iterator.next().getName());
                        if (name.equals(value)) {
                            iterator.remove();
                            break;
                        }
                        String v1 = String.valueOf(iterator.next().getUserId());
                        if (uId.equals(v1)){
                            iterator.remove();
                            break;
                        }
                        String v2 = String.valueOf(iterator.next().getImage());
                        if (img.equals(v2)){
                            iterator.remove();
                            break;
                        }
                    }
                    rv_horizontal.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, selectedContactList.size());
                    notifyDataSetChanged();
                    if (selectedContactList.size() > 0){
                        CreateGroup.view_crtgrp.setVisibility(View.VISIBLE);
                    }else {
                        CreateGroup.view_crtgrp.setVisibility(View.GONE);
                    }
                } else {
                    contactList.get(position).setStatus(true);
                    holder.iv_select_crt_grp.setImageResource(R.mipmap.selected);
                    FriendListGS pojoClass = new FriendListGS();
                    pojoClass.setName(contactList.get(position).getName());
                    pojoClass.setStatus(contactList.get(position).getStatus());
                    pojoClass.setUserId(contactList.get(position).getUserId());
                    pojoClass.setImage(contactList.get(position).getImage());
                    selectedContactList.add(pojoClass);

                    rv_horizontal.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, selectedContactList.size());
                    notifyDataSetChanged();
                    if (selectedContactList.size() > 0){
                        CreateGroup.view_crtgrp.setVisibility(View.VISIBLE);
                    }else {
                        CreateGroup.view_crtgrp.setVisibility(View.GONE);
                    }
                }

                Log.d(TAG+"selectedContactList", " " + selectedContactList + " and size is " + selectedContactList.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class Holder extends RecyclerView.ViewHolder{
        private TextView tv_name_crtgrp_row;
        private View view_crtgrp_row;
        private ImageView iv_select_crt_grp;
        private app.com.perfec10.helper.RoundedImageView riv_crtgrp_row;
        private LinearLayout ll_click_crt_grp;

        public Holder(View itemView) {
            super(itemView);
            view_crtgrp_row = (View) itemView.findViewById(R.id.view_crtgrp_row);
            tv_name_crtgrp_row = (TextView) itemView.findViewById(R.id.tv_name_crtgrp_row);
            iv_select_crt_grp = (ImageView) itemView.findViewById(R.id.iv_select_crt_grp);
            ll_click_crt_grp = (LinearLayout) itemView.findViewById(R.id.ll_click_crt_grp);
            riv_crtgrp_row = (app.com.perfec10.helper.RoundedImageView) itemView.findViewById(R.id.riv_crtgrp_row);
        }
    }
}
