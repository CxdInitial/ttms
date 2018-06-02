## 按角色划分场景

### 游客

#### 登录

pre-condition：未登录
guarantee：进入登录状态
trigger：用户意愿

MSS

1. 输入学工号与密码（本系统密码或教务处密码）
2. 进入主页

Extension

1a：验证失败

​	.1： 重复步骤1

1b：学工号未注册

​	.1：联系管理员注册

### 教师

#### 退出登录

pre-condition：已登录
guarantee：进入未登录状态
trigger：修改密码、用户意愿

MSS

1. 退出登录
2. 回到登录页面

#### 查看监考安排

pre-condition：已登录
guarantee：无
trigger：用户意愿

MSS

1. 进入监考页面
2. 选择监考者
3. 选择起止日期
4. 选择教室
5. 选择考试起止时间

Extension

3a：起始日期大于终止日期

​	.1：重复步骤3

5a：开始节数大于终止节数

​	.1：重复步骤5

#### 查看通知任务

pre-condition：已登录
guarantee：无
trigger：用户意愿

MSS

1. 进入任务中心
2. 选择通知类任务
3. 选择发布的起止时间
4. 选择未读或已读

Extension

3a：起始时间大于终止时间

​	.1：重复步骤3

#### 查看收集任务

pre-condition：已登录
guarantee：无
trigger：用户意愿

MSS

1. 进入任务中心
2. 选择收集类任务
3. 选择发布的起止时间
4. 选择未读或已读
4. 选择未回复或已回复
5. 选择是否已经截止

Extension

3a：起始时间大于终止时间

​	.1：重复步骤3

#### 查看上传任务

pre-condition：已登录
guarantee：无
trigger：用户意愿

MSS

1. 进入任务中心
2. 选择上传类任务
3. 选择发布的起止时间
4. 选择未读或已读
5. 选择未上传或已上传
6. 选择是否已经截止


Extension

3a：起始时间大于终止时间

​	.1：重复步骤3

#### 查看填写任务

pre-condition：已登录
guarantee：无
trigger：用户意愿

MSS

1. 进入任务中心
2. 选择上传类任务
3. 选择发布的起止时间
4. 选择未读或已读
5. 选择已填写或未填写
6. 选择是否已经截止

Extension

3a：起始时间大于终止时间

​	.1：重复步骤3

#### 回复收集类任务

pre-condition：已登录
guarantee：无
trigger：用户意愿

MSS

1. 查看收集任务
2. 回复内容

Extension

1a：任务已经过期

​	.1：得到超期提示

​	.2：继续步骤2

#### 回复上传类任务

pre-condition：已登录
guarantee：无
trigger：用户意愿

MSS

1. 查看上传任务
2. 从任务描述中下载模版
3. 填写所需内容
4. 提交填写完成的文件

Extension

1a：任务已经过期

​	.1：得到超期提示

​	.2：继续步骤2

#### 回复填写类任务

pre-condition：已登录
guarantee：无
trigger：用户意愿

MSS

1. 查看填写任务
2. 从任务描述中下载共同填写的文件
3. 填写自己的一部分
4. 提交填写完成的文件

Extension

1a：任务已经过期

​	.1：得到超期提示

​	.2：继续步骤2

4a：有人抢先提交了

​	.1：重复步骤4

### 管理员

#### 注册

pre-condition：已登录为管理员
guarantee：新增用户
trigger：无

MSS

1. 输入学工号
2. 输入姓名、性别、职称
3. 手机号码
4. 提交

Extension

1a：该学工号已经注册

​	.1：提示已经注册

​	.2：结束注册

3a：该手机号已经注册

​	.1：提示已经注册

​	.2：结束注册

#### 任命管理员

pre-condition：已登录为管理员
guarantee：新增用户
trigger：无

MSS

1. 输入学工号
2. 任命为管理员

Extension

1a：该学工号已经为管理员了

​	.1：提示已为管理员

​	.2：结束任命

#### 删除管理员

pre-condition：已登录为管理员
guarantee：新增用户
trigger：无

MSS

1. 输入学工号
2. 取消任命

Extension

1a：该学工号非管理员

​	.1：提示非管理员

​	.2：结束删除

#### 发布任务

pre-condition：已登录为管理员
guarantee：新增任务
trigger：无

MSS：

1. 输入任命名称、任命描述
2. 确定起止时间
3. 上传附件

#### 编辑任务

pre-condition：已登录为管理员
guarantee：修改任务
trigger：无

MSS

1. 查看任务
2. 选择任务
3. 调整内容、标题
4. 调整截止时间
5. 重传附件

Extension

4a：存在先于该时间的提交记录

​	.1：提示错误

​	.2：重复步骤4

#### 新增考试

pre-condition：已登录为管理员
guarantee：新增未指派人员的监考
trigger：无

MSS

1. 输入考试日期
2. 输入考试开始、结束的节数
3. 输入教学楼、教室号

Extension

2a：开始与结束之间有着休息时间

​	.1：提示错误

​	.2：重复步骤2

3a：该教室已有其他考试

​	.1：提示错误

​	.2：重复步骤1

#### 安排监考

pre-condition：已登录为管理员
guarantee：监考人员调整
trigger：无

MSS

1. 查看监考安排
2. 选择一个考试
3. 安排若干名教师
4. 提交

Extension

3a：教师时间冲突

​	.1：提示冲突

## 数据格式

