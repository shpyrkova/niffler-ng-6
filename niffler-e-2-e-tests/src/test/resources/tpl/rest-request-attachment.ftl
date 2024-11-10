<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpRequestAttachment" -->
<head>
<meta http-equiv ="content-type" content="text/html; charset = UTF-8">
    <link type="text/css" href="https://yandex.st/highlightjs/8.0/styles/github.min.css" rel="stylesheet"/>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/highlight.min.js"></script>
    <script type="text/javascript">hljs.initHighlightingOnLoad();</script>
    <style>
        pre {
        white-space: pre-wrap;
        }
    </style>
</head>
<body>
<h5>${data.method} ${data.url}request</h5>
<div>
    <#if data.body?has_content><pre><code>Request body: ${data.body}</code></pre></#if>
    <code>Headers:<#if data.headers?has_content>
        <#list data.headers?keys as key>${key}: ${data.headers[key]} </#list>
        <#else>""</#if>
    </code>
    <pre><code>Cookies: <#if data.cookies?has_content>${data.cookies}<#else>""</#if></code></pre>
    <pre><code>Params: <#if data.formParams?has_content>${data.formParams}<#else>""</#if></code></pre>
    <pre><code>${data.curl}</code></pre>
</div>
</body>
</html>