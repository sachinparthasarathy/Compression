package chunkedcompression.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import chunkedcompression.CompressionBase;
import chunkedcompression.Constants;

/**
 * 
 * @author Sachin Parthasarathy
 *
 */
 
public class ZipCompression extends CompressionBase
{
	private ZipOutputStream zipOutputStream;
	private long zipIndex = 0;
	private long fileIndex = 0;
	private int maxSplitSize = 0;

	@Override
	protected void compress(List<File> files, String outputPath, int maxSplitsize) 
	{
		try
		{
			this.maxSplitSize = maxSplitsize; // giving room for zip headers
			addFilesToZip(files, outputPath);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This function adds the input file list to the zip archives. Each zip archive has an upper bound on its size.
	 * 
	 * @param files		List of files to be compressed
	 * @param outputPath		Path where the compressed zip archives will be stored
	 * @throws IOException		Throws an IO exception if the file operations fails
	 */
	private void addFilesToZip(List<File> files,String outputPath) throws IOException 
	{
		String zipFileName = outputPath + File.separator + Constants.name + zipIndex + Constants.zipExtension;
		OutputStreamWithLength chunkedFile = new OutputStreamWithLength(zipFileName);
		zipOutputStream = new ZipOutputStream(chunkedFile);
		//long jvmMemory = Runtime.getRuntime().freeMemory();
		
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
			
			ZipEntry entry =new ZipEntry(zipEntryPath);
			zipOutputStream.putNextEntry(entry);

			//long fileSize = file.length();
			//int bufferSize = (int)Math.min(jvmMemory, fileSize);
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
			
			inputFileStream.close();
		}

		zipOutputStream.closeEntry();
		zipOutputStream.finish();
	}
}