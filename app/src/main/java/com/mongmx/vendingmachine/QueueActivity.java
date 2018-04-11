package com.mongmx.vendingmachine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.mongmx.vendingmachine.adapters.QueueAdapter;
import com.mongmx.vendingmachine.api.APIService;
import com.mongmx.vendingmachine.models.Queue;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class QueueActivity extends Activity {

    private RestAdapter restAdapter;
    TextView queueCount;
    ListView listViewQueue;
    QueueAdapter mQueueAdapter;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_queue);

        queueCount = (TextView) findViewById(R.id.queueCount);
        listViewQueue = (ListView) findViewById(R.id.listViewQueue);

//        restAdapter = new RestAdapter.Builder().setEndpoint(getString(R.string.api_endpoint)).build();
        SharedPreferences sp = getSharedPreferences("VM_PREF", Context.MODE_PRIVATE);
        restAdapter = new RestAdapter.Builder().setEndpoint(sp.getString("Endpoint", "")).build();
        getQueue();

        timer = new Timer();
        refreshQueue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_queue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_main) {
            Intent intent = new Intent(this, MainActivity.class);
            QueueActivity.this.startActivity(intent);
        } else if (id == R.id.action_logout) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getQueue() {
        APIService service = restAdapter.create(APIService.class);
        service.getQueueCallback(new Callback<List<Queue>>() {
            @Override
            public void success(List<Queue> queues, Response response) {
                showData(queues);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void showData(List<Queue> queues) {
        mQueueAdapter = new QueueAdapter(this, queues);
        queueCount.setText(String.valueOf(queues.size()));
        listViewQueue.setAdapter(mQueueAdapter);
//        Toast.makeText(this, "All queue = "+String.valueOf(queues.size()), Toast.LENGTH_SHORT).show();
    }

    private void refreshQueue() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getQueue();
                refreshQueue();
            }
        }, 10000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

}
