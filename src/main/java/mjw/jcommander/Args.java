package mjw.jcommander;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Args {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = {"-log", "-verbose"}, description = "Level of verbosity")
    private Integer verbose = 1;

    @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
    private String groups;

    @Parameter(names = "-debug", description = "Debug mode")
    private boolean debug = false;

    private Integer setterParameter;

    @Parameter(names = "-setterParameter", description = "A parameter annotation on a setter method")
    public void setParameter(Integer value) {
        this.setterParameter = value;
    }

    public static void main(String[] args) {
        Args arg = new Args();
        String[] argv = {"-log", "2", "-groups", "unit"};
        JCommander.newBuilder()
                .addObject(arg)
                .build()
                .parse(argv);
        assertEquals(arg.verbose.intValue(), 2);
        assertEquals(arg.groups, "unit");
        assertTrue(arg.parameters.isEmpty());
        assertFalse(arg.debug);
    }
}