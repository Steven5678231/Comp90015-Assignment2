package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent; 
import java.awt.Font;
import java.awt.Image;
import java.awt.Cursor;
import java.awt.Point;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Chaoxian Zhou, Yangyang Long, Jiuzhou Han, Wentao Yan
 * @date 19/10/2019
 */
public class DrawingBoard extends JFrame {// implements FrameGetShape
	
	// Canvas configuration
	int canvasWidth = 1024;
	int canvasHeight = 1024;
	BufferedImage image = new BufferedImage( canvasWidth, canvasHeight, BufferedImage.TYPE_INT_BGR);// Size and type of the image
	Graphics gs = image.getGraphics();
	Graphics2D g = (Graphics2D) gs;
	DrawPictureCanvas canvas = new DrawPictureCanvas();
	//BufferedImage image2 = new BufferedImage( canvasWidth, canvasHeight, BufferedImage.TYPE_INT_BGR);
	
	// Layered Pane initialization
	ResizableShapes resizableShape;
    ResizableLine resizableLine;
	JLayeredPane lp = new JLayeredPane();
	JLabel backgroundImage = new JLabel();
	boolean resizable = false;
    boolean open = false;


	// Mouse action coordinates initialization
	int x1;
	int y1;
	int x2;
	int y2;
	int x = -1;
	int y = -1;
	
	// Components
	private JToolBar toolBar;
	private JButton btnSave;
	private JButton btnSaveas;	
	private JButton btnDraw;
	private JButton btnErase;
	private JButton btnShapes;
	private JPopupMenu shapesMenu;
	private JMenuItem itemLine;
	private JMenuItem itemCircle;
	private JMenuItem itemOval;
	private JMenuItem itemSquare;
	private JMenuItem itemRectangle;
	private JButton btnClear;
	private boolean isActive;
	private JButton btnText;	
	private JButton btnPixelSize;
	private JPopupMenu pixelsizeMenu;
	private JButton btnFc;
	private JTextPane textPane;
	private JButton btnConfirm;
	public boolean canvasChangable = true;

	// Initial parameters
	Color foregroundColor = Color.BLACK;
	Color backgroundColor = Color.WHITE;
	Color eraserColor = new Color(254,254,254);
	private String keyword = "pencil"; 
	private int pixel_size = 3; 
  private boolean fill = false;
	private String inputString;
	public boolean mouseIsPressed = false;
	//public boolean specialOperationIsConfirmed = false;
	public boolean penEraOperation = true;
	
	int hasSaved = 0;
    String path=null;
	int type = 0;
	int type2 =0 ;
	String path2=null;
	int secondSaved=0;
	
	// Board config
	int boardWidth = 1024;
	int boardHeight = 768;
	
	/*
	 * Create the white board for the manager
	 */
	public DrawingBoard() {
		setResizable(false);
		setBounds( 500, 100, boardWidth, boardHeight);
		this.isActive = false;		
		setTitle("Manager Whiteboard");
		init();
		publicInit();
		addListener();
	}
	
	/*
	 *  Create the white board for the client
	 */
	public DrawingBoard(BufferedImage importedImage) {
		setResizable(false);
		setBounds( 500, 100, boardWidth, boardHeight);
		
		setTitle("Client Whiteboard ");
		this.image = importedImage;
		init2(importedImage);
		publicInit();
		addListener();
	}
	
	/*
	 *  Construct the opened white board for the manager
	 */
	public DrawingBoard(BufferedImage image2,int hasSaved,int type2,String path) {
		setResizable(false);
		setBounds( 500, 100, boardWidth, boardHeight);
		setTitle("Manager WhiteBoard");
		this.hasSaved=hasSaved;
		this.type=type2;
		this.path=path;
		
		this.image = image2;
		init2(image2);
		publicInit();
		addListener();
	}
	
