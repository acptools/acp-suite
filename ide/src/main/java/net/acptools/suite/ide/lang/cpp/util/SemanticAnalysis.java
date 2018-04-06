package net.acptools.suite.ide.lang.cpp.util;

import net.acptools.suite.ide.lang.cpp.core.*;
import net.acptools.suite.ide.lang.cpp.generated.Parser;

import java.util.*;

public class SemanticAnalysis {

    public static Parser parser;

    private static final Type[] BASIC_TYPES = new Type[]{
            new Type("int"),
            new Type("float"),
            new Type("double"),
            new Type("long"),
            new Type("char"),
            new Type("void"),
            new Type("string"),
            new Type("bool")
    };

    public static ArrayList<String> variaveis = new ArrayList<String>();
    public static ArrayList<String> valores = new ArrayList<String>();
    private static SemanticAnalysis sAnalysis;

    public static SemanticAnalysis getInstance() {
        if (sAnalysis == null)
            sAnalysis = new SemanticAnalysis();
        return sAnalysis;
    }

    // Object Attributes

    private Program cProgram; // ...

    private Stack<ScopedEntity> scopeStack;

    private Map<String, Function> estimatedFunctions = new HashMap<>();

    private Map<String, Variable> estimatedVariables = new HashMap<>();

    private SemanticAnalysis() {
        scopeStack = new Stack<ScopedEntity>();
        cProgram = new Program();

        cProgram.addVariable(new Variable("led", new Type("Led")));

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(new Variable("text", new Type("string")));
        Function f = new Function("ACP_TRACE", parameters);
        f.setReturnType(new Type("void"));
        f.setReturnedType(new Type("void"));
        estimatedFunctions.put(f.getName(), f);

        parameters = new ArrayList<>();
        parameters.add(new Variable("text", new Type("string")));
        f = new Function("F", parameters);
        f.setReturnType(new Type("string"));
        f.setReturnedType(new Type("string"));
        estimatedFunctions.put(f.getName(), f);

        parameters = new ArrayList<>();
        parameters.add(new Variable("pin", new Type("int")));
        f = new Function("analogRead", parameters);
        f.setReturnType(new Type("double"));
        f.setReturnedType(new Type("double"));
        estimatedFunctions.put(f.getName(), f);

        f = new Function("millis", new ArrayList<>());
        f.setReturnType(new Type("long"));
        f.setReturnedType(new Type("long"));
        estimatedFunctions.put(f.getName(), f);

        estimatedVariables.put("led", new Variable("led", new Type("Led")));
    }

    // Operations ...

    private void createNewScope(ScopedEntity scope) {
        scopeStack.push(scope);
    }

    public void exitCurrentScope() {
        ScopedEntity scoped = scopeStack.pop();

        if (scoped instanceof Function)
            ((Function) scoped).validateReturnedType();
    }

    public ScopedEntity getCurrentScope() {
        return scopeStack.peek();
    }

    public void addFunctionAndNewScope(Function f) {
        cProgram.checkOverload(f);
        cProgram.addFunction(f);
        createNewScope(f);
    }

    public void createIf(Object e) {
        if (e instanceof Function) {
            Expression ex = new Expression(((Function) e).getReturnType());
            createIf(ex);
        } else {
            createIf((Expression) e);
        }

    }

    public void createIf(Expression e) {
        createNewScope(new IfElse(e));
    }

    public void createElse() {
        createNewScope(new IfElse());
    }

    public void addVariable(Variable v) {
        if (checkVariableNameCurrentScope(v.getName()))
            throw new SemanticException("Variable name \"" + v.getName() + "\" already exists");

        if (!scopeStack.isEmpty()) {
            scopeStack.peek().addVariable(v);
        } else {
            cProgram.addVariable(v);
        }
    }

    public Identifier getIdentifier(String name) {
        if (!checkVariableNameAllScopes(name) && !checkFunctionName(name)) {
            estimatedVariables.put(name, new Variable(name, new Type(name)));
            // TODO: warning throw new SemanticException("Identifier name doesn't exists: " + name);
        }

        if (estimatedVariables.get(name) != null)
            return estimatedVariables.get(name);

        if (cProgram.getFunctions().get(name) != null)
            return cProgram.getFunctions().get(name);

        if (estimatedFunctions.get(name) != null) {
            return estimatedFunctions.get(name);
        }

        for (int i = scopeStack.size() - 1; i >= 0; i--)
            if (scopeStack.get(i).getVariable().get(name) != null)
                return scopeStack.get(i).getVariable().get(name);

        return cProgram.getVariable().get(name);
    }

    // Check Operations

    public void isFunction(Object o) {
        if (!(o instanceof Function))
            throw new SemanticException("Sorry, but " + o.toString() + " is not a function");
    }

    public boolean checkVariableNameCurrentScope(String name) {

        Map<String, Variable> variablesMap = new HashMap<>();
        if (scopeStack.isEmpty()) {
            variablesMap.putAll(cProgram.getVariable());
        } else {
            variablesMap.putAll(scopeStack.peek().getVariable());
        }

        variablesMap.putAll(estimatedVariables);

        return variablesMap.containsKey(name);
    }

