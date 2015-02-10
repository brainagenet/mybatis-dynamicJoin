package dynamicJoin;

/**
 * Class representing a relation between to entities.
 * @author ggefaell
 *
 */
class Relation{
	protected String clazz;
	protected String prefixFacther;
	protected String ownPrefix;
	protected String ownColumn;
	protected String foreignColumn;
	protected String joinClause;
}