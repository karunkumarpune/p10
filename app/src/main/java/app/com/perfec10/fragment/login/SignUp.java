package app.com.perfec10.fragment.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.app.Config;
import app.com.perfec10.helper.Validation;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 27/10/17.
 */

@SuppressLint("ValidFragment")
public class SignUp extends Fragment implements NetworkCallBack{
    private MainActivity mainActivity;

    private TextView tv_sw1_signup, tv_sw2_signup, tv_sw3_signup, tv_sw4_signup, tv_login_signup, tv_sigup_signup, tv_fb_signup,
            tv_already_signup;
    private EditText et_email_signup, et_password_signup, et_confpass_signup, et_name_signup;
    private LinearLayout ll_login_signup;
    private Typeface regular, bold;
    private PreferenceManager preferenceManager;
    private Progress progress;
    private String TAG = "SignUp";

    public SignUp(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public SignUp(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_screen, container, false);
        initView(view);
        clickListner();
        return view;
    }

    public void initView(View view){
        tv_sw1_signup = (TextView) view.findViewById(R.id.tv_sw1_signup);
        tv_sw2_signup = (TextView) view.findViewById(R.id.tv_sw2_signup);
        tv_sw3_signup = (TextView) view.findViewById(R.id.tv_sw3_signup);
        tv_sw4_signup = (TextView) view.findViewById(R.id.tv_sw4_signup);
        tv_login_signup = (TextView) view.findViewById(R.id.tv_login_signup);
        tv_sigup_signup = (TextView) view.findViewById(R.id.tv_sigup_signup);
        tv_fb_signup = (TextView) view.findViewById(R.id.tv_fb_signup);
        tv_already_signup = (TextView) view.findViewById(R.id.tv_already_signup);
        et_email_signup = (EditText) view.findViewById(R.id.et_email_signup);
        et_password_signup = (EditText) view.findViewById(R.id.et_password_signup);
        et_confpass_signup = (EditText) view.findViewById(R.id.et_confpass_signup);
        et_name_signup = (EditText) view.findViewById(R.id.et_name_signup);
        ll_login_signup = (LinearLayout) view.findViewById(R.id.ll_login_signup);


        tv_sw1_signup.setTypeface(regular);
        tv_sw2_signup.setTypeface(regular);
        tv_sw3_signup.setTypeface(regular);
        tv_sw4_signup.setTypeface(regular);
        tv_already_signup.setTypeface(regular);
        et_email_signup.setTypeface(regular);
        et_password_signup.setTypeface(regular);
        et_confpass_signup.setTypeface(regular);
        et_name_signup.setTypeface(regular);
        tv_sigup_signup.setTypeface(bold);
        tv_fb_signup.setTypeface(bold);
        tv_login_signup.setTypeface(bold);
    }

