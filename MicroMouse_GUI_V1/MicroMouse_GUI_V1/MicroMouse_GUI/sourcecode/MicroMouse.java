
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.event.*;
import java.lang.*;
import java.math.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

class AI extends Thread 
{
	int speed = 100;
	boolean sleep = false;
	Maze maze;

	AI(Maze m)
	{
		maze = m;
		init();
	}

	public void setSpeed(int spd)
	{
		speed = spd;
	}
	public void run()
	{
		while(true)
		{
			try
			{
				Thread.sleep(1000-speed);
			}catch(InterruptedException e)
			{
			}

			int exit = move();
			
			if(exit == 1) {
				break;
			}
			
			maze.repaint();

			while(sleep)
			{
				try
				{
					Thread.sleep(100);
				}catch(InterruptedException e)
				{
				}
			}
		}
	}

	int position[][] = new int[16][16];
	int middleFloodAr[][] = new int[16][16];
	int startAr[][] = new int[16][16];
	int repeat[][] = new int[16][16];

	int nodeRepeat[][] = new int [16][16];
	int unexploredNodes[][] = new int[16][16];

	int nodeCount[][][] = new int [16][16][4];
	int nodeCount_copy[][][] = new int [16][16][4];

	int repeatCount[][] = new int [16][16];

	int prev_steps = 0;
	int curr_steps = 0;

	int paths = 0; //number paths a node can take

	int node[][] = new int[16][16]; //number of allowed paths to take at coordinates
	int entrances[][] = new int[16][16];
	int exits[][] = new int [16][16];
	int exits_copy[][] = new int [16][16];

	boolean hitDeadEnd = false;

	int total_steps = 0;
	
	int count = 0;

	boolean override = false; // Maria - 

	private void init()
	{
		int max = 14;
		int min = 7;

		for(int y = 0; y < 16; y++)
		{
			int cnt = min;
			for(int x = 0; x < 16; x++)
			{
				if(x < 8)
				{
					middleFloodAr[x][y] = max-x;
				}else
				{
					middleFloodAr[x][y] = cnt;
					cnt++;
				}

			}

			if(y < 7)
			{
				max--;
				min--;
			}else if(y > 7)
			{
				max++;
				min++;
			}

		}

		for(int y = 0; y < 16; y++)
		{
			int start = 15 - y;
			for(int x = 0; x < 16; x++)
			{
				repeat[x][y] = 0;
				startAr[x][y] = start + x;
			}
		}
		position = middleFloodAr;
	}

	public void reset_values(){/*********************************************************/
		for(int y = 0; y < 16; y++)
		{
			int start = 15 - y;
			for(int x = 0; x < 16; x++)
			{
				exits_copy[x][y] = 0;
				node[x][y] = 0;
				nodeRepeat[x][y] = 0;
				// Maria - entrances[x][y] = 0;
				// Maria - exits[x][y] = 0;
				
			}
		}
		position = middleFloodAr;
	}

	public void resetArr(int arr[][]){
		for(int y = 0; y < 16; y++)
		{
			int start = 15 - y;
			for(int x = 0; x < 16; x++)
			{
				arr[x][y] = 0;
			}
		}
	}

	//global variables to check direction
	int up_ = 1;
	int down_ = 4;
	int left_ = 3;
	int right_ = 2;

	int state = 0;
	int last = 0;
	int i = 0;

	boolean last_run = true;

	int mousePath[][] = new int [16][16];

	
	/* Maria - 
	private void copyArr(int arr1[][], int arr2[][]){ 
		
		for(int y = 0; y < 16; y++)
		{
			int start = 15 - y;
			for(int x = 0; x < 16; x++)
			{
				arr1[x][y] = arr2[x][y];
				
			}
		}
	}
	*/

