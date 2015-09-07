package ccc.GameSnake;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.KeyEvent;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

public class GameSnake extends Activity {
	SurfaceView gameSurfaceView;
	SurfaceHolder surfaceHolder;
	Thread gameThread;
	Boolean isGameThreadStop = true;
	GameObj backimg;
	int gameFPS = 25;
	KeyHandler keyHandler = new KeyHandler();
	TouchPoint touchPoint = new TouchPoint();
	PowerManager.WakeLock wakeLock;
	drawAction nowDrawWork;
	SnakeObj snake;
	AppleObj apple;
	GameStat gameStat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ���ê��A�C
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ���õ������D
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// �������]�����V���P ��Ĳ�o�ù���V����
		setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);

		// �q���޲z�A�Ȩ��o
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"GameSnake PowerControl");

		gameSurfaceView = new SurfaceView(this);
		surfaceHolder = gameSurfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceDestroyed(SurfaceHolder arg0) {
			}

			public void surfaceCreated(SurfaceHolder arg0) {
				if (backimg == null) {
					// �Ĥ@��Activity���J��
					Resources rs = getResources();
					backimg = new GameObj(rs.getDrawable(R.drawable.backimg));
					SurfaceView sv = gameSurfaceView;
					backimg.setRect(new Rect(sv.getLeft(), sv.getTop(), sv
							.getRight(), sv.getBottom()));
					readyGame();
				} else {
					// �g��Activity��^���J��
					draw(nowDrawWork);
					openOptionsMenu();

				}
			}

			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
					int arg3) {

			}
		});
		setContentView(gameSurfaceView);
	}

	/**
	 * �q������ ����i�J��v���A����
	 */
	protected void powerControl(boolean needWake) {
		if (needWake && !wakeLock.isHeld()) {
			wakeLock.acquire();
		} else if (!needWake && wakeLock.isHeld()) {
			wakeLock.release();
		}

	}

	@Override
	protected void onPause() {
		pauseGame();
		super.onPause();
	};

	protected static final int MENU_Resume = Menu.FIRST;
	protected static final int MENU_Reset = Menu.FIRST + 1;
	protected static final int MENU_Quit = Menu.FIRST + 2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_Resume, 0, "�~��");
		menu.add(0, MENU_Reset, 0, "���s�}�l");
		menu.add(0, MENU_Quit, 0, "���}");
		return super.onCreateOptionsMenu(menu);
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_Resume:
			resumeGame();
			break;
		case MENU_Reset:
			readyGame();
			break;
		case MENU_Quit:
			gameExit();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		pauseGame();
		return super.onMenuOpened(featureId, menu);
	};

	void gameExit() {
		gameThreadStop();
		if (gameThread != null) {
			try {
				gameThread.join();
			} catch (InterruptedException e) {

			}
		}
		finish();// �����C��
	}

	@Override
	public boolean onTouchEvent(android.view.MotionEvent event) {
		if (nowDrawWork == drawAction.game)
			touchPoint.update(event);
		return true;
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		keyHandler.keyDown(keyCode);
		return super.onKeyDown(keyCode, event);

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		keyHandler.keyUp(keyCode);
		return super.onKeyUp(keyCode, event);
	}

	public void gameThreadStart() {
		isGameThreadStop = false;
		powerControl(true);
		if (gameThread == null) {
			gameThread = new Thread(gameRun);
			gameThread.start();
		} else if (!gameThread.isAlive()) {
			gameThread = new Thread(gameRun);
			gameThread.start();
		}
	}

	public void gameThreadStop() {
		isGameThreadStop = true;
		powerControl(false);
	}


	void readyGame() {
		gameThreadStop();
		nowDrawWork = drawAction.ready;
		Resources rs = getResources();
		snake = new SnakeObj(GameSnake.this, backimg.getRect());
		apple = new AppleObj(rs.getDrawable(R.drawable.apple), backimg
				.getRect());
		apple.random(backimg.getRect());
		gameStat = new GameStat(System.currentTimeMillis() + 3000);
		gameThreadStart();
	}


	void startGame() {
		gameStat = new GameStat(System.currentTimeMillis() + 30000);
		nowDrawWork = drawAction.game;
	}


	void pauseGame() {
		gameThreadStop();
		if (nowDrawWork != drawAction.over) {
			gameStat.timePause();
			draw(drawAction.pause);
		}

	}


	void resumeGame() {
		if (nowDrawWork != drawAction.over) {
			gameThreadStart();
			gameStat.timeResume();
		}
	}

	Runnable gameRun = new Runnable() {
		public void run() {
			long delayTime = 1000 / gameFPS;
			while (!isGameThreadStop) {
				long startTime = System.currentTimeMillis();
				if (nowDrawWork == drawAction.game)
					gameUpdate();
				draw(nowDrawWork);
				long endTime = System.currentTimeMillis();
				long waitTime = delayTime - (startTime - endTime);
				if (waitTime > 0) {
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	};

	boolean isKeyDown(int keyCode) {
		return keyHandler.isKeyDown(keyCode);
	}


	void gameUpdate() {
		boolean isChangeMove = false;

		if (touchPoint.isChangeVector) {
			snake.move(touchPoint.lastVectorX, touchPoint.lastVectorY);
			isChangeMove = true;
		} else {

			if (isKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT)) {
				snake.move(1, 0);
				isChangeMove = true;
			}
			if (isKeyDown(KeyEvent.KEYCODE_DPAD_LEFT)) {
				snake.move(-1, 0);
				isChangeMove = true;
			}
			if (isKeyDown(KeyEvent.KEYCODE_DPAD_UP)) {
				snake.move(0, -1);
				isChangeMove = true;
			}
			if (isKeyDown(KeyEvent.KEYCODE_DPAD_DOWN)) {
				snake.move(0, 1);
				isChangeMove = true;
			}
		}


		if (!isChangeMove)
			snake.move();


		snake.update();


		if (snake.isEatApple(apple)) {

			snake.add();

			gameStat.addTime(3000);


			while (snake.isEatApple(apple))
				apple.random(backimg.getRect());
		}

		gameStat.updateScroe(snake.getLength());


		if (gameStat.isTimeOver())
			nowDrawWork = drawAction.over;
	}


	enum drawAction {
		ready, game, pause, over
	}


	void draw(drawAction action) {
		Canvas canvas = null;
		try {
			canvas = surfaceHolder.lockCanvas(null);
			synchronized (surfaceHolder) {
				draw(action, canvas);
			}
		} finally {
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}


	void draw(drawAction action, Canvas canvas) {
		switch (action) {
		case ready:
			drawReady(canvas);
			break;
		case game:
			drawGame(canvas);
			break;
		case pause:
			drawPause(canvas);
			break;
		case over:
			drawOver(canvas);
			break;
		}
	}


	void drawReady(Canvas canvas) {
		clear(canvas);
		Paint pt = new Paint();
		pt.setTextAlign(Paint.Align.CENTER);
		pt.setARGB(255, 0, 0, 255);
		pt.setTextSize(30);
		canvas.drawText(gameStat.getCountdownTime() + "���C���}�l-", backimg
				.centerX(), backimg.centerY(), pt);
		if (gameStat.isTimeOver())
			startGame();
	}


	void drawGame(Canvas canvas) {
		clear(canvas);
		apple.draw(canvas);
		snake.draw(canvas);
		gameStat.draw(canvas);
		touchPoint.draw(canvas);
	}

	void drawPause(Canvas canvas) {
		draw(nowDrawWork, canvas);
		Paint pt = new Paint();
		pt.setARGB(30, 0, 0, 100);
		canvas.drawRect(backimg.getRect(), pt);
		pt.setTextAlign(Paint.Align.CENTER);
		pt.setARGB(150, 200, 200, 200);
		pt.setTextSize(50);
		canvas.drawText("-�C���Ȱ�-", backimg.centerX(), backimg.centerY(), pt);
	}

	void drawOver(Canvas canvas) {

		gameThreadStop();
		drawGame(canvas);
		Paint pt = new Paint();
		pt.setARGB(30, 30, 30, 30);
		canvas.drawRect(backimg.getRect(), pt);
		pt.setTextAlign(Paint.Align.CENTER);
		pt.setARGB(100, 0, 0, 255);
		pt.setTextSize(50);
		canvas.drawText("-�C������-", backimg.centerX(), backimg.centerY(), pt);
	}

	void clear(Canvas canvas) {
		Paint p = new Paint();
		p.setARGB(100, 0, 0, 0);
		backimg.draw(canvas);
	}

}