
import java.util.*;

public class Exact_cover {
	
	//solves the exact cover problem for X={1,....,n} and C a collection of subsets of X
	public static Set<Set<Set<Integer>>> naive_backtracking(Set<Integer> X, Set<Set<Integer>> C) {
		Set<Set<Set<Integer>>> exact_covers= new HashSet<Set<Set<Integer>>>();
		if (X.isEmpty()) {
			Set<Set<Integer>> empty=new HashSet<Set<Integer>>();
			exact_covers.add(empty);
			return exact_covers;
		}
		
		//int x=X.iterator().next();//select any element of X
		
		//----------------------------------------------------------------
		//smarter way of choosing x so that number of S in C containing x is minimal:
		
		//create hashmap storing, for each x, the number of sets S of C which contain x
		HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
		for (Integer x: X) counts.put(x, 0);
		
		for (Set<Integer> S: C) {
			for (Integer x: S) {
				counts.put(x, counts.get(x)+1);
			}
		}
		//find x for which counts is minimal
		int x_optimal=0;
		int count_min=Integer.MAX_VALUE;
		for (int x: X) {
			if (counts.get(x)<count_min) {
				x_optimal=x;
				count_min=counts.get(x);
			}
		}
		//----------------------------------------------------------------
		
		for (Set<Integer> S: C){
			if (S.contains(x_optimal)) {
				
				Set<Integer> Xstar= new HashSet<Integer>(X);
				Set<Set<Integer>> Cstar= new HashSet<Set<Integer>>(C);
				
				for (Integer y: S) {
					Xstar.remove(y);
					for (Set<Integer> T: C) {
						if (T.contains(y)) {
							Cstar.remove(T);
						}
					}
				}


				for (Set<Set<Integer>> P: naive_backtracking(Xstar,Cstar)) {
					P.add(S);
					exact_covers.add(P);
				}
				
			}
		}
		return exact_covers;
	}
	
    //converts from set (X,C) representation to matrix representation of an exact-cover problem
    public static int[][] sets_to_matrix(Set<Integer> X,Set<Set<Integer>> C) { 
    	int[][] M= new int[C.size()][X.size()];
    	int r=0;
    	for (Set<Integer> S: C) {
    		int c=0;
    		for (Integer e: X) {
    			if (S.contains(e)){
    				M[r][c]=1;
    			}
    			c++;
    		}
    		r++;
    	}
    	return M;
    }
	
	static void print_exact_covers(Set<Set<Set<Integer>>> ec) {
		if (ec.isEmpty()){
			System.out.println("Empty");
		}
		else {
			for (Set<Set<Integer>> P: ec) {
				System.out.println();
				System.out.print("[");
				for (Set<Integer> S: P) {
					System.out.print("{");
					for (Integer e: S) {
						System.out.print(e+",");
					}
					System.out.print("},");
				}
				System.out.print("]");

			}
		}
	}
	
    // get all subsets of given set[] 
    static Set<Set<Integer>> allSubsets(int n) //generates set of all subsets of [1,n]
    { 
		Set<Set<Integer>> C= new HashSet<Set<Integer>>();
  
        for (int i = 0; i < (1<<n); i++) 
        { 	
    		Set<Integer> S= new HashSet<Integer>();
  
            for (int j = 0; j < n; j++) 
  
                // (1<<j) is a number with jth bit 1 
                // applying 'and' to the subset index gives which indices are present
                if ((i & (1 << j)) > 0) 
                    S.add(j+1);
  
            C.add(S);
        } 
        return C;
    } 
    
//----------------------------------------------------------------------------------------------------------	
                             //*****************MAIN METHOD*********************
//----------------------------------------------------------------------------------------------------------
	public static void main(String[] args) {
		
		//------------------EXACT COVER PROBLEM 0: GIVEN IN PROJECT DESCRIPTION------------------------
		Integer arrX[]={1,2,3,4,5,6,7};
		Set<Integer> X= new HashSet<Integer>(Arrays.asList(arrX));
	
		Set<Set<Integer>> C= new HashSet<Set<Integer>>();
		Integer arr[]={3,5,6};
		C.add(new HashSet<Integer>(Arrays.asList(arr)));
		Integer arr1[]={1, 4, 7};
		C.add(new HashSet<Integer>(Arrays.asList(arr1)));
		Integer arr2[]={2, 3, 6};
		C.add(new HashSet<Integer>(Arrays.asList(arr2)));
		Integer arr3[]={1, 4};
		C.add(new HashSet<Integer>(Arrays.asList(arr3)));
		Integer arr4[]={2, 7};
		C.add(new HashSet<Integer>(Arrays.asList(arr4)));
		Integer arr5[]={4, 5, 7};
		C.add(new HashSet<Integer>(Arrays.asList(arr5)));

		//----------------EXACT COVER PROBLEM 1: PARTITIONS OF [1,N]-----------------------------------------------
		//Using X=[1,n], C=P(X)
		int n=5;
		Integer arrX1[]=new Integer [(int) n];
		for (int i=1;i<=n;i++) {
			arrX1[i-1]=i;
		}
		Set<Integer> X1= new HashSet<Integer>(Arrays.asList(arrX1));
		Set<Set<Integer>> C1= allSubsets(n);
		//------------------------EXACT COVER PROBLEM 1: PARTITIONS OF [1,N] WITH EQUALLY SIZED SUBSETS-----------------------	
		//Using X=[1,n], C={S in P(X), |S|=k}
		int k= 3;
		Set<Set<Integer>> C2= new HashSet<Set<Integer>>();
		for (Set<Integer> S: allSubsets(n)) {
			if (S.size()==k) {
				C2.add(S);
			}
		}
		
		//--------------------------------------------------------------
		//Problem 0 : (X,C)
		//Problem 1: (X1,C1)
		//Problem 2: (X1,C2)
		
		
		DancingLinks dl = new DancingLinks(sets_to_matrix(X1,C1));
		

		long startTime = System.nanoTime();	
		Set<Set<data_object>> exact_covers_dl=dl.exactCover(dl.master_header);
		long elapsedTime = System.nanoTime() - startTime;
		System.out.println("\nDLX (ms): "+elapsedTime/Math.pow(10, 6));
		System.out.println("Number of exact_covers: "+exact_covers_dl.size());
	

		startTime = System.nanoTime();
		Set<Set<Set<Integer>>> exact_covers_naive=naive_backtracking(X1,C1);
		elapsedTime = System.nanoTime() - startTime;
		
		System.out.println("\nNaive backtracking (ms): "+elapsedTime/Math.pow(10, 6));
		System.out.println("Naive gives the same result: "+ exact_covers_naive.size());
		
		
		Set<Set<Set<Integer>>> exact_covers=new HashSet<Set<Set<Integer>>>();
		for (Set<data_object> cover_data_objects: exact_covers_dl) {
			Set<Set<Integer>> cover_sets=new HashSet<Set<Integer>>();
			for (data_object t: cover_data_objects) cover_sets.add(dl.set_of_row.get(t.row_id));
			exact_covers.add(cover_sets);
		}
		System.out.println("\nExact covers: ");
		print_exact_covers(exact_covers);

	}

}
