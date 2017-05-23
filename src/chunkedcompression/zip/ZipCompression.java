package chunkedcompression.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import chunkedcompression.CompressionBase;

/**
 * This class implements the Chunked Zip compression algorithm
 *   
 * @author Sachin Parthasarathy
 *
 */
public class ZipCompression extends CompressionBase
{
	/**
	 * Output zip output stream
	 */
	private ZipOutputStream zipOutputStream;
	
	/**
	 * Index of current zip file written.
	 */
	private long zipIndex = 0;
	
	/**
	 * Fragment index of current file added to the zip output
	 */
	private long fileIndex = 0;
	
	/**
	 * Maximum size of a single zip archive
	 */
	private int maxSplitSize = 0;
	
	/**
	 * Total bytes of all the files
	 */
	private long totalBytes = 0;

	@Override
	/**
	 * Implements the compress method
	 * @param files		List of files to be compressed
	 * @param outputPath	Path where the compressed zip archives will be stored
	 * @param Maximum size of a single zip archive
	 */
	protected void compress(List<File> files, String outputPath, int maxSplitsize) 
	{
		try
		{
			long startTime = System.currentTimeMillis();
			long elapsedTime = 0L;
			System.out.println("Starting compression...");
			this.maxSplitSize = (int)(maxSplitsize * 1024 * 1024 * 0.96); // giving room for zip headers
			totalBytes = new ZipUtils().getTotalFileSize(files);
			addFilesToZip(files, outputPath);
			System.out.println("Finished compression of " + files.size() + " files.");
			elapsedTime = System.currentTimeMillis();
			System.out.println("Compression took " + (elapsedTime - startTime)/1000 + " seconds");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This function adds the input file list to the zip archives. Each zip archive has an upper bound on its size.
	 * Once the size if reached a new zip file is created. The size of compressed bytes written is provided by OutputStreamWithLength
	 * @param files		List of files to be compressed
	 * @param outputPath		Path where the compressed zip archives will be stored
	 * @throws IOException		Throws an IO exception if the file operations fails
	 */
	private void addFilesToZip(List<File> files,String outputPath) throws IOException 
	{
		// create a zip output stream
		String zipFileName = outputPath + File.separator + Constants.name + zipIndex + Constants.zipExtension;
		OutputStreamWithLength chunkedFile = new OutputStreamWithLength(zipFileName);
		zipOutputStream = new ZipOutputStream(chunkedFile);
		double totalBytesRead = 0L;
		int count = 10; 
		for(File file:files)
		{
			// reset the file index
			fileIndex = 0;
			String zipEntryPath = file.getAbsolutePath().substring(basePath.length() + 0);
			
			if(file.isDirectory())
			{
				ZipEntry entry =new ZipEntry(zipEntryPath+"/");
				zipOutputStream.putNextEntry(entry);
				zipOutputStream.closeEntry();
				continue;
			}			
			else
			{
				zipEntryPath = zipEntryPath + Constants.fragmentLabel + fileIndex++;
			}			

			// add the current file to the zip
			ZipEntry entry =new ZipEntry(zipEntryPath);
			zipOutputStream.putNextEntry(entry);

			byte[] buffer = new byte[1024];        	 

			FileInputStream inputFileStream = new FileInputStream(file);
			int len;  
			while ((len = inputFileStream.read(buffer)) > 0) 
			{   
				if((chunkedFile.getCurrentWriteLength() + len ) > maxSplitSize){
					// close current zip output stream
					zipOutputStream.closeEntry();
					zipOutputStream.finish();
					
					// reset the write length
					chunkedFile.setCurrentWriteLength(0);
					
					// create new zip output stream
					zipIndex += 1;
					zipFileName = outputPath+ File.separator + Constants.name + zipIndex + Constants.zipExtension;
					chunkedFile = new OutputStreamWithLength(zipFileName);
					zipOutputStream = new ZipOutputStream(chunkedFile);

					// add the current file to write remaining bytes
					zipEntryPath = file.getAbsolutePath().substring(basePath.length() + 0);
					zipEntryPath = zipEntryPath + Constants.fragmentLabel + fileIndex++;
					entry = new ZipEntry(zipEntryPath);
					zipOutputStream.putNextEntry(entry);

					// write the bytes to the zip output stream
					zipOutputStream.write(buffer, 0, len);
				}
				else
				{
					// write the bytes to the zip output stream
					zipOutputStream.write(buffer, 0, len);
				}
				
				//Show progress
				totalBytesRead += len;
				double progress = totalBytesRead / totalBytes;
				if (progress*100 > 10)
				{
					totalBytesRead = 0L;
					System.out.println("Finished " + count +"%");
					count += 10;
				}
			}						
			inputFileStream.close();
		}

		zipOutputStream.closeEntry();
		zipOutputStream.finish();
	}
}