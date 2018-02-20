package app.com.perfec10.fragment.profile;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;
import de.hdodenhof.circleimageview.CircleImageView;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 20/11/17.
 */

@SuppressLint("ValidFragment")
public class MyProfile extends Fragment implements NetworkCallBack{
    private MainActivity mainActivity;
    private TextView tv_namehead_profile, tv_sw1_profile, tv_sw2_profile, tv_sw3_profile, tv_sw4_profile,
            tv_sw5_profile, tv_sw6_profile, tv_sw7_profile, tv_sw8_profile, tv_sw9_profile, tv_sw10_profile,
            tv_postno_profile, tv_frndno_profile, tv_name_profile, tv_location_profile, tv_age_profile,
            tv_gender_profile, tv_race_profile, tv_email_profile, tv_sw11_profile;
    private LinearLayout ll_posts_profile, ll_frndtop_profile, ll_myprofile_profile, ll_frnds_profile,
            ll_setting_profile;
    private ImageView iv_edit_profile, iv_back_profile;
    private Typeface regular, bold;
    private PreferenceManager preferenceManager;
    private Progress progress;
    private CircleImageView civ_profile;
    private RelativeLayout rl_back_profile;
    private ImageLoader imageLoader;
    private String imageUrl;
    private boolean isVisible = false;
    private String TAG = "MyProfile";

    public MyProfile(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        preferenceManager = new PreferenceManager(mainActivity);
        Log.d(TAG+" access token ", preferenceManager.getUserAuthkey()+" ");
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public MyProfile(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser){
            isVisible = true;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile, container, false);
        initview(view);
        clickListner();
        Profile.calledFrom = "0";
        return view;
    }

    public void initview(View view){
        civ_profile = (CircleImageView) view.findViewById(R.id.civ_profile);

        tv_namehead_profile = (TextView) view.findViewById(R.id.tv_namehead_profile);
        tv_sw1_profile = (TextView) view.findViewById(R.id.tv_sw1_profile);
        tv_sw2_profile = (TextView) view.findViewById(R.id.tv_sw2_profile);
        tv_sw3_profile = (TextView) view.findViewById(R.id.tv_sw3_profile);
        tv_sw4_profile = (TextView) view.findViewById(R.id.tv_sw4_profile);
        tv_sw5_profile = (TextView) view.findViewById(R.id.tv_sw5_profile);
        tv_sw6_profile = (TextView) view.findViewById(R.id.tv_sw6_profile);
        tv_sw7_profile = (TextView) view.findViewById(R.id.tv_sw7_profile);
        tv_sw8_profile = (TextView) view.findViewById(R.id.tv_sw8_profile);
        tv_sw9_profile = (TextView) view.findViewById(R.id.tv_sw9_profile);
        tv_sw10_profile = (TextView) view.findViewById(R.id.tv_sw10_profile);
        tv_sw11_profile = (TextView) view.findViewById(R.id.tv_sw11_profile);
        tv_postno_profile = (TextView) view.findViewById(R.id.tv_postno_profile);
        tv_frndno_profile = (TextView) view.findViewById(R.id.tv_frndno_profile);
        tv_name_profile = (TextView) view.findViewById(R.id.tv_name_profile);
        tv_location_profile = (TextView) view.findViewById(R.id.tv_location_profile);
        tv_age_profile = (TextView) view.findViewById(R.id.tv_age_profile);
        tv_gender_profile = (TextView) view.findViewById(R.id.tv_gender_profile);
        tv_race_profile = (TextView) view.findViewById(R.id.tv_race_profile);
        tv_email_profile = (TextView) view.findViewById(R.id.tv_email_profile);

        ll_posts_profile = (LinearLayout) view.findViewById(R.id.ll_posts_profile);
        ll_frndtop_profile = (LinearLayout) view.findViewById(R.id.ll_frndtop_profile);
        ll_myprofile_profile = (LinearLayout) view.findViewById(R.id.ll_myprofile_profile);
        ll_frnds_profile = (LinearLayout) view.findViewById(R.id.ll_frnds_profile);
        ll_setting_profile = (LinearLayout) view.findViewById(R.id.ll_setting_profile);
        rl_back_profile = (RelativeLayout) view.findViewById(R.id.rl_back_profile);

        iv_edit_profile = (ImageView) view.findViewById(R.id.iv_edit_profile);
        iv_back_profile = (ImageView) view.findViewById(R.id.iv_back_profile);

        tv_namehead_profile.setTypeface(regular);
        tv_sw1_profile.setTypeface(regular);
        tv_sw2_profile.setTypeface(regular);
        tv_sw3_profile.setTypeface(regular);
        tv_sw4_profile.setTypeface(regular);
        tv_sw5_profile.setTypeface(regular);
        tv_sw6_profile.setTypeface(regular);
        tv_sw7_profile.setTypeface(regular);
        tv_sw8_profile.setTypeface(regular);
        tv_sw9_profile.setTypeface(regular);
        tv_sw10_profile.setTypeface(regular);
        tv_sw11_profile.setTypeface(regular);
        tv_postno_profile.setTypeface(bold);
        tv_frndno_profile.setTypeface(bold);
        tv_name_profile.setTypeface(regular);
        tv_age_profile.setTypeface(regular);
        tv_gender_profile.setTypeface(regular);
        tv_race_profile.setTypeface(regular);
        tv_email_profile.setTypeface(regular);

        imageLoader = CustomVolleyRequestQueue.getInstance(mainActivity).getImageLoader();

        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("user_id", preferenceManager.getKeyUserId());

        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.d(TAG+" params of profile ", jsonObject+" ");

        if (Network.isConnected(mainActivity)){
            if (!Profile.logout.equals("1")){
                progress.show();
                Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.getUserDetail, NetworkConstants.requestCodeSignup);
            }
        }else {
            progress.dismiss();
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
       // iv_back_profile.setBackgroundResource(R.mipmap.user);
    }

