<#-- @ftlvariable name="error" type="java.lang.String" -->
<#-- @ftlvariable name="pageUser" type="com.k.ktor.cons.model.User" -->

<#import "template.ftl" as layout />

<@layout.mainLayout title="Welcome">
<form class="pure-form pure-form-stacked" action="/login" method="post">
    <#if error??>
        <p class="alert-info">${error}</p>
    </#if>

    <fieldset>
        <legend>Sign in</legend>

        <label for="username">Login</label>
        <input type="text" name="username" id="username" value="<#if pageUser??>${pageUser.username}</#if>">

        <label for="password">Password</label>
        <input type="password" name="password" id="password">

        <input class="pure-button pure-button-primary" type="submit" value="Register">
    </fieldset>
</form>
</@layout.mainLayout>
