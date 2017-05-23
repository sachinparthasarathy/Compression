package chunkedcompression.zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * This class contains general utility functions used throughout the application
 * @author Sachin Parthasarathy
 *
 */
public class ZipUtils {
	
	/**
	 * This function checks if the arguments passed are null and displays
	 * the error message
	 * @param args
	 * @param errorMessage
	 * @return
	 */
	public String nullCheck(String args,String errorMessage)
	{
		if(args == null){
			System.out.println(errorMessage);
			System.exit(0);
		}
		return args;
	}
	
	/**
	 * This function checks if the argument passed is a number
	 * @param args
	 * @return the converted number
	 */
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
	
	/**
	 * This function checks if the path is valid
	 * @param path
	 * @return if the path is a valid path
	 */
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
	
	/**
	 * Checks validity of input path
	 * @param path
	 */
	public void inputPathCheck(String path)
	{
		if(!PathCheck(path))
		{
			System.out.println("Input Path invalid");
			System.exit(0);
		}
	}
	
	/**
	 * Checks validity of output path.If not,it creates the directory
	 * @param path
	 */
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
	
	/**
	 * Checks if the output directory is empty
	 * @param outputPath
	 */
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
	
	/**
	 * Returns the total file size in bytes
	 * @param files
	 * @return total file size
	 */
	public long getTotalFileSize(List<File> files)
	{
		long totalSize = 0L;
		for(File f : files)
			totalSize += f.length();
		return totalSize;
	}
}