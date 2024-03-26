package be.kuleuven.gt.ee2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class new_order extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        fetchDataPeriodically();

        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this)
                .asGif()
                .load(R.drawable.food_delivery)
                .into(imageView);
    }

    private void fetchDataPeriodically() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://studev.groept.be/api/a23ib2c01/New_Get_Latest_Data")
                        .build();
                Log.d("NewOrderActivity", "Sending network request");

                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        Log.e("NewOrderActivity", "Error during network request", e);
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            Log.d("NewOrderActivity", "Response data: " + responseData);

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<DB>>(){}.getType();
                            List<DB> dbList = gson.fromJson(responseData, type);

                            runOnUiThread(() -> {
                                updateUI(dbList);
                            });
                        } else {
                            Log.e("NewOrderActivity", "Response not successful");
                        }
                    }
                });

                handler.postDelayed(this, 1000); // 1秒后再次执行
            }
        };

        executorService.submit(runnable); // 使用ExecutorService异步执行任务
    }

    private void updateUI(List<DB> dbList) {
        Log.d("NewOrderActivity", "updateUI called");

        if (dbList != null && !dbList.isEmpty()) {
            DB firstItem = dbList.get(0);
            int id = firstItem.getId();
            int tableNumber = firstItem.getTableNumber();
            String timestamp = firstItem.getTime();
            Log.d("NewOrderActivity", "Updating UI with ID: " + id + ", Table Number: " + tableNumber + ", Order Time: " + timestamp + ", Plate Status: " + firstItem.getPlateStatus());

            TextView customerIdTextView = findViewById(R.id.customer_id_value);
            customerIdTextView.setText(String.valueOf(id));

            TextView tableNumberTextView = findViewById(R.id.table_number_value);
            tableNumberTextView.setText(String.valueOf(tableNumber));

            TextView timestampTextView = findViewById(R.id.time_value);
            timestampTextView.setText(timestamp);

            updateUIBasedOnPlateStatus(firstItem.getPlateStatus());
        } else {
            Log.d("NewOrderActivity", "DB list is empty or null");
        }
    }

    private void updateUIBasedOnPlateStatus(String plateStatus) {
        TextView deliveryLabel = findViewById(R.id.delivery_label);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        ImageView imageView = findViewById(R.id.imageView);
        TextView plateStatusValue = findViewById(R.id.plate_status_value);
        TextView plateStatusLabel = findViewById(R.id.plate_status_label);

        if ("No".equals(plateStatus)) {
            deliveryLabel.setVisibility(View.VISIBLE);
            deliveryLabel.setText("Delivering...");
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            plateStatusLabel.setVisibility(View.GONE);
            plateStatusValue.setVisibility(View.GONE);
        }
        if("Yes".equals(plateStatus)) {
            Log.d("NewOrderActivity", "Plate status is No, updating UI for 'No' status");
            deliveryLabel.setVisibility(View.VISIBLE);
            deliveryLabel.setText("Returning...");
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            plateStatusLabel.setVisibility(View.GONE);
            plateStatusValue.setVisibility(View.GONE);
        }
        if("".equals(plateStatus)) {
            deliveryLabel.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            // 当plateStatus不是"Yes"或"No"时
            plateStatusLabel.setVisibility(View.VISIBLE);
            plateStatusValue.setVisibility(View.VISIBLE);
            plateStatusValue.setText(plateStatus); // 显示实际的plateStatus值
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}


