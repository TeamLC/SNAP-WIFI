package com.Dev.unknown.snapwifi;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CustomDialog extends AppCompatActivity {

    private String text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        text = intent.getExtras().getString("password");
        callFunction();
    }

    public String callFunction() {
        final Dialog dig = new Dialog(CustomDialog.this);
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dig.setContentView(R.layout.activity_custom_dialog);
        WindowManager.LayoutParams params = dig.getWindow().getAttributes();
        params.width = 830;
        params.height = 730;
        dig.show();

        Window window = dig.getWindow();
        window.setAttributes(params);

        final EditText et = (EditText)dig.findViewById(R.id.et);
        final Button PositiveButton = (Button)dig.findViewById(R.id.ok_btn);
        final Button NegativeButton = (Button)dig.findViewById(R.id.cancel_btn);

        et.setText(text);

        PositiveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = et.getText().toString();
                Intent intent = new Intent(CustomDialog.this, Load.class);
                intent.putExtra("pass2", text);
                startActivity(intent);
            }
        });

        NegativeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dig.dismiss();
            }
        });

        return text;
    }
}