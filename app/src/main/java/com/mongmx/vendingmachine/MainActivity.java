package com.mongmx.vendingmachine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mongmx.vendingmachine.adapters.CategoryAdapter;
import com.mongmx.vendingmachine.adapters.OrderMainAdapter;
import com.mongmx.vendingmachine.adapters.ProductAdapter;
import com.mongmx.vendingmachine.api.APIService;
import com.mongmx.vendingmachine.models.Category;
import com.mongmx.vendingmachine.models.Order;
import com.mongmx.vendingmachine.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity {

    private RestAdapter restAdapter;
    private RelativeLayout menuCateLayout;
    private RelativeLayout cate1Layout;
    private RelativeLayout cate2Layout;
    private RelativeLayout cate3Layout;
    private RelativeLayout cate4Layout;
    private RelativeLayout cate5Layout;
    private RelativeLayout cate6Layout;
    private RelativeLayout cate7Layout;
    ListView mListView;
    GridView mCateGridView;
    GridView mGridView1;
    GridView mGridView2;
    GridView mGridView3;
    GridView mGridView4;
    GridView mGridView5;
    GridView mGridView6;
    GridView mGridView7;
    TextView textViewOrderTotal;
    TextView textViewCateName;
    Button btnClear;
    OrderMainAdapter mOrderAdapter;
    List<Product> allProducts = new ArrayList<>();
    List<Order> orders = new ArrayList<>();
    List<Category> mCategories;
    List<Product> mProducts;

    double orderTotal = 0;

    Timer timer;

    AdapterView.OnItemClickListener productClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            addToCart(((Product) parent.getItemAtPosition(position)));
        }
    };

    AdapterView.OnItemClickListener categoryClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            menuCateLayout.setVisibility(View.GONE);
            cate1Layout.setVisibility(View.VISIBLE);

            mProducts = mCategories.get(position).getProducts();
            mGridView1.setAdapter(new ProductAdapter(MainActivity.this, mProducts));
            mGridView1.setOnItemClickListener(productClickListener);
            textViewCateName.setText(mCategories.get(position).getName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        menuCateLayout = (RelativeLayout) findViewById(R.id.menu_cate);
        cate1Layout = (RelativeLayout) findViewById(R.id.cate_1);
        cate2Layout = (RelativeLayout) findViewById(R.id.cate_2);
        cate3Layout = (RelativeLayout) findViewById(R.id.cate_3);
        cate4Layout = (RelativeLayout) findViewById(R.id.cate_4);
        cate5Layout = (RelativeLayout) findViewById(R.id.cate_5);
        cate6Layout = (RelativeLayout) findViewById(R.id.cate_6);
        cate7Layout = (RelativeLayout) findViewById(R.id.cate_7);
        mListView = (ListView) findViewById(R.id.listView);
        mCateGridView = (GridView) findViewById(R.id.cateGridView);
        mGridView1 = (GridView) findViewById(R.id.gridView1);
        mGridView2 = (GridView) findViewById(R.id.gridView2);
        mGridView3 = (GridView) findViewById(R.id.gridView3);
        mGridView4 = (GridView) findViewById(R.id.gridView4);
        mGridView5 = (GridView) findViewById(R.id.gridView5);
        mGridView6 = (GridView) findViewById(R.id.gridView6);
        mGridView7 = (GridView) findViewById(R.id.gridView7);
        textViewOrderTotal = (TextView) findViewById(R.id.textViewOrderTotal);
        textViewCateName = (TextView) findViewById(R.id.txtCateName);
        btnClear = (Button) findViewById(R.id.btn_clear);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orders.clear();
                mOrderAdapter.notifyDataSetChanged();
                textViewOrderTotal.setText("รวม   0.00   บาท");
            }
        });

