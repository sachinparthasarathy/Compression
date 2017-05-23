package chunkedcompression.zip;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipOutputStream;

import chunkedcompression.CompressionBase;

/**
 * This class implements the Chunked Zip compression algorithm
 *   
 * @author Sachin Parthasarathy
 *
 */

public class ThreadedZipCompression extends CompressionBase
{

	/**
	 * Maximum size of a single zip archive
	 */
	private int maxSplitSize = 0;
	
	/**
	 * Current Zip number
	 */
	public static AtomicInteger cnt = new AtomicInteger(0);

	@Override
	/**
	 * Implements the compress method
	 * @param files		List of files to be compressed
	 * @param outputPath	Path where the compressed zip archives will be stored
	 * @param Maximum size of a single zip archive
	 */
	protected void compress(List<File> files, String outputPath, int maxSplitsize) 
	{
		try
		{
			long startTime = System.currentTimeMillis();
			long elapsedTime = 0L;
			System.out.println("Starting compression...");
			this.maxSplitSize = (int)(maxSplitsize * 1024 * 1024 * 0.97); // giving room for zip headers


			//Single thread of producer to produce data
			ExecutorService producerexecutor = Executors.newFixedThreadPool
					(1);
			Producer producer = new Producer(files, basePath);
			producerexecutor.execute(producer);

			//Wait for producer to produce some data
			Thread.sleep(500);
			
			ZipUtils ziputils  = new ZipUtils();
			//Heuristic to determine number of starting threads
			long approxCompressedSize = (int)(ziputils.getTotalFileSize(files) * 0.6);
			int consumerThreadpool = Math.min(Constants.noOfProcessors - 1,
			(int)approxCompressedSize/maxSplitSize);
			consumerThreadpool = consumerThreadpool == 0 ? 1 : consumerThreadpool;
			
			ExecutorService consumerexecutor = Executors.newFixedThreadPool
					(consumerThreadpool);

			for(int i = 0; i < consumerThreadpool; i++)
			{
				//Atomically increment the zip index
				int zipIndex = cnt.incrementAndGet();
				String zipFileName = outputPath + File.separator + Constants.name + zipIndex + Constants.zipExtension;
				OutputStreamWithLength chunkedFile = new OutputStreamWithLength(zipFileName);
				ZipOutputStream zipOutputStream = new ZipOutputStream(chunkedFile);
				//Call consumer to consume data for each thread
				BufferWorker worker = new BufferWorker(outputPath
						, maxSplitSize, chunkedFile , zipOutputStream);
				consumerexecutor.execute(worker);
			}
			
			
			//Wait for producer to close
			producerexecutor.shutdown();
			try {
				while (!producerexecutor.awaitTermination(2, TimeUnit.SECONDS)) {}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Wait for all consumers to close
			consumerexecutor.shutdown();
			try {
				while (!consumerexecutor.awaitTermination(2, TimeUnit.SECONDS)) {}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("Finished compression of " + files.size() + " files.");
			elapsedTime = System.currentTimeMillis();
			System.out.println("Compression took " + (elapsedTime - startTime)/1000 + " seconds");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}