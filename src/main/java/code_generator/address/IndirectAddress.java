package code_generator.address;

import code_generator.VarType;

public class IndirectAddress extends Address{
    public IndirectAddress(int num, VarType varType) {
        super(num, varType);
    }

    public String inString() {
        return "@" + num;
    }
}