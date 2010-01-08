package IC.LIR;

public class LIRUpType {

	private String	LIRCode;
	private LIRFlagEnum LIRInstType;
	
	public LIRUpType(String lIRCode, LIRFlagEnum astType) {
		super();
		LIRCode = lIRCode;
		this.LIRInstType = astType;
	}

	public String getLIRCode() {
		return LIRCode;
	}

	public LIRFlagEnum getLIRInstType() {
		return LIRInstType;
	} 
}
