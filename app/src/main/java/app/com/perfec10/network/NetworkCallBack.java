package app.com.perfec10.network;

/**
 * Created by fluper on 5/9/17.
 */

public interface NetworkCallBack<E> {

    public void onSuccess(E data, int requestCode, int statusCode);

    public void onError(String msg);
}