package app;

import app.tools.AccData;
import app.tools.DiffTools;

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
		String trace = "13331194450_00003_0";
		DiffTools.printAcc(trace);
	}

	/*
	 * 计算某算法的综合正确率
	 */
	public static void func2() {
		String type = "FWMM";
		String suffix = "5";
		AccData ans = DiffTools.calcAccAll(type, suffix);
		System.out.println(type + ":" + ans);
	}
}
