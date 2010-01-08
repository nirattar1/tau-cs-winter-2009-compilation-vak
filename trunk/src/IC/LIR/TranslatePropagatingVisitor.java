package IC.LIR;

import IC.AST.*;
import IC.SymbolTable.*;
import IC.TypeTable.*;
import IC.LIR.LIRFlagEnum;
import java.util.*;

/**
 * Translating visitor to LIR
 */
public class TranslatePropagatingVisitor implements PropagatingVisitor<Integer, LIRUpType>{

	private static GlobalSymbolTable global;
	private static String currentClassName;
	
	public TranslatePropagatingVisitor(GlobalSymbolTable global){
		TranslatePropagatingVisitor.global = global;
	}
	
	// string literals counter
	private int stringLiteralsCounter = 0;
	// string literals map, each element literal string is mapped to the format 'str<i>'
	private Map<String,String> stringLiterals = new HashMap<String,String>();
	// class layouts
	private Map<String,ClassLayout> classLayouts = new HashMap<String,ClassLayout>();
	// class dispatch tables, each element in the format: '_DV_<class name>: [<method1>,<method2>,...]'
	private List<String> classDispatchTable = new ArrayList<String>();
	// methods procedural code string representation
	private List<String> methods = new ArrayList<String>();
	// main method string representation
	private String mainMethod = "";
	
	/**
	 * Program propagating visitor:
	 * - recursive calls to all classes in program
	 * - returns the LIR representation of the IC program ordered by:
	 * 		- string literals
	 * 		- class dispatch tables
	 * 		- methods
	 * 		- main method
	 * @param program
	 * @param d
	 * @return
	 */
	public LIRUpType visit(Program program, Integer d){
		// visit all classes recursively
		for(ICClass c: program.getClasses()){
			c.accept(this, 0);
		}
		
		// return LIR representation for the IC program
		String lirBuffer = "";
		
		// (1) insert all string literals
		lirBuffer += "# string literals\n";
		for (String strLiteral: this.stringLiterals.keySet()){
			lirBuffer += this.getStringLiterals().get(strLiteral)+": \""+strLiteral+"\"\n";
		}
		lirBuffer += "\n";
		
		// (2) insert class dispatch tables
		lirBuffer += "# class dispatch tables\n";
		for (String classDisTab: this.classDispatchTable){
			lirBuffer += classDisTab+"\n";
		}
		lirBuffer += "\n";
		
		// (3) insert all methods
		lirBuffer += "# methods\n";
		for (String methodStr: this.methods){
			lirBuffer += methodStr+"\n";
		}
		
		// (4) insert main method
		lirBuffer += "# main method\n";
		lirBuffer += this.mainMethod;
		
		return new LIRUpType(lirBuffer, LIRFlagEnum.EXPLICIT,"");
	}

