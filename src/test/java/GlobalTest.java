import constants.Constants;
import utils.GaussianDistribution;

/*
 * @author pjl
 * @version 创建时间：2019年7月4日 下午7:50:09
 * 类说明
 */
public class GlobalTest {
	protected static double MAX_RADIUS = 200;
	static GaussianDistribution gd = new GaussianDistribution(Constants.MU, Constants.SIGMA);

	static double func1(double distance) {
		if (distance < MAX_RADIUS) {
			return Math.pow(1.0 - (distance / MAX_RADIUS), 2);
		} else {
			return 0.0;
		}
	}

	static double func0(double distance) {
		if (distance < MAX_RADIUS) {
			return gd.probabilityDensity(distance);
		} else {
			return 0.0;
		}
	}

	public static void main(String[] args) {
		int max = 100000;
		long begintime, endtime;
		double num = 50.0;
//		begintime = System.nanoTime();
		begintime = System.currentTimeMillis();
		for (int i = 0; i < max; ++i) {
			func0(num);
		}
		endtime = System.currentTimeMillis();
		System.out.println("time0:" + (endtime - begintime));
		begintime = System.currentTimeMillis();
		for (int i = 0; i < max; ++i) {
			func1(num);
		}
		endtime = System.currentTimeMillis();
		System.out.println("time1:" + (endtime - begintime));
		begintime = System.currentTimeMillis();
		for (int i = 0; i < max; ++i) {
			func0(num);
		}
		endtime = System.currentTimeMillis();
		System.out.println("time0:" + (endtime - begintime));
		begintime = System.currentTimeMillis();
		for (int i = 0; i < max; ++i) {
			func1(num);
		}
		endtime = System.currentTimeMillis();
		System.out.println("time1:" + (endtime - begintime));
	}
}
