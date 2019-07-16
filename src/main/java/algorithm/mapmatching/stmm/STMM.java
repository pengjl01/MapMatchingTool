package algorithm.mapmatching.stmm;

import org.locationtech.jts.index.SpatialIndex;

import algorithm.mapmatching.hmm.HMM;

/*
 * @author pjl
 * @version 创建时间：2019年7月15日 下午7:32:29
 * stmm
 * https://www.sciencedirect.com/science/article/pii/S0968090X15000728#b0110
 */
public class STMM extends HMM {

	public STMM(SpatialIndex index) {
		super(index);
	}

	public STMM(SpatialIndex index, boolean debug) {
		super(index, debug);
	}
}
