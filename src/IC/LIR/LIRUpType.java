package IC.LIR;

public class LIRUpType {

	private String	LIRCode;
	private LIRFlagEnum LIRInstType;
	private String targetRegister;

	public LIRUpType(String lIRCode, LIRFlagEnum astType, String targetRegister) {
		super();
		this.LIRCode = lIRCode;
		this.LIRInstType = astType;
		this.targetRegister = targetRegister;
	}

	public String getLIRCode() {
		return LIRCode;
	}

	public LIRFlagEnum getLIRInstType() {
		return LIRInstType;
	}
	
	public String getTargetRegister() {
		return targetRegister;
	}
}
