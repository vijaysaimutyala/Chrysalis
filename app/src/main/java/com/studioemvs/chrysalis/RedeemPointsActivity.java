package com.studioemvs.chrysalis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RedeemPointsActivity extends AppCompatActivity implements View.OnClickListener{
    int totalPoints;
    EditText redeemPoints;
    Button redeem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_points);
        Bundle bundle = getIntent().getExtras();
        totalPoints = bundle.getInt("actPoints");

        redeemPoints = (EditText)findViewById(R.id.edt_redeemPoints);
        redeem = (Button)findViewById(R.id.btn_redeemPoints);

        redeemPoints.setText(String.valueOf(totalPoints));
        redeem.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_redeemPoints:
                Toast.makeText(this, "Points redeemed for level progression", Toast.LENGTH_SHORT).show();
                redeemPoints.setText("");
                break;
        }

    }
}
