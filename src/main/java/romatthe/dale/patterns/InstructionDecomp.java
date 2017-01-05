package romatthe.dale.patterns;

import javaslang.Tuple;
import javaslang.Tuple4;
import javaslang.match.annotation.Patterns;
import javaslang.match.annotation.Unapply;
import romatthe.dale.cpu.Instruction;

@Patterns
public class InstructionDecomp {

    @Unapply
    static Tuple4<Integer, Integer, Integer, Integer> Instruction(Instruction instruction) {
        return Tuple.of(instruction.getNth(0), instruction.getNth(1), instruction.getNth(2), instruction.getNth(3));
    }

}
