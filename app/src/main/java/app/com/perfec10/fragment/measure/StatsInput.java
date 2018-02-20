package app.com.perfec10.fragment.measure;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.login.AddFrndAdapter;
import app.com.perfec10.fragment.measure.adapater.InputFieldsAdapter;
import app.com.perfec10.helper.RecyclerItemClickListener;
import app.com.perfec10.model.FbFrndsGS;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.util.GPSTracker;
import app.com.perfec10.util.PreferenceManager;

/**
 * Created by fluper on 22/11/17.
 */

@SuppressLint("ValidFragment")
public class StatsInput extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private MainActivity mainActivity;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private TextView  tv_selectage_stats, tv_selectheight_stats, tv_selectlocation_stats,
            tv_selectweight_stats, tv_sw12_stats, tv_sw13_stats, tv_sw14_stats,
            tv_sw15_stats, tv_sw16_stats, tv_sw17_stats;
    private RecyclerView rv_age_stats, rv_height_stats, rv_weight_stats, rv_taged_stats;
    private LinearLayoutManager linearLayoutManager1, linearLayoutManager2, linearLayoutManager3,
            linearLayoutManager4;
    private String[] ages = {"< 16 years", "16 to 20 years", "21 to 25 years", "26 to 30 years",
            "31 to 35 years","36 to 45 years", "46 to 55 years", "> 55 years"};

    private String[] weights = {"< 40 kg", "40 to 50 kg", "51 to 60 kg", "61 to 70 kg", "71 to 80 kg",
            "81 to 90 kg", "91 to 105 kg", "106 to 120 kg", "> 120 kg"};

    private String[] heights = {"< 4ft 9in", "4ft 9in to 5ft", "5ft 1in to 5ft 3in",
            "5ft 4in to 5ft 6in", "5ft 7in to 5ft 9in", "5ft 10in to 6ft",
            "6ft 1in to 6ft 4in", "> 6ft 4in"};
    private ArrayList<String> searchedlist;

    public static EditText et_tag_stats, et_race_stats, et_note_stats;
    private LinearLayout ll_age_stats, ll_height_stats, ll_weight_stats, ll_tut_stats,
            ll_back_statsinp, ll_location_statsinp;
    private boolean ageb = false, heightb = false, weightb = false;
    private PreferenceManager preferenceManager;
    private Typeface regular, bold;
    private double score =  0;
    private String selecAge="", selecHeigh = "", selecWeigh = "", scores = "";
    private Geocoder geocoder;
    List<Address> addresses;
    private String TAG = "StatsInput";
    private ArrayList<FriendListGS> friendList;

    public StatsInput(MainActivity mainActivity, String scores){
        this.mainActivity = mainActivity;
        this.scores = scores;
        preferenceManager = new PreferenceManager(mainActivity);
    }

    public StatsInput(){

    }

    @Override
    public void onAttach(Context context) {
        mGoogleApiClient = mainActivity.getGoogleApiClient();
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
       /* mGoogleApiClient = new GoogleApiClient
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
       View view = inflater.inflate(R.layout.stats_layout, container, false);
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
        Log.d(TAG, "on pause called ");
        if (et_note_stats.getText().toString().length() > 0){
            Stats.note = et_note_stats.getText().toString();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGoogleApiClient = null;
    }

    public void initView(View view){
        tv_selectage_stats = (TextView) view.findViewById(R.id.tv_selectage_stats);
        tv_selectheight_stats = (TextView) view.findViewById(R.id.tv_selectheight_stats);
        tv_selectweight_stats = (TextView) view.findViewById(R.id.tv_selectweight_stats);
        tv_selectlocation_stats = (TextView) view.findViewById(R.id.tv_selectlocation_stats);

        tv_sw12_stats = (TextView) view.findViewById(R.id.tv_sw12_stats);
        tv_sw13_stats = (TextView) view.findViewById(R.id.tv_sw13_stats);
        tv_sw14_stats = (TextView) view.findViewById(R.id.tv_sw14_stats);
        tv_sw15_stats = (TextView) view.findViewById(R.id.tv_sw15_stats);
        tv_sw16_stats = (TextView) view.findViewById(R.id.tv_sw16_stats);
        tv_sw17_stats = (TextView) view.findViewById(R.id.tv_sw17_stats);

        et_tag_stats = (EditText) view.findViewById(R.id.et_tag_stats);
        et_race_stats = (EditText) view.findViewById(R.id.et_race_stats);
        et_note_stats = (EditText) view.findViewById(R.id.et_note_stats);

        ll_age_stats = (LinearLayout) view.findViewById(R.id.ll_age_stats);
        ll_height_stats = (LinearLayout) view.findViewById(R.id.ll_height_stats);
        ll_weight_stats = (LinearLayout) view.findViewById(R.id.ll_weight_stats);
        ll_tut_stats = (LinearLayout) view.findViewById(R.id.ll_tut_stats);
        ll_back_statsinp = (LinearLayout) view.findViewById(R.id.ll_back_statsinp);
        ll_location_statsinp = (LinearLayout) view.findViewById(R.id.ll_location_statsinp);

        tv_sw12_stats.setTypeface(regular);
        tv_sw13_stats.setTypeface(regular);
        tv_sw14_stats.setTypeface(regular);
        tv_sw15_stats.setTypeface(regular);
        tv_sw16_stats.setTypeface(regular);
        tv_sw17_stats.setTypeface(regular);

       /* if (preferenceManager.getKey_statsWalkthrough().equalsIgnoreCase("1")){
            ll_tut_stats.setVisibility(View.GONE);
        }else {
            ll_tut_stats.setVisibility(View.VISIBLE);
        }*/

        //you can hard-code the lat & long if you have issues with getting it
        //remove the below if-condition and use the following couple of lines
        //double latitude = 37.422005;
        //double longitude = -122.084095


        rv_height_stats = (RecyclerView) view.findViewById(R.id.rv_height_stats);
        linearLayoutManager2 = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        rv_height_stats.setLayoutManager(linearLayoutManager2);

        rv_weight_stats = (RecyclerView) view.findViewById(R.id.rv_weight_stats);
        linearLayoutManager3 = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        rv_weight_stats.setLayoutManager(linearLayoutManager3);

        rv_age_stats = (RecyclerView) view.findViewById(R.id.rv_age_stats);
        linearLayoutManager1 = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        rv_age_stats.setLayoutManager(linearLayoutManager1);
        // test
        rv_taged_stats = (RecyclerView) view.findViewById(R.id.rv_taged_stats);
        linearLayoutManager4 = new LinearLayoutManager(mainActivity);
        rv_taged_stats.setLayoutManager(linearLayoutManager4);

        if (preferenceManager.getKey_location().equals("yes")){
            if (Stats.location != null && Stats.location.length() > 0){
                tv_selectlocation_stats.setText(Stats.location);
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
                    tv_selectlocation_stats.setText(address);
                    Stats.location = address;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {

        }


        // Toast.makeText(mainActivitySignUP, address+" ", Toast.LENGTH_SHORT).show();
        if (Stats.age != null && Stats.age.length()> 0){
            tv_selectage_stats.setText(Stats.age);
        }else {

        }
        if (Stats.height != null && Stats.height.length() > 0){
            tv_selectheight_stats.setText(Stats.height);
        }
        if (Stats.wieght != null && Stats.wieght.length() > 0){
            tv_selectweight_stats.setText(Stats.wieght);
        }
        if (Stats.tagPerson != null && Stats.tagPerson.length() > 0){
            et_tag_stats.setText(Stats.tagPerson);
        }
        if (Stats.race != null && Stats.race.length() > 0){
            et_race_stats.setText(Stats.race);
        }
        if (Stats.note != null && Stats.note.length() > 0){
            et_note_stats.setText(Stats.note);
        }

        friendList = preferenceManager.getFrndList(mainActivity);

        if (friendList != null){
            edit_Text_Focus_Listner();
        }

    }

    public void edit_Text_Focus_Listner() {
        et_tag_stats.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_tag_stats.getText().toString().length() > 1){

                        searchedlist = new ArrayList<>();
                        for (int i =0 ; i < friendList.size(); i++){
                            String s = et_tag_stats.getText().toString();

                            boolean a = Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(friendList.get(i).getName()).find();

                            if (a){
                                FriendListGS friendLis = new FriendListGS();
                                friendLis.setUserId(friendList.get(i).getUserId());
                                friendLis.setName(friendList.get(i).getName());

                                searchedlist.add(friendList.get(i).getName());

                            }

                        }
                        String[] stockArr = new String[searchedlist.size()];
                        stockArr = searchedlist.toArray(stockArr);
                        rv_taged_stats.setVisibility(View.VISIBLE);
                        InputFieldsAdapter inputFieldsAdapter = new InputFieldsAdapter(mainActivity, stockArr,"1" );
                        rv_taged_stats.setAdapter(inputFieldsAdapter);


                }else {
                    rv_taged_stats.setVisibility(View.GONE);
                }

                //  search_result(jsonObject.toString());
            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    public void clickListner(){

        ll_age_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ageb){
                    Log.d(TAG+" inside ", "if");
                    InputFieldsAdapter inputFieldsAdapter = new InputFieldsAdapter(mainActivity, ages, "1");
                    rv_age_stats.setAdapter(inputFieldsAdapter);
                    rv_age_stats.setVisibility(View.VISIBLE);
                    tv_selectage_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_up,0);
                    ageb = true;
                }else {
                    Log.d(TAG+" inside ", "else");
                    tv_selectage_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                    rv_age_stats.setVisibility(View.GONE);
                    ageb = false;
                }
                Log.d(TAG+" outside ", "else");
            }
        });
        ll_height_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!heightb){
                    InputFieldsAdapter inputFieldsAdapter2 = new InputFieldsAdapter(mainActivity, heights, "1");
                    rv_height_stats.setAdapter(inputFieldsAdapter2);
                    rv_height_stats.setVisibility(View.VISIBLE);
                    tv_selectheight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_up,0);
                    heightb = true;
                }else {
                    tv_selectheight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                    rv_height_stats.setVisibility(View.GONE);
                    heightb = false;
                }
            }
        });
        ll_weight_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!weightb){
                    InputFieldsAdapter inputFieldsAdapter2 = new InputFieldsAdapter(mainActivity, weights, "1");
                    rv_weight_stats.setAdapter(inputFieldsAdapter2);
                    rv_weight_stats.setVisibility(View.VISIBLE);
                    tv_selectweight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_up,0);
                    weightb = true;
                }else {
                    tv_selectweight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                    rv_weight_stats.setVisibility(View.GONE);
                    weightb = false;
                }
            }
        });
        rv_age_stats.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        tv_selectage_stats.setText(ages[position]);
                        rv_age_stats.setVisibility(View.GONE);
                        Stats.age = tv_selectage_stats.getText().toString();
                        tv_selectage_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);

                    }
                })
        );
        rv_height_stats.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        tv_selectheight_stats.setText(heights[position]);
                        rv_height_stats.setVisibility(View.GONE);
                        Stats.height = tv_selectheight_stats.getText().toString();
                        tv_selectheight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                    }
                })
        );
        rv_weight_stats.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        tv_selectweight_stats.setText(weights[position]);
                        rv_weight_stats.setVisibility(View.GONE);
                        Stats.wieght = tv_selectweight_stats.getText().toString();
                        tv_selectweight_stats.setCompoundDrawablesWithIntrinsicBounds(0,0, R.mipmap.drop_down,0);
                    }
                })
        );
        rv_taged_stats.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        et_tag_stats.setText(searchedlist.get(position));
                        rv_taged_stats.setVisibility(View.GONE);

                    }
                })
        );
        ll_tut_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_tut_stats.setVisibility(View.GONE);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("cameraWalkthrough", "");
                    jsonObject.put("shareWalkthrough", "1");
                    jsonObject.put("statsWalkthrough", "");
                    Log.d(TAG+" params of walk through", jsonObject+" ");
                    preferenceManager.setKey_statsWalkthrough("1");
                    if (Network.isConnected(mainActivity)){
                       // walkThrough(jsonObject.toString());

                    }else {
                        Toast.makeText(mainActivity, "No Internet Connection ", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        ll_back_statsinp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stats.tagPerson = et_tag_stats.getText().toString();
                Stats.race = et_race_stats.getText().toString();
                mainActivity.getSupportFragmentManager().popBackStack();
            }
        });
        ll_location_statsinp.setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            //if (resultCode == RESULT_OK) { PS@4201
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
                tv_selectlocation_stats.setText(address);
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
