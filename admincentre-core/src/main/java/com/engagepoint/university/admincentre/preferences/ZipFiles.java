package com.engagepoint.university.admincentre.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.KeyType;

public class ZipFiles {

    private static final String KEY_TYPE_SEPARATOR = "-";
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipFiles.class);
    List<String> filesListInDir = new ArrayList<String>();

    /**
     * Exporting preferences into zip archive
     *
     * @param rootPreferences preference from which archive starts
     * @param zipDirName path and name of archive
     * @throws BackingStoreException now zip files one by one create
     * ZipOutputStream to write to the zip file
     */
    public void exportZipPreferences(Preferences rootPreferences, String zipDirName)
            throws BackingStoreException {
        try {
            OutputStream fos = new FileOutputStream(zipDirName);
            exportZipPreferences(rootPreferences, fos);
        } catch (IOException e) {
            LOGGER.warn("Failed to export preference: " + rootPreferences + " to: " +  zipDirName, e);
        }
    }

    public void exportZipPreferencesXML(Preferences rootPreferences, String zipDirName)
            throws BackingStoreException {
        try {
            OutputStream fos = new FileOutputStream(zipDirName);
            exportZipPreferencesXML(rootPreferences, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportZipPreferencesXML(Preferences rootPreferences, OutputStream fos)
            throws BackingStoreException {
        try {
            ZipOutputStream zos = new ZipOutputStream(fos);
            writeByteXML(zos, rootPreferences, rootPreferences.absolutePath());

            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void exportZipPreferences(Preferences rootPreferences, OutputStream fos)
            throws BackingStoreException {
        try {

            // now zip files one by one
            // create ZipOutputStream to write to the zip file
            ZipOutputStream zos = new ZipOutputStream(fos);
            writeByte(zos, rootPreferences,
                    rootPreferences.absolutePath());

            zos.close();
            fos.close();
        } catch (IOException e) {
            LOGGER.warn("Failed to export preference: " + rootPreferences + "/n" +
                    "Probably it is something wrong with the Output Stream", e);
        }
    }

    private void writeByteXML(ZipOutputStream zos, Preferences rootPreferences, String rootPath)
            throws BackingStoreException, IOException {
        String rootName = rootPreferences.absolutePath();
        String rightFormatPath = rootName.replaceFirst(rootPath, "");
        System.out.println("Zipping " + rightFormatPath);
        // Preferences childNode = rootPreferences.node(rootName);
        // for ZipEntry we need to keep only relative file path, so we
        // used substring on absolute path
        String[] keys = rootPreferences.keys();
        if (keys.length != 0) {
            String zipEntryName = rightFormatPath + "/" + rootPreferences.name() + ".xml";
            if (zipEntryName.charAt(0) == '/') {
                zipEntryName = zipEntryName.substring(1);
            }
            ZipEntry ze = new ZipEntry(zipEntryName);
            zos.putNextEntry(ze);
            byte[] openingTag = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"

            + "\n<properties>").getBytes();
            zos.write(openingTag, 0, openingTag.length);
            for (String keyName : keys) {
                String writedString = "\n<entry ";
                Key key = ((NodePreferences) rootPreferences).getKey(keyName);
                String preferenceName = saveConvert(key.getName(), true);
                writedString = writedString + "key=\"" + preferenceName + "\" ";
                // + KEY_TYPE_SEPARATOR + key.getType(), true);
                String preferenceType = saveConvert(key.getType().toString(), true);
                writedString = writedString + "type=\"" + preferenceType + "\">";
                String preferenceValue = saveConvert(key.getValue(), false);
                writedString = writedString + preferenceValue + "</entry>";
                byte[] writedByte = writedString.getBytes();
                zos.write(writedByte, 0, writedByte.length);
            }
            byte[] closingTag = "\n</properties>".getBytes();
            zos.write(closingTag, 0, closingTag.length);
            zos.closeEntry();
        }
        for (String filePath : rootPreferences.childrenNames()) {
            writeByte(zos, rootPreferences.node(filePath), rootPath);
        }

    }

/**
 * 
 * @param zos
 * @param rootPreferences
 * @param rootPath
 * @throws BackingStoreException
 * @throws IOException
 * For ZipEntry we need to keep only relative file path, so we used substring on absolute path.
 */
    private void writeByte(ZipOutputStream zos, Preferences rootPreferences, String rootPath)
            throws BackingStoreException,
            IOException {
        String rootName = rootPreferences.absolutePath();
        String rightFormatPath = rootName.replaceFirst(rootPath, "");
        System.out.println("Zipping " + rightFormatPath);
        String[] keys = rootPreferences.keys();
        if (keys.length != 0) {
            String zipEntryName = rightFormatPath + "/"
                    + rootPreferences.name() + ".properties";
            if (zipEntryName.charAt(0) == '/') {
                zipEntryName = zipEntryName.substring(1);
            }
            ZipEntry ze = new ZipEntry(zipEntryName);
            zos.putNextEntry(ze);
            for (String keyName : keys) {
                Key key = ((NodePreferences) rootPreferences).getKey(keyName);
                String preferenceName = saveConvert(key.getName()
                        + KEY_TYPE_SEPARATOR + key.getType(), true);
                String preferenceValue = saveConvert(key.getValue(), false);
                byte[] writedByte = (preferenceName + "=" + preferenceValue + "\n")
                        .getBytes();
                zos.write(writedByte, 0, writedByte.length);
            }
            zos.closeEntry();
        }
        for (String filePath : rootPreferences.childrenNames()) {
            writeByte(zos, rootPreferences.node(filePath), rootPath);
        }
    }

    /*
     * Converts unicodes to encoded &#92;uxxxx and escapes special characters
     * with a preceding slash.
     * Handle common case first, selecting largest block that avoids the specials below.
     */
    private String saveConvert(String theString, boolean escapeSpace) {
        boolean escapeUnicode = true;
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuffer outBuffer = new StringBuffer(bufLen);
        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch (aChar) {
                case ' ':
                    if (x == 0 || escapeSpace) {
                        outBuffer.append('\\');
                    }
                    outBuffer.append(' ');
                    break;
                case '\t':
                    outBuffer.append('\\');
                    outBuffer.append('t');
                    break;
                case '\n':
                    outBuffer.append('\\');
                    outBuffer.append('n');
                    break;
                case '\r':
                    outBuffer.append('\\');
                    outBuffer.append('r');
                    break;
                case '\f':
                    outBuffer.append('\\');
                    outBuffer.append('f');
                    break;
                case '=': // Fall through
                case ':': // Fall through
                case '#': // Fall through
                case '!':
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >> 8) & 0xF));
                        outBuffer.append(toHex((aChar >> 4) & 0xF));
                        outBuffer.append(toHex(aChar & 0xF));
                    } else {
                        outBuffer.append(aChar);
                    }
                    break;
            }
        }
        return outBuffer.toString();
    }

    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }
    /**
     * A table of hex digits
     */
    private static final char[] hexDigit = {'0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public void importZipPreferences(String zipDirName, String preferencesPath) throws IOException {
        importZipPreferences(new FileInputStream(zipDirName), preferencesPath);
    }

    private void load0(LineReader lr, String rootPath, String relativePath) throws IOException {
        String path = rootPath + relativePath;
        if (path.indexOf("//") != -1) {
            path = path.replace("//", "/");
        }
        NodePreferences currentPreferences = (NodePreferences) new NodePreferences(null, "").node(path);
        char[] convtBuf = new char[1024];
        int limit;
        int keyLen;
        int valueStart;
        char c;
        boolean hasSep;
        boolean precedingBackslash;

        while ((limit = lr.readLine()) >= 0) {
            c = 0;
            keyLen = 0;
            valueStart = limit;
            hasSep = false;
            precedingBackslash = false;
            while (keyLen < limit) {
                c = lr.lineBuf[keyLen];
                if ((c == '=' || c == ':') && !precedingBackslash) {
                    valueStart = keyLen + 1;
                    hasSep = true;
                    break;
                } else if ((c == ' ' || c == '\t' || c == '\f') && !precedingBackslash) {
                    valueStart = keyLen + 1;
                    break;
                }
                if (c == '\\') {
                    precedingBackslash = !precedingBackslash;
                } else {
                    precedingBackslash = false;
                }
                keyLen++;
            }
            while (valueStart < limit) {
                c = lr.lineBuf[valueStart];
                if (c != ' ' && c != '\t' && c != '\f') {
                    if (!hasSep && (c == '=' || c == ':')) {
                        hasSep = true;
                    } else {
                        break;
                    }
                }
                valueStart++;
            }
            String key = loadConvert(lr.lineBuf, 0, keyLen, convtBuf);
            String value = loadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);
            int separatorIndex = key.indexOf(KEY_TYPE_SEPARATOR);
            KeyType type = KeyType.valueOf(key.substring(separatorIndex + 1));
            key = key.substring(0, separatorIndex);
            System.out.println("Key: " + key + "\nType: " + type + "\nValue: " + value);
            currentPreferences.put(key, type, value);
        }
    }

    class LineReader {

        public LineReader(InputStream inStream) {
            this.inStream = inStream;
            inByteBuf = new byte[8192];
        }

        public LineReader(Reader reader) {
            this.reader = reader;
            inCharBuf = new char[8192];
        }
        byte[] inByteBuf;
        char[] inCharBuf;
        char[] lineBuf = new char[1024];
        int inLimit = 0;
        int inOff = 0;
        InputStream inStream;
        Reader reader;

        int readLine() throws IOException {
            int len = 0;
            char c = 0;
            boolean skipWhiteSpace = true;
            boolean isCommentLine = false;
            boolean isNewLine = true;
            boolean appendedLineBegin = false;
            boolean precedingBackslash = false;
            boolean skipLF = false;

            while (true) {
                if (inOff >= inLimit) {
                    inLimit = (inStream == null) ? reader.read(inCharBuf)
                            : inStream.read(inByteBuf);
                    inOff = 0;
                    if (inLimit <= 0) {
                        if (len == 0 || isCommentLine) {
                            return -1;
                        }
                        return len;
                    }
                }
                if (inStream != null) {
                    //The line below is equivalent to calling a
                    //ISO8859-1 decoder.
                    c = (char) (0xff & inByteBuf[inOff++]);
                } else {
                    c = inCharBuf[inOff++];
                }
                if (skipLF) {
                    skipLF = false;
                    if (c == '\n') {
                        continue;
                    }
                }
                if (skipWhiteSpace) {
                    if (c == ' ' || c == '\t' || c == '\f') {
                        continue;
                    }
                    if (!appendedLineBegin && (c == '\r' || c == '\n')) {
                        continue;
                    }
                    skipWhiteSpace = false;
                    appendedLineBegin = false;
                }
                if (isNewLine) {
                    isNewLine = false;
                    if (c == '#' || c == '!') {
                        isCommentLine = true;
                        continue;
                    }
                }

                if (c != '\n' && c != '\r') {
                    lineBuf[len++] = c;
                    if (len == lineBuf.length) {
                        int newLength = lineBuf.length * 2;
                        if (newLength < 0) {
                            newLength = Integer.MAX_VALUE;
                        }
                        char[] buf = new char[newLength];
                        System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
                        lineBuf = buf;
                    }
                    //flip the preceding backslash flag
                    if (c == '\\') {
                        precedingBackslash = !precedingBackslash;
                    } else {
                        precedingBackslash = false;
                    }
                } else {
                    // reached EOL
                    if (isCommentLine || len == 0) {
                        isCommentLine = false;
                        isNewLine = true;
                        skipWhiteSpace = true;
                        len = 0;
                        continue;
                    }
                    if (inOff >= inLimit) {
                        inLimit = (inStream == null)
                                ? reader.read(inCharBuf)
                                : inStream.read(inByteBuf);
                        inOff = 0;
                        if (inLimit <= 0) {
                            return len;
                        }
                    }
                    if (precedingBackslash) {
                        len -= 1;
                        //skip the leading whitespace characters in following line
                        skipWhiteSpace = true;
                        appendedLineBegin = true;
                        precedingBackslash = false;
                        if (c == '\r') {
                            skipLF = true;
                        }
                    } else {
                        return len;
                    }
                }
            }
        }
    }

    /*
     * Converts encoded &#92;uxxxx to unicode chars
     * and changes special saved chars to their original forms
     */
    private String loadConvert(char[] in, int off, int len, char[] convtBuf) {
        if (convtBuf.length < len) {
            int newLen = len * 2;
            if (newLen < 0) {
                newLen = Integer.MAX_VALUE;
            }
            convtBuf = new char[newLen];
        }
        char aChar;
        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = in[off++];
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed \\uxxxx encoding.");
                        }
                    }
                    out[outLen++] = (char) value;
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    out[outLen++] = aChar;
                }
            } else {
                out[outLen++] = aChar;
            }
        }
        return new String(out, 0, outLen);
    }

    /**
     * test method
     */
    public void importZipPreferences(InputStream is, String preferencesPath) throws IOException {
        ZipInputStream zis = new ZipInputStream(is);

        ZipEntry entry = zis.getNextEntry();

        while (entry != null) {

            String path = entry.getName();
            int lastIndex = path.lastIndexOf('/');
            if (lastIndex != -1) {
                path = "/" + path.substring(0, lastIndex);
            } else {
                path = "";
            }
            System.out.println(path);
            // InputStream stream = zis.
            load0(new LineReader(zis), preferencesPath, path);
            // String myString = IOUtils.toString(stream, "UTF-8");
            // System.out.println(myString);
            // stream.close();
            entry = zis.getNextEntry();
        }
        zis.close();
        // zipFile.close();
    }

    public void importZipPreferencesXML(String zipDirName, String preferencesPath)
            throws IOException {
        importZipPreferencesXML(new FileInputStream(zipDirName), preferencesPath);
    }

    public void importZipPreferencesXML(InputStream is, String preferencesPath) throws IOException {
        ZipInputStream zis = new ZipInputStream(is);

        ZipEntry entry = zis.getNextEntry();

        while (entry != null) {

            String path = entry.getName();
            int lastIndex = path.lastIndexOf('/');
            if (lastIndex != -1) {
                path = "/" + path.substring(0, lastIndex);
            } else {
                path = "";
            }
            System.out.println(path);
            // InputStream stream = zis.
            loadXML(streamToFile(zis), preferencesPath, path);
            // String myString = IOUtils.toString(stream, "UTF-8");
            // System.out.println(myString);
            // stream.close();
            entry = zis.getNextEntry();
        }
        zis.close();

    }

    private static final String PREFIX = "stream2file";
    private static final String SUFFIX = ".tmp";
    private File streamToFile(InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        // read from is to buffer
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }

        return tempFile;
    }
    
    static void loadXML(File file, String rootPath, String relativePath) throws IOException,
            InvalidPropertiesFormatException {
        String path = rootPath + relativePath;
        if (path.indexOf("//") != -1) {
            path = path.replace("//", "/");
        }
        NodePreferences currentPreferences = (NodePreferences) new NodePreferences(null, "")
                .node(path);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        Document doc;
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
            // optional, but recommended
            // read this -
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getDocumentElement().getChildNodes();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("key id : " + eElement.getAttribute("key"));
                    System.out.println("type id : " + eElement.getAttribute("type"));

                    System.out.println("Value Name : " + eElement.getTextContent());
                    currentPreferences.put(eElement.getAttribute("key"),
                            KeyType.valueOf(eElement.getAttribute("type")),
                            eElement.getTextContent());
                }
            }
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

}
