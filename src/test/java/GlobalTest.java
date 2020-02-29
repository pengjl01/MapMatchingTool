/*
 * @author pjl
 * @version 创建时间：2019年7月4日 下午7:50:09
 * 类说明
 */
public class GlobalTest {
	static double BETA = 5.0;

	static double func1(double distance) {
		return (Math.pow(Math.E, -(distance / BETA)));
	}

	public static void main(String[] args) {

	}
}
