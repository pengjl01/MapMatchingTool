package app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.locationtech.jts.index.SpatialIndex;

import algorithm.mapmatching.Matcher;
import algorithm.mapmatching.fwmm.FWMM;
import algorithm.mapmatching.fwmm.WMM;
import app.tools.APPTools;
import app.tools.DiffTools;
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
		func1();
	}

	/*
	 * 一次匹配一种并展示
	 */
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

	/*
	 * 一次匹配多种并计算精确度
	 */
	static void func2() {
		System.out.println("n1:" + WMM.n1 + ";n2:" + WMM.n2 + ";n3:" + WMM.n3);
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(ROADFILE), trans);
		SpatialIndex index = APPTools.buildSTRTree(roadCollection);
		// display a data store file chooser dialog for shapefiles
//      File file = JFileDataStoreChooser.showOpenFile("shp", null);
		List<Matcher> matchers = new ArrayList<Matcher>();
		matchers.add(new FWMM(index));
		matchers.add(new WMM(index));
//		matchers.add(new HMM(index));
//		matchers.add(new SimpleDistance(index));
		String FILE = "13321174830_00004";
		for (Matcher m : matchers) {
			String type = m.getClass().getSimpleName();
			String outputTXT = PATH + "myresultTXT\\" + FILE + "_" + type + ".txt";
			String outputSHP = PATH + "myresultSHP\\" + FILE + "_" + type + ".shp";
			String inputSHP = PATH + "myshpdata\\" + FILE + ".shp";
			APPTools.doMatch(new File(inputSHP), outputTXT, outputSHP, m, index);
//			VisualTools.show(inputSHP, outputSHP, roadCollection);
		}
		calcAcc(FILE);
//		APPTools.doMatch(inputSHP, outputTXT, outputSHP, m, index);
//		这里优化不？
//		VisualTools.show(inputSHP, outputSHP, roadCollection);
//			matchDiff有问题，目前不可用
//			SimpleFeatureCollection pointDiff = Matcher.matchDiff(pointOrigin, pointMatched);
//			Tools.addLines(map, pointDiff, Color.green);
		System.out.println("-----------------------------------------------");
	}

	static void func2_test() {
		WMM.n1 = 1.9;
		WMM.n2 = 1.3;
		WMM.n3 = 0.7;
		func2();
		WMM.n1 = 2.0;
		WMM.n2 = 1.4;
		WMM.n3 = 0.7;
		func2();
		WMM.n1 = 2.0;
		WMM.n2 = 1.2;
		WMM.n3 = 0.7;
		func2();
		WMM.n1 = 2.0;
		WMM.n2 = 1.3;
		WMM.n3 = 0.8;
		func2();
		WMM.n1 = 2.0;
		WMM.n2 = 1.3;
		WMM.n3 = 0.6;
		func2();
	}

	/*
	 * 仅展示，单线
	 */
	static void func3() {
		String file1 = "13321174830_00003";
//		String file2 = "13321164368_00044";
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(ROADFILE), trans);
		String inputSHP = PATH + "myshpdata\\" + file1 + ".shp";
//		String inputSHP2 = PATH + "myshpdata\\" + file2 + ".shp";
		VisualTools.show(inputSHP, roadCollection);
	}

	/*
	 * 仅展示，对照原始数据
	 */
	static void func4() {
		String file1 = "13321174881_00003";
		String type = "WMM";
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(ROADFILE), trans);
		String inputSHP = PATH + "myshpdata\\" + file1 + ".shp";
		String outputSHP = PATH + "myresultSHP\\" + file1 + "_" + type + ".shp";
		VisualTools.show(inputSHP, outputSHP, roadCollection);
	}

	static void calcAcc(String FILE) {
		// D:\study\研究生\毕业论文\data\data_来自es\myresult
		String path = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresultTXT\\";
		List<String> types = new ArrayList<String>();
//		types.add("TRUTH");
		types.add("FWMM");
		types.add("WMM");
		types.add("HMM");
		types.add("SimpleDistance");
//		DiffTools.makeDiff(path + trace, types);
		DiffTools.calcAccuracy(path, FILE, types);

	}
}
