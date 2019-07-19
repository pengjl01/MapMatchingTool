package algorithm.pso;

import java.io.IOException;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;

import algorithm.mapmatching.fwmm.WMM;
import app.tools.APPTools;
import app.tools.AccData;
import app.tools.DiffTools;
import constants.Constants;
import utils.output.OutputWriter;

/*
 * @author pjl
 * @version 创建时间：2019年7月13日 下午7:49:19
 * 粒子群优化算法-专为WMM开发
 * 可复用性很差
 */
public class PSO {
	/*
	 * 输入区
	 */
	WMM m;
	SimpleFeatureCollection pointOrigin;
	List<String> truth;
	float gBestAcc;
	double[] gBestN;
	PSONode[] populationData;
	int step = 0;
	boolean debug = false;
	OutputWriter ow;

	protected PSO() {

	}

	public PSO(WMM m, SimpleFeatureCollection pointOrigin, List<String> truth) throws IOException {
		this.m = m;
		this.pointOrigin = pointOrigin;
		this.truth = truth;
		gBestN = new double[3];
		initPopulation();
		ow = new OutputWriter(Constants.TXT_RESULT_PATH + "PSOResult.txt");
	}

	public PSO(WMM m, SimpleFeatureCollection pointOrigin, List<String> truth, boolean debug) {
		this.debug = debug;
		this.m = m;
		this.pointOrigin = pointOrigin;
		this.truth = truth;
		gBestN = new double[3];
		initPopulation();
	}

	public void start() throws IOException {
		while (!shouldStop()) {
			ow.write("step:" + step);
			if (debug) {
				System.out.println("step:" + step);
			}
			updateParams();
			updateAcc();
			findGroupBest();
			step += 1;
			ow.write(this.toString());
			System.out.println("***************************************");
			if (debug) {
				System.out.println(this);
				System.out.println("***************************************");
			}
		}
		ow.close();
	}

	boolean shouldStop() {
		if (step >= PSOConstants.maxgen)
			return true;
		return false;
	}

	/*
	 * 初始化种群的参数
	 */
	void initPopulation() {
		populationData = new PSONode[PSOConstants.popSize];
		for (int i = 0; i < PSOConstants.popSize; ++i) {
			populationData[i] = new PSONode();
		}
	}

	/*
	 * 计算所有粒子的精确度并更新
	 */
	void updateAcc() throws IOException {
		for (int i = 0; i < PSOConstants.popSize; ++i) {
			updateAcc(populationData[i]);
			ow.write(i + "th:" + populationData[i]);
			if (debug) {
				System.out.println(i + "th:" + populationData[i]);
			}
		}
	}

	/*
	 * 计算单个粒子的精确度并更新
	 */
	private void updateAcc(PSONode psoNode) {
		float acc = calcAcc(psoNode);
		psoNode.updateAcc(acc);
	}

	/*
	 * 寻找全局最优
	 */
	private void findGroupBest() {
		for (int i = 0; i < PSOConstants.popSize; ++i) {
			if (populationData[i].acc > gBestAcc) {
				gBestAcc = populationData[i].acc;
				gBestN[0] = populationData[i].n[0];
				gBestN[1] = populationData[i].n[1];
				gBestN[2] = populationData[i].n[2];
			}
		}

	}

	/*
	 * 返回当前参数下的精度
	 */
	float calcAcc(PSONode psoNode) {
		WMM.n1 = psoNode.n[0];
		WMM.n2 = psoNode.n[1];
		WMM.n3 = psoNode.n[2];
		AccData accData = DiffTools.calcAcc(truth, APPTools.getMatchingAnswer(m.match(pointOrigin)), "");
		return accData.calcAcc();
	}

	/*
	 * 更新整个族群的速度与参数值
	 */
	private void updateParams() {
		for (int i = 0; i < PSOConstants.popSize; ++i) {
			populationData[i].updateParams(gBestAcc, gBestN, getOmega());
		}
	}

	/*
	 * 计算omega的值
	 */
	private double getOmega() {
		return (PSOConstants.omegaini - PSOConstants.omegaend) * (PSOConstants.maxgen - step) / PSOConstants.maxgen
				+ PSOConstants.omegaend;
	}

	@Override
	public String toString() {
		return "Best:" + gBestN[0] + ";" + gBestN[1] + ";" + gBestN[2] + ";acc:" + gBestAcc;
	}
}
