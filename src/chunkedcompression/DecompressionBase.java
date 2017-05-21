package chunkedcompression;


public abstract class DecompressionBase {

	abstract protected void decompress(String inputPath, String outputPath);
	
	public void run(String inputPath, String outputPath)
	{
		decompress(inputPath, outputPath);
		
	}

}
