package chunkedcompression.zip;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import chunkedcompression.DecompressionBase;

/**
 * This class implements the Chunked Zip decompression algorithm
 *   
 * @author Sachin Parthasarathy
 *
 */
public class ZipDecompression extends DecompressionBase {

	@Override
	/**
	 * Implements the decompress method
	 * @param inputPath		Path where the compressed zip archives are stored
	 * @param outputPath	Path where the decompressed output will be stored
	 */
	public void decompress(String inputPath, String outputPath)
	{
		long startTime = System.currentTimeMillis();
		long elapsedTimedecompress = 0L;
		long elapsedTimemerge = 0L;
		System.out.println("Starting decompression...");
		File folder = new File(inputPath);  	
		File[] files = folder.listFiles();

		//Create multiple threads for parallel decompression
		ExecutorService executor = Executors.newFixedThreadPool
				(Constants.noOfProcessors);

		int count = 0;
		double totalRead = 0;
		for(File file :files)
		{
			String fileName = file.getName();
			Runnable worker = new ZipDecompressWorker(inputPath + File.separator + fileName,
					outputPath);
			executor.execute(worker);
			//Show progress
			totalRead += 1;
			if(totalRead/files.length >= 0.1)
			{
				totalRead = 0;
				count += 10;
				System.out.println("Finished " + count +"%");				
			}
		}

		executor.shutdown();
		//Wait for all threads to finish
		try {
			while (!executor.awaitTermination(2, TimeUnit.SECONDS)) {}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Finished decompression...");
		elapsedTimedecompress = System.currentTimeMillis();
		System.out.println("DeCompression took " + (elapsedTimedecompress - startTime)/1000 + " seconds");

		//Merge the fragments of the decompressed files
		ZipMerge mergingAlgorithm = new ZipMerge(outputPath);
		mergingAlgorithm.mergeHelper();	
		System.out.println("Finished merging...");
		elapsedTimemerge = System.currentTimeMillis();
		System.out.println("Merging took " + (elapsedTimemerge - elapsedTimedecompress)/1000 + " seconds");
	}
}