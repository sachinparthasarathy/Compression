package code;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


class Merge implements Runnable
{
	List<String> inputFile;
	String outputFile;

	Merge(List<String> inputFile,String outputFile)
	{
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}

	public void run()
	{
		Path outFile=Paths.get(outputFile);
		//System.out.println("TO "+outFile);
		try(FileChannel out=FileChannel.open(outFile, CREATE, WRITE)) {
			for(int ix=0, n=inputFile.size(); ix<n; ix++) {
				Path inFile=Paths.get(inputFile.get(ix));
				//System.out.println(inFile+"...");
				try(FileChannel in=FileChannel.open(inFile, READ)) {
					for(long p=0, l=in.size(); p<l; )
						p+=in.transferTo(p, l-p, out);
				}
				File f = new File(inputFile.get(ix));
				f.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


public class MergeFilesHelper {
	private String OUTPUT_FOLDER = "";
	private HashMap<String,List<String>> map = new HashMap<>();
	private static final int MergeThreadPool = 15;


	public MergeFilesHelper(String outputFolder)
	{
		this.OUTPUT_FOLDER = outputFolder;
		walk(OUTPUT_FOLDER);
	}

	public void doMerge()
	{
		ExecutorService executor = Executors.newFixedThreadPool(MergeThreadPool);
		for (Map.Entry<String,List<String>> entry : map.entrySet())
		{
			List<String> toBemergedFiles = entry.getValue();
			if(toBemergedFiles.size() == 1)
			{
				File oldFile = new File(toBemergedFiles.get(0));
				File newFile = new File(entry.getKey());
				oldFile.renameTo(newFile);    			
			}
			else
			{
				
				Runnable worker = new Merge(toBemergedFiles,entry.getKey());
				executor.execute(worker);				
			}
		}
		executor.shutdown();
		try {
			while (!executor.awaitTermination(2, TimeUnit.SECONDS)) {}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void walk( String path ) {

		File root = new File( path );
		File[] list = root.listFiles();

		if (list == null) return;

		for ( File file : list ) {
			if ( file.isDirectory() ) {
				walk( file.getAbsolutePath() );
			}
			else {
				String fragmentedName = file.getName();
				int idx = fragmentedName.lastIndexOf(".") - 5;
				String originalName = file.getParent() + "\\"+ fragmentedName.substring(0,idx);
				List<String> mergedFiles = new LinkedList<>();
				if(map.containsKey(originalName))
					mergedFiles = map.get(originalName);
				mergedFiles.add(file.getParent()+"\\"+fragmentedName);
				map.put(originalName, mergedFiles);
			}
		}
	}
}