	private int move() //get rid of all exits_copy to revert
	{

		// cut off dead end
		if(node[maze.Rx][maze.Ry] > 2 && hitDeadEnd && entrances[maze.Rx][maze.Ry] != last){
         System.out.println("HERE");
			
			if(last == 1){//up
				maze.top[maze.Rx][maze.Ry+1] = true;
			}
			if(last == 2){//right
				maze.side[maze.Rx][maze.Ry] = true;
			}
			if(last == 3){//left 
				maze.side[maze.Rx+1][maze.Ry] = true;
			}
			if(last == 4){//down
				maze.top[maze.Rx][maze.Ry] = true;
			}
			
			hitDeadEnd = false; // Maria - Can we change this to maybe continue reversing until the previous node. 
					
			//override = true;

			node[maze.Rx][maze.Ry] -= 1; // update path of this node, reduced to 2
			
			resetArr(nodeRepeat); //not sure ***********************************************************************

			// repeat[maze.Rx][maze.Ry] -= 1; // Maria - I think repeat should still be counting
			
			count = 0;
			
		}

		// Maria - Changes from beginning of move to now yieled 93 total steps on last run

		if(node[maze.Rx][maze.Ry] > 2 && nodeRepeat[maze.Rx][maze.Ry] > 1 && entrances[maze.Rx][maze.Ry] != last){
			
				if(last == up_){
					maze.top[maze.Rx][maze.Ry+1] = true;
				}
				if(last == right_){
					maze.side[maze.Rx][maze.Ry] = true;
				}
				if(last == left_){
					maze.side[maze.Rx+1][maze.Ry] = true;
				}
				if(last == down_){
					maze.top[maze.Rx][maze.Ry] = true;
				}

			prev_steps = 0;
			curr_steps = 0;
		
			node[maze.Rx][maze.Ry] -= 1;
			count = 0;
			// Maria - resetArr(nodeRepeat);
			// Maria - resetArr(exits_copy);
			repeat[maze.Rx][maze.Ry] -= 1;

		}	

		repeat[maze.Rx][maze.Ry]++; // increment that we've been here before
		 

		if(position[maze.Rx][maze.Ry] == 0)
		{
			//reset values
			count = 0;
			prev_steps = 0;
			curr_steps = 0;

			System.out.println("Mouse reached its destination");
			System.out.println("");
			System.out.println("Path of the Mouse and number of times mouse entered each box.");
			System.out.println("");
			state = 1;

			//copyArr(exits_copy, exits);
			
			position = startAr;
			
			return 1;
		}

		//check for walls
		boolean right = false;
		boolean left = false;
		boolean up = false;
		boolean down = false;

		paths = 0;

		if(!maze.getTop(maze.Rx, maze.Ry)){
			up = true;
			paths++;
			
		}
		if(!maze.getRight(maze.Rx, maze.Ry)){
			right = true;
			paths++;
			
		}
		if(!maze.getLeft(maze.Rx, maze.Ry)){
			left = true;
			paths++;
			
		}
		if(!maze.getBottom(maze.Rx, maze.Ry)){
			down = true;
			paths++;
			
		}

		//Stops from going in reverse except at dead end
		if(paths != 1){
			if(up_ == last){
				down = false;
			}
			if (right_ == last){
				left = false;
			}
			if(left_ == last){
				right = false;
			}
			if(down_ == last){
				up = false;
			}
			
			if(paths > 2){
				
				if(exits_copy[maze.Rx][maze.Ry] == up_ ){
					up = false;
				}
				if(exits_copy[maze.Rx][maze.Ry] == right_){
					right = false;
				}
				if(exits_copy[maze.Rx][maze.Ry] == left_){
					left = false;
				}
				if(exits_copy[maze.Rx][maze.Ry] == down_){
					down = false;
				}

				
					if(entrances[maze.Rx][maze.Ry] == up_ ){
						down = false;
					}
					if(entrances[maze.Rx][maze.Ry] == right_){
						left = false;
					}	
					if(entrances[maze.Rx][maze.Ry] == left_){
						right = false;
					}
					if(entrances[maze.Rx][maze.Ry] == down_){
						down = false;
					}
				
						
				
				
			}
		}

		/*************************************************************/
		curr_steps++;
		
		System.out.println(curr_steps);


		if(paths > 2){
			
			if(entrances[maze.Rx][maze.Ry] == 0){ 
				entrances[maze.Rx][maze.Ry] = last;		
					
			}

			node[maze.Rx][maze.Ry] = paths; // set the path for the nodes
			nodeRepeat[maze.Rx][maze.Ry] = count++; // we've been at this node before 

		}
		
		// Check for dead end 
		if(paths == 1){
			if((maze.Rx == 0 && maze.Ry != 15) || (maze.Rx !=0 && maze.Ry == 15) || (maze.Rx !=0 && maze.Ry != 15)){
				hitDeadEnd = true;
				System.out.println("DEAD END");
			}

			//not sure to reset even at start position
			//curr_steps = 0;		
		}
 
		/************************************************************/
		if(state == 0 || state == 1)
		{
		int best = 10000;
			if(last_run){
				if(up){
				//if(up_ != last){
					if(repeat[maze.Rx][maze.Ry-1] < best)
					{
						best = repeat[maze.Rx][maze.Ry-1];
					}else if(repeat[maze.Rx][maze.Ry-1] > best)
					{
						up = false;
					}
				/*}
				else{
					down = false;
				}*/
				
				}
				if(right) {
				//if(right_ != last){
					if(repeat[maze.Rx+1][maze.Ry] < best)
					{
						best = repeat[maze.Rx+1][maze.Ry];
						up = false;
					}else if(repeat[maze.Rx+1][maze.Ry] > best)
					{
						right = false;
					}
				//}
			//	else {
			//		left = false;
			//	}
				
				}
				if(left) {
				//if(left_ != last){
					if(repeat[maze.Rx-1][maze.Ry] < best)
					{
						up = false;
						right = false;
						best = repeat[maze.Rx-1][maze.Ry];
					}else if(repeat[maze.Rx-1][maze.Ry] > best)
					{
						left = false;
					}
				//}
				//else{
				//	right = false;
				//}
				}
				if(down) {
			   //if(down_ != last){
					if(repeat[maze.Rx][maze.Ry+1] < best)
					{
						up = false;
						right = false;
						left = false;
						best = repeat[maze.Rx][maze.Ry+1];
					}else if(repeat[maze.Rx][maze.Ry+1] > best)
					{
						down = false;
					}
				//}
				//else {
				//	up = false;
				//}
				}
			}

			best = 35;

			if(up)
			{
				if(position[maze.Rx][maze.Ry-1] < best)
				{
					best = position[maze.Rx][maze.Ry-1];
				} else if(position[maze.Rx][maze.Ry-1] > best)
				{
					up = false;
				}
			}
			if(right)
			{
				if(position[maze.Rx+1][maze.Ry] < best)
				{
					up = false;
					best = position[maze.Rx+1][maze.Ry];
				}else if(position[maze.Rx+1][maze.Ry] > best)
				{
					right = false;
				}
			}
			if(left)
			{
				if(position[maze.Rx-1][maze.Ry] < best)
				{
					up = false;
					right = false;
					best = position[maze.Rx-1][maze.Ry];
								if(i == 167)
								{
									System.out.println(best);
			}
				}else if(position[maze.Rx-1][maze.Ry] > best)
				{
					left = false;
				}
			}
			if(down)
			{
				if(position[maze.Rx][maze.Ry+1] < best) {
					up = false;
					right = false;
					left = false;
					best = position[maze.Rx][maze.Ry+1];
				} else if(position[maze.Rx][maze.Ry+1] > best) {
					down = false;
				}
			}
		} 
		if(i == 167) {
			System.out.println(i + ": " + left);
		}
		if(up && last == 1) {
			right = false;
			left = false;
			down = false;

		} else if(right && last == 2) {
			up = false;
			left = false;
			down = false;
			
		} else if(left && last == 3) {
			up = false;
			right = false;
			down = false;
			
		} else if(down && last == 4) {
			up = false;
			right = false;
			left = false;
			
		}

		i++;

		if (!last_run) {
			mousePath[maze.Rx][maze.Ry] = 1;
		}
		

		if(up)
		{			
			if(exits[maze.Rx][maze.Ry] == 0 && paths > 2){
				exits[maze.Rx][maze.Ry] = 1;
			}
			exits_copy[maze.Rx][maze.Ry] = 1;

			maze.moveUp();

			
			last = 1;
			
			if(!hitDeadEnd){
				System.out.println("Direction : " + Direction.getHeadDirection(true, false, false, false));
			} else {
				System.out.println("Direction : " + Direction.getHeadDirection(false, false, true, false));
			}
			
		} else if(right) {
			if(exits[maze.Rx][maze.Ry] == 0 && paths > 2){
				exits[maze.Rx][maze.Ry] = 2;
				
			}
			exits_copy[maze.Rx][maze.Ry] = 2;

			maze.moveRight();
			
			last = 2;
			if(!hitDeadEnd){
				System.out.println("Direction : " + Direction.getHeadDirection(false, true, false, false));
			} else {
				System.out.println("Direction : " + Direction.getHeadDirection(false, false, false, true));
			}
		} else if(left) {
			if(exits[maze.Rx][maze.Ry] == 0 && paths > 2){
				exits[maze.Rx][maze.Ry] = 3;
			}
			exits_copy[maze.Rx][maze.Ry] = 3;

			maze.moveLeft();
			
			last = 3;
			if(!hitDeadEnd){
				System.out.println("Direction : " + Direction.getHeadDirection(false, false, false, true));
			} else {
				System.out.println("Direction : " + Direction.getHeadDirection(false, true, false, false));
			}
			
		} else {
			if(exits[maze.Rx][maze.Ry] == 0 && paths > 2){
				exits[maze.Rx][maze.Ry] = 4;
		
			}
			exits_copy[maze.Rx][maze.Ry] = 4;

			maze.moveDown();		
			
			last = 4;
			
			if(!hitDeadEnd){
				System.out.println("Direction : " + Direction.getHeadDirection(false, false, true, false));
			}
			else{
				System.out.println("Direction : " + Direction.getHeadDirection(true, false, false, false));
			}
		}

		
		total_steps++;

		return 0;
	}
}

