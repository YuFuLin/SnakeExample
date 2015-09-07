package ccc.GameSnake;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * @author Administrator
 * 
 */
public class SnakeObj {

	private class exGameObj extends GameObj {


		public PointF nextMove = new PointF();


		public PointF[] logPath = new PointF[3];

		public exGameObj(Drawable drawable) {
			super(drawable);
			init();
		}

		public exGameObj(exGameObj gameObj, Drawable drawable) {
			super(gameObj, drawable);
			init();
		}


		private void init() {
			for (int i = 0; i < logPath.length; i++) {
				logPath[i] = new PointF();
			}
		}


		public void updataMove() {


			float dx = nextMove.x - logPath[0].x;
			float dy = nextMove.y - logPath[0].y;

			for (int i = logPath.length - 1; i > 0; i--) {
				logPath[i].set(logPath[i - 1].x, logPath[i - 1].y);
			}
			logPath[0].set(nextMove.x, nextMove.y);


			this.moveTo(nextMove.x - this.getWidth() / 2, nextMove.y
					- this.getHeight() / 2);

			if (dx * dx + dy * dy > 4 * 4) {
				this.angle = (float) (Math.atan2(dy, dx) * 180 / Math.PI);
			}
		}
	}


	private exGameObj head;


	private List<exGameObj> bodys = new ArrayList<exGameObj>();


	private exGameObj tail;


	private Resources rs;


	private Rect actRect;


	private float dstVectorX = 1;

	private float dstVectorY = 0;

	public SnakeObj(Activity content, Rect actRect) {
		this.actRect = actRect;
		rs = content.getResources();

		Drawable d_head = rs.getDrawable(R.drawable.head);

		Drawable d_body = rs.getDrawable(R.drawable.body);

		Drawable d_tail = rs.getDrawable(R.drawable.tail);


		head = new exGameObj(d_head);
		exGameObj body = new exGameObj(d_body);
		tail = new exGameObj(d_tail);
		init(head);
		init(body);
		init(tail);
		bodys.add(body);
	}


	private void init(exGameObj obj) {
		float x = actRect.centerX();
		float y = actRect.centerY();
		obj.nextMove.set(x, y);
		for (int i = 0; i < obj.logPath.length; i++) {
			obj.logPath[i].set(x, y);
		}
	}


	public int getLength() {
		return bodys.size() - 1;
	}

	public void draw(Canvas canvas) {
		tail.draw(canvas);
		for (int i = 0; i < bodys.size(); i++) {
			bodys.get(bodys.size() - 1 - i).draw(canvas);
		}
		head.draw(canvas);
	}


	public void update() {
		

		updataMove(bodys.get(bodys.size() - 1), tail);
		

		for (int i = bodys.size() - 1; i >= 0; i--) {
			exGameObj moveObj = bodys.get(i);
			if (i == 0) {
				updataMove(head, moveObj);
			} else {
				updataMove(bodys.get(i - 1), moveObj);
			}
		}
		

		head.updataMove();


		for (int i = 1; i < bodys.size(); i++) {
			Rect h = new Rect(head.getRect());
			Rect b = new Rect(bodys.get(i).getRect());
			

			scaleRect(h, -5, -5);
			scaleRect(b, -10, -10);
			
			if (Rect.intersects(h, b)) {
				this.cut();
				break;
			}
		}

	}


	private void updataMove(exGameObj fd, exGameObj bk) {
		bk.updataMove();
		PointF fwp = fd.logPath[fd.logPath.length - 1];
		bk.nextMove.set(fwp.x, fwp.y);
	}
	
	
	
	private void scaleRect(Rect rect, int scaleX, int scaleY) {
		rect.set(rect.left - scaleX, rect.top - scaleY, rect.right + scaleX,
				rect.bottom + scaleY);
	}


	private void scaleRect(RectF rect, int scaleX, int scaleY) {
		rect.set(rect.left - scaleX, rect.top - scaleY, rect.right + scaleX,
				rect.bottom + scaleY);
	}


