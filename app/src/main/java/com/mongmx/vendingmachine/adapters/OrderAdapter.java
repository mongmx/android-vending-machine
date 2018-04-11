package com.mongmx.vendingmachine.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mongmx.vendingmachine.R;
import com.mongmx.vendingmachine.models.Order;

import java.util.List;

public class OrderAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    List<Order> mOrders;
    ViewHolder mViewHolder;

    public OrderAdapter(Activity activity, List<Order> orders) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mOrders = orders;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return this.mOrders.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return this.mOrders.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.order, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.name = (TextView) convertView.findViewById(R.id.order_product_name);
            mViewHolder.qty = (TextView) convertView.findViewById(R.id.order_product_qty);
            mViewHolder.price = (TextView) convertView.findViewById(R.id.order_product_price);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        Order order = mOrders.get(position);
        mViewHolder.name.setText(order.getName());
        mViewHolder.qty.setText(String.valueOf(order.getQty()) + " ชิ้น");
        mViewHolder.price.setText(String.format("%.02f", order.getPrice()) + " บาท");

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView qty;
        TextView price;
    }

}
