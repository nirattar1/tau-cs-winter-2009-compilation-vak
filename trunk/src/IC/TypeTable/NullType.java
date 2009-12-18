package IC.TypeTable;

/**
 * Null Type
 */
public class NullType extends Type {
	public NullType(){
		super("null");
	}
	
	public boolean subtypeOf(Type t){
		if (t.getName() == this.getName()) return true;
		else return false;
	}
}
