<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=!messagesPerField.existsError('username'); section>
    <#if section = "header">
        <div class="reset-icon-container">
            <div class="reset-icon">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10c1.18 0 2.34-.21 3.41-.6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    <path d="M8 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M16 18l2-2 4 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </div>
            <div class="reset-glow"></div>
        </div>
        <h2 class="auth-title">Reset your password</h2>
        <p class="auth-subtitle">Enter your email and we'll send you a link to get back into your account</p>
    <#elseif section = "form">
        <form id="kc-reset-password-form" class="auth-form" action="${url.loginAction}" method="post">
            <div class="form-field">
                <div class="field-header">
                    <label for="username" class="field-label">
                        <#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if>
                    </label>
                </div>
                <div class="input-wrapper">
                    <div class="input-icon">
                        <#if realm.loginWithEmailAllowed>
                            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                                <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                                <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                            </svg>
                        <#else>
                            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
                            </svg>
                        </#if>
                    </div>
                    <input
                            type="text"
                            id="username"
                            name="username"
                            class="form-input ${messagesPerField.existsError('username')?then('error', '')}"
                            value="${(auth.attemptedUsername!'')}"
                            placeholder="<#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if>"
                            required
                            autofocus
                            autocomplete="<#if realm.loginWithEmailAllowed>email<#else>username</#if>"
                    />
                </div>
                <#if messagesPerField.existsError('username')>
                    <span class="field-error" aria-live="polite">
                        ${kcSanitize(messagesPerField.get('username'))?no_esc}
                    </span>
                </#if>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary" id="kc-submit">
                    <span class="btn-text">
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                            <path d="M.05 3.555A2 2 0 012 2h12a2 2 0 011.95 1.555L8 8.414.05 3.555zM0 4.697v7.104L5.803 8.414.05 3.555zM6.761 8.83l-6.57 2.027A2 2 0 002 12h12a2 2 0 001.808-.632l.013-.013L6.761 8.83zm3.436-.586L16 11.801V4.697l-5.803 3.546z"/>
                        </svg>
                        ${msg("doSubmit")}
                    </span>
                    <span class="btn-loader" style="display: none;">
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor" class="spinner">
                            <path d="M8 0a8 8 0 000 16A8 8 0 008 0zM7 4a1 1 0 012 0v4a1 1 0 01-2 0V4z"/>
                        </svg>
                        Sending...
                    </span>
                </button>
            </div>
        </form>

        <div class="reset-info">
            <div class="info-card">
                <div class="info-icon">
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                    </svg>
                </div>
                <div class="info-content">
                    <h4>What happens next?</h4>
                    <ul class="info-steps">
                        <li>We'll send a password reset link to your email</li>
                        <li>Click the link in your email (check spam folder too)</li>
                        <li>Create a new secure password</li>
                        <li>Sign in with your new password</li>
                    </ul>
                </div>
            </div>
        </div>

        <style>
            .reset-icon-container {
                position: relative;
                display: flex;
                justify-content: center;
                margin-bottom: var(--space-lg);
            }

            .reset-icon {
                position: relative;
                z-index: 2;
                color: var(--color-info);
                animation: reset-float 3s ease-in-out infinite;
            }

            .reset-glow {
                position: absolute;
                inset: -15px;
                border-radius: 50%;
                background: radial-gradient(circle, rgba(88, 166, 255, 0.3) 0%, transparent 70%);
                filter: blur(15px);
                animation: reset-glow-pulse 4s ease-in-out infinite;
            }

            @keyframes reset-float {
                0%, 100% { transform: translateY(0) rotate(0deg); }
                33% { transform: translateY(-5px) rotate(2deg); }
                66% { transform: translateY(2px) rotate(-1deg); }
            }

            @keyframes reset-glow-pulse {
                0%, 100% { opacity: 0.5; transform: scale(0.95); }
                50% { opacity: 0.8; transform: scale(1.05); }
            }

            .form-actions {
                margin-top: var(--space-xl);
            }

            .reset-info {
                margin-top: var(--space-xl);
                animation: slide-up 0.5s ease-out 0.2s both;
            }

            .info-card {
                background: rgba(88, 166, 255, 0.05);
                border: 1px solid rgba(88, 166, 255, 0.15);
                border-radius: var(--radius-lg);
                padding: var(--space-lg);
                display: flex;
                gap: var(--space-md);
                align-items: flex-start;
            }

            .info-icon {
                flex-shrink: 0;
                color: var(--color-info);
                margin-top: 2px;
            }

            .info-content h4 {
                margin: 0 0 var(--space-sm);
                color: var(--color-info);
                font-size: 0.95rem;
                font-weight: 600;
            }

            .info-steps {
                margin: 0;
                padding-left: var(--space-lg);
                color: var(--color-text-secondary);
                font-size: 0.85rem;
                line-height: 1.6;
            }

            .info-steps li {
                margin-bottom: var(--space-xs);
            }

            .info-steps li:last-child {
                margin-bottom: 0;
            }

            @keyframes slide-up {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            /* Enhanced button loading state */
            #kc-submit:disabled {
                cursor: not-allowed;
                opacity: 0.7;
            }

            #kc-submit:disabled:hover {
                transform: none;
            }

            @media (max-width: 480px) {
                .info-card {
                    flex-direction: column;
                    text-align: center;
                }

                .info-steps {
                    text-align: left;
                    padding-left: var(--space-lg);
                }
            }
        </style>
    <#elseif section = "info">
        <div class="auth-footer-link">
            <p>Remember your password? <a href="${url.loginUrl}">Back to Sign In</a></p>
        </div>
    </#if>
</@layout.registrationLayout>