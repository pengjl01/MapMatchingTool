package app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/*
 * @author pjl
 * @version 创建时间：2019年6月6日 下午6:18:12
 * 搜索匹配结果的区别
 */
public class FindDifference {
	public static void main(String[] args) {
		// D:\study\研究生\毕业论文\data\data_来自es\myresult
		String path = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresult\\";
		String trace = "13321164368_00036_";
		List<String> files = new ArrayList<String>();
		files.add("fwmm");
		files.add("hmm");
//		files.add("simple");
		makeDiff(path + trace, files);
//		String file1 = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresult\\my.txt";
//		String file2 = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresult\\myfast.txt";
//		String outString = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresult\\my_myfast.txt";

	}

	public static void makeDiff(String base, List<String> files) {
		BufferedWriter bw = null;
		try {
			StringBuilder outBuilder = new StringBuilder();
			BufferedReader[] brs = new BufferedReader[files.size()];
			for (int i = 0; i < files.size(); ++i) {
				outBuilder.append(files.get(i));
				outBuilder.append("_");
				brs[i] = new BufferedReader(new InputStreamReader(
						new BufferedInputStream(new FileInputStream(inputFileName(base, files.get(i)))), "UTF-8"));
			}
			outBuilder.deleteCharAt(outBuilder.length() - 1);
			bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(outputFileName(base, outBuilder.toString())), "UTF-8"));
			String[] lines = new String[files.size()];
			for (int i = 0; i < files.size(); ++i) {
				lines[i] = brs[i].readLine();
			}
			int j = 0;
			for (int n = 0;; ++n) {
				int bc = breakCheck(lines);
				if (bc > 0) {
					if (bc == 2)
						System.out.println("files have different length");
					break;
				}
				StringBuilder sb = new StringBuilder();
				sb.append(n);
				boolean noDiff = true;
				for (String s : lines) {
					sb.append(" " + s);
					if (noDiff) {
						if (!s.equals(lines[0])) {
							noDiff = false;
						}
					}
				}
				if (!noDiff) {
					bw.write(sb.toString());
					bw.newLine();
					++j;
				}
				for (int i = 0; i < files.size(); ++i) {
					lines[i] = brs[i].readLine();
				}
			}
			System.out.println("total " + j + " different");
		} catch (Exception e) {
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

	static String inputFileName(String base, String file) {
		return base + "result_" + file + ".txt";
	}

	static String outputFileName(String base, String file) {
		return base + "diff_" + file + ".txt";
	}
}
