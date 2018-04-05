package com.tudelft.tbd.map;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

public class MapUpdateThread extends Thread {
    private MapView mapView;
    MapUpdateThread(MapView mapView) {
        this.mapView = mapView;
    }

    @SuppressLint("WrongCall")
    @Override
    public void run() {
        Canvas c = null;
        try {
            c = mapView.getHolder().lockCanvas();
            synchronized (mapView.getHolder()) {
                if (c != null) {
                    mapView.onDraw(c);
                }
            }
        } finally {
            if (c != null) {
                mapView.getHolder().unlockCanvasAndPost(c);
            }
        }
    }
}
