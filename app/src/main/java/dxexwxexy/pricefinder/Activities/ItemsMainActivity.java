package dxexwxexy.pricefinder.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;

import dxexwxexy.pricefinder.Data.Item;
import dxexwxexy.pricefinder.R;

public class ItemsMainActivity extends AppCompatActivity {

    /***
     * Fields used for UI manipulation.
     */
    private SwipeRefreshLayout refreshLayout;
    private ItemManager adapter;

    /***
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initRecyclerView(null);
        initUI();


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
                adapter.sort(0);
                return true;
            case R.id.sort_current:
                adapter.sort(1);
                return true;
            case R.id.sort_diff:
                adapter.sort(2);
                return true;
        }
        return false;
    }

    /**
     * Initializes the RecyclerViewer for the items of operations.
     */
    private void initRecyclerView(ArrayList<Item> items) {
        RecyclerView recyclerView = findViewById(R.id.items_list);
        adapter = (items == null) ? new ItemManager(this) : new ItemManager(items, this);
        recyclerView.setAdapter(adapter);
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
               if(name.getText().toString().matches("") || url.getText().toString().matches("") ){
                   Toast.makeText(this, "Fields missing", Toast.LENGTH_SHORT).show();
               } else {
                   adapter.addItem(new Item(name.getText().toString(), url.getText().toString()));
                   dialog.dismiss();
                   adapter.notifyDataSetChanged();
               }
            });
        });
        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            adapter.refresh();
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
        outState.putParcelableArrayList("items", adapter.getItems());
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
}
