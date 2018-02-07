package app.com.perfec10.fragment.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.model.FbFrndsGS;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;
import de.hdodenhof.circleimageview.CircleImageView;

import static app.com.perfec10.fragment.profile.EditProfile.social_id;
import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 30/11/17.
 */

@SuppressLint("ValidFragment")
public class FriendProfile extends Fragment implements NetworkCallBack{
    private MainActivity mainActivity;
    private String userId;
    private ImageView iv_back_oth_pro, iv_backimg_oth_pro, iv_delete_frnd;
    private TextView tv_namehead_oth_pro, tv_sw1_oth_pro, tv_sw2_oth_pro,  tv_sw4_oth_pro
            ,tv_sw5_oth_pro, tv_sw6_oth_pro, tv_sw7_oth_pro, tv_sw8_oth_pro, tv_email_oth_pro,
            tv_race_oth_pro, tv_gender_oth_pro, tv_age_oth_pro, tv_location_oth_pro,
            tv_frndno_oth_pro, tv_postno_oth_pro, tv_sw9_oth_pro, tv_name_oth_pro;
    private CircleImageView civ_oth_pro;
    private Typeface regular, bold;
    private Progress progress;
    private ImageLoader imageLoader;
    private String TAG = "FriendProfile";
    private String nAme, socialId, imgUrl;
    private ArrayList<FbFrndsGS> fBfiendList = new ArrayList<>();
    private ArrayList<FriendListGS> fiendList = new ArrayList<>();
    private PreferenceManager preferenceManager;

    public FriendProfile(MainActivity mainActivity, String userId){
        this.mainActivity = mainActivity;
        this.userId = userId;
        Profile.calledFrom = "1";
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        preferenceManager = new PreferenceManager(mainActivity);
    }

