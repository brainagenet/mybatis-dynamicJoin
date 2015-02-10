package dynamicJoin;

import java.util.List;

public class JoinFactory {

	private static String right = " RIGHT JOIN ";
	private static String left = " LEFT JOIN ";

	/**
	 * Obtains the list of joins using the parameter entity. For each field
	 * of entitty annotatted as {@link JoinableField} that is not null its joins are generated. 
	 * 
	 * All joins are obtained recursively for each entity that has a field != null.
	 * 
	 *  If all fields annotatted with {@link JoinableField} are null it returns an empty list.
	 *  
	 * @param entity Entity with fields initialized. 
	 * @return List of joinables.
	 * @author ggefaell
	 */
	public static <E> List<Joinable> LeftJoins(E entity) {
		if (entity == null)
			throw new IllegalArgumentException("Entity must not be null");
		return new GenericJoin<E>(entity).getNestedJoins();
	}

	public static <E> List<Joinable> RightJoins(E entidad) {
		if (entidad == null)
			throw new IllegalArgumentException("Entity must not be null");
		return new GenericJoin<E>(entidad, right).getNestedJoins();
	}

}
