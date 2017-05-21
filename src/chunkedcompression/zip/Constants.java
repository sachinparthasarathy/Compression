package chunkedcompression.zip;

public class Constants {
	public static final String name = "Compressed";
	public static final String fragmentLabel = ".frag.";
	public static final String zipExtension = ".zip";
	public static final int noOfProcessors = Runtime.getRuntime().availableProcessors();
	public static final long jvmMemory = Runtime.getRuntime().freeMemory();

}
