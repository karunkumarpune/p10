package app.com.perfec10.fragment.friendgroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.login.AddFrndAdapter;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.helper.Validation;
import app.com.perfec10.model.AddFrndGS;
import app.com.perfec10.model.FbFrndsGS;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.fragment.profile.EditProfile.social_id;
import static app.com.perfec10.helper.HelperClass.returnEmptyString;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by fluper on 20/11/17.
 */

@SuppressLint("ValidFragment")
public class AddFriend extends Fragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private TextView tv_sw1_addfrnd;
    private LinearLayout ll_mail_addfrnd, ll_fb_addfrnd;
    private ImageView iv_mail_addfrnd, iv_fb_addfrnd, iv_search_addfrnd, iv_cancle_addfrnd, iv_next_addfrnd;
    private EditText et_seach_addfrnd;
    private Button btn_search_addfrnd;
    private RecyclerView rv_serachlist_addfrnd;
    private LinearLayoutManager linearLayoutManager;
    private Progress progress;
    private String friendId = "";
    private Typeface regular, bold;
    private String TAG = "AddFriend";
    private ArrayList<AddFrndGS> searchedList = new ArrayList<>();
    private View view;
    private ArrayList<FbFrndsGS> fbFrndsList;
    private ArrayList<FriendListGS> sharedFrndList;
    private boolean fbClick = false;


    private AddFrndAdapter addFrndAdapter;


    private CallbackManager callbackManager;
    //private ArrayList<FbFrndsGS> fbFriends;
    private PreferenceManager preferenceManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    public AddFriend(MainActivity mainActivity) {
        this.mainActivity = mainActivity;


        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        progress = new Progress(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        preferenceManager = new PreferenceManager(mainActivity);


        //  Log.d(TAG, "fb frnd name "+ mainActivitySignUP.fbFriends.get(0).getId()+" ");
    }

    public AddFriend() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_friend, container, false);
            Profile.calledFrom = "1";
            initView(view);
            clickListner();
        }

        return view;
    }

    public void initView(View view) {
        tv_sw1_addfrnd = (TextView) view.findViewById(R.id.tv_sw1_addfrnd);

        ll_mail_addfrnd = (LinearLayout) view.findViewById(R.id.ll_mail_addfrnd);
        ll_fb_addfrnd = (LinearLayout) view.findViewById(R.id.ll_fb_addfrnd);

        iv_mail_addfrnd = (ImageView) view.findViewById(R.id.iv_mail_addfrnd);
        iv_fb_addfrnd = (ImageView) view.findViewById(R.id.iv_fb_addfrnd);
        iv_search_addfrnd = (ImageView) view.findViewById(R.id.iv_search_addfrnd);
        iv_cancle_addfrnd = (ImageView) view.findViewById(R.id.iv_cancle_addfrnd);
        iv_next_addfrnd = (ImageView) view.findViewById(R.id.iv_next_addfrnd);

        et_seach_addfrnd = (EditText) view.findViewById(R.id.et_seach_addfrnd);

        btn_search_addfrnd = (Button) view.findViewById(R.id.btn_search_addfrnd);

        rv_serachlist_addfrnd = (RecyclerView) view.findViewById(R.id.rv_serachlist_addfrnd);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_serachlist_addfrnd.setLayoutManager(linearLayoutManager);

        tv_sw1_addfrnd.setTypeface(bold);
        et_seach_addfrnd.setTypeface(regular);

        fbFrndsList = preferenceManager.getFbFrnds(mainActivity);
        sharedFrndList = preferenceManager.getFrndList(mainActivity);
        if (sharedFrndList != null) {

        } else {
            sharedFrndList = new ArrayList<>();
        }

        edit_Text_Focus_Listner();

    }

    public void edit_Text_Focus_Listner() {
        et_seach_addfrnd.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_seach_addfrnd.getText().toString().length() > 0) {
                    ArrayList<FbFrndsGS> searchedlist = new ArrayList<>();
                    if (fbFrndsList != null) {
                        for (int i = 0; i < fbFrndsList.size(); i++) {
                            String s = et_seach_addfrnd.getText().toString();
                            boolean a = Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(fbFrndsList.get(i).getName()).find();

                            if (a) {
                                FbFrndsGS fbFrndsGS = new FbFrndsGS();
                                fbFrndsGS.setId(fbFrndsList.get(i).getId());
                                fbFrndsGS.setName(fbFrndsList.get(i).getName());
                                fbFrndsGS.setStatus(fbFrndsList.get(i).getStatus());
                                fbFrndsGS.setImage(fbFrndsList.get(i).getImage());
                                searchedlist.add(fbFrndsGS);
                            }

                        }
                    }

                    if (fbClick) {

                        AddFrndAdapter addFrndAdapter = new AddFrndAdapter(mainActivity, searchedlist);
                        rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                    }

                } else {
                    if (fbClick) {
                        if (fbFrndsList != null) {
                          //  AddFrndAdapter addFrndAdapter = new AddFrndAdapter(mainActivitySignUP, fbFrndsList);
                           // rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                        } else {
                            rv_serachlist_addfrnd.setVisibility(View.GONE);
                        }

                    }

                }

                //  search_result(jsonObject.toString());
            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    public void clickListner() {
        iv_search_addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        iv_cancle_addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_seach_addfrnd.setText("");
            }
        });
        ll_fb_addfrnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //  startActivity(new Intent(getActivity(), Facebook_Friends.class));


                // facebookFriends();

                facebookFriends();

            }
        });
        ll_mail_addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_search_addfrnd.setVisibility(View.VISIBLE);
                fbClick = false;
                // iv_fb_addfrnd.setImageResource(R.mipmap.facebook_unselect);
                ll_mail_addfrnd.setBackgroundColor(Color.parseColor("#cedc00"));
                ll_fb_addfrnd.setBackgroundColor(Color.parseColor("#ffffff"));
                iv_mail_addfrnd.setImageResource(R.mipmap.email_select);
                if (searchedList.size() > 0) {
                    rv_serachlist_addfrnd.setVisibility(View.VISIBLE);
                    AddFrndAdapter addFrndAdapter = new AddFrndAdapter(mainActivity, searchedList, "email");
                    rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                    addFrndAdapter.notifyDataSetChanged();
                } else {
                    rv_serachlist_addfrnd.setVisibility(View.GONE);
                }
            }
        });
        tv_sw1_addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager.popBackStack();
            }
        });

        btn_search_addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation.validEmail(et_seach_addfrnd.getText().toString())) {
                    progress.show();
                    searchUser();
                    Log.d(TAG + " edit text ", et_seach_addfrnd.getText().toString() + " ");
                } else {
                    if (fbClick) {
                        Toast.makeText(mainActivity, "Please Enter name", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mainActivity, "Please Enter valid Email", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
        iv_next_addfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchedList.size() > 0 || fbFrndsList != null) {
                    ArrayList<Integer> eid = new ArrayList<Integer>();
                    ArrayList<Long> fid = new ArrayList<Long>();
                    for (int i = 0; i < searchedList.size(); i++) {
                        String status = searchedList.get(i).getSelected();
                        if (status.equals("1")) {
                            eid.add(Integer.parseInt(searchedList.get(i).getUserId()));
                        }
                    }
                    if (fbFrndsList != null) {
                        for (int i = 0; i < fbFrndsList.size(); i++) {
                            if (fbFrndsList.get(i).getStatus().equals("1")) {
                                fid.add(Long.parseLong(fbFrndsList.get(i).getId()));
                            }
                        }
                    }

                    if (fid.size() > 0 || eid.size() > 0) {
                        Log.d(TAG, "selected list " + fid);
                        addFriend(eid, fid);
                    } else {
                        Toast.makeText(mainActivity, " Please Select Friend to Add", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mainActivity, " Please Search Friends to Add", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void facebookFriends() {

        Log.d(TAG, "fb-Friends accessTokens" + preferenceManager.getKey_fb_accessTokens());
        Log.d(TAG, "fb-Friends userId" + preferenceManager.getKey_fb_userId());
        try {
            AccessToken token = AccessToken.getCurrentAccessToken();
            GraphRequest.newMyFriendsRequest(token,
                    new GraphRequest.GraphJSONArrayCallback() {
                        @Override
                        public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                            System.out.println("jsonArray: " + jsonArray);
                            System.out.println("GraphResponse: " + response);
                            if (jsonArray.length() > 0) {
                                Log.d(TAG, "have fb frnds");
                                fbFrndsList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                             String id = jsonObject.getString("id");
                                            String name = jsonObject.getString("name");
                                            String friendsProfilePicUrl = "https://graph.facebook.com/" + id + "/picture?type=normal";


                                            FbFrndsGS fbFrndsGS = new FbFrndsGS();
                                            fbFrndsGS.setId(id);
                                            fbFrndsGS.setName(name);
                                            fbFrndsGS.setStatus("0");
                                            fbFrndsGS.setImage(friendsProfilePicUrl);

                                                fbFrndsList.add(fbFrndsGS);


                                       // Log.e("list:>",social_id.toString()==);


                                    //    if(social_id.toString()==)

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (fbFrndsList != null) {
                                    for(int i=0;i<fbFrndsList.size();i++){
                                        FbFrndsGS m=fbFrndsList.get(i);
                                        Long  s= Long.valueOf(m.getId());
                                        for(int j=0; j < social_id.size();j++){
                                                Long ss= social_id.get(j);
                                                if(s.equals(ss)){
                                                    fbFrndsList.remove(m);
                                                }
                                        }
                                    }

                                    if (fbFrndsList.size() > 0) {
                                       rv_serachlist_addfrnd.setVisibility(View.VISIBLE);
                                        addFrndAdapter = new AddFrndAdapter(mainActivity, fbFrndsList);
                                       rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                                       addFrndAdapter.notifyDataSetChanged();
                                    } else {
                                        rv_serachlist_addfrnd.setVisibility(View.GONE);
                                    }


                                }

                                // addFrndAdapter = new AddFrndAdapter(mainActivitySignUP, fbFrndsList);
                                //  rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                                //  addFrndAdapter.notifyDataSetChanged();

                                       /* Gson gson = new Gson();
                                        String fnd_list = gson.toJson(fbFriends);
                                        preferenceManager.setKey_frindsfb(fnd_list);*/
                                //  FbFrndsGS fbFrndsGS = new FbFrndsGS(fbFriends);
                                //    preferenceManager.saveFbFrnds(Facebook_Friends.this, fbFriends);
                                //  FbFrndsGS fbFrndsGS1 = (FbFrndsGS) getParcelable("student");


                                if (preferenceManager.getKey_linkedfb().equals("link")) {
                                    fbClick = true;
                                    iv_fb_addfrnd.setImageResource(R.mipmap.facebook_select);
                                    ll_fb_addfrnd.setBackgroundColor(Color.parseColor("#cedc00"));
                                    ll_mail_addfrnd.setBackgroundColor(Color.parseColor("#ffffff"));
                                    //  iv_mail_addfrnd.setImageResource(R.mipmap.email_unselect);
                                    btn_search_addfrnd.setVisibility(View.GONE);

                                   /* if (fbFrndsList != null) {
                                        if (fbFrndsList.size() > 0) {
                                            rv_serachlist_addfrnd.setVisibility(View.VISIBLE);
                                            addFrndAdapter = new AddFrndAdapter(mainActivitySignUP, fbFrndsList);
                                            rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                                            addFrndAdapter.notifyDataSetChanged();
                                        } else {

                                            rv_serachlist_addfrnd.setVisibility(View.GONE);
                                        }
                                    }*/

                                    et_seach_addfrnd.setText("");
                                } else {
                                    Toast.makeText(mainActivity, "Not Linked with Facebook", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }
                    }).executeAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchUser() {
        if (Network.isConnected(mainActivity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", et_seach_addfrnd.getText().toString());
            Log.d(TAG + " params search frnd", jsonObject + " ");
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.searchUser, 1);
        } else {
            Toast.makeText(mainActivity, "No Internt Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    public void addFriend(ArrayList<Integer> eid, ArrayList<Long> fid) {
        if (Network.isConnected(mainActivity)) {
            /*{
        "normal":[2,3],
        "  social":["dfwewed"]
            }*/
            JsonObject jsonObject = new JsonObject();
            //     jsonObject.addProperty("user_id",friendId);

            Gson gson = new Gson();
            if (eid.size() > 0) {
                jsonObject.add("normal", gson.toJsonTree(eid));
            }
            if (fid.size() > 0) {
                jsonObject.add("social", gson.toJsonTree(fid));
            }
            Log.d(TAG + " params add friend", jsonObject + " ");
            progress.show();
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.addFriend, 2);
        } else {
            Toast.makeText(mainActivity, "No Internt Connection ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG + " response seach user ", data + " ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode == 1) {
                        Log.d(TAG + " reponse profile", jsonObject + " ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.equals("Successfull")) {
                            progress.dismiss();
                            JsonArray result = jsonObject.getAsJsonArray("result");
                            if (result.size() > 0) {

                                JsonObject json = result.get(0).getAsJsonObject();
                                String user_id = json.get("user_id") + "";
                                friendId = user_id;
                                if (!friendId.equals(preferenceManager.getKeyUserId())) {

                                    String name = json.get("name") + "";
                                    name = name.replace("\"", "");
                                    String email = json.get("email") + "";
                                    email = email.replace("\"", "");
                                    String url = json.get("image") + "";
                                    url = url.replace("\"", "");
                                    AddFrndGS addFrndGS = new AddFrndGS();
                                    addFrndGS.setName(name);
                                    addFrndGS.setUserId(friendId);
                                    addFrndGS.setEmail(email);
                                    addFrndGS.setImage(url);
                                    addFrndGS.setSelected("0");
                                    boolean b = false;// for checking already added id
                                    if (searchedList.size() > 0) {
                                        for (int i = 0; i < searchedList.size(); i++) {
                                            String id = searchedList.get(i).getUserId();
                                            if (id.equals(friendId)) {

                                                b = true;
                                                break;
                                            } else {
                                                b = false;
                                            }
                                        }
                                    } else {
                                        b = false;
                                    }
                                    if (!b) {
                                        searchedList.add(addFrndGS);
                                    }
                                    View view = mainActivity.getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                    rv_serachlist_addfrnd.setVisibility(View.VISIBLE);
                                    AddFrndAdapter addFrndAdapter = new AddFrndAdapter(mainActivity,
                                            searchedList, "email");
                                    rv_serachlist_addfrnd.setAdapter(addFrndAdapter);
                                } else {
                                    Toast.makeText(mainActivity, "You can't add your self", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    } else if (requestCode == 2) {
                        Log.d(TAG + " response add friend ", jsonObject + " ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.contains("Successfull")) {
                            Toast.makeText(mainActivity, "Friend Added Successfully ", Toast.LENGTH_SHORT).show();
                            JsonArray result = jsonObject.getAsJsonArray("result");
                            if (result.size() > 0) {
                                for (int i = 0; i < result.size(); i++) {
                                    JsonObject json = result.get(i).getAsJsonObject();
                                    FriendListGS friendListGS = new FriendListGS();
                                    friendListGS.setUserId(json.get("user_id").getAsInt() + "");
                                    friendListGS.setName(json.get("name").getAsString());
                                    String social_id = json.get("social_id") + "";
                                    social_id = social_id.replace("\"", "");
                                    ArrayList<FriendListGS> oldList = preferenceManager.getFrndList(mainActivity);

                                    sharedFrndList.add(friendListGS);
                                    if (oldList != null) {
                                        for (int b = 0; b < oldList.size(); b++) {
                                            String id = json.get("user_id").getAsInt() + "";
                                            String id1 = oldList.get(b).getUserId();
                                            if (id.equals(id1)) {
                                                sharedFrndList.remove(b);
                                            } else {

                                            }
                                        }
                                    }

                                    if (fbFrndsList != null) {
                                        for (int a = 0; a < fbFrndsList.size(); a++) {
                                            String sId = fbFrndsList.get(a).getId();

                                            if (social_id.equals(sId)) {
                                                fbFrndsList.remove(a);
                                            }
                                        }
                                        preferenceManager.saveFbFrnds(mainActivity, fbFrndsList);
                                    }

                                }

                            }
                            Log.d(TAG, "shared " + sharedFrndList.size());
                            preferenceManager.saveFrndList(mainActivity, sharedFrndList);
                            preferenceManager.setKey_frndcount("1");
                            Profile.fragmentManager.popBackStack();
                        } else {
                            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    Log.d(TAG + " Outcome", e.toString());
                }
                break;
            case 201:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 204:
                Toast.makeText(mainActivity, "No Data Found", Toast.LENGTH_SHORT).show();
                break;
            case 400:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                break;
            case 401:
                message = returnEmptyString(jsonObject.get("message"));
                Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
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
}
