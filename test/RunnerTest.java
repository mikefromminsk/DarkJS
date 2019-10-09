
import org.junit.jupiter.api.Test;
import org.pdk.engine.Runner;
import org.pdk.files.converters.Converter;
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
        Storage storage = new Storage("out/run");
        Runner runner = new Runner(new NodeBuilder(storage));
        Converter converter = storage.converterManager.fileConverters.get("js");
        NodeBuilder builder = new NodeBuilder(storage);
        for (File f : list) {
            if (f.isFile()) {
                Node module = new Node(storage);
                FileData data = new FileData(storage, new FileInputStream(f));
                Node node =  converter.getBuilder().build(module, converter.getParser().parse(data));
                runner.run(node);
                Node[] locals = builder.set(node).getLocals();
                BooleanData testVar = (BooleanData) builder.set(locals[locals.length - 1]).getValue();
                assertTrue(testVar.value);
                if (!testVar.value){
                    System.out.println(f.getName());
                    System.out.println(new Scanner(new NodeSerializer(storage, node)).useDelimiter("\\A").next());
                    return;
                }

            }
        }
        assertTrue(true);
    }
}
