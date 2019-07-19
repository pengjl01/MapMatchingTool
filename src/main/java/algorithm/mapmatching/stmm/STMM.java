package algorithm.mapmatching.stmm;

import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.linearref.LinearLocation;
import org.locationtech.jts.linearref.LocationIndexedLine;

import algorithm.mapmatching.MatcherIMPL;
import algorithm.mapmatching.fwmm.Tools;
import algorithm.pathfinder.astar.Astar;
import data.simplefeature.pointfeature.PointFeature;
import data.simplefeature.roadsegment.RoadSegment;
import entity.graph.AdjacencyGraph;
import entity.graph.Graph;

/*
 * @author pjl
 * @version 创建时间：2019年7月15日 下午7:32:29
 * stmm
 * https://www.sciencedirect.com/science/article/pii/S0968090X15000728#b0110
 * 果然人类之间无法相互理解，此算法实现写的真烂
 */
public class STMM extends MatcherIMPL {

	public STMM(SpatialIndex index) {
		super(index);
	}

	public STMM(SpatialIndex index, boolean debug) {
		super(index, debug);
	}

	public static Double wd = 32.0;
	public static Double wb = 21.0;
	public static Double ws = 35.0;
	public static Double wh = 12.0;
	protected static double LowProbabilityRoutes = 2000;
	RoadSegment[] matchedRoads;

	@Override
	protected void initMatch(SimpleFeatureCollection points) {
		origin = sfc2PointFeatureList(points);
		matchedIds = new String[origin.size()];
		matchedPoints = new Point[origin.size()];
		matchedRoads = new RoadSegment[origin.size()];
	}

	@Override
	protected void matchFeature(int i) {
		if (i == 0) {
			imp();
		} else {
			smp(i);
		}

	}

	/*
	 * 搜索距离第一个点最近的点作为第一个点的位置（论文里没写怎么确定第一个点，我只能这么做了）
	 */
	private void imp() {
		PointFeature firstPoint = origin.get(0);
		Coordinate firstCoor = firstPoint.getPoint().getCoordinate();
		List<RoadSegment> lines = getPossibleRoads(firstCoor, LowProbabilityRoutes / 2);
		if (lines.size() > 0) {
			double minFirstDis = Double.POSITIVE_INFINITY;
			Coordinate firstMatchedCoor = null;
			RoadSegment firstClostestRoad = null;
			for (RoadSegment r : lines) {
				LocationIndexedLine line = new LocationIndexedLine(r.getGeom());
				LinearLocation here = line.project(firstCoor);
				Coordinate point = line.extractPoint(here);
				double dist = point.distance(firstCoor);
				if (dist < minFirstDis) {
					minFirstDis = dist;
					firstMatchedCoor = point;
					firstClostestRoad = r;
				}
			}
			saveData(0, firstClostestRoad.getID(), firstMatchedCoor);
			matchedRoads[0] = firstClostestRoad;
		}
	}

