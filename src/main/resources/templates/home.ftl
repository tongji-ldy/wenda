<html>
<body>
<pre>
    <#include "header.ftl"><!--显示公共文件-->

    ${value1}<!--后端中变量值-->

    <#list colors as color>
        ${color}
    </#list>

    <#list map?keys as key>
        ${key}--${map[key]}
    </#list>

    ${user.getName()}
</pre>
</body>
</html>