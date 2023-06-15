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
import java.util.HashMap;

public class HuffManCodeTree implements IHuffConstants{
	public TreeNode root;
	private HashMap<Integer, String> bitMaps;
	private PriorityQueue<TreeNode> queue;
	private int headerSize;
	
	// constructor for SCF
	public HuffManCodeTree(int[] frequencies) {
		bitMaps = new HashMap<>();
		// build the priority queue using given frequencies
		queue = createQueue(frequencies);
		// now build the actual tree
		root = treeBuilder();
	}
	
	// constructor for STF
	public HuffManCodeTree(Decompress decompress, BitInputStream in) throws IOException {
		bitMaps = new HashMap<>();
		root = decompress.readHeaderSTF(in);
	}
	/*
	 * pre: none
	 * post: returns a priority queue of tree nodes 
	 */
	public PriorityQueue<TreeNode> createQueue(int[] frequencies) {
		PriorityQueue<TreeNode> result = new PriorityQueue<>();
		// iterate through all values with frequencies
		for (int freqIndex = 0; freqIndex < frequencies.length; freqIndex++) {
			// if a frequency pops up, add a tree node with the value
			// being checked to the priority queue 
			if (frequencies[freqIndex] != 0) {
				result.add(new TreeNode(freqIndex, frequencies[freqIndex]));
			}
		}
		return result;
	}
	
	/*
	 * pre: none
	 * post: builds the Huffman tree and returns the root of that tree
	 */
	public TreeNode treeBuilder() {
		// go until we have made the last node
		while (queue.size() > 1) {
			TreeNode firstNode = queue.remove();
			TreeNode secondNode = queue.remove();
			// create new node with left child being first node and right child being 
			// second node with a value of -1
			TreeNode newNode = new TreeNode(firstNode, -1, secondNode);
			// add new node to prioritized position in queue
			queue.add(newNode);
		}
		// return the root left in the queue
		return queue.remove();
	}
	
	/*
	 * pre: none
	 * post: fiils the bitmappings for each value in the Huffman tree
	 * starting at the root
	 */
	public void fillBitMap(String currVal, int[] countBits) {
		fillBitMap(currVal, countBits, root);
	}
	/*
	 * pre: none
	 * post: fills the bitmappings for each value in the Huffman tree
	 */
	public void fillBitMap(String currVal, int[] countBits, TreeNode node) {
		// node is a leaf, put bit value to bitmaps
		if (node.isLeaf()) {
			bitMaps.put(node.getValue(), currVal);
			countBits[0] += currVal.length() * node.getFrequency();
			// must reset back to top of the tree	
		}
		else {
			// if can iterate through left, concatenate 0 and go left
			if (node.getLeft() != null) {
				fillBitMap(currVal + "0", countBits, node.getLeft());
			}
			// if can iterate through right, concatenate 1 and go right
			if (node.getRight() != null) {
				fillBitMap(currVal + "1", countBits, node.getRight());
			}
		}
	}
	
	/*
	 * pre: none
	 * post: return bitmaps
	 */
	public HashMap<Integer, String> getBitMap() {
		return bitMaps;
	}
	
	/*
	 * pre: none
	 * post: returns the size 
	 */
	public int size(Compress compressor) {
		// calculate size if header size is 0
		if (headerSize == 0) {
			int[] countBits = new int[1];
			// writes headerSTF, calculates its size, and puts size in countBits
			compressor.makeHeaderSTF(null, root, countBits);
			// make value of countBits equal header size
			headerSize = countBits[0];
		}
		return headerSize;
	}
	
	/*
	 * pre: none
	 * post: make header and update countBits
	 */
	public void makeHeaderSTF(BitOutputStream out, int[] countBits,
			Compress compressor) {
		size(compressor);
		compressor.makeHeaderSTF(out, root, countBits);
	}
}
