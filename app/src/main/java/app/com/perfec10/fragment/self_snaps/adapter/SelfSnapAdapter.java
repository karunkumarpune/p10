package app.com.perfec10.fragment.self_snaps.adapter;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendposts.Comments;
import app.com.perfec10.fragment.friendposts.LikeScreen;
import app.com.perfec10.fragment.home.SelfSnaps;
import app.com.perfec10.fragment.measure.Share;
import app.com.perfec10.fragment.self_snaps.SelfSnapDetail;
import app.com.perfec10.model.SelfPostGS;
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

public class SelfSnapAdapter extends RecyclerView.Adapter<SelfSnapAdapter.Holder> implements NetworkCallBack {
    private MainActivity mainActivity;
    private ArrayList<SelfPostGS> postList;
    private Typeface bold, regular;
    private int updatePost;
    private PreferenceManager preferenceManager;
    private Progress progress;
    public static String inputId, postId, tagPerson, locations, age, height, wieght, race, scores;
    private String TAG = "SelfSnapAdapter";

    public SelfSnapAdapter(MainActivity mainActivity, ArrayList<SelfPostGS> postList) {
        this.mainActivity = mainActivity;
        this.postList = postList;

        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        preferenceManager = new PreferenceManager(mainActivity);
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.self_snaps_adapter, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        Log.d(TAG, "inside bind view");
        if (position!=0) {
            holder.ll_header.setVisibility(View.GONE);
        }
        holder.tv_delete_selfrow.setVisibility(View.GONE);
        holder.tv_names_selfrow.setText(preferenceManager.getKey_userName());

            Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+ preferenceManager.getKey_userImg()).
                    error(R.mipmap.user).into(holder.civ_frndpic_selfrow);

        Log.d(TAG, "user img "+NetworkConstants.imageBaseUrl+preferenceManager.getKey_userImg());

