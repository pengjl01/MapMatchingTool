package algorithm.mapmatching.hmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;

import algorithm.mapmatching.MatcherIMPL;
import algorithm.pathfinder.astar.Astar;
import constants.Constants;
import data.simplefeature.pointfeature.PointFeature;
import data.simplefeature.roadsegment.RoadSegment;
import entity.graph.AdjacencyGraph;
import entity.graph.Graph;
import utils.GaussianDistribution;

/*
 * @author pjl
 * @version 创建时间：2019年5月20日 下午8:27:36
 * 标准hmm
 */
public class HMM extends MatcherIMPL {
	protected int maxInterval = 180000;// 3min
	protected GaussianDistribution gd;

	protected double BETA = 5;
	protected double MAX_RADIUS = 200;
	protected double RADIUS = 50;
	protected double LowProbabilityRoutes = 2000;
	/*
	 * 当前节点之前有多少个奇异点
	 */
	static double MIN_PROB = 0.0001;
	/*
	 * HMM匹配过程需要的参数
	 */
//	上一个状态
	public List<HMMNode> preState = new ArrayList<HMMNode>();
//	上一个pointFeatures的时间
	public Long preDatetime = null;
//	匹配链中的pointFeatures
	public List<Integer> matchingFeatureIndexs = new ArrayList<Integer>();
//	当前pointFeatures的时间
	Long datetime = null;

	public HMM(SpatialIndex index) {
		super(index);
		gd = new GaussianDistribution(Constants.MU, Constants.SIGMA);
	}

	public HMM(SpatialIndex index, boolean debug) {
		super(index, debug);
		gd = new GaussianDistribution(Constants.MU, Constants.SIGMA);
	}

	@Override
	public void match(int start, int end) {
		for (int i = start; i < end; ++i)
			matchFeature(i);
		saveData();
	}

	@Override
	public void matchFeature(int i) {
		if (debug) {
			System.out.println("--------------------------");
		}
		datetime = origin.get(i).getTime();
		// 判定重置马尔科夫链(两点时间差大于设定值)
		if (preDatetime != null && datetime - preDatetime > maxInterval) {
			saveData();
			preDatetime = null;
		}
		List<HMMNode> nextState = getNextState(origin.get(i));
		if (debug) {
			System.out.println("thisState size : " + nextState.size());
		}
		// 该点有效，覆盖之前的状态
		if (nextState.size() > 0) {
			matchingFeatureIndexs.add(i);
			preDatetime = datetime;
			preState = nextState;
		} else {// 否则gap++，不覆盖上一个状态
			String osm_id = null;
			PointFeature temp = origin.get(i);
			Point p = temp.getPoint();
			Coordinate GPSCoordinate = p.getCoordinate();
			List<RoadSegment> lines = getPossibleRoads(GPSCoordinate);
			double minDist = Constants.MAX_SEARCH_DISTANCE + 10000;
			Coordinate minDistPoint = null;
			for (RoadSegment road : lines) {
				LocationIndexedLine line = new LocationIndexedLine(road.getGeom());
				LinearLocation here = line.project(GPSCoordinate);
				Coordinate point = line.extractPoint(here);
				double dist = point.distance(GPSCoordinate);
				if (dist < minDist) {
					minDist = dist;
					minDistPoint = point;
					osm_id = road.getID();
				}
			}
			if (minDistPoint == null) {
				minDistPoint = GPSCoordinate;
			}
			saveData(i, osm_id, geometryFactory.createPoint(minDistPoint));
		}
	}

	/*
	 * 将概率标准化（使列表中hmmnode的概率之和为1）
	 */
	static void normalize(List<HMMNode> nextState) {
		if (nextState != null && nextState.size() > 0) {
			double sum = 0;
			for (HMMNode h : nextState) {
				sum += h.prob;
			}
			Iterator<HMMNode> iterator = nextState.iterator();
			while (iterator.hasNext()) {
				HMMNode hmmNode = iterator.next();
				double prob = hmmNode.prob / sum;
				if (prob < MIN_PROB) {
					iterator.remove();
				} else {
					hmmNode.prob = prob;
				}
			}
		}

	}

	/*
	 * 计算下一个马尔科夫链的状态列表
	 */
	List<HMMNode> getNextState(PointFeature pointFeature) {
		Coordinate pCoordinate = pointFeature.getPoint().getCoordinate();
		List<HMMNode> nextStateMap = new ArrayList<HMMNode>();
//	提取该点可能道路
		List<RoadSegment> lines = getPossibleRoads(pCoordinate, LowProbabilityRoutes / 2);
		if (lines.size() > 0) {
			Graph graph = new AdjacencyGraph(lines);
			for (RoadSegment lineFeature : lines) {
				LocationIndexedLine line = new LocationIndexedLine(lineFeature.getGeom());
//				备选道路上与当前点距离最近的点
				Coordinate closestCoordinate = line.extractPoint(line.project(pCoordinate));
				HMMNode hmmNode = getNextHMMNode(pointFeature, lineFeature, graph, pCoordinate, closestCoordinate,
						line);
				if (hmmNode != null)
					nextStateMap.add(hmmNode);
			}
		}
//		概率归一，防止过小
		normalize(nextStateMap);
		return nextStateMap;
	}

