package app;

import java.io.File;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;

import constants.Constants;
import data.datareader.SHPReader;
import data.simplefeature.pointfeature.BJTaxiData;
import data.simplefeature.pointfeature.PointFeature;

/*
 * @author pjl
 * @version 创建时间：2019年7月16日 上午10:24:02
 * 从shp文件读取数据，统计采样间隔
 */
public class TimeStatistics {
	static int max = 120;

	/*
	 * 含0统计
	 */
	public static void func1() {
		int delta = 1;
		int[] time = new int[(max / delta)];
		File[] inputFiles = new File(Constants.SHP_INPUT_PATH).listFiles();
		for (File file : inputFiles) {
			if (file.isFile() && file.getName().endsWith(".shp")) {
				Long pretime = null;
				SimpleFeatureCollection pointOrigin = SHPReader.readSHP(file, false);
				SimpleFeatureIterator i = pointOrigin.features();
				while (i.hasNext()) {
					PointFeature pf = new BJTaxiData(i.next());
					Long timeLong = pf.getTime();
					if (pretime != null) {
						int index = (int) ((timeLong - pretime) / (delta * 1000));
						if (index == time.length)
							index = time.length - 1;
						time[index] += 1;
					}
					pretime = timeLong;
				}
			}
		}
		for (int i = 0; i < time.length; ++i) {
//			System.out.println(i + 1 + ":" + time[i]);
			System.out.println(time[i]);
		}
	}

	/*
	 * 
	 * 统计总数
	 */
	public static void func2() {
		int total = 0;
//		File[] inputFiles = new File(Constants.INPUTPATH + "myshpdata\\").listFiles();
		File[] inputFiles = new File("D:\\study\\研究生\\毕业论文\\data\\map\\myosm").listFiles();
		for (File file : inputFiles) {
			if (file.isFile() && file.getName().endsWith(".shp")) {
				SimpleFeatureCollection pointOrigin = SHPReader.readSHP(file, false);
				SimpleFeatureIterator i = pointOrigin.features();
				while (i.hasNext()) {
					i.next();
					total++;
				}
			}
		}
		System.out.println("total:" + total);
	}

	/*
	 * 不含0统计
	 */
	public static void func3() {
		int delta = 5;
		int[] time = new int[(max / delta)];
		File[] inputFiles = new File(Constants.SHP_INPUT_PATH).listFiles();
		for (File file : inputFiles) {
			if (file.isFile() && file.getName().endsWith(".shp")) {
				Long pretime = null;
				SimpleFeatureCollection pointOrigin = SHPReader.readSHP(file, false);
				SimpleFeatureIterator i = pointOrigin.features();
				while (i.hasNext()) {
					PointFeature pf = new BJTaxiData(i.next());
					Long timeLong = pf.getTime();
					if (pretime != null) {
						long deltaTime = timeLong - pretime;
						if (deltaTime != 0) {
							int index = (int) ((deltaTime - 1) / (delta * 1000));
							if (index == time.length)
								index = time.length - 1;
							time[index] += 1;
						}

					}
					pretime = timeLong;
				}
			}
		}
		for (int i = 0; i < time.length; ++i) {
//			System.out.println(i + 1 + ":" + time[i]);
			System.out.println(time[i]);
		}
	}

	public static void main(String[] args) {
		func2();
	}
}
