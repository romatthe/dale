package romatthe.dale.cpu;

import org.apache.commons.io.IOUtils;
import static romatthe.dale.patterns.InstructionDecompPatterns.*;

import java.io.IOException;
import java.io.InputStream;

import static javaslang.API.$;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Patterns.*;
import static javaslang.Patterns.Tuple4;

public class Cpu {

    private byte[] memory = new byte[4096];
    private short programCounter = 0x200;   // Program counter starts at 0x200
    private short opcode = 0;
    private short indexRegisterI = 0;
    private short stackPointer = 0;

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
            memory[i + 512] = data[i];

        // Reset timers
        this.run();
    }

    private void run() {
        while(true) {
            this.step();
        }
    }

    private void step() {
        // Fetch the entire opcode
        // The Chip 8 Opcodes consist of 2 bytes each, so we much fetch them and merge them
        Instruction instruction = new Instruction(this.getNextOpcode());

        String returnVal = Match(instruction).of(
            Case(Instruction($(0x0), $(0x0), $(0xE), $(0x0)), ()    -> "00E0"),
            Case(Instruction($(0x0), $(0x0), $(0xE), $(0xE)), ()    -> "00EE"),
            Case(Instruction($(0x0), $(), $(), $()), ()             -> "ONNN"),
            Case(Instruction($(), $(), $(), $()), ()                -> "(catch all)")
        );

        System.out.println(returnVal);

        this.programCounter += 2;

        // Update timers
        //if(delay_timer > 0)
        //    --delay_timer;
        //
        //if(sound_timer > 0)
        //{
        //    if(sound_timer == 1)
        //        printf("BEEP!\n");
        //    --sound_timer;
        //}
    }

    private int getNextOpcode() {
        return (this.memory[this.programCounter] << 8) | (0x00FF & this.memory[this.programCounter +1] & 0x0FFF);
    }

}
