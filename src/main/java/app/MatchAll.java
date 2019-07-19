package app;

import java.io.File;
import java.io.IOException;
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
import constants.Constants;
import data.datareader.SHPReader;
import utils.output.OutputWriter;

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
		matchers.add(new SimpleDistance(index));
		matchers.add(new STMM(index));
		matchers.add(new HMM(index));
		matchers.add(new STDM(index));
		matchers.add(new WMM(index));
		matchers.add(new FWMM(index));
		OutputWriter ow;
		try {
			ow = new OutputWriter(Constants.DATA_HOME_PATH + "MatchAllResult.txt");
			File[] inputFiles = new File(Constants.SHP_INPUT_PATH).listFiles();
			for (File file : inputFiles) {
				if (file.isFile() && file.getName().endsWith(".shp")) {
					String name = file.getName().replaceAll("[.][^.]+$", "");
					ow.write(file.getName() + ":");
					System.out.println(file + " start");
					for (Matcher m : matchers) {
						String type = m.getClass().getSimpleName();
						String outputTXT = Constants.TXT_RESULT_PATH + name + "_" + type + ".txt";
						String outputSHP = Constants.SHP_OUTPUT_PATH + name + "_" + type + ".shp";
						APPTools.doMatch(file, outputTXT, outputSHP, m, index, ow);
					}
				}
			}
			ow.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		APPTools.doMatch(inputSHP, outputTXT, outputSHP, m, index);
//		这里优化不？
//		VisualTools.show(inputSHP, outputSHP, roadCollection);
//			matchDiff有问题，目前不可用
//			SimpleFeatureCollection pointDiff = Matcher.matchDiff(pointOrigin, pointMatched);
//			Tools.addLines(map, pointDiff, Color.green);

	}
}
