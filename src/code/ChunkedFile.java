package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class ChunkedFile extends OutputStream{

	private File file;
	private RandomAccessFile raf;
	private long currlen;

	public long getCurrlen() {
		return currlen;
	}

	public void setCurrlen(long currlen) {
		this.currlen = currlen;
	}

	public ChunkedFile(String zipFilename) throws FileNotFoundException {
		 this.currlen = 0;
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
