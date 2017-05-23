package chunkedcompression.zip.multiThreaded;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import chunkedcompression.zip.Constants;
import chunkedcompression.zip.OutputStreamWithLength;

/**
 * This class implements the Consumer which reads data from buffer,
 * compresses it and write it to disk
 * @author Sachin Parthasarathy
 *
 */
class BufferWorker implements Runnable
{
	/**
	 * Maximum split size defined by user
	 */
	private int maxSplitSize;
	
	/**
	 * Output path of the compressed files 
	 */
	private String outputPath;
	
	/**
	 * Output zip output stream
	 */
	private ZipOutputStream zipOutputStream;
	
	/**
	 * Filter output stream
	 */
	private OutputStreamWithLength chunkedFile;

	public BufferWorker(String outputPath, int maxSplitSize, 
			OutputStreamWithLength chunkedFile, ZipOutputStream zipOutputStream)
	{
		this.outputPath = outputPath;	
		this.maxSplitSize = maxSplitSize;
		this.zipOutputStream = zipOutputStream;
		this.chunkedFile = chunkedFile;
	}

	/**
	 * Overrides the run method and implements the consumer
	 */
	@Override
	public void run() 
	{
		try
		{
			Buffer dataToWrite;
			while(Producer.isProducing == true || Producer.producer.size() > 0)
			{
				/*Producer has finished producing and consumer has finished
				 * consuming*/
				if( Producer.isProducing == false && Producer.producer.size() == 0)
				{
					break;
				}
				/*Consumer has consumed everything but Producer will
				 * 	produce eventually*/
				if( Producer.isProducing && Producer.producer.size() == 0)
				{
					Thread.sleep(5);
				}
				if(Producer.producer.size() > 0)
				{
					synchronized (Producer.producer) {
						//Remove from the buffer
						if(Producer.producer.size() > 0)
							dataToWrite = Producer.producer.remove();
						else continue;
					}					
					//Write buffer data to compressed output stream
					writeToStream(dataToWrite);
					//Give chance to other threads to write
					Thread.sleep(10);			
				}
			}			
			zipOutputStream.closeEntry();
			zipOutputStream.finish();
		}
		catch(Exception e)
		{}
	}

	/**
	 * The entry is a directory.Add it to the zip output stream
	 * @param buffer Buffer data
	 */
	private void addDirEntry(Buffer buffer) {
		try
		{
			ZipEntry entry =new ZipEntry(buffer.fragmentName+"/");
			zipOutputStream.putNextEntry(entry);
			zipOutputStream.closeEntry();
		}
		catch(Exception e)
		{}
	}

	/**
	 * Write to compressed zip output stream
	 * @param buffer Buffer data
	 */
	private void writeToStream(Buffer buffer)
	{
		try
		{			
			if((chunkedFile.getCurrentWriteLength() + buffer.len ) 
					> maxSplitSize)
			{
				// close current zip output stream
				zipOutputStream.closeEntry();
				zipOutputStream.finish();

				// reset the write length
				chunkedFile.setCurrentWriteLength(0);

				// create new zip output stream
				int zipIndex = ThreadedZipCompression.cnt.incrementAndGet();
				String zipFileName = outputPath+ File.separator + Constants.name + zipIndex + Constants.zipExtension;
				chunkedFile = new OutputStreamWithLength(zipFileName);
				zipOutputStream = new ZipOutputStream(chunkedFile);

				//The entry is a directory
				if(buffer.isDirectory)
					addDirEntry(buffer);
				else
				{
					// add the current file to write remaining bytes
					ZipEntry entry = new ZipEntry(buffer.fragmentName);
					zipOutputStream.putNextEntry(entry);
					// write the bytes to the zip output stream
					zipOutputStream.write(buffer.buffer, 0, buffer.len);
				}
			}
			else
			{
				//The entry is a directory
				if(buffer.isDirectory)
					addDirEntry(buffer);
				else
				{
					// write the bytes to the zip output stream
					ZipEntry entry =new ZipEntry(buffer.fragmentName);
					zipOutputStream.putNextEntry(entry);
					zipOutputStream.write(buffer.buffer, 0, buffer.len);
				}
			}
		}
		catch(Exception e)
		{}
	}
}