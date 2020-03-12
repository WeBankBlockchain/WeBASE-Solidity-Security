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


import com.webank.webase.solidity.security.base.exception.BaseException;
import com.webank.webase.solidity.security.scan.entity.ScanInfo;
import com.webank.webase.solidity.security.scan.entity.ScanInputParam;
import com.webank.webase.solidity.security.util.CommonUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ScanService.
 * 
 */
@Slf4j
@Service
public class ScanService {

    private static final String BASE_COMMAND = "slither %s";

    /**
     * contract scan.
     * 
     * @param zipFile file
     * @return
     */
    public List<ScanInfo> scan(ScanInputParam inputParam) throws BaseException {

        List<ScanInfo> scanInfos = new ArrayList<>();

        String path =
                new File("contract").getAbsolutePath() + File.separator + inputParam.getAppid();
        // clear folder
        CommonUtils.deleteFiles(path);
        // unzip
        CommonUtils.zipBase64ToFile(inputParam.getContractSource(), path);
        // get sol files
        File solFileList = new File(path);
        File[] solFiles = solFileList.listFiles();
        if (solFiles == null || solFiles.length == 0) {
            return scanInfos;
        }

        for (File solFile : solFiles) {
            if (!solFile.getName().endsWith(".sol")) {
                continue;
            }
            String contractName =
                    solFile.getName().substring(0, solFile.getName().lastIndexOf("."));
            String command = String.format(BASE_COMMAND, path + File.separator + solFile.getName());
            ScanInfo scanInfo = CommonUtils.shellExecuter(command, path);
            scanInfo.setContractName(contractName);
            scanInfos.add(scanInfo);
        }

        log.debug("end scan.");
        return scanInfos;
    }
}
