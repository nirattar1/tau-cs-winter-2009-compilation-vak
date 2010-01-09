package IC.TypeTable;

/**
 * Primitive Int Type
 */
public class IntType extends Type {
	public IntType(){
		super("int");
	}
	
	public boolean subtypeOf(Type t){
		if (t == this) return true;
		//if (t.getName() == this.getName()) return true;
		else return false;
	}
}