	HMMNode getNextHMMNode(PointFeature pointFeature, RoadSegment lineFeature, Graph graph, Coordinate pCoordinate,
			Coordinate closestCoordinate, LocationIndexedLine line) {
//		计算各参数
		Map<String, Object> paramsMap = getParamsMap(lineFeature, graph, pCoordinate, closestCoordinate, pointFeature,
				line);
//		计算最终概率
		double prob = calcProb(paramsMap);
		if (prob > 0) {
			String osmid = lineFeature.getID();
			System.out.println("road id:" + osmid + " prob:" + prob);
			HMMNode parentNode = ((TPData) paramsMap.get("tpData")).parentNode;
			return new HMMNode(prob, parentNode, closestCoordinate, lineFeature, pointFeature);
		}
		return null;
	}

//	由参数map计算最终权值
	double calcProb(Map<String, Object> paramsMap) {
		double ep = (double) paramsMap.get("ep");
		TPData tpData = (TPData) paramsMap.get("tpData");
		return ep * tpData.tp;
	}

	Map<String, Object> getParamsMap(RoadSegment lineFeature, Graph graph, Coordinate pCoordinate,
			Coordinate closestCoordinate, PointFeature pointFeature, LocationIndexedLine line) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		double distance = closestCoordinate.distance(pCoordinate);
		double ep = getEmissionProbility(distance);
		paramsMap.put("ep", ep);
		if (ep > 0) {
			if (debug) {
				System.out.println(" distance=" + distance + " ep=" + gd.probabilityDensity(distance));
			}
			TPData tpData = getBestTP(lineFeature, graph, pCoordinate, closestCoordinate);
			paramsMap.put("tpData", tpData);
		} else {
			paramsMap.put("tpData", new TPData());
		}
		return paramsMap;
	}

	TPData getBestTP(RoadSegment lineFeature, Graph graph, Coordinate pCoordinate, Coordinate closestCoordinate) {
		TPData tpData = new TPData();
		double maxtp = Double.NEGATIVE_INFINITY;
		for (HMMNode h : preState) {
			Coordinate prepCoordinate = (h.point.getPoint()).getCoordinate();
			double temptp = getTransitionProbility(lineFeature, graph, closestCoordinate, h,
					prepCoordinate.distance(pCoordinate));
			if (debug) {
				String preosmid = h.road.getID();
				System.out.println("    road id:" + preosmid + " tp:" + temptp);
			}
			if (temptp > maxtp) {
				maxtp = temptp;
				tpData.parentNode = h;
			}
		}
		if (maxtp > 0)
			tpData.tp = maxtp;
		return tpData;
	}

	/*
	 * 从列表中找到概率最大的hmmnode
	 */
	static HMMNode getBest(List<HMMNode> stateMap) {
		HMMNode best = stateMap.get(0);
		for (int i = 1; i < stateMap.size(); ++i) {
			if (stateMap.get(i).prob > best.prob)
				best = stateMap.get(i);
		}
		return best;
	}

	/*
	 * 结果保存，重置马尔科夫链
	 */
	void saveData() {
		if (debug) {
			System.out.println("writeData,matchingFeaturesSize= " + matchingFeatureIndexs.size());
		}
		if (preState != null && preState.size() > 0) {
			HMMNode best = getBest(preState);
			// 回溯得到链
			Stack<HMMNode> stack = new Stack<HMMNode>();
			while (best != null) {
				stack.push(best);
				best = best.parent;
			}
			// 向数组写入数据
			for (int i = 0; i < matchingFeatureIndexs.size(); ++i) {
				HMMNode h = stack.pop();
				int loc = matchingFeatureIndexs.get(i);
				matchedIds[loc] = h.road.getID();
				matchedPoints[loc] = geometryFactory.createPoint(h.matchedCoor);
			}
		}
		preState.clear();
		matchingFeatureIndexs.clear();
	}

	/*
	 * 求某道路的可能概率 假设数据是服从二维高斯分布
	 */
	double getEmissionProbility(double distance) {
		if (distance < MAX_RADIUS) {
			return gd.probabilityDensity(distance);
		} else {
			return 0.0;
		}
	}

	double getTransitionProbility(RoadSegment lineFeature, Graph graph, Coordinate closestCoordinate, HMMNode h,
			Double distance) {
		return getTransitionProbility(lineFeature, graph, closestCoordinate, h, distance, null);
	}

	/*
	 * 对可能的点进行tp的计算
	 */
	double getTransitionProbility(RoadSegment lineFeature, Graph graph, Coordinate closestCoordinate, HMMNode h,
			Double distance, List<Coordinate> path) {
		double tp = Double.NEGATIVE_INFINITY;
		String roadid1 = lineFeature.getID();
		String roadid2 = h.road.getID();
//		两个点匹配在同一个道路上
		if (roadid1.equals(roadid2)) {
			tp = getSameSegmentTP(lineFeature, graph, closestCoordinate, h, distance, path);
		}
//		两个点匹配在图的不同段上
		else {
			tp = getDiffSegmentTP(lineFeature, graph, closestCoordinate, h, distance, path);
		}
//		tp与之前点的概率相乘，得到最终tp
		return tp * h.prob;
	}

	/*
	 * 求转移概率tp基础版
	 */
	double getTransitionProbility(double roadDistance, double distance) {
		if (debug) {
			System.out.println("  roadDistance:" + roadDistance + " distance:" + distance);
		}
		double dt = Math.abs(Math.abs(distance) - Math.abs(roadDistance));
		return dt > LowProbabilityRoutes ? 0 : (Math.pow(Math.E, -(dt / BETA))) / BETA;
	}

	/*
	 * 求转移概率tp,addDistance为匹配点到端点的距离
	 */
	double getTransitionProbility(Coordinate a, Coordinate b, Graph g, double addDistance, double distance,
			List<Coordinate> path) {
		Astar astar = new Astar(g, a, b);
		path = astar.findCoordinatePath();
		return getTransitionProbility(Math.abs(astar.routeDistance()) + Math.abs(addDistance), distance);
	}

	/*
	 * tp计算（两个点在同一个道路上（是否在同一段上仍需确认））
	 */
	double getSameSegmentTP(RoadSegment lineFeature, Graph graph, Coordinate closestCoordinate, HMMNode h,
			Double distance, List<Coordinate> path) {
		double tp = Double.NEGATIVE_INFINITY;
		char oneway = lineFeature.getOneway();
		LocationIndexedLine line = new LocationIndexedLine(lineFeature.getGeom());
		LinearLocation llthis = line.project(closestCoordinate);
		LinearLocation llpre = line.project(h.matchedCoor);
//		两个点在同一个段上且符合道路方向性限制
		if (onSameSegment(oneway, llthis, llpre)) {
			return getTransitionProbility(closestCoordinate.distance(h.matchedCoor), distance);
		} else {
			tp = getDiffSegmentTP(lineFeature, graph, closestCoordinate, h, distance, path);
		}
		return tp;
	}

	boolean onSameSegment(char oneway, LinearLocation llthis, LinearLocation llpre) {
//		return llthis.getSegmentIndex() == llpre.getSegmentIndex()
//				&& llthis.getComponentIndex() == llpre.getComponentIndex()
//				&& (oneway == 'B' || (oneway == 'F' && llthis.getSegmentFraction() >= llpre.getSegmentFraction())
//						|| (oneway == 'T' && llthis.getSegmentFraction() <= llpre.getSegmentFraction()));
		if (llthis.getSegmentIndex() != llpre.getSegmentIndex())
			return false;
		if (llthis.getComponentIndex() != llpre.getComponentIndex())
			return false;
		if (oneway == 'F') {
			if (llthis.getSegmentFraction() >= llpre.getSegmentFraction()) {
				return true;
			} else {
				return false;
			}
		}
		if (oneway == 'T')
			if (llthis.getSegmentFraction() <= llpre.getSegmentFraction()) {
				return true;
			} else {
				return false;
			}
		return true;
	}

	/*
	 * tp计算（不在同一段上）
	 */
	double getDiffSegmentTP(RoadSegment lineFeature, Graph graph, Coordinate closestCoordinate, HMMNode h,
			Double distance, List<Coordinate> path) {
		double tp = Double.NEGATIVE_INFINITY;
		Coordinate[] nowNodes = lineFeature.getClosestNodes(closestCoordinate);
		Coordinate[] nearestNode = h.road.getClosestNodes(h.matchedCoor);
		for (Coordinate now : nowNodes) {
			for (Coordinate pre : nearestNode) {
				List<Coordinate> temppath = null;
				double temptp = getTransitionProbility(pre, now, graph,
						closestCoordinate.distance(now) + h.matchedCoor.distance(pre), distance, temppath);
				if (temptp > tp) {
					tp = temptp;
					path = temppath;
				}
			}
		}
		return tp;
	}

	public static void main(String[] args) {
		HMM aHmm = new HMM(null);
		for (int i = 0; i < 300; i += 10) {
			System.out.println(i + " : " + aHmm.getEmissionProbility(i));
		}

	}
}
