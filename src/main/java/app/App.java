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
import algorithm.mapmatching.simpledistance.SimpleDistance;
import algorithm.mapmatching.stdm.STDM;
import algorithm.mapmatching.stmm.STMM;
import app.tools.APPTools;
import app.tools.DiffTools;
import app.tools.VisualTools;
import constants.Constants;
import data.datareader.SHPReader;

/**
 * Hello world!
 *
 */
public class App {
	static boolean trans = true;

	public static void main(String[] args) throws Exception {
		func3();
	}

	/*
	 * 一次匹配一种并展示
	 */
	static void func1() {
		// display a data store file chooser dialog for shapefiles
//      File file = JFileDataStoreChooser.showOpenFile("shp", null);
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(Constants.ROADFILE), trans);
		SpatialIndex index = APPTools.buildSTRTree(roadCollection);
		boolean debug = true;
//		Matcher m = new SimpleDistance(index);
//		Matcher m = new HMM(index, debug);
//		Matcher m = new WMM(index, debug);
		Matcher m = new FWMM(index, debug);
//		Matcher m = new STMM(index, debug);
//		Matcher m = new STDM(index, debug);
		String type = m.getClass().getSimpleName();
		String FILE = "13321174830_00003_1";
		String outputTXT = Constants.TXT_RESULT_PATH + FILE + "_" + type + ".txt";
		String outputSHP = Constants.SHP_OUTPUT_PATH + FILE + "_" + type + ".shp";
		String inputSHP = Constants.SHP_INPUT_PATH + FILE + ".shp";
		APPTools.doMatch(new File(inputSHP), outputTXT, outputSHP, m, index, null);
		DiffTools.printAcc(FILE, type);
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
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(Constants.ROADFILE), trans);
		SpatialIndex index = APPTools.buildSTRTree(roadCollection);
		// display a data store file chooser dialog for shapefiles
//      File file = JFileDataStoreChooser.showOpenFile("shp", null);
		List<Matcher> matchers = new ArrayList<Matcher>();
		matchers.add(new STMM(index));
		matchers.add(new SimpleDistance(index));
		matchers.add(new HMM(index));
		matchers.add(new WMM(index));
		matchers.add(new FWMM(index));
		matchers.add(new STDM(index));
		String FILE = "13321174830_00003_1";
		for (Matcher m : matchers) {
			String type = m.getClass().getSimpleName();
			String outputTXT = Constants.TXT_RESULT_PATH + FILE + "_" + type + ".txt";
			String outputSHP = Constants.SHP_OUTPUT_PATH + FILE + "_" + type + ".shp";
			String inputSHP = Constants.SHP_INPUT_PATH + FILE + ".shp";
			APPTools.doMatch(new File(inputSHP), outputTXT, outputSHP, m, index, null);
//			VisualTools.show(inputSHP, outputSHP, roadCollection);
		}
		DiffTools.printAcc(FILE);
//		APPTools.doMatch(inputSHP, outputTXT, outputSHP, m, index);
//		这里优化不？
//		VisualTools.show(inputSHP, outputSHP, roadCollection);
//			matchDiff有问题，目前不可用
//			SimpleFeatureCollection pointDiff = Matcher.matchDiff(pointOrigin, pointMatched);
//			Tools.addLines(map, pointDiff, Color.green);
		System.out.println("-----------------------------------------------");
	}

	/*
	 * 仅展示，单线
	 */
	static void func3() {
		String file1 = "13321174830_00003_1";
//		String file2 = "13321164368_00044";
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(Constants.ROADFILE), trans);
		String inputSHP = Constants.SHP_INPUT_PATH + file1 + ".shp";
//		String inputSHP2 = PATH + "myshpdata\\" + file2 + ".shp";
		VisualTools.show(inputSHP, roadCollection);
	}

	/*
	 * 仅展示，对照原始数据
	 */
	static void func4() {
		String file1 = "13331194450_00003";
		String type = "WMM";
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(Constants.ROADFILE), trans);
		String inputSHP = Constants.SHP_INPUT_PATH + file1 + ".shp";
		String outputSHP = Constants.SHP_OUTPUT_PATH + file1 + "_" + type + ".shp";
		VisualTools.show(inputSHP, outputSHP, roadCollection);
	}
}
