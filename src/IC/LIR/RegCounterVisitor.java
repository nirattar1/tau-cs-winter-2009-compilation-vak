package IC.LIR;

import IC.AST.*;

/**
 * Visitor to update the number of registers used in each AST node
 * for the Setti-Ulman optimization
 */
public class RegCounterVisitor implements Visitor {

	@Override
	/**
	 * Program visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(Program program) {
		int maxRequired = 0;
		for (ICClass c: program.getClasses()){
			// update maximum required registers
			int classReq = (Integer) c.accept(this);
			maxRequired = Math.max(classReq, maxRequired);
		}
		program.setRequiredRegs(maxRequired);
		return maxRequired;
	}

	@Override
	/**
	 * ICClass visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(ICClass icClass) {
		int maxRequired = 0;
		for (Method m: icClass.getMethods()){
			// update maximum required registers
			int methodReq = (Integer) m.accept(this);
			maxRequired = Math.max(methodReq, maxRequired);
		}
		icClass.setRequiredRegs(maxRequired);
		return maxRequired;
	}

	@Override
	/**
	 * Field visitor: always 0
	 */
	public Object visit(Field field) {
		field.setRequiredRegs(0);
		return 0;
	}

	@Override
	/**
	 * VirtualMethod visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(VirtualMethod method) {
		return methodsHelper(method);
	}

	@Override
	/**
	 * VirtualMethod visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(StaticMethod method) {
		return methodsHelper(method);
	}
	
	/**
	 * method visit helper for static and virtual methods
	 * @param method
	 * @return
	 */
	public Object methodsHelper(Method method){
		int maxRequired = 0;
		for (Statement s: method.getStatements()){
			// update maximum required registers
			int statReq = (Integer) s.accept(this);
			maxRequired = Math.max(statReq, maxRequired);
		}
		method.setRequiredRegs(maxRequired);
		return maxRequired;
	}

	@Override
	/**
	 * LibraryMethod visitor: always 0
	 */
	public Object visit(LibraryMethod method) {
		method.setRequiredRegs(0);
		return 0;
	}

	@Override
	/**
	 * Formal visitor: always 0
	 */
	public Object visit(Formal formal) {
		formal.setRequiredRegs(0);
		return 0;
	}

	@Override
	/**
	 * PrimitiveType visitor: always 0
	 */
	public Object visit(PrimitiveType type) {
		type.setRequiredRegs(0);
		return 0;
	}

	@Override
	/**
	 * UserType visitor: always 0
	 */
	public Object visit(UserType type) {
		type.setRequiredRegs(0);
		return 0;
	}

	@Override
	/**
	 * VirtualMethod visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(Assignment assignment) {
		int res = getSettiUlmanVal(assignment.getAssignment().accept(this),
									assignment.getVariable().accept(this));
		assignment.setRequiredRegs(res);
		return res;
		
	}

	@Override
	/**
	 * CallStatement visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(CallStatement callStatement) {
		int res = (Integer)callStatement.getCall().accept(this);
		callStatement.setRequiredRegs(res);
		return res;
	}

	@Override
	/**
	 * Return visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(Return returnStatement) {
		int res = (Integer)returnStatement.getValue().accept(this);
		returnStatement.setRequiredRegs(res);
		return res;
	}

	@Override
	/**
	 * If visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(If ifStatement) {
		int res = Math.max((Integer)ifStatement.getCondition().accept(this),
							(Integer)ifStatement.getOperation().accept(this));
		if (ifStatement.hasElse()){
			int elseRes = (Integer)ifStatement.accept(this);
			res = Math.max(res, elseRes);
		}
		
		ifStatement.setRequiredRegs(res);
		return res;
	}

	@Override
	/**
	 * While visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(While whileStatement) {
		int res = Math.max((Integer)whileStatement.getCondition().accept(this),
							(Integer)whileStatement.getOperation().accept(this));
		whileStatement.setRequiredRegs(res);
		return res;
	}

	@Override
	/**
	 * Break visitor: always 0
	 */
	public Object visit(Break breakStatement) {
		breakStatement.setRequiredRegs(0);
		return 0;
	}

	@Override
	/**
	 * Continue visitor: always 0
	 */
	public Object visit(Continue continueStatement) {
		continueStatement.setRequiredRegs(0);
		return 0;
	}

	@Override
	/**
	 * StatementsBlock visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(StatementsBlock statementsBlock) {
		int maxRequired = 0;
		for (Statement s: statementsBlock.getStatements()){
			// update maximum required registers
			int statReq = (Integer) s.accept(this);
			maxRequired = Math.max(statReq, maxRequired);
		}
		statementsBlock.setRequiredRegs(maxRequired);
		return maxRequired;
	}

	@Override
	/**
	 * LocalVariable visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
	public Object visit(LocalVariable localVariable) {
		int res = localVariable.hasInitValue() ?
					(Integer)localVariable.getInitValue().accept(this) : 0;
		localVariable.setRequiredRegs(res);
		return res;
	}

	@Override
	/**
	 * VariableLocation visitor:
	 * - get the maximum number of required registers
	 * - return the result
	 */
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
	
	/////////////
	// helpers //
	/////////////
	
	/**
	 * returns the number of registers used by the Setti-Ulman algorithm
	 * for the given 2 values
	 */
	public static int getSettiUlmanVal(Object node1, Object node2){
		int n1 = (Integer) node1;
		int n2 = (Integer) node2;
		if (n1 == n2) return n1 + 1;
		else return Math.max(n1, n2);
	}
	

}
