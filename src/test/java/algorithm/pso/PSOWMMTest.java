package algorithm.pso;

/*
 * @author pjl
 * @version 创建时间：2019年7月15日 下午1:38:45
 * 类说明
 */
public class PSOWMMTest extends PSOWMM {
	public static void main(String[] args) {
		PSOWMM p = new PSOWMM();
		p.initPopulation();
		for (PSONode a : p.populationData) {
			System.out.println(a);
		}
	}
}