/*
*Dont touch below 
*Prints out whats happening
*/

class MicroMouse extends JFrame implements ActionListener, ChangeListener
{
	JFrame main;
	JPanel btn;
	Maze maze;
	AI ai;

	int speed = 50;
	public MicroMouse()
	{
		main = new JFrame("MicroMouse");
		btn = new JPanel();
		main.setSize(450, 550);
		main.setLayout(new BorderLayout());
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Maze maze = new Maze();
		main.getContentPane().add(maze, BorderLayout.LINE_START);

		JSlider spd = new JSlider(JSlider.HORIZONTAL, 0, 1000, 650);
		spd.addChangeListener(this);
		main.getContentPane().add(spd, BorderLayout.PAGE_END);

		main.getContentPane().add(btn, BorderLayout.LINE_END);

        main.setVisible(true);

        maze.LoadMaze();
		maze.moveUp();
		maze.Rx = 0;
		maze.Ry = 15;
		ai = new AI(maze);
		
		//first node
		ai.node[0][15] = 3; // MIGHT NEED TO DELETE

		//loopng after each run
		for(int i=0; i<6; i++){
			
			ai.total_steps = 0;

			maze.Rx = 0;
			maze.Ry = 15;
			ai.reset_values();
			
			if(i == 5){
				ai.last_run = false;
			}
			ai.run();

			System.out.println("\nLOOP: " + (i + 1) + "\nSTEPS TAKEN: " + ai.total_steps + "\n");
		}

		System.out.println("MOUSE PATH: \n");

		for(int y = 0; y < 16; y++)
		{
			int start = 15 - y;
			for(int x = 0; x < 16; x++)
			{
				
				System.out.print(ai.mousePath[x][y] + " ");
			}
			System.out.println("");
		}

	}
	public static void main(String arg[]){
		new MicroMouse();
	}

