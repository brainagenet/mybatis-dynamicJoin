package dynamicJoin;

public class Alias {

	private char alias = 'a';
	private String aliasFather;
	
	public Alias(){}
	public Alias(String nomEntid) {
		aliasFather = nomEntid;
	}

	public String getCurrentAlias(){
		return String.valueOf(alias); 
	}
	
	public String nextAlias(){
		return String.valueOf(++alias); 
	}

	public String getAliasPadre() {
		return aliasFather;
	}

	public void setAliasFather(String aliasFather) {
		this.aliasFather = aliasFather;
	}
}
