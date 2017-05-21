
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

public class TestCompressDecompress {

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

	public void veriyEquals(String inputPath,String outputPath,
			String decompressOutputPath,int maxSplitSize) throws IOException {

		File f = new File(outputPath);
		empty(f);
		f.mkdirs();
		
		f = new File(decompressOutputPath);
		empty(f);
		f.mkdirs();

		CompressionBase compressionAlgorithm = new ZipCompression();
		compressionAlgorithm.run(inputPath,outputPath,maxSplitSize);

		DecompressionBase decompressionAlgorithm = new ZipDecompression();
		decompressionAlgorithm.run(outputPath,decompressOutputPath);
		CompareDirectories compare = new CompareDirectories();
		boolean equals = compare.EnumerateAndCompare(inputPath,decompressOutputPath); 
		assertEquals(equals, true);
	}

	@Test
	public void test1() throws IOException {   
		String inputPath = new File("test/resources/input/test1").getAbsolutePath();
		String outputPath = System.getProperty("java.io.tmpdir")
				+ File.separator + "zipOutput1";
		String decompressOutputPath = System.getProperty("java.io.tmpdir")
				+ File.separator + "decompressOutput1";
		int maxSplitSize = 2;

		veriyEquals(inputPath, outputPath, decompressOutputPath, maxSplitSize);		
	}
	
	@Test
	public void test2() throws IOException {   
		String inputPath = new File("test/resources/input/test2").getAbsolutePath();
		String outputPath = System.getProperty("java.io.tmpdir")
				+ File.separator + "zipOutput2";
		String decompressOutputPath = System.getProperty("java.io.tmpdir")
				+ File.separator + "decompressOutput2";
		int maxSplitSize = 2;

		veriyEquals(inputPath, outputPath, decompressOutputPath, maxSplitSize);		
	}

	
	public static void main(String[] args) throws Exception {                    
	       JUnitCore.main(
	         "TestCompressDecompress");
	}
}