## Polyominos and Exact Covers
One Paragraph of project description goes here
The current repository is an Eclipse Java Project designed generate to and manipulate Polyominos and solve ceartain exact cover problems, including Polyomino tiling. We summarise the purpose of each class and the methods that can be called to execute our programs. __Examples to show that our methods work are given in the main of each of the following classes:__ tester, Exact_cover, Tiling, Sudoku, Redelmeier.

## tester 

## Polyomino

This class defines the Polyomino object, which has the fields: vertices (ArrayList of Square objects), color, and area (number of vertices). There are several ways to construct a Polyomino:

    Polyomino(final ArrayList<Square> vertices)

This is the original Polyomino constructor. It builds a Polyomino P from its vertex list, and translates this polyomino such that the point (xmin,ymin) is at the origin, where xmin and ymin are the minimal x (resp. y) values of P.vertices. This is the canonical form which I use in my naive Polyomino generation scheme.


Later we overide this constructor with

    Polyomino(final ArrayList<Square> vertices,String s)

where the String s, which should typically only receive as input "R" is used to indicate the this Polyomino should not be put into my canonical form. This constructor is used for Redelmeier generation scheme, where translating to the origin would be unnecessary and costly. 

We may also create a Polyomino (in my canonical form) from string representing vertices:

	Polyomino(String s)

or several Polyominos from a text file:

	public static ArrayList<Polyomino> get_polyominos(File file) 

There are also several methods for manipulating a Polyomino object:

    //translation by integer pair (x,y) 
    void translate(int x, int y) 

	//rotation 90 degrees anti-clockwise, and put back into canonical form
	void rotate() 

    //reflection across x-axis
	void reflect_x()

	//scale polyomino by a factor k
	void dilate(int k)

   //We can also access the set of of reflections/rotations/symmetries with:

    ArrayList<Polyomino> reflections ()
	ArrayList<Polyomino> rotations ()
    ArrayList<Polyomino> symmetries ()

For printing vertices of a Polyomino:

    void print_vertices() 

For adding a Polyomino to an Image2d object img (with the possibility of coloring the Polyomino's edges white):

    void add_to(Image2d img, boolean borders) 

## Generate

This class defines the method 

	public static ArrayList<Polyomino> polyominos(int area, String type) 

which generates all Polyominos of a given integer area and type ("fixed", "onesided", or "free") using my naive scheme.

## Redelmeier

Here we generate all Polyominos of a given integer area and type ("fixed", "onesided", or "free") using the more clever Redelmeier scheme, using:
	
	public static ArrayList<Polyomino> polyominos(int area, String type)

## Exact_cover

Here we define some functions allowing us to solve exact cover problems. 

To solve an exact cover problem for X={1,...,n} and C a collection of subsets of X, use:

	public static Set<Set<Set<Integer>>> naive_backtracking(Set<Integer> X, Set<Set<Integer>> C) 

To convert from a set representation  (X,C) to matrix representation M(X,C) of an exact-cover problem:

    public static int[][] sets_to_matrix(Set<Integer> X,Set<Set<Integer>> C) 

Finally, to generate all subsets of {1,...,n} use:

    static Set<Set<Integer>> allSubsets(int n)

## DancingLinks

We can solve exact cover problems more cleverly with the Dancing Links method. To solve an exact cover problem represented by an integer matrix M, 
first define a DancingLinks object dl with 

    public DancingLinks(int[][] M) 

This initializes the linked data-object matrix structure. Given the master header of dl, dl.master_header, (denoted "H" in the project description), we can then call

    public Set<Set<data_object>> exactCover(data_object dl.master_header)

which outputs the set of exact covers of M, in which each cover is represented by a set of data-objects, each of which corresponds to a row of M in the cover. To put the set of exact covers into the more useful form  Set<Set<Set<Integer>>>  as above, see the example in the  main method of Exact_cover.

## Tiling

To tile a given Polyomino P with tiles from a list of Polyominos polyominos_list, call:


    public static Set<Set<Polyomino>> tilings(ArrayList<Polyomino> polyominos_list,Polyomino P, boolean use_all_once, boolean rotations, boolean reflections) 

If use_all_once is set to true, the method only outputs tilings in which all tiles are used exactly once. rotations and reflections indicate whether we may rotate or reflect the tiles.

To find the set of all tilings in which a polyomino of area n tiles its k-dilate (with the possibility of not using tiles with the same orientation), call

    public static Set<Set<Set<Polyomino>>> tile_dilate(int n,int k, boolean use_all_once)

Any tiling may be displayed with:

    public static void display_tiling(Set<Polyomino> tiling)

## Sudoku

To solve a sudoku problem, express it as a 9x9 integer matrix M in which entry is 0-9. A zero represents an unfilled grid square, and the nonzero entries are clues. We can create a Sudoku object with:

    public Sudoku(int[][] M) 

and solve it by calling 

    public ArrayList<int[][]> solve() 

Finally we may display the solutions (only if we have already called the above function) with:

    public void display_solutions() 

## Image2d
We display Polyominos (and more generally, ColoredPolygons) using this class.The constructor that instantiates an image of a specified width and height is:

    public Image2d(int width, int height) 

## Square
This class defines the Square object, which is a pair of integers (x,y) representing the bottom-left vertex of a lattice square. A total order on Squares is defined, which allows us to compare Squares.

## data_object 
This class defines the data-object object, which is used in the DancingLinks class. The nature of this object is defined in the project description.

## Authors

* **Joshua Benabou** 
