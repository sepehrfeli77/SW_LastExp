package code_generator;

import log.LogHelper;
import error_handler.ErrorHandlerHelper;
import scanner.token.Token;
import semantic.symbol.Symbol;
import semantic.symbol.SymbolTable;
import semantic.symbol.SymbolType;

import java.util.Stack;

/**
 * Created by Alireza on 6/27/2015.
 */
public class CodeGenerator {
    private Memory memory = new Memory();
    private Stack<Address> ss = new Stack<Address>();
    private Stack<String> symbolStack = new Stack<>();
    private Stack<String> callStack = new Stack<>();
    private SymbolTable symbolTable;

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public Stack<Address> getSs() {
        return ss;
    }

    public void setSs(Stack<Address> ss) {
        this.ss = ss;
    }

    public Stack<String> getSymbolStack() {
        return symbolStack;
    }

    public void setSymbolStack(Stack<String> symbolStack) {
        this.symbolStack = symbolStack;
    }

    public Stack<String> getCallStack() {
        return callStack;
    }

    public void setCallStack(Stack<String> callStack) {
        this.callStack = callStack;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public CodeGenerator() {
        symbolTable = new SymbolTable(memory);
        //TODO
    }

    public void printMemory() {
        getMemory().pintCodeBlock();
    }

    public void semanticFunction(int func, Token next) {
        LogHelper.print("codegenerator : " + func);
        switch (func) {
            case 0:
                return;
            case 1:
                checkID();
                break;
            case 2:
                pid(next);
                break;
            case 3:
                fpid();
                break;
            case 4:
                kpid(next);
                break;
            case 5:
                intpid(next);
                break;
            case 6:
                startCall();
                break;
            case 7:
                call();
                break;
            case 8:
                arg();
                break;
            case 9:
                assign();
                break;
            case 10:
                add();
                break;
            case 11:
                sub();
                break;
            case 12:
                mult();
                break;
            case 13:
                label();
                break;
            case 14:
                save();
                break;
            case 15:
                whileMethod();
                break;
            case 16:
                jpfSave();
                break;
            case 17:
                jpHere();
                break;
            case 18:
                print();
                break;
            case 19:
                equal();
                break;
            case 20:
                lessThan();
                break;
            case 21:
                and();
                break;
            case 22:
                not();
                break;
            case 23:
                defClass();
                break;
            case 24:
                defMethod();
                break;
            case 25:
                popClass();
                break;
            case 26:
                extend();
                break;
            case 27:
                defField();
                break;
            case 28:
                defVar();
                break;
            case 29:
                methodReturn();
                break;
            case 30:
                defParam();
                break;
            case 31:
                lastTypeBool();
                break;
            case 32:
                lastTypeInt();
                break;
            case 33:
                defMain();
                break;
            default:
                ErrorHandlerHelper.printError("Function not defined");
        }
    }

    private void defMain() {
        //ss.pop();
        getMemory().add3AddressCode(getSs().pop().num, Operation.JP, new Address(getMemory().getCurrentCodeBlockAddress(), VarType.Address), null, null);
        String methodName = "main";
        String className = getSymbolStack().pop();

        getSymbolTable().addMethod(className, methodName, getMemory().getCurrentCodeBlockAddress());

        getSymbolStack().push(className);
        getSymbolStack().push(methodName);
    }

    //    public void spid(Token next){
//        symbolStack.push(next.value);
//    }
    public void checkID() {
        getSymbolStack().pop();
        if (getSs().peek().varType == VarType.Non) {
            ErrorHandlerHelper.printError("Error was occurred");
        }
    }

    public void pid(Token next) {
        if (getSymbolStack().size() > 1) {
            String methodName = getSymbolStack().pop();
            String className = getSymbolStack().pop();
            try {

                Symbol s = getSymbolTable().get(className, methodName, next.value);
                VarType t;
                if (s.type == SymbolType.Bool) {
                    t = VarType.Bool;
                } else {
                    t = VarType.Int;
                }
                getSs().push(new Address(s.address, t));


            } catch (Exception e) {
                getSs().push(new Address(0, VarType.Non));
            }
            getSymbolStack().push(className);
            getSymbolStack().push(methodName);
        } else {
            getSs().push(new Address(0, VarType.Non));
        }
        getSymbolStack().push(next.value);
    }

    public void fpid() {
        getSs().pop();
        getSs().pop();

        Symbol s = getSymbolTable().get(getSymbolStack().pop(), getSymbolStack().pop());
        VarType t;
        if (s.type == SymbolType.Bool) {
            t = VarType.Bool;
        } else {
            t = VarType.Int;
        }
        getSs().push(new Address(s.address, t));

    }

    public void kpid(Token next) {
        getSs().push(getSymbolTable().get(next.value));
    }

    public void intpid(Token next) {
        getSs().push(new Address(Integer.parseInt(next.value), VarType.Int, TypeAddress.Imidiate));
    }

    public void startCall() {
        //TODO: method ok
        getSs().pop();
        getSs().pop();
        String methodName = getSymbolStack().pop();
        String className = getSymbolStack().pop();
        getSymbolTable().startCall(className, methodName);
        getCallStack().push(className);
        getCallStack().push(methodName);

        //symbolStack.push(methodName);
    }

    public void call() {
        //TODO: method ok
        String methodName = getCallStack().pop();
        String className = getCallStack().pop();
        try {
            getSymbolTable().getNextParam(className, methodName);
            ErrorHandlerHelper.printError("The few argument pass for method");
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
        VarType t;
        if (getSymbolTable().getMethodReturnType(className, methodName) == SymbolType.Int) {
            t = VarType.Int;
        } else {
            t = VarType.Bool;
        }
        Address temp = new Address(getMemory().getTemp(), t);
        getSs().push(temp);
        getMemory().add3AddressCode(Operation.ASSIGN, new Address(temp.num, VarType.Address, TypeAddress.Imidiate), new Address(getSymbolTable().getMethodReturnAddress(className, methodName), VarType.Address), null);
        getMemory().add3AddressCode(Operation.ASSIGN, new Address(getMemory().getCurrentCodeBlockAddress() + 2, VarType.Address, TypeAddress.Imidiate), new Address(getSymbolTable().getMethodCallerAddress(className, methodName), VarType.Address), null);
        getMemory().add3AddressCode(Operation.JP, new Address(getSymbolTable().getMethodAddress(className, methodName), VarType.Address), null, null);

        //symbolStack.pop();


    }

    public void arg() {
        //TODO: method ok

        String methodName = getCallStack().pop();
//        String className = symbolStack.pop();
        try {
            Symbol s = getSymbolTable().getNextParam(getCallStack().peek(), methodName);
            VarType t;
            if (s.type == SymbolType.Bool) {
                t = VarType.Bool;
            } else {
                t = VarType.Int;
            }
            Address param = ss.pop();
            if (param.varType != t) {
                ErrorHandlerHelper.printError("The argument type isn't match");
            }
            getMemory().add3AddressCode(Operation.ASSIGN, param, new Address(s.address, t), null);

//        symbolStack.push(className);

        } catch (IndexOutOfBoundsException e) {
            ErrorHandlerHelper.printError("Too many arguments pass for method");
        }
        getCallStack().push(methodName);

    }

    public void assign() {

        Address s1 = getSs().pop();
        Address s2 = getSs().pop();
//        try {
        if (s1.varType != s2.varType) {
            ErrorHandlerHelper.printError("The type of operands in assign is different ");
        }
//        }catch (NullPointerException d)
//        {
//            d.printStackTrace();
//        }
        getMemory().add3AddressCode(Operation.ASSIGN, s1, s2, null);

    }

    public void add() {
        arithmetic("In add two operands must be integer", Operation.ADD);
    }

    public void sub() {
        arithmetic("In sub two operands must be integer", Operation.SUB);
    }

    public void mult() {
        arithmetic("In mult two operands must be integer", Operation.MULT);
    }

    public void arithmetic(String text, Operation type) {
        Address temp = new Address(getMemory().getTemp(), VarType.Int);
        Address s2 = getSs().pop();
        Address s1 = getSs().pop();
        if (s1.varType != VarType.Int || s2.varType != VarType.Int) {
            ErrorHandlerHelper.printError(text);
        }
        getMemory().add3AddressCode(type, s1, s2, temp);
//        memory.saveMemory();
        getSs().push(temp);
    }

    public void label() {
        getSs().push(new Address(getMemory().getCurrentCodeBlockAddress(), VarType.Address));
    }

    public void save() {
        getSs().push(new Address(getMemory().saveMemory(), VarType.Address));
    }

    public void whileMethod() {
        memory.add3AddressCode(ss.pop().num, Operation.JPF, ss.pop(), new Address(memory.getCurrentCodeBlockAddress() + 1, VarType.Address), null);
        memory.add3AddressCode(Operation.JP, ss.pop(), null, null);
    }

    public void jpfSave() {
        Address save = new Address(memory.saveMemory(), VarType.Address);
        memory.add3AddressCode(ss.pop().num, Operation.JPF, ss.pop(), new Address(memory.getCurrentCodeBlockAddress(), VarType.Address), null);
        ss.push(save);
    }

    public void jpHere() {
        memory.add3AddressCode(ss.pop().num, Operation.JP, new Address(memory.getCurrentCodeBlockAddress(), VarType.Address), null, null);
    }

    public void print() {
        memory.add3AddressCode(Operation.PRINT, ss.pop(), null, null);
    }

    public void equal() {
        Address temp = new Address(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != s2.varType) {
            ErrorHandlerHelper.printError("The type of operands in equal operator is different");
        }
        memory.add3AddressCode(Operation.EQ, s1, s2, temp);
        ss.push(temp);
    }

    public void lessThan() {
        Address temp = new Address(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Int || s2.varType != VarType.Int) {
            ErrorHandlerHelper.printError("The type of operands in less than operator is different");
        }
        memory.add3AddressCode(Operation.LT, s1, s2, temp);
        ss.push(temp);
    }

    public void and() {
        Address temp = new Address(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Bool || s2.varType != VarType.Bool) {
            ErrorHandlerHelper.printError("In and operator the operands must be boolean");
        }
        memory.add3AddressCode(Operation.AND, s1, s2, temp);
        ss.push(temp);

    }

    public void not() {
        Address temp = new Address(memory.getTemp(), VarType.Bool);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.Bool) {
            ErrorHandlerHelper.printError("In not operator the operand must be boolean");
        }
        memory.add3AddressCode(Operation.NOT, s1, s2, temp);
        ss.push(temp);

    }

    public void defClass() {
        ss.pop();
        symbolTable.addClass(symbolStack.peek());
    }

    public void defMethod() {
        ss.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethod(className, methodName, memory.getCurrentCodeBlockAddress());

        symbolStack.push(className);
        symbolStack.push(methodName);

    }

    public void popClass() {
        symbolStack.pop();
    }

    public void extend() {
        ss.pop();
        symbolTable.setSuperClass(symbolStack.pop(), symbolStack.peek());
    }

    public void defField() {
        ss.pop();
        symbolTable.addField(symbolStack.pop(), symbolStack.peek());
    }

    public void defVar() {
        ss.pop();

        String var = symbolStack.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethodLocalVariable(className, methodName, var);

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void methodReturn() {
        //TODO : call ok

        String methodName = symbolStack.pop();
        Address s = ss.pop();
        SymbolType t = symbolTable.getMethodReturnType(symbolStack.peek(), methodName);
        VarType temp = VarType.Int;
        if (t == SymbolType.Bool) {
            temp = VarType.Bool;
        }
        if (s.varType != temp) {
            ErrorHandlerHelper.printError("The type of method and return address was not match");
        }
        memory.add3AddressCode(Operation.ASSIGN, s, new Address(symbolTable.getMethodReturnAddress(symbolStack.peek(), methodName), VarType.Address, TypeAddress.Indirect), null);
        memory.add3AddressCode(Operation.JP, new Address(symbolTable.getMethodCallerAddress(symbolStack.peek(), methodName), VarType.Address), null, null);

        //symbolStack.pop();

    }

    public void defParam() {
        //TODO : call Ok
        ss.pop();
        String param = symbolStack.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethodParameter(className, methodName, param);

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    public void lastTypeBool() {
        symbolTable.setLastType(SymbolType.Bool);
    }

    public void lastTypeInt() {
        symbolTable.setLastType(SymbolType.Int);
    }

}