	private void smp(int i) {
		PointFeature prePoint = origin.get(i - 1);
		Coordinate preCoor = prePoint.getPoint().getCoordinate();
		PointFeature thisPoint = origin.get(i);
		Coordinate thisCoor = thisPoint.getPoint().getCoordinate();
		List<RoadSegment> lines = getPossibleRoads(thisCoor, LowProbabilityRoutes / 2);
		if (lines.size() > 0) {
			Graph graph = new AdjacencyGraph(lines);
			Coordinate preMatchedCoor = matchedPoints[i - 1].getCoordinate();
			RoadSegment preClostestRoad = matchedRoads[i - 1];
			/*
			 * 第一个点的最终位置插入图中
			 */
			Coordinate[] preNextNodes = preClostestRoad.getClosestNodes(preMatchedCoor);
			if (preNextNodes != null) {
//				把当前点添加到图
				if (!graph.cutAndAdd(preNextNodes[0], preNextNodes[1], preMatchedCoor)) {
					System.out.println("imp error: first add & cut error");
				}
			}
			/*
			 * 确定第二个点的正确位置
			 */
			double maxWeight = Double.NEGATIVE_INFINITY;
			RoadSegment bestRoadSegment = null;
			Coordinate bestMatchedCoor = null;
			for (RoadSegment r : lines) {
				double dJWeight = 0;
				double dAlphaWeight = 0;
				double dLWeight = 0;
				double dThetaWeight = 0;
				LocationIndexedLine line = new LocationIndexedLine(r.getGeom());
				LinearLocation here = line.project(thisCoor);
				Coordinate candidateCoor = line.extractPoint(here);
				Coordinate[] thisNextNodes = r.getClosestNodes(candidateCoor);
				/*
				 * dJWeight计算
				 */
				double dJ = getDJ(graph, candidateCoor, preMatchedCoor, thisNextNodes, preCoor.distance(thisCoor));
				dJWeight = calcDJWeight(dJ);
				/*
				 * dAlphaWeight计算
				 */
				if (preMatchedCoor.equals(thisCoor) || preMatchedCoor.equals(candidateCoor)) {
					dAlphaWeight = 1;
				} else {
					double dalpha1 = algorithm.mapmatching.fwmm.Tools.calcDirection(preMatchedCoor, thisCoor);
					double dalpha2 = algorithm.mapmatching.fwmm.Tools.calcDirection(preMatchedCoor, candidateCoor);
					dAlphaWeight = calcDAlfhaWeight(Math.abs(dalpha1 - dalpha2));
				}
				/*
				 * dLWeight计算
				 */
				double dl = candidateCoor.distance(thisCoor);
				dLWeight = calcDLWeight(dl);
				/*
				 * dThetaWeight计算
				 */
				double dTheta = getDTheta(line, candidateCoor, r, thisPoint);
				dThetaWeight = calcDThetaWeight(dTheta);
				/*
				 * 综合weight计算
				 */
				double weight = calcWeight(dJWeight, dAlphaWeight, dLWeight, dThetaWeight);
				if (weight > maxWeight) {
					bestRoadSegment = r;
					bestMatchedCoor = candidateCoor;
				}
				if (debug) {
					System.out.println("   ID:" + r.getID() + ";dJWeight:" + dJWeight + ";dAlphaWeight:" + dAlphaWeight
							+ ";dLWeight:" + dLWeight + ";dThetaWeight:" + dThetaWeight + ";Weight:" + weight);
				}
			}
			if (debug) {
				System.out.println("Final ID:" + bestRoadSegment.getID());
			}
			saveData(i, bestRoadSegment.getID(), bestMatchedCoor);
			matchedRoads[i] = bestRoadSegment;
		}

	}

	double calcWeight(double dJWeight, double dAlphaWeight, double dLWeight, double dThetaWeight) {
		return wd * dJWeight + wb * dAlphaWeight + ws * dLWeight + wh * dThetaWeight;
	}

	double getDJ(Graph graph, Coordinate candidate, Coordinate preMatchedCoor, Coordinate[] nearestNodes,
			double distance) {
		if (candidate.equals(preMatchedCoor))
			return 0.0;
		if (nearestNodes != null) {
			if (!graph.cutAndAdd(nearestNodes[0], nearestNodes[1], candidate)) {
				return 0.0;
			}
		}
		Astar astar = new Astar(graph, preMatchedCoor, candidate);
		if (nearestNodes != null) {
			graph.repareCut(nearestNodes[0], nearestNodes[1], candidate);
		}
		return Math.abs(Math.abs(astar.routeDistance()) - Math.abs(distance));
	}

	double calcDJWeight(double dJ) {
		if (dJ <= 200)
			return 1 - dJ / 200.0;
		else {
			return 0.0;
		}
	}

	double calcDAlfhaWeight(double dAlpha) {
		return Math.abs(Math.cos(dAlpha));
	}

	double calcDLWeight(double dL) {
		if (dL <= 1000) {
			return 1 - dL / 1000.0;
		} else {
			return 0.0;
		}
	}

	double getDTheta(LocationIndexedLine line, Coordinate closestCoordinate, RoadSegment lineFeature,
			PointFeature pointFeature) {
		char oneway = lineFeature.getOneway();
		Double direction = pointFeature.getDirection();
		if (direction > 0) {
			List<Double> lineDirection = Tools.getRoadDirection(line, closestCoordinate, oneway);
			double ans = Double.POSITIVE_INFINITY;
			for (Double d : lineDirection) {
				double temp = Math.abs((d - direction));
				if (temp < ans)
					ans = temp;
			}
			return ans;
		}
		return 0;
	}

	double calcDThetaWeight(double dTheta) {
		return Math.abs(Math.cos(dTheta));
	}
}
