package app.com.perfec10.fragment.profile;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import app.com.perfec10.fragment.login.PreLogin;
import app.com.perfec10.util.PreferenceManager;

/**
 * Created by fluper on 6/11/17.
 */

@SuppressLint("ValidFragment")
public class Profile extends Fragment {
    public static  MainActivity mainActivity;
    private PreferenceManager preferenceManager;
    public static String calledFrom = "0", logout = "0";
    public static FragmentManager fragmentManager;
    private FrameLayout profile_fram;
    public static Profile profile;
    private Button btn_logout_pr;
    private String TAG = "Profile";

    public Profile(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        preferenceManager = new PreferenceManager(mainActivity);
       }

    public Profile(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        initview(view);
        clickListner();
        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG+" called From ",calledFrom+" ");
        if (calledFrom.equals("1")){
            fragmentManager = mainActivity.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.profile_fram, new MyProfile(mainActivity));
            fragmentManager.beginTransaction().replace(R.id.profile_fram, new FriendsList(mainActivity)).addToBackStack("add").commit();
        }
        super.onResume();
    }

    public void initview(View view){

        profile_fram = (FrameLayout) view.findViewById(R.id.profile_fram);
        fragmentManager = mainActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.profile_fram, new MyProfile(mainActivity)).commit();
      //  fragmentManager.beginTransaction().replace(R.id.profile_fram, new FriendsList(mainActivity)).commit();

    }

    public void clickListner(){

    }

    public static void changeProfileFragment(Fragment fragment, String fragmentName){
        try{
            fragmentManager = mainActivity.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_frame, fragment).addToBackStack(fragmentName).commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
