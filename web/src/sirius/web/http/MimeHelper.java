package sirius.web.http;

import com.google.common.io.Files;
import sirius.kernel.commons.Strings;

import java.util.Map;
import java.util.TreeMap;

public class MimeHelper {
    private static final String APPLICATION_X_SHOCKWAVE_FLASH = "application/x-shockwave-flash";
    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_JPEG = "image/jpeg";
    public static final String APPLICATION_PDF = "application/pdf".intern();
    public static final String TEXT_CSS = "text/css".intern();
    public static final String TEXT_JAVASCRIPT = "text/javascript".intern();

    private static final Map<String, String> mimeTable = new TreeMap<String, String>();

    static {
        mimeTable.put("ai", "application/postscript");
        mimeTable.put("aif", "audio/x-aiff");
        mimeTable.put("aifc", "audio/x-aiff");
        mimeTable.put("aiff", "audio/x-aiff");
        mimeTable.put("asc", "text/plain");
        mimeTable.put("atom", "application/atom+xml");
        mimeTable.put("au", "audio/basic");
        mimeTable.put("avi", "video/x-msvideo");
        mimeTable.put("bcpio", "application/x-bcpio");
        mimeTable.put("bin", "application/octet-stream");
        mimeTable.put("bmp", "image/bmp");
        mimeTable.put("cdf", "application/x-netcdf");
        mimeTable.put("cgm", "image/cgm");
        mimeTable.put("class", "application/octet-stream");
        mimeTable.put("cpio", "application/x-cpio");
        mimeTable.put("cpt", "application/mac-compactpro");
        mimeTable.put("csh", "application/x-csh");
        mimeTable.put("css", TEXT_CSS);
        mimeTable.put("csv", "text/comma-separated-values");
        mimeTable.put("dcr", "application/x-director");
        mimeTable.put("dif", "video/x-dv");
        mimeTable.put("dir", "application/x-director");
        mimeTable.put("djv", "image/vnd.djvu");
        mimeTable.put("djvu", "image/vnd.djvu");
        mimeTable.put("dll", "application/octet-stream");
        mimeTable.put("dmg", "application/octet-stream");
        mimeTable.put("dms", "application/octet-stream");
        mimeTable.put("doc", "application/msword");
        mimeTable.put("dtd", "application/xml-dtd");
        mimeTable.put("dv", "video/x-dv");
        mimeTable.put("dvi", "application/x-dvi");
        mimeTable.put("dxr", "application/x-director");
        mimeTable.put("eps", "application/postscript");
        mimeTable.put("etx", "text/x-setext");
        mimeTable.put("exe", "application/octet-stream");
        mimeTable.put("ez", "application/andrew-inset");
        mimeTable.put("gif", "image/gif");
        mimeTable.put("gram", "application/srgs");
        mimeTable.put("grxml", "application/srgs+xml");
        mimeTable.put("gtar", "application/x-gtar");
        mimeTable.put("hdf", "application/x-hdf");
        mimeTable.put("hqx", "application/mac-binhex40");
        mimeTable.put("htm", "text/html");
        mimeTable.put("html", "text/html");
        mimeTable.put("ice", "x-conference/x-cooltalk");
        mimeTable.put("ico", "image/x-icon");
        mimeTable.put("ics", "text/calendar");
        mimeTable.put("ief", "image/ief");
        mimeTable.put("ifb", "text/calendar");
        mimeTable.put("iges", "model/iges");
        mimeTable.put("igs", "model/iges");
        mimeTable.put("jnlp", "application/x-java-jnlp-file");
        mimeTable.put("jp2", "image/jp2");
        mimeTable.put("jpe", IMAGE_JPEG);
        mimeTable.put("jpeg", IMAGE_JPEG);
        mimeTable.put("jpg", IMAGE_JPEG);
        mimeTable.put("js", TEXT_JAVASCRIPT);
        mimeTable.put("kar", "audio/midi");
        mimeTable.put("latex", "application/x-latex");
        mimeTable.put("lha", "application/octet-stream");
        mimeTable.put("log", "text/plain");
        mimeTable.put("lzh", "application/octet-stream");
        mimeTable.put("m3u", "audio/x-mpegurl");
        mimeTable.put("m4a", "audio/mp4a-latm");
        mimeTable.put("m4b", "audio/mp4a-latm");
        mimeTable.put("m4p", "audio/mp4a-latm");
        mimeTable.put("m4u", "video/vnd.mpegurl");
        mimeTable.put("m4v", "video/x-m4v");
        mimeTable.put("mac", "image/x-macpaint");
        mimeTable.put("man", "application/x-troff-man");
        mimeTable.put("mathml", "application/mathml+xml");
        mimeTable.put("me", "application/x-troff-me");
        mimeTable.put("mesh", "model/mesh");
        mimeTable.put("mid", "audio/midi");
        mimeTable.put("midi", "audio/midi");
        mimeTable.put("mif", "application/vnd.mif");
        mimeTable.put("mov", "video/quicktime");
        mimeTable.put("movie", "video/x-sgi-movie");
        mimeTable.put("mp2", "audio/mpeg");
        mimeTable.put("mp3", "audio/mpeg");
        mimeTable.put("mp4", "video/mp4");
        mimeTable.put("mpe", "video/mpeg");
        mimeTable.put("mpeg", "video/mpeg");
        mimeTable.put("mpg", "video/mpeg");
        mimeTable.put("mpga", "audio/mpeg");
        mimeTable.put("ms", "application/x-troff-ms");
        mimeTable.put("msh", "model/mesh");
        mimeTable.put("mxu", "video/vnd.mpegurl");
        mimeTable.put("nc", "application/x-netcdf");
        mimeTable.put("oda", "application/oda");
        mimeTable.put("ogg", "video/ogg");
        mimeTable.put("ogv", "video/ogg");
        mimeTable.put("pbm", "image/x-portable-bitmap");
        mimeTable.put("pct", "image/pict");
        mimeTable.put("pdb", "chemical/x-pdb");
        mimeTable.put("pdf", "application/pdf");
        mimeTable.put("pgm", "image/x-portable-graymap");
        mimeTable.put("pgn", "application/x-chess-pgn");
        mimeTable.put("pic", "image/pict");
        mimeTable.put("pict", "image/pict");
        mimeTable.put("png", IMAGE_PNG);
        mimeTable.put("pnm", "image/x-portable-anymap");
        mimeTable.put("pnt", "image/x-macpaint");
        mimeTable.put("pntg", "image/x-macpaint");
        mimeTable.put("ppm", "image/x-portable-pixmap");
        mimeTable.put("ppt", "application/vnd.ms-powerpoint");
        mimeTable.put("ps", "application/postscript");
        mimeTable.put("qt", "video/quicktime");
        mimeTable.put("qti", "image/x-quicktime");
        mimeTable.put("qtif", "image/x-quicktime");
        mimeTable.put("ra", "audio/x-pn-realaudio");
        mimeTable.put("ram", "audio/x-pn-realaudio");
        mimeTable.put("ras", "image/x-cmu-raster");
        mimeTable.put("rdf", "application/rdf+xml");
        mimeTable.put("rgb", "image/x-rgb");
        mimeTable.put("rm", "application/vnd.rn-realmedia");
        mimeTable.put("roff", "application/x-troff");
        mimeTable.put("rtf", "text/rtf");
        mimeTable.put("rtx", "text/richtext");
        mimeTable.put("sgm", "text/sgml");
        mimeTable.put("sgml", "text/sgml");
        mimeTable.put("sh", "application/x-sh");
        mimeTable.put("shar", "application/x-shar");
        mimeTable.put("silo", "model/mesh");
        mimeTable.put("sit", "application/x-stuffit");
        mimeTable.put("skd", "application/x-koan");
        mimeTable.put("skm", "application/x-koan");
        mimeTable.put("skp", "application/x-koan");
        mimeTable.put("skt", "application/x-koan");
        mimeTable.put("smi", "application/smil");
        mimeTable.put("smil", "application/smil");
        mimeTable.put("snd", "audio/basic");
        mimeTable.put("so", "application/octet-stream");
        mimeTable.put("spl", "application/x-futuresplash");
        mimeTable.put("src", "application/x-wais-source");
        mimeTable.put("sv4cpio", "application/x-sv4cpio");
        mimeTable.put("sv4crc", "application/x-sv4crc");
        mimeTable.put("svg", "image/svg+xml");
        mimeTable.put("swf", APPLICATION_X_SHOCKWAVE_FLASH);
        mimeTable.put("t", "application/x-troff");
        mimeTable.put("tar", "application/x-tar");
        mimeTable.put("tcl", "application/x-tcl");
        mimeTable.put("tex", "application/x-tex");
        mimeTable.put("texi", "application/x-texinfo");
        mimeTable.put("texinfo", "application/x-texinfo");
        mimeTable.put("tif", "image/tiff");
        mimeTable.put("tiff", "image/tiff");
        mimeTable.put("tr", "application/x-troff");
        mimeTable.put("tsv", "text/tab-separated-values");
        mimeTable.put("txt", "text/plain");
        mimeTable.put("ustar", "application/x-ustar");
        mimeTable.put("vcd", "application/x-cdlink");
        mimeTable.put("vrml", "model/vrml");
        mimeTable.put("vxml", "application/voicexml+xml");
        mimeTable.put("wav", "audio/x-wav");
        mimeTable.put("wbmp", "image/vnd.wap.wbmp");
        mimeTable.put("wbmxl", "application/vnd.wap.wbxml");
        mimeTable.put("wml", "text/vnd.wap.wml");
        mimeTable.put("wmlc", "application/vnd.wap.wmlc");
        mimeTable.put("wmls", "text/vnd.wap.wmlscript");
        mimeTable.put("wmlsc", "application/vnd.wap.wmlscriptc");
        mimeTable.put("wrl", "model/vrml");
        mimeTable.put("xbm", "image/x-xbitmap");
        mimeTable.put("xht", "application/xhtml+xml");
        mimeTable.put("xhtml", "application/xhtml+xml");
        mimeTable.put("xls", "application/msexcel");
        mimeTable.put("xlsx", "application/msexcel");
        mimeTable.put("xml", "text/xml");
        mimeTable.put("xpm", "image/x-xpixmap");
        mimeTable.put("xsl", "application/xml");
        mimeTable.put("xslt", "application/xslt+xml");
        mimeTable.put("xul", "application/vnd.mozilla.xul+xml");
        mimeTable.put("xwd", "image/x-xwindowdump");
        mimeTable.put("xyz", "chemical/x-xyz");
        mimeTable.put("zip", "application/zip");

    }

    public static String guessMimeType(String name) {
        if (Strings.isEmpty(name)) {
            return null;
        }
        // Fast lookup for common types....
        if (name.charAt(name.length() - 4) == '.') {
            String ending = name.substring(name.length() - 3).toLowerCase().intern();
            if ("jpg" == ending) {
                return IMAGE_JPEG;
            }
            if ("swf" == ending) {
                return APPLICATION_X_SHOCKWAVE_FLASH;
            }
            if ("pdf" == ending) {
                return APPLICATION_PDF;
            }
            if ("png" == ending) {
                return IMAGE_PNG;
            }
            if ("css" == ending) {
                return TEXT_CSS;
            }
            if ("xml" == ending) {
                return TEXT_JAVASCRIPT;
            }
            if ("txt" == ending) {
                return TEXT_JAVASCRIPT;
            }
        }

        name = Files.getFileExtension(name).toLowerCase();
        String result = mimeTable.get(name);
        if (result == null) {
            return "application/octet-stream";
        } else {
            return result;
        }
    }
}