package be.kuleuven.gt.ee2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class order_history extends AppCompatActivity {

    private static String GET_URL = "https://studev.groept.be/api/a22pt315/login/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history);
    }



}