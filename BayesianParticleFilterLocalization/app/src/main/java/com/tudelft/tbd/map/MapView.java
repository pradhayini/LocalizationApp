package com.tudelft.tbd.map;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tudelft.tbd.viewmodels.BaseViewModel;


/**
 * Reference: http://www.edu4java.com/en/androidgame/androidgame3.html
 */
public class MapView extends SurfaceView {
    private static final int mapHeight = 2610;
    private static final int mapWidth = 1450;
    private MapUpdateThread mapUpdateThread;

    private boolean initialized = false;

    private BaseViewModel viewModel;

    public MapView(Context context) {
        super(context);

        initialize();
    }

    private void initialize() {
        if(!initialized) {
            mapUpdateThread = new MapUpdateThread(this);

            SurfaceHolder holder = getHolder();
            holder.addCallback(new SurfaceHolder.Callback() {

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    boolean retry = true;

                    while (retry) {
                        try {
                            mapUpdateThread.join();
                            retry = false;
                        } catch (InterruptedException e) {
                            // Nothing to do!
                        }
                    }
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mapUpdateThread.start();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

            });
            initialized = true;
        }
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(canvas != null){
            final float scaleFactor = Math.min( getWidth() / (float)mapWidth, getHeight() / (float)mapHeight );
            if(Float.compare(scaleFactor, 0) > 0){
                canvas.scale(scaleFactor, scaleFactor);
            }
            if(viewModel != null){
                viewModel.createMap(canvas);
            }
        }
    }

    public void setViewModel(BaseViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void startDrawing(){
        mapUpdateThread.run();
    }
}
