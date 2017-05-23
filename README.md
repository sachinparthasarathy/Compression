# Compression
compression
Project hierarchy
--------------------
CompressionBase: Base class for a compression algorithm. Provides a hook named compress(..
DecompressionBase
...

How to run?

For compression, java -jar
For decompression, 

For single threaded compression,
For single threaded decompression,


Statistics
---------------

Multi-threaded
File size	Compression time	Decompression time

Single-threaded
File size	Compression time	Decompression time


Implementation in a nutshell
----------------------------------
This project implements Chunked Zip compression. There are two variants of the implementation:
1. Single thread chunked compression
2. Multi-threaded chucked compression

The decompression is done in a multi-threaded way for both the variants.

Both the implementations uses OutstreamWithLength as the data sink. The main idea is to keep track of the bytes written so far. When a byte array is written to a ZipOutputStream it uses a FilteredOutputStream too write the zipped bytes.
The final write call is on an OutputStream. OutputStreamWithLength extends the OutputStream class and increments the bytes written so far. It uses a random access file to write out the bytes.


Single thread chunked compression

This method goes file by file. For each file, it reads a byte buffer and writes it to the zip output stream. If the stream has space it writes to the current stream else it creates new stream.

Multi-threaded chunked compression

This method is implemented as a producer-consumer problem. Producer thread keeps reading bytes file by file and enqueing it. The consumer process retreives these bytes and writes it to a zip output stream. 
Again if the stream has space it writes to the current stream else it creates new stream.
