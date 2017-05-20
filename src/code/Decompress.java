package code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompress
{
    List<String> fileList;
    private static final String INPUT_ZIP_FILE = "/home/errol/output/Compressed2.zip";
    private static final String OUTPUT_FOLDER = "/home/errol/output/unzip";

    public static void main( String[] args )
    {
    	Decompress unZip = new Decompress();
    	unZip.deCompressZip(INPUT_ZIP_FILE,OUTPUT_FOLDER);
    }


    public void deCompressZip(String zipFile, String outputFolder){

     byte[] buffer = new byte[1024];

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
            File newFile = new File(outputFolder + File.separator + fileName);
            
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