package chunkedcompression.zip.multiThreaded;


import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import chunkedcompression.zip.Constants;
import chunkedcompression.zip.ZipUtils;

/**
 * This class implements the Producer which writes data from buffer,
 * which will be read by the consumer
 * @author Sachin Parthasarathy
 *
 */
class Producer implements Runnable
{
	/**
	 * List of input files
	 */
	private List<File> files;
	
	/**
	 * Base Path of input directory
	 */
	private String basePath;
	
	/**
	 * Buffer from which consumer will read data
	 */
	public static volatile Queue<Buffer> producer = new ConcurrentLinkedQueue<Buffer>();
	
	/**
	 * Flag to indicate if producer is still producing data
	 */
	public static boolean isProducing = true;
	
	/**
	 * Total bytes of the files
	 */
	private long totalBytes = 0;
	

	public Producer(List<File> files, String basePath)
	{
		this.files = files;
		this.basePath = basePath;
	}

	/**
	 * Overrides the run method and implements the producer 
	 */
	public void run() 
	{		
		int bufferSize =  1024 * 1024;
		//Total file size in bytes
		totalBytes = new ZipUtils().getTotalFileSize(files);
		double totalBytesRead = 0L;
		int count = 0;
		int len = 0;
		for(File file:files)
		{
			byte[] buffer = new byte[bufferSize];
			long fragmentIndex = 0; 
			String zipEntryPath = file.getAbsolutePath().substring(basePath.length() + 0);
			//Directory entry
			if(file.isDirectory())
			{
				producer.add(new Buffer(zipEntryPath,new byte[0],len,true));
			}
			//Empty file entry
			else if(file.length() == 0)
			{
				String name = zipEntryPath + Constants.fragmentLabel + (fragmentIndex++);	
				producer.add(new Buffer(name,new byte[0],len,false));
			}			
			else
			{
				try
				{				
					FileInputStream inputFileStream = new FileInputStream(file);
					while ((len = inputFileStream.read(buffer)) > 0) 
					{
						/*Producer is producing faster than consumer can consume.
						 * Sleep for some time and wait for consumer to consume
						 */
						while(producer.size()*bufferSize > Constants.jvmMemory * 0.8)
							Thread.sleep(100);
						String name = zipEntryPath + Constants.fragmentLabel + (fragmentIndex++);
						//Add datato buffer
						producer.add(new Buffer(name,buffer,len,false));
						buffer = new byte[bufferSize];
						
						//Show progress
						totalBytesRead += len;
						double progress = totalBytesRead / totalBytes;
						if (progress*100 >= 10)
						{
							totalBytesRead = 0L;
							count += 10;
							System.out.println("Finished " + count +"%");
						}
					}
					inputFileStream.close();
				}
				catch(Exception e)
				{
					System.out.println(producer.size());
				}
			}					
		}
		//Finished producing
		isProducing = false;
	}
}