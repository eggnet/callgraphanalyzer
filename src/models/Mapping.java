package models;

/**
 * Class for a mapping of a variable
 * currently it doesn't actually need to be used, as we only have these two
 * parameters, but I expect we may need to include more later.
 * @author braden
 *
 */
public class Mapping {
	private String Type;
	private String VarName;
	public Mapping(String type, String var) {
		this.Type = type;
		this.VarName = var;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getVarName() {
		return VarName;
	}
	public void setVarName(String varName) {
		VarName = varName;
	}
}
