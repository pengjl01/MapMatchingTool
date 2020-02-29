package entity.graph;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/*
 * @author pjl
 * @version 创建时间：2019年6月26日 下午8:39:43
 * 类说明
 */
public class AdjacencyGraphTest extends AdjacencyGraph {
	@Test
	public void passThroughNodesTest() throws Exception {
		AdjacencyGraph graph = buildSampleAdjacencyGraph();
		Coordinate a = new Coordinate(1, 8);
		Coordinate b = new Coordinate(1, 3);
		List<Coordinate> coorList = graph.passThroughNodes(a, b);
		assertEquals("size=5", coorList.size(), 5);
		assertEquals("(1,8)", coorList.get(0), a);
		assertEquals("(1,7)", coorList.get(1), new Coordinate(1, 7));
		assertEquals("(1,6)", coorList.get(2), new Coordinate(1, 6));
		assertEquals("(1,5)", coorList.get(3), new Coordinate(1, 5));
		assertEquals("(1,3)", coorList.get(4), b);
		a = new Coordinate(4, 5);
		b = new Coordinate(2, 5);
		coorList = graph.passThroughNodes(b, a);
		assertEquals("size=3", coorList.size(), 3);
		assertEquals("(2,5)", coorList.get(0), b);
		assertEquals("(3,5)", coorList.get(1), new Coordinate(3, 5));
		assertEquals("(4,5)", coorList.get(2), a);
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

	public static AdjacencyGraph buildSampleAdjacencyGraph() {
		WKTReader reader = new WKTReader(JTSFactoryFinder.getGeometryFactory());
		try {
			MultiLineString geom1 = (MultiLineString) reader.read("MULTILINESTRING ((1 1, 1 9))");
			MultiLineString geom2 = (MultiLineString) reader.read("MULTILINESTRING ((1 7, 6 7, 6 5, 1 5))");
			MultiLineString geom3 = (MultiLineString) reader.read("MULTILINESTRING ((1 6, 3 5))");
			AdjacencyGraph graph = new AdjacencyGraph();
			graph.addNodes(geom1);
			graph.addNodes(geom2);
			graph.addNodes(geom3);
			graph.addEdges(geom1, 'B');
			graph.addEdges(geom2, 'B');
			graph.addEdges(geom3, 'F');
			return graph;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		Graph graph = buildSampleAdjacencyGraph();
		Coordinate node1 = new Coordinate(1, 7);
		Coordinate node2 = new Coordinate(6, 7);
		Coordinate newNode = new Coordinate(5, 7);
		System.out.println(graph.toString());
		graph.cutAndAdd(node1, node2, newNode);
		System.out.println(graph.toString());
		graph.repareCut(node1, node2, newNode);
		System.out.println(graph.toString());

	}
}
