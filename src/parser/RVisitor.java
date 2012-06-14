package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.CallGraph;
import models.Clazz;
import models.Mapping;
import models.Method;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import callgraphanalyzer.CallGraphResources;

import callgraphanalyzer.Mappings;

public class RVisitor extends ASTVisitor
{

	private Resolver	resolver;
	private CallGraph	callGraph;
	private Clazz		clazz;
	private Method		method;
	private Mappings	mappings	= new Mappings();

	public RVisitor(Resolver resolver, CallGraph callGraph, Clazz clazz,
			Method method)
	{
		this.resolver = resolver;
		this.callGraph = callGraph;
		this.clazz = clazz;
		this.method = method;
	}

	/**
	 * This is to grab the parameter types of the current funtion we are in so
	 * we can map them.
	 */
	@Override
	public boolean visit(MethodDeclaration node)
	{
		// Add the parameter names and types to the variable mapping
		List<SingleVariableDeclaration> list = node.parameters();
		mappings.newMap();
		for (SingleVariableDeclaration var : list)
		{
			Mapping m = new Mapping(var.getType().toString(), var.getName()
					.getFullyQualifiedName());
			mappings.addMapping(var.getName().getFullyQualifiedName(), m);
		}
		return super.visit(node);
	}

	/**
	 * This is to remove the map of the method's parameters we had initially set
	 * up.
	 */
	@Override
	public void endVisit(MethodDeclaration node)
	{
		mappings.removeMap();
	}

	/**
	 * This is the main item we are looking for to resolve and will be our entry
	 * point.
	 */
	@Override
	public boolean visit(MethodInvocation node)
	{
		// Get the fully qualified type
		String type = null;
		if (node.getExpression() != null)
			type = resolveExpression(node.getExpression());
		else
			type = clazz.getName();

		// This means we were unable to resolve expression of
		// the method invocation because it is not user defined and
		// not in our libraries.
		if (type == null)
			return super.visit(node);

		// Get the method call name
		String methodName = node.getName().getFullyQualifiedName();

		// Get the fully qualified parameter types
		List<String> parameters = resolveParameters(node.arguments());

		// Build the method call
		String methodToResolve = methodNameBuilder(type, methodName, parameters);

		List<Method> resolved = lookupClassMethod(type, methodName,
				methodToResolve);

		// Deal with how the method got resolved.
		if (resolved.isEmpty())
		{
			// Try inherit resolving
			if (!parameters.isEmpty())
				resolved = inheritResolveParameters(type, methodName,
						parameters);
		}
		if (!resolved.isEmpty())
		{
			if (resolved.size() == 1)
			{
				method.addMethodCall(resolved.get(0));
				resolved.get(0).addCalledBy(method);
			}
			else
			{
				for (Method res : resolved)
				{
					method.addFuzzyCall(res);
					res.addFuzzyCalledBy(method);
				}
			}
		}
		// The resolve has failed
		else
			method.addUnresolvedCall(node.toString());

		return super.visit(node);
	}

