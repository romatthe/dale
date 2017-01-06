package romatthe.dale.cpu.instructions;

import com.sun.org.apache.bcel.internal.generic.ConstantPushInstruction;
import romatthe.dale.cpu.Cpu;

import java.util.function.BiFunction;

public class Instructions {

    /**
     * This instruction is mostly ignored and used by few Roms
     */
    public BiFunction<Cpu, Integer, SideEffect> funcSYS = (cpu, op) -> SideEffect.NONE;

    /**
     * Clears the screen
     */
    public BiFunction<Cpu, Integer, SideEffect> funcCLR = (cpu, op) -> SideEffect.CLEAR_SCREEN;

    /**
     * Returns from a subroutine. Pop the current value in the stack pointer off of
     * the stack, and set the program counter to the value popped.
     */
    public BiFunction<Cpu, Integer, SideEffect> funcRTS = (cpu, op) -> {
        cpu.setStackPointer(cpu.getStackPointer() - 1);
        cpu.setProgramCounter(cpu.getMemory().read(cpu.getStackPointer()) << 8);
        cpu.setStackPointer(cpu.getStackPointer() - 1);
        cpu.setProgramCounter(cpu.getProgramCounter() + cpu.getMemory().read(cpu.getStackPointer()));

        return SideEffect.NONE;
    };

    /**
     * Sets the Program Counter to the address defined by operand NNN
     */
    public BiFunction<Cpu, Integer, SideEffect> funcJUMP = (cpu, op) -> {
        cpu.setProgramCounter(op & 0x0FFF);

        return SideEffect.NONE;
    };

    /**
     * Call routine at address defined by operand NNN. Save the current Program Counter on the
     * stack, then set the Program Counter to the address defined by operand NNN
     */
    public BiFunction<Cpu, Integer, SideEffect> funcCALL = (cpu, op) -> {
        cpu.getMemory().write(cpu.getStackPointer(), cpu.getProgramCounter() & 0x00FF);
        cpu.setStackPointer(cpu.getStackPointer() + 1);
        cpu.getMemory().write(cpu.getStackPointer(), (cpu.getProgramCounter() & 0xFF00) >> 8);
        cpu.setStackPointer(cpu.getStackPointer() + 1);
        cpu.setProgramCounter(op & 0x0FFF);

        return SideEffect.NONE;
    };

    /**
     * Skip next instruction if register defined by operand S equals value
     * defined by operand NN
     */
    public BiFunction<Cpu, Integer, SideEffect> funcSKE = (cpu, op) -> {
        if (cpu.getRegisterAt((op & 0x0F00) >> 8) == (op & 0x00FF)) {
            cpu.setProgramCounter(cpu.getProgramCounter() + 2);
        }

        return SideEffect.NONE;
    };

    /**
     * Skip next instruction if register defined by operand S does not equal
     * value defined by operand NN
     */
    public BiFunction<Cpu, Integer, SideEffect> funcSKNE = (cpu, op) -> {
        if (cpu.getRegisterAt((op & 0x0F00) >> 8) != (op & 0x00FF)) {
            cpu.setProgramCounter(cpu.getProgramCounter() + 2);
        }

        return SideEffect.NONE;
    };

    /**
     * Skip next instruction if register defined by operand S equals value
     * defined by operand T
     */
    public BiFunction<Cpu, Integer, SideEffect> funcSKRE = (cpu, op) -> {
        if (cpu.getRegisterAt((op & 0x0F00) >> 8) == cpu.getRegisterAt((op & 0x0F0) >> 4)) {
            cpu.setProgramCounter(cpu.getProgramCounter() + 2);
        }

        return SideEffect.NONE;
    };

    /**
     * Load register defined by operand s with value defined by operand nn
     */
    public BiFunction<Cpu, Integer, SideEffect> funcLOAD = (cpu, op) -> {
        cpu.setRegisterAt((op & 0x0F00) >> 8, (short) (op & 0x00FF));

        return SideEffect.NONE;
    };

    /**
     * Add value defined by operand nn to register defined by operand s
     */
    public BiFunction<Cpu, Integer, SideEffect> funcADD = (cpu, op) -> {
        int registerValue = cpu.getRegisterAt((op & 0x0F00) >> 8);
        short newRegisterValue = (short) (registerValue + (op & 0x00FF));
        newRegisterValue = (newRegisterValue < 256) ? (short) newRegisterValue : (short) (newRegisterValue - 256);

        cpu.setRegisterAt((op & 0x0F00) >> 8, newRegisterValue);

        return SideEffect.NONE;
    };

    /**
     * Move value from register defined by operand s to register defined by operand t
     */
    public BiFunction<Cpu, Integer, SideEffect> funcMOVE = (cpu, op) -> {
        int registerSource = (op & 0x0F00) >> 8;
        int registerTarget = (op & 0x00F0) >> 4;
        short valueToMove = cpu.getRegisterAt(registerSource);
        cpu.setRegisterAt(registerTarget, valueToMove);

        return SideEffect.NONE;
    };

