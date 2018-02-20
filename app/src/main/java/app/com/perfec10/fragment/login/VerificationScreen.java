package app.com.perfec10.fragment.login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.home.Home;
import app.com.perfec10.helper.Validation;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fluper on 28/10/17.
 */

@SuppressLint("ValidFragment")
public class VerificationScreen extends Fragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private PreferenceManager preferenceManager;
    private Typeface regular, bold;
    private Progress progress;
    private ProgressDialog progressDialog;
    private String TAG = "VerificationScreen";
    @BindView(R.id.tv_sw1_verify) TextView tv_sw1_verify;
    @BindView(R.id.tv_sw2_verify) TextView tv_sw2_verify;
    @BindView(R.id.tv_sw3_verify) TextView tv_sw3_verify;
    @BindView(R.id.tv_resend_verify) TextView tv_resend_verify;
    @BindView(R.id.tv_send_verify) TextView tv_send_verify;
    @BindView(R.id.et_otp_verify) EditText et_otp_verify;
    @BindView(R.id.et_change_verfiy) EditText et_change_verfiy;
    @BindView(R.id.ll_changeclick_verify) LinearLayout ll_changeclick_verify;
    @BindView(R.id.ll_changelayout_verify) LinearLayout ll_changelayout_verify;
    @BindView(R.id.btn_change_verify) Button btn_change_verify;


    public VerificationScreen(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public VerificationScreen(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verification, container, false);
        ButterKnife.bind(this, view);
        initView();
        clickListner();

        return view;
    }

    public void initView(){
        tv_sw1_verify.setTypeface(regular);
        tv_sw2_verify.setTypeface(regular);
        tv_sw3_verify.setTypeface(regular);
        et_otp_verify.setTypeface(regular);
        et_change_verfiy.setTypeface(regular);
        tv_resend_verify.setTypeface(regular);
        tv_send_verify.setTypeface(bold);
        btn_change_verify.setTypeface(bold);
    }

    public void clickListner(){
        ll_changeclick_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.changeFragment(new ChangeEmail(mainActivity), "change_email");
            }
        });
      
        tv_send_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = et_otp_verify.getText().toString();
                if (otp.length() > 0){
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("verification_code", et_otp_verify.getText().toString());
                    Log.d(TAG+" params of vrification ", jsonObject.toString());
                    progress.show();
                    verifyEmail(jsonObject.toString());
                }else {
                    et_otp_verify.setError("Enter OTP");
                }
            }
        });
        tv_resend_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                resendOtp();
            }
        });
    }

    public void verifyEmail(final String jsonObject) {
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        Log.d(TAG+" Verification api ", NetworkConstants.verifyEmailUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkConstants.verifyEmailUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG+" verify_response", "" + response);
                        try {
                            final JSONObject jsonObject = Model.getObject(response);
                            if (jsonObject != null) {
                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    if (message != null && message.equalsIgnoreCase("Your email id has been verified")) {
                                        preferenceManager.setKeyVerified("1");
                                        MainActivity.clearBackStack();
                                        mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivity), "home").commit();

                                        progress.dismiss();

                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d(TAG+" error ocurred", "TimeoutError");
                    //    Toast.makeText(getActivity(), "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG+" error ocurred", "AuthFailureError");
                    //    Toast.makeText(getActivity(), "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d(TAG+" error ocurred", "ServerError");
                    Toast.makeText(mainActivity, "Please Enter valid OTP", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Log.d(TAG+" error ocurred", "NetworkError");
                } else if (error instanceof ParseError) {
                    Log.d(TAG+" error ocurred", "ParseError");
                    //    Toast.makeText(getActivity(), "Internet connection is slow", Toast.LENGTH_LONG).show();
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new ArrayMap<>();
                header.put("accessToken", preferenceManager.getUserAuthkey());
                return header;
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
                800000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);
    }

    public void resendOtp() {
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        Log.d(TAG+" resend api ", NetworkConstants.resendUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkConstants.resendUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG+" resend_response", "" + response);
                        try {
                            final JSONObject jsonObject = Model.getObject(response);
                            if (jsonObject != null) {
                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    if (message != null && message.equalsIgnoreCase("Otp send sucessfully.")) {
                                        Toast.makeText(mainActivity, "OTP resend successfully", Toast.LENGTH_SHORT).show();
                                        progress.dismiss();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d(TAG+" error ocurred", "TimeoutError");
                    //    Toast.makeText(getActivity(), "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG+" error ocurred", "AuthFailureError");
                    //    Toast.makeText(getActivity(), "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d(TAG+" error ocurred", "ServerError");
                  //  Toast.makeText(mainActivitySignUP, "Please enter valid OTP", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Log.d(TAG+" error ocurred", "NetworkError");
                } else if (error instanceof ParseError) {
                    Log.d(TAG+" error ocurred", "ParseError");
                    //    Toast.makeText(getActivity(), "Internet connection is slow", Toast.LENGTH_LONG).show();
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new ArrayMap<>();
                header.put("accessToken", preferenceManager.getUserAuthkey());
                return header;
            }


        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                800000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);
    }

    public void changeEmail(final String jsonObject) {
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        Log.d(TAG+" changeEmail api ", NetworkConstants.changeMailUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkConstants.changeMailUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG+" changeEmail_response", "" + response);
                        try {
                            final JSONObject jsonObject = Model.getObject(response);
                            if (jsonObject != null) {
                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    if (message != null && message.equalsIgnoreCase("successfull")) {
                                        Toast.makeText(mainActivity, "Email changed successfully ", Toast.LENGTH_SHORT).show();
                                        ll_changelayout_verify.setVisibility(View.GONE);
                                        progress.dismiss();

                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d(TAG+" error ocurred", "TimeoutError");
                    //    Toast.makeText(getActivity(), "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG+" error ocurred", "AuthFailureError");
                    //    Toast.makeText(getActivity(), "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d(TAG+" error ocurred", "ServerError");
                   // Toast.makeText(mainActivitySignUP, "Email Id already registered", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Log.d(TAG+" error ocurred", "NetworkError");
                } else if (error instanceof ParseError) {
                    Log.d(TAG+" error ocurred", "ParseError");
                    //    Toast.makeText(getActivity(), "Internet connection is slow", Toast.LENGTH_LONG).show();
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new ArrayMap<>();
                header.put("accessToken", preferenceManager.getUserAuthkey());
                return header;
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
                800000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {

    }

    @Override
    public void onError(String msg) {

    }
}
