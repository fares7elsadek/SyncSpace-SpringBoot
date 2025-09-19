<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false showAnotherWayIfPresent=true>
    <!DOCTYPE html>
    <html class="${properties.kcHtmlClass!}" <#if realm.internationalizationEnabled>lang="${locale.currentLanguageTag}"</#if>>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="robots" content="noindex, nofollow">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <#if properties.meta?has_content>
            <#list properties.meta?split(' ') as meta>
                <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
            </#list>
        </#if>

        <title>${msg("loginTitle",(realm.displayName!''))}</title>
        <link rel="icon" href="${url.resourcesPath}/img/favicon.ico" />

        <#-- Preload critical resources -->
        <link rel="preload" href="${url.resourcesPath}/css/fonts.css" as="style">
        <link rel="preload" href="${url.resourcesPath}/img/logo.svg" as="image">

        <#if properties.stylesCommon?has_content>
            <#list properties.stylesCommon?split(' ') as style>
                <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet" />
            </#list>
        </#if>
        <#if properties.styles?has_content>
            <#list properties.styles?split(' ') as style>
                <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
            </#list>
        </#if>

        <#if properties.scripts?has_content>
            <#list properties.scripts?split(' ') as script>
                <script src="${url.resourcesPath}/${script}" type="text/javascript" defer></script>
            </#list>
        </#if>
    </head>

    <body class="syncspace-body ${bodyClass}">
    <!-- Space background layers -->
    <div class="space-bg">
        <div class="stars-layer-1"></div>
        <div class="stars-layer-2"></div>
        <div class="stars-layer-3"></div>
        <div class="nebula-layer"></div>
        <canvas id="particle-canvas"></canvas>
    </div>

    <!-- Main container -->
    <div class="auth-container">
        <div class="auth-card">
            <!-- Logo & Brand -->
            <div class="auth-header">
                <div class="logo-container">
                    <img src="${url.resourcesPath}/img/logo.svg" alt="SyncSpace" class="logo" />
                    <div class="logo-glow"></div>
                </div>
                <h1 class="brand-name">SyncSpace</h1>
                <p class="brand-tagline">Connect to the universe of collaboration</p>
            </div>

            <!-- Messages -->
            <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                <div class="alert alert-${message.type}">
                    <div class="alert-icon">
                        <#if message.type = 'success'>
                            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                            </svg>
                        <#elseif message.type = 'warning'>
                            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                            </svg>
                        <#elseif message.type = 'error'>
                            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                            </svg>
                        <#else>
                            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                            </svg>
                        </#if>
                    </div>
                    <span class="alert-text">${kcSanitize(message.summary)?no_esc}</span>
                </div>
            </#if>

            <!-- Content -->
            <div class="auth-content">
                <#nested "header">

                <div class="auth-form-wrapper">
                    <#nested "form">

                    <#if auth?has_content && auth.showTryAnotherWayLink() && showAnotherWayIfPresent>
                        <form id="kc-select-try-another-way-form" action="${url.loginAction}" method="post">
                            <div class="alternative-login">
                                <input type="hidden" name="tryAnotherWay" value="on"/>
                                <a href="#" class="alternative-link"
                                   onclick="document.forms['kc-select-try-another-way-form'].submit();return false;">
                                    ${msg("doTryAnotherWay")}
                                </a>
                            </div>
                        </form>
                    </#if>

                    <#if displayInfo>
                        <#nested "info">
                    </#if>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <div class="auth-footer">
            <p>&copy; 2024 SyncSpace. All rights reserved.</p>
        </div>
    </div>

    <!-- Accessibility controls -->
    <div class="accessibility-controls">
        <button id="toggle-animations" class="a11y-btn" aria-label="Toggle animations">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/>
            </svg>
        </button>
    </div>
    </body>
    </html>
</#macro>