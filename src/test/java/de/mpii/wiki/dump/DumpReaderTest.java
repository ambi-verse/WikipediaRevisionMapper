package de.mpii.wiki.dump;

import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.*;

import static org.junit.Assert.assertEquals;


public class DumpReaderTest {

    @Test
    public void verifyTargetDumpReaderProcessing() throws IOException, XMLStreamException {
        File tmpDump = File.createTempFile("wiki-dump", "xml");

        // base structure - "<mediawiki><page><title></title><id><id></page></mediawiki>"
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpDump));
        bw.write("<mediawiki>"
                + "<page>"
                + "<title>Test1</title>"
                + "<id>1</id>"
                + "<revision>"
                + "<id>1234556</id>"
                + "<text xml:space=\"preserve\">Information written about Test1.</text>"
                + "</revision>"
                + "</page>"
                + "<page>"
                + "<title>Test2</title>"
                + "<id>2</id>"
                + "<revision>"
                + "<id>1234556</id>"
                + "<text xml:space=\"preserve\">Information written about Test2</text>"
                + "</revision>"
                + "</page>"
                + "<page>"
                + "<title>Test3</title>"
                + "<id>3</id>"
                + "<revision>"
                + "<id>1234556</id>"
                + "<text xml:space=\"preserve\">#REDIRECT [[Test1]] {{R from CamelCase}}</text>"
                + "</revision>"
                + "</page>"
                + "<page>"
                + "<title>Test4</title>"
                + "<id>4</id>"
                + "<revision>"
                + "<id>1234556</id>"
                + "<text xml:space=\"preserve\">Test 4 may refer to * [[Test_4|Test-4]] * [[Some_Test|4th Test]] {{disambig}}</text>"
                + "</revision>"
                + "</page>"
                + "</mediawiki>");
        bw.close();

        DumpData dumpData = new DumpData(DumpType.TARGET);

        DumpReader.read(tmpDump, dumpData);

        assertEquals(4, dumpData.size());

        assertEquals(true, dumpData.isArticle(1));
        assertEquals(true, dumpData.isArticle(2));
        assertEquals(false, dumpData.isArticle(3));

        assertEquals(true, dumpData.isRedirect(3));
        assertEquals(1, dumpData.getRedirectedId(3));

        // disambiguation are processed only for Target Dump Type

        assertEquals(true, dumpData.isDisambiguation(4));

        tmpDump.delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void verifySourceDumpReaderProcessing() throws IOException, XMLStreamException {
        File tmpDump = File.createTempFile("wiki-dump", "xml");

        // base structure - "<mediawiki><page><title></title><id><id></page></mediawiki>"
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpDump));
        bw.write("<mediawiki>"
                + "<page>"
                + "<title>Test1</title>"
                + "<id>1</id>"
                + "<revision>"
                + "<id>1234556</id>"
                + "<text xml:space=\"preserve\">Information written about Test1.</text>"
                + "</revision>"
                + "</page>"
                + "<page>"
                + "<title>Test2</title>"
                + "<id>2</id>"
                + "<revision>"
                + "<id>1234556</id>"
                + "<text xml:space=\"preserve\">Information written about Test2</text>"
                + "</revision>"
                + "</page>"
                + "<page>"
                + "<title>Test3</title>"
                + "<id>3</id>"
                + "<revision>"
                + "<id>1234556</id>"
                + "<text xml:space=\"preserve\">#REDIRECT [[Test1]] {{R from CamelCase}}</text>"
                + "</revision>"
                + "</page>"
                + "<page>"
                + "<title>Test4</title>"
                + "<id>4</id>"
                + "<revision>"
                + "<id>1234556</id>"
                + "<text xml:space=\"preserve\">Test 4 may refer to * [[Test_4|Test-4]] * [[Some_Test|4th Test]] {{disambig}}</text>"
                + "</revision>"
                + "</page>"
                + "</mediawiki>");
        bw.close();

        DumpData dumpData = new DumpData(DumpType.SOURCE);

        DumpReader.read(tmpDump, dumpData);

        assertEquals(4, dumpData.size());

        assertEquals(true, dumpData.isArticle(1));
        assertEquals(true, dumpData.isArticle(2));

        assertEquals(1, dumpData.getRedirectedId(3));
        assertEquals(true, dumpData.isRedirect(3));

        // disambiguations are not processed for source dump
        assertEquals(false, dumpData.isArticle(3));
        assertEquals(false, dumpData.isArticle(4));

        assertEquals(true, dumpData.isDisambiguation(4));

        // remove tmp files
        tmpDump.delete();
    }
}