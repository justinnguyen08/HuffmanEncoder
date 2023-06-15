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

public class Decompress implements IHuffConstants{

	/* 
	 * pre: none
	 * post: writes the uncompressed bits to out 
	 * and returns how many bits were written
	 */
	public int decompress(BitInputStream in, BitOutputStream out, 
			TreeNode root) throws IOException {
		// start at root of tree
		TreeNode node = root;
		boolean read = true;
		int numBits = 0;
		while (read) {
			// read in one bit at a time
			int inbits = in.readBits(1);
			if (inbits == -1) {
				in.close();
				out.close();
				throw new IOException("Error reading the file.");
			}
			// inbits reads a 0, go left on the tree
			else if (inbits == 0) {
				node = node.getLeft();
			}
			// inbits read a 1, go right on the tree
			else if (inbits == 1) {
				node = node.getRight();
			}
			if (node.isLeaf()) {
				// reached pseudo_eof value, stop reading
				if (node.getValue() == PSEUDO_EOF) {
					read = false;
					in.close();
					out.close();
				}
				// read a value that is not = pseudo_eof value
				else {
					// write uncompressed bits
					out.writeBits(BITS_PER_WORD, inbits);
					numBits += BITS_PER_WORD;
					// go back to root of tree
					node = root;
				}
			}
		}
		return numBits;
	}
	
	/*
	 * pre: none
	 * post: reads all bits from in and builds frequencies
	 */
	public void readHeaderSCF(BitInputStream in, int[] frequencies) throws IOException {
		for (int index = 0; index < ALPH_SIZE; index++) {
			int inbits = in.readBits(BITS_PER_INT);
			if (inbits == -1) {
				throw new IOException("Header was not read correctly.");
			}
			// store frequency in frequencies
			frequencies[index] = inbits;
		}
		// only gonna be one pseudo_eof, increment this frequency by on
		frequencies[PSEUDO_EOF]++;
	}
	
	/*
	 * pre: none
	 * post: returns the root of tree
	 */
	public TreeNode readHeaderSTF(BitInputStream in) throws IOException {
		int inbits = in.readBits(1);
		// got to an internal node, still build tree recursively 
		// and storing left and right nodes
		if (inbits == 0) {
			return new TreeNode(readHeaderSTF(in), -1, readHeaderSTF(in));
		}
		// got to a leaf node, create a new treenode thats a leaf
		else if (inbits == 1) {
			return new TreeNode(in.readBits(BITS_PER_WORD + 1), 1);
		}
		// bit = -1, did not read header correctly
		else {
			throw new IOException("Header was not read correctly.");
		}
	}
}
