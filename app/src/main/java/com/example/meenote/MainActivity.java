package com.example.meenote;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    boolean penMode = true;  // ペン: true, 消しゴム: false
    boolean markerMode = false;
    int color = 1;  // 1: BLACK, 2: RED
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decor = getWindow().getDecorView();
        // hide navigation bar, hide status bar. don't show navigation when tapped.
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        final CanvasView cv = (CanvasView) findViewById(R.id.canvas_view);

        //-- Button関連
        Button bt = (Button) findViewById(R.id.undo_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv.delete();
            }
        });

        bt = (Button) findViewById(R.id.change_color_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!penMode || markerMode) return;
                cv.changeColor();
                Button bt = (Button) findViewById(R.id.change_color_button);
                if (color == 1) bt.setBackgroundColor(Color.RED);
                else if (color == 2) bt.setBackgroundColor(Color.BLACK);
                color = 3 - color;
            }
        });

        bt = (Button) findViewById(R.id.change_pen_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markerMode) return;
                cv.changePen();
                Button bt = (Button) findViewById(R.id.change_pen_button);
                if (penMode) bt.setText("書く");
                else bt.setText("消す");
                penMode = !penMode;
            }
        });

        bt = (Button) findViewById(R.id.next_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markerMode) return;
                cv.nextPage();
                ++page;
                TextView tx = (TextView) findViewById(R.id.page_text);
                tx.setText(String.valueOf(page + 1));
            }
        });

        bt = (Button) findViewById(R.id.prev_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markerMode) return;
                cv.prevPage();
                if (page != 0) --page;
                TextView tx = (TextView) findViewById(R.id.page_text);
                tx.setText(String.valueOf(page + 1));
            }
        });

        bt = (Button) findViewById(R.id.shift_setting_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv.setShiftValues();
            }
        });

        bt = (Button) findViewById(R.id.marker_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerMode = !markerMode;
                cv.marker(markerMode);
                Button bt = (Button) findViewById(R.id.marker_button);
                if (markerMode) bt.setBackgroundColor(Color.GRAY);
                else bt.setBackgroundColor(Color.CYAN);
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decor = getWindow().getDecorView();
        // hide navigation bar, hide status bar. don't show navigation when tapped.
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}