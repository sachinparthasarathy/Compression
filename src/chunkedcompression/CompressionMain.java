package chunkedcompression;

import chunkedcompression.zip.ZipCompression;
import chunkedcompression.zip.ZipUtils;

/**
 * Main driver program for chunked compression
 * @author Sachin Parthasarathy
 *
 */

public class CompressionMain {
	public static void main(String[] args) 
	{
		String inputPath = null;
		String outputPath  = null;
		int maxSplitsize = 0;	
		if(args.length != 3){
			System.out.println("Usage: java chunkedcompression.CompressionMain inputPath outputPath "
					+ "maxSplitsize");
			System.exit(0);
		}

		ZipUtils ziputils = new ZipUtils();

		inputPath = ziputils.nullCheck(args[0],"Please enter the Input file Path!!!");
		outputPath = ziputils.nullCheck(args[1],"Please enter the Output file Path!!!");
		String maxSize = ziputils.nullCheck(args[2],"Please enter the maximum"
				+ "split file size!!!");
		maxSplitsize = ziputils.numberCheck(maxSize);				

		// check if paths are valid
		ziputils.inputPathCheck(inputPath);
		ziputils.outputPathCheck(outputPath);
		ziputils.checkOutputDirEmpty(outputPath);

		long startTime = System.currentTimeMillis();
		long elapsedTime = 0L;

		// Invoke the zip compression strategy
		CompressionBase compressionAlgorithm = new ZipCompression();
		compressionAlgorithm.run(inputPath, outputPath, maxSplitsize);

		elapsedTime = System.currentTimeMillis();
		System.out.println("Compression took " + (elapsedTime - startTime)/1000 + " seconds");
	}
}