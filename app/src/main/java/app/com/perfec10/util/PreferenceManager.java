package app.com.perfec10.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.perfec10.model.FbFrndsGS;
import app.com.perfec10.model.FriendListGS;


@SuppressWarnings("ALL")
public class PreferenceManager {
    private static final String PREF_NAME = "PERFEC120";

    public static final String key_permissions = "permissions";
    public static final String key_userAuthkey = "userAuthkey";
    public static final String key_userId = "userId";
    public static final String key_userEmailId = "userEmail";
    public static final String key_userName = "userName";
    public static final String key_verficationCode = "verification_code";
    public static final String key_verified = "email_verified";
    public static final String key_userImg = "userImage";
    public static final String key_cameraWalkthrough = "camera_walkthrough";
    public static final String key_statsWalkthrough = "stats_walkthrough";
    public static final String key_shareWalkthrough = "share_walkthrough";
    public static final String key_location = "location";
    public static final String key_frndcount = "frnd_count";
    public static final String key_linkedfb = "linked_fb";
    public static final String key_fromfb = "from_fb";
    public static final String key_notifiablePersonal = "notifiableForPersonal";
    public static final String key_notifiableother = "notifiableForOtherSnaps";
    public static final String key_frindsfb = "frnds_fb";


    public static final String key_frindslist = "fnds_list";
    public static final String key_fb_accesstoken = "fb_accesstoken";


    public static final String key_fb_accessTokens = "fb_accesstokens";
    public static final String key_fb_userId = "key_fb_userid";

    public static final String key_Sesstion = "key_Session";




    public SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;



    public PreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void clearPreferences() {
        editor.clear();
        editor.commit();
        Log.d("TAG", "Deleted all user info from shared preference");
    }

    public void setKey_permissions(String permissions) {
        editor.putString(key_permissions, permissions);
        editor.commit();
    }

    public void setKey_Sesstion(String sesstion) {
        editor.putString(key_Sesstion, sesstion);
        editor.commit();
    }

    public String getKey_Sesstion() {
        return pref.getString(key_Sesstion, "");
    }




    public String getKey_permissions() {
        return pref.getString(key_permissions, "");
    }

    public void setUserAuthkey(String user_authkey) {
        editor.putString(key_userAuthkey, user_authkey);
        editor.commit();
    }

    public String getUserAuthkey() {
        return pref.getString(key_userAuthkey, "");
    }

    public void setKeyUserId(String userId) {
        editor.putString(key_userId, userId);
        editor.commit();
    }

    public String getKeyUserId() {
        return pref.getString(key_userId, "");
    }

    public void setKey_userEmailId(String userEmailId) {
        editor.putString(key_userEmailId, userEmailId);
        editor.commit();
    }

    public String getKeyUserEmailId() {
        return pref.getString(key_userEmailId, "");
    }

    public void setKeyVerficationCode(String verficationCode) {
        editor.putString(key_verficationCode, verficationCode);
        editor.commit();
    }

    public String getKey_userName() {
        return pref.getString(key_userName, "");
    }

    public void setKey_userName(String userName) {
        editor.putString(key_userName, userName);
        editor.commit();
    }

    public String getKeyVerficationCode() {
        return pref.getString(key_verficationCode, "");
    }

    public void setKeyVerified(String verified) {
        editor.putString(key_verified, verified);
        editor.commit();
    }

    public String getKey_cameraWalkthrough() {
        return pref.getString(key_cameraWalkthrough, "");
    }

    public void setKey_cameraWalkthrough(String cameraWalkthrough) {
        editor.putString(key_cameraWalkthrough, cameraWalkthrough);
        editor.commit();
    }

    public String getKey_userImg() {
        return pref.getString(key_userImg, "");
    }

    public void setKey_userImg(String userImg) {
        editor.putString(key_userImg, userImg);
        editor.commit();
    }

    public String getKey_statsWalkthrough() {
        return pref.getString(key_statsWalkthrough, "");
    }

    public void setKey_statsWalkthrough(String statsWalkthrough) {
        editor.putString(key_statsWalkthrough, statsWalkthrough);
        editor.commit();
    }

