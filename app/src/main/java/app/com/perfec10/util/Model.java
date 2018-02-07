package app.com.perfec10.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mindz on 6/10/16.
 */

@SuppressWarnings("ALL")
public class Model {

    public static void ShowToast(Context activity, String str) {
        if (str != null)
            Toast.makeText(activity, str, Toast.LENGTH_LONG).show();
    }

    public static boolean checkInternetConenction(Context context) {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()== NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()== NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;
    }

    public static JSONObject getJsonObject(JSONObject jObject, String title) {
        try {
            return jObject.getJSONObject(title);
        } catch (Exception ignored) {
        }
        return new JSONObject();
    }

    public static JSONObject getObject(String str) {
        try {
            return new JSONObject(str);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static JSONObject getObject(JSONArray jSon, int index) {
        try {
            return jSon.getJSONObject(index);
        } catch (Exception ignored) {

        }
        return null;
    }

    public static JSONArray getArray(String str) {
        try {
            return new JSONArray(str);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static JSONArray getArray(JSONObject object, String tag) {

        try {
            return object.getJSONArray(tag);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static JSONArray getArrays(JSONObject object, String key) {
        try {
            return object.getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getString(JSONObject jObject, String title) {
        try {
            return jObject.getString(title);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static int getInt(JSONObject jObject, String title) {
        try {
            return jObject.getInt(title);
        } catch (Exception ignored) {
        }
        return 0;
    }

    public static String getString(JSONArray jsonArray, int index) {
        try {
            return jsonArray.getString(index);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static float getFloat(JSONObject jObject, String title) {
        try {
            return (float) jObject.getDouble(title);
        } catch (Exception ignored) {
        }
        return 0;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDate(String date) {
        Date dates;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dates = format.parse(date);
            if (String.valueOf(dates).length() > 16)
                date = String.valueOf(dates).substring(0, 16);
            else
                date = dates.toString();
        } catch (Exception ignored) {
        }
        return date;
    }

    public static String encode_To_Base64(String value) throws UnsupportedEncodingException {
        // Sending side
        String base64="";
        try {
            String Value = value;
            byte[] data = Value.getBytes("UTF-8");
            base64 = Base64.encodeToString(data, Base64.NO_WRAP);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        //Log.d("base64", "base64 " + base64);
        return base64;

    }


//    public static String POST(String url,String []tag,String []value) {
//        InputStream inputStream;
//        StringBuilder builder = new StringBuilder();
//        try {
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost(url);
//            JSONObject jsonObject = new JSONObject();
//            for(int i=0;i<tag.length;i++)
//                jsonObject.put(tag[i],value[i]);
//            StringEntity se = new StringEntity(jsonObject.toString());
//            httpPost.setEntity(se);
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");
//            HttpResponse httpResponse = httpclient.execute(httpPost);
//            inputStream = httpResponse.getEntity().getContent();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    inputStream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                builder.append(line);
//            }
//        } catch (Exception ignored) {     }
//        return builder.toString();
//    }


//    public static JSONArray getJSONArrayData(String url) {
//        StringBuilder builder = new StringBuilder();
//        HttpClient client = new DefaultHttpClient();
//        HttpGet httpGet = new HttpGet(url);
//        try {
//            HttpResponse response = client.execute(httpGet);
//
//            HttpEntity entity = response.getEntity();
//            InputStream content = entity.getContent();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                builder.append(line);
//            }
//        } catch (Exception ignored) {
//        }
//        JSONArray jsonArray = null;
//        try {
//
//            jsonArray = new JSONArray(builder.toString());
//        } catch (Exception ignored) {
//        }
//        return jsonArray;
//    }


}
