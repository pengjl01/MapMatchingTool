package utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/*
 * @author pjl
 * @version 创建时间：2019年7月5日 下午1:27:11
 * 类说明
 */
public class GaussianDistributionTest extends GaussianDistribution {

	public GaussianDistributionTest() {
		super();
	}

	@Test
	public void getX10Test() throws Exception {
		double a1 = 1.345324234;
		double a2 = 133.655324234;
		double a3 = 0.365324234;
		double a4 = 35.845324234;
		assertEquals("1.344324234", 13, getX10(a1));
		assertEquals("133.655324234", 1337, getX10(a2));
		assertEquals("0.365024234", 4, getX10(a3));
		assertEquals("35.849324234", 358, getX10(a4));
	}

	public static void main(String[] args) {
		GaussianDistribution gd = new GaussianDistribution(0, 10);
		double t = 0.0;
		for (int i = 0; i < 200; ++i) {
//			System.out.println(0.5 + gd.x10LessThanProbs[i]);

			System.out.println(i + " : " + gd.xMoreThanProb(t));
//			System.out.println(i + " : " + gd.probabilityDensity(t));
//			System.out.println(gd.xMoreThanProb(t));
			t += 1.0;

		}

	}
}
