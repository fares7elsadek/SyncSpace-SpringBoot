<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
<#if section = "header">
<h2 class="auth-title">Welcome back</h2>
<p class="auth-subtitle">Sign in to continue to SyncSpace</p>
<#elseif section = "form">
<div id="kc-form">
    <#if realm.password>
    <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post" class="auth-form">
        <!-- Username/Email field -->
        <div class="form-field">
            <label for="username" class="field-label">Email or username</label>
            <div class="input-wrapper">
                <svg class="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                    <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                </svg>
                <input id="username"
                       class="form-input ${messagesPerField.existsError('username','password')?then('error','')}"
                       name="username"
                       value="${(login.username!'')}"
                       type="text"
                       autofocus
                       autocomplete="username"
                       placeholder="Enter your email or username"
                       aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                       <#if usernameEditDisabled??>disabled</#if> />
            </div>
            <#if messagesPerField.existsError('username')>
                <span class="field-error" aria-live="polite">
                                ${kcSanitize(messagesPerField.getFirstError('username'))?no_esc}
                            </span>
            </#if>
        </div>

        <!-- Password field -->
        <div class="form-field">
            <div class="field-header">
                <label for="password" class="field-label">Password</label>
                <#if realm.resetPasswordAllowed>
                    <a href="${url.loginResetCredentialsUrl}" class="field-link">Forgot password?</a>
                </#if>
            </div>
            <div class="input-wrapper">
                <svg class="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/>
                </svg>
                <input id="password"
                       class="form-input ${messagesPerField.existsError('password')?then('error','')}"
                       name="password"
                       type="password"
                       autocomplete="current-password"
                       placeholder="Enter your password"
                       aria-invalid="<#if messagesPerField.existsError('password')>true</#if>" />
                <button type="button" class="toggle-password" aria-label="Toggle password visibility">
                    <svg class="eye-open" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M10 12a2 2 0 100-4 2 2 0 000 4z"/>
                        <path fill-rule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clip-rule="evenodd"/>
                    </svg>
                    <svg class="eye-closed" width="20" height="20" viewBox="0 0 20 20" fill="currentColor" style="display: none;">
                        <path fill-rule="evenodd" d="M3.707 2.293a1 1 0 00-1.414 1.414l14 14a1 1 0 001.414-1.414l-1.473-1.473A10.014 10.014 0 0019.542 10C18.268 5.943 14.478 3 10 3a9.958 9.958 0 00-4.512 1.074l-1.78-1.781zm4.261 4.26l1.514 1.515a2.003 2.003 0 012.45 2.45l1.514 1.514a4 4 0 00-5.478-5.478z" clip-rule="evenodd"/>
                        <path d="M12.454 16.697L9.75 13.992a4 4 0 01-4.404-4.404l-2.172-2.172A9.961 9.961 0 00.458 10c1.274 4.057 5.065 7 9.542 7a9.959 9.959 0 002.454-.303z"/>
                    </svg>
                </button>
            </div>
            <#if messagesPerField.existsError('password')>
                <span class="field-error" aria-live="polite">
                                ${kcSanitize(messagesPerField.getFirstError('password'))?no_esc}
                            </span>
            </#if>
        </div>

        <!-- Remember me -->
        <#if realm.rememberMe && !usernameEditDisabled??>
            <div class="form-checkbox">
                <input id="rememberMe" name="rememberMe" type="checkbox" <#if login.rememberMe??>checked</#if>>
                <label for="rememberMe">Remember me</label>
            </div>
        </#if>

        <!-- Hidden fields -->
        <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>

        <!-- Submit button -->
        <button type="submit" name="login" id="kc-login" class="btn btn-primary">
            <span class="btn-text">Sign In</span>
            <span class="btn-loader" style="display: none;">
                            <svg class="spinner" width="20" height="20" viewBox="0 0 20 20" fill="none">
                                <circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-dasharray="50.265" stroke-dashoffset="37.699"/>
                            </svg>
                        </span>
        </button>
    </form>
    </#if>

    <!-- Social login -->
    <#if realm.social?? && realm.social?size gt 0>
        <div class="social-divider">
            <span>Or continue with</span>
        </div>
        <div class="social-providers">
            <#list realm.social as provider>
                <a href="${provider.loginUrl}" class="social-btn social-${provider.alias}">
                    <#if provider.alias == "google">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                            <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                            <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                            <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                        </svg>
                    <#elseif provider.alias == "github">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.17 6.839 9.49.5.092.682-.217.682-.482 0-.237-.008-.866-.013-1.7-2.782.603-3.369-1.34-3.369-1.34-.454-1.156-1.11-1.463-1.11-1.463-.908-.62.069-.607.069-.607 1.003.07 1.531 1.03 1.531 1.03.892 1.529 2.341 1.087 2.91.831.092-.646.35-1.086.636-1.336-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.268 2.75 1.026A9.578 9.578 0 0112 6.836c.85.004 1.705.114 2.504.336 1.909-1.294 2.747-1.026 2.747-1.026.546 1.377.203 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.336-.012 2.415-.012 2.743 0 .267.18.578.688.48C19.138 20.167 22 16.418 22 12c0-5.523-4.477-10-10-10z"/>
                        </svg>
                    <#else>
                        <span>${provider.alias}</span>
                    </#if>
                </a>
            </#list>
        </div>
    </#if>

    <!-- Sign up link -->
    <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
        <div class="auth-footer-link">
            <span>Don't have an account?</span>
            <a href="${url.registrationUrl}">Sign up</a>
        </div>
    </#if>
</div>
</#if>
</@layout.registrationLayout>