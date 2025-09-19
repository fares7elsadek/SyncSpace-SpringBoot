<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm'); section>
    <#if section = "header">
        <h2 class="auth-title">Create your account</h2>
        <p class="auth-subtitle">Join SyncSpace and start collaborating</p>
    <#elseif section = "form">
        <form id="kc-register-form" action="${url.registrationAction}" method="post" class="auth-form">
            <div class="form-row">
                <!-- First Name -->
                <div class="form-field">
                    <label for="firstName" class="field-label">First name</label>
                    <div class="input-wrapper">
                        <svg class="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                            <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
                        </svg>
                        <input type="text"
                               id="firstName"
                               class="form-input ${messagesPerField.existsError('firstName')?then('error','')}"
                               name="firstName"
                               value="${(register.formData.firstName!'')}"
                               placeholder="John"
                               required
                               aria-invalid="<#if messagesPerField.existsError('firstName')>true</#if>" />
                    </div>
                    <#if messagesPerField.existsError('firstName')>
                        <span class="field-error" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('firstName'))?no_esc}
                        </span>
                    </#if>
                </div>

                <!-- Last Name -->
                <div class="form-field">
                    <label for="lastName" class="field-label">Last name</label>
                    <div class="input-wrapper">
                        <svg class="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                            <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
                        </svg>
                        <input type="text"
                               id="lastName"
                               class="form-input ${messagesPerField.existsError('lastName')?then('error','')}"
                               name="lastName"
                               value="${(register.formData.lastName!'')}"
                               placeholder="Doe"
                               required
                               aria-invalid="<#if messagesPerField.existsError('lastName')>true</#if>" />
                    </div>
                    <#if messagesPerField.existsError('lastName')>
                        <span class="field-error" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('lastName'))?no_esc}
                        </span>
                    </#if>
                </div>
            </div>

            <!-- Email -->
            <div class="form-field">
                <label for="email" class="field-label">Email address</label>
                <div class="input-wrapper">
                    <svg class="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                        <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                    </svg>
                    <input type="email"
                           id="email"
                           class="form-input ${messagesPerField.existsError('email')?then('error','')}"
                           name="email"
                           value="${(register.formData.email!'')}"
                           placeholder="john.doe@example.com"
                           autocomplete="email"
                           required
                           aria-invalid="<#if messagesPerField.existsError('email')>true</#if>" />
                </div>
                <#if messagesPerField.existsError('email')>
                    <span class="field-error" aria-live="polite">
                        ${kcSanitize(messagesPerField.get('email'))?no_esc}
                    </span>
                </#if>
            </div>

            <!-- Username (if not using email as username) -->
            <#if !realm.registrationEmailAsUsername>
                <div class="form-field">
                    <label for="username" class="field-label">Username</label>
                    <div class="input-wrapper">
                        <svg class="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                            <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
                        </svg>
                        <input type="text"
                               id="username"
                               class="form-input ${messagesPerField.existsError('username')?then('error','')}"
                               name="username"
                               value="${(register.formData.username!'')}"
                               placeholder="johndoe"
                               autocomplete="username"
                               required
                               aria-invalid="<#if messagesPerField.existsError('username')>true</#if>" />
                    </div>
                    <#if messagesPerField.existsError('username')>
                        <span class="field-error" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('username'))?no_esc}
                        </span>
                    </#if>
                </div>
            </#if>

            <!-- Password -->
            <#if passwordRequired??>
                <div class="form-field">
                    <label for="password" class="field-label">Password</label>
                    <div class="input-wrapper">
                        <svg class="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                            <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/>
                        </svg>
                        <input type="password"
                               id="password"
                               class="form-input ${messagesPerField.existsError('password')?then('error','')}"
                               name="password"
                               placeholder="Create a strong password"
                               autocomplete="new-password"
                               required
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
                            ${kcSanitize(messagesPerField.get('password'))?no_esc}
                        </span>
                    </#if>
                    <div class="password-strength">
                        <div class="strength-meter">
                            <div class="strength-meter-fill"></div>
                        </div>
                        <span class="strength-text">Enter password</span>
                    </div>
                </div>

                <!-- Confirm Password -->
                <div class="form-field">
                    <label for="password-confirm" class="field-label">Confirm password</label>
                    <div class="input-wrapper">
                        <svg class="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                            <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/>
                        </svg>
                        <input type="password"
                               id="password-confirm"
                               class="form-input ${messagesPerField.existsError('password-confirm')?then('error','')}"
                               name="password-confirm"
                               placeholder="Confirm your password"
                               autocomplete="new-password"
                               required
                               aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>" />
                    </div>
                    <#if messagesPerField.existsError('password-confirm')>
                        <span class="field-error" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
                        </span>
                    </#if>
                </div>
            </#if>

            <!-- reCAPTCHA -->
            <#if recaptchaRequired??>
                <div class="form-field">
                    <div class="g-recaptcha" data-size="normal" data-sitekey="${recaptchaSiteKey}"></div>
                </div>
            </#if>

            <!-- Terms of Service -->
            <div class="form-checkbox">
                <input id="termsAccepted" name="termsAccepted" type="checkbox" required>
                <label for="termsAccepted">
                    I agree to the <a href="#" class="field-link">Terms of Service</a> and <a href="#" class="field-link">Privacy Policy</a>
                </label>
            </div>

            <!-- Submit Button -->
            <button type="submit" class="btn btn-primary">
                <span class="btn-text">Create Account</span>
                <span class="btn-loader" style="display: none;">
                    <svg class="spinner" width="20" height="20" viewBox="0 0 20 20" fill="none">
                        <circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-dasharray="50.265" stroke-dashoffset="37.699"/>
                    </svg>
                </span>
            </button>
        </form>

        <!-- Sign in link -->
        <div class="auth-footer-link">
            <span>Already have an account?</span>
            <a href="${url.loginUrl}">Sign in</a>
        </div>
    </#if>
</@layout.registrationLayout>