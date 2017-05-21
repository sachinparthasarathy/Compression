package chunkedcompression.zip;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
	public void decompress(String inputPath, String outputPath)
	{
		System.out.println("Starting decompression...");
		Map<String,List<String>> fragmentsMap = new ConcurrentHashMap<>();
		File folder = new File(inputPath);  	
		File[] files = folder.listFiles();

		ExecutorService executor = Executors.newFixedThreadPool
				(Constants.noOfProcessors);

		for(File file :files)
		{
			String fileName = file.getName();
			Runnable worker = new ZipDecompressWorker(inputPath + File.separator + fileName,
					outputPath,fragmentsMap);
			executor.execute(worker);			
		}
		
		executor.shutdown();
		
		try {
			while (!executor.awaitTermination(2, TimeUnit.SECONDS)) {}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mergeDecompressedFiles(fragmentsMap);
		
		System.out.println("Finished decompression...");
	}

	private void mergeDecompressedFiles(Map<String,List<String>> fragmentsMap) {
		ZipMerge mergingAlgorithm = new ZipMerge(fragmentsMap);
		mergingAlgorithm.mergeHelper();		
	}
}
