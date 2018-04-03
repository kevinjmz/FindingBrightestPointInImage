
/************************************************************************************** 
 ***  This program shows an example of elementary operations in Java						***
 ***  It reads an image from a file, finds the pixel with the highest intensity   	***
 ***  in the image and  ***  draws a square around that pixel								***
 ***  Last modified by Olac Fuentes, June 7, 2015											***			
 **************************************************************************************/ 

/****************************************************
 * Kevin Jimenez 
 * The University of Texas at El Paso
 * CS2302 Data Structures
 * Lab 1
 * Professor Olac Fuentes
 * Last modified 29/01/2016
 ****************************************************/
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

class imageMax {

	public static int getPixelIntensity(int RGB){
		// Decodes the 32-bit integer value stored in RGB, splitting it into red, green and blue values
		// The returned intensity is simply the average of the three basic components
		int red = RGB >> 16 & 0xff;
		int green = RGB >> 8 & 0xff;
		int blue = RGB >> 0 & 0xff;

		return (red+green+blue)/3;  //returns the value of a single pixel
	}

	public static int[][] extractIntensity( BufferedImage I){
		// Receives an image I and returns an array of the same size as I where each element contains the intensity
		// of the corresponding pixel in I
		int rows = I.getHeight();
		int cols = I.getWidth();
		int [][] T = new int[cols][rows];
		for (int y =0;y<rows;y++)
			for (int x =0;x<cols;x++)
				T[x][y] = getPixelIntensity(I.getRGB(x,y));
		return T;//returns 2d array the area that has being analized
	}

	public static int[] maxArray(int [][] A){
		// Returns the x,y coordiates of the largest element in A
		int [] max = {0,0};
		int maxVal = A[0][0];
		for (int i =0;i<A.length;i++)
			for (int j =0;j<A[i].length; j++)
				if (A[i][j] > maxVal){
					maxVal = A[i][j];
					max[0]=i;
					max[1]=j;
				}
		return max;
	}

	public static int [] SummedAreaTable (int [][] I, int width, int height){
		int [][] newtable= new int [I.length+1][I[0].length+1];//w x h
		int [] infoArray= new int [3];

		for(int j=1; j<newtable.length;j++){
			for (int i=1; i<newtable[0].length; i++){
				newtable[j][i]=newtable[j-1][i]+newtable[j][i-1]-newtable[j-1][i-1]+I[j-1][i-1];//algorithm to create new bigger table
			}
		}

		double getArea=0;
		double maxArea=0;
		int maxX=0;
		int maxY=0;

		for (int x=0;x<I[0].length-height-1;x++){
			for (int y=0;y<I.length-width-1;y++){
				getArea=newtable[width+y][height+x] - newtable[y][height+x]-newtable[width+y][x] + newtable[y][x];//algorithm to get areas of values
				if (getArea>maxArea){
					maxArea=getArea;//get maximum area of the entire image
					maxX=x;
					maxY=y;

				}
			}
		}
		infoArray[0]=(int) maxArea;
		infoArray[1]=maxY;
		infoArray[2]=maxX;
		return infoArray;
	}

	public static int [] maxAverageNaive (int [][] I,int height, int width){
		int sumOfPixels = 0;
		int littleSquare=height*width;
		int average =0;
		int [] infoArray= new int[3];  //0 is the value of the average, 1 is the biggestX and 2 is biggestY
		infoArray[0]=0;
		infoArray[1]=0;
		infoArray[2]=0;
		int XMAX = 0;
		int YMAX = 0;

		for(int i=0; i < I.length-width; i++){ //width
			for(int j=0;j < I[0].length-height; j++){//height
				sumOfPixels=0;
				XMAX=i;
				YMAX=j;
				for (int k =i; k<=width+i; k++){//traverse area values
					for (int l=j;l<=height+j;l++){
						sumOfPixels=sumOfPixels + I[k][l];//add all area values
					}	
				}
				average=sumOfPixels / littleSquare;//get average


				if (average > infoArray[0]){
					infoArray[0]=average;
					infoArray[1]=XMAX;
					infoArray[2]=YMAX;	
				}

			}
		}
		return infoArray;
	}


