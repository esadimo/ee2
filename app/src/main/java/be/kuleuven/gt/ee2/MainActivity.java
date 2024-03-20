package be.kuleuven.gt.ee2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import android.widget.TextView;


import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
public class MainActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newOrderButton = findViewById(R.id.button);
        Button orderHistoryButton = findViewById(R.id.button2);
        newOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start NewOrderActivity
                Intent intent = new Intent(MainActivity.this, new_order.class);
                startActivity(intent);
            }
        });

        orderHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start NewOrderActivity
                Intent intent = new Intent(MainActivity.this, order_history.class);
                startActivity(intent);
            }
        });
//        fetchDataPeriodically();
    }
//    private void fetchDataPeriodically() {
//        final Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                Request request = new Request.Builder()
//                        .url("https://studev.groept.be/api/a23ib2c01/New_Get_Latest_Data")
//                        .build();
//
//                try (Response response = client.newCall(request).execute()) {
//                    String responseData = response.body().string();
//                    Gson gson = new Gson();
//
//                    Type type = new com.google.gson.reflect.TypeToken<List<DB>>(){}.getType();
//                    List<DB> dbList = gson.fromJson(responseData, type);
//
//                    // 更新UI操作
//                    runOnUiThread(() -> updateUI(dbList));
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                handler.postDelayed(this, 30000); // 30秒后再次执行
//            }
//        };
//        handler.post(runnable); // 立即执行
//    }
//
//    private void updateUI(List<DB> dbList) {
//        // 假设您想展示第一个元素的id。确保dbList不为空且至少有一个元素。
//        if (dbList != null && !dbList.isEmpty()) {
//            DB firstItem = dbList.get(0); // 获取列表中的第一个元素
//            int id = firstItem.getId(); // 获取id
//
//            // 在UI线程中更新TextView
//            runOnUiThread(() -> {
//                TextView customerIdTextView = findViewById(R.id.customer_id_value);
//                customerIdTextView.setText(String.valueOf(id));
//            });
//        }
//    }


}
