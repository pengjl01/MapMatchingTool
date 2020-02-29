package algorithm.mapmatching.hmm;

import org.locationtech.jts.geom.Coordinate;

import data.simplefeature.pointfeature.PointFeature;
import data.simplefeature.roadsegment.RoadSegment;

public class HMMNode {
	public HMMNode parent = null;
	public double prob;// 该节点的概率
//	public int idx;
	public Coordinate matchedCoor;// 匹配点的坐标
//	public Long osm_id;
	public RoadSegment road;// 该节点对应的道路feature
	public PointFeature point;
	public Coordinate prepCoordinate;
	public Coordinate[] nearestNode;

	public HMMNode(double prob, HMMNode parent, Coordinate lastCoordinate, RoadSegment road, PointFeature point) {
		this.parent = parent;
		this.prob = prob;
		this.matchedCoor = lastCoordinate;
		this.road = road;
		this.point = point;
		prepCoordinate = (this.point.getPoint()).getCoordinate();
		nearestNode = this.road.getClosestNodes(this.matchedCoor);
	}
}