	public static void main(String[] args) throws IOException{
		int [] results= new int [3];
		int ScanningHeight;
		int ScanningWidth;
		Scanner choice = new Scanner (System.in);
		int algorithm=0;


		BufferedImage Img=ImageIO.read(new File("sunset.jpg")); //bring image
		Dimension imgDim = new Dimension(Img.getWidth(), Img.getHeight()); //create window 
		BufferedImage newImage = new BufferedImage(imgDim.width, imgDim.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = newImage.createGraphics(); 
		g2d.setBackground(Color.WHITE);
		g2d.fillRect(0, 0, imgDim.width, imgDim.height);
		g2d.setColor(Color.BLUE);
		BasicStroke bs = new BasicStroke(1);
		g2d.setStroke(bs);
		g2d.drawImage(Img, 0, 0, null);

		int [][] intensity =  extractIntensity(Img);
		int [] max =  maxArray(intensity);
		System.out.println("Maximum intensity of a pixel is " +intensity[max[0]][max[1]]+ " in x = "+max[0]+", y = "+ max[1]);

		System.out.println("Please type the desired height of your scanning rectangle");
		Scanner input = new Scanner (System.in);
		ScanningHeight= input.nextInt();
		if (ScanningHeight>Img.getHeight()||ScanningHeight<=0)
		{
			System.out.println("Sorry your the amount you entered do not match the image properties.\n Please enter it again");
			ScanningHeight= input.nextInt();
		}

		System.out.println("Please type the desired width of your scanning rectangle");
		ScanningWidth= input.nextInt();

		if (ScanningWidth>Img.getTileWidth()||ScanningWidth<=0)
		{
			System.out.println("Sorry your the amount you entered do not match the image properties.\n Please enter it again");
			ScanningWidth= input.nextInt();
		}

		System.out.println("Which method would you like to choose? Write the 1 for Naive Algorithm or 2 for Summed Area Table Algorithm.");
		algorithm=choice.nextInt();

		while( algorithm!=1 && algorithm!=2)//check for fake prompts
		{System.out.println("Sorry but the input you entered is not valid please try again. Type 1 for Naive Algorithm or 2 for Summed Area Table Algorithm.");
		algorithm=choice.nextInt();}

		if (algorithm==1)
		{
			long start = System.nanoTime();	
			results=maxAverageNaive(intensity,ScanningHeight,ScanningWidth);// call naive algorithm
			long end = System.nanoTime();//calculate time execution
			long Time = end - start;
			System.out.println("The time spent to run Naive Algorithm was:  "+Time+" ns.");

			g2d.drawLine(results[1], results[2], results[1]+ScanningWidth, results[2]);//x,y,x+w,y
			g2d.drawLine(results[1], results[2], results[1], results[2]+ScanningHeight);//x,y,x,y+h
			g2d.drawLine(results[1]+ScanningWidth,results[2], results[1]+ScanningWidth, results[2]+ScanningHeight);//x+w,y,x+w,y+h
			g2d.drawLine(results[1], results[2]+ScanningHeight,results[1]+ScanningWidth, results[2]+ScanningHeight);//x,y+h,x+w,y+h

			System.out.println( "The maximum average intensity is  "+results[0]+" in x="+results[1]+" and y="+results[2]);	

		}	

		if (algorithm==2)
		{
			long startsum = System.nanoTime();	
			results=SummedAreaTable(intensity, ScanningWidth, ScanningHeight);//call summed area algorithm
			long endsum = System.nanoTime();//calculate time execution
			long Time2 = endsum - startsum;
			System.out.println("The time spent to run Sum Algorithm was:  "+Time2+"  ns.");
			g2d.drawLine(results[1], results[2], results[1]+ScanningWidth, results[2]);//Draw square x,y,x+w,y
			g2d.drawLine(results[1], results[2], results[1], results[2]+ScanningHeight);//x,y,x,y+h
			g2d.drawLine(results[1]+ScanningWidth,results[2], results[1]+ScanningWidth, results[2]+ScanningHeight);//x+w,y,x+w,y+h
			g2d.drawLine(results[1], results[2]+ScanningHeight,results[1]+ScanningWidth, results[2]+ScanningHeight);//x,y+h,x+w,y+h
			System.out.println( "The maximum average intensity is  "+results[0]+" in x="+results[1]+" and y="+results[2]);	


		}

		ImageIcon ii = new ImageIcon(newImage);
		JOptionPane.showMessageDialog(null, ii);


	}
}