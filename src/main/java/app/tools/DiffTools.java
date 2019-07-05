/*
 * 需要重新设计，凑合用还行，但太蠢
 */

package app.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

/*
 * @author pjl
 * @version 创建时间：2019年6月6日 下午6:18:12
 * 搜索匹配结果的区别
 */
public class DiffTools {
	public static BufferedReader[] buildBufferedReaders(String input, List<String> types) throws IOException {
		BufferedReader[] brs = new BufferedReader[types.size()];
		for (int i = 0; i < types.size(); ++i) {
			String inputFileName = inputFileName(input, types.get(i));
			brs[i] = new BufferedReader(
					new InputStreamReader(new BufferedInputStream(new FileInputStream(inputFileName)), "UTF-8"));
		}
		return brs;
	}

	public static String[] readLines(int size, BufferedReader[] brs) throws IOException {
		String[] lines = new String[size];
		for (int i = 0; i < size; ++i) {
			lines[i] = brs[i].readLine();
		}
		int bc = breakCheck(lines);
		if (bc > 0) {
			if (bc == 2)
				System.out.println("files have different length");
			return null;
		}
		return lines;
	}

	public static void makeDiff(String input, List<String> types) {
		BufferedWriter bw = null;
		try {
			StringBuilder outputFileName = new StringBuilder();
			BufferedReader[] brs = buildBufferedReaders(input, types);
			for (int i = 0; i < types.size(); ++i) {
				outputFileName.append(types.get(i));
				outputFileName.append("_");
			}
			outputFileName.deleteCharAt(outputFileName.length() - 1);
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFileName(input, outputFileName.toString())), "UTF-8"));
			String[] lines = readLines(types.size(), brs);
			int j = 0;
			for (int n = 0; lines != null; ++n) {
				boolean noDiff = true;
				for (String s : lines) {
					if (noDiff) {
						if (!s.equals(lines[0])) {
							noDiff = false;
						}
					}
				}
				if (!noDiff) {
					bw.write(String.valueOf(n + 1));
					for (String s : lines) {
						bw.write(" " + s);
					}
					bw.newLine();
					++j;
				}
				lines = readLines(types.size(), brs);
			}
			System.out.println("total " + j + " different");
		} catch (IOException e) {
			// TODO: handle exception
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void calcAccuracy(String input, List<String> types) {
		try {
			BufferedReader[] brs = buildBufferedReaders(input, types);
			BufferedReader brt = new BufferedReader(
					new InputStreamReader(new BufferedInputStream(new FileInputStream(truthFileName(input))), "UTF-8"));
			String[] lines = readLines(types.size(), brs);
			String truth = brt.readLine();
			int[] diff = new int[types.size()];
			int n;
			for (n = 0; lines != null; ++n) {
				for (int i = 0; i < types.size(); ++i) {
					if (!truth.equals(lines[i])) {
						++diff[i];
					}
				}
				lines = readLines(types.size(), brs);
				truth = brt.readLine();
			}
			for (int i = 0; i < types.size(); ++i) {
				System.out.println(types.get(i) + " different: " + diff[i] + " acc: " + ((float) (n - diff[i])) / n);
			}
			for (BufferedReader br : brs) {
				br.close();
			}
			brt.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
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

	static String truthFileName(String base) {
		return base + "_TRUTH.txt";
	}

	static String inputFileName(String base, String type) {
		return base + "_" + type + ".txt";
	}

	static String outputFileName(String base, String type) {
		return base + "_" + "diff_" + type + ".txt";
	}
}
