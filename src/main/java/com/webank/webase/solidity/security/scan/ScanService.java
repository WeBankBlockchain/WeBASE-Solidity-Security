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


import com.alibaba.fastjson.JSONArray;
import com.webank.webase.solidity.security.scan.entity.CompileInfo;
import com.webank.webase.solidity.security.base.ConstantCode;
import com.webank.webase.solidity.security.base.ResponseEntity;
import com.webank.webase.solidity.security.base.exception.BaseException;
import com.webank.webase.solidity.security.util.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.solidity.compiler.CompilationResult;
import org.fisco.bcos.web3j.solidity.compiler.CompilationResult.ContractMetadata;
import org.fisco.bcos.web3j.solidity.compiler.SolidityCompiler;
import org.fisco.bcos.web3j.solidity.compiler.SolidityCompiler.Options;
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
        // clear temp folder
        CommonUtils.deleteFiles(path);
        // unzip
        CommonUtils.unZipFiles(zipFile, path);
        // get sol files
        File solFileList = new File(path);
        File[] solFiles = solFileList.listFiles();
        if (solFiles == null || solFiles.length == 0) {
            return response;
        }

        List<CompileInfo> compileInfos = new ArrayList<>();
        for (File solFile : solFiles) {
            if (!solFile.getName().endsWith(".sol")) {
                continue;
            }
            String contractName =
                    solFile.getName().substring(0, solFile.getName().lastIndexOf("."));
            // compile
            SolidityCompiler.Result res =
                    SolidityCompiler.compile(solFile, true, Options.ABI, Options.BIN);
            // check result
            if (res.isFailed()) {
                log.warn("compile fail. contract:{} compile error. {}", contractName, res.errors);
                throw new BaseException(ConstantCode.SYSTEM_ERROR.getCode(), res.errors);
            }
            // parse result
            CompilationResult result = CompilationResult.parse(res.output);
            List<ContractMetadata> contracts = result.getContracts();
            if (contracts.size() > 0) {
                CompileInfo compileInfo = new CompileInfo();
                compileInfo.setContractName(contractName);
                compileInfo.setContractBin(result.getContract(contractName).bin);
                compileInfo
                        .setContractAbi(JSONArray.parseArray(result.getContract(contractName).abi));
                compileInfos.add(compileInfo);
            }
        }
        response.setData(compileInfos);
        return response;
    }
}