    public FriendProfile(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_profile, container, false);
        initView((view));
        clickListner();
        return view;
    }

    public void initView(View v){
        tv_sw1_oth_pro = (TextView) v.findViewById(R.id.tv_sw1_oth_pro);
        tv_sw2_oth_pro = (TextView) v.findViewById(R.id.tv_sw2_oth_pro);
        tv_sw4_oth_pro = (TextView) v.findViewById(R.id.tv_sw4_oth_pro);
        tv_sw5_oth_pro = (TextView) v.findViewById(R.id.tv_sw5_oth_pro);
        tv_sw6_oth_pro = (TextView) v.findViewById(R.id.tv_sw6_oth_pro);
        tv_sw7_oth_pro = (TextView) v.findViewById(R.id.tv_sw7_oth_pro);
        tv_sw8_oth_pro = (TextView) v.findViewById(R.id.tv_sw8_oth_pro);
        tv_sw9_oth_pro = (TextView) v.findViewById(R.id.tv_sw9_oth_pro);
        tv_namehead_oth_pro = (TextView) v.findViewById(R.id.tv_namehead_oth_pro);
        tv_email_oth_pro = (TextView) v.findViewById(R.id.tv_email_oth_pro);
        tv_race_oth_pro = (TextView) v.findViewById(R.id.tv_race_oth_pro);
        tv_gender_oth_pro = (TextView) v.findViewById(R.id.tv_gender_oth_pro);
        tv_age_oth_pro = (TextView) v.findViewById(R.id.tv_age_oth_pro);
        tv_location_oth_pro = (TextView) v.findViewById(R.id.tv_location_oth_pro);
        tv_frndno_oth_pro = (TextView) v.findViewById(R.id.tv_frndno_oth_pro);
        tv_postno_oth_pro = (TextView) v.findViewById(R.id.tv_postno_oth_pro);
        tv_name_oth_pro = (TextView) v.findViewById(R.id.tv_name_oth_pro);

        iv_back_oth_pro = (ImageView) v.findViewById(R.id.iv_back_oth_pro);
        iv_backimg_oth_pro = (ImageView) v.findViewById(R.id.iv_backimg_oth_pro);
        iv_delete_frnd = (ImageView) v.findViewById(R.id.iv_delete_frnd);

        civ_oth_pro = (CircleImageView) v.findViewById(R.id.civ_oth_pro);

        tv_sw1_oth_pro.setTypeface(regular);
        tv_sw2_oth_pro.setTypeface(regular);
        tv_sw4_oth_pro.setTypeface(regular);
        tv_sw5_oth_pro.setTypeface(regular);
        tv_sw6_oth_pro.setTypeface(regular);
        tv_sw7_oth_pro.setTypeface(regular);
        tv_sw8_oth_pro.setTypeface(regular);
        tv_sw9_oth_pro.setTypeface(regular);
        tv_namehead_oth_pro.setTypeface(bold);
        tv_name_oth_pro.setTypeface(regular);
        tv_email_oth_pro.setTypeface(regular);
        tv_race_oth_pro.setTypeface(regular);
        tv_gender_oth_pro.setTypeface(regular);
        tv_age_oth_pro.setTypeface(regular);
        tv_location_oth_pro.setTypeface(regular);
        tv_frndno_oth_pro.setTypeface(bold);
        tv_postno_oth_pro.setTypeface(bold);

        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("user_id", userId);

        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.d(TAG+" params of frnd profile", jsonObject+" ");
        progress.show();
        if (Network.isConnected(mainActivity)){
            Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.getUserDetail, 1);
        }else {
            progress.dismiss();
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickListner(){
        iv_back_oth_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager.popBackStack();
            }
        });
        iv_delete_frnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
                alertDialog.setTitle("Confirm Delete...");
                alertDialog.setMessage("Are you sure you want to Remove "+tv_namehead_oth_pro.getText().toString()+" ?");

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //  Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                        deleteFrnd();
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
            }
        });
    }

    public void deleteFrnd(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userId);
        Log.d(TAG+" paramss delete frnd", jsonObject+" ");
        if (Network.isConnected(mainActivity)){
            progress.show();
            Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.deleteFriend,2);
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response ", data+" ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {

                    social_id.clear();

                    if (requestCode==1){
                        Log.d(TAG+" reponse profile", jsonObject+" ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.equals("Successfull")){
                            JsonArray result = jsonObject.getAsJsonArray("result");
                            JsonObject json = result.get(0).getAsJsonObject();
                            Log.d(TAG+" email", json.get("email").getAsString());
                            //String name = json.get("name");
                            tv_email_oth_pro.setText(json.get("email").getAsString());
                            String name = json.get("name").getAsString();
                            name = name.replace("\"","");
                            nAme = name;
                            socialId = json.get("social_id")+"";
                            socialId = socialId.replace("\"","");
                            imgUrl = json.get("image")+"";
                            imgUrl = imgUrl.replace("\"", "");

                            Log.d(TAG+" name ", name);
                            if (!name.equalsIgnoreCase("null")){
                                tv_namehead_oth_pro.setText(name);
                                tv_name_oth_pro.setText(name);
                            }else {
                                tv_namehead_oth_pro.setText(" ");
                            }
                            String location = json.get("location")+"";
                            location = location.replace("\"","");
                            Log.d(TAG+" location ", location+" ");
                            if (!location.equalsIgnoreCase("null")){
                                tv_location_oth_pro.setText(location);
                            }else {
                                tv_location_oth_pro.setText("");
                            }
                            String age = json.get("age")+"";
                            age = age.replace("\"","");
                            if (!age.equalsIgnoreCase("null")){
                                tv_age_oth_pro.setText(age);
                            }else {
                                tv_age_oth_pro.setText("");
                            }

                            String gender = json.get("gender")+"";
                            gender = gender.replace("\"","");
                            if (!gender.equalsIgnoreCase("null")){
                                tv_gender_oth_pro.setText(gender);
                            }else {
                                tv_gender_oth_pro.setText("");
                            }
                            String race = json.get("race")+"";
                            race = race.replace("\"","");
                            if (!race.equalsIgnoreCase("null")){
                                tv_race_oth_pro.setText(race);
                            }else {
                                tv_race_oth_pro.setText("");
                            }

                            JsonArray friends = json.getAsJsonArray("friends");
                            tv_frndno_oth_pro.setText(friends.size()+"");
                           // JsonArray posts = json.getAsJsonArray("totalPosts");
                            tv_postno_oth_pro.setText(json.get("totalPosts").getAsString());
                            String image = json.get("image").getAsString();
                            Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+image).into(iv_backimg_oth_pro);

                            Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+image).into(civ_oth_pro);
                        }
                    }else if (requestCode==2){
                        Log.d(TAG+" delete frnd ", data+" ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.contains("Sucessful")){
                            if (socialId.equals("null")){

                            }else {
                                fBfiendList = preferenceManager.getFbFrnds(mainActivity);
                                FbFrndsGS fbFrndsGS = new FbFrndsGS();
                                fbFrndsGS.setId(socialId);
                                fbFrndsGS.setName(nAme);
                                fbFrndsGS.setStatus("0");
                                fbFrndsGS.setImage(imgUrl);
                                fBfiendList.add(fbFrndsGS);
                                preferenceManager.saveFbFrnds(mainActivity, fBfiendList);
                            }
                            Toast.makeText(mainActivity, "Friend Removed Successfully ", Toast.LENGTH_SHORT).show();
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
