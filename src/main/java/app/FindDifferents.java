package app;

import java.util.ArrayList;
import java.util.List;

import app.tools.DiffTools;

/*
 * @author pjl
 * @version 创建时间：2019年7月3日 下午2:11:44
 * 类说明
 */
public class FindDifferents {
	public static void main(String[] args) {
		func1();
	}

	public static void func1() {
		// D:\study\研究生\毕业论文\data\data_来自es\myresult
		String path = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresultTXT\\";
		String trace = "13321174881_00006";
		List<String> types = new ArrayList<String>();
		types.add("TRUTH");
		types.add("FWMM");
		types.add("WMM");
		types.add("HMM");
//		files.add("simple");
		DiffTools.makeDiff(path + trace, types);
//		String file1 = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresult\\my.txt";
//		String file2 = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresult\\myfast.txt";
//		String outString = "D:\\study\\研究生\\毕业论文\\data\\data_来自es\\myresult\\my_myfast.txt";

	}
}
