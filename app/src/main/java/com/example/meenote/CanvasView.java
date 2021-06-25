package com.example.meenote;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorLong;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CanvasView extends View {

    private Context mContext;

    public class PathInfo {
        public Path path;
        public int color;

        public PathInfo(Path path, int color) {
            this.path = path;
            this.color = color;
        }
    }

    private ArrayList<ArrayList<PathInfo>> pathList;  // 直線リスト
    private Paint paint;
    private Paint prevPaint;
    private Paint paint0;  // 消しゴム
    private Paint paint1;
    private Paint paint2;
    private Paint paint3;  // マーカー
    private ArrayList<Integer> colorList;  // 色リスト

    private float x, y;  // タッチ座標
    private boolean eraseMode = false;

    private float startX, startY;  // パスのスタート座標
    private int page = 0;  // ページ番号


    //======================================================================================
    //--  コンストラクタ
    //======================================================================================
    public CanvasView(Context context) {
        super(context);
        mContext = context;

        //-- 初期化
        //- Path関連
        pathList = new ArrayList();   // リストの作成
        pathList.add(new ArrayList());   // 1 ページ目の作成

        //- Paint関連
        colorList = new ArrayList();
        colorList.add(new Integer(Color.WHITE));
        colorList.add(new Integer(Color.BLACK));
        colorList.add(new Integer(Color.RED));
        colorList.add(new Integer(Color.argb(99, 0, 100, 220)));   // BLUE

        paint0 = new Paint();
        paint0.setColor(colorList.get(0).intValue());   // 色の指定
        paint0.setStyle(Paint.Style.STROKE); // 描画設定を'線'に設定
        paint0.setAntiAlias(true);           // アンチエイリアスの適応
        paint0.setStrokeWidth(100);            // 線の太さ

        paint1 = new Paint();
        paint1.setColor(colorList.get(1).intValue());   // 色の指定
        paint1.setStyle(Paint.Style.STROKE); // 描画設定を'線'に設定
        paint1.setAntiAlias(true);           // アンチエイリアスの適応
        paint1.setStrokeWidth(2);            // 線の太さ

        paint2 = new Paint();
        paint2.setColor(colorList.get(2).intValue());   // 色の指定
        paint2.setStyle(Paint.Style.STROKE); // 描画設定を'線'に設定
        paint2.setAntiAlias(true);           // アンチエイリアスの適応
        paint2.setStrokeWidth(2);            // 線の太さ

        paint3 = new Paint();
        paint3.setColor(colorList.get(3).intValue());   // 色の指定
        paint3.setStyle(Paint.Style.STROKE); // 描画設定を'線'に設定
        paint3.setAntiAlias(true);           // アンチエイリアスの適応
        paint3.setStrokeWidth(5);            // 線の太さ

        paint = paint1;
        prevPaint = null;
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        //-- 初期化
        //- Path関連
        pathList = new ArrayList();   // リストの作成
        pathList.add(new ArrayList());   // 1 ページ目の作成

        //- Paint関連
        colorList = new ArrayList();
        colorList.add(new Integer(Color.WHITE));
        colorList.add(new Integer(Color.BLACK));
        colorList.add(new Integer(Color.RED));
        colorList.add(new Integer(Color.argb(99, 0, 100, 220)));   // BLUE

        paint0 = new Paint();
        paint0.setColor(colorList.get(0).intValue());   // 色の指定
        paint0.setStyle(Paint.Style.STROKE); // 描画設定を'線'に設定
        paint0.setAntiAlias(true);           // アンチエイリアスの適応
        paint0.setStrokeWidth(100);            // 線の太さ

        paint1 = new Paint();
        paint1.setColor(colorList.get(1).intValue());   // 色の指定
        paint1.setStyle(Paint.Style.STROKE); // 描画設定を'線'に設定
        paint1.setAntiAlias(true);           // アンチエイリアスの適応
        paint1.setStrokeWidth(2);            // 線の太さ

        paint2 = new Paint();
        paint2.setColor(colorList.get(2).intValue());   // 色の指定
        paint2.setStyle(Paint.Style.STROKE); // 描画設定を'線'に設定
        paint2.setAntiAlias(true);           // アンチエイリアスの適応
        paint2.setStrokeWidth(2);            // 線の太さ

        paint3 = new Paint();
        paint3.setColor(colorList.get(3).intValue());   // 色の指定
        paint3.setStyle(Paint.Style.STROKE); // 描画設定を'線'に設定
        paint3.setAntiAlias(true);           // アンチエイリアスの適応
        paint3.setStrokeWidth(5);            // 線の太さ

        paint = paint1;
        prevPaint = null;
    }

    //======================================================================================
    //--  描画メソッド
    //======================================================================================
    @Override
    protected void onDraw(Canvas canvas) {

        for ( PathInfo pathElem : pathList.get(page) ) {
            if (pathElem.color == colorList.get(1).intValue()) canvas.drawPath(pathElem.path, paint1);   // Pathの描画
            else if (pathElem.color == colorList.get(2).intValue()) canvas.drawPath(pathElem.path, paint2);
            else if (pathElem.color == colorList.get(0).intValue()) canvas.drawPath(pathElem.path, paint0);
            else if (pathElem.color == colorList.get(3).intValue()) canvas.drawPath(pathElem.path, paint3);
        }

        if (erasing) {
            //- 消しゴム有効範囲円の描画
            Paint paintCircle = new Paint();
            paintCircle.setColor(Color.CYAN);   // 色の指定
            paintCircle.setStyle(Paint.Style.STROKE); // 描画設定を'線'に設定
            paintCircle.setAntiAlias(true);           // アンチエイリアスの適応
            paintCircle.setStrokeWidth(4);            // 線の太さ
            canvas.drawCircle(x, y, 50, paintCircle);
        }

        invalidate();   // 再描画
    }

    //======================================================================================
    //--  タッチイベント
    //=====================================================================================
    private Path drawingPath;
    private float shiftX = -4;
    private float shiftY = 0;
    private float[] shiftXs = new float[]{-6, -8, -10, -4};
    private float[] shiftYs = new float[]{0, 0, 0, 0};
    private int idx = 0;

    public void setShiftValues() {
        ++idx;
        if (idx == shiftXs.length) idx = 0;
        shiftX = shiftXs[idx];
        shiftY = shiftYs[idx];
    }

    public boolean erasing = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x = event.getX();
        y = event.getY();

        if (Math.sqrt( Math.pow(x - startX, 2) + Math.pow(y - startY, 2) ) > 5) {
            startX = -100;
            startY = -100;
        }

        switch ( event.getAction() ) {
            case MotionEvent.ACTION_DOWN:                             //- 画面をタッチしたとき
                drawingPath = new Path();                         // 新たなPathのインスタンスの作成
                drawingPath.moveTo(event.getX() + shiftX, event.getY() + shiftY);   // 始点を設定
                pathList.get(page).add(new PathInfo(drawingPath, paint.getColor()));   // リストにPathを追加
                if (paint == paint0) erasing = true;
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:                               //- 画面から指を離したとき
            case MotionEvent.ACTION_CANCEL:                           //- 異常が起こったとき
                drawingPath.moveTo(event.getX() + shiftX, event.getY() + shiftY);   // 移動先の追加
                if (Math.sqrt( Math.pow(x - startX, 2) + Math.pow(y - startY, 2) ) < 5) {
                    delete();  // パスの削除
                    //- 点の描画
                    Path pathDot = new Path();
                    pathDot.addCircle(startX + shiftX, startY + shiftY, 1, Path.Direction.CW);
                    pathList.get(page).add(new PathInfo(pathDot, paint.getColor()));
                }
                erasing = false;
                break;
            case MotionEvent.ACTION_MOVE:                             //- タッチしながら指をスライドさせたとき
                drawingPath.lineTo(event.getX() + shiftX, event.getY() + shiftY);   // 移動先の追加
                break;
        }

        return true;   /* 返却値は必ず "true" にすること!! */
    }

    //======================================================================================
    //--  削除メソッド
    //======================================================================================
    public void delete() {
        if (pathList.get(page).size() == 0) return;
        pathList.get(page).remove(pathList.get(page).size() - 1);   // 末尾を削除
    }

    public void deleteAll() {
        if (pathList.get(page).size() == 0) return;
        pathList.get(page).clear();   // ページのパスをすべて削除
    }

    //======================================================================================
    //--  色変更メソッド
    //======================================================================================
    public void changeColor() {
        if (paint == paint1) paint = paint2;
        else if (paint == paint2) paint = paint1;
        prevPaint = paint;
    }

    //======================================================================================
    //--  ペンの種類変更メソッド
    //======================================================================================
    public void changePen() {
        if (eraseMode) {
            if (prevPaint != null) paint = prevPaint;
            else paint = paint1;
        }
        else {
            prevPaint = paint;
            paint = paint0;
        }
        eraseMode = !eraseMode;
    }

    //======================================================================================
    //--  マーカー変更メソッド
    //======================================================================================
    public void marker(boolean markerMode) {
        if (markerMode) {
            paint = paint3;
        }
        else {
            while (!pathList.get(page).isEmpty() && pathList.get(page).get(pathList.get(page).size() - 1).color == colorList.get(3).intValue()) delete();
            if (eraseMode) paint = paint0;
            else {
                if (prevPaint != null) paint = prevPaint;
                else paint = paint1;
            }
        }
    }

    //======================================================================================
    //--  ページ遷移メソッド
    //======================================================================================
    public void nextPage() {
        ++page;
        if (pathList.size() <= page) {
            pathList.add(new ArrayList());   // 次ページの作成
        }
    }

    public void prevPage() {
        if (page == 0) return;
        --page;
    }

}