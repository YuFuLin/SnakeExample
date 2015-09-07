package ccc.GameSnake;

import android.graphics.Canvas;
import android.graphics.Paint;

public class GameStat {
	private Long startTime=0l;
	private Long overTime=0l;
	private Long pauseTime=0l;
	private boolean isTimePause=false;
	private int  gameScore;

	public GameStat(long gameOverTime){
		this.startTime=System.currentTimeMillis();
		this.overTime=gameOverTime;
	}
	
	public void updateScroe(int gameScore){
		this.gameScore=gameScore;
	}
	
	public void draw(Canvas canvas){
		Paint pt=new Paint();
		pt.setARGB(150, 255, 0, 0);
		pt.setTextSize(24);
		canvas.drawText("分數:"+this.gameScore, 20, 40, pt);
		canvas.drawText("剩餘時間:"+getCountdownTime(), 20, 80, pt);
	}
	

	public int getCountdownTime(){
		if(!isTimeOver()){
			if(isTimePause)
				return (int)((this.overTime-pauseTime)/1000)+1;
			else
				return (int)((this.overTime-System.currentTimeMillis())/1000)+1;
		}
		else{
			return 0;
		}
	}
	

	public void addTime(int addMicroseconds){
		overTime+=addMicroseconds;
	}
	public boolean isTimeOver(){
		if(isTimePause)
			return pauseTime>overTime;
		else
			return System.currentTimeMillis()>overTime;
	}
	

	public void timePause(){
		if(!isTimePause){
			pauseTime=System.currentTimeMillis();
		}
		isTimePause=true;
	}
	

	public boolean isTimePause(){
		return this.isTimePause;
	}
	

	public void timeResume(){
		if(isTimePause){
			overTime=System.currentTimeMillis()+overTime-pauseTime;
		}
		isTimePause=false;
	}

}
