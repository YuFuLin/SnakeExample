package ccc.GameSnake;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class TouchPoint {
	
	//The x axis is detected finally
	public float lastVectorX;
	//The y axis is detected finally
	public float lastVectorY;
	
	//Check change of vector
	public boolean isChangeVector=false;
	
	//觸控點座標
	List<PointF> points = new ArrayList<PointF>();
	
	public TouchPoint(){
	}
	
	//Update the touch point
	public void update(android.view.MotionEvent event){
		for (int i = 0; i < event.getHistorySize(); i+=3) {
			points.add(new PointF(event.getHistoricalX(i), event
					.getHistoricalY(i)));
		}
		
		changeVector();
	}
	
	//Change the touch point
	private void changeVector(){
		if (points.size() > 1) {//感測2點以上
			this.lastVectorX = points.get(points.size() - 1).x
					- points.get(0).x;
			this.lastVectorY = points.get(points.size() - 1).y
					- points.get(0).y;
		}
		isChangeVector=true;
	}
	
	//Draw on the vector
	public void draw(Canvas canvas){
		if (points.size() > 1) {//感測2點以上
			Paint p = new Paint();
			p.setARGB(255, 0, 0, 0);
			p.setStrokeWidth(3);
			for (int i = 0; i < points.size() - 1; i++) {
				float x1=points.get(i).x;
				float y1=points.get(i).y;
				float x2=points.get(i+1).x;
				float y2=points.get(i+1).y;
				canvas.drawLine(x1,y1,x2,y2, p);
			}
		}
		this.resetPoint();
	}
	
	//Reset the point
	public void resetPoint(){
		isChangeVector=false;
	    points.clear();
	}

}
