package data.tools;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/*
 * @author pjl
 * @version 创建时间：2019年7月17日 下午7:22:52
 * 用于将结果进行输出，也可以用于简单日志记录
 */
public class TXTWriter {
	BufferedWriter bw;

	public TXTWriter(String fileName) throws IOException {
		if (fileName.endsWith(".txt")) {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
		} else {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName + ".txt"), "UTF-8"));
		}

	}

	public void write(Object o) throws IOException {
		bw.write(o.toString());
		bw.newLine();
	}

	public void write(String s) throws IOException {
		bw.write(s);
		bw.newLine();
	}

	public void close() throws IOException {
		bw.close();
	}
}
