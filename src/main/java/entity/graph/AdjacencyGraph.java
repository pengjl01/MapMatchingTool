package entity.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiLineString;

import data.simplefeature.roadsegment.RoadSegment;

/*
 * @author pjl
 * @version 创建时间：2019年6月26日 下午12:57:37
 * 使用邻接表存储联通关系的图实现
 * 主要用于Astar
 */
public class AdjacencyGraph implements Graph {
	private List<Coordinate> nodesList;
	private Map<Coordinate, Integer> nodesMap;
	private List<Set<Integer>> edges;

// 测试用
	protected AdjacencyGraph() {
		nodesList = new ArrayList<Coordinate>();
		nodesMap = new HashMap<Coordinate, Integer>();
		edges = new ArrayList<Set<Integer>>();
	}

	public AdjacencyGraph(List<RoadSegment> roads) {
		nodesList = new ArrayList<Coordinate>();
		nodesMap = new HashMap<Coordinate, Integer>();
		edges = new ArrayList<Set<Integer>>();
		for (RoadSegment road : roads) {
			addNodes(road.getGeom());
			addEdges(road.getGeom(), road.getOneway());
		}
	}

	/*
	 * 提取形状中的所有节点，并添加节点
	 */
	@Override
	public void addNodes(MultiLineString geom) {
		Coordinate[] coors = geom.getCoordinates();
		for (Coordinate c : coors) {
			addNode(c);
		}
	}

//  添加一个节点
	protected void addNode(Coordinate c) {
		if (!nodesMap.containsKey(c)) {
			nodesMap.put(c, nodesMap.size());
			nodesList.add(c);
			edges.add(new HashSet<Integer>());
		}
	}

//  移除最后一个节点，还需要另外删除边信息
	protected void removeLastNode() {
		nodesMap.remove(nodesList.get(nodesList.size() - 1));
		edges.remove(edges.size() - 1);
		nodesList.remove(nodesList.size() - 1);
	}

	/*
	 * 添加一条路上的所有段
	 */
	@Override
	public void addEdges(MultiLineString geom, char oneway) {
		for (int j = 0; j < geom.getNumGeometries(); ++j) {
			Coordinate[] coors = geom.getGeometryN(j).getCoordinates();
			for (int i = 1; i < coors.length; ++i) {
				List<Coordinate> passThroughNodes = passThroughNodes(coors[i - 1], coors[i]);
//				非‘T’表示可以正向
				if (oneway != 'T') {
					for (int t = 1; t < passThroughNodes.size(); ++t) {
						addEdge(getIndex(passThroughNodes.get(t - 1)), getIndex(passThroughNodes.get(t)));
					}
				}
//				非‘F’表示可以逆向
				if (oneway != 'F') {
					for (int t = 1; t < passThroughNodes.size(); ++t) {
						addEdge(getIndex(passThroughNodes.get(t)), getIndex(passThroughNodes.get(t - 1)));
					}
				}
			}
		}
	}

//  添加一条边
	protected void addEdge(int startIndex, int endIndex) {
		edges.get(startIndex).add(endIndex);
	}

//  删除一条边
	protected void removeEdge(int startIndex, int endIndex) {
		edges.get(startIndex).remove(endIndex);
	}

//	返回a到b上经过的所有节点，返回的列表是有序的，按照a到b依次经过的顺序
	protected List<Coordinate> passThroughNodes(Coordinate a, Coordinate b) {
		// 首先计算a与b的大小关系
		final int xcompare;
		if (a.x >= b.x)
			xcompare = -1;
		else
			xcompare = 1;
		final int ycompare;
		if (a.y >= b.y)
			ycompare = -1;
		else
			ycompare = 1;
//		定义比较器
		Comparator<Coordinate> comparator = new Comparator<Coordinate>() {
			@Override
			public int compare(Coordinate e1, Coordinate e2) {
				if (e1.x > e2.x)
					return xcompare;
				else if (e1.x < e2.x)
					return -xcompare;
				else if (e1.y > e2.y)
					return ycompare;
				else if (e1.y < e2.y)
					return -ycompare;
				return 0;
			}
		};
		Queue<Coordinate> que = new PriorityQueue<Coordinate>(comparator);
//		将所有共线顶点添加到队列
		que.add(a);
		for (Coordinate c : nodesList) {
			if (GraphTools.threePointCollinearity(a, b, c)) {
				if (!c.equals(a) && !c.equals(b))
					que.add(c);
			}
		}
		que.add(b);
//		转为list
		return GraphTools.cutQueueAndBuildList(a, b, que);
	}

