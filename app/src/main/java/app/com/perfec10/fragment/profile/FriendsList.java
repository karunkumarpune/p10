package app.com.perfec10.fragment.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.AddFriend;
import app.com.perfec10.fragment.friendgroup.CreateGroup;
import app.com.perfec10.fragment.friendgroup.adapter.GroupFriendAdapter;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.fragment.profile.EditProfile.social_id;
import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 20/11/17.
 */

@SuppressLint("ValidFragment")
public class FriendsList extends Fragment implements NetworkCallBack{
    private MainActivity mainActivity;
    private TextView tv_sw1_frndlist, tv_sw2_frndlist, tv_sw3_frndlist, tv_sw4_frndlist, tv_sw5_frndlist,
            tv_sw6_frndlist, tv_addfrnd_frndlist, tv_group_frndlist, tv_frndlist_frndlist, tv_nogroup_frndlist,
            tv_nofrnd_frndlist, tv_sw7_frndlist, tv_invitefrnd_frndlist;
    private RecyclerView rv_grouplist_frndlist, rv_frndlist_frndlist;
    private LinearLayoutManager linearLayoutManager, linearLayoutManager1;
    private LinearLayout ll_myprofile_frndlist, ll_frnds_frndlist, ll_setting_profile;
    private Typeface regular, bold;
    private PreferenceManager preferenceManager;
    private Progress progress;
    private ArrayList<FriendListGS> friendList;
    private  GroupFriendAdapter groupFriendAdapter1;

    public static String social;
    private int frndNo;
    private String TAG = "FriendsList";

    public FriendsList(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public FriendsList(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_list, container, false);
        initView(view);
        clickListrner();
        return view;
    }

    public void initView(View view){
        tv_sw1_frndlist = (TextView) view.findViewById(R.id.tv_sw1_frndlist);
        tv_sw2_frndlist = (TextView) view.findViewById(R.id.tv_sw2_frndlist);
        tv_sw3_frndlist = (TextView) view.findViewById(R.id.tv_sw3_frndlist);
        tv_sw4_frndlist = (TextView) view.findViewById(R.id.tv_sw4_frndlist);
        tv_sw5_frndlist = (TextView) view.findViewById(R.id.tv_sw5_frndlist);
        tv_sw6_frndlist = (TextView) view.findViewById(R.id.tv_sw6_frndlist);
        tv_sw7_frndlist = (TextView) view.findViewById(R.id.tv_sw7_frndlist);
        tv_addfrnd_frndlist = (TextView) view.findViewById(R.id.tv_addfrnd_frndlist);
        tv_group_frndlist = (TextView) view.findViewById(R.id.tv_group_frndlist);
        tv_frndlist_frndlist = (TextView) view.findViewById(R.id.tv_frndlist_frndlist);
        tv_nofrnd_frndlist = (TextView) view.findViewById(R.id.tv_nofrnd_frndlist);
        tv_nogroup_frndlist = (TextView) view.findViewById(R.id.tv_nogroup_frndlist);
        tv_invitefrnd_frndlist = (TextView) view.findViewById(R.id.tv_invitefrnd_frndlist);

        tv_sw1_frndlist.setTypeface(regular);
        tv_sw2_frndlist.setTypeface(regular);
        tv_sw3_frndlist.setTypeface(regular);
        tv_sw4_frndlist.setTypeface(regular);
        tv_sw5_frndlist.setTypeface(regular);
        tv_sw6_frndlist.setTypeface(regular);
        tv_sw7_frndlist.setTypeface(regular);
        tv_addfrnd_frndlist.setTypeface(regular);
        tv_group_frndlist.setTypeface(regular);
        tv_frndlist_frndlist.setTypeface(regular);
        tv_nogroup_frndlist.setTypeface(regular);
        tv_nofrnd_frndlist.setTypeface(regular);
        tv_invitefrnd_frndlist.setTypeface(regular);

        rv_grouplist_frndlist = (RecyclerView) view.findViewById(R.id.rv_grouplist_frndlist);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_grouplist_frndlist.setLayoutManager(linearLayoutManager);
        /*GroupFriendAdapter groupFriendAdapter = new GroupFriendAdapter(mainActivitySignUP);
        rv_grouplist_frndlist.setAdapter(groupFriendAdapter);*/
        rv_frndlist_frndlist = (RecyclerView) view.findViewById(R.id.rv_frndlist_frndlist);
        linearLayoutManager1 = new LinearLayoutManager(mainActivity);
        rv_frndlist_frndlist.setLayoutManager(linearLayoutManager1);

        ll_myprofile_frndlist = (LinearLayout) view.findViewById(R.id.ll_myprofile_frndlist);
        ll_setting_profile = (LinearLayout) view.findViewById(R.id.ll_setting_profile);

        friendGroupList();
     }

