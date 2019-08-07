package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.tools.AccData;
import app.tools.DiffTools;
import constants.Constants;
import data.tools.TXTWriter;

/*
 * @author pjl
 * @version 创建时间：2019年7月3日 下午2:11:44
 * 类说明
 */
public class FindDifferents {
	public static void main(String[] args) {
		makeAccData();
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

	/*
	 * 每个算法生成9行6列数据， 行表示不同轨迹 列表示不同space 最后一行为一列的加权和（综合正确率）
	 */
	public static void makeAccData() {
		try {
			TXTWriter tw = new TXTWriter(Constants.DATA_HOME_PATH + "AccData");
			List<String> types = new ArrayList<String>();
			types.add("FWMM");
			types.add("WMM");
			types.add("HMM");
			types.add("SimpleDistance");
			types.add("STMM");
			types.add("STDM");
			List<String> names = new ArrayList<String>();
			names.add("13321174830_00003");
			names.add("13321174830_00004");
			names.add("13321174830_00005");
			names.add("13321174881_00003");
			names.add("13321174881_00006");
			names.add("13331194030_00001");
			names.add("13331194214_00001");
//			names.add("13331194450_00003");
			for (String type : types) {
				tw.write(type);
				AccData sum = new AccData();
				for (String name : names) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < 6; ++i) {
						String suffix = String.valueOf(i);
						AccData data = DiffTools.calcAcc(name, type, suffix);
						sum.plus(data);
						sb.append(data.calcAcc());
						sb.append(" ");
					}
					tw.write(sb.toString());
				}
				/*
				 * 这里重复计算了，可以优化（但没必要）
				 */
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 6; ++i) {
					AccData data = DiffTools.calcAccAll(type, String.valueOf(i));
					sb.append(data.calcAcc());
					sb.append(" ");
				}
				tw.write(sb.toString());
				tw.write(sum.calcAcc());
			}
			tw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
