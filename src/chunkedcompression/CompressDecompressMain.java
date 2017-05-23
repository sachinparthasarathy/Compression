package chunkedcompression;


import chunkedcompression.zip.ZipCompression;
import chunkedcompression.zip.ZipDecompression;
import chunkedcompression.zip.ZipUtils;
import chunkedcompression.zip.multiThreaded.ThreadedZipCompression;

/**
 * Main driver program for chunked compression and decompression
 * @author Sachin Parthasarathy
 *
 */

public class CompressDecompressMain {
	public static void main(String[] args) 
	{
		String inputPath = null;
		String outputPath  = null;
		int maxSplitsize = 0;


		if(!(args.length == 3 || args.length == 2)){
			System.out.println("Usage for compression: "
					+ "java chunkedcompression.CompressDecompressMain inputPath outputPath "
					+ "maxSplitsize");
			System.out.println("Usage for decompression: "
					+ "java chunkedcompression.CompressDecompressMain inputPath outputPath ");
			System.exit(0);
		}

		ZipUtils ziputils = new ZipUtils();

		inputPath = ziputils.nullCheck(args[0],"Please enter the Input file Path!!!");
		outputPath = ziputils.nullCheck(args[1],"Please enter the Output file Path!!!");

		// check if paths are valid
		ziputils.inputPathCheck(inputPath);
		ziputils.outputPathCheck(outputPath);
		ziputils.checkOutputDirEmpty(outputPath);

		if(args.length == 3)
		{
			String maxSize = ziputils.nullCheck(args[2],"Please enter the maximum"
					+ "split file size!!!");
			maxSplitsize = ziputils.numberCheck(maxSize);
			/*
			 * Invoke the multi-threaded zip compression strategy
			 */
			//CompressionBase compressionAlgorithm = new ThreadedZipCompression();

			/*
			 *  Invoke the single-threaded zip compression strategy
			 */
			CompressionBase compressionAlgorithm = new ZipCompression();
			compressionAlgorithm.run(inputPath, outputPath, maxSplitsize);
		}
		else
		{
			// invoke the zip decompression strategy
			DecompressionBase decompressiongorithm = new ZipDecompression();
			decompressiongorithm.run(inputPath, outputPath);
		}
	}
}