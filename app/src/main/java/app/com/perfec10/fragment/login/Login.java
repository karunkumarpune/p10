package app.com.perfec10.fragment.login;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.app.Config;
import app.com.perfec10.fragment.home.Home;
import app.com.perfec10.helper.Validation;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;
import butterknife.BindView;
import butterknife.ButterKnife;

import static app.com.perfec10.activity.MainActivity.fragmentManager;
import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 25/10/17.
 */

@SuppressLint("ValidFragment")
public class Login extends Fragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private Typeface regular, bold;
    private PreferenceManager preferenceManager;
    private Progress progress;
    private String TAG = "Login";
    private ArrayList<FriendListGS> frndList;

    @BindView(R.id.tv_sw1_login) TextView tv_sw1_login;
    @BindView(R.id.tv_sw2_login) TextView tv_sw2_login;
    @BindView(R.id.tv_sign_login) TextView tv_sign_login;
    @BindView(R.id.tv_signup_login) TextView tv_signup_login;
    @BindView(R.id.tv_fb_login) TextView tv_fb_login;
    @BindView(R.id.tv_new_login) TextView tv_new_login;
    @BindView(R.id.et_email_login) EditText et_email_login;
    @BindView(R.id.et_password_login) EditText et_password_login;
    @BindView(R.id.ll_signup_login) LinearLayout ll_signup_login;

    public Login(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_new, container, false);
        ButterKnife.bind(this, view);
        initView();
        clickListner();
        return view;
    }

    public void initView(){
        tv_sw1_login.setTypeface(regular);
        tv_sw2_login.setTypeface(regular);
        tv_new_login.setTypeface(regular);
        tv_sign_login.setTypeface(bold);
        tv_fb_login.setTypeface(bold);
        tv_signup_login.setTypeface(bold);
        et_email_login.setTypeface(regular);
        et_password_login.setTypeface(regular);
    }

    public void clickListner(){
        tv_sign_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation.validEmail(et_email_login.getText().toString())){

                    String pass = et_password_login.getText().toString();
                    if (pass.length() > 0){
                        if (pass.length() >=8){
                            Log.d(TAG+" no of back ", MainActivity.fragmentManager.getBackStackEntryCount()+" ");

                            login(et_email_login.getText().toString(), et_password_login.getText().toString());
                               }else {
                            et_password_login.setError("Enter valid Password");
                        }

                    }else {
                        et_password_login.setError("Enter Password");
                    }

                }else {
                    et_email_login.setError("Enter Valid Email");
                }
            }
        });
        ll_signup_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.popBackStack();
                MainActivity.changeFragment(new SignUp(mainActivity), "signUp");
            }
        });
        tv_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loginFacebook();
            }
        });

    }

    public void login(String email, String password){
        if (Network.isConnected(mainActivity)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("latitude", "");
            jsonObject.addProperty("longitude", "");
            jsonObject.addProperty("device_type", "2");
            SharedPreferences pref = mainActivity.getSharedPreferences(Config.SHARED_PREF, 0);
            String regId = pref.getString("regId", null);
            jsonObject.addProperty("device_token",  regId);
            jsonObject.addProperty("password", password);
            Log.d(TAG+" params signup ", jsonObject+" ");
            progress.show();

            login_result(jsonObject.toString());
          //  Network.hitPostApi(mainActivity, jsonObject, this, NetworkConstants.loginUrl, NetworkConstants.requestCodeSignup);
        }else {
            Toast.makeText(mainActivity, "No Internt Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    public void login_result(final String jsonObject) {
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkConstants.loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG+" login_response", "" + response);
                        final JSONObject jsonObject = Model.getObject(response);
                        if (jsonObject != null) {
                            Log.d(TAG+" _response", "" + response);
                            if (jsonObject.has("message")) {
                                try {
                                    String msg = jsonObject.getString("message");
                                    if (msg.equals("User  login successfull.")){
                                        JSONArray result = Model.getArray(jsonObject, "result");
                                        Log.e(TAG+" result ", result+" ");
                                        JSONObject json = Model.getObject(result, 0);
                                        Log.e(TAG+ " json ", json+" ");
                                        preferenceManager.setUserAuthkey(Model.getString(json, "accessToken"));
                                        preferenceManager.setKeyUserId(Model.getString(json, "user_id"));
                                        preferenceManager.setKey_userEmailId(Model.getString(json, "email"));
                                        preferenceManager.setKey_userImg(Model.getString(json, "image"));
                                        preferenceManager.setKey_userName(Model.getString(json, "name"));
                                        preferenceManager.setKeyVerficationCode(Model.getString(json, "verification_code"));
                                        preferenceManager.setKey_cameraWalkthrough(Model.getString(json, "cameraWalkthrough"));
                                        preferenceManager.setKey_statsWalkthrough(Model.getString(json, "statsWalkthrough"));
                                        preferenceManager.setKey_shareWalkthrough(Model.getString(json, "shareWalkthrough"));
                                        preferenceManager.setKey_frndcount(Model.getString(json, "totalFriends"));
                                        String is_verified = Model.getString(json, "is_verified");
                                        preferenceManager.setKeyVerified(is_verified);
                                        String location = Model.getString(json, "isLocationAccessible");
                                        if (location.equals("1")){
                                            preferenceManager.setKey_location("yes");
                                        }else {
                                            preferenceManager.setKey_location("no");
                                        }
                                        String isFacebookLinked = Model.getString(json, "isFacebookLinked");
                                        if (isFacebookLinked.equals("1")){
                                            preferenceManager.setKey_linkedfb("link");
                                            preferenceManager.setKey_fromfb("0");
                                        }else {
                                            preferenceManager.setKey_linkedfb("notlink");
                                            preferenceManager.setKey_fromfb("0");
                                        }
                                        String notifiSelf = Model.getString(json, "isNotifiableForPersonalSnaps");
                                        if (notifiSelf.equals("1")){
                                            preferenceManager.setKey_notifiablePersonal("1");
                                        }else {
                                            preferenceManager.setKey_notifiablePersonal("0");
                                        }
                                        String notifiOther = Model.getString(json, "isNotifiableForOtherSnaps");
                                        if (notifiOther.equals("1")){
                                            preferenceManager.setKey_notifiableother("1");
                                        }else {
                                            preferenceManager.setKey_notifiableother("0");
                                        }
                                        JSONArray friends = json.getJSONArray("friends");
                                        if (friends.length() > 0){
                                            frndList = new ArrayList<>();
                                            for (int i = 0; i < friends.length(); i++){
                                                JSONObject jo = friends.getJSONObject(i);
                                                FriendListGS friendListGS = new FriendListGS();
                                                friendListGS.setUserId(Model.getString(jo, "user_id"));
                                                friendListGS.setName(Model.getString(jo, "name"));
                                                frndList.add(friendListGS);
                                            }
                                            preferenceManager.saveFrndList(mainActivity, frndList);

                                        }
                                        Log.e(TAG+" verified value ", is_verified);
                                        Log.d(TAG+" preference value frnd ", preferenceManager.getKey_frndcount());
                                        /*if (is_verified.equals("0")){
                                            Log.e("inside ", "if");
                                            MainActivity.clearBackStack();
                                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new VerificationScreen(mainActivity), "verification").commit();
                                        }else {*/
                                            Log.e(TAG+" inside ", "else");
                                            MainActivity.clearBackStack();
                                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivity), "home").commit();
                                      //  }
                                    }else if (msg.equalsIgnoreCase("User registration successfull.")){
                                        et_password_login.setText("");
                                        et_email_login.setText("");
                                        Toast.makeText(mainActivity, "User not Registered ", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
             //   Log.d("login" + ": ", "Error Response code: " + error.networkResponse.statusCode);
                VolleyLog.d("login", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d(TAG+" error ocurred", "TimeoutError");
                    //    Toast.makeText(mainActivity, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG+" error ocurred", "AuthFailureError");
                    //    Toast.makeText(mainActivity, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d(TAG+" error ocurred", "ServerError");
                    et_password_login.setText("");
                        Toast.makeText(mainActivity, "Please enter Valid Credentials.", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Log.d(TAG+" error ocurred", "NetworkError");
                    Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d(TAG+" error ocurred", "ParseError");
                    //    Toast.makeText(mainActivity, "Internet connection is ", Toast.LENGTH_LONG).show();
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

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


    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    Log.d(TAG+" reponse signup", jsonObject+" ");
                    message = returnEmptyString(jsonObject.get("message"));
                    Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                    if (message.equals("User  login successfull.")){
                        JsonArray result = jsonObject.getAsJsonArray("result");
                        JsonObject json = result.getAsJsonObject();
                        preferenceManager.setUserAuthkey(returnEmptyString(json.get("accessToken")));
                        preferenceManager.setKeyUserId(returnEmptyString(json.get("user_id")));
                        preferenceManager.setKey_userEmailId(returnEmptyString(json.get("email")));
                        preferenceManager.setKeyVerficationCode(returnEmptyString(json.get("verification_code")));
                        String is_verified = returnEmptyString(json.get("is_verified"));
                        /*if (is_verified.equals("0")){
                            MainActivity.clearBackStack();
                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new VerificationScreen(mainActivity), "verification").commit();
                        }else {*/
                            MainActivity.clearBackStack();
                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivity), "home").commit();

                       // }
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
        progress.dismiss();
        Log.d(TAG+" msg error ", msg);
    }
}
