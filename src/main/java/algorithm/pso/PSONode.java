package algorithm.pso;

import java.util.Random;

/*
 * @author pjl
 * @version 创建时间：2019年7月13日 下午7:46:40
 * pso的一个个体的数据
 */
public class PSONode {
	public float acc;
	public double[] n;
	public double[] speed;
	public float pBestAcc;
	public double[] pBestN;

	public PSONode() {
		n = new double[3];
		n[0] = new Random().nextDouble() * 5.0;
		n[1] = new Random().nextDouble() * 5.0;
		n[2] = new Random().nextDouble() * 5.0;
		speed = new double[3];
		speed[0] = new Random().nextDouble() * 0.5;
		speed[1] = new Random().nextDouble() * 0.5;
		speed[2] = new Random().nextDouble() * 0.5;
		pBestN = new double[3];
		pBestN[0] = n[0];
		pBestN[1] = n[1];
		pBestN[2] = n[2];
	}

	@Override
	public String toString() {
		return "n1:" + n[0] + ";n2:" + n[1] + ";n3:" + n[2] + ";acc:" + acc + ";pBestn1:" + pBestN[0] + ";pBestn2:"
				+ pBestN[1] + ";pBestn3:" + pBestN[2] + ";pBestAcc" + pBestAcc;
	}

	/*
	 * 更新acc，自动更新个体极值 使用有诸多限制
	 */
	public void updateAcc(float acc) {
		this.acc = acc;
		if (acc > pBestAcc) {
			pBestN[0] = n[0];
			pBestN[1] = n[1];
			pBestN[2] = n[2];
			pBestAcc = acc;
		}

	}

	/*
	 * 更新参数值
	 */
	public void updateParams(float gBestAcc, double[] gBestN, double omega) {
		double rand1 = new Random().nextDouble();
		double rand2 = new Random().nextDouble();
		for (int i = 0; i < 3; ++i) {
			speed[i] = omega * speed[i] + PSOConstants.c1 * rand1 * (pBestN[i] - n[i])
					+ PSOConstants.c2 * rand2 * (gBestN[i] - n[i]);
			if (speed[i] > PSOConstants.maxSpeed) {
				speed[i] = PSOConstants.maxSpeed;
			}
			if (speed[i] < PSOConstants.minSpeed) {
				speed[i] = PSOConstants.minSpeed;
			}
			n[i] = n[i] + speed[i];
		}
	}
}
