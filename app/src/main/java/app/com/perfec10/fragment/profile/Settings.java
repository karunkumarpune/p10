package app.com.perfec10.fragment.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.login.AddFriendAllowDialoge;
import app.com.perfec10.fragment.login.PreLogin;
import app.com.perfec10.fragment.self_snaps.SelfSnapDetail;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 21/11/17.
 */

@SuppressLint("ValidFragment")
public class Settings extends Fragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private TextView tv_sw1_sett, tv_sw2_sett, tv_sw3_sett, tv_sw4_sett, tv_sw5_sett,
            tv_sw11_sett, tv_sw12_sett, tv_sw13_sett, tv_sw14_sett, tv_sw15_sett, tv_sw16_sett,
            tv_sw17_sett, tv_sw18_sett, tv_use_sett, tv_report_sett, tv_terms_sett, tv_logout_sett;
    private Switch sw_snap_sett, sw_snapfrnd_sett, sw_location_sett;
    private LinearLayout ll_myprofile_sett, ll_frnds_sett;
    private PreferenceManager preferenceManager;
    private Progress progress;
    public static Switch sw_fb_sett;
    private String TAG = "Settings";
    private String oldNotiSelf, oldNotiOther, oldLocation, oldFbLink;
    private View view;

    public Settings(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public Settings(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.settings, container, false);
            initView(view);
            clickListner();
        }

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        Log.d(TAG+" on resume ", "after link ");
        if (preferenceManager.getKey_linkedfb().equals("link")){
            sw_fb_sett.setChecked(true);
            Log.d(TAG+" on resume ", "after link true");
        }else {
            sw_fb_sett.setChecked(false);
            Log.d(TAG+" on resume ", "after link false");
        }
    }

    public void initView(View view){
       tv_sw1_sett = (TextView) view.findViewById(R.id.tv_sw1_sett);
       tv_sw2_sett = (TextView) view.findViewById(R.id.tv_sw2_sett);
       tv_sw3_sett = (TextView) view.findViewById(R.id.tv_sw3_sett);
       tv_sw4_sett = (TextView) view.findViewById(R.id.tv_sw4_sett);
       tv_sw5_sett = (TextView) view.findViewById(R.id.tv_sw5_sett);
       tv_sw11_sett = (TextView) view.findViewById(R.id.tv_sw11_sett);
       tv_sw12_sett = (TextView) view.findViewById(R.id.tv_sw12_sett);
       tv_sw13_sett = (TextView) view.findViewById(R.id.tv_sw13_sett);
       tv_sw14_sett = (TextView) view.findViewById(R.id.tv_sw14_sett);
       tv_sw15_sett = (TextView) view.findViewById(R.id.tv_sw15_sett);
       tv_sw16_sett = (TextView) view.findViewById(R.id.tv_sw16_sett);
       tv_sw17_sett = (TextView) view.findViewById(R.id.tv_sw17_sett);
       tv_sw18_sett = (TextView) view.findViewById(R.id.tv_sw18_sett);
       tv_use_sett = (TextView) view.findViewById(R.id.tv_use_sett);
       tv_report_sett = (TextView) view.findViewById(R.id.tv_report_sett);
       tv_terms_sett = (TextView) view.findViewById(R.id.tv_terms_sett);
       tv_logout_sett = (TextView) view.findViewById(R.id.tv_logout_sett);

       sw_snap_sett = (Switch) view.findViewById(R.id.sw_snap_sett);
       sw_snapfrnd_sett = (Switch) view.findViewById(R.id.sw_snapfrnd_sett);
       sw_location_sett = (Switch) view.findViewById(R.id.sw_location_sett);
       sw_fb_sett = (Switch) view.findViewById(R.id.sw_fb_sett);

       ll_myprofile_sett = (LinearLayout) view.findViewById(R.id.ll_myprofile_sett);
       ll_frnds_sett = (LinearLayout) view.findViewById(R.id.ll_frnds_sett);

       if (preferenceManager.getKey_location().equals("yes")){
           sw_location_sett.setChecked(true);
       }
       if (preferenceManager.getKey_fromfb().equals("1")){
           sw_fb_sett.setChecked(true);
           sw_fb_sett.setClickable(false);
       }
       if(preferenceManager.getKey_notifiablePersonal().equals("1")){
           sw_snap_sett.setChecked(true);
       }
       if (preferenceManager.getKey_notifiableother().equals("1")){
           sw_snapfrnd_sett.setChecked(true);
       }
       oldNotiSelf = preferenceManager.getKey_notifiablePersonal();
       oldNotiOther = preferenceManager.getKey_notifiableother();
       oldLocation = preferenceManager.getKey_location();
       oldFbLink = preferenceManager.getKey_linkedfb();
   }

    public void clickListner(){
        tv_logout_sett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
                alertDialog.setTitle("Confirm Logout...");
                alertDialog.setMessage("Are you sure you want to logout?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //  Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                        logout();

                        preferenceManager.clearPreferences();
                        Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                       for (int i= 0; i < Profile.fragmentManager.getBackStackEntryCount(); i++){
                           Profile.fragmentManager.popBackStack();
                       }
                        Profile.logout = "1";
                        SelfSnapDetail.change = false;
                        MainActivity.fragmentManager = mainActivity.getSupportFragmentManager();
                        MainActivity.fragmentManager.beginTransaction().replace(R.id.main_frame, new PreLogin(mainActivity), "loggin home").commit();
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
        ll_frnds_sett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                Profile.fragmentManager.beginTransaction().replace(R.id.profile_fram, new FriendsList(mainActivity)).commit();
            }
        });
        ll_myprofile_sett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager = mainActivity.getSupportFragmentManager();
                Profile.fragmentManager.beginTransaction().replace(R.id.profile_fram, new MyProfile(mainActivity)).commit();
            }
        });
        sw_location_sett.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    preferenceManager.setKey_location("yes");
                }else {
                    preferenceManager.setKey_location("no");
                    Toast.makeText(mainActivity, "App will not able to access location now", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sw_snap_sett.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    preferenceManager.setKey_notifiablePersonal("1");
                }else {
                    preferenceManager.setKey_notifiablePersonal("0");
                }
            }
        });
        sw_snapfrnd_sett.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    preferenceManager.setKey_notifiableother("1");
                }else {
                    preferenceManager.setKey_notifiableother("0");
                }
            }
        });
        tv_report_sett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ReportProblemDialoge reportProblemDialoge = new ReportProblemDialoge(mainActivity );
                reportProblemDialoge.show(mainActivity.getSupportFragmentManager(), "fsdf");
            }
        });
        sw_fb_sett.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (sw_fb_sett.isPressed()){

                        if (b){

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
                            alertDialog.setMessage("Are you sure you want to link your account");
                            alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    preferenceManager.setKey_linkedfb("link");
                                    ((MainActivity)getActivity()).linkFacebook();

                                }
                            });

                            alertDialog.show();

                        }else {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
                            alertDialog.setMessage("Are you sure you want to delink your account");
                            alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dilink();

                                }
                            });
                            // Showing Alert Message
                            alertDialog.show();

                        }

                }

            }
        });

    }

    public void dilink(){
        if (Network.isConnected(mainActivity)){
            JsonObject jsonObject = new JsonObject();
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.unLink, 2);
        }else {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void logout(){
        if (Network.isConnected(mainActivity)){
            JsonObject jsonObject = new JsonObject();
            progress.show();
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.logout, 1);
        }else {
            Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {

        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode==1){
                        Log.d(TAG+" reponse Logout ", data+" ");
                        message = returnEmptyString(jsonObject.get("message"));

                        if (message.equals("Logout successfully")) {
                            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                        }

                    }else if (requestCode==2){
                        Log.d(TAG+" response unlink ", data+" ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.equals("Your account Delinked sucessfully.")){
                            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                            preferenceManager.setKey_linkedfb("notlink");
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
                //   showSessionDialog(message);
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 403:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 500:
                Toast.makeText(mainActivity, "in 500", Toast.LENGTH_SHORT).show();
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
