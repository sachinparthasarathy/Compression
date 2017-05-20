package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.zip.ZipOutputStream;

public class ChunkedFile extends OutputStream{

	private File file;
	private RandomAccessFile raf;
	public static long currlen = 0;
	public static long maxlen = 10 * 1024 * 1024;

	public ChunkedFile(String zipFilename) throws FileNotFoundException {
		 currlen = 0;
         file = new File(zipFilename);
		 raf = new RandomAccessFile(file,"rw");
	}
		
	@Override
	public void write(int b) throws IOException {
		byte[] buff = new byte[1];
		buff[0] = (byte) b;
	    write(buff, 0, 1);
	}
	
	@Override
	public void write(byte b[], int off, int len) throws IOException {
		try
		{
			raf.write(b, off, len);
			currlen += len; 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

	public void close() throws IOException{
		raf.close();
	}
}
