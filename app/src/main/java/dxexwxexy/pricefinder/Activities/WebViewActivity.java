package dxexwxexy.pricefinder.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import dxexwxexy.pricefinder.Data.Item;
import dxexwxexy.pricefinder.R;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    String product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent item= getIntent();
       product=item.getExtras().getString("items");
       Toast.makeText(this,product,Toast.LENGTH_SHORT).show();


        webView = (WebView) findViewById(R.id.webView1);
        WebSettings settigns= webView.getSettings();
        settigns.setJavaScriptEnabled(true);
        webView.loadUrl(product);

    }
}
