/*
 * 需要重新设计，凑合用还行，但太蠢
 */

package app.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import constants.Constants;

/*
 * @author pjl
 * @version 创建时间：2019年6月6日 下午6:18:12
 * 搜索匹配结果的区别
 */
public class DiffTools {
	/*
	 * makediff就凑合用之前的算了。。。。反正用的也不多
	 */
//	public static void makeDiff(String path, String trace, List<String> types) {
//		List<String> truth = file2List(truthFileName(path, trace));
//		List<String>[] datas = new ArrayList[types.size()];
//		for (int i = 0; i < types.size(); ++i) {
//			datas[i] = file2List(inputFileName(path, trace, types.get(i)));
//		}
//		BufferedWriter bw = null;
//		StringBuilder outputFileName = new StringBuilder();
//		for (int i = 0; i < types.size(); ++i) {
//			outputFileName.append(types.get(i));
//			outputFileName.append("_");
//		}
//		outputFileName.deleteCharAt(outputFileName.length() - 1);
//		try {
//			bw = new BufferedWriter(new OutputStreamWriter(
//					new FileOutputStream(outputFileName(path, trace, outputFileName.toString())), "UTF-8"));
//			
//		} catch (IOException e) {
//			// TODO: handle exception
//		} finally {
//			if (bw != null) {
//				try {
//					bw.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//	/*
//	 * makeDiff用
//	 */
//	static int breakCheck(String[] lines) {
//		int nullsum = 0;
//		for (String s : lines) {
//			if (s == null)
//				nullsum++;
//		}
//		if (nullsum == 0)
//			return 0;
//		if (nullsum == lines.length)
//			return 1;
//		return 2;
//	}
	/*
	 * 计算多种算法的正确率并打印
	 */
	public static void printAcc(String path, String trace, List<String> types) {
		List<AccData> accDatas = new ArrayList<AccData>();
		List<String> truth = file2List(truthFileName(path, trace));
		for (int i = 0; i < types.size(); ++i) {
			List<String> data = file2List(inputFileName(path, trace, types.get(i)));
			accDatas.add(calcAcc(truth, data, types.get(i)));
		}
		for (AccData a : accDatas)
			System.out.println(a);
	}

	/*
	 * 计算多种算法的正确率并打印
	 */
	public static void printAcc(String FILE) {
		List<String> types = new ArrayList<String>();
		types.add("FWMM");
		types.add("WMM");
		types.add("HMM");
		types.add("SimpleDistance");
		types.add("STMM");
		types.add("STDM");
//		DiffTools.makeDiff(path + trace, types);
		printAcc(Constants.TXT_RESULT_PATH, FILE, types);
	}

	/*
	 * 计算单个算法的正确率并打印
	 */
	public static void printAcc(String FILE, String type) {
		List<String> types = new ArrayList<String>();
		types.add(type);
		printAcc(Constants.TXT_RESULT_PATH, FILE, types);
	}

	/*
	 * 文件读成list方便使用
	 */
	public static List<String> file2List(String file) {
		List<String> ans = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), "UTF-8"));
			String line = br.readLine();
			while (line != null) {
				ans.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
		return ans;
	}

	public static String truthFileName(String path, String trace) {
		return path + "truth\\" + trace + "_TRUTH.txt";
	}

	public static String inputFileName(String path, String trace, String type) {
		return path + trace + "_" + type + ".txt";
	}

	public static String outputFileName(String path, String trace, String type) {
		return path + trace + "_" + "diff_" + type + ".txt";
	}

	/*
	 * 计算给定算法type下的suffix后缀下的综合正确率
	 */
	public static AccData calcAccAll(String type, String suffix) {
		File[] inputFiles = new File(Constants.TXT_RESULT_PATH).listFiles();
		AccData ans = new AccData();
		for (File file : inputFiles) {
			if (file.isFile() && file.getName().endsWith(suffix + "_" + type + ".txt")) {
				String name = file.getName().replaceAll("[.][^.]+$", "");
				if (name.endsWith(type)) {
					name = name.substring(0, 17);
					AccData t = calcAcc(name, type, suffix);
					ans.plus(t);
				}

			}
		}
		return ans;
	}

	/*
	 * 计算type算法下以suffix为后缀的name轨迹的正确率
	 */
	public static AccData calcAcc(String name, String type, String suffix) {
		String truthFileName = DiffTools.truthFileName(Constants.TXT_RESULT_PATH, name + "_" + suffix);
		List<String> truth = DiffTools.file2List(truthFileName);
		String ansFileName = DiffTools.inputFileName(Constants.TXT_RESULT_PATH, name + "_" + suffix, type);
		List<String> data = DiffTools.file2List(ansFileName);
		return DiffTools.calcAcc(truth, data, type);
	}

	/*
	 * 基本版，对比两个list，给出AccData
	 */
	public static AccData calcAcc(List<String> truth, List<String> file, String info) {
//		if (truth == null || file == null || truth.size() != file.size()) {
//			System.out
//					.println("DiffTools: two Lists have different size. truth:" + truth.size() + ";ans:" + file.size());
//		}
		int diff = 0;
		for (int i = 0; i < truth.size(); ++i) {
			if (!truth.get(i).equals(file.get(i))) {
				++diff;
			}
		}
		return new AccData(truth.size(), diff, info);
	}
}
