package chunkedcompression;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		
		compress(fileList, outputPath, maxSplitSize);
	}
	
	private void buildFileList(String inputPath)
	{
		File dir = new File(inputPath);
		File[] files = dir.listFiles();
		fileList.addAll(Arrays.asList(files));
		
	    for(File file :files)
		{
			if (true == file.isDirectory())
			{
				buildFileList(file.getAbsolutePath());
			}
		}
	}
	
	//abstract void close();
}