    public boolean checkVariableNameAllScopes(String name) {
        HashSet<String> variablesName = new HashSet<String>();
        variablesName.addAll(estimatedVariables.keySet());
        variablesName.addAll(cProgram.getVariable().keySet());
        if (!scopeStack.isEmpty()) {
            variablesName.addAll(scopeStack.peek().getVariable().keySet());

            for (int i = 0; i < scopeStack.size() - 1; i++) {
                variablesName.addAll(scopeStack.get(i).getVariable().keySet());
            }
        }
        return variablesName.contains(name);
    }

    public boolean checkTypeExists(Type type) {
        for (int i = 0; i < BASIC_TYPES.length; i++)
            if (BASIC_TYPES[i].getName().equals(type.getName()))
                return true;

        for (int i = 0; i < scopeStack.size(); i++) {
            if (scopeStack.get(i).getTypes().containsKey(type.getName())) {
                return true;
            }
        }
        return false;
    }

    public void checkFunctionCallException(String functionName) {
        if (!checkFunctionCall(functionName)) {
            // TODO: vytvorit funkciu
            throw new SemanticException("Calling function not declared: " + functionName + "()");
        }
    }

    public void checkFunctionCallException(String functionName, Type[] types) {
        if (!checkFunctionCall(functionName, types)) {
            // TODO: warning na pravdepodobnu chybu!
            //throw new SemanticException("Calling function not declared: " + functionName + " " + Arrays.toString(types));
        }
    }

    public boolean checkFunctionName(String functionName) {
        Function f = getFunctions().get(functionName);
        return f != null;
    }

    public boolean checkFunctionCall(String functionName) {
        Function f = getFunctions().get(functionName);
        return f != null && f.getParameterTypes().length == 0;
    }

    public boolean checkFunctionCall(String functionName, Type[] types) {
        Function f = getFunctions().get(functionName);
        if (f != null && f.getParameterTypes().length == types.length) {
            for (int i = 0; i < types.length; i++) {
                if (!(types[i].getName().equals(f.getParameterTypes()[i].getName())))
                    return false;
            }
            return true;
        }
        return false;
    }

    public void checkReturnedType(Object e) {
        Type typeToCheck;
        if (e instanceof Function)
            typeToCheck = ((Function) e).getReturnType();
        else
            typeToCheck = ((Expression) e).getType();


        Function f = null;
        if (scopeStack.peek() instanceof Function) {
            f = (Function) scopeStack.peek();
        } else {
            for (int i = scopeStack.size() - 1; i >= 0; i--) {
                if (scopeStack.get(i) instanceof Function) {
                    f = (Function) scopeStack.get(i);
                    break;
                }
            }
        }

        if (f == null)
            throw new SemanticException("Checking return type without function");

        if (!f.getReturnType().equals(typeToCheck)) {
            throw new SemanticException("Wrong return type: " + f.getReturnType() + " and " + typeToCheck);
        }

        f.setReturnedType(typeToCheck);
    }

    private boolean checkIsNumber(Type t) {
        return t.equals(new Type("int")) || t.equals(new Type("float"));
    }

    public Expression getExpressionForOperation(Operation op, Object e1, Object e2) {
        Expression ex1, ex2;
        if (e1 instanceof Function) {
            ex1 = new Expression(((Function) e1).getReturnType());
        } else {
            ex1 = (Expression) e1;
        }
        if (e2 instanceof Function) {
            ex2 = new Expression(((Function) e2).getReturnType());
        } else {
            ex2 = (Expression) e2;
        }
        return getExpressionForOperation(op, ex1, ex2);
    }

    public Expression getExpressionForOperation(Operation op, Expression e1, Expression e2) {

        boolean typeCheck = false;
        switch (op) {
            case AND_OP:
            case OR_OP:
                /*if (typeCheck && e1.getType().equals(new Type("int")))*/
                return new Expression(new Type("int")); // OK
            case EQ_OP:
            case GE_OP:
            case LE_OP:
            case LESS_THAN:
            case MORE_THAN:
            case NE_OP:
                /*if (checkIsNumber(e1.getType()) && checkIsNumber(e2.getType()) ||
                        (typeCheck && e1.getType().equals(e2.getType())))*/
                return new Expression(new Type("int"));
            case MINUS:
            case MULT:
            case PERC:
            case PLUS:
            case DIV:
                /*if (checkIsNumber(e1.getType()) && checkIsNumber(e2.getType()))*/
                return new Expression(e1.getType());
        }
        throw new SemanticException("Illegal Operation between " + e1.getType() + " and " + e2.getType());
    }

    public Expression getExpressionType(Expression e1) {
        return new Expression(e1.getType());
    }

    public Function createMethodFunction(Expression object, String method) {
        Function f = new Function(object.getType() + "." + method);
        f.setReturnType(new Type("void"));
        estimatedFunctions.put(f.getName(), f);
        return f;
    }

    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();
        functions.putAll(estimatedFunctions);
        functions.putAll(cProgram.getFunctions());
        return functions;
    }

    public Program getProgram() {
        return cProgram;
    }

    public static void reset() {
        sAnalysis = null;
    }
}