    public void clickListner(){
        tv_sigup_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation.validEmail(et_email_signup.getText().toString())){
                    if (et_name_signup.getText().toString().length() > 0){
                    if (et_password_signup.getText().toString().length()!=0){
                        if (et_password_signup.getText().toString().length()>=8){
                            if (et_confpass_signup.getText().toString().length() != 0){
                                if ((et_password_signup.getText().toString()).equals(et_confpass_signup.getText().toString())){
                                    signUp(et_email_signup.getText().toString(), et_confpass_signup.getText().toString(),
                                            et_name_signup.getText().toString());
                                }else {
                                    Toast.makeText(mainActivity, "Password and Confirm Password must be same", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                et_confpass_signup.setError("Enter Confirm Password");
                            }
                        }else {
                            Toast.makeText(mainActivity, "Password must have minimun 8 characters", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        et_password_signup.setError("Enter Password");
                    }
                    }else {
                        et_name_signup.setText("");
                        et_name_signup.setError("Enter Name");
                    }
                }else {
                    et_email_signup.setError("Enter Valid Email");
                }
            }
        });
        tv_fb_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loginFacebook();
            }
        });
        ll_login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.fragmentManager.popBackStack();
                MainActivity.changeFragment(new Login(mainActivity), "login");
            }
        });
    }

    public void signUp(String email, String password, String name){
        if (Network.isConnected(mainActivity)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("social_id", "");
            jsonObject.addProperty("latitude", "");
            jsonObject.addProperty("longitude", "");
            jsonObject.addProperty("device_type", "2");
            SharedPreferences pref = mainActivity.getSharedPreferences(Config.SHARED_PREF, 0);
            String regId = pref.getString("regId", null);
            jsonObject.addProperty("device_token",  regId);
            jsonObject.addProperty("login_type", "0");// for social 1, email 0
            jsonObject.addProperty("password", password);
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("image", "");
            Log.d(TAG+" params signup ", jsonObject+" ");
            progress.show();
            signup_result(jsonObject.toString());
          //  Network.hitPostApi(mainActivity, jsonObject, this, NetworkConstants.signupUrl, NetworkConstants.requestCodeSignup);
        }else {
            Toast.makeText(mainActivity, "No Internt Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    public void signup_result(final String jsonObject) {
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkConstants.signupUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG+" signup_response", "" + response);

                        try {
                            JSONObject object=new JSONObject(response);
                            JSONArray array=object.getJSONArray("result");
                            for(int i=0;i<array.length();i++){
                                JSONObject obj=array.getJSONObject(i);
                                int isAcceptedTermsConditions=obj.getInt("isAcceptedTermsConditions");
                                Log.d("TAGS"," isAcceptedTermsConditions :"+isAcceptedTermsConditions);

                                if(isAcceptedTermsConditions==0){
                                    startActivity(new Intent(mainActivity,TermCoditionsActivity.class));
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final JSONObject jsonObject = Model.getObject(response);
                        if (jsonObject != null) {
                            Log.d(TAG+" _response", "" + response);
                            if (jsonObject.has("message")) {
                                try {
                                    String msg = jsonObject.getString("message");
                                    if (msg.equals("User registration successfull.")){
                                        JSONArray result = Model.getArray(jsonObject, "result");
                                        Log.e(TAG+" result ", result+" ");
                                        JSONObject json = Model.getObject(result, 0);
                                        Log.e(TAG+" json ", json+" ");

                                        preferenceManager.setUserAuthkey(Model.getString(json, "accessToken"));
                                        preferenceManager.setKeyUserId(Model.getString(json, "user_id"));
                                        preferenceManager.setKey_userEmailId(Model.getString(json, "email"));
                                        preferenceManager.setKeyVerficationCode(Model.getString(json, "verification_code"));
                                        String is_verified = Model.getString(json, "is_verified");
                                        preferenceManager.setKeyVerified(is_verified);
                                        preferenceManager.setKey_userName(Model.getString(json, "name"));
                                        preferenceManager.setKey_cameraWalkthrough(Model.getString(json, "cameraWalkthrough"));
                                        Log.e(TAG+" verified value ", is_verified);
                                        preferenceManager.setKey_location("yes");
                                        preferenceManager.setKey_notifiableother("1");
                                        preferenceManager.setKey_notifiablePersonal("1");
                                        preferenceManager.setKey_linkedfb("notlink");
                                        preferenceManager.setKey_frndcount("0");
                                        preferenceManager.setKey_fromfb("0");
                                        if (is_verified.equals("0")){
                                            Log.e(TAG+" inside ", "if");
                                            MainActivity.clearBackStack();
                                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new VerificationScreen(mainActivity), "verification").commit();
                                        }else {
                                            Log.e(TAG+" inside ", "else");
                                            MainActivity.clearBackStack();
                                            AddFriendAllowDialoge addFriendAllowDialoge = new AddFriendAllowDialoge(mainActivity , "email");
                                            addFriendAllowDialoge.show(mainActivity.getSupportFragmentManager(), "fsdf");

                                         //   mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivity), "home").commit();
                                        }
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
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d(TAG+" error ocurred", "TimeoutError");
                    //    Toast.makeText(mainActivity, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG+" error ocurred", "AuthFailureError");
                    //    Toast.makeText(mainActivity, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d(TAG+" error ocurred", "ServerError");
                        Toast.makeText(mainActivity, "The email has already been taken.", Toast.LENGTH_LONG).show();
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
        //JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:

                    message = returnEmptyString(jsonObject.get("message"));

                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 201:
                try {
                    Log.d(TAG+" reponse signup", jsonObject+" ");
                    message = returnEmptyString(jsonObject.get("message"));
                    Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                    if (message.equals("User registration successfull.")){
                       // JsonArray result = jsonObject.getAsJsonArray("result");
                        /*JSONArray result = Models.getArray(jsonObject, "result");
                        Log.e("result ", result+" ");
                       JSONObject json = Models.getObject(result, 0);
                        Log.e("json ", json+" ");*/
                        /*preferenceManager.setUserAuthkey(HelperClass.returnEmptyString(json.get("accessToken")));
                        preferenceManager.setKeyUserId(HelperClass.returnEmptyString(json.get("user_id")));
                        preferenceManager.setKey_userEmailId(HelperClass.returnEmptyString(json.get("email")));
                        preferenceManager.setKeyVerficationCode(HelperClass.returnEmptyString(json.get("verification_code")));
                        String is_verified = HelperClass.returnEmptyString(json.get("is_verified"));
                       *//* Log.e("verified value ", is_verified);
                        if (is_verified.equals("0")){
                            Log.e("inside ", "if");
                            MainActivity.clearBackStack();
                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new VerificationScreen(mainActivity), "verification").commit();

                        }else {
                            Log.e("inside ", "else");
                            MainActivity.clearBackStack();
                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivity), "home").commit();
                        }*/
                    }

                } catch (Exception e) {
                    Log.d(TAG+" Outcome", e.toString());
                }

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
