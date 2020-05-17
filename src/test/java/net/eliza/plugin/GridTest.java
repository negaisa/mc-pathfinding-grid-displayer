package net.eliza.plugin;

import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class GridTest {

    @Test
    public void testImport() throws IOException {
        Path resourceDirectory = Paths.get("src","test","resources");
        Path path = resourceDirectory.resolve("grid.dat");

        try (DataInputStream in = new DataInputStream(Files.newInputStream(path))) {
            Grid grid = Grid.importGrid(in);

            assertTrue(grid.isObstacle(1, 1, 1));
            assertTrue(grid.isObstacle(2, 2, 2));

            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        if (x == 1 && y == 1 && z == 1) {
                            continue;
                        }

                        if (x == 2 && y == 2 && z == 2) {
                            continue;
                        }

                        assertFalse(grid.isObstacle(x, y, z));
                    }
                }
            }
        }
    }

}
