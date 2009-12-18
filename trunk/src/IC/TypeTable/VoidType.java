package IC.TypeTable;

/**
 * Primitive Void Type
 */
public class VoidType extends Type {
	public VoidType(){
		super("void");
	}
	
	public boolean subtypeOf(Type t){
		if (t.getName() == this.getName()) return true;
		else return false;
	}
}
