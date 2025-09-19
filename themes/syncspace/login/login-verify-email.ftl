<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "header">
        <div class="verify-icon-container">
            <div class="verify-icon">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="currentColor" stroke-width="2"/>
                    <polyline points="22,6 12,13 2,6" stroke="currentColor" stroke-width="2"/>
                    <circle cx="12" cy="12" r="3" fill="currentColor"/>
                    <path d="M10.5 12l1 1 2-2" stroke="#fff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </div>
            <div class="verify-glow"></div>
        </div>
        <h2 class="auth-title">Check your email</h2>
        <p class="auth-subtitle">We've sent a verification link to your email address</p>
    <#elseif section = "form">
        <div class="verify-content">
            <div class="email-display">
                <div class="email-icon">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="currentColor" stroke-width="2"/>
                        <polyline points="22,6 12,13 2,6" stroke="currentColor" stroke-width="2"/>
                    </svg>
                </div>
                <div class="email-info">
                    <span class="email-label">Verification email sent to:</span>
                    <span class="email-address">${user.email!}</span>
                </div>
            </div>

            <div class="verification-steps">
                <h4>What to do next:</h4>
                <div class="steps-list">
                    <div class="step">
                        <div class="step-number">1</div>
                        <div class="step-content">
                            <h5>Check your inbox</h5>
                            <p>Look for an email from SyncSpace with the subject "Verify your email address"</p>
                        </div>
                    </div>
                    <div class="step">
                        <div class="step-number">2</div>
                        <div class="step-content">
                            <h5>Click the verification link</h5>
                            <p>Click the "Verify Email" button or link in the email we sent you</p>
                        </div>
                    </div>
                    <div class="step">
                        <div class="step-number">3</div>
                        <div class="step-content">
                            <h5>Complete your registration</h5>
                            <p>You'll be redirected back to complete your account setup</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="resend-section">
                <p class="resend-text">Didn't receive the email?</p>
                <form id="kc-verify-email-form" action="${url.loginAction}" method="post">
                    <button type="submit" name="resend-email" class="btn btn-secondary resend-btn">
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                            <path d="M11.534 7h3.932a.25.25 0 01.192.41l-1.966 2.36a.25.25 0 01-.384 0l-1.966-2.36a.25.25 0 01.192-.41zm-11 2h3.932a.25.25 0 00.192-.41L2.692 6.23a.25.25 0 00-.384 0L.342 8.59A.25.25 0 00.534 9z"/>
                            <path fill-rule="evenodd" d="M8 3c-1.552 0-2.94.707-3.857 1.818a.5.5 0 11-.771-.636A6.002 6.002 0 0113.917 7H12.9A5.002 5.002 0 008 3zM3.1 9a5.002 5.002 0 008.757 2.182.5.5 0 11.771.636A6.002 6.002 0 012.083 9H3.1z"/>
                        </svg>
                        <span class="resend-text">Resend verification email</span>
                        <span class="resend-timer" style="display: none;">Resend in <span id="countdown">60</span>s</span>
                    </button>
                </form>
            </div>

            <div class="verify-help">
                <div class="help-card">
                    <div class="help-icon">
                        <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-3a1 1 0 00-.867.5 1 1 0 11-1.731-1A3 3 0 0113 8a3.001 3.001 0 01-2 2.83V11a1 1 0 11-2 0v-1a1 1 0 011-1 1 1 0 100-2zm0 8a1 1 0 100-2 1 1 0 000 2z" clip-rule="evenodd"/>
                        </svg>
                    </div>
                    <div class="help-content">
                        <h5>Troubleshooting tips</h5>
                        <ul class="help-tips">
                            <li>Check your spam or junk folder</li>
                            <li>Make sure you entered the correct email address</li>
                            <li>The verification link expires in 24 hours</li>
                            <li>Contact support if you continue having issues</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const resendBtn = document.querySelector('.resend-btn');
                const resendText = document.querySelector('.resend-text');
                const resendTimer = document.querySelector('.resend-timer');
                const countdownEl = document.getElementById('countdown');
                let countdown = 60;
                let countdownInterval;

                function startCountdown() {
                    resendBtn.disabled = true;
                    resendText.style.display = 'none';
                    resendTimer.style.display = 'inline';
                    countdown = 60;

                    countdownInterval = setInterval(() => {
                        countdown--;
                        countdownEl.textContent = countdown;

                        if (countdown <= 0) {
                            clearInterval(countdownInterval);
                            resendBtn.disabled = false;
                            resendText.style.display = 'inline';
                            resendTimer.style.display = 'none';
                        }
                    }, 1000);
                }

                resendBtn.addEventListener('click', function(e) {
                    if (!this.disabled) {
                        startCountdown();
                        // Show feedback
                        showResendFeedback();
                    }
                });

                function showResendFeedback() {
                    const feedback = document.createElement('div');
                    feedback.className = 'resend-feedback';
                    feedback.textContent = 'Verification email sent!';
                    feedback.style.cssText = `
                        position: fixed;
                        top: 20px;
                        right: 20px;
                        background: var(--color-success);
                        color: white;
                        padding: 12px 24px;
                        border-radius: 8px;
                        font-size: 14px;
                        font-weight: 500;
                        z-index: 10000;
                        animation: slide-in-right 0.3s ease-out;
                    `;

                    document.body.appendChild(feedback);

                    setTimeout(() => {
                        feedback.remove();
                    }, 3000);
                }
            });
        </script>

        <style>
            .verify-icon-container {
                position: relative;
                display: flex;
                justify-content: center;
                margin-bottom: var(--space-lg);
            }

            .verify-icon {
                position: relative;
                z-index: 2;
                color: var(--color-success);
                animation: verify-pulse 2s ease-in-out infinite;
            }

            .verify-glow {
                position: absolute;
                inset: -20px;
                border-radius: 50%;
                background: radial-gradient(circle, rgba(63, 185, 80, 0.3) 0%, transparent 70%);
                filter: blur(20px);
                animation: verify-glow-pulse 3s ease-in-out infinite;
            }

            @keyframes verify-pulse {
                0%, 100% { transform: scale(1); }
                50% { transform: scale(1.05); }
            }

            @keyframes verify-glow-pulse {
                0%, 100% { opacity: 0.6; transform: scale(0.9); }
                50% { opacity: 1; transform: scale(1.1); }
            }

            .verify-content {
                display: flex;
                flex-direction: column;
                gap: var(--space-xl);
            }

            .email-display {
                display: flex;
                align-items: center;
                gap: var(--space-md);
                padding: var(--space-lg);
                background: rgba(63, 185, 80, 0.05);
                border: 1px solid rgba(63, 185, 80, 0.15);
                border-radius: var(--radius-lg);
                animation: slide-up 0.5s ease-out 0.1s both;
            }

            .email-icon {
                color: var(--color-success);
                flex-shrink: 0;
            }

            .email-info {
                display: flex;
                flex-direction: column;
                gap: var(--space-xs);
                min-width: 0;
            }

            .email-label {
                font-size: 0.8rem;
                color: var(--color-text-muted);
                font-weight: 500;
            }

            .email-address {
                font-size: 0.9rem;
                color: var(--color-success);
                font-weight: 600;
                word-break: break-all;
            }

            .verification-steps {
                animation: slide-up 0.5s ease-out 0.2s both;
            }

            .verification-steps h4 {
                margin: 0 0 var(--space-lg);
                color: var(--color-text-primary);
                font-size: 1rem;
                font-weight: 600;
            }

            .steps-list {
                display: flex;
                flex-direction: column;
                gap: var(--space-lg);
            }

            .step {
                display: flex;
                gap: var(--space-md);
                align-items: flex-start;
            }

            .step-number {
                display: flex;
                align-items: center;
                justify-content: center;
                width: 32px;
                height: 32px;
                background: var(--color-brand-gradient);
                color: white;
                border-radius: 50%;
                font-size: 0.85rem;
                font-weight: 700;
                flex-shrink: 0;
                box-shadow: var(--glow-brand);
            }

            .step-content h5 {
                margin: 0 0 var(--space-xs);
                color: var(--color-text-primary);
                font-size: 0.9rem;
                font-weight: 600;
            }

            .step-content p {
                margin: 0;
                color: var(--color-text-secondary);
                font-size: 0.85rem;
                line-height: 1.5;
            }

            .resend-section {
                text-align: center;
                padding: var(--space-lg) 0;
                border-top: 1px solid var(--color-border-secondary);
                animation: slide-up 0.5s ease-out 0.3s both;
            }

            .resend-text {
                margin: 0 0 var(--space-md);
                color: var(--color-text-secondary);
                font-size: 0.85rem;
            }

            .resend-btn {
                position: relative;
                overflow: hidden;
            }

            .resend-btn:disabled {
                opacity: 0.6;
                cursor: not-allowed;
                transform: none !important;
            }

            .resend-timer {
                color: var(--color-text-muted);
            }

            .verify-help {
                animation: slide-up 0.5s ease-out 0.4s both;
            }

            .help-card {
                display: flex;
                gap: var(--space-md);
                padding: var(--space-lg);
                background: rgba(88, 166, 255, 0.05);
                border: 1px solid rgba(88, 166, 255, 0.15);
                border-radius: var(--radius-lg);
                align-items: flex-start;
            }

            .help-icon {
                color: var(--color-info);
                flex-shrink: 0;
                margin-top: 2px;
            }

            .help-content h5 {
                margin: 0 0 var(--space-sm);
                color: var(--color-info);
                font-size: 0.9rem;
                font-weight: 600;
            }

            .help-tips {
                margin: 0;
                padding-left: var(--space-lg);
                color: var(--color-text-secondary);
                font-size: 0.8rem;
                line-height: 1.6;
            }

            .help-tips li {
                margin-bottom: var(--space-xs);
            }

            .help-tips li:last-child {
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

            @keyframes slide-in-right {
                from {
                    opacity: 0;
                    transform: translateX(100px);
                }
                to {
                    opacity: 1;
                    transform: translateX(0);
                }
            }

            @media (max-width: 640px) {
                .email-display {
                    flex-direction: column;
                    text-align: center;
                }

                .help-card {
                    flex-direction: column;
                    text-align: center;
                }

                .help-tips {
                    text-align: left;
                }

                .step {
                    align-items: center;
                    text-align: center;
                    flex-direction: column;
                }

                .step-number {
                    margin-bottom: var(--space-sm);
                }
            }
        </style>
    <#elseif section = "info">
        <div class="auth-footer-link">
            <p>Need help? <a href="mailto:support@syncspace.com">Contact Support</a></p>
        </div>
    </#if>
</@layout.registrationLayout>