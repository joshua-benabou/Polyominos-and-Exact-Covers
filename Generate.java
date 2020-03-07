import java.util.ArrayList;

public class Generate {
	
	//recursive function returning ArrayList of polyominos of given area and type
	public static ArrayList<Polyomino> polyominos(int area, String type) {
		
		if (!(type.equals("fixed")||type.equals("onesided")||type.equals("free"))){
			throw new IllegalArgumentException("Invalid polyomino type");
		}
		
		ArrayList<Polyomino> list_new= new ArrayList<Polyomino>();//the list we're going to return
		
		if (area==1) { //base case 
			ArrayList<Square> vertices=new ArrayList<Square>();
			vertices.add(new Square(0,0));
			list_new.add(new Polyomino(vertices));
		}

		
		if (area>1) {
			ArrayList<Polyomino> list_old=polyominos(area-1,type);
			
			for (int i=0 ;i<list_old.size();i++) {//iterate over polyominos of size (area-1)
				Polyomino poly=list_old.get(i);
				
				//consider the smallest rectangle R containing P, and add extend R by one unit in each of the 4 directions U,D,L,R to obtain R'
				//for squares in R' we store in a matrix the boolean (A is in P)
				//only the truth values for the squares of P and it's immediate neighbors will be looked up in this matrix, but this isn't an issue
				//we create this method to avoid iterating through P.vertices each time to check whether a candidate square is in P 
				//this reduces the lookup time from O(n) to O(1)
				
				int [] dim=poly.get_dimmensions();
				boolean [][] is_in_P=new boolean [dim[0]+3][dim[1]+3];
				for (Square s: poly.vertices) is_in_P[s.x+1][s.y+1]=true;
				
		  
				for (Square p: poly.vertices) {//iterate over squares in a polyomino
					ArrayList<Square >adjacent_squares= new ArrayList<Square>();
					adjacent_squares.add(new Square(p.x,p.y+1));
					adjacent_squares.add(new Square(p.x+1,p.y));
					adjacent_squares.add(new Square(p.x,p.y-1));
					adjacent_squares.add(new Square(p.x-1,p.y));
					
					for (Square square: adjacent_squares) {
						if (!is_in_P[square.x+1][square.y+1]) {//identify a candidate square to add amongst neighbors

							//we insert the new square in the right position so the new polyomino remains sorted
							
							//---------------------clone poly.vertices----------------
							ArrayList<Square> new_squares=new ArrayList<Square>();
							for (Square q: poly.vertices) {
									new_squares.add(q.clone());
							}
							//------------------------------------------------------
							
							int ind=poly.vertices.size();
							for (int j=0; j< poly.vertices.size();j++) {
								if (square.lessThan(poly.vertices.get(j))) {
									ind=j;
									break;
								}
							}
																
							new_squares.add(ind,square);
							
							Polyomino new_poly = new Polyomino(new_squares);
							
							//we now check if our new polyomino has already been generated; if not, add it to our list
							boolean already_generated=false;
							
							ArrayList<Polyomino> rotations=new ArrayList<Polyomino>();
							ArrayList<Polyomino> symmetries=new ArrayList<Polyomino>();
							
							if (type=="onesided") rotations=new_poly.rotations();
							if (type=="free") symmetries=new_poly.symmetries();
						
							
							for (Polyomino q: list_new) {
								
								if (type=="fixed") {
									if (q.equals(new_poly)) {
										already_generated=true;
										break;
									}
								}

								if (type=="onesided") {										
									for (Polyomino s: rotations) {
										if (q.equals(s)) {
											already_generated=true;
											break;
										}
									}
									if (already_generated) break;					
								}
								
								
								if (type=="free") {																			
									for (Polyomino s: symmetries) {
										if (q.equals(s)) {
											already_generated=true;
											break;
										}
									}
									if (already_generated) break;					
								}										
							}
													
							if (!already_generated) list_new.add(new_poly);		
						}
					}										
				}
			}		
		}	
		return list_new;
	}
}


