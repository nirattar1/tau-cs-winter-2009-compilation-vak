package IC.LIR;

import IC.AST.*;

/**
 * Translating visitor to LIR
 */
public class TranslatePropagatingVisitor implements PropagatingVisitor<Integer, String>{

	public String visit(Program program, Integer d){
		return ""; //TODO update
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
}
