
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import chunkedcompression.CompareDirectories;
import chunkedcompression.CompressionBase;
import chunkedcompression.DecompressionBase;
import chunkedcompression.zip.ZipCompression;
import chunkedcompression.zip.ZipDecompression;
import chunkedcompression.zip.multiThreaded.ThreadedZipCompression;

/**
 * This class performs unit tests on sample files for both single 
 * and multi-threaded  compression and decompression
 * @author Sachin Parthasarathy
 *
 */
public class TestCompressDecompress {

	/**
	 * Clear the temp folders
	 * @param folder
	 * @throws IOException
	 */
	private void empty(File folder) throws IOException {
		 File[] files = folder.listFiles();
		    if(files!=null) {
		        for(File f: files) {
		            if(f.isDirectory()) {
		            	empty(f);
		            } else {
		                f.delete();
		            }
		        }
		    }
		    folder.delete();
	}

	/**
	 * Run compression and decompression and verify if the directories match
	 * @param inputPath
	 * @param outputPath
	 * @param decompressOutputPath
	 * @param maxSplitSize
	 * @param compressionAlgorithm
	 * @throws IOException
	 */
	public void veriyEquals(String inputPath,String outputPath,
			String decompressOutputPath,int maxSplitSize
			,CompressionBase compressionAlgorithm) throws IOException {

		File f = new File(outputPath);
		empty(f);
		f.mkdirs();
		
		f = new File(decompressOutputPath);
		empty(f);
		f.mkdirs();

		compressionAlgorithm.run(inputPath,outputPath,maxSplitSize);

		DecompressionBase decompressionAlgorithm = new ZipDecompression();
		decompressionAlgorithm.run(outputPath,decompressOutputPath);
		CompareDirectories compare = new CompareDirectories();
		boolean equals = compare.EnumerateAndCompare(inputPath,decompressOutputPath); 
		assertEquals(equals, true);
	}

	@Test
	/**
	 * Unit test on sample1 with image files
	 * @throws IOException
	 */
	public void test1() throws IOException {   
		String inputPath = new File("test/resources/input/test1").getAbsolutePath();
		String outputPath = System.getProperty("java.io.tmpdir")
				+ File.separator + "zipOutput1";
		String decompressOutputPath = System.getProperty("java.io.tmpdir")
				+ File.separator + "decompressOutput1";
		int maxSplitSize = 2;

		veriyEquals(inputPath, outputPath, decompressOutputPath, maxSplitSize
				,  new ZipCompression());		
	}
	
	@Test
	/**
	 * Unit test on sample 2 with empty files, directories and pdf
	 * @throws IOException
	 */
	public void test2() throws IOException {   
		String inputPath = new File("test/resources/input/test2").getAbsolutePath();
		String outputPath = System.getProperty("java.io.tmpdir")
				+ File.separator + "zipOutput2";
		String decompressOutputPath = System.getProperty("java.io.tmpdir")
				+ File.separator + "decompressOutput2";
		int maxSplitSize = 2;

		veriyEquals(inputPath, outputPath, decompressOutputPath, maxSplitSize
				, new ThreadedZipCompression());		
	}

	
	public static void main(String[] args) throws Exception {                    
	       JUnitCore.main(
	         "TestCompressDecompress");
	}
}