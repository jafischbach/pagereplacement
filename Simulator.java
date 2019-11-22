import java.util.Random;

/**
 * Simulates page replacement algorithms using a given or randomly generated sequence 
 * of page accesses.
 * 
 * Each page access in the sequence is an integer of one to three digits.  One- and two-digit
 * integers represent page reads.  Three-digit integers represent page writes, where the
 * rightmost two digits represents the page number. (Ex. 108 indicates page 8 is being written to.)
 * 
 * For each page access, the simulation updates the appropriate page table entry and
 * generates a "page fault" (by calling the handlePageFault() method of the MemoryManager)
 * for each invalid page.
 * 
 * @author Adam Fischbach
 * @version Fall 2013
 */
import javax.swing.JOptionPane;
public class Simulator {
	
	/**
	 * Starts the simulation program with a sample sequence of page accesses.
	 */
	public static void main(String[] args) {
		//(new Simulator(10, 4)).simulate(20, 20);
		//int[] a = {1,2,3,4,1,102,5,6,2,7,103,104,102,6,107,2,7};
		int[] a = {1,2,3,4,2,2,1,5,1,4,6,3,5,2,1};
		(new Simulator(8, 4)).simulate(a);
	}
	
	public static MemoryManager initManager(PageTable table, Simulator sim) {
		return new MemoryManagerFIFO(table, sim);
	}
	
	private GUI gui;				// The user interface
	private PageTable table;		// The page table
	private MemoryManager manager;	// The memory manager
	private Random rand;			// The random number generator
	private int faults;				// The number of page faults generated
	private int[] frames;			// Array of frames (not currently used)
	private int current;			// Current page access array index
	private int[] accesses;			// Sequence of page accesses
	private boolean stepping;		// Is simulation running?
	private int writeFreq;			// Frequency that page references are writes
	
	/**
	 * Create a new simulator with the specified table size and number of frames.
	 * The table size must be less than 100.
	 * @param tableSize number of pages
	 * @param numFrames number of frames
	 */
	public Simulator(int tableSize, int numFrames) {
		if (tableSize < 100) {
			stepping = false;
			writeFreq = 20;
			table = new PageTable(tableSize);
			gui = new GUI(tableSize, numFrames, table, this);
			rand = new Random();
			frames = new int[numFrames];
			for(int i=0; i<numFrames; i++) {
				frames[i] = -1;
			}
			manager = initManager(table, this);
			//manager = new MemoryManagerLRU(table, this);
		} else {
			throw new IllegalArgumentException("table size cannot exceed 99: " + tableSize);
		}
	}
	
	/**
	 * Advances the simulation by one page access.
	 */
	public void stepSim() {
		if (current < accesses.length) {
			if (accesses[current] >= 100) {
				accessPage(accesses[current]%100, true);
			} else {
				accessPage(accesses[current], false);
			}
			current++;
			if (current == accesses.length) {
				gui.simEnded();
				JOptionPane.showMessageDialog(null, "Faults: " + faults);
			} else {
				gui.nextData(current);
			}
			for(int i=0; i<table.size(); i++) {
				gui.updatePageEntry(i);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Simulation Ended");
		}
	}
	
	/**
	 * Starts a new simulation using the specified sequence of accesses.
	 * @param accesses the sequence of page accesses
	 */
	public void simulate(int[] accesses) {
		this.accesses = accesses;
		gui.initData(accesses);
		faults = 0;
		current = 0;
	}
	
	/**
	 * Starts a new simulation using a random sequence of accesses of the
	 * specified length.
	 * @param length length of sequence of page accesses
	 * @param writeFreq frequency that page references should be writes (higher number = more writes)
	 */
	public void simulate(int length, int writeFreq) {
		this.writeFreq = writeFreq;
		simulate(generateAccesses(length, writeFreq));
	}
	
	/**
	 * Resets the page table, page frames and the MemoryManager.
	 */
	public void clearTables() {
		for(int i=0; i<table.size(); i++) {
			PTEntry e = table.getEntry(i);
			e.setValid(false);
			e.setAccessed(false);
			e.setModified(false);
			e.setFrame(-1);
			gui.updatePageEntry(i);
		}
		for(int i=0; i<frames.length; i++) {
			frames[i] = -1;
			gui.updateFrameEntry(-1, i);
		}
		manager = initManager(table, this);
		//manager = new MemoryManagerLRU(table, this);
	}
	
	/**
	 * Returns the entire sequence of page accesses.
	 * @return array containing all page accesses.
	 */
	public int[] getAccesses() {
		int[] temp = new int[accesses.length];
		for(int i=0; i<accesses.length; i++) {
			temp[i] = accesses[i]%100;
		}
		return temp;
	}
	
	/**
	 * Returns the index of the current page reference.
	 * @return current page reference.
	 */
	public int getCurrentAccess() {
		return current;
	}
	
	/**
	 * Returns an array containing page numbers of resident pages.
	 * @return frame table
	 */
	public int[] getFrameTable() {
		return frames;
	}
	
	/**
	 * Returns the current write frequency.
	 * @return write frequency.
	 */
	public int getWriteFreq() {
		return writeFreq;
	}
	
	/**
	 * Sets the write frequency to the given value.
	 * @param writeFreq
	 */
	public void setWriteFreq(int writeFreq) {
		this.writeFreq = writeFreq;
	}
	
	/**
	 * Runs the simulation to its conclusion by stepping through all remaining
	 * page accesses.
	 */
	public void stepAll() {
		stepping = true;
		while (stepping && current < accesses.length) {
			stepSim();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(null, "Something interrupted me!");
			}
		}
	}
	
