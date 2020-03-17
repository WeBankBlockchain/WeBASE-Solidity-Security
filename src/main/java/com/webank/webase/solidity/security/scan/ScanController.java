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
import com.webank.webase.solidity.security.base.controller.BaseController;
import com.webank.webase.solidity.security.base.exception.BaseException;
import com.webank.webase.solidity.security.scan.entity.ScanInputParam;
import io.swagger.annotations.ApiOperation;
import java.time.Duration;
import java.time.Instant;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ScanController.
 * 
 */
@Log4j2
@RestController
@RequestMapping(value = "/scan")
public class ScanController extends BaseController {
    @Autowired
    ScanService scanService;

    /**
     * contract compile.
     */
    @ApiOperation(value = "contract scan", notes = "contract scan")
    @PostMapping
    public ResponseEntity scan(@RequestBody @Valid ScanInputParam scanInputParam,
            BindingResult result) throws BaseException {
        checkBindResult(result);
        Instant startTime = Instant.now();
        log.info("start scan startTime:{} compileInputParam:{}", startTime.toEpochMilli(),
                JSON.toJSONString(scanInputParam));

        ResponseEntity scanResult = scanService.scan(scanInputParam);

        log.info("end scan useTime:{}", Duration.between(startTime, Instant.now()).toMillis());
        return scanResult;
    }
}
