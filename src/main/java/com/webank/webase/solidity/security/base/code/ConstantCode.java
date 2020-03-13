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

package com.webank.webase.solidity.security.base.code;

/**
 * ConstantCode.
 *
 */
public interface ConstantCode {
    // return success
    RetCode RET_SUCCEED = RetCode.mark(0, "success");

    // generate code
    RetCode NO_SOL_FILES = RetCode.mark(203001, "There is no sol files under contracts folder");
    RetCode SHELL_EXECUTE_ERROR = RetCode.mark(203002, "shell execute error");

    // system error
    RetCode SYSTEM_EXCEPTION = RetCode.mark(103001, "system error");
    RetCode PARAM_EXCEPTION = RetCode.mark(103002, "param valid fail");
}