	/**
	 * This will be the main point for resolving expressions. We need to cover
	 * all the cases here of the Expression types we want to handle.
	 * 
	 * @param expression
	 * @return
	 */
	private String resolveExpression(Expression expression)
	{
		// Handle method invocation
		if (expression instanceof MethodInvocation)
		{
			return resolveMethodInvocation((MethodInvocation) expression);
		}
		// Handle variable
		else if (expression instanceof SimpleName)
		{
			return resolveSimpleName((SimpleName) expression);
		}
		// Handle boolean literal
		else if (expression instanceof BooleanLiteral)
		{
			return "boolean";
		}
		// Handle character literal
		else if (expression instanceof CharacterLiteral)
		{
			return "char";
		}
		// Handle null literal
		else if (expression instanceof NullLiteral)
		{
			return "null";
		}
		// Handle number literal
		else if (expression instanceof NumberLiteral)
		{
			return resolveNumberLiteral((NumberLiteral) expression);
		}
		// Handle String literal
		else if (expression instanceof StringLiteral)
		{
			return "String";
		}
		// Handle Type literal
		else if (expression instanceof TypeLiteral)
		{
			return resolveTypeLiteral((TypeLiteral) expression);
		}
		// Handle cast expression
		else if (expression instanceof CastExpression)
		{
			return resolveCastExpression((CastExpression) expression);
		}
		// Handle Qualified names
		else if (expression instanceof QualifiedName)
		{
			return resolveQualifiedName((QualifiedName) expression);
		}
		// Handle explicit super field
		// access
		else if (expression instanceof SuperFieldAccess)
		{
			return resolveSuperFieldAccess((SuperFieldAccess) expression);
		}
		// Handle explicit super method
		// invocation
		else if (expression instanceof SuperMethodInvocation)
		{
			return resolveSuperMethodInvocation((SuperMethodInvocation) expression);
		}
		// Handle explicit use of
		// "this"
		else if (expression instanceof ThisExpression)
		{
			return resolveThisExpression((ThisExpression) expression);
		}
		// Handle parathesized
		// expression
		else if (expression instanceof ParenthesizedExpression)
		{
			return resolveExpression(((ParenthesizedExpression) expression)
					.getExpression());
		}
		// Handle postfix
		// operator
		else if (expression instanceof PostfixExpression)
		{
			return resolveExpression(((PostfixExpression) expression)
					.getOperand());
		}
		// Handle prefix
		// operator
		else if (expression instanceof PrefixExpression)
		{
			return resolveExpression(((PrefixExpression) expression)
					.getOperand());
		}
		// Handle
		// instanceof
		// operator
		// Defaults
		// to bool
		// for now.
		else if (expression instanceof InstanceofExpression)
		{
			return "boolean";
		}
		// Handle
		// Infix
		// operator
		else if (expression instanceof InfixExpression)
		{
			String left = resolveExpression(((InfixExpression) expression)
					.getLeftOperand());
			String right = resolveExpression(((InfixExpression) expression)
					.getRightOperand());
			if (left != null && !left.equals(right))
				return binaryNumericPromotion(left, right);
			else
				return left;
		}
		// Handle
		// class
		// instance
		// creation
		else if (expression instanceof ClassInstanceCreation)
		{
			return ((ClassInstanceCreation) expression).getType().toString();
		}
		// Handle
		// assignment
		else if (expression instanceof Assignment)
		{
			return resolveExpression(((Assignment) expression)
					.getLeftHandSide());
		}

		// Handle
		// field
		// access
		else if (expression instanceof FieldAccess)
		{
			// This
			// also
			// handles
			// this
			// expressions
			// kind
			// of.
			return resolveFieldAccess((FieldAccess) expression);
		}

		return null;
	}

	/**
	 * This method is here because we need to be able to solve method invocations
	 * that are expression in a method invocation. Right now we are not caching
	 * results so we end up resolving method invocations more than once.
	 * @param methodInvocation
	 * @return
	 */
	private String resolveMethodInvocation(MethodInvocation methodInvocation)
	{
		// Get the fully qualified type
		String type = null;
		if (methodInvocation.getExpression() != null)
			type = resolveExpression(methodInvocation.getExpression());
		else
			type = clazz.getName();

		// This means we were unable to resolve expression of
		// the method invocation
		if (type == null)
			return null;

		// Get the method call
		String methodName = methodInvocation.getName().getFullyQualifiedName();

		// Get the fully qualified parameter types
		List<String> parameters = resolveParameters(methodInvocation
				.arguments());

		String methodToResolve = methodNameBuilder(type, methodName, parameters);

		List<Method> resolved = lookupClassMethod(type, methodName,
				methodToResolve);

		// Handle how the method call got resolved.
		if (resolved.isEmpty())
		{
			// Try inherit resolving
			if (!parameters.isEmpty())
				resolved = inheritResolveParameters(type, methodName,
						parameters);
		}
		if (resolved.size() == 1)
		{
			if (!CallGraphResources.isLiteral(resolved.get(0).getReturnType())
					&& callGraph.getAllClazzes().contains(
							resolved.get(0).getReturnType()))
				return resolved.get(0).getReturnType();
			else
				return null;
		}
		// The resolve has failed
		else
			return null;
	}

