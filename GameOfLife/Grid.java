public class Grid{
	private boolean[][] grid;
	private static boolean borderless;

	public Grid(int x, int y){
		grid = new boolean[x][y];
		borderless = false;
		//System.out.println("================\t" + grid.length + " , " + grid[0].length + "\t=================");
	}
	public boolean getStatus(int x, int y){
		return grid[x][y];
	}
	public void setGrid(boolean[][] newGrid){
		grid = newGrid;
	}
	public void update(boolean borderlessParam){
		borderless = borderlessParam;
		boolean[][] temp = new boolean[grid.length][grid[0].length];
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[i].length; j++){
        		temp[i][j] = deadOrAlive(i,j);
			}
		}
		grid = temp;
	}
	private boolean deadOrAlive(int x, int y){
		//System.out.println("CHECKING POINT:\t" + x + " , " + y);
		int n = 0;
		for(int i = x - 1; i <= x + 1; i++){
			for(int j = y - 1; j <= y + 1; j++){
                if(i >= 0 && i < grid.length && j >= 0 && j < grid.length && grid[i][j]) n++;
				else if(borderless){
					int virtualX = i;	int virtualY = j;
					if(virtualX == -1) virtualX = grid.length - 1;
					else if(virtualX == grid.length) virtualX = 0;

					if(virtualY == -1) virtualY = grid.length - 1;
					else if(virtualY == grid[virtualX].length) virtualY = 0;
                	
                	if(grid[virtualX][virtualY])n++;
				}
			}
		}
		if(grid[x][y])n--;
		return ((grid[x][y] && (n == 2 || n == 3)) || (!grid[x][y] && n == 3));
	}
	private void print(){
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[i].length; j++){
				if(grid[i][j])System.out.print("X");
				else System.out.print("_");
			}
			System.out.println();
		}
		System.out.println("=============================");
	}
	public void clicked(int x, int y){
		if(x >= 0 && x < grid.length && y >= 0 && y < grid[0].length)
			grid[x][y] = !grid[x][y];
	}
	public void clear(){
		grid = new boolean[grid.length][grid[0].length];
	}
	public static void main(String[]args){
		Grid x = new Grid(10,10);
	}
}