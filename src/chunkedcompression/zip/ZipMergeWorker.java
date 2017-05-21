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

class ZipMergeWorker implements Runnable
{
	List<String> inputFile;
	String outputFile;
	int noOfFiles;

	public ZipMergeWorker(List<String> inputFile,String outputFile,int noOfFiles)
	{
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.noOfFiles = noOfFiles;
	}

	public void run()
	{
		doMerge();
	}


	private void doMerge()
	{
		if(noOfFiles == 1)
		{
			File oldFile = new File(inputFile.get(0));
			File newFile = new File(outputFile);
			oldFile.renameTo(newFile);    			
		}
		else
		{
			Path outFile=Paths.get(outputFile);
			try(FileChannel out=FileChannel.open(outFile, CREATE, WRITE)) {
				for(int ix=0, n=inputFile.size(); ix<n; ix++) {
					Path inFile=Paths.get(inputFile.get(ix));
					try(FileChannel in=FileChannel.open(inFile, READ)) {
						for(long p=0, l=in.size(); p<l; )
							p+=in.transferTo(p, l-p, out);
					}
					Files.delete(inFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}