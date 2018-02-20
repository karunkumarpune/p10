package app.com.perfec10.fragment.measure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.home.CropZoomScreen;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.GPSTracker;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by admin on 11/9/2017.
 */

@SuppressLint("ValidFragment")
public class Stats extends Fragment implements NetworkCallBack, GoogleApiClient.OnConnectionFailedListener{
    private MainActivity mainActivity;
    private ImageView iv_share_stats, iv_close_stats, iv_sc1_stats, iv_sc2_stats, iv_sc3_stats, iv_sc4_stats, iv_sc5_stats, iv_sc6_stats,
            iv_sc7_stats, iv_sc8_stats, iv_sc9_stats, iv_sc10_stats;
    private TextView tv_pt1_stats, tv_pt2_stats, tv_pt3_stats, tv_pt4_stats, tv_pt5_stats,
            tv_sw1_stats, tv_sw2_stats, tv_sw3_stats, tv_sw4_stats, tv_sw5_stats, tv_sw6_stats,
            tv_sw7_stats, tv_sw8_stats, tv_sw9_stats, tv_sw10_stats, tv_sw11_stats, tv_sw18_stats, tv_score_stats;
    private LinearLayout ll_mine_rating, ll_tut_stats, ll_bodyview_stats;
    public static RatingBar rb_stats;
    private PreferenceManager preferenceManager;
    private Typeface regular, bold;
    private Progress progress;
    private String bust_waist, waist_hips, legs_body, body_waist, shoulder_hips,
                    scores, picture, angle, gender;
    public static String input_id, post_id;
    public static String tagPerson, location, age, height, wieght, race, score, note;
    public static boolean updatePost = false;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private Geocoder geocoder;
    List<Address> addresses;
    private String TAG = "Stats";
    public static String notMineRating = "0";
    public String newScore = "";


    public Stats(MainActivity mainActivity, String bust_waist, String waist_hips, String legs_body,
                 String body_waist, String shoulder_hips, String scores, String picture,
                 String angle, String input_id,String post_id ,String gender){
        this.mainActivity = mainActivity;
        this.bust_waist = bust_waist;
        this.waist_hips = waist_hips;
        this.legs_body = legs_body;
        this.body_waist = body_waist;
        this.shoulder_hips = shoulder_hips;
        this.scores = scores;
        this.picture = picture;
        this.angle = angle;
        this.input_id = input_id;
        this.post_id = post_id;
        this.gender = gender;
        preferenceManager = new PreferenceManager(mainActivity);
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        Log.d("from back ", scores+" ");
        score = scores;
    }

