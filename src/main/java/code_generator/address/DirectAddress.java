package code_generator.address;

import code_generator.VarType;

public class DirectAddress extends Address{
    public DirectAddress(int num, VarType varType) {
        super(num, varType);
    }

    public String inString() {
        return num + "";
    }
}
