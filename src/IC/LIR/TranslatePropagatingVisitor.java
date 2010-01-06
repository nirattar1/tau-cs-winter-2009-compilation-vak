package IC.LIR;

import IC.AST.*;
import java.util.*;

/**
 * Translating visitor to LIR
 */
public class TranslatePropagatingVisitor implements PropagatingVisitor<Integer, String>{

	// string literals counter
	private int stringLiteralsCounter = 0;
	// string literals list, each element in the format: 'str<i>: "<string>"'
	private List<String> stringLiterals = new ArrayList<String>();
	// class dispatch tables, each element in the format: '_DV_<class name>: [<method1>,<method2>,...]'
	private List<String> classDispatchTable = new ArrayList<String>();
	// methods procedural code string representation
	private List<String> methods = new ArrayList<String>();
	// main method string representation
	private String mainMethod = "";
	
	
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

	public String visit(ICClass icClass, Integer d){
		return ""; //TODO update
	}

	public String visit(Field field, Integer d){
		return ""; //TODO update
	}

	public String visit(VirtualMethod method, Integer d){
		return ""; //TODO update
	}

	public String visit(StaticMethod method, Integer d){
		return ""; //TODO update
	}

	public String visit(LibraryMethod method, Integer d){
		return ""; //TODO update
	}

	public String visit(Formal formal, Integer d){
		return ""; //TODO update
	}

	public String visit(PrimitiveType type, Integer d){
		return ""; //TODO update
	}

	public String visit(UserType type, Integer d){
		return ""; //TODO update
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
