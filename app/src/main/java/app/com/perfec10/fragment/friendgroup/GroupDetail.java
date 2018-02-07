package app.com.perfec10.fragment.friendgroup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.EditGroup;
import app.com.perfec10.fragment.friendgroup.adapter.ParticipantAdaptor;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.CircleImageView;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 30/11/17.
 */

@SuppressLint("ValidFragment")
public class GroupDetail extends Fragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private String groupId;
    private ImageView iv_back_grp, iv_edit_grp, iv_delete_grp, iv_add_contact, iv_backimg_grp;
    private TextView tv_name_grp, tv_nomember_grp;
    public static TextView tv_participant_grp;
    private RecyclerView rv_contacts_grp;
    private LinearLayoutManager linearLayoutManager;
    private Progress progress;
    private PreferenceManager preferenceManager;
    private Typeface regular, bold;
    private CircleImageView civ_grp;
    private ArrayList<FriendListGS> participants;
    private String imgUrl;
    private String TAG = "GroupDetail";

    public GroupDetail(MainActivity mainActivity, String groupId){
        this.mainActivity = mainActivity;
        this.groupId = groupId;
        Profile.calledFrom = "1";
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        Profile.calledFrom = "1";
   }

    public GroupDetail(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_detail, container, false);
        initView(view);
        clickListner();
        return view;
    }

    public void initView(View v){
        iv_back_grp = (ImageView) v.findViewById(R.id.iv_back_grp);
        iv_edit_grp = (ImageView) v.findViewById(R.id.iv_edit_grp);
        iv_delete_grp = (ImageView) v.findViewById(R.id.iv_delete_grp);
        iv_add_contact = (ImageView) v.findViewById(R.id.iv_add_contact);
        iv_backimg_grp = (ImageView) v.findViewById(R.id.iv_backimg_grp);

        tv_name_grp = (TextView) v.findViewById(R.id.tv_name_grp);
        tv_participant_grp = (TextView) v.findViewById(R.id.tv_participant_grp);
        tv_nomember_grp = (TextView) v.findViewById(R.id.tv_nomember_grp);

        civ_grp = (CircleImageView) v.findViewById(R.id.civ_grp);

        rv_contacts_grp = (RecyclerView) v.findViewById(R.id.rv_contacts_grp);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_contacts_grp.setLayoutManager(linearLayoutManager);
        groupDetail();
    }

    public void groupDetail(){
        if (Network.isConnected(mainActivity)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("group_id", groupId);
            Log.d(TAG+" params grp detail ", jsonObject+" ");
            progress.show();
            Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.groupDetail, 1);
        }else {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickListner(){
        iv_back_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager.popBackStack();
            }
        });
        iv_edit_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Profile.changeProfileFragment(new EditGroup(mainActivity, groupId, tv_name_grp.getText().toString(), imgUrl),"edit_group");
            }
        });
        iv_delete_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Network.isConnected(mainActivity)){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
                    alertDialog.setTitle("Confirm Delete...");
                    alertDialog.setMessage("Are you sure you want to delete the group ?");

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //  Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                            deleteGrp();
                        }
                    });
                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();
                }else {
                    Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.changeProfileFragment(new ContactList(mainActivity, groupId, participants), "contact");
            }
        });
    }

    public void deleteGrp(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("group_id", groupId);
        Log.d(TAG+" params del grp ", jsonObject+" ");
        if (Network.isConnected(mainActivity)){
            progress.show();
            Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.deleteGroup,2);
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response grp ", data+" ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode==1){
                        Log.d(TAG+" reponse profile", jsonObject+" ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.equals("Sucessfull.")){
                            JsonArray result = jsonObject.getAsJsonArray("result");
                            JsonObject json = result.get(0).getAsJsonObject();
                            String name = json.get("name")+"";
                            name = name.replace("\"", "");
                            tv_name_grp.setText(name);
                            String image = json.get("image")+"";
                            image = image.replace("\"","");
                            imgUrl = image;
                            Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+image).into(civ_grp);
                            Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+image).into(iv_backimg_grp);

                            JsonArray groupMembers = json.getAsJsonArray("groupMembers");
                            if (groupMembers.size() > 0){
                                participants = new ArrayList<>();
                                tv_participant_grp.setText("Participants : "+groupMembers.size());
                                for (int i = 0; i< groupMembers.size(); i++){
                                    JsonObject jso = groupMembers.get(i).getAsJsonObject();
                                    FriendListGS friendListGS = new FriendListGS();
                                    friendListGS.setUserId(jso.get("user_id")+"");
                                    friendListGS.setName(jso.get("name")+"");
                                    friendListGS.setImage(jso.get("image")+"");
                                    participants.add(friendListGS);
                                }
                                ParticipantAdaptor participantAdaptor = new ParticipantAdaptor(mainActivity, participants, groupId);
                                rv_contacts_grp.setAdapter(participantAdaptor);
                            }else {
                                tv_nomember_grp.setVisibility(View.VISIBLE);
                            }
                        }
                    }else if (requestCode==2){
                    Log.d(TAG+" response delete grp ", data+" ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.contains("successfully")){
                            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                            Profile.fragmentManager.popBackStack();
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
