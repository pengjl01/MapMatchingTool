package algorithm.pathfinder.astar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;

import algorithm.pathfinder.PathFinder;
import entity.graph.Graph;

public class Astar implements PathFinder {
	Graph graph;
	Map<Integer, AstarNode> closeList;
	Queue<AstarNode> openList;
	Coordinate start;
	Coordinate end;
	Integer startIndex;
	Integer endIndex;

	protected Astar() {
		closeList = new HashMap<Integer, AstarNode>();
		openList = new PriorityQueue<AstarNode>();
	}

	public Astar(Graph graph, Coordinate start, Coordinate end) {
		this.graph = graph;
		closeList = new HashMap<Integer, AstarNode>();
		openList = new PriorityQueue<AstarNode>();
		this.end = end;
		this.start = start;
		this.startIndex = graph.getIndex(start);
		this.endIndex = graph.getIndex(end);
		solve();
	}

	public Astar(Graph graph, int startIndex, int endIndex) {
		this.graph = graph;
		closeList = new HashMap<Integer, AstarNode>();
		openList = new PriorityQueue<AstarNode>();
		this.end = graph.getNode(endIndex);
		this.start = graph.getNode(startIndex);
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		solve();
	}

	private void solve() {
		if (startIndex == null || endIndex == null)
			return;
		AstarNode temp = new AstarNode(startIndex, null, 0, start.distance(end));
		openList.add(temp);
		while (openList.size() != 0) {
			temp = openList.poll();
			if (temp.index == endIndex) {
				closeList.put(endIndex, temp);
				openList.clear();
			} else {
				if (!closeList.containsKey(temp.index)) {
					for (int i = 0; i < graph.getNodeNum(); ++i) {
						if (!closeList.containsKey(i) && graph.directConnected(temp.index, i)) {
							AstarNode temp2 = new AstarNode(i, temp, temp.G + distance(temp.index, i),
									distance(i, endIndex));
							openList.add(temp2);
						}
					}
					closeList.put(temp.index, temp);
				}
			}
		}
	}

	/*
	 * 未校验i、j是否越界
	 */
	private double distance(int i, int j) {
		return graph.getNode(i).distance(graph.getNode(j));
	}

//	public List<Node> findpath() {
//		List<Node> ans = new ArrayList<Node>();
//		Node temp = closeList.get(endIndex);
//		while (temp.parent != null) {
//			ans.add(temp);
//			temp = temp.parent;
//		}
//		ans.add(temp);
//		Collections.reverse(ans);
//		return ans;
//	}

	public List<Coordinate> findCoordinatePath() {
		List<Coordinate> ans = new ArrayList<Coordinate>();
		AstarNode temp = closeList.get(endIndex);
		if (temp == null)
			return ans;
		while (temp.parent != null) {
			ans.add(graph.getNode(temp.index));
			temp = temp.parent;
		}
		ans.add(graph.getNode(temp.index));
		Collections.reverse(ans);
		return ans;
	}

	public Double routeDistance() {
		AstarNode temp = closeList.get(endIndex);
		if (temp == null)
			return Double.POSITIVE_INFINITY;
		return temp.G;
	}
}
