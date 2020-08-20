import java.io.*;

public class IOHelper {
    public static BufferedReader getBufferedReader(String file, int bufferSize) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, "utf-8");
        BufferedReader br = new BufferedReader(isr, bufferSize);
        return br;
    }
    public static BufferedWriter getBufferedWriter(String file, int bufferSize) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
        BufferedWriter bw = new BufferedWriter(osw, bufferSize);
        return bw;
    }
    public static BufferedReader getBufferedReader(String file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, "utf-8");
        BufferedReader br = new BufferedReader(isr);
        return br;
    }
    public static BufferedWriter getBufferedWriter(String file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
        BufferedWriter bw = new BufferedWriter(osw);
        return bw;
    }


    public static BufferedReader getBufferedReader(InputStream is, int bufferSize) throws IOException {
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(isr, bufferSize);
        return br;
    }
    public static BufferedWriter getBufferedWriter(OutputStream os, int bufferSize) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
        BufferedWriter bw = new BufferedWriter(osw, bufferSize);
        return bw;
    }
    public static BufferedReader getBufferedReader(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(isr);
        return br;
    }
    public static BufferedWriter getBufferedWriter(OutputStream os) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
        BufferedWriter bw = new BufferedWriter(osw);
        return bw;
    }
}
