package romatthe.dale.cpu;

public class Memory {

    private short[] memory;

    public Memory(int size) {
        this.memory = new short[size];
    }

    public short read(int address) {
        if (address > this.memory.length) {
            throw new IllegalArgumentException("Location must be less than memory size");
        }

        if (address < 0) {
            throw new IllegalArgumentException("Location must be 0 or larger");
        }

        return (short)(memory[address] & 0xFF);
    }

    public void write(int address, int value) {
        if (address > this.memory.length) {
            throw new IllegalArgumentException("Location must be less than memory size");
        }

        if (address < 0) {
            throw new IllegalArgumentException("Location must be 0 or larger");
        }

        memory[address] = (short)(value & 0xFF);
    }
}
