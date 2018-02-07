package app.com.perfec10.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;

import java.io.File;

import app.com.perfec10.util.Constants;
import app.com.perfec10.util.PreferenceManager;



/*
*
 * Created by fluper on 5/9/17.
 */


public class Network {
    static int status;

    public static boolean isConnected(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param1 Context
     * @param2 JsonOnject if onject is not there just use a new Instance of JsonObject Class
     * @param3 Callback of Network
     * @param4 requset code (it is used to handle separate responses from different apis)
     */


    public static void hitPostApi(final Context context, final JsonObject object, final NetworkCallBack listener, final String networkUrl, final int requestCode) {

        Ion.with(context).load(networkUrl).setTimeout(90 * 1000)
                .setJsonObjectBody(object)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e == null) {
                            if (result != null) {
                                //do your stuff here
                                JsonObject obj = result.getResult();
                                int statusCode = result.getHeaders().code();
                                listener.onSuccess(obj, requestCode, statusCode);
                            } else {
                                listener.onError("Network Error");
                            }
                        } else {
                            if (e instanceof com.google.gson.JsonParseException) {
                                listener.onError("Bad Request");
                            }
                            if (e instanceof java.util.concurrent.TimeoutException) {
                                listener.onError("Oops! Time Out");
                            } else {
                                listener.onError("Network Error Occured");
                            }
                        }
                    }
                });
    }

    public static void hitPostApiWithAuth(final Context context, final JsonObject object, final NetworkCallBack listener, final String networkUrl, final int requestCode) {
        String auth = new PreferenceManager(context).getUserAuthkey();
        Ion.with(context).load(networkUrl).setTimeout(20 * 1000).addHeader("accessToken", auth)
                .setJsonObjectBody(object)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e == null) {
                            if (result != null) {
                                //do your stuff here
                                JsonObject obj = result.getResult();
                                int statusCode = result.getHeaders().code();
                                listener.onSuccess(obj, requestCode, statusCode);
                            } else {
                                listener.onError("Network Error");
                            }
                        } else {
                            if (e instanceof com.google.gson.JsonParseException) {
                                listener.onError("Bad Request");
                            }
                            if (e instanceof java.util.concurrent.TimeoutException) {
                                listener.onError("Oops! Time Out");
                            } else {
                                listener.onError("Network Error Occured");
                            }
                        }
                    }
                });
    }

    public static void hitEditProfileApiWithAuth(final Context context, final String phone, final String imagePath, final NetworkCallBack listener, final String networkUrl, final int requestCode) {
        if (null != imagePath) {
            String auth = new PreferenceManager(context).getString(Constants.AUTH, "");
            Ion.with(context).load(networkUrl).setTimeout(20 * 1000).addHeader("auth", auth)
                    .setMultipartParameter("phone", phone)
                    .setMultipartFile("profile_url", new File(imagePath))
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e == null) {
                                if (result != null) {
                                    //do your stuff here
                                    JsonObject obj = result.getResult();
                                    int statusCode = result.getHeaders().code();
                                    listener.onSuccess(obj, requestCode, statusCode);
                                } else {
                                    listener.onError("Network Error");
                                }
                            } else {
                                if (e instanceof com.google.gson.JsonParseException) {
                                    listener.onError("Bad Request");
                                }
                                if (e instanceof java.util.concurrent.TimeoutException) {
                                    listener.onError("Oops! Time Out");
                                } else {
                                    listener.onError("Network Error Occured");
                                }
                            }
                        }
                    });
        } else {
            String auth = new PreferenceManager(context).getString(Constants.AUTH, "");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("phone", phone);
            Ion.with(context).load(networkUrl).setTimeout(20 * 1000).addHeader("auth", auth)
                    .setJsonObjectBody(jsonObject)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e == null) {
                                if (result != null) {
                                    //do your stuff here
                                    JsonObject obj = result.getResult();
                                    int statusCode = result.getHeaders().code();
                                    listener.onSuccess(obj, requestCode, statusCode);
                                } else {
                                    listener.onError("Network Error");
                                }
                            } else {
                                if (e instanceof com.google.gson.JsonParseException) {
                                    listener.onError("Bad Request");
                                }
                                if (e instanceof java.util.concurrent.TimeoutException) {
                                    listener.onError("Oops! Time Out");
                                } else {
                                    listener.onError("Network Error Occured");
                                }
                            }
                        }
                    });
        }

    }


    public static void hitCreateTicketApiWithAuth(final Context context,
                                                  final String categoryId,
                                                  final String userId,
                                                  final String agentId,
                                                  final String enterRoomId,
                                                  final File videoFile,
                                                  final File imageFile,
                                                  final String mediaType,
                                                  final String title,
                                                  final String desc,
                                                  final NetworkCallBack listener,
                                                  final String networkUrl,
                                                  final int requestCode) {
        if (mediaType.equalsIgnoreCase("1")) {
            String auth = new PreferenceManager(context).getString(Constants.AUTH, "");
            Ion.with(context).load(networkUrl).setTimeout(20 * 1000).addHeader("auth", auth)
                    .setMultipartParameter("title", title)
                    .setMultipartParameter("category_id", categoryId)
                    .setMultipartParameter("user_id", userId)
                    .setMultipartParameter("agent_id", agentId)
                    .setMultipartParameter("description", desc)
                    .setMultipartParameter("room_permission", enterRoomId)
                    .setMultipartParameter("media_type", mediaType)
                    .setMultipartFile("video", videoFile)
                    .setMultipartFile("complaint_photo", imageFile)
                    .asJsonObject()
                    .withResponse()


                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {if (e == null) {
                                if (result != null) {
                                    //do your stuff here
                                    JsonObject obj = result.getResult();
                                    int statusCode = result.getHeaders().code();
                                    listener.onSuccess(obj, requestCode, statusCode);
                                } else {
                                    listener.onError("Network Error");
                                }
                            } else {
                                if (e instanceof com.google.gson.JsonParseException) {
                                    listener.onError("Bad Request");
                                }
                                if (e instanceof java.util.concurrent.TimeoutException) {
                                    listener.onError("Oops! Time Out");
                                } else {
                                    listener.onError("Network Error Occured");
                                }
                            }
                        }
                    });
        } else if (mediaType.equalsIgnoreCase("0")) {
            String auth = new PreferenceManager(context).getString(Constants.AUTH, "");
            Ion.with(context).load(networkUrl).setTimeout(20 * 1000).addHeader("auth", auth)
                    .setMultipartParameter("title", title)
                    .setMultipartParameter("category_id", categoryId)
                    .setMultipartParameter("user_id", userId)
                    .setMultipartParameter("agent_id", agentId)
                    .setMultipartParameter("description", desc)
                    .setMultipartParameter("room_permission", enterRoomId)
                    .setMultipartParameter("media_type", mediaType)
                    .setMultipartFile("complaint_photo", imageFile)
                    .asJsonObject()
                    .withResponse()

                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e == null) {
                                if (result != null) {
                                    //do your stuff here
                                    JsonObject obj = result.getResult();
                                    int statusCode = result.getHeaders().code();
                                    listener.onSuccess(obj, requestCode, statusCode);
                                } else {
                                    listener.onError("Network Error");
                                }
                            } else {
                                if (e instanceof com.google.gson.JsonParseException) {
                                    listener.onError("Bad Request");
                                }
                                if (e instanceof java.util.concurrent.TimeoutException) {
                                    listener.onError("Oops! Time Out");
                                } else {
                                    listener.onError("Network Error Occured");
                                }
                            }
                        }
                    });
        } else {
            String auth = new PreferenceManager(context).getString(Constants.AUTH, "");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", title);
            jsonObject.addProperty("category_id", categoryId);
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("agent_id", agentId);
            jsonObject.addProperty("description", desc);
            jsonObject.addProperty("media_type", mediaType);
            jsonObject.addProperty("room_permission", enterRoomId);
            Ion.with(context).load(networkUrl).setTimeout(20 * 1000).addHeader("auth", auth)
                    .setJsonObjectBody(jsonObject)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e == null) {
                                if (result != null) {
                                    //do your stuff here
                                    JsonObject obj = result.getResult();
                                    int statusCode = result.getHeaders().code();
                                    listener.onSuccess(obj, requestCode, statusCode);
                                } else {
                                    listener.onError("Network Error");
                                }
                            } else {
                                if (e instanceof com.google.gson.JsonParseException) {
                                    listener.onError("Bad Request");
                                }
                                if (e instanceof java.util.concurrent.TimeoutException) {
                                    listener.onError("Oops! Time Out");
                                } else {
                                    listener.onError("Network Error Occured");
                                }
                            }
                        }
                    });
        }

    }



  /*  * @param1 Context
    * @param2 Callback of Network
    * @param3 requset code (it is used to handle separate responses from different apis)*/


    public static void hitGetApi(final Context context, final NetworkCallBack listener, final String networkUrl, final int requestCode) {
        String auth = new PreferenceManager(context).getUserAuthkey();
        Ion.with(context).load(networkUrl).setTimeout(20 * 1000).addHeader("accessToken", auth)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e == null) {
                            if (result != null) {
                                //do your stuff here
                                JsonObject obj = result.getResult();
                                int statusCode = result.getHeaders().code();
                                listener.onSuccess(obj, requestCode, statusCode);
                            } else {
                                listener.onError("Bad Request");
                            }
                        } else {
                            if (e instanceof com.google.gson.JsonParseException) {
                                listener.onError("PHP Controller Exceptions");
                            }
                            if (e instanceof java.util.concurrent.TimeoutException) {
                                listener.onError("Oops! Time Out");
                            } else {
                                listener.onError("Network Error");
                            }
                        }
                    }
                });
    }

    public static void hitSendChatMessageMultiPartApiWithAuth(final Context context,
                                                              final String content,
                                                              String ticketId,
                                                              String userId,
                                                              final String imagePath,
                                                              String messageType,
                                                              final NetworkCallBack listener, final String networkUrl,
                                                              final int requestCode) {
        if (!TextUtils.isEmpty(imagePath)) {
            String auth = new PreferenceManager(context).getString(Constants.AUTH, "");
            Ion.with(context).load(networkUrl).setTimeout(20 * 1000).addHeader("auth", auth)
                    .setMultipartParameter("content", "demo")
                    .setMultipartParameter("ticket_id", ticketId)
                    .setMultipartParameter("user_id", userId)
                    .setMultipartParameter("message_type", messageType)
                    .setMultipartFile("comment_photo", new File(imagePath))
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e == null) {
                                if (result != null) {
                                    //do your stuff here
                                    JsonObject obj = result.getResult();
                                    int statusCode = result.getHeaders().code();
                                    listener.onSuccess(obj, requestCode, statusCode);
                                } else {
                                    listener.onError("Network Error");
                                }
                            } else {
                                if (e instanceof com.google.gson.JsonParseException) {
                                    listener.onError("Bad Request");
                                }
                                if (e instanceof java.util.concurrent.TimeoutException) {
                                    listener.onError("Oops! Time Out");
                                } else {
                                    listener.onError("Network Error Occured");
                                }
                            }
                        }
                    });
        } else {
            String auth = new PreferenceManager(context).getString(Constants.AUTH, "");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("content", content);
            jsonObject.addProperty("ticket_id", ticketId);
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("message_type", messageType);
            Ion.with(context).load(networkUrl).setTimeout(20 * 1000).addHeader("auth", auth)
                    .setJsonObjectBody(jsonObject)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (e == null) {
                                if (result != null) {
                                    //do your stuff here
                                    JsonObject obj = result.getResult();
                                    int statusCode = result.getHeaders().code();
                                    listener.onSuccess(obj, requestCode, statusCode);
                                } else {
                                    listener.onError("Network Error");
                                }
                            } else {
                                if (e instanceof com.google.gson.JsonParseException) {
                                    listener.onError("Bad Request");
                                }
                                if (e instanceof java.util.concurrent.TimeoutException) {
                                    listener.onError("Oops! Time Out");
                                } else {
                                    listener.onError("Network Error Occured");
                                }
                            }
                        }
                    });
        }

    }


    public static void measureImage(Context context, Bundle bundle,
                                    int timeout, final String url, final NetworkCallBack callback, final int requestCode) {
        String auth = new PreferenceManager(context).getUserAuthkey();
        Builders.Any.B ion = getIon(context, url, timeout, auth);
        ion.setMultipartParameter("picture", bundle.getString("picture"))
                .setMultipartParameter("gender", bundle.getString("gender"))
                .setMultipartParameter("angle", bundle.getString("angle"))
                .setMultipartFile("image", new File(bundle.getString("image")))
                .asJsonObject().withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) {
                            callback.onError(NetworkConstants.TIMEOUT);
                            return;
                        }
                        status = result.getHeaders().code();
                        JsonObject resultObject = result.getResult();
                        callback.onSuccess(resultObject, status, requestCode);
                    }
                });
    }

    private static Builders.Any.B getIon(Context context, String url, int timeout, String auth) {
        return Ion.with(context).load(url).setTimeout(timeout).setHeader("accessToken", auth);
    }

    /*private static void showSessionDialog(final Activity context) {
        final Dialog otpSendDialog = new Dialog(context);
        otpSendDialog.setContentView(R.layout.session_expired_dialog);
        otpSendDialog.setTitle("Alert!!!");
        otpSendDialog.setCancelable(false);
        TextView tv_dialog = (TextView) otpSendDialog.findViewById(R.id.tv_dialog_message);
        TextView tv_dissmiss = (TextView) otpSendDialog.findViewById(R.id.tv_dissmiss);
        tv_dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpSendDialog.dismiss();
                logoutUser(context);
            }
        });
        otpSendDialog.show();
    }


    private static void logoutUser(Activity context) {
        new SharedPreference(context).clear();
        context.startActivity(new Intent(context, com.app.projcetstructure.activities.MainActivity.class));
        context.finishAffinity();
    }*/

}
