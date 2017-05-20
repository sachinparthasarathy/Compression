package code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DecompressMain
{
	private static final String INPUT_ZIP_FOLDER = "C:\\interview_prep\\ZipOutput";
	private static final String OUTPUT_FOLDER = "C:\\interview_prep\\DecompressOutput";

	private static final int DecompressThreadPool = 25;

	public static void main( String[] args )
	{
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0L;
		Constants.jvmMemory = Runtime.getRuntime().freeMemory();
		decompress();		
		MergeFilesHelper m = new MergeFilesHelper(OUTPUT_FOLDER);
		m.doMerge();
		elapsedTime = System.currentTimeMillis();
		System.out.println(elapsedTime - startTime);
	}

	public static void decompress()
	{
		File folder = new File(INPUT_ZIP_FOLDER);  	
		File[] files = folder.listFiles();

		ExecutorService executor = Executors.newFixedThreadPool(DecompressThreadPool);

		for(File file :files)
		{
			String fileName = file.getName();
			Runnable worker = new DecompressWorker(INPUT_ZIP_FOLDER + "\\"+fileName,
					OUTPUT_FOLDER);
			executor.execute(worker);			
		}
		executor.shutdown();
		try {
			while (!executor.awaitTermination(2, TimeUnit.SECONDS)) {}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
}

class DecompressWorker implements Runnable
{
	private String INPUT_ZIP_FILE = "";
	private String OUTPUT_FOLDER = "";

	public DecompressWorker(String inputFile,String outputFolder)
	{
		this.INPUT_ZIP_FILE = inputFile;
		this.OUTPUT_FOLDER = outputFolder;
	}

	@Override
	public void run()
	{
		deCompressZip(INPUT_ZIP_FILE);
	}

	public void deCompressZip(String zipFile){

		int bufferSize = (int)Math.min(Constants.jvmMemory, new File(zipFile).length());
		byte[] buffer = new byte[bufferSize];   
		try{
			File folder = new File(OUTPUT_FOLDER);    	

			if(!folder.exists()){
				folder.mkdir();
			}
			ZipInputStream zipInputStream =
					new ZipInputStream(new FileInputStream(zipFile));

			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while(zipEntry!=null){

				String fileName = zipEntry.getName();
				File newFile = new File(OUTPUT_FOLDER + File.separator + fileName);
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fileOpStream = new FileOutputStream(newFile);
				int len;
				while ((len = zipInputStream.read(buffer)) > 0) {
					fileOpStream.write(buffer, 0, len);
				}

				fileOpStream.close();
				zipEntry = zipInputStream.getNextEntry();
			}
			zipInputStream.closeEntry();
			zipInputStream.close();

		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}