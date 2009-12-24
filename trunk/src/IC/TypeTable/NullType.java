package IC.TypeTable;

/**
 * Null Type
 */
public class NullType extends Type {
	public NullType(){
		super("null");
	}
	
	public boolean subtypeOf(Type t){
		if (TypeTable.isPrimitive(t.getName())){
			return false;
		} else return true;
	}
}
