package chunkedcompression;

/**
 * Base class for chunked decompression. Provides a hook for the specific algorithms.
 * @author Sachin Parthasarathy
 *
 */
public abstract class DecompressionBase {

	abstract protected void decompress(String inputPath, String outputPath);
	
	public void run(String inputPath, String outputPath)
	{
		decompress(inputPath, outputPath);
	}
}
