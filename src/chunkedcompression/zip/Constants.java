package chunkedcompression.zip;

/**
 * This class maintains all the constants which are used.
 * @author Sachin Parthasarathy
 *
 */
public class Constants {
	public static final String name = "Compressed";
	public static final String fragmentLabel = ".frag.";
	public static final String zipExtension = ".zip";
	public static final int noOfProcessors = Runtime.getRuntime().availableProcessors();
	public static final long jvmMemory = Runtime.getRuntime().freeMemory();
	

}