        String date1 = postList.get(position).getCreated_at();
        String pic = postList.get(position).getPicture();
        if (pic.equals("1")){
            holder.iv_per10_selfrow.setVisibility(View.GONE);
        }else {
            holder.iv_per10_selfrow.setVisibility(View.VISIBLE);
        }
        /* String date1 = postList.get(position).getCreated_at();
        String[] d = date1.split(" ");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date testDate = null;
        try {
            testDate = sdf.parse(d[0]);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM,");
        String newFormat = formatter.format(testDate);
        System.out.println(".....Date..."+testDate);
        System.out.println(".....Date..."+date1);
        System.out.println(".....Date..."+newFormat);
        String s = d[1].substring(0,5);
        String _24HourTime = s;
        Date _24HourDt = null;
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            _24HourDt = _24HourSDF.parse(_24HourTime);
            s = _12HourSDF.format(_24HourDt);
            System.out.println(_12HourSDF.format(_24HourDt));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        System.out.println(convertUtcTimeToLocal(date1,"yyyy-MM-dd HH:mm:ss"));
        String s = convertUtcTimeToLocal(date1,"yyyy-MM-dd HH:mm:ss");
        Log.d(TAG+" requried date ", convertFormatOfDate(s,
                "yyyy-MM-dd HH:mm:ss","dd MMM yyyy, hh:mm a" ));
        String dat = convertFormatOfDate(s,
                "yyyy-MM-dd HH:mm:ss","dd MMM yyyy, hh:mm a" );
        holder.tv_datetime_selfrow.setText(dat);
        holder.tv_pt1_selfrow.setText(postList.get(position).getBustWaist());
        holder.tv_pt2_selfrow.setText(postList.get(position).getWaistHips());
        holder.tv_pt3_selfrow.setText(postList.get(position).getLegsBody());
        holder.tv_pt4_selfrow.setText(postList.get(position).getBodyWaist());
        holder.tv_pt5_selfrow.setText(postList.get(position).getShoulderHips());
        String rating = postList.get(position).getScore();
        rating = rating.replace("\"", "");
        holder.tv_score_selfrow.setText(rating);
        String tagName = postList.get(position).getTagPerson()+"";
        tagName = tagName.replace("\"", "");
        tagName = tagName.replace("@","");
        if (!tagName.equals("null")){
            holder.tv_name_selfrow.setText(tagName);
        }else {
            holder.tv_name_selfrow.setText("");
        }

        String location = postList.get(position).getLocation();
        if (!location.equals("null")){
            location = location.replace("\"", "");
            location = location.replace("\\", "");
            holder.tv_location_selfrow.setText("Snapped at "+location);
        }else {
            holder.tv_location_selfrow.setText("");
        }

        String caption = postList.get(position).getCaption();
        caption = caption.replace("\"", "");
        caption = caption.replace("\\", "");
        if (!caption.equals("null")){
            holder.tv_caption_selfrow.setText(caption);
        }else {
            holder.tv_caption_selfrow.setVisibility(View.GONE);
        }

        String angle = String.valueOf(postList.get(position).getAngle());
        if (angle.equals("0")) {
            holder.tv_viewtype_selfrow.setText("Front View");
        } else if (angle.equals("1")) {
            holder.tv_viewtype_selfrow.setText("Back View");
        }
        String a = postList.get(position).getLiked() + " ";
        Log.e(TAG+" like status ", a);
        if (postList.get(position).getLiked().equals("1")){
           // holder
           holder.tv_sw11_selfrow.setTextColor(Color.BLUE);
            holder.iv_like_selfrow.setColorFilter(ContextCompat.getColor(mainActivity, R.color.fb_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
          }else {
            ColorStateList oldColors =  holder.tv_commcount_selfrow.getTextColors();
            holder.tv_sw11_selfrow.setTextColor(oldColors);
        //   holder.iv_like_frndrow.setColorFilter(ContextCompat.getColor(mainActivity, oldColors), android.graphics.PorterDuff.Mode.MULTIPLY);
          }
        holder.tv_commcount_selfrow.setText(postList.get(position).getTotalComment() + " Comments");
        if (postList.get(position).getTotalLike() == 1) {
            String id = postList.get(position).getLikeId1();
            Log.d(TAG+" like id ", id+" ");
            Log.d(TAG+" preference id ", preferenceManager.getKeyUserId());
            if (id.equals(preferenceManager.getKeyUserId())){
                holder.tv_likename_selfrow.setText(/*postList.get(position).getLikeName1()*/  "You like it");
            }else {
                holder.tv_likename_selfrow.setText(postList.get(position).getLikeName1() +" like it");
            }

        } else if (postList.get(position).getTotalLike() == 2) {
            String id1 = postList.get(position).getLikeId1();
            String id2 = postList.get(position).getLikeId2();

            if (id1.equals(preferenceManager.getKeyUserId())){
                holder.tv_likename_selfrow.setText( "You and " + postList.get(position).getLikeName2() + " like it");
            }else if (id2.equals(preferenceManager.getKeyUserId())){
                holder.tv_likename_selfrow.setText( "You and " + postList.get(position).getLikeName1() + " like it");
            }else {
                holder.tv_likename_selfrow.setText( postList.get(position).getLikeName1()+", " + postList.get(position).getLikeName2() + " like it");
            }
        } else if (postList.get(position).getTotalLike() > 2) {

            String id1 = postList.get(position).getLikeId1();
            String id2 = postList.get(position).getLikeId2();

            if (id1.equals(preferenceManager.getKeyUserId())){
                holder.tv_likename_selfrow.setText( "You, " + postList.get(position).getLikeName2()+" and " + (postList.get(position).getTotalLike() - 2) + " other like it");
            }else if (id2.equals(preferenceManager.getKeyUserId())){
                holder.tv_likename_selfrow.setText( "You, " + postList.get(position).getLikeName1()+" and " + (postList.get(position).getTotalLike() - 2) + " other like it");
            }else {
                holder.tv_likename_selfrow.setText( postList.get(position).getLikeName1()+", " + postList.get(position).getLikeName2()+" and " + (postList.get(position).getTotalLike() - 2) + " other like it");
            }

             } else {
            holder.tv_likename_selfrow.setText("0 likes");
        }

        String ratin = postList.get(position).getScore();
        ratin = ratin.replace("\"", "");
        if (ratin.equals("null")){
            ratin="0";
        }
        final Double score = Double.parseDouble(ratin+"");
        if (score==0.5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==1){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
        }else if (score==1.5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==2){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
        }else if (score==2.5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==3){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
        }else if (score==3.5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==4){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
        }else if (score==4.5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
        }else if (score==5.5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_selfrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==6){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_selfrow.setImageResource(R.mipmap.star_fill);
        }else if (score==6.5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_selfrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==7){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_selfrow.setImageResource(R.mipmap.star_fill);
        }else if (score==7.5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_selfrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==8){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_selfrow.setImageResource(R.mipmap.star_fill);
        }else if (score==8.5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc9_selfrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==9){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc9_selfrow.setImageResource(R.mipmap.star_fill);
        }else if (score==9.5){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc9_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc10_selfrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==10){
            holder.iv_sc1_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc9_selfrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc10_selfrow.setImageResource(R.mipmap.star_fill);
        }else {

        }
        holder.ll_like_selfrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Network.isConnected(mainActivity)){
                    progress.show();
                    likeDislike(position);
                }else {
                    Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        holder.ll_comm_selfrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new Comments(mainActivity, postList.get(position).getPostId() + ""), "comment");
            }
        });
        holder.ll_share_selfrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputId = postList.get(position).getInputId()+"";
                postId = postList.get(position).getPostId()+"";
                tagPerson = postList.get(position).getTagPerson()+"";
                tagPerson = tagPerson.replace("\"","");
                tagPerson = tagPerson.replace("\\","");
                locations = postList.get(position).getLocation()+"";
                age = postList.get(position).getAge()+"";
                age = age.replace("\"", "");
                age = age.replace("\\", "");
                height = postList.get(position).getHeight()+"";
                height = height.replace("\"", "");
                height = height.replace("\\", "");
                wieght = postList.get(position).getWeight()+"";
                wieght = wieght.replace("\"", "");
                wieght = wieght.replace("\\", "");
                race = postList.get(position).getRace()+"";
                race = race.replace("\\", "");
                String rating = postList.get(position).getScore();
                rating = rating.replace("\"", "");
                scores = rating;
                String caption = postList.get(position).getCaption();
                caption = caption.replace("\"", "");
                String location = postList.get(position).getLocation();
                location = location.replace("\"", "");
                MainActivity.changeFragment(new Share(mainActivity, postList.get(position).getBustWaist(),
                        postList.get(position).getWaistHips(), postList.get(position).getLegsBody(),
                        postList.get(position).getBodyWaist(), postList.get(position).getShoulderHips(),
                        rating, postList.get(position).getAngle()+"",
                        Float.parseFloat(rating), "", caption, location, "adapter"), "share");
            }
        });
        holder.tv_commcount_selfrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new Comments(mainActivity, postList.get(position).getPostId() + ""), "comment");
            }
        });
        holder.tv_likename_selfrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new LikeScreen(mainActivity, postList.get(position).getPostId() + ""), "comment");
            }
        });
        holder.ll_detail_selfrow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                inputId = postList.get(position).getInputId()+"";
                postId = postList.get(position).getPostId()+"";
                tagPerson = postList.get(position).getTagPerson()+"";
                tagPerson = tagPerson.replace("\"","");
                if (tagPerson.equals("null")){
                    tagPerson = "";
                }
                locations = postList.get(position).getLocation()+"";
                locations = locations.replace("\"", "");
                if (locations.equals("null")){
                    locations = "";
                }
                age = postList.get(position).getAge()+"";
                age = age.replace("\"", "");
                if (age.equals("null")){
                    age = "";
                }
                height = postList.get(position).getHeight()+"";
                height = height.replace("\"", "");
                if (height.equals("null")){
                    height = "";
                }
                wieght = postList.get(position).getWeight()+"";
                wieght = wieght.replace("\"", "");
                if (wieght.equals("null")){
                    wieght = "";
                }
                race = postList.get(position).getRace()+"";
                race = race.replace("\"", "");
                if (race.equals("null")){
                    race = "";
                }
                String rating = postList.get(position).getScore();
                rating = rating.replace("\"", "");
                scores = rating;
                /*FragmentTransaction ft = mainActivity.getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.popin, R.anim.popin);*/
                MainActivity.changeFragment(new SelfSnapDetail(mainActivity, holder.tv_datetime_selfrow.getText().toString(),
                        postList.get(position).getCaption(), postList.get(position).getLocation(),
                        postList.get(position).getBustWaist(), postList.get(position).getWaistHips(),
                        postList.get(position).getLegsBody(), postList.get(position).getBodyWaist(),
                        postList.get(position).getShoulderHips(),score,
                        postList.get(position).getTotalLike()+"",postList.get(position).getTotalComment()+"",
                        postList.get(position).getPostId()+"", ""+postList.get(position).getAngle(),
                        postList.get(position).getLiked()), "snapdetail");
            }
        });
        holder.iv_menu_selfrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean show = false;
                if (holder.tv_delete_selfrow.getVisibility()==View.VISIBLE){
                    holder.tv_delete_selfrow.setVisibility(View.GONE);

                }else {
                    holder.tv_delete_selfrow.setVisibility(View.VISIBLE);

                }

            }
        });
        holder.tv_delete_selfrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePost = position;
                deletePost(postList.get(position).getPostId()+"");
            }
        });
    }
    public static String convertUtcTimeToLocal(String dateTimeString, String currentFormat) {

        SimpleDateFormat df = new SimpleDateFormat(currentFormat, Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df.setTimeZone(TimeZone.getDefault());

        return df.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertFormatOfDate(String date , String currentFormat ,
                                             String convertedFormat) {

        Log.d("tag", "Inside convertFormatOfDate()");

        SimpleDateFormat sdf1 = new SimpleDateFormat(currentFormat, Locale.US);
        SimpleDateFormat sdf2 = new SimpleDateFormat(convertedFormat, Locale.US);

        try {
            return sdf2.format(sdf1.parse(date));
        } catch (ParseException e) {

            e.printStackTrace();
        }

        Log.d("tag", "Outside convertFormatOfDate()");

        return null;
    }
    public void deletePost(String postId){
        if (Network.isConnected(mainActivity)){
            JsonObject jsonObject = new JsonObject();
            List<Integer> pId = new ArrayList<Integer>();
            pId.add(Integer.parseInt(postId));
            Gson gson = new Gson();
            jsonObject.add("post_id", gson.toJsonTree(pId));
            Log.d("params delte post ", jsonObject+" ");
            progress.show();
            Network.hitPostApiWithAuth(mainActivity,jsonObject, this,NetworkConstants.deleteUserPost, 2);
        }else {
            Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
        }
    }
    public void likeDislike(int position) {
        updatePost = position;
        if (Network.isConnected(mainActivity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("post_id", postList.get(position).getPostId());
            if (postList.get(position).getLiked().equals("1")){

                jsonObject.addProperty("like_dislike_type", "0");
            }else if (postList.get(position).getLiked().equals("0")){
                Log.e(TAG+" YYYYYYYYYYyy null ",preferenceManager.getKey_userName() );
                postList.get(position).setLikeName1(preferenceManager.getKey_userName());
                jsonObject.addProperty("like_dislike_type", "1");
            }
            Log.d(TAG+" params like ", jsonObject + " ");
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.postLike, 1);
        } else {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response ", data + " ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode==1){
                        Log.d(TAG+" reponse like", jsonObject + " ");

                        message = returnEmptyString(jsonObject.get("message"));

                        if (message.equals("Liked successfully.")) {
                            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                            //    Holder holder = new Holder();
                            int c = postList.get(updatePost).getTotalLike();
                            postList.get(updatePost).setTotalLike(c + 1);
                            postList.get(updatePost).setLiked("1");
                            postList.get(updatePost).setLikeId1(preferenceManager.getKeyUserId());
                            notifyDataSetChanged();
                        }
                        if (message.equals("Disliked successfully.")) {
                            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                            postList.get(updatePost).setLiked("0");
                            int c = postList.get(updatePost).getTotalLike();
                            postList.get(updatePost).setTotalLike(c - 1);
                            notifyDataSetChanged();
                        }
                    }else if (requestCode==2){
                        Log.d(TAG, "response delete "+jsonObject);
                        message = returnEmptyString(jsonObject.get("message"));
                        Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                        postList.remove(updatePost);
                        notifyDataSetChanged();
                        if (postList.size()==0){
                            SelfSnaps.ll_header.setVisibility(View.VISIBLE);
                            SelfSnaps.tv_sw3_selfpost.setVisibility(View.VISIBLE);
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
                Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mainActivity, "Network Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onError(String msg) {

    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tv_sw1_selfrow, tv_sw2_selfrow, tv_sw3_selfrow, tv_sw4_selfrow, tv_sw5_selfrow,
                tv_sw6_selfrow, tv_sw7_selfrow, tv_sw8_selfrow, tv_sw9_selfrow, tv_sw10_selfrow,
                tv_sw11_selfrow, tv_sw12_selfrow, tv_sw13_selfrow, tv_caption_selfrow, tv_name_selfrow,
                tv_location_selfrow, tv_date_selfrow, tv_pt1_selfrow, tv_pt2_selfrow, tv_pt3_selfrow,
                tv_pt4_selfrow, tv_pt5_selfrow, tv_score_selfrow, tv_viewtype_selfrow, tv_likename_selfrow,
                tv_commcount_selfrow, tv_names_selfrow, tv_datetime_selfrow, tv_delete_selfrow;
        private ImageView iv_sc1_selfrow, iv_sc2_selfrow, iv_sc3_selfrow, iv_sc4_selfrow, iv_sc5_selfrow,
                iv_sc6_selfrow, iv_sc7_selfrow, iv_sc8_selfrow, iv_sc9_selfrow, iv_sc10_selfrow,
                iv_like_selfrow, iv_per10_selfrow, iv_menu_selfrow;
        private LinearLayout ll_header, ll_like_selfrow, ll_comm_selfrow, ll_share_selfrow, ll_detail_selfrow;
        private CircleImageView civ_frndpic_selfrow;

        public Holder(View itemView) {
            super(itemView);
            tv_sw1_selfrow = (TextView) itemView.findViewById(R.id.tv_sw1_selfrow);
            tv_sw2_selfrow = (TextView) itemView.findViewById(R.id.tv_sw2_selfrow);
            tv_sw3_selfrow = (TextView) itemView.findViewById(R.id.tv_sw3_selfrow);
            tv_sw4_selfrow = (TextView) itemView.findViewById(R.id.tv_sw4_selfrow);
            tv_sw5_selfrow = (TextView) itemView.findViewById(R.id.tv_sw5_selfrow);
            tv_sw6_selfrow = (TextView) itemView.findViewById(R.id.tv_sw6_selfrow);
            tv_sw7_selfrow = (TextView) itemView.findViewById(R.id.tv_sw7_selfrow);
            tv_sw8_selfrow = (TextView) itemView.findViewById(R.id.tv_sw8_selfrow);
            tv_sw9_selfrow = (TextView) itemView.findViewById(R.id.tv_sw9_selfrow);
            tv_sw10_selfrow = (TextView) itemView.findViewById(R.id.tv_sw10_selfrow);
            tv_sw11_selfrow = (TextView) itemView.findViewById(R.id.tv_sw11_selfrow);
            tv_sw12_selfrow = (TextView) itemView.findViewById(R.id.tv_sw12_selfrow);
            tv_sw13_selfrow = (TextView) itemView.findViewById(R.id.tv_sw13_selfrow);
            tv_caption_selfrow = (TextView) itemView.findViewById(R.id.tv_caption_selfrow);
            tv_name_selfrow = (TextView) itemView.findViewById(R.id.tv_name_selfrow);
            tv_location_selfrow = (TextView) itemView.findViewById(R.id.tv_location_selfrow);
            tv_date_selfrow = (TextView) itemView.findViewById(R.id.tv_date_selfrow);
            tv_pt1_selfrow = (TextView) itemView.findViewById(R.id.tv_pt1_selfrow);
            tv_pt2_selfrow = (TextView) itemView.findViewById(R.id.tv_pt2_selfrow);
            tv_pt3_selfrow = (TextView) itemView.findViewById(R.id.tv_pt3_selfrow);
            tv_pt4_selfrow = (TextView) itemView.findViewById(R.id.tv_pt4_selfrow);
            tv_pt5_selfrow = (TextView) itemView.findViewById(R.id.tv_pt5_selfrow);
            tv_score_selfrow = (TextView) itemView.findViewById(R.id.tv_score_selfrow);
            tv_viewtype_selfrow = (TextView) itemView.findViewById(R.id.tv_viewtype_selfrow);
            tv_likename_selfrow = (TextView) itemView.findViewById(R.id.tv_likename_selfrow);
            tv_commcount_selfrow = (TextView) itemView.findViewById(R.id.tv_commcount_selfrow);
            tv_names_selfrow = (TextView) itemView.findViewById(R.id.tv_names_selfrow);
            tv_datetime_selfrow = (TextView) itemView.findViewById(R.id.tv_datetime_selfrow);
            tv_delete_selfrow = (TextView) itemView.findViewById(R.id.tv_delete_selfrow);

            iv_sc1_selfrow = (ImageView) itemView.findViewById(R.id.iv_sc1_selfrow);
            iv_sc2_selfrow = (ImageView) itemView.findViewById(R.id.iv_sc2_selfrow);
            iv_sc3_selfrow = (ImageView) itemView.findViewById(R.id.iv_sc3_selfrow);
            iv_sc4_selfrow = (ImageView) itemView.findViewById(R.id.iv_sc4_selfrow);
            iv_sc5_selfrow = (ImageView) itemView.findViewById(R.id.iv_sc5_selfrow);
            iv_sc6_selfrow = (ImageView) itemView.findViewById(R.id.iv_sc6_selfrow);
            iv_sc7_selfrow = (ImageView) itemView.findViewById(R.id.iv_sc7_selfrow);
            iv_sc8_selfrow = (ImageView) itemView.findViewById(R.id.iv_sc8_selfrow);
            iv_sc9_selfrow = (ImageView) itemView.findViewById(R.id.iv_sc9_selfrow);
            iv_sc10_selfrow = (ImageView) itemView.findViewById(R.id.iv_sc10_selfrow);
            iv_like_selfrow = (ImageView) itemView.findViewById(R.id.iv_like_selfrow);
            iv_per10_selfrow = (ImageView) itemView.findViewById(R.id.iv_per10_selfrow);
            iv_menu_selfrow = (ImageView) itemView.findViewById(R.id.iv_menu_selfrow);

            ll_header = (LinearLayout) itemView.findViewById(R.id.ll_header);
            ll_like_selfrow = (LinearLayout) itemView.findViewById(R.id.ll_like_selfrow);
            ll_comm_selfrow = (LinearLayout) itemView.findViewById(R.id.ll_comm_selfrow);
            ll_share_selfrow = (LinearLayout) itemView.findViewById(R.id.ll_share_selfrow);
            ll_detail_selfrow = (LinearLayout) itemView.findViewById(R.id.ll_detail_selfrow);

            civ_frndpic_selfrow = (CircleImageView) itemView.findViewById(R.id.civ_frnd_selfrow);

        }
    }
}
