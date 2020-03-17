# WeBASE-Solidity-Security



## 1. 合约安全检测服务说明

合约安全检测服务对外提供检测接口。

此接口接收一个合约文件的zip压缩包Base64编码，然后内部解压分析后，返回给检测端一个检测结果。



## 2. 合约安全检测接口

### 2.1. 合约安全检测接口
#### 接口描述

调用此接口进行合约安全检测。

输入：合约文件zip压缩包Base64编码（合约文件放在contracts文件夹下，每个合约的文件名要和合约名要一致，合约引用需使用“./xxx.sol”，将文件夹打包成zip文件，以业务编号appid命名，然后将zip文件转成Base64编码）。文件夹格式如下：

```
|- contracts
| |- Evidence.sol
| |- EvidenceFactory.sol
|- docs
| |- deploy.md
```

输出：合约安全检测信息（JSON格式)。

#### 接口URL

http://localhost:5007/WeBASE-Solidity-Security/scan

#### 调用方法

HTTP POST

#### 请求参数

***1）入参表***

| 序号 | 输入参数       | 类型   | 可为空 | 备注                        |
| ---- | -------------- | ------ | ------ | --------------------------- |
| 1    | appid          | String | 否     | 业务编号，用来区分合约      |
| 2    | contractSource | String | 否     | 合约文件zip压缩包Base64编码 |

**2）数据格式**

```
{
  "appid": "appid001",
  "contractSource": "UEsDBBQAAAAAACizbFAAAAAAAAAAAAAAAAAKAAAAY29udHJhY3RzL1BLAwQUAAAACAAIiGVQuSZqBJ0AAABvAQAAGAAAAGNvbnRyYWN0cy9IZWxsb1dvcmxkLnNvbIWOMQvCMBCF90L/w43tUiS4FXcnFwc3IbRnCCQXSa6CSP+7sakYpNo33r33vXf1UlkJwRnda77DedNsG9GWReeIvewY9miMOzlv+kdZQFRgr0kBSYttuuANieGIfIi3KvvXSwax5LgM1LF2BAq5qmN5YBkjHnnwFOZE/VqQNH/yFeMXKUTSuylPotWfsTQNSJpgO/qNE6s88R84PgFQSwMEFAAAAAAAE7NsUAAAAAAAAAAAAAAAAAUAAABkb2NzL1BLAwQKAAAAAADjgGxQ1Qj/mQkAAAAJAAAADgAAAGRvY3MvZGVwbG95Lm1kIyBEZXBsb3kKUEsBAj8AFAAAAAAAKLNsUAAAAAAAAAAAAAAAAAoAJAAAAAAAAAAQAAAAAAAAAGNvbnRyYWN0cy8KACAAAAAAAAEAGACqQxwMevjVAapDHAx6+NUBEu2Y9Xn41QFQSwECPwAUAAAACAAIiGVQuSZqBJ0AAABvAQAAGAAkAAAAAAAAACAAAAAoAAAAY29udHJhY3RzL0hlbGxvV29ybGQuc29sCgAgAAAAAAABABgAselMfMzy1QF9ieU0evjVAapDHAx6+NUBUEsBAj8AFAAAAAAAE7NsUAAAAAAAAAAAAAAAAAUAJAAAAAAAAAAQAAAA+wAAAGRvY3MvCgAgAAAAAAABABgAa0Sa9Xn41QFrRJr1efjVARPSmfV5+NUBUEsBAj8ACgAAAAAA44BsUNUI/5kJAAAACQAAAA4AJAAAAAAAAAAgAAAAHgEAAGRvY3MvZGVwbG95Lm1kCgAgAAAAAAABABgAIBTBN0X41QFrRJr1efjVAWtEmvV5+NUBUEsFBgAAAAAEAAQAfQEAAFMBAAAAAA=="
}
```

#### 响应参数

***1）出参表***

| 序号 | 输出参数 | 类型   | 可为空 | 备注                       |
| ---- | -------- | ------ | ------ | -------------------------- |
| 1    | code     | Int    | 否     | 返回码，0：正常 其它：异常 |
| 2    | message  | String | 是     | 错误信息                   |
| 3    | data     | Json   | 是     | 检测结果                   |

**2）数据格式**

