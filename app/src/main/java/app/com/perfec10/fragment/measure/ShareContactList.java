package app.com.perfec10.fragment.measure;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.adapter.CreateGroupFriendsAdapter;
import app.com.perfec10.fragment.home.Home;
import app.com.perfec10.fragment.login.PreLogin;
import app.com.perfec10.fragment.measure.adapater.ShareContactsAdapter;
import app.com.perfec10.fragment.self_snaps.SelfSnapDetail;
import app.com.perfec10.fragment.self_snaps.adapter.SelfSnapAdapter;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 9/12/17.
 */

@SuppressLint("ValidFragment")
public class ShareContactList extends Fragment implements NetworkCallBack{
    private MainActivity mainActivity;
    private TextView tv_sw1_share_list, tv_shareall_share_list, tv_sw2_share_list,
            tv_sw3_share_list, tv_sw4_share_list, tv_sw5_share_list;
    private ImageView iv_back_share_list, iv_search_share_list, iv_cancel_share_list,
            iv_send_share_list;
    private EditText et_search_share_list;
    private LinearLayout ll_frqfrnd_share_list, ll_frqgrp_share_list, ll_allgrp_share_list,
            ll_allfrnd_share_list;
    private RecyclerView rv_frqfrnd_share_list, rv_frqgrp_share_list, rv_allgrp_share_list,
            rv_allfrnd_share_list;
    private LinearLayoutManager linearLayoutManager, linearLayoutManagergrp,
            linearLayoutManagerfeqF, linearLayoutManagerfreqG;
    private Typeface regular, bold;
    private ArrayList<FriendListGS> freqFriends, freqGrp, allFrnds, allGrps;
    private Progress progress;
    private String from;
    private String TAG = "ShareContactList";


    public ShareContactList(MainActivity mainActivity, String from){
        this.mainActivity = mainActivity;
        this.from = from;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        Log.d(TAG+" from ", from);
    }