//        restAdapter = new RestAdapter.Builder().setEndpoint(getString(R.string.api_endpoint)).build();
        SharedPreferences sp = getSharedPreferences("VM_PREF", Context.MODE_PRIVATE);
        restAdapter = new RestAdapter.Builder().setEndpoint(sp.getString("Endpoint", "")).build();

        getProductList();

        timer = new Timer();
        refreshProductList();

        mOrderAdapter = new OrderMainAdapter(this, orders);
        mListView.setAdapter(mOrderAdapter);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        } else if (id == R.id.action_logout) {
//            finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void showMenuCate(View v) {
        cate1Layout.setVisibility(View.GONE);
        cate2Layout.setVisibility(View.GONE);
        cate3Layout.setVisibility(View.GONE);
        cate4Layout.setVisibility(View.GONE);
        cate5Layout.setVisibility(View.GONE);
        cate6Layout.setVisibility(View.GONE);
        cate7Layout.setVisibility(View.GONE);
        menuCateLayout.setVisibility(View.VISIBLE);
    }

    public void showCate1(View v) {
        menuCateLayout.setVisibility(View.GONE);
        cate1Layout.setVisibility(View.VISIBLE);
    }

    public void showCate2(View v) {
        menuCateLayout.setVisibility(View.GONE);
        cate2Layout.setVisibility(View.VISIBLE);
    }

    public void showCate3(View v) {
        menuCateLayout.setVisibility(View.GONE);
        cate3Layout.setVisibility(View.VISIBLE);
    }

    public void showCate4(View v) {
        menuCateLayout.setVisibility(View.GONE);
        cate4Layout.setVisibility(View.VISIBLE);
    }

    public void showCate5(View v) {
        menuCateLayout.setVisibility(View.GONE);
        cate5Layout.setVisibility(View.VISIBLE);
    }

    public void showCate6(View v) {
        menuCateLayout.setVisibility(View.GONE);
        cate6Layout.setVisibility(View.VISIBLE);
    }

    public void showCate7(View v) {
        menuCateLayout.setVisibility(View.GONE);
        cate7Layout.setVisibility(View.VISIBLE);
    }

    private void showData(List<Category> categories) {
        mCategories = categories;
        allProducts.clear();
        for (Category category : categories) {
            for (Product product : category.getProducts()) {
                allProducts.add(product);
            }
//            Log.i("CATEGORY", category.getName());
        }

        mCateGridView.setAdapter(new CategoryAdapter(this, categories));
        mCateGridView.setOnItemClickListener(categoryClickListener);

//        List<Product> products = categories.get(0).getProducts();
//        mGridView1.setAdapter(new ProductAdapter(this, products));
//        mGridView1.setOnItemClickListener(productClickListener);
//
//        products = categories.get(1).getProducts();
//        mGridView2.setAdapter(new ProductAdapter(this, products));
//        mGridView2.setOnItemClickListener(productClickListener);
//
//        products = categories.get(2).getProducts();
//        mGridView3.setAdapter(new ProductAdapter(this, products));
//        mGridView3.setOnItemClickListener(productClickListener);
//
//        products = categories.get(3).getProducts();
//        mGridView4.setAdapter(new ProductAdapter(this, products));
//        mGridView4.setOnItemClickListener(productClickListener);
//
//        products = categories.get(4).getProducts();
//        mGridView5.setAdapter(new ProductAdapter(this, products));
//        mGridView5.setOnItemClickListener(productClickListener);
//
//        products = categories.get(5).getProducts();
//        mGridView6.setAdapter(new ProductAdapter(this, products));
//        mGridView6.setOnItemClickListener(productClickListener);
//
//        products = categories.get(6).getProducts();
//        mGridView7.setAdapter(new ProductAdapter(this, products));
//        mGridView7.setOnItemClickListener(productClickListener);

//        Toast.makeText(this, "All product = "+String.valueOf(allProducts.size()), Toast.LENGTH_LONG).show();
    }

    private void addToCart(Product neededProduct) {
        for (Order order : orders) {
            if (order.getId() == neededProduct.getId()) {
                if (order.getQty() < order.getMaxQty()) {
                    order.setQty(order.getQty() + 1);
                    order.setPrice(order.getPrice() + neededProduct.getPrice());
                    mOrderAdapter.notifyDataSetChanged();
                    countTotal();
                }
                return;
            }
        }

        Order newItem = new Order();
        newItem.setId(neededProduct.getId());
        newItem.setName(neededProduct.getName());
        newItem.setPrice(neededProduct.getPrice());
        newItem.setQty(1);
        newItem.setMaxQty(neededProduct.getStock());

        orders.add(newItem);
        mOrderAdapter.notifyDataSetChanged();
        countTotal();
    }

    public void countTotal() {
        orderTotal = 0;
        for (Order order : orders) {
            orderTotal += order.getPrice();
        }
        textViewOrderTotal.setText("รวม   " + String.format("%.02f", orderTotal) + "   บาท");
    }

    private void getProductList() {
        APIService service = restAdapter.create(APIService.class);
        service.getProductListCallback(new Callback<List<Category>>() {
            @Override
            public void success(List<Category> categories, Response response) {
                showData(categories);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void postOrder(View v) {
        if (!orders.isEmpty()) {
            Intent mIntent = new Intent(this, PaymentActivity.class);
            mIntent.putExtra("ordersJson", new Gson().toJson(orders));
            this.startActivityForResult(mIntent, 90);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cate1Layout.setVisibility(View.GONE);
        cate2Layout.setVisibility(View.GONE);
        cate3Layout.setVisibility(View.GONE);
        cate4Layout.setVisibility(View.GONE);
        cate5Layout.setVisibility(View.GONE);
        cate6Layout.setVisibility(View.GONE);
        cate7Layout.setVisibility(View.GONE);
        menuCateLayout.setVisibility(View.VISIBLE);

        timer = new Timer();
        refreshProductList();

        switch(requestCode) {
            case 90:
//                if (resultCode != RESULT_OK) {
                    Bundle res = data.getExtras();
                    Boolean result = res.getBoolean("clear_order");
                    if (result == true) {
                        orders.clear();
                        mOrderAdapter.notifyDataSetChanged();
                        textViewOrderTotal.setText("รวม   0.00   บาท");
                    }
//                }
                break;
        }
    }

    private void refreshProductList() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getProductList();
                refreshProductList();
            }
        }, 10000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
