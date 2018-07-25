package dxexwxexy.pricefinder.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import dxexwxexy.pricefinder.Data.Item;
import dxexwxexy.pricefinder.R;

public class ItemsView extends AppCompatActivity {

    /***
     * Fields used for UI manipulation.
     */
    private SwipeRefreshLayout refreshLayout;
    private RecyclerViewAdapter adapter;

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
        adapter = (items == null) ? new RecyclerViewAdapter() : new RecyclerViewAdapter(items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /***
     * Initializes the UI Components.
     */
    private void initUI() {
        FloatingActionButton addItem = findViewById(R.id.add_fab);
        addItem.setOnClickListener(view -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ItemsView.this);
            @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.add_dialog,null);
            EditText name = mView.findViewById(R.id.add_item_name);
            EditText price = mView.findViewById(R.id.add_price);
            EditText url = mView.findViewById(R.id.add_url);
            Button add = mView.findViewById(R.id.add_item);
            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
            add.setOnClickListener(v -> {
               if(name.getText().toString().matches("") ||price.getText().toString().matches("") || url.getText().toString().matches("") ){
                   Toast.makeText(this, "Fields missing", Toast.LENGTH_SHORT).show();
               } else {
                   adapter.addItem(new Item(name.getText().toString(), url.getText().toString(), Double.parseDouble(price.getText().toString())));
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

    /**
     * Class required to use a RecyclerViewer.
     *
     */
    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        /**
         * Fields used by the RecyclerViewer.
         */
        private ArrayList<Item> items;
        private ArrayList<Item> selectedItems;
        private boolean selectionMode;
        private android.support.v7.view.ActionMode.Callback callback = new android.support.v7.view.ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                selectionMode = true;
                mode.getMenuInflater().inflate(R.menu.manage_items, menu);
                disableRefresh();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        mode.finish();
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ItemsView.this);
                        View view = getLayoutInflater().inflate(R.layout.edit_dialog, null);
                        EditText editName = view.findViewById(R.id.edit_item_name);
                        EditText editPrice = view.findViewById(R.id.edit_price);
                        EditText editURL = view.findViewById(R.id.edit_url);
                        Button done = view.findViewById(R.id.edit_done);
                        alertBuilder.setView(view);
                        AlertDialog dialog = alertBuilder.create();
                        dialog.show();
                        done.setOnClickListener(e -> {
                            Item edit = getSelected();
                            if (edit == null) {
                                Toast.makeText(ItemsView.this, "No Item Selected", Toast.LENGTH_SHORT).show();
                            } else {
                                if (!editName.getText().toString().matches("") || !editPrice.getText().toString().matches("")) {
                                    edit.setName(editName.getText().toString());
                                    edit.setPrice(Double.parseDouble(editPrice.getText().toString()));
                                    if (!editURL.getText().toString().matches("")) {
                                        edit.setURL(editURL.getText().toString());
                                    }
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(ItemsView.this, "Missing Fields", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        return true;
                    case R.id.delete:
                        deleteSelection();
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
                for (Item item :
                        selectedItems) {
                    item.setSelected(false);
                }
                selectionMode=!selectionMode;
                notifyDataSetChanged();
                enableRefresh();
                selectionMode = false;
            }
        };

        /***
         * Default Constructor
         */
        RecyclerViewAdapter() {
            initItems();
            this.selectedItems = new ArrayList<>();
            this.selectionMode = false;
        }

        /**
         * Constructor for restoring to previous state.
         * @param items Previously created list.
         */
        RecyclerViewAdapter(ArrayList<Item> items) {
            this.items = items;
            this.selectedItems = new ArrayList<>();
            this.selectionMode = false;
        }

        /***
         * Method to simulate data read.
         */
        private void initItems() {
            items = new ArrayList<>();
            //Sample
            items.add(new Item("GTX 1080 Ti", "https://images.nvidia.com/" +
                    "geforce-com/international/images/nvidia-geforce-gtx-1080-ti/" +
                    "GeForce_GTX_1080ti_3qtr_top_left.png",499.99));
        }

        /**
         * {@inheritDoc}
         * @param parent
         * @param viewType
         * @return
         */
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container, parent, false);
            return new ViewHolder(view);
        }

        /**
         * {@inheritDoc}
         * @param holder
         * @param position
         */
        @SuppressLint({"NewApi", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder content = (ViewHolder) holder;
            content.setItem(items.get(position));
            content.parentLayout.setBackgroundResource(R.drawable.item_view_background);
            content.name.setText(items.get(position).getName());
            content.initialPrice.setText("$"+items.get(position).getInitialPrice());
            content.currentPrice.setText("$"+items.get(position).getCurrentPrice());
            Picasso.get().load(items.get(position).getURL()).into(content.itemIcon);
            if (Integer.parseInt(items.get(position).getDifference()) <= 0) {
                content.difference.setBackgroundColor(getColor(R.color.green));
                content.difference.setText(items.get(position).getDifference()+"%");
            } else if (Integer.parseInt(items.get(position).getDifference()) <= 20) {
                content.difference.setBackgroundColor(getColor(R.color.yellow));
                content.difference.setText("+"+items.get(position).getDifference()+"%");
            } else if (Integer.parseInt(items.get(position).getDifference()) <= 40) {
                content.difference.setBackgroundColor(getColor(R.color.orange));
                content.difference.setText("+"+items.get(position).getDifference()+"%");
            } else  {
                content.difference.setBackgroundColor(getColor(R.color.red));
                content.difference.setText("+"+items.get(position).getDifference()+"%");
            }
            content.parentLayout.setOnLongClickListener(view -> {
                selectionMode = true;
                ((AppCompatActivity) view.getContext()).startSupportActionMode(callback);
                content.item.setSelected(true);
                content.itemView.setBackgroundColor(content.item.getIsSelected() ? Color.GRAY : Color.WHITE);
                return true;
            });
            content.parentLayout.setOnClickListener(e -> {
                if (selectionMode) {
                    content.item.setSelected(!content.item.getIsSelected());
                    content.itemView.setBackgroundColor(content.item.getIsSelected() ? Color.GRAY : Color.WHITE);
                } else {
                    Intent intent = new Intent(ItemsView.this, WebViewActivity.class);
                    intent.putExtra("items", content.item.getSite());
                    startActivity(intent);
                }
            });


        }

        /**
         * {@inheritDoc}
         * @return
         */
        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        void deleteSelection() {
            for (Item item : items) {
                if (item.getIsSelected()) {
                    selectedItems.add(item);
                }
            }
            items.removeAll(selectedItems);
            selectedItems.clear();
        }

        void addItem(Item item) {
            items.add(item);
        }

        void refresh() {
            for (Item item : items) {
                item.updateCurrentPrice();
            }
            notifyDataSetChanged();
        }

        ArrayList<Item> getItems() {
            return items;
        }

        Item getSelected() {
            for (Item item : items) {
                if (item.getIsSelected()) {
                    return item;
                }
            }
            return null;
        }

        void sort(int type) {
            switch (type) {
                case 0:
                    Collections.sort(items, Item.COMPARE_BY_NAME);
                    break;
                case 1:
                    Collections.sort(items, Item.COMPARE_BY_CURR);
                    break;
                case 2:
                    Collections.sort(items, Item.COMPARE_BY_DIFF);
                    break;
            }
            notifyDataSetChanged();
        }

        /**
         * Instance used by RecyclerViewer.
         */
        class ViewHolder extends RecyclerView.ViewHolder {

            /**
             * Fields used by the item_container layout.
             */
            TextView name, initialPrice, currentPrice, difference;
            ImageView itemIcon;
            ConstraintLayout parentLayout;
            Item item;

            /***
             * Default Constructor
             * @param itemView View Containing the holder views.
             */
            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.edit_item_name);
                initialPrice = itemView.findViewById(R.id.edit_initial_price);
                currentPrice = itemView.findViewById(R.id.current_price);
                difference = itemView.findViewById(R.id.difference);
                itemIcon = itemView.findViewById(R.id.edit_icon);
                parentLayout = itemView.findViewById(R.id.item_holder);
            }

            void setItem(Item item) {
                this.item = item;
            }
        }
    }

}
