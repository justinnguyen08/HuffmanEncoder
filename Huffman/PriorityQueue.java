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

import java.util.LinkedList;

public class PriorityQueue<E extends Comparable<? super E>> {

	private LinkedList<E> myCon;
	
	public PriorityQueue() {
		myCon = new LinkedList<E>();
	}
	
	/*
	 * pre: none
	 * post: returns true if inserted the specified element into this priority queue
	 * else returns false
	 */
	public boolean add(E val) {
		// empty linked list, just add to empty list
		if (myCon.isEmpty()) {
			return myCon.add(val);
		} else {
			int tempSize = myCon.size();
			int index = 0; 
			// iterate through myCon while size has not been changed
			while (index < myCon.size() && tempSize == myCon.size()) {
				// only add in appropriate index
				if (val.compareTo(myCon.get(index)) < 0) {
					myCon.add(index, val);
				}
				// iterate to next value of myCon
				index++;
			}
			// need to add at last index, just use LinkedList add
			if (tempSize == myCon.size()) {
				return myCon.add(val);
			}
			// if size has changed, return true
			return tempSize != myCon.size();
		}
	}
	
	/*
	 * pre: none
	 * post: removes the first element in the queue
	 */
	public E remove() {
		return myCon.removeFirst();
	}
	
	/*
	 * pre: none
	 * post: returns the size of the queue
	 */
	public int size() {
		return myCon.size();
	}
	
	/*
	 * pre: none
	 * post: returns the toString of the queue
	 */
	public String toString() {
		return myCon.toString();
	}
}
