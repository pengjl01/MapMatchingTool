package app;

import java.io.File;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.locationtech.jts.index.SpatialIndex;

import algorithm.mapmatching.fwmm.WMM;
import algorithm.pso.PSO;
import app.tools.APPTools;
import app.tools.DiffTools;
import data.datareader.SHPReader;

/*
 * @author pjl
 * @version 创建时间：2019年7月15日 下午2:37:13
 * 类说明
 */
public class PSOAPP {
//	D:\study\研究生\毕业论文\data\map\myosm
	static String ROADFILE = "C:\\study\\myosm\\bj_small.shp";
//	static String ROADFILE = "D:\\study\\研究生\\毕业论文\\data\\map\\bj2011\\myshp\\road_network.shp";
//	D:\study\研究生\毕业论文\data\data_来自es\myshpdata
	static String PATH = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\";
	static String TRUTHPATH = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresultTXT\\";
	static boolean trans = true;
	static String FILE = "13321174830_00004";

	static void func1() {
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(ROADFILE), trans);
		SpatialIndex index = APPTools.buildSTRTree(roadCollection);
		WMM m = new WMM(index);
//		Matcher m = new FWMM(index, debug);
		String inputSHP = PATH + "myshpdata\\" + FILE + ".shp";
		SimpleFeatureCollection pointOrigin = SHPReader.readSHP(new File(inputSHP), trans);
		List<String> truth = DiffTools.file2List(DiffTools.truthFileName(TRUTHPATH, FILE));
		PSO pso = new PSO(m, pointOrigin, truth, true);
		pso.start();
	}

	public static void main(String[] args) {
		func1();
	}
}
