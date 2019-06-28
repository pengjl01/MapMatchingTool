package entity.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.locationtech.jts.geom.Coordinate;

/*
 * @author pjl
 * @version 创建时间：2019年6月26日 下午1:38:47
 * 图的工具类
 */
public class GraphTools {

	/*
	 * 判断三点共线,设计为c到ab的距离小于ab线段距离的10000分之一， 相对于面积法直接设定阈值，此方法具有一定自适应能力
	 */
	public static boolean threePointCollinearity(Coordinate a, Coordinate b, Coordinate c) {
		double s = (a.x * b.y - b.x * a.y) + (b.x * c.y - c.x * b.y) + (c.x * a.y - a.x * c.y);
		double l = a.distance(b);
		return Math.abs(s / l / l) < 0.0002;
	}

//	/*
//	 * 判断三点共线,double存在精度问题,故面积法不可直接使用
//	 */
//	public static boolean threePointCollinearity0(Coordinate a, Coordinate b, Coordinate c) {
//		return ((a.x * b.y - b.x * a.y) + (b.x * c.y - c.x * b.y) + (c.x * a.y - a.x * c.y)) == 0.0;
//	}

	/*
	 * 从queue中提取node1到node2之间的部分（包含） 如果queue中不包含node1或者node2，或者其长度小于3，均视为异常情况，返回null
	 */
	public static List<Coordinate> cutQueueAndBuildList(Coordinate node1, Coordinate node2, Queue<Coordinate> que) {
		List<Coordinate> ans = new ArrayList<Coordinate>();
		boolean flag = false;
		while (!que.isEmpty()) {
			Coordinate coor = que.poll();
			if (coor.equals(node1))
				flag = true;
			if (flag)
				ans.add(coor);
			if (coor.equals(node2))
				break;
		}
		return ans;
//		if (ans.size() >= 2 && ans.get(0).equals(node1) && ans.get(ans.size() - 1).equals(node2)) {
//			return ans;
//		} else {
//			return null;
//		}
	}
}
