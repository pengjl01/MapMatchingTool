package app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.tools.AccData;
import app.tools.DiffTools;
import constants.Constants;

/*
 * @author pjl
 * @version 创建时间：2019年7月3日 下午2:11:44
 * 类说明
 */
public class FindDifferents {
	public static void main(String[] args) {
		func2();
	}

	public static void func1() {
		String trace = "13321174881_00003";
		List<String> types = new ArrayList<String>();
//		types.add("TRUTH");
		types.add("FWMM");
		types.add("WMM");
		types.add("HMM");
		types.add("SimpleDistance");
//		DiffTools.makeDiff(path + trace, types);
		DiffTools.calcAccuracy(Constants.TXT_RESULT_PATH, trace, types);

	}

	/*
	 * 计算某算法的综合正确率
	 */
	public static void func2() {
		String type = "FWMM";
		File[] inputFiles = new File(Constants.TXT_RESULT_PATH).listFiles();
		AccData ans = new AccData();
		for (File file : inputFiles) {
			if (file.isFile() && file.getName().endsWith(".txt")) {
				String name = file.getName().replaceAll("[.][^.]+$", "");
				if (name.endsWith(type)) {
					name = name.substring(0, 17);
					String truthFileName = DiffTools.truthFileName(Constants.TXT_RESULT_PATH, name);
					List<String> truth = DiffTools.file2List(truthFileName);
					String ansFileName = DiffTools.inputFileName(Constants.TXT_RESULT_PATH, name, type);
					List<String> data = DiffTools.file2List(ansFileName);
					AccData t = DiffTools.calcAcc(truth, data, type);
					ans.plus(t);
				}

			}
		}
		System.out.println(type + ":" + ans);
	}
}
