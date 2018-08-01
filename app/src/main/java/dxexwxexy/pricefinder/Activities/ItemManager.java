package dxexwxexy.pricefinder.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Collections;

import dxexwxexy.pricefinder.Data.Item;
import dxexwxexy.pricefinder.Data.ItemDatabase;
import dxexwxexy.pricefinder.R;

/**
 * Class required to use a RecyclerViewer.
 *
 */
public class ItemManager extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Fields used by the RecyclerViewer.
     */
    private ItemDatabase itemDatabase;
    private ArrayList<Item> items;
    private ArrayList<Item> selectedItems;
    private boolean selectionMode;
    private Context context;
    private ActionMode.Callback callback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            selectionMode = true;
            mode.getMenuInflater().inflate(R.menu.manage_items, menu);
            ((ItemsMainActivity)context).disableRefresh();
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
                    Item edit = getSelected();
                    if (edit == null) {
                        Toast.makeText(context, "No Item Selected", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.edit_dialog, null);
                        EditText editName = view.findViewById(R.id.edit_item_name);
                        editName.setText(edit.getName());
                        EditText editURL = view.findViewById(R.id.edit_url);
                        editURL.setText(edit.getURL());
                        Button done = view.findViewById(R.id.edit_done);
                        alertBuilder.setView(view);
                        AlertDialog dialog = alertBuilder.create();
                        dialog.show();
                        done.setOnClickListener(e -> {
                            String ebay = "\\S+\\.ebay\\.\\S+";
                            String amazon = "\\S+\\.amazon\\.\\S+";
                            if (!editName.getText().toString().matches("")) {
                                if (editURL.getText().toString().matches(ebay) || editURL.getText().toString().matches(amazon)) {
                                    itemDatabase.editItem(edit, editName.getText().toString(), editURL.getText().toString());
                                    edit.setName(editName.getText().toString());
                                    edit.setURL(editURL.getText().toString());
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(context, "Missing Fields", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
            ((ItemsMainActivity)context).enableRefresh();
            selectionMode = false;
        }
    };

    /***
     * Default Constructor
     */
    ItemManager(Context context) {
        this.context = context;
        initItems();
        this.selectedItems = new ArrayList<>();
        this.selectionMode = false;
    }

    /**
     * Constructor for restoring to previous state.
     *
     * @param items Previously created list.
     */
    ItemManager(ArrayList<Item> items, Context context) {
        this.context = context;
        this.items = items;
        this.selectedItems = new ArrayList<>();
        this.selectionMode = false;
    }

    /***
     * Method to simulate data read.
     */
    private void initItems() {
        itemDatabase = new ItemDatabase(context);
        items = itemDatabase.getItems();
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
        content.itemIcon.setImageResource(items.get(position).getStore().equals("amazon") ?
                R.drawable.ic_amazon : R.drawable.ic_ebay);
        if (Integer.parseInt(items.get(position).getDifference()) <= 0) {
            content.difference.setBackgroundColor(context.getColor(R.color.green));
            content.difference.setText(items.get(position).getDifference()+"%");
        } else if (Integer.parseInt(items.get(position).getDifference()) <= 20) {
            content.difference.setBackgroundColor(context.getColor(R.color.yellow));
            content.difference.setText("+"+items.get(position).getDifference()+"%");
        } else if (Integer.parseInt(items.get(position).getDifference()) <= 40) {
            content.difference.setBackgroundColor(context.getColor(R.color.orange));
            content.difference.setText("+"+items.get(position).getDifference()+"%");
        } else  {
            content.difference.setBackgroundColor(context.getColor(R.color.red));
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
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("items", content.item.getURL());
                context.startActivity(intent);
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

    private void deleteSelection() {
        for (Item item : items) {
            if (item.getIsSelected()) {
                selectedItems.add(item);
                itemDatabase.deleteItem(item);
            }
        }
        items.removeAll(selectedItems);
        selectedItems.clear();
    }

    void addItem(Item item) {
        items.add(item);
        ((ItemsMainActivity) context).initUpdating();
        new Thread(() -> {
            while (!item.isFetched());
            item.updateCurrentPrice();
            itemDatabase.addItem(item);
        }).start();
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

    private Item getSelected() {
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
            case 3:
                Collections.sort(items, Item.COMPARE_BY_STORE);
        }
        notifyDataSetChanged();
    }

    boolean isFetched() {
        boolean fetchDone = true;
        for (Item item : items) {
            fetchDone &= item.isFetched();
        }
        return fetchDone;
    }

    public void resetFetch() {

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