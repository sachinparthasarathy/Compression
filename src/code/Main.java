package code;

import java.io.File;

public class Main {


	public static void main(String[] args) 
	{
		 String inputPath = null;
		 String outputPath  = null;
		 boolean isValidInput = false;
		 boolean isOutputPathInvalid = true;
		 if(args.length > 0){
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
				 compressor.run(inputPath, outputPath);
				 elapsedTime = System.currentTimeMillis();
				 System.out.println(elapsedTime - startTime);
			 }else{
				 System.out.println("Input Path invalid");
			 }
		 }
		 else
		 {
			 System.out.println("Usage: java Driver inputPath outputPath");
		 }
	}

}