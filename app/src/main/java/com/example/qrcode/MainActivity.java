package com.example.qrcode;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private EditText etName, etAddress;
    private Button buttonScan;

    ActivityResultLauncher<Intent> scanLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        buttonScan = findViewById(R.id.buttonScan);

        scanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String scanResult = data.getStringExtra("SCAN_RESULT");
                            if (scanResult != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(scanResult);
                                    String title = jsonObject.getString("title");
                                    String website = jsonObject.getString("website");

                                    // Populate the EditTexts
                                    etName.setText(title);
                                    etAddress.setText(website);
                                } catch (Exception e) {
                                    etName.setText("Invalid QR Code");
                                    etAddress.setText("");
                                }
                            }
                        }
                    } else {
                        etName.setText("Scan cancelled");
                        etAddress.setText("");
                    }
                }
        );

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.journeyapps.barcodescanner.CaptureActivity.class);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                scanLauncher.launch(intent); // Launch the scanner activity
            }
        });



        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etAddress.getText().toString();
                if (!url.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            }
        });
    }
}