package dxexwxexy.pricefinder.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dxexwxexy.pricefinder.Data.Item;
import dxexwxexy.pricefinder.R;

public class ItemsView extends AppCompatActivity {

    /***
     * Fields used for UI manipulation.
     */
    private ArrayList<Item> items;
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
        initItems();
        initRecyclerView();
        initUI();
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
     * Initializes the RecyclerViewer for the items of operations.
     */
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.items_list);
        adapter = new RecyclerViewAdapter(items);
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
            View mView = getLayoutInflater().inflate(R.layout.add_dialog, null);
            EditText name = mView.findViewById(R.id.add_name);
            EditText price = mView.findViewById(R.id.add_price);
            EditText url = mView.findViewById(R.id.add_url);
            Button add = mView.findViewById(R.id.add_item);
            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
            add.setOnClickListener(v -> {
                items.add(new Item(name.getText().toString(), url.getText().toString(), Double.parseDouble(price.getText().toString())));
                dialog.dismiss();
            });
        });
        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            for (Item item : items) {
                item.updateCurrentPrice();
            }
            adapter.notifyDataSetChanged();
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
        outState.putParcelableArrayList("items", items);
    }

    /***
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        items = savedInstanceState.getParcelableArrayList("items");
        initRecyclerView();
    }

    public class ItemListView extends ArrayAdapter<Item> {

        class ViewHolder {

            /**
             * Fields used by the item_container layout.
             */
            TextView name, initialPrice, currentPrice, difference;
            ImageView itemIcon;
            ConstraintLayout parentLayout;

            /***
             * Default Constructor
             * @param itemView View Containing the holder views.
             */
            ViewHolder(View itemView) {
                name = itemView.findViewById(R.id.item_name);
                initialPrice = itemView.findViewById(R.id.initial_price);
                currentPrice = itemView.findViewById(R.id.current_price);
                difference = itemView.findViewById(R.id.difference);
                itemIcon = itemView.findViewById(R.id.item_icon);
                parentLayout = itemView.findViewById(R.id.item_holder);
                registerForContextMenu(parentLayout);
            }

            public void setData(String name, String initialPrice, String currentPrice, String difference) {
                this.name.setText(name);
                // TODO: 7/21/2018 finish listview implmentation
            }
        }

        /**
         * Constructor
         *
         * @param context  The current context.
         * @param items  The objects to represent in the ListView.
         */
        public ItemListView(@NonNull Context context, @NonNull List<Item> items) {
            super(context, R.layout.item_container, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Item item = items.get(position);
            assert convertView != null;
            ViewHolder viewHolder = new ViewHolder(convertView);
            viewHolder.setData();

            return convertView;
        }
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

        /***
         * Default Constructor
         * @param items ArrayList containing instances of Item.
         */
        RecyclerViewAdapter(ArrayList<Item> items) {
            this.items = items;
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
        }

        /**
         * {@inheritDoc}
         * @return
         */
        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
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

            /***
             * Default Constructor
             * @param itemView View Containing the holder views.
             */
            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.item_name);
                initialPrice = itemView.findViewById(R.id.initial_price);
                currentPrice = itemView.findViewById(R.id.current_price);
                difference = itemView.findViewById(R.id.difference);
                itemIcon = itemView.findViewById(R.id.item_icon);
                parentLayout = itemView.findViewById(R.id.item_holder);
                registerForContextMenu(parentLayout);
            }
        }
    }
}
