package chunkedcompression;


import chunkedcompression.zip.ZipDecompression;
import chunkedcompression.zip.ZipUtils;

/**
 * Main driver program for chunked decompression
 * @author Sachin Parthasarathy
 *
 */

public class DecompressionMain
{
	public static void main( String[] args )
	{
		String inputPath = null;
		String outputPath = null;

		if(args.length != 2){
			System.out.println("Usage: java chunkedcompression.DecompressionMain inputPath outputPath ");
			System.exit(0);
		}

		ZipUtils ziputils = new ZipUtils();
		inputPath = ziputils.nullCheck(args[0],"Please enter the Input file Path!!!");
		outputPath = ziputils.nullCheck(args[1],"Please enter the Output file Path!!!");

		// check if paths are valid
		ziputils.inputPathCheck(inputPath);
		ziputils.outputPathCheck(outputPath);
		ziputils.checkOutputDirEmpty(outputPath);				

		// invoke the zip decompression strategy
		DecompressionBase decompressiongorithm = new ZipDecompression();
		decompressiongorithm.run(inputPath, outputPath);		
	}	
}