	public void actionPerformed(ActionEvent e){
	}
	public void stateChanged(ChangeEvent e)
	{
	    JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	    	ai.setSpeed((int)source.getValue());
	    }
	}
}


class Maze extends JPanel
{
	boolean top[][] = new boolean[16][17];
	boolean side[][] = new boolean [17][16];
	int Rx = 0;
	int Ry = 15;
	int Sx = 0;
	int Sy = 15;

	int x = 300;
	
	Map<Integer,Integer> mouseMap = new HashMap<Integer, Integer>();
	
	public boolean redrawPath = false;

	protected void paintComponent(Graphics g) {
		setSize(430,430);
		g.setColor(Color.WHITE);
	    g.fillRect(0, 0, getWidth(), getHeight());
	    g.setColor(Color.BLACK);
	    for(int y = 0; y < 17; y++) {
			for(int x = 0; x < 16; x++){
				if(top[x][y]) {
					g.drawLine(x*26+5,y*26+5, x*26+31,y*26+5);
				}
			}
		}
		for(int y = 0; y < 16; y++) {
			for(int x = 0; x < 17; x++) {
				if(side[x][y])
				{
					g.drawLine(x*26+5,y*26+5, x*26+5,y*26+31);
				}
			}
		}

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(6+(Sx*26), 6+(Sy*26), 25, 25);

		g.setColor(Color.CYAN);
		g.fillRect(6+(7*26), 6+(7*26), 51, 51);

		g.setColor(Color.ORANGE);
		g.fillRect(9+(Rx*26), 9+(Ry*26), 19, 19);
		
		if(Direction.mouseHead.equals("N")) {
			g.setColor(Color.BLACK);
			g.fillRect(9+(Rx*26), 9+(Ry*26), 19, 5);
		}
		if(Direction.mouseHead.equals("E")) {
			g.setColor(Color.BLACK);
			g.fillRect(24+(Rx*26), 9+(Ry*26), 5, 19);
		}
		if(Direction.mouseHead.equals("W")) {
			g.setColor(Color.BLACK);
			g.fillRect(5+(Rx*26), (9+Ry*26), 5, 19);
		}
		if(Direction.mouseHead.equals("S")) {
			g.setColor(Color.BLACK);
			g.fillRect((9+Rx*26), (24+Ry*26), 19,5);
		}
	}
	void setStart(int x, int y) {
		Sx = x;
		Sy = y;
	}
	boolean[][] getTop() {
		return top;
	}
	boolean[][] getSide() {
		return side;
	}
	void printMaze(){
		for(int y = 0; y < 16; y++) {
			System.out.print(" ");
			for(int x = 0; x < 16; x++) {
				if(top[x][y]) {
					System.out.print("- ");
				} else {
					System.out.print("  ");
				}
			}
			System.out.println("");
			for(int x = 0; x < 17; x++) {
				if(x == Rx && y == Ry) {
					if(side[x][y]) {
						System.out.print("|0");
					} else {
						System.out.print(" 0");
					}
				} else {
					if(side[x][y]) {
						System.out.print("| ");
					} else {
						System.out.print("  ");
					}
				}
			}
			System.out.println("");
		}
		System.out.print(" ");
		for(int x = 0; x < 16; x++) {
			if(top[x][16]) {
				System.out.print("- ");
			} else {
				System.out.print("  ");
			}
		}
		System.out.println("");
	}

