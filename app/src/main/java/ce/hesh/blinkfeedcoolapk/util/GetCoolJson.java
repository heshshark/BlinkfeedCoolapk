package ce.hesh.blinkfeedcoolapk.util;

import android.os.Build;
import android.os.Handler;

import com.coolapk.market.util.AuthUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Hesh on 2016/11/29.
 */

public class GetCoolJson {

    private final static String TAG = "GetCoolJson";
    private static String url = "https://api.coolapk.com/v6/topic/feedList?type=all&page=1";
    public static final int PARSESUCCWSS = 0x2001;
    private Handler handler;

    public GetCoolJson(Handler handler) {
        // TODO Auto-generated constructor stub
        this.handler = handler;
    }

    public static String getJsonFromInternet() throws IOException {

        Request request = new Request.Builder().url(url).build();
        Response response = null;
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

        return "null";
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
                                .addHeader("X-App-Token", AuthUtils.getAS("650730e8-130f-3aad-8903-507f7f72dcd30x58"))
                                .addHeader("Cookie", "uid=478139; username=lifing; token=9ee0221eWLLnEmC_Vw478bp7Y69woewrqmK-FkTvMfZiEYOi91jqEm--_SIr21k-XXUiSRsCQYxbvehUrsXlWhddWr31LGPoA_UE2Y0FAILqbNjmuKurGXL9USiEscC9-UQ9RxtuufPHf89UP4Cn9lHvuZ691zSqhwTRFo-F2Mf1Ot2Iw6KjNaPifyHSUc249nG4_kFmLpdA-spetvwLXnqpJ5q5sw")
                                .build();
                        return chain.proceed(request);
                    }

                })
                .build();

        return httpClient;
    }
}
