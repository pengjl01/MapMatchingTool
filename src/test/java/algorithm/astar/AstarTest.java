package algorithm.astar;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import algorithm.pathfinder.astar.Astar;
import entity.graph.AdjacencyGraphTest;
import entity.graph.Graph;

/*
 * @author pjl
 * @version 创建时间：2019年6月27日 下午3:05:15
 * 类说明
 */
public class AstarTest extends Astar {
	@Test
	public void AstarTestNormal() throws Exception {
		Graph graph = AdjacencyGraphTest.buildSampleAdjacencyGraph();
		Coordinate start = new Coordinate(1, 7);
		Coordinate end = new Coordinate(6, 5);
		Astar astar = new Astar(graph, start, end);
		List<Coordinate> routeTrue = new ArrayList<Coordinate>();
		routeTrue.add(new Coordinate(1, 7));
		routeTrue.add(new Coordinate(1, 6));
		routeTrue.add(new Coordinate(3, 5));
		routeTrue.add(new Coordinate(6, 5));
		List<Coordinate> route = astar.findCoordinatePath();
		assertEquals("two routes should have same length", route.size(), routeTrue.size());
		for (int i = 0; i < route.size(); ++i) {
			assertEquals("every node should be the same", route.get(i), routeTrue.get(i));
		}
	}

	@Test
	public void AstarAddEdgeTest() throws Exception {
		Graph graph = AdjacencyGraphTest.buildSampleAdjacencyGraph();
		Coordinate node1 = new Coordinate(1, 7);
		Coordinate node2 = new Coordinate(6, 7);
		Coordinate newNode = new Coordinate(5, 7);
		graph.cutAndAdd(node1, node2, newNode);
		Coordinate node11 = new Coordinate(3, 5);
		Coordinate node21 = new Coordinate(1, 6);
		Coordinate newNode1 = new Coordinate(2, 5.5);
		graph.cutAndAdd(node11, node21, newNode1);
		Astar astar = new Astar(graph, newNode, newNode1);
		List<Coordinate> routeTrue = new ArrayList<Coordinate>();
		routeTrue.add(new Coordinate(5, 7));
		routeTrue.add(new Coordinate(1, 7));
		routeTrue.add(new Coordinate(1, 6));
		routeTrue.add(new Coordinate(2, 5.5));
		List<Coordinate> route = astar.findCoordinatePath();
		assertEquals("two routes should have same length", route.size(), routeTrue.size());
		for (int i = 0; i < route.size(); ++i) {
			assertEquals("every node should be the same", route.get(i), routeTrue.get(i));
		}
	}

	@Test
	public void AstarDelEdgeTest() throws Exception {
		Graph graph = AdjacencyGraphTest.buildSampleAdjacencyGraph();
		String origin = graph.toString();
		Coordinate node1 = new Coordinate(1, 7);
		Coordinate node2 = new Coordinate(6, 7);
		Coordinate newNode = new Coordinate(5, 7);
		graph.cutAndAdd(node1, node2, newNode);
		Coordinate node11 = new Coordinate(3, 5);
		Coordinate node21 = new Coordinate(1, 6);
		Coordinate newNode1 = new Coordinate(2, 5.5);
		graph.cutAndAdd(node11, node21, newNode1);
		graph.repareCut(node11, node21, newNode1);
		graph.repareCut(node1, node2, newNode);
		String after = graph.toString();
		assertEquals("删掉加入的节点后图应该和之前一样", origin, after);
	}

//	public static void main(String[] args) {
//	}
}
