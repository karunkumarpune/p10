package app.com.perfec10.fragment.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.fragment.self_snaps.adapter.SelfSnapAdapter;
import app.com.perfec10.helper.EndlessRecyclerViewScrollListener;
import app.com.perfec10.model.SelfPostGS;
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
public class SelfSnaps extends Fragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private TextView tv_sw1_selfpost, tv_sw2_selfpost;
    public static  TextView tv_sw3_selfpost;
    private RecyclerView rv_self_post;
    public static LinearLayout ll_header;
    private LinearLayoutManager linearLayoutManager;
    private Typeface regular, bold;
    private PreferenceManager preferenceManager;
    private Progress progress;
    private ArrayList<SelfPostGS> postList = new ArrayList<>();
    private ProgressBar progressbar_selfpost;
    private int pageId, count=0;
    private boolean flag = true;
    private SelfSnapAdapter selfSnapAdapter;
    boolean isViewVisible = false;
    private boolean callView;
    private View view;
    private String TAG = "SelfSnaps";

    public SelfSnaps(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public SelfSnaps() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        /*if (getView() != null){
            callView = true;*/
            if (isVisibleToUser ) {
                //  onCreateView();
                postList = new ArrayList<>();
              //  listenerView();
                isViewVisible = true;
                Log.e(TAG+" isVisibleToUser ", "isVisibleToUser");
            }
        /*}else {
            callView = false;
            Log.d("called from create ", "view");
        }*/

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            view = inflater.inflate(R.layout.self_snaps, container, false);
            initView(view);
            clickListner();
            /*if (!callView){
                postList.clear();
                listenerView();
            }*/

        return view;
    }



    public void initView(View view) {
        tv_sw1_selfpost = (TextView) view.findViewById(R.id.tv_sw1_selfpost);
        tv_sw2_selfpost = (TextView) view.findViewById(R.id.tv_sw2_selfpost);
        tv_sw3_selfpost = (TextView) view.findViewById(R.id.tv_sw3_selfpost);
        ll_header=(LinearLayout) view.findViewById(R.id.ll_header);
        progressbar_selfpost = (ProgressBar) view.findViewById(R.id.progressbar_selfpost);

        rv_self_post = (RecyclerView) view.findViewById(R.id.rv_self_post);
        rv_self_post.setNestedScrollingEnabled(false);

        tv_sw1_selfpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mainActivity,MyPerfec10Body.class));
            }
        });


        Profile.fragmentManager = mainActivity.getSupportFragmentManager();
        for (int i = 0; i < Profile.fragmentManager.getBackStackEntryCount(); i++) {
            Profile.fragmentManager.popBackStack();
        }
    }

    public void clickListner() {

    }

    public void selfPost() {
        if (Network.isConnected(mainActivity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", preferenceManager.getKeyUserId());
            jsonObject.addProperty("page", pageId);
            Log.d(TAG+" params self post ", jsonObject + "");
            if (pageId == 1) {
                progress.show();
            }
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.userPost, 1);
        } else {
            Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        postList = new ArrayList<>();
        listenerView();
        super.onResume();
        Log.d(TAG+" onResumeeee", "onResumeee"+callView);
        /*if (!callView){
            postList.clear();
            listenerView();
        }*/
    }

    public void listenerView() {

        pageId = 1;
        flag = true;
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_self_post.setLayoutManager(linearLayoutManager);
        selfPost();
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                progressbar_selfpost.setVisibility(View.VISIBLE);
                pageId = pageId + 1;
                selfPost();
                Log.e(TAG+" onLoadMore ", "is called" +totalItemsCount);
            }
        };
        scrollListener.resetState();
        rv_self_post.addOnScrollListener(scrollListener);

    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response self post ", data + " ");
        String message = "";
        String likeStatus = "";
        JsonObject jsonObject = (JsonObject) data;
        if (progress.isShowing()) {
            progress.dismiss();
        }
        if (progressbar_selfpost.getVisibility() == View.VISIBLE) {
            progressbar_selfpost.setVisibility(View.GONE);
        }
        switch (statusCode) {
            case 200:
                try {
                    message = returnEmptyString(jsonObject.get("message"));

                    if (message.equals("Successful")) {
                        JsonArray result = jsonObject.getAsJsonArray("result");
                        if (result.size() > 0) {
                            count=count+result.size();
                            for (int i = 0; i < result.size(); i++) {
                                SelfPostGS selfPostGS = new SelfPostGS();
                                JsonObject json = result.get(i).getAsJsonObject();
                                selfPostGS.setInputId(json.get("input_id").getAsInt());
                                selfPostGS.setCreated_at(json.get("created_at").getAsString());
                                selfPostGS.setPostId(json.get("post_id").getAsInt());
                                selfPostGS.setUserId(json.get("user_id").getAsInt());
                                selfPostGS.setBustWaist(json.get("bust_waist").getAsString());
                                selfPostGS.setWaistHips(json.get("waist_hips").getAsString());
                                selfPostGS.setLegsBody(json.get("legs_body").getAsString());
                                selfPostGS.setBodyWaist(json.get("body_waist").getAsString());
                                selfPostGS.setShoulderHips(json.get("shoulder_hips").getAsString());
                                selfPostGS.setTagPerson(json.get("tag_person") + "");
                                selfPostGS.setLocation(json.get("location") + "");
                                selfPostGS.setAge(json.get("age") + "");
                                selfPostGS.setHeight(json.get("height") + "");
                                selfPostGS.setWeight(json.get("weight") + "");
                                selfPostGS.setRace(json.get("race") + "");
                                selfPostGS.setPicture(json.get("picture").getAsString());
                                selfPostGS.setCaption(json.get("caption") + "");
                                selfPostGS.setTotalLike(json.get("totalLike").getAsInt());
                                selfPostGS.setTotalComment(json.get("totalComment").getAsInt());
                                selfPostGS.setAngle(json.get("angle").getAsInt());
                                String score = json.get("score")+"";
                                score = score.replace("\"", "");
                                if (score.equals("null")){
                                    score = "0";
                                }
                                selfPostGS.setScore(score);

                                JsonArray recentLikeUser = json.get("recentLikeUser").getAsJsonArray();
                                if (recentLikeUser.size() > 0) {
                                    JsonObject json1 = recentLikeUser.get(0).getAsJsonObject();
                                    selfPostGS.setLikeName1(json1.get("name").getAsString());
                                    String id = json1.get("user_id")+"";
                                    id = id.replace("\"", "");
                                    selfPostGS.setLikeId1(id);
                                    if (recentLikeUser.size() > 1) {
                                        JsonObject json2 = recentLikeUser.get(1).getAsJsonObject();
                                        selfPostGS.setLikeName2(json2.get("name").getAsString());
                                        String id2 = json2.get("user_id")+"";
                                        id2 = id2.replace("\"","");
                                        selfPostGS.setLikeId2(id2);
                                    }
                                    for (int a = 0; a < recentLikeUser.size(); a++) {
                                        JsonObject jo = recentLikeUser.get(a).getAsJsonObject();
                                        String s = jo.get("user_id").getAsString();
                                        if (s.equals(preferenceManager.getKeyUserId())) {
                                            selfPostGS.setLiked("1");
                                            break;
                                        } else {
                                            selfPostGS.setLiked("0");
                                        }
                                    }
                                }else {
                                    selfPostGS.setLiked("0");
                                }
                                postList.add(selfPostGS);
                            }
                          /* SelfSnapAdapter selfSnapAdapter = new SelfSnapAdapter(mainActivity, postList);
                           rv_self_post.setAdapter(selfSnapAdapter);*/
                            if (flag) {
                                Log.d(TAG+" first ", "time");
                                ll_header.setVisibility(View.GONE);
                                selfSnapAdapter = new SelfSnapAdapter(mainActivity, postList);
                                rv_self_post.setAdapter(selfSnapAdapter);

                                flag = false;
                            } else {
                                Log.d(TAG+" again", "and again");
                                selfSnapAdapter = new SelfSnapAdapter(mainActivity, postList);
                                Parcelable recyclerViewState;
                                recyclerViewState = rv_self_post.getLayoutManager().onSaveInstanceState();
                                // Restore state
                                rv_self_post.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                                rv_self_post.setAdapter(selfSnapAdapter);
                            }
                        }
                    } else {
                        tv_sw3_selfpost.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.e(TAG+" Outcome", e.toString());
                }
                break;
            case 201:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 204:
                if (postList.size() > 0){
                   /* if (progress.isShowing()) {
                        progress.dismiss();
                    }
                    if (progressbar_selfpost.getVisibility()==View.VISIBLE) {
                        progressbar_selfpost.setVisibility(View.GONE);
                    }*/
                }else {
                    tv_sw3_selfpost.setVisibility(View.VISIBLE);
                }
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
            default:
                Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void onError(String msg) {

    }
}
