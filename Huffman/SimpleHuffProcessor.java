/*  Student information for assignment:
 *
 *  On MY honor, Justin Nguyen, this programming assignment is MY own work
 *  and I have not provided this code to any other student.
 *
 *  Number of slip days used: 2
 *
 *  Student 1 (Student whose turnin account is being used)
 *  UTEID: jn28429
 *  email address: justinguyen08@gmail.com
 *  Grader name: Trisha
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SimpleHuffProcessor implements IHuffProcessor {

    private int[] frequencies;
    private int headerFormat;
    private IHuffViewer myViewer;
    private HuffManCodeTree tree;
    private Compress compressor;
    private Decompress decompressor;
    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it int one as needed.
     * @param in is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind of
     * header to use, standard count format, standard tree format, or
     * possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     * Note, to determine the number of
     * bits saved, the number of bits written includes
     * ALL bits that will be written including the
     * magic number, the header format number, the header to
     * reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */ 
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
    	this.headerFormat = headerFormat;
    	frequencies = new int[ALPH_SIZE+1];
    	compressor = new Compress();
    	int[] origBits = new int[1];
    	int[] newBits = new int[BITS_PER_INT + BITS_PER_INT];
    	tree = new HuffManCodeTree(frequencies);
    	// count bits in original file
    	compressor.countBits(new BitInputStream(in), frequencies, origBits);
    	// if STF header
    	if (headerFormat == STORE_TREE) {
    		// add size of tree and BITS_PER_INT
    		newBits[0] += tree.size(compressor) + BITS_PER_INT;
    	}
    	// if SCF header
    	else if (headerFormat == STORE_COUNTS) {
    		// add SCF header size 
    		newBits[0] += ALPH_SIZE + BITS_PER_INT;
    	}
    	// if STF header
    	
    	else {
    		in.close();
    		throw new IOException("File compression that is not STF or SCF not implemented.");
    	}
    	// store each huffman value into a new map
    	tree.fillBitMap("", newBits);
    	// return number of bits saved
        return origBits[0] - newBits[0];
    }

    /**
	 * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br> pre: <code>preprocessCompress</code> must be called before this method
     * @param in is the stream being compressed (NOT a BitInputStream)
     * @param out is bound to a file/stream to which bits are written
     * for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than the input file.
     * If this is false do not create the output file if it is larger than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
    	if (force) {
    		BitInputStream bitIn = new BitInputStream(in);
    		BitOutputStream bitOut = new BitOutputStream(out);
    		// write magic number and headerFormat
    		bitOut.writeBits(BITS_PER_INT, MAGIC_NUMBER);
    		bitOut.writeBits(BITS_PER_INT, headerFormat);
    		int[] countBits = new int [BITS_PER_INT + BITS_PER_INT];
    		if (headerFormat == STORE_TREE) {
    			// write size of tree
    			bitOut.writeBits(BITS_PER_INT, tree.size(compressor));
    			// add BITS_PER_INT for the size of the tree
    			countBits[0] += BITS_PER_INT;
    			// write headerSTF
    			tree.makeHeaderSTF(bitOut, countBits, compressor);
    		}
    		// write headerSCF if headerFormat is SCF
    		else if (headerFormat == STORE_COUNTS) {
    			compressor.makeHeaderSCF(bitOut, countBits, frequencies);
    		}
    		else {
    			bitIn.close();
    			bitOut.close();
    			throw new IOException("File compression that is not STF or SCF not implemented.");
    		}
    		compressor.compress(bitIn, bitOut, tree, countBits);
    		return countBits[0];
    	}
    	// force is not true, cannot compress file	
    	myViewer.showError("Will not force compression");
        return 0;
    }

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     * @param in is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
    	decompressor = new Decompress();
    	BitInputStream bitIn = new BitInputStream(in);
    	BitOutputStream bitOut = new BitOutputStream(out);
    	// throw error if file not compressed
    	if (bitIn.readBits(BITS_PER_INT) != MAGIC_NUMBER) {
    		bitIn.close();
    		bitOut.close();
    		throw new IOException("File has not been compressed.");
    	}
    	int format = bitIn.readBits(BITS_PER_INT);
    	if (format == STORE_TREE) {
    		// read header
    		bitIn.readBits(BITS_PER_INT);
    		// store root of huffman tree
    		tree = new HuffManCodeTree(decompressor, bitIn);
    	}
    	else if (format == STORE_COUNTS) {
    		// read header
    		decompressor.readHeaderSCF(bitIn, frequencies);
    		// rebuild huffman tree
    		tree = new HuffManCodeTree(frequencies);
    	}
    	else {
    		bitIn.close();
    		bitOut.close();
    		throw new IOException("File compression that is not STF or SCF not implemented.");
    	}
	    return decompressor.decompress(bitIn, bitOut, tree.root);
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s){
        if(myViewer != null)
            myViewer.update(s);
    }
}
