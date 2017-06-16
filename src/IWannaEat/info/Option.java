package IWannaEat.info;

import java.io.Serializable;

public class Option implements Serializable, Colors{
	private String id;
	private String name;
	private int side;
	private int table[] = new int[25];
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	public int getTable(int index){
		return this.table[index];
	}
	public void setTable(int color, int index){
		this.table[index] = color;
	}
}