package algorithm.mapmatching.fwmm;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.linearref.LocationIndexedLine;

/*
 * @author pjl
 * @version 创建时间：2019年7月1日 下午9:18:26
 * 类说明
 */
public class ToolsTest extends Tools {
//	点在道路中间，最普适的状况
	@Test
	public void getRoadDirectionMiddleOfRoadTest() throws Exception {
		LocationIndexedLine line = buildTestLine();
		Coordinate c1 = new Coordinate(1, 2);
		List<Double> test = getRoadDirection(line, c1, 'F');
		assertEquals("只有一个方向值", 1, test.size());
		assertEquals("正向方向为0", (Double) 0.0, test.get(0));
		Coordinate c2 = new Coordinate(2, 3);
		List<Double> test2 = getRoadDirection(line, c2, 'T');
		assertEquals("只有一个方向值", 1, test2.size());
		assertEquals("逆向方向为pi*3/2(即-1/2*pi)", (Double) (-Math.PI * 1 / 2), test2.get(0));
		Coordinate c3 = new Coordinate(2, 3);
		List<Double> test3 = getRoadDirection(line, c3, 'B');
		assertEquals("2个方向值", 2, test3.size());
		assertEquals("正向方向为pi*3/2(即-1/2*pi)", (Double) (Math.PI * 1 / 2), test3.get(0));
		assertEquals("逆向方向为pi*3/2(即-1/2*pi)", (Double) (-Math.PI * 1 / 2), test3.get(1));
	}

//  点是道路第一个节点
	@Test
	public void getRoadDirectionStartNodeTest() throws Exception {
		LocationIndexedLine line = buildTestLine();
		Coordinate c1 = new Coordinate(1, 1);
		List<Double> test = getRoadDirection(line, c1, 'B');
		assertEquals("2个方向值", 2, test.size());
		assertEquals("正向方向为0", (Double) 0.0, test.get(0));
		assertEquals("反向方向为pi", (Double) Math.PI, test.get(1));
	}

//  点是道路中间节点
	@Test
	public void getRoadDirectionMiddleNodeTest() throws Exception {
		LocationIndexedLine line = buildTestLine();
		Coordinate c1 = new Coordinate(1, 3);
		List<Double> test = getRoadDirection(line, c1, 'B');
		assertEquals("4个方向值", 4, test.size());
		assertEquals("正向方向为pi*3/2(即-1/2*pi)", (Double) (Math.PI * 1 / 2), test.get(0));
		assertEquals("逆向方向为pi*3/2(即-1/2*pi)", (Double) (-Math.PI * 1 / 2), test.get(1));
		assertEquals("正向方向为0", (Double) 0.0, test.get(2));
		assertEquals("反向方向为pi", (Double) Math.PI, test.get(3));
	}

//  点是道路最后一个节点
	@Test
	public void getRoadDirectionEndNodeTest() throws Exception {
		LocationIndexedLine line = buildTestLine();
		Coordinate c1 = new Coordinate(3, 3);
		List<Double> test = getRoadDirection(line, c1, 'B');
		assertEquals("2个方向值", 2, test.size());
		assertEquals("正向方向为pi*3/2(即-1/2*pi)", (Double) (Math.PI * 1 / 2), test.get(0));
		assertEquals("逆向方向为pi*3/2(即-1/2*pi)", (Double) (-Math.PI * 1 / 2), test.get(1));
	}

	LocationIndexedLine buildTestLine() {
		WKTReader reader = new WKTReader(JTSFactoryFinder.getGeometryFactory());
		try {
			LocationIndexedLine line = new LocationIndexedLine(reader.read("MULTILINESTRING ((1 1, 1 3, 3 3))"));
			return line;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {

	}
}
