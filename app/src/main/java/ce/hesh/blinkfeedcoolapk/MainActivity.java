package ce.hesh.blinkfeedcoolapk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.coolapk.market.util.AuthUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ce.hesh.blinkfeedcoolapk.bean.FeedInfo;
import ce.hesh.blinkfeedcoolapk.util.GetCoolJson;

/**
 * Created by Hesh on 2016/11/22.
 */

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "BlinkfeedTesr";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d(TAG, AuthUtils.getAS("650730e8-130f-3aad-8903-507f7f72dcd30x58"));


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String json = GetCoolJson.getJsonFromInternet();
                        JsonParser parser=new JsonParser();
                        JsonObject obj=parser.parse(json).getAsJsonObject();
                        JsonArray array=obj.getAsJsonArray("data");


                        Gson gson = new Gson();
                        List<FeedInfo> feedInfos = new ArrayList<>();
                        for (int i = 0; i < array.size(); i++) {
                            JsonElement j=array.get(i);
                            FeedInfo feedInfo=gson.fromJson(j, FeedInfo.class);
                            feedInfos.add(feedInfo);
                        }
                        System.out.println(feedInfos.get(2).getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }
}
