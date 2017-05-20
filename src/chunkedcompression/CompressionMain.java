package code;

import java.io.File;

public class CompressorMain {
	public static void main(String[] args) 
	{
		String inputPath = null;
		String outputPath  = null;
		int maxSplitsize = 0;
		boolean isValidInput = false;
		boolean isOutputPathInvalid = true;
		if(args.length != 3){
			System.out.println("Usage: java Driver inputPath outputPath "
					+ "maxSplitsize");
			System.exit(0);
		}

		if(args[0] == null){
			System.out.println("Please enter the Input file Path!!!");
			System.exit(0);
		}else{
			inputPath = args[0];
		}

		if(args[1] == null){
			System.out.println("Please enter the Output file Path!!!");
			System.exit(0);
		}else{
			outputPath = args[1];
		}
		
		if(args[2] == null){
			System.out.println("Please enter the maximum split size!!!");
			System.exit(0);
		}else{
			try
			{
				maxSplitsize = Integer.parseInt(args[2]);
				if(maxSplitsize > 0)
					 maxSplitsize = maxSplitsize * 1024 * 1024;
				else
					System.out.println("Please enter an integer number > 0!!!");
			}
			catch(NumberFormatException e)
			{
				System.out.println("Please enter a number");
				System.exit(0);
			}
		}

		// check if paths are valid
		File inputFile = new File(inputPath);
		File outputFile = new File(outputPath);

		if(!outputFile.exists())
			isOutputPathInvalid = outputFile.mkdirs();

		if(!isOutputPathInvalid)
		{
			System.out.println("Output Path invalid");
			System.exit(0);
		}

		if(inputFile.exists())
			isValidInput = true;

		if(isValidInput){
			long startTime = System.currentTimeMillis();
			long elapsedTime = 0L;
			Compressor compressor = new ZipCompressor();
			compressor.run(inputPath, outputPath,maxSplitsize);
			elapsedTime = System.currentTimeMillis();
			System.out.println("Took " + (elapsedTime - startTime)/1000 +" seconds");
		}else{
			System.out.println("Input Path invalid");
		}
	}

}