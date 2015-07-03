package com.yucan.sensoro.demo.db;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;

import javax.xml.transform.Templates;

/**
 * Created by LiuYucan on 15-7-1.
 */
public class DrawView extends View {
    int temp;
    int initialTemp;
    int finalTemp;
    int rotateDegree;
    int pointAX;
    int pointAY;
    int pointBX;
    int pointBY;
    int pointCX;
    int pointCY;
    int centerX;
    int centerY;
    final static int DEGREE = 140;
    int xPixel;
    int yPixel;
    int tempWidth;


    public DrawView(Context context, int a, int b) {
        super(context);
        xPixel = a;
        yPixel = b;
        tempWidth = a/40;
        temp = -10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setAntiAlias(true);

        RectF oval = new RectF(0, 0, xPixel/3, yPixel/3);
        centerX = (xPixel/3)/2;
        centerY = (yPixel/3)/2;
        canvas.drawArc(oval, 200, 140, true, p);
        RectF oval2 = new RectF(tempWidth, tempWidth+5, xPixel/3-tempWidth, yPixel/3-tempWidth+5);
        p.setColor(Color.WHITE);
        canvas.drawArc(oval2, 200, 140, true, p);
        p.setColor(Color.BLACK);
        canvas.drawCircle(centerX, centerY, tempWidth/2, p);

        initialTemp = -10;
        finalTemp = 50;
        calculatePoints();

        Path path = new Path();
        path.moveTo(pointAX, pointAY);
        path.lineTo(pointBX, pointBY);
        path.lineTo(pointCX, pointCY);
        path.close();
        canvas.drawPath(path, p);
    }

    void calculatePoints() {
        rotateDegree = (int)(((float)(temp-initialTemp)/(finalTemp-initialTemp))*DEGREE) + 20;
        pointAX = (int)(centerX+Math.cos(Math.toRadians(rotateDegree - 90))*tempWidth/2);//get the ordination of left bottom point
        pointAY = (int)(centerY+Math.sin(Math.toRadians(rotateDegree - 90))*tempWidth/2);
        pointBX = (int)(centerX+Math.cos(Math.toRadians(rotateDegree + 90))*tempWidth/2);//get the ordination of right bottom point
        pointBY = (int)(centerY+Math.sin(Math.toRadians(rotateDegree + 90))*tempWidth/2);
        pointCX = (int)(centerX-Math.cos(Math.toRadians(rotateDegree))*(xPixel/6-tempWidth/2));// get the ordination of top point
        pointCY = (int)(centerY-Math.sin(Math.toRadians(rotateDegree))*(xPixel/6-tempWidth/2));
    }

    public void setTemp(int input){
        temp = input;
        this.postInvalidate();
    }
}