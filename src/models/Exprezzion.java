package models;

import java.util.ArrayList;
import java.util.List;

public class Exprezzion {
	
	private String 				expression;
	private String 				methodCall;
	private List<Exprezzion> 	parameters;
	private String 				resolvedType;
	
	public Exprezzion() {
		parameters = new ArrayList<Exprezzion>();
		expression = "";
		methodCall = "";
		resolvedType = "";
	}

	public Exprezzion(String expression, String methodCall, List<Exprezzion> parameters, String resolvedType) {
		this.expression = expression;
		this.methodCall = methodCall;
		this.parameters = parameters;
		this.resolvedType = resolvedType;
	}
	
	public void print() {
		System.out.println("        " + "Expression: " + expression + " Method Call: " + methodCall + 
							" Resolved Type: " + resolvedType);
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

	public List<Exprezzion> getParameters() {
		return parameters;
	}

	public void setParameters(List<Exprezzion> parameters) {
		this.parameters = parameters;
	}

	public String getResolvedType() {
		return resolvedType;
	}

	public void setResolvedType(String resolvedType) {
		this.resolvedType = resolvedType;
	}
}
