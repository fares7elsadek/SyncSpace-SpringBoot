<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        ${msg("infoTitle")}
    <#elseif section = "form">
        <p class="auth-subtitle">${message.summary?no_esc}</p>
    </#if>
</@layout.registrationLayout>