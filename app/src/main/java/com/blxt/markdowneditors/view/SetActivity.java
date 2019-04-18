package com.blxt.markdowneditors.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blxt.markdowneditors.R;

public class SetActivity extends AppCompatActivity {

    public static void startSetActivity(Context context) {
        Intent intent = new Intent(context, SetActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
    }
}
