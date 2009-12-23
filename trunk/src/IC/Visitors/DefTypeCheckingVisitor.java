package IC.Visitors;

import IC.AST.*;
import IC.TypeTable.*;
import IC.SymbolTable.*;

/**
 * Visitor for resolving the following issues:
 * - check illegal use of undefined symbols
 * - Type checks
 */
public class DefTypeCheckingVisitor implements Visitor {
	private IC.SymbolTable.GlobalSymbolTable global;
	
	/**
	 * constructor
	 * @param global: the program's global symbol table
	 */
	public DefTypeCheckingVisitor(IC.SymbolTable.GlobalSymbolTable global){
		this.global = global;
	}

	@Override
	/**
	 * Program Visitor:
	 * - recursive calls to all classes
	 * returns null if encountered an error, true otherwise
	 */
	public Object visit(Program program) {
		// recursive call to class 
		for(ICClass c: program.getClasses()){
			if (c.accept(this) == null) return null;
		}
		return true;
	}

	@Override
	/**
	 * ICClass Visitor:
	 * - recursive calls to all methods
	 * returns null if encountered an error, true otherwise
	 */
	public Object visit(ICClass icClass) {
		// by now all fields are defined legally
		// check only methods
		for(Method m: icClass.getMethods()){
			if (m.accept(this) == null) return null;
		}
		return true;
	}

	@Override
	/**
	 * Field visitor: never called
	 */
	public Object visit(Field field) {
		return true;
	}
	
	/**
	 * Method Visitor:
	 * - recursive calls to all statements (used by static, virtual and library method)
	 * returns null if encountered an error, true otherwise
	 */
	public Object methodVisitHelper(Method method){
		// recursive call to all statements in method
		for(Statement s: method.getStatements()){
			if (s.accept(this) == null) return null;
		}
		return true;
	}

	@Override
	/**
	 * VirtualMethod visitor: see methodVisitHelper documentation
	 */
	public Object visit(VirtualMethod method) {
		return methodVisitHelper(method);
	}

	@Override
	/**
	 * StaticMethod visitor: see methodVisitHelper documentation
	 */
	public Object visit(StaticMethod method) {
		return methodVisitHelper(method);
	}

	@Override
	/**
	 * LibraryMethod visitor: see methodVisitHelper documentation
	 */
	public Object visit(LibraryMethod method) {
		return methodVisitHelper(method);
	}

	@Override
	/**
	 * Formal visitor: never called
	 */
	public Object visit(Formal formal) {
		return true;
	}

	@Override
	/**
	 * PrimitiveType visitor: never called
	 */
	public Object visit(PrimitiveType type) {
		return true;
	}

	@Override
	 /**
	  * UserType visitor: never called
	  */
	public Object visit(UserType type) {
		return true;
	}

	@Override
	/**
	 * Assignment visitor:
	 * - recursive calls to location and assignment
	 * - type check: check that the assignment type <= location type
	 * returns null if encountered an error, true otherwise
	 */
	public Object visit(Assignment assignment) {
		// check location recursively
		IC.TypeTable.Type locationType = (IC.TypeTable.Type) assignment.getVariable().accept(this);
		if (locationType == null) return null;
		// check assignment recursively
		IC.TypeTable.Type assignmentType = (IC.TypeTable.Type) assignment.getAssignment().accept(this);
		if (assignmentType == null) return null;
		
		// type check
		// check that the assignment is of the same type / subtype of the location type
		if (!assignmentType.subtypeOf(locationType)){
			System.err.println(new SemanticError("type mismatch, not of type "+locationType.getName(),
					assignment.getLine(),
					assignmentType.getName()));
			return null;
		}
		
		return true;
	}

	@Override
	/**
	 * CallStatement visitor:
	 * - recursive calls to call
	 * returns null if encountered an error, true otherwise
	 */
	public Object visit(CallStatement callStatement) {
		if (callStatement.getCall().accept(this) == null) return null;
		else return true;
	}

	@Override
	/**
	 * returnStatement visitor:
	 * - recursive call to call (static or virtual call)
	 * - type check: check that the returned call type <= enclosing method's type
	 * returns null if encountered an error, true otherwise
	 */
	public Object visit(Return returnStatement) {
		// check return statement recursively
		IC.TypeTable.Type returnedValueType = null; // dummy initialization
		if (returnStatement.hasValue()){
			returnedValueType = (IC.TypeTable.Type) returnStatement.getValue().accept(this);
			if (returnedValueType == null) return null;
		} else try{
			returnedValueType = TypeTable.getType("void");
		} catch(SemanticError se){System.err.println("*** BUG: DefTypeCheckingVisitor, Return visitor");} // will never get here
		
		// type check
		// check that the return type is the same type / subtype of the enclosing method's type
		try{
			IC.TypeTable.Type returnType = ((BlockSymbolTable) returnStatement.getEnclosingScope()).getVarSymbolRec("_ret").getType();
			if (!returnedValueType.subtypeOf(returnType)){
				System.err.println(new SemanticError("type mismatch, not of type "+returnType.getName(),
						returnStatement.getLine(),
						returnedValueType.getName()));
				return null;
			}
		} catch (SemanticError se){System.err.println("*** BUG: DefTypeCheckingVisitor, Return visitor");} // will never get here
		
		return true;
	}

