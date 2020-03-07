import java.util.ArrayList;
import java.util.LinkedList;



public class Redelmeier {

			
	public static ArrayList<Polyomino> polyominos_upto(Polyomino P_initial, LinkedList<Square> unvisited_squares, int area, String type){
		
		ArrayList<Polyomino> list_polys= new ArrayList<Polyomino>();
		
		//a polyomino of area A in Redelmeier-canonical form of occupies cells in [0,A-1]x[-(A-1),A-1]
		//the matrix "free" stores which squares in [-1,A-1]x[-(A-1),A-1] are free
		//free[s]=(s.x>0||(s.x==0 && s.y>0)&&!(not in P)&&(not adjacent to P)
		//so leftmost bottom cell is at the origin i.e x_min=0 and min ( y s.t (0,y) in P) =0
		//square [i,j] corresponds to free[i+1][j+(area-1)]
		boolean[][] free=new boolean[area+1][2*area-1];
		
		for (int i=0;i<area+1;i++) {//intialize matrix to all true values
			for (int j=0;j<2*area-1;j++) {
				free[i][j]=true;
			}
		}
		
		//if x=-1 or (x=0 && y<0) the square (x,y) is BORDER, so not free
		for(int j=0;j<2*area-1;j++) {
			free[0][j]=false;
		}
		for(int j=0;j<area-1;j++) {
			free[1][j]=false;
		}

			
		while (!unvisited_squares.isEmpty()) {
						
			ArrayList<Square> new_vertices=new ArrayList<Square>();
			
			for (Square q: P_initial.vertices) {//mark squares of P and squares adjacent to P as not free
					new_vertices.add(q.clone());//it is necessary to clone q to ensure distinct object references
					free[q.x+1][q.y+(area-1)]=false;//squares OCCUPIED by polyomino are not free
					
					for (int i=-1;i<=0;i++) {//this is a quick way of iterating through D,U,R,L
						free[(q.x+i)+1][(q.y+i+1)+(area-1)]=false;
						free[(q.x+i+1)+1][(q.y+i)+(area-1)]=false; 
					}
				
			}
			
			Square ts =unvisited_squares.removeFirst();//trial_square
			new_vertices.add(ts);
			
			Polyomino new_poly=new Polyomino(new_vertices,"Redelmeier");
			list_polys.add(new_poly);

			
			if (new_vertices.size()<area) {
				
				//the new neighbors are the adjacent squares of the trial square, if they are in the feasible region
				/*due to Redelmeier's canonical representation of polyominos fixing the leftmost square 
				 * of the bottom row of a polyomino at the origin, the feasible region for new squares is 
				 * y>0 union (y=0 and x>0)
				*/
				int new_neighbors=0;
				
				//define new_neighbors and add them to the list of unvisited squares
				for (int i=-1;i<=0;i++) {//we iterate through D,U,R,L this way to avoid having to store the vectors pointing to adjacent squares
					Square s=new Square(ts.x+i,ts.y+i+1);
					if (free[s.x+1][s.y+(area-1)]) {
						new_neighbors+=1;
						unvisited_squares.addFirst(s);
					}	
					Square t=new Square(ts.x+i+1,ts.y+i);
					if (free[t.x+1][t.y+(area-1)]) {
						new_neighbors+=1;
						unvisited_squares.addFirst(t);
					}	
				}

				//we copy unvisited_squares 
				LinkedList<Square> unvisited_squares_copy=new LinkedList<Square>();
				for (Square q: unvisited_squares) {
					unvisited_squares_copy.add(q.clone());
				}
			
				ArrayList<Polyomino> more_polys=polyominos_upto(new_poly,unvisited_squares_copy,area,type);
				
				
				for (Polyomino P:more_polys) {//join to list_polys
					if (type=="fixed") list_polys.add(P);

					if (type=="free") {//we'll only add P to the set of free polyominos if it is the minimal member of its symmetry group
						if ( P.equals(P.minimal_symmetry()) ) list_polys.add(P);
					}
					
					if (type=="onesided") {
						if ( P.equals(P.minimal_rotation()) ) list_polys.add(P);
					}

				}
			
							
				for (int i=0;i<new_neighbors;i++) {// remove the newly added squares from the unvisited set
					unvisited_squares.removeFirst();
				}
			}
			
				
		}
		
		return list_polys;
	}
	
