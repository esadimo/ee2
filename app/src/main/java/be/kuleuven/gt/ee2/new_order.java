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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class new_order extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();

    // UI components as member variables
    private TextView customerIdTextView, tableNumberTextView, timestampTextView, plateStatusValue, distanceTextView;
    private ProgressBar progressBar;
    private ImageView imageView;
    private LinearLayout distanceLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        // Initialize UI components
        initViewComponents();
        fetchDataPeriodically();
    }

    private void initViewComponents() {
        customerIdTextView = findViewById(R.id.customer_id_value);
        tableNumberTextView = findViewById(R.id.table_number_value);
        timestampTextView = findViewById(R.id.time_value);
        plateStatusValue = findViewById(R.id.plate_status_value);
        distanceTextView = findViewById(R.id.distance_left);
        progressBar = findViewById(R.id.progress_bar);
        imageView = findViewById(R.id.imageView);
        distanceLayout = findViewById(R.id.distance_layout);

        Glide.with(this).asGif().load(R.drawable.food_delivery).into(imageView);
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
                            Type type = new TypeToken<List<DB>>(){}.getType();
                            List<DB> dbList = gson.fromJson(responseData, type);

                            runOnUiThread(() -> updateUI(dbList));
                        }
                    }
                });

                handler.postDelayed(this, 1000);
            }
        };

        executorService.submit(runnable);
    }

    private void updateUI(List<DB> dbList) {
        if (dbList != null && !dbList.isEmpty()) {
            DB firstItem = dbList.get(0);

            customerIdTextView.setText(String.valueOf(firstItem.getId()));
            tableNumberTextView.setText(String.valueOf(firstItem.getTableNumber()));
            timestampTextView.setText(firstItem.getTime());
            plateStatusValue.setText(firstItem.getPlateStatus());

            updateUIBasedOnPlateStatus(firstItem.getPlateStatus());
            updateProgressBarAndDistance(firstItem);
        }
    }

    private void updateUIBasedOnPlateStatus(String plateStatus) {
        TextView deliveryLabel = findViewById(R.id.delivery_label);

        boolean isStatusYes = "Yes".equals(plateStatus);
        deliveryLabel.setVisibility(isStatusYes ? View.VISIBLE : View.VISIBLE);
        progressBar.setVisibility(isStatusYes ? View.VISIBLE : View.VISIBLE);
        imageView.setVisibility(isStatusYes ? View.VISIBLE : View.GONE);
        distanceLayout.setVisibility(isStatusYes ? View.VISIBLE : View.GONE);

        if ("Yes".equals(plateStatus)) {
            deliveryLabel.setText("Delivering...");
        } else if ("No".equals(plateStatus)) {
            deliveryLabel.setText("Returning...");
        }
    }

    private void updateProgressBarAndDistance(DB dbItem) {
        int carLocation = dbItem.getCarLocation();
        String plateStatus = dbItem.getPlateStatus();
        int tableNumber = dbItem.getTableNumber();

        updateProgressBar(plateStatus, tableNumber, carLocation);
        updateDistanceText(plateStatus, carLocation);
    }

    private void updateProgressBar(String plateStatus, int tableNumber, int carLocation) {
        if ("No".equals(plateStatus)) {
            progressBar.setProgress(100);
            return;
        }

        int progress = 0;
        if ("Yes".equals(plateStatus)) {
            if (tableNumber == 1 || tableNumber == 3) {
                switch (carLocation) {
                    case 40: progress = 0; break;
                    case 30: progress = 25; break;
                    case 20: progress = 50; break;
                    case 10: progress = 75; break;
                    case 0: progress = 100; break;
                    default: return;
                }
            } else if (tableNumber == 2) {
                switch (carLocation) {
                    case 20: progress = 0; break;
                    case 10: progress = 50; break;
                    case 0: progress = 100; break;
                    default: return;
                }
            }
        }

        progressBar.setProgress(progress);
    }

    private void updateDistanceText(String plateStatus, int carLocation) {
        if ("Yes".equals(plateStatus)){
            if(carLocation==0)
            {
                distanceTextView.setText("");
            } else
            {
                distanceTextView.setText(carLocation+"cm");
            }

        } else {
            distanceTextView.setText("0cm");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}



