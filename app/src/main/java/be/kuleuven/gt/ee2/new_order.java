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
import android.widget.TextView;
import android.util.Log;
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

                            runOnUiThread(() -> updateUI(dbList));
                        }
                    }
                });

                handler.postDelayed(this, 30000); // 30秒后再次执行
            }
        };

        executorService.submit(runnable); // 使用ExecutorService异步执行任务
    }


private void updateUI(List<DB> dbList) {
    Log.d("NewOrderActivity", "updateUI called");

    if (dbList != null && !dbList.isEmpty()) {
        DB firstItem = dbList.get(0);
        int id = firstItem.getId();
        int tableNumber = firstItem.getTableNumber(); // 获取第一个元素的 Table Number
        String timestamp = firstItem.getTime();
        Log.d("NewOrderActivity", "Updating UI with ID: " + id + " and Table Number: " + tableNumber + "and Order Time: " + timestamp);

        TextView customerIdTextView = findViewById(R.id.customer_id_value);
        customerIdTextView.setText(String.valueOf(id));

        TextView tableNumberTextView = findViewById(R.id.table_number_value); // 确保使用正确的 TextView ID
        tableNumberTextView.setText(String.valueOf(tableNumber));

        TextView timestampTextView = findViewById(R.id.time_value);
        timestampTextView.setText(String.valueOf(timestamp));
    }

    Log.d("NewOrderActivity", "updateUI finished");
}



    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // 关闭ExecutorService
    }
}
