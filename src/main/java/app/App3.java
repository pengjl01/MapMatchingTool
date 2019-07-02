package app;

import java.awt.Color;
import java.io.File;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.map.MapContent;
import org.geotools.swing.JMapFrame;
import org.locationtech.jts.index.SpatialIndex;

import algorithm.mapmatching.Matcher;
import algorithm.mapmatching.fwmm.FWMM;
import app.visual.VisualTools;
import data.datareader.SHPReader;
import data.tools.DataTools;

/**
 * Hello world!
 *
 */
public class App3 {
	static String TITLE = "my-mapmatching";
	static String ROADFILE = "D:\\study\\研究生\\毕业论文\\小论文\\mapmatching\\testdata\\shpdata\\road_network.shp";
	static String POINTFILE = "D:\\study\\研究生\\毕业论文\\小论文\\mapmatching\\testdata\\shpdata\\gps_data.shp";
	static String RESULT = "D:\\study\\研究生\\毕业论文\\小论文\\mapmatching\\testdata\\result.txt";
	static boolean trans = false;

	public static void main(String[] args) throws Exception {
		// display a data store file chooser dialog for shapefiles
//        File file = JFileDataStoreChooser.showOpenFile("shp", null);
		MapContent map = new MapContent();
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(ROADFILE), trans);
		SpatialIndex index = DataTools.buildSTRTree(roadCollection);
		boolean debug = false;
//		Matcher m = new SimpleDistance(index);
//		Matcher m = new HMM(index, debug);
		Matcher m = new FWMM(index, debug);
//		Matcher m = new FWMMFast(index, debug);

		SimpleFeatureCollection pointOrigin = SHPReader.readSHP(new File(POINTFILE), trans);
		long start = System.currentTimeMillis();
		SimpleFeatureCollection pointMatched = m.match(pointOrigin, 0, -100);
		System.out.println("轨迹共" + pointMatched.size() + "个点");
		System.out.println("匹配耗时" + (System.currentTimeMillis() - start) / 1000 + "s");
//			matchDiff有问题，目前不可用
//			SimpleFeatureCollection pointDiff = Matcher.matchDiff(pointOrigin, pointMatched);
//			Tools.addLines(map, pointDiff, Color.green);
		VisualTools.addLines(map, VisualTools.point2Route(pointOrigin, index), Color.blue);
		VisualTools.addLines(map, VisualTools.point2Line(pointMatched), Color.red);
		VisualTools.addPoints(map, pointOrigin, Color.blue);
		VisualTools.addPoints(map, pointMatched, Color.red);
//			++i;
//			if (i >= 100) {
//				j += i;
//				i = 0;
//				System.out.println(j + " files added");
//				break;
//			}
		VisualTools.addRoad(map, roadCollection);
		JMapFrame.showMap(map);
	}
}
