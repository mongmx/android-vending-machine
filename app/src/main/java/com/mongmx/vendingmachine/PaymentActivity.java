package com.mongmx.vendingmachine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.mongmx.vendingmachine.adapters.OrderAdapter;
import com.mongmx.vendingmachine.api.APIService;
import com.mongmx.vendingmachine.models.Order;
import com.mongmx.vendingmachine.models.Queue;
import com.mongmx.vendingmachine.models.QueueItem;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PaymentActivity extends Activity {

    private final String TAG = PaymentActivity.class.getSimpleName();
    private static UsbSerialPort sPort = null;
//    private static UsbSerialPort sPort = 0;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {
        @Override
        public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

        @Override
        public void onNewData(final byte[] data) {
            PaymentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PaymentActivity.this.updateReceivedData(data);
                }
            });
        }
    };
//    UsbDeviceConnection connection;

    TextView paymentTitle;
    TextView textViewCoinCount;
    TextView textViewPayAction;
    TextView textViewPayActionCash;
    TextView textViewPayOrder;
//    TextView textViewTotalPay;
    ListView mListView;
    Button btnInsertCoin;
    Button btnCash;


    OrderAdapter mOrderAdapter;
    List<Order> orders;
    double orderTotal = 0;
    String ordersJson;
    Dialog dialog;
    Dialog dialogPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment);

        paymentTitle = (TextView) findViewById(R.id.payment_title);
        textViewCoinCount = (TextView) findViewById(R.id.textViewCoinCount);
        textViewPayAction = (TextView) findViewById(R.id.textViewPayAction);
        textViewPayActionCash = (TextView) findViewById(R.id.textViewPayActionCash);
        textViewPayOrder = (TextView) findViewById(R.id.textViewPayOrder);
        mListView = (ListView) findViewById(R.id.listViewPayOrder);
        btnInsertCoin = (Button) findViewById(R.id.btn_insert_coin);
        btnCash = (Button) findViewById(R.id.btnCash);

        Intent mIntent = getIntent();
        ordersJson = mIntent.getStringExtra("ordersJson");
        orders = new Gson().fromJson(ordersJson, new TypeToken<List<Order>>() {
        }.getType());

        mOrderAdapter = new OrderAdapter(this, orders);
        mListView.setAdapter(mOrderAdapter);

        int itemCount = 0;
        for (Order order : orders) {
            order.setStatus(1);
            itemCount += order.getQty();
            orderTotal += order.getPrice();
        }
        textViewPayOrder.setText("สินค้า " + String.valueOf(orders.size()) + " รายการ " + String.valueOf(itemCount) + " ชิ้น");
        textViewCoinCount.setText(String.format("%.0f", orderTotal));

        //connectArduino();
        /**
         * USB Serial Connection
         */

//        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().findAllDrivers(manager).get(0);
//        if (driver.isEmpty()) {
//            return;
//        }
//
//        // Open a connection to the first available driver.
//        UsbSerialDriver driver = availableDrivers.get(0);
//        connection = manager.openDevice(driver.getDevice());
//        if (connection == null) {
//            return;
//        }
//        // Read some data! Most have just one port (port 0).
        sPort = driver.getPorts().get(0);

        btnInsertCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                orderTotal--;
                orderTotal = 0;
                textViewCoinCount.setText(String.format("%.0f", orderTotal));
                if (orderTotal == 0) {
                    textViewPayAction.setText("จ่ายครบแล้ว... กรุณารอสลิป");
//                    textViewPayAction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 70);
                    postOrder();
                    btnInsertCoin.setVisibility(View.GONE);
                }
            }
        });

        btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                orderTotal--;
                orderTotal = 0;
                textViewCoinCount.setText(String.format("%.0f", orderTotal));
                if (orderTotal == 0) {
                    textViewPayAction.setText("จ่ายครบแล้ว... กรุณารอสลิป");
//                    textViewPayAction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 70);
                    for (Order order : orders) {
                        order.setStatus(3);
                    }
                    postOrder();
//                    btnInsertCoin.setVisibility(View.GONE);
                }
            }
        });

        dialogPayment = new Dialog(PaymentActivity.this);
        dialogPayment.setContentView(R.layout.dialog_payment);
        dialogPayment.setTitle("วิธีการจ่ายเงิน");
        TextView textViewTotalPay = (TextView) dialogPayment.findViewById(R.id.textViewTotalPay);
        textViewTotalPay.setText(String.format("%.0f", orderTotal));
        Button dBtnCoin = (Button) dialogPayment.findViewById(R.id.dBtnCoin);
        dBtnCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPayment.hide();
            }
        });
        Button dBtnCash = (Button) dialogPayment.findViewById(R.id.dBtnCash);
        dBtnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPayment.hide();
                orderTotal = 0;
                textViewCoinCount.setText(String.format("%.0f", orderTotal));
                if (orderTotal == 0) {
                    textViewPayAction.setVisibility(View.GONE);
                    textViewPayActionCash.setVisibility(View.VISIBLE);
                    for (Order order : orders) {
                        order.setStatus(3);
                    }
                    postOrder();
                }
            }
        });
        dialogPayment.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
        Bundle conData = new Bundle();
        conData.putBoolean("clear_order", true);
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
            paymentTitle.setText("No device");
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                paymentTitle.setText("Device failed");
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                paymentTitle.setText("Error: " + e.getMessage());
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
//            paymentTitle.setText(sPort.getClass().getSimpleName());
            paymentTitle.setText("OK");
        }
        onDeviceStateChange();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
