package chunkedcompression.zip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class OutputStreamWithLength extends OutputStream{

	private File file;
	private RandomAccessFile outputFile;
	private long currentWriteLength;

	public long getCurrentWriteLength() 
	{
		return currentWriteLength;
	}

	public void setCurrentWriteLength(long length) {
		this.currentWriteLength = length;
	}

	public OutputStreamWithLength(String zipFilename) throws FileNotFoundException {
		 this.currentWriteLength = 0;
         file = new File(zipFilename);
		 outputFile = new RandomAccessFile(file, "rw");
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
			outputFile.write(b, off, len);
			currentWriteLength += len; 
		}
		catch(Exception e)
		{
			System.err.println("Not able to write to zip file");
			e.printStackTrace();
		}
	}
	

	public void close() throws IOException
	{
		outputFile.close();
	}
}
