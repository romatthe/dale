package romatthe.dale.cpu;

import java.util.Arrays;

public class Instruction {

    private int opcode;
    private int[] ints;

    public Instruction(int opcode) {
        this.opcode = opcode;
        this.ints =
            Arrays.stream(String.format("%04x", this.opcode).split(""))
                .mapToInt(s -> Integer.parseInt(s, 16))
                .toArray();
    }

    public int getNth(int n) {
        return this.ints[n];
    }
}
