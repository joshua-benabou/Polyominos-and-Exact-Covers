import java.util.*;
import java.lang.Math;

public class Sudoku {
	
	final int[][] sudoku_matrix;
	int nb_filled;
	ArrayList<int[][]> solutions;

	
	public Sudoku(int[][] M) {
		sudoku_matrix=M;
		if (!(M.length==9&M[0].length==9)){//check if input matrix dimensions are correct
			throw new IllegalArgumentException("Matrix must be 9x9");
		}

	}
	
	public void display_solutions() {//can only be called if solutions has been defined
		
		System.out.println("Sudoku problem ("+nb_filled+" clues): \n");
	
		for (int i=0;i<9;i++) {
			for (int j=0;j<9;j++) {
				if (sudoku_matrix[i][j]==0) System.out.print("- ");
				else System.out.print(sudoku_matrix[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println(solutions.size()+" solutions:\n");
		for (int[][] sol: solutions) {
			for (int i=0;i<9;i++) {
				for (int j=0;j<9;j++) {
					System.out.print(sol[i][j]+" ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	//to understand how this works, go to bottom of file
	public ArrayList<int[][]> solve() {
		nb_filled=0;
		for (int i=0; i<9;i++) {
			for (int j=0; j<9;j++) {
				if (!(sudoku_matrix[i][j]==0)) nb_filled++;
			}
		}
	
		int nb_rows=nb_filled+9*(81-nb_filled);
		int[][] exact_cover_matrix=new int[nb_rows][324];
		int[][] triplets=new int[nb_rows][3];//matrix storing triples (i,j,n) for each row of exact_cover_matrix
		boolean[] filled_in=new boolean[nb_rows];//stores indices of rows corresponding to filled in squares
		solutions = new ArrayList<int[][]>();
		//---------------------------------------------------------------------
		//We first build the exact_cover_matrix, and record the relation between each row and the triplet (i,j,n) associated
		
		int r=0;//index of row of exact_cover_matrix being defined
		
		for (int i=0; i<9;i++) {
			for (int j=0; j<9;j++) {
				int ss=(int) ( 3*Math.floor(j/3)+Math.floor(i/3) );
				
				if (sudoku_matrix[i][j]==0) {//if square not already filled in (i.e not a clue)
					for (int n=1;n<=9;n++) {//define the 9 rows r,...,r+8 of exact_cover_matrix corresponding to the triplet (i,j,n) 
						for (int l:Arrays.asList(index(i,j),81+index(i,n-1),2*81+index(j,n-1),3*81+index(ss,n-1))) {// we place 1 at four positions
							exact_cover_matrix[r][l]=1;
						}
						triplets[r]= new int[]{i,j,n};
						filled_in[r]=false;
						r++;
					}
				}
				else {//if square already filled in, define a single row of exact_cover_matrix corresponding to the triplet (i,j,n) 
					int n=sudoku_matrix[i][j];
					
					for (int l:Arrays.asList(index(i,j),81+index(i,n-1),2*81+index(j,n-1),3*81+index(ss,n-1))) {
						exact_cover_matrix[r][l]=1;
					}
					triplets[r]= new int[]{i,j,n};
					filled_in[r]=true;
		
					r++;			
				}			
			}
			
		}

		//---------------------------------------------------------------------
		/*we now solve the exact cover problem with DLX and for each cover, 
		  we find indices of rows in the solution, then use our mapping to find out 
		  what to fill in: number n at the square (i,j) if (i,j,n) is a triplet
		*/
		
		DancingLinks dl= new DancingLinks(exact_cover_matrix);	
		Set<Set<data_object>> exact_covers=dl.exactCover(dl.master_header);
		
		//now convert  the exact covers to solution matrices
		for (Set<data_object> cover: exact_covers) {
			int[][] sol_matrix= new int[9][9]; 
			
			for (data_object t: cover) {
				int row_id=t.row_id;
				int i=triplets[row_id][0];
				int j=triplets[row_id][1];
				int n=triplets[row_id][2];
				sol_matrix[i][j]=n;
			}
			
			solutions.add(sol_matrix);
		}
		
		return solutions;
		
	}
	
	private int index(int x, int n) {//if x is in [0,8] and n also, then the result is in [0,81-1]
		return 9*x+n;
	}
	

	public static void main(String[] args) {
		int[][] M= new int[][]{
			  {5, 3, 0, 0, 7, 0, 0, 0, 0},
			  {6, 0, 0, 1, 9, 5, 0, 0, 0},
			  {0, 9, 8, 0, 0, 0, 0, 6, 0},
			  {8, 0, 0, 0, 6, 0, 0, 0, 3},
			  {4, 0, 0, 8, 0, 3, 0, 0, 1},
			  {7, 0, 0, 0, 2, 0, 0, 0, 6},
			  {0, 6, 0, 0, 0, 0, 2, 8, 0}, 
			  {0, 0, 0, 4, 1, 9, 0, 0, 5},
			  {0, 0, 0, 0, 8, 0, 0, 7, 9},
		};
		
		int[][] N= new int[][]{//matrix with 23 clues admitting 96 solutions
			  {5, 3, 0, 0, 0, 0, 0, 0, 0},
			  {6, 0, 0, 1, 9, 5, 0, 0, 0},
			  {0, 9, 8, 0, 0, 0, 0, 0, 0},
			  {0, 0, 0, 0, 6, 0, 0, 0, 3},
			  {4, 0, 0, 0, 0, 3, 0, 0, 1},
			  {7, 0, 0, 0, 2, 0, 0, 0, 6},
			  {0, 6, 0, 0, 0, 0, 2, 8, 0}, 
			  {0, 0, 0, 0, 0, 0, 0, 0, 5},
			  {0, 0, 0, 0, 8, 0, 0, 7, 9},
		};
		
		int[][] the_solution= new int[][]{//unique solution to M
			  {5, 3, 4, 6, 7, 8, 9, 1, 2},
			  {6, 7, 2, 1, 9, 5, 3, 4, 8},
			  {1, 9, 8, 3, 4, 2, 5, 6, 7},
			  {8, 5, 9, 7, 6, 1, 4, 2, 3},
			  {4, 2, 6, 8, 5, 3, 7, 9, 1},
			  {7, 1, 3, 9, 2, 4, 8, 5, 6},
			  {9, 6, 1, 5, 3, 7, 2, 8, 4}, 
			  {2, 8, 7, 4, 1, 9, 6, 3, 5},
			  {3, 4, 5, 2, 8, 6, 1, 7, 9},
		};
		
		Sudoku sdk = new Sudoku(N);
		
		ArrayList<int[][]> solutions = sdk.solve();
		sdk.display_solutions();
	}
	
	
}
/*

ALGORITHM:

-suppose we place some number n at the square S=(r,c) of index I in 3x3 subsquare of index k 1-9.
-the associated row is initially all 0's; and we fill in 1 in exactly 4 column positions:
- at column index(i-1,j) (first block, constraint A: fill in all cells )
- at column 81+index(i,n) (second block, constraint B: each number 1-9 appears exactly once per row)
- at column 2*81+index(j,n) (third block, constraint C: each number 1-9 appears exactly once per column)
- at column 3*81 + index(k,n) (4th block, constraint D: each number 1-0 appears exactly once per 3x3 subsquare)

where we define index(x,n) for x 1-9 and n 1-9 as 9x+n

-for each such row R created by placing n at the square S=(i,j), we associate the triplet (i,j,n) to R
(and store this mapping somewhere)

-the matrix M initially has 4*81=324 columns, and F+9*(9^2-F) rows where F=# squares already filled in
-each already filled in square contributes one row, and the empty ones 9 rows 

-as we are creating the matrix M, we store, for each square already filled in, the id of the columns where we write a 1 in the associated row (4 of them)
-we combine all these column id's, over all the already filled in squares in a list L

-once we've calculated the exact cover matrix M, we solve the exact cover problem with DLX
and for each cover, we find indices of rows in the solution, then use our mapping to find out 
what to fill in: number n at the square (i,j) if (i,j,n) is a triplet


Note that the subsquares are enumerated in the following fashion:
		
		1 4 7
		2 5 8
		3 6 9
		
*/