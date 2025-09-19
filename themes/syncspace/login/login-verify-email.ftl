<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=false; section>
    <#if section = "header">
        <h2 class="auth-title">${msg("emailVerifyTitle")}</h2>
    <#elseif section = "form">
        <p class="auth-subtitle">${msg("emailVerifyInstruction1",user.email!)}</p>
    <#elseif section = "info">
        <p class="auth-subtitle">${msg("emailVerifyInstruction2")}</p>
        <form class="form" action="${url.loginAction}" method="post">
            <input type="hidden" id="kc-attempted-username" value="${auth.attemptedUsername}" />
            <button type="submit" class="btn btn-primary">${msg("doResend")}</button>
        </form>
    </#if>
</@layout.registrationLayout>