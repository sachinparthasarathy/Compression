package chunkedcompression;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for chunked compression. Provides a hook for the specific algorithms.
 * @author Sachin Parthasarathy
 *
 */
public abstract class  CompressionBase 
{
	protected String basePath = "";

	abstract protected void compress(List<File> file, String outputPath, int maxSplitSize); 

	List<File> fileList = new ArrayList<File>();

	public void run(String inputPath, String outputPath, int maxSplitSize)
	{
		// append trailing slash is not there
		inputPath = inputPath.charAt(inputPath.length() - 1) 
				== File.separatorChar?inputPath:inputPath+File.separator;

		basePath = inputPath;

		buildFileList(inputPath);

		// call the compress method
		compress(fileList, outputPath, maxSplitSize);
	}

	/**
	 * Creates a list of files in the input directory
	 * @param inputPath
	 */
	private void buildFileList(String inputPath)
	{
		try
		{
			File dir = new File(inputPath);
			File[] files = dir.listFiles();
			//Some files in Windows are not accessible
			if(null == files)
				return;
			fileList.addAll(Arrays.asList(files));

			for(File file :files)
			{
				if (true == file.isDirectory())
				{
					buildFileList(file.getAbsolutePath());
				}
			}
		}
		catch(Exception e){
			System.out.println("File enumeration went wrong..."+inputPath);			
		}
	}
}