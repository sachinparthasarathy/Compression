package chunkedcompression.zip;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class merges all the decompressed fragments into a single file
 * @author Sachin Parthasarathy
 *
 */
public class ZipMerge {
	/**
	 * This map contains the list of fragments for each file
	 */
	private Map<String,List<String>> fragmentsMap;
	
	/**
	 * Final output path of decompressed output
	 */
	private String outputPath;

	public ZipMerge(String outputPath)
	{
		this.fragmentsMap = new ConcurrentHashMap<>();
		this.outputPath = outputPath;
	}
	
	/**
	 * This function walks through the entire directory and stores all the
	 * fragments in a map for merging
	 * @param path Path of the decompressed output files
	 */
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
				//Get the non fragmented file name
				String originalName = file.getParent() + File.separator 
						+ fragmentedName.substring(0,idx);
				List<String> mergedFiles = new LinkedList<>();
				if(fragmentsMap.containsKey(originalName))
					mergedFiles = fragmentsMap.get(originalName);
				//Add the fragments to the map
				mergedFiles.add(file.getParent()+ File.separator  
						+fragmentedName);
				fragmentsMap.put(originalName, mergedFiles);
			}
		}
	}

	
	/**
	 * This function creates multiple threads and calls the 
	 * Worker class to merge the fragments
	 */
	public void mergeHelper()
	{
		walk(outputPath);
		//Execute the merge in parallel
		ExecutorService executor = Executors.newFixedThreadPool
				(Constants.noOfProcessors);		
		
		for (Map.Entry<String,List<String>> entry : fragmentsMap.entrySet())
		{
			List<String> toBemergedFiles = entry.getValue();

			// Sort on fragment index so that ordering is maintained
			fragmentSort(toBemergedFiles);

			Runnable worker = new ZipMergeWorker(toBemergedFiles,entry.getKey()
					,toBemergedFiles.size());
			executor.execute(worker);			
		}
		executor.shutdown();
		try {
			while (!executor.awaitTermination(2, TimeUnit.SECONDS)) {}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}	

	/**
	 * This function sorts the fragments for each file based on their 
	 * numbering
	 * @param toBemergedFiles List of all fragments for each file
	 */
	private void fragmentSort(List<String> toBemergedFiles)
	{
		Collections.sort(toBemergedFiles, new Comparator<String>() {
			@Override
			public int compare(String fragment1, String fragment2)
			{
				try
				{
					int idx1 = fragment1.lastIndexOf(".") + 1;
					int idx2 = fragment2.lastIndexOf(".") + 1;
					fragment1 = fragment1.substring(idx1);
					fragment2 = fragment2.substring(idx2);
					if(Integer.parseInt(fragment1) < Integer.parseInt(fragment2))
						return -1;
				}
				catch(Exception e)
				{					
				}
				return 1;

			}
		});
	}
}