	/**
	 * Resolves a SimpleName Expression node to a type.
	 * @param name
	 * @return
	 */
	private String resolveSimpleName(SimpleName name)
	{
		String type = mappings.lookupType(name.toString());
		if (type == null)
			type = clazz.lookupField(callGraph, name.toString());
		if (type == null)
		{
			Clazz typeClazz = callGraph.lookupUnqualifiedClassName(clazz,
					name.toString());
			if (typeClazz != null)
				type = typeClazz.getName();
		}
		else
		{
			Clazz typeClazz = null;
			if (!CallGraphResources.isLiteral(type))
			{
				typeClazz = callGraph.lookupUnqualifiedClassName(clazz, type);
				if (typeClazz != null)
					type = typeClazz.getName();
				else
					type = null;
			}
		}
		return type;
	}

	/**
	 * Resolve a QualifiedName node to a type.
	 * @param qualifiedName
	 * @return
	 */
	private String resolveQualifiedName(QualifiedName qualifiedName)
	{
		String type = null;
		if (qualifiedName.getQualifier() instanceof SimpleName)
			type = resolveSimpleName((SimpleName) qualifiedName.getQualifier());
		else if (qualifiedName.getQualifier() instanceof QualifiedName)
			type = resolveQualifiedName((QualifiedName) qualifiedName
					.getQualifier());

		return lookupClassField(type, qualifiedName.getName().toString());
	}

	/**
	 * Resolves a TypeLiteral node to a type.
	 * @param node
	 * @return
	 */
	private String resolveTypeLiteral(TypeLiteral node)
	{
		String type = null;
		Clazz typeClazz = callGraph.lookupUnqualifiedClassName(clazz, node
				.getType().toString());
		if (typeClazz != null)
			type = typeClazz.getName();
		return type;
	}

	/**
	 * Resolve a CastExpression node to a type
	 * @param node
	 * @return
	 */
	private String resolveCastExpression(CastExpression node)
	{
		String type = node.getType().toString();
		if (!CallGraphResources.isLiteral(type))
		{
			Clazz typeClazz = callGraph.lookupUnqualifiedClassName(clazz, type);
			if (typeClazz != null)
				type = typeClazz.getName();
		}
		return type;
	}

	/**
	 * Resolve a FieldAccess node to a type.
	 * @param fieldAccess
	 * @return
	 */
	private String resolveFieldAccess(FieldAccess fieldAccess)
	{
		String className = null;

		if (fieldAccess.getExpression() != null)
			className = resolveExpression(fieldAccess.getExpression());
		else
			className = this.clazz.getName();

		return lookupClassField(className, fieldAccess.getName()
				.getFullyQualifiedName());
	}

	/**
	 * Resolves a NumberLiteral to a type. Note: There is no
	 * numeric promotion in the resolver.
	 * @param numberLiteral
	 * @return
	 */
	private String resolveNumberLiteral(NumberLiteral numberLiteral)
	{
		if (numberLiteral.getToken().contains("F")
				|| numberLiteral.getToken().contains("f"))
			return "float";
		if (numberLiteral.getToken().contains("D")
				|| numberLiteral.getToken().contains("d")
				|| numberLiteral.getToken().contains("E")
				|| numberLiteral.getToken().contains("e"))
			return "double";
		if (numberLiteral.getToken().contains("L")
				|| numberLiteral.getToken().contains("l"))
			return "long";
		if (!numberLiteral.getToken().contains("."))
			return "int";
		else
			return "double";
	}

	/**
	 * Resolve a SuperFieldAccess node to a type.
	 * @param access
	 * @return
	 */
	private String resolveSuperFieldAccess(SuperFieldAccess access)
	{
		Clazz superClazz = null;
		String className = null;
		if (access.getQualifier() == null)
			superClazz = clazz.getSuperClazz();
		else
		{
			className = resolveExpression(access.getQualifier());
			Clazz lookupClazz = callGraph.getClazzes().get(className);
			if (lookupClazz != null)
				superClazz = lookupClazz.getSuperClazz();
		}

		if (superClazz == null)
			return null;
		else
			return superClazz.getName();
	}

