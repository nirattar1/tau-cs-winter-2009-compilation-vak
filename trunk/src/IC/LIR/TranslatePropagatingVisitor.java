package IC.LIR;

import IC.AST.*;
import IC.SymbolTable.*;
import IC.TypeTable.*;
import IC.LIR.LIRFlagEnum;
import java.util.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

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
	// counter for labels (if, while)
	private int labelCounter = 0;
	// identifier for current while
	private int currWhileID = -1;
	
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
		tr += getMoveCommand(var.getLIRInstType());
		tr += assignReg+","+varReg+"\n";
		
		return new LIRUpType(tr, LIRFlagEnum.STATEMENT,"");
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
			tr += getMoveCommand(loc.getLIRInstType());
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
		tr += getMoveCommand(array.getLIRInstType());
		tr += array.getTargetRegister()+",R"+d+"\n";
		
		// translate index
		LIRUpType index = location.getIndex().accept(this, d+1);
		tr += index.getLIRCode();
		
		// move result to a single register
		tr += getMoveCommand(index.getLIRInstType());
		tr += index.getTargetRegister()+",R"+(d+1)+"\n";
		
		return new LIRUpType(tr, LIRFlagEnum.ARR_LOCATION,"R"+d+"[R"+(d+1)+"]");
	}

	/**
	 * CallStatement propagating visitor:
	 * - translate recursively the call expression and return its translation
	 */
	public LIRUpType visit(CallStatement callStatement, Integer d){
		return callStatement.getCall().accept(this, d);
	}

	/**
	 * Return propagating visitor:
	 * - translate recursively the returned expression
	 * - concatenate the translations to the LIR return statement update instruction
	 */
	public LIRUpType visit(Return returnStatement, Integer d){
		String tr = "";
		LIRUpType returnVal = returnStatement.getValue().accept(this, d);
		tr += returnVal.getLIRCode();
		tr += "Return "+returnVal.getTargetRegister()+"\n";
		
		return new LIRUpType(tr, LIRFlagEnum.STATEMENT, "");
	}

	/**
	 * If propagating visitor:
	 * - translate recursively the condition, then statement and else statement
	 * - concatenate the translations to the LIR if statement update instruction
	 */
	public LIRUpType visit(If ifStatement, Integer d){
		String tr = "";
		String falseLabel = "_false_label"+labelCounter;
		String endLabel = "_end_label"+(labelCounter++);
		
		// recursive call the condition expression
		LIRUpType condExp = ifStatement.getCondition().accept(this, d);
		tr += getMoveCommand(condExp.getLIRInstType());
		tr += condExp.getTargetRegister()+",R"+d+"\n";
		
		// check condition
		tr += "Compare 0,R"+d+"\n";
		if (ifStatement.hasElse()) tr += "JumpTrue "+falseLabel+"\n";
		else tr += "JumpTrue "+endLabel+"\n";
		
		// recursive call to the then statement
		LIRUpType thenStat = ifStatement.getOperation().accept(this, d);
		tr += thenStat.getLIRCode();
		
		if (ifStatement.hasElse()){
			tr += "Jump "+endLabel+"\n";

			// recursive call to the else statement
			tr += falseLabel+":\n";
			LIRUpType elseStat = ifStatement.getElseOperation().accept(this, d);
			tr += elseStat.getLIRCode();
		}
		
		tr += endLabel+":\n";
		
		return new LIRUpType(tr, LIRFlagEnum.STATEMENT,"");
	}

	/**
	 * While propagating visitor:
	 * - translate recursively the condition, and then statement
	 * - concatenate the translations to the LIR while statement update instruction
	 */
	public LIRUpType visit(While whileStatement, Integer d){
		// save while id previous value and set current
		int prevWhileID = currWhileID;
		currWhileID = labelCounter;
		
		String tr = "";
		String whileLabel = "_while_cond_label"+labelCounter;
		String endLabel = "_end_label"+(labelCounter++);
		
		tr += whileLabel+":\n";
		// recursive call to condition
		LIRUpType condExp = whileStatement.getCondition().accept(this, d);
		tr += getMoveCommand(condExp.getLIRInstType());
		tr += condExp.getTargetRegister()+",R"+d+"\n";
		
		// check condition
		tr += "Compare 0,R"+d+"\n";
		tr += "JumpTrue "+endLabel+"\n";
		
		// recursive call to operation statement
		tr += whileStatement.getOperation().accept(this,d).getLIRCode();
		tr += "Jump "+whileLabel+"\n";
		tr += endLabel+":\n";
		
		// set while id back to previous value
		currWhileID = prevWhileID;
		return new LIRUpType(tr, LIRFlagEnum.STATEMENT,"");
	}

	/**
	 * Break propagating visitor:
	 * - return the break statement
	 */
	public LIRUpType visit(Break breakStatement, Integer d){
		String tr = "Jump _end_label"+currWhileID+"\n";
		return new LIRUpType(tr, LIRFlagEnum.STATEMENT,"");
	}

	/**
	 * Continue propagating visitor:
	 * - return the continue statement
	 */
	public LIRUpType visit(Continue continueStatement, Integer d){
		String tr = "Jump _while_cond_label"+currWhileID+"\n";
		return new LIRUpType(tr, LIRFlagEnum.STATEMENT,"");
	}

	/**
	 * StatementsBlock propagating visitor:
	 * - translate recursively all statements in the block
	 * - concatenate the translations to the LIR code
	 */
	public LIRUpType visit(StatementsBlock statementsBlock, Integer d){
		String tr = "";
		
		// recursive call to all statements in the block
		for (Statement s: statementsBlock.getStatements()){
			tr += s.accept(this, d).getLIRCode();
		}
		
		return new LIRUpType(tr, LIRFlagEnum.STATEMENT,"");
	}

	/**
	 * LocalVariable propagating visitor:
	 * - translate recursively the init value
	 * - concatenate the translations to the LIR local variable statement instruction
	 */
	public LIRUpType visit(LocalVariable localVariable, Integer d){
		String tr = "";
		
		if (localVariable.hasInitValue()){
			LIRUpType initVal = localVariable.getInitValue().accept(this, d);
			tr += initVal.getLIRCode();
			tr += getMoveCommand(initVal.getLIRInstType());
			tr += initVal.getTargetRegister()+",R"+d+"\n";
			// move register into the local var name
			tr += "Move R"+d+","+localVariable.getName()+"\n";
		}
		
		return new LIRUpType(tr, LIRFlagEnum.STATEMENT,"");
	}

	/**
	 * StaticCall propagating visitor:
	 * - translate recursively the list of arguments
	 * - concatenate the translations to the LIR static call statement instruction
	 */
	public LIRUpType visit(StaticCall call, Integer d){
		String tr = "";
		
		// recursive calls to all arguments
		int i = d;
		for (Expression arg: call.getArguments()){
			LIRUpType argExp = arg.accept(this, i);
			tr += "# argument #"+(i-d)+":\n";
			tr += argExp.getLIRCode();
			tr += getMoveCommand(argExp.getLIRInstType());
			tr += argExp.getTargetRegister()+",R"+i+"\n";
			// increment registers count
			i++;
		}
		
		// call statement
		ClassLayout thisClassLayout = classLayouts.get(call.getClassName());
		Method thisMethod = thisClassLayout.getMethodFromName(call.getName());
		tr += "# call statement:\n";
		// construct method label
		String methodName = "_"+((ClassSymbolTable) thisMethod.getEnclosingScope()).getMySymbol().getName()+
							"_"+call.getName();
		tr += "StaticCall "+methodName+"(";
		// insert <formal>=<argument register>
		for(i = 0; i < call.getArguments().size(); i++){
			tr += thisMethod.getFormals().get(i)+"=R"+(d+i)+",";
		}
		// remove last comma
		tr = tr.substring(0, tr.length()-1);
		tr += "),R"+d+"\n";
		
		return new LIRUpType(tr, LIRFlagEnum.STATEMENT,"");
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
	
	/**
	 * returns the correct move command for the given LIR flag enum
	 * @param type
	 * @return
	 */
	private String getMoveCommand(LIRFlagEnum type){
		switch(type){
		case IMMEDIATE: return "Move ";
		case LOC_VAR_LOCATION: return "Move ";
		case EXT_VAR_LOCATION: return "MoveField ";
		case ARR_LOCATION: return "MoveArray ";
		default:
			System.err.println("*** BUG: TranslatePropagatingVisitor: unhandled LIR instruction type");
			return null;
		}
	}
}
