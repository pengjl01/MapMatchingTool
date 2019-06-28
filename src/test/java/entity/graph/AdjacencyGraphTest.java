package entity.graph;

import org.geotools.geometry.jts.JTSFactoryFinder;
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
	public static Graph buildSampleAdjacencyGraph() {
		WKTReader reader = new WKTReader(JTSFactoryFinder.getGeometryFactory());
		try {
			MultiLineString geom1 = (MultiLineString) reader.read("MULTILINESTRING ((1 1, 1 9))");
			MultiLineString geom2 = (MultiLineString) reader.read("MULTILINESTRING ((1 7, 6 7, 6 5, 1 5))");
			MultiLineString geom3 = (MultiLineString) reader.read("MULTILINESTRING ((1 6, 3 5))");
			Graph graph = new AdjacencyGraph();
			graph.addNodes(geom1);
			graph.addNodes(geom2);
			graph.addNodes(geom3);
			graph.addEdges(geom1, 'B');
			graph.addEdges(geom2, 'B');
			graph.addEdges(geom3, 'T');
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
		graph.showGraph();
		graph.cutAndAdd(node1, node2, newNode);
		graph.showGraph();
		graph.repareCut(node1, node2, newNode);
		graph.showGraph();

	}
}
