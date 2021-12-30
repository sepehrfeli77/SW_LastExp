package code_generator.address;

import code_generator.VarType;

public class ImmediateAddress extends Address{
    public ImmediateAddress(int num, VarType varType) {
        super(num, varType);
    }

    public String inString() {
        return "#" + num;
    }
}