	/**
	 * ICClass propagating visitor:
	 * - updates class dispatch tables
	 * - recursive calls to all methods in the class
	 * @param icClass
	 * @param d
	 * @return
	 */
	public LIRUpType visit(ICClass icClass, Integer d){
		// create class layout
		ClassLayout classLayout;
		if (icClass.hasSuperClass()){
			// already have super-class layout at this point
			classLayout = new ClassLayout(icClass, classLayouts.get(icClass.getSuperClassName()));
		} else {
			classLayout = new ClassLayout(icClass);
		}
		// insert to classLayouts
		classLayouts.put(icClass.getName(), classLayout);
		// insert class dispatch table representation
		classDispatchTable.add(classLayout.getDispatchTable());

		// set current class name
		currentClassName = icClass.getName();
		
		// recursive calls to methods
		for(Method m: icClass.getMethods()){
			m.accept(this,0);
			// each method will be responsible to insert its string rep. to the methods list
		}

		// fields: no need for recursive calls
		
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,"");
	}

	/**
	 * Field propagating visitor: never called
	 */
	public LIRUpType visit(Field field, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,"");
	}

	/**
	 * VirtualMethod propagating visitor:
	 * see methodVisitHelper documentation
	 * @param method
	 * @param d
	 * @return
	 */
	public LIRUpType visit(VirtualMethod method, Integer d){
		methodVisitHelper(method, d);
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,"");
	}

	/**
	 * StaticMethod propagating visitor:
	 * see methodVisitHelper documentation
	 * @param method
	 * @param d
	 * @return
	 */
	public LIRUpType visit(StaticMethod method, Integer d){
		methodVisitHelper(method, d);
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,"");
	}
	
	/**
	 * Virtual / Static method visitor helper
	 * - creates LIR representation for the method code and updates methods list
	 * - includes recursive calls to all method's statements
	 * @param method
	 * @param d
	 * @return
	 */
	public LIRUpType methodVisitHelper(Method method, Integer d){
		String methodLIRCode = "";
		
		// create method label
		String methodLabel = "_";
		methodLabel += ((ClassSymbolTable) method.getEnclosingScope()).getMySymbol().getName();
		methodLabel += "_"+method.getName();
		
		methodLIRCode += methodLabel+":\n";
		
		// insert method's code recursively
		for (Statement s: method.getStatements()){
			methodLIRCode += s.accept(this,0);
		}
		
		// update methods list
		methods.add(methodLIRCode);
		
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,"");
	}

	/**
	 * LibraryMethod propagating visitor:
	 * does nothing since its LIR implementation is provided externally
	 */
	public LIRUpType visit(LibraryMethod method, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,"");
	}

	/**
	 * Formal propagating visitor: never called
	 */
	public LIRUpType visit(Formal formal, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,"");
	}

	/**
	 * PrimitiveType propagating visitor: never called
	 */
	public LIRUpType visit(PrimitiveType type, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,"");
	}

	/**
	 * UserType propagating visitor: never called
	 */
	public LIRUpType visit(UserType type, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,"");
	}

	/**
	 * Assignment propagating visitor:
	 * - translate recursively the variable and the assignment
	 * - concatenate the translations to the LIR assignment instruction
	 */
	public LIRUpType visit(Assignment assignment, Integer d){
		String tr = "";
		
		// translate variable
		LIRUpType var = assignment.getVariable().accept(this, d);
		String varReg = var.getTargetRegister();
		
		// translate assignment
		int assign_d = d + assignment.getVariable().getRequiredRegs();
		LIRUpType assign = assignment.getAssignment().accept(this, assign_d);
		String assignReg = assign.getTargetRegister();
		
		// insert assignment and variable LIR code
		tr += assign.getLIRCode()+var.getLIRCode();
		
		// handle all variable cases
		switch (var.getLIRInstType()){
		case LOC_VAR_LOCATION:
			// doesn't use any registers
			tr += "Move ";
			break;
		case EXT_VAR_LOCATION:
			// uses one register
			tr += "MoveField ";
			break;
		case ARR_LOCATION:
			tr += "MoveArray ";
			break;
		default:
			System.err.println("*** BUG: TranslatePropagatingVisitor Assignment: unhandled LIR instruction type");
		}
		tr += assignReg+","+varReg+"\n";
		
		return new LIRUpType(tr, LIRFlagEnum.EXPLICIT,"");
	}

	/**
	 * returns the ASTNode Field for the given field name,
	 * starting the search from the given ICClass.
	 * returns null if didn't find it (not supposed to happen).
	 * @param icClass
	 * @param fieldName
	 * @return
	 */
	public Field getFieldASTNodeRec(ICClass icClass, String fieldName){
		for(Field f: icClass.getFields()){
			if (f.getName().equals(fieldName))
				return f;
		}
		if (icClass.hasSuperClass()){
			return getFieldASTNodeRec(global.getClass(icClass.getSuperClassName()).getIcClass(), fieldName);
		} else
			System.err.println("*** BUG: TranslatePropagatingVisitor getFieldASTNodeRec bug");
		return null;
	}
	
	/**
	 * VariableLocation propagating visitor:
	 * - translate recursively the location
	 * - concatenate the translations to the LIR location update instruction
	 */
	public LIRUpType visit(VariableLocation location, Integer d){
		String tr = "";
		
		if (location.isExternal()){
			// translate the location
			LIRUpType loc = location.getLocation().accept(this, d);
			// add code to translation
			tr += loc.getLIRCode();
			
			// get the ClassLayout for the location
			IC.TypeTable.Type locationClassType = 
				(IC.TypeTable.Type)location.getLocation().accept(new IC.Visitors.DefTypeSemanticChecker(global));
			ClassLayout locationClassLayout = classLayouts.get(locationClassType.getName());
			
			// get the field offset for the variable
			Field f = getFieldASTNodeRec(locationClassLayout.getICClass(), location.getName());
			
			// get the field offset
			int fieldOffset = locationClassLayout.getFieldOffset(f);
			
			// translate this step
			switch(loc.getLIRInstType()){
			case LOC_VAR_LOCATION:
				tr += "Move ";
				break;
			case EXT_VAR_LOCATION:
				tr += "MoveField ";
				break;
			case ARR_LOCATION:
				tr += "MoveArray ";
				break;
			default:
				System.err.println("*** BUG: TranslatePropagatingVisitor VariableLocation: unhandled LIR instruction type");	
			}
			String locReg = "R"+d;
			tr += loc.getTargetRegister()+","+locReg+"\n";
			
			return new LIRUpType(tr, LIRFlagEnum.EXT_VAR_LOCATION, locReg+"."+fieldOffset);
		}else{
			// translate only the variable name
			return new LIRUpType("",LIRFlagEnum.LOC_VAR_LOCATION,location.getName());
		}
	}

	/**
	 * ArrayLocation propagating visitor:
	 * - translate recursively the array and the index
	 * - concatenate the translations to the LIR array location update instruction
	 */
	public LIRUpType visit(ArrayLocation location, Integer d){
		String tr = "";
		
		// translate array
		LIRUpType array = location.getArray().accept(this, d);
		tr += array.getLIRCode();
		
		// move result to a single register
		switch (array.getLIRInstType()){
		case ARR_LOCATION:
			tr += "MoveArray ";
			break;
		case LOC_VAR_LOCATION:
			tr += "Move ";
			break;
		case EXT_VAR_LOCATION:
			tr += "MoveField ";
			break;
		default:
			System.err.println("*** BUG: TranslatePropagatingVisitor ArrayLocation: unhandled LIR instruction type");
		}
		tr += array.getTargetRegister()+",R"+d+"\n";
		
		// translate index
		LIRUpType index = location.getIndex().accept(this, d+1);
		tr += index.getLIRCode();
		
		// move result to a single register
		switch (index.getLIRInstType()){
		case IMMEDIATE:
			tr += "Move ";
			break;
		case LOC_VAR_LOCATION:
			tr += "Move ";
			break;
		case EXT_VAR_LOCATION:
			tr += "MoveField ";
			break;
		case ARR_LOCATION:
			tr += "MoveArray ";
			break;
		default:
			System.err.println("*** BUG: TranslatePropagatingVisitor ArrayLocation: unhandled LIR instruction type");
		}
		tr += index.getTargetRegister()+",R"+(d+1)+"\n";
		
		return new LIRUpType(tr, LIRFlagEnum.ARR_LOCATION,"R"+d+"[R"+(d+1)+"]");
	}

	public LIRUpType visit(CallStatement callStatement, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(Return returnStatement, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(If ifStatement, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(While whileStatement, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(Break breakStatement, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(Continue continueStatement, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(StatementsBlock statementsBlock, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(LocalVariable localVariable, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(StaticCall call, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(VirtualCall call, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(This thisExpression, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(NewClass newClass, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(NewArray newArray, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(Length length, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(MathBinaryOp binaryOp, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(LogicalBinaryOp binaryOp, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(MathUnaryOp unaryOp, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(LogicalUnaryOp unaryOp, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(Literal literal, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}

	public LIRUpType visit(ExpressionBlock expressionBlock, Integer d){
		return new LIRUpType("", LIRFlagEnum.EXPLICIT,""); //TODO update
	}
	
	// getters and setters
	//////////////////////

	public int getStringLiteralsCounter() {
		return stringLiteralsCounter;
	}

	public void setStringLiteralsCounter(int stringLiteralsCounter) {
		this.stringLiteralsCounter = stringLiteralsCounter;
	}

	public Map<String,String> getStringLiterals() {
		return stringLiterals;
	}

	public void setStringLiterals(HashMap<String,String> stringLiterals) {
		this.stringLiterals = stringLiterals;
	}

	public List<String> getClassDispatchTable() {
		return classDispatchTable;
	}

	public void setClassDispatchTable(List<String> classDispatchTable) {
		this.classDispatchTable = classDispatchTable;
	}

	public List<String> getMethods() {
		return methods;
	}

	public void setMethods(List<String> methods) {
		this.methods = methods;
	}

	public String getMainMethod() {
		return mainMethod;
	}

	public void setMainMethod(String mainMethod) {
		this.mainMethod = mainMethod;
	}
}
