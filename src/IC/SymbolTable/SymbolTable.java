package IC.SymbolTable;

/**
 * Abstract class for the symbol tables
 *
 */
public abstract class SymbolTable {
	protected SymbolTable parent;
	protected int depth;
	
	/**
	 * constructor for generic SymbolTable 
	 * @param parent
	 */
	public SymbolTable(SymbolTable parent){
		this.parent = parent;
		// update depth if not global
		if (parent != null){
			this.depth = parent.getDepth()+1;
		} else this.depth = -1; // this is global
	}
	
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
}
