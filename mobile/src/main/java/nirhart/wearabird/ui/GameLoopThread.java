package nirhart.wearabird.ui;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created on 02/04/2016.
 */
public class GameLoopThread extends Thread {

	public static final int STATE_PAUSE = 1;
	public static final int STATE_RUNNING = 2;
	// desired fps
	private final static int MAX_FPS = 60;
	// maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;
	private final SurfaceHolder surfaceHolder;
	private final Object runningLock = new Object();
	private int mode;
	private GameView gameView;
	private boolean gameDrawablesInit = false;
	private boolean running;

	public GameLoopThread(SurfaceHolder surfaceHolder, GameView gameView) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gameView = gameView;
	}

	public void setRunning(boolean running) {
		synchronized (runningLock) {
			this.running = running;
		}
	}

	@Override
	public void run() {
		Canvas canvas;
		long beginTime;
		long timeDiff;
		int sleepTime;
		int framesSkipped;

		while (running) {
			canvas = null;
			// try locking the canvas for exclusive pixel editing
			// in the surface
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;    // resetting the frames skipped
					if (!gameDrawablesInit) {
						gameDrawablesInit = true;
						this.gameView.initDrawables(canvas.getWidth(), canvas.getHeight());
					}
					// update game state
					if (mode == STATE_RUNNING) {
						this.gameView.update();
					}
					// render state to the screen
					// draws the canvas on the panel
					synchronized (runningLock) {
						if (running) {
							this.gameView.render(canvas);
						}
					}

					if (mode == STATE_RUNNING) {
						// calculate how long did the cycle take
						timeDiff = System.currentTimeMillis() - beginTime;
						// calculate sleep time
						sleepTime = (int) (FRAME_PERIOD - timeDiff);

						if (sleepTime > 0) {
							// if sleepTime > 0 we're OK
							try {
								// send the thread to sleep for a short period
								// very useful for battery saving
								Thread.sleep(sleepTime);
							} catch (InterruptedException ignore) {
							}
						}

						while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
							// we need to catch up
							this.gameView.update();
							sleepTime += FRAME_PERIOD;
							framesSkipped++;
						}
					}
				}
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	public void pause() {
		setState(STATE_PAUSE);
	}

	public void unPause() {
		setState(STATE_RUNNING);
	}

	public void setState(int mode) {
		this.mode = mode;
	}

	public void init() {
		gameDrawablesInit = false;
	}
}