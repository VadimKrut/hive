package com.pathcreator.hive.util;

import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MimeTypeDetector {

    private static final Map<String, byte[]> AI_MAGIC_BYTES = Map.of("ai", new byte[]{(byte) 0x25, (byte) 0x50, (byte) 0x44, (byte) 0x46});
    private static final Map<String, byte[]> BMP_MAGIC_BYTES = Map.of("bmp", new byte[]{(byte) 0x42, (byte) 0x4D});
    private static final Map<String, byte[]> CLASS_MAGIC_BYTES = Map.of("class", new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE});
    private static final Map<String, byte[]> JPG_MAGIC_BYTES = Map.of("jpg", new byte[]{(byte) 0xFF, (byte) 0xD8});
    private static final Map<String, byte[]> JP2_MAGIC_BYTES = Map.of("jp2", new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, (byte) 0x6A, (byte) 0x50, (byte) 0x20, (byte) 0x20, (byte) 0x0D, (byte) 0x0A});
    private static final Map<String, byte[]> GIF_MAGIC_BYTES = Map.of("gif", new byte[]{(byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38});
    private static final Map<String, byte[]> TIF_MAGIC_BYTES = Map.of("tif", new byte[]{(byte) 0x49, (byte) 0x49});
    private static final Map<String, byte[]> PNG_MAGIC_BYTES = Map.of("png", new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47});
    private static final Map<String, byte[]> WAV_MAGIC_BYTES = Map.of("wav", new byte[]{(byte) 0x52, (byte) 0x49, (byte) 0x46, (byte) 0x46});
    private static final Map<String, byte[]> ELF_MAGIC_BYTES = Map.of("elf", new byte[]{(byte) 0x7F, (byte) 0x45, (byte) 0x4C, (byte) 0x46});
    private static final Map<String, byte[]> PSD_MAGIC_BYTES = Map.of("psd", new byte[]{(byte) 0x38, (byte) 0x42, (byte) 0x50, (byte) 0x53});
    private static final Map<String, byte[]> WMF_MAGIC_BYTES = Map.of("wmf", new byte[]{(byte) 0xD7, (byte) 0xCD, (byte) 0xC6, (byte) 0x9A});
    private static final Map<String, byte[]> MID_MAGIC_BYTES = Map.of("mid", new byte[]{(byte) 0x4D, (byte) 0x54, (byte) 0x68, (byte) 0x64});
    private static final Map<String, byte[]> ICO_MAGIC_BYTES = Map.of("ico", new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00});
    private static final Map<String, byte[]> MP3_MAGIC_BYTES = Map.of("mp3", new byte[]{(byte) 0x49, (byte) 0x44, (byte) 0x33});
    private static final Map<String, byte[]> AVI_MAGIC_BYTES = Map.of("avi", new byte[]{(byte) 0x52, (byte) 0x49, (byte) 0x46, (byte) 0x46});
    private static final Map<String, byte[]> SWF_MAGIC_BYTES = Map.of("swf", new byte[]{(byte) 0x46, (byte) 0x57, (byte) 0x53});
    private static final Map<String, byte[]> FLV_MAGIC_BYTES = Map.of("flv", new byte[]{(byte) 0x46, (byte) 0x4C, (byte) 0x56});
    private static final Map<String, byte[]> MP4_MAGIC_BYTES = Map.of("mp4", new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0x66, (byte) 0x74, (byte) 0x79, (byte) 0x70, (byte) 0x6D, (byte) 0x70, (byte) 0x34, (byte) 0x32});
    private static final Map<String, byte[]> MOV_MAGIC_BYTES = Map.of("mov", new byte[]{(byte) 0x6D, (byte) 0x6F, (byte) 0x6F, (byte) 0x76});
    private static final Map<String, byte[]> WMV_MAGIC_BYTES = Map.of("wmv", new byte[]{(byte) 0x30, (byte) 0x26, (byte) 0xB2, (byte) 0x75, (byte) 0x8E, (byte) 0x66, (byte) 0xCF});
    private static final Map<String, byte[]> WMA_MAGIC_BYTES = Map.of("wma", new byte[]{(byte) 0x30, (byte) 0x26, (byte) 0xB2, (byte) 0x75, (byte) 0x8E, (byte) 0x66, (byte) 0xCF});
    private static final Map<String, byte[]> ZIP_MAGIC_BYTES = Map.of("zip", new byte[]{(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04});
    private static final Map<String, byte[]> GZ_MAGIC_BYTES = Map.of("gz", new byte[]{(byte) 0x1F, (byte) 0x8B, (byte) 0x08});
    private static final Map<String, byte[]> TAR_MAGIC_BYTES = Map.of("tar", new byte[]{(byte) 0x75, (byte) 0x73, (byte) 0x74, (byte) 0x61, (byte) 0x72});
    private static final Map<String, byte[]> MSI_MAGIC_BYTES = Map.of("msi", new byte[]{(byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, (byte) 0x1A, (byte) 0xE1});
    private static final Map<String, byte[]> OBJ_MAGIC_BYTES = Map.of("obj", new byte[]{(byte) 0x4C, (byte) 0x01});
    private static final Map<String, byte[]> DLL_MAGIC_BYTES = Map.of("dll", new byte[]{(byte) 0x4D, (byte) 0x5A});
    private static final Map<String, byte[]> CAB_MAGIC_BYTES = Map.of("cab", new byte[]{(byte) 0x4D, (byte) 0x53, (byte) 0x43, (byte) 0x46});
    private static final Map<String, byte[]> EXE_MAGIC_BYTES = Map.of("exe", new byte[]{(byte) 0x4D, (byte) 0x5A});
    private static final Map<String, byte[]> RAR_MAGIC_BYTES = Map.of("rar", new byte[]{(byte) 0x52, (byte) 0x61, (byte) 0x72, (byte) 0x21, (byte) 0x1A, (byte) 0x07, (byte) 0x00});
    private static final Map<String, byte[]> SYS_MAGIC_BYTES = Map.of("sys", new byte[]{(byte) 0x4D, (byte) 0x5A});
    private static final Map<String, byte[]> HLP_MAGIC_BYTES = Map.of("hlp", new byte[]{(byte) 0x3F, (byte) 0x5F, (byte) 0x03, (byte) 0x00});
    private static final Map<String, byte[]> VMDK_MAGIC_BYTES = Map.of("vmdk", new byte[]{(byte) 0x4B, (byte) 0x44, (byte) 0x4D, (byte) 0x56});
    private static final Map<String, byte[]> PST_MAGIC_BYTES = Map.of("pst", new byte[]{(byte) 0x21, (byte) 0x42, (byte) 0x44, (byte) 0x4E, (byte) 0x42});
    private static final Map<String, byte[]> PDF_MAGIC_BYTES = Map.of("pdf", new byte[]{(byte) 0x25, (byte) 0x50, (byte) 0x44, (byte) 0x46});
    private static final Map<String, byte[]> DOC_MAGIC_BYTES = Map.of("doc", new byte[]{(byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, (byte) 0x1A, (byte) 0xE1});
    private static final Map<String, byte[]> RTF_MAGIC_BYTES = Map.of("rtf", new byte[]{(byte) 0x7B, (byte) 0x5C, (byte) 0x72, (byte) 0x74, (byte) 0x66, (byte) 0x31});
    private static final Map<String, byte[]> XLS_MAGIC_BYTES = Map.of("xls", new byte[]{(byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, (byte) 0x1A, (byte) 0xE1});
    private static final Map<String, byte[]> PPT_MAGIC_BYTES = Map.of("ppt", new byte[]{(byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, (byte) 0x1A, (byte) 0xE1});
    private static final Map<String, byte[]> VSD_MAGIC_BYTES = Map.of("vsd", new byte[]{(byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, (byte) 0x1A, (byte) 0xE1});
    private static final Map<String, byte[]> DOCX_MAGIC_BYTES = Map.of("docx", new byte[]{(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04});
    private static final Map<String, byte[]> XLSX_MAGIC_BYTES = Map.of("xlsx", new byte[]{(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04});
    private static final Map<String, byte[]> PPTX_MAGIC_BYTES = Map.of("pptx", new byte[]{(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04});
    private static final Map<String, byte[]> MDB_MAGIC_BYTES = Map.of("mdb", new byte[]{(byte) 0x53, (byte) 0x74, (byte) 0x61, (byte) 0x6E, (byte) 0x64, (byte) 0x61, (byte) 0x72, (byte) 0x64, (byte) 0x20, (byte) 0x4A, (byte) 0x65, (byte) 0x74});
    private static final Map<String, byte[]> PS_MAGIC_BYTES = Map.of("ps", new byte[]{(byte) 0x25, (byte) 0x21});
    private static final Map<String, byte[]> MSG_MAGIC_BYTES = Map.of("msg", new byte[]{(byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, (byte) 0x1A, (byte) 0xE1});
    private static final Map<String, byte[]> EPS_MAGIC_BYTES = Map.of("eps", new byte[]{(byte) 0x25, (byte) 0x21, (byte) 0x50, (byte) 0x53, (byte) 0x2D, (byte) 0x41, (byte) 0x64, (byte) 0x6F, (byte) 0x62, (byte) 0x65, (byte) 0x2D, (byte) 0x33, (byte) 0x2E, (byte) 0x30, (byte) 0x20, (byte) 0x45, (byte) 0x50, (byte) 0x53, (byte) 0x46, (byte) 0x2D, (byte) 0x33, (byte) 0x20, (byte) 0x30});
    private static final Map<String, byte[]> JAR_MAGIC_BYTES = Map.of("jar", new byte[]{(byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04, (byte) 0x14, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x08, (byte) 0x00});
    private static final Map<String, byte[]> SLN_MAGIC_BYTES = Map.of("sln", new byte[]{(byte) 0x4D, (byte) 0x69, (byte) 0x63, (byte) 0x72, (byte) 0x6F, (byte) 0x73, (byte) 0x6F, (byte) 0x66, (byte) 0x74, (byte) 0x20, (byte) 0x56, (byte) 0x69, (byte) 0x73, (byte) 0x75, (byte) 0x61, (byte) 0x6C, (byte) 0x20, (byte) 0x53, (byte) 0x74, (byte) 0x75, (byte) 0x64, (byte) 0x69, (byte) 0x6F, (byte) 0x20, (byte) 0x53, (byte) 0x6F, (byte) 0x6C, (byte) 0x75, (byte) 0x74, (byte) 0x69, (byte) 0x6F, (byte) 0x6E, (byte) 0x20, (byte) 0x46, (byte) 0x69, (byte) 0x6C, (byte) 0x65});
    private static final Map<String, byte[]> ZLIB_MAGIC_BYTES = Map.of("zlib", new byte[]{(byte) 0x78, (byte) 0x9C});
    private static final Map<String, byte[]> SDF_MAGIC_BYTES = Map.of("sdf", new byte[]{(byte) 0x78, (byte) 0x9C});

    public static List<String> detectMimeType(byte[] fileBytes) {
        Map<String, byte[]>[] allMagicBytes = new Map[]{
                AI_MAGIC_BYTES, BMP_MAGIC_BYTES, CLASS_MAGIC_BYTES, JPG_MAGIC_BYTES,
                JP2_MAGIC_BYTES, GIF_MAGIC_BYTES, TIF_MAGIC_BYTES, PNG_MAGIC_BYTES,
                WAV_MAGIC_BYTES, ELF_MAGIC_BYTES, PSD_MAGIC_BYTES, WMF_MAGIC_BYTES,
                MID_MAGIC_BYTES, ICO_MAGIC_BYTES, MP3_MAGIC_BYTES, AVI_MAGIC_BYTES,
                SWF_MAGIC_BYTES, FLV_MAGIC_BYTES, MP4_MAGIC_BYTES, MOV_MAGIC_BYTES,
                WMV_MAGIC_BYTES, WMA_MAGIC_BYTES, ZIP_MAGIC_BYTES, GZ_MAGIC_BYTES,
                TAR_MAGIC_BYTES, MSI_MAGIC_BYTES, OBJ_MAGIC_BYTES, DLL_MAGIC_BYTES,
                CAB_MAGIC_BYTES, EXE_MAGIC_BYTES, RAR_MAGIC_BYTES, SYS_MAGIC_BYTES,
                HLP_MAGIC_BYTES, VMDK_MAGIC_BYTES, PST_MAGIC_BYTES, PDF_MAGIC_BYTES,
                DOC_MAGIC_BYTES, RTF_MAGIC_BYTES, XLS_MAGIC_BYTES, PPT_MAGIC_BYTES,
                VSD_MAGIC_BYTES, DOCX_MAGIC_BYTES, XLSX_MAGIC_BYTES, PPTX_MAGIC_BYTES,
                MDB_MAGIC_BYTES, PS_MAGIC_BYTES, MSG_MAGIC_BYTES, EPS_MAGIC_BYTES,
                JAR_MAGIC_BYTES, SLN_MAGIC_BYTES, ZLIB_MAGIC_BYTES, SDF_MAGIC_BYTES
        };
        List<String> matchingExtensions = new ArrayList<>();
        for (Map<String, byte[]> magicBytesMap : allMagicBytes) {
            for (Map.Entry<String, byte[]> entry : magicBytesMap.entrySet()) {
                if (startsWith(fileBytes, entry.getValue())) {
                    matchingExtensions.add(entry.getKey());
                }
            }
        }
        return matchingExtensions.isEmpty() ? List.of("unknown") : matchingExtensions;
    }

    private static boolean startsWith(byte[] fileBytes, byte[] magicBytes) {
        if (fileBytes.length < magicBytes.length) {
            return false;
        }
        for (int i = 0; i < magicBytes.length; i++) {
            if (fileBytes[i] != magicBytes[i]) {
                return false;
            }
        }
        return true;
    }

    private static String experimentalDetection(byte[] fileBytes) {
        int lengthToCheck = Math.min(fileBytes.length, 8000);
        String content = new String(fileBytes, 0, lengthToCheck, StandardCharsets.UTF_8);
        System.out.println(content);
        if (content.contains("%PDF")) {
            if (content.contains("Adobe Illustrator")) {
                return "ai";
            }
            return "pdf";
        }
        if (content.startsWith("PK")) {
            if (content.contains("[Content_Types].xml")) {
                if (content.contains("word/")) {
                    return "docx";
                } else if (content.contains("xl/")) {
                    return "xlsx";
                }
                return "zip";
            } else if (content.contains("ppt/")) {
                return "pptx";
            }
            return "zip";
        }
        return "unknown";
    }

    public static String detectTika(InputStream inputStream) throws IOException {
        return new Tika().detect(inputStream);
    }
}