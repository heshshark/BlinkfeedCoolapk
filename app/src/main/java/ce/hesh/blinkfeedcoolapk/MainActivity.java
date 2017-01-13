package ce.hesh.blinkfeedcoolapk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.coolapk.market.util.AuthUtils;

/**
 * Created by Hesh on 2016/11/22.
 */

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "BlinkfeedTest";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, AuthUtils.getAS("650730e8-130f-3aad-8903-507f7f72dcd30x58"));
    }
}
