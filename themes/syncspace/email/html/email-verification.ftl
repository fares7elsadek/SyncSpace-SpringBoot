<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verify Your Email - SyncSpace</title>
    <style>
        body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
        table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
        img { -ms-interpolation-mode: bicubic; border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; }
        body { margin: 0 !important; padding: 0 !important; width: 100% !important; height: 100% !important; }
        body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Arial, sans-serif; background-color: #1e1f22; color: #f2f3f5; }
        .email-wrapper { width: 100%; background-color: #1e1f22; padding: 40px 20px; }
        .email-container { max-width: 600px; margin: 0 auto; background-color: #2b2d31; border-radius: 16px; overflow: hidden; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4); }
        .email-header { background: linear-gradient(135deg, #5865f2 0%, #7289da 100%); padding: 40px 40px 60px; text-align: center; position: relative; }
        .logo { width: 80px; height: 80px; margin: 0 auto 20px; }
        .email-title { font-size: 28px; font-weight: 700; color: white; margin: 0; letter-spacing: -0.02em; }
        .email-content { padding: 40px; background-color: #2b2d31; }
        .greeting { font-size: 18px; color: #f2f3f5; margin: 0 0 20px; font-weight: 600; }
        .message { font-size: 15px; color: #b5bac1; line-height: 1.6; margin: 0 0 30px; }
        .button-container { text-align: center; margin: 40px 0; }
        .verify-button { display: inline-block; padding: 16px 48px; background: linear-gradient(135deg, #5865f2 0%, #7289da 100%); color: white !important; text-decoration: none; border-radius: 8px; font-size: 16px; font-weight: 600; box-shadow: 0 4px 12px rgba(88, 101, 242, 0.3); }
        .info-box { background-color: #313338; border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 8px; padding: 20px; margin: 30px 0; }
        .info-box p { margin: 0; font-size: 14px; color: #b5bac1; line-height: 1.6; }
        .info-box strong { color: #f2f3f5; }
        .security-notice { background-color: rgba(242, 63, 67, 0.1); border: 1px solid rgba(242, 63, 67, 0.2); border-radius: 8px; padding: 16px; margin: 20px 0; }
        .security-notice p { margin: 0; font-size: 13px; color: #f23f43; line-height: 1.5; }
        .link-fallback { margin-top: 30px; padding-top: 30px; border-top: 1px solid rgba(255, 255, 255, 0.08); }
        .link-fallback p { font-size: 13px; color: #80848e; margin: 0 0 10px; }
        .link-url { word-break: break-all; font-size: 12px; color: #5865f2; text-decoration: none; }
        .email-footer { padding: 30px 40px; background-color: #1e1f22; text-align: center; }
        .email-footer p { margin: 0 0 8px; font-size: 13px; color: #80848e; line-height: 1.5; }
        .email-footer a { color: #5865f2; text-decoration: none; }
        @media only screen and (max-width: 600px) {
            .email-wrapper { padding: 20px 10px !important; }
            .email-header { padding: 30px 20px 40px !important; }
            .email-content { padding: 30px 20px !important; }
            .email-title { font-size: 24px !important; }
            .verify-button { padding: 14px 36px !important; font-size: 15px !important; }
        }
    </style>
</head>
<body>
<div class="email-wrapper">
    <table role="presentation" class="email-container" width="100%" cellspacing="0" cellpadding="0" border="0">
        <tr>
            <td class="email-header">
                <div class="logo">
                    <img src="${url.resourcesPath}/img/logo.svg" alt="SyncSpace" width="80" height="80" style="display: block;">
                </div>
                <h1 class="email-title">Verify Your Email</h1>
            </td>
        </tr>
        <tr>
            <td class="email-content">
                <p class="greeting">Hello<#if user.firstName??> ${user.firstName}</#if>!</p>
                <p class="message">
                    Welcome to SyncSpace! We're excited to have you on board. To complete your registration and start collaborating, please verify your email address by clicking the button below.
                </p>
                <div class="button-container">
                    <a href="${link}" class="verify-button">Verify Email Address</a>
                </div>
                <div class="info-box">
                    <p><strong>Important:</strong> This verification link will expire in <strong>${linkExpiration}</strong>. If you didn't create an account with SyncSpace, you can safely ignore this email.</p>
                </div>
                <div class="security-notice">
                    <p><strong>Security tip:</strong> Never share this email or verification link with anyone. SyncSpace will never ask for your password via email.</p>
                </div>
                <div class="link-fallback">
                    <p>If the button doesn't work, copy and paste this link into your browser:</p>
                    <a href="${link}" class="link-url">${link}</a>
                </div>
            </td>
        </tr>
        <tr>
            <td class="email-footer">
                <p><strong>SyncSpace</strong> - Connect to the universe of collaboration</p>
                <p>Need help? <a href="mailto:support@syncspace.com">Contact Support</a></p>
                <p>&copy; 2025 SyncSpace. All rights reserved.</p>
            </td>
        </tr>
    </table>
</div>
</body>
</html>