    public void clickListrner(){
        tv_addfrnd_frndlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.changeProfileFragment(new AddFriend(mainActivity), "add_frnd");
            }
        });

        tv_group_frndlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (frndNo > 0){
                    Profile.changeProfileFragment(new CreateGroup(mainActivity), "create_group");
                }else {
                    Toast.makeText(mainActivity, "You have no friends right now.", Toast.LENGTH_LONG).show();
                }

            }
        });
        ll_myprofile_frndlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                Profile.fragmentManager.beginTransaction().replace(R.id.profile_fram, new MyProfile(mainActivity)).commit();
            }
        });
        ll_setting_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                Profile.fragmentManager.beginTransaction().replace(R.id.profile_fram, new Settings(mainActivity)).commit();
            }
        });
        tv_invitefrnd_frndlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String sAux = "\n"+preferenceManager.getKey_userName()+" has invited you to join Perfec10.\n" +
                            "\"What body proportions make it a Perfect 10?\n" +
                            "Snap , Analyze, Rate and Share!\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=copydata.cloneit&hl=en  \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });
    }

    public void friendGroupList(){
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
                    if (message.equals("Successfull")){
                        JsonArray result = jsonObject.getAsJsonArray("result");
                        JsonObject json = result.get(0).getAsJsonObject();
                        JsonArray friends = json.getAsJsonArray("userFriends");
                        if (friends.size() > 0){
                            frndNo = friends.size();
                            rv_frndlist_frndlist.setVisibility(View.VISIBLE);
                            tv_nofrnd_frndlist.setVisibility(View.GONE);
                            friendList = new ArrayList<FriendListGS>();
                            for (int i = 0; i < friends.size(); i++){
                                JsonObject jso = friends.get(i).getAsJsonObject();
                                FriendListGS friendListGS = new FriendListGS();
                                String s = "0";
                                /*if (jso.has("social_id")){
                                    if (!(jso.get("social_id") ==null)){
                                        s = jso.get("social_id").getAsString();
                                    }

                                }*/
                                
                                /*String social_ids;
                                if(s==null){
                                    social_ids="12";
                                }else {
                                    social_ids=s;
                                }*/
                                String social_ids = getDefaultJsonValue(jso.get("social_id"));
                                friendListGS.setUserId(jso.get("user_id")+"");
                                friendListGS.setEmail(jso.get("email")+"");
                                friendListGS.setName(jso.get("name")+"");
                                friendListGS.setLocation(jso.get("location")+"");
                                friendListGS.setImage(jso.get("image")+"");
                                friendListGS.setGender(jso.get("gender")+"");
                                friendListGS.setAge(jso.get("age")+"");
                                friendListGS.setRace(jso.get("race")+"");
                                friendList.add(friendListGS);
                                social_id.add(Long.valueOf(social_ids));
                            }
                            groupFriendAdapter1 = new GroupFriendAdapter(mainActivity, friendList, "friend");
                            rv_frndlist_frndlist.setAdapter(groupFriendAdapter1);
                            groupFriendAdapter1.notifyDataSetChanged();
                        }else {
                          //  preferenceManager.setKey_frndcount("0");
                            //rv_frndlist_frndlist.setVisibility(View.GONE);
                            tv_nofrnd_frndlist.setVisibility(View.VISIBLE);
                        }
                        JsonArray groups = json.getAsJsonArray("userGroups");
                        if (groups.size() > 0){
                            friendList = new ArrayList<>();
                            rv_grouplist_frndlist.setVisibility(View.VISIBLE);
                            tv_nogroup_frndlist.setVisibility(View.GONE);
                            for (int i = 0; i < groups.size(); i++){
                            JsonObject jso = groups.get(i).getAsJsonObject();
                            FriendListGS friendListGS = new FriendListGS();
                            Log.d(TAG+" from inside ", jso.get("group_id")+"");
                            friendListGS.setGroupId(jso.get("group_id")+"");
                            //friendListGS.setName(jso.get("name")+"");
                            friendListGS.setName(jso.get("name")+"");
                            friendListGS.setImage(jso.get("image")+"");
                            friendList.add(friendListGS);
                            }
                            Log.d(TAG+" name group ",friendList.get(0).getGroupId() );
                            GroupFriendAdapter groupFriendAdapter = new GroupFriendAdapter(mainActivity, friendList, "group");
                            rv_grouplist_frndlist.setAdapter(groupFriendAdapter);
                            groupFriendAdapter.notifyDataSetChanged();

                        }else {
                            rv_grouplist_frndlist.setVisibility(View.GONE);
                            tv_nogroup_frndlist.setVisibility(View.VISIBLE);
                        }
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

    private String getDefaultJsonValue(JsonElement element){
        if(element.isJsonNull())
            return "0";
        else
            return element.getAsString();
    }
}
