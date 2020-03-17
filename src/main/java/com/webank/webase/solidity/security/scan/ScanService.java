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


import com.alibaba.fastjson.JSON;
import com.webank.webase.solidity.security.base.ResponseEntity;
import com.webank.webase.solidity.security.base.code.ConstantCode;
import com.webank.webase.solidity.security.base.exception.BaseException;
import com.webank.webase.solidity.security.scan.entity.ScanInputParam;
import com.webank.webase.solidity.security.scan.entity.ScanResult;
import com.webank.webase.solidity.security.util.CommonUtils;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ScanService.
 * 
 */
@Slf4j
@Service
public class ScanService {

    private static final String BASE_COMMAND = "slither --json %s %s";
    private static final String STR_CONTRACTS = "contracts";
    private static final String STR_CHECK_RESULT = "checkResult.json";

    /**
     * contract scan.
     * 
     * @param zipFile file
     * @return
     */
    public ResponseEntity scan(ScanInputParam inputParam) throws BaseException {
        String path =
                new File(STR_CONTRACTS).getAbsolutePath() + File.separator + inputParam.getAppid();
        // clear folder
        CommonUtils.deleteFiles(path);

        // unzip
        CommonUtils.zipBase64ToFile(inputParam.getContractSource(), path);

        // check sol files
        String solPath = path + File.separator + STR_CONTRACTS;
        File solFileList = new File(solPath);
        File[] solFiles = solFileList.listFiles();
        if (solFiles == null || solFiles.length == 0) {
            log.error("There is no sol files under contracts folder.");
            throw new BaseException(ConstantCode.NO_SOL_FILES);
        }

        // shell execute
        String resultPath = path + File.separator + STR_CHECK_RESULT;
        String command = String.format(BASE_COMMAND, resultPath, solPath);
        CommonUtils.shellExecuter(command, path);

        // get result
        ResponseEntity baseResponse = new ResponseEntity(ConstantCode.RET_SUCCEED);
        String result = CommonUtils.readFile(resultPath);
        ScanResult scanResult = CommonUtils.object2JavaBean(JSON.parse(result), ScanResult.class);
        if (!scanResult.isSuccess()) {
            log.error("contracts abnormal");
            throw new BaseException(ConstantCode.CONTRACTS_ABNORMAL.getCode(),
                    scanResult.getError());
        }

        // return
        baseResponse.setMessage(scanResult.getError());
        baseResponse.setData(scanResult.getResults());
        return baseResponse;
    }
}
