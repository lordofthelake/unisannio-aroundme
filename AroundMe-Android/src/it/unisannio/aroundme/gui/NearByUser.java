package it.unisannio.aroundme.gui;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class NearByUser implements Serializable, Comparable<NearByUser>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public NearByUser(){
		/*	
		 * Genera un utente fittizio
		 * */
		Random rnd=new Random(100);
		this.firstName="Tizio";
		this.lastName="Caio "+rnd.nextInt();
		this.dinstance=rnd.nextInt();
		this.affinity=rnd.nextInt();
		this.userID="0000";
		this.profileUrl="http://facebook.com/"+this.userID;
		this.interestsWithoutYou=new ArrayList<String>();
		this.interestsWithYou=new ArrayList<String>();
		this.male=true;
		this.currentLat=53.214332;
		this.currentLong=51.214332;
	}
	public NearByUser(
			String firstName,String lastName,int dinstance,
			int affinity,String userID,String ImageUrl,String profileUrl,
			double currentLat, double currentLong,
			ArrayList<String> interestsWithYou, ArrayList<String> interestsWithoutYou
			){
		
		this.firstName=firstName;
		this.lastName=lastName;
		this.dinstance=dinstance;
		this.affinity=affinity;
		this.userID=userID;
		this.profileUrl=profileUrl;
		this.imageUrl=ImageUrl;
		this.interestsWithoutYou=interestsWithoutYou;
		this.interestsWithYou=interestsWithYou;
		this.male=true;
		this.currentLat=currentLat;
		this.currentLong=currentLong;
	}
	public int compareTo(NearByUser another) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String getImageUrl(){
		return imageUrl;
	}
	public String getFirstName() {
		return firstName;
	}
	public int getDinstance() {
		return dinstance;
	}
	public String getLastName(){
		return lastName;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public ArrayList<String> getInterestsWithYou() {
		return interestsWithYou;
	}
	public ArrayList<String> getLikesWithoutYou() {
		return interestsWithoutYou;
	}
	public boolean isMale() {
		return male;
	}
	public boolean isFemale(){
		return !male;
	}
	public int getAffinity() {
		return affinity;
	}
	public double getCurrentLat() {
		return currentLat;
	}
	public double getCurrentLong() {
		return currentLong;
	}
	
	private String userID;
	private String firstName;
	private String imageUrl;
	private String lastName;
	private int dinstance;
	private String profileUrl;
	private ArrayList<String> interestsWithYou;
	private ArrayList<String> interestsWithoutYou;
	private boolean male;
	private int affinity;
	private double currentLat;
	private double currentLong;
}
