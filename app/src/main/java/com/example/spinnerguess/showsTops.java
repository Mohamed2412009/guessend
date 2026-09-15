package com.example.spinnerguess;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class showsTops extends AppCompatActivity {

    private TextView tvTop1, tvTop2, tvTop3;
    private Button btnNewGameTop, btnExitTop;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows_tops);

        tvTop1 = findViewById(R.id.tvTop1);
        tvTop2 = findViewById(R.id.tvTop2);
        tvTop3 = findViewById(R.id.tvTop3);
        btnNewGameTop = findViewById(R.id.btnNewGameTop);
        btnExitTop = findViewById(R.id.btnExitTop);

        db = new DBHelper(this);
        loadTopScores();

        btnNewGameTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(showsTops.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnExitTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }

    private void loadTopScores() {
        ArrayList<ModelUser> users = db.selectAll();

        if (users.size() > 0 && users.get(0) != null) {
            tvTop1.setText("1. " + users.get(0).getUserName() + " - Score: " + users.get(0).getScore());
        }
        if (users.size() > 1 && users.get(1) != null) {
            tvTop2.setText("2. " + users.get(1).getUserName() + " - Score: " + users.get(1).getScore());
        }
        if (users.size() > 2 && users.get(2) != null) {
            tvTop3.setText("3. " + users.get(2).getUserName() + " - Score: " + users.get(2).getScore());
        }
    }
}