
public class Square implements Comparable<Square>{
	int x;
	int y;
	
	public Square(int x,int y){
		this.x=x;
		this.y=y;
	}
	
	public int compareTo(Square q) {
		if (this.x>q.x) {
			return 1;
		}
		if (this.x==q.x) {
			if (this.y>q.y) {
				return 1;
			}
			if (this.y==q.y) {
				return 0;
			}
			return -1;
		}			
		return -1;		
	}
	
	public boolean equals(Square q) {
		return this.compareTo(q)==0;
	}
	
	public boolean lessThan(Square q) {
		return this.compareTo(q)==-1;
	}
	public boolean greaterThan(Square q) {
		return this.compareTo(q)==1;
	}
	
	public Square clone() {
		return new Square(this.x,this.y);
	}
}
