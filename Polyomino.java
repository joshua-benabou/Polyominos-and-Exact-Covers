import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Polyomino {
	
	ArrayList<Square> vertices= new ArrayList<Square>();
	int area;
	Color color=new Color(255,0,0);//default color is red
	boolean redelmeier=false;//default canonical form is with (x_min,y_min) is at the origin, according to my naive implementation
	
	//CONSTRUCTOR: polyomino in my canonical form from ArrayList of vertices, WHICH WE ASSUME IS ORDERED
	Polyomino(final ArrayList<Square> vertices){
		this.vertices=vertices;
		area=vertices.size();
		translate_to_origin();
	}
	
	//OVERLOADED CONSTRUCTOR: for generating Polyominos without translating to origin, according to Redelmeier's method
	Polyomino(final ArrayList<Square> vertices,String s){//creating polyomino  from ArrayList of vertices
		this.vertices=vertices;
		area=vertices.size();
		redelmeier=true;
	}
	
	//creates Polyomino in my canonical form from string representing vertices 
	Polyomino(String s){
		int l=s.length();//for N vertices, the string length is (6*N-2)+2=6*N
		area=l/7;
		//parse string 
		int x_min = -Integer.MAX_VALUE;
		int y_min= -Integer.MAX_VALUE;
		for (int k=1; k<=area;k++) {
			//coordinate x_k is at position 7*(k-1)+2=7k-5, y_k at 7k-3
			
			int x=Character.getNumericValue(s.charAt(7*k-5));
			int y=Character.getNumericValue(s.charAt(7*k-3));
	
			
			if (x<x_min) x_min=x;	
			if (y<y_min) y_min=y;

			
			vertices.add(new Square(x,y));
		}
		translate(-x_min,-y_min);//put in canonical form
	}

	
	boolean contains(Square square) {
		for (Square p: vertices) {
			if (p.x==square.x && p.y==square.y) return true;
		}
		return false;
	}


	//test if two Polyominos have the same vertices,IN THE SAME ORDER
	boolean equals (Polyomino q) {
		if (!(q.area==area)) return false;
		else {
			for (int i=0; i<vertices.size();i++) {
				if (!vertices.get(i).equals(q.vertices.get(i))) return false;
			}
			return true;
		}

	}
	//imposing an order on Polyominos of the same area in which (x_min,y_min) at origin and for which vertex lists are ORDERED (lexicographically)
	public boolean lessThan(Polyomino q) {
		int i=0;
		boolean lessThan=false;
		while (i<vertices.size()) {
			if ( vertices.get(i).lessThan((q.vertices.get(i))) ){
				lessThan=true;
				break;
			}
			
			if ( vertices.get(i).greaterThan((q.vertices.get(i))) ){
				break;
			}
			i++;
		}
		
		return lessThan;
		
	}
	
	//sometimes we need to copy a Polyomino, but this is expensive and should be used carefully
	Polyomino copy() {
		ArrayList<Square> vertices_copy= new ArrayList<Square>();
		for (Square s: vertices) {
			vertices_copy.add(s.clone());
		}
		if (redelmeier) return new Polyomino(vertices_copy,"R");
		return new Polyomino(vertices_copy);	
	}
	
	//orders vertices according to total order defined on Square objects
	void order_vertices() {
		Collections.sort(vertices);
	}

	void translate(int x, int y) {
		for (Square p: vertices) {
			p.x+=x;
			p.y+=y;
		}
	}
	
	//function necessary for my naive method of generating polyominos, putting (x_min,y_min) at the origin
	void translate_to_origin() {
		
		int x_min=Integer.MAX_VALUE;
		int y_min=Integer.MAX_VALUE;
		
		for (Square s: vertices) {
			
			if (s.x<x_min) x_min=s.x;		
			if (s.y<y_min) y_min=s.y;
		
		}
		
		translate(-x_min,-y_min);
	}

	
	//rotation 90 degrees anti-clockwise (x,y)->(-y,x), and put back into canonical form
	void rotate() {
		for (Square p: vertices) {
			int temp=p.x;
			p.x=-p.y;
			p.y=temp;
		}
		order_vertices();
		translate_to_origin();
	}

	
	//reflection across x-axis, and put in canonical form
	void reflect_x() {
		for (Square p: vertices) {
			p.x=-p.x;
		}
		order_vertices();
		translate_to_origin();
	}
	
	
	//reflection across y -axis, and put in canonical form
	void reflect_y() {
		for (Square p: vertices) {
			p.y=-p.y;
		}	
		order_vertices();
		translate_to_origin();
	}

	//scale polyomino by a factor k
	//not necessary to translate to origin: if (x_min,y_min) is initially at the origin, it is after dilation too
	void dilate(int k) {//for integers k>=1
		
		ArrayList<Square> vertices_new = new ArrayList<Square>();
		for (Square p: vertices) {
			for (int i=0;i<k;i++) {
				for (int j=0;j<k;j++) {
					vertices_new.add(new Square(k*p.x+i,k*p.y+j));
				}
			}
		}

		vertices=vertices_new;
		area=vertices_new.size();
		order_vertices();
	}
	
	//returns list of x and y reflections of polyomino (2 elements)
	ArrayList<Polyomino> reflections (){
		ArrayList<Polyomino> reflections = new ArrayList<Polyomino>();		
		
		this.reflect_x();
		reflections.add(this.copy());
		this.reflect_x();
		
		this.reflect_y();
		reflections.add(this.copy());
		this.reflect_y();
											
		return reflections;
	}
	
	//returns list of 4 rotations of polyomino
	ArrayList<Polyomino> rotations (){
		ArrayList<Polyomino> rotations = new ArrayList<Polyomino>();		
	
		for (int k=0;k<4;k++) {//rotations
			this.rotate();
			rotations.add(this.copy());
		}										
		return rotations;
	}
	
	Polyomino minimal_rotation(){//calculates minimal element of set of symmetries of polyomino
		ArrayList<Polyomino> rotations = this.rotations();
		Polyomino min_rotation = rotations.get(0);
		for (int i=1; i<rotations.size();i++) {
			Polyomino R=rotations.get(i);
			if (R.lessThan(min_rotation) ) min_rotation = R;
		}
		return min_rotation;
	}
	
	
	
	//returns list of 8 symmetries of polyomino (rotations, reflections, and their compositions)
	ArrayList<Polyomino> symmetries (){
		ArrayList<Polyomino> symmetries = new ArrayList<Polyomino>();
		
		for (int j=0;j<2;j++) {//reflections_x (we don't also need to do reflections_y, as we would be double counting with rotations by pi	
			this.reflect_x();
			for (int k=0;k<4;k++) {//rotations
				this.rotate();
				
				symmetries.add(this.copy());
			}									
		}
		return symmetries;
	}
	
	Polyomino minimal_symmetry(){//calculates minimal element of set of symmetries of polyomino
		ArrayList<Polyomino> symmetries = this.symmetries();
		Polyomino min_symmetry = symmetries.get(0);
		for (int i=1; i<symmetries.size();i++) {
			Polyomino S=symmetries.get(i);
			if (S.lessThan(min_symmetry) ) min_symmetry = S;
		}
		return min_symmetry;
	}
	
	
	//returns list of distinct symmetries of polyomino
	ArrayList<Polyomino> distinct_symmetries(){
		ArrayList<Polyomino> distinct_symmetries= new ArrayList<Polyomino>();
		
		for (Polyomino Q: this.symmetries()) { 
			boolean already_present=false;
			
			for (int i=0; i< distinct_symmetries.size();i++) {
				if (Q.equals(distinct_symmetries.get(i))) {
					already_present=true;
					break;
				}
			}
			if (!already_present) distinct_symmetries.add(Q);

		}
		
		return distinct_symmetries;

	}
	
	//return width, height of P
	int [] get_dimmensions() {
		int x_max = -Integer.MAX_VALUE;
		int y_max= -Integer.MAX_VALUE;
		for (Square s: vertices) {		
			if (s.x>x_max) x_max=s.x;	
			if (s.y>y_max) y_max=s.y;
		}
		return new int[] {x_max,y_max};
	}
	
	
	void print_vertices() {
		System.out.print('[');
		for (int i=0;i<vertices.size();i++) {
			System.out.print("("+vertices.get(i).x +","+ vertices.get(i).y+") ");
		}
		System.out.print(']');
	}
	
	//adds polyomino to an image by adding each square individually 
	void add_to(Image2d img, boolean borders) {
		for (Square p: vertices) {
			int x1=p.x;
			int y1=p.y;
			
			int[] xcoords= {x1,x1+1,x1+1,x1};
			int[] ycoords= {y1,y1,y1+1,y1+1};
		    img.addPolygon(xcoords,ycoords, color);
		    
			if (borders) {//this is only necessary to display polyominos which touch, e.g for tilings
				//we just need to check if the square to the left and below are in the polyomino; if not, add border
				if (!this.contains(new Square(x1-1,y1))) img.addEdge(x1, y1, x1, y1+1, 2);
				if (!this.contains(new Square(x1,y1-1))) img.addEdge(x1, y1, x1+1, y1, 2);
			}

		}
	}
	 
	//extract list of polyominos from file
	public static ArrayList<Polyomino> get_polyominos(File file) {
		
		ArrayList<Polyomino> polyominos= new ArrayList<Polyomino>();
		Scanner fScn = null;
		try {
			fScn = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(fScn.hasNextLine()){  
		    String s = fScn.nextLine();
		    polyominos.add(new Polyomino(s));
		}
		
		return polyominos;
	}

}
