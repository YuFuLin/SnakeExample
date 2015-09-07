package ccc.GameSnake;

import java.util.Random;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class AppleObj extends GameObj{
	private Random r=new Random();
	private Rect actRect;
	public AppleObj(Drawable drawable,Rect limitRect) {
		super(drawable);
		this.actRect=limitRect;
	}
	
	
	/**
	 * Set the random position for items
	 */
	public void random(Rect limitRect){
		this.actRect=limitRect;
		this.moveTo(actRect.left+r.nextInt(actRect.width()-this.getWidth()),actRect.top+r.nextInt(actRect.height()-this.getHeight()));	
	}
	
	/**
	 * Set the random position for items
	 */
	public void random(){
		this.random(this.actRect);
	}
}
