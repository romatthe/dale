package romatthe.dale.cpu;

import org.apache.commons.io.IOUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;

public class Cpu {

    private short opcode = 0;
    private short indexRegisterI = 0;
    private int programCounter = 0x200;   // Program counter starts at 0x200
    private int stackPointer = 0;
    private Memory memory = new Memory(0x1000);
    private short[] registers = new short[16];

    public Cpu() throws IOException {
        // Clear display
        // Clear stack
        // Clear registers V0-VF
        // Clear memory

        InputStream in = this.getClass().getClassLoader().getResourceAsStream("PONG");
        byte[] data = IOUtils.toByteArray(in);

        // Load the fontset into memory
        for(int i = 0; i < 80; i++) {
            //TODO this.memory[i] = chip8_fontset[i];
        }

        // Load the program into memory
        for(int i = 0; i < data.length; ++i)
            this.memory.write(i + 512, data[i]);

        // Reset timers
        this.run();
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public short getOpcode() {
        return opcode;
    }

    public void setOpcode(short opcode) {
        this.opcode = opcode;
    }

    public short getIndexRegisterI() {
        return indexRegisterI;
    }

    public void setIndexRegisterI(short indexRegisterI) {
        this.indexRegisterI = indexRegisterI;
    }

    public int getStackPointer() {
        return stackPointer;
    }

    public void setStackPointer(int stackPointer) {
        this.stackPointer = stackPointer;
    }

    public short getRegisterAt(int register) {
        return this.registers[register];
    }

    public void setRegisterAt(int register, short value) {
        this.registers[register] = value;
    }

    private void run() {
        while(true) {
            this.step();
        }
    }

    private void step() {
        // Fetch the entire opcode
        // The Chip 8 Opcodes consist of 2 bytes each, so we much fetch them and merge them
        int opcode = this.getNextOpcode();

        // The first switch inspects the 4 most significant bits
        // Therefore we do `opcode & 0xF000`
        switch(opcode & 0xF000) {
            case 0x0000:
                switch(opcode) {
                    case 0x00E0: System.out.println("0x00E0"); break;
                    case 0x00EE: System.out.println("0x00EE"); break;
                    default: System.out.println("0x0NNN"); break;
                }
                break;
            case 0x1000: System.out.println("0x1NNN"); break;
            case 0x2000: System.out.println("0x2NNN"); break;
            case 0x3000: System.out.println("0x3XNN"); break;
            case 0x4000: System.out.println("0x4XNN"); break;
            case 0x5000: System.out.println("0x5XY0"); break;
            case 0x6000: System.out.println("0x6XNN"); break;
            case 0x7000: System.out.println("0x7XNN"); break;
            case 0x8000:
                // The first switch inspects the 4 least significant bits
                // Therefore we do `opcode & 0x000F`
                switch(opcode & 0x000F) {
                    case 0x0000: System.out.println("0x8XY0"); break;
                    case 0x0001: System.out.println("0x8XY1"); break;
                    case 0x0002: System.out.println("0x8XY2"); break;
                    case 0x0003: System.out.println("0x8XY3"); break;
                    case 0x0004: System.out.println("0x8XY4"); break;
                    case 0x0005: System.out.println("0x8XY5"); break;
                    case 0x0006: System.out.println("0x8XY6"); break;
                    case 0x0007: System.out.println("0x8XY7"); break;
                    case 0x000E: System.out.println("0x8XYE"); break;
                }
                break;
            case 0x9000: System.out.println("0x9XY0"); break;
            case 0xA000: System.out.println("0xANNN"); break;
            case 0xB000: System.out.println("0xBNNN"); break;
            case 0xC000: System.out.println("0xCXNN"); break;
            case 0xD000: System.out.println("0xDXYN"); break;
            case 0xE000:
                // The first switch inspects the 4 least significant bits
                // Therefore we do `opcode & 0x000F`
                switch(opcode & 0x000F) {
                    case 0x0001: System.out.println("0xEXA1"); break;
                    case 0x000E: System.out.println("0xEX9E"); break;
                }
                break;
            case 0xF000:
                // The first switch inspects the 8 least significant bits
                // Therefore we do `opcode & 0x00FF`
                switch(opcode & 0x00FF) {
                    case 0x0007: System.out.println("0xFX07"); break;
                    case 0x000A: System.out.println("0xFX0A"); break;
                    case 0x0015: System.out.println("0xFX15"); break;
                    case 0x0018: System.out.println("0xFX18"); break;
                    case 0x001E: System.out.println("0xFX1E"); break;
                    case 0x0029: System.out.println("0xFX29"); break;
                    case 0x0033: System.out.println("0xFX33"); break;
                    case 0x0055: System.out.println("0xFX55"); break;
                    case 0x0065: System.out.println("0xFX65"); break;
                }
                break;
            default:
                throw new NotImplementedException();
        }

        this.programCounter += 2;
    }

    private int getNextOpcode() {
        return ((this.memory.read(this.programCounter) << 8) | (0x00FF & this.memory.read(this.programCounter +1)))  & 0xFFFF;
    }

}