    /**
     * Perform logical OR on register defined by operand s and register defined by operand t
     * and store in register defined by operand t
     */
    public BiFunction<Cpu, Integer, SideEffect> funcOR = (cpu, op) -> {
        int registerSource = (op & 0x0F00) >> 8;
        int registerTarget = (op & 0x00F0) >> 4;
        short valueToMove = (short) (cpu.getRegisterAt(registerSource) | cpu.getRegisterAt(registerTarget));
        cpu.setRegisterAt(registerTarget, valueToMove);

        return SideEffect.NONE;
    };

    /**
     * Perform logical AND on register defined by operand s and register defined by operand t
     * and store in register defined by operand t
     */
    public BiFunction<Cpu, Integer, SideEffect> funcAND = (cpu, op) -> {
        int registerSource = (op & 0x0F00) >> 8;
        int registerTarget = (op & 0x00F0) >> 4;
        short valueToMove = (short) (cpu.getRegisterAt(registerSource) & cpu.getRegisterAt(registerTarget));
        cpu.setRegisterAt(registerTarget, valueToMove);

        return SideEffect.NONE;
    };

    /**
     * Perform logical XOR on register defined by operand s and register defined by operand t
     * and store in register defined by operand t
     */
    public BiFunction<Cpu, Integer, SideEffect> funcXOR = (cpu, op) -> {
        int registerSource = (op & 0x0F00) >> 8;
        int registerTarget = (op & 0x00F0) >> 4;
        short valueToMove = (short) (cpu.getRegisterAt(registerSource) ^ cpu.getRegisterAt(registerTarget));
        cpu.setRegisterAt(registerTarget, valueToMove);

        return SideEffect.NONE;
    };

    /**
     * Add value in register defined by operand s and value in register defined by operand t
     * and store in register defined by operand s. Set register F on carry.
     */
    public BiFunction<Cpu, Integer, SideEffect> funcADDR = (cpu, op) -> {
        int registerSource = (op & 0x0F00) >> 8;
        int registerTarget = (op & 0x00F0) >> 4;
        short valueToMove = (short) (cpu.getRegisterAt(registerSource) + cpu.getRegisterAt(registerTarget));

        if (valueToMove > 255) {
            valueToMove = (short) (valueToMove - 256);
            cpu.setRegisterAt(0xF, (short) 1);
        } else {
            cpu.setRegisterAt(0xF, (short) 0);
        }

        cpu.setRegisterAt(registerTarget, valueToMove);

        return SideEffect.NONE;
    };

    /**
     * Subtract value in register defined by operand s from value in register defined by operand t
     * and store in register defined by operand s. If a borrow is NOT generated,
     * set a carry flag in register F.
     */
    public BiFunction<Cpu, Integer, SideEffect> funcSUB = (cpu, op) -> {
        int registerSource = (op & 0x0F00) >> 8;
        int registerTarget = (op & 0x00F0) >> 4;

        int resultValue;
        if (cpu.getRegisterAt(registerSource) > cpu.getRegisterAt(registerTarget)) {
            resultValue = cpu.getRegisterAt(registerSource) - cpu.getRegisterAt(registerTarget);
            cpu.setRegisterAt(0xF, (short) 1);
        } else {
            resultValue = 256 + cpu.getRegisterAt(registerSource) - cpu.getRegisterAt(registerTarget);
            cpu.setRegisterAt(0xF, (short) 0);
        }

        cpu.setRegisterAt(registerTarget, (short) resultValue);

        return SideEffect.NONE;
    };

    /**
     * Shift bits in register s 1 bit to the right. Bit 0 shifts to register F
     */
    public BiFunction<Cpu, Integer, SideEffect> funcSHR = (cpu, op) -> {
        int register = (op & 0x0F00) >> 8;
        cpu.setRegisterAt(0xF, (short) (cpu.getRegisterAt(register) & 0x1));
        cpu.setRegisterAt(register, (short) (cpu.getRegisterAt(register) >> 1));

        return SideEffect.NONE;
    };

    /**
     * Shift bits in register s 1 bit to the left. Bit 7 shifts to register F
     */
    public BiFunction<Cpu, Integer, SideEffect> funcSHL = (cpu, op) -> {
        int register = (op & 0x0F00) >> 8;
        cpu.setRegisterAt(0xF, (short)((cpu.getRegisterAt(register) & 0x80) >> 8));
        cpu.setRegisterAt(register, (short) (cpu.getRegisterAt(register) << 1));

        return SideEffect.NONE;
    };

    /**
     * Skip next instruction if register defined by operand s is not not equal to
     * register defined by operand t
     */
    public BiFunction<Cpu, Integer, SideEffect> funcSKRNE = (cpu, op) -> {
        int registerS = (op & 0x0F00) >> 8;
        int registerT = (op & 0x00F0) >> 4;

        if (cpu.getRegisterAt(registerS) != cpu.getRegisterAt(registerT)) {
            cpu.setProgramCounter(cpu.getProgramCounter() + 2);
        }
        
        return SideEffect.NONE;
    };

}