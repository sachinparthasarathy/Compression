package chunkedcompression;

import chunkedcompression.zip.ZipDecompression;
import chunkedcompression.zip.ZipUtils;

public class DecompressionMain
{
	public static void main( String[] args )
	{
		String inputPath = null;
		String outputPath = null;

		if(args.length != 2){
			System.out.println("Usage: java DecompressMain inputPath outputPath ");
			System.exit(0);
		}

		ZipUtils ziputils = new ZipUtils();
		inputPath = ziputils.nullCheck(args[0],"Please enter the Input file Path!!!");
		outputPath = ziputils.nullCheck(args[1],"Please enter the Output file Path!!!");

		// check if paths are valid
		ziputils.inputPathCheck(inputPath);
		ziputils.outputPathCheck(outputPath);
		ziputils.checkOutputDirEmpty(outputPath);		

		long startTime = System.currentTimeMillis();
		long elapsedTime = 0L;

		DecompressionBase decompressiongorithm = new ZipDecompression();
		decompressiongorithm.run(inputPath, outputPath);

		elapsedTime = System.currentTimeMillis();
		System.out.println("Decompression took " + (elapsedTime - startTime)/1000 +" seconds");
	}	
}