	//generates polyominos of a given area and type
	public static ArrayList<Polyomino> polyominos(int area, String type){
		
		if (!(type.equals("fixed")||type.equals("onesided")||type.equals("free"))){
			throw new IllegalArgumentException("Invalid polyomino type");
		}

		ArrayList<Polyomino> polys_areaN= new ArrayList<Polyomino>();
		ArrayList<Square> empty= new ArrayList<Square>();
		LinkedList<Square> unvisited=new LinkedList<Square>();
		unvisited.add(new Square(0,0));
		Polyomino seed= new Polyomino(empty,"Redelmeier");
		ArrayList<Polyomino> polys_all=polyominos_upto(seed,unvisited,area,type);//all fixed polyominos of size<=area
		for (Polyomino P: polys_all) {
			if (P.area==area) polys_areaN.add(P);
		}
		
		return polys_areaN; 
	}
	

//----------------------------------------------------------------------------------------------------------	
                             //*****************MAIN METHOD*********************
//----------------------------------------------------------------------------------------------------------
	
	public static void main(String[] args){
		LinkedList<Square> unvisited=new LinkedList<Square>();
		unvisited.add(new Square(0,0));
		
		int n=8;
		//------------------------------GENERATE FIXED POLYOMINOS WITH NAIVE AND REDELEMEIER-----------------------------------------------
		String type="fixed";
		
		long startTime = System.nanoTime();	
		ArrayList<Polyomino> polys_fixed=polyominos(n,type);
		long elapsedTime = System.nanoTime() - startTime;


		System.out.println("#fixed_polyominos of area "+n+": "+polys_fixed.size());
		System.out.println("Execution time, Redelmeier (ms): "+elapsedTime/Math.pow(10, 6));
		
		startTime = System.nanoTime();
		ArrayList<Polyomino> polys_naive_fixed=Generate.polyominos(n,type);//polyominos of size n
		elapsedTime = System.nanoTime() - startTime;
		System.out.println("Naive gives the same result:" + polys_naive_fixed.size());
		System.out.println("Execution time, Naive (ms): "+elapsedTime/Math.pow(10, 6));
		
		System.out.println();
		//-----------------------GENERATE FREE POLYOMINOS WITH NAIVE AND REDELEMEIER------------------------------------------------------
		type="free";
		
		startTime = System.nanoTime();	
		ArrayList<Polyomino> polys=polyominos(n,type);
		elapsedTime = System.nanoTime() - startTime;

		System.out.println("#free_polyominos of area "+n+": "+polys.size());
		System.out.println("Execution time, Redelmeier (ms): "+elapsedTime/Math.pow(10, 6));
		
		startTime = System.nanoTime();//
		ArrayList<Polyomino> polys_naive_free=Generate.polyominos(n,type);//polyominos of size n
		elapsedTime = System.nanoTime() - startTime;
		System.out.println("Naive gives the same result:" + polys_naive_free.size());
		System.out.println("Execution time, Naive (ms): "+elapsedTime/Math.pow(10, 6));

		
		System.out.println();

		//------------------------------GENERATE FIXED POLYOMINOS WITH NAIVE AND REDELEMEIER-----------------------------------------------
		type="onesided";
		
		startTime = System.nanoTime();	
		ArrayList<Polyomino> polys_onesided=polyominos(n,type);
		elapsedTime = System.nanoTime() - startTime;


		System.out.println("#onesided polyominos of area "+n+": "+polys_onesided.size());
		System.out.println("Execution time, Redelmeier (ms): "+elapsedTime/Math.pow(10, 6));
		
		startTime = System.nanoTime();//
		ArrayList<Polyomino> polys_naive_onesided=Generate.polyominos(n,type);//polyominos of size n
		elapsedTime = System.nanoTime() - startTime;
		System.out.println("Naive gives the same result:" + polys_naive_onesided.size());
		System.out.println("Execution time, Naive (ms): "+elapsedTime/Math.pow(10, 6));
	
	}

}