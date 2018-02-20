package app.com.perfec10.fragment.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.measure.Stats;
import app.com.perfec10.fragment.measure.StatsInput;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.model.Models;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 25/10/17.
 */

@SuppressLint("ValidFragment")
public class Home extends Fragment implements NetworkCallBack{
    private MainActivity mainActivity;
  //  private int[] tabIcon = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
    @BindView(R.id.tab_home) TabLayout tab_home;
    @BindView(R.id.vp_home) ViewPager vp_home;
    @BindView(R.id.ll_first_home) LinearLayout ll_first_home;
    Uri outPutfileUri;
    static int TAKE_PIC = 1;
    private View view;
    public static String editUserId = "";
    private String TAG = "Home";
    private PreferenceManager preferenceManager;
    private static Home instance = null;

    @SuppressLint("ValidFragment")
    public Home(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(mainActivity);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.home, container, false);
            ButterKnife.bind(this, view);

            initiView(view);
            clcikListner();
        }

       // takeImageFromCamera();
        Log.d(TAG+" on create view ", "");
        Profile.logout = "0";
        String s = "["+"2"+"]";
        Log.d(TAG+" string ", s);

        return view;
    }

    @Override
    public void onResume() {

        Log.d(TAG+" on resume ", "is called home"+ CropZoomScreen.backFrom);
      //  try {
            if (CropZoomScreen.backFrom.equals("stats")){
                CropZoomScreen.backFrom = "imputImage";
                if (Stats.updatePost){
                    Log.d(TAG+" inside ", "if home post updated ");
                }else {
                    Log.d(TAG+" inside ", "else home post not updated ");
                    updatePost();

                }
            }

        /*} catch (Exception e) {
            e.printStackTrace();
        }*/
        super.onResume();
    }

    public void initiView(View view){

        if (preferenceManager.getKey_cameraWalkthrough().equalsIgnoreCase("1")){
            ll_first_home.setVisibility(View.GONE);
        }else {
            ll_first_home.setVisibility(View.VISIBLE);
        }

        InputMethodManager imm = (InputMethodManager)mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        setupViewPager(vp_home);
        vp_home.setOffscreenPageLimit(0);
      //  start_camera();
       // mCamera.takePicture(null, null, mPictureCallback);
        //tab_home.setupWithViewPager(vp_home);
        TabLayout.Tab firstTab = tab_home.newTab();
        TabLayout.Tab secondTab = tab_home.newTab();
        TabLayout.Tab thirdTab = tab_home.newTab();
        TabLayout.Tab fourTab = tab_home.newTab();

        firstTab.setIcon(R.mipmap.home);
        secondTab.setIcon(R.mipmap.frnd_post);
        thirdTab.setIcon(R.mipmap.my_snap);
        fourTab.setIcon(R.mipmap.profile);

        tab_home.addTab(firstTab);
        tab_home.addTab(secondTab);
        tab_home.addTab(thirdTab);
        tab_home.addTab(fourTab);

       /* LinearLayout tabOne = (LinearLayout) LayoutInflater.from(mainActivitySignUP).inflate(R.layout.custom_hometab, null);
        tab_home.getTabAt(0).setCustomView(tabOne);
        LinearLayout tabTwo = (LinearLayout) LayoutInflater.from(mainActivitySignUP).inflate(R.layout.custom_hometab, null);
        tab_home.getTabAt(1).setCustomView(tabTwo);
        LinearLayout tabThree = (LinearLayout) LayoutInflater.from(mainActivitySignUP).inflate(R.layout.custom_hometab, null);
        tab_home.getTabAt(2).setCustomView(tabThree);
        LinearLayout tabFour = (LinearLayout) LayoutInflater.from(mainActivitySignUP).inflate(R.layout.custom_hometab, null);
        tab_home.getTabAt(3).setCustomView(tabFour);*/
    }

    public void clcikListner(){
        ll_first_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_first_home.setVisibility(View.GONE);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("cameraWalkthrough", "1");
                    jsonObject.put("shareWalkthrough", "");
                    jsonObject.put("statsWalkthrough", "");
                    Log.d(TAG+" params of walk through", jsonObject+" ");
                    preferenceManager.setKey_cameraWalkthrough("1");
                    if (Network.isConnected(mainActivity)){
                        walkThrough(jsonObject.toString());

                    }else {
                        Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        tab_home.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
               /* if(tab.getPosition()==0){

                }
                if (tab.getPosition()==3){
                    btn_logout_home.setVisibility(View.VISIBLE);
                }else {
                    btn_logout_home.setVisibility(View.GONE);
                }*/

               if (MainActivity.save){
                    Log.d(TAG+" save not ", "in profile ");
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
                               try {
                                   vp_home.setCurrentItem(tab.getPosition());
                               }catch (Exception e){}
                               MainActivity.save = false;
                           }
                       });
                       // Showing Alert Message
                       alertDialog.show();

               }else {

                   try {
                       vp_home.setCurrentItem(tab.getPosition());
                   }catch (Exception e){}
               }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab) {
                if (MainActivity.save){
                    Log.d(TAG+" save not ", "in profile ");
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
                            try {
                                vp_home.setCurrentItem(tab.getPosition());
                            }catch (Exception e){}
                            MainActivity.save = false;
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();

                }else {

                    try {
                        vp_home.setCurrentItem(tab.getPosition());
                    }catch (Exception e){}
                }
            }
        });
        vp_home.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tab_home.setScrollPosition(position, positionOffset, false);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new HomeCamera(mainActivity), "ONE");
       // adapter.addFragment(new CameraNew(mainActivitySignUP), "ONE");
        adapter.addFragment(new FriendsPost(), "TWO");
        adapter.addFragment(new SelfSnaps(), "THREE");
        adapter.addFragment(new SelfSnaps(), "THREE");
        viewPager.setAdapter(adapter);

    }

    public void updatePost(){
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("input_id",Stats.input_id);
            jsonObject.addProperty("post_id",Stats.post_id);
            if (StatsInput.et_tag_stats !=null){
                String tag = StatsInput.et_tag_stats.getText().toString();
                tag = tag.replace("@", "");
                jsonObject.addProperty("tag_person", tag);
            }else {

                jsonObject.addProperty("tag_person", "");
            }
            jsonObject.addProperty("location", "");
            if (Stats.age != null){

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

            jsonObject.addProperty("caption", "");
            List<Integer> fid = new ArrayList<Integer>();
            Gson gson = new Gson();
            jsonObject.add("friends_id",gson.toJsonTree(fid));
            jsonObject.add("groups_id",gson.toJsonTree(fid));
            Stats stats = new Stats();
            String s = stats.newScore;
            float a = Stats.rb_stats.getRating();
            Log.d(TAG+" back home rate user ", a+" ");
            Log.d(TAG+" back home rate given", Stats.score+" ");
            Log.d(TAG+" back home rate main", mainActivity.userRated+" ");
            Models models = new Models();
            Log.d(TAG+" new Score ", a+" ");
            if (Stats.score != null){
                if (Stats.score.equals("0")){
                    jsonObject.addProperty("score", mainActivity.userRated+"");
                }else{
                    jsonObject.addProperty("score", Stats.score);
                }

            }else {
                    jsonObject.addProperty("score", "0");
            }

            Log.d(TAG+" params update post ", jsonObject+" ");
           Network.hitPostApiWithAuth(mainActivity,jsonObject,this, NetworkConstants.updatePost,
                    1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTabIcons() {
        Log.d(TAG+" counts tab ", tab_home.getTabCount()+" ");
        /*tab_home.getTabAt(0).setIcon(tabIcon[0]);
        tab_home.getTabAt(1).setIcon(tabIcon[1]);
        tab_home.getTabAt(2).setIcon(tabIcon[2]);*/
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response update post", data+" ");
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        Stats.notMineRating = "0";
        switch (statusCode) {
            case 200:
                try {

                    message = returnEmptyString(jsonObject.get("message"));
                    //  Toast.makeText(mainActivitySignUP, message, Toast.LENGTH_SHORT).show();
                    Stats.age = ""; Stats.height = ""; Stats.wieght = ""; Stats.note = "";
                    Stats.score = "";
                    if (StatsInput.et_race_stats != null){
                        StatsInput.et_tag_stats.setText("");
                    }
                    if (StatsInput.et_race_stats != null){
                        StatsInput.et_race_stats.setText("");
                    }
                    if (StatsInput.et_note_stats != null){
                        StatsInput.et_note_stats.setText("");
                    }
                    Stats.input_id = "";
                    Stats.updatePost = true;
                    Stats.post_id = "";
                    Stats.score = "";
                    Stats.rb_stats.setRating(0);

                    /*for (int i =0; i<=2; i++){
                        mainActivitySignUP.getSupportFragmentManager().popBackStack();
                    }*/
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
                //Toast.makeText(mainActivitySignUP, message, Toast.LENGTH_SHORT).show();
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeCamera(mainActivity);
                  //  return new CameraNew(mainActivitySignUP);
                case 1:
                    return new FriendsPost(mainActivity);
                case 2:
                    return new SelfSnaps(mainActivity);
                case  3:
                    return new Profile(mainActivity);
            }

            return null;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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



}
