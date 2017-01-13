package ce.hesh.blinkfeedcoolapk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.socks.library.KLog;

import ce.hesh.blinkfeedcoolapk.R;
import ce.hesh.blinkfeedcoolapk.util.AccountUtil;
import ce.hesh.blinkfeedcoolapk.util.InfoUtil;

/**
 * 登录界面，用于获取用户cookies并添加系统账户
 * Created by Hesh on 2016/11/30.
 */

public class LoginActivity extends AppCompatActivity {

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }


    private void init() {
        webView = (WebView) findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();
        settings.setSavePassword(false);
        settings.setJavaScriptEnabled(true);
        //WebView加载web资源
        webView.loadUrl("https://account.coolapk.com/auth/login");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                KLog.i(url);
                //使用酷安账户登录
                if (url.equals("http://www.coolapk.com/")) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String CookieStr = cookieManager.getCookie(url);
                    KLog.d("Cookies = " + CookieStr);
                    if (CookieStr.length() > 200) {
                        String[] cookies = CookieStr.split(";");
                        String[] names = cookies[2].split("=");
                        String name = names[1];
                        String loginCookie = cookies[1] + cookies[2] + cookies[3];
                        InfoUtil.saveCoockie(getApplicationContext(), loginCookie);
                        AccountUtil.addAccount(LoginActivity.this, name);
                    }
                    finish();
                }
                //使用第三方登录
                if (url.startsWith("https://account.coolapk.com/auth/callback")) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String CookieStr = cookieManager.getCookie(url);
                    KLog.d("Cookies = " + CookieStr);
                    if (CookieStr.length() > 200) {
                        String[] cookies = CookieStr.split(";");
                        String[] names = cookies[3].split("=");
                        String name = names[1];
                        String loginCookie = cookies[2] + cookies[3] + cookies[4];
                        InfoUtil.saveCoockie(getApplicationContext(), loginCookie);
                        AccountUtil.addAccount(LoginActivity.this, name);
                    }
                    finish();
                }
                super.onPageFinished(view, url);
            }
        });
    }


    @Override
    protected void onResume() {
        KLog.d("onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        KLog.d("onDestroy");
        super.onDestroy();
        if (webView != null) {
            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
        }
        CookieManager instance = CookieManager.getInstance();
        if (instance != null) {
            instance.removeAllCookie();
        }
    }


}

