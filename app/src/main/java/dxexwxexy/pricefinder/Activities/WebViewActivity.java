package dxexwxexy.pricefinder.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        if (savedInstanceState == null) {
            webView.loadUrl(product);
        }
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
            case R.id.share:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Item URL");
                    i.putExtra(Intent.EXTRA_TEXT, "Check This Out!\n" + webView.getUrl());
                    startActivity(Intent.createChooser(i, "Pick an App"));
                } catch(Exception ignored) { }
                return true;
            case R.id.close:
                finish();
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

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }
}
