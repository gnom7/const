<#-- @ftlvariable name="error" type="java.lang.Throwable" -->

<#import "template.ftl" as layout />

<@layout.mainLayout title="Error">
<h3>Oops, error</h3>
    <#if error??>
        <p>${error}</p>
        <p>${error.message}</p>
    </#if>
</@layout.mainLayout>
