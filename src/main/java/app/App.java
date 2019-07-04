package app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.locationtech.jts.index.SpatialIndex;

import algorithm.mapmatching.Matcher;
import algorithm.mapmatching.fwmm.FWMM;
import algorithm.mapmatching.fwmm.WMM;
import algorithm.mapmatching.hmm.HMM;
import app.tools.APPTools;
import app.tools.VisualTools;
import data.datareader.SHPReader;

/**
 * Hello world!
 *
 */
public class App {
//	D:\study\研究生\毕业论文\data\map\myosm
	static String ROADFILE = "C:\\study\\myosm\\bj_small.shp";
//	static String ROADFILE = "D:\\study\\研究生\\毕业论文\\data\\map\\bj2011\\myshp\\road_network.shp";
//	D:\study\研究生\毕业论文\data\data_来自es\myshpdata
	static String PATH = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\";
	static boolean trans = true;

	public static void main(String[] args) throws Exception {
		func2();
	}

	static void func1() {
		// display a data store file chooser dialog for shapefiles
//      File file = JFileDataStoreChooser.showOpenFile("shp", null);
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(ROADFILE), trans);
		SpatialIndex index = APPTools.buildSTRTree(roadCollection);
		boolean debug = true;
//		Matcher m = new SimpleDistance(index);
//		Matcher m = new HMM(index, debug);
//		Matcher m = new WMM(index, debug);
		Matcher m = new FWMM(index, debug);
		String type = m.getClass().getSimpleName();
		String FILE = "13321174881_00006";
		String outputTXT = PATH + "myresultTXT\\" + FILE + "_" + type + ".txt";
		String outputSHP = PATH + "myresultSHP\\" + FILE + "_" + type + ".shp";
		String inputSHP = PATH + "myshpdata\\" + FILE + ".shp";
		APPTools.doMatch(new File(inputSHP), outputTXT, outputSHP, m, index);
//		这里优化不？
		VisualTools.show(inputSHP, outputSHP, roadCollection);
//			matchDiff有问题，目前不可用
//			SimpleFeatureCollection pointDiff = Matcher.matchDiff(pointOrigin, pointMatched);
//			Tools.addLines(map, pointDiff, Color.green);

	}

	static void func2() {
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(ROADFILE), trans);
		SpatialIndex index = APPTools.buildSTRTree(roadCollection);
		// display a data store file chooser dialog for shapefiles
//      File file = JFileDataStoreChooser.showOpenFile("shp", null);
		List<Matcher> matchers = new ArrayList<Matcher>();
		matchers.add(new FWMM(index));
		matchers.add(new WMM(index));
		matchers.add(new HMM(index));
//		matchers.add(new SimpleDistance(index));
		String FILE = "13321174881_00006";
		for (Matcher m : matchers) {
			String type = m.getClass().getSimpleName();
			String outputTXT = PATH + "myresultTXT\\" + FILE + "_" + type + ".txt";
			String outputSHP = PATH + "myresultSHP\\" + FILE + "_" + type + ".shp";
			String inputSHP = PATH + "myshpdata\\" + FILE + ".shp";
			APPTools.doMatch(new File(inputSHP), outputTXT, outputSHP, m, index);
			VisualTools.show(inputSHP, outputSHP, roadCollection);
		}

//		APPTools.doMatch(inputSHP, outputTXT, outputSHP, m, index);
//		这里优化不？
//		VisualTools.show(inputSHP, outputSHP, roadCollection);
//			matchDiff有问题，目前不可用
//			SimpleFeatureCollection pointDiff = Matcher.matchDiff(pointOrigin, pointMatched);
//			Tools.addLines(map, pointDiff, Color.green);

	}

	static void func3() {
		String file1 = "13321174881_00006";
//		String file2 = "13321164368_00044";
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(ROADFILE), trans);
		String inputSHP = PATH + "myshpdata\\" + file1 + ".shp";
//		String inputSHP2 = PATH + "myshpdata\\" + file2 + ".shp";
		VisualTools.show(inputSHP, roadCollection);
	}
}