	@Override
	public boolean cutAndAdd(Coordinate node1, Coordinate node2, Coordinate newNode) {
		if (!GraphTools.threePointCollinearity(node1, node2, newNode))
			return false;
		addNode(newNode);
		Integer indexNew = getIndex(newNode);
		List<Coordinate> list = passThroughNodes(node1, node2);
		Coordinate[] nearestNodes = getNearestNode(list, newNode);
		int index1 = getIndex(nearestNodes[0]);
		int index2 = getIndex(nearestNodes[1]);
		if (directConnected(index1, index2)) {
			removeEdge(index1, index2);
			addEdge(index1, indexNew);
			addEdge(indexNew, index2);
		}
		if (directConnected(index2, index1)) {
			removeEdge(index2, index1);
			addEdge(index2, indexNew);
			addEdge(indexNew, index1);
		}
		return true;
	}

	@Override
	public boolean repareCut(Coordinate node1, Coordinate node2, Coordinate newNode) {
		if (!GraphTools.threePointCollinearity(node1, node2, newNode))
			return false;
		Integer indexNew = getIndex(newNode);
		List<Coordinate> list = passThroughNodes(node1, node2);
		Coordinate[] nearestNodes = getNearestNode(list, newNode);
		int index1 = getIndex(nearestNodes[0]);
		int index2 = getIndex(nearestNodes[1]);
		if (directConnected(index1, indexNew)) {
			addEdge(index1, index2);
			removeEdge(index1, indexNew);
		}
		if (directConnected(index2, indexNew)) {
			addEdge(index2, index1);
			removeEdge(index2, indexNew);
		}
		removeLastNode();
		return true;
	}

	private Coordinate[] getNearestNode(List<Coordinate> list, Coordinate coor) {
		Coordinate[] ans = new Coordinate[2];
		for (int i = 1; i < list.size() - 1; ++i) {
			if (list.get(i).equals(coor)) {
				ans[0] = list.get(i - 1);
				ans[1] = list.get(i + 1);
				return ans;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder strbuilder = new StringBuilder();
		strbuilder.append("------------------------\n");
		for (int i = 0; i < nodesList.size(); ++i) {
			strbuilder.append("index: " + i + " coor: " + nodesList.get(i) + "\n");
			Set<Integer> e = edges.get(i);
			strbuilder.append("Connected: ");
			for (Integer ed : e) {
				strbuilder.append(" " + ed);
			}
			strbuilder.append("\n");
		}
		strbuilder.append("------------------------\n");
		return strbuilder.toString();
	}

	/*
	 * 图访问
	 */
	@Override
	public Integer getIndex(Coordinate c) {
		return nodesMap.get(c);
	}

	@Override
	public Coordinate getNode(int index) {
		if (index >= nodesList.size())
			return null;
		return nodesList.get(index);
	}

	@Override
	public int getNodeNum() {
		return nodesList.size();
	}

	@Override
	public boolean directConnected(Coordinate startNode, Coordinate endNode) {
		Integer startIndex = getIndex(startNode);
		Integer endIndex = getIndex(endNode);
		return directConnected(startIndex, endIndex);
	}

	@Override
	public boolean directConnected(Integer startIndex, Integer endIndex) {
		if (startIndex >= nodesList.size() || endIndex >= nodesList.size())
			return false;
		return edges.get(startIndex).contains(endIndex);
	}
}
