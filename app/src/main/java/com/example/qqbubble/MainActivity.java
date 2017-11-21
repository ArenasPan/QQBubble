package com.example.qqbubble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DragLayout dragLayout;

    private int startX;
    private int startY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dragLayout = findViewById(R.id.drag_layout);
    }


    public void click(View view) {
        if (view.getId() == R.id.reset) {
            dragLayout.reset();
        }
    }
}
