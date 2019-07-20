package constants;

/*
 * @author pjl
 * @version 创建时间：2019年4月11日 下午4:34:53
 * 类说明
 */
public class Constants {
	// 路径设置
	// D:\study\研究生\毕业论文\data\map\myosm
	public final static String ROADFILE = "C:\\study\\myosm\\bj_small.shp";
	public final static String DATA_HOME_PATH = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\";
//	public final static String DATA_HOME_PATH = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\mydatabackup\\";
	public final static String SHP_INPUT_PATH = DATA_HOME_PATH + "myshpdata\\";
	public final static String SHP_OUTPUT_PATH = DATA_HOME_PATH + "myresultSHP\\";
	public final static String TXT_RESULT_PATH = DATA_HOME_PATH + "myresultTXT\\";
//最大搜索半径
	public final static double MAX_SEARCH_DISTANCE = 200;
//	public final static double MAX_SEARCH_DISTANCE = 5E-4;
	public static final double NOT_MOVED_DISTANCE = 10;
//二维正态分布参数
	public final static double MU1 = 0;
	public final static double MU2 = 0;
	public final static double SIGMA1 = 10;
	public final static double SIGMA2 = 10;
	public final static double RHO = 0;
//一维正态分布参数
	public static final double MU = 0;
	public static final double SIGMA = 4.07;
//	不同地图适配参数
//	osm
	public static final String ROAD_ID = "osm_id";
	public static final String ONEWAY = "oneway";
//	bj2011现已转换，废弃
//	public static final String ROAD_ID = "ID";
//	public static final String ONEWAY = "Oneway";
}
