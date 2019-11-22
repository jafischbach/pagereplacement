/**
 * The Page Table!
 * 
 * @author Adam Fischbach
 * @version Fall 2013
 *
 */
public class PageTable {

	private PTEntry[] table;		// Table of entries
	
	/**
	 * Create a new page table of the specified size.
	 * @param size size of page table
	 */
	public PageTable(int size) {
		table = new PTEntry[size];
		populateTable();
	}
	
	/**
	 * Returns the specified entry.
	 * @param pn number of page table entry to return
	 * @return page table entry
	 */
	public PTEntry getEntry(int pn) {
		return table[pn];
	}
	
	/**
	 * Returns the size of the page table.
	 * @return size of table
	 */
	public int size() {
		return table.length;
	}
	
	/**
	 * Fills the table with new entries.
	 */
	private void populateTable() {
		for(int i=0; i<table.length; i++) {
			table[i] = new PTEntry();
		}
	}
	
}
