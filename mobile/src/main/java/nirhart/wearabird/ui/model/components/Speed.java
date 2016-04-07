package nirhart.wearabird.ui.model.components;

@SuppressWarnings("unused")
public class Speed {

	// Velocity
	private float xv = 1;
	private float yv = 1;

	// Acceleration
	private float ya = 0;
	private float xa = 0;

	public Speed() {
	}

	public Speed(float xv, float yv, float xa, float ya) {
		this.xv = xv;
		this.yv = yv;
		this.xa = xa;
		this.ya = ya;
	}

	public float getXv() {
		return xv;
	}

	public void setXv(float xv) {
		this.xv = xv;
	}

	public float getYv() {
		return yv;
	}

	public void setYv(float yv) {
		this.yv = yv;
	}

	public float getYa() {
		return ya;
	}

	public void setYa(float ya) {
		this.ya = ya;
	}

	public float getXa() {
		return xa;
	}

	public void setXa(float xa) {
		this.xa = xa;
	}

	public void update() {
		this.xv += this.xa;
		this.yv += this.ya;
	}
}
