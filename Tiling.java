import java.awt.Color;
import java.util.*;

public class Tiling {


	
	//returns all tilings of a Polyomino P by polyominos from polyominos_list
	//possibility of using all tiles exactly once and allowing rotations and/or relfections
	public static Set<Set<Polyomino>> tilings(ArrayList<Polyomino> polyominos_list,Polyomino P, boolean use_all_once, boolean rotations, boolean reflections) {
		
		HashMap<Integer, Square> hmap_P = new HashMap<Integer, Square>();
		Set<Integer> X= new HashSet<Integer>();
		Set<Set<Integer>> C= new HashSet<Set<Integer>>();	
		Set<Set<Polyomino>> tilings = new HashSet<Set<Polyomino>>();
		
		if (use_all_once) {//quick check to see if there is a tiling of P using each tile exactly once: ( sum of area(tile) ) == area(P)
			int total_area=0;
			for (Polyomino q: polyominos_list) total_area+=q.area;
			if (!(total_area==P.area)) return tilings;
		}
					
		//index squares of P
		for (int i=0; i<P.vertices.size();i++) {
			hmap_P.put(i+1,P.vertices.get(i));
			X.add(i+1);
		}
		if (use_all_once) {//we add extra elements to the ground set to ensure each polyomino is used exactly once
			for (int j=0;j<polyominos_list.size();j++) {
				X.add(P.vertices.size()+j+1);
			}
		}
			
		//we now define C
		//if A= set of (fixed/one-sided/free) polyominos of area n
		//C= union over Q in A of union of S subset of X corresponding to indices of squares of P covered by some translate of Q
		//to generate C, we iterate over the polyominos Q in A and we see, for each possible translation of Q,
		//if Q fits in P, in which case we record the indices of the squares of P it covers
		
		for (int k=0; k<polyominos_list.size();k++) {
			Polyomino tile=polyominos_list.get(k);
			
			ArrayList<Polyomino> orientations_of_tile=new ArrayList<Polyomino>();
			if (rotations && reflections) orientations_of_tile=tile.distinct_symmetries();
			else if (rotations) orientations_of_tile=tile.rotations();
			else if (reflections) orientations_of_tile=tile.reflections();
			else orientations_of_tile.add(tile);//if no rotations, the only possible orientation is the tile as it was given
		
			for (Polyomino Q: orientations_of_tile) {
				//for each VALID translation of Q, we calculate the indices of squares of P which Q occupies, and add this to C
				//we choose some square of Q, which we will "nail" to the squares of P and see if Q fits in in P in that position
				Square nail = Q.vertices.get(0);
				for (Square s: P.vertices) {
				    Set<Integer> indices_Q_translated= new HashSet<Integer>();
				    boolean Q_fits_inP=true;
					for (Square q: Q.vertices) {//lets check if this translation of Q fits in P
						Square translated_q=new Square (q.x+s
								.x-nail.x,q.y+s.y-nail.y);
						if (!P.contains(translated_q)) {//check if square translated_q fits is in P
							Q_fits_inP=false;
							break;
						}
						
						hmap_P.forEach((key, value) -> {//get index of square translated_q
						    if (value.equals(translated_q)) {
						    	indices_Q_translated.add(key);
						    
						    }
						});
						
					}
					if (Q_fits_inP) {
						//we add an element which corresponds to putting a one in a dummy column in the exact cover matrix
						if (use_all_once) indices_Q_translated.add(k+P.vertices.size()+1); 
						C.add(indices_Q_translated);
						
					}
				}
			}
		}
		//Initialization step complete, with complexity at least |polyominos_list|*4*area(P)*area(Q)*area(P)
		
		if(!(C.size()==0)) {
			
			int[][] M=Exact_cover.sets_to_matrix(X,C);
		
			//we add n columns (initialized to all zeros) to M where n=polyominos_list.size().
			//Each row of M corresponds to some translation of a polyomino Pk in polyominos_list-{P1,...,Pn}
			//in a such a row we place a 1 in the column k
			//any exact cover of M must then use each Pk exactly once 

			DancingLinks dl = new DancingLinks(M);
			Set<Set<data_object>> exact_covers_data_objects=dl.exactCover(dl.master_header);
			
			for (Set<data_object> cover_data_objects: exact_covers_data_objects) {
				
				Set<Set<Integer>> cover_sets=new HashSet<Set<Integer>>();
				for (data_object t: cover_data_objects) cover_sets.add(dl.set_of_row.get(t.row_id));
				
				Set<Polyomino> T = new HashSet<Polyomino>();//a tiling, i.e a set of polyominos
				
				for (Set<Integer> indices: cover_sets) {
					ArrayList<Square> vertices= new ArrayList<Square>();
					//convert indices to corresponding squares of P, 
					//and add the corresponding Polyomino R to T
					
					for (int index: indices) {
						if (index<P.vertices.size()+1) {//if not a dummy index (in the case of use_all_once polyominos)
							vertices.add(hmap_P.get(index));
						}
					}
					
					T.add(new Polyomino(vertices,"R"));
					
				}
				tilings.add(T);
			}
		}

		return tilings;
	}
	