    public Stats(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGoogleApiClient = mainActivity.getGoogleApiClient();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_status_layout, container, false);
        CropZoomScreen.backFrom = "stats";
        initView(view);//
        clickListner();
        Log.d(TAG+" auth-token ", preferenceManager.getUserAuthkey()+" ");
        Log.d(TAG+" auth-token ", CropZoomScreen.backFrom+" ");
       // score = "";
        return view;
    }

    @Override
    public void onResume() {
        /*if (notMineRating.equals("0")){
            score = scores;

            Log.d(TAG, "pic is 0");
        }else if (notMineRating.equals("1")){
            score = rb_stats.getRating()+"";
            Log.d(TAG, "pic is 1");
        }
        Log.d("scores static ", score+" ");*/
        updatePost = false;
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(mainActivity);
        mGoogleApiClient.disconnect();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGoogleApiClient = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void initView(View view){
        tv_pt1_stats = (TextView) view.findViewById(R.id.tv_pt1_stats);
        tv_pt2_stats = (TextView) view.findViewById(R.id.tv_pt2_stats);
        tv_pt3_stats = (TextView) view.findViewById(R.id.tv_pt3_stats);
        tv_pt4_stats = (TextView) view.findViewById(R.id.tv_pt4_stats);
        tv_pt5_stats = (TextView) view.findViewById(R.id.tv_pt5_stats);
        tv_sw1_stats = (TextView) view.findViewById(R.id.tv_sw1_stats);
        tv_sw2_stats = (TextView) view.findViewById(R.id.tv_sw2_stats);
        tv_sw3_stats = (TextView) view.findViewById(R.id.tv_sw3_stats);
        tv_sw4_stats = (TextView) view.findViewById(R.id.tv_sw4_stats);
        tv_sw5_stats = (TextView) view.findViewById(R.id.tv_sw5_stats);
        tv_sw6_stats = (TextView) view.findViewById(R.id.tv_sw6_stats);
        tv_sw7_stats = (TextView) view.findViewById(R.id.tv_sw7_stats);
        tv_sw8_stats = (TextView) view.findViewById(R.id.tv_sw8_stats);
        tv_sw9_stats = (TextView) view.findViewById(R.id.tv_sw9_stats);
        tv_sw10_stats = (TextView) view.findViewById(R.id.tv_sw10_stats);
        tv_sw11_stats = (TextView) view.findViewById(R.id.tv_sw11_stats);
        tv_sw18_stats = (TextView) view.findViewById(R.id.tv_sw18_stats);
        tv_score_stats = (TextView) view.findViewById(R.id.tv_score_stats);

        iv_close_stats = (ImageView) view.findViewById(R.id.iv_close_stats);
        iv_share_stats = (ImageView) view.findViewById(R.id.iv_share_stats);
        iv_sc1_stats = (ImageView) view.findViewById(R.id.iv_sc1_stats);
        iv_sc2_stats = (ImageView) view.findViewById(R.id.iv_sc2_stats);
        iv_sc3_stats = (ImageView) view.findViewById(R.id.iv_sc3_stats);
        iv_sc4_stats = (ImageView) view.findViewById(R.id.iv_sc4_stats);
        iv_sc5_stats = (ImageView) view.findViewById(R.id.iv_sc5_stats);
        iv_sc6_stats = (ImageView) view.findViewById(R.id.iv_sc6_stats);
        iv_sc7_stats = (ImageView) view.findViewById(R.id.iv_sc7_stats);
        iv_sc8_stats = (ImageView) view.findViewById(R.id.iv_sc8_stats);
        iv_sc9_stats = (ImageView) view.findViewById(R.id.iv_sc9_stats);
        iv_sc10_stats = (ImageView) view.findViewById(R.id.iv_sc10_stats);

        ll_mine_rating = (LinearLayout) view.findViewById(R.id.ll_mine_rating);
        ll_tut_stats = (LinearLayout) view.findViewById(R.id.ll_tut_stats);
        ll_bodyview_stats = (LinearLayout) view.findViewById(R.id.ll_bodyview_stats);

        rb_stats = (RatingBar) view.findViewById(R.id.rb_stats);

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

                location = address;

        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_pt1_stats.setTypeface(regular);
        tv_pt2_stats.setTypeface(regular);
        tv_pt3_stats.setTypeface(regular);
        tv_pt4_stats.setTypeface(regular);
        tv_pt5_stats.setTypeface(regular);
        tv_sw1_stats.setTypeface(regular);
        tv_sw2_stats.setTypeface(regular);
        tv_sw3_stats.setTypeface(regular);
        tv_sw4_stats.setTypeface(regular);
        tv_sw5_stats.setTypeface(regular);
        tv_sw6_stats.setTypeface(regular);
        tv_sw7_stats.setTypeface(regular);
        tv_sw8_stats.setTypeface(regular);
        tv_sw9_stats.setTypeface(regular);
        tv_sw10_stats.setTypeface(regular);
        tv_sw11_stats.setTypeface(regular);
        tv_score_stats.setTypeface(regular);
        tv_sw18_stats.setTypeface(bold);

        // gender 0=male, 1=female
        if (gender.equals("0")){
            // angle 0=front, 1=back
            if (angle.equals("0")){
            ll_bodyview_stats.setBackgroundResource(R.mipmap.male_front);
            }else if (angle.equals("1")){
                ll_bodyview_stats.setBackgroundResource(R.mipmap.male_back);
            }
        }else if (gender.equals("1")){
            if (angle.equals("0")){
                ll_bodyview_stats.setBackgroundResource(R.mipmap.female_front);
            }else if (angle.equals("1")){
                ll_bodyview_stats.setBackgroundResource(R.mipmap.female_back);
            }
        }

        if (picture.equals("0")){
           // score = scores;
            notMineRating = "0";
        }
        // picture = 0, mine  = 1, not mine
        if (picture.equals("0")){
          //  newScore = scores;
            notMineRating = "0";
            ll_mine_rating.setVisibility(View.VISIBLE);
            rb_stats.setVisibility(View.GONE);
            double score = Double.parseDouble(scores);
            if (score==0.5){
                iv_sc1_stats.setImageResource(R.mipmap.star_half_fill);
            }else if (score==1){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
            }else if (score==1.5){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_half_fill);
            }else if (score==2){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
            }else if (score==2.5){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_half_fill);
            }else if (score==3){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
            }else if (score==3.5){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_half_fill);
            }else if (score==4){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
            }else if (score==4.5){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_half_fill);
            }else if (score==5){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
            }else if (score==5.5){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
                iv_sc6_stats.setImageResource(R.mipmap.star_half_fill);
            }else if (score==6){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
                iv_sc6_stats.setImageResource(R.mipmap.star_fill);
            }else if (score==6.5){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
                iv_sc6_stats.setImageResource(R.mipmap.star_fill);
                iv_sc7_stats.setImageResource(R.mipmap.star_half_fill);
            }else if (score==7){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
                iv_sc6_stats.setImageResource(R.mipmap.star_fill);
                iv_sc7_stats.setImageResource(R.mipmap.star_fill);
            }else if (score==7.5){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
                iv_sc6_stats.setImageResource(R.mipmap.star_fill);
                iv_sc7_stats.setImageResource(R.mipmap.star_fill);
                iv_sc8_stats.setImageResource(R.mipmap.star_half_fill);
            }else if (score==8){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
                iv_sc6_stats.setImageResource(R.mipmap.star_fill);
                iv_sc7_stats.setImageResource(R.mipmap.star_fill);
                iv_sc8_stats.setImageResource(R.mipmap.star_fill);
            }else if (score==8.5){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
                iv_sc6_stats.setImageResource(R.mipmap.star_fill);
                iv_sc7_stats.setImageResource(R.mipmap.star_fill);
                iv_sc8_stats.setImageResource(R.mipmap.star_fill);
                iv_sc9_stats.setImageResource(R.mipmap.star_half_fill);
            }else if (score==9){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
                iv_sc6_stats.setImageResource(R.mipmap.star_fill);
                iv_sc7_stats.setImageResource(R.mipmap.star_fill);
                iv_sc8_stats.setImageResource(R.mipmap.star_fill);
                iv_sc9_stats.setImageResource(R.mipmap.star_fill);
            }else if (score==9.5){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
                iv_sc6_stats.setImageResource(R.mipmap.star_fill);
                iv_sc7_stats.setImageResource(R.mipmap.star_fill);
                iv_sc8_stats.setImageResource(R.mipmap.star_fill);
                iv_sc9_stats.setImageResource(R.mipmap.star_fill);
                iv_sc10_stats.setImageResource(R.mipmap.star_half_fill);
            }else if (score==10){
                iv_sc1_stats.setImageResource(R.mipmap.star_fill);
                iv_sc2_stats.setImageResource(R.mipmap.star_fill);
                iv_sc3_stats.setImageResource(R.mipmap.star_fill);
                iv_sc4_stats.setImageResource(R.mipmap.star_fill);
                iv_sc5_stats.setImageResource(R.mipmap.star_fill);
                iv_sc6_stats.setImageResource(R.mipmap.star_fill);
                iv_sc7_stats.setImageResource(R.mipmap.star_fill);
                iv_sc8_stats.setImageResource(R.mipmap.star_fill);
                iv_sc9_stats.setImageResource(R.mipmap.star_fill);
                iv_sc10_stats.setImageResource(R.mipmap.star_fill);
            }else {

            }
            tv_score_stats.setText(scores);
        }else {
            notMineRating = "1";
            ll_mine_rating.setVisibility(View.GONE);
            rb_stats.setVisibility(View.VISIBLE);
            tv_score_stats.setText(rb_stats.getRating()+"");
          //  score = "0";
            rb_stats.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                  //  score = rb_stats.getRating()+"";
                 //   newScore = score;
                    tv_score_stats.setText(rb_stats.getRating()+"");
                    updatePost = false;
                 //   Log.d("rb rated ", score);
                }
            });
        }

      Log.d(TAG+" from back ", bust_waist);
        tv_pt1_stats.setText(bust_waist);
        tv_pt2_stats.setText(waist_hips);
        tv_pt3_stats.setText(legs_body);
        tv_pt4_stats.setText(body_waist);
        tv_pt5_stats.setText(shoulder_hips);

        Log.d(TAG+" picture ",picture);

        if (preferenceManager.getKey_statsWalkthrough().equalsIgnoreCase("1")){
            ll_tut_stats.setVisibility(View.GONE);
        }else {
            ll_tut_stats.setVisibility(View.VISIBLE);
        }

    }
    public void clickListner(){
        tv_sw18_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notMineRating = "0";
                MainActivity.changeFragment(new StatsInput(mainActivity, scores), "stats_input");
            }
        });
        iv_share_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (!tv_selectage_stats.getText().equals("Select Age")){
                    selecAge = tv_selectage_stats.getText().toString();
                }
                if (!tv_selectheight_stats.getText().equals("Select Height")){
                    selecHeigh = tv_selectheight_stats.getText().toString();
                }
                if (!tv_selectweight_stats.getText().equals("Select Weight")){
                    selecWeigh = tv_selectweight_stats.getText().toString();
                }*/
                if (picture.equals("0")){// mine
                    MainActivity.changeFragment(new Share(mainActivity, bust_waist, waist_hips, legs_body
                            ,body_waist, shoulder_hips, scores, angle, Float.parseFloat(scores), "0", "stats"), "share");
                }else if (picture.equals("1")){
                    double a = rb_stats.getRating();
                    if (a == 0.0){
                        Toast.makeText(mainActivity, "Please Rate first", Toast.LENGTH_SHORT).show();
                    }else {
                        MainActivity.changeFragment(new Share(mainActivity, bust_waist, waist_hips, legs_body
                                ,body_waist, shoulder_hips, scores, angle, rb_stats.getRating(),"1", "stats"), "share");
                    }
                }

            }
        });
        iv_close_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*for (int i =0; i<=2; i++){
                    mainActivitySignUP.getSupportFragmentManager().popBackStack();
                }*/
                if (picture.equals("1")){
                    double a = rb_stats.getRating();
                    if (a == 0.0){
                        Toast.makeText(mainActivity, "Please Rate First", Toast.LENGTH_SHORT).show();
                    }else {
                        progress.show();
                        updatePost();
                    }
                }else {
                    progress.show();
                    updatePost();
                }

            }
        });
        ll_tut_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Network.isConnected(mainActivity)){
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("cameraWalkthrough", "");
                        jsonObject.put("shareWalkthrough", "");
                        jsonObject.put("statsWalkthrough", "1");
                        Log.d(TAG+" params of walk through", jsonObject+" ");
                        preferenceManager.setKey_statsWalkthrough("1");
                        walkThrough(jsonObject+"");
                        ll_tut_stats.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(mainActivity, "No Intenet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void updatePost(){
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("input_id",input_id);
            jsonObject.addProperty("post_id", post_id);
            if (StatsInput.et_tag_stats !=null){
                String tag = StatsInput.et_tag_stats.getText().toString();
                tag = tag.replace("@", "");
                jsonObject.addProperty("tag_person", tag);
            }else {

                jsonObject.addProperty("tag_person", "");
            }
            if (preferenceManager.getKey_location().equals("yes")){
                if (Stats.location != null){
                    jsonObject.addProperty("location", Stats.location);
                }else {
                    jsonObject.addProperty("location", "");
                }
            }else {
                jsonObject.addProperty("location", "");
            }


            if (Stats.age != null){
                Log.d(TAG+" inside ", "if");
                jsonObject.addProperty("age",Stats.age);
            }else {
                Log.d(TAG+" inside ", "else");
                jsonObject.addProperty("age","");
            }
            if (Stats.height != null){
                jsonObject.addProperty("height", Stats.height);
            }else {
                jsonObject.addProperty("height", "");
            }
            if (Stats.wieght != null){
                jsonObject.addProperty("weight", Stats.wieght);
            }else {
                jsonObject.addProperty("weight", "");
            }
            if (StatsInput.et_race_stats != null){
                jsonObject.addProperty("race", StatsInput.et_race_stats.getText().toString());

            }else {
                jsonObject.addProperty("race", "");

            }
            if (StatsInput.et_note_stats != null){
                jsonObject.addProperty("note", StatsInput.et_note_stats.getText().toString());
            }else {
                jsonObject.addProperty("note", "");
            }

            jsonObject.addProperty("caption", "");
            List<Integer> fid = new ArrayList<Integer>();
            Gson gson = new Gson();

                jsonObject.add("friends_id",gson.toJsonTree(fid));
                jsonObject.add("groups_id",gson.toJsonTree(fid));

            if (picture.equals("0")){
                jsonObject.addProperty("score", scores);
            }else {
                jsonObject.addProperty("score", rb_stats.getRating()+"");
            }

            Log.d(TAG+" location ", Stats.location+" ");

            Log.d(TAG+" params update post ", jsonObject+" ");
            if (Network.isConnected(mainActivity)){
                Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.updatePost,
                        1);
            }else {
                Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }

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
                    Log.d(TAG+"error ocurred", "TimeoutError");
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
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response update post ", data+" ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {

                    message = returnEmptyString(jsonObject.get("message"));
                  //  Toast.makeText(mainActivitySignUP, message, Toast.LENGTH_SHORT).show();
                    age = ""; height = ""; wieght = "";score="";note = "";
                    if (StatsInput.et_race_stats != null){
                        StatsInput.et_tag_stats.setText("");
                    }
                    if (StatsInput.et_race_stats != null){
                        StatsInput.et_race_stats.setText("");
                    }
                    if (StatsInput.et_note_stats != null){
                        StatsInput.et_note_stats.setText("");
                    }
                    input_id = "";
                    updatePost = true;
                    for (int i =0; i<=2; i++){
                        mainActivity.getSupportFragmentManager().popBackStack();
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

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
