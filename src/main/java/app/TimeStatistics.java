package app;

import java.io.File;

import app.tools.StatisticsTools;
import constants.Constants;

/*
 * @author pjl
 * @version 创建时间：2019年7月16日 上午10:24:02
 * 从shp文件读取数据，统计采样间隔
 */
public class TimeStatistics {
	/*
	 * 统计各间隔下的数据总数
	 */
	public static void func1() {
		for (int i = 0; i <= 5; ++i) {
//			"D:\\study\\研究生\\毕业论文\\data\\map\\myosm"
			StatisticsTools.countDataNumPath(Constants.SHP_INPUT_PATH, String.valueOf(i));
		}
	}

	/*
	 * 统计各间隔下的以10秒为间隔的数据总数
	 */ public static void func2() {
		for (int i = 0; i <= 5; ++i) {
			StatisticsTools.countSectionNumPath(StatisticsTools.STATISTICS_RESULT_PATH, "_" + i, 10);
		}
	}

	/*
	 * 统计某个shp的数据总数
	 */
	public static void func3() {
		StatisticsTools.countDataNum(
				new File("D:\\study\\研究生\\毕业论文\\data\\data_来自es\\mydatabackup\\myshpdata\\" + "13321174830_00003.shp"));

	}

	/*
	 * 统计各间隔下的数据数 生成数据
	 */
	public static void genarateIntevalData() {
		for (int i = 0; i <= 5; ++i) {
			StatisticsTools.spaceCount(Constants.SHP_INPUT_PATH, String.valueOf(i));
		}
	}

	public static void main(String[] args) {
		genarateIntevalData();
	}
}