	/**
	 * Pauses the simulation.
	 */
	public void pauseSim() {
		stepping = false;
	}
	
	/**
	 * This method loads the specified page into the specified frame.
	 * @param pageNumber the page to load
	 * @param frameNumber the frame in which to load the page
	 * @return the page number of the evicted page (or -1 if frame was empty)
	 */
	public int loadPage(int pageNumber, int frameNumber) {
		int evicted = frames[frameNumber];
		frames[frameNumber] = pageNumber;
		if(gui.terminalOut()) {
			System.out.println("\tLoading page " + pageNumber + " into frame " + frameNumber);
		}
		gui.updatePageEntry(pageNumber);
		gui.updateFrameEntry(pageNumber, frameNumber);
		return evicted;
	}
	
	/**
	 * Returns the number of frames.
	 * @return number of frames
	 */
	public int getNumberFrames() {
		return frames.length;
	}
	
	/**
	 * Updates simulation for the given evicted page.
	 * @param pageNumber page number of page being evicted.
	 */
	public void evict(int pageNumber) {
		if (gui.terminalOut()) {
			System.out.println("\tEVICTING Page " + pageNumber);
		}
		gui.updatePageEntry(pageNumber);
	}
	
	/**
	 * Generates a random sequence of page accesses.
	 * @param length length of sequence
	 * @param writeFreq frequency that page references should be writes (higher number = more writes)
	 * @return array of page accesses
	 */
	private int[] generateAccesses(int length, int writeFreq) {
		int[] accesses = new int[length];
		for(int i=0; i<length; i++) {
			accesses[i] = rand.nextInt(table.size());
			if (rand.nextInt(100) < writeFreq) {
				accesses[i] += 100;
			}
		}
		return accesses;
	}
	
	/**
	 * Simulation calls this method for each reference in the sequence of page references.
	 * Sets Accessed bit in page table entry.
	 * Sets Modified bit in page table entry if reference is a write.
	 * Generates a "page fault" if specified page is not valid.
	 * @param pageNumber the page that is being accessed
	 * @param isWrite true is reference is a write, false if reference is a read
	 */
	private void accessPage(int pageNumber, boolean isWrite) {
		if(gui.terminalOut()) {
			String s = isWrite ? "Writing page " : "Reading page ";
			System.out.println(s + pageNumber);
		}
		PTEntry entry = table.getEntry(pageNumber);
		if(!entry.isValid()) {
			manager.handlePageFault(pageNumber);
			faults++;
		} else {
			entry.setAccessed(true);
			manager.pageAccessed(pageNumber);
		}
		//entry.setAccessed(true);
		if (isWrite) {
			entry.setModified(true);
		}
		//gui.updatePageEntry(pageNumber);
	}
	
	/**
	 * Simulation calls this method for each write access in the sequence of page accesses.
	 * Sets Accessed and Modified bits in page table entry.
	 * Generates a "page fault" if specified page is not valid.
	 * @param pageNumber the page that is being accessed.
	 */
	/*private void modifyPage(int pageNumber) {
		if(output) {
			System.out.println("Writing page " + pageNumber);
		}
		PTEntry entry = table.getEntry(pageNumber);
		if(!entry.isValid()) {
			manager.handlePageFault(pageNumber);
			faults++;
		} else {
			manager.pageAccessed(pageNumber);
		}
		entry.setAccessed(true);
		entry.setModified(true);
		gui.updatePageEntry(pageNumber);
	}*/
	
}
