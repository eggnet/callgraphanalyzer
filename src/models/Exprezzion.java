package models;

public class Exprezzion {
	
	private String expression;
	private String methodCall;
	private String resolvedType;
	
	public Exprezzion() {
		
	}

	public Exprezzion(String expression, String methodCall, String resolvedType) {
		this.expression = expression;
		this.methodCall = methodCall;
		this.resolvedType = resolvedType;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getMethodCall() {
		return methodCall;
	}

	public void setMethodCall(String methodCall) {
		this.methodCall = methodCall;
	}

	public String getResolvedType() {
		return resolvedType;
	}

	public void setResolvedType(String resolvedType) {
		this.resolvedType = resolvedType;
	}
}
