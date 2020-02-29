package utils;

/*
 * @author pjl
 * @version 创建时间：2019年4月11日 下午8:30:24
 * 一维高斯分布
 */
public class GaussianDistribution {
	static int STEP_NUM = 10;
	static int PLUS_STEP_NUM = 10000;
	double mu, sigma;
	double num;
	double[] x10LessThanProbs;

	public GaussianDistribution() {
		this.mu = 0;
		this.sigma = 1;
		num = 1 / (Math.sqrt(2 * Math.PI) * sigma);
		calcProbs(200);
	}

	public GaussianDistribution(double mu, double sigma) {
		this.mu = mu;
		this.sigma = sigma;
		num = 1 / (Math.sqrt(2 * Math.PI) * sigma);
		calcProbs(200);
	}

	/*
	 * @param max:最大值
	 * 
	 * @param stepNum:每个1被划分为多少份
	 * 
	 * @param plusStepNum：每份划分为多少份来计算积分
	 */
	private void calcProbs(int max) {
		int arrayLength = max * STEP_NUM;
		x10LessThanProbs = new double[arrayLength];
		double plusStepLength = 1.0 / STEP_NUM / PLUS_STEP_NUM;
		double sum = 0;
		int j = 0;
		double leftX = 0;
		for (int i = 0; j < arrayLength; ++i) {
			if (i % PLUS_STEP_NUM == 0) {
				x10LessThanProbs[j] = sum;
				++j;
			}
			sum += (plusStepLength * (probabilityDensity(leftX) + probabilityDensity(leftX + plusStepLength)));
			leftX += plusStepLength;
		}
	}

	public double probabilityDensity(double x) {
//		double p1 = -Math.pow(x - mu, 2) / (2 * Math.pow(sigma, 2));
		double p1 = -0.5 * Math.pow((x - mu) / sigma, 2);
		return num * Math.pow(Math.E, p1);
	}

	/*
	 * 返回x绝对值大于minx的概率，以0.1为精确度四舍五入取整
	 */
	public double xMoreThanProb(double minx) {
		return 1 - xLessThanProb(minx);
	}

	/*
	 * 返回x绝对值小于maxx的概率，以0.1为精确度四舍五入取整
	 */
	public double xLessThanProb(double maxx) {
		return x10LessThanProbs[getX10(maxx)];
	}

	/*
	 * 乘十取整
	 */
	protected static int getX10(double d) {
		return (int) Math.round(d * 10);
	}
}
