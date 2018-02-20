package app.com.perfec10.fragment.friendposts;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendposts.adapter.CommentsAdapter;
import app.com.perfec10.fragment.friendposts.adapter.FriendPostAdapter;
import app.com.perfec10.fragment.friendposts.adapter.LikeAdapter;
import app.com.perfec10.helper.EndlessRecyclerViewScrollListener;
import app.com.perfec10.model.CommentGS;
import app.com.perfec10.model.LikeGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.CircleImageView;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 9/12/17.
 */

@SuppressLint("ValidFragment")
public class Comments extends Fragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private String postId;
    private TextView tv_sw1_comment, tv_sw2_comment;
    private ImageView iv_back_comment, iv_send_comment;
    private RecyclerView rv_list_comment;
    private LinearLayoutManager linearLayoutManager;
    private EditText et_comment;
    private Typeface bold, regular;
    private Progress progress;
    private PreferenceManager preferenceManager;
    private ArrayList<CommentGS> commentsList = new ArrayList<>();
    private int pageId = 1;
    private boolean flag = true;
    private CommentsAdapter commentsAdapter;
    private ProgressBar progress_bar_comm;
    View view;

    public Comments(MainActivity mainActivity, String postId){
        this.mainActivity = mainActivity;
        this.postId = postId;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        preferenceManager = new PreferenceManager(mainActivity);
    }

    public Comments(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.comments, container, false);
        initView(view);
        clickListner();
        listenerView();
        return view;
    }

    public void initView(View view){
        tv_sw1_comment = (TextView) view.findViewById(R.id.tv_sw1_comment);
        tv_sw2_comment = (TextView) view.findViewById(R.id.tv_sw2_comment);
        tv_sw1_comment.setTypeface(bold);
        tv_sw2_comment.setTypeface(regular);
        iv_back_comment = (ImageView) view.findViewById(R.id.iv_back_comment);
        iv_send_comment = (ImageView) view.findViewById(R.id.iv_send_comment);
        rv_list_comment = (RecyclerView) view.findViewById(R.id.rv_list_comment);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_list_comment.setLayoutManager(linearLayoutManager);

        progress_bar_comm = (ProgressBar) view.findViewById(R.id.progress_bar_comm);

        et_comment = (EditText) view.findViewById(R.id.et_comment);
        et_comment.setTypeface(regular);
    }

    public void commentList(){
        if (Network.isConnected(mainActivity)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("post_id", postId);
            jsonObject.addProperty("page",pageId+"" );
            Log.d("params comments ", jsonObject+" ");
            if (pageId==1){
                progress.show();
            }

            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.postCommentUsers, 1);
        }else {
            Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickListner(){
    iv_back_comment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainActivity.getSupportFragmentManager().popBackStack();
        }
    });
        iv_send_comment .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_comment.getText().toString().length() > 0){
                    addComment();
                }else {
                    et_comment.setError("Add Comment");
                }

            }
        });
    }

    public void listenerView() {
        pageId = 1;
        flag = true;
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_list_comment.setLayoutManager(linearLayoutManager);
        commentList();

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        progress_bar_comm.setVisibility(View.VISIBLE);
                        pageId = pageId + 1;
                        commentList();
                        Log.e("on load more ", "is called");
                    }
                });
            }
        };
        scrollListener.resetState();
        rv_list_comment.addOnScrollListener(scrollListener);
    }

    public void addComment(){
        if (Network.isConnected(mainActivity)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("post_id", postId);
            jsonObject.addProperty("comment", et_comment.getText().toString());
            Log.d("params add comm ", jsonObject+" ");
            progress.show();
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.commentOnPost, 2);
        }else {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        progress.dismiss();
        String message = "";
        Log.d("response comment ", data+" ");
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode==1){
                        message = jsonObject.get("message").getAsString();
                        if (message.equals("Successful.")){
                            JsonArray result = jsonObject.getAsJsonArray("result");
                            if (result.size() > 0){
                            for (int i = 0; i < result.size(); i++){
                                JsonObject json = result.get(i).getAsJsonObject();
                                CommentGS commentGS = new CommentGS();
                                commentGS.setUserId(json.get("user_id").getAsInt());
                                commentGS.setImage(json.get("image")+"");
                                commentGS.setCommentId(json.get("comment_id").getAsInt());
                                commentGS.setComment(json.get("comment")+"");
                                commentGS.setName(json.get("name")+"");
                                commentsList.add(commentGS);
                            }
                                /* commentsAdapter = new CommentsAdapter(mainActivitySignUP, commentsList);
                                rv_list_comment.setAdapter(commentsAdapter);*/

                                if (flag) {
                                    Log.d("first ", "time");
                                    commentsAdapter = new CommentsAdapter(mainActivity, commentsList);
                                    rv_list_comment.setAdapter(commentsAdapter);

                                    flag = false;
                                } else {
                                    Log.d("again", "and again");
                                    commentsAdapter = new CommentsAdapter(mainActivity, commentsList);
                                    Parcelable recyclerViewState;
                                    recyclerViewState = rv_list_comment.getLayoutManager().onSaveInstanceState();
                                    // Restore state
                                    rv_list_comment.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                                    rv_list_comment.setAdapter(commentsAdapter);
                                }
                            }
                            if (progress.isShowing()) {
                                progress.dismiss();
                            }
                            if (progress_bar_comm.getVisibility()==View.VISIBLE) {
                                progress_bar_comm.setVisibility(View.GONE);
                            }

                        }
                    }else if (requestCode==2){
                        Log.d("response add comm 1", data+" ");
                        message = jsonObject.get("message").getAsString();
                        if (message.equals("successful")){
                            Toast.makeText(mainActivity, "Comment Added successfully ", Toast.LENGTH_SHORT).show();
                            et_comment.setText("");
                            tv_sw2_comment.setVisibility(View.GONE);
                            mainActivity.getSupportFragmentManager().popBackStack();
                            InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS,0);

                            if (commentsList!=null) {
                                commentsList.clear();
                                pageId=1;
                            }
                            commentList();
                        }
                    }

                } catch (Exception e) {
                    Log.d("Outcome", e.toString());
                }
                break;
            case 201:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 204:
                if (commentsList.size() > 0){
                    if (progress.isShowing()) {
                        progress.dismiss();
                    }
                    if (progress_bar_comm.getVisibility()==View.VISIBLE) {
                        progress_bar_comm.setVisibility(View.GONE);
                    }
                }else {
                    tv_sw2_comment.setVisibility(View.VISIBLE);
                }

                break;
            case 400:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 401:
                message = returnEmptyString(jsonObject.get("message"));
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
