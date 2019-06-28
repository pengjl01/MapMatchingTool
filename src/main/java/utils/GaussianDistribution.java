package utils;

/*
 * @author pjl
 * @version 创建时间：2019年4月11日 下午8:30:24
 * 一维高斯分布
 */
public class GaussianDistribution {
	double mu, sigma;
	double num;

	public GaussianDistribution(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
		num = 1 / (Math.sqrt(2 * Math.PI) * sigma);
	}

	public double probabilityDensity(double x) {
//		double p1 = -Math.pow(x - mu, 2) / (2 * Math.pow(sigma, 2));
		double p1 = -0.5 * Math.pow((x - mu) / sigma, 2);
		return num * Math.pow(Math.E, p1);
	}
}
