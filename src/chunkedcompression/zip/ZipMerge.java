package chunkedcompression.zip;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ZipMerge {
	private Map<String,List<String>> fragmentsMap = new ConcurrentHashMap<>();

	public ZipMerge(Map<String,List<String>> fragmentsMap)
	{
		this.fragmentsMap = fragmentsMap;
	}

	public void mergeHelper()
	{
		ExecutorService executor = Executors.newFixedThreadPool
				(Constants.noOfProcessors);
		for (Map.Entry<String,List<String>> entry : fragmentsMap.entrySet())
		{
			List<String> toBemergedFiles = entry.getValue();

			// Sort on fragment index
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
	
	private void fragmentSort(List<String> toBemergedFiles)
	{
		Collections.sort(toBemergedFiles, new Comparator<String>() {
			@Override
			public int compare(String fragment1, String fragment2)
			{
				int idx1 = fragment1.lastIndexOf(".") + 1;
        		int idx2 = fragment2.lastIndexOf(".") + 1;
        		fragment1 = fragment1.substring(idx1);
        		fragment2 = fragment2.substring(idx2);
            	if(Integer.parseInt(fragment1) < Integer.parseInt(fragment2))
            		return -1;
                return 1;
			}
		});
	}
}