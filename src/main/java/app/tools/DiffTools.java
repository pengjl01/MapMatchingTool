/*
 * 需要重新设计，凑合用还行，但太蠢
 */

package app.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*
 * @author pjl
 * @version 创建时间：2019年6月6日 下午6:18:12
 * 搜索匹配结果的区别
 */
public class DiffTools {
//	public static BufferedReader[] buildBufferedReaders(String path, String trace, List<String> types)
//			throws IOException {
//		BufferedReader[] brs = new BufferedReader[types.size()];
//		for (int i = 0; i < types.size(); ++i) {
//			String inputFileName = inputFileName(path, trace, types.get(i));
//			brs[i] = new BufferedReader(
//					new InputStreamReader(new BufferedInputStream(new FileInputStream(inputFileName)), "UTF-8"));
//		}
//		return brs;
//	}
//
//	public static String[] readLines(int size, BufferedReader[] brs) throws IOException {
//		String[] lines = new String[size];
//		for (int i = 0; i < size; ++i) {
//			lines[i] = brs[i].readLine();
//		}
//		int bc = breakCheck(lines);
//		if (bc > 0) {
//			if (bc == 2)
//				System.out.println("files have different length");
//			return null;
//		}
//		return lines;
//	}
//
//	public static void makeDiff(String path, String trace, List<String> types) {
//		BufferedWriter bw = null;
//		try {
//			StringBuilder outputFileName = new StringBuilder();
//			BufferedReader[] brs = buildBufferedReaders(path, trace, types);
//			for (int i = 0; i < types.size(); ++i) {
//				outputFileName.append(types.get(i));
//				outputFileName.append("_");
//			}
//			outputFileName.deleteCharAt(outputFileName.length() - 1);
//			bw = new BufferedWriter(new OutputStreamWriter(
//					new FileOutputStream(outputFileName(path, trace, outputFileName.toString())), "UTF-8"));
//			String[] lines = readLines(types.size(), brs);
//			int j = 0;
//			for (int n = 0; lines != null; ++n) {
//				boolean noDiff = true;
//				for (String s : lines) {
//					if (noDiff) {
//						if (!s.equals(lines[0])) {
//							noDiff = false;
//						}
//					}
//				}
//				if (!noDiff) {
//					bw.write(String.valueOf(n + 1));
//					for (String s : lines) {
//						bw.write(" " + s);
//					}
//					bw.newLine();
//					++j;
//				}
//				lines = readLines(types.size(), brs);
//			}
//			System.out.println("total " + j + " different");
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
//
//	public static void calcAccuracy(String path, String trace, List<String> types) {
//		try {
//			BufferedReader[] brs = buildBufferedReaders(path, trace, types);
//			BufferedReader brt = new BufferedReader(new InputStreamReader(
//					new BufferedInputStream(new FileInputStream(truthFileName(path, trace))), "UTF-8"));
//			String[] lines = readLines(types.size(), brs);
//			String truth = brt.readLine();
//			int[] diff = new int[types.size()];
//			int n;
//			for (n = 1; lines != null; ++n) {
//				for (int i = 0; i < types.size(); ++i) {
//					if (!truth.equals(lines[i])) {
//						++diff[i];
//					}
//				}
//				lines = readLines(types.size(), brs);
//				truth = brt.readLine();
//			}
//			System.out.println("total: " + n + " points.");
//			for (int i = 0; i < types.size(); ++i) {
//				System.out.println(types.get(i) + " different: " + diff[i] + " acc: " + ((float) (n - diff[i])) / n);
//			}
//			for (BufferedReader br : brs) {
//				br.close();
//			}
//			brt.close();
//		} catch (IOException e) {
//			// TODO: handle exception
//		}
//	}
	public static void calcAccuracy(String path, String trace, List<String> types) {
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

	public static AccData calcAcc(List<String> truth, List<String> file, String info) {
		if (truth == null || file == null || truth.size() != file.size()) {
			System.out
					.println("DiffTools: two Lists have different size. truth:" + truth.size() + ";ans:" + file.size());
		}
		int diff = 0;
		for (int i = 0; i < truth.size(); ++i) {
			if (!truth.get(i).equals(file.get(i))) {
				++diff;
			}
		}
		return new AccData(truth.size() + 1, diff, info);
	}

	static int breakCheck(String[] lines) {
		int nullsum = 0;
		for (String s : lines) {
			if (s == null)
				nullsum++;
		}
		if (nullsum == 0)
			return 0;
		if (nullsum == lines.length)
			return 1;
		return 2;
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
}
