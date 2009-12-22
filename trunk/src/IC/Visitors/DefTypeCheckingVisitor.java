package IC.Visitors;

import IC.AST.*;

/**
 * Visitor for resolving the following issues:
 * - check illegal use of undefined symbols
 * - Type checks
 */
public class DefTypeCheckingVisitor implements Visitor {

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
	public Object visit(Assignment assignment) {
		// check location recursively
		IC.TypeTable.Type locationType = (IC.TypeTable.Type) assignment.getVariable().accept(this);
		if (locationType == null) return null;
		// check assignment recursively
		IC.TypeTable.Type assignmentType = (IC.TypeTable.Type) assignment.getAssignment().accept(this);
		if (assignmentType == null) return null;
		
		// type check
		
		
		return null;
	}

	@Override
	public Object visit(CallStatement callStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Return returnStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(If ifStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(While whileStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Break breakStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Continue continueStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StatementsBlock statementsBlock) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VariableLocation location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
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
