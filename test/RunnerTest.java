
import org.junit.jupiter.api.Test;
import org.pdk.engine.Runner;
import org.pdk.converters.Converter;
import org.pdk.modules.ModuleManager;
import org.pdk.store.NodeBuilder;
import org.pdk.store.NodeSerializer;
import org.pdk.store.Storage;
import org.pdk.store.model.data.BooleanData;
import org.pdk.store.model.data.FileData;
import org.pdk.store.model.node.Node;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class RunnerTest {

    @Test
    void run() throws IOException {
        File[] list = new File("test_res/run").listFiles();
        if (list == null) return;
        Storage storage = new Storage("out/run", true);
        NodeBuilder builder = new NodeBuilder(storage);
        Runner runner = new Runner(builder, new ModuleManager(builder));
        Converter converter = storage.converterManager.fileConverters.get("js");
        for (File f : list) {
            if (f.isFile()) {
                Node module = new Node(storage);
                FileData data = new FileData(storage, new FileInputStream(f));
                Object parserResult = converter.getParser().parse(data);
                Node node = converter.getBuilder().build(module, parserResult);

                try {
                    runner.run(node);
                    Node[] locals = builder.set(node).getLocals();
                    BooleanData testVar = (BooleanData) builder.set(locals[locals.length - 1]).getValue();
                    if (!testVar.value)
                        printNode(storage, node);
                    assertTrue(testVar.value);
                } catch (Exception e) {
                    e.printStackTrace();
                    printNode(storage, node);
                    fail(e.getMessage());
                }
            }
        }
    }

    private void printNode(Storage storage, Node node) {
        String str = new Scanner(new NodeSerializer(storage, node, 15)).useDelimiter("\\A").next();
        System.out.println(str);
    }
}
