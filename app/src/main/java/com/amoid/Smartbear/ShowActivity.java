package com.amoid.smartbear;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ShowActivity extends AppCompatActivity {
    private SurfaceHolder holder;
    private Paint paint;
    final int HEIGHT = 1080;
    final int WIDTH = 1920;
    final int X_OFFSET = 5;
    private int cx = X_OFFSET;
    // 实际的Y轴的位置
    int centerY = HEIGHT / 2;
    Timer timer = new Timer();
    TimerTask task = null;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_show);
        final SurfaceView surface = (SurfaceView)
                findViewById(R.id.show);
        // 初始化SurfaceHolder对象
        holder = surface.getHolder();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(100);

        //WindowManager windowsManager = this.getWindowManager();
        //DisplayMetrics displayMetrics = new DisplayMetrics();

        //windowsManager.getDefaultDisplay().getMetrics(displayMetrics);

        //HEIGHT = displayMetrics.widthPixels;
        //WIDTH = displayMetrics.heightPixels;

        Toast.makeText(getApplicationContext(), " H is " + HEIGHT + " W is " + WIDTH, Toast.LENGTH_SHORT).show();

        Button sin = (Button)findViewById(R.id.sin);
        Button cos = (Button)findViewById(R.id.cos);
        Button exitBtn = (Button)findViewById(R.id.exit);

        OnClickListener listener = (new OnClickListener()
        {
            @Override
            public void onClick(final View source)
            {
                drawBack(holder);
                cx = X_OFFSET;
                if(task != null)
                {
                    task.cancel();
                }
                task = new TimerTask()
                {
                    public void run()
                    {
                        if (source.getId() == R.id.exit) {
                            ActivityCollector.finishAll();
                        }
                        int cy = source.getId() == R.id.sin ? centerY
                                - (int)(100 * Math.sin((cx - 5) * 2
                                * Math.PI / 150))
                                : centerY - (int)(100 * Math.cos ((cx - 5)
                                * 2 * Math.PI / 150));
                        Canvas canvas = holder.lockCanvas(new Rect(cx ,
                                cy - 2  , cx + 2, cy + 2));
                        canvas.drawPoint(cx , cy , paint);
                        cx ++;
                        if (cx > WIDTH)
                        {
                            task.cancel();
                            task = null;
                        }
                        holder.unlockCanvasAndPost(canvas);
                    }
                };
                timer.schedule(task , 0 , 30);
            }
        });

        sin.setOnClickListener(listener);
        cos.setOnClickListener(listener);
        exitBtn.setOnClickListener(listener);

        holder.addCallback(new Callback()
        {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height)
            {
                drawBack(holder);
            }
            @Override
            public void surfaceCreated(final SurfaceHolder myHolder){ }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                timer.cancel();
            }
        });
    }

    private void drawBack(SurfaceHolder holder)
    {
        Canvas canvas = holder.lockCanvas();
        // 绘制白色背景
        canvas.drawColor(Color.WHITE);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(2);
        // 绘制坐标轴
        canvas.drawLine(X_OFFSET , centerY , WIDTH , centerY , p);
        canvas.drawLine(X_OFFSET , 40 , X_OFFSET , HEIGHT , p);
        holder.unlockCanvasAndPost(canvas);
        holder.lockCanvas(new Rect(0 , 0 , 0 , 0));
        holder.unlockCanvasAndPost(canvas);
    }
}
