package chunkedcompression;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import chunkedcompression.zip.ZipUtils;

/**
 * 
 * @author Sachin Parthasarathy
 * 
 */
public class CompareDirectories {

	public static void main(String[] args) {
		String inputPath = null;
		String outputPath  = null;
		if(args.length != 2){
			System.out.println("Usage: java chunkedcompression.CompareDirectories inputPath outputPath");
			System.exit(0);
		}

		ZipUtils ziputils = new ZipUtils();

		inputPath = ziputils.nullCheck(args[0],"Please enter the Input file Path!!!");
		outputPath = ziputils.nullCheck(args[1],"Please enter the Output file Path!!!");			

		// check if paths are valid
		if(!new File(inputPath).exists())
		{
			System.out.println("Input Directory doesnt exist");
			System.exit(0);
		}
		if(!new File(outputPath).exists())
		{
			System.out.println("Output Directory doesnt exist");
			System.exit(0);
		}
		System.out.println("Input directory :"+inputPath);
		System.out.println("Output directory :"+outputPath);
		boolean compareResult = false;
		try {
			compareResult = new CompareDirectories().EnumerateAndCompare(inputPath, outputPath);
		} catch (IOException e) {
			System.out.println("Diff failed...!!");
		}

		if(compareResult)
			System.out.println("Zipped output and decompress output match");
		else
			System.out.println("Directories not equal");
	}
	
	public boolean EnumerateAndCompare(String dir1, String dir2) throws IOException
	{
		boolean isCompare = true;
		File[] fileList1 = new File(dir1).listFiles();
		File[] fileList2 = new File(dir2).listFiles();

		Arrays.sort(fileList1);
		Arrays.sort(fileList2);

		if(fileList1.length != fileList2.length)
			return false;

		for(int i = 0; i<fileList1.length; i++)
		{
			if((fileList1[i].isDirectory() && !fileList2[i].isDirectory()) ||(!fileList1[i].isDirectory() && fileList2[i].isDirectory()))
				return false;
			if (true == fileList1[i].isDirectory())
			{
				isCompare &= EnumerateAndCompare(fileList1[i].getAbsolutePath(), fileList2[i].getAbsolutePath());
			}
			else
				isCompare &= compare(fileList1[i], fileList2[i]);
		}

		return isCompare;
	}

	private final boolean compare(final File file1, final File file2) throws IOException {
		
		Path filea = Paths.get(file1.getAbsolutePath());
		Path fileb = Paths.get(file2.getAbsolutePath());
		
	    if (Files.size(filea) != Files.size(fileb)) {
	        return false;
	    }
	    final long size = Files.size(filea);
	    final int mapspan = 4 * 1024 * 1024;
	    try (FileChannel chana = (FileChannel)Files.newByteChannel(filea);
	            FileChannel chanb = (FileChannel)Files.newByteChannel(fileb)) {
	        for (long position = 0; position < size; position += mapspan) {
	            MappedByteBuffer mba = mapChannel(chana, position, size, mapspan);
	            MappedByteBuffer mbb = mapChannel(chanb, position, size, mapspan);

	            if (mba.compareTo(mbb) != 0) {
	                return false;
	            }
	        }
	    }
	    return true;
	}
	
	private MappedByteBuffer mapChannel(FileChannel channel, long position, long size, int mapspan) throws IOException {
	    final long end = Math.min(size, position + mapspan);
	    final long maplen = (int)(end - position);
	    return channel.map(MapMode.READ_ONLY, position, maplen);
	}
}