	//returns, for each n-polyomino P which tiles its k-dilate, the set of such tilings
	public static Set<Set<Set<Polyomino>>> tile_dilate(int n,int k, boolean use_all_once) {
	
		Set<Set<Set<Polyomino>>> tilings_of_dilate = new HashSet<Set<Set<Polyomino>>>();	
		ArrayList<Polyomino> polyominos_list=Generate.polyominos(n,"free");
			
		for (int p=0; p<polyominos_list.size();p++) {
				
			Polyomino P=polyominos_list.get(p);
			Polyomino dilate_P=polyominos_list.get(p).copy();
			dilate_P.dilate(k);
			
			//*****it's not obvious if calculating distinct_symmetries will save time
			//it will reduce the number of tile combinations but increase time to calculate symmetries
			//however calculation of symmetries is done in constant time (we check for duplicates in a list of 8 elements)			
			
			Set<Set<Polyomino>> T=tilings(P.distinct_symmetries(),dilate_P,use_all_once,false,false);

			
			if (!T.isEmpty()){
				tilings_of_dilate.add(T);
			}	
		}
		
		return tilings_of_dilate;
	}
	
	public static void display_tiling(Set<Polyomino> tiling){
		
		int area=0;
		for (Polyomino p: tiling) area+=p.area;
		Image2d img = new Image2d(600,400);
		int scale_factor=Math.max(1,(int) Math.sqrt(600*400/(Math.pow(img.scaling,2)*area))/3);//scale factor to ensure the tiling fits in the windows
		int k=0;
		for (Polyomino q: tiling) {
			Polyomino t=q.copy();
			t.color= Color.getHSBColor((float) k / tiling.size(), 1, 1);
			t.dilate(scale_factor);
			t.add_to(img,true);
			k++;
		}
		
		Image2dViewer frame = new Image2dViewer(img);//display
		
	}
		
	
	public static Polyomino rectangle(int a,int b) {
		ArrayList<Square> vertices= new ArrayList<Square>();

		for (int i=0; i<a;i++) {
			for (int j=0;j<b;j++) {
				vertices.add(new Square(i,j));
			}
		}

		return new Polyomino(vertices);		
	}
	
	public static Polyomino triangle() {
		ArrayList<Square> vertices= new ArrayList<Square>();
		int L=9;
		
		for (int i=0; i<= L/2+1;i++) {
			for (int j=i;j<=L-i;j++) {
				vertices.add(new Square(j,2*i));
				vertices.add(new Square(j,2*i+1));
			}
		}

		return new Polyomino(vertices);		
	}

