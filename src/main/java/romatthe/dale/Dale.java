package romatthe.dale;

import romatthe.dale.cpu.Cpu;

import java.io.IOException;
import java.nio.file.Path;

public class Dale {
    public static void main(String[] args) throws IOException {
        Path path = java.nio.file.FileSystems.getDefault().getPath("C:/temp");
        Cpu cpu = new Cpu();
    }
}
