package com.mongmx.vendingmachine.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mongmx.vendingmachine.R;
import com.mongmx.vendingmachine.api.APIService;
import com.mongmx.vendingmachine.models.Queue;
import com.mongmx.vendingmachine.models.QueueItem;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class QueueAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    List<Queue> mQueue;
    ViewHolder mViewHolder;
    RestAdapter restAdapter;
//    Context mContext;

    public QueueAdapter(Activity activity, List<Queue> queues) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mQueue = queues;
//        SharedPreferences sp = mContext.getSharedPreferences("VM_PREF", Context.MODE_PRIVATE);
        restAdapter = new RestAdapter
                .Builder()
//                .setEndpoint(activity.getApplication().getString(R.string.api_endpoint))
//                .setEndpoint(sp.getString("Endpoint", ""))
                .setEndpoint("http://crm.sarabunshops.com/api")
                .build();
    }

    @Override
    public int getCount() {
        return this.mQueue.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mQueue.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.queue_item, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.id = (TextView) convertView.findViewById(R.id.textViewQueueId);
            mViewHolder.item = (TextView) convertView.findViewById(R.id.textViewQueueItem);
            mViewHolder.itemQty = (TextView) convertView.findViewById(R.id.textViewQueueItemQty);
            mViewHolder.closeQueue = (Button) convertView.findViewById(R.id.buttonCloseQueue);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        final Queue queue = mQueue.get(position);
        String itemName = "";
        String itemQty = "";
        for (QueueItem item : queue.getQueueItems()) {
            itemName += item.getName()+"\n";
            itemQty += String.valueOf(item.getQuantity())+"\n";
        }
        mViewHolder.id.setText(String.valueOf(queue.getId()));
        mViewHolder.item.setText(itemName);
        mViewHolder.itemQty.setText(itemQty);
        mViewHolder.closeQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postCloseQueue(v, queue);
                mQueue.remove(queue);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView id;
        TextView item;
        TextView itemQty;
        Button closeQueue;
    }

    private void postCloseQueue(View v, Queue queue) {
        APIService service = restAdapter.create(APIService.class);
        service.postCloseQueue(queue, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
        Toast.makeText(v.getContext(), "Close queue "+String.valueOf(queue.getId()), Toast.LENGTH_SHORT).show();
    }

}
