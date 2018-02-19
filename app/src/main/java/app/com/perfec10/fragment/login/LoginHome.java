package app.com.perfec10.fragment.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;

import app.com.perfec10.R;
import app.com.perfec10.activity.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fluper on 26/10/17.
 */

@SuppressLint("ValidFragment")
public class LoginHome extends Fragment {
    private MainActivity mainActivity;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private Typeface bold, medium, regular;
    private String fb_user_name="";
    private String TAG = "LoginHome";

    @BindView(R.id.btn_email_loginhome) Button btn_email_loginhome;
    @BindView(R.id.btn_fb_loginhome) Button btn_fb_loginhome;
    @BindView(R.id.tv_newuser_loginhome) TextView tv_newuser_loginhome;
    @BindView(R.id.tv_signup_loginhome) TextView tv_signup_loginhome;
    @BindView(R.id.ll_signup_loginhome) LinearLayout ll_signup_loginhome;



    public LoginHome(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        medium = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/roboto-medium.ttf");
        bold = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Bold.ttf");
        regular = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    public LoginHome() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_home, container, false);
        ButterKnife.bind(this, view);
        initView();
        clickListner();

        return view;
    }

    public void initView() {
        tv_signup_loginhome.setTypeface(bold);
        tv_newuser_loginhome.setTypeface(regular);
        btn_email_loginhome.setTypeface(bold);
        btn_fb_loginhome.setTypeface(bold);
    }

    public void clickListner() {

        btn_email_loginhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new Login(mainActivity), "login");

            }
        });

        ll_signup_loginhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.changeFragment(new SignUp(mainActivity), "login");

            }
        });
        btn_fb_loginhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loginFacebook();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void displayMessage(Profile profile) {
        if (profile != null) {
            Log.d(TAG+" profile name", profile.getName());
            fb_user_name = profile.getName();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
