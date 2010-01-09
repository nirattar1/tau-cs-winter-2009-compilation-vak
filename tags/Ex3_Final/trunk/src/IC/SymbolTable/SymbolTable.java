package IC.SymbolTable;

/**
 * Abstract class for the symbol tables
 *
 */
public abstract class SymbolTable {
	protected SymbolTable parent;
	
	/**
	 * constructor for generic SymbolTable 
	 * @param parent
	 */
	public SymbolTable(SymbolTable parent){
		this.parent = parent;
	}
}