    public String getKey_shareWalkthrough() {
        return pref.getString(key_shareWalkthrough, "");
    }

    public void setKey_shareWalkthrough(String shareWalkthrough) {
        editor.putString(key_shareWalkthrough, shareWalkthrough);
        editor.commit();
    }

    public String getKey_location() {
        return pref.getString(key_location, "");
    }

    public void setKey_location(String location) {
        editor.putString(key_location, location);
        editor.commit();
    }

    public String getKey_frndcount() {
        return pref.getString(key_frndcount, "");
    }

    public void setKey_frndcount(String frndcount) {
        editor.putString(key_frndcount, frndcount);
        editor.commit();
    }

    public String getKey_linkedfb() {
        return pref.getString(key_linkedfb, "");
    }

    public void setKey_linkedfb(String linkedfb) {
        editor.putString(key_linkedfb, linkedfb);
        editor.commit();
    }

    public String getKey_fromfb() {
        return pref.getString(key_fromfb, "");
    }

    public void setKey_fromfb(String fromfb) {
        editor.putString(key_fromfb, fromfb);
        editor.commit();
    }


    public String getKey_notifiablePersonal() {
        return pref.getString(key_notifiablePersonal, "");
    }

    public void setKey_notifiablePersonal(String notifiablePersonal) {
        editor.putString(key_notifiablePersonal, notifiablePersonal);
        editor.commit();
    }

    public String getKey_notifiableother() {
        return pref.getString(key_notifiableother, "");
    }

    public void setKey_notifiableother(String notifiableother) {
        editor.putString(key_notifiableother, notifiableother);
        editor.commit();
    }

    public void saveFbFrnds(Context context, List<FbFrndsGS> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(key_frindsfb, jsonFavorites);

        editor.commit();
    }

    public ArrayList<FbFrndsGS> getFbFrnds(Context context) {
        SharedPreferences settings;
        List<FbFrndsGS> favorites;

        settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(key_frindsfb)) {
            String jsonFavorites = settings.getString(key_frindsfb, null);
            Gson gson = new Gson();
            FbFrndsGS[] favoriteItems = gson.fromJson(jsonFavorites,
                    FbFrndsGS[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<FbFrndsGS>(favorites);
        } else
            return null;

        return (ArrayList<FbFrndsGS>) favorites;
    }

    public void saveFrndList(Context context, List<FriendListGS> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(key_frindslist, jsonFavorites);

        editor.commit();
    }

    public ArrayList<FriendListGS> getFrndList(Context context) {
        SharedPreferences settings;
        List<FriendListGS> favorites;

        settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(key_frindslist)) {
            String jsonFavorites = settings.getString(key_frindslist, null);
            Gson gson = new Gson();
            FriendListGS[] favoriteItems = gson.fromJson(jsonFavorites,
                    FriendListGS[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<FriendListGS>(favorites);
        } else
            return null;

        return (ArrayList<FriendListGS>) favorites;
    }






    public String getKey_fb_accesstoken() {
        return pref.getString(key_fb_accesstoken, "");
    }

    public void setKey_fb_accesstoken(String fb_accesstoken) {
        editor.putString(key_fb_accesstoken, fb_accesstoken);
        editor.commit();
    }


 /*facebook access token set get*/
 public String getKey_fb_accessTokens() {
        return pref.getString(key_fb_accessTokens, "");
    }

    public void setKey_fb_accessTokens(String fb_accesstoken) {
        editor.putString(key_fb_accessTokens, fb_accesstoken);
        editor.commit();
    }

    /*facebook user ID set get*/
    public String getKey_fb_userId() {
        return pref.getString(key_fb_userId, "");
    }

    public void setKey_fb_userId(String userId) {
        editor.putString(key_fb_userId, userId);
        editor.commit();
    }
  /*...............................................*/





    public String getKeyVerified() {
        return pref.getString(key_verified, "");
    }


    public String getString(String key, String defValue) {
        return pref.getString(key, defValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }


}
