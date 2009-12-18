package IC.TypeTable;

/**
 * Primitive Boolean Type
 */
public class BoolType extends Type {
	public BoolType(){
		super("boolean");
	}
	
	public boolean subtypeOf(Type t){
		if (t.getName() == this.getName()) return true;
		else return false;
	}
}