package app.com.perfec10.fragment.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.adapter.ParticipantAdaptor;
import app.com.perfec10.fragment.friendposts.adapter.FriendPostAdapter;
import app.com.perfec10.fragment.login.PreLogin;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.helper.EndlessRecyclerViewScrollListener;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.model.FriendsPostGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 26/10/17.
 */

@SuppressLint("ValidFragment")
public class FriendsPost extends Fragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private RecyclerView rv_friend_post;
    private LinearLayoutManager linearLayoutManager;
    private PreferenceManager preferenceManager;
    private Progress progress;
    private ArrayList<FriendsPostGS> friendsPost = new ArrayList<>();
    private TextView tv_sw1_frnd_post;
    private ProgressBar progressbar;
    private int pageId = 1;
    private boolean flag = true;
    private FriendPostAdapter friendPostAdapter;
    private String TAG = "FriendsPost";

    public FriendsPost(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public FriendsPost(){

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
           //listenerView();
            friendsPost = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_post, container, false);
        initview(view);
        clickListner();

        return view;
    }

    @Override
    public void onResume() {
        Log.e(TAG+" on resume ", "called ");
        friendsPost = new ArrayList<>();
        listenerView();
        super.onResume();
    }

    public void initview(View view){
        tv_sw1_frnd_post = (TextView) view.findViewById(R.id.tv_sw1_frnd_post);
        rv_friend_post = (RecyclerView) view.findViewById(R.id.rv_friend_post);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_friend_post.setLayoutManager(linearLayoutManager);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);

        JsonObject jsonObject = new JsonObject();
        if (Network.isConnected(mainActivity)){
          //  progress.show();
          //  Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.friendPost,1);

        }else {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        Profile.fragmentManager = mainActivity.getSupportFragmentManager();
        for (int i= 0; i < Profile.fragmentManager.getBackStackEntryCount(); i++){
            Profile.fragmentManager.popBackStack();
        }
    }

    public void clickListner(){

    }



    public void listenerView() {
        pageId = 1;
        flag = true;
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_friend_post.setLayoutManager(linearLayoutManager);
        friendPost();

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.VISIBLE);
                        pageId = pageId + 1;
                        friendPost();
                        Log.e(TAG+" on load more ", "is called");
                    }
                });
            }
        };
        scrollListener.resetState();
        rv_friend_post.addOnScrollListener(scrollListener);
    }

    public void friendPost(){
        if (Network.isConnected(mainActivity)){
            if (pageId==1){
                progress.show();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("page",pageId);
            Log.d(TAG+" params frnd ", jsonObject+" ");
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.friendPost,1);

        }else {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response frnd post ", data+" ");
        progress.dismiss();
        String message = "";
        String likeStatus = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                        Log.d(TAG+" reponse post", jsonObject+" ");
                        message = returnEmptyString(jsonObject.get("message"));

                        if (message.equals("Successful.")){
                            JsonArray result = jsonObject.getAsJsonArray("result");
                            if (result.size() > 0){
                                for (int i = 0; i < result.size(); i++){
                                    JsonObject json = result.get(i).getAsJsonObject();
                                    FriendsPostGS friendsPostGS = new FriendsPostGS();
                                    friendsPostGS.setName(json.get("name").getAsString());
                                    friendsPostGS.setImage(json.get("image")+"");// have to check is image is null to catch m to nai ja raha
                                    friendsPostGS.setCreatedAt(json.get("created_at").getAsString());
                                    friendsPostGS.setShare_date(json.get("share_date").getAsString());
                                    friendsPostGS.setInputId(json.get("input_id").getAsInt());
                                    friendsPostGS.setPostId(json.get("post_id").getAsInt());
                                    friendsPostGS.setUserId(json.get("user_id").getAsInt());
                                    friendsPostGS.setBustWaist(json.get("bust_waist").getAsString());
                                    friendsPostGS.setWaistHips(json.get("waist_hips").getAsString());
                                    friendsPostGS.setLegsBody(json.get("legs_body").getAsString());
                                    friendsPostGS.setBodyWaist(json.get("body_waist").getAsString());
                                    friendsPostGS.setShoulderHips(json.get("shoulder_hips").getAsString());
                                    friendsPostGS.setTagPerson(json.get("tag_person")+"");
                                    friendsPostGS.setLocation(json.get("location")+"");
                                    friendsPostGS.setAge(json.get("age")+"");
                                    friendsPostGS.setAngle(json.get("angle").getAsString());
                                    friendsPostGS.setHeight(json.get("height")+"");
                                    friendsPostGS.setWeight(json.get("weight")+"");
                                    friendsPostGS.setPicture(json.get("picture").getAsString());
                                    String score = json.get("score")+"";
                                    score = score.replace("\"", "");
                                    if (score.equals("null")){
                                        score = "0";
                                    }
                                    friendsPostGS.setScore(score);
                                    friendsPostGS.setRace(json.get("race")+"");
                                    friendsPostGS.setCaption(json.get("caption")+"");
                                    friendsPostGS.setTotalLike(json.get("totalLike").getAsInt());
                                    friendsPostGS.setTotalComment(json.get("totalComment").getAsInt());

                                    JsonArray recentLikeUser = json.getAsJsonArray("recentLikeUser");
                                    if (recentLikeUser.size() > 0){
                                        JsonObject json1 = recentLikeUser.get(0).getAsJsonObject();
                                        friendsPostGS.setLikeName(json1.get("name")+"");
                                        String id = json1.get("user_id")+"";
                                        id = id.replace("\"", "");
                                        friendsPostGS.setLikeId1(id);
                                        if (recentLikeUser.size() > 1){
                                            JsonObject json2 = recentLikeUser.get(1).getAsJsonObject();
                                            friendsPostGS.setLikeName2(json2.get("name")+"");
                                            String id2 = json2.get("user_id")+"";
                                            id2 = id2.replace("\"","");
                                            friendsPostGS.setLikeId2(id2);
                                        }
                                        for (int a = 0; a < recentLikeUser.size(); a++){
                                            JsonObject jo = recentLikeUser.get(a).getAsJsonObject();
                                            String s = jo.get("user_id")+"";
                                            if (s.equals(preferenceManager.getKeyUserId())){
                                                friendsPostGS.setLiked("1");
                                                break;
                                            }else {
                                                friendsPostGS.setLiked("0");
                                            }
                                        }
                                    }else {
                                        friendsPostGS.setLiked("0");
                                    }

                                    JsonArray recentComments = json.getAsJsonArray("recentComments");
                                    if (recentComments.size() > 0){
                                        JsonObject json1 = recentComments.get(0).getAsJsonObject();
                                        friendsPostGS.setCommImg1(json1.get("image")+"");
                                        friendsPostGS.setCommName1(json1.get("name")+"");
                                        friendsPostGS.setCommComm1(json1.get("comment")+"");
                                        if (recentComments.size() > 1){
                                            JsonObject json2 = recentComments.get(1).getAsJsonObject();
                                            friendsPostGS.setCommImg2(json2.get("image")+"");
                                            friendsPostGS.setCommName2(json2.get("name")+"");
                                            friendsPostGS.setCommComm2(json2.get("comment")+"");
                                        }
                                    }

                                    friendsPost.add(friendsPostGS);
                                    Log.d(TAG, "total c "+friendsPost.get(i).getTotalLike());
                                }
                                if (friendsPost.size()>0){
                                    tv_sw1_frnd_post.setVisibility(View.GONE);
                                }
                                if (flag) {
                                    Log.d(TAG+" first ", "time");
                                    friendPostAdapter = new FriendPostAdapter(mainActivity, friendsPost);
                                    rv_friend_post.setAdapter(friendPostAdapter);

                                    flag = false;
                                } else {
                                    Log.d(TAG+" again", "and again");
                                    friendPostAdapter = new FriendPostAdapter(mainActivity, friendsPost);
                                    Parcelable recyclerViewState;
                                    recyclerViewState = rv_friend_post.getLayoutManager().onSaveInstanceState();
                                    // Restore state
                                    rv_friend_post.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                                    rv_friend_post.setAdapter(friendPostAdapter);
                                }
                            }else {
                                tv_sw1_frnd_post.setVisibility(View.GONE);
                            }
                            if (progress.isShowing()) {
                                progress.dismiss();
                            }
                            if (progressbar.getVisibility()==View.VISIBLE) {
                                progressbar.setVisibility(View.GONE);
                            }
                               /* Log.d("like status ", likeStatus+"");
                                FriendPostAdapter friendPostAdapter = new FriendPostAdapter(mainActivity, friendsPost);
                                rv_friend_post.setAdapter(friendPostAdapter);*/
                            }else {
                               // tv_sw1_frnd_post.setVisibility(View.VISIBLE);
                            }

                } catch (Exception e) {
                    Log.e(TAG+" Outcome", e.toString());
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
                //   showSessionDialog(message);
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 403:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 204:
                Log.d(TAG+" no more data ", "found");
                if (friendsPost.size() > 0){
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                    if (progressbar.getVisibility()==View.VISIBLE) {
                        progressbar.setVisibility(View.GONE);
                    }
                    tv_sw1_frnd_post.setVisibility(View.GONE);
                  //  Toast.makeText(mainActivity, "No More Data to show", Toast.LENGTH_SHORT).show();
                }else {
                    tv_sw1_frnd_post.setVisibility(View.VISIBLE);
                }

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
