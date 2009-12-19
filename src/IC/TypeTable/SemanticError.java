package IC.TypeTable;

/**
 * The error class thrown by the parser in case of a semantic error
 * this exception class contains the exception line number, a message and the token
 * that caused the error
 */
public class SemanticError extends Exception {
	private static final long serialVersionUID = -4099339233866617388L;
	
	private int line;
	private String value;
	
	public SemanticError(String message, int line, String value){
		super(message);
		this.line = line;
		this.value = value;
	}
	
	public SemanticError(String message, String value){
		super(message);
		this.line = -1;
		this.value = value;
	}
	
	 /**
     * returns a string representation for the error with line number, message and the token that
     * caused the syntax error
     */
	public String toString(){
		return "Semantic error at line "+line+": "+this.getMessage()+": "+value;
	}
	
	/**
	 * set the error's line
	 * @param line
	 */
	public void setLine(int line){
		this.line = line;
	}
}