	/**
	 * Resolves a SuperMethodInvocation to a type.
	 * @param invocation
	 * @return
	 */
	private String resolveSuperMethodInvocation(SuperMethodInvocation invocation)
	{
		Clazz superClazz = null;
		String className = null;
		if (invocation.getQualifier() == null)
			superClazz = clazz.getSuperClazz();
		else
		{
			className = resolveExpression(invocation.getQualifier());
			Clazz lookupClazz = callGraph.getClazzes().get(className);
			if (lookupClazz != null)
				superClazz = lookupClazz.getSuperClazz();
		}

		if (superClazz == null)
			return null;
		else
			return superClazz.getName();
	}

	/**
	 * Resolve a ThisExpression node to a type.
	 * @param thisExpression
	 * @return
	 */
	private String resolveThisExpression(ThisExpression thisExpression)
	{
		return clazz.getName();
	}

	/**
	 * This function takes two numeric type and will resolve
	 * what type the result should be on a binary operator.
	 * @param left
	 * @param right
	 * @return
	 */
	private String binaryNumericPromotion(String left, String right)
	{
		if (left == null)
			return right;
		else if (right == null)
			return left;
		else if (left.equals("String") || right.equals("String"))
			return "String";
		else if (left.equals("double") || right.equals("double"))
			return "double";
		else if (left.equals("float") || right.equals("float"))
			return "float";
		else if (left.equals("long") || right.equals("long"))
			return "long";
		else
			return "int";
	}

	/**
	 * This function will resolve parameters for a method
	 * invocations.
	 * @param parameters
	 * @return
	 */
	private List<String> resolveParameters(List<Expression> parameters)
	{
		List<String> types = new ArrayList<String>();

		for (Expression expression : parameters)
			types.add(resolveExpression(expression));

		for (int i = 0; i < types.size(); i++)
		{
			// Turn null into the string null
			if (types.get(i) == null)
				types.set(i, "null");
			else if (CallGraphResources.isLiteral(types.get(i)))
				continue;
			else if (!callGraph.getClazzes().containsKey(types.get(i)))
				types.set(i, "null");
		}

		return types;
	}

	/**
	 * This function will return all the possible parameter lists that are
	 * possible for the given parameter list with inheritance and interface
	 * implementation.
	 * 
	 * @param parameters
	 * @return
	 */
	private List<Method> inheritResolveParameters(String type,
			String methodName, List<String> parameters)
	{
		List<ArrayList<String>> inheritParameters = getInheritParameters(parameters);
		List<Method> inheritMethods = new ArrayList<Method>();

		for (ArrayList<String> list : inheritParameters)
		{
			String methodToResolve = methodNameBuilder(type, methodName, list);

			List<Method> resolvedMethod = lookupClassMethod(type, methodName,
					methodToResolve);
			if (!resolvedMethod.isEmpty())
				inheritMethods.addAll(resolvedMethod);
		}

		return inheritMethods;
	}

