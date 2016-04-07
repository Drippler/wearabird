package nirhart.wearabird.ui.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

import nirhart.wearabird.R;
import nirhart.wearabird.ui.model.components.Speed;

public class Coins {

	private static final int MAX_COINS_ROWS = 5;
	private static final int MAX_COINS_COLS = 8;
	private static final int COINS_ROTATION_SPEED = 3; // The less in here the faster the coins rotating

	private Bitmap[] bitmaps;
	private List<Coin> coinsPool;
	private Speed speed;
	private int coinWidth, coinHeight, width, height;
	private int tick;
	private int coinsInitTick;
	private int coinsRotationTick;
	private Paint coinsCollectedPaint;
	private float coinsCollectedLocation;
	private String coinsPrefix;
	private int coinsCollected;
	private Bitmap currentBitmap;
	private Player player;
	private Rect playerRect;

	public Coins(Context context, int[] resources, Speed speed, int width, int height, Player player) {
		this.coinsPool = new ArrayList<>();
		this.speed = speed;
		this.width = width;
		this.height = height;
		this.player = player;
		this.playerRect = new Rect();
		this.coinsCollectedPaint = new Paint();
		this.coinsCollectedPaint.setColor(Color.WHITE);
		this.coinsCollectedPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, context.getResources().getDisplayMetrics()));
		this.coinsCollectedPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		this.coinsCollectedLocation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 35, context.getResources().getDisplayMetrics());
		this.coinsPrefix = context.getString(R.string.coins_prefix);
		initBitmaps(context, resources, height);
	}

	private void initBitmaps(Context context, int[] resources, int height) {
		int length = resources.length;
		this.bitmaps = new Bitmap[length];
		Resources res = context.getResources();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		for (int i = 0; i < length; i++) {
			Bitmap cloudDecoded = BitmapFactory.decodeResource(res, resources[i], options);
			float scale = (float) options.outHeight / ((float) height / 12f);

			coinWidth = (int) (options.outWidth / scale);
			coinHeight = (int) (options.outHeight / scale);

			bitmaps[i] = Bitmap.createScaledBitmap(cloudDecoded, coinWidth, coinHeight, true);
			coinsInitTick = (int) (coinWidth * 1.2f * MAX_COINS_COLS);
			tick = coinsInitTick;
		}
	}

	public void draw(Canvas canvas) {
		for (int i = 0; i < coinsPool.size(); i++) {
			coinsPool.get(i).draw(canvas);
		}

		canvas.drawText(coinsPrefix + coinsCollected, coinsCollectedLocation, coinsCollectedLocation, coinsCollectedPaint);
	}

	private void createNewCoinsSet() {
		int coinsRows;
		int coinsCols;

		coinsRows = 1 + (int) (Math.random() * (MAX_COINS_ROWS - 1));
		coinsCols = 1 + (int) (Math.random() * (MAX_COINS_COLS - 1));

		float yOffset = (float) (0.5 * Math.random() * height);
		for (int i = 0; i < coinsCols; i++) {
			for (int j = 0; j < coinsRows; j++) {
				Coin coin = getCoin();
				coin.x = width + i * coinWidth;
				coin.y = yOffset + j * coinHeight;
				coin.setDirty(false);
			}
		}
	}

	private Coin getCoin() {
		for (int i = 0; i < coinsPool.size(); i++) {
			if (coinsPool.get(i).isDirty()) {
				return coinsPool.get(i);
			}
		}

		Coin coin = new Coin();
		coinsPool.add(coin);
		return coin;
	}

	public void update() {
		playerRect.set(player.getX(), player.getY(), player.getX() + player.getWidth(), player.getY() + player.getHeight());
		coinsRotationTick++;
		if (coinsRotationTick == bitmaps.length * COINS_ROTATION_SPEED) {
			coinsRotationTick = 0;
		}

		currentBitmap = bitmaps[coinsRotationTick / COINS_ROTATION_SPEED];

		tick += Math.abs(speed.getXv() * 4);
		for (int i = 0; i < coinsPool.size(); i++) {
			coinsPool.get(i).update(speed.getXv() * 4);
		}

		if (tick > coinsInitTick && Math.random() < 0.8) {
			tick = 0;
			createNewCoinsSet();
		}
	}

	public int getCoinsCollected() {
		return coinsCollected;
	}

	private class Coin {
		private float x, y;
		private boolean dirty;
		private boolean gained;
		private Paint paint;

		public Coin() {
			dirty = true;
			paint = new Paint();
		}

		public void update(float xv) {
			if (!dirty) {
				x += xv;
				if (x < -coinWidth) {
					dirty = true;
				}

				if (gained) {
					paint.setAlpha(paint.getAlpha() - 3);
					y -= 5;
					if (y < -coinHeight || paint.getAlpha() <= 0) {
						dirty = true;
					}
				} else {
					if (!dirty) {
						if (overlaps(playerRect)) {
							gainCoin();
						}
					}
				}
			}
		}

		public boolean overlaps(Rect r) {
			return !(x + coinWidth < r.left || x > r.right || y + coinHeight < r.top || y > r.bottom);
		}

		private void gainCoin() {
			if (!gained) {
				gained = true;
				coinsCollected++;
			}
		}

		public boolean isDirty() {
			return dirty;
		}

		public void setDirty(boolean dirty) {
			this.dirty = dirty;
			if (!dirty) {
				gained = false;
				paint.setAlpha(255);
			}
		}

		public void draw(Canvas canvas) {
			if (!dirty) {
				canvas.drawBitmap(currentBitmap, x, y, paint);
			}
		}
	}
}
