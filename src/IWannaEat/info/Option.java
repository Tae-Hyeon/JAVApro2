package IWannaEat.info;

import java.io.Serializable;

public class Option implements Serializable{
	String id;
	String Shapname;
	int breaktime;
	int table;

	public void setShapname(String s){
		Shapname=s;
	}
	public void setbreakime(int s){
		breaktime=s;
	}
	public void setid(String s){
		id=s;
	}
	public void settalbe(int s){
		table=s;
	}
	public String getShapname(){
		return Shapname;
	}
	public int getbreakime(){
		return breaktime;
	}
	public String getid(){
		return id;
	}
	public int gettalbe(){
		return table;
	}
	
}