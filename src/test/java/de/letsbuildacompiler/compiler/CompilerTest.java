package de.letsbuildacompiler.compiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sun.org.apache.bcel.internal.util.ClassPath.ClassFile;

public class CompilerTest {

    private Path tempDir;

    @BeforeMethod
    public void createTempDir() throws IOException {
        //temporäres Verzeichnis anlegen
        tempDir = Files.createTempDirectory("compilerTest");
    }

    @AfterMethod
    public void deleteTempDir() {
        deleteRecursive(tempDir.toFile());
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursive(child);
            }
        }
        if (!file.delete()) {
            throw new Error("Could not delete file <" + file + ">");
        }
    }

    @Test(dataProvider = "provideCodeExpectedText")
    public void runningCodeOutputsExpectedText(String code, String expectedText) throws Exception {
        // testen das bestimmter Output ausgegeben wurde, welchen wir compiliert und laufen gelassen haben

        //execution
        String actualOutput = compileAndRun(code); //der tatsächliche Output

        //evaluation
        Assert.assertEquals(actualOutput, expectedText); //was wir erwarten
    }

    @DataProvider
    public Object[][] provideCodeExpectedText() {
        return new Object[][] {
                { "println(1+2);", "3\n" },
                { "println(1+2+42);", "45\n" },
                { "println(1); println(2);", "1\n2\n" },
                { "println(3-2);", "1\n" },
                { "println(2*3);", "6\n" },
                { "println(2+3*3);", "11\n" },
                { "println(9-2*3);", "3\n" },
        };
    }

    private String compileAndRun(String code) throws Exception {
        //code muss in Jasmin Code umgeschrieben werden
        code = Main.compileToJasminCode(new ANTLRInputStream(code));

        //Code den wir übergeben bekommen muss kompiliert werden | dazu brauchen wir das Jasmin jar
        ClassFile classFile = new ClassFile();
        classFile.readJasmin(new StringReader(code), "", false);

        //ClassFile ist momentan nur in Memory und das müssen wir noch in eine Datei schreiben
        Path outputPath = tempDir.resolve(classFile.getClassName() + ".class");
        classFile.write(Files.newOutputStream(outputPath));

        //die klasse die wir generiert haben müssen wir ausführen und das Ergebnis zurück geben
        return runJavaClass(tempDir, classFile.getClassName());
    }

    private String runJavaClass(Path dir, String className) throws Exception {
        //neuen Java Prozess starten (zur Sicherheit damit alles seperat ist)
        Process process = Runtime.getRuntime().exec(new String[] { "java", "-cp", dir.toString(), className });

        //vom Prozess den Outputstream holen welcher für uns der Inputsream ist
        try (InputStream in = process.getInputStream()) {
            //einlesen | Scanner kann von Input einlesen
            return new Scanner(in).useDelimiter("\\A").next();
        }
    }
}
