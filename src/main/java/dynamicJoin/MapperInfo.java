package dynamicJoin;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

class MapperInfo {
	String tableName;
	String aliasTabla;
	String select;
	private boolean resulMapJoinsExists = false;
	private Object entidad;
	Map<String, Relation> joins = new HashMap<>();
	
	private static final Logger log = Logger.getLogger( MapperInfo.class.getName() );

	private static ConcurrentHashMap<String, MapperInfo> cache = new ConcurrentHashMap<>();

	public static <E> MapperInfo getMapperInfo(Object entidad) throws Exception {
		String nomEntid = entidad.getClass().getSimpleName();
		if (cache.containsKey(nomEntid))
			return cache.get(nomEntid);
		else {
			MapperInfo info = new MapperInfo();
			info.entidad = entidad;
			info.parseMapper(entidad);
			if (info.tableName == null)
				throw new JoinException("No sql TableName defined in mapper", entidad.getClass());
			if (!info.resulMapJoinsExists)
				log.info("No resultMap for joins defined in mapper "+  entidad.getClass().getName());
			if (info.aliasTabla == null)
				info.aliasTabla = info.tableName;
			cache.put(nomEntid, info);
			return info;
		}
	}

	private void parseMapper(Object entidad) throws SAXException, IOException {
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setEntityResolver(new DummyEntityResolver());
		reader.setContentHandler(new CustomHandler());
		reader.parse(new InputSource(entidad.getClass().getClassLoader()
				.getResourceAsStream("mybatis\\mappers\\" + entidad.getClass().getSimpleName() + "Mapper.xml")));
	}

	private class CustomHandler extends DefaultHandler {
		boolean inResultMap = false;
		boolean inBaseColumn = false;
		boolean inTableName = false;
		boolean inAliasTable = false;

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

			if (qName.equalsIgnoreCase("resultMap")) {
				if (attributes.getValue("id").contains("Joins")) {
					resulMapJoinsExists = true;
					inResultMap = true;
				} else {
					inResultMap = false;
				}
			}
			if (qName.equalsIgnoreCase("sql") && attributes.getValue("id").equalsIgnoreCase("Base_Column_List")) {
				inBaseColumn = true;
			}

			if (qName.equalsIgnoreCase("sql") && attributes.getValue("id").equalsIgnoreCase("TableName")) {
				inTableName = true;
			}

			if (qName.equalsIgnoreCase("sql") && attributes.getValue("id").equalsIgnoreCase("AliasTable")) {
				inAliasTable = true;
			}

			if (inResultMap && (qName.equalsIgnoreCase("association") || qName.equalsIgnoreCase("collection"))) {

				boolean fieldAnotated = false;
				Relation rel = new Relation();
				for (Field field : entidad.getClass().getDeclaredFields()) {
					if (field.getName().equals(attributes.getValue("property"))
							&& field.getAnnotation(JoinableField.class) != null) {
						fieldAnotated = true;
						rel.foreignColumn = field.getAnnotation(JoinableField.class).foreignColumn(); 
						rel.ownColumn = field.getAnnotation(JoinableField.class).ownColumn();
						break;
					}
				}
				
				if (!fieldAnotated) {
					log.info( "The relationship " + attributes.getValue("property") + " for entity "
							+ entidad.getClass().getSimpleName()
							+ " isn't annotated with JoinableField. Ignoring relationship for dynamic join");
					return;
				}
				validateAttributes(attributes, qName);
				if (qName.equalsIgnoreCase("collection"))
					rel.clazz = attributes.getValue("ofType");
				else
					rel.clazz = attributes.getValue("javaType");
				rel.ownPrefix = attributes.getValue("columnPrefix");

				joins.put(attributes.getValue("property"), rel);
			}
		}

		private void validateAttributes(Attributes attributes, String tipoRelacion) {

			if (tipoRelacion.equalsIgnoreCase("collection") && attributes.getValue("ofType") == null)
				throw new JoinException("No type defined por relationship " + attributes.getValue("property") + " at resultmap",
						entidad.getClass());
			if (tipoRelacion.equalsIgnoreCase("association") && attributes.getValue("javaType") == null)
				throw new JoinException("No type defined por relationship " + attributes.getValue("property") + " at resultmap",
						entidad.getClass());
			if (attributes.getValue("columnPrefix") == null)
				throw new JoinException("No columnPrefix defined por relationship " + attributes.getValue("property")
						+ "  at resultmap", entidad.getClass());
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {

			if (qName.equalsIgnoreCase("resultMap")) {
				inResultMap = false;
			}
		}

		public void characters(char ch[], int start, int length) throws SAXException {
			if (inBaseColumn) {
				select = new String(ch, start, length).trim();
				inBaseColumn = false;
			}
			if (inTableName) {
				tableName = new String(ch, start, length).trim();
				inTableName = false;
			}
			if (inAliasTable) {
				aliasTabla = new String(ch, start, length).trim();
				inAliasTable = false;
			}
		}
	}

	/**
	 * Dymmy class for avoiding resolvig dtd
	 * 
	 * @author ggefaell
	 */
	private class DummyEntityResolver implements EntityResolver {
		public InputSource resolveEntity(String publicID, String systemID) throws SAXException {
			return new InputSource(new StringReader(""));
		}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