	public static Polyomino diamond() {
		int L=9;
		
		ArrayList<Square> vertices= new ArrayList<Square>();
		
		for (int i=-5; i<=5;i++) {
			for (int j=Math.abs(i);j<=L-Math.abs(i);j++) {
				vertices.add(new Square(j,i));
				if (i<=0) {
					vertices.add(new Square(j,i-1));
				}
			}
		}
			
		return new Polyomino(vertices);
	}

	
	public static Polyomino stairs() {
		int L=9;
		
		ArrayList<Square> vertices= new ArrayList<Square>();
		
		for (int i=0; i<=5;i++) {
			int j=-i;
			while (j>=-i-L/2) {
				vertices.add(new Square(2*i,j));
				if (i<5) vertices.add(new Square(2*i+1,j));
				j--;
			}
			if (i<5) vertices.add(new Square(2*i+1,j));
		}
	                       
		return new Polyomino(vertices);		
	}
//----------------------------------------------------------------------------------------------------------	
						         //*****************MAIN METHOD*********************
//----------------------------------------------------------------------------------------------------------
	public static void main(String[] args) {
		
		//-------------------------PENTAMINO TILINGS OF THE GIVEN FIGURES-------------------------------------------
		//takes about 3 s
		String type="free";
		boolean use_all_once=true;
		boolean rotations=true;
		boolean reflections=true;
			
		ArrayList<Polyomino> pentaminos= Redelmeier.polyominos(5,type);

		System.out.println("# of " +type+" pentaminos:"+pentaminos.size());
		long startTime = System.nanoTime();
		
		Set<Set<Polyomino>> pentamino_tilings= tilings(pentaminos,triangle(),use_all_once,rotations,reflections);
		
		long elapsedTime = System.nanoTime() - startTime;  
		
		
		System.out.println("\n#" +type +" pentamino tilings of figure: "+ pentamino_tilings.size());
		System.out.println("Execution time(s): "+elapsedTime/Math.pow(10, 9));
		if (!pentamino_tilings.isEmpty()) {
			System.out.println("Example tiling is displayed");
			Set<Polyomino> example_tiling=pentamino_tilings.iterator().next();//choose some tiling from the solution set
			display_tiling(example_tiling);
		}
		
		//----------------------------------RECTANGLE TILINGS ----------------------------------------------
		//tile a x b rectangle with polyominos of area n
		int a=5;
		int b=12;
		int n=5;
		type="free";
		use_all_once=true;
		rotations=true;
		reflections=true;

		startTime = System.nanoTime();
		ArrayList<Polyomino> polyominos_list=Redelmeier.polyominos(n,type);
		
		Set<Set<Polyomino>> rectangle_tilings= tilings(polyominos_list,rectangle(a,b),use_all_once,rotations,reflections);
		 
		elapsedTime = System.nanoTime() - startTime;
		System.out.println("\n# of "+type +" polyominos of area "+ n+": "+polyominos_list.size());
		System.out.println("\n#tilings of "+a +"x" + b+" rectangle by " + 
		type + " " +n+"-polyominos: " + rectangle_tilings.size());
		System.out.println("Execution time(s): "+elapsedTime/Math.pow(10, 9));
		if (!rectangle_tilings.isEmpty()) {
			System.out.println("Example tiling is displayed");
			Set<Polyomino> example_tiling=rectangle_tilings.iterator().next();//choose some tiling from the solution set
			display_tiling(example_tiling);
	
	
		}
	
		//---------------------TILINGS BY A POLYOMINO OF ITS DILATE---------------------------------------------------
		//(n,k)=(8,4) takes about 40 s
		use_all_once=false;
		int m=8;
		int k=4;

		startTime = System.nanoTime();
		
		Set<Set<Set<Polyomino>>> tilings_of_dilate= tile_dilate(m,k,use_all_once);
			 
		elapsedTime = System.nanoTime() - startTime;
		
		System.out.println("\n# of "+m+"-polyominos tiling their "+k+"-dilate: "+tilings_of_dilate.size());
		System.out.println("Execution time(s): "+elapsedTime/Math.pow(10, 9));
		if (!tilings_of_dilate.isEmpty()) {
			System.out.println("Example tiling is displayed");
			Set<Set<Polyomino>> tilings=tilings_of_dilate.iterator().next();
			Set<Polyomino> example_tiling=tilings.iterator().next();
			display_tiling(example_tiling);
			
		}
	
		
	}
}
