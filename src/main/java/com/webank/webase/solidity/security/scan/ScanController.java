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

import com.webank.webase.solidity.security.base.BaseController;
import com.webank.webase.solidity.security.base.ResponseEntity;
import com.webank.webase.solidity.security.base.exception.BaseException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * ScanController.
 * 
 */
@Api(value = "/scan", tags = "scan interface")
@Slf4j
@RestController
@RequestMapping(value = "/scan")
public class ScanController extends BaseController {
    @Autowired
    ScanService scanService;

    /**
     * contract compile.
     * 
     * @param file file
     * @return
     */
    @ApiOperation(value = "contract scan", notes = "contract scan")
    @PostMapping("/scan")
    public ResponseEntity compile(
            @ApiParam(value = "contract zip file",
                    required = true) @RequestParam("file") MultipartFile file)
            throws BaseException, IOException {
        return scanService.scan(file);
    }
}
