import java.util.*;

public class DancingLinks {
	
	int [][] M;//problem matrix
	ArrayList<Set<Integer>> set_of_row=new ArrayList<Set<Integer>>();//stores set from C corresponding to each row
	int nb_rows;
	int nb_columns;
	
	//we don't need to implement the column_object and data_objects separately
	//a column_object is simply a data_object with the fields "size" and "col_id" defined
	data_object master_header = new data_object();
	data_object[][] linked_matrix;//the famous dancing links structure
	
	
	public DancingLinks(int[][] M) {//constructor which builds the linked_matrix structure
		this.M=M;
		nb_rows=M.length;
		nb_columns=M[0].length;
		
		for (int i=0;i<nb_rows;i++) {//set up set_of_row
			Set<Integer> S = new HashSet<Integer>();
			for (int j=0;j<nb_columns;j++) {
				if (M[i][j]==1) {
					S.add(j+1);//we assume we are solving an exact cover problem for $X={1,....,n}$ and $C$ a collection of subsets of X
				}
			}
			set_of_row.add(S);
		}
	
		linked_matrix = new data_object[nb_rows+1][nb_columns];//last row will be the headers (column_objects)
		for (int i=0; i<=nb_rows;i++) {//initialize the matrix objects one-by-one
			for (int j=0;j<nb_columns;j++) {
				linked_matrix[i][j]=new data_object();
			}

		}
		
		for (int j=0; j<nb_columns;j++) {//setting up the column headers
			
			linked_matrix[nb_rows][j].col_id=j+1;//number of column
			
			linked_matrix[nb_rows][j].U=linked_matrix[nb_rows][j];//column headers initially linked to themselves in U,D directions
			linked_matrix[nb_rows][j].D=linked_matrix[nb_rows][j];
			
			linked_matrix[nb_rows][j].R=linked_matrix[nb_rows][(j+1)%nb_columns];//cyclic structure in R,L directions
			linked_matrix[nb_rows][j].L=linked_matrix[nb_rows][mod((j-1),nb_columns)];
			
		}
		
		master_header.R=linked_matrix[nb_rows][0];//setting up the master_header
		linked_matrix[nb_rows][0].L=master_header;
		master_header.L=linked_matrix[nb_rows][nb_columns-1];
		linked_matrix[nb_rows][nb_columns-1].R=master_header;
		
		for (int i=0; i<nb_rows;i++) {//create data_objects corresponding to elements of M
			for (int j=0;j<nb_columns;j++) {

				
				if (M[i][j]==1) {//if there is a 1, we define the data_object associated
					linked_matrix[i][j].row_id=i;
					linked_matrix[i][j].C=linked_matrix[nb_rows][j];
					linked_matrix[nb_rows][j].size+=1;
					
					//-----------------------RIGHT
					for (int c=j+1; c<=j+nb_columns; c++) {//find first one in M to the right of this square
						if (M[i][c%nb_columns]==1) {
							linked_matrix[i][j].R=linked_matrix[i][c%nb_columns];
							break;
						}
					}
					//---------------------LEFT------------------------
					for (int c=j-1; c>=j-nb_columns; c--) {//find first one in M to the left of this square
						if (M[i][mod(c,nb_columns)]==1) {
							linked_matrix[i][j].L=linked_matrix[i][mod(c,nb_columns)];
							break;
						}
					}
					//-------------------DOWN---------------------------
					int r=i+1;
					while (r<nb_rows) {//find first one below this square
						if (M[r][j]==1) {
							linked_matrix[i][j].D=linked_matrix[r][j];
							break;
						}
						 r++;
					}
					if (r==nb_rows) {
						linked_matrix[i][j].D=linked_matrix[nb_rows][j];
						linked_matrix[nb_rows][j].U=linked_matrix[i][j];
					}
					//-----------------UP--------------------------------
					r=i-1;
					while (r>=0) {//find first one below this square
						if (M[r][j]==1) {
							linked_matrix[i][j].U=linked_matrix[r][j];
							break;
						}
						 r--;
					}
					
					if (r==-1) {
						linked_matrix[i][j].U=linked_matrix[nb_rows][j];
						linked_matrix[nb_rows][j].D=linked_matrix[i][j];
					}
				

				}

			}
		}	
	
	}
	
	public static void coverColumn(data_object x) {
		x.R.L = x.L; 
		x.L.R = x.R;
		data_object t=x.D;
		while (!t.equals(x)) {
			data_object y=t.R;
			while (!y.equals(t)) {
				y.D.U = y.U; 
				y.U.D = y.D;
				y.C.size-= 1;
				
				y=y.R;
			}
			t = t.D;
		}
	}
	
	public static void uncoverColumn(data_object x) {
		x.R.L = x; 
		x.L.R = x;
		data_object t=x.U;
		while (!t.equals(x)) { 
			data_object y=t.L;
			while (!y.equals(t)){
				y.D.U = y; 
				y.U.D = y;
				y.C.size+= 1;
				
				y=y.L;
			}
			t=t.U;
		}
	}

	public Set<Set<data_object>> exactCover(data_object master_header){
			Set<Set<data_object>> exact_cover=new HashSet<Set<data_object>>();
			
			if (master_header.R.equals(master_header)) {
				Set<data_object> empty=new HashSet<data_object>();
				exact_cover.add(empty);
				return exact_cover;
			}
			//Choose a column x with x.S minimal; 
			//----------------------------------------------
			data_object x=master_header.R;
			int min_size=Integer.MAX_VALUE;
			data_object itr=master_header.R;
			
			while (!(itr.col_id==master_header.col_id)) {
				if(itr.size<min_size) {
					x=itr;
					min_size=x.size;
				}
				itr=itr.R;
			}	
			//---------------------------------------------
			
			coverColumn(x);
			data_object t=x.U;
			
			while (! t.equals(x)) {
				data_object y=t.L;
				while  (!y.equals(t)) {
					coverColumn(y.C);
					y = y.L;
				}

				
				for (Set<data_object> P: exactCover(master_header)) {							
					P.add(t);	
					exact_cover.add(P);
				}
	
				data_object z=t.R;
				while(!z.equals(t)) {
							
					uncoverColumn(z.C);
					z=z.R; 
				}
	
				t=t.U;
					
			}

			uncoverColumn(x);
					
			return exact_cover;
	
	}
	

	
	private int mod(int x,int n) {//extends x%n for x negative		
		int r = (x % n); 
		return ((r >> 31) & n) + r;
	}
	
}