a.检测正常返回结果示例
```
{
  "code": 0,
  "message": null,
  "data": {
    "detectors": [
      {
        "elements": [
          {
            "source_mapping": {
              "starting_column": 1,
              "ending_column": 24,
              "filename_used": "/WeBASE-Solidity-Security/dist/contracts/appid001/contracts/HelloWorld.sol",
              "filename_relative": "contracts/HelloWorld.sol",
              "start": 0,
              "length": 23,
              "filename_short": "contracts/HelloWorld.sol",
              "is_dependency": false,
              "lines": [
                1
              ],
              "filename_absolute": "/dist/contracts/appid001/contracts/HelloWorld.sol"
            },
            "name": "^0.4.2",
            "type": "pragma",
            "type_specific_fields": {
              "directive": [
                "solidity",
                "^",
                "0.4",
                ".2"
              ]
            }
          }
        ],
        "impact": "Informational",
        "confidence": "High",
        "markdown": "Pragma version[^0.4.2](contracts/HelloWorld.sol#L1) allows old versions\n",
        "description": "Pragma version^0.4.2 (contracts/HelloWorld.sol#1) allows old versions\n",
        "id": "b93b7ce0902076867f801a1b62ad7557d0305767e196372ed38c0d7076440c1e",
        "check": "solc-version"
      }
    ]
  }
}
```
b.检测异常返回结果示例
```
{
  "code": 203003,
  "message": "Traceback (most recent call last):\n  File \"/usr/local/lib/python3.6/dist-packages/crytic_compile/platform/solc.py\", line 309, in _run_solc\n    ret = json.loads(stdout)\n  File \"/usr/lib/python3.6/json/__init__.py\", line 354, in loads\n    return _default_decoder.decode(s)\n  File \"/usr/lib/python3.6/json/decoder.py\", line 339, in decode\n    obj, end = self.raw_decode(s, idx=_w(s, 0).end())\n  File \"/usr/lib/python3.6/json/decoder.py\", line 357, in raw_decode\n    raise JSONDecodeError(\"Expecting value\", s, err.value) from None\njson.decoder.JSONDecodeError: Expecting value: line 1 column 1 (char 0)\n\nDuring handling of the above exception, another exception occurred:\n\nTraceback (most recent call last):\n  File \"/usr/local/lib/python3.6/dist-packages/slither/__main__.py\", line 578, in main_impl\n    (slither_instances, results_detectors, results_printers, number_contracts) = process_all(filename, args, detector_classes, printer_classes)\n  File \"/usr/local/lib/python3.6/dist-packages/slither/__main__.py\", line 60, in process_all\n    compilations = compile_all(target, **vars(args))\n  File \"/usr/local/lib/python3.6/dist-packages/crytic_compile/crytic_compile.py\", line 1023, in compile_all\n    compilations.append(CryticCompile(filename, **kwargs))\n  File \"/usr/local/lib/python3.6/dist-packages/crytic_compile/crytic_compile.py\", line 142, in __init__\n    self._compile(target, **kwargs)\n  File \"/usr/local/lib/python3.6/dist-packages/crytic_compile/crytic_compile.py\", line 915, in _compile\n    self._platform.compile(self, target, **kwargs)\n  File \"/usr/local/lib/python3.6/dist-packages/crytic_compile/platform/solc.py\", line 90, in compile\n    working_dir=solc_working_dir,\n  File \"/usr/local/lib/python3.6/dist-packages/crytic_compile/platform/solc.py\", line 312, in _run_solc\n    raise InvalidCompilation(f\"Invalid solc compilation {stderr}\")\ncrytic_compile.platform.exceptions.InvalidCompilation: Invalid solc compilation /data/mingzhenliu/webase/WeBASE-Solidity-Security/dist/contracts/appid003/contracts/HelloWorld.sol:6:16: Error: Expected ';' but got '('\n    functio get()constant returns(string){\r\n               ^\ncontracts/HelloWorld.sol:6:16: Error: Expected ';' but got '('\n    functio get()constant returns(string){\r\n               ^\n\n",
  "data": null
}
```

**3）返回码信息**

| Codze  | message                                      | 描述                        |
| ------ | -------------------------------------------- | --------------------------- |
| 0      | success                                      | 正常                        |
| 103001 | system error                                 | 系统异常                    |
| 103002 | param valid fail                             | 参数错误                    |
| 203001 | There is no sol files under contracts folder | contracts文件夹下不存在合约 |
| 203002 | shell execute error                          | shell执行错误               |
| 203003 | contracts abnormal                           | 合约检测异常                |

## 3. 服务安装

### 3.1 前提条件

| 软件名称 | 版本           |
| -------- | -------------- |
| Java     | JDK8或以上版本 |
| Python   | 3.6+           |
| solc     | 0.4.25         |
| slither  |                |

#### 安装slither

从pip安装slither

``` bash
pip install slither-analyzer
```

根据需要检测的不同的合约版本，部署合约编译器 solc。此处以0.4.25合约为例，下载后放入PATH目录中。

``` bash
curl -LO https://github.com/FISCO-BCOS/solidity/releases/download/v0.4.25/solc-static-linux # 其它版本自行下载：https://github.com/FISCO-BCOS/solidity/releases
chmod +x solc-static-linux
mv solc-static-linux solc
sudo cp solc /bin/
```

安装成功可用命令检查

```
slither --version
```

### 3.2 WeBASE-Solidity-Security服务安装

#### 3.2.1 拉取代码
执行命令：
```
git clone https://github.com/WeBankFinTech/WeBASE-Solidity-Security.git
```

进入目录：

```
cd WeBASE-Solidity-Security
```

####  3.2.1 编译代码

使用以下方式编译构建：

方式一：如果服务器已安装Gradle，且版本为Gradle-4.10或以上

```shell
gradle build -x test
```

方式二：如果服务器未安装Gradle，或者版本不是Gradle-4.10或以上，使用gradlew编译

```shell
chmod +x ./gradlew && ./gradlew build -x test
```

构建完成后，会在根目录WeBASE-Solidity-Security下生成已编译的代码目录dist。

#### 3.2.2 服务启停

返回到dist目录执行：
```shell
启动: bash start.sh
停止: bash stop.sh
检查: bash status.sh
```
**备注**：服务进程起来后，需通过日志确认是否正常启动，出现以下内容表示正常；如果服务出现异常，确认修改配置后，重启提示服务进程在运行，则先执行stop.sh，再执行start.sh。

```
...
	Application() - main run success...
```

#### 3.2.3 查看日志

在dist目录查看：

```
服务日志：tail -f log/WeBASE-Solidity-Security.log
```