//        final String message = "Read " + data.length + " bytes: \n"
//                + HexDump.dumpHexString(data) + "\n\n";
//        paymentTitle.setText(message);
        final String message = new String(data);
        if (message.equals("C1")) {
            orderTotal--;
            textViewCoinCount.setText(String.format("%.0f", orderTotal));
            paymentTitle.setText(message + " " + String.valueOf(orderTotal));
            if (orderTotal == 0) {
                textViewPayAction.setText("จ่ายครบแล้ว... กรุณารอสลิป");
//                textViewPayAction.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 70);
                postOrder();
                btnInsertCoin.setVisibility(View.GONE);
            }
        }
    }

    private void postOrder() {
//        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(getString(R.string.api_endpoint)).build();
        SharedPreferences sp = getSharedPreferences("VM_PREF", Context.MODE_PRIVATE);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(sp.getString("Endpoint", "")).build();
//        Log.d("RETROFIT", sp.getString("Endpoint", ""));
        APIService service = restAdapter.create(APIService.class);
//        Log.i("RETROFIT", ordersJson);
        service.postOrderCallback(orders, new Callback<Queue>() {
            @Override
            public void success(Queue queue, Response response) {
                dialog = new Dialog(PaymentActivity.this);
                dialog.setContentView(R.layout.dialog_print);
                dialog.setTitle("รับรายการเสร็จแล้ว");
                TextView textViewQueueNumber = (TextView) dialog.findViewById(R.id.textViewQueueNumber);
                textViewQueueNumber.setText("คิวที่   " + String.valueOf(queue.getId()));
                dialog.show();
                printSlip(queue);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        dialogPayment.dismiss();
                        Bundle conData = new Bundle();
                        conData.putBoolean("clear_order", true);
                        Intent intent = new Intent();
                        intent.putExtras(conData);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, 5000);
            }

            @Override
            public void failure(RetrofitError error) {
                postOrder();
//                Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void printSlip(Queue queue) {
        String id = String.valueOf(queue.getId());
        String total = String.valueOf(queue.getTotal());
        String item = "";
        for (QueueItem qItem: queue.getQueueItems()) {
            int quantity = qItem.getQuantity();
            int price = (int)(qItem.getPrice());
            item += qItem.getName() + "\rX" + String.valueOf(quantity) +
                    " = " + String.valueOf(quantity * price) + " Baht\r";
        }
        String temp = "    Queue ## " + id + " ##\r" +
                "-------------------------------\r" +
                "Item\r" +
                item +
                "-------------------------------\r" +
                "Total = " + total + " Baht\r" +
                "-------------------------------\r\r\r\n";
//        String temp = "\n{q:" + id + ",t:" + total;
//        temp += ",i:[{n:\"xxx\",a:1,p:10},{n:\"xxx\",a:1,p:10}]}";
        for (int i = 0; i < temp.length(); i++) {
            char b = temp.charAt(i);
            sendCommand((byte) b);
        }
    }

    private void sendCommand(byte b) {
        byte[] temp = new byte[1];
        temp[0] = b;
        try {
            if (sPort != null) sPort.write(temp, 200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnBack(View v) {
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }

        Bundle conData = new Bundle();
        conData.putBoolean("clear_order", false);
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);

        finish();
    }
}
