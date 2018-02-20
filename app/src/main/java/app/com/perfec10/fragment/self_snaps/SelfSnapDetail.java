package app.com.perfec10.fragment.self_snaps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendposts.Comments;
import app.com.perfec10.fragment.friendposts.LikeScreen;
import app.com.perfec10.fragment.measure.Share;
import app.com.perfec10.fragment.measure.adapater.InputFieldsAdapter;
import app.com.perfec10.fragment.self_snaps.adapter.SelfSnapAdapter;
import app.com.perfec10.helper.RecyclerItemClickListener;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.CircleImageView;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 6/12/17.
 */

@SuppressLint("ValidFragment")
public class SelfSnapDetail extends Fragment implements NetworkCallBack, GoogleApiClient.OnConnectionFailedListener {
    private MainActivity mainActivity;
    private String date, caption, location, pt1, pt2, pt3, pt4, pt5, totalLike, totalComm,
            postId, angle, likStatus;
    private CircleImageView civ_self_detail, civ_frndpic_detail, civ_frndpic2_detail, civ_userpic_detail;
    private TextView tv_name_detail, tv_datetime_detail, tv_caption_detail, tv_snapname_detail,
            tv_snapdetail_detail, tv_pt1_detail, tv_pt2_detail, tv_pt3_detail, tv_pt4_detail,
            tv_pt5_detail, tv_sw1_detail, tv_sw2_detail, tv_sw3_detail, tv_sw4_detail, tv_sw5_detail,
            tv_sw6_detail, tv_sw7_detail, tv_sw8_detail, tv_sw9_detail, tv_sw10_detail, tv_score_detail,
            tv_viewtype_frndrow, tv_like_detail, tv_comment_detail, tv_sw11_detail, tv_sw12_detail,
            tv_comname_detail, tv_comm_detail, tv_comname2_detail, tv_comm2_detail, et_comm_detail
            , tv_viewtype_detail;

    public static TextView tv_location_eddetail,tv_age_eddetail,tv_height_eddetail, tv_weight_eddetail;
    private PreferenceManager preferenceManager;
    private double scores;
    public static EditText tv_tagged_eddetail, tv_race_eddetail, et_addnote_eddetail;
    private LinearLayout ll_comment_detail, ll_like_detail, ll_share_detail, ll_comm1_detail, ll_comm2_detail;
    private ImageView iv_back_detail, iv_sc1_detail, iv_sc2_detail, iv_sc3_detail, iv_sc4_detail,
            iv_sc5_detail, iv_sc6_detail, iv_sc7_detail, iv_sc8_detail, iv_sc9_detail,
            iv_sc10_detail, iv_like_detail, iv_save_detail;
    private Progress progress;
    private RecyclerView rv_age_stats, rv_height_stats, rv_weight_stats, rv_fndlist_eddetail;
    private LinearLayoutManager linearLayoutManager1, linearLayoutManager2, linearLayoutManager3,
            linearLayoutManager4;
    private String[] ages = {"< 16 years", "16 to 20 years", "21 to 25 years", "26 to 30 years",
            "31 to 35 years","36 to 45 years", "46 to 55 years", "> 55 years"};

    private String[] weights = {"< 40 kg", "40 to 50 kg", "51 to 60 kg", "61 to 70 kg", "71 to 80 kg",
            "81 to 90 kg", "91 to 105 kg", "106 to 120 kg", "> 120 kg"};

    private String[] heights = {"< 4ft 9in", "4ft 9in to 5ft", "5ft 1in to 5ft 3in",
            "5ft 4in to 5ft 6in", "5ft 7in to 5ft 9in", "5ft 10in to 6ft",
            "6ft 1in to 6ft 4in", "> 6ft 4in"};
    private boolean ageb = false, heightb = false, weightb = false;
    private LinearLayout ll_location_detail, ll_age_detail, ll_height_detail, ll_weight_detail,
            ll_scoretype_detail;
    private Geocoder geocoder;
    List<Address> addresses;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    public static boolean change = false;
    private String TAG = "SelfSnapDetail";
    private ArrayList<FriendListGS> sharedList;
    private ArrayList<String> searchedlist;

