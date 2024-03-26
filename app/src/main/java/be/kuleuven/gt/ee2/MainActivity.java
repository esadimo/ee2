package be.kuleuven.gt.ee2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.os.Handler;
import android.os.Looper;
import okhttp3.OkHttpClient;
import androidx.appcompat.app.AppCompatActivity;


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
    }
}
