package IC.Parser;

/**
 * The error class thrown by the lexer in case of a lexical error
 * this exception class contains the exception line number, a message and the character
 * that caused the error
 */
public class LexicalError extends Exception
{
	private String message;
	private int line;
	private String value;

	public LexicalError(String message) {
    	super(message);
    	this.message = message;
    }
    
    public LexicalError(String message, int line, String value){
    	super(message);
    	this.message = message;
    	this.line = ++line;
    	this.value = value;
    }
    
    /**
     * in case there is a value to be passed, passes a concatenation of line number,
     * message and value. Else returns only line number and message.
     */
    public String toString(){
    	if (this.value == null) return this.line+": "+this.message;
    	else return this.line+": "+this.message+" '"+this.value+"'";
    }
}

