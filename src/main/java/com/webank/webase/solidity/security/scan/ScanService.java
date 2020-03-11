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

package com.webank.webase.solidity.security.scan;


import com.webank.webase.solidity.security.base.ConstantCode;
import com.webank.webase.solidity.security.base.ResponseEntity;
import com.webank.webase.solidity.security.base.exception.BaseException;
import com.webank.webase.solidity.security.scan.entity.ScanInfo;
import com.webank.webase.solidity.security.util.CommonUtils;

import java.io.File;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * ScanService.
 * 
 */
@Slf4j
@Service
public class ScanService {
    /**
     * contract scan.
     * 
     * @param zipFile file
     * @return
     */
    public ResponseEntity scan(MultipartFile zipFile) throws BaseException, IOException {
        ResponseEntity response = new ResponseEntity(ConstantCode.RET_SUCCEED);
        String path = new File("temp").getAbsolutePath();
        //clear temp folder
        //CommonUtils.deleteFiles(path);
        //unzip
        CommonUtils.unZipFiles(zipFile, path);

        exeScan(String cmd, String path)

        return response;
    }

    public static ScanInfo exeScan(String cmd, String path){
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(cmd,null,new File(path));
            proc.getInputStream();
            proc.getErrorStream();
            proc.waitFor();
            Thread.sleep(1000);//等待后台线程读写完毕
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                proc.getErrorStream().close();
                proc.getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            proc.destroy();
        }
    }
}
