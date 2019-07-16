package app;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.locationtech.jts.index.SpatialIndex;

import algorithm.mapmatching.fwmm.WMM;
import algorithm.pso.PSO;
import app.tools.APPTools;
import app.tools.DiffTools;
import constants.Constants;
import data.datareader.SHPReader;

/*
 * @author pjl
 * @version 创建时间：2019年7月15日 下午2:37:13
 * 类说明
 */
public class PSOAPP {
	static boolean trans = true;
	static String FILE = "13321174830_00004";

	static void func1() throws IOException {
		SimpleFeatureCollection roadCollection = SHPReader.readSHP(new File(Constants.ROADFILE), trans);
		SpatialIndex index = APPTools.buildSTRTree(roadCollection);
		WMM m = new WMM(index);
//		Matcher m = new FWMM(index, debug);
		String inputSHP = Constants.INPUTPATH + "myshpdata\\" + FILE + ".shp";
		SimpleFeatureCollection pointOrigin = SHPReader.readSHP(new File(inputSHP), trans);
		List<String> truth = DiffTools.file2List(DiffTools.truthFileName(Constants.RESULTPATH, FILE));
		PSO pso = new PSO(m, pointOrigin, truth, true);
		pso.start();
	}

	public static void main(String[] args) throws IOException {
		func1();
	}
}
