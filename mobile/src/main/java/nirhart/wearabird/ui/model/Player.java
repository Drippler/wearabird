/**
 *
 */
package nirhart.wearabird.ui.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import nirhart.wearabird.ui.model.components.Speed;

/**
 * This is a test droid that is dragged, dropped, moved, smashed against
 * the wall and done other terrible things with.
 * Wait till it gets a weapon!
 *
 * @author impaler
 */
@SuppressWarnings("unused")
public class Player {

	private int currentBitmap;
	private Bitmap[] bitmaps;
	private int x;
	private int y;
	private int bitmapChangeTick;
	private Speed speed;
	private int width, height;

	public Player(Context context, int[] resources, int x, int y, int screenHeight) {
		this.x = x;
		this.y = y;
		this.speed = new Speed();
		this.speed.setXv(0);
		this.speed.setYv(0);
		this.speed.setYa(0.15f);
		initBitmaps(context, resources, screenHeight);
	}

	private void initBitmaps(Context context, int[] resources, int height) {
		int length = resources.length;
		this.bitmaps = new Bitmap[length];
		Resources res = context.getResources();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		for (int i = 0; i < length; i++) {
			Bitmap playerDecoded = BitmapFactory.decodeResource(res, resources[i], options);
			float scale = (float) options.outHeight / ((float) height / 5f);
			int scaledWidth = (int) (options.outWidth / scale);
			int scaledHeight = (int) (options.outHeight / scale);

			if (this.width == 0) {
				this.width = scaledWidth;
				this.height = scaledHeight;
			}

			bitmaps[i] = Bitmap.createScaledBitmap(playerDecoded, scaledWidth, scaledHeight, true);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}

	public void draw(Canvas canvas) {
		if (bitmapChangeTick % 2 == 0) {
			if (speed.getYv() <= 0 || currentBitmap != 0) {
				currentBitmap = (currentBitmap + 1) % bitmaps.length;
			}
		}

		canvas.drawBitmap(bitmaps[currentBitmap], x, y, null);
	}

	public void update() {
		bitmapChangeTick++;
		speed.update();
		x += (speed.getXv());
		y += (speed.getYv());

	}
}