	void LoadMaze() {
		File fin = new File("../InputTextFiles/maze2.txt");
		try {
			FileInputStream in = new FileInputStream (fin);
			char let;
			let = (char)in.read();
			boolean flag = true;
			boolean spaces = false;
			int x = 0;
			int y = 0;
			while((byte)let != -1) {
				if(flag && x < 16) {
					if(spaces) {
						if(let == '-' || let == '_') {
							top[x][y] = true;
							x++;
						} else {
							top[x][y] = false;
							x++;
						}
						spaces = false;
					} else {
						spaces = true;
					}
				} else if(!flag && x < 17) {
					if(!spaces) {
						if(let == '|') {
							side[x][y] = true;
							x++;
						} else {
							side[x][y] = false;
							x++;
						}
						spaces = true;
					} else {
					spaces = false;
					}
				}
				if(let == '\n') {
					flag = !flag;
					if(flag == true) {
						y++;
					}
					x = 0;
					spaces = false;
				}
				let = (char)in.read();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		for(int y = 0; y < 16; y++) {
			for(int x = 0; x < 17; x++) {
				if(side[x][y]){
				} else	{
				}
			}
		}
	}
	boolean getTop(int x, int y) {
		return top[x][y];
	}
	boolean getBottom(int x, int y) {
		return top[x][y+1];
	}
	boolean getLeft(int x, int y){
		return side[x][y];
	}
	boolean getRight(int x, int y) {
		return side[x+1][y];
	}
	void setRx(int x) {
		Rx = x;
	}
	void setRy(int y) {
		Ry = y;
	}
	void setPos(int x, int y) {
		Ry = y;
		Rx = x;
	}
	int getRx() {
		return Rx;
	}
	int getRy() {
		return Ry;
	}
	boolean moveUp() {
		if(!getTop(Rx, Ry)) {
			Ry--;
			return true;
		} else {
			return false;
		}
	}
	boolean moveDown() {
		if(!getBottom(Rx, Ry)) {
			Ry++;
			return true;
		} else {
			return false;
		}
	}
	boolean moveRight() {
		if(!getRight(Rx, Ry)) {
			Rx++;
			return true;
		} else {
			return false;
		}
	}
	boolean moveLeft() {
		if(!getLeft(Rx, Ry)) {
			Rx--;
			return true;
		} else {
			return false;
		}
	}
}
