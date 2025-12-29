package mjw.ffm;

import java.lang.foreign.*;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.VarHandle;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HexFormat;
import java.util.Set;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static java.nio.file.StandardOpenOption.*;

public class MemoryMappingExample {

    static final SequenceLayout ptsLayout
            = MemoryLayout.sequenceLayout(10,
            MemoryLayout.structLayout(
                    ValueLayout.JAVA_INT.withName("x"),
                    ValueLayout.JAVA_INT.withName("y")));

    static final VarHandle xHandle
            = ptsLayout.varHandle(
            PathElement.sequenceElement(),
            PathElement.groupElement("x"));

    static final VarHandle yHandle
            = ptsLayout.varHandle(
            PathElement.sequenceElement(),
            PathElement.groupElement("y"));

    static void main(String[] args) {
        MemoryMappingExample myApp = new MemoryMappingExample();
        try {
            Files.deleteIfExists(Paths.get("point-array.data"));
            myApp.createFile();
            myApp.readFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void createFile() throws Exception {
        try (var fc = FileChannel.open(Path.of("point-array.data"),
                Set.of(CREATE, READ, WRITE));
             Arena arena = Arena.ofConfined()) {

            MemorySegment mapped = fc.map(READ_WRITE, 0L, ptsLayout.byteSize(), arena);

            System.out.println("Empty mapped segment:");
            System.out.println(toHex(mapped));

            for (int i = 0; i < ptsLayout.elementCount(); i++) {
                int xValue = i;
                int yValue = i * 10;
                xHandle.set(mapped, 0L, (long) i, xValue);
                yHandle.set(mapped, 0L, (long) i, yValue);
            }

            System.out.println("Populated mapped segment:");
            System.out.println(toString(mapped));
            System.out.println("Populated mapped segment in hex:");
            System.out.println(toHex(mapped));
        }
    }


    void readFile() throws Exception {
        try (var fc = FileChannel.open(Path.of("point-array.data"),
                Set.of(SPARSE, READ));
             Arena arena = Arena.ofConfined()) {

            MemorySegment mapped = fc.map(READ_ONLY, 0L, ptsLayout.byteSize(), arena);

            System.out.println("Contents of point-array.data:");
            System.out.println(toString(mapped));
        }
    }

    static String toString(MemorySegment seg) {
        String outputString = "";
        for (int i = 0; i < ptsLayout.elementCount(); i++) {
            int xVal = (int) xHandle.get(seg, 0L, (long) i);
            int yVal = (int) yHandle.get(seg, 0L, (long) i);
            outputString += "(" + xVal + ", " + yVal + ")";
            if ((i + 1 != ptsLayout.elementCount())) outputString += "\n";
        }
        return outputString;
    }

    static String toHex(MemorySegment seg) {
        String outputString = "";
        HexFormat formatter = HexFormat.of();

        byte[] byteArray = seg.toArray(java.lang.foreign.ValueLayout.JAVA_BYTE);

        for (int i = 0; i < byteArray.length; i++) {
            outputString += formatter.toHexDigits(byteArray[i]) + " ";
            if ((i + 1) % 8 == 0 && (i + 1) % 16 != 0) {
                outputString += " ";
            }
            if ((i + 1) % 16 == 0 && (i + 1) < byteArray.length) {
                outputString += "\n";
            }
        }
        return outputString;
    }
}