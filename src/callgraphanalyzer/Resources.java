package callgraphanalyzer;


public class Resources {
	public static final int ANALYZER_MAX_DEPTH = 1;
	public static boolean isLiteral(String literal) {
		if(literal.equals("String") || literal.equals("int") || literal.equals("long") ||
				literal.equals("short") || literal.equals("byte") || literal.equals("double") ||
				literal.equals("float") || literal.equals("boolean") || literal.equals("char") ||
				literal.equals("null"))
			return true;
		else
			return false;
	}
	
	public static boolean isGeneric(String type) {
		if(type.contains("<") && type.contains(">"))
			return true;
		else
			return false;
	}
	
	public static int findTypeDivider(String methodName) {
		int index = -1;
		for(int i = 0; i < methodName.length(); i++) {
			if(methodName.charAt(i) == '.')
				index = i;
			if(methodName.charAt(i) == '(')
				break;
		}
		return index;
	}
	
	public static int convertLineStartToCharStart(int lineStart, String file) {
		int index = -1;
		int lineCount = 1;
		
		for(char c: file.toCharArray()) {
			if(c == '\n')
				lineCount++;
			else if(lineCount == lineStart) {
				index++;
				break;
			}
			index++;
		}
		
		return index;
	}
	
	public static int convertLineEndToCharEnd(int lineEnd, String file) {
		int index = -1;
		int lineCount = 1;
		
		for(char c: file.toCharArray()) {
			if(c == '\n' && lineCount == lineEnd) {
				index++;
				break;
			}
			if(c == '\n')
				lineCount++;
			index++;
		}
		
		return index;
	}
	
	public static int convertCharToLine(int charNum, String file) {
		int index = -1;
		int lineCount = 1;
		
		for(char c: file.toCharArray()) {
			index++;
			if(index == charNum) 
				break;
			if(c == '\n')
				lineCount++;
		}
		
		return lineCount;
	}

}
