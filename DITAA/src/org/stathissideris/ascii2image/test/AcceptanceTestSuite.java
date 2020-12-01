package org.stathissideris.ascii2image.test;

import org.junit.Before;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.stathissideris.ascii2image.core.CommandLineConverter;
import org.stathissideris.ascii2image.core.ConversionOptions;

@RunWith(Parameterized.class)
public class AcceptanceTestSuite {

    private String tempDir;
    private String inputName;
    private ConversionOptions options;

    private static final String INPUT_DIR = "tests/text";

    public AcceptanceTestSuite(String inFile){
        this.inputName = inFile;
        this.options = new ConversionOptions();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getInputNames(){
        File fileDir = new File(AcceptanceTestSuite.INPUT_DIR);
        File[] files = fileDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });

        Collection<Object[]> inputNames = new ArrayList<Object[]>();
        for(File f : files){
            String path = f.getName();
            // compute the root name without the extension (know it ends in .txt based on filter)
            String rootPath = path.substring(0, path.length() - 4);
            // check for an expected output file
            File outFile = new File(AcceptanceTestSuite.INPUT_DIR + "/" + rootPath + ".png");
            if(outFile.exists()) {
                inputNames.add(new Object[]{rootPath});
            }
        }
        return inputNames;
    }

    @Before
    public void setUp() throws IOException {
        Path dirPath = Files.createTempDirectory("ditaa");
        this.tempDir = dirPath.toString();
    }

    @Test(expected = Test.None.class)
    public void checkDITAAOutput() throws NoSuchAlgorithmException, IOException {
        String inputFile = AcceptanceTestSuite.INPUT_DIR + "/" + this.inputName + ".txt";
        String outputFile = this.tempDir + "/" + this.inputName + ".png";
        CommandLineConverter.runSimpleMode(options, inputFile, outputFile);

        // get the MD5 of the current output
        byte[] actualDigest = this.getMD5(outputFile);

        // get the MD5 of the expected output
        String expectedName = AcceptanceTestSuite.INPUT_DIR + "/" + this.inputName + ".png";
        byte[] expectedDigest = this.getMD5(expectedName);

        assert(Arrays.equals(actualDigest, expectedDigest));
    }

    private byte[] getMD5(String filename) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = Files.newInputStream(Paths.get(filename));
             DigestInputStream dis = new DigestInputStream(is, md))
        {
            int c = 0;
            while((c = dis.read()) != -1){
                continue;
            }
        }
        return md.digest();
    }
}
