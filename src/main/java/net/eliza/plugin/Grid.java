package net.eliza.plugin;

import java.io.DataInput;
import java.io.IOException;
import java.util.BitSet;

public class Grid {

    private final int width;
    private final int height;
    private final BitSet data;

    Grid(int width, int height, BitSet data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public boolean isObstacle(int x, int y, int z) {
       return data.get(x + width * (y + width * z));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return width * width * height;
    }

    public static Grid importGrid(DataInput in) throws IOException {
        int width = in.readInt();
        int height = in.readInt();

        int length = width * width * height;
        int lengthWithPadding = (int) Math.ceil(length / 8.0);

        byte[] bytes = new byte[lengthWithPadding];
        in.readFully(bytes);

        return new Grid(width, height, BitSet.valueOf(bytes));
    }

}
