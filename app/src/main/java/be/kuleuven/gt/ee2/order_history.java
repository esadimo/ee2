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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import android.view.Gravity;

public class order_history extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history);
        linearLayout = findViewById(R.id.linearLayout);
        fetchDataPeriodically();
    }

    private void fetchDataPeriodically() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://studev.groept.be/api/a23ib2c01/New_History_Order")
                        .build();

                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            String responseData = response.body().string();
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<DB>>(){}.getType();
                            List<DB> dbList = gson.fromJson(responseData, type);

                            handler.post(() -> updateUI(dbList));
                        }
                    }
                });

                handler.postDelayed(this, 1000);
            }
        };

        executorService.submit(runnable);
    }

    private void updateUI(List<DB> dbList) {
        linearLayout.removeAllViews();

        for (DB item : dbList) {
            LinearLayout row = createRowLayout();
            row.addView(createTextView(String.valueOf(item.getId())));
            row.addView(createTextView(String.valueOf(item.getTableNumber())));
            row.addView(createTextView(item.getDate()));
            linearLayout.addView(row);
        }
    }

    private LinearLayout createRowLayout() {
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
        return row;
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        textView.setText(text);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}

