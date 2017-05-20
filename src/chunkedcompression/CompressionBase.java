package code;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class  Compressor 
{
	protected String basePath = "";
	
	abstract void compress(List<File> file, String outputPath,int maxSplitSize); 
	
	List<File> fileList = new ArrayList<File>();
	
	public void run(String inputPath, String outputPath,int maxSplitSize)
	{
		inputPath = inputPath.charAt(inputPath.length()-1) 
				== File.separatorChar?inputPath:inputPath+File.separator;
		basePath = inputPath;
		EumerateAndCompress(inputPath, outputPath);
		compress(fileList, outputPath,maxSplitSize);
		
		close();
	}
	
	private void EumerateAndCompress(String inputPath, String outputPath)
	{
		File dir = new File(inputPath);
		File[] files = dir.listFiles();
		fileList.addAll(Arrays.asList(files));
	    for(File file :files)
		{
			if (true == file.isDirectory())
			{
				EumerateAndCompress(file.getAbsolutePath(), outputPath);
			}
		}
	}
	
	void close() {}
}