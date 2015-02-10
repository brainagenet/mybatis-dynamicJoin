package dynamicJoin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Class for creating joins for any entity using the Mapper.xml of that entitiy. This class is private as {@link JoinFactory}
 * should be called from another packages.
 * 
 * The relations must be anotated with {@link JoinableField} in the  entity class, otheriwse it will be ignored.
 * 
 * Considerations that the Mappers must have for the dynamic join generation works. These are MANDATORY:
 *  - Ther must be a Result map with the word Join in it's id.
 *  - Each associaton or collection must define a columnPrefix. Same prefix can be used for all relations.
 *  - If an entitity has two or more relations with the same Entitty (for example: a child has a Mother and a 
 *  Father each one being a Person entitity) they must have diferent columnPrefix.
 *  - Each associaton must define a javaType (por associaton) or typeOf (for collections)  
 *  
 *  - There must be a <sql id="Base_Column_List"> for all entities without comments.
 *  
 *  - Ther must be a <sql> with the real name of the table: 
 *  <sql id="TableName">
 * 	   AUTHOR
 *  </sql>
 *  
 *  If the Mapper was generated with an alias and the Base_Column_List looks like  PT.ID as PT_ID  a <sql> with
 *  id AliasTable must be defined. This sql is not mandatory and is not validated.
 *  <sql id="AliasTable">
 * 	   PT
 *  </sql>
 *  
 *  If requirements are not met a JoinException is thrown with the reason.  
 *  
 *  Pay attention to the name of the columns defined in Base_Column_List and the name of the columns in the ResultMap.
 * @author ggefaell
  * @param <E> 
 */
class GenericJoin<E> implements Joinable {
	
	private List<Joinable> nestedAnidados = new ArrayList<>();
	private MapperInfo info;
	private String ownPrefix = "";
	private String joinStmnt;
	private String selectStmnt;
	
	private E entity;
	private String joinClause;
	


	GenericJoin(E entidad) {
		this(entidad," LEFT JOIN ");
	}
	
	GenericJoin(E entidad,String joinClau){
		this.joinClause = joinClau;
		try {
			this.info = MapperInfo.getMapperInfo(entidad);
			validateTableName();
			construirJoins(entidad,new Alias(this.info.tableName));
		} catch (Exception e) {
			throw new JoinException(e.getMessage(), entidad.getClass());
		}
	}



	private GenericJoin(E entidad,Alias pAlias, Relation rel) {

		String nomEntid = entidad.getClass().getSimpleName();
		try {
			this.info = MapperInfo.getMapperInfo(entidad);
		} catch (Exception e) {
			throw new JoinException(e.getMessage(), entidad.getClass());
		}
		validateTableName();
		nomEntid = this.info.getTableName();
		this.joinClause = rel.joinClause;
		this.ownPrefix = rel.prefixFacther.concat(rel.ownPrefix);
		this.joinStmnt = new StringBuilder(rel.joinClause).append(nomEntid).append(" ").append(pAlias.nextAlias()).append(" ON ").append(pAlias.getAliasPadre()).append(".").append(rel.ownColumn).append(" = ")
				.append(pAlias.getCurrentAlias()).append(".").append(rel.foreignColumn).toString();
		this.selectStmnt= this.info.select.replaceAll(this.info.aliasTabla.toUpperCase().concat("\\."), pAlias.getCurrentAlias().concat("."));
		String aux = pAlias.getAliasPadre();
		pAlias.setAliasFather(pAlias.getCurrentAlias());
		selectStmnt = selectStmnt.replaceAll("\\s[Aa][Ss]\\s", " as ".concat(this.ownPrefix));
		try {
			construirJoins(entidad,pAlias);
		} catch (Exception e) {
			throw new JoinException(e.getMessage(), entidad.getClass());
		}
		pAlias.setAliasFather(aux);
	}

	private void validateTableName() {
		if (info.tableName.isEmpty())
			throw new  JoinException("No tablename defined for mapper.",entity.getClass());
	}

	
	@SuppressWarnings("rawtypes")
	private void construirJoins(E entidad, Alias alias) throws Exception{
		Class<? extends Object> clase = entidad.getClass();
		for (Field field : clase.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.getAnnotation(JoinableField.class) !=null && field.get(entidad)!=null){
				Relation rel = this.info.joins.get(field.getName());
				if (rel==null){
					throw new  JoinException("Asked for relation " + field.getName() 
							+ " but no relationship was found in the mapper",entidad.getClass());
				}
				Object parmeter = field.get(entidad); 
				if (field.get(entidad) instanceof Collection)
					parmeter = (E) getEntity(rel.clazz, field.get(entidad));
				rel.prefixFacther = this.ownPrefix;
				rel.joinClause = this.joinClause;
				this.nestedAnidados.add(new GenericJoin(parmeter,alias,rel));
			}
		}
	}



	private Object getEntity(String clase, Object object) throws Exception {
		if (object instanceof Collection){
			Collection col = (Collection)object;
			if (col.size()>0) {
				return col.iterator().next();
			}
		}
		return Class.forName(clase).newInstance();
	}

	
	@Override
	public String getSelectStmnt() {
		return selectStmnt;
	}

	@Override
	public String getJoinStmnt() {
		return joinStmnt;
	}

	@Override
	public List<Joinable> getNestedJoins() {
		List<Joinable> retorno = new ArrayList<>();
		for (Joinable joinable : nestedAnidados) {
			retorno.add(joinable);
			retorno.addAll(joinable.getNestedJoins());
		}
		return retorno;
	}

}
