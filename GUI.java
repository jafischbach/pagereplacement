import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.event.*;
/**
 * The user interface for the page replacement simulator.
 * 
 * @author Adam Fischbach
 * @version Fall 2013
 *
 */
public class GUI {
	
	private PageTable table;		// Reference to page table
	private Simulator sim;			// Reference to simulator
	
	private JFrame frame;			// Main window
	private JLabel[] entries;		// Page table entries display
	private JLabel[] frames;		// Page frame entries display
	private JPanel dataPanel;		// Panel for page references display
	private JTextField[] data;		// List of page references display
	private JButton go;				// Button to start the simulation
	private JButton pause;			// Button to pause the simulation
	private JButton step;			// Button to step through the simulation
	private JCheckBoxMenuItem text;	// Output to text file if checked
	
	/**
	 * Create a new GUI object for the simulation.
	 * @param numEntries number of page table entries
	 * @param numFrames number of page frames
	 * @param table reference to page table
	 * @param sim reference to simulation
	 */
	public GUI(int numEntries, int numFrames, PageTable table, Simulator sim) {
		frame = new JFrame("Page Replacement");
		entries = new JLabel[numEntries];
		frames = new JLabel[numFrames];
		this.table = table;
		this.sim = sim;
		makeFrame();
	}
	
	/**
	 * Updates the displayed page table entry for the given page.
	 * @param pageNumber number of page to update
	 */
	public void updatePageEntry(int pageNumber) {
		PTEntry e = table.getEntry(pageNumber);
		entries[pageNumber].setText(e.toString());
		if (!e.isValid()) {
			entries[pageNumber].setBackground(Color.WHITE);
		} else if (e.isModified()) {
			entries[pageNumber].setBackground(Color.PINK);
		} else {
			entries[pageNumber].setBackground(Color.GREEN);
		}
	}
	
	/**
	 * Updates the displayed page frame entry for the given frame.
	 * @param pageNumber page number to display in frame entry
	 * @param frameNumber number of frame entry to update
	 */
	public void updateFrameEntry(int pageNumber, int frameNumber) {
		frames[frameNumber].setText(pageNumber+"");
	}
	
	/**
	 * Advances displayed list of page references to the given index.
	 * @param next index of next page reference
	 */
	public void nextData(int next) {
		data[next-1].setBackground(Color.WHITE);
		data[next-1].setForeground(Color.BLACK);
		data[next].setBackground(Color.RED);
		data[next].setForeground(Color.WHITE);
	}
	
	/**
	 * Called by Simulator to indicate simulation has ended.
	 */
	public void simEnded() {
		go.setEnabled(false);
		pause.setEnabled(false);
		step.setEnabled(false);
		if (text.getState()) {
			System.out.println("****** SIMULATION ENDED ******");
		}
	}
	
	/**
	 * Returns true if terminal output check box is selected.
     * @return true if terminal output selected.
     */
	public boolean terminalOut() {
		return text.getState();
	}
	
	/**
	 * Displays the given list of page references.
	 * @param accesses page references to display
	 */
	public void initData(int[] accesses) {
		if (data != null) { // remove previous references if they exist
			for(int i=0; i<data.length; i++) {
				dataPanel.remove(data[i]);
			}
		}
		data = new JTextField[accesses.length];
		for(int i=0; i<accesses.length; i++) {
			data[i] = new JTextField((accesses[i]%100)+"");
			data[i].setEditable(false);
			if (accesses[i] > 99) {
				data[i].setFont(new Font(null, Font.BOLD, 16));
			} else {
				data[i].setFont(new Font(null, Font.PLAIN, 16));
			}
			dataPanel.add(data[i]);
		}
		data[0].setBackground(Color.RED);
		data[0].setForeground(Color.WHITE);
		frame.setVisible(true);	// display/re-draw window since displayed references have changed
	}
	
	// Build the main window.
	private void makeFrame() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.setJMenuBar(makeMenuBar());
		