    public void clickListner(){
        ll_posts_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ll_frndtop_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Profile.changeProfileFragment(new FriendsList(mainActivitySignUP), "stats");
            }
        });
        ll_myprofile_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ll_frnds_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                Profile.fragmentManager.beginTransaction().replace(R.id.profile_fram, new FriendsList(mainActivity)).addToBackStack("friends").commit();
            }
        });

        ll_setting_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                Profile.fragmentManager.beginTransaction().replace(R.id.profile_fram, new Settings(mainActivity)).addToBackStack("settings").commit();
            }
        });
        iv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // calling edit
                String post = tv_postno_profile.getText().toString();
                String frnd = tv_frndno_profile.getText().toString();
                String name = tv_name_profile.getText().toString();
                String locat = tv_location_profile.getText().toString();
                String age = tv_age_profile.getText().toString();
                String gender = tv_gender_profile.getText().toString();
                String race = tv_race_profile.getText().toString();
                Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                Profile.fragmentManager.beginTransaction().replace(R.id.profile_fram,
                new EditProfile(mainActivity, post, frnd, name, locat, age, gender, race, imageUrl)).addToBackStack("edit").commit();
            }
        });
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response of profile ", data+" ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    Log.d(TAG+" reponse profile", jsonObject+" ");
                    message = returnEmptyString(jsonObject.get("message"));
                   if (message.equals("Successfull")){
                       JsonArray result = jsonObject.getAsJsonArray("result");
                       JsonObject json = result.get(0).getAsJsonObject();
                       Log.d(TAG+" email", " "+json.get("email").getAsString());
                       //String name = json.get("name");
                       tv_email_profile.setText(json.get("email").getAsString());
                       String name = json.get("name").getAsString();
                       if (!name.equalsIgnoreCase("null")){
                           tv_namehead_profile.setText(name);
                           tv_name_profile.setText(name);
                       }else {
                           tv_namehead_profile.setText(" ");
                           tv_name_profile.setText(" ");
                       }

                       String image = json.get("image")+"";
                       image = image.replace("\"", "");
                       //civ_profile.setImageURI(NetworkConstants.baseProfileUrl+image);
                       Log.d(TAG+" profile ", NetworkConstants.imageBaseUrl+image);
                       imageLoader.get(NetworkConstants.imageBaseUrl+image,
                               ImageLoader.getImageListener(civ_profile,
                               R.mipmap.user, R.mipmap.user));
                       //civ_profile.setImageUrl(NetworkConstants.imageBaseUrl+image, imageLoader);
                       imageUrl = NetworkConstants.imageBaseUrl+image;

                           Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+image).into(iv_back_profile);

                       Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+image).into(civ_profile);
                       String location = json.get("location")+"";
                       location = location.replace("\"","");
                       Log.d(TAG+" location ", location+" ");
                        if (!location.equalsIgnoreCase("null")){
                            tv_location_profile.setText(location);
                        }else {
                            tv_location_profile.setText("");
                        }
                        String age = json.get("age")+"";
                        age = age.replace("\"","");
                        if (!age.equalsIgnoreCase("null")){
                            tv_age_profile.setText(age);
                        }else {
                            tv_age_profile.setText("");
                        }

                       String gender = json.get("gender")+"";
                        gender = gender.replace("\"", "");
                        if (!gender.equalsIgnoreCase("null")){
                            if (gender.equals("0")){
                                tv_gender_profile.setText("Male");
                            }else if (gender.equals("1")){
                                tv_gender_profile.setText("Female");
                            }

                        }else {
                            tv_gender_profile.setText("");
                        }
                        String race = json.get("race")+"";
                        race = race.replace("\"", "");
                        if (!race.equalsIgnoreCase("null")){
                            tv_race_profile.setText(race);
                        }else {
                            tv_race_profile.setText("");
                        }
                        JsonArray friends = json.getAsJsonArray("friends");
                        Log.d(TAG+" friends ", friends+"");
                       tv_frndno_profile.setText(friends.size()+"");
                       JsonArray posts = json.getAsJsonArray("posts");
                       String totalPosts = json.get("totalPosts")+"";
                       totalPosts = totalPosts.replace("\"", "");
                       tv_postno_profile.setText(totalPosts);

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
      //  Log
    }
}