	@Override
	/**
	 * If visitor:
	 * - recursive calls condition, operation and elseOperation
	 * - type check: check that the condition type is of type boolean
	 * returns null if encountered an error, true otherwise
	 */
	public Object visit(If ifStatement) {
		// check condition recursively
		IC.TypeTable.Type conditionType = (IC.TypeTable.Type) ifStatement.getCondition().accept(this);
		if (conditionType == null) return null;
		
		// type check
		// check that the condition is of type boolean
		try{
			if (!conditionType.subtypeOf(TypeTable.getType("boolean"))){
				System.err.println(new SemanticError("condition in if statement not of type boolean",
						ifStatement.getCondition().getLine(),
						conditionType.getName()));
				return null;
			}
		} catch (SemanticError se){System.err.println("*** BUG: DefTypeCheckingVisitor, If visitor");} // will never get here
		
		// check operation, elseOperation recursively
		if (ifStatement.getOperation().accept(this) == null) return null;
		if (ifStatement.hasElse()){
			if (ifStatement.getElseOperation().accept(this) == null) return null;
		}
		
		return true;
	}

	@Override
	/**
	 * While visitor:
	 * - recursive calls condition and operation
	 * - type check: check that the condition type is of type boolean
	 * returns null if encountered an error, true otherwise
	 */
	public Object visit(While whileStatement) {
		// check condition recursively
		IC.TypeTable.Type conditionType = (IC.TypeTable.Type) whileStatement.getCondition().accept(this);
		if (conditionType == null) return null;
		
		// type check
		// check that the condition is of type boolean
		try{
			if (!conditionType.subtypeOf(TypeTable.getType("boolean"))){
				System.err.println(new SemanticError("condition in while statement not of type boolean",
						whileStatement.getCondition().getLine(),
						conditionType.getName()));
				return null;
			}
		} catch (SemanticError se){System.err.println("*** BUG: DefTypeCheckingVisitor, While visitor");} // will never get here
		
		// check operation recursively
		if (whileStatement.getOperation().accept(this) == null) return null;
		
		return true;
	}

	@Override
	/**
	 * Break visitor: does nothing
	 */
	public Object visit(Break breakStatement) {
		return true;
	}

	@Override
	/**
	 * Continue visitor: does nothing
	 */
	public Object visit(Continue continueStatement) {
		return true;
	}

	@Override
	/**
	 * StatementsBlock visitor:
	 * - recursive calls to all statements
	 * returns null if encountered an error, true otherwise
	 */
	public Object visit(StatementsBlock statementsBlock) {
		// recursive call to all statements
		for(Statement s: statementsBlock.getStatements()){
			if (s.accept(this) == null) return null;
		}
		return true;
	}

	@Override
	/**
	 * LocalVariable visitor:
	 * - recursive call to initValue (if exists)
	 * - type check: check that the initValue type is a subtype of the local variable's type
	 * returns null if encountered an error, true otherwise
	 */
	public Object visit(LocalVariable localVariable) {
		// recursive call to initValue
		if (localVariable.hasInitValue()){
			IC.TypeTable.Type initValueType = (IC.TypeTable.Type) localVariable.getInitValue().accept(this);
			if (initValueType == null) return null;
			
			try{
				// type check
				// check that the initValue type is a subtype of the local variable's type
				IC.TypeTable.Type localVariableType = ((BlockSymbolTable) localVariable.getEnclosingScope()).getVarSymbol(localVariable.getName()).getType();
			
				if (!initValueType.subtypeOf(localVariableType)){
					System.err.println(new SemanticError("type mismatch, not of type "+localVariableType.getName(),
							localVariable.getLine(),
							initValueType.getName()));
					return null;
				}
			} catch (SemanticError se){System.err.println("*** BUG: DefTypeCheckingVisitor, LocalVariable visitor");} // will never get here
		}
		
		return true;
	}

	@Override
	/**
	 * VariableLocation visitor:
	 * - recursive call to location (if exists)
	 * returns null if encountered an error, and the location type otherwise
	 */
	public Object visit(VariableLocation location) {
		// recursive call to location (if exists)
		if (location.isExternal()){
			IC.TypeTable.Type locationType = (IC.TypeTable.Type) location.getLocation().accept(this);
			if (locationType == null) return null;
			// check if the location is a class type
			try{
				TypeTable.getClassType(locationType.getName());
				// if location is a class, check that it has a field with this name
				IC.SymbolTable.ClassSymbolTable cst = this.global.getClassSymbolTable(locationType.getName());
				try{
					IC.SymbolTable.FieldSymbol fs = cst.getFieldSymbolRec(location.getName());
					// return the type of this field
					return fs.getType(); // this line will never throw error
				} catch(SemanticError se){ // the external location has no field with this name 
					se.setLine(location.getLine());
					System.err.println(se);
					return null;
				}
			} catch(SemanticError se){ // in case the external location is not a user defined class 
				System.err.println(new SemanticError("location of type "+locationType.getName()+" does not have field",
						location.getLine(),
						location.getName()));
				return null;
			}
		} else { // this location is not external
			try{
				IC.TypeTable.Type thisLocationType = ((BlockSymbolTable) location.getEnclosingScope()).getVarSymbolRec(location.getName()).getType();
				return thisLocationType;
			} catch(SemanticError se){ // in case this location is not defined
				se.setLine(location.getLine());
				System.err.println(se);
				return null;
			}
		}
	}

	@Override
	/**
	 * ArrayLocation visitor:
	 * - recursive call to array and index
	 * returns null if encountered an error, and the array[index] type otherwise
	 */
	public Object visit(ArrayLocation location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StaticCall call) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VirtualCall call) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(This thisExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewClass newClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewArray newArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Length length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathBinaryOp binaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalBinaryOp binaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MathUnaryOp unaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LogicalUnaryOp unaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Literal literal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ExpressionBlock expressionBlock) {
		// TODO Auto-generated method stub
		return null;
	}

}
