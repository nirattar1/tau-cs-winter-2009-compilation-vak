package IC.LIR;

import IC.AST.*;
import IC.SymbolTable.ClassSymbolTable;

import java.util.*;

/**
 * Translating visitor to LIR
 */
public class TranslatePropagatingVisitor implements PropagatingVisitor<Integer, String>{

	// string literals counter
	private int stringLiteralsCounter = 0;
	// string literals list, each element in the format: 'str<i>: "<string>"'
	private List<String> stringLiterals = new ArrayList<String>();
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
	public String visit(Program program, Integer d){
		// visit all classes recursively
		for(ICClass c: program.getClasses()){
			c.accept(this, 0);
		}
		
		// return LIR representation for the IC program
		String lirBuffer = "";
		
		// (1) insert all string literals
		lirBuffer += "# string literals\n";
		for (String strLiteral: this.stringLiterals){
			lirBuffer += strLiteral+"\n";
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
		
		return lirBuffer;
	}

	/**
	 * ICClass propagating visitor:
	 * - updates class dispatch tables
	 * - recursive calls to all methods in the class
	 * @param icClass
	 * @param d
	 * @return
	 */
	public String visit(ICClass icClass, Integer d){
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

		// recursive calls to methods
		for(Method m: icClass.getMethods()){
			m.accept(this,0);
			// each method will be responsible to insert its string rep. to the methods list
		}

		// fields: no need for recursive calls
		
		return "";
	}

	/**
	 * Field propagating visitor: never called
	 */
	public String visit(Field field, Integer d){
		return "";
	}

	/**
	 * VirtualMethod propagating visitor:
	 * see methodVisitHelper documentation
	 * @param method
	 * @param d
	 * @return
	 */
	public String visit(VirtualMethod method, Integer d){
		methodVisitHelper(method, d);
		return "";
	}

	/**
	 * StaticMethod propagating visitor:
	 * see methodVisitHelper documentation
	 * @param method
	 * @param d
	 * @return
	 */
	public String visit(StaticMethod method, Integer d){
		methodVisitHelper(method, d);
		return "";
	}
	
	/**
	 * Virtual / Static method visitor helper
	 * - creates LIR representation for the method code and updates methods list
	 * - includes recursive calls to all method's statements
	 * @param method
	 * @param d
	 * @return
	 */
	public String methodVisitHelper(Method method, Integer d){
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
		
		return "";
	}

	/**
	 * LibraryMethod propagating visitor:
	 * does nothing since its LIR implementation is provided externally
	 */
	public String visit(LibraryMethod method, Integer d){
		return "";
	}

	/**
	 * Formal propagating visitor: never called
	 */
	public String visit(Formal formal, Integer d){
		return "";
	}

	/**
	 * PrimitiveType propagating visitor: never called
	 */
	public String visit(PrimitiveType type, Integer d){
		return "";
	}

	/**
	 * UserType propagating visitor: never called
	 */
	public String visit(UserType type, Integer d){
		return "";
	}

	
	public String visit(Assignment assignment, Integer d){
		return ""; //TODO update
	}

	public String visit(CallStatement callStatement, Integer d){
		return ""; //TODO update
	}

	public String visit(Return returnStatement, Integer d){
		return ""; //TODO update
	}

	public String visit(If ifStatement, Integer d){
		return ""; //TODO update
	}

	public String visit(While whileStatement, Integer d){
		return ""; //TODO update
	}

	public String visit(Break breakStatement, Integer d){
		return ""; //TODO update
	}

	public String visit(Continue continueStatement, Integer d){
		return ""; //TODO update
	}

	public String visit(StatementsBlock statementsBlock, Integer d){
		return ""; //TODO update
	}

	public String visit(LocalVariable localVariable, Integer d){
		return ""; //TODO update
	}

	public String visit(VariableLocation location, Integer d){
		return ""; //TODO update
	}

	public String visit(ArrayLocation location, Integer d){
		return ""; //TODO update
	}

	public String visit(StaticCall call, Integer d){
		return ""; //TODO update
	}

	public String visit(VirtualCall call, Integer d){
		return ""; //TODO update
	}

	public String visit(This thisExpression, Integer d){
		return ""; //TODO update
	}

	public String visit(NewClass newClass, Integer d){
		return ""; //TODO update
	}

	public String visit(NewArray newArray, Integer d){
		return ""; //TODO update
	}

	public String visit(Length length, Integer d){
		return ""; //TODO update
	}

	public String visit(MathBinaryOp binaryOp, Integer d){
		return ""; //TODO update
	}

	public String visit(LogicalBinaryOp binaryOp, Integer d){
		return ""; //TODO update
	}

	public String visit(MathUnaryOp unaryOp, Integer d){
		return ""; //TODO update
	}

	public String visit(LogicalUnaryOp unaryOp, Integer d){
		return ""; //TODO update
	}

	public String visit(Literal literal, Integer d){
		return ""; //TODO update
	}

	public String visit(ExpressionBlock expressionBlock, Integer d){
		return ""; //TODO update
	}
	
	// getters and setters
	//////////////////////

	public int getStringLiteralsCounter() {
		return stringLiteralsCounter;
	}

	public void setStringLiteralsCounter(int stringLiteralsCounter) {
		this.stringLiteralsCounter = stringLiteralsCounter;
	}

	public List<String> getStringLiterals() {
		return stringLiterals;
	}

	public void setStringLiterals(List<String> stringLiterals) {
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
