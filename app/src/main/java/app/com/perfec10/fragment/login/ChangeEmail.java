package app.com.perfec10.fragment.login;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import app.com.perfec10.helper.Validation;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fluper on 31/10/17.
 */

@SuppressLint("ValidFragment")
public class ChangeEmail extends Fragment {
    private MainActivity mainActivity;
    private Progress progress;
    private PreferenceManager preferenceManager;
    private Typeface regular, bold;
    @BindView(R.id.tv_sw1_change) TextView tv_sw1_change;
    @BindView(R.id.tv_send_change) TextView tv_send_change;
    @BindView(R.id.et_change_email) EditText et_change_email;
    private String TAG = "ChangeEmail";

    public ChangeEmail(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public ChangeEmail(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_email, container, false);
        ButterKnife.bind(this, view);
        initView();
        clickListner();
        return view;
    }

    public void initView(){
        tv_send_change.setTypeface(bold);
        tv_sw1_change.setTypeface(regular);
        et_change_email.setTypeface(regular);
    }

    public void clickListner(){
        tv_send_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Network.isConnected(mainActivity)){
                    if (Validation.validEmail(et_change_email.getText().toString())){
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("email", et_change_email.getText().toString());
                        Log.d(TAG+" paramsof change email ", jsonObject+" ");
                        progress.show();
                        changeEmail(jsonObject.toString());
                    }else {
                        et_change_email.setError("Enter Valid Email");
                    }
                }else {
                    Toast.makeText(mainActivity, "No Internt Connection ", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
                                    if (message != null && message.equalsIgnoreCase("Your email has been changed. Please verify OTP received on your new email address.")) {
                                        Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                                        MainActivity.fragmentManager.popBackStack();
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
                     Toast.makeText(mainActivity, "The email has already been taken.", Toast.LENGTH_SHORT).show();
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
}
