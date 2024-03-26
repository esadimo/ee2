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

                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<DB>>(){}.getType();
                            List<DB> dbList = gson.fromJson(responseData, type);

                            runOnUiThread(() -> {
                                updateUI(dbList);
                            });
                        }
                    }
                });

                handler.postDelayed(this, 1000);
            }
        };

        executorService.submit(runnable); // 使用ExecutorService异步执行任务
    }

    private void updateUI(List<DB> dbList) {

        if (dbList != null && !dbList.isEmpty()) {
            DB firstItem = dbList.get(0);
            int id = firstItem.getId();
            int tableNumber = firstItem.getTableNumber();
            String timestamp = firstItem.getTime();
            String platestatus = firstItem.getPlateStatus();

            TextView customerIdTextView = findViewById(R.id.customer_id_value);
            customerIdTextView.setText(String.valueOf(id));

            TextView tableNumberTextView = findViewById(R.id.table_number_value);
            tableNumberTextView.setText(String.valueOf(tableNumber));

            TextView timestampTextView = findViewById(R.id.time_value);
            timestampTextView.setText(String.valueOf(timestamp));

            TextView plateStatus = findViewById(R.id.plate_status_value);
            updateUIBasedOnPlateStatus(firstItem.getPlateStatus());


            int carLocation = firstItem.getCarLocation();
            updateProgressBar(platestatus,tableNumber, carLocation);
            updateDistanceText(platestatus,carLocation);
        }

    }

    private void updateUIBasedOnPlateStatus(String plateStatus) {
        TextView deliveryLabel = findViewById(R.id.delivery_label);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        ImageView imageView = findViewById(R.id.imageView);
        TextView plateStatusValue = findViewById(R.id.plate_status_value);
        TextView plateStatusLabel = findViewById(R.id.plate_status_label);

        if ("Yes".equals(plateStatus)) {
            deliveryLabel.setVisibility(View.VISIBLE);
            deliveryLabel.setText("Delivering...");
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            plateStatusLabel.setVisibility(View.VISIBLE);
            plateStatusValue.setVisibility(View.VISIBLE);
            plateStatusValue.setText(plateStatus);

        } else if ("No".equals(plateStatus)) {
            deliveryLabel.setVisibility(View.VISIBLE);
            deliveryLabel.setText("Returning...");
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            plateStatusLabel.setVisibility(View.VISIBLE);
            plateStatusValue.setVisibility(View.VISIBLE);
            plateStatusValue.setText(plateStatus);

        }else {
            deliveryLabel.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            plateStatusLabel.setVisibility(View.VISIBLE);
            plateStatusValue.setVisibility(View.VISIBLE);
            plateStatusValue.setText(plateStatus);
        }
    }

    private void updateProgressBar(String plateStatus,int tableNumber, int carLocation) {
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        if ("No".equals(plateStatus)) {
            progressBar.setProgress(100);
            return;
        }
        if ("Yes".equals(plateStatus)) {
            int progress;

            if (tableNumber == 1 || tableNumber == 3) {
                switch (carLocation) {
                    case 40:
                        progress = 0;
                        break;
                    case 30:
                        progress = 25;
                        break;
                    case 20:
                        progress = 50;
                        break;
                    case 10:
                        progress = 75;
                        break;
                    case 0:
                        progress = 100;
                        break;
                    default:
                        return; // 如果 carLocation 不在指定范围内，则不更新进度
                }
            } else if (tableNumber == 2) {
                switch (carLocation) {
                    case 20:
                        progress = 0;
                        break;
                    case 10:
                        progress = 50;
                        break;
                    case 0:
                        progress = 100;
                        break;
                    default:
                        return; // 如果 carLocation 不在指定范围内，则不更新进度
                }
            } else {
                return; // 如果 tableNumber 不是 1、2 或 3，则不更新进度
            }

            progressBar.setProgress(progress);
        }
    }

    private void updateDistanceText(String plateStatus,int carLocation) {
        if ("Yes".equals(plateStatus)){
            TextView distanceTextView = findViewById(R.id.distance_left);
            String distanceText = carLocation + "cm";
            distanceTextView.setText(distanceText);
        } else {
            TextView distanceTextView = findViewById(R.id.distance_left);
            String distanceText = 0 + "cm";
            distanceTextView.setText(distanceText);
        }

    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}

