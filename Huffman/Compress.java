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

public class Compress implements IHuffConstants{
	
	/*
	 * pre: none
	 * post: creating compressed data using bit sequence in bitmaps
	 */
	public void compress(BitInputStream in, BitOutputStream out, 
			HuffManCodeTree tree, int[] countBits) throws IOException {
		int inbits = in.readBits(BITS_PER_WORD);
		while (inbits != -1) {
			String bitCode = tree.getBitMap().get(inbits);
			// counts how many bits were added and scans bitcode into compressed file
			for (int index = 0; index < bitCode.length(); index++) {
				out.writeBits(1, Integer.parseInt(bitCode.substring(index, index+1)));
				countBits[0]++;
			}
			inbits = in.readBits(BITS_PER_WORD);
		}
		// scan pseudo_eof value
		String bitCode2 = tree.getBitMap().get(PSEUDO_EOF);
		for (int index = 0; index < bitCode2.length(); index++) {
			out.writeBits(1,  Integer.parseInt(bitCode2.substring(index, index+1)));
			countBits[0]++;
		}
		out.close();
	}
	
	/*
	 * pre: none
	 * post: reads and counts number of bits in file
	 */
	public void countBits(BitInputStream in, int[] frequencies, int[] countBits) throws IOException {
		int inbits = in.readBits(BITS_PER_WORD);
		while (inbits != -1) {
			// increment by bits per word (8 bits)
			countBits[0]+= BITS_PER_WORD;
			// increment frequncy of inbit
			frequencies[inbits]++;
			inbits = in.readBits(BITS_PER_WORD);
		}
		// there will only be one pseudo eof value, increment once here
		frequencies[PSEUDO_EOF]++;
	}
	
	/*
	 * pre: none
	 * post: writes header and stores header size
	 */
	public void makeHeaderSTF(BitOutputStream out, TreeNode node, int[] countBits) {
		// node not null, increment countBits
		countBits[0]++;
		if (node.isLeaf()) {
			if (out != null) {
				// at leaf node, write 1 
				out.writeBits(1, 1);
				out.writeBits(BITS_PER_WORD + 1, node.getValue());
				// write bits
			}
			countBits[0] += BITS_PER_WORD + 1;
		}
		else {
			// at internal node, write 0
			if (out != null) {
				out.writeBits(1, 0);
			}
			// recursively iterate to left child
			if (node.getLeft() != null) {
				makeHeaderSTF(out, node.getLeft(), countBits);
			}
			// recursively iterate to right child
			if (node.getRight() != null) {
				makeHeaderSTF(out, node.getRight(), countBits);
			}
		}
	}

	/*
	 * pre: none
	 * post: makes SCF header and stores its size
	 */
	public void makeHeaderSCF(BitOutputStream out, int[] countBits, int[] frequencies) {
		// iterate through each frequency and write bits for each index
		for (int index = 0; index < ALPH_SIZE; index++) {
			out.writeBits(BITS_PER_INT, frequencies[index]);
			countBits[0] += BITS_PER_INT;
		}
	}

}
