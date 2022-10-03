package chunkedcompression.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This is a worker class which performs the actual decompression in parallel
 * @author Sachin Parthasarathy
 *
 */
class ZipDecompressWorker implements Runnable
{
	/**
	 * Path where the compressed zip archives are stored
	 */
	private String inputZipFile = "";
	
	/**
	 * Path where the decompressed output will be stored
	 */
	private String outputFolder = "";

	public ZipDecompressWorker(String inputFile,String outputFolder)
	{
		this.inputZipFile = inputFile;
		this.outputFolder = outputFolder;
	}

	@Override
	/**
	 * Overrides the run method which performs the decompression
	 */
	public void run()
	{
		deCompressZip(inputZipFile);
	}

	/**
	 * This functions implements the decompression function in parallel
	 * @param zipFile The zip file to be decompressed
	 */
	public void deCompressZip(String zipFile){

		//Read 1024 byte chunks at a time
		byte[] buffer = new byte[1024];   
		String fileNameTemp = "";
		try{
			File folder = new File(outputFolder);    	

			if(!folder.exists()){
				folder.mkdir();
			}
			ZipInputStream zipInputStream =
					new ZipInputStream(new FileInputStream(zipFile));

			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while(zipEntry!=null){

				String fileName = zipEntry.getName();
				fileNameTemp = fileName;
				File newFile = new File(outputFolder, fileName);
				if(!newFile.toPath().normalize().startsWith(outputFolder)) {
					throw new IOException("Bad zip entry");
				}
				//Create the parent directories
				new File(newFile.getParent()).mkdirs();
				//If current entry is a directory, create a directory
				if(fileName.endsWith("/"))
				{
					newFile.mkdir();
					zipEntry = zipInputStream.getNextEntry();
					continue;
				}
				//Create file output stream for writing the decompressed file.
				FileOutputStream fileOpStream = new FileOutputStream(newFile);
				int len;
				while ((len = zipInputStream.read(buffer)) > 0) {
					fileOpStream.write(buffer, 0, len);
				}
				fileOpStream.close();
				//Get next entry from the zip
				zipEntry = zipInputStream.getNextEntry();
			}
			zipInputStream.closeEntry();
			zipInputStream.close();

		}catch(IOException ex){
			System.out.println(fileNameTemp + " "+zipFile);
			ex.printStackTrace();
		}
	}
}
