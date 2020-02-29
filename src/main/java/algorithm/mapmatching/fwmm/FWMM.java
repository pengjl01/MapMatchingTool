package algorithm.mapmatching.fwmm;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.linearref.LocationIndexedLine;

import algorithm.mapmatching.hmm.HMMNode;
import data.simplefeature.pointfeature.PointFeature;
import data.simplefeature.roadsegment.RoadSegment;

/*
 * @author pjl
 * @version 创建时间：2019年4月4日 下午1:58:28
 * 类说明
 */
public class FWMM extends WMM {
//	置信态切换至快速匹配态的参数
	public static int SWITCH_FLAG = 3;
	public static double SWITCH_PROB = 0.95;
//	保持快速匹配态的参数
	public static double KEEP_DIRECTION_PROB = 0.7;
	public static double KEEP_DISTANCE = 30;

	public FWMM(SpatialIndex index) {
		super(index);
	}

	public FWMM(SpatialIndex index, boolean debug) {
		super(index, debug);
	}

	int status = 0;// 0：置信过程，1：快速匹配过程
	int matchflag = 0;
	RoadSegment preroad = null;

//	debug only
	int fastmatchnum = 0;

	@Override
	public void match(int start, int end) {
//			对所有待匹配点
		for (int i = start; i < end; ++i) {
			if (status == 0) {
				super.matchFeature(i);
//					快速匹配检验
				canFastMatch();
			}
//					快速匹配阶段
			else if (status == 1) {
				fastMatch(i);
			}
		}
		saveData();
		System.out.println("fastmatchnum " + fastmatchnum);
	}

	private void fastMatch(int i) {
		if (debug) {
			System.out.println("fast matching");
		}
		PointFeature temp = origin.get(i);
		Point point = temp.getPoint();
		Coordinate pCoordinate = point.getCoordinate();
		LocationIndexedLine line = new LocationIndexedLine(preroad.getGeom());
		Coordinate closestCoordinate = line.extractPoint(line.project(pCoordinate));
		double directionprob = calcDirectionProb(line, closestCoordinate, preroad, temp);
		if (canKeepFast(directionprob, pCoordinate.distance(closestCoordinate))) {
			String osmid = preroad.getID();
			saveData(i, osmid, closestCoordinate);
			++fastmatchnum;
		} else {
			super.matchFeature(i);
			status = 0;
		}
	}

	private boolean canKeepFast(double directionprob, double distance) {
		return (directionprob > KEEP_DIRECTION_PROB && distance < KEEP_DISTANCE);
	}

	private void canFastMatch() {
		if (preState != null && preState.size() > 0) {
			// 判定是否可以从置信态转为快速匹配态
			HMMNode best = getBest(preState);
			if (best.road == preroad) {
				matchflag++;
			} else {
				matchflag = 0;
				preroad = best.road;
			}
//			连续FAST_MATCH_FLAG个点匹配在同一条道路上
			if (matchflag >= SWITCH_FLAG) {
				saveData();
				status = 1;
			}
//		最优点概率超过SWITCH_PROB
			if (best.prob >= SWITCH_PROB) {
				status = 1;
			}
		}
	}
}