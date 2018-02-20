package app.com.perfec10.fragment.friendgroup;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.adapter.CreateGroupFriendsAdapter;
import app.com.perfec10.fragment.friendgroup.adapter.GroupFriendAdapter;
import app.com.perfec10.fragment.friendgroup.adapter.HorizontalSelectAdaptor;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 21/11/17.
 */

@SuppressLint("ValidFragment")
public class CreateGroup extends Fragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private RecyclerView rv_selected_crt_grp;
    public static RecyclerView rv_all_crt_grp;
    private LinearLayoutManager linearLayoutManager, linearLayoutManager1;
    private TextView tv_sw1_crt_grp, tv_nofrnd_crt_grp;
    private ImageView iv_search_crt_grp, iv_cancle_crt_grp, iv_create_crt_grp, iv_back_crtgrp;
    private EditText et_search_crt_grp;
    private PreferenceManager preferenceManager;
    private Progress progress;
    private ArrayList<FriendListGS> friendList;
    private Typeface regular, bold;
    public static CreateGroupFriendsAdapter createGroupFriendsAdapter;
    private HorizontalSelectAdaptor horizontalSelectAdaptor;
    public static View view_crtgrp;
    public static String backFrom;
    private View view;
    private String TAG = "CreateGroup";

    public CreateGroup(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        Profile.calledFrom = "1";
    }

    public CreateGroup(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.create_group, container, false);
            initView(view);
            clickistner();
        }

        return view;
    }

    public void initView(View view){
        tv_sw1_crt_grp = (TextView) view.findViewById(R.id.tv_sw1_crt_grp);
        tv_nofrnd_crt_grp = (TextView) view.findViewById(R.id.tv_nofrnd_crt_grp);

        et_search_crt_grp = (EditText) view.findViewById(R.id.et_search_crt_grp);

        iv_search_crt_grp = (ImageView) view.findViewById(R.id.iv_search_crt_grp);
        iv_cancle_crt_grp = (ImageView) view.findViewById(R.id.iv_cancle_crt_grp);
        iv_create_crt_grp = (ImageView) view.findViewById(R.id.iv_create_crt_grp);
        iv_back_crtgrp = (ImageView) view.findViewById(R.id.iv_back_crtgrp);

        view_crtgrp = (View) view.findViewById(R.id.view_crtgrp);

        rv_selected_crt_grp = (RecyclerView) view.findViewById(R.id.rv_selected_crt_grp);
        linearLayoutManager1 = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        rv_selected_crt_grp.setLayoutManager(linearLayoutManager1);
        rv_selected_crt_grp.setItemAnimator(new DefaultItemAnimator());

        rv_all_crt_grp = (RecyclerView) view.findViewById(R.id.rv_all_crt_grp);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_all_crt_grp.setLayoutManager(linearLayoutManager);
        rv_all_crt_grp.setItemAnimator(new DefaultItemAnimator());
        friendList();
        edit_Text_Focus_Listner();
    }

    public void clickistner(){
        iv_cancle_crt_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_crt_grp.setText("");
            }
        });
        iv_back_crtgrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager.popBackStack();
            }
        });
        iv_create_crt_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (CreateGroupFriendsAdapter.selectedContactList.size() > 0){
                        ArrayList<FriendListGS> sendList = new ArrayList<>();
                        sendList = CreateGroupFriendsAdapter.selectedContactList;
                        Log.d(TAG+" size of send list ", sendList.size()+" ");
                        Profile.changeProfileFragment(new CreateGrpSubject(mainActivity, sendList, friendList.size()), "cre");
                    }else {
                        Toast.makeText(mainActivity, "Select Participants of group", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    public void edit_Text_Focus_Listner() {
        et_search_crt_grp.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_search_crt_grp.getText().toString().length() > 0){
                    ArrayList<FriendListGS> searchedlist = new ArrayList<>();
                    for (int i =0 ; i < friendList.size(); i++){
                        String s = et_search_crt_grp.getText().toString();
                        boolean a = Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(friendList.get(i).getName()).find();

                        if (a){
                            FriendListGS friendListGS = new FriendListGS();
                            friendListGS.setUserId(friendList.get(i).getUserId());
                            friendListGS.setName(friendList.get(i).getName());
                            friendListGS.setImage(friendList.get(i).getImage());
                            searchedlist.add(friendListGS);
                        }

                    }
                    CreateGroupFriendsAdapter createGroupFriendsAdapter = new CreateGroupFriendsAdapter(mainActivity, searchedlist, rv_selected_crt_grp);
                    rv_all_crt_grp.setAdapter(createGroupFriendsAdapter);
                    }else {
                    CreateGroupFriendsAdapter createGroupFriendsAdapter = new CreateGroupFriendsAdapter(mainActivity, friendList, rv_selected_crt_grp);
                    rv_all_crt_grp.setAdapter(createGroupFriendsAdapter);
                }

                    //  search_result(jsonObject.toString());
                }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    public void friendList(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", preferenceManager.getKeyUserId());
        Log.d(TAG+" params friend list ", jsonObject+" ");
        if (Network.isConnected(mainActivity)){
            progress.show();
            Network.hitPostApiWithAuth(mainActivity,jsonObject, this, NetworkConstants.friendList, 2);
        }else {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response friends list ", data+" ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    Log.d(TAG+" reponse friend list", jsonObject+" ");
                    message = returnEmptyString(jsonObject.get("message"));
                    if (message.contains("Successfull")){
                        JsonArray result = jsonObject.getAsJsonArray("result");
                        JsonObject json = result.get(0).getAsJsonObject();
                        JsonArray friends = json.getAsJsonArray("userFriends");
                        if (friends.size() > 0){
                            tv_nofrnd_crt_grp.setVisibility(View.GONE);
                            friendList = new ArrayList<FriendListGS>();
                            for (int i = 0; i < friends.size(); i++){
                                JsonObject jso = friends.get(i).getAsJsonObject();
                                FriendListGS friendListGS = new FriendListGS();
                                friendListGS.setUserId(jso.get("user_id")+"");
                                friendListGS.setEmail(jso.get("email")+"");
                                friendListGS.setName(jso.get("name")+"");
                                friendListGS.setLocation(jso.get("location")+"");
                                friendListGS.setImage(jso.get("image")+"");
                                friendListGS.setGender(jso.get("gender")+"");
                                friendListGS.setAge(jso.get("age")+"");
                                friendListGS.setRace(jso.get("race")+"");
                                friendListGS.setStatus(false);
                                friendList.add(friendListGS);
                            }
                            createGroupFriendsAdapter = new CreateGroupFriendsAdapter(mainActivity, friendList, rv_selected_crt_grp);
                            rv_all_crt_grp.setAdapter(createGroupFriendsAdapter);
                            createGroupFriendsAdapter.notifyDataSetChanged();
                        }else {
                           // rv_frndlist_frndlist.setVisibility(View.GONE);
                            tv_nofrnd_crt_grp.setVisibility(View.VISIBLE);
                        }
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

    @Override
    public void onStop() {
        super.onStop();
      //  Toast.makeText(mainActivitySignUP, "on Stop Called", Toast.LENGTH_SHORT).show();
        // i have to clear static data here when user going outside the app

      //  CreateGroupFriendsAdapter.contactList.clear();
      //  CreateGroupFriendsAdapter.selectedContactList.clear();
    }
}
