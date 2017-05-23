# Chunked Compression

Project description
--------------------
The project implements a single threaded and multi-threaded solution to zip
compression and decompression using jdk's zip implementation.

CompressionBase: Base class for a compression algorithm. Provides a hook named compress.

DecompressionBase: Base class for a decompression algorithm. Provides a hook named decompress.

ZipCompression : Extends CompressionBase for a single threaded compression algorithm.

ThreadedZipCompression : Extends CompressionBase for a multi threaded compression algorithm.

ZipDecompression : Extends DecompressionBase for a multi threaded decompression algorithm.

Unit Tests
--------------
Simple unit tests have been added in test folder.


How to run?
--------------------
For compression,
1. Multi threaded compression
java -cp CompressDecompressThreaded.jar chunkedcompression.CompressionMain inputDirectory outputDirectory maxSplitSize

2. Single threaded compression
java -cp CompressDecompress.jar chunkedcompression.CompressionMain inputDirectory outputDirectory maxSplitSize


For multi-threaded decompression,
java -cp CompressDecompressThreaded.jar chunkedcompression.DecompressionMain zipinputDirectory decompressedoutputDirectory


For verification/Difference checking,
java -cp CompressDecompressThreaded.jar chunkedcompression.CompareDirectories originalInputDirectory decompressedoutputDirectory
 


Statistics
------------

Multi-threaded 
File size	        Compression time	 
2GB(38937 files)         184sec  
10GB(43 files)           330sec  

Single-threaded
File size	        Compression time
2GB(38937 files)         220sec
10GB(43 files)           707sec

Decompression time for single and multi-threaded
 File size	        Decompression time
 2GB(38937 files)         110sec
 10GB(43 files)           503sec
 


Implementation in a nutshell
-------------------------------
Both the single and multi-threaded implementations use OutstreamWithLength as the data sink. The main idea is to keep track of the bytes written so far. When a byte array is written to a ZipOutputStream it uses a FilteredOutputStream to write the zipped bytes. The final write call is on an OutputStream. OutputStreamWithLength extends the OutputStream class and increments the bytes written so far. It uses a random access file to write out the bytes.


Single threaded chunked compression
----------------------------------
This method goes file by file. For each file, it reads a byte buffer and writes it to the zip output stream. If the stream has space it writes to the current stream else it creates new stream.

Multi-threaded chunked compression
-----------------------------------
This method is implemented as a producer-consumer problem. Producer thread keeps reading bytes file by file and enqueuing it. The consumer process retrieves these bytes and writes it to a zip output stream. Again if the stream has space it writes to the current stream else it creates new stream.
