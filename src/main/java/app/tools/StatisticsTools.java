package app.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;

import constants.Constants;
import data.datareader.SHPReader;
import data.simplefeature.pointfeature.BJTaxiData;
import data.simplefeature.pointfeature.PointFeature;
import data.tools.TXTWriter;

/*
 * @author pjl
 * @version 创建时间：2019年7月20日 上午10:55:35
 * 统计数据用
 */
public class StatisticsTools {
	public final static String STATISTICS_RESULT_PATH = Constants.DATA_HOME_PATH + "statistics\\";
	/*
	 * 写statistics的分段统计；跑低频数据，如果没问题，就nb了
	 * 
	 */
	static int max = 450;

	/*
	 * 含间隔0统计
	 */
	public static void spaceCount(String path, String suffix) {
		int[] time = new int[(max)];
		File[] inputFiles = new File(path).listFiles();
		int sum = 0;
		for (File file : inputFiles) {
			if (file.isFile() && file.getName().endsWith(suffix + ".shp")) {
				Long pretime = null;
				SimpleFeatureCollection pointOrigin = SHPReader.readSHP(file, false);
				SimpleFeatureIterator i = pointOrigin.features();
				while (i.hasNext()) {
					PointFeature pf = new BJTaxiData(i.next());
					Long timeLong = pf.getTime();
					if (pretime != null) {
						int index = (int) ((timeLong - pretime) / (1000));
						if (index >= time.length) {
							System.out.println("max is too small. max=" + max + " space=" + index);
						} else {
							time[index] += 1;
							sum += 1;
						}
					}
					pretime = timeLong;
				}
				i.close();
			}
		}
		try {
			TXTWriter tw = new TXTWriter(STATISTICS_RESULT_PATH + suffix);
			tw.write(sum);
			for (int i = 0; i < time.length; ++i) {
				tw.write(time[i]);
			}
			tw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 分段统计
	 */
	public static int[] countSectionNum(String file, int interval) {
		List<String> data = DiffTools.file2List(file);
		int size = data.size() / interval + 1;
		int[] ans = new int[size];
		for (int i = 0; i < size; ++i) {
			int count = 0;
			for (int j = 0; j < interval; ++j) {
				int index = i * interval + j;
				if (index >= data.size())
					break;
				count += Integer.valueOf(data.get(index));
			}
			ans[i] = count;
		}
		return ans;
	}

	/*
	 * 对目录下所有进行分段统计
	 */
	public static void countSectionNumPath(String path, String suffix, int interval) {
		File[] inputFiles = new File(path).listFiles();
		int[] sum = null;
		for (File file : inputFiles) {
			if (file.isFile() && file.getName().endsWith(suffix + ".txt")) {
				int[] temp = countSectionNum(file.getPath(), interval);
				if (sum == null) {
					sum = temp;
				} else {
					for (int i = 0; i < sum.length; ++i) {
						sum[i] += temp[i];
					}
				}
			}
		}
		for (int i = 0; i < sum.length; ++i) {
			System.out.println("less than " + interval * (i + 1) + ":" + sum[i]);
		}
	}
//	/*
//	 * 分段统计
//	 */
//	public static void countSectionNum(String file, int interval, int[] ans) {
//		List<String> data = DiffTools.file2List(file);
//		int size = data.size() / interval + 1;
//		if (size != ans.length) {
//			System.out.println("Statistics.showSpace: size err");
//		}
//		for (int i = 0; i < size; ++i) {
//			int count = 0;
//			for (int j = 0; j < interval; ++j) {
//				count += Integer.valueOf(data.get(i * interval + j));
//			}
//			ans[i] = count;
//		}
//	}

//	/*
//	 * 不含间隔0统计
//	 * 不怎么用
//	 */
//	public static void spaceCountNoZero(String suffix) {
//		int delta = 5;
//		int[] time = new int[(max / delta)];
//		File[] inputFiles = new File(Constants.SHP_INPUT_PATH).listFiles();
//		for (File file : inputFiles) {
//			if (file.isFile() && file.getName().endsWith(suffix + ".shp")) {
//				Long pretime = null;
//				SimpleFeatureCollection pointOrigin = SHPReader.readSHP(file, false);
//				SimpleFeatureIterator i = pointOrigin.features();
//				while (i.hasNext()) {
//					PointFeature pf = new BJTaxiData(i.next());
//					Long timeLong = pf.getTime();
//					if (pretime != null) {
//						long deltaTime = timeLong - pretime;
//						if (deltaTime != 0) {
//							int index = (int) ((deltaTime - 1) / (delta * 1000));
//							if (index == time.length)
//								index = time.length - 1;
//							if (index >= time.length) {
//								System.out.println("max is too small. max=" + max + " space=" + delta * index);
//							} else {
//								time[index] += 1;
//							}
//						}
//
//					}
//					pretime = timeLong;
//				}
//				i.close();
//			}
//		}
//		for (int i = 0; i < time.length; ++i) {
////			System.out.println("less than "+delta * index+ ":" + time[i]);
//			System.out.println(time[i]);
//		}
//	}
	/*
	 * 
	 * 统计数据总数
	 */
	public static void countDataNumPath(String path, String suffix) {
		int total = 0;
		File[] inputFiles = new File(path).listFiles();
		for (File file : inputFiles) {
			if (file.isFile() && file.getName().endsWith(suffix + ".shp")) {
				SimpleFeatureCollection pointOrigin = SHPReader.readSHP(file, false);
				SimpleFeatureIterator i = pointOrigin.features();
				while (i.hasNext()) {
					i.next();
					total++;
				}
				i.close();
			}
		}
		System.out.println(suffix + " total:" + total);
	}

	public static void countDataNum(File file) {
		SimpleFeatureCollection pointOrigin = SHPReader.readSHP(file, false);
		SimpleFeatureIterator i = pointOrigin.features();
		int total = 0;
		while (i.hasNext()) {
			i.next();
			total++;
		}
		i.close();
		System.out.println(file.getName() + " total:" + total);
	}
}
