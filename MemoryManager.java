
public interface MemoryManager {

	/**
	 * This method is called by the simulator when a page fault should occur.
	 * The specified page is not currently resident in a frame, so this method
	 * must choose a frame in which to load the page either by finding a free
	 * frame or evicting a resident page.
	 * @param pageNumber page number of page simulator tried to access
	 */
	public void handlePageFault(int pageNumber);
	
	/**
	 * This method is called by the simulator whenever a page is accessed.
	 * This method is not needed for all page replacement algorithms.
	 * @param pageNumber number page being accessed
	 */
	public void pageAccessed(int pageNumber);
	
}
