package ce.hesh.blinkfeedcoolapk.util;

import android.os.Build;
import android.os.Handler;

import com.coolapk.market.util.AuthUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.socks.library.KLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ce.hesh.blinkfeedcoolapk.bean.Favority;
import ce.hesh.blinkfeedcoolapk.bean.FeedInfo;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Hesh on 2016/11/29.
 */

public class NetUtil {


    private static String mDeviceId;
    private static String mCookies;

    static {
        mDeviceId = InfoUtil.getDeviceID();
        mCookies = InfoUtil.getCookie();
    }
    private Handler handler;

    public NetUtil(Handler handler) {
        // TODO Auto-generated constructor stub
        this.handler = handler;
    }

    public static String getJsonFromInternet(String detail_url) throws IOException {

        Request request = new Request.Builder().url(detail_url).build();
        Response response;
        try {
            response = genericClient().newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static OkHttpClient genericClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("X-Requested-With", "XMLHttpRequest")
                                .addHeader("X-Api-Version", "7")
                                .addHeader("X-App-Version", "keep-alive")
                                .addHeader("X-App-Id", "coolmarket")
                                .addHeader("X-Sdk-Int", String.valueOf(Build.VERSION.SDK_INT))
                                .addHeader("X-Sdk-Locale", "zh-CN")
                                .addHeader("X-App-Token", AuthUtils.getAS(mDeviceId))
                                .addHeader("Cookie", mCookies)
                                .build();
                        KLog.d(AuthUtils.getAS(mDeviceId));
                        return chain.proceed(request);
                    }

                })
                .build();

        return httpClient;
    }

    public static List<FeedInfo> getFeedInfos(String detailUrl){
        String JsonString = null;
        try {
            JsonString = NetUtil.getJsonFromInternet(detailUrl);
            if (JsonString ==null)
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(JsonString).getAsJsonObject();
        JsonArray array = obj.getAsJsonArray("data");
        Gson gson = new Gson();
        List<FeedInfo> feedInfos = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonElement j = array.get(i);
            FeedInfo feedInfo = gson.fromJson(j, FeedInfo.class);
            feedInfos.add(feedInfo);
        }
        return feedInfos;
    }

    public static FeedInfo getFeedInfo(String detailUrl){
        String JsonString = null;
        try {
            JsonString = NetUtil.getJsonFromInternet(detailUrl);
            if (JsonString ==null)
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(JsonString).getAsJsonObject();
        JsonObject data = obj.getAsJsonObject("data");
        Gson gson = new Gson();
        FeedInfo feedInfo = gson.fromJson(data,FeedInfo.class);
        return feedInfo;
    }

    public static Favority doFav(String detailUrl){
        String JsonString = null;
        try {
            JsonString = NetUtil.getJsonFromInternet(detailUrl);
            if (JsonString ==null)
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Favority favority = gson.fromJson(JsonString,Favority.class);
        return favority;
    }
}