    public ShareContactList(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_contact_list, container, false);
        initView(view);
        clickListner();
        return view;
    }

    public void initView(View v){
        tv_sw1_share_list = (TextView) v.findViewById(R.id.tv_sw1_share_list);
        tv_shareall_share_list = (TextView) v.findViewById(R.id.tv_shareall_share_list);
        tv_sw2_share_list = (TextView) v.findViewById(R.id.tv_sw2_share_list);
        tv_sw3_share_list = (TextView) v.findViewById(R.id.tv_sw3_share_list);
        tv_sw4_share_list = (TextView) v.findViewById(R.id.tv_sw4_share_list);
        tv_sw5_share_list = (TextView) v.findViewById(R.id.tv_sw5_share_list);

        tv_sw1_share_list.setTypeface(regular);
        tv_sw2_share_list.setTypeface(regular);
        tv_sw3_share_list.setTypeface(regular);
        tv_sw4_share_list.setTypeface(regular);
        tv_sw5_share_list.setTypeface(regular);
        tv_shareall_share_list.setTypeface(regular);

        iv_back_share_list = (ImageView) v.findViewById(R.id.iv_back_share_list);
        iv_search_share_list = (ImageView) v.findViewById(R.id.iv_search_share_list);
        iv_cancel_share_list = (ImageView) v.findViewById(R.id.iv_cancel_share_list);
        iv_send_share_list = (ImageView) v.findViewById(R.id.iv_send_share_list);

        et_search_share_list = (EditText) v.findViewById(R.id.et_search_share_list);
        et_search_share_list.setTypeface(regular);

        ll_frqfrnd_share_list = (LinearLayout) v.findViewById(R.id.ll_frqfrnd_share_list);
        ll_frqgrp_share_list = (LinearLayout) v.findViewById(R.id.ll_frqgrp_share_list);
        ll_allgrp_share_list = (LinearLayout) v.findViewById(R.id.ll_allgrp_share_list);
        ll_allfrnd_share_list = (LinearLayout) v.findViewById(R.id.ll_allfrnd_share_list);

        rv_frqfrnd_share_list = (RecyclerView) v.findViewById(R.id.rv_frqfrnd_share_list);
        linearLayoutManagerfeqF = new LinearLayoutManager(mainActivity);
        rv_frqfrnd_share_list.setLayoutManager(linearLayoutManagerfeqF);
        rv_frqgrp_share_list = (RecyclerView) v.findViewById(R.id.rv_frqgrp_share_list);
        linearLayoutManagerfreqG = new LinearLayoutManager(mainActivity);
        rv_frqgrp_share_list.setLayoutManager(linearLayoutManagerfreqG);
        rv_allgrp_share_list = (RecyclerView) v.findViewById(R.id.rv_allgrp_share_list);
        linearLayoutManagergrp = new LinearLayoutManager(mainActivity);
        rv_allgrp_share_list.setLayoutManager(linearLayoutManagergrp);
        rv_allfrnd_share_list = (RecyclerView) v.findViewById(R.id.rv_allfrnd_share_list);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_allfrnd_share_list.setLayoutManager(linearLayoutManager);
        progress.show();
        Network.hitGetApi(mainActivity,this, NetworkConstants.recentShare, 1);
        edit_Text_Focus_Listner();
    }

    public void clickListner(){
    iv_back_share_list.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainActivity.getSupportFragmentManager().popBackStack();
        }
    });
    iv_cancel_share_list.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            et_search_share_list.setText("");
        }
    });
        tv_shareall_share_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (allFrnds != null){
                    int size = allFrnds.size();
                    for (int a = 0; a < size; a++){
                        if (allFrnds.get(a).getStatus()){
                            allFrnds.get(a).setStatus(false);
                        }else {
                            allFrnds.get(a).setStatus(true);
                        }
                        ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, allFrnds);
                        rv_allfrnd_share_list.setAdapter(shareContactsAdapter);
                        shareContactsAdapter.notifyDataSetChanged();
                    }
                }
                if (freqFriends != null){
                    int size = freqFriends.size();
                    for (int b = 0; b < size; b++){
                        if (freqFriends.get(b).getStatus()){
                            freqFriends.get(b).setStatus(false);
                        }else {
                            freqFriends.get(b).setStatus(true);
                        }
                    }
                    ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, freqFriends);
                    rv_frqfrnd_share_list.setAdapter(shareContactsAdapter);
                    shareContactsAdapter.notifyDataSetChanged();
                }
                if (allGrps != null){
                    int size = allGrps.size();
                    for (int b = 0; b < size; b++){
                        if (allGrps.get(b).getStatus()){
                            allGrps.get(b).setStatus(false);
                        }else {
                            allGrps.get(b).setStatus(true);
                        }
                    }
                    ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, allGrps);
                    rv_allgrp_share_list.setAdapter(shareContactsAdapter);
                    shareContactsAdapter.notifyDataSetChanged();
                }
                if (freqGrp != null){
                    int size = freqGrp.size();
                    for (int b = 0; b < size; b++){
                        if (freqGrp.get(b).getStatus()){
                            freqGrp.get(b).setStatus(false);
                        }else {
                            freqGrp.get(b).setStatus(true);
                        }
                    }
                    ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, freqGrp);
                    rv_frqgrp_share_list.setAdapter(shareContactsAdapter);
                    shareContactsAdapter.notifyDataSetChanged();
                }
                /*if (allFrnds.size() > 0){
                    for (int a = 0; a < allFrnds.size();a++){

                        allFrnds.get(a).setStatus(true);
                    }
                }
                ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivitySignUP, allFrnds);
                rv_frqfrnd_share_list.setAdapter(shareContactsAdapter);
                shareContactsAdapter.notifyDataSetChanged();*/

                /*bt_select_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VerticalRecyclerAdapter.contactList!=null) {       // contatct list is static
                    int size=VerticalRecyclerAdapter.contactList.size();
                    for (int position=0; position<size; position++) {
                        if (contactList.get(position).getStatus()) {
                            contactList.get(position).setStatus(false);
                        } else {
                            contactList.get(position).setStatus(true);
                        }
                        verticalRecyclerAdapter.notifyDataSetChanged();    // adapter is static
                    }
                }

            }
        });*/

                /*boolean allfnd = false, allgrp = false, recentFrnd = false, recentGrp = false;
                if (freqGrp.size() > 0){
                    recentGrp = true;
                }else {
                    recentGrp = false;
                }
                if (freqFriends.size() > 0){
                    recentFrnd = true;
                }else {
                    recentFrnd =false;
                }
                if (allFrnds.size() > 0){
                    allfnd = true;
                }else {
                    allfnd = false;
                }
                if (allGrps.size() > 0){
                    allgrp = true;
                }else {
                    allgrp = false;
                }
                if (!recentFrnd || !recentGrp || !allfnd || !allgrp){
                    // if contact list is not null
                    Log.d("not ", "zero");
                }else {
                    Toast.makeText(mainActivitySignUP, "No contacts to Share ", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        iv_send_share_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePost();
            }
        });

    }

    public void edit_Text_Focus_Listner() {
        et_search_share_list.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // search for all frnds
                if (et_search_share_list.getText().toString().length() > 0){
                    ArrayList<FriendListGS> searchedlist = new ArrayList<>();
                    for (int i =0 ; i < allFrnds.size(); i++){
                        String s = et_search_share_list.getText().toString();
                        boolean a = Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(allFrnds.get(i).getName()).find();

                        if (a){
                            FriendListGS friendListGS = new FriendListGS();
                            friendListGS.setUserId(allFrnds.get(i).getUserId());
                            friendListGS.setName(allFrnds.get(i).getName());
                            friendListGS.setImage(allFrnds.get(i).getImage());
                            searchedlist.add(friendListGS);
                        }

                    }
                    ShareContactsAdapter createGroupFriendsAdapter = new ShareContactsAdapter(mainActivity, searchedlist);
                    rv_allfrnd_share_list.setAdapter(createGroupFriendsAdapter);

                }else {
                    ShareContactsAdapter createGroupFriendsAdapter = new ShareContactsAdapter(mainActivity, allFrnds);
                    rv_allfrnd_share_list.setAdapter(createGroupFriendsAdapter);
                }
                // search for all grps
                if (et_search_share_list.getText().toString().length() > 0){
                    ArrayList<FriendListGS> searchedList = new ArrayList<>();
                    for (int i = 0; i < allGrps.size(); i++){
                        String s = et_search_share_list.getText().toString();
                        boolean a = Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(allGrps.get(i).getName()).find();

                        if (a){
                            FriendListGS friendListGS = new FriendListGS();
                            friendListGS.setUserId(allGrps.get(i).getUserId());
                            friendListGS.setName(allGrps.get(i).getName());
                            friendListGS.setImage(allGrps.get(i).getImage());
                            searchedList.add(friendListGS);
                        }
                    }
                    ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, searchedList);
                    rv_allgrp_share_list.setAdapter(shareContactsAdapter);
                }else {
                    ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, allGrps);
                    rv_allgrp_share_list.setAdapter(shareContactsAdapter);
                }
                // search frequent frnds
                if (et_search_share_list.getText().toString().length() > 0){
                    ArrayList<FriendListGS> searchedList = new ArrayList<>();
                    for (int i = 0; i < freqFriends.size(); i++){
                        String s = et_search_share_list.getText().toString();
                        boolean a = Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(freqFriends.get(i).getName()).find();

                        if (a){
                            FriendListGS friendListGS = new FriendListGS();
                            friendListGS.setUserId(freqFriends.get(i).getUserId());
                            friendListGS.setName(freqFriends.get(i).getName());
                            friendListGS.setImage(freqFriends.get(i).getImage());
                            searchedList.add(friendListGS);
                        }
                    }
                    ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, searchedList);
                    rv_frqfrnd_share_list.setAdapter(shareContactsAdapter);
                }else {
                    ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, freqFriends);
                    rv_frqfrnd_share_list.setAdapter(shareContactsAdapter);
                }
                // search in frequent groups
                if (et_search_share_list.getText().toString().length() > 0){
                    ArrayList<FriendListGS> searchedList = new ArrayList<>();
                    for (int i = 0; i < freqGrp.size(); i++){
                        String s = et_search_share_list.getText().toString();
                        boolean a = Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(freqGrp.get(i).getName()).find();

                        if (a){
                            FriendListGS friendListGS = new FriendListGS();
                            friendListGS.setUserId(freqGrp.get(i).getUserId());
                            friendListGS.setName(freqGrp.get(i).getName());
                            friendListGS.setImage(freqGrp.get(i).getImage());
                            searchedList.add(friendListGS);
                        }
                    }
                    ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, searchedList);
                    rv_frqgrp_share_list.setAdapter(shareContactsAdapter);
                }else {
                    ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, freqGrp);
                    rv_frqgrp_share_list.setAdapter(shareContactsAdapter);
                }

            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    public void updatePost(){
        try {
            JsonObject jsonObject = new JsonObject();
            if (from.equals("stats")){
                jsonObject.addProperty("input_id",Stats.input_id);
                jsonObject.addProperty("post_id", Stats.post_id);

                if (StatsInput.et_tag_stats !=null){
                    String tag = StatsInput.et_tag_stats.getText().toString();
                    tag = tag.replace("@", "");
                    jsonObject.addProperty("tag_person", tag);
                }else {

                    jsonObject.addProperty("tag_person", "");
                }
                if(Stats.location !=null){
                    jsonObject.addProperty("location", Stats.location);
                }else {
                    jsonObject.addProperty("location", "");
                }

                if (Stats.age != null){
                    Log.d(TAG+" inside ", "if");
                    jsonObject.addProperty("age",Stats.age);
                }else {
                    Log.d(TAG+" inside ", "else");
                    jsonObject.addProperty("age","");
                }
                if (Stats.height != null){
                    jsonObject.addProperty("height", Stats.height);
                }else {
                    jsonObject.addProperty("height", "");
                }
                if (Stats.wieght != null){
                    jsonObject.addProperty("weight", Stats.wieght);
                }else {
                    jsonObject.addProperty("weight", "");
                }
                if (StatsInput.et_race_stats != null){
                    jsonObject.addProperty("race", StatsInput.et_race_stats.getText().toString());
                }else {
                    jsonObject.addProperty("race", "");
                }

                if (Stats.score != null){
                    if (Stats.score.equals("0")){
                        jsonObject.addProperty("score", Stats.rb_stats.getRating()+"");
                    }else {
                        jsonObject.addProperty("score", Stats.score);
                    }

                }else {
                    jsonObject.addProperty("score", "0");
                }
                if (StatsInput.et_note_stats != null){
                    jsonObject.addProperty("note", StatsInput.et_note_stats.getText().toString());
                }else {
                    jsonObject.addProperty("note", "");
                }
            }
            else if (from.equals("adapter")){
                jsonObject.addProperty("input_id",SelfSnapAdapter.inputId);
                jsonObject.addProperty("post_id", SelfSnapAdapter.postId);
                if (!SelfSnapAdapter.tagPerson.equals("null")){

                    jsonObject.addProperty("tag_person", SelfSnapAdapter.tagPerson.toString());
                }else {

                    jsonObject.addProperty("tag_person", "");
                }
                if (!Stats.location.equals("null")){
                    jsonObject.addProperty("location", Stats.location);
                }else {
                    jsonObject.addProperty("location", "");
                }

                if (!SelfSnapAdapter.age.equals("null")){
                    Log.d(TAG+" inside ", "if");
                    jsonObject.addProperty("age",SelfSnapAdapter.age);
                }else {
                    Log.d(TAG+" inside ", "else");
                    jsonObject.addProperty("age","");
                }
                if (!SelfSnapAdapter.height.equals("null")){
                    jsonObject.addProperty("height", SelfSnapAdapter.height);
                }else {
                    jsonObject.addProperty("height", "");
                }
                if (!SelfSnapAdapter.wieght.equals("null")){
                    jsonObject.addProperty("weight", SelfSnapAdapter.wieght);
                }else {
                    jsonObject.addProperty("weight", "");
                }
                if (!SelfSnapAdapter.race.equals("null")){
                    jsonObject.addProperty("race", SelfSnapAdapter.race);
                }else {
                    jsonObject.addProperty("race", "");
                }
                if (SelfSnapAdapter.scores != null){
                    jsonObject.addProperty("score", SelfSnapAdapter.scores);
                }else {
                    jsonObject.addProperty("score", "0");
                }
                if (SelfSnapDetail.et_addnote_eddetail != null){
                    jsonObject.addProperty("note", SelfSnapDetail.et_addnote_eddetail.getText().toString());
                }else {
                    jsonObject.addProperty("note", "");
                }
            }

            jsonObject.addProperty("caption", Share.et_caption_share.getText().toString());

            List<Integer> fid = new ArrayList<Integer>();
            for (int a = 0; a < allFrnds.size(); a++){
                if (allFrnds.get(a).getStatus()){
                    fid.add(Integer.parseInt(allFrnds.get(a).getUserId())) ;
                }
            }
            for (int b = 0; b < freqFriends.size(); b++){
                if (freqFriends.get(b).getStatus()){
                    fid.add(Integer.parseInt(freqFriends.get(b).getUserId())) ;
                }
            }
            List<Integer> gId = new ArrayList<Integer>();
            for (int c = 0; c < allGrps.size(); c++){
                if (allGrps.get(c).getStatus()){
                    gId.add(Integer.parseInt(allGrps.get(c).getGroupId()));
                }
            }
            for (int d = 0; d < freqGrp.size(); d++){
                if (freqGrp.get(d).getStatus()){
                    gId.add(Integer.parseInt(freqGrp.get(d).getGroupId()));
                }
            }
            Gson gson = new Gson();

            if (fid.size() > 0){
                jsonObject.add("friends_id",gson.toJsonTree(fid));
            }else {
                jsonObject.add("friends_id",gson.toJsonTree(fid));
            }
            if (gId.size() > 0){
                jsonObject.add("groups_id", gson.toJsonTree(gId));
            }else {
                jsonObject.add("groups_id", gson.toJsonTree(gId));
            }


            Log.d(TAG+" params update postss ", jsonObject+" ");
            if (Network.isConnected(mainActivity)){
                if (fid.size() > 0 || gId.size() > 0){
                    progress.show();
                     Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.updatePost,
                        2);
                }else {
                    Toast.makeText(mainActivity, "Please select Participants to share", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        progress.dismiss();
        Log.d(TAG+" response list ", data+" ");
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode==1){
                        message = jsonObject.get("message").getAsString();
                        if (message.equalsIgnoreCase("Recent post users.")){
                            JsonObject result = jsonObject.get("result").getAsJsonObject();
                            JsonArray recentFriends = result.getAsJsonArray("recentFriends");
                            freqFriends = new ArrayList<>();
                            if (recentFriends.size() > 0){
                                ll_frqfrnd_share_list.setVisibility(View.VISIBLE);
                                for (int i = 0; i < recentFriends.size(); i++){
                                JsonObject json = recentFriends.get(i).getAsJsonObject();
                                FriendListGS friendListGS = new FriendListGS();
                                friendListGS.setName(json.get("name").getAsString());
                                friendListGS.setUserId(json.get("user_id").getAsInt()+"");
                                friendListGS.setImage(json.get("image")+"");
                                friendListGS.setStatus(false);
                                freqFriends.add(friendListGS);
                                }
                                ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, freqFriends);
                                rv_frqfrnd_share_list.setAdapter(shareContactsAdapter);
                            }else {
                                ll_frqfrnd_share_list.setVisibility(View.GONE);
                            }

                            JsonArray recentGroups = result.getAsJsonArray("recentGroups");
                            freqGrp = new ArrayList<>();
                            if (recentGroups.size() > 0){
                                ll_frqgrp_share_list.setVisibility(View.VISIBLE);
                                for (int i = 0; i < recentGroups.size(); i++){
                                    FriendListGS friendListGS = new FriendListGS();
                                    JsonObject json = recentGroups.get(i).getAsJsonObject();
                                    friendListGS.setName(json.get("name").getAsString());
                                    friendListGS.setGroupId(json.get("group_id").getAsString());
                                    friendListGS.setImage(json.get("image")+"");
                                    friendListGS.setStatus(false);
                                    freqGrp.add(friendListGS);
                                }
                                ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, freqGrp);
                                rv_frqgrp_share_list.setAdapter(shareContactsAdapter);
                            }else {
                                ll_frqgrp_share_list.setVisibility(View.GONE);
                            }

                            JsonArray friends = result.getAsJsonArray("friends");
                            allFrnds = new ArrayList<>();
                            if (friends.size() > 0){
                                ll_allfrnd_share_list.setVisibility(View.VISIBLE);
                            for (int i = 0; i < friends.size(); i++){
                                FriendListGS friendListGS = new FriendListGS();
                                JsonObject json = friends.get(i).getAsJsonObject();
                                friendListGS.setName(json.get("name").getAsString());
                                friendListGS.setUserId(json.get("user_id").getAsInt()+"");
                                friendListGS.setImage(json.get("image")+"");
                                friendListGS.setStatus(false);
                                allFrnds.add(friendListGS);
                            }
                                ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, allFrnds);
                                rv_allfrnd_share_list.setAdapter(shareContactsAdapter);
                            }else {
                                ll_allfrnd_share_list.setVisibility(View.GONE);
                            }

                            JsonArray groups = result.getAsJsonArray("groups");
                            allGrps = new ArrayList<>();
                            if (groups.size() > 0){
                                ll_allgrp_share_list.setVisibility(View.VISIBLE);
                                for (int i = 0; i < groups.size(); i++){
                                    FriendListGS friendListGS = new FriendListGS();
                                    JsonObject json = groups.get(i).getAsJsonObject();
                                    friendListGS.setName(json.get("name").getAsString());
                                    friendListGS.setGroupId(json.get("group_id").getAsString());
                                    friendListGS.setImage(json.get("image")+"");
                                    friendListGS.setStatus(false);
                                    allGrps.add(friendListGS);
                                }
                                ShareContactsAdapter shareContactsAdapter = new ShareContactsAdapter(mainActivity, allGrps);
                                rv_allgrp_share_list.setAdapter(shareContactsAdapter);

                            }else {
                                ll_allgrp_share_list.setVisibility(View.GONE);
                            }
                        }
                    }else if (requestCode==2){
                        Log.d(TAG+" share send ", data+" ");
                        message = jsonObject.get("message").getAsString();
                        Toast.makeText(mainActivity, "Your post has been shared successfully", Toast.LENGTH_SHORT).show();
                        Stats.score = ""; Stats.post_id=""; Stats.input_id=""; Stats.wieght= "";
                        Stats.height = ""; Stats.age = ""; Stats.location = ""; Stats.updatePost = true;
                        for (int i =0; i < MainActivity.fragmentManager.getBackStackEntryCount(); i++){
                            mainActivity.getSupportFragmentManager().popBackStack();
                        }
                        //  MainActivity.fragmentManager = mainActivitySignUP.getSupportFragmentManager();
                       // MainActivity.fragmentManager.beginTransaction().replace(R.id.main_frame, new Home(mainActivitySignUP), " home").commit();
                    }

                } catch (Exception e) {
                    Log.d(TAG+" Outcome", e.toString());
                }
                break;
            case 201:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 400:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 401:
                message = returnEmptyString(jsonObject.get("message"));
                break;
            case 403:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onError(String msg) {

    }
}
