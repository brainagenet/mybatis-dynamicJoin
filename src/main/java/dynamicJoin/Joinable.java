package dynamicJoin;

import java.util.List;


public interface Joinable{
	/**
	 * Obtains the columns for the select statement for this joinable. No trailing comma. 
	 * @return
	 * @author ggefaell
	 */
	String getSelectStmnt();
	
	/**
	 * Obtains the join statment for this joinable.
	 * @return
	 * @author ggefaell
	 */
	String getJoinStmnt();
	
	/**
	 * Obtains the list of all the nested joins of this joinable.  
	 * @return
	 * @author ggefaell
	 */
	List<Joinable> getNestedJoins();
	

}
