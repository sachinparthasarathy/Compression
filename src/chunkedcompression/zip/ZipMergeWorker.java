package chunkedcompression.zip;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * This class merges the fragments for each file in parallel
 * @author Sachin Parthasarathy
 *
 */
class ZipMergeWorker implements Runnable
{
	/**
	 * List of input fragments
	 */
	List<String> inputFile;
	
	/**
	 * Name of the finally merged file
	 */
	String outputFile;
	
	/**
	 * Number of fragments in each file
	 */
	int noOfFragments;

	public ZipMergeWorker(List<String> inputFile,String outputFile
			,int noOfFragments)
	{
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.noOfFragments = noOfFragments;
	}

	@Override
	/**
	 * Overrides the run method which performs the decompression
	 */
	public void run()
	{
		doMerge();
	}


	/**
	 * This function performs the actual merge
	 */
	private void doMerge()
	{
		//If its a single fragment, just rename the file and remove the fragment
		// label
		if(noOfFragments == 1)
		{
			File oldFile = new File(inputFile.get(0));
			File newFile = new File(outputFile);
			oldFile.renameTo(newFile);    			
		}
		else
		{
			//Combine all the fragments into a single output stream
			Path outFile=Paths.get(outputFile);
			try(FileChannel out=FileChannel.open(outFile, CREATE, WRITE)) {
				for(int ix=0, n=inputFile.size(); ix<n; ix++) {
					Path inFile=Paths.get(inputFile.get(ix));
					try(FileChannel in=FileChannel.open(inFile, READ)) {
						for(long p=0, l=in.size(); p<l; )
							p+=in.transferTo(p, l-p, out);
					}
					//Delete the fragments
					Files.delete(inFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}