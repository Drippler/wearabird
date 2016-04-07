package nirhart.wearabird.ui.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import nirhart.wearabird.ui.model.components.Speed;

public class Clouds {

	private final int cloudsNum;
	private Bitmap[] bitmaps;
	private Cloud[] clouds;
	private Speed speed;
	private int width, height;
	private int tick;
	private int cloudsInitTick;

	public Clouds(Context context, int[] resources, int cloudsNum, Speed speed, int width, int height) {
		this.clouds = new Cloud[cloudsNum];
		for (int i = 0; i < cloudsNum; i++) {
			this.clouds[i] = new Cloud();
		}
		this.speed = speed;
		this.cloudsNum = cloudsNum;
		this.width = width;
		this.height = height;
		initBitmaps(context, resources, height);
	}

	private void initBitmaps(Context context, int[] resources, int height) {
		int length = resources.length;
		this.bitmaps = new Bitmap[length];
		Resources res = context.getResources();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		for (int i = 0; i < length; i++) {
			Bitmap cloudDecoded = BitmapFactory.decodeResource(res, resources[length - 1 - i], options);
			float scale = (float) options.outHeight / ((float) height / 7f);
			int scaledWidth = (int) (options.outWidth / scale);
			int scaledHeight = (int) (options.outHeight / scale);

			bitmaps[i] = Bitmap.createScaledBitmap(cloudDecoded, scaledWidth, scaledHeight, true);

			cloudsInitTick = scaledWidth * 2;
			tick = cloudsInitTick;
		}
	}

	public void draw(Canvas canvas) {
		for (int i = 0; i < cloudsNum; i++) {
			clouds[i].draw(canvas);
		}
	}

	private void createNewCloud(int cloudNum) {
		int resource = (int) (Math.random() * bitmaps.length);
		Cloud cloud = clouds[cloudNum];
		Bitmap bitmap = bitmaps[resource];
		cloud.setBitmap(bitmap);
		cloud.x = width;
		cloud.y = (float) (Math.random() * height - bitmap.getHeight() / 2);
		cloud.setDirty(false);
	}

	public void update() {
		tick += Math.abs(speed.getXv());
		for (int i = 0; i < cloudsNum; i++) {
			clouds[i].update(speed.getXv());
		}

		if (tick > cloudsInitTick && Math.random() < 0.8) {
			tick = 0;
			tryToInitACloud();
		}
	}

	private void tryToInitACloud() {
		for (int i = 0; i < cloudsNum; i++) {
			if (clouds[i].isDirty()) {
				createNewCloud(i);
				break;
			}
		}
	}

	private class Cloud {
		private float x, y;
		private Bitmap bitmap;
		private boolean dirty;

		public Cloud() {
			dirty = true;
		}

		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		public void update(float xv) {
			if (!dirty) {
				x += xv;
				if (x < -bitmap.getWidth()) {
					dirty = true;
				}
			}
		}

		public boolean isDirty() {
			return dirty;
		}

		public void setDirty(boolean dirty) {
			this.dirty = dirty;
		}

		public void draw(Canvas canvas) {
			if (!dirty) {
				canvas.drawBitmap(bitmap, x, y, null);
			}
		}
	}
}
