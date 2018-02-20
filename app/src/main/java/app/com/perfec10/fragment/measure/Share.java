package app.com.perfec10.fragment.measure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.home.Home;
import app.com.perfec10.fragment.self_snaps.SelfSnapDetail;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.GPSTracker;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;

import static app.com.perfec10.util.Constants.is_externally_Share;
import static app.com.perfec10.util.Constants.is_externally_Shares;

/**
 * Created by admin on 11/10/2017.
 */

@SuppressLint("ValidFragment")
public class Share extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private MainActivity mainActivity;
    private TextView tv_title_share, tv_sw1_share, tv_colaba_share, tv_pt1_share, tv_pt2_share, tv_pt3_share, tv_pt4_share, tv_pt5_share,
            tv_sw2_share, tv_sw3_share, tv_sw4_share, tv_sw5_share, tv_sw6_share, tv_sw7_share, tv_sw8_share, tv_sw9_share, tv_sw10_share,
            tv_sw11_share, tv_score_share, tv_toshare_share, tv_sw12_share, tv_viewtype_share, tv_location_share;
    private ImageView iv_sc1_share, iv_sc2_share, iv_sc3_share, iv_sc4_share, iv_sc5_share, iv_sc6_share, iv_sc7_share, iv_sc8_share,
            iv_sc9_share, iv_sc10_share, iv_back_share, iv_instagram_share;
    public static EditText et_caption_share;
    private Typeface regular, bold;
    private double score =  0;
    private LinearLayout ll_guide_share, ll_location_share, ll_sharefrnd_share, ll_share_scoretype,
            ll_all_share, ll_screensort_share;
    private PreferenceManager preferenceManager;
    private String bust, waist, legs, body, shoulder, scores, angle, from;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private float rating;
    private String mine, caption, location;
    private Geocoder geocoder;
    List<Address> addresses;
    private LinearLayout.LayoutParams layoutParams;
    private String TAG = "Share";
    private View view;
    private Bitmap mbitmap;

    public Share(MainActivity mainActivity, String bust, String waist, String legs, String body,
                 String shoulder, String scores, String angle, Float rating, String mine,
                 String from){
        this.mainActivity = mainActivity;
        this.bust = bust;
        this.waist = waist;
        this.legs = legs;
        this.body = body;
        this.shoulder = shoulder;
        this.scores = scores;
        this.angle = angle;
        this.rating = rating;
        this.mine = mine;
        this.from = from;
        preferenceManager  = new PreferenceManager(mainActivity);
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        Log.d(TAG+" rating from back ", rating+" ");
        SelfSnapDetail.change = false;
    }

    public Share(MainActivity mainActivity, String bust, String waist, String legs, String body,
                 String shoulder, String scores, String angle, Float rating, String mine,
                 String caption, String location, String from){
        this.mainActivity = mainActivity;
        this.bust = bust;
        this.waist = waist;
        this.legs = legs;
        this.body = body;
        this.shoulder = shoulder;
        this.scores = scores;
        this.angle = angle;
        this.rating = rating;
        this.mine = mine;
        this.caption = caption;
        this.location = location;
        this.from = from;
        preferenceManager  = new PreferenceManager(mainActivity);
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        Log.d(TAG+" rating from back ", rating+" ");
        Log.d(TAG+" from ", "share "+from);
        SelfSnapDetail.change = false;
    }

    public Share(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGoogleApiClient = mainActivity.getGoogleApiClient();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*try {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(mainActivitySignUP)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(mainActivitySignUP, this)
                    .build();
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

       /* if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
            try {
                mGoogleApiClient = new GoogleApiClient
                        .Builder(mainActivitySignUP)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage(mainActivitySignUP, this)
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.share_layout, container, false);
        initView(v);
        clickListner();

        Log.d("TAGS","Sahre onCreateView");

        return v;


    }

    @Override
    public void onStart() {
        super.onStart();


        if(is_externally_Share==1){
            View view1 = mainActivity.getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager)mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            mainActivity.getSupportFragmentManager().popBackStack();
        }


        if(is_externally_Shares==2){
            View view1 = mainActivity.getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager)mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Home(mainActivity), "home").commit();
        }


        mGoogleApiClient.connect();
        Log.d("TAGS","Sahre onStart");

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("TAGS","Sahre onResume");
    }



    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

        Log.d("TAGS","Sahre onStop");

    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(mainActivity);
        mGoogleApiClient.disconnect();
        Log.d("TAGS","Sahre onPause");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGoogleApiClient = null;
        Log.d("TAGS","Sahre onDetach");

    }

    public void initView(View view){
        tv_title_share = (TextView) view.findViewById(R.id.tv_title_share);
        tv_sw1_share = (TextView) view.findViewById(R.id.tv_sw1_share);
        tv_sw2_share = (TextView) view.findViewById(R.id.tv_sw2_share);
        tv_sw3_share = (TextView) view.findViewById(R.id.tv_sw3_share);
        tv_sw4_share = (TextView) view.findViewById(R.id.tv_sw4_share);
        tv_sw5_share = (TextView) view.findViewById(R.id.tv_sw5_share);
        tv_sw6_share = (TextView) view.findViewById(R.id.tv_sw6_share);
        tv_sw7_share = (TextView) view.findViewById(R.id.tv_sw7_share);
        tv_sw8_share = (TextView) view.findViewById(R.id.tv_sw8_share);
        tv_sw9_share = (TextView) view.findViewById(R.id.tv_sw9_share);
        tv_sw10_share = (TextView) view.findViewById(R.id.tv_sw10_share);
        tv_sw11_share = (TextView) view.findViewById(R.id.tv_sw11_share);
        tv_sw12_share = (TextView) view.findViewById(R.id.tv_sw12_share);
        tv_colaba_share = (TextView) view.findViewById(R.id.tv_colaba_share);
        tv_pt1_share = (TextView) view.findViewById(R.id.tv_pt1_share);
        tv_pt2_share = (TextView) view.findViewById(R.id.tv_pt2_share);
        tv_pt3_share = (TextView) view.findViewById(R.id.tv_pt3_share);
        tv_pt4_share = (TextView) view.findViewById(R.id.tv_pt4_share);
        tv_pt5_share = (TextView) view.findViewById(R.id.tv_pt5_share);
        tv_score_share = (TextView) view.findViewById(R.id.tv_score_share);
        tv_toshare_share = (TextView) view.findViewById(R.id.tv_toshare_share);
        tv_viewtype_share = (TextView) view.findViewById(R.id.tv_viewtype_share);
        tv_location_share = (TextView) view.findViewById(R.id.tv_location_share);

        layoutParams = (LinearLayout.LayoutParams)tv_score_share.getLayoutParams();

        et_caption_share = (EditText) view.findViewById(R.id.et_caption_share);

        iv_sc1_share = (ImageView) view.findViewById(R.id.iv_sc1_share);
        iv_sc2_share = (ImageView) view.findViewById(R.id.iv_sc2_share);
        iv_sc3_share = (ImageView) view.findViewById(R.id.iv_sc3_share);
        iv_sc4_share = (ImageView) view.findViewById(R.id.iv_sc4_share);
        iv_sc5_share = (ImageView) view.findViewById(R.id.iv_sc5_share);
        iv_sc6_share = (ImageView) view.findViewById(R.id.iv_sc6_share);
        iv_sc7_share = (ImageView) view.findViewById(R.id.iv_sc7_share);
        iv_sc8_share = (ImageView) view.findViewById(R.id.iv_sc8_share);
        iv_sc9_share = (ImageView) view.findViewById(R.id.iv_sc9_share);
        iv_sc10_share = (ImageView) view.findViewById(R.id.iv_sc10_share);
        iv_back_share = (ImageView) view.findViewById(R.id.iv_back_share);
        iv_instagram_share = (ImageView) view.findViewById(R.id.iv_instagram_share);

        ll_guide_share = (LinearLayout) view.findViewById(R.id.ll_guide_share);
        ll_location_share = (LinearLayout) view.findViewById(R.id.ll_location_share);
        ll_sharefrnd_share = (LinearLayout) view.findViewById(R.id.ll_sharefrnd_share);
        ll_share_scoretype = (LinearLayout) view.findViewById(R.id.ll_share_scoretype);
        ll_all_share = (LinearLayout) view.findViewById(R.id.ll_all_share);
        ll_screensort_share = (LinearLayout) view.findViewById(R.id.ll_screensort_share);

        tv_title_share.setTypeface(regular);
        tv_sw1_share.setTypeface(bold);
        tv_sw2_share.setTypeface(regular);
        tv_sw3_share.setTypeface(regular);
        tv_sw4_share.setTypeface(regular);
        tv_sw5_share.setTypeface(regular);
        tv_sw6_share.setTypeface(regular);
        tv_sw7_share.setTypeface(regular);
        tv_sw8_share.setTypeface(regular);
        tv_sw9_share.setTypeface(regular);
        tv_sw10_share.setTypeface(regular);
        tv_sw11_share.setTypeface(regular);
        tv_sw12_share.setTypeface(regular);
        tv_colaba_share.setTypeface(regular);
        tv_score_share.setTypeface(regular);
        tv_toshare_share.setTypeface(regular);
        tv_pt1_share.setTypeface(bold);
        tv_pt2_share.setTypeface(bold);
        tv_pt3_share.setTypeface(bold);
        tv_pt4_share.setTypeface(bold);
        tv_pt5_share.setTypeface(bold);
        tv_viewtype_share.setTypeface(regular);
        et_caption_share.setTypeface(regular);

        tv_pt1_share.setText(bust);
        tv_pt2_share.setText(waist);
        tv_pt3_share.setText(legs);
        tv_pt4_share.setText(body);
        tv_pt5_share.setText(shoulder);
        if (mine.equals("0")){
            tv_score_share.setText(scores);
            ll_share_scoretype.setBackgroundResource(R.mipmap.perfec10_score);
        }else {
            tv_score_share.setText(rating+"");
            ll_share_scoretype.setBackgroundResource(R.mipmap.other_score);
            layoutParams.setMargins(0,3,0,0);
            tv_score_share.setLayoutParams(layoutParams);
        }
        if (angle.equals("0")){
            tv_viewtype_share.setText("Front View");
        }else {
            tv_viewtype_share.setText("Back View");
        }

        if (from.equals("adapter")){

            caption = caption.replace("\\", "");
            caption = caption.replace("null", "");
            location = location.replace("null", "");
            et_caption_share.setText(caption);
            tv_location_share.setText(location);
        }
        // test
            // have to check
        score = Double.parseDouble(rating+"");
        if (score==0.5){
            iv_sc1_share.setImageResource(R.mipmap.star_half_fill);
        }else if (score==1){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
        }else if (score==1.5){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_half_fill);
        }else if (score==2){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
        }else if (score==2.5){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_half_fill);
        }else if (score==3){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
        }else if (score==3.5){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_half_fill);
        }else if (score==4){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
        }else if (score==4.5){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_half_fill);
        }else if (score==5){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
        }else if (score==5.5){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
            iv_sc6_share.setImageResource(R.mipmap.star_half_fill);
        }else if (score==6){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
            iv_sc6_share.setImageResource(R.mipmap.star_fill);
        }else if (score==6.5){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
            iv_sc6_share.setImageResource(R.mipmap.star_fill);
            iv_sc7_share.setImageResource(R.mipmap.star_half_fill);
        }else if (score==7){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
            iv_sc6_share.setImageResource(R.mipmap.star_fill);
            iv_sc7_share.setImageResource(R.mipmap.star_fill);
        }else if (score==7.5){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
            iv_sc6_share.setImageResource(R.mipmap.star_fill);
            iv_sc7_share.setImageResource(R.mipmap.star_fill);
            iv_sc8_share.setImageResource(R.mipmap.star_half_fill);
        }else if (score==8){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
            iv_sc6_share.setImageResource(R.mipmap.star_fill);
            iv_sc7_share.setImageResource(R.mipmap.star_fill);
            iv_sc8_share.setImageResource(R.mipmap.star_fill);
        }else if (score==8.5){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
            iv_sc6_share.setImageResource(R.mipmap.star_fill);
            iv_sc7_share.setImageResource(R.mipmap.star_fill);
            iv_sc8_share.setImageResource(R.mipmap.star_fill);
            iv_sc9_share.setImageResource(R.mipmap.star_half_fill);
        }else if (score==9){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
            iv_sc6_share.setImageResource(R.mipmap.star_fill);
            iv_sc7_share.setImageResource(R.mipmap.star_fill);
            iv_sc8_share.setImageResource(R.mipmap.star_fill);
            iv_sc9_share.setImageResource(R.mipmap.star_fill);
        }else if (score==9.5){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
            iv_sc6_share.setImageResource(R.mipmap.star_fill);
            iv_sc7_share.setImageResource(R.mipmap.star_fill);
            iv_sc8_share.setImageResource(R.mipmap.star_fill);
            iv_sc9_share.setImageResource(R.mipmap.star_fill);
            iv_sc10_share.setImageResource(R.mipmap.star_half_fill);
        }else if (score==10){
            iv_sc1_share.setImageResource(R.mipmap.star_fill);
            iv_sc2_share.setImageResource(R.mipmap.star_fill);
            iv_sc3_share.setImageResource(R.mipmap.star_fill);
            iv_sc4_share.setImageResource(R.mipmap.star_fill);
            iv_sc5_share.setImageResource(R.mipmap.star_fill);
            iv_sc6_share.setImageResource(R.mipmap.star_fill);
            iv_sc7_share.setImageResource(R.mipmap.star_fill);
            iv_sc8_share.setImageResource(R.mipmap.star_fill);
            iv_sc9_share.setImageResource(R.mipmap.star_fill);
            iv_sc10_share.setImageResource(R.mipmap.star_fill);
        }else {

        }

        if (preferenceManager.getKey_shareWalkthrough().equalsIgnoreCase("1")){
            ll_guide_share.setVisibility(View.GONE);
        }else {
            ll_guide_share.setVisibility(View.VISIBLE);
        }

        if (preferenceManager.getKey_location().equals("yes")){
            if (Stats.location != null && Stats.location.length() > 0){
                if (!from.equals("adapter")){
                    tv_location_share.setText(Stats.location);
                }

            }else {
                geocoder = new Geocoder(mainActivity, Locale.getDefault());
                GPSTracker gpsTracker = new GPSTracker(mainActivity);
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    if (!from.equals("adapter")){
                        tv_location_share.setText(address);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {

        }
        view = ll_screensort_share;
        view.setDrawingCacheEnabled(true);

    }

    public void clickListner(){
        iv_back_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = mainActivity.getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager)mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                mainActivity.getSupportFragmentManager().popBackStack();
            }
        });
        ll_guide_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_guide_share.setVisibility(View.GONE);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("cameraWalkthrough", "");
                    jsonObject.put("shareWalkthrough", "1");
                    jsonObject.put("statsWalkthrough", "");
                    Log.d(TAG+" params of walk through", jsonObject+" ");
                    preferenceManager.setKey_shareWalkthrough("1");
                    if (Network.isConnected(mainActivity)){
                        walkThrough(jsonObject.toString());
                        ll_guide_share.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        ll_location_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferenceManager.getKey_location().equals("yes")){
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(mainActivity), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(mainActivity, "On the location from the application settings to access location", Toast.LENGTH_LONG).show();
                }

            }
        });
        ll_sharefrnd_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!preferenceManager.getKey_frndcount().equals("0")){
                    MainActivity.changeFragment(new ShareContactList(mainActivity, from), "share_list");
                }else {
                    Toast.makeText(mainActivity, "you have no friends.Please add friends", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ll_all_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*  Sahre Externally Data...........................................*/
                try {
                    is_externally_Shares=2;
                    is_externally_Share=1;
                    screenShot(view);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.setType("image/png");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Perfec10");
                    String sAux = "\nLet me recommend you this wonderful application and check out my score through Perfec10\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.lenovo.anyshare.gps&hl=en  \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mbitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    i.putExtra(Intent.EXTRA_STREAM, getImageUri(mainActivity, mbitmap));
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }




    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void screenShot(View view) {
        mbitmap = getBitmapOFRootView(ll_all_share);
     //   iv_instagram_share.setImageBitmap(mbitmap);
        createImage(mbitmap);
    }

    public Bitmap getBitmapOFRootView(View v) {
        View rootview = ll_screensort_share;
        rootview.setDrawingCacheEnabled(true);
        Bitmap bitmap1 = rootview.getDrawingCache();
        return bitmap1;
    }

    public void createImage(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File file = new File(Environment.getExternalStorageDirectory() +
                "/capturedscreenandroid.jpg");
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes.toByteArray());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void walkThrough(final String jsonObject) {
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkConstants.walkThroughUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG+" walkthrough_response", "" + response);
                        final JSONObject jsonObjec = Model.getObject(response);
                        if (jsonObjec != null) {
                            Log.d(TAG+" _response", "" + response);
                            if (jsonObjec.has("message")) {
                                try {
                                    String msg = jsonObjec.getString("message");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                walkThrough(jsonObject);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d(TAG+" error ocurred", "TimeoutError");
                    //    Toast.makeText(mainActivitySignUP, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG+" error ocurred", "AuthFailureError");
                    //    Toast.makeText(mainActivitySignUP, "Internet connection is slow", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d(TAG+" error ocurred", "ServerError");

                } else if (error instanceof NetworkError) {
                    Log.d(TAG+" error ocurred", "NetworkError");
                    Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d(TAG+" error ocurred", "ParseError");
                    //    Toast.makeText(mainActivitySignUP, "Internet connection is ", Toast.LENGTH_LONG).show();
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
                1100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            //if (resultCode == RESULT_OK) {
            try {
                Place place = PlacePicker.getPlace(data, mainActivity);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitude);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitude);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);
                Log.d(TAG+" Address ", address);
                //   tvPlaceDetails.setText(stBuilder.toString());
                // }
                tv_location_share.setText(address);
                Stats.location = address;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
