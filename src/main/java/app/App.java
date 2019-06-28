package app;

import java.awt.Color;
import java.io.File;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.map.MapContent;
import org.geotools.swing.JMapFrame;
import org.locationtech.jts.index.SpatialIndex;

import algorithm.mapmatching.Matcher;
import algorithm.mapmatching.hmm.HMM;
import app.visual.VisualTools;
import data.datareader.SHPReader;
import data.tools.DataTools;

/**
 * Hello world!
 *
 */
public class App {
	static String TITLE = "my-mapmatching";
//	D:\study\研究生\毕业论文\data\map\myosm
//	static String ROADFILE = "D:\\study\\研究生\\毕业论文\\data\\map\\myosm\\bj_small.shp";
	static String ROADFILE = "D:\\study\\研究生\\毕业论文\\data\\map\\bj2011\\myshp\\road_network.shp";
//	D:\study\研究生\毕业论文\data\data_来自es\myshpdata
	static String PATH = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myshpdata\\";
	static String RESULTPATH = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresult\\";
	static boolean trans = true;

	public static void main(String[] args) throws Exception {
		// display a data store file chooser dialog for shapefiles
//        File file = JFileDataStoreChooser.showOpenFile("shp", null);
		MapContent map = new MapContent();
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(ROADFILE), trans);
		SpatialIndex index = DataTools.buildSTRTree(roadCollection);
		boolean debug = true;
//		Matcher m = new SimpleDistance(index);
		Matcher m = new HMM(index, debug);
//		Matcher m = new MyMatcher(index, debug);
//		Matcher m = new MyMatcherFast(index, debug);
		String FILE = "13321164368_00036";
		String POINTFILE = PATH + FILE + ".shp";
		String RESULT = RESULTPATH + FILE + "_result_temp.txt";
		SimpleFeatureCollection pointOrigin = SHPReader.readSHP(new File(POINTFILE), trans);

		long start = System.currentTimeMillis();
		SimpleFeatureCollection pointMatched = m.match(pointOrigin, 0, 20);
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

		VisualTools.saveResult(pointMatched, RESULT);
	}
}
