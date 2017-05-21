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
			System.out.println("Starting compression...");
			this.maxSplitSize = (int)(maxSplitsize * 1024 * 1024 * 0.97); // giving room for zip headers
			addFilesToZip(files, outputPath);
			System.out.println("Finished compression of " + files.size() + " files.");
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
		int count = 0;
		for(File file:files)
		{
			if(file.isDirectory())
				continue;

			// reset the file index
			fileIndex = 0;
		
			String zipEntryPath = file.getAbsolutePath().substring(basePath.length() + 0);

			if(!file.isDirectory())
				zipEntryPath = zipEntryPath + Constants.fragmentLabel + fileIndex++;
			else
				zipEntryPath = zipEntryPath +"/";

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
			}
			
			count += 1;
			
			if (count % 10 == 0)
			{
				System.out.println("Finished " + count + " of " + files.size() + " files...");
			}
			
			inputFileStream.close();
		}

		zipOutputStream.closeEntry();
		zipOutputStream.finish();
	}
}