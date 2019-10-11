
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
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class RunnerTest {

    String streamToString(InputStream stream){
        return new Scanner(stream).useDelimiter("\\A").next();
    }

    @Test
    void run() throws IOException {
        File[] list = new File("test_res/run").listFiles();
        if (list == null) return;
        Storage storage = new Storage("out/run", true);
        Runner runner = new Runner(new NodeBuilder(storage));
        Converter converter = storage.converterManager.fileConverters.get("js");
        NodeBuilder builder = new NodeBuilder(storage);
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
                    assertTrue(testVar.value);
                    if (!testVar.value) {
                        System.out.println(streamToString(new NodeSerializer(storage, node)));
                        return;
                    }
                    assertTrue(true);
                } catch (Exception e) {
                    System.out.println(streamToString(new NodeSerializer(storage, node)));
                    fail(e.getMessage());
                }
            }
        }
    }
}
