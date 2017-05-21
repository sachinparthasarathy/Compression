package chunkedcompression.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class ZipDecompressWorker implements Runnable
{
	private String inputZipFile = "";
	private String outputFolder = "";
	private Map<String,List<String>> fragmentsMap = new ConcurrentHashMap<>();

	public ZipDecompressWorker(String inputFile,String outputFolder,
			Map<String,List<String>> fragmentsMap)
	{
		this.inputZipFile = inputFile;
		this.outputFolder = outputFolder;
		this.fragmentsMap = fragmentsMap;
	}

	@Override
	public void run()
	{
		deCompressZip(inputZipFile);
	}

	public void deCompressZip(String zipFile){

		byte[] buffer = new byte[1024];   
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
				File newFile = new File(outputFolder + File.separator + fileName);
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fileOpStream = new FileOutputStream(newFile);
				int len;
				while ((len = zipInputStream.read(buffer)) > 0) {
					fileOpStream.write(buffer, 0, len);
				}

				fileOpStream.close();
				addToMap(newFile);
				zipEntry = zipInputStream.getNextEntry();
			}
			zipInputStream.closeEntry();
			zipInputStream.close();

		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	private void addToMap(File file)
	{
		String fragmentedName = file.getName();
		int idx = fragmentedName.lastIndexOf(".") - 5;
		String originalName = file.getParent() + File.separator 
				+ fragmentedName.substring(0,idx);
		List<String> mergedFiles = new LinkedList<>();
		if(fragmentsMap.containsKey(originalName))
			mergedFiles = fragmentsMap.get(originalName);
		mergedFiles.add(file.getParent()+ File.separator 
				+fragmentedName);
		fragmentsMap.put(originalName, mergedFiles);
	}
}