	private void addListener() {
		// Canvas-Mouse clicked action listener
		canvas.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e)
			{
				int xc = e.getX();
				int yc = e.getY();
				if(keyword=="text")
				{
					inputString = JOptionPane.showInputDialog(null,null,"Plese input the content:",JOptionPane.PLAIN_MESSAGE);
					if(inputString!=null)
					{
						mouseIsPressed = true;
						System.out.println(xc+" "+yc);
						g.setFont(new Font("Comic Sans MS", Font.PLAIN, 5*pixel_size));
						g.setColor(foregroundColor);
						g.drawString(inputString, xc, yc);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
						}
					}
				}
				canvas.repaint();
			}
		});
		
		// Canvas-Mouse pressed action listener
		// Record the coordinates when the mouse is pressed. 
		canvas.addMouseListener(new MouseAdapter(){
			public void mousePressed(final MouseEvent e)
			{
				mouseIsPressed = true;
				x1 = e.getX();
				y1 = e.getY();
			}
		});
		
		// Canvas-Mouse release action listener
		// When the mouse is released, draw corresponding objects.
		canvas.addMouseListener(new MouseAdapter(){
			public void mouseReleased(final MouseEvent e){
				x = -1;
				y = -1;
				x2 = e.getX();
				y2 = e.getY();
				int xx1 = x1;
				int yy1 = y1;
				int xx2 = x2;
				int yy2 = y2;
				g.setColor(foregroundColor);
				if(keyword=="line")
				{
					Image backImage;
					backImage = image;
						
					backgroundImage = new JLabel();
					ImageIcon canvasContent = new ImageIcon(backImage);
					backgroundImage.setIcon(canvasContent);
					backgroundImage.setBounds(0, 0, canvasWidth, canvasWidth);
					lp.add(backgroundImage, new Integer(150));
					
					
					resizableLine = new ResizableLine(keyword,xx1,yy1,xx2,yy2,foregroundColor,pixel_size);
					resizableLine.setOpaque(false);
					resizableLine.setBounds(0, 0, canvasWidth, canvasWidth);
					lp.add(resizableLine, new Integer(200));
					btnConfirm.setVisible(true);
					
					resizable = true;
				}
				else if(keyword=="oval"||keyword=="circle"||keyword=="square"||keyword=="rectangle")
				{
					if(x2-x1>0&&y2-y1>0)
					{
						xx1 = x1;
						yy1 = y1;
						xx2 = x2;
						yy2 = y2;
					}
					else if(x2-x1>0&&y2-y1<0)
					{
						xx1 = x1;
						yy1 = y2;
						xx2 = x2;
						yy2 = y1;
					}
					else if(x2-x1<0&&y2-y1<0)
					{
						xx1 = x2;
						yy1 = y2;
						xx2 = x1;
						yy2 = y1;
					}
					else if(x2-x1<0&&y2-y1>0)
					{
						xx1 = x2;
						yy1 = y1;
						xx2 = x1;
						yy2 = y2;
					}
					
	
					Image backImage;
					backImage = image;
						
					backgroundImage = new JLabel();
					ImageIcon canvasContent = new ImageIcon(backImage);
					backgroundImage.setIcon(canvasContent);
					backgroundImage.setBounds(0, 0, canvasWidth, canvasWidth);
					lp.add(backgroundImage, new Integer(150));
					
					
					resizableShape = new ResizableShapes(keyword,xx1,yy1,xx2,yy2,foregroundColor,pixel_size);
					resizableShape.setOpaque(false);
					resizableShape.setBounds(0, 0, canvasWidth, canvasWidth);
					lp.add(resizableShape, new Integer(200));
					btnConfirm.setVisible(true);
					
					resizable = true;
				}
				canvas.repaint();
				// Start to download server's image
				mouseIsPressed = false;
			}
		});
		
		// Canvas-Mouse action listener
		canvas.addMouseMotionListener(new MouseMotionAdapter()
		{
			// Drag the mouse to draw specific objects.
			public void mouseDragged(final MouseEvent e)
			{
				if(x > 0 && y > 0){
					if(keyword=="rubber")
					{
						BasicStroke bStroke = new BasicStroke(pixel_size*2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
						g.setStroke(bStroke);
						g.setColor(eraserColor);
//						g.fillRect(x, y, pixel_size, pixel_size);
						g.drawLine(x, y, e.getX(), e.getY());
					}
					else if(keyword=="pencil")
					{
						BasicStroke bStroke = new BasicStroke(pixel_size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
						g.setStroke(bStroke);
						g.setColor(foregroundColor);
						g.drawLine(x, y, e.getX(), e.getY());
					}
				}
				x = e.getX();
				y = e.getY();
				canvas.repaint();
			}
		
			// Set the cursor when the mouse is moving on the canvas.
			public void mouseMoved(final MouseEvent event){
					setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			}
		});
		
	
	toolBar.addMouseMotionListener(new MouseMotionAdapter() {
		public void mouseMoved(MouseEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	});

	// "New" button listener
	/*btnNew.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			DrawingBoard drawingBoard = new DrawingBoard();
			drawingBoard.setVisible(true);
			drawingBoard.setDefaultCloseOperation(2);
			System.out.println("new");
		}
	});*/
	
	// "Open" button listener
//	btnOpen.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//			// Call 'openWhiteBoard' button
//			openWhiteBoard();
//		}
//	});
	
	// "Save" button listener
	btnSave.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Call 'save' function below
			save();
		}
	});
	
	// "Saveas" button listener
	btnSaveas.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Call 'saveAs' function below
			saveAs();
		}
	});
	
	
	
	// "Pencil" button listener
	btnDraw.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			mouseIsPressed = false;
			penEraOperation = true;
			if(resizable)
			{
				confirmAction();
			}
			keyword = "pencil";
			textPane.setText(keyword);
		}
	});
	
	// "Rubber" button listener
	btnErase.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			mouseIsPressed = false;
			penEraOperation = true;
			if(resizable)
			{
				confirmAction();
			}
			keyword = "rubber";
			textPane.setText(keyword);
		}
	});
	
	// "Shapes" button listener
	btnShapes.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			mouseIsPressed = false;
			penEraOperation = false;
			shapesMenu.show(btnShapes,0+btnShapes.getWidth(),0);
			textPane.setText(keyword);
		}
	});
	
	// "Line" button listener
	itemLine.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(resizable)
			{
				confirmAction();
			}
			keyword = "line";
			textPane.setText(keyword);
		}
	});
	
	// "Oval" button listener
	itemOval.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(resizable)
			{
				confirmAction();
			}
			keyword = "oval";
			textPane.setText(keyword);
		}
	});
	
	// "Circle" button listener
	itemCircle.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(resizable)
			{
				confirmAction();
			}
			keyword = "circle";
			textPane.setText(keyword);
		}
	});
	
	// "Rectangle" button listener
	itemRectangle.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(resizable)
			{
				confirmAction();
			}
			keyword = "rectangle";
			textPane.setText(keyword);
		}
	});
	
	// "Square" button listener
	itemSquare.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(resizable)
			{
				confirmAction();
			}
			keyword = "square";
			textPane.setText(keyword);
		}
	});
	
	// "Text" button listener
	btnText.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			penEraOperation = false;
			mouseIsPressed = false;
			if(resizable)
			{
				confirmAction();
			}
			keyword = "text";
			textPane.setText(keyword);
		}
	});
	
	/// "Clear" button listener
