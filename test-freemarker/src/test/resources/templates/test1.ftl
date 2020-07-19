<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
<!-- html语法注释 -->

<#-- freemarker语法注释 -->

<#-- 插值 ${} 和jsp中的一样 -->
Hello ${name}!
<br/>
遍历模型数据中的list中的学生信息[数据模型中的名称为stuList]
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>金额</td>
        <td>出生日期</td>
    </tr>
    <#if stuList??>
        <#list stuList as stu>
            <tr>
                <td>${stu_index+1}</td>
                <td <#if stu.name=='小明'>style="background: cornflowerblue;" </#if>>${stu.name}</td>
                <td>${stu.age}</td>
                <td <#if stu.money gt 300>style="background: cornflowerblue;"</#if>>${stu.money}</td>
                <td>${stu.birthday?string("yyyy年MM月dd日")}</td>
            </tr>
        </#list>
        <br>
    <#--内建函数语法格式： 变量+?+函数名称-->
        学生的个数:${stuList?size}
    </#if>
</table>
<#------------------------------------------------------------------>
<br>
遍历数据模型中的stuMap(map数据),
第一种方法:在中括号中填写map的Key
第二种方法:在map后面直接加 " .key "
<br>
第一种:对于空值判断处理
<br>
<#if stuMap?? && stuMap.stu1??>
    姓名:${stuMap['stu1'].name}<br>
    年龄:${stuMap['stu1'].age}<br>
</#if>
第二种:对于空值判断处理
<br>
金额:${(stuMap['stu1'].money)!''}<br>
<#-------------------------------------------------->
姓名:${stuMap.stu2.name}<br>
年龄:${stuMap.stu2.age}<br>
金额:${stuMap.stu2.money}<br>

遍历map中的key stuMap?keys就是keys列表(是一个list)
<br>
<#list stuMap?keys as k>
    姓名:${stuMap[k].name}<br>
    年龄:${stuMap[k].age}<br>
    金额:${stuMap[k].money}<br>
</#list>
<#-------------------------------------------------->
<br>
数字类型转换为字符串 变量?c
<br>
将json字符串转成对象 --其中用到了 assign标签，assign的作用是定义一个变量
<br>
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />
开户行：${data.bank} 账号：${data.account}

<br>
</body>
</html>