	/**
	 * Given a list of types, this function will return a list of all
	 * possible parameters sets that could be used for the given set.
	 * Example// A inherits from B, given list of (A, int) the result
	 * would be [(A, int), (B, int)].
	 * @param front
	 * @return
	 */
	private List<ArrayList<String>> getInheritParameters(List<String> front)
	{
		// Remove all generic parameter types
		for (int i = 0; i < front.size(); i++)
		{
			try
			{
				if (front.get(i).contains("<") && front.get(i).contains(">"))
					front.set(i, front.get(i).substring(0, front.get(i).indexOf("<")));
			}
			catch (Exception e)
			{
				continue;
			}
		}

		List<ArrayList<String>> combinations = new ArrayList<ArrayList<String>>();
		List<ArrayList<String>> oldCombinations = new ArrayList<ArrayList<String>>();
		String type = front.get(front.size() - 1);

		for (front = front.subList(0, front.size() - 1); !front.isEmpty(); front = front
				.subList(0, front.size() - 1))
		{
			// The first case is that the we have no combinations yet.
			if (oldCombinations.size() == 0 && type != null)
			{
				for (String typeName : getSuperAndInterfaces(type))
				{
					ArrayList<String> list = new ArrayList<String>();
					list.add(typeName);
					combinations.add(list);
				}
			}

			// The second case is that we have combinations and a type
			else if (type != null && oldCombinations.size() != 0)
			{
				for (String typeName : getSuperAndInterfaces(type))
				{
					for (ArrayList<String> ending : oldCombinations)
					{
						ArrayList<String> list = new ArrayList<String>();
						list.add(typeName);
						list.addAll(ending);
						combinations.add(list);
					}
				}
			}

			oldCombinations = combinations;
			combinations = new ArrayList<ArrayList<String>>();
			type = front.get(front.size() - 1);
		}

		// Do the last step
		if (oldCombinations.size() == 0 && type != null)
		{
			for (String typeName : getSuperAndInterfaces(type))
			{
				ArrayList<String> list = new ArrayList<String>();
				list.add(typeName);
				combinations.add(list);
			}
		}
		else if (type != null && oldCombinations.size() != 0)
		{
			for (String typeName : getSuperAndInterfaces(type))
			{
				for (ArrayList<String> ending : oldCombinations)
				{
					ArrayList<String> list = new ArrayList<String>();
					list.add(typeName);
					list.addAll(ending);
					combinations.add(list);
				}
			}
		}

		return combinations;
	}

	/**
	 * This method should resolve generic return types to actual return types.
	 * 
	 * @param type
	 * @param method
	 * @return
	 */
	private String getGenericReturnType(String type, Method method)
	{
		String types = type.substring(type.lastIndexOf("<") + 1,
				type.lastIndexOf(">"));
		String[] generics = types.split(",(?![^<>]*>");
		List<String> genericParameters = method.getClazz().getGenericTypes();

		int i = 0;
		for (String genericType : genericParameters)
		{
			if (genericType.equals(method.getReturnType()))
				return generics[i];
			i++;
		}

		return null;
	}

	/**
	 * This function will return a list of fully qualified
	 * name of classes which are the recursive interfaces and
	 * super classes of a given class name.
	 * @param className
	 * @return
	 */
	private List<String> getSuperAndInterfaces(String className)
	{
		List<String> result = new ArrayList<String>();

		// Check for literal
		if (CallGraphResources.isLiteral(className))
		{
			result.add(className);
			return result;
		}

		Clazz base = callGraph.lookupUnqualifiedClassName(clazz, className);
		Clazz superClass = null;
		Clazz superInterface = null;
		if (base != null)
		{
			for (superClass = base; superClass != null; superClass = superClass
					.getSuperClazz())
			{
				result.add(superClass.getName());
				result.addAll(getInterfaces(superClass));
			}
		}

		return result;
	}

	/**
	 * This returns an unresolved type list of all interfaces that a given clazz
	 * implements.
	 * 
	 * @param clazz
	 * @return
	 */
	private List<String> getInterfaces(Clazz clazz)
	{
		List<String> interfaces = new ArrayList<String>();

		for (Clazz inter : clazz.getInterfaces())
		{
			interfaces.add(inter.getName());
			interfaces.addAll(getInterfaces(inter));
		}

		return interfaces;
	}

