package dxexwxexy.pricefinder.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.Toolbar;

import dxexwxexy.pricefinder.Data.Item;
import dxexwxexy.pricefinder.R;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    String product;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent item = getIntent();
        product = item.getStringExtra("items");
        webView = findViewById(R.id.webView1);
        webView.setWebViewClient(new WebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl(product);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.navigation);
        setSupportActionBar(toolbar);
        setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                return true;
            case R.id.foward:
                if (webView.canGoForward()) {
                    webView.goForward();
                }
                return true;
            case R.id.reload:
                webView.reload();
                return true;
            case R.id.copy:
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Link Copied", webView.getUrl());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }
}
