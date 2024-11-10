<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpResponseAttachment" -->
<head>
<meta http-equiv="content-type" content="text/html; charset = UTF-8">
    <link type="text/css" href="https://yandex.st/highlightjs/8.0/styles/github.min.css" rel="stylesheet"/>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/highlight.min.js"></script>
    <script type="text/javascript">hljs.initHighlightingOnLoad();</script>
    <style>
        pre {
        white-space: pre-wrap;
        margin: 0;
        }
    </style>
</head>
<body>
<div>
    <pre><code>Response code: ${data.responseCode}</code></pre>
    <pre><code>Response body: <#if data.body?has_content>${data.body?html}<#else>""</#if></code></pre>
    <code>Headers:<#if data.headers?has_content>
        <#list data.headers?keys as key>${key}: ${data.headers[key]}</#list>
        <#else>""</#if>
    </code>
    <pre><code>Cookies: <#if data.cookies?has_content>${data.cookies}<#else>""</#if></code></pre>
</div>
</body>
</html>