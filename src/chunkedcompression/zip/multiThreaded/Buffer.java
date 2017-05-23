package chunkedcompression.zip.multiThreaded;

/**
 * This class defines a custom buffer data type which is used in Threaded
 * Compression
 * @author Sachin Parthasarathy
 *
 */

class Buffer
{
	/**
	 * Name of the fragment.Fragment is the smallest unit of data produced
	 */
	String fragmentName;
	
	/**
	 * Stores the actual fragment data
	 */
	byte[] buffer;
	
	/**
	 * Stores the length of the fragment
	 */
	int len;
	
	/**
	 * If the current entry is a file or a directory
	 */
	boolean isDirectory;
	
	public Buffer(String fragmentName, byte[] buffer, int len, 
			boolean isDirectory) {
		super();
		this.fragmentName = fragmentName;
		this.buffer = buffer;
		this.len = len;
		this.isDirectory = isDirectory;
	}
	public String getFragmentName() {
		return fragmentName;
	}
	public void setFragmentName(String fragmentName) {
		this.fragmentName = fragmentName;
	}
	public byte[] getBuffer() {
		return buffer;
	}
	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
}