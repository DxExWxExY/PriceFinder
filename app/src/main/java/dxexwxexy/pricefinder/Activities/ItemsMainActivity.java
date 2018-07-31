package dxexwxexy.pricefinder.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import dxexwxexy.pricefinder.Data.Item;
import dxexwxexy.pricefinder.R;

public class ItemsMainActivity extends AppCompatActivity {

    /***
     * Fields used for UI manipulation.
     */
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loading;
    private ItemManager itemManager;
    private Handler handler;
    private Thread updater;

    /***
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_view);
        CheckInternetConnection();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initRecyclerView(null);
        initUI();
        initUpdating();
    }

    private void initUpdating() {
        handler = new Handler(msg -> {
            if (msg.arg1 == 1) {
                refreshLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
                itemManager.refresh();
            } else {
                refreshLayout.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
            }
            return true;
        });
        if (updater == null) {
            updater = new Thread(() -> {
                while (!itemManager.isFetched()) {
                    Message message = new Message();
                    message.arg1 = 0;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = new Message();
                message.arg1 = 1;
                handler.sendMessage(message);
            });
            updater.start();
        } else {
            updater.start();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sort_name:
                itemManager.sort(0);
                return true;
            case R.id.sort_current:
                itemManager.sort(1);
                return true;
            case R.id.sort_diff:
                itemManager.sort(2);
                return true;
        }
        return false;
    }

    /**
     * Initializes the RecyclerViewer for the items of operations.
     */
    private void initRecyclerView(ArrayList<Item> items) {
        RecyclerView recyclerView = findViewById(R.id.items_list);
        itemManager = (items == null) ? new ItemManager(this) : new ItemManager(items, this);
        recyclerView.setAdapter(itemManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /***
     * Initializes the UI Components.
     */
    private void initUI() {
        FloatingActionButton addItem = findViewById(R.id.add_fab);
        addItem.setOnClickListener(view -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ItemsMainActivity.this);
            @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.add_dialog,null);
            EditText name = mView.findViewById(R.id.add_item_name);
            EditText url = mView.findViewById(R.id.add_url);
            Button add = mView.findViewById(R.id.add_item);
            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
            add.setOnClickListener(v -> {
                String ebay = "\\S+\\.ebay\\.\\S+";
                String amazon = "\\S+\\.amazon\\.\\S+";
                if(name.getText().toString().matches("") || url.getText().toString().matches("") ){
                    Toast.makeText(this, "Fields missing", Toast.LENGTH_SHORT).show();
                } else if (url.getText().toString().matches(ebay) || url.getText().toString().matches(amazon)){
                    itemManager.addItem(new Item(name.getText().toString(), url.getText().toString()));
                    dialog.dismiss();
                    itemManager.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            });
        });
        refreshLayout = findViewById(R.id.swipe_refresh);
        loading = findViewById(R.id.loading_icon);
        refreshLayout.setVisibility(View.INVISIBLE);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            itemManager.refresh();
            refreshLayout.setRefreshing(false);
        });
    }

    /***
     * {@inheritDoc}
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("items", itemManager.getItems());
    }

    /***
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initRecyclerView(savedInstanceState.getParcelableArrayList("items"));
    }

    public void disableRefresh() {
        refreshLayout.setRefreshing(false);
        refreshLayout.setEnabled(false);
    }

    public void enableRefresh() {
        refreshLayout.setEnabled(true);
    }

    public  void CheckInternetConnection() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if (!wifi.isConnected()) {


            if (mobile.isConnected()) {
            } else {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("No wifi Connection")
                        .setMessage("Would you like to go to wifi settings?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); // continue with delete
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // do nothing
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        }

        if (!mobile.isConnected()) {

            if (wifi.isConnected()) {
            } else {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("No Internet Detected")
                        .setMessage("Would you like to go to Network settings?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            startActivity(new Intent(Settings.ACTION_SETTINGS)); // continue with delete
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // do nothing
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }

        }
    }
}
