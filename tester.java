import java.util.*;
import java.io.File;


public class tester {

	public static void main(String[] args) {
	    Image2d img1 = new Image2d(600,400);
	    File file = new File("C:\\Users\\Joshua\\Documents\\polyominosINF421.txt");
		ArrayList<Polyomino> polyominos=Polyomino.get_polyominos(file);//extract polyominos from text file
		
		//properly display polyominos so they don't overlap
		for (int i=0;i<polyominos.size();i++) {
	    	Polyomino p =polyominos.get(i);
	    	p.translate(5*i, 0);
	    	p.add_to(img1,false);
		}
		
		
	    Image2dViewer frame1 = new Image2dViewer(img1);//display
	     
	    //now let's test translation, reflection, rotation, dilation on the above polyominos
	    ArrayList<Polyomino> polyominos2=Polyomino.get_polyominos(file);
	    Image2d img2 = new Image2d(600,400);
		for (int i=0;i<polyominos2.size();i++) {//properly display polyminos so they don't overlap
	    	Polyomino p =polyominos2.get(i);
	    	
	    	if (i==0) p.rotate();
	    	if (i==1) p.dilate(2);
	    	if (i==2) {
	    		p.reflect_y();
	    		p.translate(3, 0);
	    	}
	    	if (i==4) p.translate(0, 5);
	    
	    	if (i==5) {
	    		p.reflect_x();
	    	}
	    	p.translate(7*i, 0);
	    	
	    	p.add_to(img2,false); 	

		}
		
		Image2dViewer frame2 = new Image2dViewer(img2);//display
		
		//--------------------------------------------------------------------------------------
		//displaying the 10 octominos which tile their 4-dilate
		
		int [] tiles= new int[] {4,5,7,46,50,83,150,303,333,368};
		int k=0;
		Image2d img3 = new Image2d(600,400);
		ArrayList<Polyomino> polyominos_list=Generate.polyominos(8,"free");
		for (int i=0;i<polyominos_list.size();i++) {
	    	Polyomino p =polyominos_list.get(i);
	    	for (int c:tiles) {
	    		if (c==i){
	    			p.rotate();
	    	    	p.translate(9*k, 0);
	    	    	p.add_to(img3,false);
	    	    	k++;
	    		}
	    		
	    	}
		}
		Image2dViewer frame3 = new Image2dViewer(img3);//display
		
	    
	}
	
}
