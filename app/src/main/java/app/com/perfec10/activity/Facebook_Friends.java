package app.com.perfec10.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.com.perfec10.model.FbFrndsGS;
import app.com.perfec10.util.PreferenceManager;

/**
 * Created by fluper-pc on 2/2/18.
 */

public class Facebook_Friends extends AppCompatActivity {
    private String TAG = "Facebook_Friends";
    private CallbackManager callbackManager;
    private ArrayList<FbFrndsGS> fbFriends;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(Facebook_Friends.this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        facebookFriends();
    }

    private void facebookFriends() {

        Log.d(TAG,"fb-Friends accessTokens"+ preferenceManager.getKey_fb_accessTokens());
        Log.d(TAG, "fb-Friends userId"+preferenceManager.getKey_fb_userId());

        //AccessToken token = AccessToken.getCurrentAccessToken();
       /* AccessToken.refreshCurrentAccessTokenAsync(new AccessToken.AccessTokenRefreshCallback() {
            @Override
            public void OnTokenRefreshed(AccessToken accessToken) {
                Log.d(TAG, "refreshCurrentAccessTokenAsync: "+accessToken.getToken());
            }

            @Override
            public void OnTokenRefreshFailed(FacebookException exception) {
                Log.d(TAG, "refreshCurrentAccessTokenAsync Failed: "+exception.toString());
            }
        });
*/

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
                                fbFriends = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String name = jsonObject.getString("name");
                                        String id = jsonObject.getString("id");
                                        String friendsProfilePicUrl = "https://graph.facebook.com/" + id + "/picture?type=normal";
                                        FbFrndsGS fbFrndsGS = new FbFrndsGS();
                                        fbFrndsGS.setId(id);
                                        fbFrndsGS.setName(name);
                                        fbFrndsGS.setStatus("0");
                                        fbFrndsGS.setImage(friendsProfilePicUrl);
                                        fbFriends.add(fbFrndsGS);

                                        Log.d(TAG, "friends image " + friendsProfilePicUrl);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                       /* Gson gson = new Gson();
                                        String fnd_list = gson.toJson(fbFriends);
                                        preferenceManager.setKey_frindsfb(fnd_list);*/
                                //  FbFrndsGS fbFrndsGS = new FbFrndsGS(fbFriends);
                                //    preferenceManager.saveFbFrnds(Facebook_Friends.this, fbFriends);
                                //  FbFrndsGS fbFrndsGS1 = (FbFrndsGS) getParcelable("student");
                            }
                        }
                    }).executeAsync();
        } catch (Exception e) {e.printStackTrace();
        }
    }
}
