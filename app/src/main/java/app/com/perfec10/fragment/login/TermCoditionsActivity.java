package app.com.perfec10.fragment.login;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import app.com.perfec10.R;

public class TermCoditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_coditions);

        WebView webView = findViewById(R.id.webid);
        WebSettings settings = webView.getSettings();


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) //required for running javascript on android 4.1 or later
        {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        settings.setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/pdf/term.pdf");
        webView.requestFocus();
    }
}
