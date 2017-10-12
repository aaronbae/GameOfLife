import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


public class GameOfLife
{
	private Grid grid;
	private JFrame mainFrame;
	private Timer myTimer;
	private Point myPointer;
	private Point prevPointer;
	private static boolean started;
	private static boolean borderless;
	private static int TIMEINTERVAL = (int)(1000 * Math.pow(0.9551, 25));
	private static int XAXIS = 50;
	private static int YAXIS = 50;
	private static int SIDE = 30;

	public GameOfLife()
	{
		grid = new Grid(XAXIS, YAXIS);
		started = false;
		borderless = false;
		createGUI();
	}

	private void createGUI()
	{
		setFontSize();
		constructFrame();
		constructTimer();
		constructMenuBar();
		constructGridPanel();
		constructMenuPanel();

		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
	}

	private void setFontSize() {
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel() {

				@Override
				public UIDefaults getDefaults() {
					UIDefaults ret = super.getDefaults();
					ret.put("defaultFont", new Font(Font.MONOSPACED, Font.BOLD, 32)); // supersize me
					return ret;
				}

			});
		} catch (Exception e) {
			throw new Error(e);
		}
		
	}
	private void constructFrame()
	{
		mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setTitle("GameOfLife");
		mainFrame.getContentPane().setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));
	}     

	private void constructTimer()
	{
		ActionListener updater = new ActionListener() 
		{
            public void actionPerformed(ActionEvent e) 
			{
				grid.update(borderless);
				mainFrame.getContentPane().repaint();
			}
        };
        myTimer = new Timer(TIMEINTERVAL, updater);
        myTimer.setCoalesce(true);
	}

	private void constructGridPanel()
	{
		JPanel gridPanel = new JPanel()
		{
			public void paintComponent(Graphics g){
				for(int i = 0; i < XAXIS; i++){
					for(int j = 0; j < YAXIS; j++){
						if(grid.getStatus(i,j)) g.setColor(Color.BLACK);
						else g.setColor(Color.WHITE);
						g.fillRect(i * SIDE, j * SIDE,SIDE,SIDE);
					}
				}
			}
		};
		gridPanel.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent evt)
            {
                int x = (int)(evt.getPoint().getX() / SIDE);
                int y = (int)(evt.getPoint().getY() / SIDE);
				grid.clicked(x,y);
				mainFrame.repaint();
            }
        });
        gridPanel.addMouseMotionListener(new MouseAdapter()
        {
			public void mouseDragged(MouseEvent evt){
				if(!comparePoints(myPointer, evt.getPoint()))
				{
					int x = (int)(evt.getPoint().getX() / SIDE);
		            int y = (int)(evt.getPoint().getY() / SIDE);
					grid.clicked(x,y);
					mainFrame.repaint();
					myPointer = evt.getPoint();
				}
			}
			public void mouseMoved(MouseEvent evt)
			{
				myPointer = evt.getPoint();
			}

		});
        gridPanel.setPreferredSize(new Dimension(XAXIS * SIDE, YAXIS * SIDE));
		mainFrame.add(gridPanel);
	}
	private void constructMenuPanel()
	{
		JPanel menuPanel = new JPanel();
		mainFrame.add(menuPanel);

		JButton startOrStop = new JButton("Start");
		startOrStop.addActionListener(new ActionListener() 
		{
            public void actionPerformed(ActionEvent e) {
                if(started){
					started = false;
					myTimer.stop();
					((JButton)e.getSource()).setText("Start");
				}
                else {
					started = true;
					myTimer.start();
					((JButton)e.getSource()).setText("Stop");
				}
            }
        });
		menuPanel.add(startOrStop);

		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				grid.clear();
				mainFrame.repaint();
			}
		});
		menuPanel.add(clear);

        menuPanel.add(new JLabel("Slow"));
		JSlider slider = new JSlider(0, 100 ,25);
		slider.addChangeListener(new ChangeListener()
		{
            public void stateChanged(ChangeEvent e)
			{
				int n = ((JSlider)e.getSource()).getValue();
                TIMEINTERVAL = (int)(1000 * Math.pow(0.9551, n));
                myTimer.setDelay(TIMEINTERVAL);
            }
        });
		menuPanel.add(slider);
		menuPanel.add(new JLabel("Fast"));
		

		JCheckBox borderlessCheckBox = new JCheckBox("Borderless");
		borderlessCheckBox.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				borderless = ((JCheckBox)e.getSource()).isSelected();
			}
		});
		menuPanel.add(borderlessCheckBox);
	}
	private boolean comparePoints(Point a, Point b)
	{
		int ax = (int)(a.getX() / SIDE);	int ay = (int)(a.getY() / SIDE);
		int bx = (int)(b.getX() / SIDE);	int by = (int)(b.getY() / SIDE);
		if(ax != bx || ay != by)
			return false;//NO, the two Points are different points
		else
			return true;//YES, the two Points are the same points
	}
	private void constructMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);

		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				loadGrid();
				mainFrame.repaint();
			}
		});
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				saveGrid();
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				mainFrame.setVisible(false);
				mainFrame.dispose();
			}
		});

		JMenuItem oscillators = new JMenuItem("Oscillators");
		JMenuItem spaceships = new JMenuItem("Spaceships");
		JMenuItem guns = new JMenuItem("Guns");
		JMenuItem Bae = new JMenuItem("Bae");
		
		JMenuItem min = new JMenuItem("Minimum");
		min.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				changeSize(50,50);
			}
		});
		JMenuItem standard = new JMenuItem("Standard");
		standard.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				changeSize(60,60);
			}
		});
		JMenuItem fullScreen = new JMenuItem("Full Screen");
		fullScreen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				int x = (int)(dim.getWidth() / SIDE);
				int y = (int)(dim.getHeight() / SIDE) - 13;
				changeSize(x , y);
				mainFrame.setState(Frame.MAXIMIZED_BOTH);
			}
		});
		JMenuItem customize = new JMenuItem("Customize Size");

		JMenuItem about = new JMenuItem("About");


		JMenu file = new JMenu("File");
		file.add(open);
		file.add(save);
		file.addSeparator();
		file.add(exit);

		JMenu tools = new JMenu("Tools");
		tools.add(oscillators);
		tools.add(spaceships);
		tools.add(guns);
		tools.add(Bae);

		JMenu views = new JMenu("Views");
		views.add(min);
		views.add(standard);
		views.add(fullScreen);
		views.addSeparator();
		views.add(customize);

		JMenu help = new JMenu("Help");
		help.add(about);

		menuBar.add(file);
		menuBar.add(tools);
		menuBar.add(views);
		menuBar.add(help);
	}
	private void saveGrid()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setFileFilter(new FileFilter() {
	        public boolean accept(File f) {
	            if (f.isDirectory()) {
	                return true;
	            }
	            final String name = f.getName();
	            return name.endsWith(".png");
	        }
	        public String getDescription() {
	            return "*.png";
	        }
		});
		int result = fileChooser.showSaveDialog(mainFrame);
		if(result == JFileChooser.CANCEL_OPTION)return;
		File file = new File(fileChooser.getSelectedFile() + ".png");
		try{
			BufferedImage image = new BufferedImage(XAXIS * SIDE, YAXIS * SIDE, BufferedImage.TYPE_INT_BGR);
            Graphics2D graphics = image.createGraphics();

			graphics.setPaint( Color.WHITE );
			graphics.fillRect( 0, 0, image.getWidth(), image.getHeight() );
            graphics.setPaint( Color.BLACK );

			for(int i = 0; i < XAXIS; i++){
				for(int j = 0; j < YAXIS; j++){
                	if(grid.getStatus(i,j)){
						graphics.fillRect(i * SIDE,j * SIDE, SIDE, SIDE);
                	}
				}
			}
			ImageIO.write(image ,"png" ,file);
		} catch (IOException e){
			System.out.println("FileNotFoundException");
		}
	}
	private void loadGrid()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setFileFilter(new FileFilter() {
	        public boolean accept(File f) {
	            if (f.isDirectory()) {
	                return true;
	            }
	            final String name = f.getName();
	            return name.endsWith(".png");
	        }
	        public String getDescription() {
	            return "*.png";
	        }
		});
		int result = fileChooser.showOpenDialog(mainFrame);
		if(result == JFileChooser.CANCEL_OPTION)return;
		File file = fileChooser.getSelectedFile();

		BufferedImage image;
		boolean[][] newGrid;
		try{
			image = ImageIO.read(file);
			int width = image.getWidth() / SIDE;	int height = image.getHeight() / SIDE;
			newGrid = new boolean[width][height];
			for(int i = 0; i < width; i++){
				for(int j = 0; j < height; j++){
					if(Color.BLACK.getRGB() == image.getRGB(i * SIDE,j * SIDE)){
						newGrid[i][j] = true;
					}
				}
			}
			grid.setGrid(newGrid);
		} catch (IOException e){
			System.out.println("FileNotFoundException");
		}
	}
	private void changeSize(int x, int y)
	{
		mainFrame.setVisible(false);
		XAXIS = x;
		YAXIS = y;
		grid = new Grid(XAXIS, YAXIS);

		createGUI();
	}

	public static void main(String[]args)
	{
		GameOfLife x = new GameOfLife();
	}
}