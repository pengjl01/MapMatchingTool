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
import app.tools.APPTools;
import constants.Constants;
import data.datareader.SHPReader;

/**
 * Hello world!
 *
 */
public class MatchAll {
	static boolean trans = true;
//	static String FILE = "13321164368_00036";

	public static void main(String[] args) throws Exception {
		func1();
	}

	/*
	 * 一次使用多种算法，匹配全部数据获得结果
	 */
	static void func1() {
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(Constants.ROADFILE), trans);
		SpatialIndex index = APPTools.buildSTRTree(roadCollection);
		// display a data store file chooser dialog for shapefiles
//      File file = JFileDataStoreChooser.showOpenFile("shp", null);
		List<Matcher> matchers = new ArrayList<Matcher>();
		matchers.add(new FWMM(index));
		matchers.add(new WMM(index));
		matchers.add(new HMM(index));
		matchers.add(new SimpleDistance(index));
		File[] inputFiles = new File(Constants.INPUTPATH + "myshpdata\\").listFiles();
		for (File file : inputFiles) {
			if (file.isFile() && file.getName().endsWith(".shp")) {
				String name = file.getName().replaceAll("[.][^.]+$", "");
				System.out.println(file + " start");
				for (Matcher m : matchers) {
					String type = m.getClass().getSimpleName();
					String outputTXT = Constants.INPUTPATH + "myresultTXT\\" + name + "_" + type + ".txt";
					String outputSHP = Constants.INPUTPATH + "myresultSHP\\" + name + "_" + type + ".shp";
					APPTools.doMatch(file, outputTXT, outputSHP, m, index);
				}
			}
		}
//		APPTools.doMatch(inputSHP, outputTXT, outputSHP, m, index);
//		这里优化不？
//		VisualTools.show(inputSHP, outputSHP, roadCollection);
//			matchDiff有问题，目前不可用
//			SimpleFeatureCollection pointDiff = Matcher.matchDiff(pointOrigin, pointMatched);
//			Tools.addLines(map, pointDiff, Color.green);

	}
}
