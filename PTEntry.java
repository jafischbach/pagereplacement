/**
 * A single entry of the page table.
 * 
 * Includes frame number as well as Valid, Accessed and Modified bits.
 * 
 * @author Adam Fischbach
 * @version Fall 2013
 */
public class PTEntry {
	
	private int frame;			// Number of frame in which page is loaded (if valid)
	private boolean accessed;	// Has page been accessed?
	private boolean modified;	// Has page been modified?
	private boolean valid;		// Is page loaded in a frame?
	
	/**
	 * Create a new page table entry.
	 */
	public PTEntry() {
		frame = -1;
		accessed = false;
		modified = false;
		valid = false;
	}
	
	/**
	 * Return frame number.
	 * @return frame number
	 */
	public int getFrame() {
		return frame;
	}
	
	/**
	 * Set the frame number to the specified value.
	 * @param frame new frame number
	 */
	public void setFrame(int frame) {
		this.frame = frame;
	}
	
	/**
	 * Returns true if Accessed bit is set.
	 * @return true is accessed
	 */
	public boolean isAccessed() {
		return accessed;
	}
	
	/**
	 * Returns true if Modified bit is set.
	 * @return true if modified
	 */
	public boolean isModified() {
		return modified;
	}
	
	/**
	 * Returns true of Valid bit is set.
	 * @return true if valid
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Sets the Accessed bit to the specified value (1-true, 0-false)
	 * @param value new value of Accessed
	 */
	public void setAccessed(boolean value) {
		accessed = value;
	}
	
	/**
	 * Sets the Modified bit to the specified value (1-true, 0-false)
	 * @param value new value of Modified
	 */
	public void setModified(boolean value) {
		modified = value;
	}
	
	/**
	 * Sets the Valid bit to the specified value (1-true, 0-false)
	 * @param value new value of Valid
	 */
	public void setValid(boolean value) {
		valid = value;
	}
	
	/**
	 * Returns the page table entry as a string.
	 */
	public String toString() {
		String s = " ";
		s += valid ? "1  " : "0  ";
		s += accessed ? "1  " : "0  ";
		s += modified ? "1     " : "0     ";
		return s + frame;
	}
	
}
