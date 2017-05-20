package code;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCompressor extends Compressor
{
	private ZipOutputStream zipOutputStream;
    private String name = "Compressed";
    private long zipIndex = 0;
    private long fileIndex = 0;
    private int maxSplitSize = 0;
    
	@Override
	void compress(List<File> files, String outputPath,int maxSplitsize) 
	{
		try
		{
			this.maxSplitSize = maxSplitsize;
			addFilesToZip(files,outputPath);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void addFilesToZip(List<File> files,String outputPath) throws IOException 
    {
		String zipFileName = outputPath+ Constants.seperator + name+zipIndex+".zip";
   	    ChunkedFile chunkedFile = new ChunkedFile(zipFileName);
   	    zipOutputStream = new ZipOutputStream(chunkedFile);
   	    //long jvmMemory = Runtime.getRuntime().freeMemory();
   	    for(File file:files)
        {
   	    	if(file.isDirectory())
	    		continue;
   	    	
   	    	 fileIndex = 0;
        	 String zipEntryPath = file.getAbsolutePath().substring(basePath.length() + 0);
        	 
        	 if(!file.isDirectory())
    	    	zipEntryPath = zipEntryPath + ".frag." + fileIndex++;
        	 else
        		 zipEntryPath = zipEntryPath +"/";
			 ZipEntry entry =new ZipEntry(zipEntryPath);
        	 zipOutputStream.putNextEntry(entry);
        	        	 
        	 //long fileSize = file.length();
        	 //int bufferSize = (int)Math.min(jvmMemory, fileSize);
        	 byte[] buffer = new byte[1024];        	 

        	 FileInputStream in = new FileInputStream(file);
             int len;  
             while ((len = in.read(buffer)) > 0) 
             {   
            	 if((chunkedFile.getCurrlen() + len )> maxSplitSize){
                     zipOutputStream.closeEntry();
                     zipOutputStream.finish();
                     chunkedFile.setCurrlen(0);
                     zipIndex ++;
                     zipFileName = outputPath+ Constants.seperator + name+zipIndex+".zip";
                     
                     chunkedFile = new ChunkedFile(zipFileName);
                	 zipOutputStream = new ZipOutputStream(chunkedFile);
                   	  
                	 zipEntryPath = file.getAbsolutePath().substring(basePath.length() + 0);
                	 zipEntryPath = zipEntryPath + ".frag." + fileIndex++;
        			 entry =new ZipEntry(zipEntryPath);
                	 zipOutputStream.putNextEntry(entry);
                	   
                	 zipOutputStream.write(buffer, 0, len);
            	 }
            	 else
            	 {
                	 zipOutputStream.write(buffer, 0, len);
            	 }
             }
             in.close();
        }
       
        zipOutputStream.closeEntry();
        zipOutputStream.finish();
          
    }
}