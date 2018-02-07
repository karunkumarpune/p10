package app.com.perfec10.fragment.friendposts;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendposts.adapter.LikeAdapter;
import app.com.perfec10.fragment.measure.adapater.ShareContactsAdapter;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.model.LikeGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 12/12/17.
 */

@SuppressLint("ValidFragment")
public class LikeScreen extends Fragment implements NetworkCallBack{
    private MainActivity mainActivity;
    private String postId;
    private TextView tv_sw1_like, tv_count_like;
    private ImageView iv_back_like;
    private RecyclerView rv_list_like;
    private LinearLayoutManager linearLayoutManager;
    private Typeface bold, regular;
    private Progress progress;
    private int page = 0;
    private ArrayList<LikeGS> likeList = new ArrayList<>();

    public LikeScreen(MainActivity mainActivity, String postId){
        this.mainActivity = mainActivity;
        this.postId = postId;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public LikeScreen(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.like_screen, container, false);
        initView(v);
        clickListner();
        return v;
    }

    public void initView(View v){
        tv_sw1_like = (TextView) v.findViewById(R.id.tv_sw1_like);
        tv_count_like = (TextView) v.findViewById(R.id.tv_count_like);

        tv_sw1_like.setTypeface(bold);
        tv_count_like.setTypeface(regular);

        iv_back_like = (ImageView) v.findViewById(R.id.iv_back_like);

        rv_list_like = (RecyclerView) v.findViewById(R.id.rv_list_like);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_list_like.setLayoutManager(linearLayoutManager);

        like();
    }

    public void clickListner(){
        iv_back_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.getSupportFragmentManager().popBackStack();
            }
        });
    }

    public void like(){
        if (Network.isConnected(mainActivity)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("post_id", postId);
            jsonObject.addProperty("page", page);
            Log.d("params like list ", jsonObject+" ");
            progress.show();
            Network.hitPostApiWithAuth(mainActivity, jsonObject,this, NetworkConstants.postLikeUsers,1);
        }else {
            Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        progress.dismiss();
        Log.d("response list ", data+" ");
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode==1){
                        message = jsonObject.get("message").getAsString();
                        if (message.equals("Successful.")){
                            JsonArray result = jsonObject.getAsJsonArray("result");
                            if (result.size() > 0){
                                if (result.size()==1){
                                    tv_count_like.setText("All Like "+result.size());
                                }else {
                                    tv_count_like.setText("All Likes "+result.size());
                                }
                                for (int i = 0; i < result.size(); i++){
                                    LikeGS likeGS = new LikeGS();
                                    JsonObject json = result.get(i).getAsJsonObject();
                                    likeGS.setUserId(json.get("user_id").getAsInt());
                                    likeGS.setName(json.get("name").getAsString());
                                    likeGS.setImage(json.get("image")+"");
                                    likeList.add(likeGS);
                                }
                                LikeAdapter likeAdapter = new LikeAdapter(mainActivity, likeList);
                                rv_list_like.setAdapter(likeAdapter);
                            }
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
                tv_count_like.setText("0 Likes");
                Toast.makeText(mainActivity, "No likes to Show", Toast.LENGTH_SHORT).show();
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
