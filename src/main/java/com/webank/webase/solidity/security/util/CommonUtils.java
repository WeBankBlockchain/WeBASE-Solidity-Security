/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.webank.webase.solidity.security.util;

import com.alibaba.fastjson.JSON;
import com.webank.webase.solidity.security.base.code.ConstantCode;
import com.webank.webase.solidity.security.base.exception.BaseException;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * CommonUtils.
 * 
 */
@Slf4j
public class CommonUtils {

    /**
     * 文件Base64加密
     * 
     * @param filePath 文件路径
     * @return
     */
    public static String fileToBase64(String filePath) {
        if (filePath == null) {
            return null;
        }
        FileInputStream inputFile = null;
        try {
            File file = new File(filePath);
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            return Base64.getEncoder().encodeToString(buffer);
        } catch (IOException e) {
            log.error("base64ToFile IOException:[{}]", e.toString());
        } finally {
            close(inputFile);
        }
        return null;
    }

    /**
     * 文件压缩并Base64加密
     * 
     * @param srcFiles
     * @return
     */
    public static String fileToZipBase64(List<File> srcFiles) {
        long start = System.currentTimeMillis();
        String toZipBase64 = "";
        ZipOutputStream zos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            zos = new ZipOutputStream(baos);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[1024];
                log.info("fileToZipBase64 fileName: [{}] size: [{}] ", srcFile.getName(),
                        srcFile.length());
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            log.info("fileToZipBase64 cost time：[{}] ms", (end - start));
        } catch (IOException e) {
            log.error("fileToZipBase64 IOException:[{}]", e.toString());
        } finally {
            close(zos);
        }

        byte[] refereeFileBase64Bytes = Base64.getEncoder().encode(baos.toByteArray());
        try {
            toZipBase64 = new String(refereeFileBase64Bytes, "UTF-8");
        } catch (IOException e) {
            log.error("fileToZipBase64 IOException:[{}]", e.toString());
        }
        return toZipBase64;
    }

    /**
     * zip Base64 解密 解压缩.
     * 
     * @param base64 base64加密字符
     * @param path 解压文件夹路径
     */
    public static void zipBase64ToFile(String base64, String path) {
        ByteArrayInputStream bais = null;
        ZipInputStream zis = null;
        try {
            File file = new File(path);
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
            }

            byte[] byteBase64 = Base64.getDecoder().decode(base64);
            bais = new ByteArrayInputStream(byteBase64);
            zis = new ZipInputStream(bais);
            ZipEntry entry = zis.getNextEntry();
            File fout = null;
            while (entry != null) {
                if (entry.isDirectory()) {
                    File subdirectory = new File(path + File.separator + entry.getName());
                    if (!subdirectory.exists() && !subdirectory.isDirectory()) {
                        subdirectory.mkdirs();
                    }
                } else {
                    log.info("zipBase64ToFile file name:[{}]",
                            path + File.separator + entry.getName());
                    fout = new File(path, entry.getName());
                    BufferedOutputStream bos = null;
                    try {
                        bos = new BufferedOutputStream(new FileOutputStream(fout));
                        int offo = -1;
                        byte[] buffer = new byte[1024];
                        while ((offo = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, offo);
                        }
                    } catch (IOException e) {
                        log.error("base64ToFile IOException:[{}]", e.toString());
                    } finally {
                        close(bos);
                    }
                }
                // next
                entry = zis.getNextEntry();
            }
        } catch (IOException e) {
            log.error("base64ToFile IOException:[{}]", e.toString());
        } finally {
            close(zis);
            close(bais);
        }
    }

    /**
     * close Closeable.
     * 
     * @param closeable object
     */
    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("closeable IOException:[{}]", e.toString());
            }
        }
    }

    /**
     * Object to JavaBean.
     * 
     * @param obj obj
     * @param clazz clazz
     * @return
     */
    public static <T> T object2JavaBean(Object obj, Class<T> clazz) {
        if (obj == null || clazz == null) {
            log.warn("Object2JavaBean. obj or clazz null");
            return null;
        }
        String jsonStr = JSON.toJSONString(obj);
        return JSON.parseObject(jsonStr, clazz);
    }

    /**
     * delete single File.
     * 
     * @param filePath filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * delete Files.
     * 
     * @param path path
     * @return
     */
    public static boolean deleteFiles(String path) {
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        if (files == null) {
            return false;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteFiles(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        return true;
    }

    /**
     * 在指定目录执行shell命令
     * 
     * @param command shell
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String shellExecuter(String command, String path) {
        log.info("shellExecuter start. command:{}", command);
        String shellRsp = null;
        int exitCode = 0;
        Process process;
        try {
            process = Runtime.getRuntime().exec(command, null, new File(path));
            // 等待程序执行结束并输出状态
            exitCode = process.waitFor();
            if (exitCode == 0) {
                shellRsp = readInputStream(process.getInputStream());
            } else {
                shellRsp = readInputStream(process.getErrorStream());
            }
        } catch (IOException | InterruptedException e) {
            log.error("shellExecuter Exception:[{}]", e.toString());
            throw new BaseException(ConstantCode.SHELL_EXECUTE_ERROR);
        }

        log.debug("shellExecuter finish. shllRsp:{}", shellRsp);
        return shellRsp;
    }

    /**
     * read InputStream.
     * 
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String readInputStream(final InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        // 逐行读取
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        inputStream.close();
        return sb.toString();
    }

    /**
     * read File.
     * 
     * @param filePath filePath
     * @return
     */
    public static String readFile(String filePath) {
        log.info("readFile dir:{}", filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        String result = null;
        try {
            result = readInputStream(new FileInputStream(file));
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
        return result;
    }
}