		JPanel upperPanel = new JPanel();
		upperPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		dataPanel = new JPanel();
		upperPanel.add(dataPanel);
		frame.add(upperPanel, BorderLayout.NORTH);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 2));
		frame.add(mainPanel, BorderLayout.CENTER);
		
		mainPanel.add(makeTable());
		mainPanel.add(makeFrames());
		
		frame.setSize(550, 450);
		frame.setLocationRelativeTo(null);
	}
	
	// Build and return the menu bar
	private JMenuBar makeMenuBar() {
		JMenuBar bar = new JMenuBar();
		
		JMenu outputMenu = new JMenu("Output");
		text = new JCheckBoxMenuItem("Terminal");
		outputMenu.add(text);
		bar.add(outputMenu);
		
		JMenu simMenu = new JMenu("Sim");
		JMenuItem reset = new JMenuItem("Reset sim (random)");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sim.clearTables();
				sim.simulate(sim.getAccesses().length, sim.getWriteFreq());
				go.setEnabled(true);
				pause.setEnabled(true);
				step.setEnabled(true);
			}
		});
		simMenu.add(reset);
		JMenuItem writeFreq = new JMenuItem("Set write frequency");
		writeFreq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n;
				try {
					String s = JOptionPane.showInputDialog(frame, "Enter write frequency:");
					if (s == null) {
						return;
					}
					n = Integer.parseInt(s);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(frame, "Error: write frequency must be an integer.");
					return;
				}
				sim.setWriteFreq(n);
			}
		});
		simMenu.add(writeFreq);
		bar.add(simMenu);
		
		return bar;
	}
	
	// Build and return the displayed page table.
	private JPanel makeTable() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JPanel t = new JPanel(new GridLayout(entries.length+1, 1, 5, 5));
		JLabel key = new JLabel(" v  a  m     Frame");
		key.setFont(new Font(null, Font.PLAIN, 20));
		t.add(key);
		for(int i=0; i<entries.length; i++) {
			entries[i] = new JLabel(" 0  0  0     -1");
			entries[i].setBorder(BorderFactory.createLoweredBevelBorder());
			entries[i].setFont(new Font(null, Font.BOLD, 20));
			entries[i].setBackground(Color.WHITE);
			entries[i].setOpaque(true);
			t.add(entries[i]);
		}
		p.add(t, BorderLayout.CENTER);
		
		JPanel labels = new JPanel(new GridLayout(entries.length+1, 1, 5, 5));
		labels.add(new JLabel());
		for(int i=0; i<entries.length; i++) {
			JLabel l = new JLabel(i+" ");
			l.setFont(new Font(null, Font.PLAIN, 20));
			labels.add(l);
		}
		p.add(labels, BorderLayout.WEST);
		
		return p;
	}
	
	// Build and return displayed page frames and button panel.
	private JPanel makeFrames() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout(5, 5));
		p.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(5, 5, 5, 5)));
		
		// *** Build displayed page frames ***
		
		JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new EmptyBorder(20, 20, 20, 20));
		JPanel f = new JPanel();
		f.setLayout(new GridLayout(frames.length, 1));
		for(int i=0; i<frames.length; i++) {
			frames[i] = new JLabel("-1");
			frames[i].setBorder(BorderFactory.createLoweredBevelBorder());
			frames[i].setFont(new Font(null, Font.BOLD, 20));
			frames[i].setHorizontalAlignment(JTextField.CENTER);
			//frames[i].setEditable(false);
			f.add(frames[i]);
		}
		top.add(f, BorderLayout.CENTER);
	
		JPanel topLabels = new JPanel(new GridLayout(frames.length, 1));
		for(int i=0; i<frames.length; i++) {
			JLabel l = new JLabel(i+" ");
			l.setFont(new Font(null, Font.PLAIN, 20));
			l.setHorizontalTextPosition(SwingConstants.CENTER);
			topLabels.add(l);
		}
		top.add(topLabels, BorderLayout.WEST);
		
		p.add(top, BorderLayout.CENTER);
		
		// *** Build button panel ***
		
		JPanel bottom = new JPanel(new GridLayout(1, 3, 5, 5));
		bottom.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(5, 5, 5, 5)));
		go = new JButton("Go");
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				go.setEnabled(false);
				step.setEnabled(false);
				(new Thread() {
					public void run() {		
						sim.stepAll();
					}
				}).start();
			}
		});
		bottom.add(go);
		
		pause = new JButton("Pause");
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sim.pauseSim();
				go.setEnabled(true);
				step.setEnabled(true);
			}
		});
		bottom.add(pause);
		
		step = new JButton("Step");
		step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sim.stepSim();
			}
		});
		bottom.add(step);
		p.add(bottom, BorderLayout.SOUTH);
		
		return p;
	}
}
	