    public SelfSnapDetail(MainActivity mainActivity, String date, String caption, String location,
            String pt1, String pt2, String pt3, String pt4, String pt5, Double scores, String totalLike,
                          String totalComm, String postId, String angle, String likeStatus){
        this.mainActivity = mainActivity;
        this.date = date;
        this.caption = caption;
        this.location = location;
        this.pt1 = pt1;
        this.pt2 = pt2;
        this.pt3 = pt3;
        this.pt4 = pt4;
        this.pt5 = pt5;
        this.scores = scores;
        this.totalLike = totalLike;
        this.totalComm = totalComm;
        this.postId = postId;
        this.angle = angle;
        this.likStatus = likeStatus;
        preferenceManager = new PreferenceManager(mainActivity);
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    public SelfSnapDetail(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGoogleApiClient = mainActivity.getGoogleApiClient();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        /*mGoogleApiClient = new GoogleApiClient
                .Builder(mainActivitySignUP)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(mainActivitySignUP, this)
                .build();*/
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.selfsnap_detail, container, false);
        initView(view);
        clickListner();
        return view;
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
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGoogleApiClient = null;
    }

    public void initView(View v){
        tv_name_detail = (TextView) v.findViewById(R.id.tv_name_detail);
        tv_datetime_detail = (TextView) v.findViewById(R.id.tv_datetime_detail);
        tv_pt1_detail = (TextView) v.findViewById(R.id.tv_pt1_detail);
        tv_pt2_detail = (TextView) v.findViewById(R.id.tv_pt2_detail);
        tv_pt3_detail = (TextView) v.findViewById(R.id.tv_pt3_detail);
        tv_pt4_detail = (TextView) v.findViewById(R.id.tv_pt4_detail);
        tv_pt5_detail = (TextView) v.findViewById(R.id.tv_pt5_detail);
        tv_score_detail = (TextView) v.findViewById(R.id.tv_score_detail);
        tv_like_detail = (TextView) v.findViewById(R.id.tv_like_detail);
        tv_comment_detail = (TextView) v.findViewById(R.id.tv_comment_detail);
        tv_viewtype_frndrow = (TextView) v.findViewById(R.id.tv_viewtype_frndrow);
        tv_snapname_detail = (TextView) v.findViewById(R.id.tv_snapname_detail);
        tv_snapdetail_detail = (TextView) v.findViewById(R.id.tv_snapdetail_detail);
        et_comm_detail = (TextView) v.findViewById(R.id.et_comm_detail);
        tv_location_eddetail = (TextView) v.findViewById(R.id.tv_location_eddetail);
        tv_caption_detail = (TextView) v.findViewById(R.id.tv_caption_detail);
        tv_sw11_detail = (TextView) v.findViewById(R.id.tv_sw11_detail);
        tv_age_eddetail = (TextView) v.findViewById(R.id.tv_age_eddetail);
        tv_height_eddetail = (TextView) v.findViewById(R.id.tv_height_eddetail);
        tv_weight_eddetail = (TextView) v.findViewById(R.id.tv_weight_eddetail);
        tv_comm_detail = (TextView) v.findViewById(R.id.tv_comm_detail);
        tv_comname_detail = (TextView) v.findViewById(R.id.tv_comname_detail);
        tv_comname2_detail = (TextView) v.findViewById(R.id.tv_comname2_detail);
        tv_comm2_detail = (TextView) v.findViewById(R.id.tv_comm2_detail);
        tv_viewtype_detail = (TextView) v.findViewById(R.id.tv_viewtype_detail);

        tv_tagged_eddetail = (EditText) v.findViewById(R.id.tv_tagged_eddetail);
        tv_race_eddetail = (EditText) v.findViewById(R.id.tv_race_eddetail);
        et_addnote_eddetail = (EditText) v.findViewById(R.id.et_addnote_eddetail);

        iv_back_detail = (ImageView) v.findViewById(R.id.iv_back_detail);
        iv_sc1_detail = (ImageView) v.findViewById(R.id.iv_sc1_detail);
        iv_sc2_detail = (ImageView) v.findViewById(R.id.iv_sc2_detail);
        iv_sc3_detail = (ImageView) v.findViewById(R.id.iv_sc3_detail);
        iv_sc4_detail = (ImageView) v.findViewById(R.id.iv_sc4_detail);
        iv_sc5_detail = (ImageView) v.findViewById(R.id.iv_sc5_detail);
        iv_sc6_detail = (ImageView) v.findViewById(R.id.iv_sc6_detail);
        iv_sc7_detail = (ImageView) v.findViewById(R.id.iv_sc7_detail);
        iv_sc8_detail = (ImageView) v.findViewById(R.id.iv_sc8_detail);
        iv_sc9_detail = (ImageView) v.findViewById(R.id.iv_sc9_detail);
        iv_sc10_detail = (ImageView) v.findViewById(R.id.iv_sc10_detail);
        iv_like_detail = (ImageView) v.findViewById(R.id.iv_like_detail);
        iv_save_detail = (ImageView) v.findViewById(R.id.iv_save_detail);

        ll_comment_detail = (LinearLayout) v.findViewById(R.id.ll_comment_detail);
        ll_like_detail = (LinearLayout) v.findViewById(R.id.ll_like_detail);
        ll_share_detail = (LinearLayout) v.findViewById(R.id.ll_share_detail);
        ll_comm1_detail = (LinearLayout) v.findViewById(R.id.ll_comm1_detail);
        ll_comm2_detail = (LinearLayout) v.findViewById(R.id.ll_comm2_detail);
        ll_location_detail = (LinearLayout) v.findViewById(R.id.ll_location_detail);
        ll_age_detail = (LinearLayout) v.findViewById(R.id.ll_age_detail);
        ll_height_detail = (LinearLayout) v.findViewById(R.id.ll_height_detail);
        ll_weight_detail = (LinearLayout) v.findViewById(R.id.ll_weight_detail);
        ll_scoretype_detail = (LinearLayout) v.findViewById(R.id.ll_scoretype_detail);

        civ_self_detail = (CircleImageView) v.findViewById(R.id.civ_self_detail);
        civ_frndpic_detail = (CircleImageView) v.findViewById(R.id.civ_frndpic_detail);
        civ_frndpic2_detail = (CircleImageView) v.findViewById(R.id.civ_frndpic2_detail);
        civ_userpic_detail = (CircleImageView) v.findViewById(R.id.civ_userpic_detail);

        rv_height_stats = (RecyclerView) v.findViewById(R.id.rv_height_stats);
        linearLayoutManager2 = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        rv_height_stats.setLayoutManager(linearLayoutManager2);

        rv_weight_stats = (RecyclerView) v.findViewById(R.id.rv_weight_stats);
        linearLayoutManager3 = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        rv_weight_stats.setLayoutManager(linearLayoutManager3);

        rv_fndlist_eddetail = (RecyclerView) v.findViewById(R.id.rv_fndlist_eddetail);
        linearLayoutManager4 = new LinearLayoutManager(mainActivity);
        rv_fndlist_eddetail.setLayoutManager(linearLayoutManager4);

        rv_age_stats = (RecyclerView) v.findViewById(R.id.rv_age_stats);
        linearLayoutManager1 = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        rv_age_stats.setLayoutManager(linearLayoutManager1);

        Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+preferenceManager.getKey_userImg()).error(R.mipmap.user).into(civ_self_detail);
        Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+preferenceManager.getKey_userImg()).error(R.mipmap.user).into(civ_userpic_detail);

        tv_name_detail.setText(preferenceManager.getKey_userName());
        tv_datetime_detail.setText(date);
        location = location.replace("\"", "");
        if (!location.equals("null")){
            tv_snapdetail_detail.setText("Snapped at "+location);
            tv_location_eddetail.setText(location);
        }else {
            tv_snapdetail_detail.setText(" ");
            tv_location_eddetail.setText("");
        }

        tv_pt1_detail.setText(pt1);
        tv_pt2_detail.setText(pt2);
        tv_pt3_detail.setText(pt3);
        tv_pt4_detail.setText(pt4);
        tv_pt5_detail.setText(pt5);
        tv_score_detail.setText(scores+"");
      //  tv_like_detail.setText(totalLike+" Like");
        tv_comment_detail.setText(totalComm+" Comment");
        caption = caption.replace("\"", "");
        if (!caption.equals("null")){
            tv_caption_detail.setText(caption);
        }else {
            tv_caption_detail.setVisibility(View.GONE);
        }
        if(!SelfSnapAdapter.tagPerson.equals("null")){
            SelfSnapAdapter.tagPerson = SelfSnapAdapter.tagPerson.replace("@", "");
            tv_tagged_eddetail.setText(SelfSnapAdapter.tagPerson);
        }else {
            tv_tagged_eddetail.setText("");
        }
        if (!SelfSnapAdapter.age.equals("null")){
            tv_age_eddetail.setText(SelfSnapAdapter.age);
        }else {
            tv_age_eddetail.setText("");
        }
        if (!SelfSnapAdapter.height.equals("null")){
            tv_height_eddetail.setText(SelfSnapAdapter.height);
        }else {
            tv_height_eddetail.setText("");
        }
        if (!SelfSnapAdapter.wieght.equals("null")){
            tv_weight_eddetail.setText(SelfSnapAdapter.wieght);
        }else {
            tv_weight_eddetail.setText("");
        }
        if (!SelfSnapAdapter.race.equals("null")){
            tv_race_eddetail.setText(SelfSnapAdapter.race);
        }else {
            tv_race_eddetail.setText("");
        }
        /*if (Integer.parseInt(totalComm) > 0){

        }else {
            ll_comm1_detail.setVisibility(View.GONE);
            ll_comm2_detail.setVisibility(View.GONE);
        }
*/
        if (likStatus.equals("1")){

            tv_sw11_detail.setTextColor(Color.BLUE);
            iv_like_detail.setColorFilter(ContextCompat.getColor(mainActivity, R.color.fb_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        }else {
            ColorStateList oldColors =  tv_sw11_detail.getTextColors();
            tv_sw11_detail.setTextColor(oldColors);
            //   holder.iv_like_frndrow.setColorFilter(ContextCompat.getColor(mainActivitySignUP, oldColors), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        if (scores==0.5){
            iv_sc1_detail.setImageResource(R.mipmap.star_half_fill);
        }else if (scores==1){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
        }else if (scores==1.5){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_half_fill);
        }else if (scores==2){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
        }else if (scores==2.5){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_half_fill);
        }else if (scores==3){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
        }else if (scores==3.5){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_half_fill);
        }else if (scores==4){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
        }else if (scores==4.5){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_half_fill);
        }else if (scores==5){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
        }else if (scores==5.5){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
            iv_sc6_detail.setImageResource(R.mipmap.star_half_fill);
        }else if (scores==6){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
            iv_sc6_detail.setImageResource(R.mipmap.star_fill);
        }else if (scores==6.5){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
            iv_sc6_detail.setImageResource(R.mipmap.star_fill);
            iv_sc7_detail.setImageResource(R.mipmap.star_half_fill);
        }else if (scores==7){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
            iv_sc6_detail.setImageResource(R.mipmap.star_fill);
            iv_sc7_detail.setImageResource(R.mipmap.star_fill);
        }else if (scores==7.5){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
            iv_sc6_detail.setImageResource(R.mipmap.star_fill);
            iv_sc7_detail.setImageResource(R.mipmap.star_fill);
            iv_sc8_detail.setImageResource(R.mipmap.star_half_fill);
        }else if (scores==8){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
            iv_sc6_detail.setImageResource(R.mipmap.star_fill);
            iv_sc7_detail.setImageResource(R.mipmap.star_fill);
            iv_sc8_detail.setImageResource(R.mipmap.star_fill);
        }else if (scores==8.5){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
            iv_sc6_detail.setImageResource(R.mipmap.star_fill);
            iv_sc7_detail.setImageResource(R.mipmap.star_fill);
            iv_sc8_detail.setImageResource(R.mipmap.star_fill);
            iv_sc9_detail.setImageResource(R.mipmap.star_half_fill);
        }else if (scores==9){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
            iv_sc6_detail.setImageResource(R.mipmap.star_fill);
            iv_sc7_detail.setImageResource(R.mipmap.star_fill);
            iv_sc8_detail.setImageResource(R.mipmap.star_fill);
            iv_sc9_detail.setImageResource(R.mipmap.star_fill);
        }else if (scores==9.5){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
            iv_sc6_detail.setImageResource(R.mipmap.star_fill);
            iv_sc7_detail.setImageResource(R.mipmap.star_fill);
            iv_sc8_detail.setImageResource(R.mipmap.star_fill);
            iv_sc9_detail.setImageResource(R.mipmap.star_fill);
            iv_sc10_detail.setImageResource(R.mipmap.star_half_fill);
        }else if (scores==10){
            iv_sc1_detail.setImageResource(R.mipmap.star_fill);
            iv_sc2_detail.setImageResource(R.mipmap.star_fill);
            iv_sc3_detail.setImageResource(R.mipmap.star_fill);
            iv_sc4_detail.setImageResource(R.mipmap.star_fill);
            iv_sc5_detail.setImageResource(R.mipmap.star_fill);
            iv_sc6_detail.setImageResource(R.mipmap.star_fill);
            iv_sc7_detail.setImageResource(R.mipmap.star_fill);
            iv_sc8_detail.setImageResource(R.mipmap.star_fill);
            iv_sc9_detail.setImageResource(R.mipmap.star_fill);
            iv_sc10_detail.setImageResource(R.mipmap.star_fill);
        }else {

        }
        if (Network.isConnected(mainActivity)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("post_id", postId);
            progress.show();
            Network.hitPostApiWithAuth(mainActivity, jsonObject,this, NetworkConstants.getPost, 3);
        }else {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        sharedList = preferenceManager.getFrndList(mainActivity);
        if (sharedList != null){

        }else {
            sharedList = new ArrayList<>();
        }
        edit_Text_Focus_Listner();
    }

    public void edit_Text_Focus_Listner() {
        tv_tagged_eddetail.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (tv_tagged_eddetail.getText().toString().length() > 1){

                        searchedlist = new ArrayList<>();
                        for (int i =0 ; i < sharedList.size(); i++){
                            String s = tv_tagged_eddetail.getText().toString();

                            boolean a = Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).
                                    matcher(sharedList.get(i).getName()).find();

                            if (a){
                                FriendListGS friendLis = new FriendListGS();
                                friendLis.setUserId(sharedList.get(i).getUserId());
                                friendLis.setName(sharedList.get(i).getName());

                                searchedlist.add(sharedList.get(i).getName());

                            }

                        }
                        String[] stockArr = new String[searchedlist.size()];
                        stockArr = searchedlist.toArray(stockArr);
                        rv_fndlist_eddetail.setVisibility(View.VISIBLE);
                        InputFieldsAdapter inputFieldsAdapter = new InputFieldsAdapter(mainActivity, stockArr,"1" );
                        rv_fndlist_eddetail.setAdapter(inputFieldsAdapter);


                }else {
                    rv_fndlist_eddetail.setVisibility(View.GONE);
                }

                //  search_result(jsonObject.toString());
            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    public void clickListner(){
        iv_back_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = tv_tagged_eddetail.getText().toString();

                if (tag.equals(SelfSnapAdapter.tagPerson)){
                    if (tv_location_eddetail.getText().toString().equals(SelfSnapAdapter.locations)){
                        if (tv_age_eddetail.getText().toString().equals(SelfSnapAdapter.age)){
                            if (tv_height_eddetail.getText().toString().equals(SelfSnapAdapter.height)){
                                if (tv_weight_eddetail.getText().toString().equals(SelfSnapAdapter.wieght)){
                                    if (tv_race_eddetail.getText().toString().equals(SelfSnapAdapter.race)){

                                    }else {
                                        change = true;
                                    }
                                }else {
                                    change = true;
                                }
                            }else {
                                change = true;
                            }
                        }else {
                            change = true;
                        }
                    }else {
                        change = true;
                    }
                }else {
                    change = true;
                }
                if (!change){
                    mainActivity.getSupportFragmentManager().popBackStack();
                }else {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
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
                            dialog.cancel();
                            change = false;
                            mainActivity.getSupportFragmentManager().popBackStack();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();

                }

            }
        });
        tv_like_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new LikeScreen(mainActivity, postId), "like");
            }
        });
        tv_comment_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new Comments(mainActivity, postId),"comment");
            }
        });
        ll_comment_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new Comments(mainActivity, postId),"comment");
            }
        });
        et_comm_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new Comments(mainActivity, postId),"comment");
            }
        });
        ll_share_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new Share(mainActivity,pt1, pt2, pt3, pt4, pt5,scores+"",
                        angle,Float.parseFloat(scores+""), "","","", "adapter"),"share");
            }
        });
        ll_like_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeDislike();
            }
        });

        ll_age_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ageb){
                    Log.d(TAG+" inside ", "if");
                    InputFieldsAdapter inputFieldsAdapter = new InputFieldsAdapter(mainActivity, ages, "1");
                    rv_age_stats.setAdapter(inputFieldsAdapter);
                    rv_age_stats.setVisibility(View.VISIBLE);
                //    tv_selectage_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_up,0);
                    ageb = true;
                }else {
                    Log.d(TAG+" inside ", "else");
                 //   tv_selectage_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                    rv_age_stats.setVisibility(View.GONE);
                    ageb = false;
                }
                Log.d(TAG+" outside ", "else");
            }
        });

        ll_height_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!heightb){
                    InputFieldsAdapter inputFieldsAdapter2 = new InputFieldsAdapter(mainActivity, heights, "1");
                    rv_height_stats.setAdapter(inputFieldsAdapter2);
                    rv_height_stats.setVisibility(View.VISIBLE);
                   // tv_selectheight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_up,0);
                    heightb = true;
                }else {
                  //  tv_selectheight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                    rv_height_stats.setVisibility(View.GONE);
                    heightb = false;
                }
            }
        });
        ll_weight_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!weightb){
                    InputFieldsAdapter inputFieldsAdapter2 = new InputFieldsAdapter(mainActivity, weights, "1");
                    rv_weight_stats.setAdapter(inputFieldsAdapter2);
                    rv_weight_stats.setVisibility(View.VISIBLE);
                   // tv_selectweight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_up,0);
                    weightb = true;
                }else {
                  //  tv_selectweight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                    rv_weight_stats.setVisibility(View.GONE);
                    weightb = false;
                }
            }
        });
        ll_location_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferenceManager.getKey_location().equals("yes")){
                    // MainActivity.changeFragment(new GoogleMap(mainActivitySignUP), "google");
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

        rv_age_stats.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        tv_age_eddetail.setText(ages[position]);
                        rv_age_stats.setVisibility(View.GONE);
                       // SelfSnapAdapter.age = tv_age_eddetail.getText().toString();
                       // tv_selectage_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                       // change = true;
                    }
                })
        );
        rv_height_stats.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        tv_height_eddetail.setText(heights[position]);
                        rv_height_stats.setVisibility(View.GONE);
                      //  SelfSnapAdapter.height = tv_height_eddetail.getText().toString();
                      //  tv_selectheight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                    }
                })
        );
        rv_weight_stats.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        tv_weight_eddetail.setText(weights[position]);
                        rv_weight_stats.setVisibility(View.GONE);
                     //   SelfSnapAdapter.wieght = tv_weight_eddetail.getText().toString();
                     //   tv_selectweight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                    }
                })
        );
        rv_fndlist_eddetail.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        tv_tagged_eddetail.setText(searchedlist.get(position));
                        rv_fndlist_eddetail.setVisibility(View.GONE);

                    }
                })
        );
        iv_save_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePost();
            }
        });
    }

    public void updatePost(){
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("input_id",SelfSnapAdapter.inputId);
            jsonObject.addProperty("post_id", SelfSnapAdapter.postId);

                String taged = tv_tagged_eddetail.getText().toString();

                jsonObject.addProperty("tag_person", taged);

                jsonObject.addProperty("location", tv_location_eddetail.getText().toString());

                jsonObject.addProperty("age",tv_age_eddetail.getText().toString());

                jsonObject.addProperty("height", tv_height_eddetail.getText().toString());

                jsonObject.addProperty("weight", tv_weight_eddetail.getText().toString());

                jsonObject.addProperty("race", tv_race_eddetail.getText().toString());
                jsonObject.addProperty("note", et_addnote_eddetail.getText().toString());

            if (!caption.equals("null")){
                jsonObject.addProperty("caption", caption);
            }else {
                jsonObject.addProperty("caption", "");
            }

            List<Integer> fid = new ArrayList<Integer>();
            Gson gson = new Gson();

            jsonObject.add("friends_id",gson.toJsonTree(fid));
            jsonObject.add("groups_id",gson.toJsonTree(fid));


                jsonObject.addProperty("score", scores);


           // Log.d("location ", tv_location_eddetail+" ");
            Log.d(TAG+" params update post ", jsonObject+" ");
            if (Network.isConnected(mainActivity)){
                progress.show();
                Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.updatePost,
                        2);
            }else {
                Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void likeDislike() {

        if (Network.isConnected(mainActivity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("post_id", postId);
            if (likStatus.equals("1")){

                jsonObject.addProperty("like_dislike_type", "0");
            }else if (likStatus.equals("0")){
                Log.e(TAG+" YYYYYYYYYYyy null ",preferenceManager.getKey_userName() );
              //  tv_like_detail.setText(preferenceManager.getKey_userName());
                jsonObject.addProperty("like_dislike_type", "1");
            }
            Log.d(TAG+" params like ", jsonObject + " ");
            progress.show();
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.postLike, 1);
        } else {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        progress.dismiss();
        Log.d(TAG+" response like ", data + " ");
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode==1){
                        Log.d(TAG+" reponse post", jsonObject + " ");

                        message = returnEmptyString(jsonObject.get("message"));

                        if (message.equals("Liked successfully.")) {
                            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                            //    Holder holder = new Holder();
                            int c = Integer.parseInt(totalLike);
                            likStatus = "1";
                            if (c==1){
                                tv_like_detail.setText( "You Like it");
                            }else {
                               // tv_like_detail.setText("You "+ (c+1) +" Like");
                            }
                            tv_sw11_detail.setTextColor(Color.BLUE);
                            iv_like_detail.setColorFilter(ContextCompat.getColor(mainActivity, R.color.fb_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
                            if (Network.isConnected(mainActivity)){
                                JsonObject jsonObject1 = new JsonObject();
                                jsonObject1.addProperty("post_id", postId);
                                progress.show();
                                Network.hitPostApiWithAuth(mainActivity, jsonObject1,this, NetworkConstants.getPost, 3);
                            }else {
                                Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (message.equals("Disliked successfully.")) {
                            likStatus = "0";
                            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                            int c = Integer.parseInt(totalLike);
                            if (c==1){
                              //  tv_like_detail.setText((c-1)+ " Like");
                            }
                            ColorStateList oldColors =  tv_like_detail.getTextColors();
                            tv_sw11_detail.setTextColor(oldColors);
                            if (Network.isConnected(mainActivity)){
                                JsonObject jsonObject1 = new JsonObject();
                                jsonObject1.addProperty("post_id", postId);
                                progress.show();
                                Network.hitPostApiWithAuth(mainActivity, jsonObject1,this, NetworkConstants.getPost, 3);
                            }else {
                                Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }else if (requestCode==2){
                        Log.d(TAG+" response update post ", data+" ");
                        message = jsonObject.get("message").getAsString();
                        change = false;

                        Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                        String s = tv_tagged_eddetail.getText().toString();

                        SelfSnapAdapter.tagPerson = s;
                        SelfSnapAdapter.locations = tv_location_eddetail.getText().toString();
                        SelfSnapAdapter.age = tv_age_eddetail.getText().toString();
                        SelfSnapAdapter.height = tv_height_eddetail.getText().toString();
                        SelfSnapAdapter.wieght = tv_weight_eddetail.getText().toString();
                        SelfSnapAdapter.race = tv_race_eddetail.getText().toString();
                    }else if (requestCode==3){
                        Log.d(TAG+" reponse detail post ", data+" ");
                        message = jsonObject.get("message").getAsString();
                        if(message.equals("successful")){
                            JsonArray result = jsonObject.getAsJsonArray("result");
                            JsonObject json = result.get(0).getAsJsonObject();
                            String tag_person = json.get("tag_person")+"";
                            tag_person = tag_person.replace("\"", "");
                            if (!tag_person.equals("null")){
                                tv_snapname_detail.setText(tag_person);
                            }else {
                                tv_snapname_detail.setText("");
                            }
                            String angle = json.get("angle").getAsString();
                            if (angle.equals("0")){
                                tv_viewtype_detail.setText("Front View");
                            }else {
                                tv_viewtype_detail.setText("Back View");
                            }
                            String note = json.get("note")+"";
                            note = note.replace("\"", "");
                            String picture = json.get("picture")+"";
                            picture = picture.replace("\"", "");
                            if (picture.equals("1")){
                                ll_scoretype_detail.setBackgroundResource(R.mipmap.other_score);
                            }else {
                                ll_scoretype_detail.setBackgroundResource(R.mipmap.perfec10_score);
                            }
                            if (!note.equals("null")){
                                et_addnote_eddetail.setText(note);
                            }

                            JsonArray recentLikeUser = json.getAsJsonArray("recentLikeUser");
                            if (recentLikeUser.size() > 0){
                                if (recentLikeUser.size()==1){
                                    JsonObject jo = recentLikeUser.get(0).getAsJsonObject();
                                    String name = jo.get("name")+"";
                                    String id = jo.get("user_id")+"";
                                    id = id.replace("\"", "");
                                    name = name.replace("\"", "");
                                    if (id.equals(preferenceManager.getKeyUserId())){
                                        tv_like_detail.setText("You Like it");
                                    }else {
                                        tv_like_detail.setText(name+" Like it");
                                    }

                                }else if (recentLikeUser.size()==2){
                                    JsonObject jo = recentLikeUser.get(0).getAsJsonObject();
                                    String name1 = jo.get("name")+"";
                                    String id1 = jo.get("user_id")+"";
                                    id1 = id1.replace("\"", "");
                                    name1 = name1.replace("\"", "");
                                    JsonObject jo2 = recentLikeUser.get(1).getAsJsonObject();
                                    String name2 = jo2.get("name")+"";
                                    String id2 = jo.get("user_id")+"";
                                    id2 = id2.replace("\"", "");
                                    name2 = name2.replace("\"", "");
                                    if (id1.equals(preferenceManager.getKeyUserId())){
                                        tv_like_detail.setText("You, "+name2+" Likes it");
                                    }else if (id2.equals(preferenceManager.getKeyUserId())){
                                        tv_like_detail.setText("You, "+name1+" Likes it");
                                    }else {
                                        tv_like_detail.setText(name1+", "+name2+" Likes it");
                                    }

                                }else if (recentLikeUser.size() > 2){
                                    JsonObject jo = recentLikeUser.get(0).getAsJsonObject();
                                    String name1 = jo.get("name")+"";
                                    String id1 = jo.get("user_id")+"";
                                    id1 = id1.replace("\"", "");
                                    name1 = name1.replace("\"", "");
                                    JsonObject jo2 = recentLikeUser.get(1).getAsJsonObject();
                                    String name2 = jo2.get("name")+"";
                                    name2 = name2.replace("\"", "");
                                    String id2 = jo.get("user_id")+"";
                                    id2 = id2.replace("\"", "");
                                    int c = Integer.parseInt(totalLike);
                                    if (id1.equals(preferenceManager.getKeyUserId())){
                                        tv_like_detail.setText("You, "+name2+" and "+(c-2)+" other Likes it");
                                    }else if (id2.equals(preferenceManager.getKeyUserId())){
                                        tv_like_detail.setText("You, "+name1+" and "+(c-2)+" other Likes it");
                                    }else {
                                        tv_like_detail.setText(name1+", "+name2+" and "+(c-2)+" other Likes it");
                                    }

                                }
                            }else {
                                tv_like_detail.setText("0 Like");
                            }
                            JsonArray recentComments = json.getAsJsonArray("recentComments");
                            if (recentComments.size() > 0){
                                if (recentComments.size()==1){
                                    JsonObject jo1 = recentComments.get(0).getAsJsonObject();
                                    String name1 = jo1.get("name")+"";
                                    name1 = name1.replace("\"", "");
                                    tv_comname_detail.setText(name1);
                                    String comm1 = jo1.get("comment")+"";
                                    comm1 = comm1.replace("\"", "");
                                    tv_comm_detail.setText(comm1);
                                    String img1 = jo1.get("image")+"";
                                    img1 = img1.replace("\"", "");
                                    Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+img1).error(R.mipmap.user).into(civ_frndpic_detail);
                                    ll_comm2_detail.setVisibility(View.GONE);
                                }else if (recentComments.size() > 1){
                                    JsonObject jo1 = recentComments.get(0).getAsJsonObject();
                                    String name1 = jo1.get("name")+"";
                                    name1 = name1.replace("\"", "");
                                    tv_comname_detail.setText(name1);
                                    String comm1 = jo1.get("comment")+"";
                                    comm1 = comm1.replace("\"", "");
                                    tv_comm_detail.setText(comm1);
                                    String img1 = jo1.get("image")+"";
                                    img1 = img1.replace("\"", "");
                                    Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+img1).error(R.mipmap.user).into(civ_frndpic_detail);

                                    JsonObject jo2 = recentComments.get(1).getAsJsonObject();
                                    String name2 = jo2.get("name")+"";
                                    name2 = name2.replace("\"", "");
                                    tv_comname2_detail.setText(name2);
                                    String comm2 = jo2.get("comment")+"";
                                    comm2 = comm2.replace("\"", "");
                                    tv_comm2_detail.setText(comm2);
                                    String img2 = jo2.get("image")+"";
                                    img2 = img2.replace("\"", "");
                                    Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+img2).error(R.mipmap.user).into(civ_frndpic2_detail);

                                }
                            }else {
                                ll_comm1_detail.setVisibility(View.GONE);
                                ll_comm2_detail.setVisibility(View.GONE);
                            }

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                tv_location_eddetail.setText(address);
                SelfSnapAdapter.locations = address;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
