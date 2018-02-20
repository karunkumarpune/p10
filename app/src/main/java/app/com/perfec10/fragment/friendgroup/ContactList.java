package app.com.perfec10.fragment.friendgroup;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.friendgroup.adapter.ContactListAdapter;
import app.com.perfec10.fragment.friendgroup.adapter.CreateGroupFriendsAdapter;
import app.com.perfec10.fragment.friendgroup.adapter.GroupFriendAdapter;
import app.com.perfec10.fragment.home.Home;
import app.com.perfec10.fragment.login.VerificationScreen;
import app.com.perfec10.fragment.profile.Profile;
import app.com.perfec10.model.FriendListGS;
import app.com.perfec10.network.Network;
import app.com.perfec10.network.NetworkCallBack;
import app.com.perfec10.network.NetworkConstants;
import app.com.perfec10.util.Model;
import app.com.perfec10.util.PreferenceManager;
import app.com.perfec10.util.Progress;

import static app.com.perfec10.helper.HelperClass.returnEmptyString;

/**
 * Created by fluper on 4/12/17.
 */

@SuppressLint("ValidFragment")
public class ContactList extends Fragment implements NetworkCallBack {
    private MainActivity mainActivity;
    private ImageView iv_add_contactlist, iv_search_contactlist, iv_close_contactlist, iv_back_contactlist;
    private TextView tv_sw1_contactlist, tv_sw2_contactlist;
    private RecyclerView rv_contactlist;
    private LinearLayoutManager linearLayoutManager;
    private String groupId;
    private Progress progress;
    private Typeface regular, bold;
    private EditText et_search_contactlist;
    private PreferenceManager preferenceManager;
    private ArrayList<FriendListGS> contactList, alreadyMember;
    private ArrayList<String > userId;
    private String TAG = "ContactList";

    public ContactList(MainActivity mainActivity, String groupId, ArrayList<FriendListGS> alreadyMember) {
        this.mainActivity = mainActivity;
        this.groupId = groupId;
        this.alreadyMember = alreadyMember;
        progress = new Progress(mainActivity);
        preferenceManager = new PreferenceManager(mainActivity);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
        Log.d(TAG+" parancdjh ", alreadyMember + " ");
    }

