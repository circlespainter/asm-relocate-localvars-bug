import com.google.common.io.ByteStreams;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class ASMTest {
    public static void main(String[] args) throws IOException {
        final byte[] c = ByteStreams.toByteArray(ASMTest.class.getClassLoader().getResourceAsStream("Val.class"));

        final ClassReader cr = new ClassReader(c);
        final ClassWriter cw = new ClassWriter(0);
        final ClassVisitor cv = new RemappingClassAdapter(cw, new Remapper() {
            @Override
            public String mapDesc(String desc) {
                return super.mapDesc(desc);
            }
        });
        cr.accept(cv, ClassReader.EXPAND_FRAMES);

        final byte[] c1 = cw.toByteArray();
        writeToFile("Val-remapped.class", c1);
    }

    private static void writeToFile(String name, byte[] data) {
        try (OutputStream os = Files.newOutputStream(Paths.get(name), StandardOpenOption.CREATE_NEW)) {
            os.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ASMTest() {}
}
