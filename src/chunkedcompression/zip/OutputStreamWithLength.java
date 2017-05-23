package chunkedcompression.zip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * This class extends the OutputStream and keeps track of the number of bytes written at any instance.
 * This can be used as a data sink by classes like FilterOutputStream, ZipOutputStream
 *  
 * @author Sachin Parthasarathy
 *
 */
public class OutputStreamWithLength extends OutputStream{

	/**
	 * The output file
	 */
	public File file;
	
	/**
	 * Using a random access file to output
	 */
	private RandomAccessFile outputFile;
	
	/**
	 * Tracks the number of bytes written so far
	 */
	private long currentWriteLength;

	/**
	 * Returns the current number of bytes written
	 * @return currentWriteLength
	 */
	public long getCurrentWriteLength() 
	{
		return currentWriteLength;
	}

	/**
	 * Sets the current number of bytes written to the length passed in
	 * @param length
	 */
	public void setCurrentWriteLength(long length) {
		this.currentWriteLength = length;
	}

	/** 
	 * @param zipFilename
	 * @throws FileNotFoundException
	 */
	public OutputStreamWithLength(String zipFilename) throws FileNotFoundException {
		 this.currentWriteLength = 0;
         file = new File(zipFilename);
		 outputFile = new RandomAccessFile(file, "rw");
	}
		
	@Override
	/**
	 * Writes the input byte to the output stream
	 * @param b
	 */
	public void write(int b) throws IOException {
		byte[] buff = new byte[1];
		buff[0] = (byte) b;
	    write(buff, 0, 1);
	}
	
	@Override	
	/**
	 * Writes the byte array of length len from offset off
	 * @param b
	 * @param off
	 * @param len
	 */
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
	
	/**
	 * Close the output stream
	 */
	public void close() throws IOException
	{
		outputFile.close();
	}
}
