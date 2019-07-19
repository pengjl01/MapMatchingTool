package app.tools;

/*
 * @author pjl
 * @version 创建时间：2019年7月10日 下午2:14:44
 * 对比正确率用
 */
public class AccData {
//	用来记一些东西方便跟踪这条数据的来源
	String info;
	int total;
	int err;

	public AccData() {
		total = 0;
		err = 0;
		info = "";
	}

	public AccData(int total, int err) {
		this.total = total;
		this.err = err;
		info = "";
	}

	public AccData(int total, int err, String info) {
		this.total = total;
		this.err = err;
		this.info = info;
	}

	public void addData(int deltaTotal, int deltaErr) {
		total += deltaTotal;
		err += deltaErr;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getErr() {
		return err;
	}

	public void setErr(int err) {
		this.err = err;
	}

	public float calcAcc() {
		return ((float) (total - err)) / total;
	}

	@Override
	public String toString() {
		return info + ": Total:" + total + ";Err:" + err + ";Acc:" + calcAcc();
	}

	/*
	 * 用于多条轨迹综合正确率计算
	 */
	public void plus(AccData accData) {
		this.total += accData.total;
		this.err += accData.err;
	}
}