	/**
	 * This function handles when we see a constructor call. We need to be able
	 * to link methods to other constructors for a dependency.
	 */
	@Override
	public boolean visit(ClassInstanceCreation node)
	{
		// Get the type
		String type = node.getType().toString();
		if (callGraph.lookupUnqualifiedClassName(clazz, type) != null)
			type = callGraph.lookupUnqualifiedClassName(clazz, type).getName();

		// This means we were unable to resolve the method invocation
		if (type == null)
			return super.visit(node);

		// Get the method call
		// For constructors method name should be same as the type.
		String methodName = node.getType().toString();

		List<String> parameters = resolveParameters(node.arguments());

		String methodToResolve = methodNameBuilder(type, methodName, parameters);

		List<Method> resolved = lookupClassMethod(type, methodName,
				methodToResolve);

		// The resolving has failed
		if (resolved.isEmpty())
		{
			if (parameters.size() != 0)
				method.addUnresolvedCall(node.toString());
			return super.visit(node);
		}
		// We resolved
		else
		{
			if (resolved.size() == 1)
			{
				method.addMethodCall(resolved.get(0));
				resolved.get(0).addCalledBy(method);
			}
			// We fuzzy resolved.
			else
			{
				for (Method m : resolved)
				{
					method.addFuzzyCall(m);
					m.addFuzzyCalledBy(method);
				}
			}
		}

		return super.visit(node);
	}

	/**
	 * This will return the fully qualified type name for a given fully
	 * qualified class name and field name.
	 * 
	 * @param className
	 * @param field
	 * @return
	 */
	private String lookupClassField(String className, String field)
	{
		if (className == null || field == null)
			return null;

		Clazz typeClazz = callGraph.getClazzes().get(className);

		// This means we were unable to look up the class for the field.
		if (typeClazz == null)
			return null;

		return typeClazz.lookupField(callGraph, field);
	}

	/**
	 * This will return a list of all possible methods that could be called by
	 * this class and method name.
	 * 
	 * @param type
	 * @param methodToResolve
	 * @return
	 */
	private List<Method> lookupClassMethod(String type, String methodName,
			String methodToResolve)
	{
		List<Method> returnMethods = new ArrayList<Method>();
		Clazz callingClazz = callGraph.getClazzes().get(type);

		if (callingClazz != null)
		{
			if (!callingClazz.isInterface())
				returnMethods.addAll(callingClazz.hasMethod(methodToResolve));
			else
				returnMethods.addAll(callingClazz
						.hasImplementedMethod(methodToResolve));
		}

		return returnMethods;
	}

	/**
	 * This function will return a fully qualified method name.
	 * @param type
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	private String methodNameBuilder(String type, String methodName,
			List<String> parameterTypes)
	{
		String builder = "";

		if (type != null && !type.equals(""))
			builder += type + ".";

		builder += methodName + "(";

		for (String param : parameterTypes)
			builder += param + ", ";

		if (parameterTypes.size() != 0)
			builder = builder.substring(0, builder.length() - 2);

		builder += ")";

		return builder;
	}

	/***********************************************************************************
	 * This divider is here to break up this class into its two main components.
	 * The top half is dedicated to resolving method invocations. The bottom
	 * half is dedicated to creating the variable mapping so that we can look up
	 * the types of declared variables when we need to.
	 **********************************************************************************/

	@Override
	public boolean visit(VariableDeclarationExpression node)
	{
		SimpleName varName = null;
		for (Iterator<VariableDeclarationFragment> i = node.fragments()
				.iterator(); i.hasNext();)
		{
			VariableDeclarationFragment frag = (VariableDeclarationFragment) i
					.next();
			varName = frag.getName();
		}
		Mapping m = new Mapping(node.getType().toString(),
				varName.getFullyQualifiedName());
		mappings.addMapping(varName.getFullyQualifiedName(), m);

		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationStatement node)
	{
		SimpleName varName = null;
		for (Iterator<VariableDeclarationFragment> i = node.fragments()
				.iterator(); i.hasNext();)
		{
			VariableDeclarationFragment frag = (VariableDeclarationFragment) i
					.next();
			varName = frag.getName();
		}
		Mapping m = new Mapping(node.getType().toString(),
				varName.getFullyQualifiedName());
		mappings.addMapping(varName.getFullyQualifiedName(), m);
		return super.visit(node);
	}

	@Override
	public boolean visit(Block node)
	{
		mappings.newMap();
		return super.visit(node);
	}

	@Override
	public void endVisit(Block node)
	{
		mappings.removeMap();
	}

}
