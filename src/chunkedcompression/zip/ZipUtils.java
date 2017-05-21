package chunkedcompression.zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipUtils {
	public String nullCheck(String args,String errorMessage)
	{
		if(args == null){
			System.out.println(errorMessage);
			System.exit(0);
		}
		return args;
	}
	
	
	public int numberCheck(String args)
	{
		int number = 0;
		try
		{
			number = Integer.parseInt(args);
			if(number <= 0)
			{
				System.out.println("Please enter an integer number > 0!!!");
				System.exit(0);
			}
		}
		catch(NumberFormatException e)
		{
			System.out.println("Please enter a number");
			System.exit(0);
		}		
		return number;
	}
	
	public boolean PathCheck(String path)
	{
		File file = new File(path);
		if(!file.exists())
		{
			System.out.println("Input Path invalid");
			System.exit(0);
		}
		return true;
	}
	
	public void inputPathCheck(String path)
	{
		if(!PathCheck(path))
		{
			System.out.println("Input Path invalid");
			System.exit(0);
		}
	}
	
	public void outputPathCheck(String path)
	{
		boolean isOutputPathInvalid = false;
		if(!PathCheck(path))
		{
			isOutputPathInvalid = new File(path).mkdirs();	
		}
		if(isOutputPathInvalid)
		{
			System.out.println("Output Path invalid");
			System.exit(0);
		}		
	}
	
	public void checkOutputDirEmpty(String outputPath)
	{
		Path directory = Paths.get(outputPath);
		try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
			if(dirStream.iterator().hasNext())
			{
				System.out.println("Output Directory not empty");
				System.exit(0);
			}
	    } catch (IOException e) {
	    	System.out.println("Check output directory path");
		}
	}
}