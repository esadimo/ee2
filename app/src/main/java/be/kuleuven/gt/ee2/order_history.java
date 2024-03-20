package be.kuleuven.gt.ee2;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import android.view.Gravity;


public class order_history extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history);
        fetchDataPeriodically();
    }
    private void fetchDataPeriodically() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://studev.groept.be/api/a23ib2c01/New_History_Order")
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

                handler.postDelayed(this, 30000); // 每30秒执行一次
            }
        };

        executorService.submit(runnable); // 使用ExecutorService异步执行任务
    }

    private void updateUI(List<DB> dbList) {
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.removeAllViews(); // 移除所有现有视图

        for (DB item : dbList) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);

            // 为每个字段创建一个 TextView
            TextView idTextView = createTextView(String.valueOf(item.getId()));
            TextView tableNumberTextView = createTextView(String.valueOf(item.getTableNumber()));
            TextView timeTextView = createTextView(item.getDate());

            // 将 TextView 添加到行中
            row.addView(idTextView);
            row.addView(tableNumberTextView);
            row.addView(timeTextView);

            // 将行添加到外层的 LinearLayout 中
            linearLayout.addView(row);
        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));
        textView.setText(text);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }


}
