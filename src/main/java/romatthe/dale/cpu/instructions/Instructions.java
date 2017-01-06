package romatthe.dale.cpu.instructions;

import com.sun.org.apache.bcel.internal.generic.ConstantPushInstruction;
import javafx.geometry.Side;
import romatthe.dale.cpu.Cpu;

import java.util.Random;
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

    /**
     * Load index with value defined by operand nnn
     */
    public BiFunction<Cpu, Integer, SideEffect> funcLOADI = (cpu, op) -> {
        cpu.setIndexRegisterI((short)(op & 0x0FFF));

        return SideEffect.NONE;
    };

    /**
     * Jump to address defined by operand nnn + index
     */
    public BiFunction<Cpu, Integer, SideEffect> funcJUMPI = (cpu, op) -> {
        cpu.setProgramCounter(cpu.getIndexRegisterI() + (op & 0x0FFF));

        return SideEffect.NONE;
    };

    /**
     * Generate random number between 0 and value defined by operand nn and store in
     * register defined by operand t
     */
    public BiFunction<Cpu, Integer, SideEffect> funcRAND = (cpu, op) -> {
        Random random = new Random();

        int targetRegister = (op & 0x0F00) >> 8;
        int maxValue = (op & 0x00FF);
        cpu.setRegisterAt(targetRegister, (short)(maxValue & random.nextInt(256)));

        return SideEffect.NONE;
    };

    /**
     * Draws the sprite in register defined by operand n to the coordinates defined by  the registers defined by operand s (x) and t (y).
     * Drawing is done via a XOR routine, meaning that if the target pixel is already turned on,
     * and a pixel is set to be turned on at that same location via the draw, then the pixel is turned off.
     * The routine will wrap the pixels if they are drawn off the edge of the screen. Each sprite is 8 bits wide.
     * Consecutive bytes in the memory pointed to by the index register make up the bytes of the sprite.
     * Each bit in the sprite byte determines whether a pixel is turned on (1) or turned off (0).
     * If writing a pixel to a location causes that pixel to be turned off, then register F will be set to 1.
     */
    public BiFunction<Cpu, Integer, SideEffect> funcDRAW = (cpu, op) -> {
        // Get the x and y coordinates
        int xRegister = (op & 0x0F00) >> 8;
        int yRegister = (op & 0x00F0) >> 4;
        int xPos = cpu.getRegisterAt(xRegister);
        int yPos = cpu.getRegisterAt(yRegister);
        short byteCount = (short)(op & 0xF);

        cpu.setRegisterAt(0xF, (short)0);

        // The sprite byte array has size n
        byte[] sprite = new byte[byteCount];

        // Loop over each byte (representing a row) of the sprite
        for (int i = 0; i < byteCount; i++) {
            byte pixel = (byte)cpu.getMemory().read(cpu.getIndexRegisterI() + i);

            // Loop over the 8 bits of the row
            for(int xline = 0; xline < 8; xline++) {
                // TODO
            }
        }

        /*
        // Read the sprite to draw
        for (int yIndex = 0; yIndex < (op & 0xF); yIndex++) {
            short colorByte = memory.read(index + yIndex);
            int yCoord = yPos + yIndex;
            yCoord = yCoord % mScreen.getHeight();

            int mask = 0x80;

            for (int xIndex = 0; xIndex < 8; xIndex++) {
                int xCoord = xPos + xIndex;
                xCoord = xCoord % mScreen.getWidth();

                boolean turnOn = (colorByte & mask) > 0;
                boolean currentOn = mScreen.pixelOn(xCoord, yCoord);

                if (turnOn && currentOn) {
                    v[0xF] |= 1;
                    turnOn = false;
                } else if (!turnOn && currentOn) {
                    turnOn = true;
                }

                mScreen.drawPixel(xCoord, yCoord, turnOn);
                mask = mask >> 1;
            }
        }
        lastOpDesc = "DRAW V" + toHex(xRegister, 1) + ", V" + toHex(yRegister, 1);
        */

        return SideEffect.DRAW;
    };

    /**
     * Move delay timer value into register defined by operand t
     */
    public BiFunction<Cpu, Integer, SideEffect> funcMOVED = (cpu, op) -> {
        int targetRegister = (op & 0x0F00) >> 8;
        cpu.setRegisterAt(targetRegister, cpu.getDelayRegister());

        return SideEffect.NONE;
    };

    /**
     * Wait for keypress and store in register defined by operand t
     */
    public BiFunction<Cpu, Integer, SideEffect> funcKEYD = (cpu, op) -> {
        // TODO

        return SideEffect.NONE;
    };

    /**
     * Load value in register defined by operand s into delay register
     */
    public BiFunction<Cpu, Integer, SideEffect> funcLOADD = (cpu, op) -> {
        int sourceRegister = (op & 0x0F00) >> 8;
        cpu.setDelayRegister(cpu.getRegisterAt(sourceRegister));

        return SideEffect.NONE;
    };

    /**
     * Load value in register defined by operand s into sound register
     */
    public BiFunction<Cpu, Integer, SideEffect> funcLOADS = (cpu, op) -> {
        int sourceRegister = (op & 0x0F00) >> 8;
        cpu.setSoundRegister(cpu.getRegisterAt(sourceRegister));

        return SideEffect.NONE;
    };

    /**
     * Add value in register defined by operand s to index
     */
    public BiFunction<Cpu, Integer, SideEffect> funcADDI = (cpu, op) -> {
        int sourceRegister = (op & 0x0F00) >> 8;
        cpu.setIndexRegisterI((short)(cpu.getIndexRegisterI() + cpu.getRegisterAt(sourceRegister)));

        return SideEffect.NONE;
    };

    /**
     * Load index with sprite from register defined by operand s
     */
    public BiFunction<Cpu, Integer, SideEffect> funcLDSPR = (cpu, op) -> {
        int sourceRegister = (op & 0x0F00) >> 8;
        cpu.setIndexRegisterI((short)(cpu.getRegisterAt(sourceRegister) * 5));

        return SideEffect.NONE;
    };

    /**
     * Store the binary coded decimal value of register defined by operand s at index
     *
     * Take the value stored in source and place the digits in the following
     * locations:
     *
     * hundreds -> memory[index]
     * tens     -> memory[index + 1]
     * ones     -> memory[index + 2]
     *
     */
    public BiFunction<Cpu, Integer, SideEffect> funcBCD = (cpu, op) -> {
        int sourceRegister = (op & 0x0F00) >> 8;
        int bcd = cpu.getRegisterAt(sourceRegister);
        cpu.getMemory().write(cpu.getIndexRegisterI(), bcd / 100);
        cpu.getMemory().write(cpu.getIndexRegisterI() + 1, (bcd % 100) / 10);
        cpu.getMemory().write(cpu.getIndexRegisterI() + 2, (bcd % 100) % 10);

        return SideEffect.NONE;
    };

    /**
     * Store all of the S registers in the memory pointed to by the index
     * register. The source register contains the number of S registers to
     * store. For example, to store all of the S registers, the source register
     * would contain the value 0xF.
     */
    public BiFunction<Cpu, Integer, SideEffect> funcSTOR = (cpu, op) -> {
        int numberOfRegisters = (op & 0x0F00) >> 8;
        for (int i = 0; i <= numberOfRegisters; i++) {
            cpu.getMemory().write(cpu.getRegisterAt(i), cpu.getIndexRegisterI() + i);
        }

        return SideEffect.NONE;
    };

    /**
     * Read all of the S registers from the memory pointed to by the index
     * register. The source register contains the number of S registers to load.
     * For example, to load all of the S registers, the source register would
     * contain the value 0xF.
     */
    public BiFunction<Cpu, Integer, SideEffect> funcSTOR = (cpu, op) -> {
        int numberOfRegisters = (op & 0x0F00) >> 8;
        for (int i = 0; i <= numberOfRegisters; i++) {
            cpu.getMemory().write(cpu.getRegisterAt(i), cpu.getIndexRegisterI() + i);
            cpu.setRegisterAt(i, cpu.getMemory().read(cpu.getIndexRegisterI() + 1));
        }

        return SideEffect.NONE;
    };

}