package app.com.perfec10.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import app.com.perfec10.R;
import app.com.perfec10.app.Config;
import app.com.perfec10.fragment.home.Home;
import app.com.perfec10.fragment.login.AddFriendAllowDialoge;
import app.com.perfec10.fragment.login.PreLogin;
import app.com.perfec10.fragment.measure.Stats;
import app.com.perfec10.fragment.self_snaps.SelfSnapDetail;
import app.com.perfec10.fragment.self_snaps.adapter.SelfSnapAdapter;
import app.com.perfec10.model.FbFrndsGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, NetworkCallBack {
    public static FragmentManager fragmentManager;
    private FrameLayout main_frame;
    private static MainActivity mainActivity;
    private PreferenceManager preferenceManager;
    private CallbackManager callbackManager;
    private Progress progress;
    private boolean sentToSettings = false;
    public static boolean save = false;
    private GoogleApiClient mGoogleApiClient;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private final static String[] permissionsRequired = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private SharedPreferences permissionStatus;
    private String TAG = "MainActivity";
    public String userRated = "";
    private ArrayList<FbFrndsGS> fbFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity = this;
        super.onCreate(savedInstanceState);
        //Facebook.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        preferenceManager = new PreferenceManager(mainActivity);
        main_frame = (FrameLayout) findViewById(R.id.main_frame);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        Log.d(TAG+" permissions ", preferenceManager.getKey_permissions()+" ");
        String s = preferenceManager.getKey_permissions();
        displayFirebaseRegId();
        if (s.length() > 0){
            if (s.equals("0")){
               // readPermissions();
                requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
        }else {
           // readPermissions();
            requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
        }

        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
            try {
                mGoogleApiClient = new GoogleApiClient
                        .Builder(mainActivity)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage(mainActivity, this)
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        facebookLogin();

        /*if (app.com.perfec10.fragment.profile.Settings.sw_fb_sett != null){
            if (app.com.perfec10.fragment.profile.Profile.logout.equals("0")){
                Toast.makeText(mainActivity, app.com.perfec10.fragment.profile.Profile.logout, Toast.LENGTH_SHORT).show();
                facebookLink();
            }else {
                Toast.makeText(mainActivity, app.com.perfec10.fragment.profile.Profile.logout, Toast.LENGTH_SHORT).show();
             //   facebookLogin();
            }

        }else {
            Toast.makeText(mainActivity, "jdfghdf", Toast.LENGTH_SHORT).show();
            facebookLogin();
        }*/

        String isVeriied = preferenceManager.getKeyVerified();
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        Log.d(TAG+" value of verified ", isVeriied+" ");

        if (isVeriied.equals("1")){// email is verified
            fragmentManager = mainActivity.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame, new Home(mainActivity)).commit();
        }else {
            fragmentManager = mainActivity.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame, new PreLogin(mainActivity)).commit();
           // fragmentManager.beginTransaction().replace(R.id.main_frame, new Home(mainActivity)).commit();
        }

    }

    public GoogleApiClient getGoogleApiClient(){

        return mGoogleApiClient;
    }

    public static void changeFragment(Fragment fragment, String fragmentName){
        try{
            fragmentManager = mainActivity.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame, fragment).addToBackStack(fragmentName).commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);
      //  FirebaseApp.initializeApp(getApplicationContext());
        //  Log.d("registar ID ", FirebaseInstanceId.getInstance().getToken()+" ");


    }


    @Override
    public void onBackPressed() {
        if (SelfSnapDetail.tv_tagged_eddetail != null){
            String tag = SelfSnapDetail.tv_tagged_eddetail.getText().toString();

            if (tag.equals(SelfSnapAdapter.tagPerson)){
                if (SelfSnapDetail.tv_location_eddetail.getText().toString().equals(SelfSnapAdapter.locations)){
                    if (SelfSnapDetail.tv_age_eddetail.getText().toString().equals(SelfSnapAdapter.age)){
                        if (SelfSnapDetail.tv_height_eddetail.getText().toString().equals(SelfSnapAdapter.height)){
                            if (SelfSnapDetail.tv_weight_eddetail.getText().toString().equals(SelfSnapAdapter.wieght)){
                                if (SelfSnapDetail.tv_race_eddetail.getText().toString().equals(SelfSnapAdapter.race)){
                                    SelfSnapDetail.change = false;
                                }else {
                                    SelfSnapDetail.change = true;
                                }
                            }else {
                                SelfSnapDetail.change = true;
                            }
                        }else {
                            SelfSnapDetail.change = true;
                        }
                    }else {
                        SelfSnapDetail.change = true;
                    }
                }else {
                    SelfSnapDetail.change = true;
                }
            }else {
                SelfSnapDetail.change = true;
            }
            if (SelfSnapDetail.change){
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mainActivity);
                // alertDialog.setTitle("Confirm Save...");
                alertDialog.setMessage("You have unsaved changes,if you navigate away from this screen all unsaved changes will lost.");

                alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //  Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();

                        dialog.cancel();
                    }
                });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Proceed Anyway", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SelfSnapDetail.change = false;
                        SelfSnapDetail.tv_tagged_eddetail = null;
                        dialog.cancel();
                        mainActivity.getSupportFragmentManager().popBackStack();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
        }else {
                super.onBackPressed();
            }

        }else if (Stats.notMineRating != null){
            Log.d(TAG+" else ", "back ");
            if (Stats.notMineRating.equals("1")){
                float a = Stats.rb_stats.getRating();
                Log.d(TAG+" back press rate ", a+" ");
                if (a > 0){
                    userRated = a+"";
                    /*getSupportFragmentManager().getFragments()
                            .get(getSupportFragmentManager().getBackStackEntryCount() - 1).onResume();
                    */

                    super.onBackPressed();
                }else {
                    Toast.makeText(mainActivity, "Please rate First ", Toast.LENGTH_SHORT).show();
                }
            }else {
                super.onBackPressed();
            }
        }else {
            super.onBackPressed();
        }
    }

    public static void clearBackStack() {
        FragmentManager fm = mainActivity.getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /*public void getFriendList(){
        response = fb.request("me/friends");
        JSONObject fbFriendObj = Model.getObject(response);
        JSONArray fbFriendArray = fbFriendObj.getJSONArray("data");
        *//*friendsName = new ArrayList<String>();
        friendsId = new ArrayList<String>();
        friendPic = new ArrayList<String>();
        int i;
        for(i=0;i<=fbFriendArray.length();i++){
            JSONObject friendIDName = fbFriendArray.getJSONObject(i);
            Iterator it = friendIDName.keys();
            while(it.hasNext()){
                String nameId = (String)it.next();
                String name = friendIDName.getString(nameId);
                if(nameId.equals("id")){
                    friendsId.add(name);
                    friendPic.add(graph_path + name + "/picture");
                }else if(nameId.equals("name")){
                    friendsName.add(name);
                }
            }
        }*//*
    }*/

    private void facebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e(TAG+" LoginActivity  ", response.toString());
                                Log.e(TAG+"LoginActivity Response ", object+" ");
                                try {
                                    AccessToken token = AccessToken.getCurrentAccessToken();
                                    String tokens =token.getToken();
                                    Log.d(TAG, "AccessToken22: "+token.getToken());
                                    String id = object.optString("id");
                                    String fName = object.optString("first_name");
                                    String lName = object.optString("last_name");
                                    String email = object.optString("email");
                                    String birthday = object.optString("birthday");
                                    JSONObject picture = object.getJSONObject("picture").getJSONObject("data");
                                    String pictureUrl = picture.getString("url");
                                   // JSONArray friends = object.

                                    if (app.com.perfec10.fragment.profile.Settings.sw_fb_sett != null){

                                        if (preferenceManager.getKey_userName().length() > 0){
                                            linkfb(id,tokens);
                                            preferenceManager.setKey_fb_accesstoken(id);
                                          //  Toast.makeText(mainActivity, "in linking ", Toast.LENGTH_SHORT).show();
                                        }else {
                                            if (email != null && email.length() > 0){
                                                if (Network.isConnected(mainActivity)){

                                                    preferenceManager.setKey_fb_accesstoken(id);

                                                    preferenceManager.setKey_fb_accessTokens(token.getToken());
                                                    preferenceManager.setKey_fb_userId(token.getUserId());

                                                    JsonObject jsonObject = new JsonObject();
                                                    jsonObject.addProperty("email", email);
                                                    jsonObject.addProperty("social_id", id);
                                                    jsonObject.addProperty("latitude", "");
                                                    jsonObject.addProperty("longitude", "");
                                                    jsonObject.addProperty("device_type", "2");
                                                    SharedPreferences pref = mainActivity.getSharedPreferences(Config.SHARED_PREF, 0);
                                                    String regId = pref.getString("regId", null);
                                                    jsonObject.addProperty("device_token",  regId);
                                                    jsonObject.addProperty("login_type", "1");// for social 1, email 0
                                                    jsonObject.addProperty("password", "");
                                                    jsonObject.addProperty("name", fName);
                                                    jsonObject.addProperty("image", pictureUrl);
                                                    Log.d(TAG+" params signup ", jsonObject+" ");

                                                    progress.show();
                                                    signup_result(jsonObject.toString());
                                                    //  Network.hitPostApi(mainActivity, jsonObject, this, NetworkConstants.signupUrl, NetworkConstants.requestCodeSignup);
                                                }else {
                                                    Toast.makeText(mainActivity, "No Internt Connection ", Toast.LENGTH_SHORT).show();
                                                }
                                            }else {
                                                Toast.makeText(mainActivity, "This account is not linked with any email", Toast.LENGTH_SHORT).show();
                                            }
                                           // Toast.makeText(mainActivity, " again in signup ", Toast.LENGTH_SHORT).show();
                                        }

                                    }else {
                                        if (email != null && email.length() > 0){
                                            if (Network.isConnected(mainActivity)){
                                                JsonObject jsonObject = new JsonObject();
                                                jsonObject.addProperty("email", email);
                                                jsonObject.addProperty("social_id", id);
                                                jsonObject.addProperty("latitude", "");
                                                jsonObject.addProperty("longitude", "");
                                                jsonObject.addProperty("device_type", "2");
                                                SharedPreferences pref = mainActivity.getSharedPreferences(Config.SHARED_PREF, 0);
                                                String regId = pref.getString("regId", null);
                                                jsonObject.addProperty("device_token",  regId);
                                                jsonObject.addProperty("login_type", "1");// for social 1, email 0
                                                jsonObject.addProperty("password", "");
                                                jsonObject.addProperty("name", fName);
                                                jsonObject.addProperty("image", pictureUrl);
                                                Log.d(TAG+" params signup ", jsonObject+" ");

                                                progress.show();
                                                signup_result(jsonObject.toString());
                                                //  Network.hitPostApi(mainActivity, jsonObject, this, NetworkConstants.signupUrl, NetworkConstants.requestCodeSignup);
                                            }else {
                                                Toast.makeText(mainActivity, "No Internt Connection ", Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Toast.makeText(mainActivity, "This account is not linked with any email", Toast.LENGTH_SHORT).show();
                                        }
                                    //    Toast.makeText(mainActivity, "in signup ", Toast.LENGTH_SHORT).show();
                                    }

                                    // Toast.makeText(MainActivity.this, "Welcome :" +fName+" "+lName+", Email :"+email+" Birthday :"+birthday+","+" image-url :"+pictureUrl, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
              /*  try {
                    AccessToken accesstoken = AccessToken.getCurrentAccessToken();
                    accesstoken.getToken();
                    Log.d(TAG, "AccessToken: "+accesstoken);
                    GraphRequest.newMyFriendsRequest(accesstoken,
                            new GraphRequest.GraphJSONArrayCallback() {
                                @Override
                                public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                                    System.out.println("jsonArray: " + jsonArray);
                                    System.out.println("GraphResponse: " + response);
                                    if (jsonArray.length() > 0){
                                        Log.d(TAG, "have fb frnds");
                                        fbFriends = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++){
                                            try {
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                String name = jsonObject.getString("name");
                                                String id = jsonObject.getString("id");
                                                String friendsProfilePicUrl="https://graph.facebook.com/"+id+"/picture?type=normal";
                                                FbFrndsGS fbFrndsGS = new FbFrndsGS();
                                                fbFrndsGS.setId(id);
                                                fbFrndsGS.setName(name);
                                                fbFrndsGS.setStatus("0");
                                                fbFrndsGS.setImage(friendsProfilePicUrl);
                                                fbFriends.add(fbFrndsGS);

                                                Log.d(TAG, "friends image "+friendsProfilePicUrl);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                       *//* Gson gson = new Gson();
                                        String fnd_list = gson.toJson(fbFriends);
                                        preferenceManager.setKey_frindsfb(fnd_list);*//*
                                      //  FbFrndsGS fbFrndsGS = new FbFrndsGS(fbFriends);
                                        preferenceManager.saveFbFrnds(MainActivity.this, fbFriends);
                                      //  FbFrndsGS fbFrndsGS1 = (FbFrndsGS) getParcelable("student");
                                    }
                                }
                            }).executeAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,picture.type(large),birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                if (app.com.perfec10.fragment.profile.Settings.sw_fb_sett != null){
                    if (app.com.perfec10.fragment.profile.Profile.logout.equals("0")){
                        app.com.perfec10.fragment.profile.Settings.sw_fb_sett.setChecked(false);
                        preferenceManager.setKey_linkedfb("notlink");
                        Toast.makeText(mainActivity, "Link Cancelled", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(mainActivity,"Login Cancelled", Toast.LENGTH_SHORT).show();

                    }

                }else {
                    Toast.makeText(mainActivity, "Login Cancelled", Toast.LENGTH_SHORT).show();
                }
              //  Toast.makeText(MainActivity.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, "Problem connecting to Facebook", Toast.LENGTH_SHORT).show();
            }

        });
    }


    public void linkfb(String socialId,String tokens){
        if (app.com.perfec10.fragment.profile.Settings.sw_fb_sett != null){
            if (Network.isConnected(mainActivity)){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("social_id", socialId);
                jsonObject.addProperty("facebookToken",tokens);
                Log.d(TAG+" params links ", jsonObject+" ");
                Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.linking, 1);
            }else {
                Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void loginFacebook() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday","user_friends", "read_friendlists"));
    }

    public void linkFacebook() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void signup_result(final String jsonObject) {
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkConstants.signupUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG+" signup_response", "" + response);
                        final JSONObject jsonObject = Model.getObject(response);
                        if (jsonObject != null) {
                            Log.d(TAG+"_response", "" + response);
                            if (jsonObject.has("message")) {
                                try {
                                    String msg = jsonObject.getString("message");
                                    if (msg.contains("successfull.")){
                                        JSONArray result = Model.getArray(jsonObject, "result");
                                        Log.e(TAG+" result ", result+" ");
                                        JSONObject json = Model.getObject(result, 0);
                                        Log.e(TAG+" json ", json+" ");
                                        preferenceManager.setUserAuthkey(Model.getString(json, "accessToken"));
                                        preferenceManager.setKeyUserId(Model.getString(json, "user_id"));
                                        preferenceManager.setKey_userEmailId(Model.getString(json, "email"));
                                        preferenceManager.setKey_userName(Model.getString(json, "name"));
                                        preferenceManager.setKey_userImg(Model.getString(json, "image"));
                                        preferenceManager.setKeyVerficationCode(Model.getString(json, "verification_code"));
                                        String is_verified = Model.getString(json, "is_verified");
                                        preferenceManager.setKey_cameraWalkthrough(Model.getString(json, "cameraWalkthrough"));
                                        preferenceManager.setKey_statsWalkthrough(Model.getString(json, "statsWalkthrough"));
                                        preferenceManager.setKey_shareWalkthrough(Model.getString(json, "shareWalkthrough"));
                                        preferenceManager.setKeyVerified(is_verified);
                                        Log.e(TAG+" verified value ", is_verified);
                                        preferenceManager.setKey_location("yes");
                                        preferenceManager.setKey_linkedfb("link");
                                        preferenceManager.setKey_fromfb("1");
                                        preferenceManager.setKey_notifiablePersonal("1");
                                        preferenceManager.setKey_notifiableother("1");
                                        progress.dismiss();
                                        String isLogin = Model.getString(json, "isLogin");
                                        MainActivity.clearBackStack();
                                        JSONArray friends = Model.getArray(json, "friends");
                                        ArrayList<FbFrndsGS> fbFrndsList = preferenceManager.getFbFrnds(mainActivity);
                                        if (fbFrndsList != null){
                                            for (int a = 0; a < fbFrndsList.size(); a++){
                                                String soId = fbFrndsList.get(a).getId();
                                                if (friends.length() > 0){
                                                    for (int i = 0; i < friends.length(); i++){
                                                        JSONObject jon = Model.getObject(friends, i);
                                                        String sId = Model.getString(jon, "social_id");

                                                        if (soId.equals(sId)){
                                                            fbFrndsList.remove(a);
                                                        }
                                                    }
                                                }
                                            }
                                            preferenceManager.saveFbFrnds(mainActivity, fbFrndsList);
                                        }


                                        if (isLogin.equals("1")){// user has loggedin before

                                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivity), "home").commit();
                                        }else {
                                            // AddAfterSignup is called only when loggedin from fb

                                            AddFriendAllowDialoge addFriendAllowDialoge = new AddFriendAllowDialoge(mainActivity , "fb");
                                            addFriendAllowDialoge.show(mainActivity.getSupportFragmentManager(), "fsdf");

                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                VolleyLog.d(TAG+"error ", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d(TAG+"error ocurred", "TimeoutError");
                    //    Toast.makeText(mainActivity, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG+"error ocurred", "AuthFailureError");
                    //    Toast.makeText(mainActivity, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d(TAG+"error ocurred", "ServerError");
                        Toast.makeText(getApplicationContext(), "ServerError", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Log.d(TAG+"error ocurred", "NetworkError");
                    Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d(TAG+"error ocurred", "ParseError");
                    //    Toast.makeText(mainActivity, "Internet connection is ", Toast.LENGTH_LONG).show();
                }
            }

        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

           /* {

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }*/

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jsonObject == null ? null : jsonObject.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonObject, "utf-8");
                    return null;
                }
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                1100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);
    }

    private void readPermissions() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
                    ) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(MainActivity.this, "your message here", Toast.LENGTH_SHORT).show();
                        closeAppOnDenyPermission();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Camera and Location", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(MainActivity.this, "your message here", Toast.LENGTH_SHORT).show();
                        closeAppOnDenyPermission();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {
        // txtPermissions.setText("We've got all permissions");
        // Toast.makeText(getBaseContext(), "We got All Permissions", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public static void requestPermissions(final @NonNull Activity activity,
                                          final @NonNull String[] permissions, final @IntRange(from = 0) int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity instanceof ActivityCompat.RequestPermissionsRequestCodeValidator) {
                ((ActivityCompat.RequestPermissionsRequestCodeValidator) activity)
                        .validateRequestPermissionsRequestCode(requestCode);
            }
            activity.requestPermissions(permissions, requestCode);
        } else if (activity instanceof ActivityCompat.OnRequestPermissionsResultCallback) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    final int[] grantResults = new int[permissions.length];

                    PackageManager packageManager = activity.getPackageManager();
                    String packageName = activity.getPackageName();

                    final int permissionCount = permissions.length;
                    for (int i = 0; i < permissionCount; i++) {
                        grantResults[i] = packageManager.checkPermission(
                                permissions[i], packageName);
                    }

                    ((ActivityCompat.OnRequestPermissionsResultCallback) activity).onRequestPermissionsResult(
                            requestCode, permissions, grantResults);
                }
            });
        }
    }


	
	@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CALLBACK_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
               /* if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {*/
                    Log.d(TAG+" grantResults", grantResults.length+" ");
                    Log.d(TAG+" permissions.length", permissions.length+" ");
                    int grand = 0;
                try {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        Log.d(TAG+" first ","permisssion");
                        grand++;
                    }else {
                        Log.d(TAG+ " fisrt ", "else");
                        Toast.makeText(MainActivity.this, "You have denied the permissions, the application may misbehave", Toast.LENGTH_LONG).show();
                        closeAppOnDenyPermission();
                    }
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED){
                        Log.d(TAG+" second ","permisssion");
                        grand++;
                    }else {
                        Log.d(TAG+ " second ", "else");
                        Toast.makeText(MainActivity.this, "You have denied the permissions, the application may misbehave", Toast.LENGTH_LONG).show();
                        closeAppOnDenyPermission();
                    }
                    if (grantResults[2] == PackageManager.PERMISSION_GRANTED){
                        Log.d(TAG+" third ","permisssion");
                        grand++;
                    }else {
                        Log.d(TAG+" third ", "else");
                        Toast.makeText(MainActivity.this, "You have denied the permissions, the application may misbehave", Toast.LENGTH_LONG).show();
                        closeAppOnDenyPermission();
                    }
                    if (grantResults[3] == PackageManager.PERMISSION_GRANTED){
                        Log.d(TAG+ " four ","permisssion");
                        grand++;
                    }else {
                        Log.d(TAG+" four ", "else");
                        Toast.makeText(MainActivity.this, "You have denied the permissions, the application may misbehave", Toast.LENGTH_LONG).show();
                        closeAppOnDenyPermission();
                    }
                    if (grantResults[4] == PackageManager.PERMISSION_GRANTED){
                        Log.d(TAG+" five ","permisssion");
                        grand++;
                    }else {
                        Log.d(TAG+" five ", "else");
                        Toast.makeText(MainActivity.this, "You have denied the permissions, the application may misbehave", Toast.LENGTH_LONG).show();
                        closeAppOnDenyPermission();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (grand==5) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        preferenceManager.setKey_permissions("1");

                    } else {
                        preferenceManager.setKey_permissions("0");
                        Toast.makeText(MainActivity.this, "You have denied the permissions, the application may misbehave", Toast.LENGTH_LONG).show();
                        closeAppOnDenyPermission();
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }
                return;


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
	
	private void closeAppOnDenyPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response linking ", data+" ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode == 1) {
                        message = returnEmptyString(jsonObject.get("message"));

                        if (message.equals("Your account linked sucessfully.")) {
                            Toast.makeText(mainActivity, "Congratulations! Your Perfec10 account is linked with Facebook",
                                    Toast.LENGTH_SHORT).show();
                            preferenceManager.setKey_linkedfb("link");
                            app.com.perfec10.fragment.profile.Settings.sw_fb_sett.setChecked(true);
                        }

                    }

                } catch (Exception e) {
                    Log.e(TAG+" Outcome", e.toString());
                }
                break;
            case 201:
                app.com.perfec10.fragment.profile.Settings.sw_fb_sett.setChecked(false);
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                preferenceManager.setKey_linkedfb("notlink");
                preferenceManager.saveFbFrnds(mainActivity, fbFriends);
                break;
            case 400:
                app.com.perfec10.fragment.profile.Settings.sw_fb_sett.setChecked(false);
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                preferenceManager.setKey_linkedfb("notlink");
                preferenceManager.saveFbFrnds(mainActivity, fbFriends);
                break;
            case 401:
                app.com.perfec10.fragment.profile.Settings.sw_fb_sett.setChecked(false);
                message = returnEmptyString(jsonObject.get("message"));
                //   showSessionDialog(message);
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                preferenceManager.setKey_linkedfb("notlink");
                preferenceManager.saveFbFrnds(mainActivity, fbFriends);
                break;
            case 403:
                app.com.perfec10.fragment.profile.Settings.sw_fb_sett.setChecked(false);
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                preferenceManager.setKey_linkedfb("notlink");
                preferenceManager.saveFbFrnds(mainActivity, fbFriends);
                break;
            case 500:
                app.com.perfec10.fragment.profile.Settings.sw_fb_sett.setChecked(false);
                Toast.makeText(mainActivity, "in 500", Toast.LENGTH_SHORT).show();
                preferenceManager.setKey_linkedfb("notlink");
                preferenceManager.saveFbFrnds(mainActivity, fbFriends);
                break;
            default:
                app.com.perfec10.fragment.profile.Settings.sw_fb_sett.setChecked(false);
                Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_SHORT).show();
                preferenceManager.setKey_linkedfb("notlink");
                preferenceManager.saveFbFrnds(mainActivity, fbFriends);
                break;
        }
    }

    @Override
    public void onError(String msg) {

    }
}