>[RESTful API 设计指南](http://www.ruanyifeng.com/blog/2014/05/restful_api.html)
>
>Http方法：
>
>- GET（SELECT）：从服务器取出资源（一项或多项）。
>- POST（CREATE）：在服务器新建一个资源。
>- PUT（UPDATE）：在服务器更新资源（客户端提供改变后的完整资源）。
>- PATCH（UPDATE）：在服务器更新资源（客户端提供改变的属性）。
>- DELETE（DELETE）：从服务器删除资源。
>
>Http状态码：
>
>- 200 OK - [GET]：服务器成功返回用户请求的数据，该操作是幂等的（Idempotent）。
>- 201 CREATED - [POST/PUT/PATCH]：用户新建或修改数据成功。
>- 202 Accepted - [*]：表示一个请求已经进入后台排队（异步任务）
>- 204 NO CONTENT - [DELETE]：用户删除数据成功。
>- 400 INVALID REQUEST - [POST/PUT/PATCH]：用户发出的请求有错误，服务器没有进行新建或修改数据的操作，该操作是幂等的。
>- 401 Unauthorized - [*]：表示用户没有权限（令牌、用户名、密码错误）。
>- 403 Forbidden - [*] 表示用户得到授权（与401错误相对），但是访问是被禁止的。
>- 404 NOT FOUND - [*]：用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的。
>- 406 Not Acceptable - [GET]：用户请求的格式不可得（比如用户请求JSON格式，但是只有XML格式）。
>- 410 Gone -[GET]：用户请求的资源被永久删除，且不会再得到的。
>- 422 Unprocesable entity - [POST/PUT/PATCH] 当创建一个对象时，发生一个验证错误。
>- 500 INTERNAL SERVER ERROR - [*]：服务器发生错误，用户将无法判断发出的请求是否成功。

### 用户相关

* 登录

  URI：/authentication

  方法：post

  传入JSON：

  ```json
  {
      "number":"(学工号)",
      "password":"(至少6位，只能为数字或字母，必须混合字母和数字)"
  }
  ```

  返回：

  * 状态码：201（登录成功）／400（校验失败）／404（教务处通过未注册）／422（密码错误）／403（已登录）

* 退出

  URI：/authentication

  方法：delete

  传入JSON：无

  返回：

  * 状态码：204（退出成功）／401（未登录）

* 注册

  URI：/user

  方法：post

  传入JSON：

  ```json
  {
      "number":"(学工号)",
      "password":"",
      "admin":"true|false",
      "name":"(2－5个汉字)",
      "title":"(职称)",
      "intro":"(介绍)",
      "male":"true|false",
      "phone":"(手机号)"
  }
  ```

  返回：

  * 201（添加成功）／400（校验失败）／422（添加时数据冲突）／401（未登录为管理员）

* 查找

  URI：/user/{学工号}

  方法：get

  传入：无

  返回：

  * JSON

    ```json
    {
        "user":{
        	"number":"(学工号)",
     	   	"password":"",
        	"admin":"true|false"
    	    "name":"(2－5个汉字)",
        	"title":"(职称)",
    	    "intro":"(介绍)",
        	"male":"true|false",
        	"phone":"(手机号)"
    	}
    }
    ```

  * 状态码：200（成功）／404（未找到）／401（未登录为管理员）

* 查找

  URI：/user

  方法：get

  传入：

  ```json
  {
      "beginIndex":"(小于总数的非负数)"，
      "count":"(不超过总数的正数)"，
      "orderBy":"(number|title|busyness)"
  }
  ```

  返回：

  * JSON

    ```json
    {
        users:[
            {...},
            {...},
            ...
        ]
    }
    ```

  * 状态码：200（成功）／404（未找到）／401（未登录为管理员）

* 查找用户总数

  URI：/count/user

  传入：无

  返回：

  * 状态码：200（成功） 401（未登录为管理员）

  * JSON：

    ```json
    {
        "count":"(长整数)"
    }
    ```

* 更新密码

  URI：/user/{学工号}

  方法：patch

  传入JSON：

  ```json
  {
      //更新后自动退出登录
      "password":"(至少6位，只能为数字或字母，必须混合字母和数字)"
  }
  ```

  返回：

  * 状态码：201（成功）／400（密码格式错误）／401（未登录为管理员或本人）

* 更新信息

  URI：/user/{学工号}

  方法：patch

  传入JSON：

  ```json
  {
      //传入以下若干字段
      "admin":"true|false"
      "name":"(2－5个汉字)",
      "title":"(职称)",
      "intro":"(介绍)",
      "male":"true|false",
      "phone":"(手机号)"
  }
  ```

  返回：

  - 状态码：201（成功）／400（校验失败）／422（数据冲突）／401（未登录为管理员或本人）

* 删除用户

  URI：/user/{学工号}

  方法：delete

  传入JSON：无

  返回：

  - 状态码：204（删除成功）／401（未登录为管理员）

### 考试相关

* 查询考试总数

  ​


* 查找

  URI：/examination/{id}

  方法：get

  传入：无

  返回JSON：

  ```json
  {
      "examination":{
          "id":"(ID)",
          "date":"(日期)",
          "course":"(科目)",
          "begin":"(第几节课开始)",
          "end":"(第几节课结束)",
          "classroom":{
              "id":"(ID)",
              "area":"(丹青楼|成栋楼|锦绣楼)",
              "no":"(教室号)"
          },
          "superviseRecords":[
              {
              	"id":"(ID)",
                  "supervisor":{
                      "number":"(学工号)",
                      "name":"姓名"，
                      ...
                  }
              },
              {
                  ...
              },
              ...
          ]
      }
  }
  ```

* 按条件查找考试

  ​