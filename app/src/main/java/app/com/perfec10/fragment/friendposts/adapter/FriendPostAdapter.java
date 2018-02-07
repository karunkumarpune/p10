package app.com.perfec10.fragment.friendposts.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendposts.Comments;
import app.com.perfec10.fragment.friendposts.LikeScreen;
import app.com.perfec10.helper.CustomVolleyRequestQueue;
import app.com.perfec10.model.FriendsPostGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.CircleImageView;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 4/12/17.
 */

public class FriendPostAdapter extends RecyclerView.Adapter<FriendPostAdapter.Holder>
                                implements NetworkCallBack{
    private MainActivity mainActivity;
    private ArrayList<FriendsPostGS> friendsPost;
    private Typeface bold, regular;
    private PreferenceManager preferenceManager;
    private int updatePost;
    private Progress progress;
    private String TAG = "FriendPostAdapter";

    public FriendPostAdapter(MainActivity mainActivity, ArrayList<FriendsPostGS> friendsPost){
        this.mainActivity = mainActivity;
        this.friendsPost = friendsPost;

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
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.friendpost_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        holder.tv_name_frndrow.setTypeface(bold);
        holder.tv_datetime_frndrow.setTypeface(regular);
        holder.tv_comments_frndrow.setTypeface(regular);
        holder.tv_snapname_frnd_row.setTypeface(bold);
        holder.tv_snapdetail_frnd_row.setTypeface(regular);
        holder.tv_pt1_frndrow.setTypeface(bold);
        holder.tv_pt2_frndrow.setTypeface(bold);
        holder.tv_pt3_frndrow.setTypeface(bold);
        holder.tv_pt4_frndrow.setTypeface(bold);
        holder.tv_pt5_frndrow.setTypeface(bold);
        holder.tv_sw1_frndrow.setTypeface(regular);
        holder.tv_sw2_frndrow.setTypeface(regular);
        holder.tv_sw3_frndrow.setTypeface(regular);
        holder.tv_sw4_frndrow.setTypeface(regular);
        holder.tv_sw5_frndrow.setTypeface(regular);
        holder.tv_sw6_frndrow.setTypeface(regular);
        holder.tv_sw7_frndrow.setTypeface(regular);
        holder.tv_sw8_frndrow.setTypeface(regular);
        holder.tv_sw9_frndrow.setTypeface(regular);
        holder.tv_sw10_frndrow.setTypeface(regular);
        holder.tv_sw11_frndrow.setTypeface(regular);
        holder.tv_sw12_frndrow.setTypeface(regular);
        holder.tv_viewtype_frndrow.setTypeface(bold);
        holder.tv_like_frndrow.setTypeface(regular);
        holder.tv_comment_frndrow.setTypeface(regular);
        holder.tv_comname_frndrow.setTypeface(bold);
        holder.tv_comm_frndrow.setTypeface(regular);
        holder.tv_comname2_frndrow.setTypeface(bold);
        holder.tv_comm2_frndrow.setTypeface(regular);
        holder.tev_comm_frndrow.setTypeface(regular);

        String pic = friendsPost.get(position).getPicture();
        if (pic.equals("0")){
            holder.ll_pictype_frndrow.setBackgroundResource(R.mipmap.perfec10_score);
        }else {
            holder.ll_pictype_frndrow.setBackgroundResource(R.mipmap.other_score);
        }

        holder.tv_name_frndrow.setText(friendsPost.get(position).getName());
        String img = friendsPost.get(position).getImage();
        img = img.replace("\"", "");
        Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+img).error(R.mipmap.user).into(holder.civ_frnd_frndrow);
        Log.d(TAG +" user img ", NetworkConstants.imageBaseUrl+preferenceManager.getKey_userImg());
        Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+preferenceManager.getKey_userImg()).error(R.mipmap.user).into(holder.civ_userpic_frndrow);
        String date1 = friendsPost.get(position).getShare_date();
        /*String[] d = date1.split(" ");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy dd MMM  h:mm:ss", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        String createString = date1;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM, h:mm a");
        Date createdData = null;
        try {
            createdData = simpleDateFormat.parse(createString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

     //   Log.d(" new date ", df.format(createdData)+" ");


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date testDate = null;
        try {
            testDate = sdf.parse(d[0]);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM,");
        String newFormat = formatter.format(testDate);
        System.out.println(".....Date..."+newFormat);
            String s = d[1].substring(0,5);
            String _24HourTime = s;
            Date _24HourDt = null;
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            sdf.setTimeZone(TimeZone.getDefault());
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
        holder.tv_datetime_frndrow.setText(dat);
        String cap = friendsPost.get(position).getCaption();
        cap = cap.replace("\"", "");
        if (!cap.equals("null")){
            holder.tv_comments_frndrow.setText(cap);
        }else {
            holder.tv_comments_frndrow.setVisibility(View.GONE);
        }
        String tag = friendsPost.get(position).getTagPerson();
        tag = tag.replace("\"", "");
        tag = tag.replace("@","");
        if (!tag.equals("null")){
            holder.tv_snapname_frnd_row.setText(tag);
        }else {
            holder.tv_snapname_frnd_row.setText("");
        }

        String location = friendsPost.get(position).getLocation();
        location = location.replace("\"", "");
        if (!location.equals("null")){
            holder.tv_snapdetail_frnd_row.setText("Snapped at "+location);
        }

        holder.tv_pt1_frndrow.setText(friendsPost.get(position).getBustWaist());
        holder.tv_pt2_frndrow.setText(friendsPost.get(position).getWaistHips());
        holder.tv_pt3_frndrow.setText(friendsPost.get(position).getLegsBody());
        holder.tv_pt4_frndrow.setText(friendsPost.get(position).getBodyWaist());
        holder.tv_pt5_frndrow.setText(friendsPost.get(position).getShoulderHips());
        holder.tv_score_frndrow.setText(friendsPost.get(position).getScore());
        String angle = friendsPost.get(position).getAngle();
        if (angle.equals("0")){
            holder.tv_viewtype_frndrow.setText("Front View");
        }else if (angle.equals("1")){
            holder.tv_viewtype_frndrow.setText("Back View");
        }
        holder.tv_comment_frndrow.setText(friendsPost.get(position).getTotalComment()+" Comments");
       Log.d(TAG, "total like "+friendsPost.get(position).getTotalLike());
        if (friendsPost.get(position).getTotalLike()==1){
            String name = friendsPost.get(position).getLikeName();
            name = name.replace("\"", "");
            String id = friendsPost.get(position).getLikeId1();

            if (id.equals(preferenceManager.getKeyUserId())){
                holder.tv_like_frndrow.setText(/*postList.get(position).getLikeName1()*/  "You like it");
            }else {

                holder.tv_like_frndrow.setText(name +" like it");
            }
        }else if (friendsPost.get(position).getTotalLike()==2){
            String name = friendsPost.get(position).getLikeName();
            String name2 = friendsPost.get(position).getLikeName2();
            name = name.replace("\"", "");
            name2 = name2.replace("\"", "");
            String id1 = friendsPost.get(position).getLikeId1();
            String id2 = friendsPost.get(position).getLikeId2();

            if (id1.equals(preferenceManager.getKeyUserId())){
                holder.tv_like_frndrow.setText( "You and " + name2 + " like it");
            }else if (id2.equals(preferenceManager.getKeyUserId())){
                holder.tv_like_frndrow.setText( "You and " + name + " like it");
            }else {
                holder.tv_like_frndrow.setText( name+", " + name2 + " like it");
            }
        }else if (friendsPost.get(position).getTotalLike() > 2){
            String name = friendsPost.get(position).getLikeName();
            name = name.replace("\"", "");
            String name2 = friendsPost.get(position).getLikeName2();
            name2 = name2.replace("\"", "");
            String id1 = friendsPost.get(position).getLikeId1();
            String id2 = friendsPost.get(position).getLikeId2();

            if (id1.equals(preferenceManager.getKeyUserId())){
                holder.tv_like_frndrow.setText( "You, " + name2+" and " + (friendsPost.get(position).getTotalLike() - 2) + " other like it");
            }else if (id2.equals(preferenceManager.getKeyUserId())){
                holder.tv_like_frndrow.setText( "You, " + name+" and " + (friendsPost.get(position).getTotalLike() - 2) + " other like it");
            }else {
                holder.tv_like_frndrow.setText( name+", " + name2+" and " + (friendsPost.get(position).getTotalLike() - 2) + " other like it");
            }
        }else {
            holder.tv_like_frndrow.setText("0 likes");
        }
        if(friendsPost.get(position).getTotalComment()==1){
            holder.ll_comm2_frndrow.setVisibility(View.GONE);
            String comm1 = friendsPost.get(position).getCommComm1();
            comm1 = comm1.replace("\"", "");
            holder.tv_comm_frndrow.setText(comm1);
            String name1 = friendsPost.get(position).getCommName1();
            name1 = name1.replace("\"", "");
            holder.tv_comname_frndrow.setText(name1);
            String image = friendsPost.get(position).getCommImg1();
            image = image = image.replace("\"", "");
            Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+image).error(R.mipmap.user).into(holder.civ_frndpic_frndrow);

        }else if (friendsPost.get(position).getTotalComment() > 1){
            String comm1 = friendsPost.get(position).getCommComm1();
            comm1 = comm1.replace("\"", "");
            holder.tv_comm_frndrow.setText(comm1);
            String name1 = friendsPost.get(position).getCommName1();
            name1 = name1.replace("\"", "");
            holder.tv_comname_frndrow.setText(name1);
            String image1 = friendsPost.get(position).getCommImg1();
            image1 = image1.replace("\"", "");
            Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+image1).error(R.mipmap.user).into(holder.civ_frndpic_frndrow);

            String comm2 = friendsPost.get(position).getCommComm2();
            comm2 = comm2.replace("\"", "");
            holder.tv_comm2_frndrow.setText(comm2);
            String name2 = friendsPost.get(position).getCommName2();
            name2 = name2.replace("\"", "");
            holder.tv_comname2_frndrow.setText(name2);
            String image2 = friendsPost.get(position).getCommImg2();
            image2 = image2.replace("\"", "");
            Picasso.with(mainActivity).load(NetworkConstants.imageBaseUrl+image2).error(R.mipmap.user).into(holder.civ_frndpic2_frndrow);

        }else if (friendsPost.get(position).getTotalComment()==0){
            holder.ll_comm1_frndrow.setVisibility(View.GONE);
            holder.ll_comm2_frndrow.setVisibility(View.GONE);
        }
        if (friendsPost.get(position).getLiked() != null){
            if (friendsPost.get(position).getLiked().equals("1")){

                holder.tv_sw11_frndrow.setTextColor(Color.BLUE);
                holder.iv_like_frndrow.setColorFilter(ContextCompat.getColor(mainActivity, R.color.fb_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
            }else {
                ColorStateList oldColors =  holder.tv_sw12_frndrow.getTextColors();
                holder.tv_sw11_frndrow.setTextColor(oldColors);
                //   holder.iv_like_frndrow.setColorFilter(ContextCompat.getColor(mainActivity, oldColors), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }

         String rating = friendsPost.get(position).getScore();
        Double score = null;
        try {
            score = Double.parseDouble(rating+"");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (score==0.5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==1){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
        }else if (score==1.5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==2){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
        }else if (score==2.5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==3){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
        }else if (score==3.5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==4){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
        }else if (score==4.5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
        }else if (score==5.5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_frndrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==6){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_frndrow.setImageResource(R.mipmap.star_fill);
        }else if (score==6.5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_frndrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==7){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_frndrow.setImageResource(R.mipmap.star_fill);
        }else if (score==7.5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_frndrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==8){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_frndrow.setImageResource(R.mipmap.star_fill);
        }else if (score==8.5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc9_frndrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==9){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc9_frndrow.setImageResource(R.mipmap.star_fill);
        }else if (score==9.5){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc9_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc10_frndrow.setImageResource(R.mipmap.star_half_fill);
        }else if (score==10){
            holder.iv_sc1_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc2_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc3_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc4_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc5_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc6_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc7_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc8_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc9_frndrow.setImageResource(R.mipmap.star_fill);
            holder.iv_sc10_frndrow.setImageResource(R.mipmap.star_fill);
        }else {

        }
        holder.tv_comment_frndrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new Comments(mainActivity, ""+friendsPost.get(position).getPostId()), "comment");
            }
        });
        holder.tv_like_frndrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            MainActivity.changeFragment(new LikeScreen(mainActivity, ""+friendsPost.get(position).getPostId()), "like");
            }
        });
        holder.ll_comment_frndrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new Comments(mainActivity, friendsPost.get(position).getPostId()+""),"comment");
            }
        });
        holder.ll_like_frndrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Network.isConnected(mainActivity)){
                    progress.show();
                    likePost(position);
                }else {
                    Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
                }

                }
        });
        holder.ll_addcomm_frndrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new Comments(mainActivity, ""+friendsPost.get(position).getPostId()), "commentt");
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

    public void likePost(int position){
        updatePost = position;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", friendsPost.get(position).getPostId());
        if (friendsPost.get(position).getLiked() != null){
            if (friendsPost.get(position).getLiked().equals("1")){

                jsonObject.addProperty("like_dislike_type", "0");
            }else if (friendsPost.get(position).getLiked().equals("0")){
               // friendsPost.get(position).setLikeName(preferenceManager.getKey_userName());

                jsonObject.addProperty("like_dislike_type", "1");
            }
        }else {
            jsonObject.addProperty("like_dislike_type", "1");
        }


        Log.d(TAG+" params like ", jsonObject+" ");

        Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.postLike,1);

    }

    @Override
    public int getItemCount() {
        return friendsPost.size();
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {

        Log.d(TAG+" response like ", data+" ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    Log.d(TAG+" reponse post", jsonObject+" ");

                    message = returnEmptyString(jsonObject.get("message"));

                    if (message.equals("Liked successfully.")){
                        Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                    //    Holder holder = new Holder();
                        int totalLike = jsonObject.get("totalLike").getAsInt();
                        int c = totalLike;
                        friendsPost.get(updatePost).setTotalLike(c);
                        friendsPost.get(updatePost).setLiked("1");
                      //
                        if (c==1){
                            friendsPost.get(updatePost).setLikeName(preferenceManager.getKey_userName());
                            friendsPost.get(updatePost).setLikeId1(preferenceManager.getKeyUserId());
                        }

                        if (c==2 || c > 2){

                            String nam1 = friendsPost.get(updatePost).getLikeName();
                            Log.d(TAG, "name of 1 like "+nam1);
                            String id1 = friendsPost.get(updatePost).getLikeId1();
                            friendsPost.get(updatePost).setLikeName2(nam1);
                            friendsPost.get(updatePost).setLikeId2(id1);
                            friendsPost.get(updatePost).setLikeName("You");
                            friendsPost.get(updatePost).setLikeId1(preferenceManager.getKeyUserId());
                        }
                        Log.d(TAG+" updated frnd list ", friendsPost.get(updatePost).getLikeName()+" "+friendsPost.get(updatePost).getLikeName2());
                        notifyDataSetChanged();
                    }
                    if (message.equals("Disliked successfully.")){
                        Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                        friendsPost.get(updatePost).setLiked("0");
                        int totalLike = jsonObject.get("totalLike").getAsInt();
                        int c = totalLike;
                        Log.d(TAG+"dislike updated frnd ", friendsPost.get(updatePost).getLikeName()+" "+friendsPost.get(updatePost).getLikeName2());
                        if (c==1){
                            friendsPost.get(updatePost).setLikeName(friendsPost.get(updatePost).getLikeName2());
                            friendsPost.get(updatePost).setLikeId1(friendsPost.get(updatePost).getLikeId2());
                        }
                        if (c > 2 || c== 2){
                            Log.d(TAG, "dislike name 1 "+friendsPost.get(updatePost).getLikeName());
                            Log.d(TAG, "dislike name 2 "+friendsPost.get(updatePost).getLikeName2());
                            friendsPost.get(updatePost).setLikeName(friendsPost.get(updatePost).getLikeName2());
                            friendsPost.get(updatePost).setLikeId1(friendsPost.get(updatePost).getLikeId2());
                        }
                        friendsPost.get(updatePost).setTotalLike(c);

                        notifyDataSetChanged();
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

    public class Holder extends RecyclerView.ViewHolder{
    private CircleImageView civ_frnd_frndrow, civ_frndpic_frndrow, civ_userpic_frndrow, civ_frndpic2_frndrow;
    private TextView tv_name_frndrow, tv_datetime_frndrow, tv_comments_frndrow, tv_snapdetail_frnd_row,
            tv_pt1_frndrow, tv_pt2_frndrow, tv_pt3_frndrow, tv_pt4_frndrow, tv_pt5_frndrow,tv_sw1_frndrow,
            tv_sw2_frndrow, tv_sw3_frndrow,tv_sw4_frndrow,tv_sw5_frndrow,tv_sw6_frndrow,tv_sw7_frndrow,
            tv_sw8_frndrow,tv_sw9_frndrow,tv_sw10_frndrow,tv_sw11_frndrow,tv_sw12_frndrow, tv_score_frndrow
            ,tv_viewtype_frndrow, tv_comname_frndrow, tv_comm_frndrow, tv_snapname_frnd_row,
            tv_like_frndrow, tv_comment_frndrow, tv_comname2_frndrow, tv_comm2_frndrow, tev_comm_frndrow;

    private LinearLayout ll_comm1_frndrow, ll_comm2_frndrow, ll_comment_frndrow, ll_like_frndrow,
            ll_addcomm_frndrow, ll_pictype_frndrow;
    private ImageView iv_sc1_frndrow, iv_sc2_frndrow, iv_sc3_frndrow, iv_sc4_frndrow,
            iv_sc5_frndrow, iv_sc6_frndrow, iv_sc7_frndrow, iv_sc8_frndrow, iv_sc9_frndrow,
            iv_sc10_frndrow, iv_like_frndrow;

        public Holder(View itemView) {
            super(itemView);
            civ_frnd_frndrow = (CircleImageView) itemView.findViewById(R.id.civ_frnd_frndrow);
            civ_frndpic_frndrow = (CircleImageView) itemView.findViewById(R.id.civ_frndpic_frndrow);
            civ_frndpic2_frndrow = (CircleImageView) itemView.findViewById(R.id.civ_frndpic2_frndrow);
            civ_userpic_frndrow = (CircleImageView) itemView.findViewById(R.id.civ_userpic_frndrow);

            tv_name_frndrow = (TextView) itemView.findViewById(R.id.tv_name_frndrow);
            tv_datetime_frndrow = (TextView) itemView.findViewById(R.id.tv_datetime_frndrow);
            tv_comments_frndrow = (TextView) itemView.findViewById(R.id.tv_comments_frndrow);
            tv_snapdetail_frnd_row = (TextView) itemView.findViewById(R.id.tv_snapdetail_frnd_row);
            tv_pt1_frndrow = (TextView) itemView.findViewById(R.id.tv_pt1_frndrow);
            tv_pt2_frndrow = (TextView) itemView.findViewById(R.id.tv_pt2_frndrow);
            tv_pt3_frndrow = (TextView) itemView.findViewById(R.id.tv_pt3_frndrow);
            tv_pt4_frndrow = (TextView) itemView.findViewById(R.id.tv_pt4_frndrow);
            tv_pt5_frndrow = (TextView) itemView.findViewById(R.id.tv_pt5_frndrow);
            tv_sw1_frndrow = (TextView) itemView.findViewById(R.id.tv_sw1_frndrow);
            tv_sw2_frndrow = (TextView) itemView.findViewById(R.id.tv_sw2_frndrow);
            tv_sw3_frndrow = (TextView) itemView.findViewById(R.id.tv_sw3_frndrow);
            tv_sw4_frndrow = (TextView) itemView.findViewById(R.id.tv_sw4_frndrow);
            tv_sw5_frndrow = (TextView) itemView.findViewById(R.id.tv_sw5_frndrow);
            tv_sw6_frndrow = (TextView) itemView.findViewById(R.id.tv_sw6_frndrow);
            tv_sw7_frndrow = (TextView) itemView.findViewById(R.id.tv_sw7_frndrow);
            tv_sw8_frndrow = (TextView) itemView.findViewById(R.id.tv_sw8_frndrow);
            tv_sw9_frndrow = (TextView) itemView.findViewById(R.id.tv_sw9_frndrow);
            tv_sw10_frndrow = (TextView) itemView.findViewById(R.id.tv_sw10_frndrow);
            tv_sw11_frndrow = (TextView) itemView.findViewById(R.id.tv_sw11_frndrow);
            tv_sw12_frndrow = (TextView) itemView.findViewById(R.id.tv_sw12_frndrow);
            tv_score_frndrow = (TextView) itemView.findViewById(R.id.tv_score_frndrow);
            tv_viewtype_frndrow = (TextView) itemView.findViewById(R.id.tv_viewtype_frndrow);
            tv_comname_frndrow = (TextView) itemView.findViewById(R.id.tv_comname_frndrow);
            tv_comm_frndrow = (TextView) itemView.findViewById(R.id.tv_comm_frndrow);
            tv_snapname_frnd_row = (TextView) itemView.findViewById(R.id.tv_snapname_frnd_row);
            tv_like_frndrow = (TextView) itemView.findViewById(R.id.tv_like_frndrow);
            tv_comment_frndrow = (TextView) itemView.findViewById(R.id.tv_comment_frndrow);
            tv_comname2_frndrow = (TextView) itemView.findViewById(R.id.tv_comname2_frndrow);
            tv_comm2_frndrow = (TextView) itemView.findViewById(R.id.tv_comm2_frndrow);
            tev_comm_frndrow = (TextView) itemView.findViewById(R.id.et_comm_frndrow);

            ll_comm1_frndrow = (LinearLayout) itemView.findViewById(R.id.ll_comm1_frndrow);
            ll_comm2_frndrow = (LinearLayout) itemView.findViewById(R.id.ll_comm2_frndrow);
            ll_comment_frndrow = (LinearLayout) itemView.findViewById(R.id.ll_comment_frndrow);
            ll_like_frndrow = (LinearLayout) itemView.findViewById(R.id.ll_like_frndrow);
            ll_addcomm_frndrow = (LinearLayout) itemView.findViewById(R.id.ll_addcomm_frndrow);
            ll_pictype_frndrow = (LinearLayout) itemView.findViewById(R.id.ll_pictype_frndrow);

            iv_sc1_frndrow = (ImageView) itemView.findViewById(R.id.iv_sc1_frndrow);
            iv_sc2_frndrow = (ImageView) itemView.findViewById(R.id.iv_sc2_frndrow);
            iv_sc3_frndrow = (ImageView) itemView.findViewById(R.id.iv_sc3_frndrow);
            iv_sc4_frndrow = (ImageView) itemView.findViewById(R.id.iv_sc4_frndrow);
            iv_sc5_frndrow = (ImageView) itemView.findViewById(R.id.iv_sc5_frndrow);
            iv_sc6_frndrow = (ImageView) itemView.findViewById(R.id.iv_sc6_frndrow);
            iv_sc7_frndrow = (ImageView) itemView.findViewById(R.id.iv_sc7_frndrow);
            iv_sc8_frndrow = (ImageView) itemView.findViewById(R.id.iv_sc8_frndrow);
            iv_sc9_frndrow = (ImageView) itemView.findViewById(R.id.iv_sc9_frndrow);
            iv_sc10_frndrow = (ImageView) itemView.findViewById(R.id.iv_sc10_frndrow);
            iv_like_frndrow = (ImageView) itemView.findViewById(R.id.iv_like_frndrow);
        }
    }
}
