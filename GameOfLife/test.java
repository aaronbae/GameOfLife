import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class test{
	private static int XAXIS = 50;
	private static int YAXIS = 50;
	private static int SIDE = 10;
	public static void main(String[]args)throws IOException{
    	Grid grid = new Grid(XAXIS, YAXIS);
    	saveGrid(grid);
	}
	public static void saveGrid(Grid grid)
	{
		try{
			int name = Calendar.getInstance().getTime().hashCode();
			System.out.println(name);
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
			ImageIO.write(image ,"png" ,new File(name + ".png"));
		} catch (IOException e){
			System.out.println("FILENOTFOUNDEXCEPTION");
		}
	}
}