	private float getAngle(float angle, float addAngle) {
		angle += addAngle;
		angle %= 360;
		if (angle > 180)
			angle -= 360;
		if (angle < -180)
			angle += 360;
		return angle;
	}

	

	public void move(float dx, float dy) {
		this.dstVectorX = dx;
		this.dstVectorY = dy;

		float rotateAngle = getAngleByXY(dx, dy);

		float limitAngle = 25;
	
		float limitLeftAngle = getAngle(head.angle, limitAngle);

		float limitRightAngle = getAngle(head.angle, -limitAngle);


		if (Math.abs(getAngle(rotateAngle, -head.angle)) > limitAngle) {			
			if (getAngle(rotateAngle, -head.angle) > 0) {

				rotateAngle = limitLeftAngle;
			} else {

				rotateAngle = limitRightAngle;
			}
		}
		
	
		head.angle = rotateAngle;
		

		double dreg = head.angle * Math.PI / 180;
		int moveDistance=6;
		dx = (float) Math.cos(dreg) * moveDistance;
		dy = (float) Math.sin(dreg) * moveDistance;
		float ndx = head.logPath[0].x + dx;
		float ndy = head.logPath[0].y + dy;
		

		RectF limitRect = new RectF(actRect);
		

		scaleRect(limitRect, -head.getWidth() / 2, -head.getHeight() / 2);
		

		if (!limitRect.contains(ndx, ndy)) {
			boolean isTouchEdge = false;
			if (ndx < limitRect.left) {
				if (head.angle < 0)
					head.angle = limitLeftAngle;
				else
					head.angle = limitRightAngle;
				ndx = limitRect.left;
				isTouchEdge = true;
			}
			if (ndx > limitRect.right) {
				if (head.angle > 0)
					head.angle = limitLeftAngle;
				else
					head.angle = limitRightAngle;
				ndx = limitRect.right;
				isTouchEdge = true;
			}

			if (ndy < limitRect.top) {
				if (head.angle > -90)
					head.angle = limitLeftAngle;
				else
					head.angle = limitRightAngle;
				ndy = limitRect.top;
				isTouchEdge = true;
			}
			if (ndy > limitRect.bottom) {
				if (head.angle > 90)
					head.angle = limitLeftAngle;
				else
					head.angle = limitRightAngle;
				ndy = limitRect.bottom;
				isTouchEdge = true;
			}
			
			if (isTouchEdge) {

				this.cut();
				

				this.dstVectorX = (float) Math.cos(head.angle * Math.PI / 180);
				this.dstVectorY = (float) Math.sin(head.angle * Math.PI / 180);
			}
		}

		head.nextMove.set(ndx, ndy);

	}


	public void move() {
		move(this.dstVectorX, this.dstVectorY);
	}

	


	private float getAngleByXY(float dx, float dy) {
		return (float) (Math.atan2(dy, dx) * 180 / Math.PI);
	}


	public void add() {
		exGameObj newBody = new exGameObj(tail, rs.getDrawable(R.drawable.body));
		newBody.nextMove.set(tail.nextMove.x, tail.nextMove.y);

		for (int i = 0; i < tail.logPath.length; i++) {
			newBody.logPath[i].set(tail.logPath[i].x, tail.logPath[i].y);
			tail.logPath[i].set(tail.nextMove.x, tail.nextMove.y);
		}
		bodys.add(newBody);

	}


	public void cut(int bodyIndex) {
		if (bodyIndex>0&&bodyIndex < bodys.size()) {
			exGameObj lastBody = bodys.get(bodyIndex-1);
			tail.setRect(lastBody.getRect());
			tail.nextMove.set(lastBody.nextMove.x, lastBody.nextMove.y);
			for (int i = 0; i < tail.logPath.length; i++) {
				tail.logPath[i].set(lastBody.logPath[i].x,
						lastBody.logPath[i].y);
			}
			
			for (int i = bodyIndex; i < bodys.size(); i++) {
					bodys.remove(bodys.size() - 1);
			}
			
		

		}

	}


	public void cut() {
		cut(bodys.size() - 1);
	}


	public boolean isEatApple(GameObj apple) {
		return Rect.intersects(apple.getRect(), head.getRect());
	}

}
