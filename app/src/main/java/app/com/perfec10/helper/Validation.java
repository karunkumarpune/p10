package app.com.perfec10.helper;

import android.util.Patterns;

import app.com.perfec10.util.Constants;

/**
 * Created by fluper on 25/10/17.
 */

public class Validation {// 1962972527276093

    public static boolean validEmail(String email) {
        if (email.length() < 3 || email.length() > 265)
            return false;
        else {
            if (email.matches(Constants.EMAIL_PATTERN)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean isValidYoutubeUrl(String url) {
        String regExp = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com)";
        if (url.isEmpty()) {
            return false;
        }
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            return false;
        }
        return true;
    }

    public static boolean validMobile(String mobile) {
        if (mobile.length() < 6 || mobile.length() > 16)
            return false;
        else return true;
    }

    public static boolean isNumber(String number) {
        boolean flag = true;
        try {
            Long.parseLong(number);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
}