    public ContactList() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contact_list, container, false);
        initView(v);
        clickListner();
        return v;
    }

    public void initView(View view) {
        iv_add_contactlist = (ImageView) view.findViewById(R.id.iv_add_contactlist);
        iv_search_contactlist = (ImageView) view.findViewById(R.id.iv_search_contactlist);
        iv_close_contactlist = (ImageView) view.findViewById(R.id.iv_close_contactlist);
        iv_back_contactlist = (ImageView) view.findViewById(R.id.iv_back_contactlist);

        tv_sw1_contactlist = (TextView) view.findViewById(R.id.tv_sw1_contactlist);
        tv_sw2_contactlist = (TextView) view.findViewById(R.id.tv_sw2_contactlist);
        et_search_contactlist = (EditText) view.findViewById(R.id.et_search_contactlist);
        rv_contactlist = (RecyclerView) view.findViewById(R.id.rv_contactlist);
        linearLayoutManager = new LinearLayoutManager(mainActivity);
        rv_contactlist.setLayoutManager(linearLayoutManager);
        tv_sw1_contactlist.setTypeface(bold);
        et_search_contactlist.setTypeface(regular);
        contactList();
        edit_Text_Focus_Listner();
    }

    public void clickListner() {
        iv_add_contactlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Network.isConnected(mainActivity)) {
                    addContact();
                } else {
                    Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_back_contactlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.fragmentManager.popBackStack();
            }
        });
        iv_close_contactlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_contactlist.setText("");
            }
        });
    }

    public void edit_Text_Focus_Listner() {
        et_search_contactlist.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_search_contactlist.getText().toString().length() > 0) {
                    ArrayList<FriendListGS> searchedlist = new ArrayList<>();
                    for (int i = 0; i < contactList.size(); i++) {
                        String s = et_search_contactlist.getText().toString();
                        boolean a = Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(contactList.get(i).getName()).find();

                        if (a) {
                            FriendListGS friendListGS = new FriendListGS();
                            friendListGS.setUserId(contactList.get(i).getUserId());
                            friendListGS.setName(contactList.get(i).getName());
                            friendListGS.setImage(contactList.get(i).getImage());
                            searchedlist.add(friendListGS);
                        }

                    }
                    ContactListAdapter contactListAdapter = new ContactListAdapter(mainActivity, searchedlist);
                    rv_contactlist.setAdapter(contactListAdapter);
                } else {
                    ContactListAdapter contactListAdapter = new ContactListAdapter(mainActivity, contactList);
                    rv_contactlist.setAdapter(contactListAdapter);
                }

                //  search_result(jsonObject.toString());
            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    public void contactList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", preferenceManager.getKeyUserId());
        Log.d(TAG+" params conatct list ", jsonObject + " ");
        if (Network.isConnected(mainActivity)) {
            progress.show();
            Network.hitPostApiWithAuth(mainActivity, jsonObject, this, NetworkConstants.friendList, 1);
        } else {
            Toast.makeText(mainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void addContact() {
        userId = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {
            if (contactList.get(i).getStatus()) {
                String s = contactList.get(i).getUserId();
                userId.add(s);
            }
            Log.d(TAG+" user to add ", userId + " ");
        }
        String s = String.valueOf(userId);
        s = s.replace("\"", "");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", s);
        jsonObject.addProperty("group_id", groupId);

        Log.d(TAG+" params add contact ", jsonObject + " ");
        String js = jsonObject + "";
        js = js.replace("\"[", "[");
        js = js.replace("]\"", "]");
        Log.d(TAG+" params ", js);


        if (userId.size() > 0) {
            progress.show();
            addMember(js);
        } else {
            Toast.makeText(mainActivity, "Select participants to add", Toast.LENGTH_SHORT).show();
        }

        //  Network.hitPostApiWithAuth(mainActivitySignUP,JsonObject.js,this, NetworkConstants.addgroupMember, 2);
    }

    public void addMember(final String jsonObject) {
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        Log.d(TAG+" addgroupMember api ", NetworkConstants.changeMailUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkConstants.addgroupMember,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG+" addgroupMember_response", "" + response);
                        try {
                            final JSONObject jsonObject = Model.getObject(response);
                            if (jsonObject != null) {
                                if (jsonObject.has("message")) {
                                    String message = jsonObject.getString("message");
                                    if (message.equalsIgnoreCase("Successful.")) {
                                        Toast.makeText(mainActivity, "Users added Successfully", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onSuccess(Object data, int requestCode, int statusCode) {
        Log.d(TAG+" response add contact ", data + " ");
        progress.dismiss();
        String message = "";
        JsonObject jsonObject = (JsonObject) data;
        switch (statusCode) {
            case 200:
                try {
                    if (requestCode == 1) {
                        Log.d(TAG+" reponse friend list", jsonObject + " ");
                        message = returnEmptyString(jsonObject.get("message"));
                        if (message.equals("Successfull")) {
                            JsonArray result = jsonObject.getAsJsonArray("result");
                            JsonObject json = result.get(0).getAsJsonObject();
                            JsonArray friends = json.getAsJsonArray("userFriends");
                            if (friends.size() > 0) {
                                ArrayList<FriendListGS> newList = new ArrayList<>();
                                contactList = new ArrayList<FriendListGS>();

                                    for (int i = 0; i < friends.size(); i++) {
                                        JsonObject jso = friends.get(i).getAsJsonObject();
                                        FriendListGS friendListGS = new FriendListGS();
                                        friendListGS.setUserId(jso.get("user_id") + "");
                                        friendListGS.setEmail(jso.get("email") + "");
                                        friendListGS.setName(jso.get("name") + "");
                                        friendListGS.setLocation(jso.get("location") + "");
                                        friendListGS.setImage(jso.get("image") + "");
                                        friendListGS.setGender(jso.get("gender") + "");
                                        friendListGS.setAge(jso.get("age") + "");
                                        friendListGS.setRace(jso.get("race") + "");
                                        friendListGS.setStatus(false);
                                        String uid = jso.get("user_id") + "";
                                        uid = uid.replace("\"", "");
                                        contactList.add(friendListGS);
                                        // newList.add(friendListGS);                                     // contactList.add(friendListGS);


                                }

                                for(int i=0;i<alreadyMember.size();i++){
                                        for(int j=0;j<contactList.size();j++){
                                            if(alreadyMember.get(i).getUserId().equalsIgnoreCase(contactList.get(j).getUserId())){
                                                contactList.remove(j);
                                            }
                                        }
                                }
                                ContactListAdapter contactListAdapter = new ContactListAdapter(mainActivity, contactList);
                                rv_contactlist.setAdapter(contactListAdapter);
                            } else {

                            }
                        }
                    } else if (requestCode == 2) {
                        Log.d(TAG+" reponse add contact", jsonObject + " ");
                        message = returnEmptyString(jsonObject.get("message"));
                        Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
                        Profile.fragmentManager.popBackStack();
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