//	btnClear.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//			clearContent();
//		}
//	});
	
	// "Foreground Color" button listener
	btnFc.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(resizable)
			{
				confirmAction();
			}
			new JColorChooser();
			Color fgColor = JColorChooser.showDialog(DrawingBoard.this,"Color", Color.CYAN);
			if(fgColor != null){
				foregroundColor = fgColor; // if the color selected is not null
			}
			// set this color as the background of the button
			btnFc.setBackground(foregroundColor);			
		}
	});
	

	
	// "Pixel Size" button listener
	btnPixelSize.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) 
		{
			if(resizable)
			{
				confirmAction();
			}
			pixelsizeMenu.show(btnPixelSize,0+btnPixelSize.getWidth(),0);
		}
	});
	
	// "Confirm" button listener
	btnConfirm.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			mouseIsPressed = true;
			if(keyword=="line")
			{
				confirmLine();
			}
			else
			{
				confirmAction();
			}
			resizable = false;			
		}
	});

	}
	
	/*
	 * Function of saving the canvas
	 */
	public void save() {
		if (DrawingBoard.this.hasSaved == 0) {
			 try {
				  
			        BufferedImage awtImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);						
		            Graphics g = awtImage.getGraphics();
		            canvas.printAll(g);   
			        JFileChooser fileSaveChooser = new JFileChooser("Save a file");
					FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG", "jpeg");						
					fileSaveChooser.setFileFilter(filter);
					fileSaveChooser.addChoosableFileFilter(new 
							FileNameExtensionFilter("PNG","png"));
					fileSaveChooser.addChoosableFileFilter(new 
							FileNameExtensionFilter("JPG","jpg"));
					int returnVal = fileSaveChooser.showSaveDialog(fileSaveChooser);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
					    File fileToSave = fileSaveChooser.getSelectedFile();
						String ends = fileSaveChooser.getFileFilter().getDescription();
						ends = ends.toLowerCase();
						if(ends.equals("jpeg")) type=1;
						if(ends.equals("jpg")) type=2;
						if(ends.equals("png")) type=3;
						String fileName = fileToSave.getAbsolutePath().toLowerCase();
						DrawingBoard.this.hasSaved = 1;
						/*
						 * If the user does not put extension at the end of the file name,
						 * automatically create one.
						 */
						if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg" )||
								fileName.endsWith(".png")) {								 
							ImageIO.write( awtImage,ends,fileToSave);
							path = fileToSave.getAbsolutePath();
						} else {
							File newFile =new File(fileToSave.getAbsolutePath() + "." + ends);
							ImageIO.write( awtImage,ends,newFile);
							path = newFile.getAbsolutePath();
						}
					}
			          
			    } 
			    catch(IOException e1){
					System.out.println("Problems reading.");
				}
		}
		// if this file has been saved once. execute the following code
	     if(DrawingBoard.this.hasSaved ==1 ) {
		       BufferedImage awtImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);						
	           Graphics g = awtImage.getGraphics();
	           canvas.printAll(g);
	   	       if(type == 1)
					try {
					
						ImageIO.write(awtImage,"jpeg",new File(path));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	   	        if(type == 2)
					try {
						
						ImageIO.write(awtImage,"jpg",new File(path));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	   	       if(type == 3)
					try {
						 
						ImageIO.write(awtImage,"png",new File(path));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
			}
	     }		
		System.out.println("save");
		
	}
	
	public void setCanvas(BufferedImage importedImage) {		
		// Combine the background and imported buffered image
//		Graphics2D g2d = this.image.createGraphics();
//		int importedImgWidth = importedImage.getWidth();
//		int importedImgHeigth = importedImage.getHeight();
//		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,(float)1));
//		g2d.drawImage(importedImage, 0, 0, importedImgWidth, importedImgHeigth, null);
//		g2d.dispose();
		
		this.image=importedImage;
		this.gs=this.image.getGraphics();
		this.g=(Graphics2D)this.gs;
		this.g.setColor(backgroundColor);
		this.g.setColor(foregroundColor);
		this.canvas.setImage(this.image);
		this.canvas.repaint();
	}
	
	/*
	 * Function of saving canvas as user-defined type
	 */
	public void saveAs() {
		Point p = new Point(0, 0);
	    SwingUtilities.convertPointToScreen(p, canvas);    

	    /*
	     * Try to screenShot the current image on the canvas and save it as jpg or png.
	     * Catch possible extensions such as AWTException or file exceptions.
	     */
	    try {
	        BufferedImage awtImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);						
	        Graphics g = awtImage.getGraphics();
	        canvas.printAll(g);
	        
	        JFileChooser fileSaveChooser = new JFileChooser("Save a file");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG", "jpeg");						
			fileSaveChooser.setFileFilter(filter);
			fileSaveChooser.addChoosableFileFilter(new 
					FileNameExtensionFilter("PNG","png"));
			fileSaveChooser.addChoosableFileFilter(new 
					FileNameExtensionFilter("JPG","jpg"));

			int returnVal = fileSaveChooser.showSaveDialog(fileSaveChooser);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
			    File fileToSave = fileSaveChooser.getSelectedFile();
				String ends = fileSaveChooser.getFileFilter().getDescription();
				ends = ends.toLowerCase();
				String fileName = fileToSave.getAbsolutePath().toLowerCase();
				
				/*
				 * If the user does not put extension at the end of the file name,
				 * automatically create one.
				 */
				if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg" )||
						fileName.endsWith(".png")) {								 
					ImageIO.write( awtImage,ends,fileToSave);
				} else {
					File newFile =new File(fileToSave.getAbsolutePath() + "." + ends);
					ImageIO.write( awtImage,ends,newFile);
				}
			}
	        
	    } 
	    catch(IOException e1){
			System.out.println("Problems reading.");
		}
		System.out.println("save as");
	}
	
	/*
	 * Function of opening a white board
	 */
	public DrawingBoard openWhiteBoard() {
		try {
		    JFileChooser fileOpenChooser = new JFileChooser("Open a file");
    		int returnVal = fileOpenChooser.showOpenDialog(fileOpenChooser);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File fileToOpen = fileOpenChooser.getSelectedFile();
				String fileName = fileToOpen.getAbsolutePath().toLowerCase();
				 path2 =fileToOpen.getAbsolutePath();
				if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg" )||
						fileName.endsWith(".png")) {
					if(fileName.endsWith(".jpeg")) type2=1;
					if(fileName.endsWith(".jpg")) type2=2;
					if(fileName.endsWith(".png")) type2=3;					
					image=ImageIO.read(fileToOpen);					
					return new DrawingBoard(image,1,type2,path2);	

				}else {
					JOptionPane.showMessageDialog(null, "Please choose valid image!");	
					return null;
				} 	
			}
			else {
				return null;
			}
		
		   
		 }catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		 } catch (IOException e1) {
			e1.printStackTrace();
			return null;
		 }
			//System.out.println("open");
		
	}
	
	/*
	 *  Set the active status of the white board (whether closed)
	 */
	public boolean setActive(boolean status) {
		this.isActive = status;
		return this.isActive;
	}
	
	/*
	 *  Return the active status of the white board (whether closed)
	 */
	public boolean getActive() {
		return this.isActive;
	}

	/*
	 * ???
	 */
	public void publicInit()
	{
		
		// Left Tool Bar
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setOrientation(SwingConstants.VERTICAL);
		toolBar.setBackground(Color.WHITE);
		getContentPane().add(toolBar, BorderLayout.WEST);
		toolBar.addSeparator();
		
		// New
//		btnNew = new JButton();
//		btnNew.setBackground(Color.WHITE);
//		btnNew.setToolTipText("New file");
//		btnNew.setIcon(new ImageIcon(DrawingBoard.class.getResource("/img/btnNew_16.png")));
//		toolBar.add(btnNew);
		// Open
//		btnOpen = new JButton();
//		btnOpen.setBackground(Color.WHITE);
//		btnOpen.setToolTipText("Open file");
//		btnOpen.setIcon(new ImageIcon(DrawingBoard.class.getResource("/img/btnOpen_16.png")));
//		toolBar.add(btnOpen);
		// Save
		btnSave = new JButton();
		btnSave.setBackground(Color.WHITE);
		btnSave.setToolTipText("Save file");
		btnSave.setIcon(new ImageIcon(DrawingBoard.class.getResource("/img/btnSave_16.png")));
		toolBar.add(btnSave);
		// Save as
		btnSaveas = new JButton();
		btnSaveas.setBackground(Color.WHITE);
		btnSaveas.setToolTipText("Save file as");
		btnSaveas.setIcon(new ImageIcon(DrawingBoard.class.getResource("/img/btnSaveas_16.png")));
		toolBar.add(btnSaveas);
		// Separator
		toolBar.addSeparator();
		// Pencil(free drawing)
		btnDraw = new JButton();
		btnDraw.setBackground(Color.WHITE);
		btnDraw.setIcon(new ImageIcon(DrawingBoard.class.getResource("/img/btnDraw_16.png")));
		btnDraw.setToolTipText("Pencil");
		toolBar.add(btnDraw);
		// Rubber
		btnErase = new JButton();
		btnErase.setBackground(Color.WHITE);
		btnErase.setIcon(new ImageIcon(DrawingBoard.class.getResource("/img/btnEraser_16.png")));
		btnErase.setToolTipText("Rubber");
		toolBar.add(btnErase);
		
		// Popup Menu for Shapes
		shapesMenu = new JPopupMenu();
		shapesMenu.setBackground(Color.WHITE);
		// item line
		itemLine = new JMenuItem(new ImageIcon(DrawingBoard.class.getResource("/img/line_16.png")));
		itemLine.setToolTipText("Line");
		itemLine.setBackground(Color.WHITE);
		shapesMenu.add(itemLine);
		// item circle
		itemCircle = new JMenuItem(new ImageIcon(DrawingBoard.class.getResource("/img/circle_16.png")));
		itemCircle.setToolTipText("Circle");
		itemCircle.setBackground(Color.WHITE);
		shapesMenu.add(itemCircle);
		// item oval
		itemOval = new JMenuItem(new ImageIcon(DrawingBoard.class.getResource("/img/oval_16.png")));
		itemOval.setToolTipText("Oval");
		itemOval.setBackground(Color.WHITE);
		shapesMenu.add(itemOval);
		// item square
		itemSquare = new JMenuItem(new ImageIcon(DrawingBoard.class.getResource("/img/square_16.png")));
		itemSquare.setToolTipText("Square");
		itemSquare.setBackground(Color.WHITE);
		shapesMenu.add(itemSquare);
		// item rectangle
		itemRectangle = new JMenuItem(new ImageIcon(DrawingBoard.class.getResource("/img/rectangle_16.png")));
		itemRectangle.setToolTipText("Rectangle");
		itemRectangle.setBackground(Color.WHITE);
		shapesMenu.add(itemRectangle);
		// Shapes
		btnShapes = new JButton();
		btnShapes.setBackground(Color.WHITE);
		btnShapes.setIcon(new ImageIcon(DrawingBoard.class.getResource("/img/btnShapes_16.png")));
		btnShapes.setToolTipText("Shapes");
		toolBar.add(btnShapes);
		// Text
		btnText = new JButton("");
		btnText.setBackground(Color.WHITE);
		btnText.setIcon(new ImageIcon(DrawingBoard.class.getResource("/img/text_16.png")));
		toolBar.add(btnText);
		toolBar.addSeparator();
		
		// PopMenu of Pixel size
		pixelsizeMenu = new JPopupMenu();
		pixelsizeMenu.setBackground(Color.WHITE);
		String[] strlistPixelsize = { " 3 ", " 5 ", " 7 ", " 9 ", "12", "14"};
		int[] intarrayPixelsize = { 3, 5, 7, 9, 12, 14 };
		for(int i=0; i < strlistPixelsize.length; i++) 
		{
			final Integer inneri = new Integer(i);
			JMenuItem itemPixelsize = new JMenuItem(strlistPixelsize[i]);
			itemPixelsize.setBackground(Color.WHITE);
			itemPixelsize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) 
				{
					pixel_size = intarrayPixelsize[inneri];
					btnPixelSize.setText(strlistPixelsize[inneri]);
					BasicStroke bStroke = new BasicStroke(pixel_size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
					g.setStroke(bStroke);
				}
			});
			pixelsizeMenu.add(itemPixelsize);
		}
		// Fill in Color
		/*btnFill = new JButton();
		btnFill.setBackground(Color.WHITE);
		btnFill.setToolTipText("Fill in color");
		btnFill.setIcon(new ImageIcon(DrawingBoard.class.getResource("/img/notfill_16.png")));
		toolBar.add(btnFill);*/

		// Pixel Size
		btnPixelSize = new JButton();
		btnPixelSize.setFont(new Font("Bahnschrift", Font.PLAIN, 12));
		btnPixelSize.setToolTipText("Line size");
		btnPixelSize.setText(" 3 ");
		btnPixelSize.setBackground(Color.WHITE);
		toolBar.add(btnPixelSize);
		// Separater
		toolBar.addSeparator();

		// Forground Color
		btnFc = new JButton("     ");
		btnFc.setBackground(foregroundColor);
		btnFc.setToolTipText("Foreground Color");
		toolBar.add(btnFc);

		// Confirm resizeshape
		btnConfirm = new JButton();
		btnConfirm.setVisible(false);
		btnConfirm.setBackground(Color.WHITE);
		btnConfirm.setIcon(new ImageIcon(DrawingBoard.class.getResource("/img/confirm.png")));
		btnConfirm.setToolTipText("Confirm shape");
		toolBar.add(btnConfirm);
		

		// Condition description text panel
		textPane = new JTextPane();
		getContentPane().add(textPane, BorderLayout.SOUTH);
		textPane.setEditable(false);
		textPane.setText("pencil");
	}

	public void confirmAction() 
	{
			double xTopleft = resizableShape.returnX();
			double yTopleft = resizableShape.returnY();
			double width = resizableShape.returnWidth();
			double height = resizableShape.returnHeight();
			lp.remove(backgroundImage);
			lp.remove(resizableShape);
			if(keyword=="oval"||keyword=="circle")
			{
				BasicStroke bStroke = new BasicStroke(pixel_size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				g.setStroke(bStroke);
				g.setColor(foregroundColor);
				g.drawOval((int) xTopleft,(int) yTopleft,(int) width,(int) height);
			}
			else if(keyword=="square"||keyword=="rectangle")
			{
				BasicStroke bStroke = new BasicStroke(pixel_size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				g.setStroke(bStroke);
				g.setColor(foregroundColor);
				g.drawRect((int) xTopleft,(int) yTopleft,(int) width,(int) height);
			}
			canvas.repaint();
			btnConfirm.setVisible(false);
	}
	
	/*
	 * Initialize canvas
	 */
	public void init() {
		g.setColor(backgroundColor);// set the color for drawing
		g.fillRect(0, 0, canvasWidth, canvasHeight);//set background
		g.setColor(foregroundColor);//set the color for drawing
		canvas.setImage(image);//set background color of canvas
		Container s = getContentPane();
		lp = new JLayeredPane();
		canvas.setBounds(0, 0, canvasWidth, canvasHeight);
		lp.add(canvas, new Integer(100));
		s.add(lp,BorderLayout.CENTER);
	}
	
	public void init2(BufferedImage image2) {
		gs = image2.getGraphics();
		g = (Graphics2D) gs;
		g.setColor(backgroundColor);
		g.setColor(foregroundColor);
		canvas.setImage(image2);
		Container s = getContentPane();
		lp = new JLayeredPane();
		canvas.setBounds(0, 0, canvasWidth, canvasHeight);
		lp.add(canvas, new Integer(100));
		s.add(lp,BorderLayout.CENTER);
        open = true;
	}	

	public boolean clearContent() {
		if(resizable) {
		    confirmAction();
		}
	   image = new BufferedImage( canvasWidth, canvasHeight, BufferedImage.TYPE_INT_BGR);
	   gs = image.getGraphics();
	   g = (Graphics2D) gs;
	   g.setColor(backgroundColor);
	   g.fillRect(0, 0, canvasWidth, canvasHeight);
	   g.setColor(foregroundColor);
	   canvas.setImage(image);
	   canvas.repaint();
	   
	   return true;
	}
	
	public void confirmLine()
	 {
	  double xs = resizableLine.returnXs();
	  double ys = resizableLine.returnYs();
	  double xe = resizableLine.returnXe();
	  double ye = resizableLine.returnYe();
	  lp.remove(backgroundImage);
	  lp.remove(resizableLine);
	  BasicStroke bStroke = new BasicStroke(pixel_size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	  g.setStroke(bStroke);
	  g.setColor(foregroundColor);
	  g.drawLine((int) xs,(int) ys,(int) xe,(int) ye);
	  canvas.repaint();
	  btnConfirm.setVisible(false);
	 }
}
