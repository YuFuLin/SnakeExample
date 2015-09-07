package ccc.GameSnake;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class GameObj {
	/**
	 * The angle of items
	 */
	public float angle;
	
	/**
	 * The resource of objext
	 */
	public Drawable drawable;
	
	/**
	 * Is visible
	 */
	public boolean Visible = true;
	
	/**
	 * Control Enable
	 */
	public boolean Enable = true;
	
	/**
	 * Item position temp
	 */
	private Rect saveRect;
	
	/**
	 * Item angle temp
	 */
	public float saveAngle;

	public GameObj(Drawable drawable) {
		this.drawable = drawable;
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
				.getIntrinsicHeight());
		this.save();
	}
	public GameObj(GameObj gameObj,Drawable drawable) {
		this.drawable = drawable;
		this.drawable.setBounds(gameObj.drawable.copyBounds());
		this.angle=gameObj.angle;
		this.save();
	}
	
	/**
	 * The item state
	 */
	public void save() {
		if (Enable) {
			saveRect = drawable.copyBounds();
			saveAngle = angle;
		}
	}

	/**
	 * Restore the item state
	 */
	public void restore() {
		if (Enable) {
			drawable.setBounds(saveRect);
			angle = saveAngle;
		}
	}

	
	
	/**
	 * Rotate the item
	 */
	public void rotate(float angle) {
		if (Enable) {
			this.angle += angle;
			this.angle %= 360;
		}
	}

	

	public void setAngle(float angle) {
		if (Enable) {
			this.angle = angle;
			this.angle %= 360;
		}
	}


	public float getAngle(float angle) {
		return angle;
	}


	public void moveTo(int newLeft, int newTop) {
		if (Enable) {
			Rect rect = drawable.getBounds();
			drawable.setBounds(newLeft, newTop, newLeft + rect.width(), newTop
					+ rect.height());
		}
	}
	

	public void moveTo(float newLeft, float newTop) {
		moveTo((int)newLeft,(int)newTop);
	}


	public void move(int dx, int dy) {
		if (Enable) {
			Rect rect = drawable.getBounds();
			drawable.setBounds(rect.left + dx, rect.top + dy, rect.right + dx,
					rect.bottom + dy);
		}
	}
	

	public void move(float dx, float dy) {
		move((int)dx,(int)dy);
	}
	

	public void scale(int addScaleX, int addScaleY) {
		if (Enable) {
			Rect rect = drawable.getBounds();
			drawable.setBounds(rect.left - addScaleX, rect.top - addScaleY,
					rect.right + addScaleX, rect.bottom + addScaleY);
		}
	}

	public void draw(Canvas canvas) {
		if (Visible) {
			canvas.save();
			canvas.rotate(angle, drawable.getBounds().centerX(), drawable
					.getBounds().centerY());
			drawable.draw(canvas);
			canvas.restore();
		}
	}


	public int centerX() {
		return drawable.getBounds().centerX();
	}


	public int centerY() {
		return drawable.getBounds().centerY();
	}


	public Rect getRect() {
		return drawable.getBounds();
	}


	public int getHeight() {
		return drawable.getBounds().height();
	}

	
	public int getWidth() {
		return drawable.getBounds().width();
	}


	public int getSrcHeight() {
		return drawable.getIntrinsicHeight();
	}


	public int getSrcWidth() {
		return drawable.getIntrinsicWidth();
	}


	public void setRect(Rect rect) {
		drawable.setBounds(rect);
	}


	public void setRect(int left, int top, int right, int bottom) {
		drawable.setBounds(left, top, right, bottom);

	}


	public boolean intersect(Rect r) {
		return drawable.getBounds().intersect(r);
	}
	

	public boolean intersect(GameObj obj) {
		return this.intersect(obj.getRect());
	}

	public boolean contains(Rect r) {
		return drawable.getBounds().contains(r);
	}
	

	public boolean contains(GameObj obj) {
		return this.contains(obj.getRect());
	}
	
	
	public boolean contains(int x,int y) {
		return drawable.getBounds().contains(x, y);
	}
}