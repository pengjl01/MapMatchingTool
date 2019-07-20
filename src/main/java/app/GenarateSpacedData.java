package app;

import constants.Constants;
import data.tools.SpaceGenerator;

/*
 * @author pjl
 * @version 创建时间：2019年7月19日 下午3:34:52
 * 数据间隔生成
 */
public class GenarateSpacedData {
	public static void main(String[] args) {
		func1(0);
		func2(0);
		func1(5);
		func2(5);
//		func3(2, 4);
	}

	public static void func1(int space) {
		SpaceGenerator.addSpacePath(Constants.TXT_RESULT_PATH + "truth\\", space);
	}

	public static void func2(int space) {
		SpaceGenerator.addSpacePath(Constants.SHP_INPUT_PATH, space);
	}

	public static void func3(int spaceLow, int spaceHigh) {
		for (int i = spaceLow; i <= spaceHigh; ++i) {
			func1(i);
			func2(i);
		}
	}
}
