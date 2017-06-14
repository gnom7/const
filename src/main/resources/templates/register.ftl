<#-- @ftlvariable name="error" type="java.lang.String" -->
<#-- @ftlvariable name="pageUser" type="com.k.ktor.cons.model.User" -->

<#import "template.ftl" as layout />

<@layout.mainLayout title="Welcome">
<form class="pure-form pure-form-stacked" action="/register" method="post">
    <#if error??>
        <p class="alert-info">${error}</p>
    </#if>

    <fieldset>
        <legend>Sign up</legend>

        <label for="username">Login</label>
        <input type="text" name="username" id="username" value="${pageUser.username}">

        <label for="email">Mail</label>
        <input type="email" name="email" id="email" value="${pageUser.email}">

        <label for="displayName">Display name</label>
        <input type="text" name="displayName" id="displayName" value="${pageUser.displayName}">

        <label for="password">Password</label>
        <input type="password" name="password" id="password">

        <input type="hidden" name="id" id="userId">

        <input class="pure-button pure-button-primary" type="submit" value="Register">
    </fieldset>
</form>
</@layout.mainLayout>
